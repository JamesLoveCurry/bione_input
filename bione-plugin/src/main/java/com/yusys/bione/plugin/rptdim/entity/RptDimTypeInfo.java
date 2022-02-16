package com.yusys.bione.plugin.rptdim.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;

/**
 * The persistent class for the RPT_DIM_TYPE_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_DIM_TYPE_INFO")
public class RptDimTypeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DIM_TYPE_NO")
	@ExcelColumn(index = "A", name = "维度")
	private String dimTypeNo;

	@Column(name = "CATALOG_ID")
	private String catalogId;

	@Column(name = "DIM_TYPE_DESC")
	@ExcelColumn(index = "C", name = "维度描述")
	private String dimTypeDesc;

	@Column(name = "DIM_TYPE_EN_NM")
	private String dimTypeEnNm;

	@Column(name = "DIM_TYPE_NM")
	@ExcelColumn(index = "B", name = "维度名称")
	private String dimTypeNm;

	@Column(name = "DIM_TYPE")
	private String dimType;

	@Column(name = "DIM_TYPE_STRUCT")
	private String dimTypeStruct;

	@Column(name = "START_DATE", length = 8)
	private String startDate;

	@Column(name = "END_DATE", length = 8)
	private String endDate;
	
	@Column(name = "BUSI_DEF")
	private String busiDef;

	@Column(name = "BUSI_RULE")
	private String busiRule;

	@Column(name = "DEF_DEPT", length = 500)
	private String defDept;

	@Column(name = "USE_DEPT", length = 500)
	private String useDept;

	@Column(name = "DIM_STS")
	private String dimSts;

	@Transient
	private String catalogNm;

	public String getDimTypeStruct() {
		return dimTypeStruct;
	}

	public void setDimTypeStruct(String dimTypeStruct) {
		this.dimTypeStruct = dimTypeStruct;
	}

	public RptDimTypeInfo() {
	}

	public String getDimTypeNo() {
		return this.dimTypeNo;
	}

	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getDimTypeDesc() {
		return this.dimTypeDesc;
	}

	public void setDimTypeDesc(String dimTypeDesc) {
		this.dimTypeDesc = dimTypeDesc;
	}

	public String getDimTypeEnNm() {
		return this.dimTypeEnNm;
	}

	public void setDimTypeEnNm(String dimTypeEnNm) {
		this.dimTypeEnNm = dimTypeEnNm;
	}

	public String getDimTypeNm() {
		return this.dimTypeNm;
	}

	public void setDimTypeNm(String dimTypeNm) {
		this.dimTypeNm = dimTypeNm;
	}

	public String getDimType() {
		return dimType;
	}

	public void setDimType(String dimType) {
		this.dimType = dimType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getBusiDef() {
		return busiDef;
	}

	public void setBusiDef(String busiDef) {
		this.busiDef = busiDef;
	}

	public String getBusiRule() {
		return busiRule;
	}

	public void setBusiRule(String busiRule) {
		this.busiRule = busiRule;
	}

	public String getDefDept() {
		return defDept;
	}

	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}

	public String getUseDept() {
		return useDept;
	}

	public void setUseDept(String useDept) {
		this.useDept = useDept;
	}

	public String getDimSts() {
		return dimSts;
	}

	public void setDimSts(String dimSts) {
		this.dimSts = dimSts;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCatalogNm() {
		return catalogNm;
	}

	public void setCatalogNm(String catalogNm) {
		this.catalogNm = catalogNm;
	}

}