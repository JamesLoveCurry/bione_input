package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_CELLLINE_REL database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_CELLLINE_REL")
public class RptDesignCelllineRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignCelllineRelPK id;

    public RptDesignCelllineRel() {
    }

	public RptDesignCelllineRelPK getId() {
		return this.id;
	}

	public void setId(RptDesignCelllineRelPK id) {
		this.id = id;
	}
	
}