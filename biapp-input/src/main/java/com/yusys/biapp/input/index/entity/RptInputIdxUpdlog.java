package com.yusys.biapp.input.index.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "RPT_INPUT_IDX_UPDLOG")
public class RptInputIdxUpdlog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5271050752656179648L;
	/*
	 * @Id
	 * 
	 * @Column(name = "LOG_ID") private String logId;
	 * 
	 * @Column(name = "DATA_DATE") private String dataDate;
	 * 
	 * @Column(name = "INDEX_ID") private String indexId;
	 */

	@EmbeddedId
	private RptInputIdxUpdlogPK id;

	@Column(name = "UPDATE_DATE")
	private Timestamp updateDate;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "UPDATE_USER")
	private String updateUser;

	@Column(name = "ORG_NO")
	private String orgNo;

	public RptInputIdxUpdlogPK getId() {
		return id;
	}

	public void setId(RptInputIdxUpdlogPK id) {
		this.id = id;
	}

	public Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
}
