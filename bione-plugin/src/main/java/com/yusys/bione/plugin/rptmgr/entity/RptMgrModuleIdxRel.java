package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_MODULE_IDX_REL database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_MODULE_IDX_REL")
public class RptMgrModuleIdxRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrModuleIdxRelPK id;

	@Column(name="FILTER_FORMULA", length=2000)
	private String filterFormula;

    public RptMgrModuleIdxRel() {
    }

	public RptMgrModuleIdxRelPK getId() {
		return this.id;
	}

	public void setId(RptMgrModuleIdxRelPK id) {
		this.id = id;
	}
	
	public String getFilterFormula() {
		return this.filterFormula;
	}

	public void setFilterFormula(String filterFormula) {
		this.filterFormula = filterFormula;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptMgrModuleIdxRel other = (RptMgrModuleIdxRel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}