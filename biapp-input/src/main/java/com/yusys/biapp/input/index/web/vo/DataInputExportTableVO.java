package com.yusys.biapp.input.index.web.vo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataInputExportTableVO {
	private String sheetName;
	private List<Map<String, Object>> infoList;
	private LinkedHashMap<String, String> columnInfo;

	public List<Map<String, Object>> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<Map<String, Object>> infoList) {
		this.infoList = infoList;
	}

	public LinkedHashMap<String, String> getColumnInfo() {
		return columnInfo;
	}

	public void setColumnInfo(LinkedHashMap<String, String> columnInfo) {
		this.columnInfo = columnInfo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

}
