package com.yusys.bione.plugin.draw.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DRAW_LOG_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_DRAW_LOG_INFO")
@NamedQuery(name="RptDrawLogInfo.findAll", query="SELECT p FROM RptDrawLogInfo p")
public class RptDrawLogInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="LOG_ID")
	private String logId;

	@Column(name="DRAW_DATE")
	private String drawDate;

	@Column(name="DRAW_OBJ")
	private String drawObj;

	@Column(name="DRAW_TIME")
	private Timestamp drawTime;

	@Column(name="OBJ_ID")
	private String objId;

	@Column(name="USER_ID")
	private String userId;

	public RptDrawLogInfo() {
	}

	public String getLogId() {
		return this.logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getDrawDate() {
		return this.drawDate;
	}

	public void setDrawDate(String drawDate) {
		this.drawDate = drawDate;
	}

	public String getDrawObj() {
		return this.drawObj;
	}

	public void setDrawObj(String drawObj) {
		this.drawObj = drawObj;
	}

	public Timestamp getDrawTime() {
		return this.drawTime;
	}

	public void setDrawTime(Timestamp drawTime) {
		this.drawTime = drawTime;
	}

	public String getObjId() {
		return this.objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}