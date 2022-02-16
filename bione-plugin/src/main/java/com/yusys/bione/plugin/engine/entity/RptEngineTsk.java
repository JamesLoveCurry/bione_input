package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ENGINE_TSK database table.
 * 
 */
@Entity
@Table(name="RPT_ENGINE_TSK")
@NamedQuery(name="RptEngineTsk.findAll", query="SELECT r FROM RptEngineTsk r")
public class RptEngineTsk  implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TASK_NO")
	private String taskNo;

	@Column(name="TASK_NM")
	private String taskNm;

	@Column(name="OBJ_TYPE")
	private String objType;
	
	public RptEngineTsk() {
	}

	public String getTaskNo() {
		return this.taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public String getTaskNm() {
		return this.taskNm;
	}

	public void setTaskNm(String taskNm) {
		this.taskNm = taskNm;
	}
	
	public String getObjType() {
		return this.objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

}