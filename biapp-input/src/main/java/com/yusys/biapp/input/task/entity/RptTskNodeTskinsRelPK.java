package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RptTskNodeTskinsRelPK  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8741031597284542635L;

	@Column(name="TASK_OBJ_ID")
	private String taskObjId;
	
	@Column(name="TASK_OBJ_TYPE")
	private String taskObjType;
	
	@Column(name="TASK_NODE_INSTANCE_ID")
	private String taskNodeInstanceId;

	public String getTaskObjId() {
		return taskObjId;
	}

	public void setTaskObjId(String taskObjId) {
		this.taskObjId = taskObjId;
	}

	public String getTaskObjType() {
		return taskObjType;
	}

	public void setTaskObjType(String taskObjType) {
		this.taskObjType = taskObjType;
	}

	public String getTaskNodeInstanceId() {
		return taskNodeInstanceId;
	}

	public void setTaskNodeInstanceId(String taskNodeInstanceId) {
		this.taskNodeInstanceId = taskNodeInstanceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((taskNodeInstanceId == null) ? 0 : taskNodeInstanceId
						.hashCode());
		result = prime * result
				+ ((taskObjId == null) ? 0 : taskObjId.hashCode());
		result = prime * result
				+ ((taskObjType == null) ? 0 : taskObjType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptTskNodeTskinsRelPK other = (RptTskNodeTskinsRelPK) obj;
		if (taskNodeInstanceId == null) {
			if (other.taskNodeInstanceId != null)
				return false;
		} else if (!taskNodeInstanceId.equals(other.taskNodeInstanceId))
			return false;
		if (taskObjId == null) {
			if (other.taskObjId != null)
				return false;
		} else if (!taskObjId.equals(other.taskObjId))
			return false;
		if (taskObjType == null) {
			if (other.taskObjType != null)
				return false;
		} else if (!taskObjType.equals(other.taskObjType))
			return false;
		return true;
	}
	
	
	
}
