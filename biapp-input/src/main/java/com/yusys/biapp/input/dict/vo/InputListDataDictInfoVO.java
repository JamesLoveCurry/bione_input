package com.yusys.biapp.input.dict.vo;

import com.yusys.biapp.input.dict.entity.RptInputListDataDictInfo;

@SuppressWarnings("serial")
public class InputListDataDictInfoVO extends RptInputListDataDictInfo{
    
    private String catalogName;
    private String dsName;
    public String getCatalogName() {
        return catalogName;
    }
    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }
    public String getDsName() {
        return dsName;
    }
    public void setDsName(String dsName) {
        this.dsName = dsName;
    }
    
    public InputListDataDictInfoVO(RptInputListDataDictInfo info){
        this.setCatalogId(info.getCatalogId());
        this.setCreateTime(info.getCreateTime());
        this.setCreateUser(info.getCreateUser());
        this.setDictId(info.getDictId());
        this.setDictName(info.getDictName());
        this.setDictType(info.getDictType());
        this.setDsId(info.getDsId());
        this.setLogicSysNo(info.getLogicSysNo());
        this.setShowType(info.getShowType());
        this.setSqlText(info.getSqlText());
        this.setStaticContent(info.getStaticContent());
    }
    
    
    
}
