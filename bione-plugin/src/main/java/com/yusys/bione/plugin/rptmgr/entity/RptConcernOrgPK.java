package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the RPT_CONCERN_ORG database table.
 * 
 */
@Embeddable
public class RptConcernOrgPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="ORG_ID")
	private String orgId;

	@Column(name="MODULE_TYPE")
	private String moduleType;

	public RptConcernOrgPK() {
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrgId() {
		return this.orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getModuleType() {
		return this.moduleType;
	}
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptConcernOrgPK)) {
			return false;
		}
		RptConcernOrgPK castOther = (RptConcernOrgPK)other;
		return 
			this.userId.equals(castOther.userId)
			&& this.orgId.equals(castOther.orgId)
			&& this.moduleType.equals(castOther.moduleType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.orgId.hashCode();
		hash = hash * prime + this.moduleType.hashCode();
		
		return hash;
	}
}