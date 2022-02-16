package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_LOG_ATTACH_REL database table.
 * 
 */
@Entity
@Table(name="BIONE_LOG_ATTACH_REL")
@NamedQuery(name="BioneLogAttachRel.findAll", query="SELECT b FROM BioneLogAttachRel b")
public class BioneLogAttachRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneLogAttachRelPK id;

	public BioneLogAttachRel() {
	}

	public BioneLogAttachRelPK getId() {
		return this.id;
	}

	public void setId(BioneLogAttachRelPK id) {
		this.id = id;
	}

}