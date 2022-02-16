package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="RPT_VALID_CFGEXT_ORGMERGE")
public class RptValidCfgextOrgmerge implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHECK_ID")
	private String checkId;
	
	@Column(name="SUM_INDEX_NO")
	private String sumIndexNo;
	
	@Column(name="SUM_TEMPLATE_ID")
	private String sumTemplateId;

	@Column(name="BRANCH_INDEX_NO")
	private String branchIndexNo;

	@Column(name="BRANCH_TEMPLATE_ID")
	private String branchTemplateId;
	
	@Column(name="CHECK_DESC")
	private String checkDesc;
	
	@Column(name="START_DATE")
	private String startDate;
	
	@Column(name="END_DATE")
	private String endDate;

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getSumIndexNo() {
		return sumIndexNo;
	}

	public void setSumIndexNo(String sumIndexNo) {
		this.sumIndexNo = sumIndexNo;
	}

	public String getSumTemplateId() {
		return sumTemplateId;
	}

	public void setSumTemplateId(String sumTemplateId) {
		this.sumTemplateId = sumTemplateId;
	}

	public String getBranchIndexNo() {
		return branchIndexNo;
	}

	public void setBranchIndexNo(String branchIndexNo) {
		this.branchIndexNo = branchIndexNo;
	}

	public String getBranchTemplateId() {
		return branchTemplateId;
	}

	public void setBranchTemplateId(String branchTemplateId) {
		this.branchTemplateId = branchTemplateId;
	}

	public String getCheckDesc() {
		return checkDesc;
	}

	public void setCheckDesc(String checkDesc) {
		this.checkDesc = checkDesc;
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
}