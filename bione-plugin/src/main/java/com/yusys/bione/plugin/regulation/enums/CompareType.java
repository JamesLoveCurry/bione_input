package com.yusys.bione.plugin.regulation.enums;

/**
 * 预警校验-预警类型
 * @author maojin2
 *
 */
public enum CompareType {
	HBCOMPARE, TBCOMPARE, ERROR;
	
	public static CompareType get(String name) {
		if ("01".equals(name) || "环比".equals(name)) {
			return HBCOMPARE;
		}
		if ("02".equals(name) || "同比".equals(name)) {
			return TBCOMPARE;
		}
		return ERROR;
	}
	
	@Override
	public String toString() {
		switch (this) {
		case HBCOMPARE:
			return "01";
		case TBCOMPARE:
			return "02";
		default:
			return "";
		}
	}
}
