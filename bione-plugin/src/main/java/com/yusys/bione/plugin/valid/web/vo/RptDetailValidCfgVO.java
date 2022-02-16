package com.yusys.bione.plugin.valid.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

/**
 * <pre>
 * Title: excel 导出逻辑实体
 * Description: 
 * </pre>
 * @author lizy6 
 * @version 1.00.00
 */
@ExcelSheet(index = "0", name = "明细类-校验公式", firstRow = 1)
public class RptDetailValidCfgVO implements Serializable, AnnotationValidable {

	private static final long serialVersionUID = 1L;
	
	@BioneFieldValid(length = 200)
	@ExcelColumn(index = "A", name = "报表名称", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String rptNm;
	
	@Id
	@BioneFieldValid(length = 32)
	@ExcelColumn(index = "B", name = "校验ID", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkId;
	
	private String rptTemplateId;
	
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "C", name = "校验类型", value={"01","02","03","04","05","06"},text={"逻辑校验","正则表达式校验","字段比较校验","系统重要性银行所有交易对手合计校验","系统重要性银行各交易对手合计校验","系统重要性银行字段比较校验"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String expType;
	
	@BioneFieldValid(length = 2000)
	@ExcelColumn(index = "D", name = "表达式描述", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String expressionDesc;
	
	@BioneFieldValid(length = 2000)
	@ExcelColumn(index = "E", name = "左表达式", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String leftExpression;

	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "F", name = "运算类型", relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaSymbol","symbolNm","symbolNm"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String logicOperType;

	@BioneFieldValid(length = 2000)
	@ExcelColumn(index = "G", name = "右表达式", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String rightExpression;
	
	@Column(name="FLOAT_VAL")
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "H", name = "容差值", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private BigDecimal floatVal;

	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "I", name = "业务说明", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String busiExplain;
	
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "J", name = "配置前置条件", value={"N","Y"},text={"否","是"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String isPre;

	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "K", name = "前置左表达式", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String preLeftExpression;

	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "L", name = "前置运算类型", relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaSymbol","symbolNm","symbolNm"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String preOperType;
	
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "M", name = "前置右表达式", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String preRightExpression;

	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "N", name = "公式类型", value={"01","02"},text={"表内","表间"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkType;
	
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "O", name = "公式来源", value={"01","02"},text={"监管制度","自定义"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkSrc;
	
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "P", name = "开始时间", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String startDate;

	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "Q", name = "结束时间", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String endDate;

	private int excelRowNo;
	private String sheetName;
	
	public String getCheckId() {
		return this.checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
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

	public String getRptNm() {
		return rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
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
}
