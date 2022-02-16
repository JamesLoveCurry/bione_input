package com.yusys.bione.plugin.idxplan.web.vo;

import java.math.BigDecimal;

import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;

public class RptIdxPlanvalInfoVO {
	
	private RptIdxInfo info;
	
	private RptIdxPlanvalInfo limit;
	
	private RptOrgInfo org;
	
	private String indexCatalogNo;
	
	private String indexNo;

	private String indexVerId;
	
	private String indexNm;
	
	private String indexType;
	
	private String orgNo;
	
	private String orgNm;
	
	private BigDecimal planVal;
	
	public RptIdxPlanvalInfoVO() {
	}

	public RptIdxPlanvalInfoVO(RptIdxInfo info, com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalInfo limit,
			RptOrgInfo org) {
		this.info = info;
		this.limit = limit;
		this.org = org;
		this.indexCatalogNo = info.getIndexCatalogNo();
		this.indexNo = info.getId().getIndexNo();
		this.indexVerId = String.valueOf(info.getId().getIndexVerId());
		this.indexNm = info.getIndexNm();
		this.indexType = info.getIndexType();
		this.orgNo = org.getId().getOrgNo(); 
		this.orgNm = org.getOrgNm();	
		this.planVal = limit.getPlanVal();
	}
	
	public RptIdxPlanvalInfoVO(String indexNo, String indexVerId, String orgNo, BigDecimal planVal) {
		//super();
		this.indexNo = indexNo;
		this.indexVerId = indexVerId;
		this.orgNo = orgNo;
		this.planVal = planVal;
	}
	
	public RptIdxInfo getInfo() {
		return info;
	}

	public void setInfo(RptIdxInfo info) {
		this.info = info;
	}

	public RptIdxPlanvalInfo getLimit() {
		return limit;
	}

	public void setLimit(RptIdxPlanvalInfo limit) {
		this.limit = limit;
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

	public BigDecimal getPlanVal() {
		return planVal;
	}

	public void setPlanVal(BigDecimal planVal) {
		this.planVal = planVal;
	}
}
