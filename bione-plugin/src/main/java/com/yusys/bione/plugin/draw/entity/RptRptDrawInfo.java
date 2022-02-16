package com.yusys.bione.plugin.draw.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the PECULIAR_RPT_DRAW_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_RPT_DRAW_INFO")
@NamedQuery(name="RptRptDrawInfo.findAll", query="SELECT p FROM RptRptDrawInfo p")
public class RptRptDrawInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="DRAW_DATE")
	private String drawDate;

	public RptRptDrawInfo() {
	}

	public String getRptId() {
		return this.rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getDrawDate() {
		return this.drawDate;
	}

	public void setDrawDate(String drawDate) {
		this.drawDate = drawDate;
	}

}