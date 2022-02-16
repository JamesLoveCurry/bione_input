package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the BIONE_MSG_AUTH_OBJ_REL database table.
 * 
 */
@Embeddable
public class BioneMsgAuthObjRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="OBJ_ID")
	private String objId;

	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;

	@Column(name="ANNOUNCEMENT_ID")
	private String announcementId;

    public BioneMsgAuthObjRelPK() {
    }
	public String getObjId() {
		return this.objId;
	}
	public void setObjId(String objId) {
		this.objId = objId;
	}
	public String getLogicSysNo() {
		return this.logicSysNo;
	}
	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}
	public String getAnnouncementId() {
		return this.announcementId;
	}
	public void setAnnouncementId(String announcementId) {
		this.announcementId = announcementId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BioneMsgAuthObjRelPK)) {
			return false;
		}
		BioneMsgAuthObjRelPK castOther = (BioneMsgAuthObjRelPK)other;
		return 
			this.objId.equals(castOther.objId)
			&& this.logicSysNo.equals(castOther.logicSysNo)
			&& this.announcementId.equals(castOther.announcementId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.objId.hashCode();
		hash = hash * prime + this.logicSysNo.hashCode();
		hash = hash * prime + this.announcementId.hashCode();
		
		return hash;
    }
}