package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_MEASURE_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_MEASURE_INFO")
public class RptIdxMeasureInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MEASURE_NO", unique=true, nullable=false, length=32)
	private String measureNo;

	@Column(name="CALC_FORMULA", length=2000)
	private String calcFormula;

	@Column(name="MEASURE_NM", nullable=false, length=100)
	private String measureNm;

	@Column(name="MEASURE_TYPE", length=10)
	private String measureType;

	@Column(length=500)
	private String remark;

    public RptIdxMeasureInfo() {
    }

	public String getMeasureNo() {
		return this.measureNo;
	}

	public void setMeasureNo(String measureNo) {
		this.measureNo = measureNo;
	}

	public String getCalcFormula() {
		return this.calcFormula;
	}

	public void setCalcFormula(String calcFormula) {
		this.calcFormula = calcFormula;
	}

	public String getMeasureNm() {
		return this.measureNm;
	}

	public void setMeasureNm(String measureNm) {
		this.measureNm = measureNm;
	}

	public String getMeasureType() {
		return this.measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}