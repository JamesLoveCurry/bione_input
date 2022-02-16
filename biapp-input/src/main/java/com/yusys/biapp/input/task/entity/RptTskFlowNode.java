package com.yusys.biapp.input.task.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_flow_node")
public class RptTskFlowNode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TASK_NODE_DEF_ID")
	private String taskNodeDefId;

	@Column(name="FLOW_NODE_NM")
	private String flowNodeNm;

	@Column(name="IS_CAN_INTERRUPT")
	private String isCanInterrupt;

	@Column(name="TASK_DEF_ID")
	private String taskDefId;

	@Column(name="TASK_ORDERNO")
	private BigDecimal taskOrderno;
	

	@Column(name="NODE_TYPE")
	private String  nodeType;

    public RptTskFlowNode() {
    }

	public String getTaskNodeDefId() {
		return this.taskNodeDefId;
	}

	public void setTaskNodeDefId(String taskNodeDefId) {
		this.taskNodeDefId = taskNodeDefId;
	}

	public String getFlowNodeNm() {
		return this.flowNodeNm;
	}

	public void setFlowNodeNm(String flowNodeNm) {
		this.flowNodeNm = flowNodeNm;
	}

	public String getIsCanInterrupt() {
		return this.isCanInterrupt;
	}

	public void setIsCanInterrupt(String isCanInterrupt) {
		this.isCanInterrupt = isCanInterrupt;
	}

	public String getTaskDefId() {
		return this.taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

	public BigDecimal getTaskOrderno() {
		return this.taskOrderno;
	}

	public void setTaskOrderno(BigDecimal taskOrderno) {
		this.taskOrderno = taskOrderno;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

}