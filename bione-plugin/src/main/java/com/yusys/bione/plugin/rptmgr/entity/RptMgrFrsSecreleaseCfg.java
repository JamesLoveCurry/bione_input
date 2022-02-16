package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_FRS_SECRELEASE_CFG database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_FRS_SECRELEASE_CFG")
public class RptMgrFrsSecreleaseCfg implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrFrsSecreleaseCfgPK id;

    public RptMgrFrsSecreleaseCfg() {
    }

	public RptMgrFrsSecreleaseCfgPK getId() {
		return this.id;
	}

	public void setId(RptMgrFrsSecreleaseCfgPK id) {
		this.id = id;
	}
	
}