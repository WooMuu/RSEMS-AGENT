package com.agent.schedules;

import com.agent.ProcessInfo;
import com.agent.client.CxfClient;
import com.agent.config.LocalSettings;
import com.agent.config.SigarUtils;
import com.agent.model.monitor.MonitorInfo;
import com.agent.utils.DateUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hyperic.sigar.*;
import org.hyperic.sigar.cmd.Ps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by zjb on 2018/7/2.
 */
@Component
public class Schedules {
    private Sigar sigar = SigarUtils.sigar;
    @Autowired
    CxfClient cxfClient;
    @Autowired
    LocalSettings settings;

    public void jvm() throws UnknownHostException {
        Runtime r = Runtime.getRuntime();
        Properties props = System.getProperties();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jvm_memory_total", r.totalMemory()); //JVM可以使用的总内存
        jsonObject.put("jvm_memory_free", r.freeMemory()); //JVM可以使用的剩余内存
        jsonObject.put("jvm_processor_avaliable", r.availableProcessors()); //JVM可以使用的处理器个数

        jsonObject.put("jvm_java_version", props.getProperty("java.version")); //Java的运行环境版本
        jsonObject.put("jvm_java_vendor", props.getProperty("java.vendor")); //Java的运行环境供应商
        jsonObject.put("jvm_java_home", props.getProperty("java.home")); //Java的安装路径
        jsonObject.put("jvm_java_specification_version", props.getProperty("java.specification.version")); //Java运行时环境规范版本
        jsonObject.put("jvm_java_class_path", props.getProperty("java.class.path")); //Java的类路径
        jsonObject.put("jvm_java_library_path", props.getProperty("java.library.path")); //Java加载库时搜索的路径列表
        jsonObject.put("jvm_java_io_tmpdir", props.getProperty("java.io.tmpdir")); //默认的临时文件路径
        jsonObject.put("jvm_java_ext_dirs", props.getProperty("java.ext.dirs")); //扩展目录的路径
    }

    public void memory() throws SigarException {
        Mem mem = sigar.getMem();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memory_total", mem.getTotal() / (1024 * 1024L));// 内存总量
        jsonObject.put("memory_used", mem.getUsed() / (1024 * 1024L));// 当前内存使用量
        double usedPercent = mem.getUsedPercent();//当前内存使用量百分比
        jsonObject.put("memory_free", mem.getFree() / (1024 * 1024L));// 当前内存剩余量
        double freePercent = mem.getFreePercent();//当前内存剩余量百分比


        Swap swap = sigar.getSwap();
        jsonObject.put("memory_swap_total", swap.getTotal() / (1024 * 1024L));// 交换区总量
        jsonObject.put("memory_swap_used", swap.getUsed() / (1024 * 1024L));// 当前交换区使用量
        jsonObject.put("memory_swap_free", swap.getFree() / (1024 * 1024L));// 当前交换区剩余量
    }

    @Scheduled(cron = "0/2 * * * * ?")
    public void memoryUsedPercent() throws SigarException {
        Mem mem = sigar.getMem();
        double usedPercent = mem.getUsedPercent();//当前内存使用量百分比
        MonitorInfo info = new MonitorInfo();
        info.setCollectionIp(settings.getLocalIp());
        info.setCollectionMac(settings.getLocalMac());
        info.setCollectionTime(DateUtils.currentTimeStamp());
        info.setMonitorInfo(usedPercent);
        cxfClient.sendMonitorInfo(info, "memoryUsedPercent");
    }

    public void cpu() throws SigarException {
        CpuInfo infos[] = sigar.getCpuInfoList();
        CpuPerc cpuPercList[] = sigar.getCpuPercList();
        Cpu[] cpuList = sigar.getCpuList();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0, len = infos.length; i < len; i++) {// 不管是单块CPU还是多CPU都适用
            CpuInfo info = infos[i];
            JSONObject jso = new JSONObject();
            jso.put("mhz", info.getMhz()); //CPU的总量MHz
            jso.put("company", info.getVendor()); //CPU的厂商
            jso.put("model", info.getModel()); //CPU型号类别
            jso.put("cache_size", info.getCacheSize()); // 缓冲缓存数量

            CpuPerc cpuPerc = cpuPercList[i];
            jso.put("freq_user", CpuPerc.format(cpuPerc.getUser())); //CPU的用户使用率
            jso.put("freq_sys", CpuPerc.format(cpuPerc.getSys())); //CPU的系统使用率
            jso.put("freq_wait", CpuPerc.format(cpuPerc.getWait())); //CPU的当前等待率
            jso.put("freq_nice", CpuPerc.format(cpuPerc.getNice())); //CPU的当前错误率
            jso.put("freq_idle", CpuPerc.format(cpuPerc.getIdle())); //CPU的当前空闲率
            jso.put("freq_combined", CpuPerc.format(cpuPerc.getCombined())); //CPU总的使用率
            jsonArray.add(jso);
        }
        jsonObject.put("cpu", jsonArray);
    }

    private void who() throws SigarException {
        Who who[] = sigar.getWhoList();
        if (who != null && who.length > 0) {
            for (int i = 0; i < who.length; i++) {
                // System.out.println("当前系统进程表中的用户名" + String.valueOf(i));
                Who _who = who[i];
                System.out.println("用户控制台:    " + _who.getDevice());
                System.out.println("用户host:    " + _who.getHost());
                // System.out.println("getTime():    " + _who.getTime());
                // 当前系统进程表中的用户名
                System.out.println("当前系统进程表中的用户名:    " + _who.getUser());
            }
        }
    }

    private void file() throws Exception {
        FileSystem fslist[] = sigar.getFileSystemList();
        try {
            for (int i = 0; i < fslist.length; i++) {
                System.out.println("分区的盘符名称" + i);
                FileSystem fs = fslist[i];
                // 分区的盘符名称
                System.out.println("盘符名称:    " + fs.getDevName());
                // 分区的盘符名称
                System.out.println("盘符路径:    " + fs.getDirName());
                System.out.println("盘符标志:    " + fs.getFlags());//
                // 文件系统类型，比如 FAT32、NTFS
                System.out.println("盘符类型:    " + fs.getSysTypeName());
                // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
                System.out.println("盘符类型名:    " + fs.getTypeName());
                // 文件系统类型
                System.out.println("盘符文件系统类型:    " + fs.getType());
                FileSystemUsage usage = null;
                usage = sigar.getFileSystemUsage(fs.getDirName());
                switch (fs.getType()) {
                    case 0: // TYPE_UNKNOWN ：未知
                        break;
                    case 1: // TYPE_NONE
                        break;
                    case 2: // TYPE_LOCAL_DISK : 本地硬盘
                        // 文件系统总大小
                        System.out.println(fs.getDevName() + "总大小:    " + usage.getTotal() + "KB");
                        // 文件系统剩余大小
                        System.out.println(fs.getDevName() + "剩余大小:    " + usage.getFree() + "KB");
                        // 文件系统可用大小
                        System.out.println(fs.getDevName() + "可用大小:    " + usage.getAvail() + "KB");
                        // 文件系统已经使用量
                        System.out.println(fs.getDevName() + "已经使用量:    " + usage.getUsed() + "KB");
                        double usePercent = usage.getUsePercent() * 100D;
                        // 文件系统资源的利用率
                        System.out.println(fs.getDevName() + "资源的利用率:    " + usePercent + "%");
                        break;
                    case 3:// TYPE_NETWORK ：网络
                        break;
                    case 4:// TYPE_RAM_DISK ：闪存
                        break;
                    case 5:// TYPE_CDROM ：光驱
                        break;
                    case 6:// TYPE_SWAP ：页面交换
                        break;
                }
                System.out.println(fs.getDevName() + "读出：    " + usage.getDiskReads());
                System.out.println(fs.getDevName() + "写入：    " + usage.getDiskWrites());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return;
    }

    private void ethernet() throws SigarException {
        String[] ifaces = sigar.getNetInterfaceList();
        for (int i = 0; i < ifaces.length; i++) {
            NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
            if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                    || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                continue;
            }
            System.out.println(cfg.getName() + "IP地址:" + cfg.getAddress());// IP地址
            System.out.println(cfg.getName() + "网关广播地址:" + cfg.getBroadcast());// 网关广播地址
            System.out.println(cfg.getName() + "网卡MAC地址:" + cfg.getHwaddr());// 网卡MAC地址
            System.out.println(cfg.getName() + "子网掩码:" + cfg.getNetmask());// 子网掩码
            System.out.println(cfg.getName() + "网卡描述信息:" + cfg.getDescription());// 网卡描述信息
            System.out.println(cfg.getName() + "网卡类型" + cfg.getType());//
        }
    }

    // 获取进程的相关信息以及对进程信息进行包装
//    @Scheduled(cron = "0/2 * * * * ? ")
    public List<ProcessInfo> getProcessInfo() {
        Ps ps = new Ps();
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
        try {
            long[] pids = sigar.getProcList();
            for (long pid : pids) {
                List<String> list = ps.getInfo(sigar, pid);
                ProcessInfo info = new ProcessInfo();
                for (int i = 0; i <= list.size(); i++) {
                    switch (i) {
                        case 0:
                            info.setPid(list.get(0));
                            break;
                        case 1:
                            info.setUser(list.get(1));
                            break;
                        case 2:
                            info.setStartTime(list.get(2));
                            break;
                        case 3:
                            info.setMemSize(list.get(3));
                            break;
                        case 4:
                            info.setMemUse(list.get(4));
                            break;
                        case 5:
                            info.setMemhare(list.get(5));
                            break;
                        case 6:
                            info.setState(list.get(6));
                            break;
                        case 7:
                            info.setCpuTime(list.get(7));
                            break;
                        case 8:
                            info.setName(list.get(8));
                            break;
                    }
                }
                processInfos.add(info);
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }
        return processInfos;
    }

    //本机操作系统信息
    public void os() {
        // 取当前操作系统的信息
        OperatingSystem OS = OperatingSystem.getInstance();
        // 操作系统内核类型如： 386、486、586等x86
        System.out.println("OS.getArch() = " + OS.getArch());
        System.out.println("OS.getCpuEndian() = " + OS.getCpuEndian());//
        System.out.println("OS.getDataModel() = " + OS.getDataModel());//
        // 系统描述
        System.out.println("OS.getDescription() = " + OS.getDescription());
        System.out.println("OS.getMachine() = " + OS.getMachine());//
        // 操作系统类型
        System.out.println("OS.getName() = " + OS.getName());
        System.out.println("OS.getPatchLevel() = " + OS.getPatchLevel());//
        // 操作系统的卖主
        System.out.println("OS.getVendor() = " + OS.getVendor());
        // 卖主名称
        System.out.println("OS.getVendorCodeName() = " + OS.getVendorCodeName());
        // 操作系统名称
        System.out.println("OS.getVendorName() = " + OS.getVendorName());
        // 操作系统卖主类型
        System.out.println("OS.getVendorVersion() = " + OS.getVendorVersion());
        // 操作系统的版本号
        System.out.println("OS.getVersion() = " + OS.getVersion());
    }

    // 5.网络信息
    // a)当前机器的正式域名
    public String getFQDN() {
        Sigar sigar = null;
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            try {
                sigar = new Sigar();
                return sigar.getFQDN();
            } catch (SigarException ex) {
                return null;
            } finally {
                sigar.close();
            }
        }
    }

    // d)获取网络流量等信息
    private void net() throws Exception {
        String ifNames[] = sigar.getNetInterfaceList();
        for (int i = 0; i < ifNames.length; i++) {
            String name = ifNames[i];
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            System.out.println("网络设备名:    " + name);// 网络设备名
            System.out.println("IP地址:    " + ifconfig.getAddress());// IP地址
            System.out.println("子网掩码:    " + ifconfig.getNetmask());// 子网掩码
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                System.out.println("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
            System.out.println(name + "接收的总包裹数:" + ifstat.getRxPackets());// 接收的总包裹数
            System.out.println(name + "发送的总包裹数:" + ifstat.getTxPackets());// 发送的总包裹数
            System.out.println(name + "接收到的总字节数:" + ifstat.getRxBytes());// 接收到的总字节数
            System.out.println(name + "发送的总字节数:" + ifstat.getTxBytes());// 发送的总字节数
            System.out.println(name + "接收到的错误包数:" + ifstat.getRxErrors());// 接收到的错误包数
            System.out.println(name + "发送数据包时的错误数:" + ifstat.getTxErrors());// 发送数据包时的错误数
            System.out.println(name + "接收时丢弃的包数:" + ifstat.getRxDropped());// 接收时丢弃的包数
            System.out.println(name + "发送时丢弃的包数:" + ifstat.getTxDropped());// 发送时丢弃的包数
        }
    }
}
