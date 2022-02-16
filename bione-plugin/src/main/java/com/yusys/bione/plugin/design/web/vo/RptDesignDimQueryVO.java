package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

@ExcelSheet(index="1",name="查询条件",firstRow=1,insertType ="01",extType = "01")
public class RptDesignDimQueryVO {

	@ExcelColumn(index = "0", name = "查询名称")
	private String display;
	@ExcelColumn(index = "1", name = "维度编号")
	private String key;
	@ExcelColumn(index = "2", name = "默认值")
	private String value;
	@ExcelColumn(index = "3", name = "是否必填",value={"true","false"},text={"是","否"})
	private String requied;
	@ExcelColumn(index = "4", name = "是否复选",value={"true","false"},text={"是","否"})
	private String checked;

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getRequied() {
		return requied;
	}

	public void setRequied(String requied) {
		this.requied = requied;
	}

	
}
