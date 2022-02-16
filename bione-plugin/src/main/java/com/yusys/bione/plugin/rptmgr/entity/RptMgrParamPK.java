package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_MGR_PARAM database table.
 * 
 */
@Embeddable
public class RptMgrParamPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="SERVER_ID")
	private String serverId;

	@Column(name="ADAPTER_PARAM_ID")
	private String adapterParamId;

    public RptMgrParamPK() {
    }
	public String getServerId() {
		return this.serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getAdapterParamId() {
		return this.adapterParamId;
	}
	public void setAdapterParamId(String adapterParamId) {
		this.adapterParamId = adapterParamId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrParamPK)) {
			return false;
		}
		RptMgrParamPK castOther = (RptMgrParamPK)other;
		return 
			this.serverId.equals(castOther.serverId)
			&& this.adapterParamId.equals(castOther.adapterParamId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.serverId.hashCode();
		hash = hash * prime + this.adapterParamId.hashCode();
		
		return hash;
    }
}