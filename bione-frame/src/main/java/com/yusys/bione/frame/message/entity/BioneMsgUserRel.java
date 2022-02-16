package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_MSG_USER_REL database table.
 * 
 */
@Entity
@Table(name="BIONE_MSG_USER_REL")
public class BioneMsgUserRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneMsgUserRelPK id;

    public BioneMsgUserRel() {
    }

	public BioneMsgUserRelPK getId() {
		return this.id;
	}

	public void setId(BioneMsgUserRelPK id) {
		this.id = id;
	}
	
}