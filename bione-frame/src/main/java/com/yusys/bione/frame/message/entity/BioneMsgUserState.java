package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_MSG_USER_STATE database table.
 * 
 */
@Entity
@Table(name="BIONE_MSG_USER_STATE")
public class BioneMsgUserState implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneMsgUserStatePK id;

	@Column(name="MSG_STS")
	private String msgSts;

	@Column(name="PUSH_STS")
	private String pushSts;

    public BioneMsgUserState() {
    }

	public BioneMsgUserStatePK getId() {
		return this.id;
	}

	public void setId(BioneMsgUserStatePK id) {
		this.id = id;
	}
	
	public String getMsgSts() {
		return this.msgSts;
	}

	public void setMsgSts(String msgSts) {
		this.msgSts = msgSts;
	}

	public String getPushSts() {
		return this.pushSts;
	}

	public void setPushSts(String pushSts) {
		this.pushSts = pushSts;
	}

}