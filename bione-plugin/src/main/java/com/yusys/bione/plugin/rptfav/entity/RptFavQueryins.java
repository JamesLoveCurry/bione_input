package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_FAV_QUERYINS database table.
 * 
 */
@Entity
@Table(name="RPT_FAV_QUERYINS")
public class RptFavQueryins implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="INSTANCE_ID", unique=true, nullable=false, length=32)
	private String instanceId;

	@Column(name="CREATE_TIME")
	private Timestamp createTime;

	@Column(name="CREATE_USER", length=32)
	private String createUser;

	@Column(name="QUERY_NM", nullable=false, length=100)
	private String queryNm;
	
	@Column(name="BUSI_TYPE")
	private String busiType;
	
	@Column(length=500)
	private String remark;

    public RptFavQueryins() {
    }

	public String getInstanceId() {
		return this.instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getQueryNm() {
		return this.queryNm;
	}

	public void setQueryNm(String queryNm) {
		this.queryNm = queryNm;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

}