package com.yusys.bione.plugin.datamodel.vo;

import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;

@SuppressWarnings("serial")
public class RptDatasetVO extends RptSysModuleInfo {
	private String dsName;
	private String dsTypeName;

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getDsTypeName() {
		return dsTypeName;
	}

	public void setDsTypeName(String dsTypeName) {
		this.dsTypeName = dsTypeName;
	}

}
