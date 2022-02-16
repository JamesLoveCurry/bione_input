package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_node_exeobj_rel database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_node_exeobj_rel")
public class RptTskNodeExeobjRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptTskNodeExeobjRelPK id;

    public RptTskNodeExeobjRel() {
    }

	public RptTskNodeExeobjRelPK getId() {
		return this.id;
	}

	public void setId(RptTskNodeExeobjRelPK id) {
		this.id = id;
	}
	
}