package com.yusys.bione.plugin.valid.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 校验公式导出VO
 * @author chenhx
 * 
 *
 */
@SuppressWarnings("serial")
@ExcelSheet(index = "0", name = "校验公式", firstRow = 1)
public class CfgextLogicVO implements Serializable, AnnotationValidable {
	@BioneFieldValid(length = 100)
	private String rptId;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "A", name = "报表名称", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String rptNm;
	@BioneFieldValid(length = 100)
	private String cfgId;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "B", name = "校验ID", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkId;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "C", name = "校验序号", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String serialNumber;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "D", name = "表达式描述", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String expressionShortDesc;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "E", name = "左表达式", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String leftExpression;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "F", name = "逻辑运算类型", relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaSymbol","symbolNm","symbolNm"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String logicOperType;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "G", name = "右表达式", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String rightExpression;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "H", name = "容差", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private BigDecimal floatVal;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "I", name = "数据单位", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT, value = {"01", "02","03","04","05","06"}, text = {"元", "百", "千", "万", "亿", "百分比"}, combox = {"元", "百", "千", "万", "亿", "百分比"})
	private String dataUnit;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "J", name = "开始时间", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String startDate;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "K", name = "结束时间", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String endDate;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "L", name = "业务说明", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String busiExplain;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "M", name = "是否预校验", value={"0","1"}, text={"否","是"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String isPre;
	@BioneFieldValid(length = 100)
	private String isSelfDef;
	@BioneFieldValid(length = 100)
	private String expressionDesc;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "N", name = "是否机构过滤", value={"N","Y"}, text={"否","是"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String isOrgFilter;
	@BioneFieldValid(length = 100)
	/*@ExcelColumn(index = "O", name = "生效机构层级", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String orgLevel;*/	
	@ExcelColumn(index = "O", name = "过滤机构编号", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkOrg;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "P", name = "公式类型", value={"01","02"},text={"表内","表间"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkType;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "Q", name = "公式来源", value={"01","02"},text={"监管制度","自定义"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkSrc;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "R", name = "精度", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String dataPrecision;	
	private String orgNm;
	private String orgNo;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "S", name = "公式类型", value={"01","02","03","04","10","11","12"},text={"日","月","季","年","周","旬","半年"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String logicCheckCycle;

	public String getExpressionShortDesc() {
		return expressionShortDesc;
	}

	public void setExpressionShortDesc(String expressionShortDesc) {
		this.expressionShortDesc = expressionShortDesc;
	}

	public String getRptId() {
		return rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getRptNm() {
		return rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getExcelRowNo() {
		return excelRowNo;
	}

	public void setExcelRowNo(int excelRowNo) {
		this.excelRowNo = excelRowNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	private int excelRowNo;
	private String sheetName;

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getExpressionDesc() {
		return expressionDesc;
	}

	public void setExpressionDesc(String expressionDesc) {
		this.expressionDesc = expressionDesc;
	}

	public String getBusiExplain() {
		return busiExplain;
	}

	public void setBusiExplain(String busiExplain) {
		this.busiExplain = busiExplain;
	}

	public String getIsPre() {
		return isPre;
	}

	public void setIsPre(String isPre) {
		this.isPre = isPre;
	}

	public String getIsSelfDef() {
		return isSelfDef;
	}

	public void setIsSelfDef(String isSelfDef) {
		this.isSelfDef = isSelfDef;
	}

	public BigDecimal getFloatVal() {
		return floatVal;
	}

	public void setFloatVal(BigDecimal floatVal) {
		this.floatVal = floatVal;
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

	public String getLeftExpression() {
		return leftExpression;
	}

	public void setLeftExpression(String leftExpression) {
		this.leftExpression = leftExpression;
	}

	public String getLogicOperType() {
		return logicOperType;
	}

	public void setLogicOperType(String logicOperType) {
		this.logicOperType = logicOperType;
	}

	public String getRightExpression() {
		return rightExpression;
	}

	public void setRightExpression(String rightExpression) {
		this.rightExpression = rightExpression;
	}

	public String getIsOrgFilter() {
		return isOrgFilter;
	}

	public void setIsOrgFilter(String isOrgFilter) {
		this.isOrgFilter = isOrgFilter;
	}

	public String getCheckOrg() {
		return checkOrg;
	}

	public void setCheckOrg(String checkOrg) {
		this.checkOrg = checkOrg;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
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

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getLogicCheckCycle() {
		return logicCheckCycle;
	}

	public void setLogicCheckCycle(String logicCheckCycle) {
		this.logicCheckCycle = logicCheckCycle;
	}
}
