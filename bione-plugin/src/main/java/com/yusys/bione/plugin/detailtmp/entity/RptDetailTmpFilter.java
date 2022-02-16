package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DETAIL_TMP_FILTER database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_TMP_FILTER")
@NamedQuery(name="RptDetailTmpFilter.findAll", query="SELECT r FROM RptDetailTmpFilter r")
public class RptDetailTmpFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="FILT_INFO")
	private String filtInfo;

	public RptDetailTmpFilter() {
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getFiltInfo() {
		return this.filtInfo;
	}

	public void setFiltInfo(String filtInfo) {
		this.filtInfo = filtInfo;
	}

}