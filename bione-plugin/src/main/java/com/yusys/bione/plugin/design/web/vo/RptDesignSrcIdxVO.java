package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;

@SuppressWarnings("serial")
public class RptDesignSrcIdxVO extends RptDesignCellInfo{

	private String indexNo;

	private String dsId;
	
	private String isSum;
	
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

	public String getIsSum() {
		return isSum;
	}

	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}
	
}
