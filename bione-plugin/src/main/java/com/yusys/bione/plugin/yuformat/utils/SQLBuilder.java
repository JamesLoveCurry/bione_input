package com.yusys.bione.plugin.yuformat.utils;

/**
 * 仿照MyBatis与Hibernate，然后结合大数据中Map的思想,动态对象拼SQL的工具接口
 * @author xch
 *
 */
public interface SQLBuilder {
	public void putFieldValue(String _fieldName, String _value);
	public void putFieldValue(String _fieldName, double _value);
	public String getSQL();
}
