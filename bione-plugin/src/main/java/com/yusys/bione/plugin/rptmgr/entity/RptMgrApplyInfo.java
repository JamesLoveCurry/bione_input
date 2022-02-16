package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_APPLY_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_APPLY_INFO")
@NamedQuery(name="RptMgrApplyInfo.findAll", query="SELECT r FROM RptMgrApplyInfo r")
public class RptMgrApplyInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="APPLY_ID")
	private String applyId;

	@Column(name="APPLY_STS")
	private String applySts;

	@Column(name="APPLY_USER_ID")
	private String applyUserId;

	@Column(name="CREATE_TIME")
	private Timestamp createTime;
	
	@Column(name="COLLATE_TIME")
	private Timestamp collateTime;

	@Column(name="OBJ_ID")
	private String objId;
	
	@Column(name="CATALOG_ID")
	private String catalogId;
	
	@Column(name="APPLY_CONTENT")
	private String applyContent;
	
	@Column(name="COLLATE_CONTENT")
	private String collateContent;
	
	private String remark;

	@Column(name="RPT_ID")
	private String rptId;

	public RptMgrApplyInfo() {
	}

	public String getApplyId() {
		return this.applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

	public String getApplySts() {
		return this.applySts;
	}

	public void setApplySts(String applySts) {
		this.applySts = applySts;
	}

	public String getApplyUserId() {
		return this.applyUserId;
	}

	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getObjId() {
		return this.objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRptId() {
		return this.rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getApplyContent() {
		return applyContent;
	}

	public void setApplyContent(String applyContent) {
		this.applyContent = applyContent;
	}

	public String getCollateContent() {
		return collateContent;
	}

	public void setCollateContent(String collateContent) {
		this.collateContent = collateContent;
	}

	public Timestamp getCollateTime() {
		return collateTime;
	}

	public void setCollateTime(Timestamp collateTime) {
		this.collateTime = collateTime;
	}
	
	

}