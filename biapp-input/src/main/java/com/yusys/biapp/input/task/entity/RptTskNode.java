package com.yusys.biapp.input.task.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the rpt_fltsk_node database table.
 * 
 */
@Entity
@Table(name = "rpt_tsk_node")
public class RptTskNode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TASK_NODE_ID")
	private String taskNodeId;

	@Column(name = "TASK_ID")
	private String taskId;

	@Column(name = "TASK_NODE_DEF_ID")
	private String taskNodeDefId;

	@Column(name = "TASK_NODE_NM")
	private String taskNodeNm;

	@Column(name = "TASK_ORDERNO")
	private BigDecimal taskOrderno;

	@Column(name = "ORG_NO")
	private String orgNo;
	
	@Column(name = "HANDLE_TYPE")
	private String handleType;

	@Column(name="NODE_TYPE")
	private String nodeType;
	public RptTskNode() {
	}

	public String getTaskNodeId() {
		return this.taskNodeId;
	}

	public void setTaskNodeId(String taskNodeId) {
		this.taskNodeId = taskNodeId;
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskNodeDefId() {
		return this.taskNodeDefId;
	}

	public void setTaskNodeDefId(String taskNodeDefId) {
		this.taskNodeDefId = taskNodeDefId;
	}

	public String getTaskNodeNm() {
		return this.taskNodeNm;
	}

	public void setTaskNodeNm(String taskNodeNm) {
		this.taskNodeNm = taskNodeNm;
	}

	public BigDecimal getTaskOrderno() {
		return this.taskOrderno;
	}

	public void setTaskOrderno(BigDecimal taskOrderno) {
		this.taskOrderno = taskOrderno;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getHandleType() {
		return handleType;
	}

	public void setHandleType(String handleType) {
		this.handleType = handleType;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
}