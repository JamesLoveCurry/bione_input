package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_ADAPTER_PARAM database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_ADAPTER_PARAM")
public class RptMgrAdapterParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ADAPTER_PARAM_ID")
	private String adapterParamId;

	@Column(name="ADAPTER_ID")
	private String adapterId;

	@Column(name="DISPLAY_NM")
	private String displayNm;

	@Column(name="INIT_VAL")
	private String initVal;

	@Column(name="IS_NOTNULL")
	private String isNotnull;

	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	@Column(name="PARAM_NM")
	private String paramNm;

	private String remark;

    public RptMgrAdapterParam() {
    }

	public String getAdapterParamId() {
		return this.adapterParamId;
	}

	public void setAdapterParamId(String adapterParamId) {
		this.adapterParamId = adapterParamId;
	}

	public String getAdapterId() {
		return this.adapterId;
	}

	public void setAdapterId(String adapterId) {
		this.adapterId = adapterId;
	}

	public String getDisplayNm() {
		return this.displayNm;
	}

	public void setDisplayNm(String displayNm) {
		this.displayNm = displayNm;
	}

	public String getInitVal() {
		return this.initVal;
	}

	public void setInitVal(String initVal) {
		this.initVal = initVal;
	}

	public String getIsNotnull() {
		return this.isNotnull;
	}

	public void setIsNotnull(String isNotnull) {
		this.isNotnull = isNotnull;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public String getParamNm() {
		return this.paramNm;
	}

	public void setParamNm(String paramNm) {
		this.paramNm = paramNm;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}