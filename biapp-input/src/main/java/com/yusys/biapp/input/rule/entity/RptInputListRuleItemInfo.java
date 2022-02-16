package com.yusys.biapp.input.rule.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_LST_RULE_ITEM_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LIST_RULE_ITEM_INFO")
public class RptInputListRuleItemInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ITEM_ID", unique=true, nullable=false, length=32)
	private String itemId;

	@Column(name="AGGREGATE_FUNC", length=100)
	private String aggregateFunc;

	@Column(name="CAL_CODE", length=32)
	private String calCode;

	@Column(name="DS_ID", length=32)
	private String dsId;

	@Column(name="FIELD_NAME", length=100)
	private String fieldName;

	@Column(name="FILTER_CONDITION", length=500)
	private String filterCondition;

	@Column(name="GROUP_FIELD", length=100)
	private String groupField;

	@Column(name="LEFT_OR_RIGHT", length=32)
	private String leftOrRight;

	@Column(name="RULE_ID", nullable=false, length=32)
	private String ruleId;

	@Column(name="TABLE_EN_NAME", length=100)
	private String tableEnName;

    public RptInputListRuleItemInfo() {
    }

	public String getItemId() {
		return this.itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getAggregateFunc() {
		return this.aggregateFunc;
	}

	public void setAggregateFunc(String aggregateFunc) {
		this.aggregateFunc = aggregateFunc;
	}

	public String getCalCode() {
		return this.calCode;
	}

	public void setCalCode(String calCode) {
		this.calCode = calCode;
	}

	public String getDsId() {
		return this.dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFilterCondition() {
		return this.filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getGroupField() {
		return this.groupField;
	}

	public void setGroupField(String groupField) {
		this.groupField = groupField;
	}

	public String getLeftOrRight() {
		return this.leftOrRight;
	}

	public void setLeftOrRight(String leftOrRight) {
		this.leftOrRight = leftOrRight;
	}

	public String getRuleId() {
		return this.ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getTableEnName() {
		return this.tableEnName;
	}

	public void setTableEnName(String tableEnName) {
		this.tableEnName = tableEnName;
	}

}