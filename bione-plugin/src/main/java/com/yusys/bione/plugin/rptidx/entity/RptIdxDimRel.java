package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_DIM_REL database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_DIM_REL")
public class RptIdxDimRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxDimRelPK id;

	@Column(name="DIM_TYPE", length=20)
	private String dimType;

	@Column(name="ORDER_NUM", precision=22)
	private BigDecimal orderNum;

	@Column(name="STORE_COL", length=20)
	private String storeCol;

    public RptIdxDimRel() {
    }

	public RptIdxDimRelPK getId() {
		return this.id;
	}

	public void setId(RptIdxDimRelPK id) {
		this.id = id;
	}
	
	public String getDimType() {
		return this.dimType;
	}

	public void setDimType(String dimType) {
		this.dimType = dimType;
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