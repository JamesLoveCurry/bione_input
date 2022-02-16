package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_COMP_GRP database table.
 * 
 */
@Embeddable
public class RptIdxCompGrpPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="COMPGRP_ID")
	private String compgrpId;

	@Column(name="MAIN_INDEX_NO")
	private String mainIndexNo;

	@Column(name="INDEX_NO")
	private String indexNo;

	public RptIdxCompGrpPK() {
	}
	public String getCompgrpId() {
		return this.compgrpId;
	}
	public void setCompgrpId(String compgrpId) {
		this.compgrpId = compgrpId;
	}
	public String getMainIndexNo() {
		return this.mainIndexNo;
	}
	public void setMainIndexNo(String mainIndexNo) {
		this.mainIndexNo = mainIndexNo;
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
		if (!(other instanceof RptIdxCompGrpPK)) {
			return false;
		}
		RptIdxCompGrpPK castOther = (RptIdxCompGrpPK)other;
		return 
			this.compgrpId.equals(castOther.compgrpId)
			&& this.mainIndexNo.equals(castOther.mainIndexNo)
			&& this.indexNo.equals(castOther.indexNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.compgrpId.hashCode();
		hash = hash * prime + this.mainIndexNo.hashCode();
		hash = hash * prime + this.indexNo.hashCode();
		
		return hash;
	}
}