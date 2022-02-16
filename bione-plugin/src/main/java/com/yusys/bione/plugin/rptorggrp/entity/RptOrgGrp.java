package com.yusys.bione.plugin.rptorggrp.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_org_grp database table.
 * 
 */
@Entity
@Table(name="rpt_org_grp")
public class RptOrgGrp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="GRP_ID")
	private String grpId;

	@Column(name="GRP_NM")
	private String grpNm;
	
	@Column(name="ORG_TYPE")
	private String orgType;
	
	@Column(name="CREATE_ORG")
	private String createOrg;
	
	private String remark;

    public RptOrgGrp() {
    }

	public String getGrpId() {
		return this.grpId;
	}

	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}

	public String getGrpNm() {
		return this.grpNm;
	}

	public void setGrpNm(String grpNm) {
		this.grpNm = grpNm;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getCreateOrg() {
		return createOrg;
	}

	public void setCreateOrg(String createOrg) {
		this.createOrg = createOrg;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}