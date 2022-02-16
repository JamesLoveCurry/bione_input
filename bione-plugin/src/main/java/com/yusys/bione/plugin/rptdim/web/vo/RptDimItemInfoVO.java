package com.yusys.bione.plugin.rptdim.web.vo;

import java.util.List;

import com.google.common.collect.Lists;

public class RptDimItemInfoVO {

	private String dimItemNo;

	private String dimItemNm;

	private String dimTypeNo;

	private String remark;

	private String upNo;

	private String startDate;

	private String endDate;

	private String busiDef;

	private String busiRule;

	private String defDept;

	private String useDept;

	private String itemSts;

	private boolean isParent;

	private List<RptDimItemInfoVO> children;

	public String getDimItemNo() {
		return dimItemNo;
	}

	public void setDimItemNo(String dimItemNo) {
		this.dimItemNo = dimItemNo;
	}

	public String getDimItemNm() {
		return dimItemNm;
	}

	public void setDimItemNm(String dimItemNm) {
		this.dimItemNm = dimItemNm;
	}

	public String getDimTypeNo() {
		return dimTypeNo;
	}

	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpNo() {
		return upNo;
	}

	public void setUpNo(String upNo) {
		this.upNo = upNo;
	}

	public List<RptDimItemInfoVO> getChildren() {
		if (children == null) {
			children = Lists.newArrayList();
		}
		return children;
	}

	public void setChildren(List<RptDimItemInfoVO> children) {
		this.children = children;
	}

	public String getBusiDef() {
		return busiDef;
	}

	public void setBusiDef(String busiDef) {
		this.busiDef = busiDef;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getBusiRule() {
		return busiRule;
	}

	public void setBusiRule(String busiRule) {
		this.busiRule = busiRule;
	}

	public String getDefDept() {
		return defDept;
	}

	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}

	public String getUseDept() {
		return useDept;
	}

	public void setUseDept(String useDept) {
		this.useDept = useDept;
	}

	public String getItemSts() {
		return itemSts;
	}

	public void setItemSts(String itemSts) {
		this.itemSts = itemSts;
	}

}