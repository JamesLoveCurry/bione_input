package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
public class RptEngineTskobjRelPK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_NO")
	private String taskNo;
	
	@Column(name="OBJ_NO")
	private String objNo;
	
	@Column(name="OBJ_TYPE")
	private String objType;

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public String getObjNo() {
		return objNo;
	}

	public void setObjNo(String objNo) {
		this.objNo = objNo;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objNo == null) ? 0 : objNo.hashCode());
		result = prime * result + ((objType == null) ? 0 : objType.hashCode());
		result = prime * result + ((taskNo == null) ? 0 : taskNo.hashCode());
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
		RptEngineTskobjRelPK other = (RptEngineTskobjRelPK) obj;
		if (objNo == null) {
			if (other.objNo != null)
				return false;
		} else if (!objNo.equals(other.objNo))
			return false;
		if (objType == null) {
			if (other.objType != null)
				return false;
		} else if (!objType.equals(other.objType))
			return false;
		if (taskNo == null) {
			if (other.taskNo != null)
				return false;
		} else if (!taskNo.equals(other.taskNo))
			return false;
		return true;
	}
	
	
	
}
