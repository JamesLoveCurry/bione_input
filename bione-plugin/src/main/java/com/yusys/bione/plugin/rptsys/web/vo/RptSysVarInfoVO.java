package com.yusys.bione.plugin.rptsys.web.vo;

import com.yusys.bione.plugin.rptsys.entity.RptSysVarInfo;

@SuppressWarnings("serial")
public class RptSysVarInfoVO extends RptSysVarInfo{
	private String varValueSql;
	private String buttonType;
	private String sourceNm;
	public String getVarValueSql() {
		return varValueSql;
	}
	public void setVarValueSql(String varValueSql) {
		this.varValueSql = varValueSql;
	}
	public String getButtonType() {
		return buttonType;
	}
	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}
	public String getSourceNm() {
		return sourceNm;
	}
	public void setSourceNm(String sourceNm) {
		this.sourceNm = sourceNm;
	}
}
