package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_COMP_GRP database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_COMP_GRP")
@NamedQuery(name="RptIdxCompGrp.findAll", query="SELECT r FROM RptIdxCompGrp r")
public class RptIdxCompGrp  implements Serializable{
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxCompGrpPK id;

	public RptIdxCompGrp() {
	}

	public RptIdxCompGrpPK getId() {
		return this.id;
	}

	public void setId(RptIdxCompGrpPK id) {
		this.id = id;
	}

}