package com.yusys.bione.frame.syslog.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the BIONE_LOG_AUTH_INFO database table.
 * 
 */
@Entity
@Table(name = "BIONE_LOG_AUTH_INFO")
public class BioneLogAuthInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LOG_ID")
	private String logId;

	@Column(name = "AUTH_OBJ_ID")
	private String authObjId;

	@Column(name = "AUTH_OBJ_NO")
	private String authObjNo;

	@Column(name = "LOGIC_SYS_NO")
	private String logicSysNo;

	@Column(name = "OPER_TIME")
	private Timestamp operTime;

	@Column(name = "OPER_USER")
	private String operUser;

	public BioneLogAuthInfo() {
	}

	public BioneLogAuthInfo(String logId, String authObjId, String authObjNo,
			String logicSysNo, Timestamp operTime, String operUser) {
		this.logId = logId;
		this.authObjId = authObjId;
		this.authObjNo = authObjNo;
		this.logicSysNo = logicSysNo;
		this.operTime = operTime;
		this.operUser = operUser;
	}

	public String getLogId() {
		return this.logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getAuthObjId() {
		return this.authObjId;
	}

	public void setAuthObjId(String authObjId) {
		this.authObjId = authObjId;
	}

	public String getAuthObjNo() {
		return this.authObjNo;
	}

	public void setAuthObjNo(String authObjNo) {
		this.authObjNo = authObjNo;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public Timestamp getOperTime() {
		return this.operTime;
	}

	public void setOperTime(Timestamp operTime) {
		this.operTime = operTime;
	}

	public String getOperUser() {
		return this.operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

}