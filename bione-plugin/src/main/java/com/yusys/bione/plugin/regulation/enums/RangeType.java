package com.yusys.bione.plugin.regulation.enums;

public enum RangeType {
	NUMBER, PERCENT, ERROR;
	
	public static RangeType get(String name) {
		if ("01".equals(name) || "数字".equals(name)) {
			return NUMBER;
		}
		if ("02".equals(name) || "百分比".equals(name)) {
			return PERCENT;
		}
		return ERROR;
	}
	
	@Override
	public String toString() {
		switch (this) {
		case NUMBER:
			return "01";
		case PERCENT:
			return "02";
		default:
			return "";
		}
	}
}
