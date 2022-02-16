package com.yusys.biapp.input.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_LST_DATA_STATE database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LST_DATA_STATE")
public class RptInputLstDataState implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=32)
	private String id;

	@Column(name="CASE_ID", length=32)
	private String caseId;

	@Column(name="DATA_STS", length=32)
	private String dataSts;

	@Column(name="OPER_REMARK", length=500)
	private String operRemark;

	@Column(name="OPER_TIME", length=20)
	private String operTime;

	@Column(name="OPER_USER", length=32)
	private String operUser;

	@Column(name="ORG_NO", length=32)
	private String orgNo;

	@Column(name="TEMPLE_ID", length=32)
	private String templeId;

    public RptInputLstDataState() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCaseId() {
		return this.caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getDataSts() {
		return this.dataSts;
	}

	public void setDataSts(String dataSts) {
		this.dataSts = dataSts;
	}

	public String getOperRemark() {
		return this.operRemark;
	}

	public void setOperRemark(String operRemark) {
		this.operRemark = operRemark;
	}

	public String getOperTime() {
		return this.operTime;
	}

	public void setOperTime(String operTime) {
		this.operTime = operTime;
	}

	public String getOperUser() {
		return this.operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public String getOrgNo() {
		return this.orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getTempleId() {
		return this.templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

}