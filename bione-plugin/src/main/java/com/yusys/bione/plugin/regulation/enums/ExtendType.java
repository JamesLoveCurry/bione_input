package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 扩展类型：范围扩展、自增扩展
 */
public enum ExtendType {
	EMPTY, RANGEVERTICAL, INCREASEHORIZONTAL, RANGEHORIZONTAL, INCREASEVERTICAL, ERROR;
	
	public static ExtendType get(String name) {
		name = StringUtils.trimToEmpty(name);
		if (name.length() == 0) {
			return EMPTY;
		}
		if ("01".equals(name) || "范围扩展(纵)".equals(name)) {
			return RANGEVERTICAL;
		}
		if ("02".equals(name) || "自增扩展(纵)".equals(name)) {
			return INCREASEHORIZONTAL;
		}
		if ("03".equals(name) || "范围扩展(横)".equals(name)) {
			return RANGEHORIZONTAL;
		}
		if ("04".equals(name) || "自增扩展(横)".equals(name)) {
			return INCREASEVERTICAL;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case RANGEVERTICAL:
			return "01";
		case INCREASEHORIZONTAL:
			return "02";
		case RANGEHORIZONTAL:
			return "03";
		case INCREASEVERTICAL:
			return "04";
		case ERROR:
			return "";
		default:
			return "";	
		}
	}

	public String toCString(){
		switch (this) {
			case RANGEVERTICAL:
				return "范围扩展(纵)";
			case INCREASEHORIZONTAL:
				return "自增扩展(纵)";
			case RANGEHORIZONTAL:
				return "范围扩展(横)";
			case INCREASEVERTICAL:
				return "自增扩展(横)";
			case ERROR:
				return "未知";
			default:
				return "";
		}
	}

	public boolean valid(){
		switch (this) {
			case RANGEVERTICAL:
			case INCREASEHORIZONTAL:
			case RANGEHORIZONTAL:
			case INCREASEVERTICAL:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
