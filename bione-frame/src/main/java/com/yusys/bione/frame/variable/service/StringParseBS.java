package com.yusys.bione.frame.variable.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.variable.entity.BioneSysVarInfo;

/**
 * 字符串解析
 * 
 * <pre>
 * Title: 字符串解析
 * Description: 解析处于$间的字符的值，并生成解析后的新字符串。
 * </pre>
 * 
 * @author kangligong kanglg@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：		  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class StringParseBS extends BaseBS<BioneSysVarInfo> {
	// 占位符预设为'$'
	private String tag = "$";
	private String logicSysNO;

	/**
	 * 获取当前占位符
	 * 
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 修改占位符
	 * 
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * 解析字符串
	 * 
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getResult(String str) {
		String s = new String(str);
		String flag = StringUtils.substringBetween(s, tag);
		List<String> strList = Lists.newArrayList();
		while (flag != null) {
			strList.add(flag);
			s = StringUtils.replace(s, tag + flag + tag, "");
			flag = StringUtils.substringBetween(s, tag);
		}
		Map<String, Object> allVarMap = getValueMap(strList);
		Map<String, String> strVarMap = (Map<String, String>) allVarMap
				.get("strVarMap");// 字符串变量(直接替换)
		List<Object> objVars = (List<Object>) allVarMap.get("objVars");// 对象型变量(与SQL一起传回构造PreparedStatement)
		for (String val : strVarMap.keySet()) {
			str = StringUtils.replace(str, tag + val + tag, strVarMap.get(val));
		}
		Map<String, Object> result = Maps.newHashMap();
		result.put("sql", str);
		result.put("objVars", objVars);
		return result;
	}

	/**
	 * 解析字符串
	 * 
	 * @param str
	 * @param logSysNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getResult(String str, String logSysNo) {
		String s = new String(str);
		String flag = StringUtils.substringBetween(s, tag);
		List<String> strList = Lists.newArrayList();
		while (flag != null) {
			strList.add(flag);
			s = StringUtils.replace(s, tag + flag + tag, "");
			flag = StringUtils.substringBetween(s, tag);
		}
		Map<String, Object> allVarMap = getValueMap(strList, logSysNo);
		Map<String, String> strVarMap = (Map<String, String>) allVarMap
				.get("strVarMap");// 字符串变量(直接替换)
		List<Object> objVars = (List<Object>) allVarMap.get("objVars");// 对象型变量(与SQL一起传回构造PreparedStatement)
		for (String val : strVarMap.keySet()) {
			str = StringUtils.replace(str, tag + val + tag, strVarMap.get(val));
		}

		Map<String, Object> result = Maps.newHashMap();
		result.put("sql", str);
		result.put("objVars", objVars);
		return result;
	}

	/**
	 * 通过变量集合获取其变量-值的Map
	 * 
	 * @param strList
	 * @return
	 */
	public Map<String, Object> getValueMap(List<String> strList) {
		List<Object> objVars = Lists.newArrayList();// 平台型变量集合

		this.logicSysNO = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		Map<String, String> strMap = Maps.newHashMap();
		Map<String, List<BioneSysVarInfo>> dsMap = Maps.newHashMap();
		for (String str : strList) {
			String jql = "select info from BioneSysVarInfo info where info.logicSysNo=?0 and info.varNo=?1";
			List<BioneSysVarInfo> infoList = this.baseDAO.findWithIndexParam(
					jql, logicSysNO, str);
			if (infoList != null && infoList.size() > 0) {
				BioneSysVarInfo info = infoList.get(0);
				String varType = info.getVarType();
				if (GlobalConstants4frame.BIONE_SYS_VAR_TYPE_CONSTANT.equals(varType)) {
					strMap.put(str, info.getVarValue());
				} else if (GlobalConstants4frame.BIONE_SYS_VAR_TYPE_SQL
						.equals(varType)) {
					String dsId = info.getDsId();
					if (dsId != null) {
						if (dsMap.containsKey(dsId)) {
							List<BioneSysVarInfo> objList = dsMap.get(dsId);
							objList.add(info);
						} else {
							List<BioneSysVarInfo> objList = Lists
									.newArrayList();
							objList.add(info);
							dsMap.put(dsId, objList);
						}
					}
				} else if (GlobalConstants4frame.BIONE_SYS_VAR_TYPE_SYSTEM
						.equals(varType)) {
					Object o = this.getSystemVar(info.getVarValue());
					String className = o.getClass().getSimpleName();
					if (className.equals("String")) {
						strMap.put(str, o.toString());
					} else {
						objVars.add(o);
						strMap.put(str, "?");
					}
				}
			}
		}
		for (String key : dsMap.keySet()) {
			BioneDsInfo ds = this.getEntityById(BioneDsInfo.class, key);
			if (ds != null) {
				BioneDriverInfo driver = this.getEntityById(
						BioneDriverInfo.class, ds.getDriverId());
				if (driver != null) {
					String url = ds.getConnUrl();
					String usrId = ds.getConnUser();
					String passwd = ds.getConnPwd();
					String driverName = driver.getDriverName();
					if (driverName != null && !"".equals(driverName)
							&& url != null && !"".equals(url) && usrId != null
							&& !"".equals(usrId)) {
						Connection conn = null;
						ResultSet rs = null;
						Statement state = null;
						List<BioneSysVarInfo> objList = dsMap.get(key);
						try {
							Class.forName(driverName);
							conn = DriverManager.getConnection(url, usrId,
									passwd);
							state = conn.createStatement();
							for (BioneSysVarInfo obj : objList) {
								String sql = obj.getVarValue();
								rs = this.doSQL(state, sql, rs);
								if (rs != null
										&& rs.getMetaData().getColumnCount() == 1
										&& rs.next()) {
									int rowCount = rs.getRow();
									if (rowCount == 1) {
										String result = rs.getString(1);
										if (!rs.next()) {
											strMap.put(obj.getVarNo(), result);
										}
									}
								}
							}
						} catch (ClassNotFoundException e) {
						} catch (SQLException e) {
						} finally {
							try {
								rs.close();
								state.close();
								conn.close();
							} catch (SQLException e) {
							}
						}
					}
				}
			}
		}
		Map<String, Object> result = Maps.newConcurrentMap();
		result.put("strVarMap", strMap);// 常量或SQL型变量(字符串)
		result.put("objVars", objVars);// 平台变量(对象)
		return result;
	}

	/**
	 * 通过变量集合获取其变量-值的Map
	 * 
	 * @param strList
	 * @param logSysNo
	 * @return
	 */
	public Map<String, Object> getValueMap(List<String> strList, String logSysNo) {
		List<Object> objVars = Lists.newArrayList();// 平台型变量集合

		Map<String, String> strMap = Maps.newHashMap();
		Map<String, List<BioneSysVarInfo>> dsMap = Maps.newHashMap();
		for (String str : strList) {
			String jql = "select info from BioneSysVarInfo info where info.logicSysNo=?0 and info.varNo=?1";
			List<BioneSysVarInfo> infoList = this.baseDAO.findWithIndexParam(
					jql, logSysNo, str);
			if (infoList != null && infoList.size() > 0) {
				BioneSysVarInfo info = infoList.get(0);
				String varType = info.getVarType();
				if (GlobalConstants4frame.BIONE_SYS_VAR_TYPE_CONSTANT.equals(varType)) {// 常量型
					strMap.put(str, info.getVarValue());
				} else if (GlobalConstants4frame.BIONE_SYS_VAR_TYPE_SQL
						.equals(varType)) {// SQL型
					String dsId = info.getDsId();
					if (dsId != null) {
						if (dsMap.containsKey(dsId)) {
							List<BioneSysVarInfo> objList = dsMap.get(dsId);
							objList.add(info);
						} else {
							List<BioneSysVarInfo> objList = Lists
									.newArrayList();
							objList.add(info);
							dsMap.put(dsId, objList);
						}
					}
				} else if (GlobalConstants4frame.BIONE_SYS_VAR_TYPE_SYSTEM
						.equals(varType)) {
					Object o = this.getSystemVar(info.getVarValue());
					String className = o.getClass().toString();
					if (className.equals("String")) {
						strMap.put(str, o.toString());
					} else {
						objVars.add(o);
						strMap.put(str, "?");
					}
				}
			}
		}
		for (String key : dsMap.keySet()) {
			BioneDsInfo ds = this.getEntityById(BioneDsInfo.class, key);
			if (ds != null) {
				BioneDriverInfo driver = this.getEntityById(
						BioneDriverInfo.class, ds.getDriverId());
				if (driver != null) {
					String url = ds.getConnUrl();
					String usrId = ds.getConnUser();
					String passwd = ds.getConnPwd();
					String driverName = driver.getDriverName();
					if (driverName != null && !"".equals(driverName)
							&& url != null && !"".equals(url) && usrId != null
							&& !"".equals(usrId)) {
						Connection conn = null;
						ResultSet rs = null;
						Statement state = null;
						List<BioneSysVarInfo> objList = dsMap.get(key);
						try {
							Class.forName(driverName);
							conn = DriverManager.getConnection(url, usrId,
									passwd);
							state = conn.createStatement();
							for (BioneSysVarInfo obj : objList) {
								String sql = obj.getVarValue();
								rs = this.doSQL(state, sql, rs);
								if (rs != null
										&& rs.getMetaData().getColumnCount() == 1
										&& rs.next()) {
									int rowCount = rs.getRow();
									if (rowCount == 1) {
										String result = rs.getString(1);
										if (!rs.next()) {
											strMap.put(obj.getVarNo(), result);
										}
									}
								}
							}
						} catch (ClassNotFoundException e) {
						} catch (SQLException e) {
						} finally {
							try {
								if (rs != null) {
									rs.close();
								}
								if (state != null) {
									state.close();
								}
								if (conn != null) {
									conn.close();
								}
							} catch (SQLException e) {
							}
						}
					}
				}
			}
		}
		Map<String, Object> result = Maps.newConcurrentMap();
		result.put("strVarMap", strMap);// 常量或SQL型变量(字符串)
		result.put("objVars", objVars);// 平台变量(对象)
		return result;
	}

	private ResultSet doSQL(Statement st, String sql, ResultSet rs) {
		try {
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			return null;
		}
		return rs;
	}

	/**
	 * 平台变量转换
	 * 
	 * @param varValue
	 *            变量值
	 * @return
	 */
	private Object getSystemVar(String varValue) {
		Calendar now = new GregorianCalendar();
		if (GlobalConstants4frame.BIONE_SYSTEM_VAR_CURRENT_ORG.equals(varValue)) {
			return BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		} else if (GlobalConstants4frame.BIONE_SYSTEM_VAR_NOW_DATE.equals(varValue)) {
			return new Date(now.getTimeInMillis());
		} else if (GlobalConstants4frame.BIONE_SYSTEM_VAR_NOW_TIMESTAMP
				.equals(varValue)) {
			return new Timestamp(now.getTimeInMillis());
		} else if (GlobalConstants4frame.BIONE_SYSTEM_VAR_YESTODAYNOW_DATE
				.equals(varValue)) {
			now.add(Calendar.DATE, -1);
			return new Date(now.getTimeInMillis());
		} else if (GlobalConstants4frame.BIONE_SYSTEM_VAR_YESTODAYNOW_TIMESTAMP
				.equals(varValue)) {
			now.add(Calendar.DATE, -1);
			return new Timestamp(now.getTimeInMillis());
		}
		return null;
	}
}
