package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the rpt_fltsk_node_exeobj_rel database table.
 * 
 */
@Embeddable
public class RptTskNodeExeobjRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_NODE_ID")
	private String taskNodeId;

	@Column(name="TASK_ID")
	private String taskId;

	@Column(name="EXE_OBJ_ID")
	private String exeObjId;

    public RptTskNodeExeobjRelPK() {
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
	public String getExeObjId() {
		return this.exeObjId;
	}
	public void setExeObjId(String exeObjId) {
		this.exeObjId = exeObjId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptTskNodeExeobjRelPK)) {
			return false;
		}
		RptTskNodeExeobjRelPK castOther = (RptTskNodeExeobjRelPK)other;
		return 
			this.taskNodeId.equals(castOther.taskNodeId)
			&& this.taskId.equals(castOther.taskId)
			&& this.exeObjId.equals(castOther.exeObjId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskNodeId.hashCode();
		hash = hash * prime + this.taskId.hashCode();
		hash = hash * prime + this.exeObjId.hashCode();
		
		return hash;
    }
}