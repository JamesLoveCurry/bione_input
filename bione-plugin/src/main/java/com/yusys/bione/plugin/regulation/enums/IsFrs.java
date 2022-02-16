package com.yusys.bione.plugin.regulation.enums;

/**
 * 预警校验-监管要求
 * @author maojin2
 *
 */
public enum IsFrs {
	YES, NO, ERROR;
	
	public static IsFrs get(String name) {
		if ("01".equals(name) || "是".equals(name)) {
			return YES;
		}
		if ("02".equals(name) || "否".equals(name)) {
			return NO;
		}
		return ERROR;
	}
	
	@Override
	public String toString() {
		switch (this) {
		case YES:
			return "01";
		case NO:
			return "02";
		default:
			return "";
		}
	}
}
