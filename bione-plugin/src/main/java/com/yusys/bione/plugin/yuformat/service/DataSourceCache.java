package com.yusys.bione.plugin.yuformat.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.plugin.yuformat.utils.HashVO;

/**
 * 数据源缓存,即专门用来存储动态数据源，然后直接取动态数据源的类!!
 * @author xch
 *
 */
public class DataSourceCache {

	private static Logger logger = LoggerFactory.getLogger(DataSourceCache.class);

	private static DataSourceCache cacheInst; //

	private ConcurrentHashMap<String, BasicDataSource> dsMap = null; //存储Nmap

	//构造函数
	private DataSourceCache() {
		if (dsMap == null) {
			dsMap = new ConcurrentHashMap(); //
			HashVO[] hvs_ds = getAllDSVos(); //先从默认数据源bione_dsinfo表中找出所有动态数据源!
			if (hvs_ds != null) {
				for (int i = 0; i < hvs_ds.length; i++) {
					String str_ds = hvs_ds[i].getStringValue("ds_name"); //
					String str_driver_name = hvs_ds[i].getStringValue("driver_name"); //
					String str_conn_url = hvs_ds[i].getStringValue("conn_url"); //
					String str_conn_user = hvs_ds[i].getStringValue("conn_user"); //
					String str_conn_pwd = hvs_ds[i].getStringValue("conn_pwd"); //
					String str_validation_query = hvs_ds[i].getStringValue("validation_query"); //

					//以前直接dbcp创建连接池的方式!
					//createConnectPool(str_ds, str_driver_name, str_conn_url, str_conn_user, str_conn_pwd); //创建连接池!

					BasicDataSource ds = new BasicDataSource(); //
					ds.setDriverClassName(str_driver_name);
					ds.setUrl(str_conn_url);
					ds.setUsername(str_conn_user);
					ds.setPassword(str_conn_pwd);
					ds.setInitialSize(2);
					//ds.setMinIdle(100);
					ds.setMaxIdle(100);
					ds.setMaxActive(100);
					//ds.setRemoveAbandoned(true);
					//ds.setMaxWait(_maxWait);
					ds.setTestOnBorrow(true); //
					ds.setPoolPreparedStatements(true); //创建池!
					//ds.setMaxOpenPreparedStatements(100);
					ds.setValidationQuery(str_validation_query);
					
					//ds.addConnectionProperty("", ""); //★★
					dsMap.put(str_ds, ds); //置入缓存
					logger.info("创建数据源缓存【" + str_ds + "】【" + str_conn_url + "】");
				}
			}
		}
	}

	/**
	 * 从多个数据源的缓存中获取某个数据源中的连接.
	 * 每个数据源本身又是一个连接池!
	 * @param _ds
	 * @return
	 * @throws Exception
	 */
	public static java.sql.Connection getConnectByDSName(String _ds) throws Exception {
		BasicDataSource ds = getInstance().getDsMap().get(_ds); //
		if (ds == null) {
			logger.error("获取数据源【" + _ds + "】时为空!");
			return null; //
		}

		String str_url = ds.getUrl(); //
		try {
			logger.info("开始从数据源【" + _ds + "】【" + str_url + "】获取连接...");
			java.sql.Connection conn = ds.getConnection(); //这里才会实际构建池,并创建连接,但也可能抛异常!
			return conn; //
		} catch (Exception _ex) {
			logger.error("从数据源【" + _ds + "】【" + str_url + "】获取连接异常!原因:【" + _ex.getMessage() + "】");
			_ex.printStackTrace();
			return null; //
		}
	}

	//创建dbcp连接池,并以数据源名称注册好
	private void createConnectPool(String _ds, String _driver, String _dburl, String _user, String _pwd) {
		try {
			Class.forName(_driver); // 创建数据源
			GenericObjectPool connectionPool = new GenericObjectPool(null); //创建一个连接池!!
			connectionPool.setMaxActive(100); //一个池中最多活动连接数
			connectionPool.setMaxIdle(100); //
			//connectionPool.setMaxWait(1000);  //
			//connectionPool.setConfig(conf);
			Properties configProp = new Properties();
			configProp.setProperty("user", _user);
			configProp.setProperty("password", _pwd);

			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(_dburl, configProp); //创建工厂对象!
			PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true); //创建池工厂对象!
			Class.forName("org.apache.commons.dbcp.PoolingDriver"); // 创建dbcp池驱动
			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:"); //取得dbcp驱动!
			driver.registerPool(_ds, connectionPool); //注册连接池,以数据源名称来注册

			//默认先创建两个连接
			for (int i = 0; i < 2; i++) {
				connectionPool.addObject();
			}

			logger.info("成功创建连接池[" + _ds + "][" + _dburl + "][" + _user + "]"); //
			//return connectionPool; //
		} catch (Throwable _ex) {
			logger.error("创建连接池[" + _ds + "]异常:" + _ex.getMessage()); //
			_ex.printStackTrace();
		}
	}

	private HashVO[] getAllDSVos() {
		YuFormatBS yuFormatBS = SpringContextHolder.getBean("yuFormatBS");
		String str_sql = "select t1.ds_name,t2.driver_name,t1.conn_url,t1.conn_user,t1.conn_pwd,t2.validation_query from bione_ds_info t1,bione_driver_info t2 where t1.driver_id=t2.driver_id"; //
		HashVO[] hvs_data = yuFormatBS.getHashVOs(null, str_sql, null, false); //
		return hvs_data;
	}

	//自己创建连接池
	//String str_sql = "select t1.ds_name,t2.driver_name,t1.conn_url,t1.conn_user,t1.conn_pwd from bione_ds_info t1,bione_driver_info t2 where t1.driver_id=t2.driver_id"; //
	private Connection getConnectByDSName_old(String _ds) throws Exception {
		PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:"); //先取得池驱动!!!
		GenericObjectPool pool = (GenericObjectPool) driver.getConnectionPool(_ds);
		Connection conn = (java.sql.Connection) pool.borrowObject(); //取个对象!!!!
		logger.info("从连接池【" + _ds + "】中成功获取连接[" + conn + "]"); //
		return conn; //
	}

	private ConcurrentHashMap<String, BasicDataSource> getDsMap() {
		return this.dsMap; //
	}

	public synchronized static DataSourceCache getInstance() {
		if (cacheInst != null) {
			return cacheInst;
		}
		cacheInst = new DataSourceCache();
		return cacheInst;
	}
}
