package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_FRS_PBC_IDX_RESULT database table.
 * 
 */
@Embeddable
public class RptFrsPbcIdxResultPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="DATA_DATE")
	private String dataDate;

	@Column(name="ORG_NO")
	private String orgNo;

	@Column(name="CURRENCY")
	private String currency;
	
	@Column(name="BUSI_LINE")
	private String busiLine;

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBusiLine() {
		return busiLine;
	}

	public void setBusiLine(String busiLine) {
		this.busiLine = busiLine;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((busiLine == null) ? 0 : busiLine.hashCode());
		result = prime * result
				+ ((currency == null) ? 0 : currency.hashCode());
		result = prime * result
				+ ((dataDate == null) ? 0 : dataDate.hashCode());
		result = prime * result + ((indexNo == null) ? 0 : indexNo.hashCode());
		result = prime * result + ((orgNo == null) ? 0 : orgNo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptFrsPbcIdxResultPK other = (RptFrsPbcIdxResultPK) obj;
		if (busiLine == null) {
			if (other.busiLine != null)
				return false;
		} else if (!busiLine.equals(other.busiLine))
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (dataDate == null) {
			if (other.dataDate != null)
				return false;
		} else if (!dataDate.equals(other.dataDate))
			return false;
		if (indexNo == null) {
			if (other.indexNo != null)
				return false;
		} else if (!indexNo.equals(other.indexNo))
			return false;
		if (orgNo == null) {
			if (other.orgNo != null)
				return false;
		} else if (!orgNo.equals(other.orgNo))
			return false;
		return true;
	}
	
}