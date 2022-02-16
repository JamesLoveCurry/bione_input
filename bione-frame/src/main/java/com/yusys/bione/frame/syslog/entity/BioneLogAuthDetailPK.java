package com.yusys.bione.frame.syslog.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the BIONE_LOG_AUTH_DETAIL database table.
 * 
 */
@Embeddable
public class BioneLogAuthDetailPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="LOG_ID")
	private String logId;

	@Column(name="RES_DEF_NO")
	private String resDefNo;

	@Column(name="RES_ID")
	private String resId;

    public BioneLogAuthDetailPK() {
    }
	public String getLogId() {
		return this.logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getResDefNo() {
		return this.resDefNo;
	}
	public void setResDefNo(String resDefNo) {
		this.resDefNo = resDefNo;
	}
	public String getResId() {
		return this.resId;
	}
	public void setResId(String resId) {
		this.resId = resId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BioneLogAuthDetailPK)) {
			return false;
		}
		BioneLogAuthDetailPK castOther = (BioneLogAuthDetailPK)other;
		return 
			this.logId.equals(castOther.logId)
			&& this.resDefNo.equals(castOther.resDefNo)
			&& this.resId.equals(castOther.resId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.logId.hashCode();
		hash = hash * prime + this.resDefNo.hashCode();
		hash = hash * prime + this.resId.hashCode();
		
		return hash;
    }
}