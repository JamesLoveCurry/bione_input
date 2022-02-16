package com.yusys.bione.plugin.idxanacfg.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ANA_TMP_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_ANA_TMP_INFO")
@NamedQuery(name="RptAnaTmpInfo.findAll", query="SELECT r FROM RptAnaTmpInfo r")
public class RptAnaTmpInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="DATA_FORMAT")
	private String dataFormat;

	@Column(name="DATA_PRECISION")
	private BigDecimal dataPrecision;

	@Column(name="DATA_UNIT")
	private String dataUnit;

	@Column(name="IS_DEFAULT")
	private String isDefault;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	private String remark;

	@Column(name="TEMPLATE_FREQ")
	private String templateFreq;

	@Column(name="TEMPLATE_NM")
	private String templateNm;

	public RptAnaTmpInfo() {
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getDataFormat() {
		return this.dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public BigDecimal getDataPrecision() {
		return this.dataPrecision;
	}

	public void setDataPrecision(BigDecimal dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getDataUnit() {
		return this.dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTemplateFreq() {
		return this.templateFreq;
	}

	public void setTemplateFreq(String templateFreq) {
		this.templateFreq = templateFreq;
	}

	public String getTemplateNm() {
		return this.templateNm;
	}

	public void setTemplateNm(String templateNm) {
		this.templateNm = templateNm;
	}

}