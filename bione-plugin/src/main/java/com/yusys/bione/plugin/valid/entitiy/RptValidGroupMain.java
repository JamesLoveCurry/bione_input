package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_GROUP_MAIN database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_GROUP_MAIN")
@NamedQuery(name="RptValidGroupMain.findAll", query="SELECT r FROM RptValidGroupMain r")
public class RptValidGroupMain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="GROUP_ID")
	private String groupId;

	@Column(name="GROUP_NM")
	private String groupNm;

	@Column(name="GROUP_NO")
	private String groupNo;

	@Column(name="GROUP_TYPE")
	private String groupType;

	private String remark;

	public RptValidGroupMain() {
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupNm() {
		return this.groupNm;
	}

	public void setGroupNm(String groupNm) {
		this.groupNm = groupNm;
	}

	public String getGroupNo() {
		return this.groupNo;
	}

	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}

	public String getGroupType() {
		return this.groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}