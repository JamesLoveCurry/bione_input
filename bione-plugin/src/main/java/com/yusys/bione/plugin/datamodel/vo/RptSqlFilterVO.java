package com.yusys.bione.plugin.datamodel.vo;

import java.util.ArrayList;
import java.util.List;

public class RptSqlFilterVO {

	private String filterSql;
	private List<Object> params = new ArrayList<Object>();

	public String getFilterSql() {
		return filterSql;
	}

	public void setFilterSql(String filterSql) {
		this.filterSql = filterSql;
	}

	public List<Object> getParams() {
		return params;
	}

	public void addParam(Object param) {
		this.params.add(param);
	}

}
