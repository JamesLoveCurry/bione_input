package com.yusys.bione.comp.repository.mybatis.dialect;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;

import com.yusys.bione.comp.repository.jdbc.entity.BioneColumnMetaData;
import com.yusys.bione.comp.utils.SQLUtils;

/**
 * A dialect compatible with the H2 database.
 * 
 * @author poplar.yfyang
 * @version 1.0 2010-10-10 下午12:31
 * @since JDK 1.5
 */
public class H2Dialect implements Dialect {

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
	 * @param limit
	 *            分页每页显示纪录条数
	 * @param limitPlaceholder
	 *            分页纪录条数占位符号
	 * @return 包含占位符的分页sql
	 */
	private String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder) {
		return sql
				+ ((offset > 0) ? " limit " + limitPlaceholder + " offset " + offsetPlaceholder : " limit "
						+ limitPlaceholder);
	}

	@Override
	public String getLimitString(String sql, int offset, int limit) {
		return getLimitString(sql, offset, Integer.toString(offset), limit, Integer.toString(limit));
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
		return "date '" + date + "'";
	}
	
	public <T> List<T> getColumnMetaData(Connection con, Class<T> entityClass, String... tableNames) {
		String sql = this.getColumnMetaDataSql(con, tableNames); 
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			return new RowMapperResultSetExtractor<T>(new BeanPropertyRowMapper<T>(entityClass)).extractData(rs);
		} catch (SQLException e) {
			
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(stmt);
		}
		return null;
	}
	/***********************************************************************************************************/

	/* (non-Javadoc)
	 * @see com.yusys.bione.comp.repository.mybatis.dialect.Dialect#getTableMetaDataSql(java.sql.Connection, java.lang.String[])
	 */
	@Override
	public String getTableMetaDataSql(Connection conn, String... tableNames) {
		// TODO Auto-generated method stub
		return null;
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