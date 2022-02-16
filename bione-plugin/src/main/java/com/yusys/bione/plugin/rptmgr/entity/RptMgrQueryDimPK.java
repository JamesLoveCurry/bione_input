package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_QUERY_DIM database table.
 * 
 */
@Embeddable
public class RptMgrQueryDimPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="RPT_VER_ID")
	private long rptVerId;

	@Column(name="ORDER_NUM")
	private long orderNum;

    public RptMgrQueryDimPK() {
    }
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}
	public long getRptVerId() {
		return this.rptVerId;
	}
	public void setRptVerId(long rptVerId) {
		this.rptVerId = rptVerId;
	}
	public long getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(long orderNum) {
		this.orderNum = orderNum;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrQueryDimPK)) {
			return false;
		}
		RptMgrQueryDimPK castOther = (RptMgrQueryDimPK)other;
		return 
			this.rptId.equals(castOther.rptId)
			&& (this.rptVerId == castOther.rptVerId)
			&& (this.orderNum == castOther.orderNum);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rptId.hashCode();
		hash = hash * prime + ((int) (this.rptVerId ^ (this.rptVerId >>> 32)));
		hash = hash * prime + ((int) (this.orderNum ^ (this.orderNum >>> 32)));
		
		return hash;
    }
}