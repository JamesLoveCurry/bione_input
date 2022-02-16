package com.yusys.bione.plugin.paramtmp.service;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.SqlValidateUtils;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.paramtmp.ParamModule;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpAttr;

@Service
@Transactional(readOnly = true)
public class CommonComboBoxBS extends BaseBS<Object> {

	private static final Logger log = LoggerFactory
			.getLogger(CommonComboBoxBS.class);

	/**
	 * 得到数据源的配置信息
	 * @return
	 */
	public List<CommonComboBoxNode> getDataSource() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		String jql = "select ds from BioneDsInfo ds where logicSysNo = ?0";
		List<BioneDsInfo> list = this.baseDAO.findWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if (notEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(list.get(i).getDsId());
				node.setText(list.get(i).getDsName());
				nodes.add(node);
			}
		}
		return nodes;
	}
	/**
	 * 判断一个List是否是空的
	 * @param temp 
	 * @return true 不空  false 空
	 */
	private boolean notEmpty(List<?> temp) {
		if (temp == null || temp.size() == 0)
			return false;
		else
			return true;
	}
	/**
	 * 替换下拉框SQL语句中形如#{}的数据
	 * 使用的是ParamModule的数据
	 * @param paramJson
	 * @return
	 */
	public String replace(String paramJson) {

		Pattern pattern = Pattern.compile("\\#\\{(\\w+)\\}");
		Matcher matcher = pattern.matcher(paramJson);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb,
					ParamModule.formString(matcher.group(1)).apply());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param dsId 数据库Id
	 * @param sql 要执行的SQL语句
	 * @param type 执行SQL语句的控件的类型(树或者下拉框)
	 * @return
	 */
	public List<CommonComboBoxNode> executeSelectSqlEdit(String dsId,
			String sql) {
		List<Map<String,String>> nodeList = this.getConnAndResult(dsId, sql);
		List<CommonComboBoxNode> list = new ArrayList<CommonComboBoxNode>();
		if (nodeList != null && nodeList.size() > 0) {
			for (int i = 0; i < nodeList.size(); i++) {
				Map<String,String> columnMap = nodeList.get(i);
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(columnMap.get("id"));
				node.setText(columnMap.get("text"));
				list.add(node);
			}
		}
		return list;
	}
	
	/**
	 * 树修改时执行SQL语句
	 * @param db 数据库Id
	 * @param s 要执行的SQL语句
	 * @param type 执行SQL语句的控件的类型(树或者下拉框)
	 * @return
	 */
	public List<CommonTreeNode> executeTreeSqlEdit(String db, String s,String searchName) {
		List<Map<String,String>> object = this.getConnAndResult(db, s,searchName);// 取到节点的值
		List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
		if (object != null) {
			for (int i = 0; i < object.size(); i++) {
				Map<String,String> columnMap = object.get(i);
				CommonTreeNode node = new CommonTreeNode();
				node.setId(columnMap.get("id"));
				node.setText(columnMap.get("text"));
				if (columnMap.get("upid") != null) {
					node.setUpId(columnMap.get("upid"));
				}
				node.setIsexpand(false);
				list.add(node);
			}
			list = createTreeNode(list);// 转化成树节点的列表
		}
		return list;
	}
	
	/**
	 * 树修改时执行SQL语句
	 * @param db 数据库Id
	 * @param s 要执行的SQL语句
	 * @param type 执行SQL语句的控件的类型(树或者下拉框)
	 * @return
	 */
	public List<CommonTreeNode> executeTreeSqlEdit(String db, String s) {
		List<Map<String,String>> object = this.getConnAndResult(db, s);// 取到节点的值
		List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
		if (object != null) {
			for (int i = 0; i < object.size(); i++) {
				Map<String,String> columnMap = object.get(i);
				CommonTreeNode node = new CommonTreeNode();
				node.setId(columnMap.get("id"));
				node.setText(columnMap.get("text"));
				if (columnMap.get("upid") != null) {
					node.setUpId(columnMap.get("upid"));
				}
				node.setIsexpand(false);
				list.add(node);
			}
			list = createTreeNode(list);// 转化成树节点的列表
		}
		return list;
	}
	
	/**
	 * 树修改时执行SQL语句
	 * @param db 数据库Id
	 * @param s 要执行的SQL语句
	 * @param type 执行SQL语句的控件的类型(树或者下拉框)
	 * @return
	 */
	public List<CommonTreeNode> executePopupSqlEdit(String db, String s,String searchName) {
		List<Map<String,String>> object = this.getConnAndResult(db, s ,searchName);// 取到节点的值
		List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
		if (object != null) {
			for (int i = 0; i < object.size(); i++) {
				Map<String,String> columnMap = object.get(i);
				CommonTreeNode node = new CommonTreeNode();
				node.setId(columnMap.get("id"));
				node.setText(columnMap.get("text"));
				if (columnMap.get("upid") != null) {
					node.setUpId(columnMap.get("upid"));
				}
				node.setIsexpand(false);
				list.add(node);
			}
			list = createTreeNode(list);// 转化成树节点的列表
		}
		return list;
	}

	/**
	 * 根据driverId得到数据库的驱动信息
	 * 
	 * @param driverId
	 * @return
	 */
	public BioneDriverInfo getURLData(String driverId) {
		String jql = "select driver from BioneDriverInfo driver where driverId=?0";
		return this.baseDAO.findUniqueWithIndexParam(jql, driverId);
	}

	/**
	 * 预览时执行SQL语句
	 * @param paramId 要执行的模板的参数的ID
	 * @param value 选定的相关联的下拉框的值
	 * @param text	选定的相关联的下拉框的显示值
	 * @param type	执行SQL语句的控件的类型(树或者下拉框)
	 * @return
	 */
	public List<CommonComboBoxNode> executeSelectSqlView(String paramId, String value, String text) {
		Map<String, String> map = this.getSQL(paramId);
		String sql = map.get("sql");
		String database = map.get("database");
		if (sql != null) {
			if (StringUtils.isNotEmpty(value)) {
				sql = StringUtils.replace(sql, "#{selectedValue}", value);
			}
			if (StringUtils.isNotEmpty(text)) {
				sql = StringUtils.replace(sql, "#{selectedText}", text);
			}
			String s = this.replace(sql);
			return this.executeSelectSqlEdit(database, s);// 执行sql语句
		}
		return null;
	}
	
	/**
	 * 弹出树预览时执行SQL语句
	 * @param paramId 要执行的模板的参数的ID
	 * @param type 执行SQL语句的控件的类型(树或者下拉框)
	 * @return
	 */
	public List<CommonTreeNode> executePopupSqlView(String paramId, String value, String text,String searchName) {
		Map<String, String> map = this.getSQL(paramId);
		String dsId = map.get("database");
		String sql = map.get("sql");
		if (sql != null) {
			/*if (StringUtils.isNotEmpty(value)) {
				sql = StringUtils.replace(sql, "#{parentValue}", value);
			}*/
			//edit by fangjuan 20140723
			if (StringUtils.isNotEmpty(text)) {
				if (StringUtils.isNotEmpty(value)) {
					sql = StringUtils.replace(sql, "#{selectedValue}", value);
				}
				sql = StringUtils.replace(sql, "#{selectedText}", text);
			}else{
				if (StringUtils.isNotEmpty(value)) {
					sql = StringUtils.replace(sql, "#{parentValue}", value);
				}
			}
			
			String s = this.replace(sql);
			return executeTreeSqlEdit(dsId, s,searchName);
		}
		return null;
	}
	
	/**
	 * 树预览时执行SQL语句
	 * @param paramId 要执行的模板的参数的ID
	 * @param type 执行SQL语句的控件的类型(树或者下拉框)
	 * @return
	 */
	public List<CommonTreeNode> executeTreeSqlView(String paramId, String value, String text) {
		Map<String, String> map = this.getSQL(paramId);
		String dsId = map.get("database");
		String sql = map.get("sql");
		if (sql != null) {
			/*if (StringUtils.isNotEmpty(value)) {
				sql = StringUtils.replace(sql, "#{parentValue}", value);
			}*/
			//edit by fangjuan 20140723
			if(StringUtils.isNotEmpty(text)){
				if (StringUtils.isNotEmpty(value)) {
					sql = StringUtils.replace(sql, "#{selectedValue}", value);
				}
					sql = StringUtils.replace(sql, "#{selectedText}", text);
			}else{
				if (StringUtils.isNotEmpty(value)) {
					sql = StringUtils.replace(sql, "#{parentValue}", value);
				}
			}
			String s = this.replace(sql);
			return executeTreeSqlEdit(dsId, s);
		}
		return null;
	}
	

	/**
	 * 数据库连接和执行sql语句过程
	 * 
	 * @param dsId
	 * @param sql
	 * @param type
	 *            “treeBox” 得到的是树的节点，“CommonBox” 下拉框值
	 * @return
	 */
	private List<Map<String,String>> getConnAndResult(String dsId, String sql,String searchName) {
		String jql = "select ds from BioneDsInfo ds where ds.dsId = ?0";
		BioneDsInfo dsInfo = this.baseDAO.findUniqueWithIndexParam(jql, dsId);
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		List<Map<String,String>> list = Lists.newArrayList();
		BioneDriverInfo driverInfo = this.getURLData(dsInfo.getDriverId());
		if (driverInfo.getDriverName() != null
				&& !"".equals(driverInfo.getDriverName())
				&& driverInfo.getConnUrl() != null
				&& !"".equals(driverInfo.getConnUrl())
				&& dsInfo.getConnUser() != null
				&& !"".equals(dsInfo.getConnUser())) {
			try {
				// 注册驱动
				Driver driver = (Driver) Class.forName(
						driverInfo.getDriverName()).newInstance();
				Properties p = new Properties();
				p.put("user", dsInfo.getConnUser());
				p.put("password", dsInfo.getConnPwd());
				// 获取连接
				conn = driver.connect(dsInfo.getConnUrl(), p);
				if (conn != null) {// 执行SQL
					stm = conn.createStatement();
					String sqlTemp = StringUtils.remove(sql, ';').replaceAll("\\\\n", " ").replaceAll("\\\\t", " ");
					if(org.apache.commons.lang3.StringUtils.isNotBlank(searchName)){
						searchName = EncodeUtils.urlDecode(searchName);
						//2020 lcy 【后台管理】sql注入 代码优化
						if(SqlValidateUtils.validateStr(searchName)) {
							searchName = SqlValidateUtils.replaceValue(searchName);
						}
						sqlTemp = "select * from (" + sqlTemp +") temp where (id like '%"+searchName+"%'"+ " or text like '%"+searchName+"%')";
					}
					rs = stm.executeQuery(sqlTemp);
					String[] columnNameArr = getAndcheckColumnName(rs);
					while (rs.next()) {
						Map<String, String> resultMap = new HashMap<String, String>();
						for (String columnName : columnNameArr) {
							resultMap.put(columnName, rs.getString(columnName));
						}
						list.add(resultMap);
					}
				} else {
					log.error("通过" + dsInfo.getConnUrl() + "和" + p.toString()
							+ "获取数据库连接失败！");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(rs);// 关闭ResultSet
				JdbcUtils.closeStatement(stm);// 关闭Statement
				JdbcUtils.closeConnection(conn);// 关闭Connection
			}
		}
		return list;
	}
	
	/**
	 * 数据库连接和执行sql语句过程
	 * 
	 * @param dsId
	 * @param sql
	 * @param type
	 *            “treeBox” 得到的是树的节点，“CommonBox” 下拉框值
	 * @return
	 */
	private List<Map<String,String>> getConnAndResult(String dsId, String sql) {
		String jql = "select ds from BioneDsInfo ds where ds.dsId = ?0";
		BioneDsInfo dsInfo = this.baseDAO.findUniqueWithIndexParam(jql, dsId);
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		List<Map<String,String>> list = Lists.newArrayList();
		BioneDriverInfo driverInfo = this.getURLData(dsInfo.getDriverId());
		if (driverInfo.getDriverName() != null
				&& !"".equals(driverInfo.getDriverName())
				&& driverInfo.getConnUrl() != null
				&& !"".equals(driverInfo.getConnUrl())
				&& dsInfo.getConnUser() != null
				&& !"".equals(dsInfo.getConnUser())) {
			try {
				// 注册驱动
				Driver driver = (Driver) Class.forName(
						driverInfo.getDriverName()).newInstance();
				Properties p = new Properties();
				p.put("user", dsInfo.getConnUser());
				p.put("password", dsInfo.getConnPwd());
				// 获取连接
				conn = driver.connect(dsInfo.getConnUrl(), p);
				if (conn != null) {// 执行SQL
					stm = conn.createStatement();
					String sqlTemp = StringUtils.remove(sql, ';').replace('\n', ' ').replace('\t', ' ');
					rs = stm.executeQuery(sqlTemp);
					String[] columnNameArr = getAndcheckColumnName(rs);
					while (rs.next()) {
						Map<String, String> resultMap = new HashMap<String, String>();
						for (String columnName : columnNameArr) {
							resultMap.put(columnName, rs.getString(columnName));
						}
						list.add(resultMap);
					}
				} else {
					log.error("通过" + dsInfo.getConnUrl() + "和" + p.toString()
							+ "获取数据库连接失败！");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(rs);// 关闭ResultSet
				JdbcUtils.closeStatement(stm);// 关闭Statement
				JdbcUtils.closeConnection(conn);// 关闭Connection
			}
		}
		return list;
	}
	
	private String[] getAndcheckColumnName(ResultSet rs) throws SQLException {
		boolean hasId = false;
		boolean hasText = false;
		String[] columnNameArr = new String[rs.getMetaData().getColumnCount()];
		for (int i = 0; i < columnNameArr.length; i++) {
			String columnName = rs.getMetaData().getColumnName(i + 1).toLowerCase();
			
			columnNameArr[i] = columnName;
			
			if (columnName.equals("id")){
				hasId = true;
			}
			if (columnName.equals("text")){
				hasText = true;
			}
		}
		if (!hasId) {
			throw new IllegalArgumentException("SQL语句未定义id字段！");
		}
		if (!hasText) {
			throw new IllegalArgumentException("SQL语句未定义text字段！");
		}
		return columnNameArr;
	}
	/**
	 * 根据模板参数的ID得到其SQL语句
	 * @param paramId 模板参数ID
	 * @return
	 */
	public Map<String, String> getSQL(String paramId) {
		String jql = "select  attr  from RptParamtmpAttr attr where paramId = ?0";
		RptParamtmpAttr attr = this.baseDAO.findUniqueWithIndexParam(jql,
				paramId);
		String paramValue = attr.getParamVal();
		JSONObject queryObject = JSON.parseObject(paramValue);
		Map<String, String> map = new HashMap<String, String>();
		if (queryObject.get("datasource") != null) {
			Object datasource = queryObject.get("datasource");
			JSONObject option = JSON.parseObject(datasource.toString());
			JSONObject db = (JSONObject)JSON.toJSON(option.get("options"));
			map.put("database", db.get("db").toString());
			if (db.get("sql") != null) {
				map.put("sql", db.get("sql").toString());
			}
		}
		return map;
	}
	/**
	 * 控件为树时，执行完sql语句后，将结点的链表根据其UPID重新组成一棵树
	 * @param nodes
	 * @return
	 */
	private Map<String, CommonTreeNode> createMapCache(
			List<CommonTreeNode> nodes) {
		Map<String, CommonTreeNode> result = Maps.newHashMap();
		for (CommonTreeNode node : nodes) {
			result.put(node.getId(), node);
		}
		return result;
	}

	public List<CommonTreeNode> createTreeNode(List<CommonTreeNode> nodes) {
		List<CommonTreeNode> result = Lists.newArrayList();
		Map<String, CommonTreeNode> cache = createMapCache(nodes);
		CommonTreeNode parent = null;
		for (CommonTreeNode node : nodes) {
			parent = cache.get(node.getUpId());
			if (parent == null) {
				result.add(node);
			} else {
				parent.addChildNode(node);
			}
		}
		return result;
	}
}
