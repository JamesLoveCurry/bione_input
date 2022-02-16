package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_DETAIL_TMP_CONFIG database table.
 * 
 */
@Embeddable
public class RptDetailTmpConfigPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	public RptDetailTmpConfigPK() {
	}
	public String getCfgId() {
		return this.cfgId;
	}
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	public String getTemplateId() {
		return this.templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptDetailTmpConfigPK)) {
			return false;
		}
		RptDetailTmpConfigPK castOther = (RptDetailTmpConfigPK)other;
		return 
			this.cfgId.equals(castOther.cfgId)
			&& this.templateId.equals(castOther.templateId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.cfgId.hashCode();
		hash = hash * prime + this.templateId.hashCode();
		
		return hash;
	}
}