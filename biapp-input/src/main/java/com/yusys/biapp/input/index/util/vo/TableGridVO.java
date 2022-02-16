package com.yusys.biapp.input.index.util.vo;

import java.util.List;
import java.util.Map;

public class TableGridVO {

	@TableColumn(columnName="columns")
	private TableColumnVO columnVO;

	@TableColumn(columnName="checkbox")
	private String checkbox;
	

	@TableColumn(columnName="allowHideColumn")
	private String allowHideColumn;
	
	@TableColumn(columnName="data")
	private List<List<Map<String, String>>> data;

	@TableColumn(columnName="width")
	private String width;
	
	@TableColumn(columnName="height")
	private String height;
	
	private String cfgId;

	public static TableGridVO getDefaultTableColumn(){
		TableGridVO vo = new TableGridVO();
		vo.setCheckbox("false");
		vo.setWidth("100%");
		vo.setHeight("100");
		vo.setAllowHideColumn("false");
		
		return vo;
		
	}
	public TableColumnVO getColumnVO() {
		return columnVO;
	}

	public void setColumnVO(TableColumnVO columnVO) {
		this.columnVO = columnVO;
	}

	public String getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public String getAllowHideColumn() {
		return allowHideColumn;
	}

	public void setAllowHideColumn(String allowHideColumn) {
		this.allowHideColumn = allowHideColumn;
	}
	public List<List<Map<String, String>>> getData() {
		return data;
	}
	public void setData(List<List<Map<String, String>>> data) {
		this.data = data;
	}
	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}
	public String getCfgId() {
		return cfgId;
	}
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	
}
