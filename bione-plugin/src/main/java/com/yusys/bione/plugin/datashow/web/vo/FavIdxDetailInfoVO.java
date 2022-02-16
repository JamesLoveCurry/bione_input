package com.yusys.bione.plugin.datashow.web.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class FavIdxDetailInfoVO {
	private long indexVerId;

	private String calcCycle;

	private BigDecimal dataLen;

	private BigDecimal dataPrecision;

	private String dataType;

	private String dataUnit;

	private String valRange;

	private String endDate;

	private String indexCatalogNo;

	private String indexNm;

	private String indexSts;

	private String indexType;

	private Timestamp lastUptTime;

	private String lastUptUser;

	private String remark;

	private String detailId;

	private String indexAlias;

	private String indexNo;

	private String instanceId;
	
	private String measureNo;
	
	private String measureNm;

	private BigDecimal orderNum;
	
	private boolean hasCurrency;
	
	private List<String> dims;
	

	public boolean getHasCurrency() {
		return hasCurrency;
	}

	public void setHasCurrency(boolean hasCurrency) {
		this.hasCurrency = hasCurrency;
	}


	public FavIdxDetailInfoVO() {
	}

	public String getDetailId() {
		return this.detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getIndexAlias() {
		return this.indexAlias;
	}
	
	public String getMeasureNm() {
		return measureNm;
	}

	public void setMeasureNm(String measureNm) {
		this.measureNm = measureNm;
	}

	public void setIndexAlias(String indexAlias) {
		this.indexAlias = indexAlias;
	}
	

	public String getMeasureNo() {
		return measureNo;
	}

	public void setMeasureNo(String measureNo) {
		this.measureNo = measureNo;
	}

	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getInstanceId() {
		return this.instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public BigDecimal getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public long getIndexVerId() {
		return indexVerId;
	}

	public void setIndexVerId(long indexVerId) {
		this.indexVerId = indexVerId;
	}

	public String getCalcCycle() {
		return calcCycle;
	}

	public void setCalcCycle(String calcCycle) {
		this.calcCycle = calcCycle;
	}

	public BigDecimal getDataLen() {
		return dataLen;
	}

	public void setDataLen(BigDecimal dataLen) {
		this.dataLen = dataLen;
	}

	public BigDecimal getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(BigDecimal dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getValRange() {
		return valRange;
	}

	public void setValRange(String valRange) {
		this.valRange = valRange;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIndexCatalogNo() {
		return indexCatalogNo;
	}

	public void setIndexCatalogNo(String indexCatalogNo) {
		this.indexCatalogNo = indexCatalogNo;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getIndexSts() {
		return indexSts;
	}

	public void setIndexSts(String indexSts) {
		this.indexSts = indexSts;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public Timestamp getLastUptTime() {
		return lastUptTime;
	}

	public void setLastUptTime(Timestamp lastUptTime) {
		this.lastUptTime = lastUptTime;
	}

	public String getLastUptUser() {
		return lastUptUser;
	}

	public void setLastUptUser(String lastUptUser) {
		this.lastUptUser = lastUptUser;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<String> getDims() {
		return dims;
	}

	public void setDims(List<String> dims) {
		this.dims = dims;
	}

	
}
