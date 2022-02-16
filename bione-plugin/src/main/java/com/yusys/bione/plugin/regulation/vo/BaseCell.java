package com.yusys.bione.plugin.regulation.vo;

/**
 * 单元格基类，支持以cellNo排序
 */
public class BaseCell implements Comparable<BaseCell> {

	protected Template template;

	protected String cellNo;

	public int compareTo(BaseCell o) {
		return cellNo.compareTo(o.getCellNo());
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public String getCellNo() {
		return cellNo;
	}

	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}
}
