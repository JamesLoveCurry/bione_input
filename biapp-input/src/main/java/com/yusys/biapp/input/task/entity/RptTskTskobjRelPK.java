package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the rpt_fltsk_tskobj_rel database table.
 * 
 */
@Embeddable
public class RptTskTskobjRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_OBJ_ID")
	private String taskObjId;

	@Column(name="TASK_ID")
	private String taskId;

    public RptTskTskobjRelPK() {
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptTskTskobjRelPK)) {
			return false;
		}
		RptTskTskobjRelPK castOther = (RptTskTskobjRelPK)other;
		return 
			this.taskObjId.equals(castOther.taskObjId)
			&& this.taskId.equals(castOther.taskId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskObjId.hashCode();
		hash = hash * prime + this.taskId.hashCode();
		
		return hash;
    }
}