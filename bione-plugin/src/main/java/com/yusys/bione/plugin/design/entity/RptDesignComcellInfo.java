package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_COMCELL_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_COMCELL_INFO")
@NamedQuery(name="RptDesignComcellInfo.findAll", query="SELECT r FROM RptDesignComcellInfo r")
public class RptDesignComcellInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignComcellInfoPK id;

	@Column(name="TYPE_ID")
	private String typeId;
	
	@Column(name="COL_ID")
	private BigDecimal colId;

	@Column(name="ROW_ID")
	private BigDecimal rowId;
	
	private String content;

	public RptDesignComcellInfo() {
	}

	public RptDesignComcellInfoPK getId() {
		return this.id;
	}

	public void setId(RptDesignComcellInfoPK id) {
		this.id = id;
	}

	public BigDecimal getColId() {
		return colId;
	}

	public void setColId(BigDecimal colId) {
		this.colId = colId;
	}

	public BigDecimal getRowId() {
		return rowId;
	}

	public void setRowId(BigDecimal rowId) {
		this.rowId = rowId;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
}