package com.yusys.bione.frame.authobj.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the rpt_org_grp database table.
 * 
 */
@Entity
@Table(name="bione_org_grp")
public class BioneOrgGrp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="GRP_ID")
	private String grpId;

	@Column(name="GRP_NM")
	private String grpNm;

	@Column(name="CREATE_ORG")
	private String createOrg;

	@Column(name="CREATE_TIME")
	private Timestamp createTime;

	private String remark;

    public BioneOrgGrp() {
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

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}