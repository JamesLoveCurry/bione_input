package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_exeobj_ins database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_exeobj_ins")
public class RptTskExeobjIns implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8057035114996318746L;

	@Id
	@Column(name="EXE_OBJ_ID")
	private String exeObjId;

	@Column(name="EXE_OBJ_NM")
	private String exeObjNm;

	@Column(name="EXE_OBJ_TYPE")
	private String exeObjType;

    public RptTskExeobjIns() {
    }

	public String getExeObjId() {
		return this.exeObjId;
	}

	public void setExeObjId(String exeObjId) {
		this.exeObjId = exeObjId;
	}

	public String getExeObjNm() {
		return this.exeObjNm;
	}

	public void setExeObjNm(String exeObjNm) {
		this.exeObjNm = exeObjNm;
	}

	public String getExeObjType() {
		return this.exeObjType;
	}

	public void setExeObjType(String exeObjType) {
		this.exeObjType = exeObjType;
	}

}