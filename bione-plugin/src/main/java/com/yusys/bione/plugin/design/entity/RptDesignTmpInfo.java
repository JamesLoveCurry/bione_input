package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_TMP_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_TMP_INFO")
public class RptDesignTmpInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignTmpInfoPK id;

	@Column(name="IS_UPT")
	private String isUpt;

	private String remark;

	@Column(name="TEMPLATE_CONTENTJSON")
	private String templateContentjson;

	@Column(name="TEMPLATE_NM")
	private String templateNm;

	@Column(name="VER_END_DATE")
	private String verEndDate;

	@Column(name="VER_START_DATE")
	private String verStartDate;
	
	@Column(name="TEMPLATE_TYPE")
	private String templateType;
	
	@Column(name="PARENT_TEMPLATE_ID")
	private String parentTemplateId;

	@Column(name="LINE_ID")
	private String lineId;
	
	@Column(name="TEMPLATE_UNIT")
	private String templateUnit;
	
	@Column(name="IS_AUTO_ADJ")
	private String isAutoAdj;
	
	@Column(name="FIXED_LENGTH")
	private String fixedLength;
	
	@Column(name="IS_PAGING")
	private String isPaging;
	
	@Column(name="SORT_SQL")
	private String sortSql;

	@Column(name="IMPORT_CONFIG")
	private String importConfig;
	
    public RptDesignTmpInfo() {
    }

	public RptDesignTmpInfoPK getId() {
		return this.id;
	}

	public void setId(RptDesignTmpInfoPK id) {
		this.id = id;
	}
	
	public String getIsUpt() {
		return this.isUpt;
	}

	public void setIsUpt(String isUpt) {
		this.isUpt = isUpt;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTemplateContentjson() {
		return this.templateContentjson;
	}

	public void setTemplateContentjson(String templateContentjson) {
		this.templateContentjson = templateContentjson;
	}

	public String getTemplateNm() {
		return this.templateNm;
	}

	public void setTemplateNm(String templateNm) {
		this.templateNm = templateNm;
	}

	public String getVerEndDate() {
		return this.verEndDate;
	}

	public void setVerEndDate(String verEndDate) {
		this.verEndDate = verEndDate;
	}

	public String getVerStartDate() {
		return this.verStartDate;
	}

	public void setVerStartDate(String verStartDate) {
		this.verStartDate = verStartDate;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getParentTemplateId() {
		return parentTemplateId;
	}

	public void setParentTemplateId(String parentTemplateId) {
		this.parentTemplateId = parentTemplateId;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getTemplateUnit() {
		return templateUnit;
	}

	public void setTemplateUnit(String templateUnit) {
		this.templateUnit = templateUnit;
	}

	public String getIsAutoAdj() {
		return isAutoAdj;
	}

	public void setIsAutoAdj(String isAutoAdj) {
		this.isAutoAdj = isAutoAdj;
	}

	public String getFixedLength() {
		return fixedLength;
	}

	public void setFixedLength(String fixedLength) {
		this.fixedLength = fixedLength;
	}

	public String getIsPaging() {
		return isPaging;
	}

	public void setIsPaging(String isPaging) {
		this.isPaging = isPaging;
	}

	public String getSortSql() {
		return sortSql;
	}

	public String getImportConfig() {
		return importConfig;
	}

	public void setSortSql(String sortSql) {
		this.sortSql = sortSql;
    }

	public void setImportConfig(String importConfig) {
		this.importConfig = importConfig;
	}
	
}