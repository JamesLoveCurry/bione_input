package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 扩展方向：行扩展、列扩展
 */
public enum ExtendDirection {
	EXTEND_ROW, EXTEND_COLUMN, ERROR;
	
	public static ExtendDirection get(String name) {
		name = StringUtils.trimToEmpty(name);
		if ("01".equals(name) || "行扩展".equals(name)) {
			return EXTEND_ROW;
		}
		if ("02".equals(name) || "列扩展".equals(name)) {
			return EXTEND_COLUMN;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case EXTEND_ROW:
			return "01";
		case EXTEND_COLUMN:
			return "02";
		default:
			return "";	
		}
	}

	public String toCString(){
		switch (this) {
			case EXTEND_ROW:
				return "行扩展";
			case EXTEND_COLUMN:
				return "列扩展";
			case ERROR:
				return "未知";
			default:
				return "";
		}
	}

	public boolean valid(){
		switch (this) {
			case EXTEND_ROW:
			case EXTEND_COLUMN:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
