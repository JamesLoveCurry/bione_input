/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package com.yusys.bione.comp.repository.mybatis.dialect;

import java.sql.Connection;

/**
 * 类似hibernate的Dialect,但只精简出分页部分
 * 
 * @author poplar.yfyang
 * @version 1.0 2011-11-18 下午12:31
 * @since JDK 1.5
 */
public interface Dialect {

	/**
	 * 数据库本身是否支持分页当前的分页查询方式 如果数据库不支持的话，则不进行数据库分页
	 * 
	 * @return true：支持当前的分页查询方式
	 */
	public boolean supportsLimit();

	/**
	 * 将sql转换为分页SQL，分别调用分页sql
	 * 
	 * @param sql
	 *            SQL语句
	 * @param offset
	 *            开始条数
	 * @param limit
	 *            每页显示多少纪录条数
	 * @return 分页查询的sql
	 */
	public String getLimitString(String sql, int offset, int limit);

	/**
	 * 将sql转换为总记录数SQL
	 * 
	 * @param sql
	 *            SQL语句
	 * @return 总记录数的sql
	 */
	public String getCountString(String sql);

	/**
	 * 将sql转换为带排序的SQL
	 * 
	 * @param sql
	 *            SQL语句
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            升序降序类型
	 * @return 生成后带orderBy的sql
	 */
	public String getSortString(String sql, String orderBy, String orderType);

	/**
	 * 将sql转换为带过滤条件的SQL
	 * 
	 * @param conditionSql
	 *            条件sql
	 * 
	 * @return 生成后带过滤条件的sql
	 */
	public String getFilterString(String sql, String conditionSql);

	/**
	 * 获得数据库的日期查询字符串
	 * 
	 * @param date
	 *            日期字符串
	 * 
	 * @return 查询字符串
	 */
	public String getDateString(String date);
	
	/**
	 * 获取数据库表的元数据信息
	 * @return
	 */
	public String getTableMetaDataSql(Connection conn, String... tableNames);
	
	/**
	 * 获取包含指定列的表元数据信息
	 * @param conn
	 * @param column
	 * @return
	 */
	public String getTableMetaDataSqlWithColumns(String schemaNm,Connection conn, String... columnNames);
	
	/**
	 * 获取指定表的列元数据信息
	 * @param conn
	 * @param tableNames
	 * @return
	 */
	public String getColumnMetaDataSql(Connection conn, String... tableNames);
}
