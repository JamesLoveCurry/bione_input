package com.yusys.bione.plugin.rptbank.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="主题信息")
public class RptBankImportVO implements Serializable,AnnotationValidable{

	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "指标名称")
	private String indexNm;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "主指标ID")
	private String mainIndexNo;
	
	@ExcelColumn(index = "C", name = "分指标ID")
	private String partIndexNo;
	
	@ExcelColumn(index = "D", name = "上级指标名称")
	private String upIndexNm;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "E", name = "币种编码")
	private String currency;
	
	@ExcelColumn(index = "F", name = "备注")
	private String remark;
	
	private Integer excelRowNo;
	
	private String sheetName;

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}
	
	public String getMainIndexNo() {
		return mainIndexNo;
	}

	public void setMainIndexNo(String mainIndexNo) {
		this.mainIndexNo = mainIndexNo;
	}

	public String getPartIndexNo() {
		return partIndexNo;
	}

	public void setPartIndexNo(String partIndexNo) {
		this.partIndexNo = partIndexNo;
	}

	public String getUpIndexNm() {
		return upIndexNm;
	}

	public void setUpIndexNm(String upIndexNm) {
		this.upIndexNm = upIndexNm;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getExcelRowNo() {
		return excelRowNo;
	}

	public void setExcelRowNo(Integer excelRowNo) {
		this.excelRowNo = excelRowNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
}
