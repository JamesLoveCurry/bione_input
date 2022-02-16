package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_CATALOG database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_CATALOG")
public class RptIdxCatalog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="INDEX_CATALOG_NO", unique=true, nullable=false, length=32)
	private String indexCatalogNo;

	@Column(name="INDEX_CATALOG_NM", nullable=false, length=100)
	private String indexCatalogNm;

	@Column(length=500)
	private String remark;

	@Column(name="UP_NO", length=32)
	private String upNo;

	@Column(name = "DEF_SRC")
	private String defSrc;
	
	@Column(name = "DEF_ORG")
	private String defOrg;

	@Column(name = "DEF_USER")
	private String defUser;
	
	@Column(name="CATALOG_ORDER")
	private BigDecimal catalogOrder;
	
    public RptIdxCatalog() {
    }

	public String getIndexCatalogNo() {
		return this.indexCatalogNo;
	}

	public void setIndexCatalogNo(String indexCatalogNo) {
		this.indexCatalogNo = indexCatalogNo;
	}

	public String getIndexCatalogNm() {
		return this.indexCatalogNm;
	}

	public void setIndexCatalogNm(String indexCatalogNm) {
		this.indexCatalogNm = indexCatalogNm;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpNo() {
		return this.upNo;
	}

	public void setUpNo(String upNo) {
		this.upNo = upNo;
	}
    
	public String getDefSrc() {
		return defSrc;
	}

	public void setDefSrc(String defSrc) {
		this.defSrc = defSrc;
	}

	public String getDefOrg() {
		return defOrg;
	}

	public void setDefOrg(String defOrg) {
		this.defOrg = defOrg;
	}

	public String getDefUser() {
		return defUser;
	}

	public void setDefUser(String defUser) {
		this.defUser = defUser;
	}

	public BigDecimal getCatalogOrder() {
		return catalogOrder;
	}

	public void setCatalogOrder(BigDecimal catalogOrder) {
		this.catalogOrder = catalogOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indexCatalogNm == null) ? 0 : indexCatalogNm.hashCode());
		result = prime * result
				+ ((indexCatalogNo == null) ? 0 : indexCatalogNo.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((upNo == null) ? 0 : upNo.hashCode());
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
		RptIdxCatalog other = (RptIdxCatalog) obj;
		if (indexCatalogNm == null) {
			if (other.indexCatalogNm != null)
				return false;
		} else if (!indexCatalogNm.equals(other.indexCatalogNm))
			return false;
		if (indexCatalogNo == null) {
			if (other.indexCatalogNo != null)
				return false;
		} else if (!indexCatalogNo.equals(other.indexCatalogNo))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (upNo == null) {
			if (other.upNo != null)
				return false;
		} else if (!upNo.equals(other.upNo))
			return false;
		return true;
	}

}