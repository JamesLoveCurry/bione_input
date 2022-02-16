package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_GROUP_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_GROUP_INFO")
@NamedQuery(name="RptValidGroupInfo.findAll", query="SELECT r FROM RptValidGroupInfo r")
public class RptValidGroupInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VALID_GID")
	private String validGid;

	@Column(name="GROUP_ID")
	private String groupId;

	@Column(name="GROUP_TYPE")
	private String groupType;

	@Column(name="INDEX_NO")
	private String indexNo;

	public RptValidGroupInfo() {
	}

	public String getValidGid() {
		return this.validGid;
	}

	public void setValidGid(String validGid) {
		this.validGid = validGid;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupType() {
		return this.groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

}