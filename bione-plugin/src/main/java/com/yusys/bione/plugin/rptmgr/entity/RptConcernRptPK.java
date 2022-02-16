package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the RPT_CONCERN_RPT database table.
 * 
 */
@Embeddable
public class RptConcernRptPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="MODULE_TYPE")
	private String moduleType;

	public RptConcernRptPK() {
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
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
		if (!(other instanceof RptConcernRptPK)) {
			return false;
		}
		RptConcernRptPK castOther = (RptConcernRptPK)other;
		return 
			this.userId.equals(castOther.userId)
			&& this.rptId.equals(castOther.rptId)
			&& this.moduleType.equals(castOther.moduleType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.rptId.hashCode();
		hash = hash * prime + this.moduleType.hashCode();
		
		return hash;
	}
}