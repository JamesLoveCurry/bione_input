package com.yusys.bione.plugin.rptidx.web.vo;

import java.io.Serializable;
import java.util.List;

import com.yusys.bione.plugin.rptidx.entity.RptIdxDsDimFilter;

@SuppressWarnings("serial")
public class RptIdxDsRelVO implements Serializable{
	private String rptId;
	private String indexNo;
	private String indexVerId;
	private String indexNm;
	private String enNm;
	private String cnNm;
	private String colId;
	private String maxIndexVerId;
	private String setId;
	private String setNm;
	private String rptItemNm;
	private String isSum;
	private List<RptIdxDsDimFilter> dimfilterInfo;
	private String filterFormula;
	public String getRptId() {
		return rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
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
	public String getEnNm() {
		return enNm;
	}
	public void setEnNm(String enNm) {
		this.enNm = enNm;
	}
	public String getColId() {
		return colId;
	}
	public void setColId(String colId) {
		this.colId = colId;
	}
	public String getCnNm() {
		return cnNm;
	}
	public void setCnNm(String cnNm) {
		this.cnNm = cnNm;
	}
	public String getMaxIndexVerId() {
		return maxIndexVerId;
	}
	public void setMaxIndexVerId(String maxIndexVerId) {
		this.maxIndexVerId = maxIndexVerId;
	}
	
	public String getSetId() {
		return setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
	}
	public String getSetNm() {
		return setNm;
	}
	public void setSetNm(String setNm) {
		this.setNm = setNm;
	}
	public List<RptIdxDsDimFilter> getDimfilterInfo() {
		return dimfilterInfo;
	}
	public void setDimfilterInfo(List<RptIdxDsDimFilter> dimfilterInfo) {
		this.dimfilterInfo = dimfilterInfo;
	}
	public String getRptItemNm() {
		return rptItemNm;
	}
	public void setRptItemNm(String rptItemNm) {
		this.rptItemNm = rptItemNm;
	}
	public String getIsSum() {
		return isSum;
	}
	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}
	public String getFilterFormula() {
		return filterFormula;
	}
	public void setFilterFormula(String filterFormula) {
		this.filterFormula = filterFormula;
	}
	
	
	
}
