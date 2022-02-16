package com.yusys.bione.plugin.datashow.web.vo;

import java.math.BigDecimal;

public class IdxDimInfoVO {
	private String dimTypeNo;
	private String dimNo;
	private String dsId;
	private String dimType;
	private String storeCol;
	private BigDecimal orderNum;
	private String catalogId;
	private String dimTypeNm;
	private String dimTypeEnNm;
	private String dimTypeDesc;
	private String dimTypeStruct;
	public String getDimTypeNo() {
		return dimTypeNo;
	}
	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}
	public String getDimNo() {
		return dimNo;
	}
	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}
	public String getDsId() {
		return dsId;
	}
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}
	public String getDimType() {
		return dimType;
	}
	public void setDimType(String dimType) {
		this.dimType = dimType;
	}
	public String getStoreCol() {
		return storeCol;
	}
	public void setStoreCol(String storeCol) {
		this.storeCol = storeCol;
	}
	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	public String getDimTypeNm() {
		return dimTypeNm;
	}
	public void setDimTypeNm(String dimTypeNm) {
		this.dimTypeNm = dimTypeNm;
	}
	public String getDimTypeEnNm() {
		return dimTypeEnNm;
	}
	public void setDimTypeEnNm(String dimTypeEnNm) {
		this.dimTypeEnNm = dimTypeEnNm;
	}
	public String getDimTypeDesc() {
		return dimTypeDesc;
	}
	public void setDimTypeDesc(String dimTypeDesc) {
		this.dimTypeDesc = dimTypeDesc;
	}
	public String getDimTypeStruct() {
		return dimTypeStruct;
	}
	public void setDimTypeStruct(String dimTypeStruct) {
		this.dimTypeStruct = dimTypeStruct;
	}
	public BigDecimal getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}
}
