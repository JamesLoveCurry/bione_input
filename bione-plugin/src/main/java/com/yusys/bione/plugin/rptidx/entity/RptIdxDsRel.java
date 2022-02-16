package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_DS_REL database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_DS_REL")
public class RptIdxDsRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxDsRelPK id;
	@Column(name="FILTER_FORMULA", length=2000)
	private String filterFormula;
    public RptIdxDsRel() {
    }

	public RptIdxDsRelPK getId() {
		return this.id;
	}

	public void setId(RptIdxDsRelPK id) {
		this.id = id;
	}

	public String getFilterFormula() {
		return filterFormula;
	}

	public void setFilterFormula(String filterFormula) {
		this.filterFormula = filterFormula;
	}
	
}