package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_FAV_IDX_DIM_FILTER database table.
 * 
 */
@Embeddable
public class RptFavIdxDimFilterPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DETAIL_ID", unique=true, nullable=false, length=32)
	private String detailId;

	@Column(name="DIM_NO", unique=true, nullable=false, length=32)
	private String dimNo;

    public RptFavIdxDimFilterPK() {
    }
	public String getDetailId() {
		return this.detailId;
	}
	public void setDetailId(String detailId) {
		this.detailId = detailId;
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
		if (!(other instanceof RptFavIdxDimFilterPK)) {
			return false;
		}
		RptFavIdxDimFilterPK castOther = (RptFavIdxDimFilterPK)other;
		return 
			this.detailId.equals(castOther.detailId)
			&& this.dimNo.equals(castOther.dimNo);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.detailId.hashCode();
		hash = hash * prime + this.dimNo.hashCode();
		
		return hash;
    }
}