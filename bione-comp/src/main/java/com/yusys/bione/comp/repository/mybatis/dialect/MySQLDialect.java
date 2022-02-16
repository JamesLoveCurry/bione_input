/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package com.yusys.bione.comp.repository.mybatis.dialect;

import java.sql.Connection;
import java.sql.SQLException;




import com.yusys.bione.comp.utils.SQLUtils;

/**
 * Mysql方言的实现
 * 
 * @author poplar.yfyang
 * @version 1.0 2010-10-10 下午12:31
 * @since JDK 1.5
 */
public class MySQLDialect implements Dialect {

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
	 * 如mysql
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
		stringBuilder.append(" limit ");
		if (offset > 0) {
			stringBuilder.append(offsetPlaceholder).append(",").append(limitPlaceholder);
		} else {
			stringBuilder.append(limitPlaceholder);
		}
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
		String schemaName = null;
		String databaseName = null;
		try {
			schemaName = conn.getCatalog();
			databaseName = conn.getCatalog();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select '" + schemaName + "' as schemaname, table_name as tablename, table_comment as comments from information_schema.tables");
		if (tableNames.length > 0) {
			sql.append(" where 1 = 2 ");
			for (String tableName : tableNames) {
				sql.append(" or (table_name like '%" + tableName + "%' and  table_schema = '"+ databaseName +"')");
			}
		}else{
			sql.append(" where  table_schema = '"+ databaseName + "'");
		}
		return sql.toString().toUpperCase();
	}

	/* (non-Javadoc)
	 * @see com.yusys.bione.comp.repository.mybatis.dialect.Dialect#getTableMetaDataSqlWithColumns(java.sql.Connection, java.lang.String[])
	 */
	@Override
	public String getTableMetaDataSqlWithColumns(String schemaNm,Connection conn,
			String... columnNames) {
		String schemaName = schemaNm;
		String databaseName = null;
		try {
			schemaName = conn.getMetaData().getUserName();
			databaseName = conn.getCatalog();
		} catch (SQLException e) {
				e.printStackTrace();
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select '" + schemaName + "' as schemaname, table_name as tablename, table_comment as comments from information_schema.tables");
		if (columnNames.length > 0) {
			sql.append(" where table_schema = '"+ databaseName +"' and (table_name in (select table_name from information_schema.`COLUMNS` where 1 = 2");
			for (String columnName : columnNames) {
				sql.append(" or column_name = '" + columnName + "'");
			}
			sql.append(" group by table_name having count(*) >= " + columnNames.length + "))");
		}
		/**
         * 过滤模板选表的 校验表和删除表
         */
        sql.append(" AND TABLE_NAME NOT LIKE '%_DELETE%' ");
        sql.append(" AND TABLE_NAME NOT LIKE '%_VALIDATE%' ");
        
		// 对于Oracle, DB2数据库toUpperCase()很重要.
		return sql.toString().toUpperCase();
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
