package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_OUTER_CFG database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_OUTER_CFG")
public class RptMgrOuterCfg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="PARAMTMP_ID")
	private String paramtmpId;

	@Column(name="RPT_SRC_PATH")
	private String rptSrcPath;

	@Column(name="SEARCH_PATH")
	private String searchPath;

	@Column(name="SERVER_ID")
	private String serverId;

    public RptMgrOuterCfg() {
    }

	public String getCfgId() {
		return this.cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getParamtmpId() {
		return this.paramtmpId;
	}

	public void setParamtmpId(String paramtmpId) {
		this.paramtmpId = paramtmpId;
	}

	public String getRptSrcPath() {
		return this.rptSrcPath;
	}

	public void setRptSrcPath(String rptSrcPath) {
		this.rptSrcPath = rptSrcPath;
	}

	public String getSearchPath() {
		return this.searchPath;
	}

	public void setSearchPath(String searchPath) {
		this.searchPath = searchPath;
	}

	public String getServerId() {
		return this.serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

}