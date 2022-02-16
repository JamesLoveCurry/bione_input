package com.yusys.bione.plugin.yuformat.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.greenpineyu.fel.common.StringUtils;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;

/**
 * 明细数据校验 单条检核 工具类
 * 支持 多server 高可用部署
 * 多server url 用; 分割
 */
public class SingleCheckDataUtils {
	
	YuFormatUtil bsUtil = new YuFormatUtil();
	private HttpURLConnectionUtil connUtil = new HttpURLConnectionUtil();
	
	/**
	 * 获取配置文件中  检核请求 url
	 */
	public String getCheckUrlStr(String propertiesPath, String propertyName) {
		// 检核请求 url 
		String urlstr = null;
		
		try {
			Properties configProp = new Properties();
			configProp.load(this.getClass().getClassLoader().getResourceAsStream(propertiesPath));
			urlstr = configProp.getProperty(propertyName);
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
		
		return urlstr;
	}
	
	/**
	 * 校验引擎 URL
	 * @return
	 */
	private List<String> getCheckDataUrls(String urlstr) {
		List<String> urlList = Lists.newArrayList();
		if (org.apache.commons.lang3.StringUtils.isNotBlank(urlstr)) {
			if(urlstr.indexOf(";") > 0){
				//多机 高可用 配置
				String[] urlArray = urlstr.split(";");
				for(String url : urlArray){
					url = this.validUrlStr(url);
					urlList.add(url);
				}
			}else{
				//单机配置
				urlstr = this.validUrlStr(urlstr);
				urlList.add(urlstr);
			}
		}

		return urlList;
	}
	
	/**
	 * 校验URL
	 * @param urlstr
	 * @return
	 */
	private String validUrlStr(String urlstr) {
		if (!urlstr.endsWith("/")) {
			urlstr = urlstr + "/";
		}
		return urlstr;
	}
	
	/**
	 * 单条检核入口
	 * @param _par  校验参数
	 * @param urlstr 请求URL
	 * @return
	 * @throws Exception
	 */
	public JSONObject checkData(JSONObject _par, String urlstr) throws Exception {
		
		JSONObject jso_rt = new JSONObject();
		jso_rt.put("status", "Fail");
		
		String data = _par.getString("data");
		String tabName = _par.getString("tabName"); // 中文表名
		String tabNameen = _par.getString("tabNameEn"); // 中文表名
		String ds = _par.getString("ds"); // 数据源
		
		@SuppressWarnings("rawtypes")
		Map map = getTableTypeInfo(tabNameen, ds);
		
		// 创建一个json数据
		JSONObject params = new JSONObject();
		params.put("tableName", tabName);
		params.put("ruleId", "");
		
		JSONObject param2 = new JSONObject();
		Map<String, String> obMap = JSONObject.parseObject(data.toString(), new TypeReference<Map<String, String>>(){});
		for (Map.Entry<String, String> a:obMap.entrySet()) {
			// 判断当前字段的类型，如果是int和double类型，要去掉引号，在进行传参
			if (map.get(a.getKey().toUpperCase()) != null) {
				String colnumType = map.get(a.getKey().toUpperCase()).toString();
				// 主要校验：int类型和double类型
				if ("NUMBER".equalsIgnoreCase(colnumType)) {
					// 判断值是否含有.
					if (StringUtils.isNotEmpty(a.getValue()) && a.getValue().indexOf(".") !=-1) {
						// 判断值是否含有,
						if(a.getValue().indexOf(",") !=-1) {
							// 转double
							param2.put(a.getKey().toUpperCase(), Double.parseDouble(bsUtil.replaceAll(a.getValue(), ",", "")));
						}else {
							// 转double
							param2.put(a.getKey().toUpperCase(), Double.parseDouble(a.getValue()));
						}
					} else if (StringUtils.isNotEmpty(a.getValue()) && a.getValue().indexOf(".") ==-1) {
						// 判断值是否含有,
						if(a.getValue().indexOf(",") !=-1) {
							// 转integer 
							param2.put(a.getKey().toUpperCase(), new BigInteger(bsUtil.replaceAll(a.getValue(), ",", "")));
						}else {
							// 转integer
							param2.put(a.getKey().toUpperCase(), new BigInteger(a.getValue()));
						}
					} else {
						param2.put(a.getKey().toUpperCase(), a.getValue());
					}
				} else {
					param2.put(a.getKey().toUpperCase(), a.getValue());
				}
			}
		}
		JSONArray arr = new JSONArray();
		arr.add(param2);
		params.put("data", arr);
		
		//多Server 通信
		List<String> urlList = this.getCheckDataUrls(urlstr);
		//多Server 处理
		for(String url : urlList){
			// Post请求
			@SuppressWarnings("static-access")
			String dataStr = connUtil.doPost(url,params.toString());
			Object ruleId = "";
			// 成功状态~
			if ("200".equals(dataStr)){
				// 将dataStr转换层json格式
				JSONObject json = JSONObject.parseObject(dataStr);
				JSONArray jsonArray = JSONArray.parseArray(json.getString("data"));
				if(jsonArray.isEmpty() || jsonArray.size() < 1){
					ruleId = "";
				} else {
					ruleId = jsonArray.getJSONObject(0).get("ruleId");
				}

				jso_rt.put("ruleId", ruleId);
				jso_rt.put("status", "OK");
				break;
			}
		}
		
		return jso_rt;
	}
	
	/**	
     * 获取表字段的类型	
     * @param tableName	
     * @return	
     */	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map getTableTypeInfo(String tabNameEn,String ds) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			if (StringUtils.isNotEmpty(ds)) {
				conn = getConnectionByDs(ds);
			} else {
				conn = getConnection();
			}
			
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM " + tabNameEn + "");
		sb.append(" WHERE 1 != 1");
		
		rs = stmt.executeQuery(sb.toString());
		ResultSetMetaData md = rs.getMetaData();
		
		Map map = new HashMap();
		for (int i=1;i<md.getColumnCount()+1;i++) {
			map.put(md.getColumnName(i).toUpperCase(), md.getColumnTypeName(i));
		}
		if (rs != null){
			rs.close();
			stmt.close();
			conn.close();
		}
			
		return map;
	}
		
	/**
	 * 数据库连接
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	private Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("database.properties"));
		props.put("remarksReporting", "true");
		props.put("user", props.getProperty("jdbc.username"));
		props.put("password", props.getProperty("jdbc.password"));
		Class.forName(props.getProperty("jdbc.driverClassName"));
		Connection dbConn = DriverManager.getConnection(props.getProperty("jdbc.url"), props);
		return dbConn;
	}
	
	/**
	 * 根据数据源名称创建连接..
	 * @param ds
	 * @return
	 * @throws Exception
	 */
	private Connection getConnectionByDs(String ds) throws Exception {
		HashVO hvo = findDsVOByName(ds);
		String str_driver_name = hvo.getStringValue("driver_name"); //
		String str_conn_url = hvo.getStringValue("conn_url"); //
		String str_conn_user = hvo.getStringValue("conn_user"); //
		String str_conn_pwd = hvo.getStringValue("conn_pwd"); //

		Properties props = new Properties();
		props.put("user", str_conn_user);
		props.put("password", str_conn_pwd);
		Class.forName(str_driver_name);
		Connection dbConn = DriverManager.getConnection(str_conn_url, props);

		return dbConn;
	}

	/**
	 * 根据数据源名称取得数据源
	 * @param _dsName
	 * @return
	 */
	private HashVO findDsVOByName(String _dsName) {
		HashVO[] hvs_ds = getAllDataSourceVOs();
		for (int i = 0; i < hvs_ds.length; i++) {
			if (hvs_ds[i].getStringValue("ds_name").equals(_dsName)) {
				return hvs_ds[i];
			}
		}
		return null;
	}
		
	/**
	 * 取得所有数据源.
	 * @return
	 */
	private HashVO[] getAllDataSourceVOs() {
		String str_sql = "select t1.ds_name,t2.driver_name,t1.conn_url,t1.conn_user,t1.conn_pwd from bione_ds_info t1,bione_driver_info t2 where t1.driver_id=t2.driver_id"; //
		HashVO[] hvs_data = bsUtil.getHashVOs(str_sql);
		return hvs_data;
	}
}
