package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DETAIL_TMP_SUM database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_TMP_SUM")
@NamedQuery(name="RptDetailTmpSum.findAll", query="SELECT r FROM RptDetailTmpSum r")
public class RptDetailTmpSum implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDetailTmpSumPK id;

	@Column(name="COL_TYPE")
	private String colType;

	@Column(name="SUM_MODE")
	private String sumMode;

	public RptDetailTmpSum() {
	}

	public RptDetailTmpSumPK getId() {
		return this.id;
	}

	public void setId(RptDetailTmpSumPK id) {
		this.id = id;
	}

	public String getColType() {
		return this.colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public String getSumMode() {
		return this.sumMode;
	}

	public void setSumMode(String sumMode) {
		this.sumMode = sumMode;
	}

}