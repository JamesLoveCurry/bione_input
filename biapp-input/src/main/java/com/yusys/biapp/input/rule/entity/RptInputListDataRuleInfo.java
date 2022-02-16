package com.yusys.biapp.input.rule.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_INPUT_LIST_DATA_RULE_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_INPUT_LIST_DATA_RULE_INFO")
public class RptInputListDataRuleInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "RULE_ID", unique = true, nullable = false, length = 32)
	private String ruleId;
	
	@Column(name = "RULE_NM", length = 100)
	private String ruleNm;
	
	@Column(name = "RULE_TYPE", length = 10)
	private String ruleType;

	@Column(name = "FIELD_NM", length = 100)
	private String fieldNm;
	
	@Column(name = "MAX_VAL", length = 200)
	private String maxVal;
	
	@Column(name = "MIN_VAL", length = 200)
	private String minVal;

	@Column(name = "VALUE_SET", length = 500)
	private String valueSet;
	
	@Column(length = 200)
	private String regex;
	
	@Column(length = 200)
	private String logic;
	
	@Column(name = "FILTER_CONDITION", length = 500)
	private String filterCondition;
	
	@Column(name = "TIME_RANGE", length = 500)
	private String timeRange;
	
	@Column(name = "ERROR_TIP", length = 500)
	private String errorTip;
	
	@Column(name = "LOGIC_SYS_NO", length = 32)
	private String logicSysNo;
	
	@Column(name = "CREATE_USER", length = 32)
	private String createUser;

	@Column(name = "CREATE_DATE", length = 20)
	private String createDate;

	public RptInputListDataRuleInfo() {
	}

	public String getRuleId() {
		return this.ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getFieldNm() {
		return this.fieldNm;
	}

	public void setFieldNm(String fieldNm) {
		this.fieldNm = fieldNm;
	}

	public String getValueSet() {
		return this.valueSet;
	}

	public void setValueSet(String valueSet) {
		this.valueSet = valueSet;
	}

	public String getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getErrorTip() {
		return this.errorTip;
	}

	public void setErrorTip(String errorTip) {
		this.errorTip = errorTip;
	}

	public String getFilterCondition() {
		return this.filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getLogic() {
		return this.logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getMaxVal() {
		return this.maxVal;
	}

	public void setMaxVal(String maxVal) {
		this.maxVal = maxVal;
	}

	public String getMinVal() {
		return this.minVal;
	}

	public void setMinVal(String minVal) {
		this.minVal = minVal;
	}

	public String getRegex() {
		return this.regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getRuleNm() {
		return this.ruleNm;
	}

	public void setRuleNm(String ruleNm) {
		this.ruleNm = ruleNm;
	}

	public String getRuleType() {
		return this.ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getTimeRange() {
		return this.timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

}