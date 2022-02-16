package com.yusys.biapp.input.inputTable.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_LIST_DATALOAD_SQL_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_LIST_DATALOAD_SQL_INFO")
public class RptListDataloadSqlInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="SQL2")
	private String sql2;
	
	@Column(name="DS_ID")
    private String dsId;

    public RptListDataloadSqlInfo() {
    }

	public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getCfgId() {
		return this.cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getSql2() {
		return sql2;
	}

	public void setSql2(String sql2) {
		this.sql2 = sql2;
	}

}