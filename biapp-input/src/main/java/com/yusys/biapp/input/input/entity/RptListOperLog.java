package com.yusys.biapp.input.input.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the RPT_LIST_OPER_LOG database table.
 * 
 */
@Entity
@Table(name = "RPT_LIST_OPER_LOG")
public class RptListOperLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptListOperLogPK id;

	@Column(name = "NODE_ID")
	private String nodeId;

	@Column(name = "OPER_CONTENT")
	private String operContent;

	@Column(name = "OPER_TIME")
	private Timestamp operTime;

	@Column(name = "OPER_TYPE")
	private String operType;

	@Column(name = "OPER_USER")
	private String operUser;

	@Column(name = "TASK_ID")
	private String taskId;

	@Column(name = "TEMPLE_ID")
	private String templeId;
	
	@Transient
	private String showDate;

	public RptListOperLog() {
	}

	public RptListOperLogPK getId() {
		return this.id;
	}

	public void setId(RptListOperLogPK id) {
		this.id = id;
	}

	public String getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getOperContent() {
		return this.operContent;
	}

	public void setOperContent(String operContent) {
		this.operContent = operContent;
	}

	public Timestamp getOperTime() {
		return operTime;
	}

	public void setOperTime(Timestamp operTime) {
		this.operTime = operTime;
	}

	public String getOperType() {
		return this.operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getOperUser() {
		return this.operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTempleId() {
		return this.templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

	public String getShowDate() {
		return showDate;
	}

	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}

}