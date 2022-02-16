/**
 * 
 */
package com.yusys.bione.plugin.engine.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public enum RefreshType {
	
	REFRESH_TYPE_NODE("RefreshNodeInfo"), // 节点
	REFRESH_TYPE_DS("RefreshDSCache"),// 数据源
	REFRESH_TYPE_CFG("RefreshConfCache"), // 机构+维度+指标信息等配置信息
	REFRESH_TYPE_CFG_ONLYSERVER("RefreshConfCacheOnlyServer"),   // 机构+维度+指标信息等配置信息（只刷新server）
	REFRESH_TYPE_MODEL("RefreshModelCache"), // 没用 
	REFRESH_TYPE_INDEX("RefreshIndexCache");  // 指标事实表数据
	
	private final String code;
	/*
	 * 使用LinkedHashMap结构以保证按照定义的顺序执行相应的处理
	 */
	private static final Map<String, RefreshType> stringToEnum = new LinkedHashMap<String, RefreshType>();

	RefreshType(String code) {
		this.code = code;
	}

	static {
		for (RefreshType refreshType : values()) {
			stringToEnum.put(refreshType.toString(), refreshType);
		}
	}

	public static RefreshType getInstance(String code) {
		return stringToEnum.get(code);
	}

	@Override
	public String toString() {
		return this.code;
	}
}
