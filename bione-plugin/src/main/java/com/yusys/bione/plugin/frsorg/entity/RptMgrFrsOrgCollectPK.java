package com.yusys.bione.plugin.frsorg.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_FRS_ORG_COLLECT database table.
 * 
 */
@Embeddable
public class RptMgrFrsOrgCollectPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ORG_NO")
	private String orgNo;

	@Column(name="UP_ORG_NO")
	private String upOrgNo;

	@Column(name="ORG_TYPE")
	private String orgType;

    public RptMgrFrsOrgCollectPK() {
    }
	public String getOrgNo() {
		return this.orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	
	public String getUpOrgNo() {
		return upOrgNo;
	}
	public void setUpOrgNo(String upOrgNo) {
		this.upOrgNo = upOrgNo;
	}
	public String getOrgType() {
		return this.orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrFrsOrgCollectPK)) {
			return false;
		}
		RptMgrFrsOrgCollectPK castOther = (RptMgrFrsOrgCollectPK)other;
		return 
			this.orgNo.equals(castOther.orgNo)
			&& this.upOrgNo.equals(castOther.upOrgNo)
			&& this.orgType.equals(castOther.orgType);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.orgNo.hashCode();
		hash = hash * prime + this.upOrgNo.hashCode();
		hash = hash * prime + this.orgType.hashCode();
		
		return hash;
    }
}