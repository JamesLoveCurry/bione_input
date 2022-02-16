package com.yusys.biapp.input.inputTable.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_LIST_DATALOAD_DATA_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_LIST_DATALOAD_DATA_INFO")
public class RptListDataloadDataInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="DATA_CONTENT")
	private byte[] dataContent;

    public RptListDataloadDataInfo() {
    }

	public String getCfgId() {
		return this.cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public byte[] getDataContent() {
		return this.dataContent;
	}

	public void setDataContent(byte[] dataContent) {
		this.dataContent = dataContent;
	}

}