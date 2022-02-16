package com.yusys.bione.plugin.regulation.vo;

import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

/**
 * The persistent class for the RPT_VALID_CFGEXT_WARN database table.
 */
@ExcelSheet(index="6",name="预警校验",firstRow=1,insertType="01",extType="01")
public class RptValidCfgextWarnVO {
	
	@ExcelColumn(index = "0", name = "单元格编号")
	private String cellNo;

	@ExcelColumn(index = "1", name = "预警类型")
	private String compareType;

	@ExcelColumn(index = "2", name = "监管要求")
	private String isFrs;
	
	@ExcelColumn(index = "3", name = "最小比率")
	private BigDecimal minusRangeVal;
	
	@ExcelColumn(index = "4", name = "最大比率")
	private BigDecimal postiveRangeVal;

	//@ExcelColumn(index = "5", name = "开始日期")
	private String startDate;
	
	//@ExcelColumn(index = "6", name = "结束日期")
	private String endDate;

	@ExcelColumn(index = "5", name = "备注")
	private String remark;


	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCellNo() {
		return cellNo;
	}

	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}

	public BigDecimal getPostiveRangeVal() {
		return postiveRangeVal;
	}

	public void setPostiveRangeVal(BigDecimal postiveRangeVal) {
		this.postiveRangeVal = postiveRangeVal;
	}

	public BigDecimal getMinusRangeVal() {
		return minusRangeVal;
	}

	public void setMinusRangeVal(BigDecimal minusRangeVal) {
		this.minusRangeVal = minusRangeVal;
	}

	public String getCompareType() {
		return compareType;
	}

	public void setCompareType(String compareType) {
		this.compareType = compareType;
	}

	public String getIsFrs() {
		return isFrs;
	}

	public void setIsFrs(String isFrs) {
		this.isFrs = isFrs;
	}
}