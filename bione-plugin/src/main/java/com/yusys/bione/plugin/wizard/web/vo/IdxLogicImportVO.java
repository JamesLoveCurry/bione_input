package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;
import com.yusys.bione.frame.validator.utils.enums.FieldTypes;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="指标逻辑校验信息")
public class IdxLogicImportVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "校验公式名称")
	private String checkNm;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "左表达式")
	private String leftExpression;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "C", name = "逻辑运算类型")
	private String logicOperType;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "D", name = "右表达式")
	private String rightExpression;
	
	@ExcelColumn(index = "E", name = "容差值",type = "number")
	private BigDecimal floatVal;
	
	@BioneFieldValid(nullable=false,type=FieldTypes.DATESTR,dateFormats={"yyyyMMdd"})
	@ExcelColumn(index = "F", name = "开始日期")
	private String startDate;
	
	@ExcelColumn(index = "G", name = "业务说明")
	private String busiExplain;
	
	@ExcelColumn(index = "H", name = "关联维度项")
	private String relateDim;
	
	
	private Integer excelRowNo;
	
	private String sheetName;

	public IdxLogicImportVO() {
	}

	public IdxLogicImportVO(String checkNm,String leftExpression,String logicOperType,String rightExpression,
			BigDecimal floatVal,String startDate,String busiExplain,String relateDim) {//构造函数，创建对象，默认为空，添加所需属性
		this.setCheckNm(checkNm);
		this.setLeftExpression(leftExpression);
		this.setLogicOperType(logicOperType);
		this.setRightExpression(rightExpression);
		this.setFloatVal(floatVal);
		this.setStartDate(startDate);
		this.setBusiExplain(busiExplain);
		this.setRelateDim(relateDim);
	}

	
	public String getCheckNm() {
		return checkNm;
	}

	public void setCheckNm(String checkNm) {
		this.checkNm = checkNm;
	}

	public String getLeftExpression() {
		return leftExpression;
	}

	public void setLeftExpression(String leftExpression) {
		this.leftExpression = leftExpression;
	}

	public String getLogicOperType() {
		return logicOperType;
	}

	public void setLogicOperType(String logicOperType) {
		this.logicOperType = logicOperType;
	}

	public String getRightExpression() {
		return rightExpression;
	}

	public void setRightExpression(String rightExpression) {
		this.rightExpression = rightExpression;
	}

	public BigDecimal getFloatVal() {
		return floatVal;
	}

	public void setFloatVal(BigDecimal floatVal) {
		this.floatVal = floatVal;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getBusiExplain() {
		return busiExplain;
	}

	public void setBusiExplain(String busiExplain) {
		this.busiExplain = busiExplain;
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
