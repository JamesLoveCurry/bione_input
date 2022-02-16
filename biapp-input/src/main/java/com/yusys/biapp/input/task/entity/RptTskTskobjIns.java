package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_tskobj_ins database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_tskobj_ins")
public class RptTskTskobjIns implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TASK_OBJ_ID")
	private String taskObjId;

	@Column(name="TASK_OBJ_NM")
	private String taskObjNm;

	@Column(name="TASK_OBJ_TYPE")
	private String taskObjType;

    public RptTskTskobjIns() {
    }

	public String getTaskObjId() {
		return this.taskObjId;
	}

	public void setTaskObjId(String taskObjId) {
		this.taskObjId = taskObjId;
	}

	public String getTaskObjNm() {
		return this.taskObjNm;
	}

	public void setTaskObjNm(String taskObjNm) {
		this.taskObjNm = taskObjNm;
	}

	public String getTaskObjType() {
		return this.taskObjType;
	}

	public void setTaskObjType(String taskObjType) {
		this.taskObjType = taskObjType;
	}

}