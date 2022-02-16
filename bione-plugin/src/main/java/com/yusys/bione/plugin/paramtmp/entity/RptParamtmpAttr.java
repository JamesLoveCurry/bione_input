package com.yusys.bione.plugin.paramtmp.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_PARAMTMP_ATTRS database table.
 * 
 */
@Entity
@Table(name="RPT_PARAMTMP_ATTRS")
public class RptParamtmpAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PARAM_ID")
	private String paramId;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	@Column(name="PARAM_TYPE")
	private String paramType;

	@Column(name="PARAM_VAL")
	private String paramVal;

	@Column(name="PARAMTMP_ID")
	private String paramtmpId;

	private String remark;

    public RptParamtmpAttr() {
    }

	public String getParamId() {
		return this.paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getParamType() {
		return this.paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamVal() {
		return this.paramVal;
	}

	public void setParamVal(String paramVal) {
		this.paramVal = paramVal;
	}

	public String getParamtmpId() {
		return this.paramtmpId;
	}

	public void setParamtmpId(String paramtmpId) {
		this.paramtmpId = paramtmpId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}