package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 过滤方式：包含、不包含、等于、不等于、大于、大于等于、小于、小于等于
 */
public enum FilterMode {
	EMPTY, IN, NOT_IN, EQUAL, NOT_EQUAL, GREATER_THAN,
	GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, ERROR;
	
	public static FilterMode get(String name) {
		name = StringUtils.trimToEmpty(name);
		if (name.length() == 0) {
			return EMPTY;
		}
		if ("01".equals(name) || "包含".equals(name)) {
			return IN;
		}
		if ("02".equals(name) || "不包含".equals(name)) {
			return NOT_IN;
		}
		if ("03".equals(name) || "等于".equals(name)) {
			return EQUAL;
		}
		if ("04".equals(name) || "不等于".equals(name)) {
			return NOT_EQUAL;
		}
		if ("05".equals(name) || "大于".equals(name)) {
			return GREATER_THAN;
		}
		if ("06".equals(name) || "大于等于".equals(name)) {
			return GREATER_THAN_OR_EQUAL;
		}
		if ("07".equals(name) || "小于".equals(name)) {
			return LESS_THAN;
		}
		if ("08".equals(name) || "小于等于".equals(name)) {
			return LESS_THAN_OR_EQUAL;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case IN:
			return "01";
		case NOT_IN:
			return "02";
		case EQUAL:
			return "03";
		case NOT_EQUAL:
			return "04";
		case GREATER_THAN:
			return "05";
		case GREATER_THAN_OR_EQUAL:
			return "06";
		case LESS_THAN:
			return "07";
		case LESS_THAN_OR_EQUAL:
			return "08";
		case ERROR:
			return "ERROR";
		default:
			return "";
		}
	}
}
