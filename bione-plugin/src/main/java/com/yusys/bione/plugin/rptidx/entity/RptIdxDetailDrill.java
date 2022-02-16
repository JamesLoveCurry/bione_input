package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_SRC_REL_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_DETAIL_DRILL")
@NamedQuery(name="RptIdxDetailDrill.findAll", query="SELECT r FROM RptIdxDetailDrill r")
public class RptIdxDetailDrill implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxDetailDrillPK id;

	@Column(name="DRILL_SQL")
	private String drillSql;

	public RptIdxDetailDrill() {
	}

	public RptIdxDetailDrillPK getId() {
		return id;
	}

	public void setId(RptIdxDetailDrillPK id) {
		this.id = id;
	}

	public String getDrillSql() {
		return drillSql;
	}

	public void setDrillSql(String drillSql) {
		this.drillSql = drillSql;
	}
}