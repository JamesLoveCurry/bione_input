package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ANA_CHARTS_FORMULA_REL database table.
 * 
 */
@Entity
@Table(name="RPT_ANA_CHARTS_FORMULA_REL")
@NamedQuery(name="RptAnaChartsFormulaRel.findAll", query="SELECT r FROM RptAnaChartsFormulaRel r")
public class RptAnaChartsFormulaRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="REL_ID")
	private String relId;

	@Column(name="CHART_ID")
	private String chartId;

	@Column(name="FORMULA_CONTENT")
	private String formulaContent;

	@Column(name="FORMULA_ID")
	private String formulaId;

	@Column(name="FORMULA_NM")
	private String formulaNm;

	@Column(name="FORMULA_TYPE")
	private String formulaType;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	public RptAnaChartsFormulaRel() {
	}

	public String getRelId() {
		return this.relId;
	}

	public void setRelId(String relId) {
		this.relId = relId;
	}

	public String getChartId() {
		return this.chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getFormulaContent() {
		return this.formulaContent;
	}

	public void setFormulaContent(String formulaContent) {
		this.formulaContent = formulaContent;
	}

	public String getFormulaId() {
		return this.formulaId;
	}

	public void setFormulaId(String formulaId) {
		this.formulaId = formulaId;
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

}