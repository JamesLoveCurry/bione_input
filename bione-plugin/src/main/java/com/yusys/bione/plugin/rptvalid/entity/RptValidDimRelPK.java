package com.yusys.bione.plugin.rptvalid.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_VALID_DIM_REL database table.
 * 
 */
@Embeddable
public class RptValidDimRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CHECK_ID")
	private String checkId;

	@Column(name="VALID_TYPE")
	private String validType;

	@Column(name="DIM_NO")
	private String dimNo;

	public RptValidDimRelPK() {
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
		if (!(other instanceof RptValidDimRelPK)) {
			return false;
		}
		RptValidDimRelPK castOther = (RptValidDimRelPK)other;
		return 
			this.checkId.equals(castOther.checkId)
			&& this.validType.equals(castOther.validType)
			&& this.dimNo.equals(castOther.dimNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.checkId.hashCode();
		hash = hash * prime + this.validType.hashCode();
		hash = hash * prime + this.dimNo.hashCode();
		
		return hash;
	}
}