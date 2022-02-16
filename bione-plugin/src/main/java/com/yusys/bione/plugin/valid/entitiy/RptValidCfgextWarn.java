package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_CFGEXT_WARN database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_CFGEXT_WARN")
public class RptValidCfgextWarn implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CHECK_ID")
	private String checkId;
	
	@Column(name="INDEX_NO")
	private String indexNo;
	
	@Column(name="RPT_TEMPLATE_ID")
	private String rptTemplateId;

	@Column(name="COMPARE_VAL_TYPE")
	private String compareValType;

	@Column(name="RANGE_TYPE")
	private String rangeType;
	
	@Column(name="CHECK_NM")
	private String checkNm;
	
	@Column(name="START_DATE")
	private String startDate;
	
	@Column(name="END_DATE")
	private String endDate;
	
	@Column(name="REMARK")
	private String remark;
	
	@Column(name="UNIT")
	private String unit;
	
	@Column(name="COMPARE_TYPE")
	private String compareType;
	
	@Column(name="IS_FRS")
	private String IsFrs;
	
    public String getCompareType() {
		return compareType;
	}

	public void setCompareType(String compareType) {
		this.compareType = compareType;
	}

	public RptValidCfgextWarn() {
    }

	public String getCheckId() {
		return checkId;
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

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}


	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getCompareValType() {
		return this.compareValType;
	}

	public void setCompareValType(String compareValType) {
		this.compareValType = compareValType;
	}

	public String getRangeType() {
		return this.rangeType;
	}
	public String getCheckNm() {
		return checkNm;
	}

	public void setCheckNm(String checkNm) {
		this.checkNm = checkNm;
	}
	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRptTemplateId() {
		return rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getIsFrs() {
		return IsFrs;
	}

	public void setIsFrs(String isFrs) {
		IsFrs = isFrs;
	}

}