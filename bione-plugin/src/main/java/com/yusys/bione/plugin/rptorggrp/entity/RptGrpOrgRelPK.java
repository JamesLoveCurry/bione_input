package com.yusys.bione.plugin.rptorggrp.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the rpt_grp_org_rel database table.
 * 
 */
@Embeddable
public class RptGrpOrgRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ORG_NO")
	private String orgNo;

	@Column(name="ORG_TYPE")
	private String orgType;

	@Column(name="GRP_ID")
	private String grpId;

    public RptGrpOrgRelPK() {
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
	public String getGrpId() {
		return this.grpId;
	}
	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptGrpOrgRelPK)) {
			return false;
		}
		RptGrpOrgRelPK castOther = (RptGrpOrgRelPK)other;
		return 
			this.orgNo.equals(castOther.orgNo)
			&& this.orgType.equals(castOther.orgType)
			&& this.grpId.equals(castOther.grpId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orgNo.hashCode();
		hash = hash * prime + this.orgType.hashCode();
		hash = hash * prime + this.grpId.hashCode();
		
		return hash;
    }
}