package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;
import com.yusys.bione.frame.validator.utils.enums.FieldTypes;
@SuppressWarnings("serial")
@ExcelSheet(index="0",name="报表信息")
public class ReportImportVO  implements Serializable ,AnnotationValidable {
	@BioneFieldValid(length=32,nullable=false)
	@ExcelColumn(index = "A", name = "报表编号")
	private String rptNum;
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "报表一级目录")
	private String firstCatalog;
	@ExcelColumn(index = "C", name = "报表二级目录")
	private String secondCatalog;
	@ExcelColumn(index = "D", name = "报表三级目录")
	private String thirdCatalog;
	@BioneFieldValid(length=100,nullable=false)
	@ExcelColumn(index = "E", name = "报表名称")
	private String rptNm;
	@BioneFieldValid()
	@ExcelColumn(index = "F", name = "报表用途",value={"01","02","03","04","05","06"},text={"外部监督", "内部审计", "绩效考核", "风险管理", "运营支持", "营销扩展"})
	private String rptUse;
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "G", name = "责任部门",relDs={"com.yusys.bione.frame.variable.entity.BioneParamInfo","paramValue","paramName"})
	private String dutyDept;
	@BioneFieldValid(comboVals={"01","02"})
	@ExcelColumn(index = "H", name = "汇率",value={"01","02"},text={"内部汇率表", "年终汇率表"})
	private String parties;
	@BioneFieldValid()
	@ExcelColumn(index = "I", name = "报表币种",value={"01","02","03","04","05"},text={"外币原币", "人民币","外币折美元", "外币折人民币","全折人民币"})
	private String rptCurrtype;
	@BioneFieldValid(comboVals={"01","02","03","04","05","06","07"})
	@ExcelColumn(index = "J", name = "金额单位",value={"01","02","03","04","05","06","07"},text={"元","千","万","十万","百万","亿","十亿"})
	private String dataUnit;
	@BioneFieldValid()
	@ExcelColumn(index = "K", name = "生成频率",value={"01","02","03","04","10","11","12"},text={"日","月","季","年","周","旬","半年"})
	private String rptCycle;
	@BioneFieldValid(type=FieldTypes.DATESTR,dateFormats={"yyyyMMdd"})
	@ExcelColumn(index = "L", name = "开始日期")
	private String startDate;
	@BioneFieldValid(comboVals={"Y","N"})
	@ExcelColumn(index = "M", name = "报表状态",value={"Y","N"},text={"启用","停用"})
	private String rptSts;
	@BioneFieldValid(comboVals={"0","1","2","3","4","5","6","7","8","9"})
	@ExcelColumn(index = "N", name = "展示优先级",value={"0","1","2","3","4","5","6","7","8","9"},text={"最高","1","2","3","4","5","6","7","8","最低"})
	private BigDecimal showPriority;
	@BioneFieldValid(comboVals={"Y","N"})
	@ExcelColumn(index = "O", name = "信息权限",value={"Y","N"},text={"有","无"})
	private String infoRights;
	@BioneFieldValid(length=500)
	@ExcelColumn(index = "P", name = "报表描述")
	private String rptDesc;
	private int excelRowNo;
	private String sheetName;
	private String catalogId;
	public String getRptNum() {
		return rptNum;
	}
	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
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
	public String getRptNm() {
		return rptNm;
	}
	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}
	public String getRptUse() {
		return rptUse;
	}
	public void setRptUse(String rptUse) {
		this.rptUse = rptUse;
	}
	public String getDutyDept() {
		return dutyDept;
	}
	public void setDutyDept(String dutyDept) {
		this.dutyDept = dutyDept;
	}
	public String getParties() {
		return parties;
	}
	public void setParties(String parties) {
		this.parties = parties;
	}
	public String getRptCurrtype() {
		return rptCurrtype;
	}
	public void setRptCurrtype(String rptCurrtype) {
		this.rptCurrtype = rptCurrtype;
	}
	public String getDataUnit() {
		return dataUnit;
	}
	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}
	public String getRptCycle() {
		return rptCycle;
	}
	public void setRptCycle(String rptCycle) {
		this.rptCycle = rptCycle;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public BigDecimal getShowPriority() {
		return showPriority;
	}
	public void setShowPriority(BigDecimal showPriority) {
		this.showPriority = showPriority;
	}
	public String getInfoRights() {
		return infoRights;
	}
	public void setInfoRights(String infoRights) {
		this.infoRights = infoRights;
	}
	public String getRptDesc() {
		return rptDesc;
	}
	public void setRptDesc(String rptDesc) {
		this.rptDesc = rptDesc;
	}
	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
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
	public String getRptSts() {
		return rptSts;
	}
	public void setRptSts(String rptSts) {
		this.rptSts = rptSts;
	}
	
	
}
