package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_DETAIL_TMP_SORT database table.
 * 
 */
@Embeddable
public class RptDetailTmpSortPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="CFG_ID")
	private String cfgId;

	public RptDetailTmpSortPK() {
	}
	public String getTemplateId() {
		return this.templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getCfgId() {
		return this.cfgId;
	}
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptDetailTmpSortPK)) {
			return false;
		}
		RptDetailTmpSortPK castOther = (RptDetailTmpSortPK)other;
		return 
			this.templateId.equals(castOther.templateId)
			&& this.cfgId.equals(castOther.cfgId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.templateId.hashCode();
		hash = hash * prime + this.cfgId.hashCode();
		
		return hash;
	}
}