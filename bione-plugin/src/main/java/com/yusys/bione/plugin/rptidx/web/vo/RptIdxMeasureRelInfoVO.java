package com.yusys.bione.plugin.rptidx.web.vo;

import java.math.BigDecimal;


/**
 * @author rdpc0628
 *
 */
public class RptIdxMeasureRelInfoVO {

	private String indexNo;

	private long indexVerId;
	
	private String measureNo;
	
	private String dsId;
	
	private String storeCol;
	
	private BigDecimal orderNum;
	
	private String setNm;
    
	private String  cnNm;
	
	private String  tableEnNm;
	
	public String getCnNm() {
		return cnNm;
	}

	public void setCnNm(String cnNm) {
		this.cnNm = cnNm;
	}

	public String getSetNm() {
		return setNm;
	}

	public void setSetNm(String setNm) {
		this.setNm = setNm;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public long getIndexVerId() {
		return indexVerId;
	}

	public String getMeasureNo() {
		return measureNo;
	}

	public void setMeasureNo(String measureNo) {
		this.measureNo = measureNo;
	}

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getStoreCol() {
		return storeCol;
	}

	public void setStoreCol(String storeCol) {
		this.storeCol = storeCol;
	}

	public BigDecimal getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}

	public void setIndexVerId(long indexVerId) {
		this.indexVerId = indexVerId;
	}

	public String getTableEnNm() {
		return tableEnNm;
	}

	public void setTableEnNm(String tableEnNm) {
		this.tableEnNm = tableEnNm;
	}
}