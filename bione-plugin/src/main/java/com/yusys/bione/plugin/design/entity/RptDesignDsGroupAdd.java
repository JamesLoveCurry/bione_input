package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_DS_GROUP_ADD database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_DS_GROUP_ADD")
public class RptDesignDsGroupAdd implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignDsGroupAddPK id;

	@Column(name="APPEND_END_COL")
	private BigDecimal appendEndCol;

	@Column(name="APPEND_END_ROW")
	private BigDecimal appendEndRow;

	@Column(name="APPEND_START_COL")
	private BigDecimal appendStartCol;

	@Column(name="APPEND_START_ROW")
	private BigDecimal appendStartRow;

	@Column(name="END_COL_OFFSET")
	private BigDecimal endColOffset;

	@Column(name="END_ROW_OFFSET")
	private BigDecimal endRowOffset;

	@Column(name="START_COL_OFFSET")
	private BigDecimal startColOffset;

	@Column(name="START_ROW_OFFSET")
	private BigDecimal startRowOffset;

    public RptDesignDsGroupAdd() {
    }

	public RptDesignDsGroupAddPK getId() {
		return this.id;
	}

	public void setId(RptDesignDsGroupAddPK id) {
		this.id = id;
	}
	
	public BigDecimal getAppendEndCol() {
		return this.appendEndCol;
	}

	public void setAppendEndCol(BigDecimal appendEndCol) {
		this.appendEndCol = appendEndCol;
	}

	public BigDecimal getAppendEndRow() {
		return this.appendEndRow;
	}

	public void setAppendEndRow(BigDecimal appendEndRow) {
		this.appendEndRow = appendEndRow;
	}

	public BigDecimal getAppendStartCol() {
		return this.appendStartCol;
	}

	public void setAppendStartCol(BigDecimal appendStartCol) {
		this.appendStartCol = appendStartCol;
	}

	public BigDecimal getAppendStartRow() {
		return this.appendStartRow;
	}

	public void setAppendStartRow(BigDecimal appendStartRow) {
		this.appendStartRow = appendStartRow;
	}

	public BigDecimal getEndColOffset() {
		return this.endColOffset;
	}

	public void setEndColOffset(BigDecimal endColOffset) {
		this.endColOffset = endColOffset;
	}

	public BigDecimal getEndRowOffset() {
		return this.endRowOffset;
	}

	public void setEndRowOffset(BigDecimal endRowOffset) {
		this.endRowOffset = endRowOffset;
	}

	public BigDecimal getStartColOffset() {
		return this.startColOffset;
	}

	public void setStartColOffset(BigDecimal startColOffset) {
		this.startColOffset = startColOffset;
	}

	public BigDecimal getStartRowOffset() {
		return this.startRowOffset;
	}

	public void setStartRowOffset(BigDecimal startRowOffset) {
		this.startRowOffset = startRowOffset;
	}

}