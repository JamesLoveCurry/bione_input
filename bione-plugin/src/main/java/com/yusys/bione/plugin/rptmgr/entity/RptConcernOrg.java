package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RPT_CONCERN_ORG database table.
 * 
 */
@Entity
@Table(name="RPT_CONCERN_ORG")
@NamedQuery(name="RptConcernOrg.findAll", query="SELECT r FROM RptConcernOrg r")
public class RptConcernOrg implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptConcernOrgPK id;

	@Column(name="ORG_NM")
	private String orgNm;

	public RptConcernOrg() {
	}

	public RptConcernOrgPK getId() {
		return this.id;
	}

	public void setId(RptConcernOrgPK id) {
		this.id = id;
	}

	public String getOrgNm() {
		return this.orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

}