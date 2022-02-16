package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_tskobj_rel database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_tskobj_rel")
public class RptTskTskobjRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptTskTskobjRelPK id;

	@Column(name="TASK_OBJ_TYPE")
	private String taskObjType;

    public RptTskTskobjRel() {
    }

	public RptTskTskobjRelPK getId() {
		return this.id;
	}

	public void setId(RptTskTskobjRelPK id) {
		this.id = id;
	}
	
	public String getTaskObjType() {
		return this.taskObjType;
	}

	public void setTaskObjType(String taskObjType) {
		this.taskObjType = taskObjType;
	}

}