package com.yusys.bione.plugin.base.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_CABIN_FORMULA_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_CABIN_FORMULA_INFO")
@NamedQuery(name="RptCabinFormulaInfo.findAll", query="SELECT r FROM RptCabinFormulaInfo r")
public class RptCabinFormulaInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FORMULA_ID")
	private String formulaId;

	@Column(name="DISPLAY_CONTENT")
	private String displayContent;

	@Column(name="FORMULA_CONTENT")
	private String formulaContent;

	@Column(name="FORMULA_FREQ")
	private String formulaFreq;

	@Column(name="FORMULA_NM")
	private String formulaNm;

	@Column(name="FORMULA_TYPE")
	private String formulaType;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	private String remark;

	public RptCabinFormulaInfo() {
	}

	public String getFormulaId() {
		return this.formulaId;
	}

	public void setFormulaId(String formulaId) {
		this.formulaId = formulaId;
	}

	public String getDisplayContent() {
		return this.displayContent;
	}

	public void setDisplayContent(String displayContent) {
		this.displayContent = displayContent;
	}

	public String getFormulaContent() {
		return this.formulaContent;
	}

	public void setFormulaContent(String formulaContent) {
		this.formulaContent = formulaContent;
	}

	public String getFormulaFreq() {
		return this.formulaFreq;
	}

	public void setFormulaFreq(String formulaFreq) {
		this.formulaFreq = formulaFreq;
	}

	public String getFormulaNm() {
		return this.formulaNm;
	}

	public void setFormulaNm(String formulaNm) {
		this.formulaNm = formulaNm;
	}

	public String getFormulaType() {
		return this.formulaType;
	}

	public void setFormulaType(String formulaType) {
		this.formulaType = formulaType;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}