package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_VALID_LOGIC_DETAIL database table.
 * 
 */
@Embeddable
public class RptValidLogicDetailPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CHECK_ID", unique=true, nullable=false, length=32)
	private String checkId;

	@Column(name="DATA_DATE", unique=true, nullable=false, length=8)
	private String dataDate;

	@Column(name="ORG_NO", unique=true, nullable=false, length=32)
	private String orgNo;

	@Column(name="INDEX_NO", unique=true, nullable=false, length=32)
	private String indexNo;

//	@Column(name="ORG_TYPE", unique=true, nullable=false, length=10)
//	private String orgType;

    public RptValidLogicDetailPK() {
    }
	public String getCheckId() {
		return this.checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public String getDataDate() {
		return this.dataDate;
	}
	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}
	public String getOrgNo() {
		return this.orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public String getIndexNo() {
		return this.indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
//	public String getOrgType() {
//		return this.orgType;
//	}
//	public void setOrgType(String orgType) {
//		this.orgType = orgType;
//	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptValidLogicDetailPK)) {
			return false;
		}
		RptValidLogicDetailPK castOther = (RptValidLogicDetailPK)other;
		return 
			this.checkId.equals(castOther.checkId)
			&& this.dataDate.equals(castOther.dataDate)
			&& this.orgNo.equals(castOther.orgNo)
			&& this.indexNo.equals(castOther.indexNo);
//			&& this.orgType.equals(castOther.orgType);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.checkId.hashCode();
		hash = hash * prime + this.dataDate.hashCode();
		hash = hash * prime + this.orgNo.hashCode();
		hash = hash * prime + this.indexNo.hashCode();
//		hash = hash * prime + this.orgType.hashCode();
		
		return hash;
    }
}