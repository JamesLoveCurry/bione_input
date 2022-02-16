/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package com.yusys.bione.comp.repository.mybatis.dialect;

import com.yusys.bione.comp.utils.SQLUtils;

import java.sql.Connection;

/**
 * POSTGRESQL方言的实现
 * 
 * @author yangyf
 * @version 1.0 2018-05-30 下午12:31
 */
public class PostgreSQLDialect implements Dialect {

	@Override
	public String getLimitString(String sql, int offset, int limit) {
		return getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
	}

	public boolean supportsLimit() {
		return true;
	}

	/**
	 * 将sql变成分页sql语句,提供将offset及limit使用占位符号(placeholder)替换.
	 * 
	 * <pre>
	 * 如POSTGRESQL
	 * dialect.getLimitString("select * from user", 12, ":offset",0,":limit") 将返回
	 * select * from user limit :offset,:limit
	 * </pre>
	 * 
	 * @param sql
	 *            实际SQL语句
	 * @param offset
	 *            分页开始纪录条数
	 * @param offsetPlaceholder
	 *            分页开始纪录条数－占位符号
	 * @param limitPlaceholder
	 *            分页纪录条数占位符号
	 * @return 包含占位符的分页sql
	 */
	public String getLimitString(String sql, int offset, String offsetPlaceholder, String limitPlaceholder) {
		StringBuilder stringBuilder = new StringBuilder(sql);
		stringBuilder.append(" limit " + limitPlaceholder + "offset " + offset);
		return stringBuilder.toString();
	}

	@Override
	public String getCountString(String sql) {
		return SQLUtils.buildCountSQL(sql);
	}

	@Override
	public String getSortString(String sql, String orderBy, String orderType) {
		if (sql.contains("order by")) {
			return sql;
		} else {
			return sql + " order by " + orderBy + " " + orderType;
		}
	}

	@Override
	public String getFilterString(String sql, String conditionSql) {
		StringBuilder newSql = new StringBuilder("");
		if(sql.contains("group by")){
			String beforeSql = sql.substring(0, sql.lastIndexOf("group by"));
			String groupbySql = sql.substring(sql.lastIndexOf("group by")+9);
			if(groupbySql.contains("where")){
				newSql.append(sql);
				newSql.append(" and " + conditionSql);
			}else{
				newSql.append(beforeSql);
				if(sql.contains("where")){
					newSql.append(" and " + conditionSql);
					newSql.append(" group by " + groupbySql);
				}else{
					newSql.append(" where 1=1 and " + conditionSql);
					newSql.append(" group by " + groupbySql);
				}
			}
		}else{
			newSql.append(sql);
			if(sql.contains("where")){
				newSql.append(" and " + conditionSql);
			}else{
				newSql.append(" where 1=1 and " + conditionSql);
			}
		}
		return newSql.toString();
	}

	@Override
	public String getDateString(String date) {
		return "'" + date + "'";
	}

	/* (non-Javadoc)
	 * @see com.yusys.bione.comp.repository.mybatis.dialect.Dialect#getTableMetaDataSql(java.sql.Connection, java.lang.String[])
	 */
	@Override
	public String getTableMetaDataSql(Connection conn, String... tableNames) {
		StringBuilder sql = new StringBuilder();
		sql.append("select current_schema()  as schemaname, tablename as tablename, obj_description(relfilenode, 'pg_class') as comments from pg_tables a, pg_class b where a.tablename = b.relname and a.schemaname = current_schema()");
		if (tableNames.length > 0) {
			sql.append(" where 1 = 2 ");
			for (String tableName : tableNames) {
				sql.append(" or (table_name like '%" + tableName.toLowerCase() + "%')");
			}
		}
		return sql.toString().toUpperCase();
	}

	/* (non-Javadoc)
	 * @see com.yusys.bione.comp.repository.mybatis.dialect.Dialect#getTableMetaDataSqlWithColumns(java.sql.Connection, java.lang.String[])
	 */
	@Override
	public String getTableMetaDataSqlWithColumns(String schemaNm,Connection conn,
			String... columnNames) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yusys.bione.comp.repository.mybatis.dialect.Dialect#getColumnMetaDataSql(java.sql.Connection, java.lang.String[])
	 */
	@Override
	public String getColumnMetaDataSql(Connection conn, String... tableNames) {
		// TODO Auto-generated method stub
		return null;
	}
}
