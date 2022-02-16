package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_DESIGN_SOURCE_SORTNO database table.
 * 
 */
@Embeddable
public class RptDesignSourceSortnoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CELL_NO")
	private String cellNo;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="VER_ID")
	private long verId;

    public RptDesignSourceSortnoPK() {
    }
	
	public String getCellNo() {
		return cellNo;
	}

	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}

	public String getTemplateId() {
		return this.templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public long getVerId() {
		return this.verId;
	}
	public void setVerId(long verId) {
		this.verId = verId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptDesignSourceSortnoPK)) {
			return false;
		}
		RptDesignSourceSortnoPK castOther = (RptDesignSourceSortnoPK)other;
		return 
			this.cellNo.equals(castOther.cellNo)
			&& this.templateId.equals(castOther.templateId)
			&& (this.verId == castOther.verId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.cellNo.hashCode();
		hash = hash * prime + this.templateId.hashCode();
		hash = hash * prime + ((int) (this.verId ^ (this.verId >>> 32)));
		
		return hash;
    }
}