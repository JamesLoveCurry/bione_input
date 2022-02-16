package com.yusys.bione.plugin.rptidx.entity;


import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_idx_time_measure database table.
 * 
 */
@Entity
@Table(name="rpt_idx_val_type")
public class RptIdxValType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MODE_ID")
	private String modeId;

	@Column(name="MODE_DESC")
	private String modeDesc;

	@Column(name="MODE_NM")
	private String modeNm;

	@Column(name="MODE_TEMPLATE")
	private String modeTemplate;
	
	@Column(name="SORT_ORDER")
	private BigDecimal sortOrder;

    public RptIdxValType() {
    }

	/**
	 * @return 返回 modeId。
	 */
	public String getModeId() {
		return modeId;
	}

	/**
	 * @param modeId 设置 modeId。
	 */
	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	/**
	 * @return 返回 modeDesc。
	 */
	public String getModeDesc() {
		return modeDesc;
	}

	/**
	 * @param modeDesc 设置 modeDesc。
	 */
	public void setModeDesc(String modeDesc) {
		this.modeDesc = modeDesc;
	}

	/**
	 * @return 返回 modeNm。
	 */
	public String getModeNm() {
		return modeNm;
	}

	/**
	 * @param modeNm 设置 modeNm。
	 */
	public void setModeNm(String modeNm) {
		this.modeNm = modeNm;
	}

	/**
	 * @return 返回 modeTemplate。
	 */
	public String getModeTemplate() {
		return modeTemplate;
	}

	/**
	 * @param modeTemplate 设置 modeTemplate。
	 */
	public void setModeTemplate(String modeTemplate) {
		this.modeTemplate = modeTemplate;
	}

	/**
	 * @return 返回 sortOrder。
	 */
	public BigDecimal getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param sortOrder 设置 sortOrder。
	 */
	public void setSortOrder(BigDecimal sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}