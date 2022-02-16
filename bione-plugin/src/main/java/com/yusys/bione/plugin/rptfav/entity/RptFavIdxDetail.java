package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_FAV_IDX_DETAIL database table.
 * 
 */
@Entity
@Table(name="RPT_FAV_IDX_DETAIL")
public class RptFavIdxDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DETAIL_ID", unique=true, nullable=false, length=32)
	private String detailId;

	@Column(name="INSTANCE_ID", length=32)
	private String instanceId;
	
	@Column(name="INDEX_ALIAS", length=100)
	private String indexAlias;

	@Column(name="INDEX_NO", nullable=false, length=32)
	private String indexNo;
	
	@Column(name="MEASURE_NO", length=32)
	private String measureNo;

	@Column(name="ORDER_NUM", precision=5)
	private BigDecimal orderNum;
	
	@Column(name="DATA_TYPE")
	private String dataType;
	
	@Column(name="DATA_UNIT")
	private String dataUnit;
	
	@Column(name="DATA_PRECISION")
	private BigDecimal dataPrecision;
	
	@Column(name="RULE_ID")
	private String ruleId;
	
	@Column(name="TIME_MEASURE_ID")
	private String timeMeasureId;
	
	@Column(name="MODE_ID")
	private String modeId;
	
	@Column(name="IS_PASSYEAR")
	private String isPassyear;
	
    public RptFavIdxDetail() {
    }

	public String getDetailId() {
		return this.detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getIndexAlias() {
		return this.indexAlias;
	}

	public void setIndexAlias(String indexAlias) {
		this.indexAlias = indexAlias;
	}

	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getMeasureNo() {
		return measureNo;
	}

	public void setMeasureNo(String measureNo) {
		this.measureNo = measureNo;
	}

	public String getInstanceId() {
		return this.instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public BigDecimal getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(BigDecimal dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getTimeMeasureId() {
		return timeMeasureId;
	}

	public void setTimeMeasureId(String timeMeasureId) {
		this.timeMeasureId = timeMeasureId;
	}

	public String getModeId() {
		return modeId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getIsPassyear() {
		return isPassyear;
	}

	public void setIsPassyear(String isPassyear) {
		this.isPassyear = isPassyear;
	}
	
	

}