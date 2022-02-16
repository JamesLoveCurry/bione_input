package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DETAIL_TMP_SQL database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_TMP_SQL")
@NamedQuery(name="RptDetailTmpSql.findAll", query="SELECT r FROM RptDetailTmpSql r")
public class RptDetailTmpSql implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="DS_ID")
	private String dsId;
	
	@Column(name="PARAMTMP_ID")
	private String paramtmpId;

	private String querysql;

	public RptDetailTmpSql() {
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getDsId() {
		return this.dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getQuerysql() {
		return this.querysql;
	}

	public void setQuerysql(String querysql) {
		this.querysql = querysql;
	}

	public String getParamtmpId() {
		return paramtmpId;
	}

	public void setParamtmpId(String paramtmpId) {
		this.paramtmpId = paramtmpId;
	}
	
	

}