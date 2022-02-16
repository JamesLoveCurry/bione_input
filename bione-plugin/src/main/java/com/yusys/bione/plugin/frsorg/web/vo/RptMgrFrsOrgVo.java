package com.yusys.bione.plugin.frsorg.web.vo;

import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;


public class RptMgrFrsOrgVo extends RptOrgInfo{

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
	
	private String rhOrgNo;//人行机构编码
	
	private String rhOrgNm;//人行机构名称
	
	private String dtrctNo;//地区编码

	public String getRhOrgNo() {
		return rhOrgNo;
	}

	public void setRhOrgNo(String rhOrgNo) {
		this.rhOrgNo = rhOrgNo;
	}

	public String getRhOrgNm() {
		return rhOrgNm;
	}

	public void setRhOrgNm(String rhOrgNm) {
		this.rhOrgNm = rhOrgNm;
	}

	public String getDtrctNo() {
		return dtrctNo;
	}

	public void setDtrctNo(String dtrctNo) {
		this.dtrctNo = dtrctNo;
	}
	
	
	
}
