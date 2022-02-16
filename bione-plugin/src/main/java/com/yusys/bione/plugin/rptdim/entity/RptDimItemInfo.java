package com.yusys.bione.plugin.rptdim.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_DIM_ITEM_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_DIM_ITEM_INFO")
public class RptDimItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDimItemInfoPK id;

	@Column(name = "DIM_ITEM_NM")
	private String dimItemNm;
	
	@Column(name = "REMARK")
	private String remark;

	@Column(name = "UP_NO")
	private String upNo;

	@Column(name = "START_DATE", length = 8)
	private String startDate;

	@Column(name = "END_DATE", length = 8)
	private String endDate;
	
	@Column(name = "BUSI_DEF")
	private String busiDef;

	@Column(name = "BUSI_RULE")
	private String busiRule;

	@Column(name="RANK_ORDER")
	private BigDecimal rankOrder;
	
	@Column(name = "DEF_DEPT", length = 500)
	private String defDept;

	@Column(name = "USE_DEPT", length = 500)
	private String useDept;

	@Column(name = "ITEM_STS")
	private String itemSts;

	public RptDimItemInfo() {
	}

	public String getDimItemNm() {
		return this.dimItemNm;
	}

	public void setDimItemNm(String dimItemNm) {
		this.dimItemNm = dimItemNm;
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

	public RptDimItemInfoPK getId() {
		return id;
	}

	public void setId(RptDimItemInfoPK id) {
		this.id = id;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getBusiDef() {
		return busiDef;
	}

	public void setBusiDef(String busiDef) {
		this.busiDef = busiDef;
	}

	public String getBusiRule() {
		return busiRule;
	}

	public void setBusiRule(String busiRule) {
		this.busiRule = busiRule;
	}

	public String getDefDept() {
		return defDept;
	}

	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}

	public String getUseDept() {
		return useDept;
	}

	public void setUseDept(String useDept) {
		this.useDept = useDept;
	}

	public String getItemSts() {
		return itemSts;
	}

	public void setItemSts(String itemSts) {
		this.itemSts = itemSts;
	}
	
	public void setRankOrder(BigDecimal rankOrder) {
		this.rankOrder = rankOrder;
	}
	
	public BigDecimal getRankOrder() {
		return rankOrder;
	}

}