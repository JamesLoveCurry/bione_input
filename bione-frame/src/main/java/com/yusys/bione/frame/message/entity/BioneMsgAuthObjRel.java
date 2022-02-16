package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_MSG_AUTH_OBJ_REL database table.
 * 
 */
@Entity
@Table(name="BIONE_MSG_AUTH_OBJ_REL")
public class BioneMsgAuthObjRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneMsgAuthObjRelPK id;

	@Column(name="OBJ_DEF_NO")
	private String objDefNo;

    public BioneMsgAuthObjRel() {
    }

	public BioneMsgAuthObjRelPK getId() {
		return this.id;
	}

	public void setId(BioneMsgAuthObjRelPK id) {
		this.id = id;
	}
	
	public String getObjDefNo() {
		return this.objDefNo;
	}

	public void setObjDefNo(String objDefNo) {
		this.objDefNo = objDefNo;
	}

}