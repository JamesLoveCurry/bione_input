package com.yusys.bione.plugin.idxplan.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_PLANVAL_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_PLANVAL_INFO")
@NamedQuery(name="RptIdxPlanvalInfo.findAll", query="SELECT r FROM RptIdxPlanvalInfo r")
public class RptIdxPlanvalInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxPlanvalInfoPK id;

	@Column(name="PLAN_VAL")
	private BigDecimal planVal;

	public RptIdxPlanvalInfo() {
	}

	public RptIdxPlanvalInfoPK getId() {
		return this.id;
	}

	public void setId(RptIdxPlanvalInfoPK id) {
		this.id = id;
	}

	public BigDecimal getPlanVal() {
		return this.planVal;
	}

	public void setPlanVal(BigDecimal planVal) {
		this.planVal = planVal;
	}

}