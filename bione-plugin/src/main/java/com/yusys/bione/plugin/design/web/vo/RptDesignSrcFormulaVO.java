package com.yusys.bione.plugin.design.web.vo;


import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;

@SuppressWarnings("serial")
public class RptDesignSrcFormulaVO extends RptDesignCellInfo{
	private String excelFormula;

	private String isAnalyseExt;

	private String isRptIndex;
	
	private String AnalyseExtType;
	
	public String getExcelFormula() {
		return excelFormula;
	}

	public void setExcelFormula(String excelFormula) {
		this.excelFormula = excelFormula;
	}

	public String getIsAnalyseExt() {
		return isAnalyseExt;
	}

	public void setIsAnalyseExt(String isAnalyseExt) {
		this.isAnalyseExt = isAnalyseExt;
	}

	public String getIsRptIndex() {
		return isRptIndex;
	}

	public void setIsRptIndex(String isRptIndex) {
		this.isRptIndex = isRptIndex;
	}

	public String getAnalyseExtType() {
		return AnalyseExtType;
	}

	public void setAnalyseExtType(String analyseExtType) {
		AnalyseExtType = analyseExtType;
	}
	
	
}
