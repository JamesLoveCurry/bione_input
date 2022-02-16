package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RPT_CONCERN_RPT database table.
 * 
 */
@Entity
@Table(name="RPT_CONCERN_RPT")
@NamedQuery(name="RptConcernRpt.findAll", query="SELECT r FROM RptConcernRpt r")
public class RptConcernRpt implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptConcernRptPK id;

	@Column(name="RPT_NM")
	private String rptNm;
	
	@Column(name="CATA_ID")
	private String cataId;

	public RptConcernRpt() {
	}

	public RptConcernRptPK getId() {
		return this.id;
	}

	public void setId(RptConcernRptPK id) {
		this.id = id;
	}

	public String getRptNm() {
		return this.rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	
	public String getCataId() {
		return this.cataId;
	}

	public void setCataId(String cataId) {
		this.cataId = cataId;
	}
}