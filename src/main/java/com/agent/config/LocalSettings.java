package com.agent.config;

import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.SigarException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.agent.config.SigarUtils.sigar;

/**
 * Created by zjb on 2018/7/2.
 */
@Component
@ConfigurationProperties(prefix = "localSettings")
public class LocalSettings {
    private String serverIp;
    private String serverPort;
    private String localIp;
    private String localMac;

    public String getLocalIp() {
        return localIp;
    }

    @PostConstruct
    private void setLocalIp() {
        String address = null;
        try {
            address = InetAddress.getLocalHost().getHostAddress();
            // 没有出现异常而正常当取到的IP时，如果取到的不是网卡循回地址时就返回
            // 否则再通过Sigar工具包中的方法来获取
            if (!NetFlags.LOOPBACK_ADDRESS.equals(address)) {
                this.localIp = address;
                return;
            }
        } catch (UnknownHostException e) {
            // hostname not in DNS or /etc/hosts
        }
        try {
            address = sigar.getNetInterfaceConfig().getAddress();
        } catch (SigarException e) {
            address = NetFlags.LOOPBACK_ADDRESS;
        } finally {
            sigar.close();
        }
        this.localIp = address;
        return;
    }

    public String getLocalMac() {
        return localMac;
    }

    @PostConstruct
    private void setLocalMac() {
        try {
            String[] ifaces = sigar.getNetInterfaceList();
            String hwaddr = null;
            for (int i = 0; i < ifaces.length; i++) {
                NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
                if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress())
                        || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                        || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                    continue;
                }
                /*
                 * 如果存在多张网卡包括虚拟机的网卡，默认只取第一张网卡的MAC地址，如果要返回所有的网卡（包括物理的和虚拟的）则可以修改方法的返回类型为数组或Collection
                 * ，通过在for循环里取到的多个MAC地址。
                 */
                hwaddr = cfg.getHwaddr();
                break;
            }
            this.localMac = hwaddr != null ? hwaddr : null;
        } catch (Exception e) {
            this.localMac = "";
        }
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
