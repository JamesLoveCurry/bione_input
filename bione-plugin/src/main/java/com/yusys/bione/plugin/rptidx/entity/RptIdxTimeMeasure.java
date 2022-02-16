package com.yusys.bione.plugin.rptidx.entity;


import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_idx_time_measure database table.
 * 
 */
@Entity
@Table(name="rpt_idx_time_measure")
public class RptIdxTimeMeasure implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TIME_MEASURE_ID")
	private String timeMeasureId;

	@Column(name="MEASURE_DESC")
	private String measureDesc;

	@Column(name="MEASURE_FREQ")
	private String measureFreq;

	@Column(name="MEASURE_NM")
	private String measureNm;

	@Column(name="MEASURE_TEMPLATE")
	private String measureTemplate;
	
	@Column(name="SORT_ORDER")
	private BigDecimal sortOrder;

    public RptIdxTimeMeasure() {
    }

	public String getTimeMeasureId() {
		return this.timeMeasureId;
	}

	public void setTimeMeasureId(String timeMeasureId) {
		this.timeMeasureId = timeMeasureId;
	}

	public String getMeasureDesc() {
		return this.measureDesc;
	}

	public void setMeasureDesc(String measureDesc) {
		this.measureDesc = measureDesc;
	}

	public String getMeasureFreq() {
		return this.measureFreq;
	}

	public void setMeasureFreq(String measureFreq) {
		this.measureFreq = measureFreq;
	}

	public String getMeasureNm() {
		return this.measureNm;
	}

	public void setMeasureNm(String measureNm) {
		this.measureNm = measureNm;
	}

	public String getMeasureTemplate() {
		return this.measureTemplate;
	}

	public void setMeasureTemplate(String measureTemplate) {
		this.measureTemplate = measureTemplate;
	}

	public BigDecimal getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(BigDecimal sortOrder) {
		this.sortOrder = sortOrder;
	}

}