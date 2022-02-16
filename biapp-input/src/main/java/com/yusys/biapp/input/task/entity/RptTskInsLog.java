package com.yusys.biapp.input.task.entity;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_TSK_INS_LOG database table.
 * 
 */
@Entity
@Table(name="RPT_TSK_INS_LOG")
public class RptTskInsLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="LOG_ID")
	private String logId;

	@Column(name="OPER_TIME")
	private Timestamp operTime;

	@Column(name="OPER_TYPE")
	private String operType;

	@Column(name="OPER_USER")
	private String operUser;

	@Column(name="REMARK")
	private String remark;

	@Column(name="TASK_INSTANCE_ID")
	private String taskInstanceId;

	@Column(name="TASK_NODE_INSTANCE_ID")
	private String taskNodeInstanceId;

    public RptTskInsLog() {
    }

	public String getLogId() {
		return this.logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public Timestamp getOperTime() {
		return this.operTime;
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

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTaskInstanceId() {
		return this.taskInstanceId;
	}

	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getTaskNodeInstanceId() {
		return this.taskNodeInstanceId;
	}

	public void setTaskNodeInstanceId(String taskNodeInstanceId) {
		this.taskNodeInstanceId = taskNodeInstanceId;
	}

}