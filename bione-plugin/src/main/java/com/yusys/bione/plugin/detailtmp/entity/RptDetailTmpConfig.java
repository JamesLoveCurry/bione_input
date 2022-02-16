package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DETAIL_TMP_CONFIG database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_TMP_CONFIG")
@NamedQuery(name="RptDetailTmpConfig.findAll", query="SELECT r FROM RptDetailTmpConfig r")
public class RptDetailTmpConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDetailTmpConfigPK id;

	@Column(name="COL_ID")
	private String colId;

	@Column(name="DATA_PRECISION")
	private BigDecimal dataPrecision;

	@Column(name="DATA_TYPE")
	private String dataType;

	@Column(name="DATA_UNIT")
	private String dataUnit;

	@Column(name="DIM_TYPE_NO")
	private String dimTypeNo;

	@Column(name="DISPLAY_FORMAT")
	private String displayFormat;

	@Column(name="DISPLAY_NM")
	private String displayNm;

	@Column(name="IS_CONVER")
	private String isConver;

	@Column(name="IS_SUM")
	private String isSum;

	private BigDecimal orderno;

	@Column(name="SET_ID")
	private String setId;

	private BigDecimal width;
	
	@Column(name="IS_DES")
	private String isDes;
	
	@Column(name="DICT_ID")
	private String dictId;

	public RptDetailTmpConfig() {
	}

	public RptDetailTmpConfigPK getId() {
		return this.id;
	}

	public void setId(RptDetailTmpConfigPK id) {
		this.id = id;
	}

	public String getColId() {
		return this.colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	public BigDecimal getDataPrecision() {
		return this.dataPrecision;
	}

	public void setDataPrecision(BigDecimal dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataUnit() {
		return this.dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getDimTypeNo() {
		return this.dimTypeNo;
	}

	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public String getDisplayFormat() {
		return this.displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	public String getDisplayNm() {
		return this.displayNm;
	}

	public void setDisplayNm(String displayNm) {
		this.displayNm = displayNm;
	}

	public String getIsConver() {
		return this.isConver;
	}

	public void setIsConver(String isConver) {
		this.isConver = isConver;
	}

	public String getIsSum() {
		return this.isSum;
	}

	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}

	public BigDecimal getOrderno() {
		return this.orderno;
	}

	public void setOrderno(BigDecimal orderno) {
		this.orderno = orderno;
	}

	public String getSetId() {
		return this.setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public BigDecimal getWidth() {
		return this.width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public String getIsDes() {
		return isDes;
	}

	public void setIsDes(String isDes) {
		this.isDes = isDes;
	}

	public String getDictId() {
		return dictId;
	}

	public void setDictId(String dictId) {
		this.dictId = dictId;
	}
	
	
}