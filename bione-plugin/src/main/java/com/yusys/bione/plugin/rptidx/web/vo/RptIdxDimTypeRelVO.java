package com.yusys.bione.plugin.rptidx.web.vo;

import java.io.Serializable;

public class RptIdxDimTypeRelVO implements Serializable {

	private static final long serialVersionUID = -3366922933210021472L;

	private String dimNo;
	private String dimTypeNm;
	private String dimType;
	private String judge;
	
	public String getDimNo() {
		return dimNo;
	}
	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}
	public String getDimTypeNm() {
		return dimTypeNm;
	}
	public void setDimTypeNm(String dimTypeNm) {
		this.dimTypeNm = dimTypeNm;
	}
	public String getDimType() {
		return dimType;
	}
	public void setDimType(String dimType) {
		this.dimType = dimType;
	}
	public String getJudge() {
		return judge;
	}
	public void setJudge(String judge) {
		this.judge = judge;
	}
}
