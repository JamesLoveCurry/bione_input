package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 是、否；启用、停用
 */
public enum YesOrNo {
	EMPTY, NO, YES, ERROR;
	
	public static YesOrNo get(String name) {
		name = StringUtils.trimToEmpty(name);
		if (name.length() == 0) {
			return EMPTY;
		}
		if ("Y".equalsIgnoreCase(name) || "1".equalsIgnoreCase(name) || "true".equalsIgnoreCase(name)
				|| "是".equals(name) || "启用".equals(name)) {
			return YES;
		}
		if ("N".equalsIgnoreCase(name) || "0".equalsIgnoreCase(name) || "false".equalsIgnoreCase(name)
				|| "否".equals(name) || "停用".equals(name)) {
			return NO;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case NO:
			return "N";
		case YES:
			return "Y";
		case ERROR:
			return "ERROR";
		default:
			return "";
		}
	}

	public boolean valid(){
		switch (this) {
			case NO:
			case YES:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
