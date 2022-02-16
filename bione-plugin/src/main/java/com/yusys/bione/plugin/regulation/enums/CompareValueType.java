package com.yusys.bione.plugin.regulation.enums;

public enum CompareValueType {
	YESTERDAY, MONTH_START, LAST_MONTH_END, LAST_MONTH_SAMEDAY, QUARTER_START, LAST_QUARTER_END,
	YEAR_START, LAST_YEAR_END, LAST_YEAR_SAMEDAY, ERROR;
	
	public static CompareValueType get(String name) {
		if ("01".equals(name) || "上日".equals(name)) {
			return YESTERDAY;
		}
		if ("02".equals(name) || "月初".equals(name)) {
			return MONTH_START;
		}
		if ("03".equals(name) || "上月末".equals(name)) {
			return LAST_MONTH_END;
		}
		if ("04".equals(name) || "上月同期".equals(name)) {
			return LAST_MONTH_SAMEDAY;
		}
		if ("05".equals(name) || "季初".equals(name)) {
			return QUARTER_START;
		}
		if ("06".equals(name) || "上季末".equals(name)) {
			return LAST_QUARTER_END;
		}
		if ("07".equals(name) || "年初".equals(name)) {
			return YEAR_START;
		}
		if ("08".equals(name) || "上年末".equals(name)) {
			return LAST_YEAR_END;
		}
		if ("09".equals(name) || "上年同期".equals(name)) {
			return LAST_YEAR_SAMEDAY;
		}
		return ERROR;
	}
	
	@Override
	public String toString() {
		switch (this) {
		case YESTERDAY:
			return "01";
		case MONTH_START:
			return "02";
		case LAST_MONTH_END:
			return "03";
		case LAST_MONTH_SAMEDAY:
			return "04";
		case QUARTER_START:
			return "05";
		case LAST_QUARTER_END:
			return "06";
		case YEAR_START:
			return "07";
		case LAST_YEAR_END:
			return "08";
		case LAST_YEAR_SAMEDAY:
			return "09";
		default:
			return "";
		}
	}
}
