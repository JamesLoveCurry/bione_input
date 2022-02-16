package com.yusys.bione.plugin.datashow.web.vo;

import javax.persistence.Column;

public class RptOrgInfoVO {
	@Column(name = "ORG_NO")
	private String orgNo;
	@Column(name = "ORG_NM")
	private String orgNm;

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

}
