package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_ENGINE_AUTO_TASK_INFO database table.
 * 
 */
@Embeddable
public class RptEngineAutoTaskInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DATA_DATE")
	private String dataDate;

	@Column(name="TASK_ID")
	private String taskId;

	public RptEngineAutoTaskInfoPK() {
	}
	public String getDataDate() {
		return this.dataDate;
	}
	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
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
		if (!(other instanceof RptEngineAutoTaskInfoPK)) {
			return false;
		}
		RptEngineAutoTaskInfoPK castOther = (RptEngineAutoTaskInfoPK)other;
		return 
			this.dataDate.equals(castOther.dataDate)
			&& this.taskId.equals(castOther.taskId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dataDate.hashCode();
		hash = hash * prime + this.taskId.hashCode();
		
		return hash;
	}
}