package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ENGINE_CHECK_STS database table.
 * 
 */
@Entity
@Table(name="RPT_ENGINE_CHECK_STS")
public class RptEngineCheckSts implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptEngineCheckStsPK id;

	@Column(name="TASK_ID", length=32)
	private String taskId;
	
	@Column(name="TASK_TYPE", length=10)
	private String taskType;
	
	@Column(name="CHECK_STS", length=10)
	private String checkSts;
	
	@Column(name="START_TIME")
	private Timestamp startTime;
	
	@Column(name="END_TIME")
	private Timestamp endTime;
	
	public RptEngineCheckStsPK getId() {
		return id;
	}

	public void setId(RptEngineCheckStsPK id) {
		this.id = id;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the taskType
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	public String getCheckSts() {
		return checkSts;
	}

	public void setCheckSts(String checkSts) {
		this.checkSts = checkSts;
	}
	//非标准的hashCode和equals方法，判断实体相等的方法为id相等，不包含checkSts
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result
//				+ ((checkSts == null) ? 0 : checkSts.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		RptEngineCheckSts other = (RptEngineCheckSts) obj;
//		if (checkSts == null) {
//			if (other.checkSts != null)
//				return false;
//		} else if (!checkSts.equals(other.checkSts))
//			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	
}