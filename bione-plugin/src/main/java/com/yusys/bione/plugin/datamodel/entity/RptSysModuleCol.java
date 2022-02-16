package com.yusys.bione.plugin.datamodel.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the RPT_SYS_MODULE_COL database table.
 * 
 */
@Entity
@Table(name="RPT_SYS_MODULE_COL")
public class RptSysModuleCol implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COL_ID")
	private String colId;

	@Column(name="CN_NM")
	private String cnNm;

	@Column(name="COL_TYPE")
	private String colType;

	@Column(name="DB_TYPE")
	private String dbType;

	@Column(name="DIM_TYPE_NO")
	private String dimTypeNo;
	
	@Transient
	private  String  dimTypeName;

	@Column(name="EN_NM")
	private String enNm;

	@Column(name="IS_NULL")
	private String isNull;

	@Column(name="IS_PK")
	private String isPk;

	@Column(name="IS_QUERY_COL")
	private String isQueryCol;

	@Column(name="IS_USE")
	private String isUse;
    
	@Column(name="LEN")
	private BigDecimal len;

	@Column(name="MEASURE_NO")
	private String measureNo;
	
    @Transient
    private  String   measureName;
    
	@Column(name="ORDER_NUM")
	private BigDecimal orderNum;

	@Column(name="PRECISION2")
	private BigDecimal precision2;

	@Column(name="QUERY_LOGIC")
	private String queryLogic;

	@Column(name="SET_ID")
	private String setId;

    public RptSysModuleCol() {
    }
    
    public RptSysModuleCol(RptSysModuleCol col){
    	this.cnNm = col.getCnNm();
    	this.colId = col.getColId();
    	this.colType = col.getColType();
    	this.dbType = col.getDbType();
    	this.dimTypeName = col.getDimTypeName();
    	this.dimTypeNo = col.getDimTypeNo();
    	this.enNm = col.getEnNm();
    	this.isNull = col.getIsNull();
    	this.isPk = col.getIsPk();
    	this.isQueryCol = col.getIsQueryCol();
    	this.isUse = col.getIsUse();
    	this.len = col.getLen();
    	this.measureName = col.getMeasureName();
    	this.measureNo = col.getMeasureNo();
    	this.orderNum = col.getOrderNum();
    	this.precision2 = col.getPrecision2();
    	this.queryLogic = col.getQueryLogic();
    	this.setId = col.getSetId();
    }

	public String getColId() {
		return this.colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	public String getCnNm() {
		return this.cnNm;
	}

	public void setCnNm(String cnNm) {
		this.cnNm = cnNm;
	}

	public String getColType() {
		return this.colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public String getDbType() {
		return this.dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDimTypeNo() {
		return this.dimTypeNo;
	}

	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public String getEnNm() {
		return this.enNm;
	}

	public void setEnNm(String enNm) {
		this.enNm = enNm;
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

	public String getIsQueryCol() {
		return this.isQueryCol;
	}

	public void setIsQueryCol(String isQueryCol) {
		this.isQueryCol = isQueryCol;
	}

	public String getIsUse() {
		return this.isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public BigDecimal getLen() {
		return this.len;
	}

	public void setLen(BigDecimal len) {
		this.len = len;
	}

	public String getMeasureNo() {
		return this.measureNo;
	}

	public void setMeasureNo(String measureNo) {
		this.measureNo = measureNo;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public BigDecimal getPrecision2() {
		return precision2;
	}

	public void setPrecision2(BigDecimal precision2) {
		this.precision2 = precision2;
	}

	public String getQueryLogic() {
		return this.queryLogic;
	}

	public void setQueryLogic(String queryLogic) {
		this.queryLogic = queryLogic;
	}

	public String getSetId() {
		return this.setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public String getDimTypeName() {
		return dimTypeName;
	}

	public void setDimTypeName(String dimTypeName) {
		this.dimTypeName = dimTypeName;
	}

	public String getMeasureName() {
		return measureName;
	}

	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}
    
}