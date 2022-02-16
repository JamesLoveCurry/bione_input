package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 模板类型：明细报表、单元格报表、综合报表、纵向报表列表、横向报表列表、交叉列表
 */
public enum TemplateType {
	DETAIL, INDEX, MIX, VLIST, HLIST, CROSS, ERROR;
	
	public static TemplateType get(String name) {
		name = StringUtils.trimToEmpty(name);
		if ("01".equals(name) || "明细报表".equals(name)) {
			return DETAIL;
		}
		if ("02".equals(name) || "单元格报表".equals(name)) {
			return INDEX;
		}
		if ("03".equals(name) || "综合报表".equals(name)) {
			return MIX;
		}
		if ("04".equals(name) || "纵向报表列表".equals(name)) {
			return VLIST;
		}
		if ("05".equals(name) || "横向报表列表".equals(name)) {
			return HLIST;
		}
		if ("06".equals(name) || "交叉列表".equals(name)) {
			return CROSS;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case DETAIL:
			return "01";
		case INDEX:
			return "02";
		case MIX:
			return "03";
		case VLIST:
			return "04";
		case HLIST:
			return "05";
		case CROSS:
			return "06";
		default:
			return "";	
		}
	}

	public boolean valid(){
		switch (this) {
			case DETAIL:
			case INDEX:
			case MIX:
			case VLIST:
			case HLIST:
			case CROSS:
				return true;
			case ERROR:
			default:
				return false;
		}
	}
}
