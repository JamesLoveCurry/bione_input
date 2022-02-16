package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_FORMULA_SYMBOL database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_FORMULA_SYMBOL")
public class RptIdxFormulaSymbol implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SYMBOL_ID", unique=true, nullable=false, length=32)
	private String symbolId;

	@Column(length=500)
	private String remark;

	@Column(name="SYMBOL_DISPLAY", nullable=false, length=100)
	private String symbolDisplay;

	@Column(name="SYMBOL_NM", nullable=false, length=100)
	private String symbolNm;

	@Column(name="SYMBOL_TYPE", length=10)
	private String symbolType;

    public RptIdxFormulaSymbol() {
    }

	public String getSymbolId() {
		return this.symbolId;
	}

	public void setSymbolId(String symbolId) {
		this.symbolId = symbolId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSymbolDisplay() {
		return this.symbolDisplay;
	}

	public void setSymbolDisplay(String symbolDisplay) {
		this.symbolDisplay = symbolDisplay;
	}

	public String getSymbolNm() {
		return this.symbolNm;
	}

	public void setSymbolNm(String symbolNm) {
		this.symbolNm = symbolNm;
	}

	public String getSymbolType() {
		return this.symbolType;
	}

	public void setSymbolType(String symbolType) {
		this.symbolType = symbolType;
	}

}