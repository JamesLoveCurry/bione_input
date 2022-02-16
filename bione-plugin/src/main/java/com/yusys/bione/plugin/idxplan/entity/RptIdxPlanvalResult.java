package com.yusys.bione.plugin.idxplan.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "RPT_IDX_PLAN_RESULT")
public class RptIdxPlanvalResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3610017525520293572L;

	@EmbeddedId
	private RptIdxPlanvalResultPK id;

	@Column(name = "INDEX_VAL")
	private BigDecimal indexVal;

	public RptIdxPlanvalResultPK getId() {
		return id;
	}

	public void setId(RptIdxPlanvalResultPK id) {
		this.id = id;
	}

	public BigDecimal getIndexVal() {
		return indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}

}
