package com.yusys.bione.frame.logicsys.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_VALID_TYPE_SYS_REL database table.
 * 
 */
@Entity
@Table(name="BIONE_VALID_TYPE_SYS_REL")
@NamedQuery(name="BioneValidTypeSysRel.findAll", query="SELECT b FROM BioneValidTypeSysRel b")
public class BioneValidTypeSysRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneValidTypeSysRelPK id;

	public BioneValidTypeSysRel() {
	}

	public BioneValidTypeSysRelPK getId() {
		return this.id;
	}

	public void setId(BioneValidTypeSysRelPK id) {
		this.id = id;
	}

}