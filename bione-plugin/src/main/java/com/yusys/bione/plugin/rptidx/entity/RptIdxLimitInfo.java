package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_LIMIT_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_LIMIT_INFO")
public class RptIdxLimitInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxLimitInfoPK id;

	@Column(name="LOWER_LIMIT")
	private BigDecimal lowerLimit;

	@Column(name="UPPER_LIMIT")
	private BigDecimal upperLimit;

	@Column(name="WARNING_LIMIT")
	private BigDecimal warningLimit;

	@Column(name="WARNING_MODE")
	private String warningMode;

	public RptIdxLimitInfo() {
	}

	public RptIdxLimitInfoPK getId() {
		return this.id;
	}

	public void setId(RptIdxLimitInfoPK id) {
		this.id = id;
	}

	public BigDecimal getLowerLimit() {
		return this.lowerLimit;
	}

	public void setLowerLimit(BigDecimal lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public BigDecimal getUpperLimit() {
		return this.upperLimit;
	}

	public void setUpperLimit(BigDecimal upperLimit) {
		this.upperLimit = upperLimit;
	}

	public BigDecimal getWarningLimit() {
		return this.warningLimit;
	}

	public void setWarningLimit(BigDecimal warningLimit) {
		this.warningLimit = warningLimit;
	}

	public String getWarningMode() {
		return this.warningMode;
	}

	public void setWarningMode(String warningMode) {
		this.warningMode = warningMode;
	}

}