package com.agent.model.monitor;

import com.agent.config.LocalSettings;
import com.agent.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class MonitorInfo {
    private String Id;
    private String sdCode;
    private String companyCode;
    private String cpCode;
    private Integer collectionTime;
    private Integer createTime;
    private Integer sflag;
    private Integer analysysTime;
    private String monitorTypeCode;
    private String monitorInfoCode;
    private Double monitorInfo;
    private String comments;
    private String collectionIp;
    private String collectionMac;

    @Autowired
    static LocalSettings settings;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSdCode() {
        return sdCode;
    }

    public void setSdCode(String sdCode) {
        this.sdCode = sdCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCpCode() {
        return cpCode;
    }

    public void setCpCode(String cpCode) {
        this.cpCode = cpCode;
    }

    public Integer getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Integer collectionTime) {
        this.collectionTime = collectionTime;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getSflag() {
        return sflag;
    }

    public void setSflag(Integer sflag) {
        this.sflag = sflag;
    }

    public Integer getAnalysysTime() {
        return analysysTime;
    }

    public void setAnalysysTime(Integer analysysTime) {
        this.analysysTime = analysysTime;
    }

    public String getMonitorTypeCode() {
        return monitorTypeCode;
    }

    public void setMonitorTypeCode(String monitorTypeCode) {
        this.monitorTypeCode = monitorTypeCode;
    }

    public String getMonitorInfoCode() {
        return monitorInfoCode;
    }

    public void setMonitorInfoCode(String monitorInfoCode) {
        this.monitorInfoCode = monitorInfoCode;
    }

    public Double getMonitorInfo() {
        return monitorInfo;
    }

    public void setMonitorInfo(Double monitorInfo) {
        this.monitorInfo = monitorInfo;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCollectionIp() {
        return collectionIp;
    }

    public void setCollectionIp(String collectionIp) {
        this.collectionIp = collectionIp;
    }

    public String getCollectionMac() {
        return collectionMac;
    }

    public void setCollectionMac(String collectionMac) {
        this.collectionMac = collectionMac;
    }
}
