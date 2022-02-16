package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="RPT_VALID_LOGIC_ORG")
public class RptValidLogicOrg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHECK_ID")
	private String checkId;
	
	@Id
	@Column(name="CHECK_ORG")
	private String checkOrg;
	
	@Column(name="TEMPLATE_ID")
	private String templateId;
	
	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getCheckOrg() {
		return checkOrg;
	}

	public void setCheckOrg(String checkOrg) {
		this.checkOrg = checkOrg;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
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

	@Column(name="START_DATE")
	private String startDate;
	
	@Column(name="END_DATE")
	private String endDate;
	
	
	
}