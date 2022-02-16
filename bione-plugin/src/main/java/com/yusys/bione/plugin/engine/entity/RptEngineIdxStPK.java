package com.yusys.bione.plugin.engine.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_ENGINE_IDX_STS database table.
 * 
 */
@Embeddable
public class RptEngineIdxStPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="DATA_DATE")
	private String dataDate;

    public RptEngineIdxStPK() {
    }
	public String getIndexNo() {
		return this.indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public String getDataDate() {
		return this.dataDate;
	}
	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptEngineIdxStPK)) {
			return false;
		}
		RptEngineIdxStPK castOther = (RptEngineIdxStPK)other;
		return 
			this.indexNo.equals(castOther.indexNo)
			&& this.dataDate.equals(castOther.dataDate);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + this.dataDate.hashCode();
		
		return hash;
    }
}