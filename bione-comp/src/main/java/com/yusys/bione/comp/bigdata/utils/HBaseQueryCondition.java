package com.yusys.bione.comp.bigdata.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HBaseQueryCondition {

	public HBaseQueryCondition() {
		scanCacheNum = 100;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getStartRowKey() {
		return startRowKey;
	}

	public void setStartRowKey(String startRowKey) {
		this.startRowKey = startRowKey;
	}

	public String getStopRowKey() {
		return stopRowKey;
	}

	public void setStopRowKey(String stopRowKey) {
		this.stopRowKey = stopRowKey;
	}

	public List<?> getFiltersList() {
		return filtersList;
	}

	public void setFiltersList(List<?> filtersList) {
		this.filtersList = filtersList;
	}

	public Map<?, ?> getFamilyMap() {
		return familyMap;
	}

	public void setFamilyMap(Map<?, ?> familyMap) {
		this.familyMap = familyMap;
	}

	public int getScanCacheNum() {
		return scanCacheNum;
	}

	public void setScanCacheNum(int scanCacheNum) {
		this.scanCacheNum = scanCacheNum;
	}

	public boolean isFirstKeyOnly() {
		return firstKeyOnly;
	}

	public void setFirstKeyOnly(boolean firstKeyOnly) {
		this.firstKeyOnly = firstKeyOnly;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	public ConcurrentHashMap<String, String> getOracleMapping() {
		return oracleMapping;
	}
	
	public void addOracleLookup(String columnName, String LookupName) {
        oracleMapping.put(columnName, LookupName);
    }


	private String tableName;
	private String startRowKey;
	private String stopRowKey;
	private List<?> filtersList;
	private Map<?, ?> familyMap;
	private int scanCacheNum;
	private boolean firstKeyOnly;
	private int operator = 0;
	private ConcurrentHashMap<String, String> oracleMapping = new ConcurrentHashMap<String, String>();
	
}
