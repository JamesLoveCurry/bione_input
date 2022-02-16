package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the RPT_DETAIL_VALID_RESULT_LOGIC database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_VALID_RESULT_LOGIC")
@NamedQuery(name="RptDetailValidResultLogic.findAll", query="SELECT r FROM RptDetailValidResultLogic r")
public class RptDetailValidResultLogic implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHECK_RESULT_ID")
	private String checkResultId;

	@Column(name="CHECK_CFG_ID")
	private String checkCfgId;

	@Column(name="CHECK_TIME")
	private Timestamp checkTime;

	@Column(name="DATA_DATE")
	private String dataDate;

	@Column(name="DATA_UNIT")
	private String dataUnit;

	@Column(name="DIFFER_VAL")
	private BigDecimal differVal;

	@Column(name="EXPRESSION_SHORT_DESC")
	private String expressionShortDesc;

	@Column(name="EXPRESSION_DESC")
	private String expressionDesc;
	
	@Column(name="FLOAT_VAL")
	private BigDecimal floatVal;

	@Column(name="IS_PASS")
	private String isPass;

	@Column(name="ORG_NO")
	private String orgNo;

	@Column(name="ORG_TYPE")
	private String orgType;

	@Column(name="REPLACE_EXPRESSION")
	private String replaceExpression;

	@Column(name="ROW_ID")
	private BigDecimal rowId;

	@Column(name="RPT_TEMPLATE_ID")
	private String rptTemplateId;

	@Column(name="VERIFYT_STS")
	private String verifytSts;

	@Column(name="CHECK_TYPE")
	private String checkType;
	
	@Column(name="CHECK_SRC")
	private String checkSrc;
	
	public RptDetailValidResultLogic() {
	}

	public String getCheckResultId() {
		return this.checkResultId;
	}

	public void setCheckResultId(String checkResultId) {
		this.checkResultId = checkResultId;
	}

	public String getCheckCfgId() {
		return this.checkCfgId;
	}

	public void setCheckCfgId(String checkCfgId) {
		this.checkCfgId = checkCfgId;
	}

	public Timestamp getCheckTime() {
		return this.checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}

	public String getDataDate() {
		return this.dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getDataUnit() {
		return this.dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public BigDecimal getDifferVal() {
		return this.differVal;
	}

	public void setDifferVal(BigDecimal differVal) {
		this.differVal = differVal;
	}

	public String getExpressionShortDesc() {
		return this.expressionShortDesc;
	}

	public void setExpressionShortDesc(String expressionShortDesc) {
		this.expressionShortDesc = expressionShortDesc;
	}

	public BigDecimal getFloatVal() {
		return this.floatVal;
	}

	public void setFloatVal(BigDecimal floatVal) {
		this.floatVal = floatVal;
	}

	public String getIsPass() {
		return this.isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

	public String getOrgNo() {
		return this.orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getOrgType() {
		return this.orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getReplaceExpression() {
		return this.replaceExpression;
	}

	public void setReplaceExpression(String replaceExpression) {
		this.replaceExpression = replaceExpression;
	}

	public BigDecimal getRowId() {
		return this.rowId;
	}

	public void setRowId(BigDecimal rowId) {
		this.rowId = rowId;
	}

	public String getRptTemplateId() {
		return this.rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}

	public String getVerifytSts() {
		return this.verifytSts;
	}

	public void setVerifytSts(String verifytSts) {
		this.verifytSts = verifytSts;
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

	public String getExpressionDesc() {
		return expressionDesc;
	}

	public void setExpressionDesc(String expressionDesc) {
		this.expressionDesc = expressionDesc;
	}
	
}