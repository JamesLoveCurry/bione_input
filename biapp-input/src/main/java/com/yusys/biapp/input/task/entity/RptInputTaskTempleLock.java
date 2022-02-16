package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_TASK_TEMPLE_LOCK database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_TASK_TEMPLE_LOCK")
public class RptInputTaskTempleLock implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=32)
	private String id;

	@Column(name="LOCK_DATE", length=20)
	private String lockDate;

	@Column(name="TASK_CASE_ID", nullable=false, length=32)
	private String taskCaseId;

	@Column(name="TEMPLATE_ID", length=32)
	private String templateId;

    public RptInputTaskTempleLock() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLockDate() {
		return this.lockDate;
	}

	public void setLockDate(String lockDate) {
		this.lockDate = lockDate;
	}

	public String getTaskCaseId() {
		return this.taskCaseId;
	}

	public void setTaskCaseId(String taskCaseId) {
		this.taskCaseId = taskCaseId;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

}