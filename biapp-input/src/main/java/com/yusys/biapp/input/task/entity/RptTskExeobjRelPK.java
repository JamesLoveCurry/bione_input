package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the rpt_fltsk_exeobj_rel database table.
 * 
 */
@Embeddable
public class RptTskExeobjRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_ID")
	private String taskId;

	@Column(name="EXE_OBJ_TYPE")
	private String exeObjType;

	@Column(name="EXE_OBJ_ID")
	private String exeObjId;

    public RptTskExeobjRelPK() {
    }
	public String getTaskId() {
		return this.taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getExeObjType() {
		return this.exeObjType;
	}
	public void setExeObjType(String exeObjType) {
		this.exeObjType = exeObjType;
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
		if (!(other instanceof RptTskExeobjRelPK)) {
			return false;
		}
		RptTskExeobjRelPK castOther = (RptTskExeobjRelPK)other;
		return 
			this.taskId.equals(castOther.taskId)
			&& this.exeObjType.equals(castOther.exeObjType)
			&& this.exeObjId.equals(castOther.exeObjId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskId.hashCode();
		hash = hash * prime + this.exeObjType.hashCode();
		hash = hash * prime + this.exeObjId.hashCode();
		
		return hash;
    }
}