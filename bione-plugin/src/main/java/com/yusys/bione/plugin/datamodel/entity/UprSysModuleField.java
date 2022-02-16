package com.yusys.bione.plugin.datamodel.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the UPR_SYS_MODULE_FIELD database table.
 * 
 */
@Entity
@Table(name="UPR_SYS_MODULE_FIELD")
@NamedQuery(name="UprSysModuleField.findAll", query="SELECT u FROM UprSysModuleField u")
public class UprSysModuleField implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COL_ID")
	private String colId;

	@Column(name="CN_NAME")
	private String cnName;

	@Column(name="DATA_LENGTH")
	private BigDecimal dataLength;

	@Column(name="DATA_PRECISION")
	private BigDecimal dataPrecision;

	@Column(name="DB_TYPE")
	private String dbType;

	@Column(name="DICT_ID")
	private String dictId;

	@Column(name="EN_NAME")
	private String enName;

	@Column(name="FIELD_TYPE")
	private String fieldType;

	@Column(name="IS_NULL")
	private String isNull;

	@Column(name="IS_PK")
	private String isPk;
	
	@Column(name="IS_USE")
	private String isUse;

	@Column(name="IS_QUERY_FIELD")
	private String isQueryField;

	@Column(name="ORDER_NO")
	private BigDecimal orderNo;

	@Column(name="SET_ID")
	private String setId;
	
	@Column(name="REMARK")
	private String remark;

	public UprSysModuleField() {
	}

	public String getColId() {
		return this.colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	public String getCnName() {
		return this.cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public BigDecimal getDataLength() {
		return this.dataLength;
	}

	public void setDataLength(BigDecimal dataLength) {
		this.dataLength = dataLength;
	}

	public BigDecimal getDataPrecision() {
		return this.dataPrecision;
	}

	public void setDataPrecision(BigDecimal dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getDbType() {
		return this.dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDictId() {
		return this.dictId;
	}

	public void setDictId(String dictId) {
		this.dictId = dictId;
	}

	public String getEnName() {
		return this.enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getIsNull() {
		return this.isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getIsPk() {
		return this.isPk;
	}

	public void setIsPk(String isPk) {
		this.isPk = isPk;
	}

	public String getIsQueryField() {
		return this.isQueryField;
	}

	public void setIsQueryField(String isQueryField) {
		this.isQueryField = isQueryField;
	}

	public BigDecimal getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(BigDecimal orderNo) {
		this.orderNo = orderNo;
	}

	public String getSetId() {
		return this.setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}