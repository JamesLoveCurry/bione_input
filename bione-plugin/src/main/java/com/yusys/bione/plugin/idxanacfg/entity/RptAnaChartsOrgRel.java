package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ANA_CHARTS_ORG_REL database table.
 * 
 */
@Entity
@Table(name="RPT_ANA_CHARTS_ORG_REL")
@NamedQuery(name="RptAnaChartsOrgRel.findAll", query="SELECT r FROM RptAnaChartsOrgRel r")
public class RptAnaChartsOrgRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHART_ID")
	private String chartId;

	private String remark;

	@Column(name="SHOW_ORG")
	private String showOrg;

	@Column(name="SHOW_TYPE")
	private String showType;

	public RptAnaChartsOrgRel() {
	}

	public String getChartId() {
		return this.chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getShowOrg() {
		return this.showOrg;
	}

	public void setShowOrg(String showOrg) {
		this.showOrg = showOrg;
	}

	public String getShowType() {
		return this.showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

}