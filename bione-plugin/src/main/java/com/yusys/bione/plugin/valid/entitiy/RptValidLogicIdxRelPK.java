package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_VALID_LOGIC_IDX_REL database table.
 * 
 */
@Embeddable
public class RptValidLogicIdxRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="CHECK_ID")
	private String checkId;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="FORMULA_TYPE")
	private String formulaType;

    public RptValidLogicIdxRelPK() {
    }
	public String getIndexNo() {
		return this.indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public String getCheckId() {
		return this.checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public String getTemplateId() {
		return this.templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getFormulaType() {
		return this.formulaType;
	}
	public void setFormulaType(String formulaType) {
		this.formulaType = formulaType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptValidLogicIdxRelPK)) {
			return false;
		}
		RptValidLogicIdxRelPK castOther = (RptValidLogicIdxRelPK)other;
		return 
			this.indexNo.equals(castOther.indexNo)
			&& this.checkId.equals(castOther.checkId)
			&& this.templateId.equals(castOther.templateId)
			&& this.formulaType.equals(castOther.formulaType);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + this.checkId.hashCode();
		hash = hash * prime + this.templateId.hashCode();
		hash = hash * prime + this.formulaType.hashCode();
		
		return hash;
    }
}