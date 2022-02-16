package com.yusys.bione.plugin.mainpage.web.vo;

import java.sql.Timestamp;

public class ReportHistoryVO {
	private String hisId;
	private String rptNum;
	private String rptId;
	private String rptNm;
	private String accessDate;
	private Timestamp accessTime;
	private String accessId;
	private String userId;
	
	
	public String getRptNum() {
		return rptNum;
	}
	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}
	public String getHisId() {
		return hisId;
	}
	public void setHisId(String hisId) {
		this.hisId = hisId;
	}
	public String getRptId() {
		return rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}
	public String getRptNm() {
		return rptNm;
	}
	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}
	public String getAccessDate() {
		return accessDate;
	}
	public void setAccessDate(String accessDate) {
		this.accessDate = accessDate;
	}
	public Timestamp getAccessTime() {
		return accessTime;
	}
	public void setAccessTime(Timestamp accessTime) {
		this.accessTime = accessTime;
	}
	public String getAccessId() {
		return accessId;
	}
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
