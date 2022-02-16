package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the RPT_CABIN_IDX_FAVOUR database table.
 * 
 */
@Entity
@Table(name="RPT_CABIN_IDX_FAVOUR")
@NamedQuery(name="RptCabinIdxFavour.findAll", query="SELECT r FROM RptCabinIdxFavour r")
public class RptCabinIdxFavour implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptCabinIdxFavourPK id;

	@Temporal(TemporalType.DATE)
	@Column(name="FAVOUR_DATE")
	private Date favourDate;

	@Column(name="INDEX_DIM")
	private String indexDim;

	@Column(name="INDEX_NM")
	private String indexNm;

	@Column(name="INDEX_TYPE")
	private String indexType;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	@Column(name="INDEX_CURRENCY")
	private String indexCurrency;
	
	public RptCabinIdxFavour() {
	}

	public RptCabinIdxFavourPK getId() {
		return this.id;
	}

	public void setId(RptCabinIdxFavourPK id) {
		this.id = id;
	}

	public Date getFavourDate() {
		return this.favourDate;
	}

	public void setFavourDate(Date favourDate) {
		this.favourDate = favourDate;
	}

	public String getIndexDim() {
		return this.indexDim;
	}

	public void setIndexDim(String indexDim) {
		this.indexDim = indexDim;
	}

	public String getIndexNm() {
		return this.indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getIndexType() {
		return this.indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getIndexCurrency() {
		return indexCurrency;
	}

	public void setIndexCurrency(String indexCurrency) {
		this.indexCurrency = indexCurrency;
	}
}