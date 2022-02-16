package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ANA_CHARTS_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_ANA_CHARTS_INFO")
@NamedQuery(name="RptAnaChartsInfo.findAll", query="SELECT r FROM RptAnaChartsInfo r")
public class RptAnaChartsInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHART_ID")
	private String chartId;

	@Column(name="CHART_CFG")
	private String chartCfg;

	@Column(name="CHART_COLOR")
	private String chartColor;

	@Column(name="CHART_NM")
	private String chartNm;

	@Column(name="CHART_TYPE")
	private String chartType;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	private String remark;

	@Column(name="SHOW_TYPE")
	private String showType;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="TEXT_CFG")
	private String textCfg;

	public RptAnaChartsInfo() {
	}

	public String getChartId() {
		return this.chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getChartCfg() {
		return this.chartCfg;
	}

	public void setChartCfg(String chartCfg) {
		this.chartCfg = chartCfg;
	}

	public String getChartColor() {
		return this.chartColor;
	}

	public void setChartColor(String chartColor) {
		this.chartColor = chartColor;
	}

	public String getChartNm() {
		return this.chartNm;
	}

	public void setChartNm(String chartNm) {
		this.chartNm = chartNm;
	}

	public String getChartType() {
		return this.chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
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

	public String getShowType() {
		return this.showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTextCfg() {
		return this.textCfg;
	}

	public void setTextCfg(String textCfg) {
		this.textCfg = textCfg;
	}

}