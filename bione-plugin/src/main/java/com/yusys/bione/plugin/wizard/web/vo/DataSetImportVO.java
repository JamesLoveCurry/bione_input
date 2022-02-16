package com.yusys.bione.plugin.wizard.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

import java.io.Serializable;
@SuppressWarnings("serial")
@ExcelSheet(index="0", name="数据模型信息")
public class DataSetImportVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(length = 100 , nullable = false )
	@ExcelColumn(index = "A", name = "数据集名称")
	private String setId;
	
	@BioneFieldValid(length = 100 , nullable = false )
	@ExcelColumn(index = "B", name = "数据集名称")
	private String setNm;
	
	@BioneFieldValid(length = 100, nullable = false)
	@ExcelColumn(index = "C", name = "一级目录名称")
	private String firstcatalogNm;
	
	@BioneFieldValid(length = 100, nullable = true)
	@ExcelColumn(index = "D", name = "二级目录名称")
	private String secondCatalog;
	
	@BioneFieldValid(length = 100, nullable = true)
	@ExcelColumn(index = "E", name = "三级目录名称")
	private String thirdCatalog;
	
	@BioneFieldValid(length = 100, nullable = false )
	@ExcelColumn(index = "F", name = "数据集类型",value={"00","01","02","03"} ,text={"明细模型","多维模型","泛化模型","总账模型"})
	private String setType;
	
	@BioneFieldValid(length = 100 , nullable = false )
	@ExcelColumn(index = "G", name = "数据集物理名称")
	private String tableEnNm;
	
	@BioneFieldValid(length = 100 , nullable = false )
	@ExcelColumn(index = "H", name = "数据项名称")
	private String cnNm;
	
	@BioneFieldValid(length = 100 , nullable = false )
	@ExcelColumn(index = "I", name = "数据项物理名称")
	private String enNm;
	
	@BioneFieldValid(length = 10 , nullable = false)
	@ExcelColumn(index = "J", name = "数据项类型",value={"00","01","02"} ,text={"属性字段","度量字段","维度字段"})
	private String colType;
	
	@BioneFieldValid(length = 200, nullable = true)
	@ExcelColumn(index = "K", name = "维度名称")
	private String dimTypeNo;

	@BioneFieldValid(length = 200, nullable = true)
	@ExcelColumn(index = "L", name = "度量名称")
	private String measureNm;
	
	@BioneFieldValid(nullable = false)
	@ExcelColumn(index = "M", name = "业务类型",relDs={"com.yusys.bione.frame.variable.entity.BioneParamInfo","paramValue","paramName"," param_Type_No = 'reportorgtype' "})
	private String busiType;

	@ExcelColumn(index = "N", name = "备注")
	private String remark;

	@BioneFieldValid(nullable = false)
	@ExcelColumn(index="O",name="是否是主键")
	private String isPk;

	private String catalogId;
	
	private int excelRowNo;
	private String sheetName;

	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	public String getFirstcatalogNm() {
		return firstcatalogNm;
	}
	public void setFirstcatalogNm(String firstcatalogNm) {
		this.firstcatalogNm = firstcatalogNm;
	}
	public String getSecondCatalog() {
		return secondCatalog;
	}
	public void setSecondCatalog(String secondCatalog) {
		this.secondCatalog = secondCatalog;
	}
	public String getThirdCatalog() {
		return thirdCatalog;
	}
	public void setThirdCatalog(String thirdCatalog) {
		this.thirdCatalog = thirdCatalog;
	}
	public String getSetId() {
		return setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
	}
	public String getSetNm() {
		return setNm;
	}
	public void setSetNm(String setNm) {
		this.setNm = setNm;
	}
	public String getTableEnNm() {
		return tableEnNm;
	}
	public void setTableEnNm(String tableEnNm) {
		this.tableEnNm = tableEnNm;
	}
	public String getCnNm() {
		return cnNm;
	}
	public void setCnNm(String cnNm) {
		this.cnNm = cnNm;
	}
	public String getEnNm() {
		return enNm;
	}
	public void setEnNm(String enNm) {
		this.enNm = enNm;
	}
	
	public String getDimTypeNo() {
		return dimTypeNo;
	}
	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}
	public String getColType() {
		return colType;
	}
	public void setColType(String colType) {
		this.colType = colType;
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
	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public String getIsPk() {
		return isPk;
	}

	public void setIsPk(String isPk) {
		this.isPk = isPk;
	}

	public String getMeasureNm() {
		return measureNm;
	}

	public void setMeasureNm(String measureNm) {
		this.measureNm = measureNm;
	}
}
