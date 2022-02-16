package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the PAR_IDX_REPORT_MARK database table.
 * 
 */
@Embeddable
public class RptIdxReportMarkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DATA_DT")
	private String dataDt;

	@Column(name="IDX_NO")
	private String idxNo;

	public RptIdxReportMarkPK() {
	}
	public String getDataDt() {
		return this.dataDt;
	}
	public void setDataDt(String dataDt) {
		this.dataDt = dataDt;
	}
	public String getIdxNo() {
		return this.idxNo;
	}
	public void setIdxNo(String idxNo) {
		this.idxNo = idxNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptIdxReportMarkPK)) {
			return false;
		}
		RptIdxReportMarkPK castOther = (RptIdxReportMarkPK)other;
		return 
			this.dataDt.equals(castOther.dataDt)
			&& this.idxNo.equals(castOther.idxNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dataDt.hashCode();
		hash = hash * prime + this.idxNo.hashCode();
		
		return hash;
	}
}