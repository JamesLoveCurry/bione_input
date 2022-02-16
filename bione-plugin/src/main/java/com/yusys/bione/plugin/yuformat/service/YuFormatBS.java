package com.yusys.bione.plugin.yuformat.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.SqlValidateUtils;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.HashVOStruct;
import com.yusys.bione.plugin.yuformat.utils.SQLBuilder;

/**
 * 工具类BS,按道理应该放在service目录下,但因为没有建一个通用/基础的service目录,所以先暂时存放在util目录下
 * 因为它只是为CabinBSUtil转调使用的(也可以看成是util工具类)，以后移位置也只影响这一个类.
 * 
 * @author kf0612
 */
@Service
@Transactional(readOnly = true)
public class YuFormatBS extends BaseBS<Object> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static HashVO[] allDataBaseHvs = null; //

	private Connection createTestConn() throws Exception {
		Properties props = new Properties();
		props.put("user", "dcp");
		props.put("password", "12345678");

		// 之前高并发时,报socket time out，网上查资料说要加上这几个参数!
		props.put("oracle.net.CONNECT_TIMEOUT", "10000000");
		props.put("oracle.net.READ_TIMEOUT", "300000");
		props.put("oracle.jdbc.ReadTimeout", "300000");

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection dbConn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.251.155:1521/ycorcl", props);

		return dbConn;
	}

	public Connection getConnection() {
		return DataSourceUtils.getConnection(this.jdbcBaseDAO.getDataSource()); //
	}

	// 计算出所有表格..
	public String[][] getAllSysTables() {
		java.sql.Connection conn = null;
		ResultSet rs = null; //
		try {
			long ll_1 = System.currentTimeMillis();
			conn = DataSourceUtils.getConnection(this.jdbcBaseDAO.getDataSource()); //
			DatabaseMetaData dbmd = conn.getMetaData(); // 整个数据库的源数据对象
			rs = dbmd.getTables(conn.getCatalog(), dbmd.getUserName(), "%", new String[] { "TABLE" }); // 用户名必须大写,之前遇到多次问题!

			ArrayList<String[]> list = new ArrayList<String[]>(); //
			// 循环处理
			while (rs.next()) {
				String str_tableName = rs.getString("TABLE_NAME"); // 表名
				String str_tabType = rs.getString("TABLE_TYPE"); // 类型
				String str_tabRemark = rs.getString("REMARKS"); // 说明
				list.add(new String[] { str_tableName.toLowerCase(), str_tabType, str_tabRemark }); // 表名统一转小写
			}
			String[][] str_data = list.toArray(new String[0][0]); //
			long ll_2 = System.currentTimeMillis(); //
			System.out.println("共取得系统表【" + str_data.length + "】张，耗时【" + (ll_2 - ll_1) + "】毫秒"); // 一般要3.5秒左右!
			return str_data; //
		} catch (Exception _ex) {
			logger.error("计算所所有系统表异常", _ex); //
			return null; //
		} finally {
			if (rs != null) {
				try {
					rs.close(); //
				} catch (Exception _exx) {
					_exx.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close(); //
				} catch (Exception _exx) {
					_exx.printStackTrace();
				}
			}
		}
	}

	public HashVO[] getHashVOs(String _ds, String _sql, Object[] _objs, boolean _isDebugLog) {
		HashVOStruct hvst = getHashVOStruct(_ds, _sql, _objs, _isDebugLog); //
		if (hvst == null) {
			return null;
		} else {
			return hvst.getHashVOs(); //
		}
	}

	/**
	 * 根据SQL获得带表头结构的二维数据.
	 * 
	 * @param _sql
	 * @return
	 */
	public HashVOStruct getHashVOStruct(String _ds, String _sql, Object[] _objs, boolean _isDebugLog) {
		_sql = SqlValidateUtils.validateSql(_sql);
		if (_isDebugLog) { //
			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("★执行查询[" + _ds + "]【" + _sql + "】"); //
			if (_objs != null && _objs.length > 0) {
				for (int i = 0; i < _objs.length; i++) {
					sb_sql.append("【" + String.valueOf(_objs[i])); //
					if (_objs[i] != null) {
						sb_sql.append("/" + _objs[i].getClass().getName()); //
					}
					sb_sql.append("】"); //
				}
			}
			// System.err.println(sb_sql.toString()); //
			logger.info(sb_sql.toString()); // 改成logger,以前用System.err是因为想在开发时永远红色显示.毕竟SQL语句是最重要的信息
		}

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null; //
		ResultSet rs = null; //
		try {
			// 创建数据库连接,如果为空,则用默认数据源!
			if (_ds == null || _ds.trim().equals("")) {
				conn = DataSourceUtils.getConnection(this.jdbcBaseDAO.getDataSource());
				// int li_timeOut = conn.getNetworkTimeout(); //报错
				// System.err.println("Spring连接池的连接时长:" + li_timeOut); //
			} else {
				conn = DataSourceCache.getConnectByDSName(_ds); // 从连接池中获取!
			}

			if (_objs == null) { // 如果为空,则是标准
				stmt = conn.createStatement(); //
				// int li_timeout = stmt.getQueryTimeout(); //是零,即无限等待!
				// System.err.println("stmt连接池的连接时长:" + li_timeout); //
				rs = stmt.executeQuery(_sql); //
			} else { // 预编译
				stmt = conn.prepareStatement(_sql); //
				for (int i = 0; i < _objs.length; i++) {
					if (_objs[i] instanceof String) {
						((PreparedStatement) stmt).setString(i + 1, (String) _objs[i]); //
					} else if (_objs[i] instanceof Integer) {
						((PreparedStatement) stmt).setInt(i + 1, ((Integer) _objs[i]).intValue()); //
					} else if (_objs[i] instanceof Long) {
						((PreparedStatement) stmt).setLong(i + 1, ((Long) _objs[i]).longValue()); //
					} else if (_objs[i] instanceof Double) {
						((PreparedStatement) stmt).setDouble(i + 1, ((Double) _objs[i]).doubleValue()); //
					} else if (_objs[i] instanceof String[]) {
						Array itemArray = conn.createArrayOf("VARCHAR", (String[]) _objs[i]); //
						((PreparedStatement) stmt).setArray(i + 1, itemArray);
					} else { // 其他的当字符串处理...
						((PreparedStatement) stmt).setString(i + 1, String.valueOf(_objs[i])); //
					}
				}
				rs = ((PreparedStatement) stmt).executeQuery(); //
			}

			// 得到元数据!!!
			ResultSetMetaData rsmd = rs.getMetaData();

			String str_fromtablename = null; // 表名
			int li_columncount = rsmd.getColumnCount(); // 总共有几列
			String[] str_columnnames = new String[li_columncount]; // 每一列的名称!!
			int[] li_column_types = new int[li_columncount]; // 每一列的类型
			String[] str_column_typenames = new String[li_columncount]; // 每一列类型的名字
			int[] li_column_lengths = new int[li_columncount]; // 每一列的宽度
			int[] precision = new int[li_columncount];
			int[] scale = new int[li_columncount];

			//if (_ds == null) {
				for (int i = 1; i < li_columncount + 1; i++) { // 遍历各个列!!!
					if (i == 1) {
						try {
							str_fromtablename = rsmd.getTableName(i); // hive大数据竟然这行代码报错! 不支持这个功能？
						} catch (Exception _ex1) {
							System.err.println("取表名失败" + _ex1.getMessage()); //
						}
					}

					try {
						str_columnnames[i - 1] = rsmd.getColumnLabel(i).toLowerCase(); // 统一转小写!
						li_column_types[i - 1] = rsmd.getColumnType(i);
						str_column_typenames[i - 1] = rsmd.getColumnTypeName(i); //
						li_column_lengths[i - 1] = rsmd.getColumnDisplaySize(i); //
						// 注意，如果数据源为Oracle数据库，字段为Clob类型并且服务器端使用的jdk版本为1.6.0_26时下面一句代码则会报错，这时将jdk版本降低为1.6.0_18或jdk1.6.0_14就可以了
						precision[i - 1] = rsmd.getPrecision(i); // 精度
						scale[i - 1] = rsmd.getScale(i); //
					} catch (Exception _ex2) {
						System.err.println("计算各列失败:" + _ex2.getMessage()); //
					}
				}
			//}

			HashVOStruct hvst = new HashVOStruct(); // 创建HashVOStruct
			hvst.setTableName(str_fromtablename); // 设置从哪个表取数!!!!!
			hvst.setFromSQL(_sql); // 设置是从哪个SQL生成的!
			hvst.setHeaderName(str_columnnames); // 设置表头结构
			hvst.setHeaderType(li_column_types); // 表头字段类型!!!
			hvst.setHeaderTypeName(str_column_typenames);// 设置字段类型名称
			hvst.setHeaderLength(li_column_lengths); // 宽度
			hvst.setPrecision(precision);
			hvst.setScale(scale);
			HashVO rowHVO = null; //
			int li_rows = 0;
			long ll_allDataSize = 0;// 记录这次查询数据的大小!!!用于来判断是否会产生性能问题!!!
									// 比如发现一次远程调用中,发生的查询数据的内容非常大,甚至超过10M,则认为很可能会导致内存溢出!!!即OutOfMemory.
			Object value = null; //
			String str_colname = null; //
			int li_coltype = 0; //

			List<HashVO> al_rowData = new ArrayList<HashVO>(); // 以前是Vector,改成ArrayList,还是要快一点的!

			while (rs.next()) { // 遍历各行,循环取数!!
				li_rows++; // 累数器
				rowHVO = new HashVO(); //
				for (int i = 1; i < li_columncount + 1; i++) { // 遍历各列!
					str_colname = rsmd.getColumnLabel(i); // 列名
					li_coltype = rsmd.getColumnType(i); // 列类型
					value = null; //
					if (li_coltype == Types.VARCHAR) { // 如果是字符
						value = rs.getString(i);
					} else if (li_coltype == Types.NUMERIC) { // 如果是Number
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.DATE || li_coltype == Types.TIMESTAMP) { // 如果是日期或时间类型,统统精确到秒,Oracle中的Date类型是Types.DATE,但返回的值是Timestamp!!!
						value = rs.getTimestamp(i);
					} else if (li_coltype == Types.SMALLINT) { // 如果是整数
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Integer(bigDecimal.intValue()); //
						}
					} else if (li_coltype == Types.INTEGER) { // 如果是整数
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Long(bigDecimal.longValue()); // (rs.getBigDecimal(i)
						}
					} else if (li_coltype == Types.DECIMAL || li_coltype == Types.DOUBLE || li_coltype == Types.DOUBLE
							|| li_coltype == Types.FLOAT) {
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.CLOB) { // 如果是Clob类型,则要使用Read进行读取!!!
						java.sql.Clob clob = rs.getClob(i); // clob
						if (clob != null) {
							java.io.Reader inread = null;
							StringBuilder sb_aa = new StringBuilder(); // //
							try {
								inread = clob.getCharacterStream();
								char[] buf = new char[2048];
								int len = -1;
								while ((len = inread.read(buf)) != -1) {
									sb_aa.append(buf, 0, len); //
								}
							} finally {
								IOUtils.closeQuietly(inread);
							}
							value = new String(sb_aa.toString()); //
						}
					} else if (li_coltype == Types.BLOB) {
						Blob blob = rs.getBlob(i); //
						if (blob != null) {
							InputStream ins = blob.getBinaryStream(); // 得到输入流
							byte[] bys = readFromInputStreamToBytes(ins); //
							value = new String(bys, "UTF-8");
						}
					} else {
						value = rs.getString(i); // 其他类型直接转了符串
					}

					rowHVO.setAttributeValue(str_colname, value); // 设置字段值!!!

					// 计算内容的大小,用于判断一次远程调用中有无发生大数据量的查询!!!即可能会造成OutOfMemory的可能性!!!
					ll_allDataSize = ll_allDataSize + String.valueOf(value).length(); // 直接转String
																						// 算长度,有一定道理,因为普通数据类型就是这样!
				}
				// BS端数据权限过滤!!!
				al_rowData.add(rowHVO); // //
			} // end while..

			HashVO[] vos = al_rowData.toArray(new HashVO[0]); // 转换一把!!!
			hvst.setHashVOs(vos); // 设置数据!!

			hvst.setTotalRecordCount(li_rows); // 设置实际的总计记录数
			return hvst; // //
		} catch (Exception ex) {
			System.err.print("执行SQL异常[" + _sql + "]");
			ex.printStackTrace(); //
			// return null; //
			throw new RuntimeException("执行SQL【" + _sql + "】异常,原因:" + ex.getMessage() + "," + ex.getClass(), ex); //
		} finally {
			if (_ds == null || _ds.trim().equals("")) {
				JdbcUtils.closeResultSet(rs);
				JdbcUtils.closeStatement(stmt);
				DataSourceUtils.releaseConnection(conn, this.jdbcBaseDAO.getDataSource());
			} else {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception _exx) {
					_exx.printStackTrace();
				}

				try {
					if (stmt != null) {
						stmt.close();
					}
				} catch (Exception _exx) {
					_exx.printStackTrace();
				}

				try {
					if (conn != null) {
						conn.close(); //
					}
				} catch (Exception _exx) {
					_exx.printStackTrace();
				}
			}
		}
	}

	private byte[] readFromInputStreamToBytes(InputStream _ins) {
		if (_ins == null) {
			return null;
		}
		ByteArrayOutputStream bout = null; //
		try {
			bout = new ByteArrayOutputStream(); // Java官方网站强烈建议使用该对象读流数据,说是更健壮,更平缓,更稳定!!!因为它是一步步读的!对内存与硬盘消耗均友好!
			byte[] bys = new byte[2048]; //
			int pos = -1;
			while ((pos = _ins.read(bys)) != -1) { // 通过循环读取,更流畅,更稳定!!节约内存!
				bout.write(bys, 0, pos); //
			}
			byte[] returnBys = bout.toByteArray(); //
			return returnBys; //
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				if (bout != null) {
					bout.close(); // 关闭输出流!!!
				}
			} catch (Exception exx1) {
			}
			try {
				if (_ins != null) {
					_ins.close(); // 关闭输入流!!
				}
			} catch (Exception exx1) {
			}
		}
	}

	// 用?方式，提交PrepareStatement
	protected int executeUpdate(SQLBuilder _sqlBuilder) throws Exception {
		return 0; //
	}

	/**
	 * 根据SQL获得带表头结构的二维数据.
	 * 
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public int executeUpdate(String _sql) throws Exception {
		_sql = SqlValidateUtils.validateSql(_sql);
		logger.info("执行修改SQL【" + _sql + "】"); //
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null; //
		try {
			conn = DataSourceUtils.getConnection(this.jdbcBaseDAO.getDataSource());
			stmt = conn.createStatement(); //
			int li_rt = stmt.executeUpdate(_sql); // 执行SQL!!
			conn.commit(); //
			return li_rt; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				if (conn != null) {
					conn.rollback(); //
				}
			} catch (Exception _exx) {
			}
			throw new Exception("执行SQL发生异常【" + ex.getMessage() + "】,SQL是【" + _sql + "】"); //
		} finally {
			JdbcUtils.closeStatement(stmt);
			DataSourceUtils.releaseConnection(conn, this.jdbcBaseDAO.getDataSource());
		}
	}

	/**
	 * 根据SQL获得带表头结构的二维数据.
	 * 
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public int executeUpdate(String _ds, String _sql) throws Exception {
		_sql = SqlValidateUtils.validateSql(_sql);
		logger.info("执行修改SQL[" + _ds + "]【" + _sql + "】");
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null; //
		try {
			if (_ds == null || _ds.equals("")) {
				conn = DataSourceUtils.getConnection(this.jdbcBaseDAO.getDataSource());
			} else {
				conn = DataSourceCache.getConnectByDSName(_ds);
			}
			// 关闭自动提交
			conn.setAutoCommit(false);
			stmt = conn.createStatement(); //
			int li_rt = stmt.executeUpdate(_sql); // 执行SQL!!
			conn.commit(); //
			return li_rt; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				if (conn != null) {
					conn.rollback(); //
				}
			} catch (Exception _exx) {
			}
			throw new Exception("执行SQL发生异常【" + ex.getMessage() + "】,SQL是【" + _sql + "】", ex); //
		} finally {
			if (stmt != null) {
				try {
					stmt.close(); //
				} catch (Exception _ex) {
					_ex.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception _ex) {
					_ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 批量执行SQL
	 * 
	 * @param _sqls
	 * @return
	 * @throws Exception
	 */
	public int[] executeUpdate(String[] _sqls) throws Exception {
		for (int i = 0; i < _sqls.length; i++) {
			System.err.println("执行修改SQL【" + _sqls[i] + "】"); //
		}
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null; //
		try {

			conn = DataSourceUtils.getConnection(this.jdbcBaseDAO.getDataSource());
			stmt = conn.createStatement(); //
			for (int i = 0; i < _sqls.length; i++) {
				String sqlSr = SqlValidateUtils.validateSql(_sqls[i]);
				stmt.addBatch(sqlSr); // 加入Batch
			}
			int[] li_rt = stmt.executeBatch(); // 执行SQL!!
			conn.commit(); //
			return li_rt; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				if (conn != null) {
					conn.rollback(); //
				}
			} catch (Exception _exx) {
			}
			throw ex;
		} finally {
			JdbcUtils.closeStatement(stmt);
			DataSourceUtils.releaseConnection(conn, this.jdbcBaseDAO.getDataSource());
		}

	}
}
