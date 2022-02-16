package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_flow database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_flow")
public class RptTskFlow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TASK_DEF_ID")
	private String taskDefId;

	@Column(name="FLOW_NM")
	private String flowNm;

    public RptTskFlow() {
    }

	public String getTaskDefId() {
		return this.taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

	public String getFlowNm() {
		return this.flowNm;
	}

	public void setFlowNm(String flowNm) {
		this.flowNm = flowNm;
	}

}