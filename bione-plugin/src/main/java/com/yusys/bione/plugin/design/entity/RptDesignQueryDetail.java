package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_QUERY_DETAIL database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_QUERY_DETAIL")
public class RptDesignQueryDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignQueryDetailPK id;

	@Column(name="COLUMN_ID")
	private String columnId;

	@Column(name="DS_ID")
	private String dsId;

	@Column(name="ELEMENT_TYPE")
	private String elementType;

    public RptDesignQueryDetail() {
    }

	public RptDesignQueryDetailPK getId() {
		return this.id;
	}

	public void setId(RptDesignQueryDetailPK id) {
		this.id = id;
	}
	
	public String getColumnId() {
		return this.columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getDsId() {
		return this.dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getElementType() {
		return this.elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

}