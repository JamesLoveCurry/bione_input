package com.yusys.bione.plugin.regulation.vo;

public class FrsSystemCfgVO {

    private long systemVerId;

    private String busiType;

    private String remark;

    private String systemName;

    private String verEndDate;

    private String verStartDate;

    private String catalogId;

    public long getSystemVerId() {
        return systemVerId;
    }

    public void setSystemVerId(long systemVerId) {
        this.systemVerId = systemVerId;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getVerEndDate() {
        return verEndDate;
    }

    public void setVerEndDate(String verEndDate) {
        this.verEndDate = verEndDate;
    }

    public String getVerStartDate() {
        return verStartDate;
    }

    public void setVerStartDate(String verStartDate) {
        this.verStartDate = verStartDate;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }
}
