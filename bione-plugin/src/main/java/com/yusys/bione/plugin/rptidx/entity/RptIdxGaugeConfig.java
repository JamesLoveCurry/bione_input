package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_GAUGE_CONFIG database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_GAUGE_CONFIG")
public class RptIdxGaugeConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxGaugeConfigPK id;

	@Column(name="ORDER_NUM")
	private long orderNum;

	public RptIdxGaugeConfig() {
	}

	public RptIdxGaugeConfigPK getId() {
		return this.id;
	}

	public void setId(RptIdxGaugeConfigPK id) {
		this.id = id;
	}

	public long getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(long orderNum) {
		this.orderNum = orderNum;
	}

}