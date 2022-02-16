package com.yusys.bione.plugin.design.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class IndexImpParamVO {
	private String queryType;
	private String templateId;
	private BigDecimal verId;
	private List<String> dimNo;
	private List<Map<String,Object>> searchArg;
	private String dataDate;
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public List<String> getDimNo() {
		return dimNo;
	}
	public void setDimNo(List<String> dimNo) {
		this.dimNo = dimNo;
	}
	public BigDecimal getVerId() {
		return verId;
	}
	public void setVerId(BigDecimal verId) {
		this.verId = verId;
	}
	public List<Map<String,Object>> getSearchArg() {
		return searchArg;
	}
	public void setSearchArg(List<Map<String,Object>> searchArg) {
		this.searchArg = searchArg;
	}
	public String getDataDate() {
		return dataDate;
	}
	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}
	
}
