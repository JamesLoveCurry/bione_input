package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DETAIL_TMP_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_TMP_INFO")
@NamedQuery(name="RptDetailTmpInfo.findAll", query="SELECT r FROM RptDetailTmpInfo r")
public class RptDetailTmpInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="DS_ID")
	private String dsId;

	@Column(name="TEMPLATE_NM")
	private String templateNm;

	@Column(name="TEMPLATE_REMARK")
	private String templateRemark;

	@Column(name="TEMPLATE_STS")
	private String templateSts;

	@Column(name="TEMPLATE_TYPE")
	private String templateType;
	
	@Column(name="DEF_SRC")
	private String defSrc;
	
	@Column(name="SRC_ID")
	private String srcId;
	
	@Column(name="IS_CABIN")
	private String isCabin;
	
	@Column(name="IS_PAGE")
	private String isPage;

	
	public RptDetailTmpInfo() {
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getDsId() {
		return this.dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getTemplateNm() {
		return this.templateNm;
	}

	public void setTemplateNm(String templateNm) {
		this.templateNm = templateNm;
	}

	public String getTemplateRemark() {
		return this.templateRemark;
	}

	public void setTemplateRemark(String templateRemark) {
		this.templateRemark = templateRemark;
	}

	public String getTemplateSts() {
		return this.templateSts;
	}

	public void setTemplateSts(String templateSts) {
		this.templateSts = templateSts;
	}

	public String getTemplateType() {
		return this.templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getDefSrc() {
		return defSrc;
	}

	public void setDefSrc(String defSrc) {
		this.defSrc = defSrc;
	}

	public String getSrcId() {
		return srcId;
	}

	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}

	public String getIsCabin() {
		return isCabin;
	}

	public void setIsCabin(String isCabin) {
		this.isCabin = isCabin;
	}

	public String getIsPage() {
		return isPage;
	}

	public void setIsPage(String isPage) {
		this.isPage = isPage;
	}

	

}