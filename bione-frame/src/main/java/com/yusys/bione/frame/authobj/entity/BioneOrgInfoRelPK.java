package com.yusys.bione.frame.authobj.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the BIONE_ORG_INFO_REL database table.
 * 
 */
@Embeddable
public class BioneOrgInfoRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ORG_NO")
	private String orgNo;

	@Column(name="REL_ORG_NO")
	private String relOrgNo;

	public BioneOrgInfoRelPK() {
	}
	public String getOrgNo() {
		return this.orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public String getRelOrgNo() {
		return this.relOrgNo;
	}
	public void setRelOrgNo(String relOrgNo) {
		this.relOrgNo = relOrgNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BioneOrgInfoRelPK)) {
			return false;
		}
		BioneOrgInfoRelPK castOther = (BioneOrgInfoRelPK)other;
		return 
			this.orgNo.equals(castOther.orgNo)
			&& this.relOrgNo.equals(castOther.relOrgNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orgNo.hashCode();
		hash = hash * prime + this.relOrgNo.hashCode();
		
		return hash;
	}
}