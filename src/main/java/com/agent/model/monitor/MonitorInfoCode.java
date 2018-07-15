package com.agent.model.monitor;

public class MonitorInfoCode {
    private String Id;//
    private String code;//信息项编码
    private String name;
    private String monitorTypeCode;
    private Double thresholdMin;
    private Double thresholdMax;
    private String failureCode;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonitorTypeCode() {
        return monitorTypeCode;
    }

    public void setMonitorTypeCode(String monitorTypeCode) {
        this.monitorTypeCode = monitorTypeCode;
    }

    public Double getThresholdMin() {
        return thresholdMin;
    }

    public void setThresholdMin(Double thresholdMin) {
        this.thresholdMin = thresholdMin;
    }

    public Double getThresholdMax() {
        return thresholdMax;
    }

    public void setThresholdMax(Double thresholdMax) {
        this.thresholdMax = thresholdMax;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }
}
