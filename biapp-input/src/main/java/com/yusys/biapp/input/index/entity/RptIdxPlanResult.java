package com.yusys.biapp.input.index.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "RPT_IDX_RESULT")
public class RptIdxPlanResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3610017525520293572L;

	@EmbeddedId
	private RptIdxPlanResultPK id;

	@Column(name = "INDEX_VAL")
	private BigDecimal indexVal;

	public RptIdxPlanResultPK getId() {
		return id;
	}

	public void setId(RptIdxPlanResultPK id) {
		this.id = id;
	}

	public BigDecimal getIndexVal() {
		return indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}

}
