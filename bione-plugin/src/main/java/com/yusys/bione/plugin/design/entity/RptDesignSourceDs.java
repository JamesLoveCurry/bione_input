package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_SOURCE_DS database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_SOURCE_DS")
public class RptDesignSourceDs implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignSourceDsPK id;

	@Column(name="COLUMN_ID")
	private String columnId;

	@Column(name="DS_ID")
	private String dsId;

	@Column(name="EXT_DIRECTION")
	private String extDirection;

	@Column(name="EXT_MODE")
	private String extMode;

	@Column(name="IS_EXT")
	private String isExt;

	@Column(name="IS_FILT")
	private String isFilt;

	@Column(name="IS_GRP")
	private String isGrp;

	@Column(name="SUM_MODE")
	private String sumMode;
	
	@Column(name="IS_SORT")
	private String isSort;
	
	@Column(name="SORT_MODE")
	private String sortMode;
	
	@Column(name="SORT_ORDER")
	private BigDecimal sortOrder;
	
	@Column(name="IS_CONVER")
	private String isConver;

    public RptDesignSourceDs() {
    }

	public RptDesignSourceDsPK getId() {
		return this.id;
	}

	public void setId(RptDesignSourceDsPK id) {
		this.id = id;
	}
	
	public String getDsId() {
		return this.dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getExtDirection() {
		return this.extDirection;
	}

	public void setExtDirection(String extDirection) {
		this.extDirection = extDirection;
	}

	public String getExtMode() {
		return this.extMode;
	}

	public void setExtMode(String extMode) {
		this.extMode = extMode;
	}

	public String getIsExt() {
		return this.isExt;
	}

	public void setIsExt(String isExt) {
		this.isExt = isExt;
	}

	public String getIsFilt() {
		return this.isFilt;
	}

	public void setIsFilt(String isFilt) {
		this.isFilt = isFilt;
	}

	public String getIsGrp() {
		return this.isGrp;
	}

	public void setIsGrp(String isGrp) {
		this.isGrp = isGrp;
	}

	public String getSumMode() {
		return this.sumMode;
	}

	public void setSumMode(String sumMode) {
		this.sumMode = sumMode;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getIsSort() {
		return isSort;
	}

	public void setIsSort(String isSort) {
		this.isSort = isSort;
	}

	public String getSortMode() {
		return sortMode;
	}

	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}

	public BigDecimal getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(BigDecimal sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getIsConver() {
		return isConver;
	}

	public void setIsConver(String isConver) {
		this.isConver = isConver;
	}


}