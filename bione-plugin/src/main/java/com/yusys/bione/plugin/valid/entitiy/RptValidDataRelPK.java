package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_VALID_DATA_REL database table.
 * 
 */
@Embeddable
public class RptValidDataRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="VALID_GID")
	private String validGid;

	@Column(name="CHECK_ID")
	private String checkId;

	@Column(name="VALID_TYPE")
	private String validType;

	public RptValidDataRelPK() {
	}
	public String getValidGid() {
		return this.validGid;
	}
	public void setValidGid(String validGid) {
		this.validGid = validGid;
	}
	public String getCheckId() {
		return this.checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public String getValidType() {
		return this.validType;
	}
	public void setValidType(String validType) {
		this.validType = validType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptValidDataRelPK)) {
			return false;
		}
		RptValidDataRelPK castOther = (RptValidDataRelPK)other;
		return 
			this.validGid.equals(castOther.validGid)
			&& this.checkId.equals(castOther.checkId)
			&& this.validType.equals(castOther.validType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.validGid.hashCode();
		hash = hash * prime + this.checkId.hashCode();
		hash = hash * prime + this.validType.hashCode();
		
		return hash;
	}
}