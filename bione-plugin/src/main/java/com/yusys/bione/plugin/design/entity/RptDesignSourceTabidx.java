package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_design_source_tabidx database table.
 * 
 */
@Entity
@Table(name="rpt_design_source_tabidx")
public class RptDesignSourceTabidx implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignSourceTabidxPK id;

	@Column(name="INDEX_NO")
	private String indexNo;
	
	@Column(name="TIME_MEASURE_ID")
	private String timeMeasureId;
	
	@Column(name="RULE_ID")
	private String ruleId;
	
	@Column(name="MODE_ID")
	private String modeId;
	
	@Column(name="IS_PASSYEAR")
	private String isPassyear;
	
    public RptDesignSourceTabidx() {
    }

	public RptDesignSourceTabidxPK getId() {
		return this.id;
	}

	public void setId(RptDesignSourceTabidxPK id) {
		this.id = id;
	}
	
	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getTimeMeasureId() {
		return timeMeasureId;
	}

	public void setTimeMeasureId(String timeMeasureId) {
		this.timeMeasureId = timeMeasureId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getModeId() {
		return modeId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	public String getIsPassyear() {
		return isPassyear;
	}

	public void setIsPassyear(String isPassyear) {
		this.isPassyear = isPassyear;
	}
	
}