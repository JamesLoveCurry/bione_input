package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;

@SuppressWarnings("serial")
public class RptDesignSrcDimTabVO extends RptDesignCellInfo {
	private String dimTypeNo;
	
	private String isTotal;

	private String dateFormat;
	
	private String isConver;
	
	private String displayLevel;
	
	private String extDirection;
	
	public String getDimTypeNo() {
		return dimTypeNo;
	}

	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public String getIsTotal() {
		return isTotal;
	}

	public void setIsTotal(String isTotal) {
		this.isTotal = isTotal;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getIsConver() {
		return isConver;
	}

	public void setIsConver(String isConver) {
		this.isConver = isConver;
	}

	public String getDisplayLevel() {
		return displayLevel;
	}

	public void setDisplayLevel(String displayLevel) {
		this.displayLevel = displayLevel;
	}

	public String getExtDirection() {
		return extDirection;
	}

	public void setExtDirection(String extDirection) {
		this.extDirection = extDirection;
	}
	
}
