package com.agent.schedules;

import com.agent.client.CxfClient;
import com.agent.config.LocalSettings;
import com.agent.config.SigarUtils;
import com.agent.model.monitor.MonitorInfo;
import com.agent.utils.DateUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hyperic.sigar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by zjb on 2018/7/2.
 */
@Component
public class CPUSchedules {
    private Sigar sigar = SigarUtils.sigar;
    @Autowired
    CxfClient cxfClient;
    @Autowired
    LocalSettings settings;

    @Scheduled(cron = "0/2 * * * * ?")
    public void cpuCombinedPerc() throws SigarException {
        MonitorInfo info = new MonitorInfo();
        info.setCollectionIp(settings.getLocalIp());
        info.setCollectionMac(settings.getLocalMac());
        info.setCollectionTime(DateUtils.currentTimeStamp());
        CpuPerc cpuPercList[] = sigar.getCpuPercList();
        for (int i = 0, len = cpuPercList.length; i < len; i++) {// 不管是单块CPU还是多CPU都适用
            CpuPerc cpuPerc = cpuPercList[i];
            double cpuPercSys = cpuPerc.getCombined();//CPU的系统使用率
//            String format = CpuPerc.format(cpuPerc.getSys());//CPU的系统使用率
            info.setMonitorInfo(cpuPercSys);
            cxfClient.sendMonitorInfo(info, "cpuCombinedPerc");
        }
    }

    public void cpu() throws SigarException {
        CpuInfo infos[] = sigar.getCpuInfoList();
        CpuPerc cpuPercList[] = sigar.getCpuPercList();
//        Cpu[] cpuList = sigar.getCpuList();
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
//        jsonObject.put("cpu", jsonArray);
        cxfClient.sendMonitorInfo(jsonArray, "cpu");
    }
}
