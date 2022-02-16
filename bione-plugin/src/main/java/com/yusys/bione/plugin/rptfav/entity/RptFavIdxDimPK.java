package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_FAV_IDX_DIM database table.
 * 
 */
@Embeddable
public class RptFavIdxDimPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DIM_NO", unique=true, nullable=false, length=32)
	private String dimNo;

	@Column(name="ORDER_NUM", unique=true, nullable=false, precision=5)
	private long orderNum;

	@Column(name="INSTANCE_ID", length=32)
	private String instanceId;
	
    public RptFavIdxDimPK() {
    }
	public String getDimNo() {
		return this.dimNo;
	}
	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}
	public long getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(long orderNum) {
		this.orderNum = orderNum;
	}

	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptFavIdxDimPK)) {
			return false;
		}
		RptFavIdxDimPK castOther = (RptFavIdxDimPK)other;
		return 
			this.dimNo.equals(castOther.dimNo)
			&& (this.orderNum == castOther.orderNum);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dimNo.hashCode();
		hash = hash * prime + ((int) (this.orderNum ^ (this.orderNum >>> 32)));
		
		return hash;
    }
}