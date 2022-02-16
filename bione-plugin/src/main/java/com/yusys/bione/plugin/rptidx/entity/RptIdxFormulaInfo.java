package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_FORMULA_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_FORMULA_INFO")
public class RptIdxFormulaInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxFormulaInfoPK id;

	@Column(name="FORMULA_CONTENT")
	private String formulaContent;
    
	@Column(name="FORMULA_DESC")
	private String formulaDesc;

	@Column(name="FORMULA_TYPE", length=10)
	private String formulaType;

	@Column(length=500)
	private String remark;

    public RptIdxFormulaInfo() {
    }

	public RptIdxFormulaInfoPK getId() {
		return this.id;
	}

	public void setId(RptIdxFormulaInfoPK id) {
		this.id = id;
	}
	
	public String getFormulaContent() {
		return this.formulaContent;
	}

	public void setFormulaContent(String formulaContent) {
		this.formulaContent = formulaContent;
	}

	public String getFormulaDesc() {
		return this.formulaDesc;
	}

	public void setFormulaDesc(String formulaDesc) {
		this.formulaDesc = formulaDesc;
	}

	public String getFormulaType() {
		return this.formulaType;
	}

	public void setFormulaType(String formulaType) {
		this.formulaType = formulaType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}