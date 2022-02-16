package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_FAV_IDX_DIM_FILTER database table.
 * 
 */
@Entity
@Table(name="RPT_FAV_IDX_DIM_FILTER")
public class RptFavIdxDimFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptFavIdxDimFilterPK id;

	@Column(name="FILTER_MODE", length=10)
	private String filterMode;

	@Column(name="FILTER_VAL", length=500)
	private String filterVal;
	
	@Column(name="FILTER_EXPLAIN", length=500)
	private String filterExplain;

    public String getFilterExplain() {
		return filterExplain;
	}

	public void setFilterExplain(String filterExplain) {
		this.filterExplain = filterExplain;
	}

	public RptFavIdxDimFilter() {
    }

	public RptFavIdxDimFilterPK getId() {
		return this.id;
	}

	public void setId(RptFavIdxDimFilterPK id) {
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