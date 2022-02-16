package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_CELL_VALIDATE database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_CELL_VALIDATE")
public class RptDesignCellValidate implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignCellValidatePK id;

    public RptDesignCellValidate() {
    }

	public RptDesignCellValidatePK getId() {
		return this.id;
	}

	public void setId(RptDesignCellValidatePK id) {
		this.id = id;
	}
	
}