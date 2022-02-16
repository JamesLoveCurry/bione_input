package com.yusys.bione.plugin.valid.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.Serializable;

/**
 * <pre>
 * Title: 
 * Description:1104总分校验
 * </pre>
 * @author  maojin2
 * @version 1.00.00
   @date 2021年8月18日
 */
@SuppressWarnings("serial")
@ExcelSheet(index = "0", name = "总分校验公式", firstRow = 2)
public class CfgextOrgMergeVO implements Serializable, AnnotationValidable {
	@BioneFieldValid(length = 32)
	@ExcelColumn(index = "A", name = "校验ID", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkId;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "B", name = "总行报表名称", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String sumRptNm;
	@BioneFieldValid(length = 32)
	@ExcelColumn(index = "C", name = "总行指标编号", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String sumIndexNo;
	@BioneFieldValid(length = 100)
	@ExcelColumn(index = "D", name = "分行报表名称", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String branchRptNm;
	@BioneFieldValid(length = 32)
	@ExcelColumn(index = "E", name = "分行指标编号", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String branchIndexNo;
	@BioneFieldValid(length = 8)
	@ExcelColumn(index = "F", name = "开始时间", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String startDate;
	@BioneFieldValid(length = 8)
	@ExcelColumn(index = "G", name = "结束时间", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String endDate;
	@BioneFieldValid(length = 2000)
	@ExcelColumn(index = "H", name = "校验描述", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
	private String checkDesc;
	
	private String sumTemplateId;
	private String branchTemplateId;
	private int excelRowNo;
    private String sheetName;
    
    
	public String getCheckId() {
		return checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public String getSumRptNm() {
		return sumRptNm;
	}
	public void setSumRptNm(String sumRptNm) {
		this.sumRptNm = sumRptNm;
	}
	public String getSumIndexNo() {
		return sumIndexNo;
	}
	public void setSumIndexNo(String sumIndexNo) {
		this.sumIndexNo = sumIndexNo;
	}
	public String getBranchRptNm() {
		return branchRptNm;
	}
	public void setBranchRptNm(String branchRptNm) {
		this.branchRptNm = branchRptNm;
	}
	public String getBranchIndexNo() {
		return branchIndexNo;
	}
	public void setBranchIndexNo(String branchIndexNo) {
		this.branchIndexNo = branchIndexNo;
	}
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
	public String getCheckDesc() {
		return checkDesc;
	}
	public void setCheckDesc(String checkDesc) {
		this.checkDesc = checkDesc;
	}
	public String getSumTemplateId() {
		return sumTemplateId;
	}
	public void setSumTemplateId(String sumTemplateId) {
		this.sumTemplateId = sumTemplateId;
	}
	public String getBranchTemplateId() {
		return branchTemplateId;
	}
	public void setBranchTemplateId(String branchTemplateId) {
		this.branchTemplateId = branchTemplateId;
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
	
}
