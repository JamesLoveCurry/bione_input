package com.yusys.biapp.input.index.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="RPT_IDX_RESULT")
public class RptIdxResult  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@EmbeddedId
	private RptIdxResultPK id;
	

	@Column(name="INDEX_VAL")
	private BigDecimal  indexVal;


	public RptIdxResultPK getId() {
		return id;
	}


	public void setId(RptIdxResultPK id) {
		this.id = id;
	}


	public BigDecimal getIndexVal() {
		return indexVal;
	}


	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}
	
	
	
}
