package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_TASK_INSTANCE_INFO database table.
 * 
 */
@Embeddable
public class RptTaskInstanceInfoPK implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "TASK_NO")
	private String taskNo;

	@Column(name = "DATA_DATE")
	private String dataDate;

	@Column(name = "INSTANCE_ID")
	private String instanceId;

	public RptTaskInstanceInfoPK() {
	}

	public String getTaskNo() {
		return this.taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public String getDataDate() {
		return this.dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getInstanceId() {
		return this.instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataDate == null) ? 0 : dataDate.hashCode());
		result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
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
		RptTaskInstanceInfoPK other = (RptTaskInstanceInfoPK) obj;
		if (dataDate == null) {
			if (other.dataDate != null)
				return false;
		} else if (!dataDate.equals(other.dataDate))
			return false;
		if (instanceId == null) {
			if (other.instanceId != null)
				return false;
		} else if (!instanceId.equals(other.instanceId))
			return false;
		if (taskNo == null) {
			if (other.taskNo != null)
				return false;
		} else if (!taskNo.equals(other.taskNo))
			return false;
		return true;
	}

}