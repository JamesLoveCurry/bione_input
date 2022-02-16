package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "1", name = "总分核对", firstRow = 3)
public class SumAndApartImportVO implements Serializable, AnnotationValidable {
	@ExcelColumn(index = "A", name = "序号")
	private String sumNo;
	@ExcelColumn(index = "B", name = "数据批次")
	private String dataDt;
	@ExcelColumn(index = "C", name = "检核规则")
	private String checkRule;
	@ExcelColumn(index = "D", name = "表中文名")
	private String tabName;
	@ExcelColumn(index = "E", name = "检核要素")
	private String checkKey;
	@ExcelColumn(index = "F", name = "一级科目名称")
	private String subjectName;
	@ExcelColumn(index = "G", name = "分户账数值")
	private String ledgerSum;
	@ExcelColumn(index = "H", name = "总账数值")
	private String totalSum;
	@ExcelColumn(index = "I", name = "差额")
	private String differSum;
	@ExcelColumn(index = "J", name = "差异解释")
	private String differDesc;
	@ExcelColumn(index = "K", name = "币种")
	private String currCd;
	
	

	private int excelRowNo;
	private String sheetName;

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
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

	public String getSumNo() {
		return sumNo;
	}

	public void setSumNo(String sumNo) {
		this.sumNo = sumNo;
	}

	public String getDataDt() {
		return dataDt;
	}

	public void setDataDt(String dataDt) {
		this.dataDt = dataDt;
	}

	public String getCheckRule() {
		return checkRule;
	}

	public void setCheckRule(String checkRule) {
		this.checkRule = checkRule;
	}

	public String getCheckKey() {
		return checkKey;
	}

	public void setCheckKey(String checkKey) {
		this.checkKey = checkKey;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getLedgerSum() {
		return ledgerSum;
	}

	public void setLedgerSum(String ledgerSum) {
		this.ledgerSum = ledgerSum;
	}

	public String getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(String totalSum) {
		this.totalSum = totalSum;
	}

	public String getDifferSum() {
		return differSum;
	}

	public void setDifferSum(String differSum) {
		this.differSum = differSum;
	}

	public String getDifferDesc() {
		return differDesc;
	}

	public void setDifferDesc(String differDesc) {
		this.differDesc = differDesc;
	}

	public String getCurrCd() {
		return currCd;
	}

	public void setCurrCd(String currCd) {
		this.currCd = currCd;
	}
	
}
