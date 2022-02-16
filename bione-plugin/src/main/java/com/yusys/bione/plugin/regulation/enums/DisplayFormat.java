package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 显示格式：金额、比例、数值、文本、日期、百分点、小数
 */
public enum DisplayFormat {
	EMPTY, MONEY, RATIO, NUMBER, TEXT, DATE, PERCENT, DECIMAL, RATIO2, ERROR;
	
	public static DisplayFormat get(String name) {
		name = StringUtils.trimToEmpty(name);
		if (name.length() == 0) {
			return EMPTY;
		}
		if ("01".equals(name) || "金额".equals(name) || "原格式".equals(name)) {
			return MONEY;
		}
		if ("02".equals(name) || "比例".equals(name) || "百分比".equals(name)) {
			return RATIO;
		}
		if ("03".equals(name) || "数值".equals(name)) {
			return NUMBER;
		}
		if ("04".equals(name) || "文本".equals(name)) {
			return TEXT;
		}
		if ("05".equals(name) || "日期".equals(name)) {
			return DATE;
		}
		if ("06".equals(name) || "百分点".equals(name)) {
			return PERCENT;
		}
		if ("07".equals(name) || "小数".equals(name)) {
			return DECIMAL;
		}
		if ("08".equals(name) || "比例无百分号".equals(name)) {
			return RATIO2;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case MONEY:
			return "01";
		case RATIO:
			return "02";
		case NUMBER:
			return "03";
		case TEXT:
			return "04";
		case DATE:
			return "05";
		case PERCENT:
			return "06";
		case DECIMAL:
			return "07";
		case RATIO2:
			return "08";
		case ERROR:
			return "ERROR";
		default:
			return "";	
		}
	}

	public String toCString(){
		switch (this) {
			case MONEY:
				return "金额";
			case RATIO:
				return "百分比";
			case NUMBER:
				return "数值";
			case TEXT:
				return "文本";
			case DATE:
				return "日期";
			case PERCENT:
				return "百分点";
			case DECIMAL:
				return "小数";
			case RATIO2:
				return "百分比（无%）";
			case ERROR:
				return "未知";
			default:
				return "";
		}
	}

	public boolean valid(){
		switch (this) {
			case MONEY:
			case RATIO:
			case NUMBER:
			case TEXT:
			case DATE:
			case PERCENT:
			case DECIMAL:
			case RATIO2:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
