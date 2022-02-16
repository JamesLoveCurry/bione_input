package com.yusys.bione.plugin.regulation.vo;

import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

/**
 * The persistent class for the RPT_VALID_CFGEXT_LOGIC database table.
 */
@ExcelSheet(index="5",name="校验公式",firstRow=1,insertType="01",extType="01")
public class RptValidCfgextLogicVO {

	@ExcelColumn(index = "4", name = "结束日期")
	private String endDate;//结束日期

	@ExcelColumn(index = "0", name = "公式")
	private String expressionDesc;//完整公式

	@ExcelColumn(index = "1", name = "逻辑运算类型")
	private String logicOperType; //逻辑运算类型

	@ExcelColumn(index = "2", name = "容差值")
	private BigDecimal floatVal;//容差值

	@ExcelColumn(index = "5", name = "是否预校验", value={"","1","0"},text={"","是","否"})
	private String isPre;//是否预校验

	@ExcelColumn(index = "3", name = "开始日期")
	private String startDate;//开始日期

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getExpressionDesc() {
		return this.expressionDesc;
	}

	public void setExpressionDesc(String expressionDesc) {
		this.expressionDesc = expressionDesc;
	}

	public BigDecimal getFloatVal() {
		return this.floatVal;
	}

	public void setFloatVal(BigDecimal floatVal) {
		this.floatVal = floatVal;
	}

	public String getIsPre() {
		return this.isPre;
	}

	public void setIsPre(String isPre) {
		this.isPre = isPre;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getLogicOperType() {
		return logicOperType;
	}

	public void setLogicOperType(String logicOperType) {
		this.logicOperType = logicOperType;
	}

}