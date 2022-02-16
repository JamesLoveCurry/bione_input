package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the BIONE_MSG_USER_STATE database table.
 * 
 */
@Embeddable
public class BioneMsgUserStatePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;

    public BioneMsgUserStatePK() {
    }
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLogicSysNo() {
		return this.logicSysNo;
	}
	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BioneMsgUserStatePK)) {
			return false;
		}
		BioneMsgUserStatePK castOther = (BioneMsgUserStatePK)other;
		return 
			this.userId.equals(castOther.userId)
			&& this.logicSysNo.equals(castOther.logicSysNo);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.logicSysNo.hashCode();
		
		return hash;
    }
}