package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_design_source_tabdim database table.
 * 
 */
@Entity
@Table(name="rpt_design_source_tabdim")
public class RptDesignSourceTabdim implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignSourceTabdimPK id;

	@Column(name="DIM_TYPE_NO")
	private String dimTypeNo;
	
	@Column(name="IS_TOTAL")
	private String isTotal;
	
	@Column(name="DATE_FORMAT")
	private String dateFormat;
	
	@Column(name="IS_CONVER")
	private String isConver;
	
	@Column(name="DISPLAY_LEVEL")
	private String displayLevel;
	
	@Column(name="EXT_DIRECTION")
	private String extDirection;

    public RptDesignSourceTabdim() {
    }

	public RptDesignSourceTabdimPK getId() {
		return this.id;
	}

	public void setId(RptDesignSourceTabdimPK id) {
		this.id = id;
	}
	
	public String getDimTypeNo() {
		return this.dimTypeNo;
	}

	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public String getIsTotal() {
		return isTotal;
	}

	public void setIsTotal(String isTotal) {
		this.isTotal = isTotal;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getIsConver() {
		return isConver;
	}

	public void setIsConver(String isConver) {
		this.isConver = isConver;
	}

	public String getDisplayLevel() {
		return displayLevel;
	}

	public void setDisplayLevel(String displayLevel) {
		this.displayLevel = displayLevel;
	}

	public String getExtDirection() {
		return extDirection;
	}

	public void setExtDirection(String extDirection) {
		this.extDirection = extDirection;
	}

}