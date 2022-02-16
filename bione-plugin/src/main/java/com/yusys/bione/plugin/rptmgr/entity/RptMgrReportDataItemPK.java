package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;

/**
 * The primary key class for the RPT_MGR_REPORT_DATA_ITEM database table.
 * 
 */
@Embeddable
public class RptMgrReportDataItemPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	
	@Column(name="RPT_ITEM_ID")
	@ExcelColumn(index = "C", name = "报表项")
	private String rptItemId;

	@Column(name="RPT_ID")
	@ExcelColumn(index = "A", name = "报表")
	private String rptId;

    public RptMgrReportDataItemPK() {
    }
	public String getRptItemId() {
		return this.rptItemId;
	}
	public void setRptItemId(String rptItemId) {
		this.rptItemId = rptItemId;
	}
	public String getRptId() {
		return this.rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptMgrReportDataItemPK)) {
			return false;
		}
		RptMgrReportDataItemPK castOther = (RptMgrReportDataItemPK)other;
		return 
			this.rptItemId.equals(castOther.rptItemId)
			&& this.rptId.equals(castOther.rptId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rptItemId.hashCode();
		hash = hash * prime + this.rptId.hashCode();
		
		return hash;
    }
}