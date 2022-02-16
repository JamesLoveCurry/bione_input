package com.yusys.bione.plugin.draw.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the PECULIAR_IDX_DRAW_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_DRAW_INFO")
@NamedQuery(name="RptIdxDrawInfo.findAll", query="SELECT p FROM RptIdxDrawInfo p")
public class RptIdxDrawInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="DRAW_DATE")
	private String drawDate;

	public RptIdxDrawInfo() {
	}

	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getDrawDate() {
		return this.drawDate;
	}

	public void setDrawDate(String drawDate) {
		this.drawDate = drawDate;
	}

}