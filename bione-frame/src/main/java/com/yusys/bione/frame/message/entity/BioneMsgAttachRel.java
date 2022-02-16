package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_MSG_ATTACH_REL database table.
 * 
 */
@Entity
@Table(name="BIONE_MSG_ATTACH_REL")
public class BioneMsgAttachRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneMsgAttachRelPK id;

    public BioneMsgAttachRel() {
    }

	public BioneMsgAttachRelPK getId() {
		return this.id;
	}

	public void setId(BioneMsgAttachRelPK id) {
		this.id = id;
	}
	
}