package com.yusys.bione.frame.passwd.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the BIONE_PWD_HIS database table.
 * 
 */
@Entity
@Table(name="BIONE_PWD_HIS")
public class BionePwdHis implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;

	@Column(name="NEW_PWD")
	private String newPwd;

	@Column(name="OLD_PWD")
	private String oldPwd;

	@Column(name="UPDATE_TIME")
	private Timestamp updateTime;

	@Column(name="USER_ID")
	private String userId;

    public BionePwdHis() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getNewPwd() {
		return this.newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public String getOldPwd() {
		return this.oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public Timestamp getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}