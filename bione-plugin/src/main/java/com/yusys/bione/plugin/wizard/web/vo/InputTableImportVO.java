package com.yusys.bione.plugin.wizard.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

import java.io.Serializable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="补录表信息", firstRow = 2)
public class InputTableImportVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "字段名称")
	private String fieldEnName;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "中文名称")
	private String fieldCnName;
	
	@ExcelColumn(index = "C", name = "字段类型")
	private String fieldType;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "D", name = "默认值")
	private String defaultValue;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "E", name = "字段长度")
	private String fieldLength;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "F", name = "小数位")
	private String decimalLength;
	
	@ExcelColumn(index = "G", name = "可否为空")
	private String allowNull;
	
	private Integer excelRowNo;
	
	private String sheetName;

	public String getFieldEnName() {
		return fieldEnName;
	}

	public void setFieldEnName(String fieldEnName) {
		this.fieldEnName = fieldEnName;
	}

	public String getFieldCnName() {
		return fieldCnName;
	}

	public void setFieldCnName(String fieldCnName) {
		this.fieldCnName = fieldCnName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(String fieldLength) {
		this.fieldLength = fieldLength;
	}

	public String getDecimalLength() {
		return decimalLength;
	}

	public void setDecimalLength(String decimalLength) {
		this.decimalLength = decimalLength;
	}

	public String getAllowNull() {
		return allowNull;
	}

	public void setAllowNull(String allowNull) {
		this.allowNull = allowNull;
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
