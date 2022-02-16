package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 报表周期：日、月、季、半年、年
 */
public enum RptCycle {
	DAY, MONTH, QUARTER, HALF_YEAR, YEAR, WEEK, TENDAYS, ERROR;
	
	public static RptCycle get(String name) {
		name = StringUtils.trimToEmpty(name);
		if ("01".equals(name) || "日".equals(name)) {
			return DAY;
		}
		if ("10".equals(name) || "周".equals(name)) {
			return WEEK;
		}
		if ("11".equals(name) || "旬".equals(name)) {
			return TENDAYS;
		}
		if ("02".equals(name) || "月".equals(name)) {
			return MONTH;
		}
		if ("03".equals(name) || "季".equals(name)) {
			return QUARTER;
		}
		if ("12".equals(name) || "半年".equals(name)) {
			return HALF_YEAR;
		}
		if ("04".equals(name) || "年".equals(name)) {
			return YEAR;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case DAY:
			return "01";
		case WEEK:
			return "10";
		case TENDAYS:
			return "11";
		case MONTH:
			return "02";
		case QUARTER:
			return "03";
		case HALF_YEAR:
			return "12";
		case YEAR:
			return "04";
		default:
			return "";	
		}
	}

	public boolean valid(){
		switch (this) {
			case DAY:
			case WEEK:
			case TENDAYS:
			case MONTH:
			case QUARTER:
			case HALF_YEAR:
			case YEAR:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
