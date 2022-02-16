package com.yusys.bione.frame.syslog.web.vo;

import com.yusys.bione.frame.syslog.entity.BioneLogAuthInfo;

/**
 * The persistent class for the BIONE_LOG_AUTH_INFO database table.
 * 
 */
public class LogAuthInfoVO extends BioneLogAuthInfo{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userNo;
	private String userName;
	private String authObjName;
	private String objDefName;
	

	public LogAuthInfoVO(BioneLogAuthInfo info, String userNo, String userName, String objDefName, String authObjName){
		this.setLogId(info.getLogId());
		this.setAuthObjId(info.getAuthObjId());
		this.setAuthObjNo(info.getAuthObjNo());
		this.setLogicSysNo(info.getLogicSysNo());
		this.setOperTime(info.getOperTime());
		this.setOperUser(info.getOperUser());
		this.userNo = userNo;
		this.userName = userName;
		this.objDefName = objDefName;
		this.authObjName = authObjName;
	}
	
	public String getObjDefName() {
		return objDefName;
	}

	public void setObjDefName(String objDefName) {
		this.objDefName = objDefName;
	}
	
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAuthObjName() {
		return authObjName;
	}
	public void setAuthObjName(String authObjName) {
		this.authObjName = authObjName;
	}
	
}