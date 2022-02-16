package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DETAIL_TMP_SORT database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_TMP_SORT")
@NamedQuery(name="RptDetailTmpSort.findAll", query="SELECT r FROM RptDetailTmpSort r")
public class RptDetailTmpSort implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDetailTmpSortPK id;

	@Column(name="SORT_MODE")
	private String sortMode;

	@Column(name="SORT_ORDERNO")
	private BigDecimal sortOrderno;

	public RptDetailTmpSort() {
	}

	public RptDetailTmpSortPK getId() {
		return this.id;
	}

	public void setId(RptDetailTmpSortPK id) {
		this.id = id;
	}

	public String getSortMode() {
		return this.sortMode;
	}

	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}

	public BigDecimal getSortOrderno() {
		return this.sortOrderno;
	}

	public void setSortOrderno(BigDecimal sortOrderno) {
		this.sortOrderno = sortOrderno;
	}

}