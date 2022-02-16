package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_exeobj_nodeins_rel database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_exeobj_nodeins_rel")
public class RptTskExeobjNodeinsRel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8716204588680809560L;
	@EmbeddedId
	private RptTskExeobjNodeinsRelPK id;

    public RptTskExeobjNodeinsRel() {
    }

	public RptTskExeobjNodeinsRelPK getId() {
		return this.id;
	}

	public void setId(RptTskExeobjNodeinsRelPK id) {
		this.id = id;
	}
	
}