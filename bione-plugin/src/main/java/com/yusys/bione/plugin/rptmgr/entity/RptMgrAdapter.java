package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_ADAPTER database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_ADAPTER")
public class RptMgrAdapter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ADAPTER_ID")
	private String adapterId;

	@Column(name="ADAPTER_AUTH_TYPE")
	private String adapterAuthType;

	@Column(name="ADAPTER_NM")
	private String adapterNm;

	@Column(name="ADAPTER_TYPE")
	private String adapterType;

	private String remark;

    public RptMgrAdapter() {
    }

	public String getAdapterId() {
		return this.adapterId;
	}

	public void setAdapterId(String adapterId) {
		this.adapterId = adapterId;
	}

	public String getAdapterAuthType() {
		return this.adapterAuthType;
	}

	public void setAdapterAuthType(String adapterAuthType) {
		this.adapterAuthType = adapterAuthType;
	}

	public String getAdapterNm() {
		return this.adapterNm;
	}

	public void setAdapterNm(String adapterNm) {
		this.adapterNm = adapterNm;
	}

	public String getAdapterType() {
		return this.adapterType;
	}

	public void setAdapterType(String adapterType) {
		this.adapterType = adapterType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}