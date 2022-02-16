package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_SIMILAR_GRP database table.
 * 
 */
@Embeddable
public class RptIdxSimilarGrpPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="SIMIGRP_ID")
	private String simigrpId;

	@Column(name="INDEX_NO")
	private String indexNo;

	public RptIdxSimilarGrpPK() {
	}
	public String getSimigrpId() {
		return this.simigrpId;
	}
	public void setSimigrpId(String simigrpId) {
		this.simigrpId = simigrpId;
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
		if (!(other instanceof RptIdxSimilarGrpPK)) {
			return false;
		}
		RptIdxSimilarGrpPK castOther = (RptIdxSimilarGrpPK)other;
		return 
			this.simigrpId.equals(castOther.simigrpId)
			&& this.indexNo.equals(castOther.indexNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.simigrpId.hashCode();
		hash = hash * prime + this.indexNo.hashCode();
		
		return hash;
	}
}