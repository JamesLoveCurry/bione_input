package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_ENGINE_REPORT_STS database table.
 * 
 */
@Embeddable
public class RptEngineReportStsPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="RPT_ID", unique=true, nullable=false, length=32)
	private String rptId;

	@Column(name="DATA_DATE", unique=true, nullable=false, length=8)
	private String dataDate;

    public RptEngineReportStsPK() {
    }
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
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
		if (!(other instanceof RptEngineReportStsPK)) {
			return false;
		}
		RptEngineReportStsPK castOther = (RptEngineReportStsPK)other;
		return 
			this.rptId.equals(castOther.rptId)
			&& this.dataDate.equals(castOther.dataDate);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rptId.hashCode();
		hash = hash * prime + this.dataDate.hashCode();
		
		return hash;
    }
}