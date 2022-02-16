package com.yusys.bione.plugin.rptorg.web.vo;

import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;

public class RptOrgInfoVo extends RptOrgInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mgrOrgNm;

	public String getMgrOrgNm() {
		return mgrOrgNm;
	}

	public void setMgrOrgNm(String mgrOrgNm) {
		this.mgrOrgNm = mgrOrgNm;
	}
	
	public String upOrgNm;

	public String getUpOrgNm() {
		return upOrgNm;
	}

	public void setUpOrgNm(String upOrgNm) {
		this.upOrgNm = upOrgNm;
	}
	
	
	
}
