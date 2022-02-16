package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_FILTER_INFO database table.
 * 
 */
@Embeddable
public class RptIdxFilterInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_NO", unique=true, nullable=false, length=32)
	private String indexNo;

	@Column(name="INDEX_VER_ID", unique=true, nullable=false, precision=18)
	private long indexVerId;

	@Column(name="DIM_NO", unique=true, nullable=false, length=32)
	private String dimNo;

    public RptIdxFilterInfoPK() {
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
		if (!(other instanceof RptIdxFilterInfoPK)) {
			return false;
		}
		RptIdxFilterInfoPK castOther = (RptIdxFilterInfoPK)other;
		return 
			this.indexNo.equals(castOther.indexNo)
			&& (this.indexVerId == castOther.indexVerId)
			&& this.dimNo.equals(castOther.dimNo);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + ((int) (this.indexVerId ^ (this.indexVerId >>> 32)));
		hash = hash * prime + this.dimNo.hashCode();
		
		return hash;
    }
}