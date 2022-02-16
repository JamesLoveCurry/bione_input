package com.yusys.bione.plugin.rptidx.entity;


import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_idx_calc_rule database table.
 * 
 */
@Entity
@Table(name="rpt_idx_calc_rule")
public class RptIdxCalcRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RULE_ID")
	private String ruleId;

	@Column(name="RULE_DESC")
	private String ruleDesc;

	@Column(name="RULE_FREQ")
	private String ruleFreq;

	@Column(name="RULE_NM")
	private String ruleNm;

	@Column(name="RULE_TEMPLATE")
	private String ruleTemplate;

	@Column(name="RULE_TYPE")
	private String ruleType;
	
	@Column(name="SORT_ORDER")
	private BigDecimal sortOrder;

    public RptIdxCalcRule() {
    }

	public String getRuleId() {
		return this.ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleDesc() {
		return this.ruleDesc;
	}

	public void setRuleDesc(String ruleDesc) {
		this.ruleDesc = ruleDesc;
	}

	public String getRuleFreq() {
		return this.ruleFreq;
	}

	public void setRuleFreq(String ruleFreq) {
		this.ruleFreq = ruleFreq;
	}

	public String getRuleNm() {
		return this.ruleNm;
	}

	public void setRuleNm(String ruleNm) {
		this.ruleNm = ruleNm;
	}

	public String getRuleTemplate() {
		return this.ruleTemplate;
	}

	public void setRuleTemplate(String ruleTemplate) {
		this.ruleTemplate = ruleTemplate;
	}

	public String getRuleType() {
		return this.ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public BigDecimal getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(BigDecimal sortOrder) {
		this.sortOrder = sortOrder;
	}

}