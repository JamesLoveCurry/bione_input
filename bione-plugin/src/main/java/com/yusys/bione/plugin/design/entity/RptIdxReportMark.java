package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the PAR_IDX_REPORT_MARK database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_REPORT_MARK")
@NamedQuery(name="RptIdxReportMark.findAll", query="SELECT p FROM RptIdxReportMark p")
public class RptIdxReportMark implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxReportMarkPK id;

	private String remark;

	public RptIdxReportMark() {
	}

	public RptIdxReportMarkPK getId() {
		return this.id;
	}

	public void setId(RptIdxReportMarkPK id) {
		this.id = id;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}