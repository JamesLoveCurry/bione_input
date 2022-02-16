package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the rpt_fltsk_node_tskobj_rel database table.
 * 
 */
@Embeddable
public class RptTskNodeTskobjRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_OBJ_ID")
	private String taskObjId;

	@Column(name="TASK_ID")
	private String taskId;

	@Column(name="TASK_NODE_ID")
	private String taskNodeId;

    public RptTskNodeTskobjRelPK() {
    }
	public String getTaskObjId() {
		return this.taskObjId;
	}
	public void setTaskObjId(String taskObjId) {
		this.taskObjId = taskObjId;
	}
	public String getTaskId() {
		return this.taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskNodeId() {
		return this.taskNodeId;
	}
	public void setTaskNodeId(String taskNodeId) {
		this.taskNodeId = taskNodeId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptTskNodeTskobjRelPK)) {
			return false;
		}
		RptTskNodeTskobjRelPK castOther = (RptTskNodeTskobjRelPK)other;
		return 
			this.taskObjId.equals(castOther.taskObjId)
			&& this.taskId.equals(castOther.taskId)
			&& this.taskNodeId.equals(castOther.taskNodeId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskObjId.hashCode();
		hash = hash * prime + this.taskId.hashCode();
		hash = hash * prime + this.taskNodeId.hashCode();
		
		return hash;
    }
}