
package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_FAV_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_FAV_INFO")
@NamedQuery(name="RptDesignFavInfo.findAll", query="SELECT r FROM RptDesignFavInfo r")
public class RptDesignFavInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FAVOUR_ID")
	private String favourId;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="TEMPLATE_ID")
	private String templateId;
	
	@Column(name="VER_ID")
	private BigDecimal verId;

	public RptDesignFavInfo() {
	}

	public String getFavourId() {
		return this.favourId;
	}

	public void setFavourId(String favourId) {
		this.favourId = favourId;
	}

	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public BigDecimal getVerId() {
		return verId;
	}

	public void setVerId(BigDecimal verId) {
		this.verId = verId;
	}
	
	

}