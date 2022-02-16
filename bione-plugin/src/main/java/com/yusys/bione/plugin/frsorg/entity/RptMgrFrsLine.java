package com.yusys.bione.plugin.frsorg.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_FRS_LINE database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_FRS_LINE")
@NamedQuery(name="RptMgrFrsLine.findAll", query="SELECT r FROM RptMgrFrsLine r")
public class RptMgrFrsLine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="LINE_ID")
	private String lineId;

	@Column(name="LINE_NM")
	private String lineNm;

	public RptMgrFrsLine() {
	}

	public String getLineId() {
		return this.lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getLineNm() {
		return this.lineNm;
	}

	public void setLineNm(String lineNm) {
		this.lineNm = lineNm;
	}

}