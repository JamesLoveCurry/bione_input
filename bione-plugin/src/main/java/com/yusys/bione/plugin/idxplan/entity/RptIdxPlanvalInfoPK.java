package com.yusys.bione.plugin.idxplan.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_PLANVAL_INFO database table.
 * 
 */
@Embeddable
public class RptIdxPlanvalInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="INDEX_VER_ID")
	private long indexVerId;

	@Column(name="ORG_NO")
	private String orgNo;

	public RptIdxPlanvalInfoPK() {
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
	public String getOrgNo() {
		return this.orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptIdxPlanvalInfoPK)) {
			return false;
		}
		RptIdxPlanvalInfoPK castOther = (RptIdxPlanvalInfoPK)other;
		return 
			this.indexNo.equals(castOther.indexNo)
			&& (this.indexVerId == castOther.indexVerId)
			&& this.orgNo.equals(castOther.orgNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + ((int) (this.indexVerId ^ (this.indexVerId >>> 32)));
		hash = hash * prime + this.orgNo.hashCode();
		
		return hash;
	}
}