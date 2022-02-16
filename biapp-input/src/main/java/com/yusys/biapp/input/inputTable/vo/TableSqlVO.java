package com.yusys.biapp.input.inputTable.vo;

public class TableSqlVO extends TableVO {

	private String sqlText;
	
	private String dsId;

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}
	
}
