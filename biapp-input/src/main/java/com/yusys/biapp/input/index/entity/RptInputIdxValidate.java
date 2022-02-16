package com.yusys.biapp.input.index.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_IDX_VALIDATE")
public class RptInputIdxValidate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3868861296017960507L;

	@Id
	@Column(name="RULE_ID")
	private String ruleId;

	@Column(name="RULE_NM")
	private String ruleNm;

	@Column(name="RULE_TYPE")
	private String ruleType;
	
	@Column(name="RULE_VAL")
	private BigDecimal ruleVal;

	@Column(name="SQL_EXPRESSION")
	private String sqlExpression;
	
	@Column(name="SOURCE_ID")
	private String sourceId;
	
	@Column(name="SYMBOL")
	private String symbol;
	
	@Column(name="INDEX_NO")
	private String indexNo;
	
	
    public RptInputIdxValidate() {
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

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public BigDecimal getRuleVal() {
		return ruleVal;
	}

	public void setRuleVal(BigDecimal ruleVal) {
		this.ruleVal = ruleVal;
	}

	public String getSqlExpression() {
		return sqlExpression;
	}

	public void setSqlExpression(String sqlExpression) {
		this.sqlExpression = sqlExpression;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
}