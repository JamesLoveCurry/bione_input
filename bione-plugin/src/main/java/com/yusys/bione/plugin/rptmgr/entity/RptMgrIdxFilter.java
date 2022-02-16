package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_IDX_FILTER database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_IDX_FILTER")
public class RptMgrIdxFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrIdxFilterPK id;

	@Column(name="FILTER_MODE", length=10)
	private String filterMode;

	@Column(name="FILTER_VAL", length=2000)
	private String filterVal;

    public RptMgrIdxFilter() {
    }

	public RptMgrIdxFilterPK getId() {
		return this.id;
	}

	public void setId(RptMgrIdxFilterPK id) {
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
		RptMgrIdxFilter other = (RptMgrIdxFilter) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}