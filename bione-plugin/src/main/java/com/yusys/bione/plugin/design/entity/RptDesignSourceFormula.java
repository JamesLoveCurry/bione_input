package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_SOURCE_FORMULA database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_SOURCE_FORMULA")
public class RptDesignSourceFormula implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignSourceFormulaPK id;

	@Column(name="EXCEL_FORMULA")
	private String excelFormula;

	@Column(name="IS_ANALYSE_EXT")
	private String isAnalyseExt;
	
	@Column(name="IS_RPT_INDEX")
	private String isRptIndex;
	
	@Column(name="ANALYSE_EXT_TYPE")
	private String analyseExtType;

    public RptDesignSourceFormula() {
    }

	public RptDesignSourceFormulaPK getId() {
		return this.id;
	}

	public void setId(RptDesignSourceFormulaPK id) {
		this.id = id;
	}
	
	public String getExcelFormula() {
		return this.excelFormula;
	}

	public void setExcelFormula(String excelFormula) {
		this.excelFormula = excelFormula;
	}

	public String getIsAnalyseExt() {
		return this.isAnalyseExt;
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
		return analyseExtType;
	}

	public void setAnalyseExtType(String analyseExtType) {
		this.analyseExtType = analyseExtType;
	}

}