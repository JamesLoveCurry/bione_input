package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;


/**
 * The persistent class for the RPT_MGR_REPORT_DATA_ITEM database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_REPORT_DATA_ITEM")
public class RptMgrReportDataItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrReportDataItemPK id;

	@Column(name="RELATE_TYPE")
	private String relateType;

	@Column(name="RPT_ITEM_DESC")
	@ExcelColumn(index = "E", name = "报表项描述")
	private String rptItemDesc;

	@Column(name="RPT_ITEM_NM")
	@ExcelColumn(index = "D", name = "报表项名称")
	private String rptItemNm;

	@Column(name="RPT_ITEM_TYPE")
	private String rptItemType;

	@Column(name="ORDER_NO")
	private BigDecimal orderNo;
	
    public RptMgrReportDataItem() {
    }

	public RptMgrReportDataItemPK getId() {
		return this.id;
	}

	public void setId(RptMgrReportDataItemPK id) {
		this.id = id;
	}
	
	public String getRelateType() {
		return this.relateType;
	}

	public void setRelateType(String relateType) {
		this.relateType = relateType;
	}

	public String getRptItemDesc() {
		return this.rptItemDesc;
	}

	public void setRptItemDesc(String rptItemDesc) {
		this.rptItemDesc = rptItemDesc;
	}

	public String getRptItemNm() {
		return this.rptItemNm;
	}

	public void setRptItemNm(String rptItemNm) {
		this.rptItemNm = rptItemNm;
	}

	public String getRptItemType() {
		return this.rptItemType;
	}

	public void setRptItemType(String rptItemType) {
		this.rptItemType = rptItemType;
	}

	public BigDecimal getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(BigDecimal orderNo) {
		this.orderNo = orderNo;
	}

}