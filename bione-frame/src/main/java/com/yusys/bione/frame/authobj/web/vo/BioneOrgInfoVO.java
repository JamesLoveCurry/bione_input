package com.yusys.bione.frame.authobj.web.vo;

import java.io.Serializable;





import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="平台机构")
public class BioneOrgInfoVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "机构编码")
	private String orgNo;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "机构名称")
	private String orgName;

	@ExcelColumn(index = "C", name = "上级机构编码")
	private String upNo;
	
	private Integer excelRowNo;
	
	private String sheetName;


	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getUpNo() {
		return upNo;
	}

	public void setUpNo(String upNo) {
		this.upNo = upNo;
	}

	public Integer getExcelRowNo() {
		return excelRowNo;
	}

	public void setExcelRowNo(Integer excelRowNo) {
		this.excelRowNo = excelRowNo;
	}
	
}
