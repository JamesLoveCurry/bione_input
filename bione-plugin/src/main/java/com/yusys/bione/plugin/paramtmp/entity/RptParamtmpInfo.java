package com.yusys.bione.plugin.paramtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_PARAMTMP_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_PARAMTMP_INFO")
public class RptParamtmpInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PARAMTMP_ID")
	private String paramtmpId;

	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="PARAMTMP_NM")
	private String paramtmpNm;

	private String remark;

	@Column(name="TEMPLATE_TYPE")
	private String templateType;

	@Column(name="TEMPLATE_URL")
	private String templateUrl;

    public RptParamtmpInfo() {
    }

	public String getParamtmpId() {
		return this.paramtmpId;
	}

	public void setParamtmpId(String paramtmpId) {
		this.paramtmpId = paramtmpId;
	}

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getParamtmpNm() {
		return this.paramtmpNm;
	}

	public void setParamtmpNm(String paramtmpNm) {
		this.paramtmpNm = paramtmpNm;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTemplateType() {
		return this.templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getTemplateUrl() {
		return this.templateUrl;
	}

	public void setTemplateUrl(String templateUrl) {
		this.templateUrl = templateUrl;
	}

}