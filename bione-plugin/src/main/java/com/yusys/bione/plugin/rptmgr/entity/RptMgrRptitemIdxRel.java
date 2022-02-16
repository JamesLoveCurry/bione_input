package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_RPTITEM_IDX_REL database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_RPTITEM_IDX_REL")
public class RptMgrRptitemIdxRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrRptitemIdxRelPK id;

	@Column(name="FILTER_FORMULA")
	private String filterFormula;

	

    public RptMgrRptitemIdxRel() {
    }

	public RptMgrRptitemIdxRelPK getId() {
		return this.id;
	}

	public void setId(RptMgrRptitemIdxRelPK id) {
		this.id = id;
	}
	
	public String getFilterFormula() {
		return this.filterFormula;
	}

	public void setFilterFormula(String filterFormula) {
		this.filterFormula = filterFormula;
	}

	

}