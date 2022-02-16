package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_AUTH_USER_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_AUTH_USER_INFO")
public class RptMgrAuthUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="AUTH_ID")
	private String authId;

	@Column(name="AUTH_USER_NM")
	private String authUserNm;

	@Column(name="AUTH_USER_PWD")
	private String authUserPwd;

	private String remark;

	@Column(name="SERVER_ID")
	private String serverId;

    public RptMgrAuthUserInfo() {
    }

	public String getAuthId() {
		return this.authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getAuthUserNm() {
		return this.authUserNm;
	}

	public void setAuthUserNm(String authUserNm) {
		this.authUserNm = authUserNm;
	}

	public String getAuthUserPwd() {
		return this.authUserPwd;
	}

	public void setAuthUserPwd(String authUserPwd) {
		this.authUserPwd = authUserPwd;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getServerId() {
		return this.serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

}