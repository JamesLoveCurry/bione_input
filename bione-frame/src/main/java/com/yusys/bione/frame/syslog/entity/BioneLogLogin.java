package com.yusys.bione.frame.syslog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the BIONE_LOG_INFO database table.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "BIONE_LOG_LOGIN")
public class BioneLogLogin implements java.io.Serializable {

	// Fields
	@Id
	@Column(name = "LOG_ID")
	private String logId;
	
	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "LOGIN_TIME")
	private Long loginTime;
	
	@Column(name = "LOGOUT_TIME")
	private Long logoutTime;
	
	@Column(name = "LOGIN_DATE")
	private String loginDate;

	@Column(name = "LOGIN_IP")
	private String loginIp;
	
	@Column(name = "SESSION_ID")
	private String sessionId;
	// Constructors

	/** default constructor */
	public BioneLogLogin() {
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/** minimal constructor */
	public BioneLogLogin(String logId) {
		this.logId = logId;
	}

	/** full constructor */
	public BioneLogLogin(String logId, String userId, Long loginTime,
			Long logoutTime, String loginDate) {
		this.logId = logId;
		this.userId = userId;
		this.loginTime = loginTime;
		this.logoutTime = logoutTime;
		this.loginDate = loginDate;
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


	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}

	public Long getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Long logoutTime) {
		this.logoutTime = logoutTime;
	}

	public String getLoginDate() {
		return this.loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

}