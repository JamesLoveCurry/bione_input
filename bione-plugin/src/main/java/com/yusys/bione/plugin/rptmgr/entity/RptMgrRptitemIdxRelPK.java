package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_RPTITEM_IDX_REL database table.
 * 
 */
@Embeddable
public class RptMgrRptitemIdxRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="RPT_ITEM_ID")
	private String rptItemId;

	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="INDEX_NO")
	private String indexNo;
	
    public RptMgrRptitemIdxRelPK() {
    }
	public String getRptItemId() {
		return this.rptItemId;
	}
	public void setRptItemId(String rptItemId) {
		this.rptItemId = rptItemId;
	}
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrRptitemIdxRelPK)) {
			return false;
		}
		RptMgrRptitemIdxRelPK castOther = (RptMgrRptitemIdxRelPK)other;
		return 
			this.rptItemId.equals(castOther.rptItemId)
			&& this.rptId.equals(castOther.rptId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rptItemId.hashCode();
		hash = hash * prime + this.rptId.hashCode();
		
		return hash;
    }
	
	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
}