package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="同类组信息")
public class SimilarImportVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "指标Ⅰ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexA;
	
	@ExcelColumn(index = "B", name = "指标Ⅱ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexB;
	
	@ExcelColumn(index = "C", name = "指标Ⅲ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexC;
	
	@ExcelColumn(index = "D", name = "指标Ⅳ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexD;
	
	@ExcelColumn(index = "E", name = "指标Ⅴ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexE;
	
	@ExcelColumn(index = "F", name = "指标Ⅵ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexF;
	
	@ExcelColumn(index = "G", name = "指标Ⅶ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexG;

	@ExcelColumn(index = "H", name = "指标Ⅷ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexH;
	
	@ExcelColumn(index = "I", name = "指标Ⅸ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexI;
	
	@ExcelColumn(index = "J", name = "指标Ⅹ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexJ;
	
	@ExcelColumn(index = "K", name = "指标ⅩⅠ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexK;
	
	@ExcelColumn(index = "L", name = "指标ⅩⅡ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexL;
	
	@ExcelColumn(index = "M", name = "指标ⅩⅢ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexM;
	
	@ExcelColumn(index = "N", name = "指标ⅩⅣ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexN;
	
	@ExcelColumn(index = "O", name = "指标ⅩⅤ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexO;
	
	@ExcelColumn(index = "P", name = "指标ⅩⅥ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexP;
	
	@ExcelColumn(index = "Q", name = "指标ⅩⅦ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexQ;

	@ExcelColumn(index = "R", name = "指标ⅩⅧ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexR;
	
	@ExcelColumn(index = "S", name = "指标ⅩⅨ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexS;
	
	@ExcelColumn(index = "T", name = "指标ⅩⅩ",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxInfo","id.indexNo","indexNm"})
	private String indexT;
	
	private Integer excelRowNo;
	
	private String sheetName;

	public String getIndexA() {
		return indexA;
	}

	public void setIndexA(String indexA) {
		this.indexA = indexA;
	}

	public String getIndexB() {
		return indexB;
	}

	public void setIndexB(String indexB) {
		this.indexB = indexB;
	}

	public String getIndexC() {
		return indexC;
	}

	public void setIndexC(String indexC) {
		this.indexC = indexC;
	}

	public String getIndexD() {
		return indexD;
	}

	public void setIndexD(String indexD) {
		this.indexD = indexD;
	}

	public String getIndexE() {
		return indexE;
	}

	public void setIndexE(String indexE) {
		this.indexE = indexE;
	}

	public String getIndexF() {
		return indexF;
	}

	public void setIndexF(String indexF) {
		this.indexF = indexF;
	}

	public String getIndexG() {
		return indexG;
	}

	public void setIndexG(String indexG) {
		this.indexG = indexG;
	}

	public String getIndexH() {
		return indexH;
	}

	public void setIndexH(String indexH) {
		this.indexH = indexH;
	}

	public String getIndexI() {
		return indexI;
	}

	public void setIndexI(String indexI) {
		this.indexI = indexI;
	}

	public String getIndexJ() {
		return indexJ;
	}

	public void setIndexJ(String indexJ) {
		this.indexJ = indexJ;
	}

	public String getIndexK() {
		return indexK;
	}

	public void setIndexK(String indexK) {
		this.indexK = indexK;
	}

	public String getIndexL() {
		return indexL;
	}

	public void setIndexL(String indexL) {
		this.indexL = indexL;
	}

	public String getIndexM() {
		return indexM;
	}

	public void setIndexM(String indexM) {
		this.indexM = indexM;
	}

	public String getIndexN() {
		return indexN;
	}

	public void setIndexN(String indexN) {
		this.indexN = indexN;
	}

	public String getIndexO() {
		return indexO;
	}

	public void setIndexO(String indexO) {
		this.indexO = indexO;
	}

	public String getIndexP() {
		return indexP;
	}

	public void setIndexP(String indexP) {
		this.indexP = indexP;
	}

	public String getIndexQ() {
		return indexQ;
	}

	public void setIndexQ(String indexQ) {
		this.indexQ = indexQ;
	}

	public String getIndexR() {
		return indexR;
	}

	public void setIndexR(String indexR) {
		this.indexR = indexR;
	}

	public String getIndexS() {
		return indexS;
	}

	public void setIndexS(String indexS) {
		this.indexS = indexS;
	}

	public String getIndexT() {
		return indexT;
	}

	public void setIndexT(String indexT) {
		this.indexT = indexT;
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
