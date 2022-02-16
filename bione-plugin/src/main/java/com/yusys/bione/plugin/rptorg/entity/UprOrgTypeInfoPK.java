package com.yusys.bione.plugin.rptorg.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

/**
 * The primary key class for the UPR_ORG_TYPE_INFO database table.
 * 
 */
@Embeddable
public class UprOrgTypeInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ORG_TYPE")
	private String orgType;

	@Column(name="VERSION_ID")
	private BigDecimal versionId;

	public UprOrgTypeInfoPK() {
	}
	public String getOrgType() {
		return this.orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	public BigDecimal getVersionId() {
		return this.versionId;
	}
	public void setVersionId(BigDecimal versionId) {
		this.versionId = versionId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orgType == null) ? 0 : orgType.hashCode());
		result = prime * result
				+ ((versionId == null) ? 0 : versionId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UprOrgTypeInfoPK other = (UprOrgTypeInfoPK) obj;
		if (orgType == null) {
			if (other.orgType != null)
				return false;
		} else if (!orgType.equals(other.orgType))
			return false;
		if (versionId == null) {
			if (other.versionId != null)
				return false;
		} else if (!versionId.equals(other.versionId))
			return false;
		return true;
	}

}