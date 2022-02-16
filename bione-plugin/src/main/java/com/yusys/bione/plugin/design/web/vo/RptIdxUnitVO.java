package com.yusys.bione.plugin.design.web.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RptIdxUnitVO implements Serializable{

	private String indexNo;
	
	private String templateUnit;
	
	private String dataUnit;
	
	private String displayFormat;

	public String getTemplateUnit() {
		return templateUnit;
	}

	public void setTemplateUnit(String templateUnit) {
		this.templateUnit = templateUnit;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getDisplayFormat() {
		return displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}
	
}
