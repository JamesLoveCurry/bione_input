package com.yusys.bione.plugin.rptidx.web.vo;

import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;

@SuppressWarnings("serial")
public class RptIdxMeasureInfoVO extends RptIdxMeasureInfo{

	private String indexNo;
	private long indexVerId;
	public String getIndexNm() {
		return indexNm;
	}
	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}
	private String indexNm;
	
	public String getIndexNo() {
		return indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public long getIndexVerId() {
		return indexVerId;
	}
	public void setIndexVerId(long indexVerId) {
		this.indexVerId = indexVerId;
	}
	
}
