package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_DS_REL database table.
 * 
 */
@Embeddable
public class RptIdxDsRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="RPT_ID", unique=true, nullable=false, length=20)
	private String rptId;

	@Column(name="INDEX_NO", unique=true, nullable=false, length=32)
	private String indexNo;

	@Column(name="INDEX_VER_ID", unique=true, nullable=false, precision=18)
	private long indexVerId;

	@Column(name="SET_ID", unique=true, nullable=false, length=32)
	private String setId;

	@Column(name="COL_ID", unique=true, nullable=false, length=32)
	private String colId;

    public RptIdxDsRelPK() {
    }
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
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
	public String getSetId() {
		return this.setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
	}
	public String getColId() {
		return this.colId;
	}
	public void setColId(String colId) {
		this.colId = colId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptIdxDsRelPK)) {
			return false;
		}
		RptIdxDsRelPK castOther = (RptIdxDsRelPK)other;
		return 
			this.rptId.equals(castOther.rptId)
			&& this.indexNo.equals(castOther.indexNo)
			&& (this.indexVerId == castOther.indexVerId)
			&& this.setId.equals(castOther.setId)
			&& this.colId.equals(castOther.colId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rptId.hashCode();
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + ((int) (this.indexVerId ^ (this.indexVerId >>> 32)));
		hash = hash * prime + this.setId.hashCode();
		hash = hash * prime + this.colId.hashCode();
		
		return hash;
    }
}