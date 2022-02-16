package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="RPT_TSK_ORG_REL")
public class RptTskOrgRel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7752926560611854081L;
	
	@EmbeddedId
	private RptTskOrgRelPK id;

	public RptTskOrgRelPK getId() {
		return id;
	}

	public void setId(RptTskOrgRelPK id) {
		this.id = id;
	}
	
	
	
}
