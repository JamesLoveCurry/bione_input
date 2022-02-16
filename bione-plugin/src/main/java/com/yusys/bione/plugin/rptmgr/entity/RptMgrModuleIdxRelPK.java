package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_MODULE_IDX_REL database table.
 * 
 */
@Embeddable
public class RptMgrModuleIdxRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="SET_ID", unique=true, nullable=false, length=32)
	private String setId;

	@Column(name="INDEX_NO", unique=true, nullable=false, length=10)
	private String indexNo;

	@Column(name="COL_ID", unique=true, nullable=false, length=32)
	private String colId;

	@Column(name="RPT_ID", unique=true, nullable=false, length=32)
	private String rptId;

    public RptMgrModuleIdxRelPK() {
    }
	public String getSetId() {
		return this.setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
	}
	public String getIndexNo() {
		return this.indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public String getColId() {
		return this.colId;
	}
	public void setColId(String colId) {
		this.colId = colId;
	}
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrModuleIdxRelPK)) {
			return false;
		}
		RptMgrModuleIdxRelPK castOther = (RptMgrModuleIdxRelPK)other;
		return 
			this.setId.equals(castOther.setId)
			&& this.indexNo.equals(castOther.indexNo)
			&& this.colId.equals(castOther.colId)
			&& this.rptId.equals(castOther.rptId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.setId.hashCode();
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + this.colId.hashCode();
		hash = hash * prime + this.rptId.hashCode();
		
		return hash;
    }
}