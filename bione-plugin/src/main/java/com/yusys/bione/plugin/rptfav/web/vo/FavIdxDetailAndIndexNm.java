package com.yusys.bione.plugin.rptfav.web.vo;

import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDetail;

@SuppressWarnings("serial")
public class FavIdxDetailAndIndexNm extends RptFavIdxDetail{
	private String indexNm;
	
	private String indexType;
	
	private String indexVerId;
	
	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getIndexVerId() {
		return indexVerId;
	}

	public void setIndexVerId(String indexVerId) {
		this.indexVerId = indexVerId;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}
	
}
