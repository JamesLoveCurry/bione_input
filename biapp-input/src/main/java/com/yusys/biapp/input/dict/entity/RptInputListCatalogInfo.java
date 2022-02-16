package com.yusys.biapp.input.dict.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_INPUT_LIST_CATALOG_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_INPUT_LIST_CATALOG_INFO")
public class RptInputListCatalogInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CATALOG_ID", unique = true, nullable = false, length = 32)
	private String catalogId;

	@Column(name = "CATALOG_NAME", length = 100)
	private String catalogName;

	@Column(name = "CATALOG_TYPE", length = 10)
	private String catalogType;

	@Column(name = "CREATE_TIME", length = 20)
	private String createTime;

	@Column(name = "LOGIC_SYS_NO", length = 100)
	private String logicSysNo;

	@Column(name = "ORDER_NO", nullable = false, precision = 18)
	private BigDecimal orderNo;

	@Column(name = "UP_CATALOG", length = 32)
	private String upCatalog;
	
	@Column(name = "DEF_SRC", length = 10)
	private String defSrc;
	
	@Column(name = "DEF_ORG", length = 32)
	private String defOrg;
	
	@Column(name = "DEF_USER", length = 32)
	private String defUser;

	public RptInputListCatalogInfo() {
	}

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCatalogName() {
		return this.catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public String getCatalogType() {
		return this.catalogType;
	}

	public void setCatalogType(String catalogType) {
		this.catalogType = catalogType;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public BigDecimal getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(BigDecimal orderNo) {
		this.orderNo = orderNo;
	}

	public String getUpCatalog() {
		return this.upCatalog;
	}

	public void setUpCatalog(String upCatalog) {
		this.upCatalog = upCatalog;
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

}