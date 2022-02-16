package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_SERVER_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_SERVER_INFO")
public class RptMgrServerInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SERVER_ID")
	private String serverId;

	@Column(name="ADAPTER_ID")
	private String adapterId;

	@Column(name="AUTH_TYPE")
	private String authType;

	@Column(name="CREATE_TIME")
	private Timestamp createTime;

	private String namespace;

	@Column(name="SERVER_DESC")
	private String serverDesc;

	@Column(name="SERVER_NM")
	private String serverNm;

    public RptMgrServerInfo() {
    }

	public String getServerId() {
		return this.serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getAdapterId() {
		return this.adapterId;
	}

	public void setAdapterId(String adapterId) {
		this.adapterId = adapterId;
	}

	public String getAuthType() {
		return this.authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getServerDesc() {
		return this.serverDesc;
	}

	public void setServerDesc(String serverDesc) {
		this.serverDesc = serverDesc;
	}

	public String getServerNm() {
		return this.serverNm;
	}

	public void setServerNm(String serverNm) {
		this.serverNm = serverNm;
	}

}