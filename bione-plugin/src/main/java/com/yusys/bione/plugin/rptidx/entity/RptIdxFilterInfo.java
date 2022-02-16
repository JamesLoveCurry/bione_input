package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_FILTER_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_FILTER_INFO")
public class RptIdxFilterInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxFilterInfoPK id;

	@Column(name="FILTER_MODE", length=10)
	private String filterMode;

	@Column(name="FILTER_VAL", length=2000)
	private String filterVal;

    public RptIdxFilterInfo() {
    }

	public RptIdxFilterInfoPK getId() {
		return this.id;
	}

	public void setId(RptIdxFilterInfoPK id) {
		this.id = id;
	}
	
	public String getFilterMode() {
		return this.filterMode;
	}

	public void setFilterMode(String filterMode) {
		this.filterMode = filterMode;
	}

	public String getFilterVal() {
		return this.filterVal;
	}

	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}

}