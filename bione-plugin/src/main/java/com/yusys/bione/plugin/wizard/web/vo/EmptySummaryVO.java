package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0", name = "sheet1", firstRow = 1)
public class EmptySummaryVO implements Serializable, AnnotationValidable {
	@ExcelColumn(index = "A", name = "采集日期")
	private String dataDt;
	@ExcelColumn(index = "B", name = "机构号")
	private String orgNo;
	@ExcelColumn(index = "C", name = "表名")
	private String tabName;
	@ExcelColumn(index = "D", name = "字段名")
	private String colName;
	@ExcelColumn(index = "E", name = "空值数量")
	private String failCount;
	@ExcelColumn(index = "F", name = "空值率")
	private String emptyRate;
	@ExcelColumn(index = "G", name = "源系统")
	private String srcSys;
	@ExcelColumn(index = "H", name = "空值原因")
	private String reasons;
	@ExcelColumn(index = "I", name = "解决方案")
	private String solves;
	@ExcelColumn(index = "J", name = "解决进度")
	private String progress;

	private int excelRowNo;
	private String sheetName;
	
	public String getDataDt() {
		return dataDt;
	}
	public void setDataDt(String dataDt) {
		this.dataDt = dataDt;
	}
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
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
	public String getFailCount() {
		return failCount;
	}
	public void setFailCount(String failCount) {
		this.failCount = failCount;
	}
	public String getEmptyRate() {
		return emptyRate;
	}
	public void setEmptyRate(String emptyRate) {
		this.emptyRate = emptyRate;
	}
	public String getSrcSys() {
		return srcSys;
	}
	public void setSrcSys(String srcSys) {
		this.srcSys = srcSys;
	}
	public String getReasons() {
		return reasons;
	}
	public void setReasons(String reasons) {
		this.reasons = reasons;
	}
	public String getSolves() {
		return solves;
	}
	public void setSolves(String solves) {
		this.solves = solves;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
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
