package com.yusys.biapp.input.inputTable.vo;

import java.io.Serializable;

/**
 * 数据库建表语句
 * 
 * @author guojiangping
 * @version 1.0
 * 
 */
public class InputTableSql implements Serializable {
	private static final long serialVersionUID = 1L;

	private String tableSql;

	public String getTableSql() {
		return tableSql;
	}

	public void setTableSql(String tableSql) {
		this.tableSql = tableSql;
	}
	
}
