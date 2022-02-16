package com.yusys.bione.frame.syslog.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the BIONE_LOG_FUNC database table.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "BIONE_LOG_FUNC")
public class BioneLogFunc implements java.io.Serializable {

	// Fields
	@Id
	@Column(name = "LOG_ID")
	private String logId;
	
	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "OCCUR_TIME")
	private Timestamp occurTime;
	
	@Column(name = "OCCUR_DATE")
	private String occurDate;
	
	@Column(name = "MENU_ID")
	private String menuId;
	
	@Column(name = "LOGIN_IP")
	private String loginIp;
	
	@Column(name = "LOG_TYPE")
	private String logType;

	// Constructors

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	/** default constructor */
	public BioneLogFunc() {
	}

	/** minimal constructor */
	public BioneLogFunc(String logId) {
		this.logId = logId;
	}

	/** full constructor */
	public BioneLogFunc(String logId, String userId, Timestamp occurTime,
			String occurDate, String menuId) {
		this.logId = logId;
		this.userId = userId;
		this.occurTime = occurTime;
		this.occurDate = occurDate;
		this.menuId = menuId;
	}

	// Property accessors
	public String getLogId() {
		return this.logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Timestamp getOccurTime() {
		return this.occurTime;
	}

	public void setOccurTime(Timestamp occurTime) {
		this.occurTime = occurTime;
	}

	public String getOccurDate() {
		return this.occurDate;
	}

	public void setOccurDate(String occurDate) {
		this.occurDate = occurDate;
	}

	public String getMenuId() {
		return this.menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

}