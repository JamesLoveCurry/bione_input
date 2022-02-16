package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 数值单位：元/个、百、千、万、亿、无单位
 */
public enum DataUnit {
	EMPTY, NULL, YUAN, HUNDRED, THOUSAND, TEN_THOUSAND, A_HUNDRED_MILLION, ERROR, RATIO;
	
	public static DataUnit get(String name) {
		name = StringUtils.trimToEmpty(name);
		if (name.length() == 0 || "模板单位".equals(name)) {
			return EMPTY;
		}
		if ("00".equals(name) || "无单位".equals(name)) {
			return NULL;
		}
		if ("01".equals(name) || "元".equals(name) || "个".equals(name)) {
			return YUAN;
		}
		if ("02".equals(name) || "百".equals(name)) {
			return HUNDRED;
		}
		if ("03".equals(name) || "千".equals(name)) {
			return THOUSAND;
		}
		if ("04".equals(name) || "万".equals(name)) {
			return TEN_THOUSAND;
		}
		if ("05".equals(name) || "亿".equals(name)) {
			return A_HUNDRED_MILLION;
		}
		if ("06".equals(name) || "百分比".equals(name)) {
			return RATIO;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case NULL:
			return "00";
		case YUAN:
			return "01";
		case HUNDRED:
			return "02";
		case THOUSAND:
			return "03";
		case TEN_THOUSAND:
			return "04";
		case A_HUNDRED_MILLION:
			return "05";
		case RATIO:
			return "06";
		case ERROR:
			return "ERROR";
		default:
			return "";
		}
	}

	public String toCString(){
		switch (this) {
			case EMPTY:
				return "模板单位";
			case NULL:
				return "无单位";
			case YUAN:
				return "元";
			case HUNDRED:
				return "百";
			case THOUSAND:
				return "千";
			case TEN_THOUSAND:
				return "万";
			case A_HUNDRED_MILLION:
				return "亿";
			case RATIO:
				return "百分比";
			case ERROR:
				return "未知";
			default:
				return "";
		}
	}

	public boolean valid(){
		switch (this) {
			case EMPTY:
			case NULL:
			case YUAN:
			case HUNDRED:
			case THOUSAND:
			case TEN_THOUSAND:
			case A_HUNDRED_MILLION:
			case RATIO:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
