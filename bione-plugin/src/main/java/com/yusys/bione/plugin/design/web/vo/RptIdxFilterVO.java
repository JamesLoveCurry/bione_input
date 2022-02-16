package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

@ExcelSheet(index="4",name="指标过滤信息",firstRow=1,insertType="01",extType="01")
public class RptIdxFilterVO {

	
	@ExcelColumn(index = "A", name = "单元格编号")
	private String cellNo;
	
	@ExcelColumn(index = "B", name = "维度类型",relDs={"com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo","dimTypeNo","dimTypeNm"})
	private String dimNo;
	
	@ExcelColumn(index = "C", name = "过滤类型",value={"01","02"},text={"包含","不包含"})
	private String filterMode;
	
	@ExcelColumn(index = "D", name = "过滤信息",width=100)
	private String filterVal;

	private String dimTypeNm;
	
	
	public String getCellNo() {
		return cellNo;
	}

	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}

	public String getDimNo() {
		return dimNo;
	}

	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}

	public String getFilterVal() {
		return filterVal;
	}

	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}

	public String getFilterMode() {
		return filterMode;
	}

	public void setFilterMode(String filterMode) {
		this.filterMode = filterMode;
	}

	public String getDimTypeNm() {
		return dimTypeNm;
	}

	public void setDimTypeNm(String dimTypeNm) {
		this.dimTypeNm = dimTypeNm;
	}
	
	
}
