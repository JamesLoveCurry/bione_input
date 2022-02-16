package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_AUTH_USER_REL database table.
 * 
 */
@Embeddable
public class RptMgrAuthUserRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="AUTH_ID")
	private String authId;

	@Column(name="USER_NO")
	private String userNo;

    public RptMgrAuthUserRelPK() {
    }
	public String getAuthId() {
		return this.authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public String getUserNo() {
		return this.userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrAuthUserRelPK)) {
			return false;
		}
		RptMgrAuthUserRelPK castOther = (RptMgrAuthUserRelPK)other;
		return 
			this.authId.equals(castOther.authId)
			&& this.userNo.equals(castOther.userNo);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.authId.hashCode();
		hash = hash * prime + this.userNo.hashCode();
		
		return hash;
    }
}