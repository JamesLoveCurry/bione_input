package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 控件类型：文本、数字、单选下拉框、弹出复选框、时间点、时间区间、复选下拉框、数字区间、弹出单选框
 */
public enum ElementType {
	TEXT, NUMBER, DROPDOWN_SINGLE, POP_MULTI, TIME_POINT, TIME_REGION, DROPDOWN_MULTI,
	NUMBER_REGION, POP_SINGLE, ERROR;
	
	public static ElementType get(String name) {
		name = StringUtils.trimToEmpty(name);
		if ("01".equals(name) || "文本".equals(name)) {
			return TEXT;
		}
		if ("02".equals(name) || "数字".equals(name)) {
			return NUMBER;
		}
		if ("03".equals(name) || "单选下拉框".equals(name)) {
			return DROPDOWN_SINGLE;
		}
		if ("04".equals(name) || "弹出复选框".equals(name)) {
			return POP_MULTI;
		}
		if ("05".equals(name) || "时间点".equals(name)) {
			return TIME_POINT;
		}
		if ("06".equals(name) || "时间区间".equals(name)) {
			return TIME_REGION;
		}
		if ("07".equals(name) || "复选下拉框".equals(name)) {
			return DROPDOWN_MULTI;
		}
		if ("08".equals(name) || "数字区间".equals(name)) {
			return NUMBER_REGION;
		}
		if ("11".equals(name) || "弹出单选框".equals(name)) {
			return POP_SINGLE;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case TEXT:
			return "01";
		case NUMBER:
			return "02";
		case DROPDOWN_SINGLE:
			return "03";
		case POP_MULTI:
			return "04";
		case TIME_POINT:
			return "05";
		case TIME_REGION:
			return "06";
		case DROPDOWN_MULTI:
			return "07";
		case NUMBER_REGION:
			return "08";
		case POP_SINGLE:
			return "11";
		default:
			return "";	
		}
	}
}
