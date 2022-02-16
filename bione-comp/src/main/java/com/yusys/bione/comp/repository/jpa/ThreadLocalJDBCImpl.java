package com.yusys.bione.comp.repository.jpa;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.yusys.bione.comp.common.ThreadLocalRountingDataSource;

public class ThreadLocalJDBCImpl {

	/**
	 * 目前只支持postgresql，并且无values参数
	 */
	public static List<Object[]> queryWithNameParamAndDialect(final String sql, final int firstResult, final int maxResult) {
		StringBuilder sb = new StringBuilder(sql);
		sb.append(" limit ").append(maxResult).append(" offset ").append(firstResult);
		DataSource dataSource = ThreadLocalRountingDataSource.getDataSource();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.query(sb.toString(), new RowMapper<Object[]>() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNumber) throws SQLException {
				ResultSetMetaData metadata = rs.getMetaData();
				List<Object> list = new ArrayList<Object>();
				int columnCount = metadata.getColumnCount();
				for (int i = 1; i <= columnCount; i ++) {
					int type = metadata.getColumnType(i);
					switch (type) {
					case Types.BIGINT:
					case Types.DECIMAL:
					case Types.NUMERIC:
					case Types.REAL:
						list.add(rs.getBigDecimal(i));
						break;
					case Types.BIT:
					case Types.BOOLEAN:
						list.add(rs.getByte(i));
						break;
					case Types.BLOB:
						list.add(rs.getBlob(i));
						break;
					case Types.CHAR:
					case Types.LONGNVARCHAR:
					case Types.LONGVARCHAR:
					case Types.NCHAR:
					case Types.NVARCHAR:
					case Types.VARCHAR:
						list.add(rs.getString(i));
						break;
					case Types.CLOB:
						list.add(rs.getClob(i));
						break;
					case Types.DATE:
						list.add(rs.getDate(i));
						break;
					case Types.DOUBLE:
						list.add(rs.getDouble(i));
						break;
					case Types.FLOAT:
						list.add(rs.getFloat(i));
						break;
					case Types.INTEGER:
						list.add(rs.getInt(i));
						break;
					case Types.NCLOB:
						list.add(rs.getNClob(i));
						break;
					case Types.NULL:
						list.add(null);
						break;
					case Types.OTHER:
						list.add(rs.getObject(i));
						break;
					case Types.SMALLINT:
					case Types.TINYINT:
						list.add(rs.getShort(i));
						break;
					case Types.TIME:
						list.add(rs.getTime(i));
						break;
					case Types.TIMESTAMP:
						list.add(rs.getTimestamp(i));
						break;
					default:
						throw new UnsupportedOperationException("");
					}
				}
				return list.toArray(new Object[0]);
			}
		});
	}

	public static Object queryWithNameParamAndDialectWithSingleResult(final String sql) {
		DataSource dataSource = ThreadLocalRountingDataSource.getDataSource();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.query(sql, new RowMapper<Object>() {
			@Override
			public Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
				return rs.getObject(1);
			}
		}).get(0);
	}

}
