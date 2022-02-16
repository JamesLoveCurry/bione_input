package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ANA_TMP_IDX_REL database table.
 * 
 */
@Entity
@Table(name="RPT_ANA_TMP_IDX_REL")
@NamedQuery(name="RptAnaTmpIdxRel.findAll", query="SELECT r FROM RptAnaTmpIdxRel r")
public class RptAnaTmpIdxRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="INDEX_NM")
	private String indexNm;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	public RptAnaTmpIdxRel() {
	}

	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getIndexNm() {
		return this.indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

}