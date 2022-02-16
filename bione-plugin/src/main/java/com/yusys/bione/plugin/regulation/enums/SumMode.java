package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 汇总方式：不汇总、求和
 */
public enum SumMode {
	EMPTY, NO_SUM, SUM, ERROR;
	
	public static SumMode get(String name) {
		name = StringUtils.trimToEmpty(name);
		if (name.length() == 0) {
			return EMPTY;
		}
		if ("00".equals(name) || "不汇总".equals(name)) {
			return NO_SUM;
		}
		if ("01".equals(name) || "求和".equals(name)) {
			return SUM;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case NO_SUM:
			return "00";
		case SUM:
			return "01";
		case ERROR:
			return "ERROR";
		default:
			return "";	
		}
	}
}
