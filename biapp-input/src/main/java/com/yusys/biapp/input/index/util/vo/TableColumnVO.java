package com.yusys.biapp.input.index.util.vo;

import java.util.List;

public class TableColumnVO {

	@TableColumn(columnName="display")
	private String display;
	@TableColumn(columnName="name")
	private String name;
	@TableColumn(columnName="align")
	private String align;
	@TableColumn(columnName="width")
	private String width;
	

	@TableColumn(columnName="columns")
	private List<TableColumnVO> columns;
	
	
	public TableColumnVO(){}
	
	public static TableColumnVO getDefaultTableColumn(){
		TableColumnVO vo = new TableColumnVO();
		vo.setAlign("center");
		vo.setWidth("100%");
		
		return vo;
		
	}


	public String getDisplay() {
		return display;
	}


	public void setDisplay(String display) {
		this.display = display;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAlign() {
		return align;
	}


	public void setAlign(String align) {
		this.align = align;
	}


	public String getWidth() {
		return width;
	}


	public void setWidth(String width) {
		this.width = width;
	}


	public List<TableColumnVO> getColumns() {
		return columns;
	}


	public void setColumns(List<TableColumnVO> columns) {
		this.columns = columns;
	}

	
	
}
