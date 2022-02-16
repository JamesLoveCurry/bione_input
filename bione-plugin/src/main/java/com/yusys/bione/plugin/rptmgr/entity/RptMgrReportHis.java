package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_ADAPTER database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_REPORT_HIS")
public class RptMgrReportHis implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="HIS_ID")
	private String hisId;

	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="ACCESS_DATE")
	private String accessDate;

	@Column(name="ACCESS_TIME")
	private Timestamp accessTime;

	@Column(name="ACCESS_IP")
	private String accessIp;
	
	@Column(name="USER_ID")
	private String userId;

	public RptMgrReportHis(){
		
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


}