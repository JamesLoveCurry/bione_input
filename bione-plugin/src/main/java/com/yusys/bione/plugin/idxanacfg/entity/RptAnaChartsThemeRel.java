package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ANA_CHARTS_THEME_REL database table.
 * 
 */
@Entity
@Table(name="RPT_ANA_CHARTS_THEME_REL")
@NamedQuery(name="RptAnaChartsThemeRel.findAll", query="SELECT r FROM RptAnaChartsThemeRel r")
public class RptAnaChartsThemeRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHART_ID")
	private String chartId;

	@Column(name="THEME_ID")
	private String themeId;

	public RptAnaChartsThemeRel() {
	}

	public String getChartId() {
		return this.chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getThemeId() {
		return this.themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

}