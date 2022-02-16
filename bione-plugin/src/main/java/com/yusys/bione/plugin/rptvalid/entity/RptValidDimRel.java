package com.yusys.bione.plugin.rptvalid.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_DIM_REL database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_DIM_REL")
@NamedQuery(name="RptValidDimRel.findAll", query="SELECT r FROM RptValidDimRel r")
public class RptValidDimRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidDimRelPK id;

	@Column(name="DIM_TYPE")
	private String dimType;

	@Column(name="STORE_COL")
	private String storeCol;

	public RptValidDimRel() {
	}

	public RptValidDimRelPK getId() {
		return this.id;
	}

	public void setId(RptValidDimRelPK id) {
		this.id = id;
	}

	public String getDimType() {
		return this.dimType;
	}

	public void setDimType(String dimType) {
		this.dimType = dimType;
	}

	public String getStoreCol() {
		return this.storeCol;
	}

	public void setStoreCol(String storeCol) {
		this.storeCol = storeCol;
	}

}