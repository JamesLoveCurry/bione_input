package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_RESULT_WARN database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_RESULT_WARN")
public class RptValidResultWarn implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidResultWarnPK id;

	@Column(name="CHECK_TIME")
	private Timestamp checkTime;

	@Column(name="COMPARE_VAL", precision=20, scale=5)
	private BigDecimal compareVal;

	@Column(name="CURR_VAL", precision=20, scale=5)
	private BigDecimal currVal;

	@Column(name="IS_PASS", length=1)
	private String isPass;

	@Column(name="LEVEL_NUM", length=32)
	private String levelNum;

	@Column(name="RPT_TEMPLATE_ID", length=32)
	private String rptTemplateId;
	
	@Column(name="UNIT", length=10)
	private String unit;
	
	@Column(name="INDEX_NO", length=32)
	private String indexNo;
	
    public RptValidResultWarn() {
    }

	public RptValidResultWarnPK getId() {
		return this.id;
	}

	public void setId(RptValidResultWarnPK id) {
		this.id = id;
	}
	
	public Timestamp getCheckTime() {
		return this.checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}

	public BigDecimal getCompareVal() {
		return this.compareVal;
	}

	public void setCompareVal(BigDecimal compareVal) {
		this.compareVal = compareVal;
	}

	public BigDecimal getCurrVal() {
		return this.currVal;
	}

	public void setCurrVal(BigDecimal currVal) {
		this.currVal = currVal;
	}

	public String getIsPass() {
		return this.isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

	public String getLevelNum() {
		return this.levelNum;
	}

	public void setLevelNum(String levelNum) {
		this.levelNum = levelNum;
	}

	
	public String getRptTemplateId() {
		return rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

}