package com.yusys.bione.plugin.businessline.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_FRS_LINE database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_BUSI_LINE")
public class RptMgrBusiLine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="LINE_ID")
	private String lineId;

	@Column(name="LINE_NM")
	private String lineNm;
	
	@Column(name="RANK_ORDER")
	private BigDecimal rankOrder;

    public RptMgrBusiLine() {
    }

	public String getLineId() {
		return this.lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getLineNm() {
		return this.lineNm;
	}

	public void setLineNm(String lineNm) {
		this.lineNm = lineNm;
	}

	public BigDecimal getRankOrder() {
		return rankOrder;
	}

	public void setRankOrder(BigDecimal rankOrder) {
		this.rankOrder = rankOrder;
	}

}