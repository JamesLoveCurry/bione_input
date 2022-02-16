package com.yusys.bione.plugin.rptidx.web.vo;

import com.yusys.bione.plugin.rptidx.entity.RptIdxDsDimFilter;

@SuppressWarnings("serial")
public class RptIdxDsRelFilterVO extends RptIdxDsDimFilter{
	private String dimNo;
	private String dimNm;
	public String getDimNo() {
		return dimNo;
	}

	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}

	public String getDimNm() {
		return dimNm;
	}

	public void setDimNm(String dimNm) {
		this.dimNm = dimNm;
	}
}
