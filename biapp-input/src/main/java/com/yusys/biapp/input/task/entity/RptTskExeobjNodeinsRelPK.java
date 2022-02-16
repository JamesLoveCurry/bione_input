package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the rpt_fltsk_exeobj_nodeins_rel database table.
 * 
 */
@Embeddable
public class RptTskExeobjNodeinsRelPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2418870144001946600L;

	//default serial version id, required for serializable classes.
	
	@Column(name="EXE_OBJ_ID")
	private String exeObjId;

	@Column(name="TASK_NODE_INSTANCE_ID")
	private String taskNodeInstanceId;

    public RptTskExeobjNodeinsRelPK() {
    }
	
	public String getExeObjId() {
		return exeObjId;
	}

	public void setExeObjId(String exeObjId) {
		this.exeObjId = exeObjId;
	}

	public String getTaskNodeInstanceId() {
		return this.taskNodeInstanceId;
	}
	public void setTaskNodeInstanceId(String taskNodeInstanceId) {
		this.taskNodeInstanceId = taskNodeInstanceId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptTskExeobjNodeinsRelPK)) {
			return false;
		}
		RptTskExeobjNodeinsRelPK castOther = (RptTskExeobjNodeinsRelPK)other;
		return 
			this.exeObjId.equals(castOther.exeObjId)
			&& this.taskNodeInstanceId.equals(castOther.taskNodeInstanceId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.exeObjId.hashCode();
		hash = hash * prime + this.taskNodeInstanceId.hashCode();
		
		return hash;
    }
}