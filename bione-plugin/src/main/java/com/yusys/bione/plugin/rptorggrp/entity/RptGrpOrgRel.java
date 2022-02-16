package com.yusys.bione.plugin.rptorggrp.entity;


import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_grp_org_rel database table.
 * 
 */
@Entity
@Table(name="rpt_grp_org_rel")
public class RptGrpOrgRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptGrpOrgRelPK id;

    public RptGrpOrgRel() {
    }

	public RptGrpOrgRelPK getId() {
		return this.id;
	}

	public void setId(RptGrpOrgRelPK id) {
		this.id = id;
	}
	
}