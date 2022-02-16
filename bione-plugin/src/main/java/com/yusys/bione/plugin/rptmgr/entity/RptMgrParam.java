package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_PARAM database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_PARAM")
public class RptMgrParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrParamPK id;

	@Column(name="PARAM_VAL")
	private String paramVal;

    public RptMgrParam() {
    }

	public RptMgrParamPK getId() {
		return this.id;
	}

	public void setId(RptMgrParamPK id) {
		this.id = id;
	}
	
	public String getParamVal() {
		return this.paramVal;
	}

	public void setParamVal(String paramVal) {
		this.paramVal = paramVal;
	}

}