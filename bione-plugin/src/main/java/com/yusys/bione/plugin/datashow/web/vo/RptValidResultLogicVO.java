package com.yusys.bione.plugin.datashow.web.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;


/**
 * The persistent class for the RPT_ENGINE_CHECK_STS database table.
 * 
 */
public class RptValidResultLogicVO{
	
	@Column(name="CHECK_NM")
	private String checkNm;
	
	@Column(name="DIM_CHECK")
	private String dimCheck;
	
	@Column(name="CHECK_TIME")
	private Timestamp checkTime;

	@Column(name="VALID_GID")
	private String validGid;
	
	@Column(name="D_VAL")
	private BigDecimal dVal;
	
	@Column(name="LEVEL_NUM")
	private String levelNum;
	
	@Column(name="CURR_VAL")
	private BigDecimal currVal;
	
	@Column(name="COMPARE_VAL")
	private BigDecimal compareVal;
	
	@Column(name="IS_PASS")
	private String isPass;
	
	private String expressionDesc;

	public String getExpressionDesc() {
		return expressionDesc;
	}

	public void setExpressionDesc(String expressionDesc) {
		this.expressionDesc = expressionDesc;
	}

	public String getCheckNm() {
		return checkNm;
	}

	public void setCheckNm(String checkNm) {
		this.checkNm = checkNm;
	}

	public String getDimCheck() {
		return dimCheck;
	}

	public void setDimCheck(String dimCheck) {
		this.dimCheck = dimCheck;
	}

	public Timestamp getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}

	public String getValidGid() {
		return validGid;
	}

	public void setValidGid(String validGid) {
		this.validGid = validGid;
	}

	public BigDecimal getdVal() {
		return dVal;
	}

	public void setdVal(BigDecimal dVal) {
		this.dVal = dVal;
	}

	public String getLevelNum() {
		return levelNum;
	}

	public void setLevelNum(String levelNum) {
		this.levelNum = levelNum;
	}

	public BigDecimal getCurrVal() {
		return currVal;
	}

	public void setCurrVal(BigDecimal currVal) {
		this.currVal = currVal;
	}

	public BigDecimal getCompareVal() {
		return compareVal;
	}

	public void setCompareVal(BigDecimal compareVal) {
		this.compareVal = compareVal;
	}

	public String getIsPass() {
		return isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}
	
}