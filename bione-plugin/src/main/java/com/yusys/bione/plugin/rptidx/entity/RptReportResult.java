package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the RPT_REPORT_RESULT database table.
 * 
 */
@Entity
@Table(name="RPT_REPORT_RESULT")
@NamedQuery(name="RptReportResult.findAll", query="SELECT r FROM RptReportResult r")
public class RptReportResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptReportResultPK id;

	@Column(name="INDEX_VAL")
	private BigDecimal indexVal;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	public RptReportResult() {
	}

	public RptReportResultPK getId() {
		return this.id;
	}

	public void setId(RptReportResultPK id) {
		this.id = id;
	}

	public BigDecimal getIndexVal() {
		return this.indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

}