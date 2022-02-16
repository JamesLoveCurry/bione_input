package com.yusys.bione.plugin.regulation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 业务类型：行内、1104、人行大集中
 */
public enum BusiType {
	INTERNEL, _1104, RENHANG, EAST, INTERESTRATE, PAYMENT, DEPOSI, POVERTY, ASSET, FINANCE,ERROR;
	
	public static BusiType get(String name) {
		name = StringUtils.trimToEmpty(name);
		if ("01".equals(name) || "其他报表".equals(name)) {
			return INTERNEL;
		}
		if ("02".equals(name) || "1104报表".equals(name)) {
			return _1104;
		}
		if ("03".equals(name) || "人行大集中".equals(name)) {
			return RENHANG;
		}
		if ("04".equals(name) || "EAST".equals(name)) {
			return EAST;
		}
		if ("05".equals(name) || "利率报备".equals(name)) {
			return INTERESTRATE;
		}
		if ("06".equals(name) || "PISA报送".equals(name)) {
			return PAYMENT;
		}
		if ("10".equals(name) || "存款保险".equals(name)) {
			return DEPOSI;
		}
		if ("13".equals(name) || "精准扶贫".equals(name)) {
			return POVERTY;
		}
		if ("14".equals(name) || "资管产品".equals(name)) {
			return ASSET;
		}
		if ("16".equals(name) || "理财登记".equals(name)) {
			return FINANCE;
		}
		return ERROR;
	}

	@Override
	public String toString() {
		switch (this) {
		case INTERNEL:
			return "01";
		case _1104:
			return "02";
		case RENHANG:
			return "03";
		case EAST:
			return "04";
		case INTERESTRATE:
			return "05";
		case PAYMENT:
			return "06";
		case DEPOSI:
			return "10";
		case POVERTY:
			return "13";
		case ASSET:
			return "14";
		case FINANCE:
			return "16";
		default:
			return "";	
		}
	}

	public boolean valid(){
		switch (this) {
			case INTERNEL:
			case _1104:
			case RENHANG:
			case EAST:
			case INTERESTRATE:
			case PAYMENT:
			case DEPOSI:
			case POVERTY:
			case ASSET:
			case FINANCE:
				return true;
			case ERROR:
			default:
				return false;
				
		}
	}
}
