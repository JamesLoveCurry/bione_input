package com.yusys.bione.plugin.modeltrans.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_SYS_MODULE_COL_TRANS database table.
 * 
 */
@Embeddable
public class RptSysModuleColTranPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="SET_ID")
	private String setId;

	@Column(name="COL_ID")
	private String colId;

	public RptSysModuleColTranPK() {
	}
	public String getSetId() {
		return this.setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
	}
	public String getColId() {
		return this.colId;
	}
	public void setColId(String colId) {
		this.colId = colId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptSysModuleColTranPK)) {
			return false;
		}
		RptSysModuleColTranPK castOther = (RptSysModuleColTranPK)other;
		return 
			this.setId.equals(castOther.setId)
			&& this.colId.equals(castOther.colId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.setId.hashCode();
		hash = hash * prime + this.colId.hashCode();
		
		return hash;
	}
}