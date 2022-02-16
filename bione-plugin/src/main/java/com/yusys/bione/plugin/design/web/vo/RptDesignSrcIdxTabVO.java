package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;

@SuppressWarnings("serial")
public class RptDesignSrcIdxTabVO extends RptDesignCellInfo{

	private String indexNo;
	
	private String dsId;
	
	private String sortMode;
	
	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}


	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getSortMode() {
		return sortMode;
	}

	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}

}
