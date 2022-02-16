package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_FORMULA_FUNC database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_FORMULA_FUNC")
public class RptIdxFormulaFunc implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FORMULA_ID", unique=true, nullable=false, length=32)
	private String formulaId;

	@Column(name="FORMULA_DISPLAY", nullable=false, length=100)
	private String formulaDisplay;

	@Column(name="FORMULA_NM", nullable=false, length=100)
	private String formulaNm;

	@Column(name="FUNC_TYPE", length=10)
	private String funcType;

	private String remark;

    public RptIdxFormulaFunc() {
    }

	public String getFormulaId() {
		return this.formulaId;
	}

	public void setFormulaId(String formulaId) {
		this.formulaId = formulaId;
	}

	public String getFormulaDisplay() {
		return this.formulaDisplay;
	}

	public void setFormulaDisplay(String formulaDisplay) {
		this.formulaDisplay = formulaDisplay;
	}

	public String getFormulaNm() {
		return this.formulaNm;
	}

	public void setFormulaNm(String formulaNm) {
		this.formulaNm = formulaNm;
	}

	public String getFuncType() {
		return this.funcType;
	}

	public void setFuncType(String funcType) {
		this.funcType = funcType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}