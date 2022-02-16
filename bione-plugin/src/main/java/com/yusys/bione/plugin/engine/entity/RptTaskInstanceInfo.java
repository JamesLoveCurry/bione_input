package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the RPT_TASK_INSTANCE_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_TASK_INSTANCE_INFO")
public class RptTaskInstanceInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptTaskInstanceInfoPK id;

	@Column(name = "END_TIME")
	private Timestamp endTime;

	@Column(name = "EXEC_NODE")
	private String execNode;

	private String org;

	@Column(name = "PARENT_TASK_ID")
	private String parentTaskId;

	@Column(name = "RUN_LOG")
	private String runLog;

	@Column(name = "START_TIME")
	private Timestamp startTime;

	private String sts;

	@Column(name = "TASK_NM")
	private String taskNm;

	@Column(name = "TASK_TYPE")
	private String taskType;
	
	@Transient
	private String indexType;

	public RptTaskInstanceInfo() {
	}

	public RptTaskInstanceInfoPK getId() {
		return this.id;
	}

	public void setId(RptTaskInstanceInfoPK id) {
		this.id = id;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getExecNode() {
		return this.execNode;
	}

	public void setExecNode(String execNode) {
		this.execNode = execNode;
	}

	public String getOrg() {
		return this.org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getParentTaskId() {
		return this.parentTaskId;
	}

	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}

	public String getRunLog() {
		return this.runLog;
	}

	public void setRunLog(String runLog) {
		this.runLog = runLog;
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

	public String getTaskNm() {
		return this.taskNm;
	}

	public void setTaskNm(String taskNm) {
		this.taskNm = taskNm;
	}

	public String getTaskType() {
		return this.taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

}