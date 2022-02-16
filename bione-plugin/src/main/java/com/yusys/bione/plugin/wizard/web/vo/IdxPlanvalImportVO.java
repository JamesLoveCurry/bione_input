package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "2",name="指标计划值校验信息")
public class IdxPlanvalImportVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "对象名称")
	private String indexNo;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "机构编号")
	private String orgNo;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "C", name = "机构名称")
	private String orgNm;
	
	@ExcelColumn(index = "D", name = "计划年份")
	private String dataDate;
	
	@ExcelColumn(index = "E", name = "币种",relDs = {"com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo","id.dimItemNo","dimItemNm"})
	private String currency;
	
	@ExcelColumn(index = "F", name = "计划值",type = "number")
	private BigDecimal indexVal;
	
	@ExcelColumn(index = "G", name = "关联维度项")
	private String relateDim;
	
	
	private Integer excelRowNo;
	
	private String sheetName;

	public IdxPlanvalImportVO() {
	}

	public IdxPlanvalImportVO(String indexNo,String orgNo,String dataDate,String currency,
			BigDecimal indexVal) {//构造函数，创建对象，默认为空，添加所需属性
		this.setIndexNo(indexNo);
		this.setOrgNo(orgNo);
		this.setDataDate(dataDate);
		this.setCurrency(currency);
		this.setIndexVal(indexVal);
	}

	
	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	
	public BigDecimal getIndexVal() {
		return indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}

	public String getRelateDim() {
		return relateDim;
	}

	public void setRelateDim(String relateDim) {
		this.relateDim = relateDim;
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
