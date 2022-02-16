package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_SOURCE_IDX database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_BATCH_CFG")
public class RptDesignBatchCfg implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignBatchCfgPK id;

	@Column(name="DIM_TYPE")
	private String dimType;
	
	@Column(name="FILTER_MODE")
	private String filterMode;

	@Column(name="FILTER_VAL")
	private String filterVal;

	@Column(name="POS_TYPE")
	private String posType;
	
	@Column(name="POS_NUM")
	private String posNum;

	/**
	 * @return 返回 id。
	 */
	public RptDesignBatchCfgPK getId() {
		return id;
	}

	/**
	 * @param id 设置 id。
	 */
	public void setId(RptDesignBatchCfgPK id) {
		this.id = id;
	}

	/**
	 * @return 返回 dimType。
	 */
	public String getDimType() {
		return dimType;
	}

	/**
	 * @param dimType 设置 dimType。
	 */
	public void setDimType(String dimType) {
		this.dimType = dimType;
	}

	/**
	 * @return 返回 filterMode。
	 */
	public String getFilterMode() {
		return filterMode;
	}

	/**
	 * @param filterMode 设置 filterMode。
	 */
	public void setFilterMode(String filterMode) {
		this.filterMode = filterMode;
	}

	/**
	 * @return 返回 filterVal。
	 */
	public String getFilterVal() {
		return filterVal;
	}

	/**
	 * @param filterVal 设置 filterVal。
	 */
	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}

	/**
	 * @return 返回 posType。
	 */
	public String getPosType() {
		return posType;
	}

	/**
	 * @param posType 设置 posType。
	 */
	public void setPosType(String posType) {
		this.posType = posType;
	}

	/**
	 * @return 返回 posNum。
	 */
	public String getPosNum() {
		return posNum;
	}

	/**
	 * @param posNum 设置 posNum。
	 */
	public void setPosNum(String posNum) {
		this.posNum = posNum;
	}
	
}