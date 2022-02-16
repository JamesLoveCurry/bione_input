package com.yusys.bione.plugin.valid.web.vo;

import java.math.BigDecimal;

import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;

public class ValidCfgextWarnVO extends RptValidCfgextWarn{

	private static final long serialVersionUID = -570401087797087938L;

	private String indexNm;
	
	private String levelNum;
	
	private String levelNm;
	
	private String levelType;
	
	private BigDecimal minusRangeVal;
	
	private BigDecimal postiveRangeVal;
	
	private String remindColor;
	
	private String isPassCond;
	
	private String rptCycle;
	
	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getLevelNum() {
		return levelNum;
	}

	public void setLevelNum(String levelNum) {
		this.levelNum = levelNum;
	}

	public String getLevelNm() {
		return levelNm;
	}

	public void setLevelNm(String levelNm) {
		this.levelNm = levelNm;
	}

	public String getLevelType() {
		return levelType;
	}

	public void setLevelType(String levelType) {
		this.levelType = levelType;
	}

	public BigDecimal getMinusRangeVal() {
		return minusRangeVal;
	}

	public void setMinusRangeVal(BigDecimal minusRangeVal) {
		this.minusRangeVal = minusRangeVal;
	}

	public BigDecimal getPostiveRangeVal() {
		return postiveRangeVal;
	}

	public void setPostiveRangeVal(BigDecimal postiveRangeVal) {
		this.postiveRangeVal = postiveRangeVal;
	}

	public String getRemindColor() {
		return remindColor;
	}

	public void setRemindColor(String remindColor) {
		this.remindColor = remindColor;
	}

	public String getIsPassCond() {
		return isPassCond;
	}

	public void setIsPassCond(String isPassCond) {
		this.isPassCond = isPassCond;
	}

	public String getRptCycle() {
		return rptCycle;
	}

	public void setRptCycle(String rptCycle) {
		this.rptCycle = rptCycle;
	}
	
}
