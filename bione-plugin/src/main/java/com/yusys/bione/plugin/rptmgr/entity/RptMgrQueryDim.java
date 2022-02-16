package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_QUERY_DIM database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_QUERY_DIM")
public class RptMgrQueryDim implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrQueryDimPK id;

	@Column(name="DIM_NO")
	private String dimNo;

    public RptMgrQueryDim() {
    }

	public RptMgrQueryDimPK getId() {
		return this.id;
	}

	public void setId(RptMgrQueryDimPK id) {
		this.id = id;
	}
	
	public String getDimNo() {
		return this.dimNo;
	}

	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}

}