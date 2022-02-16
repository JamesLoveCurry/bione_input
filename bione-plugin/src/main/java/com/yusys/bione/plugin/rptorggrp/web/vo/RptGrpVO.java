package com.yusys.bione.plugin.rptorggrp.web.vo;

import com.yusys.bione.plugin.rptorggrp.entity.RptOrgGrp;

@SuppressWarnings("serial")
public class RptGrpVO extends RptOrgGrp{
	
	private String isUse;

	private String createOrgNm;

	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public String getCreateOrgNm() {
		return createOrgNm;
	}

	public void setCreateOrgNm(String createOrgNm) {
		this.createOrgNm = createOrgNm;
	}
}
