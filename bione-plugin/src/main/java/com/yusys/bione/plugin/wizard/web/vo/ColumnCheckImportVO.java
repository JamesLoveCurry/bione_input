package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0", name = "字段校验", firstRow = 2)
public class ColumnCheckImportVO implements Serializable, AnnotationValidable {
	@ExcelColumn(index = "A", name = "检核规则序号")
	private String checkNo;
	@ExcelColumn(index = "B", name = "数据批次")
	private String dataDt;
	@ExcelColumn(index = "C", name = "校验类型")
	private String checkType;
	@ExcelColumn(index = "D", name = "表中文名")
	private String tabName;
	@ExcelColumn(index = "E", name = "字段中文名")
	private String colName;
	@ExcelColumn(index = "F", name = "检核规则")
	private String checkRule;
	@ExcelColumn(index = "G", name = "错误条数")
	private String failCount;
	@ExcelColumn(index = "H", name = "总量")
	private String totalCount;
	@ExcelColumn(index = "I", name = "错误类型")
	private String failRate;
	@ExcelColumn(index = "J", name = "错误数据原因解释")
	private String failReason;

	private int excelRowNo;
	private String sheetName;

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

	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public String getDataDt() {
		return dataDt;
	}

	public void setDataDt(String dataDt) {
		this.dataDt = dataDt;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getCheckRule() {
		return checkRule;
	}

	public void setCheckRule(String checkRule) {
		this.checkRule = checkRule;
	}

	public String getFailCount() {
		return failCount;
	}

	public void setFailCount(String failCount) {
		this.failCount = failCount;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	
	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getFailRate() {
		return failRate;
	}

	public void setFailRate(String failRate) {
		this.failRate = failRate;
	}
	
}
