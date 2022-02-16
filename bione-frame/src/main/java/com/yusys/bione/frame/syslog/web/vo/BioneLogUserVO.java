package com.yusys.bione.frame.syslog.web.vo;


public class BioneLogUserVO {
	private String userId;
	private Long loginTime ;
	private Long onlineTime;
	private String loginIp;
	public String getUserId() {
		return userId;
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
	public Long getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(Long onlineTime) {
		this.onlineTime = onlineTime;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	
	
}