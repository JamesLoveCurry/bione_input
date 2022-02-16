package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_REPORT_CATALOG database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_REPORT_CATALOG")
public class RptMgrReportCatalog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="CATALOG_NM")
	private String catalogNm;

	private String remark;

	@Column(name="UP_CATALOG_ID")
	private String upCatalogId;
	
	@Column(name="EXT_TYPE")
	private String extType;
	
	@Column(name="DEF_SRC")
	private String defSrc;
	
	@Column(name="DEF_ORG")
	private String defOrg;
	
	@Column(name="DEF_USER")
	private String defUser;
	
	@Column(name="CATALOG_ORDER")
	private BigDecimal catalogOrder;

    public RptMgrReportCatalog() {
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

	public String getExtType() {
		return extType;
	}

	public void setExtType(String extType) {
		this.extType = extType;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((catalogId == null) ? 0 : catalogId.hashCode());
		result = prime * result
				+ ((catalogNm == null) ? 0 : catalogNm.hashCode());
		result = prime * result + ((defOrg == null) ? 0 : defOrg.hashCode());
		result = prime * result + ((defSrc == null) ? 0 : defSrc.hashCode());
		result = prime * result + ((defUser == null) ? 0 : defUser.hashCode());
		result = prime * result + ((extType == null) ? 0 : extType.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result
				+ ((upCatalogId == null) ? 0 : upCatalogId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptMgrReportCatalog other = (RptMgrReportCatalog) obj;
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
		if (defOrg == null) {
			if (other.defOrg != null)
				return false;
		} else if (!defOrg.equals(other.defOrg))
			return false;
		if (defSrc == null) {
			if (other.defSrc != null)
				return false;
		} else if (!defSrc.equals(other.defSrc))
			return false;
		if (defUser == null) {
			if (other.defUser != null)
				return false;
		} else if (!defUser.equals(other.defUser))
			return false;
		if (extType == null) {
			if (other.extType != null)
				return false;
		} else if (!extType.equals(other.extType))
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