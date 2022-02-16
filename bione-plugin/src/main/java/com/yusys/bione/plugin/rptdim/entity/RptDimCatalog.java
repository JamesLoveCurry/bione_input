package com.yusys.bione.plugin.rptdim.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DIM_CATALOG database table.
 * 
 */
@Entity
@Table(name="RPT_DIM_CATALOG")
public class RptDimCatalog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="CATALOG_NM")
	private String catalogNm;
	
	private String remark;

	@Column(name="UP_CATALOG_ID")
	private String upCatalogId;

    public RptDimCatalog() {
    }

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCatalogNm() {
		return this.catalogNm;
	}

	public void setCatalogNm(String catalogNm) {
		this.catalogNm = catalogNm;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpCatalogId() {
		return this.upCatalogId;
	}

	public void setUpCatalogId(String upCatalogId) {
		this.upCatalogId = upCatalogId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((catalogId == null) ? 0 : catalogId.hashCode());
		result = prime * result
				+ ((catalogNm == null) ? 0 : catalogNm.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result
				+ ((upCatalogId == null) ? 0 : upCatalogId.hashCode());
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
		RptDimCatalog other = (RptDimCatalog) obj;
		if (catalogId == null) {
			if (other.catalogId != null)
				return false;
		} else if (!catalogId.equals(other.catalogId))
			return false;
		if (catalogNm == null) {
			if (other.catalogNm != null)
				return false;
		} else if (!catalogNm.equals(other.catalogNm))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (upCatalogId == null) {
			if (other.upCatalogId != null)
				return false;
		} else if (!upCatalogId.equals(other.upCatalogId))
			return false;
		return true;
	}

	
}