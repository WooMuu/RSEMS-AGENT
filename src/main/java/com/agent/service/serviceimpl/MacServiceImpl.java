package com.agent.service.serviceimpl;

import com.agent.service.MacService;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by zjb on 2018/6/28.
 */
@Service
public class MacServiceImpl implements MacService {
    /**
     * 获取MAC地址(windows)
     *
     * @return
     */
    @Override
    public String getMac() {
        NetworkInterface byInetAddress;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            byInetAddress = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = byInetAddress.getHardwareAddress();
            return getMacFromBytes(hardwareAddress);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("获取mac地址失败：" + e.getMessage());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("获取mac地址失败：" + e.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("获取mac地址失败：" + e.getMessage());
        }
        return null;
    }

    private String getMacFromBytes(byte[] bytes) {
        StringBuffer mac = new StringBuffer();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toLowerCase();
    }

    /**
     * 获取MAC(Linux)
     *
     * @return
     */
    @Override
    public String getMacLinux() {
        String result = "";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();
                System.out.println("network : " + network);
                byte[] mac = network.getHardwareAddress();
                if (mac == null) {
                    System.out.println("null mac");
                } else {
                    System.out.print("MAC address : ");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i],
                                (i < mac.length - 1) ? ":" : ""));
                    }
                    result = sb.toString();
                    System.out.println(sb.toString());
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return result;
    }


}
