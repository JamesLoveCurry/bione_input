package com.yusys.bione.frame.passwd.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the BIONE_USER_LOCK_INFO database table.
 * 
 */
@Embeddable
public class BioneUserLockInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;

	@Column(name="USER_ID")
	private String userId;

    public BioneUserLockInfoPK() {
    }
	public String getLogicSysNo() {
		return this.logicSysNo;
	}
	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BioneUserLockInfoPK)) {
			return false;
		}
		BioneUserLockInfoPK castOther = (BioneUserLockInfoPK)other;
		return 
			this.logicSysNo.equals(castOther.logicSysNo)
			&& this.userId.equals(castOther.userId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.logicSysNo.hashCode();
		hash = hash * prime + this.userId.hashCode();
		
		return hash;
    }
}