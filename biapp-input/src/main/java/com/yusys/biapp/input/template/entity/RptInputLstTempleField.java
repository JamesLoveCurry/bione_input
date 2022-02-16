package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the RPT_INPUT_LST_TEMPLE_FIELD database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LST_TEMPLE_FIELD")
public class RptInputLstTempleField implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TEMPLE_FIELD_ID", unique=true, nullable=false, length=32)
	private String templeFieldId;

	@Column(name="ALLOW_EDIT", length=1)
	private String allowEdit;

	@Column(name="ALLOW_INPUT", length=1)
	private String allowInput;

	@Column(name="ALLOW_NULL", length=1)
	private String allowNull;

	@Column(name="ALLOW_SIFT", length=1)
	private String allowSift;

	@Column(name="DECIMAL_LENGTH", length=10)
	private String decimalLength;

	@Column(name="DICT_ID", length=32)
	private String dictId;

	@Column(name="FIELD_CN_NAME", length=100)
	private String fieldCnName;

	@Column(name="FIELD_DETAIL", length=500)
	private String fieldDetail;

	@Column(name="FIELD_EN_NAME", length=100)
	private String fieldEnName;

	@Column(name="FIELD_LENGTH", length=10)
	private String fieldLength;

	@Column(name="FIELD_TYPE", length=100)
	private String fieldType;

	@Column(name="LOGIC_SYS_NO", length=32)
	private String logicSysNo;

	@Column(name="ORDER_NO", length=18)
	private int orderNo;

	@Column(name="TEMPLE_ID", nullable=false, length=32)
	private String templeId;

	@Column(name="DEFAULT_VALUE", length=500)
	private String defaultValue;

	@Column(name = "SEARCH_TYPE", length = 10)
	private String searchType;

	@Column(name = "IS_DESEN", length = 10)
	private String isDesen;//是否脱敏

	@Transient
	private String uniqueKey ;

    public RptInputLstTempleField() {
    }

	public String getTempleFieldId() {
		return this.templeFieldId;
	}

	public void setTempleFieldId(String templeFieldId) {
		this.templeFieldId = templeFieldId;
	}

	public String getAllowEdit() {
		return this.allowEdit;
	}

	public void setAllowEdit(String allowEdit) {
		this.allowEdit = allowEdit;
	}

	public String getAllowInput() {
		return this.allowInput;
	}

	public void setAllowInput(String allowInput) {
		this.allowInput = allowInput;
	}

	public String getAllowNull() {
		return this.allowNull;
	}

	public void setAllowNull(String allowNull) {
		this.allowNull = allowNull;
	}

	public String getAllowSift() {
		return this.allowSift;
	}

	public void setAllowSift(String allowSift) {
		this.allowSift = allowSift;
	}

	public String getDecimalLength() {
		return this.decimalLength;
	}

	public void setDecimalLength(String decimalLength) {
		this.decimalLength = decimalLength;
	}

	public String getDictId() {
		return this.dictId;
	}

	public void setDictId(String dictId) {
		this.dictId = dictId;
	}

	public String getFieldCnName() {
		return this.fieldCnName;
	}

	public void setFieldCnName(String fieldCnName) {
		this.fieldCnName = fieldCnName;
	}

	public String getFieldDetail() {
		return this.fieldDetail;
	}

	public void setFieldDetail(String fieldDetail) {
		this.fieldDetail = fieldDetail;
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

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public int getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getTempleId() {
		return this.templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getIsDesen() {
		return isDesen;
	}

	public void setIsDesen(String isDesen) {
		this.isDesen = isDesen;
	}
}