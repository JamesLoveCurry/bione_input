package com.yusys.bione.plugin.base.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_CABIN_ORG_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_CABIN_ORG_INFO")
@NamedQuery(name="RptCabinOrgInfo.findAll", query="SELECT r FROM RptCabinOrgInfo r")
public class RptCabinOrgInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ORG_NO")
	private String orgNo;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	@Column(name="ORG_NM")
	private String orgNm;

	@Column(name="ORG_TYPE")
	private String orgType;

	public RptCabinOrgInfo() {
	}

	public String getOrgNo() {
		return this.orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getOrgNm() {
		return this.orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public String getOrgType() {
		return this.orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

}