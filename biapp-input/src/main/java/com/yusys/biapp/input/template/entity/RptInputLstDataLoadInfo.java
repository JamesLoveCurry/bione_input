package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_LST_DATA_LOAD_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LST_DATA_LOAD_INFO")
public class RptInputLstDataLoadInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=32)
	private String id;

	@Column(name="CONDITION_COLUMN", length=100)
	private String conditionColumn;

	@Column(name="CONDITION_VAL", length=500)
	private String conditionVal;

	@Column(name="TEMPLE_ID", nullable=false, length=32)
	private String templeId;

    public RptInputLstDataLoadInfo() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConditionColumn() {
		return this.conditionColumn;
	}

	public void setConditionColumn(String conditionColumn) {
		this.conditionColumn = conditionColumn;
	}

	public String getConditionVal() {
		return this.conditionVal;
	}

	public void setConditionVal(String conditionVal) {
		this.conditionVal = conditionVal;
	}

	public String getTempleId() {
		return templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}
}