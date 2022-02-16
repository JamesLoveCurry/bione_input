package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ANA_CHARTS_DATE_REL database table.
 * 
 */
@Entity
@Table(name="RPT_ANA_CHARTS_DATE_REL")
@NamedQuery(name="RptAnaChartsDateRel.findAll", query="SELECT r FROM RptAnaChartsDateRel r")
public class RptAnaChartsDateRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHART_ID")
	private String chartId;

	@Column(name="DATE_FREQ")
	private String dateFreq;

	@Column(name="END_DATE")
	private String endDate;

	private String remark;

	@Column(name="SHOW_TYPE")
	private String showType;

	@Column(name="START_DATE")
	private String startDate;

	public RptAnaChartsDateRel() {
	}

	public String getChartId() {
		return this.chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getDateFreq() {
		return this.dateFreq;
	}

	public void setDateFreq(String dateFreq) {
		this.dateFreq = dateFreq;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
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

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

}