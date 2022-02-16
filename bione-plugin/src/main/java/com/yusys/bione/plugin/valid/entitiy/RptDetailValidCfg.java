package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;


/**
 * The persistent class for the RPT_DETAIL_VALID_CFG database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_VALID_CFG")
@NamedQuery(name="RptDetailValidCfg.findAll", query="SELECT r FROM RptDetailValidCfg r")
public class RptDetailValidCfg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHECK_ID")
	private String checkId;
	
	@Column(name="RPT_TEMPLATE_ID")
	private String rptTemplateId;

	@Column(name="LEFT_EXPRESSION")
	private String leftExpression;

	@Column(name="LOGIC_OPER_TYPE")
	private String logicOperType;

	@Column(name="RIGHT_EXPRESSION")
	private String rightExpression;
	
	@Column(name="EXPRESSION_DESC")
	private String expressionDesc;
	
	@Column(name="FLOAT_VAL")
	private BigDecimal floatVal;

	@Column(name="BUSI_EXPLAIN")
	private String busiExplain;
	
	@Column(name="IS_PRE")
	private String isPre;

	@Column(name="PRE_LEFT_EXPRESSION")
	private String preLeftExpression;

	@Column(name="PRE_OPER_TYPE")
	private String preOperType;
	
	@Column(name="PRE_RIGHT_EXPRESSION")
	private String preRightExpression;

	@Column(name="CHECK_TYPE")
	private String checkType;
	
	@Column(name="CHECK_SRC")
	private String checkSrc;
	
	@Column(name="START_DATE")
	private String startDate;

	@Column(name="END_DATE")
	private String endDate;
	
	@Column(name="EXP_TYPE")
	private String expType;
	
	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
	}

	public RptDetailValidCfg() {
	}

	public String getCheckId() {
		return this.checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getBusiExplain() {
		return this.busiExplain;
	}

	public void setBusiExplain(String busiExplain) {
		this.busiExplain = busiExplain;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getExpressionDesc() {
		return this.expressionDesc;
	}

	public void setExpressionDesc(String expressionDesc) {
		this.expressionDesc = expressionDesc;
	}

	public BigDecimal getFloatVal() {
		return this.floatVal;
	}

	public void setFloatVal(BigDecimal floatVal) {
		this.floatVal = floatVal;
	}

	public String getIsPre() {
		return this.isPre;
	}

	public void setIsPre(String isPre) {
		this.isPre = isPre;
	}

	public String getPreLeftExpression() {
		return preLeftExpression;
	}

	public void setPreLeftExpression(String preLeftExpression) {
		this.preLeftExpression = preLeftExpression;
	}

	public String getPreOperType() {
		return preOperType;
	}

	public void setPreOperType(String preOperType) {
		this.preOperType = preOperType;
	}

	public String getPreRightExpression() {
		return preRightExpression;
	}

	public void setPreRightExpression(String preRightExpression) {
		this.preRightExpression = preRightExpression;
	}

	public String getLeftExpression() {
		return this.leftExpression;
	}

	public void setLeftExpression(String leftExpression) {
		this.leftExpression = leftExpression;
	}

	public String getLogicOperType() {
		return this.logicOperType;
	}

	public void setLogicOperType(String logicOperType) {
		this.logicOperType = logicOperType;
	}

	public String getRightExpression() {
		return this.rightExpression;
	}

	public void setRightExpression(String rightExpression) {
		this.rightExpression = rightExpression;
	}

	public String getRptTemplateId() {
		return this.rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getCheckSrc() {
		return checkSrc;
	}

	public void setCheckSrc(String checkSrc) {
		this.checkSrc = checkSrc;
	}
}