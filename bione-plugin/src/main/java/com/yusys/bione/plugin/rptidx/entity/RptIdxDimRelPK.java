package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_DIM_REL database table.
 * 
 */
@Embeddable
public class RptIdxDimRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DIM_NO", unique=true, nullable=false, length=32)
	private String dimNo;

	@Column(name="INDEX_NO", unique=true, nullable=false, length=32)
	private String indexNo;

	@Column(name="INDEX_VER_ID", unique=true, nullable=false, precision=18)
	private long indexVerId;

	@Column(name="DS_ID", unique=true, nullable=false, length=32)
	private String dsId;

    public RptIdxDimRelPK() {
    }
	public String getDimNo() {
		return this.dimNo;
	}
	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
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
	public String getDsId() {
		return this.dsId;
	}
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptIdxDimRelPK)) {
			return false;
		}
		RptIdxDimRelPK castOther = (RptIdxDimRelPK)other;
		return 
			this.dimNo.equals(castOther.dimNo)
			&& this.indexNo.equals(castOther.indexNo)
			&& (this.indexVerId == castOther.indexVerId)
			&& this.dsId.equals(castOther.dsId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dimNo.hashCode();
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + ((int) (this.indexVerId ^ (this.indexVerId >>> 32)));
		hash = hash * prime + this.dsId.hashCode();
		
		return hash;
    }
}