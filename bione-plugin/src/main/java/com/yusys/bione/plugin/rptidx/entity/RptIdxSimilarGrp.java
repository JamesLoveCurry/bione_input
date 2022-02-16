package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_SIMILAR_GRP database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_SIMILAR_GRP")
@NamedQuery(name="RptIdxSimilarGrp.findAll", query="SELECT r FROM RptIdxSimilarGrp r")
public class RptIdxSimilarGrp implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxSimilarGrpPK id;

	public RptIdxSimilarGrp() {
	}

	public RptIdxSimilarGrpPK getId() {
		return this.id;
	}

	public void setId(RptIdxSimilarGrpPK id) {
		this.id = id;
	}

}