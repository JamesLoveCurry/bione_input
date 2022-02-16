package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_CELL_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_CELL_INFO")
public class RptDesignCellInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignCellInfoPK id;

	@Column(name="CALIBER_EXPLAIN")
	private String caliberExplain;

	@Column(name="CALIBER_TECHNOLOGY")
	private String caliberTechnology;
	
	@Column(name="CELL_DATA_SRC")
	private String cellDataSrc;

	@Column(name="CELL_NM")
	private String cellNm;

	@Column(name="CELL_SUFFIX")
	private String cellSuffix;

	@Column(name="COL_ID")
	private BigDecimal colId;

	@Column(name="DATA_LEN")
	private BigDecimal dataLen;

	@Column(name="DATA_PRECISION")
	private BigDecimal dataPrecision;

	@Column(name="DATA_TYPE")
	private String dataType;

	@Column(name="DISPLAY_FORMAT")
	private String displayFormat;

	@Column(name="IS_NULL")
	private String isNull;

	@Column(name="IS_UPT")
	private String isUpt;

	private String remark;

	@Column(name="ROW_ID")
	private BigDecimal rowId;
	
	@Column(name="DATA_UNIT")
	private String dataUnit;
	
	@Column(name="BUSI_NO")
	private String busiNo;
	
	@Column(name="SORT_MODE")
	private String sortMode;
	
	@Column(name="IS_MERGE")
	private String isMerge;
	
	@Column(name="IS_MERGE_COL")
	private String isMergeCol;

    public RptDesignCellInfo() {
    }
    @Column(name="IS_LOCK")
	private String isLock;

	public String getIsMergeCol() {
		return isMergeCol;
	}


	public void setIsMergeCol(String isMergeCol) {
		this.isMergeCol = isMergeCol;
	}
	
	public String getIsMerge() {
		return isMerge;
	}


	public void setIsMerge(String isMerge) {
		this.isMerge = isMerge;
	}


	public RptDesignCellInfoPK getId() {
		return this.id;
	}

	public void setId(RptDesignCellInfoPK id) {
		this.id = id;
	}
	
	public String getCaliberExplain() {
		return this.caliberExplain;
	}

	public void setCaliberExplain(String caliberExplain) {
		this.caliberExplain = caliberExplain;
	}

	public String getCellDataSrc() {
		return this.cellDataSrc;
	}

	public void setCellDataSrc(String cellDataSrc) {
		this.cellDataSrc = cellDataSrc;
	}

	public String getCellNm() {
		return this.cellNm;
	}

	public void setCellNm(String cellNm) {
		this.cellNm = cellNm;
	}

	public String getCellSuffix() {
		return this.cellSuffix;
	}

	public void setCellSuffix(String cellSuffix) {
		this.cellSuffix = cellSuffix;
	}

	public BigDecimal getColId() {
		return this.colId;
	}

	public void setColId(BigDecimal colId) {
		this.colId = colId;
	}

	public BigDecimal getDataLen() {
		return this.dataLen;
	}

	public void setDataLen(BigDecimal dataLen) {
		this.dataLen = dataLen;
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

	public String getDisplayFormat() {
		return this.displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	public String getIsNull() {
		return this.isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getIsUpt() {
		return this.isUpt;
	}

	public void setIsUpt(String isUpt) {
		this.isUpt = isUpt;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getRowId() {
		return this.rowId;
	}

	public void setRowId(BigDecimal rowId) {
		this.rowId = rowId;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getBusiNo() {
		return busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public String getSortMode() {
		return sortMode;
	}

	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}

	public String getCaliberTechnology() {
		return caliberTechnology;
	}

	public void setCaliberTechnology(String caliberTechnology) {
		this.caliberTechnology = caliberTechnology;
	}

	public String getIsLock() {
		return isLock;
	}

	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}
}