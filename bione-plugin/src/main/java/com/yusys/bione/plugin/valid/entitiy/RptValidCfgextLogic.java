package com.yusys.bione.plugin.valid.entitiy;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the RPT_VALID_CFGEXT_LOGIC database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_CFGEXT_LOGIC")
@ExcelSheet(index="0")
public class RptValidCfgextLogic implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHECK_ID")
	private String checkId;

	@ExcelColumn(index = "A", name = "序号")
	@Column(name = "SERIAL_NUMBER")
	private String serialNumber;

	@ExcelColumn(index = "B", name = "校验公式名称")
	@Column(name="CHECK_NM")
	private String checkNm;
	
	@Column(name="END_DATE")
	private String endDate;

	@Column(name="EXPRESSION_DESC")
	private String expressionDesc;
	
	@Column(name="EXPRESSION_SHORT_DESC")
	private String expressionShortDesc;

	@ExcelColumn(index = "F", name = "容差")
	@Column(name="FLOAT_VAL")
	private BigDecimal floatVal;

	@ExcelColumn(index = "G", name = "数据单位", value = {"01", "02","03","04","05","06"}, text = {"元", "百", "千", "万", "亿", "百分比"}, combox = {"元", "百", "千", "万", "亿", "百分比"})
	@Column(name = "DATA_UNIT")
	private String dataUnit;

	@Column(name="IS_PRE")
	private String isPre;

	@Column(name="IS_SELF_DEF")
	private String isSelfDef;
	
	@ExcelColumn(index = "C", name = "系统内左公式")
	@Column(name="LEFT_EXPRESSION")
	private String leftExpression;

	@ExcelColumn(index = "D", name = "比较符")
	@Column(name="LOGIC_OPER_TYPE")
	private String logicOperType;

	@ExcelColumn(index = "E", name = "系统内右公式")
	@Column(name="RIGHT_EXPRESSION")
	private String rightExpression;

	@Column(name="BUSI_EXPLAIN")
	private String busiExplain;

	@Column(name="START_DATE")
	private String startDate;

	@Column(name="LEFT_FORMULA_INDEX")
	private String leftFormulaIndex;
	
	@Column(name="RIGHT_FORMULA_INDEX")
	private String rightFormulaIndex;

	@Column(name="IS_ORG_FILTER")
	private String isOrgFilter;
	
	@Column(name="ORG_LEVEL")
	private String orgLevel;

	@ExcelColumn(index = "H", name = "公式类型", value={"01","02"},text={"表内","表间"})
	@Column(name="CHECK_TYPE")
	private String checkType;
	
	@ExcelColumn(index = "I", name = "公式来源", value={"01","02"},text={"监管制度","自定义"})
	@Column(name="CHECK_SRC")
	private String checkSrc;
	
	@Column(name="DEF_USER")
	private String defUser;

	@Column(name="DATA_PRECISION")
	private String dataPrecision;

	@Column(name="LOGIC_CHECK_CYCLE")
	private String logicCheckCycle;
	
    public RptValidCfgextLogic() {
    }
    
	public String getCheckId() {
		return this.checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
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

	public String getExpressionShortDesc() {
		return expressionShortDesc;
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

	public String getIsPre() {
		return this.isPre;
	}

	public void setIsPre(String isPre) {
		this.isPre = isPre;
	}

	public String getIsSelfDef() {
		return this.isSelfDef;
	}

	public void setIsSelfDef(String isSelfDef) {
		this.isSelfDef = isSelfDef;
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

	public String getBusiExplain() {
		return this.busiExplain;
	}

	public void setBusiExplain(String busiExplain) {
		this.busiExplain = busiExplain;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getLeftFormulaIndex() {
		return leftFormulaIndex;
	}

	public void setLeftFormulaIndex(String leftFormulaIndex) {
		this.leftFormulaIndex = leftFormulaIndex;
	}

	public String getRightFormulaIndex() {
		return rightFormulaIndex;
	}

	public void setRightFormulaIndex(String rightFormulaIndex) {
		this.rightFormulaIndex = rightFormulaIndex;
	}

	public String getIsOrgFilter() {
		return isOrgFilter;
	}

	public void setIsOrgFilter(String isOrgFilter) {
		this.isOrgFilter = isOrgFilter;
	}

	public String getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(String orgLevel) {
		this.orgLevel = orgLevel;
	}

	public String getCheckNm() {
		return checkNm;
	}

	public void setCheckNm(String checkNm) {
		this.checkNm = checkNm;
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

	public String getDefUser() {
		return defUser;
	}

	public void setDefUser(String defUser) {
		this.defUser = defUser;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(String dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getLogicCheckCycle() {
		return logicCheckCycle;
	}

	public void setLogicCheckCycle(String logicCheckCycle) {
		this.logicCheckCycle = logicCheckCycle;
	}
}