package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_DETAIL_TMP_SEARCH database table.
 * 
 */
@Embeddable
public class RptDetailTmpSearchPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PARAM_ID")
	private String paramId;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	public RptDetailTmpSearchPK() {
	}
	public String getParamId() {
		return this.paramId;
	}
	public void setParamId(String paramId) {
		this.paramId = paramId;
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
		if (!(other instanceof RptDetailTmpSearchPK)) {
			return false;
		}
		RptDetailTmpSearchPK castOther = (RptDetailTmpSearchPK)other;
		return 
			this.paramId.equals(castOther.paramId)
			&& this.templateId.equals(castOther.templateId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.paramId.hashCode();
		hash = hash * prime + this.templateId.hashCode();
		
		return hash;
	}
}