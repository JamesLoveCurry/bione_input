package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "6", name = "跨监管一致性校验", firstRow = 1)
public class RuleEqualImportVO implements Serializable, AnnotationValidable {
	@BioneFieldValid(length = 32)
	@ExcelColumn(index = "A", name = "ID")
	private String id;
	@BioneFieldValid(nullable=true, length = 20)
	@ExcelColumn(index = "B", name = "EAST4检核规则序号")
	private String reportNo;
	@BioneFieldValid(nullable=true, length = 20)
	@ExcelColumn(index = "C", name = "EAST4检核类型")
	private String reportCd;
	@BioneFieldValid(nullable=false, length = 200)
	@ExcelColumn(index = "D", name = "检核表名")
	private String tabName;
	@BioneFieldValid(nullable=true, length = 2000)
	@ExcelColumn(index = "E", name = "数据项名称")
	private String colName;
	@BioneFieldValid(nullable=true, length = 2000)
	@ExcelColumn(index = "F", name = "规则名称")
	private String ruleName;
	@BioneFieldValid(nullable=true, length = 50)
	@ExcelColumn(index = "G", name = "报表类型")
	private String rptType;
	@BioneFieldValid(nullable=true, length = 100)
	@ExcelColumn(index = "H", name = "报表名称")
	private String rptName;
	@BioneFieldValid(nullable=true, length = 3000)
	@ExcelColumn(index = "I", name = "汇总表达式")
	private String expr;
	@ExcelColumn(index = "J", name = "最大相对误差", type = "number")
	private BigDecimal relative;
	@BioneFieldValid(nullable=true, length = 3000)
	@ExcelColumn(index = "K", name = "前置条件")
	private String cond;
	@BioneFieldValid(comboVals = { "Y", "N" }, length = 1)
	@ExcelColumn(index = "L", name = "是否启用", value = { "Y", "N" }, text = { "启用", "停用" })
	private String ruleSts;
	@BioneFieldValid(nullable=true, length = 20)
	@ExcelColumn(index = "M", name = "规则来源")
	private String ruleSource;
	@BioneFieldValid(nullable=true, length = 20)
	@ExcelColumn(index = "N", name = "约束类型")
	private String constraintType;
	@BioneFieldValid(nullable=true, length = 20)
	@ExcelColumn(index = "O", name = "展示分类")
	private String dispCd;
	@BioneFieldValid(nullable=true,comboVals = { "1", "2" }, length = 1)
	@ExcelColumn(index = "P", name = "规则业务类型", value = { "1", "2" }, text = { "全量SQL类型", "增量SQL类型" })
	private String ruleBusiType;
	private int excelRowNo;
	private String sheetName;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	public String getReportCd() {
		return reportCd;
	}
	public void setReportCd(String reportCd) {
		this.reportCd = reportCd;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	public BigDecimal getRelative() {
		return relative;
	}
	public void setRelative(BigDecimal relative) {
		this.relative = relative;
	}
	public String getCond() {
		return cond;
	}
	public void setCond(String cond) {
		this.cond = cond;
	}
	public String getRuleSts() {
		return ruleSts;
	}
	public void setRuleSts(String ruleSts) {
		this.ruleSts = ruleSts;
	}
	public String getRuleSource() {
		return ruleSource;
	}
	public void setRuleSource(String ruleSource) {
		this.ruleSource = ruleSource;
	}
	public String getConstraintType() {
		return constraintType;
	}
	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}
	public String getDispCd() {
		return dispCd;
	}
	public void setDispCd(String dispCd) {
		this.dispCd = dispCd;
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

	public String getRptType() {
		return rptType;
	}

	public void setRptType(String rptType) {
		this.rptType = rptType;
	}

	public String getRptName() {
		return rptName;
	}

	public void setRptName(String rptName) {
		this.rptName = rptName;
	}

	public String getRuleBusiType() {
		return ruleBusiType;
	}

	public void setRuleBusiType(String ruleBusiType) {
		this.ruleBusiType = ruleBusiType;
	}
}