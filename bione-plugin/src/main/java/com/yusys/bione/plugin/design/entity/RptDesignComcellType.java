package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_COMCELL_TYPE database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_COMCELL_TYPE")
@NamedQuery(name="RptDesignComcellType.findAll", query="SELECT r FROM RptDesignComcellType r")
public class RptDesignComcellType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TYPE_ID")
	private String typeId;

	@Column(name="TYPE_NM")
	private String typeNm;
	
	@Column(name="SORT_ORDER")
	private String sortOrder;

	public RptDesignComcellType() {
	}

	public String getTypeId() {
		return this.typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeNm() {
		return this.typeNm;
	}

	public void setTypeNm(String typeNm) {
		this.typeNm = typeNm;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	
}