package com.yusys.bione.plugin.detailtmp.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DETAIL_TMP_SEARCH database table.
 * 
 */
@Entity
@Table(name="RPT_DETAIL_TMP_SEARCH")
@NamedQuery(name="RptDetailTmpSearch.findAll", query="SELECT r FROM RptDetailTmpSearch r")
public class RptDetailTmpSearch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDetailTmpSearchPK id;

	private String checkbox;

	@Column(name="CN_NM")
	private String cnNm;

	@Column(name="COL_ID")
	private String colId;

	@Column(name="DEF_VAL")
	private String defVal;

	@Column(name="DIM_TYPE_NO")
	private String dimTypeNo;

	@Column(name="DIM_TYPE_STRUCT")
	private String dimTypeStruct;

	@Column(name="ELEMENT_TYPE")
	private String elementType;

	@Column(name="EN_NM")
	private String enNm;
	
	@Column(name="IS_CONVER")
	private String isConver;
	
	@Column(name="DATA_JSON")
	private String dataJson;

	private BigDecimal orderno;

	private String required;

	public RptDetailTmpSearch() {
	}

	public RptDetailTmpSearchPK getId() {
		return this.id;
	}

	public void setId(RptDetailTmpSearchPK id) {
		this.id = id;
	}

	public String getCheckbox() {
		return this.checkbox;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public String getCnNm() {
		return this.cnNm;
	}

	public void setCnNm(String cnNm) {
		this.cnNm = cnNm;
	}

	public String getColId() {
		return this.colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	public String getDefVal() {
		return this.defVal;
	}

	public void setDefVal(String defVal) {
		this.defVal = defVal;
	}

	public String getDimTypeNo() {
		return this.dimTypeNo;
	}

	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public String getDimTypeStruct() {
		return this.dimTypeStruct;
	}

	public void setDimTypeStruct(String dimTypeStruct) {
		this.dimTypeStruct = dimTypeStruct;
	}

	public String getElementType() {
		return this.elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getEnNm() {
		return this.enNm;
	}

	public void setEnNm(String enNm) {
		this.enNm = enNm;
	}

	public BigDecimal getOrderno() {
		return this.orderno;
	}

	public void setOrderno(BigDecimal orderno) {
		this.orderno = orderno;
	}

	public String getRequired() {
		return this.required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getIsConver() {
		return isConver;
	}

	public void setIsConver(String isConver) {
		this.isConver = isConver;
	}

	public String getDataJson() {
		return dataJson;
	}

	public void setDataJson(String dataJson) {
		this.dataJson = dataJson;
	}
	
	

}