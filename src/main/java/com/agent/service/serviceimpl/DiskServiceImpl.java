package com.agent.service.serviceimpl;

import com.agent.service.DiskService;
import org.apache.commons.io.FileSystemUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zjb on 2018/6/28.
 */
@Service
public class DiskServiceImpl implements DiskService {
    /**
     * 获取分区的使用占用率(windows)
     *
     * @param path(举例 d:  c:)
     * @return
     */
    @Override
    public float getPatitionUsage(String path) {
        File f = new File(path);
        long total = f.getTotalSpace();
        long free = f.getFreeSpace();
        long used = total - free;
        float usage = (float) used / total;
        return usage;
    }

    /**
     * 功能：可用磁盘(windows :默认计算项目所在磁盘可用大小 也适用于Linux)
     */
    @Override
    public int disk() {
        try {
            long total = FileSystemUtils.freeSpaceKb("/");
            double disk = (double) total / 1024;
            return (int) disk;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
