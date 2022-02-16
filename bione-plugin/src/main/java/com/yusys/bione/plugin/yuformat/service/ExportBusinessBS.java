package com.yusys.bione.plugin.yuformat.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.plugin.yuformat.utils.ExportCsvUtils;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.InsertSQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.UpdateSQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;

/**
 * 
 * <pre>
 * Title:【数据查询】
 * Description:
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
 * @date 2020年5月28日
 */
public class ExportBusinessBS {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private YuFormatUtil bsUtil = new YuFormatUtil();
	
	/**
	 * 根据用户所属机构，查询报送机构
	 * 用于解决前端传给后端一个str_LoginUserOrgNo，当前登录人员的内部所属机构号，转换为报送机构号
	 * @param loginUserOrgNo
	 * @param reportType
	 * @return
	 */
	public String getReportOrgNo (String loginUserOrgNo, String reportType) {
		String orgNo = "";
		
		String str_sql = "select org_no from rpt_org_info where org_type = '"+reportType+"' and mgr_org_no = '"+loginUserOrgNo+"'";
		HashVO[] hvs = bsUtil.getHashVOs(str_sql.toString());
		if (hvs != null && hvs.length > 0) {
			orgNo = hvs[0].getStringValue("org_no");
		}
		
		return orgNo;
	}
	
	/**
	 * 用于前端获取，明细数据量
	 * @param _par
	 * @return
	 */
	public JSONObject getMaxDataNum(JSONObject _par) {
		Map<String, String> map = bsUtil.getDataBaseparam(new String[]{"base.business.download"});
		String str_download = map.get("download");
		
        int download_num = Integer.parseInt(str_download); // 获取默认允许下载数量范围(50w)
		
		JSONObject jso_rt = new JSONObject();
		jso_rt.put("download_num", download_num);
		
		return jso_rt;
	}
	
	/**
	 * 用于前端获取，当前下载的tar文件是否存在，如果存在则提示已存在，是否覆盖
	 * @param _par
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public JSONObject getDownTarFile(JSONObject _par) throws UnsupportedEncodingException {
		JSONObject jso_rt = new JSONObject();
		
		String org_no = _par.getString("org_no");
		String tab_name = _par.getString("tab_name");
		String data_dt = _par.getString("data_dt");
		String model_type = _par.getString("model_type");
		String report_type = _par.getString("report_type");
		
		// 转换成报送机构号
		// report_type：报送类型(04,07...); model_type：模块类型（east,fsrs...）
        org_no = getReportOrgNo(org_no, report_type); 

		Map<String, String> map = bsUtil.getDataBaseparam(new String[]{"base.download.zippath"});
        String str_downtar = map.get("zippath");
        
        String zipPath = str_downtar + File.separatorChar + model_type + File.separatorChar + "download" + File.separatorChar +org_no + File.separatorChar + data_dt + File.separatorChar;
        String zipFilePath = zipPath + tab_name + ".zip";
        if(FilepathValidateUtils.validateFilepath(zipFilePath)) {
	        // 判断该文件是否存在
	        File file = new File(new String((zipFilePath).getBytes("utf-8")));
	        if (!file.exists()) {
	        	jso_rt.put("msg", "Fail");
	        } else {
	        	jso_rt.put("msg", "Ok");
	        }
        }
        return jso_rt;
	}
	
	/**
	 * 明细数据，详细信息
	 * @param dataDt 
	 * @param jso
	 * @return
	 */
	public void getBusinessDataByCVS(HttpServletRequest request, HttpServletResponse response, 
			String modelType, String crTab, String crCol,
			String tabName, String tabNameEn, String batchSql, 
			int datanum, int download, int onerun, String dirPath) {
		Connection conn = null;
		Statement stmtQuery = null;
		ResultSet rsSelectQuery = null;
		
		List<String> columnList = getColumnList(crCol, tabName);
        
		// 查询数据源(如果是fsrs，则是ds，其他ds_name)
		HashVO[] dsNmhvs = null;
		if ("fsrs".equals(modelType)) {
			dsNmhvs = bsUtil.getHashVOs("select ds from " + crTab + " where tab_name_en ='" + tabNameEn + "'");
		} else {
			dsNmhvs = bsUtil.getHashVOs("select ds_name ds from " + crTab + " where tab_name_en ='" + tabNameEn + "'");
		}
		
		String ds_name = dsNmhvs[0].getStringValue("ds");
		
		// 获取全量层数据
		String selectSql = packageSql(crCol, tabName, tabNameEn, batchSql);
		
		// 分页从oracle中获取错误数据
		try {
			conn = createDatabaseConnectionByDS(ds_name);
			stmtQuery = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			stmtQuery.setFetchSize(onerun);
	    	
			rsSelectQuery = stmtQuery.executeQuery(selectSql); // 查询数据
			generateCSV(request, response, rsSelectQuery, columnList, dirPath, datanum, onerun, download, tabName);
		} catch (Exception _ex) {
			logger.debug("发生异常,发生时间"+new SimpleDateFormat("yyyy-MM-dd hh:mm:sss").format(new Date()));
			_ex.printStackTrace();
		} finally {
			try {
				if (rsSelectQuery != null) {
					rsSelectQuery.close();
				}
			} catch (Exception _exx) {
				_exx.printStackTrace();
			}

			try {
				if (stmtQuery != null) {
					stmtQuery.close();
				}
			} catch (Exception _exx) {
				_exx.printStackTrace();
			}

			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception _exx) {
				_exx.printStackTrace();
			}
		}
	}
	
	private void generateCSV(HttpServletRequest request, HttpServletResponse response, ResultSet rs, List<String> columnList, String dirPath, int datanum, int onerun, int download, String tab_name) throws SQLException, IOException {
		List<Object[]> objlist = new ArrayList<Object[]>();
		FileOutputStream outStreasm = null;
		
		ResultSetMetaData md = rs.getMetaData(); // 获取键名
		int columnCount = md.getColumnCount(); // 获取行的数量
		
		int k = 1;
		int j = 0;

		while (rs.next()) {
			Object objArr[]  = new Object[columnCount];
			
			for (int i = 1; i <= columnCount; i++) {
				objArr[i-1] = rs.getObject(i) == null || rs.getObject(i) == ""? "" : rs.getObject(i).toString()+"\t";
			}
			objlist.add(objArr);
			
			if (k % download == 0) {
				System.out.println("--------------每隔"+download+"进行一存储：第"+j+"次--------------");
				String fileName = tab_name + "_" + j + ".csv";
				String filePath = new String((dirPath + fileName).getBytes("utf-8"));
				if(FilepathValidateUtils.validateFilepath(filePath)) {
					File downFile = new File(filePath);
			        outStreasm = new FileOutputStream(downFile);
					
			        fileName = getFileName(request, fileName);
			        ExportCsvUtils.simpleExport(true,"\n", columnList.toArray(new String[columnList.size()]), objlist, fileName, outStreasm, onerun);
					
			        objlist.clear();
					j++;
				}
			}  else if (k == datanum) {
				// 说明数据量小与50w
				if (j == 0) {
					System.out.println("--------------数据量小与"+download+"--------------");
					String fileName = getFileName(request, tab_name);;
			        
					ExportCsvUtils.simpleExport(true,"\n", columnList.toArray(new String[columnList.size()]), objlist, fileName, response.getOutputStream(), onerun);

			        objlist.clear();
				} else {
					System.out.println("--------------每隔"+download+"进行一存储（最后）：第"+j+"次--------------");
					String fileName = tab_name + "_" + j + ".csv";
					String filePath = new String((dirPath + fileName).getBytes("utf-8"));
					if(FilepathValidateUtils.validateFilepath(filePath)) {
						File downFile = new File(filePath);
				        outStreasm = new FileOutputStream(downFile);
						
				        fileName = getFileName(request, fileName);
				        ExportCsvUtils.simpleExport(true,"\n", columnList.toArray(new String[columnList.size()]), objlist, fileName, outStreasm, onerun);
	
				        objlist.clear();
					}
				}
			}
			
			k++;
			objArr = null;
		}
	}
	
	private List<String> getColumnList (String crCol, String tabName) {
		List<String> columnList = new ArrayList<String>();
		HashVO[] hvs = bsUtil.getHashVOs("select col_name from "+crCol+" where tab_name = '" + tabName + "' and is_export = 'Y' order by col_no");
		if (hvs != null && hvs.length > 0) {
			for (int i=0;i<hvs.length;i++) {
				columnList.add(hvs[i].getStringValue("col_name"));
			}
		}
		
		return columnList;
	}
	
	/**
	 * 获取全量层数据sql
	 * @param tabNameEn
	 * @param batchSql
	 * @return
	 */
	public String packageSql(String crCol, String tabName, String tabNameEn, String batchSql) {
		StringBuffer str_sql = new StringBuffer();
		String columns = "";
		
		HashVO[] hvs = bsUtil.getHashVOs("select col_name_en from " + crCol + " where tab_name = '" + tabName + "' and is_export = 'Y' order by col_no");
		if (hvs != null && hvs.length > 0) {
			for (int i=0;i<hvs.length;i++) {
				columns += hvs[i].getStringValue("col_name_en") + ",";
			}
		}
		
		if (!columns.isEmpty()) {
			columns = columns.substring(0, columns.length() - 1);
		}
		
		str_sql.append(" select "+ columns +" from " + tabNameEn + " where "); 

		if (StringUtils.isNotBlank(batchSql)) {
			String str1 = batchSql.substring(0, batchSql.indexOf("where"));
			String str2 = batchSql.substring(str1.length() + 5, batchSql.length());
			
			str_sql.append(str2);
		}
		
		return str_sql.toString();
	}
	
	/**
	 * 获取全量层数据sql，统计条数
	 * @param tabNameEn
	 * @param batchSql
	 * @return
	 */
	public String packageSqlByCount(String tabNameEn, String batchSql){
		StringBuffer str_sql = new StringBuffer();
		str_sql.append(" select count(*) c from " + tabNameEn + " where "); 

		if (StringUtils.isNotBlank(batchSql)) {
			String str1 = batchSql.substring(0, batchSql.indexOf("where"));
			String str2 = batchSql.substring(str1.length() + 5, batchSql.length());
			
			str_sql.append(str2);
		}
		
		return str_sql.toString();
	}
	
	/**
	 * 是否存在该记录（RPT_HIS_FILE_DATA）
	 */
	public boolean getIntoFileData(String orgNo, String dataDt, String modelType) {
		// 默认不存在
		boolean result = false;
		
		String str_sql = "select count(*) c from rpt_his_file_data where org_no = '"+orgNo+"' and data_date = '"+dataDt+"' and remark = '"+modelType+"'";
		HashVO[] hvs = bsUtil.getHashVOs(str_sql.toString());
		int c = hvs[0].getIntegerValue("c", 0);
		if (c > 0) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * 获取该记录的rid（RPT_HIS_FILE_DATA）
	 */
	public String getIntoFileDataRid(String orgNo, String dataDt, String modelType) {
		// 默认不存在
		String record_id = "";
		
		String str_sql = "select record_id from rpt_his_file_data where org_no = '"+orgNo+"' and data_date = '"+dataDt+"' and remark = '"+modelType+"'";
		HashVO[] hvs = bsUtil.getHashVOs(str_sql.toString());
		if (hvs != null && hvs.length > 0) {
			record_id = hvs[0].getStringValue("record_id");
		}
		
		return record_id;
	}
	
	/**
	 * 插入数据（RPT_HIS_FILE_DATA）
	 */
	public void insertIntoFileData(String repordId, String orgNo, String dataDt, String modelType) {
		InsertSQLBuilder isql = new InsertSQLBuilder("rpt_his_file_data");

		isql.putFieldValue("record_id", repordId);
		isql.putFieldValue("org_no", orgNo);
		isql.putFieldValue("data_date", dataDt);
		isql.putFieldValue("remark", modelType);
		
		try {
			bsUtil.executeUpdate(isql.getSQL());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新数据（RPT_HIS_FILE_DATA）
	 * @throws UnsupportedEncodingException 
	 */
	public void updateFileData(String recordId, String filename, String absolutepath) throws UnsupportedEncodingException {
		UpdateSQLBuilder isql = new UpdateSQLBuilder("rpt_his_file_data", "record_id='"+ recordId+ "'");
		isql.putFieldValue("filename", filename); // URLEncoder.encode(filename, "UTF-8")
		isql.putFieldValue("absolutepath", absolutepath);
		
		try {
			bsUtil.executeUpdate(isql.getSQL());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建数据库连接
	 * @param _dsName
	 * @return
	 * @throws Exception
	 */
	private Connection createDatabaseConnectionByDS(String _dsName) throws Exception {
		if (StringUtils.isBlank(_dsName)) {
			// 如果参数是null，则从database中获取数据源
			Properties props = new Properties();

			props.load(this.getClass().getClassLoader().getResourceAsStream("database.properties"));
			props.put("remarksReporting", "true");
			props.put("user", props.getProperty("jdbc.username"));
			props.put("password", props.getProperty("jdbc.password"));
			Class.forName(props.getProperty("jdbc.driverClassName"));
			Connection dbConn = DriverManager.getConnection(props.getProperty("jdbc.url"), props);

			return dbConn;
		}
		
		HashVO hvo = findDsVOByName(_dsName);

		String str_driver_name = hvo.getStringValue("driver_name");
		String str_conn_url = hvo.getStringValue("conn_url");
		String str_conn_user = hvo.getStringValue("conn_user");
		String str_conn_pwd = hvo.getStringValue("conn_pwd");

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
	 * 取得所有数据源
	 * @return
	 */
	public HashVO[] getAllDataSourceVOs() {
		String str_sql = "select t1.ds_name,t2.driver_name,t1.conn_url,t1.conn_user,t1.conn_pwd from bione_ds_info t1,bione_driver_info t2 where t1.driver_id=t2.driver_id";
		HashVO[] hvs_data = bsUtil.getHashVOs(str_sql);
		
		return hvs_data;
	}
	
	/**
	 * 浏览器名称转码
	 * @param request
	 * @param fileName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
		if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
			fileName = new String(fileName.getBytes("UTF-8"), "UTF-8"); // firefox浏览器
		} else if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 
				|| request.getHeader("User-Agent").toLowerCase().indexOf("trident") > 0 
				|| request.getHeader("User-Agent").toLowerCase().indexOf("edge")> 0) {
			fileName = URLEncoder.encode(fileName, "UTF-8");// IE浏览器
		} else if (request.getHeader("User-Agent").toLowerCase().indexOf("chrome") > 0) {
			fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");// 谷歌
		}
		
		return fileName;
	}
}