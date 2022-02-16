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
@Table(name="RPT_DESIGN_SOURCE_IDX")
public class RptDesignSourceIdx implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignSourceIdxPK id;

	@Column(name="INDEX_NO")
	private String indexNo;
	
	@Column(name="RULE_ID")
	private String ruleId;
	
	@Column(name="TIME_MEASURE_ID")
	private String timeMeasureId;
	
	@Column(name="MODE_ID")
	private String modeId;
	
	@Column(name="IS_CFG_SRCINDEX")
	private String isCfgSrcIndex;
	
    public RptDesignSourceIdx() {
    }

	public RptDesignSourceIdxPK getId() {
		return this.id;
	}

	public void setId(RptDesignSourceIdxPK id) {
		this.id = id;
	}
	
	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
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

	public String getIsCfgSrcIndex() {
		return isCfgSrcIndex;
	}

	public void setIsCfgSrcIndex(String isCfgSrcIndex) {
		this.isCfgSrcIndex = isCfgSrcIndex;
	}
}