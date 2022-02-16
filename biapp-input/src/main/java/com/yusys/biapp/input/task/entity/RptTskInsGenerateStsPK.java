package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the rpt_fltsk_ins_generate_sts database table.
 * 
 */
@Embeddable
public class RptTskInsGenerateStsPK implements Serializable{
	private static final long serialVersionUID = 1L;

	@Column(name="DATA_DATE")
	private String dataDate;

	@Column(name="TASK_ID")
	private String taskId;
	
	public RptTskInsGenerateStsPK(){
		
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataDate == null) ? 0 : dataDate.hashCode());
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
		RptTskInsGenerateStsPK other = (RptTskInsGenerateStsPK) obj;
		if (dataDate == null) {
			if (other.dataDate != null)
				return false;
		} else if (!dataDate.equals(other.dataDate))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}
	
	
}
