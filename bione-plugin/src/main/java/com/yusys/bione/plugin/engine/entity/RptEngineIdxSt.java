package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the RPT_ENGINE_IDX_STS database table.
 * 
 */
@Entity
@Table(name = "RPT_ENGINE_IDX_STS")
public class RptEngineIdxSt implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptEngineIdxStPK id;

	@Column(name = "END_TIME")
	private Timestamp endTime;

	@Column(name = "ERROR_LOG")
	private String errorLog;

	@Column(name = "START_TIME")
	private Timestamp startTime;

	@Column(name = "STS")
	private String sts;

	@Transient
	private String indexNm;

	@Transient
	private String startTimeStr;

	@Transient
	private String endTimeStr;

	public RptEngineIdxSt() {
	}

	public RptEngineIdxStPK getId() {
		return this.id;
	}

	public void setId(RptEngineIdxStPK id) {
		this.id = id;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getErrorLog() {
		return this.errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public String getSts() {
		return this.sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

}