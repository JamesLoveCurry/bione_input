package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_CABIN_IDX_FAVOUR database table.
 * 
 */
@Embeddable
public class RptCabinIdxFavourPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="INDEX_NO")
	private String indexNo;

	public RptCabinIdxFavourPK() {
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getIndexNo() {
		return this.indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptCabinIdxFavourPK)) {
			return false;
		}
		RptCabinIdxFavourPK castOther = (RptCabinIdxFavourPK)other;
		return 
			this.userId.equals(castOther.userId)
			&& this.indexNo.equals(castOther.indexNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.indexNo.hashCode();
		
		return hash;
	}
}