package com.yusys.bione.plugin.rptshare.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_SHARE_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_SHARE_INFO")
@NamedQuery(name="RptMgrShareInfo.findAll", query="SELECT r FROM RptMgrShareInfo r")
public class RptMgrShareInfo  implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SHARE_ID")
	private String shareId;

	@Column(name="CREATE_TIME")
	private Timestamp createTime;

	@Column(name="OBJ_ID")
	private String objId;
	
	@Column(name="OBJ_NAME")
	private String objName;

	@Column(name="OBJ_TYPE")
	private String objType;

	@Column(name="OBJ_USER_ID")
	private String objUserId;

	@Column(name="SHARE_STS")
	private String shareSts;

	@Column(name="SRC_USER_ID")
	private String srcUserId;
	
	private String remark;

	public RptMgrShareInfo() {
	}

	public String getShareId() {
		return this.shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
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

	public String getObjType() {
		return this.objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getObjUserId() {
		return this.objUserId;
	}

	public void setObjUserId(String objUserId) {
		this.objUserId = objUserId;
	}

	public String getShareSts() {
		return this.shareSts;
	}

	public void setShareSts(String shareSts) {
		this.shareSts = shareSts;
	}

	public String getSrcUserId() {
		return this.srcUserId;
	}

	public void setSrcUserId(String srcUserId) {
		this.srcUserId = srcUserId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	
}