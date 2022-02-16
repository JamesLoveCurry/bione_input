package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;
import com.yusys.bione.frame.validator.utils.enums.FieldTypes;

@SuppressWarnings("serial")
@ExcelSheet(index="0",name="维度信息",firstRow=2)
public class DimImportVO implements Serializable,AnnotationValidable{
	@BioneFieldValid(length=32)
	@ExcelColumn(index = "A", name = "维度代码")
	private String dimTypeNo;
	@BioneFieldValid(length=100)
	@ExcelColumn(index = "B", name = "维度一级分类")
	private String firstCatalog;
	@BioneFieldValid(length=100)
	@ExcelColumn(index = "C", name = "维度二级分类")
	private String secondCatalog;
	@BioneFieldValid(length=100)
	@ExcelColumn(index = "D", name = "维度三级分类")
	private String thirdCatalog;
	@BioneFieldValid(length=100)
	@ExcelColumn(index = "E", name = "维度名称")
	private String dimTypeNm;
	@BioneFieldValid(comboVals={"Y","N"},length=1)
	@ExcelColumn(index = "F", name = "是否启用",value={"Y","N"},text={"启用","停用"})
	private String dimSts;
	@BioneFieldValid(type=FieldTypes.DATESTR,dateFormats={"yyyyMMdd"},length=8)
	@ExcelColumn(index = "G", name = "启用日期")
	private String startDate;
	@BioneFieldValid(length=500)
	@ExcelColumn(index = "H", name = "维度描述")
	private String dimTypeDesc;
	@BioneFieldValid(length=32)
	@ExcelColumn(index = "I", name = "一级维度项")
	private String dimNo1;
	@BioneFieldValid(length=200)
	@ExcelColumn(index = "J", name = "中文名称")
	private String dimNm1;
	@BioneFieldValid(length=32)
	@ExcelColumn(index = "K", name = "二级维度项")
	private String dimNo2;
	@BioneFieldValid(length=200)
	@ExcelColumn(index = "L", name = "中文名称")
	private String dimNm2;
	@BioneFieldValid(length=32)
	@ExcelColumn(index = "M", name = "三级维度项")
	private String dimNo3;
	@BioneFieldValid(length=200)
	@ExcelColumn(index = "N", name = "中文名称")
	private String dimNm3;
	@BioneFieldValid(length=32)
	@ExcelColumn(index = "O", name = "四级维度项")
	private String dimNo4;
	@BioneFieldValid(length=200)
	@ExcelColumn(index = "P", name = "中文名称")
	private String dimNm4;
	@BioneFieldValid(length=32)
	@ExcelColumn(index = "Q", name = "五级维度项")
	private String dimNo5;
	@ExcelColumn(index = "R", name = "中文名称")
	@BioneFieldValid(length=200)
	private String dimNm5;
	@BioneFieldValid(length=32)
	@ExcelColumn(index = "S", name = "六级维度项")
	private String dimNo6;
	@BioneFieldValid(length=200)
	@ExcelColumn(index = "T", name = "中文名称")
	private String dimNm6;
	@BioneFieldValid(length=32)
	@ExcelColumn(index = "U", name = "七级维度项")
	private String dimNo7;
	@BioneFieldValid(length=200)
	@ExcelColumn(index = "V", name = "中文名称")
	private String dimNm7;
	@BioneFieldValid(length=2000)
	@ExcelColumn(index = "W", name = "维度业务定义")
	private String busiDef;
	@ExcelColumn(index = "X", name = "维度业务规则")
	private String busiRule;
	@ExcelColumn(index = "Y", name = "定义部门",relDs={"com.yusys.bione.frame.variable.entity.BioneParamInfo","paramValue","paramName"})
	private String defDept;
	@ExcelColumn(index = "Z", name = "使用部门",relDs={"com.yusys.bione.frame.variable.entity.BioneParamInfo","paramValue","paramName"})
	private String useDept;
	@ExcelColumn(index = "AA", name = "序号")
	private BigDecimal rankOrder;
	@BioneFieldValid(length=500)
	@ExcelColumn(index = "AB", name = "备注")
	private String remark;
	
	private int excelRowNo;
	private String sheetName;
	public String getDimTypeNo() {
		return dimTypeNo;
	}
	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}
	public String getFirstCatalog() {
		return firstCatalog;
	}
	public void setFirstCatalog(String firstCatalog) {
		this.firstCatalog = firstCatalog;
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
	public String getDimTypeNm() {
		return dimTypeNm;
	}
	public void setDimTypeNm(String dimTypeNm) {
		this.dimTypeNm = dimTypeNm;
	}
	public String getDimSts() {
		return dimSts;
	}
	public void setDimSts(String dimSts) {
		this.dimSts = dimSts;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getDimTypeDesc() {
		return dimTypeDesc;
	}
	public void setDimTypeDesc(String dimTypeDesc) {
		this.dimTypeDesc = dimTypeDesc;
	}
	public String getDimNo1() {
		return dimNo1;
	}
	public void setDimNo1(String dimNo1) {
		this.dimNo1 = dimNo1;
	}
	public String getDimNm1() {
		return dimNm1;
	}
	public void setDimNm1(String dimNm1) {
		this.dimNm1 = dimNm1;
	}
	public String getDimNo2() {
		return dimNo2;
	}
	public void setDimNo2(String dimNo2) {
		this.dimNo2 = dimNo2;
	}
	public String getDimNm2() {
		return dimNm2;
	}
	public void setDimNm2(String dimNm2) {
		this.dimNm2 = dimNm2;
	}
	public String getDimNo3() {
		return dimNo3;
	}
	public void setDimNo3(String dimNo3) {
		this.dimNo3 = dimNo3;
	}
	public String getDimNm3() {
		return dimNm3;
	}
	public void setDimNm3(String dimNm3) {
		this.dimNm3 = dimNm3;
	}
	public String getDimNo4() {
		return dimNo4;
	}
	public void setDimNo4(String dimNo4) {
		this.dimNo4 = dimNo4;
	}
	public String getDimNm4() {
		return dimNm4;
	}
	public void setDimNm4(String dimNm4) {
		this.dimNm4 = dimNm4;
	}
	public String getDimNo5() {
		return dimNo5;
	}
	public void setDimNo5(String dimNo5) {
		this.dimNo5 = dimNo5;
	}
	public String getDimNm5() {
		return dimNm5;
	}
	public void setDimNm5(String dimNm5) {
		this.dimNm5 = dimNm5;
	}
	public String getDimNo6() {
		return dimNo6;
	}
	public void setDimNo6(String dimNo6) {
		this.dimNo6 = dimNo6;
	}
	public String getDimNm6() {
		return dimNm6;
	}
	public void setDimNm6(String dimNm6) {
		this.dimNm6 = dimNm6;
	}
	public String getDimNo7() {
		return dimNo7;
	}
	public void setDimNo7(String dimNo7) {
		this.dimNo7 = dimNo7;
	}
	public String getDimNm7() {
		return dimNm7;
	}
	public void setDimNm7(String dimNm7) {
		this.dimNm7 = dimNm7;
	}
	public String getBusiDef() {
		return busiDef;
	}
	public void setBusiDef(String busiDef) {
		this.busiDef = busiDef;
	}
	public String getBusiRule() {
		return busiRule;
	}
	public void setBusiRule(String busiRule) {
		this.busiRule = busiRule;
	}
	public String getDefDept() {
		return defDept;
	}
	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}
	public String getUseDept() {
		return useDept;
	}
	public void setUseDept(String useDept) {
		this.useDept = useDept;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	
	public void setRankOrder(BigDecimal rankOrder) {
		this.rankOrder = rankOrder;
	}
	
	public BigDecimal getRankOrder() {
		return rankOrder;
	}
}
