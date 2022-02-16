package com.yusys.bione.plugin.idxplan.web.vo;

import java.math.BigDecimal;

import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResult;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;

public class RptIdxPlanResultVO {
	
	private RptIdxInfo info;
	
	private RptOrgInfo org;
	
	private RptIdxPlanvalResult planval;
	
	private RptDimItemInfo dim;
	
	private String indexCatalogNo;
	
	private String indexNo;

	private String indexVerId;
	
	private String indexNm;
	
	private String indexType;
	
	private String orgNo;
	
	private String orgNm;
	
	private String dataDate; 	
	
	private String currencyId;
	
	public String getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	private String currency;

	private BigDecimal indexVal;
	
	public RptIdxPlanResultVO() {
	}

	public RptIdxPlanResultVO(RptIdxInfo info, com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResult planval,
			RptDimItemInfo dim,RptOrgInfo org) {
		this.info = info;
		this.planval = planval;
		this.dim = dim;
		this.org = org;
		this.indexCatalogNo = info.getIndexCatalogNo();
		this.indexNo = info.getId().getIndexNo();
		this.indexVerId = String.valueOf(info.getId().getIndexVerId());
		this.indexNm = info.getIndexNm();
		this.indexType = info.getIndexType();
		this.orgNo = org.getId().getOrgNo(); 
		this.orgNm = org.getOrgNm();	
		this.dataDate = planval.getId().getDataDate();
		this.currencyId = dim.getId().getDimItemNo();
		this.currency = dim.getDimItemNm();
		this.indexVal = planval.getIndexVal();
	}
	
	public RptIdxPlanResultVO(RptIdxInfo info, com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResult planval,
			RptOrgInfo org) {
		this.info = info;
		this.planval = planval;
		this.org = org;
		this.indexCatalogNo = info.getIndexCatalogNo();
		this.indexNo = info.getId().getIndexNo();
		this.indexNm = info.getIndexNm();
		this.indexType = info.getIndexType();
		this.orgNo = org.getId().getOrgNo(); 
		this.orgNm = org.getOrgNm();	
		this.dataDate = planval.getId().getDataDate();
		this.currency = planval.getId().getCurrency();
		this.indexVal = planval.getIndexVal();
	}
	
	public RptIdxPlanResultVO(String indexNo, String dataDate, String orgNo, String currency, BigDecimal indexVal) {
		//super();
		this.indexNo = indexNo;
		this.dataDate = dataDate;
		this.orgNo = orgNo;
		this.currency = currency;
		this.indexVal = indexVal;
	}
	
	public RptIdxInfo getInfo() {
		return info;
	}

	public void setInfo(RptIdxInfo info) {
		this.info = info;
	}

	public RptIdxPlanvalResult getPlanval() {
		return planval;
	}

	public void setPlanval(RptIdxPlanvalResult planval) {
		this.planval = planval;
	}

	public RptDimItemInfo getDim() {
		return dim;
	}

	public void setDim(RptDimItemInfo dim) {
		this.dim = dim;
	}
	
	public RptOrgInfo getOrg() {
		return org;
	}

	public void setOrg(RptOrgInfo org) {
		this.org = org;
	}

	public String getIndexCatalogNo() {
		return indexCatalogNo;
	}

	public void setIndexCatalogNo(String indexCatalogNo) {
		this.indexCatalogNo = indexCatalogNo;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
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

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

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

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	
	public BigDecimal getIndexVal() {
		return indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}
}
