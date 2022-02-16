package com.yusys.biapp.input.input.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_LIST_OPER_LOG database table.
 * 
 */
@Embeddable
public class RptListOperLogPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="FIELD_ID")
	private String fieldId;

	@Column(name="LOG_ID")
	private String logId;

    public RptListOperLogPK() {
    }
	public String getFieldId() {
		return this.fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getLogId() {
		return this.logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptListOperLogPK)) {
			return false;
		}
		RptListOperLogPK castOther = (RptListOperLogPK)other;
		return 
			this.fieldId.equals(castOther.fieldId)
			&& this.logId.equals(castOther.logId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.fieldId.hashCode();
		hash = hash * prime + this.logId.hashCode();
		
		return hash;
    }
}