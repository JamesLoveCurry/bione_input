package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_node_tskobj_rel database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_node_tskobj_rel")
public class RptTskNodeTskobjRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptTskNodeTskobjRelPK id;
	
	@Column(name="TASK_OBJ_NM")
	private String  taskObjNm;
	
	@Column(name="TASK_OBJ_TYPE")
	private  String taskObjType;

    public RptTskNodeTskobjRel() {
    }

	public RptTskNodeTskobjRelPK getId() {
		return this.id;
	}

	public void setId(RptTskNodeTskobjRelPK id) {
		this.id = id;
	}

	public String getTaskObjNm() {
		return taskObjNm;
	}

	public void setTaskObjNm(String taskObjNm) {
		this.taskObjNm = taskObjNm;
	}

	public String getTaskObjType() {
		return taskObjType;
	}

	public void setTaskObjType(String taskObjType) {
		this.taskObjType = taskObjType;
	}

	
}