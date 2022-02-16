package com.yusys.bione.plugin.design.web.vo;


import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;

@SuppressWarnings("serial")
public class RptDesignSrcDsVO extends RptDesignCellInfo{
	private String columnId;

	private String dsId;

	private String extDirection;

	private String extMode;

	private String isExt;

	private String isFilt;

	private String isGrp;

	private String sumMode;
	
	private String isConver;

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getExtDirection() {
		return extDirection;
	}

	public void setExtDirection(String extDirection) {
		this.extDirection = extDirection;
	}

	public String getExtMode() {
		return extMode;
	}

	public void setExtMode(String extMode) {
		this.extMode = extMode;
	}

	public String getIsExt() {
		return isExt;
	}

	public void setIsExt(String isExt) {
		this.isExt = isExt;
	}

	public String getIsFilt() {
		return isFilt;
	}

	public void setIsFilt(String isFilt) {
		this.isFilt = isFilt;
	}

	public String getIsGrp() {
		return isGrp;
	}

	public void setIsGrp(String isGrp) {
		this.isGrp = isGrp;
	}

	public String getSumMode() {
		return sumMode;
	}

	public void setSumMode(String sumMode) {
		this.sumMode = sumMode;
	}

	public String getIsConver() {
		return isConver;
	}

	public void setIsConver(String isConver) {
		this.isConver = isConver;
	}
	
	
}
