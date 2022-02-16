package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_DS_DIM_FILTER database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_DS_DIM_FILTER")
public class RptIdxDsDimFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxDsDimFilterPK id;

	@Column(name="FILTER_TYPE", length=10)
	private String filterType;

	@Column(name="FILTER_VAL", length=500)
	private String filterVal;

    public RptIdxDsDimFilter() {
    }

	public RptIdxDsDimFilterPK getId() {
		return this.id;
	}

	public void setId(RptIdxDsDimFilterPK id) {
		this.id = id;
	}
	
	public String getFilterType() {
		return this.filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFilterVal() {
		return this.filterVal;
	}

	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}

}