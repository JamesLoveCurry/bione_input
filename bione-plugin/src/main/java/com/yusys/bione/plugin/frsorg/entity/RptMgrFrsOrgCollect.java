package com.yusys.bione.plugin.frsorg.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_FRS_ORG_COLLECT database table.
 * 
 */
@Entity
@Table(name="RPT_ORG_SUM_REL")
public class RptMgrFrsOrgCollect implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrFrsOrgCollectPK id;

	@Column(name="SUM_REL_TYPE")
	private String sumRelType;

    public RptMgrFrsOrgCollect() {
    }

	public RptMgrFrsOrgCollectPK getId() {
		return this.id;
	}

	public void setId(RptMgrFrsOrgCollectPK id) {
		this.id = id;
	}
	
	public String getSumRelType() {
		return this.sumRelType;
	}

	public void setSumRelType(String sumRelType) {
		this.sumRelType = sumRelType;
	}

}