package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="同业信息")
public class InterBankImportVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "数据日期")
	private String dataDt;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "机构编号")
	private String orgNo;
	
	@ExcelColumn(index = "C", name = "机构名称")
	private String orgNm;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "D", name = "币种编号")
	private String currCd;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "E", name = "项目编号")
	private String indexNo;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "F", name = "项目名称")
	private String indexNm;
	
	@ExcelColumn(index = "G", name = "项目值")
	private BigDecimal indexVal;
	
	private Integer excelRowNo;
	
	private String sheetName;

	public String getDataDt() {
		return dataDt;
	}

	public void setDataDt(String dataDt) {
		this.dataDt = dataDt;
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

	public String getCurrCd() {
		return currCd;
	}

	public void setCurrCd(String currCd) {
		this.currCd = currCd;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public BigDecimal getIndexVal() {
		return indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
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
