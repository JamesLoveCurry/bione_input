package com.yusys.biapp.input.inputTable.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_INPUT_LIST_TABLE_CONSTRAIN database table.
 * 
 */
@Entity
@Table(name = "RPT_INPUT_LIST_TABLE_CONSTRAIN")
public class RptInputListTableConstraint implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false, length = 32)
	private String id;

	@Column(name = "KEY_COLUMN", length = 500)
	private String keyColumn;

	@Column(name = "KEY_NAME", length = 100)
	private String keyName;

	@Column(name = "KEY_TYPE", length = 32)
	private String keyType;

	@Column(name = "TABLE_ID", nullable = false, length = 32)
	private String tableId;

	public RptInputListTableConstraint() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKeyColumn() {
		return this.keyColumn;
	}

	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyType() {
		return this.keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

}