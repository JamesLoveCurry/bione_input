package com.yusys.bione.plugin.paramtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_PARAMTMP_CATALOG database table.
 * 
 */
@Entity
@Table(name="RPT_PARAMTMP_CATALOG")
public class RptParamtmpCatalog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="CATALOG_NM")
	private String catalogNm;

	private String remark;

	@Column(name="UP_ID")
	private String upId;

    public RptParamtmpCatalog() {
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

	public String getUpId() {
		return this.upId;
	}

	public void setUpId(String upId) {
		this.upId = upId;
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
		result = prime * result + ((upId == null) ? 0 : upId.hashCode());
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
		RptParamtmpCatalog other = (RptParamtmpCatalog) obj;
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
		if (upId == null) {
			if (other.upId != null)
				return false;
		} else if (!upId.equals(other.upId))
			return false;
		return true;
	}

	
}