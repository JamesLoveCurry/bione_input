package com.yusys.bione.frame.authobj.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_ORG_INFO_REL database table.
 * 
 */
@Entity
@Table(name="BIONE_ORG_INFO_REL")
@NamedQuery(name="BioneOrgInfoRel.findAll", query="SELECT b FROM BioneOrgInfoRel b")
public class BioneOrgInfoRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneOrgInfoRelPK id;

	public BioneOrgInfoRel() {
	}

	public BioneOrgInfoRelPK getId() {
		return this.id;
	}

	public void setId(BioneOrgInfoRelPK id) {
		this.id = id;
	}

}