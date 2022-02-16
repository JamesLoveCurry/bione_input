package com.yusys.bione.plugin.regulation.enums;

/**
 * 一般单元格-单元格类型
 * @author yangyf1
 *
 */
public enum ComCellType {
	COMMONLY, EDIT;
	
	public static ComCellType get(String name) {
		if ("00".equals(name) || "普通".equals(name)) {
			return COMMONLY;
		}
		if ("05".equals(name) || "可编辑单元格".equals(name)) {
			return EDIT;
		}
		return COMMONLY;
	}
	
	@Override
	public String toString() {
		switch (this) {
		case COMMONLY:
			return "00";
		case EDIT:
			return "05";
		default:
			return "00";
		}
	}
}
