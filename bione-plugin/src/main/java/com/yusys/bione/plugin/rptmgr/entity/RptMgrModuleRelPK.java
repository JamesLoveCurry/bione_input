package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_MODULE_REL database table.
 * 
 */
@Embeddable
public class RptMgrModuleRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="SET_ID", unique=true, nullable=false, length=32)
	private String setId;

	@Column(name="RPT_ID", unique=true, nullable=false, length=32)
	private String rptId;

    public RptMgrModuleRelPK() {
    }
	public String getSetId() {
		return this.setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
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
		if (!(other instanceof RptMgrModuleRelPK)) {
			return false;
		}
		RptMgrModuleRelPK castOther = (RptMgrModuleRelPK)other;
		return 
			this.setId.equals(castOther.setId)
			&& this.rptId.equals(castOther.rptId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.setId.hashCode();
		hash = hash * prime + this.rptId.hashCode();
		
		return hash;
    }
}