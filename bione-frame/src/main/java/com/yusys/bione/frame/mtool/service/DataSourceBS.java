package com.yusys.bione.frame.mtool.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.bigdata.utils.HiveJdbcUtil;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jdbc.entity.BioneColumnMetaData;
import com.yusys.bione.comp.repository.jdbc.entity.BioneTableMetaData;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.repository.mybatis.dialect.DBMS;
import com.yusys.bione.comp.repository.mybatis.dialect.Dialect;
import com.yusys.bione.comp.repository.mybatis.dialect.DialectClient;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DialectUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.mtool.repository.mybatis.DataSourceDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * <pre>
 * Title:
 * Description:
 * </pre>
 * 
 * @author gaofeng gaofeng5@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：高峰  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class DataSourceBS extends BaseBS<BioneDsInfo> {
	@Autowired
	private DataSourceDao dataSourceDao;

	public SearchResult<BioneDsInfo> getList(Pager pager) {
		PageHelper.startPage(pager);
		PageMyBatis<BioneDsInfo> page = (PageMyBatis<BioneDsInfo>) dataSourceDao.search();
		SearchResult<BioneDsInfo> results = new SearchResult<BioneDsInfo>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}

	// 获取逻辑系统
	public List<BioneLogicSysInfo> getLogicSysData() {
		List<BioneLogicSysInfo> bioneLogicSysInfo = this.dataSourceDao.getBioneLogicSysInfoList();
		return bioneLogicSysInfo;
	}

	// 获取连接驱动
	public List<BioneDriverInfo> getDriverData() {
		List<BioneDriverInfo> bioneLogicSysInfo = this.dataSourceDao.getDriverList();
		return bioneLogicSysInfo;
	}

	// 获取URl
	public BioneDriverInfo getURLData(String driverId) {
		BioneDriverInfo bioneLogicSysInfo = this.dataSourceDao.getURLDataByDriverId(driverId);
		return bioneLogicSysInfo;
	}

	// 删除
	@Transactional(readOnly = false)
	public void removeDs(String dsId) {
		String[] dsIds = StringUtils.split(dsId, '/');
		List<String> list = Lists.newArrayList();
		if (dsIds != null) {
			for (String ds : dsIds) {
				list.add(ds);
			}
		}
		this.dataSourceDao.dsBatchDel(list);
	}

	// 重名验证
	public List<BioneDsInfo> checkedDsName(String dsId, String dsName) {
		List<BioneDsInfo> ds = Lists.newArrayList();
		Map<String, String> params = Maps.newHashMap();
		if(StringUtils.isNotBlank(dsId)) {
			params.put("dsId", dsId);
		}
		params.put("dsName", dsName);
		ds = this.dataSourceDao.checkedDsName(params);
		return ds;
	}

	// 获取某一数据源下的数据集数量
	public Integer getDataSetCountByDsId(Map<String, Object> param) {
		Integer num = this.dataSourceDao.getDataSetCountByDsId(param);
		if (num == null)
			num = 0;
		return num;
	}

	public BioneDriverInfo findDriverInfoById(String id) {
		return this.dataSourceDao.findDriverInfoById(id);
	}

	public BioneDsInfo findDataSourceById(String id) {
		return this.dataSourceDao.findDataSourceById(id);
	}

	public void updateDS(BioneDsInfo model) {
		this.dataSourceDao.updateDS(model);
	}

	public void saveDS(BioneDsInfo model) {
		this.dataSourceDao.saveDS(model);
	}
	
	/**
	 * 获取指定数据源.
	 * 建议使用com.yusys.bione.frame.mtool.service.DataSourceBS#
	 * releaseConnection(Connection con, DataSource dataSource)来释放连接.
	 */
	public DataSource getDataSource(String dsId) {
		// TODO suggest use dbcp datasource pool
		return null;
	}
	
	/**
	 * 获取数据源类型
	 * 
	 * @param dsId
	 * @return
	 */
	public String getDataSourceType(String dsId) {
		BioneDsInfo dsInfo = this.getEntityById(dsId);
		String jdbcUrl = dsInfo.getConnUrl();
		if (StringUtils.contains(jdbcUrl, ":h2:")) {
			return "h2";
		} else if (StringUtils.contains(jdbcUrl, ":mysql:")) {
			return "mysql";
		} else if (StringUtils.contains(jdbcUrl, ":oracle:")) {
			return "oracle";
		} else if (StringUtils.contains(jdbcUrl, ":db2:")) {
			return "db2";
		} else if (StringUtils.contains(jdbcUrl, ":postgresql:")) {
			if(GlobalConstants4frame.POSTGRESQL_DATA_SOURCE.equals(dsInfo.getDriverId())){
				return "postgresql";
			} else if (GlobalConstants4frame.ELK_DATA_SOURCE.equals(dsInfo.getDriverId())){
				return "elk";
			}
		}
		throw new IllegalArgumentException("Unknown Database of " + jdbcUrl);
	}
	
	/**
	 * 获取指定数据源连接
	 * DriverManager.getConnection(url, user, password)
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	public Connection getConnection(String dsId) throws SQLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException {
		String jql = "select ds, dr from BioneDsInfo ds, BioneDriverInfo dr where ds.dsId = ?0 and ds.driverId = dr.driverId";
		Object[] obj = this.baseDAO.findUniqueWithIndexParam(jql, dsId);
		BioneDsInfo dsInfo = (BioneDsInfo) obj[0];
		BioneDriverInfo driverInfo = (BioneDriverInfo) obj[1];
		Class.forName(driverInfo.getDriverName());
		String url = dsInfo.getConnUrl();
		String user = dsInfo.getConnUser();
		String password = dsInfo.getConnPwd();
//		if(dsInfo.getDsName().equals("Inceptor")){
		if(driverInfo.getDriverType().equals("Inceptor")){
//			url="jdbc:hive2://192.168.251.158:10000/hisdb;principal=hive/ycdata158@TDH";
//			url +=";principal="+user;
			return HiveJdbcUtil.getConnection(url,driverInfo.getDriverName(),user,password);
		}
		return DriverManager.getConnection(url, user, password);
	}
	
	/**
	 * 释放指定连接
	 * @param rs ResultSet
	 * @param stmt Statement
	 * @param conn Connection
	 */
	public void releaseConnection(ResultSet rs, Statement stmt, Connection conn) {
		JdbcUtils.closeResultSet(rs);
		JdbcUtils.closeStatement(stmt);
		JdbcUtils.closeConnection(conn);
	}
	
	/**
	 * 获取数据库表的元数据信息
	 * @return
	 */
	public List<BioneTableMetaData> getTableMetaData(String dsId, String... tableNames) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection(dsId);
			DBMS dbms = DBMS.valueOf(DialectUtils.getDataBaseType(conn, false).toUpperCase());
			Dialect dialect = DialectClient.getDbmsDialect(dbms);
			String sql = dialect.getTableMetaDataSql(conn);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			return new RowMapperResultSetExtractor<BioneTableMetaData>(
					new BeanPropertyRowMapper<BioneTableMetaData>(BioneTableMetaData.class)).extractData(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			this.releaseConnection(rs, stmt, conn);
		}
		return Lists.newArrayList();
	}
	
	/**
	 * 获取包含指定列的表元数据信息
	 * @param conn
	 * @param column
	 * @return
	 */
	public List<BioneTableMetaData> getTableMetaDataWithColumns(String dsId, String... columnNames) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			BioneDsInfo dsInfo = this.getEntityById(dsId);
			conn = this.getConnection(dsId);
			DBMS dbms = DBMS.valueOf(DialectUtils.getDataBaseType(conn, false).toUpperCase());
			Dialect dialect = DialectClient.getDbmsDialect(dbms);
			String sql = dialect.getTableMetaDataSqlWithColumns(dsInfo.getSchema2(),conn, columnNames);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			return new RowMapperResultSetExtractor<BioneTableMetaData>(
					new BeanPropertyRowMapper<BioneTableMetaData>(BioneTableMetaData.class)).extractData(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			this.releaseConnection(rs, stmt, conn);
		}
		return Lists.newArrayList();
	}
	
	/**
	 * 获取包含指定列的表元数据信息
	 * @param conn
	 * @param column
	 * @return
	 */
	public List<BioneColumnMetaData> getColumnMetaData(String dsId, String... tableNames) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection(dsId);
			DBMS dbms = DBMS.valueOf(DialectUtils.getDataBaseType(conn, false).toUpperCase());
			Dialect dialect = DialectClient.getDbmsDialect(dbms);
			String sql = dialect.getColumnMetaDataSql(conn, tableNames);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			return new RowMapperResultSetExtractor<BioneColumnMetaData>(
					new BeanPropertyRowMapper<BioneColumnMetaData>(BioneColumnMetaData.class)).extractData(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			this.releaseConnection(rs, stmt, conn);
		}
		return Lists.newArrayList();
	}
	
	
	/**
	 * 根据dsId获取 拼接sql中的schema 属性
	 * @param String dsId
	 * @return String schema
	 */
	public String getSchemaByDsId(String dsId) {
		BioneDsInfo dsInfo = this.getEntityById(dsId);
		return this.getSchemaByDsInfo(dsInfo);
	}
	
	/**
	 * 根据dsId获取 拼接sql中的schema 属性
	 * @param BioneDsInfo dsInfo
	 * @return String schema
	 */
    public String getSchemaByDsInfo(BioneDsInfo dsInfo) {
    	
    	String schema = dsInfo.getSchema2();
    	
		if(StringUtils.isEmpty(schema)) {
			if(dsInfo.getConnUrl().indexOf("mysql") > 0) {
				//mysql处理方式
				schema = dsInfo.getDbname();
			}else {
				//其它数据库处理方式  oracle db2 等
				schema = dsInfo.getConnUser();
			}
		}
		return schema;
	}

	public List<CommonComboBoxNode> getDsImportList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		List<BioneDsInfo> list = dataSourceDao.search();
		for(BioneDsInfo bdi:list){
			CommonComboBoxNode node=new CommonComboBoxNode();
			node.setId(bdi.getDsId());
			node.setText(bdi.getDsName());
			nodes.add(node);
		}
		return nodes;
	}
}
