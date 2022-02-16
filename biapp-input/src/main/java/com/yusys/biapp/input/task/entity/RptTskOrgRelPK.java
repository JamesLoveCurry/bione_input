package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the rpt_fltsk_node_tskobj_rel database table.
 * 
 */
@Embeddable
public class RptTskOrgRelPK implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7606245147180469406L;
	@Column(name="TASK_ID")
	private String taskId;
	@Column(name="ORG_NO")
	private String orgNo;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orgNo == null) ? 0 : orgNo.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
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
		RptTskOrgRelPK other = (RptTskOrgRelPK) obj;
		if (orgNo == null) {
			if (other.orgNo != null)
				return false;
		} else if (!orgNo.equals(other.orgNo))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}
	
	
}