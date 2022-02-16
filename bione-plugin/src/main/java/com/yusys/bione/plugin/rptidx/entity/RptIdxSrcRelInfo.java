package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_SRC_REL_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_SRC_REL_INFO")
@NamedQuery(name="RptIdxSrcRelInfo.findAll", query="SELECT r FROM RptIdxSrcRelInfo r")
public class RptIdxSrcRelInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxSrcRelInfoPK id;

	

	public RptIdxSrcRelInfo() {
	}

	public RptIdxSrcRelInfoPK getId() {
		return this.id;
	}

	public void setId(RptIdxSrcRelInfoPK id) {
		this.id = id;
	}

	

}