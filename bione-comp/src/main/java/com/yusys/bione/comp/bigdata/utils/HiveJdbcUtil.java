package com.yusys.bione.comp.bigdata.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.yusys.bione.comp.utils.PropertiesUtils;


/**
 * @描述 Hive的JDBC简单操作封装，包括获取数据库连接，关闭数据库对象
 * @author wzy
 * @date 2016-07-15
 * @修改记录 wzy,20160902,增加异常处理，修改获取keyberUser，KerbUserFileName参数的key（名称）
 */
public abstract class HiveJdbcUtil {
	// 日志对象
	private static Logger log = Logger.getLogger(HiveJdbcUtil.class);
	
	private static PropertiesUtils pUtils = PropertiesUtils.get("database.properties");

	// 静态初始化块，加载数据库驱动
	static {
		try {
//			String keyberUser = pUtils.getProperty("username.client.kerberos.principal");
//	String KerbUserFileName =  pUtils.getProperty("username.client.keytab.file");
	Class.forName(pUtils.getProperty("rpt.hive.jdbc.driverClassName"));
//	conf.set("hadoop.security.authentication", "kerberos");
//	UserGroupInformation.setConfiguration(conf);
//	UserGroupInformation.loginUserFromKeytab(
//			keyberUser,
//			HBaseConfig.class.getClassLoader()
//					.getResource(KerbUserFileName).getPath());
			
		} catch (ClassNotFoundException e) {
			log.error("加载Hive JDBC驱动出错[ClassNotFoundException]："
					+ e.getMessage());
			e.printStackTrace();
//		} catch (IOException e) {
//			log.error("加载Hive JDBC驱动出错[IOException]：" + e.getMessage());
//			e.printStackTrace();
		} catch (Exception e) {
			log.error("加载Hive JDBC驱动出错[Exception]：" + e.getMessage());
			e.printStackTrace();
		}
	}

	
	// 获取database.properties中默认的数据库连接
	public static Connection getConnection() throws ClassNotFoundException {
		Connection con = null;
		try {
				Class.forName(pUtils.getProperty("rpt.hive.jdbc.driverClassName"));
			
			String jdbcURL = pUtils.getProperty("rpt.hive.jdbc.dbUrl");
			con = DriverManager.getConnection(jdbcURL);
		} catch (SQLException e) {
			log.error("通过Hive JDBC获取数据库连接出错[SQLException]：" + e.getMessage());
			e.printStackTrace();
		}
		return con;
	}

	// 根据URL获取数据库连接
	public static Connection getConnection(String jdbcURL,String driverName) {
		Connection con = null;
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection(jdbcURL);
		} catch (SQLException e) {
			log.error("通过Hive JDBC获取数据库连接出错[SQLException]：" + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * LDAP获取数据库连接
	 * @param jdbcURL
	 * @param driverName
	 * @param name
	 * @param pwd
	 * @return
	 */
	public static Connection getConnection(String jdbcURL, String driverName, String name, String pwd){
		Connection conn = null;
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(jdbcURL, name, pwd);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			log.error("通过Hive JDBC获取数据库连接出错[SQLException]：" + e.getMessage());
			e.printStackTrace();
		}
		return conn;
	}
	
	// 关闭ResultSet对象、Statement对象、Connection对象
	public static void close(ResultSet rs, Statement st, Connection con) {
		HiveJdbcUtil.closeResultSet(rs);
		HiveJdbcUtil.closeStatement(st);
		HiveJdbcUtil.closeConnection(con);
	}

	// 关闭Connection对象
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error("关闭数据库Connection对象出错[SQLException]：" + e.getMessage());
				e.printStackTrace();
			} catch (Throwable e) {
				log.error("关闭数据库Connection对象出错[Throwable]：" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	// 关闭Statement对象
	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				log.error("关闭数据库Statement对象出错[SQLException]：" + e.getMessage());
				e.printStackTrace();
			} catch (Throwable e) {
				log.error("关闭数据库Statement对象出错[Throwable]：" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	// 关闭ResultSet对象
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("关闭数据库Statement对象出错[SQLException]：" + e.getMessage());
				e.printStackTrace();
			} catch (Throwable e) {
				log.error("关闭数据库Statement对象出错[Throwable]：" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}