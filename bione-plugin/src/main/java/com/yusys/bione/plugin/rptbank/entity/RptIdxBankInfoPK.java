package com.yusys.bione.plugin.rptbank.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_BANK_INFO database table.
 * 
 */


@Embeddable
public class RptIdxBankInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_ID")
	private String indexId;

	@Column(name="THEME_ID")
	private String themeId;

	public RptIdxBankInfoPK() {
	}
	public String getIndexId() {
		return this.indexId;
	}
	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}
	public String getThemeId() {
		return this.themeId;
	}
	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptIdxBankInfoPK)) {
			return false;
		}
		RptIdxBankInfoPK castOther = (RptIdxBankInfoPK)other;
		return 
			this.indexId.equals(castOther.indexId)
			&& this.themeId.equals(castOther.themeId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.indexId.hashCode();
		hash = hash * prime + this.themeId.hashCode();
		
		return hash;
	}
}