package com.yusys.bione.plugin.datamodel.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_SYS_MODULE_CATALOG database table.
 * 
 */
@Entity
@Table(name="RPT_SYS_MODULE_CATALOG")
public class RptSysModuleCatalog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="CATALOG_DESC")
	private String catalogDesc;

	@Column(name="CATALOG_NM")
	private String catalogNm;

	@Column(name="UP_ID")
	private String upId;

	@Column(name="ORDER_NO")
	private BigDecimal orderNo;
	
    public RptSysModuleCatalog() {
    }

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCatalogDesc() {
		return this.catalogDesc;
	}

	public void setCatalogDesc(String catalogDesc) {
		this.catalogDesc = catalogDesc;
	}

	public String getCatalogNm() {
		return this.catalogNm;
	}

	public void setCatalogNm(String catalogNm) {
		this.catalogNm = catalogNm;
	}

	public String getUpId() {
		return this.upId;
	}

	public void setUpId(String upId) {
		this.upId = upId;
	}

	public BigDecimal getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(BigDecimal orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((catalogDesc == null) ? 0 : catalogDesc.hashCode());
		result = prime * result
				+ ((catalogId == null) ? 0 : catalogId.hashCode());
		result = prime * result
				+ ((catalogNm == null) ? 0 : catalogNm.hashCode());
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
		RptSysModuleCatalog other = (RptSysModuleCatalog) obj;
		if (catalogDesc == null) {
			if (other.catalogDesc != null)
				return false;
		} else if (!catalogDesc.equals(other.catalogDesc))
			return false;
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
		if (upId == null) {
			if (other.upId != null)
				return false;
		} else if (!upId.equals(other.upId))
			return false;
		return true;
	}

	
}