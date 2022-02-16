package com.yusys.bione.plugin.rptmgr.web.vo;

import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;

@SuppressWarnings("serial")
public class RptTmpDataInfoVO extends RptDesignTmpInfo{
	private String rptId;
	private String rptNm;
	private String busiType;
	
	public String getRptNm() {
		return rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	public String getRptId() {
		return rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
}
