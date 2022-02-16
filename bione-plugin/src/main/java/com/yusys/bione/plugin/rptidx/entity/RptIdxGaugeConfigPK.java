package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_GAUGE_CONFIG database table.
 * 
 */
@Embeddable
public class RptIdxGaugeConfigPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="INDEX_VER_ID")
	private long indexVerId;

	@Column(name="USER_ID")
	private String userId;

	public RptIdxGaugeConfigPK() {
	}
	public String getIndexNo() {
		return this.indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public long getIndexVerId() {
		return this.indexVerId;
	}
	public void setIndexVerId(long indexVerId) {
		this.indexVerId = indexVerId;
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptIdxGaugeConfigPK)) {
			return false;
		}
		RptIdxGaugeConfigPK castOther = (RptIdxGaugeConfigPK)other;
		return 
			this.indexNo.equals(castOther.indexNo)
			&& (this.indexVerId == castOther.indexVerId)
			&& this.userId.equals(castOther.userId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + ((int) (this.indexVerId ^ (this.indexVerId >>> 32)));
		hash = hash * prime + this.userId.hashCode();
		
		return hash;
	}
}