package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_FRS_LINEFILL_CFG database table.
 * 
 */
@Embeddable
public class RptMgrFrsLinefillCfgPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ORG_ID")
	private String orgId;

	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="LINE_ID")
	private String lineId;

	@Column(name="ORG_TYPE")
	private String orgType;
	
    public RptMgrFrsLinefillCfgPK() {
    }
	public String getOrgId() {
		return this.orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}
	public String getLineId() {
		return this.lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrFrsLinefillCfgPK)) {
			return false;
		}
		RptMgrFrsLinefillCfgPK castOther = (RptMgrFrsLinefillCfgPK)other;
		return 
			this.orgId.equals(castOther.orgId)
			&& this.rptId.equals(castOther.rptId)
			&& this.lineId.equals(castOther.lineId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orgId.hashCode();
		hash = hash * prime + this.rptId.hashCode();
		hash = hash * prime + this.lineId.hashCode();
		
		return hash;
    }
}