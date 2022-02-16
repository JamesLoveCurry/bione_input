package com.yusys.bione.plugin.rptbank.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_BANK_INFO database table.
 * 
 */

@Entity
@Table(name="RPT_IDX_BANK_INFO")
@NamedQuery(name="RptIdxBankInfo.findAll", query="SELECT r FROM RptIdxBankInfo r")
public class RptIdxBankInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxBankInfoPK id;

	private String currency;

	@Column(name="INDEX_LEVEL")
	private String indexLevel;

	@Column(name="INDEX_NM")
	private String indexNm;

	@Column(name="MAIN_NO")
	private String mainNo;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	@Column(name="PART_NO")
	private String partNo;

	private String remark;

	@Column(name="UP_NO")
	private String upNo;

	public RptIdxBankInfo() {
	}

	public RptIdxBankInfoPK getId() {
		return this.id;
	}

	public void setId(RptIdxBankInfoPK id) {
		this.id = id;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getIndexLevel() {
		return this.indexLevel;
	}

	public void setIndexLevel(String indexLevel) {
		this.indexLevel = indexLevel;
	}

	public String getIndexNm() {
		return this.indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getMainNo() {
		return this.mainNo;
	}

	public void setMainNo(String mainNo) {
		this.mainNo = mainNo;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getPartNo() {
		return this.partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpNo() {
		return this.upNo;
	}

	public void setUpNo(String upNo) {
		this.upNo = upNo;
	}

}