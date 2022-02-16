package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="对比组信息")
public class CompImportVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "主指标编号",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String mainIndexNo;
	
	@ExcelColumn(index = "B", name = "指标1",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo1;
	
	@ExcelColumn(index = "C", name = "指标2",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo2;
	
	@ExcelColumn(index = "D", name = "指标3",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo3;
	
	@ExcelColumn(index = "E", name = "指标4",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo4;
	
	@ExcelColumn(index = "F", name = "指标5",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo5;
	
	@ExcelColumn(index = "G", name = "指标6",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo6;

	@ExcelColumn(index = "H", name = "指标7",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo7;
	
	@ExcelColumn(index = "I", name = "指标8",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo8;
	
	@ExcelColumn(index = "J", name = "指标9",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo9;
	
	@ExcelColumn(index = "K", name = "指标10",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo10;
	
	@ExcelColumn(index = "L", name = "指标11",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo11;
	
	@ExcelColumn(index = "M", name = "指标12",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo12;
	
	@ExcelColumn(index = "N", name = "指标13",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo13;
	
	@ExcelColumn(index = "O", name = "指标14",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo14;
	
	@ExcelColumn(index = "P", name = "指标15",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo15;
	
	@ExcelColumn(index = "Q", name = "指标16",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo16;

	@ExcelColumn(index = "R", name = "指标17",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo17;
	
	@ExcelColumn(index = "S", name = "指标18",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo18;
	
	@ExcelColumn(index = "T", name = "指标19",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo19;
	
	@ExcelColumn(index = "U", name = "指标20",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexNo20;
	
	private Integer excelRowNo;
	
	private String sheetName;

	public String getMainIndexNo() {
		return mainIndexNo;
	}

	public void setMainIndexNo(String mainIndexNo) {
		this.mainIndexNo = mainIndexNo;
	}

	public String getIndexNo1() {
		return indexNo1;
	}

	public void setIndexNo1(String indexNo1) {
		this.indexNo1 = indexNo1;
	}

	public String getIndexNo2() {
		return indexNo2;
	}

	public void setIndexNo2(String indexNo2) {
		this.indexNo2 = indexNo2;
	}

	public String getIndexNo3() {
		return indexNo3;
	}

	public void setIndexNo3(String indexNo3) {
		this.indexNo3 = indexNo3;
	}

	public String getIndexNo4() {
		return indexNo4;
	}

	public void setIndexNo4(String indexNo4) {
		this.indexNo4 = indexNo4;
	}

	public String getIndexNo5() {
		return indexNo5;
	}

	public void setIndexNo5(String indexNo5) {
		this.indexNo5 = indexNo5;
	}

	public String getIndexNo6() {
		return indexNo6;
	}

	public void setIndexNo6(String indexNo6) {
		this.indexNo6 = indexNo6;
	}

	public String getIndexNo7() {
		return indexNo7;
	}

	public void setIndexNo7(String indexNo7) {
		this.indexNo7 = indexNo7;
	}

	public String getIndexNo8() {
		return indexNo8;
	}

	public void setIndexNo8(String indexNo8) {
		this.indexNo8 = indexNo8;
	}

	public String getIndexNo9() {
		return indexNo9;
	}

	public void setIndexNo9(String indexNo9) {
		this.indexNo9 = indexNo9;
	}

	public String getIndexNo10() {
		return indexNo10;
	}

	public void setIndexNo10(String indexNo10) {
		this.indexNo10 = indexNo10;
	}

	public String getIndexNo11() {
		return indexNo11;
	}

	public void setIndexNo11(String indexNo11) {
		this.indexNo11 = indexNo11;
	}

	public String getIndexNo12() {
		return indexNo12;
	}

	public void setIndexNo12(String indexNo12) {
		this.indexNo12 = indexNo12;
	}

	public String getIndexNo13() {
		return indexNo13;
	}

	public void setIndexNo13(String indexNo13) {
		this.indexNo13 = indexNo13;
	}

	public String getIndexNo14() {
		return indexNo14;
	}

	public void setIndexNo14(String indexNo14) {
		this.indexNo14 = indexNo14;
	}

	public String getIndexNo15() {
		return indexNo15;
	}

	public void setIndexNo15(String indexNo15) {
		this.indexNo15 = indexNo15;
	}

	public String getIndexNo16() {
		return indexNo16;
	}

	public void setIndexNo16(String indexNo16) {
		this.indexNo16 = indexNo16;
	}

	public String getIndexNo17() {
		return indexNo17;
	}

	public void setIndexNo17(String indexNo17) {
		this.indexNo17 = indexNo17;
	}

	public String getIndexNo18() {
		return indexNo18;
	}

	public void setIndexNo18(String indexNo18) {
		this.indexNo18 = indexNo18;
	}

	public String getIndexNo19() {
		return indexNo19;
	}

	public void setIndexNo19(String indexNo19) {
		this.indexNo19 = indexNo19;
	}

	public String getIndexNo20() {
		return indexNo20;
	}

	public void setIndexNo20(String indexNo20) {
		this.indexNo20 = indexNo20;
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
