package com.yusys.bione.plugin.regulation.enums;

public enum LeverType {
	SYSTEM, CUSTOM, ERROR;
	
	public static LeverType get(String name) {
		if ("01".equals(name) || "内置分级".equals(name)) {
			return SYSTEM;
		}
		if ("02".equals(name) || "自定义分级".equals(name)) {
			return CUSTOM;
		}
		return ERROR;
	}
	
	@Override
	public String toString() {
		switch (this) {
		case SYSTEM:
			return "01";
		case CUSTOM:
			return "02";
		default:
			return "";
		}
	}
}
