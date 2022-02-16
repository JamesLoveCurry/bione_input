package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_AUTH_USER_REL database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_AUTH_USER_REL")
public class RptMgrAuthUserRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrAuthUserRelPK id;

    public RptMgrAuthUserRel() {
    }

	public RptMgrAuthUserRelPK getId() {
		return this.id;
	}

	public void setId(RptMgrAuthUserRelPK id) {
		this.id = id;
	}
	
}