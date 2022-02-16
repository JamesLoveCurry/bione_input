package com.yusys.bione.plugin.access.web.vo;


public class IdxUserInfoVO {
	private String userId;
	private String userNo;
	private String userName;
	private String accessTime;
	private String accessIp;
	private String indexNm;
	private String indexNo;
	private String indexVerId;
	
	public String getIndexNm() {
		return indexNm;
	}
	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}
	public String getIndexNo() {
		return indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public String getIndexVerId() {
		return indexVerId;
	}
	public void setIndexVerId(String indexVerId) {
		this.indexVerId = indexVerId;
	}
	public String getAccessIp() {
		return accessIp;
	}
	public void setAccessIp(String accessIp) {
		this.accessIp = accessIp;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getAccessTime() {
		return accessTime;
	}
	public void setAccessTime(String accessTime) {
		this.accessTime = accessTime;
	}
	
}
