package com.yusys.bione.plugin.businessline.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



@Entity
@Table(name="RPT_MGR_BUSI_CFG")
@NamedQuery(name="RptMgrBusiCfg.findAll", query="SELECT r FROM RptMgrBusiCfg r")
public class RptMgrBusiCfg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="LINE_ID")
	private String lineId;

	@Column(name="IDX_SET_ID")
	private String idxSetId;

	@Column(name="RPT_SET_ID")
	private String rptSetId;

	public RptMgrBusiCfg() {
	}

	public String getLineId() {
		return this.lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getIdxSetId() {
		return this.idxSetId;
	}

	public void setIdxSetId(String idxSetId) {
		this.idxSetId = idxSetId;
	}

	public String getRptSetId() {
		return this.rptSetId;
	}

	public void setRptSetId(String rptSetId) {
		this.rptSetId = rptSetId;
	}

}