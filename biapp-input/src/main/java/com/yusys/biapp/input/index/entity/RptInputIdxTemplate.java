package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_IDX_TEMPLATE")
public class RptInputIdxTemplate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2985246176244615680L;

	@Id
	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="TEMPLATE_TYPE")
	private String templateType;

	@Column(name="TEMPLATE_NM")
	private String templateNm;

	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="REMARK")
	private String remark;

	@Column(name="INPUT_TYPE")
	private String inputType;


    public RptInputIdxTemplate() {
    }


	public String getTemplateId() {
		return templateId;
	}


	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}


	public String getTemplateType() {
		return templateType;
	}


	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}


	public String getTemplateNm() {
		return templateNm;
	}


	public void setTemplateNm(String templateNm) {
		this.templateNm = templateNm;
	}


	public String getCatalogId() {
		return catalogId;
	}


	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getInputType() {
		return inputType;
	}


	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

}