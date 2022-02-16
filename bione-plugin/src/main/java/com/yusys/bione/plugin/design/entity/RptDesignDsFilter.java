package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_DS_FILTER database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_DS_FILTER")
public class RptDesignDsFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignDsFilterPK id;

	@Column(name="COL_ID")
	private String colId;

	@Column(name="LOGIC_REL")
	private String logicRel;

	@Column(name="OPER_TYPE")
	private String operType;

    public RptDesignDsFilter() {
    }

	public RptDesignDsFilterPK getId() {
		return this.id;
	}

	public void setId(RptDesignDsFilterPK id) {
		this.id = id;
	}
	
	public String getColId() {
		return this.colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	public String getLogicRel() {
		return this.logicRel;
	}

	public void setLogicRel(String logicRel) {
		this.logicRel = logicRel;
	}

	public String getOperType() {
		return this.operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

}