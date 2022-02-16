package com.yusys.bione.comp.repository.mybatis.dialect;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.comp.utils.SQLUtils;

/**
 * DB2的分页数据库方言实现
 * 
 * @author poplar.yfyang
 * @version 1.0 2010-10-10 下午12:31
 * @since JDK 1.5
 */
public class DB2Dialect implements Dialect {
	private static String getRowNumber(String sql) {
		StringBuilder rownumber = new StringBuilder(50).append("rownumber() over(");

		//int orderByIndex = sql.toLowerCase().indexOf("order by");

		/*if (orderByIndex > 0 && !hasDistinct(sql)) {
			rownumber.append(sql.substring(orderByIndex));
		}*/

		rownumber.append(") as rownumber_");

		return rownumber.toString();
	}

	/*private static boolean hasDistinct(String sql) {
		return sql.toLowerCase().contains("select distinct");
	}*/

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public String getLimitString(String sql, int offset, int limit) {
		return getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
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
		int startOfSelect = sql.toLowerCase().indexOf("select");

		StringBuilder pagingSelect = new StringBuilder(sql.length() + 100).append(sql.substring(0, startOfSelect)) // add
																													// the
																													// comment
				.append("select * from ( select "); // nest the main query in an
													// outer select
				

		pagingSelect.append(" row_.*,").append(getRowNumber(sql)).append(" from ( ") // add another (inner) nested
					.append(sql.substring(startOfSelect)) // add the main query
					.append(" ) as row_"); // close off the inner nested select

		pagingSelect.append(" ) as temp_ where rownumber_ ");

		// add the restriction to the outer select
		if (offset > 0) {
			// int end = offset + limit;
			String endString = offsetPlaceholder + "+" + limitPlaceholder;
			pagingSelect.append("between ").append(offsetPlaceholder).append("+1 and ").append(endString);
		} else {
			pagingSelect.append("<= ").append(limitPlaceholder);
		}

		return pagingSelect.toString();
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
		return "date('" + date + "')";
	}
	
	/**
	 * 获取数据库表的元数据信息
	 * @return
	 */
	@Override
	public String getTableMetaDataSql(Connection conn, String... tableNames) {
		String schemaName = null;
		try {
			schemaName = conn.getMetaData().getUserName();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select tabschema as schemaname, tabname as tablename, remarks as comments ");
		sql.append("from syscat.tables where tabschema = '" + schemaName + "'");
		if (tableNames.length > 0) {
			sql.append(" and (1 = 2");
			for (String tableName : tableNames) {
				sql.append(" or tabname like '%" + tableName + "%'");
			}
			sql.append(")");
		}
		// 对于Oracle, DB2数据库toUpperCase()很重要.
		return sql.toString().toUpperCase();
	}

	/**
	 * 获取包含指定列的表元数据信息
	 * @param conn
	 * @param column
	 * @return
	 */
	@Override
	public String getTableMetaDataSqlWithColumns(String schemaNm,Connection conn, String... columnNames) {
		String schemaName = schemaNm;
		if(StringUtils.isEmpty(schemaName)){
			try {
				schemaName = conn.getMetaData().getUserName();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select tabschema as schemaname, tabname as tablename, remarks as comments");
		sql.append(" from syscat.tables where tabschema = '" + schemaName + "'");
		if (columnNames.length > 0) {
			sql.append(" and tabname in (select tabname from syscat.columns where tabschema = '" + schemaName + "' and (1 = 2");
			for (String columnName : columnNames) {
				sql.append(" or colname = '" + columnName + "'");
			}
			sql.append(") group by tabname having count(*) >= " + columnNames.length + ")");
		}
		// 对于Oracle, DB2数据库toUpperCase()很重要.
		return sql.toString().toUpperCase();
	}

	/**
	 * 获取指定表的列元数据信息
	 * @param conn
	 * @param tableNames
	 * @return
	 */
	@Override
	public String getColumnMetaDataSql(Connection conn, String... tableNames) {
		String schemaName = null;
		try {
			schemaName = conn.getMetaData().getUserName();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select tabschema as schemaname, tabname as tablename,colname as columnname,typename as datatype, length as datalength,remarks as comments");
		sql.append(" from syscat.columns where tabschema = '" + schemaName + "'");
		if (tableNames.length > 0) {
			sql.append(" and (1 = 2");
			for (String tableName : tableNames) {
				sql.append(" or tabname like '%" + tableName + "%'");
			}
			sql.append(")");
		}
		// 对于Oracle, DB2数据库toUpperCase()很重要.
		return sql.toString().toUpperCase();
	}

}
