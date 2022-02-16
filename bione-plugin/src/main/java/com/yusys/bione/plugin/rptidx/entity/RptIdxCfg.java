package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_IDX_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_IDX_CFG")
public class RptIdxCfg implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxCfgPK id;

	@Column(name = "RPT_NUM")
	private String rptNum;

	public RptIdxCfg() {
	}

	public RptIdxCfgPK getId() {
		return id;
	}

	public void setId(RptIdxCfgPK id) {
		this.id = id;
	}

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}
}