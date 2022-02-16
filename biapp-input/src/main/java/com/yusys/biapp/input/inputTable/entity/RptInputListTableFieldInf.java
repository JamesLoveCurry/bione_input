package com.yusys.biapp.input.inputTable.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_INPUT_LIST_TABLE_FIELD_INF database table.
 * 
 */
@Entity
@Table(name = "RPT_INPUT_LIST_TABLE_FIELD_INF")
public class RptInputListTableFieldInf implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false, length = 32)
	private String id;

	@Column(name = "ALLOW_NULL", length = 10)
	private String allowNull;

	@Column(name = "DECIMAL_LENGTH", length = 10)
	private String decimalLength;

	@Column(name = "FIELD_CN_NAME", length = 100)
	private String fieldCnName;

	@Column(name = "FIELD_EN_NAME", length = 100)
	private String fieldEnName;

	@Column(name = "FIELD_LENGTH", length = 10)
	private String fieldLength;

	@Column(name = "FIELD_TYPE", length = 100)
	private String fieldType;

	@Column(name = "ORDER_NO", length = 18)
	private int orderNo;

	@Column(name = "TABLE_ID", nullable = false, length = 32)
	private String tableId;
	
	@Column(name = "DEFAULT_VALUE", length = 500)
	private String defaultValue;

	public RptInputListTableFieldInf() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAllowNull() {
		return this.allowNull;
	}

	public void setAllowNull(String allowNull) {
		this.allowNull = allowNull;
	}

	public String getDecimalLength() {
		return this.decimalLength;
	}

	public void setDecimalLength(String decimalLength) {
		this.decimalLength = decimalLength;
	}

	public String getFieldCnName() {
		return this.fieldCnName;
	}

	public void setFieldCnName(String fieldCnName) {
		this.fieldCnName = fieldCnName;
	}

	public String getFieldEnName() {
		return this.fieldEnName;
	}

	public void setFieldEnName(String fieldEnName) {
		this.fieldEnName = fieldEnName;
	}

	public String getFieldLength() {
		return this.fieldLength;
	}

	public void setFieldLength(String fieldLength) {
		this.fieldLength = fieldLength;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public int getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}