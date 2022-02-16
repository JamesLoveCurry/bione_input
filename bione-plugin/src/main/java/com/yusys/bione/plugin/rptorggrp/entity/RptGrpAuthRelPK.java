package com.yusys.bione.plugin.rptorggrp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_GRP_AUTH_REL database table.
 * 
 */
@Embeddable
public class RptGrpAuthRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ORG_NO")
	private String orgNo;

	@Column(name="GRP_ID")
	private String grpId;

	public RptGrpAuthRelPK() {
	}
	public String getOrgNo() {
		return this.orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
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
		if (!(other instanceof RptGrpAuthRelPK)) {
			return false;
		}
		RptGrpAuthRelPK castOther = (RptGrpAuthRelPK)other;
		return 
			this.orgNo.equals(castOther.orgNo)
			&& this.grpId.equals(castOther.grpId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orgNo.hashCode();
		hash = hash * prime + this.grpId.hashCode();
		
		return hash;
	}
}