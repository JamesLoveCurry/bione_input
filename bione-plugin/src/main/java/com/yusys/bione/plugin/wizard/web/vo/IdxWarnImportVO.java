package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;
import com.yusys.bione.frame.validator.utils.enums.FieldTypes;

@SuppressWarnings("serial")
@ExcelSheet(index = "1",name="指标警示校验信息")
public class IdxWarnImportVO implements Serializable,AnnotationValidable{
	
	@ExcelColumn(index = "A", name = "校验名称")
	private String checkNm;
	
	@ExcelColumn(index = "B", name = "对象名称")
	private String indexNo;
	
	@BioneFieldValid(comboVals={"01", "02","03","04","05","06","07","08","09"})
	@ExcelColumn(index = "C", name = "比较值类型",value = { "01", "02","03","04","05","06","07","08","09" }, 
	text = { "上日", "月初","上月末","上月同期","季初","上季末","年初","上年末","上年同期" })
	private String compareValType;
	
	@BioneFieldValid(comboVals={"01", "02"})
	@ExcelColumn(index = "D", name = "幅度类型",value = {"01", "02"},text = {"数字","百分比"})
	private String rangeType;
	
	@BioneFieldValid(type=FieldTypes.DATESTR,dateFormats={"yyyyMMdd"})
	@ExcelColumn(index = "E", name = "开始日期")
	private String startDate;
	
	@ExcelColumn(index = "F", name = "关联维度项")
	private String relateDim;
	
	@ExcelColumn(index = "G", name = "备注")
	private String remark;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "H", name = "级别名称")
	private String levelNm;
	
	@BioneFieldValid(nullable=false,comboVals={"01", "02"})
	@ExcelColumn(index = "I", name = "警戒类型",value = { "01", "02" }, text = { "内置分级", "自定义分级" })
	private String levelType;
	
	@ExcelColumn(index = "J", name = "提醒颜色")
	private String remindColor;
	
	@ExcelColumn(index = "K", name = "正向幅度值",type = "number")
	private BigDecimal postiveRangeVal;
	
	@ExcelColumn(index = "L", name = "负向幅度值",type = "number")
	private BigDecimal minusRangeVal;
	
	@BioneFieldValid(nullable=false,comboVals={"1", "0"})
	@ExcelColumn(index = "M", name = "通过条件",value = { "1", "0" }, text = { "是", "否" })
	private String isPassCond;
	
	
	private Integer excelRowNo;
	
	private String sheetName;

	public IdxWarnImportVO() {
	}

	public IdxWarnImportVO(String checkNm,String indexNo,String compareValType,String rangeType,String startDate,
			String relateDim,String remark,String levelNm,String levelType,String remindColor,
			BigDecimal postiveRangeVal, BigDecimal minusRangeVal,String isPassCond) {//构造函数，创建对象，默认为空，添加所需属性
		this.setCheckNm(checkNm);
		this.setIndexNo(indexNo);
		this.setCompareValType(compareValType);
		this.setRangeType(rangeType);
		this.setStartDate(startDate);
		this.setRelateDim(relateDim);
		this.setRemark(remark);
		this.setLevelNm(levelNm);
		this.setLevelType(levelType);
		this.setRemindColor(remindColor);
		this.setPostiveRangeVal(postiveRangeVal);
		this.setMinusRangeVal(minusRangeVal);
		this.setIsPassCond(isPassCond);
	}

	
	public String getCheckNm() {
		return checkNm;
	}

	public void setCheckNm(String checkNm) {
		this.checkNm = checkNm;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getCompareValType() {
		return compareValType;
	}

	public void setCompareValType(String compareValType) {
		this.compareValType = compareValType;
	}

	public String getRangeType() {
		return rangeType;
	}

	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getRelateDim() {
		return relateDim;
	}

	public void setRelateDim(String relateDim) {
		this.relateDim = relateDim;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLevelNm() {
		return levelNm;
	}

	public void setLevelNm(String levelNm) {
		this.levelNm = levelNm;
	}

	public String getLevelType() {
		return levelType;
	}

	public void setLevelType(String levelType) {
		this.levelType = levelType;
	}

	public String getRemindColor() {
		return remindColor;
	}

	public void setRemindColor(String remindColor) {
		this.remindColor = remindColor;
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

	public String getIsPassCond() {
		return isPassCond;
	}

	public void setIsPassCond(String isPassCond) {
		this.isPassCond = isPassCond;
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
