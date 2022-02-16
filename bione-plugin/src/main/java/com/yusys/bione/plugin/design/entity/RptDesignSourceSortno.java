package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_SOURCE_SORTNO database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_SOURCE_SORTNO")
public class RptDesignSourceSortno implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignSourceSortnoPK id;

	@Column(name="IS_GRP_SORT")
	private String isGrpSort;

	@Column(name="RELATE_CELL_NO")
	private String relateCellNo;

    public RptDesignSourceSortno() {
    }

	public RptDesignSourceSortnoPK getId() {
		return this.id;
	}

	public void setId(RptDesignSourceSortnoPK id) {
		this.id = id;
	}
	
	public String getIsGrpSort() {
		return this.isGrpSort;
	}

	public void setIsGrpSort(String isGrpSort) {
		this.isGrpSort = isGrpSort;
	}

	public String getRelateCellNo() {
		return relateCellNo;
	}

	public void setRelateCellNo(String relateCellNo) {
		this.relateCellNo = relateCellNo;
	}

}