package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_node_ins database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_node_ins")
public class RptTskNodeIns implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TASK_NODE_INSTANCE_ID")
	private String taskNodeInstanceId;

	@Column(name="END_TIME")
	private String endTime;

	@Column(name="IS_CAN_INTERRUPT")
	private String isCanInterrupt;

	@Column(name="START_TIME")
	private String startTime;

	@Column(name="STS")
	private String sts;

	@Column(name="TASK_INSTANCE_ID")
	private String taskInstanceId;

	@Column(name="TASK_NODE_NM")
	private String taskNodeNm;
	
	@Column(name="TASK_ORDERNO")
	private String taskOrderno;

	@Column(name="NODE_TYPE")
	private String nodeType;
	
	@Column(name="TASK_NODE_DEF_ID")
	private String taskNodeDefId;

	private String taskObjType;

	private String taskObjNm;

    public RptTskNodeIns() {
    }

	public String getTaskNodeInstanceId() {
		return this.taskNodeInstanceId;
	}

	public void setTaskNodeInstanceId(String taskNodeInstanceId) {
		this.taskNodeInstanceId = taskNodeInstanceId;
	}

	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getIsCanInterrupt() {
		return this.isCanInterrupt;
	}

	public void setIsCanInterrupt(String isCanInterrupt) {
		this.isCanInterrupt = isCanInterrupt;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getSts() {
		return this.sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	public String getTaskInstanceId() {
		return this.taskInstanceId;
	}

	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getTaskNodeNm() {
		return this.taskNodeNm;
	}

	public void setTaskNodeNm(String taskNodeNm) {
		this.taskNodeNm = taskNodeNm;
	}

	public String getTaskOrderno() {
		return taskOrderno;
	}

	public void setTaskOrderno(String taskOrderno) {
		this.taskOrderno = taskOrderno;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getTaskNodeDefId() {
		return taskNodeDefId;
	}

	public void setTaskNodeDefId(String taskNodeDefId) {
		this.taskNodeDefId = taskNodeDefId;
	}

	public String getTaskObjType() {
		return taskObjType;
	}

	public void setTaskObjType(String taskObjType) {
		this.taskObjType = taskObjType;
	}

	public String getTaskObjNm() {
		return taskObjNm;
	}

	public void setTaskObjNm(String taskObjNm) {
		this.taskObjNm = taskObjNm;
	}
}