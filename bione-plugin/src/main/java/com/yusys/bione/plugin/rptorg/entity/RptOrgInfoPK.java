package com.yusys.bione.plugin.rptorg.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_FRS_ORG database table.
 * 
 */
@Embeddable
public class RptOrgInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ORG_NO", unique=true, nullable=false, length=32)
	private String orgNo;

	@Column(name="ORG_TYPE", unique=true, nullable=false, length=10)
	private String orgType;

    public RptOrgInfoPK() {
    }
	public String getOrgNo() {
		return this.orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public String getOrgType() {
		return this.orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptOrgInfoPK)) {
			return false;
		}
		RptOrgInfoPK castOther = (RptOrgInfoPK)other;
		return 
			this.orgNo.equals(castOther.orgNo)
			&& this.orgType.equals(castOther.orgType);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orgNo.hashCode();
		hash = hash * prime + this.orgType.hashCode();
		
		return hash;
    }
}