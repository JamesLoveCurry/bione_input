package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 扩展方式：插入、覆盖
 */
public enum ExtendMode {
	INSERT, OVERLAP, ERROR;
	
	public static ExtendMode get(String name) {
		name = StringUtils.trimToEmpty(name);
		if ("01".equals(name) || "插入".equals(name)) {
			return INSERT;
		}
		if ("02".equals(name) || "覆盖".equals(name)) {
			return OVERLAP;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case INSERT:
			return "01";
		case OVERLAP:
			return "02";
		default:
			return "";	
		}
	}

	public String toCString() {
		switch (this) {
			case INSERT:
				return "插入";
			case OVERLAP:
				return "覆盖";
			case ERROR:
				return "未知";
			default:
				return "";
		}
	}

	public boolean valid(){
		switch (this) {
			case INSERT:
			case OVERLAP:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
