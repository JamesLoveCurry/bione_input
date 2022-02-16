package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_RPTDIM_REL database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_RPTDIM_REL")
public class RptMgrRptdimRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrRptdimRelPK id;

    public RptMgrRptdimRel() {
    }

	public RptMgrRptdimRelPK getId() {
		return this.id;
	}

	public void setId(RptMgrRptdimRelPK id) {
		this.id = id;
	}
	
}