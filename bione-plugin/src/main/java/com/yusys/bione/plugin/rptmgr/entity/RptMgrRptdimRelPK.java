package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_RPTDIM_REL database table.
 * 
 */
@Embeddable
public class RptMgrRptdimRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="DIM_NO")
	private String dimNo;

    public RptMgrRptdimRelPK() {
    }
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}
	public String getDimNo() {
		return this.dimNo;
	}
	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrRptdimRelPK)) {
			return false;
		}
		RptMgrRptdimRelPK castOther = (RptMgrRptdimRelPK)other;
		return 
			this.rptId.equals(castOther.rptId)
			&& this.dimNo.equals(castOther.dimNo);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rptId.hashCode();
		hash = hash * prime + this.dimNo.hashCode();
		
		return hash;
    }
}