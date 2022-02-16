package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the BIONE_MSG_ATTACH_REL database table.
 * 
 */
@Embeddable
public class BioneMsgAttachRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ATTACH_ID")
	private String attachId;

	@Column(name="MSG_ID")
	private String msgId;

    public BioneMsgAttachRelPK() {
    }
	public String getAttachId() {
		return this.attachId;
	}
	public void setAttachId(String attachId) {
		this.attachId = attachId;
	}
	public String getMsgId() {
		return this.msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BioneMsgAttachRelPK)) {
			return false;
		}
		BioneMsgAttachRelPK castOther = (BioneMsgAttachRelPK)other;
		return 
			this.attachId.equals(castOther.attachId)
			&& this.msgId.equals(castOther.msgId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.attachId.hashCode();
		hash = hash * prime + this.msgId.hashCode();
		
		return hash;
    }
}