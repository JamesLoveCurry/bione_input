package com.yusys.biapp.input.inputTable.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_LIST_DATALOAD_TYPE database table.
 * 
 */
@Entity
@Table(name="RPT_LIST_DATALOAD_TYPE")
public class RptListDataloadType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TYPE_ID")
	private String typeId;

	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="REMARK")
	private String remark;

	@Column(name="TYPE")
	private String type;

	@Column(name="TABLE_ID")
	private String tableId;
	
    public RptListDataloadType() {
    }

	public String getTypeId() {
		return this.typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getCfgId() {
		return this.cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

}