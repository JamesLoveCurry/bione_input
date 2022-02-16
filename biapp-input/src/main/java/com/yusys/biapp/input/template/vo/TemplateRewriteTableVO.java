package com.yusys.biapp.input.template.vo;

import java.util.List;

public class TemplateRewriteTableVO {

	private String tableName;
	private String dsId;
	private String updateType;
	private String templeId;
	private String templeNm;
	private List<TemplateRewriteColumnVO> columnList;

	public String getTempleNm() {
		return templeNm;
	}

	public void setTempleNm(String templeNm) {
		this.templeNm = templeNm;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}

	public List<TemplateRewriteColumnVO> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<TemplateRewriteColumnVO> columnList) {
		this.columnList = columnList;
	}

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getTempleId() {
		return templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

}
