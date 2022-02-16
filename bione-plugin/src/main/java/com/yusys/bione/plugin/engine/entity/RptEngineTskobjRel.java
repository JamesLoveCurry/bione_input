package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="RPT_ENGINE_TSKOBJ_REL")
public class RptEngineTskobjRel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@EmbeddedId
	private RptEngineTskobjRelPK id;
	

	public RptEngineTskobjRelPK getId() {
		return id;
	}

	public void setId(RptEngineTskobjRelPK id) {
		this.id = id;
	}

	
}
