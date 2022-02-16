package com.yusys.bione.plugin.base.utils;

import java.util.Map;

/**
 * 标明本对象有XML节点属性
 */
public interface XmlAttribute {

	/**
	 * 获得XML节点属性
	 */
	public Map<String, String> getAttrs();
	
	/**
	 * 设置XML节点属性
	 */
	public void setAttrs(Map<String, String> attrs);
}
