package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_exeobj_rel database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_exeobj_rel")
public class RptTskExeobjRel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1418761102318000347L;
	@EmbeddedId
	private RptTskExeobjRelPK id;
	
	@Column(name="EXE_OBJ_NM")
	private String exeObjNm;

    public RptTskExeobjRel() {
    }

	public RptTskExeobjRelPK getId() {
		return this.id;
	}

	public void setId(RptTskExeobjRelPK id) {
		this.id = id;
	}

	public String getExeObjNm() {
		return exeObjNm;
	}

	public void setExeObjNm(String exeObjNm) {
		this.exeObjNm = exeObjNm;
	}
	
}