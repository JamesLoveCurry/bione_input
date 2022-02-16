package com.yusys.biapp.input.template.vo;

import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;

@SuppressWarnings("serial")
public class RptInputLstTempleInfoVO extends RptInputLstTempleInfo{
	private String catalogName;
	
	private String dsName;


	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
}
