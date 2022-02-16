package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_MEASURE_REL database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_MEASURE_REL")
public class RptIdxMeasureRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxMeasureRelPK id;

	@Column(name="ORDER_NUM", precision=5)
	private BigDecimal orderNum;

	@Column(name="STORE_COL", nullable=false, length=100)
	private String storeCol;

    public RptIdxMeasureRel() {
    }

	public RptIdxMeasureRelPK getId() {
		return this.id;
	}

	public void setId(RptIdxMeasureRelPK id) {
		this.id = id;
	}
	
	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getStoreCol() {
		return this.storeCol;
	}

	public void setStoreCol(String storeCol) {
		this.storeCol = storeCol;
	}

}