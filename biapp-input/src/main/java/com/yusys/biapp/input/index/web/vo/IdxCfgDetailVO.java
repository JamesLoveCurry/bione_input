package com.yusys.biapp.input.index.web.vo;

import java.util.List;

public class IdxCfgDetailVO {
	
	private String cfgId;//配置ID
	private String idxNo;//指标标识
	private String indexNm; //指标名称
	private String orderNum;//顺序编号
	private String orgMode;//机构方式
	private String cfgNm;//配置名称
	private String orgNo;//组织号
	private String orgNm;//组织名称
	private String ruleId;//规则ID
	private String ruleNm;//规则名称
	//过滤信息
	private String filterFormula;
	
	//维度信息
	private List<DimFilterVO> dimFilterInfos;

	

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getIdxNo() {
		return idxNo;
	}

	public void setIdxNo(String idxNo) {
		this.idxNo = idxNo;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getOrgMode() {
		return orgMode;
	}

	public void setOrgMode(String orgMode) {
		this.orgMode = orgMode;
	}

	public String getCfgNm() {
		return cfgNm;
	}

	public void setCfgNm(String cfgNm) {
		this.cfgNm = cfgNm;
	}
	public List<DimFilterVO> getDimFilterInfos() {
		return dimFilterInfos;
	}

	public void setDimFilterInfos(List<DimFilterVO> dimFilterInfos) {
		this.dimFilterInfos = dimFilterInfos;
	}

	public String getFilterFormula() {
		return filterFormula;
	}

	public void setFilterFormula(String filterFormula) {
		this.filterFormula = filterFormula;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
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

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleNm() {
		return ruleNm;
	}

	public void setRuleNm(String ruleNm) {
		this.ruleNm = ruleNm;
	}
}
