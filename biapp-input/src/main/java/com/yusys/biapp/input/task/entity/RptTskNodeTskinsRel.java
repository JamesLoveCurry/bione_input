package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="RPT_TSK_NODE_TSKINS_REL")
public class RptTskNodeTskinsRel  implements Serializable{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4034637858782886996L;

	@EmbeddedId
	private RptTskNodeTskinsRelPK id;

	@Column(name="TASK_OBJ_NM")
	private String taskObjNm;

	@Column(name="ORG_NO")
	private String orgNo;

	public RptTskNodeTskinsRelPK getId() {
		return id;
	}

	public void setId(RptTskNodeTskinsRelPK id) {
		this.id = id;
	}

	public String getTaskObjNm() {
		return taskObjNm;
	}

	public void setTaskObjNm(String taskObjNm) {
		this.taskObjNm = taskObjNm;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	
	
	
}
