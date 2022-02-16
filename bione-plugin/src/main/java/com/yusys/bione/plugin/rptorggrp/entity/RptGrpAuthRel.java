package com.yusys.bione.plugin.rptorggrp.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_GRP_AUTH_REL database table.
 * 
 */
@Entity
@Table(name="RPT_GRP_AUTH_REL")
@NamedQuery(name="RptGrpAuthRel.findAll", query="SELECT r FROM RptGrpAuthRel r")
public class RptGrpAuthRel  implements Serializable{
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptGrpAuthRelPK id;

	public RptGrpAuthRel() {
	}

	public RptGrpAuthRelPK getId() {
		return this.id;
	}

	public void setId(RptGrpAuthRelPK id) {
		this.id = id;
	}

}