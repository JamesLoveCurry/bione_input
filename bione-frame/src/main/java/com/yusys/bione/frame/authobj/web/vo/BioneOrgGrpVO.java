package com.yusys.bione.frame.authobj.web.vo;


import com.yusys.bione.frame.authobj.entity.BioneOrgGrp;


/**
 * The persistent class for the rpt_org_grp database table.
 * 
 */
public class BioneOrgGrpVO extends BioneOrgGrp {

	private String createOrgNm;

	public String getCreateOrgNm() {
		return createOrgNm;
	}

	public void setCreateOrgNm(String createOrgNm) {
		this.createOrgNm = createOrgNm;
	}
}