package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 排序方式：顺序、倒序
 */
public enum SortMode {
	EMPTY, ASC, DESC, ERROR;
	
	public static SortMode get(String name) {
		name = StringUtils.trimToEmpty(name);
		if (name.length() == 0) {
			return EMPTY;
		}
		if ("01".equals(name) || "顺序".equals(name)) {
			return ASC;
		}
		if ("02".equals(name) || "倒序".equals(name)) {
			return DESC;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case ASC:
			return "01";
		case DESC:
			return "02";
		case ERROR:
			return "ERROR";
		default:
			return "";	
		}
	}

	public String toCString() {
		switch (this) {
			case ASC:
				return "顺序";
			case DESC:
				return "倒序";
			case ERROR:
				return "未知";
			default:
				return "";
		}
	}

	public boolean valid(){
		switch (this) {
			case ASC:
			case DESC:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
