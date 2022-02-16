package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_VALID_LOGIC_RPT_REL database table.
 * 
 */
@Embeddable
public class RptValidLogicRptRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="RPT_TEMPLATE_ID")
	private String rptTemplateId;

	@Column(name="CHECK_ID")
	private String checkId;

    public RptValidLogicRptRelPK() {
    }
	public String getRptTemplateId() {
		return rptTemplateId;
	}
	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}
	public String getCheckId() {
		return this.checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptValidLogicRptRelPK)) {
			return false;
		}
		RptValidLogicRptRelPK castOther = (RptValidLogicRptRelPK)other;
		return 
			this.rptTemplateId.equals(castOther.rptTemplateId)
			&& this.checkId.equals(castOther.checkId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rptTemplateId.hashCode();
		hash = hash * prime + this.checkId.hashCode();
		
		return hash;
    }
}