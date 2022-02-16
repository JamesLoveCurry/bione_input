package com.yusys.biapp.input.dict.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.rule.common.BesameValidate;
import com.yusys.biapp.input.rule.common.DataFormatValidate;
import com.yusys.biapp.input.rule.common.RuleUtils;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.utils.SysFunction;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.dialect.DBMS;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 数据操作类<br>
 * 1.取外表数据<br>
 * 2.取数据字典数据<br>
 * 3.取补录表数据<br>
 * @author tobe
 */
@Component("dataUtils")
@Transactional(readOnly = true)
public class DataUtils {
	public static int batchSize = 500;
	public final static String INPUT_TEMP_TABLE = "MID_";
	
	@Autowired
	private DataSourceBS dataSourceBS;
	
	/**
	 * 根据规则子项获取子项结果
	 * @param taskCase 任务实例
	 * @param ruleInfo 校验规则
	 * @param info 规则子项
	 * @param paramMap 其他查询参数
	 * @param cols 返回的列
	 * @param obj 可变参数<br>
	 *            第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 * @return 可能是一个结果或者一个字符集合
	 * @throws Exception
	 */
	public Object getDataByRuleItem(RptTskIns taskCase, RptInputListDataRuleInfo ruleInfo, RptInputListRuleItemInfo info, Map<String,String> paramMap,List<String> cols,Object... obj) throws Exception {
		if (StringUtils.isBlank(info.getDsId())) {// 常量
			return info.getAggregateFunc();
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";
		try {
			if (StringUtils.isNotBlank(info.getDsId())) {//来自数据库表
				String schema = this.dataSourceBS.getSchemaByDsId(info.getDsId());
				conn = this.dataSourceBS.getConnection(info.getDsId());
				stmt = conn.createStatement();
				sql = getSQLByItem(taskCase, info, null, 0, paramMap, schema, obj);
				Log.info("执行SQL："+sql);
				rs = stmt.executeQuery(sql);
				if (obj.length != 0 && !RuleUtils.LOGIC_CODE_IN.equals(ruleInfo.getLogicSysNo())) {// 有聚合函数
					if (rs.next()) {
						if (rs.getObject(1) == null) {
							return StringUtils.EMPTY;
						} else {
							return rs.getObject(1);
						}
					}
				} else {// in外表
					if (RuleUtils.LOGIC_CODE_IN.equals(ruleInfo.getLogicSysNo()) && info.getLeftOrRight().equals("left")) {
						if (!cols.contains(info.getAggregateFunc()))
							cols.add(info.getAggregateFunc());
						sql = getSQLByItem(taskCase, info, cols, 0, paramMap, schema, obj);
						Log.info("执行SQL："+sql);
						rs = stmt.executeQuery(sql);
						Map<String, String> km = Maps.newHashMap();
						while (rs.next()) {
							String k = "";
							for (String key : cols) {// 取主键值
								Object value = rs.getObject(key);
								if (value == null)
									value = "";
								k += value.toString() + DataFormatValidate.keysplit;
							}
							if (k.length() > 0) {
								k = k.substring(0, k.length() - 1);
							}
							km.put(rs.getString(info.getAggregateFunc()) + BesameValidate.keySplit + RandomUtils.uuid2(), k);
						}
						return km;
					} else {
						List<String> list = Lists.newArrayList();
						sql = getSQLByItem(taskCase, info, null, 0, paramMap, schema, obj);
						Log.info("执行SQL："+sql);
						rs = stmt.executeQuery(sql);
						while (rs.next()) {
							list.add(rs.getString(1));
						}
						return list;
					}
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return StringUtils.EMPTY;
	}

//	/**
//	 * 根据规则子项获取指定条数
//	 * @param taskCase 任务实例
//	 * @param ruleInfo 规则
//	 * @param info 规则子项
//	 * @param cols 返回的数据列
//	 * @param size 返回数据条数
//	 * @param obj 可变参数<br>
//	 *            第一个是数据日期<br>
//	 *            第二个是机构代码值,比如用户所属机构<br>
//	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
//	 *            第四个任务批次，也就是实例ID<br>
//	 * @return 字段和对应值组成的键值列表
//	 * @throws Exception
//	 */
//	public List<Map<String, Object>> getDataByRuleItem(UdipTaskCaseInfo taskCase,UdipDataRulesInfo ruleInfo, UdipRuleItemsInfo info,
//			List<String> cols, int size, Object... obj) throws Exception {
//		Connection conn = null;
//		Statement statement = null;
//		ResultSet rs = null;
//		List<Map<String, Object>> data = Lists.newArrayList();
//		try {
//			if (StringUtils.isNotBlank(info.getDsId())) {// 来着数据库表
//				conn = rdbConnectionManagerBS.getConnection(info.getDsId());
//				statement = conn.createStatement();
//
//				rs = statement.executeQuery(getSQLByItem(taskCase, info, cols, size,null, obj));
//
//				while (rs.next()) {
//					Map<String, Object> map = Maps.newHashMap();
//					for (String column : cols) {
//						map.put(column, rs.getObject(column));
//						data.add(map);
//					}
//				}
//			}
//		} catch (Exception e) {
//			throw new Exception(e);
//		} finally {
//			RdbConnectionManagerBS.closeResultSet(rs);
//			RdbConnectionManagerBS.closeStatement(statement);
//			RdbConnectionManagerBS.closeConnection(conn);
//		}
//		return data;
//	}

	/**
	 * 根据规则子项获取指定条数
	 * 
	 * @param temp
	 *            模版
	 * @param info
	 *            规则
	 * @param obj
	 *            可变参数<br>
	 *            第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 * @return 所有列的数据
	 * @throws Exception
	 */
	public List<Map<String, Object>> getDataByRuleInfo(RptInputLstTempleInfo temp, RptInputListDataRuleInfo info, Object... obj) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> data = Lists.newArrayList();
		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来着数据库表
				conn = this.dataSourceBS.getConnection(temp.getDsId());
				stmt = conn.createStatement();
				StringBuilder sql = new StringBuilder("select * from ").append(temp.getTableEnName()).append(" where 1=1 ");
				if (StringUtils.isNotBlank(info.getFilterCondition()))
					sql.append(" and ").append(info.getFilterCondition()).append(" ");
				initConditionNoState(temp, sql, null, obj);
				Log.info("执行SQL："+sql.toString());
				rs = stmt.executeQuery(sql.toString());
				while (rs.next()) {
					Map<String, Object> map = Maps.newHashMap();
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						String column = rs.getMetaData().getColumnName(i);
						map.put(column, rs.getObject(i));
					}
					data.add(map);
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return data;
	}

	/**
	 * 根据规则子项获取子项结果
	 * @return 可能是一个结果或者一个字符集合
	 * @param taskCase 任务实例
	 * @param info 规则子项
	 * @param cols 返回列
	 * @param size 分页
	 * @param paramMap 其他查询条件
	 * @param obj 可变参数<br>
	 *            第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 *            第五个schema
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String getSQLByItem(RptTskIns taskCase, RptInputListRuleItemInfo info, List<String> cols, int size,Map<String,String> paramMap, String schema,
			Object... obj) {

		String sql = StringUtils.EMPTY;
		if (cols != null) {// 有返回列
			sql = "select " + ArrayUtils.toString(cols) + " from " + schema + "." + info.getTableEnName() + " where 1=1 ";
		} else {
			sql = "select " + info.getAggregateFunc() + " from " + schema + "." + info.getTableEnName() + " where 1=1 ";

			if (StringUtils.isNotBlank(info.getFilterCondition())) {
				sql += " and " + SysFunction.replaceALL(info.getFilterCondition(), taskCase.getDataDate(), Integer.parseInt(taskCase.getDataDate()));
			}
		}
		
		if (MapUtils.isNotEmpty(paramMap)) {
			for (String key : paramMap.keySet()) {
				sql += " and " + key + "='" + paramMap.get(key) + "' ";
			}
		}
		
		if (obj != null && obj.length > 0 && StringUtils.isNotBlank(obj[0] + "")) {// 数据日期
			sql += " and " + UdipConstants.TAB_DATA_DATE + " = '" + obj[0].toString() + "'";

			if (taskCase != null) {
				String dataDate = FormatUtils.formatDate(FormatUtils.parseShortDate(obj[0].toString()));
				sql = SysFunction.replaceDate(sql, dataDate, Integer.parseInt(taskCase.getDataDate()));// 替换掉开始,结束日期
			}
		}
		if (obj != null && obj.length > 2 && StringUtils.isNotBlank(obj[2] + "")) {// 某个机构下的数据
			if (obj[1] instanceof List) {// 多值
				sql += " and " + obj[2].toString() + " in(" + ArrayUtils.toDBString((List<String>) obj[1], false) + ") ";
			} else {
				sql += " and " + obj[2].toString() + " ='" + obj[1] + "' ";
			}
		}
		if (obj != null && obj.length > 3 && StringUtils.isNotBlank(obj[3] + "")) {// 任务批次
			sql += " and " + UdipConstants.TAB_DATA_CASE + " = '" + obj[3].toString() + "'";
		}
		if (size > 0) {
			sql += " and rownum<" + size;
		}
		if (StringUtils.isNotBlank(info.getGroupField())) {
			sql += " group by " + info.getGroupField();
		}
		return sql;
	}

	/**
	 * 根据条件获取补录数据 传递过来的数据都是通过合法校验的，直接更新即可<br>
	 * @param temp 补录模板
	 * @param keyCol 模板主键
	 * @param condition 过滤条件
	 * @param colList 返回列
	 * @param sqlStr 自定义条件
	 * @param start 起始
	 * @param size 每次拿多少
	 * @param objects 可变参数<br>
	 *            1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，机构代码<br>
	 *            4.第四个参数，操作人员<br>
	 *            5.第五个参数，任务批次<br>
	 * @throws Exception
	 */
	public List<Map<String, Object>> getData(List<String>allTskNodeInsIds,RptInputLstTempleInfo temp, List<String> keyCol,
			Map<String, Map<String, String>> condition, String sqlStr, List<String> colList, 
			Map<String, String> columnsType, int start, int size, Object... objects) throws Exception {
		Connection conn = this.dataSourceBS.getConnection(temp.getDsId());
		List<Map<String, Object>> retList = this.getData(conn, allTskNodeInsIds, temp, keyCol, condition, sqlStr, colList, columnsType, start, size, objects);
		this.dataSourceBS.releaseConnection(null, null, conn);
		return retList;
	}
	
	/**
	 * 根据条件获取补录数据 传递过来的数据都是通过合法校验的，直接更新即可<br>
	 * @param temp 补录模板
	 * @param keyCol 模板主键
	 * @param condition 过滤条件
	 * @param colList 返回列
	 * @param sqlStr 自定义条件
	 * @param start 起始
	 * @param size 每次拿多少
	 * @param objects 可变参数<br>
	 *            1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，机构代码<br>
	 *            4.第四个参数，操作人员<br>
	 *            5.第五个参数，任务批次<br>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getData(Connection conn, List<String>allTskNodeInsIds,RptInputLstTempleInfo temp, List<String> keyCol,
			Map<String, Map<String, String>> condition, String sqlStr, List<String> colList, 
			Map<String, String> columnsType, int start, int size, Object... objects) throws Exception {
		List<String> colName = Lists.newArrayList();
		Map<String,String>  colNameType = columnsType;
		String dbType = DialectUtils.getDataBaseType(conn, false);
		for (int i = 0; i < colList.size(); i++) {
			if (ColumnType.TIMESTAMP.equals(colNameType.get(colList.get(i)))) {
				String colTime = "";
				if(dbType.equals("mysql")){
					colTime = "date_format(" + colList.get(i) + ",'%Y-%m-%d %H:%i:%s')" + colList.get(i);
				} else {
					colTime = "to_char(" + colList.get(i) + ",'yyyy-mm-dd hh24:mi:ss')" + colList.get(i);
				}
				colName.add(colTime);
			} else {
				colName.add(colList.get(i));
			}
		}
		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> data = Lists.newArrayList();
		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来自数据库表
				String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
				stmt = conn.createStatement();
				// 2.组装查询SQL
				StringBuilder sql = new StringBuilder();
				if (start >= 0 && size > 0){
					if(dbType.equals("oracle")){
						sql.append("select row_.*, rownum rownum_ from (");
						sql.append("select ").append(ArrayUtils.toString(colName)).append(" from " +schema+"."+ temp.getTableEnName() + "  where 1=1 ");// 组成返回字段
					}else if(dbType.equals("db2")){
						sql.append("select row_number() over() as rn,");
						sql.append(ArrayUtils.toString(colName)).append(" from " +schema+"."+ temp.getTableEnName() + "  where 1=1 ");// 组成返回字段
					}else if(dbType.equals("mysql")){
						sql.append("select * from "+schema+"."+ temp.getTableEnName() +" where 1=1 ");
					}else if(dbType.equals("postgresql")){
						sql.append("select * from "+schema+"."+ temp.getTableEnName() +" where 1=1 ");
					}
				}
				else{
					sql.append("select ").append(ArrayUtils.toString(colName)).append(" from " +schema+"."+ temp.getTableEnName() + "  where 1=1 ");// 组成返回字段
				}
				if (objects.length == 0 || objects == null) {
					initConditionBefore(dbType,sql, condition, "queryData");
				} else {
					//initCondition(temp, sql, null, objects);
					initConditionBefore(dbType,sql, condition, "");
				}
				if (StringUtils.isNotBlank(sqlStr)) {
					sql.append(sqlStr);
				}
				
				StringBuilder od = new StringBuilder();
				try {
					if(objects.length > 0){
						Pager p = (Pager)objects[objects.length-1];
						
						if(!StringUtils.isEmpty(p.getSortname()) && !StringUtils.isEmpty(p.getSortorder())){
							od.append(" order by ").append(p.getSortname()).append(" ").append(p.getSortorder()).append(" ");
							sql.append(od);
						}else{
							od.append(" order by SYS_ORDER_NO asc");
							sql.append(od);
						}
					}
				} catch (Exception e) {
					//转换失败说明最后一个参数不是分页信息
					od.append(" order by SYS_ORDER_NO asc");
					sql.append(od);
				}
				
//				if (!keyCol.isEmpty() && od.length() == 0) {
//					sql.append(" order by ").append(ArrayUtils.toString(keyCol));
//				}
				
				if (start >= 0 && size > 0) {
					StringBuilder sql2 = new StringBuilder();
					if(dbType.equals("mysql")){
						sql2.append(sql + "\n limit "+start + "," + size);
					} else if (dbType.equals("db2")){
						sql2.append(" select * from ("+sql+" ) where rn>")
						.append(start).append(" and rn<=").append(start+size);// 分批
					} else if (dbType.equals("postgresql")){
						sql2.append(sql).append(" limit ").append(size).append(" offset ").append(start);
					} else {
						sql2.append(" select * from ("+sql+" ) row_) where rownum_>")
						.append(start).append(" and rownum_<=").append(start+size);// 分批
					}
					sql = sql2;
				}
				// 3.返回查询结果
				Log.info("执行SQL："+sql.toString());
				rs = stmt.executeQuery(sql.toString());
				Map<String, Map<String, Object>> libList = null;
				String dataType = (String)objects[objects.length-2];
				
				if("export".equals(dataType)){
					libList = (Map<String, Map<String, Object>>)objects[objects.length-3];
				}
				
				while (rs.next()) {
					Map<String, Object> map = Maps.newHashMap();
					for (String column : colList) {
						if (rs.getObject(column) != null) {
							if("export".equals(dataType) && libList.containsKey(column)){
								map.put(column, libList.get(column).get(rs.getObject(column).toString().trim()));
							}else{
								map.put(column, rs.getObject(column).toString().trim());
							}
						} else {
							map.put(column, rs.getObject(column));
						}
					}
					data.add(map);
				}
				return data;
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, null);
		}
	}
	
	public String validateAddData(List<Map<String, Object>>dataAdd ,RptInputLstTempleInfo temp,  
			String caseId, String taskFlowNodeId,List<RptInputLstTempleConst> keyList,
			Map<String, RptInputLstTempleField> colMap) throws Exception{
		if(keyList==null || keyList.isEmpty())
			return null;
		StringBuilder sql = new StringBuilder();
		String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		Connection conn = this.dataSourceBS.getConnection(temp.getDsId());
		String dbType = DialectUtils.getDataBaseType(conn, false);
		String tbName = temp.getTableEnName();
		boolean ii = true;
		int size = 0;
		String field = "";
		String fieldCnNameStr = "";
		StringBuilder buff = new StringBuilder();
		List<String>priList=Lists.newArrayList();
		for (RptInputLstTempleConst cst : keyList) {
			if(cst.getKeyType().equals("primary")){
				String[] cns = cst.getKeyColumn().split(";");
				for(String cn:cns){
					priList.add(cn);
					size ++;
					if(ii){
						ii=false;
					}else{
						field+=",";
						fieldCnNameStr += "与";
					}
					if(colMap.containsKey(cn)){
						RptInputLstTempleField fieldCnName = colMap.get(cn);
						fieldCnNameStr += fieldCnName == null ? cn : fieldCnName.getFieldCnName();
					}else{
						fieldCnNameStr += cn;
					}
					field +=cn;
					
				}
			}
		}
		buff.append("  tianjin error field = "+field);
		sql.append("SELECT ").append(field).append(" FROM  ").append(schema) .append(".").append(tbName)
		.append(" WHERE  FLOW_NODE_ID ='").append(taskFlowNodeId).append("' AND (");
		buff.append("  sql="+sql.toString());
		boolean isFirst = true;
		String[] keys = colMap.keySet().toArray(new String[]{});
		buff.append("   keys ="+keys ).append("  dataAdd.size = "+dataAdd.size());
		for(Map<String,Object>data:dataAdd){
			if(isFirst)
				isFirst = false;
			else sql.append(" OR ");
			sql.append(" (  ");
			boolean isFirstCol = true;
			for (String key : priList) {// 以主键为条件
				if(key.equalsIgnoreCase("SYS_ORDER_NO"))
					continue;
				if(isFirstCol)
					isFirstCol = false;
				else
					sql.append("  AND  ");
				
				try {
					buff.append("   key = "+key+"   data.get(key) = " +data.get(key));
					String plVal = setValue(dbType,colMap.get(key).getFieldType(), data.get(key));
					if(null == plVal || "".equals(plVal)){
						return "["+fieldCnNameStr + "]对应的值不能为空";
					}
					sql.append(key).append(" = ").append("").append(plVal);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sql.append(" ) ");
		}
		sql.append(" ) ");
		buff.append(" \n  last sql = ").append(sql.toString());
		Statement stmt = null;
		ResultSet rs = null;
		boolean canReturn = false;
		String text="["+fieldCnNameStr+"]对应的值:";
		try {
			stmt = conn.createStatement();
			Log.info("执行SQL："+sql.toString());
			rs = stmt.executeQuery(sql.toString());
			isFirst = true;
			if (rs.next()) {
				text +="[";
				if(!canReturn)
					canReturn = true;
				for(int i = 1;i <=size;i++)
				{
					if(isFirst)
						isFirst=false;
					else text +=",";
					text += rs.getString(i);
				}
				text +="]";
			}
			text +="不可重复,请修改";
			if(canReturn)
				return text;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return null;
	}
	
	public List<Map<String,Object>>getValidateData(RptInputLstTempleInfo temp,List<String> taskNodeInsIds,List<RptInputLstTempleField>colList) throws Exception{

		StringBuilder sql = new StringBuilder(1000);
		String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		String tbName = temp.getTableEnName();
		if(!tbName.endsWith("VALIDATE"))
			tbName= tbName+"_VALIDATE";
		sql.append("SELECT * FROM  ").append(schema) .append(".").append(tbName);
		//sql.append(" WHERE FLOW_NODE_ID ='").append(rptTskNodeInsId).append("'");
		sql.append(" WHERE FLOW_NODE_ID IN  (");
		boolean isFirst = true;
		for(String nodeId:taskNodeInsIds){
			if(isFirst)
				isFirst = false;
			else
				sql.append(",");
			sql.append("'").append(nodeId).append("'");
		}
		sql.append(")");
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String,Object>>data=Lists.newArrayList();
		try {
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			stmt = conn.createStatement();
			Log.info("执行SQL："+sql.toString());
			// 3.返回查询结果
			rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Map<String, Object> map = Maps.newHashMap();
				for (RptInputLstTempleField column : colList) {
					if (rs.getObject(column.getFieldEnName()) != null) {
						map.put(column.getFieldEnName(), rs.getObject(column.getFieldEnName()).toString().trim());
					} else {
						map.put(column.getFieldEnName(), rs.getObject(column.getFieldEnName()));
					}
				}
				data.add(map);
			}		
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
	}
	
	public List<Map<String,Object>>getData(String sql,String dsId,List<String> colList,String dataDate, String orgNo) throws Exception{

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String,Object>>list=Lists.newArrayList();
		
		try{
			conn = this.dataSourceBS.getConnection(dsId);
			conn.createStatement();
			stmt = conn.createStatement();
			
			/**
			 * 将预加载sql里面@{dataDate}换成数据日期
			 */
			Log.info("执行SQL："+sql);
			sql = StringUtils.replace(sql, "@{dataDate}", "'" + dataDate + "'");
			sql = StringUtils.replace(sql, "@{orgNo}", "'" + orgNo + "'");
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				Map<String, Object> map = Maps.newHashMap();
				for(String col:colList){
					try {
						if (rs.findColumn(col) > 0 ) {
							map.put(col, rs.getObject(col));
						} 
					} catch (SQLException e) {
						Log.info("请检查数据预加载sql，查询结果中不存在此字段"+col);
						e.printStackTrace();
					}
				}
				list.add(map);
			}
		}finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return list;
	}

	private Long getMaxOrderNo(RptInputLstTempleInfo temp) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			stmt = conn.createStatement();
			String sql = "select max(" + UdipConstants.TAB_ORDER_NO + ") from " + temp.getTableEnName();
			Log.info("执行SQL："+sql);
			rs = stmt.executeQuery(sql);
			Long orderNo = 0l;
			while (rs.next()) {
				orderNo = rs.getLong(1);
				break;
			}
			return orderNo;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return 0l;
	}
	public void initTableData(RptInputLstTempleInfo temp,List<Map<String,Object>>dataList,Map<String, RptInputLstTempleField> tempColumns) throws Exception{

		String targetSchema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		Connection conn = this.dataSourceBS.getConnection(temp.getDsId());
		Long seqId = getMaxOrderNo(temp);
		PreparedStatement statement;
		if (dataList != null && !dataList.isEmpty()) {
			// 2.组装查询SQL
			Set<String> cols = dataList.get(0).keySet();
			StringBuilder colSql = new StringBuilder();
			StringBuilder valSql = new StringBuilder();
			// 组装列
			colSql.append("insert into ").append(targetSchema).append(".").append(temp.getTableEnName() + "(");//
			valSql.append("values(");
			colSql.append("DATAINPUT_ID").append(",COMMENTS,").append("FLOW_NODE_ID,");
			valSql.append("?,?,?,");
			for (String col : cols) {
				//判断是否 可补录
				if(col.equals("DATAINPUT_ID")||col.equals("COMMENTS")){
					continue;
				}else{
					colSql.append(col).append(",");
					valSql.append("?,");
				}
			}			
			// 添加固定列
			colSql.append(UdipConstants.TAB_DATA_CASE).append(",");
			valSql.append("?,");
			
			//添加序号
			colSql.append(UdipConstants.TAB_ORDER_NO).append(",");
			valSql.append("?,");

			colSql.append(UdipConstants.TAB_DATA_DATE).append(",").append(UdipConstants.TAB_DATA_STATE).append(",").append(UdipConstants.TAB_OPER_DATE).append(",").append(UdipConstants.TAB_OPERATOR).append(",").append(UdipConstants.TAB_OPER_ORG).append(")");
			valSql.append("?,").append("?,").append("?,").append("?,").append("?)");

//			// 覆盖式导入, 覆盖依据 caseId, sys_oper_org
//			conn.prepareStatement("delete from " + temp.getTableEnName() + " where " + UdipConstants.TAB_DATA_CASE + "=" + objects[4]
//						+ " and " + UdipConstants.TAB_OPER_ORG + "=" + objects[5]).execute();
//			
			String exeSql = colSql.append(valSql).toString();
			Log.info("执行SQL："+exeSql);
			statement = conn.prepareStatement(exeSql);
            // 组装列值
			for (Map<String, Object> data : dataList) {
				int index = 1;	
				setValue(statement, "VARCHAR2",  RandomUtils.uuid2(), index++);
				statement.setString(index++, "");
				setValue(statement, "VARCHAR2",  "INIT", index++);
				//setValue(statement, "VARCHAR2",  data.get("COMMENTS")==null?" ":data.get("COMMENTS"), index++);
				for (String col : cols) {
					if(col.equalsIgnoreCase("COMMENTS"))
						continue;
					Object o = data.get(col) == null ? StringUtils.EMPTY : data.get(col);
					String type = tempColumns.get(col).getFieldType();
					//判断是否 可补录
					String writable =  tempColumns.get(col).getAllowInput();
					if("1".equals(writable)){					
						setValue(statement, type, o, index++);
					}
				}
				statement.setString(index++, "");//任务实例ID 
				statement.setString(index++, (seqId++)+"");//序号
				String currentDate = DateUtils.getYYYY_MM_DD_HH_mm_ss();
				BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
				//statement.setString(index++, objects[4].toString());
				statement.setString(index++, currentDate);// 数据日期
				// statement.setString(index++, objects[1].toString());// 数据状态
				statement.setString(index++, UdipConstants.TASK_STATE_SAVE);
				statement.setString(index++, currentDate);// 补录日期
				statement.setString(index++, userObj.getUserId());// 补录人员
				statement.setString(index++, userObj.getOrgNo());// 补录人机构
				statement.execute();// 不支持批量操作,单条执行
			}
		}
	
	}
	
	/**
	 * 根据条件获取补录数据总条数 
	 * @param temp 补录模板
	 * @param condition 过滤条件
	 * @param objects 可变参数<br>
	 *            1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，机构代码<br>
	 *            4.第四个参数，操作人员<br>
	 *            5.第五个参数，任务批次<br>
	 * @throws Exception
	 */
	public int getDataCount(RptInputLstTempleInfo temp, Map<String, Map<String, String>> condition, String sqlStr, Object... objects) throws Exception {
		
		int dataCount = 0;
		Connection conn = null;
		if (StringUtils.isNotBlank(temp.getDsId())) {// 来自数据库表
			// 1.获取数据库连接
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			dataCount = this.getDataCount(conn, temp, condition, sqlStr, objects);
			this.dataSourceBS.releaseConnection(null, null, conn);
		}
		return dataCount;
	}
	
	public int getDataCount(Connection conn, RptInputLstTempleInfo temp, Map<String, Map<String, String>> condition, String sqlStr, Object... objects) throws Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
		Object dataCount = null;
		try {
				String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
				stmt = conn.createStatement();
				// 2.组装查询SQL
				StringBuilder sql = new StringBuilder();
				sql.append("select count(1) coun ").append(" from " +schema+"."+ temp.getTableEnName() + "  where 1=1 ");// 组成返回字段

				if (StringUtils.isNotBlank(sqlStr)) {
					sql.append(sqlStr);
				}
				// 3.返回查询结果
				Log.info("执行SQL："+sql.toString());
				rs = stmt.executeQuery(sql.toString());
				while (rs.next()) {
					dataCount = rs.getObject("coun");
				}
				return Integer.valueOf(dataCount.toString());
			
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, null);
		}
	}

	/**
	 * 保存数据
	 * @param conn
	 * @param statement
	 * @param temp
	 * @param tempColumns
	 * @param dataList
	 * @param objects
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void save(String taskNodeInstanceId,Connection conn, PreparedStatement statement, RptInputLstTempleInfo temp,
			Map<String, RptInputLstTempleField> tempColumns, List<Map<String, Object>> dataList, 
			Map<String, Map<String, Object>> liblist,Object... objects)
			throws Exception {
		String targetSchema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		
		try {
			if (dataList != null && !dataList.isEmpty()) {
				// 2.组装查询SQL
				Set<String> cols = dataList.get(0).keySet();
				StringBuilder colSql = new StringBuilder();
				StringBuilder valSql = new StringBuilder();
				// 组装列
				colSql.append("insert into ").append(targetSchema).append(".").append(temp.getTableEnName() + "(");//
				valSql.append("values(");
				colSql.append("DATAINPUT_ID").append(",COMMENTS,").append("FLOW_NODE_ID,");
				valSql.append("?,?,?,");
				for (String col : cols) {
					//判断是否 可补录
					if(col.equals("DATAINPUT_ID")||col.equals("COMMENTS")||col.equals("SYS_ORDER_NO")){
						continue;
					}else{
						 if("1".equals(tempColumns.get(col).getAllowInput())){					
							colSql.append(col).append(",");
							valSql.append("?,");
						 }
					}
				}			
				// 添加固定列
				if (objects != null && objects.length > 4) {
					colSql.append(UdipConstants.TAB_DATA_CASE).append(",");
					valSql.append("?,");
				}

				colSql.append(UdipConstants.TAB_DATA_DATE).append(",")
					.append(UdipConstants.TAB_DATA_STATE).append(",")
						.append(UdipConstants.TAB_OPER_DATE).append(",")
							.append(UdipConstants.TAB_OPERATOR).append(",")
								.append(UdipConstants.TAB_OPER_ORG).append(",")
									.append(UdipConstants.TAB_ORDER_NO).append("")
										.append(")");
				valSql.append("?,").append("?,").append("?,").append("?,").append("?,").append("?)");

//				// 覆盖式导入, 覆盖依据 caseId, sys_oper_org
//				conn.prepareStatement("delete from " + temp.getTableEnName() + " where " + UdipConstants.TAB_DATA_CASE + "=" + objects[4]
//							+ " and " + UdipConstants.TAB_OPER_ORG + "=" + objects[5]).execute();
//				
				statement = conn.prepareStatement(colSql.append(valSql).toString());
				Log.info("执行SQL："+colSql.append(valSql).toString());
	            // 组装列值
				StringBuilder objBuffer = new StringBuilder();
				// boolean supportsBatchUpdates = conn.getMetaData().supportsBatchUpdates();
				for (Map<String, Object> data : dataList) {
					int index = 1;	
					String id = RandomUtils.uuid2();
					objBuffer.append("       DATAINPUT_ID=").append(id).append("       comment=").append(data.get("COMMENTS")).append("       FLOW_NODE_ID=").append(taskNodeInstanceId);
					setValue(statement, "VARCHAR2",  id, index++);
					Object obj = data.get("COMMENTS");
					setValue(statement, "VARCHAR2", obj==null?"":(String)obj , index++);
					setValue(statement, "VARCHAR2",  taskNodeInstanceId, index++);
					for (String col : cols) {
						if(col.equalsIgnoreCase("COMMENTS")||col.equalsIgnoreCase("DATAINPUT_ID")||col.equals("SYS_ORDER_NO"))
							continue;
						Object o = data.get(col) == null ? StringUtils.EMPTY : data.get(col);
						String type = tempColumns.get(col).getFieldType();
						//判断是否 可补录
						 if("1".equals(tempColumns.get(col).getAllowInput())){		
						     if(liblist.containsKey(String.valueOf(o))){
                                 setValue(statement, type, liblist.get(String.valueOf(o)), index++);
                             }else{
                                 objBuffer.append("         ").append(col).append("=").append(o.toString());
                                 setValue(statement, type, o, index++);
                             }
						 }
					}
					if (objects != null && objects.length > 4)
					{
						statement.setString(index++, objects[4].toString());
						objBuffer.append("         objects[4]=").append(objects[4]);
					}
					statement.setString(index++, objects[0].toString());// 数据日期
					objBuffer.append("         数据日期=").append(objects[0]);
					// statement.setString(index++, objects[1].toString());// 数据状态
					statement.setString(index++, UdipConstants.TASK_STATE_SAVE);
					objBuffer.append("         TASK_STATE_SAVE=").append(UdipConstants.TASK_STATE_SAVE);
					statement.setString(index++, objects[2].toString());// 补录日期
					objBuffer.append("         补录日期=").append(objects[2].toString());
					statement.setString(index++, objects[3].toString());// 补录人员
					objBuffer.append("         补录人员=").append(objects[3].toString());
					if (objects[5] != null) {
						List<String> orgs = (List<String>) objects[5];
						if (orgs.size() > 0) {
							statement.setString(index++, orgs.get(0));// 补录人机构
							objBuffer.append("         补录人机构=").append(orgs.get(0));
						}
					}
					//修复填报时：保存数据后删除、添加数据没有保存而直接校验时错误信息行数错误20190610
					//statement.setInt(index++, dataOrderNo++);
					//获取了补录表中最大sys_order_no+1 ,代码在DataFileBS.saveDataFileById()中 mapList 20190819
					Object sysOrderNoObj =  data.get("SYS_ORDER_NO");
					int sysOrderNo = Long.valueOf(String.valueOf(sysOrderNoObj)).intValue();
					statement.setInt(index++, (int)sysOrderNo);
//					if (supportsBatchUpdates) {
					int count = 1;
					statement.addBatch();
					if (count++ % batchSize == 0) {// 每次删除batchSize条数据
						statement.executeBatch();// 批量删除
					}
//					} else {
//					
//						statement.execute();// 不支持批量操作,单条执行
//					}
				}
				statement.executeBatch();// 批量删除
			}
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}finally{
			if(null != statement){
				try {
					statement.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存新增补录数据<br>
	 * 根据主键来更新<br>
	 * @param temp 补录模板
	 * @param tempColumns 模板列
	 * @param dataList 需要保存的数据
	 * @param keyCols 主键包含的所有列
	 * @param objects 可变参数<br>
	 *            1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，补录日期<br>
	 *            4.第四个参数，补录人员<br>
	 *            5.第五个参数，任务批次<br>
	 */
	@Transactional(readOnly = false)
	public boolean updateData(RptInputLstTempleInfo temp, Map<String, RptInputLstTempleField> tempColumns,
			List<Map<String, Object>> dataList, List<String> keyCols, Object... objects) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {

			if (StringUtils.isNotBlank(temp.getDsId())) {// 来着数据库表
				// 1.获取数据库连接
				conn = this.dataSourceBS.getConnection(temp.getDsId());

				if (dataList != null && !dataList.isEmpty()) {
					// 2.组装查询SQL
					Set<String> cols = dataList.get(0).keySet();
					StringBuilder colSql = new StringBuilder();

					// 更新列
					colSql.append("update ").append(temp.getTableEnName() + " set ");//
					for (String col : cols) {
						colSql.append(col).append("=?,");
					}
					// 固定列
					colSql.append(UdipConstants.TAB_DATA_STATE).append("=?,").append(UdipConstants.TAB_OPER_DATE).append("=?,").append(UdipConstants.TAB_OPERATOR).append("=? ");
					colSql.append("where 1=1");

					// 主键列
					for (String col : keyCols) {// 以主键为条件
						colSql.append(" and ").append(col).append("=? ");
					}
					if (objects != null && objects.length > 4 && objects[4] != null)
						colSql.append(" and ").append(UdipConstants.TAB_DATA_CASE).append("=? ");
					stmt = conn.prepareStatement(colSql.toString());
					// 组装列值
					int count = 1;
					boolean supportsBatchUpdates = conn.getMetaData().supportsBatchUpdates();
					for (Map<String, Object> data : dataList) {
						int index = 1;

						// 更新列值
						for (String col : cols) {
							Object o = data.get(col) == null ? StringUtils.EMPTY : data.get(col);
							String type = tempColumns.get(col).getFieldType();
							setValue(stmt, type, o, index++);
						}
						// 固定列值
						stmt.setString(index++, objects[1].toString());// 数据状态
						stmt.setString(index++, objects[2].toString());// 补录日期
						stmt.setString(index++, objects[3].toString());// 补录人员

						// 主键列值
						for (String col : keyCols) {
							Object o = data.get(col) == null ? StringUtils.EMPTY : data.get(col);
							setValue(stmt, tempColumns.get(col).getFieldType(), o, index++);
						}
						if (objects != null && objects.length > 4 && objects[4] != null)
							stmt.setString(index++, objects[4].toString());

						if (supportsBatchUpdates) {
							stmt.addBatch();
							if (count % batchSize == 0 || count == dataList.size()) {// 每次更新batchSize条数据
								stmt.executeBatch();// 批量更新
							}
							count++;
						} else {
							stmt.execute();// 不支持批量操作,单条执行
						}
					}
				}
				// 3.返回查询结果
				return true;
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(null, stmt, conn);
		}
		return false;
	}

	private void copyDateBeforeUpdate(String schema,RptInputLstTempleInfo temp,List<Map<String, Object>> dataList, List<String> keyCols,Connection conn) throws SQLException{

		StringBuilder sql = new StringBuilder(1000);
		boolean needUpdate = true;
		boolean isFirst =true;
		Statement statement = conn.createStatement();
		List<String>dataInputIds=Lists.newArrayList();
		for (Map<String, Object> data : dataList) {
			isFirst =true;
			sql = new StringBuilder(1000);
			String dataInputId = (String)data.get("DATAINPUT_ID");
			dataInputIds.add(dataInputId);
			sql.append(" INSERT INTO ").append(schema).append(".").append(temp.getTableEnName()).append("_DELETE ");
			sql.append(" SELECT *FROM ").append(schema).append(".").append(temp.getTableEnName() );
			sql.append(" WHERE DATAINPUT_ID='").append(dataInputId);
			if(keyCols.size()==1&&keyCols.get(0).equalsIgnoreCase("SYS_ORDER_NO"))
			{
				if(needUpdate==true)
					needUpdate = false;
			}else{
				sql.append("' AND (");
				// 主键列值
				for (String col : keyCols) {
					if(col.equalsIgnoreCase("SYS_ORDER_NO"))
						continue;
					if(isFirst)
						isFirst = false;
					else sql.append("  OR  ");
					sql.append(col).append(" != '").append(data.get(col)).append("' ");
				}
				sql.append(")");
				Log.info("执行SQL："+sql.toString());
				statement.execute(sql.toString());// 不支持批量操作,单条执行
			}
		}
		//3. 更新状态和时间
		if(needUpdate){
			sql = new StringBuilder();
			sql.append(" UPDATE ").append(schema).append(".").append(temp.getTableEnName()).append("_DELETE ");
			sql.append(" SET SYS_DATA_STATE ='delete' , sys_oper_date= '").append(DateUtils.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss")).append("'");
			sql.append(" WHERE ( DATAINPUT_ID IN  (");
			isFirst = true;
			for(int dtIdx = 0; dtIdx < dataInputIds.size(); dtIdx++){
			    String dataInputId = dataInputIds.get(dtIdx);
				if(isFirst){
				    isFirst = false;
				} else{
				    sql.append(",");
				}
				sql.append("'").append(dataInputId).append("'");
				
				if(dtIdx % 999 == 0 && dtIdx < dataInputIds.size() && dtIdx != 0){
				    sql.append(") or DATAINPUT_ID IN  ( ");
				    isFirst = true;
				}
			}
			sql.append(") ");
			
			sql.append(" ) AND SYS_DATA_STATE!='delete'");
			Log.info("执行SQL："+sql.toString());
			statement.execute(sql.toString());
		}
	}
	private void update(boolean needCopy,Connection conn, PreparedStatement statement, RptInputLstTempleInfo temp,
			Map<String, RptInputLstTempleField> tempColumns, List<Map<String, Object>> dataList, List<String> keyCols,
			Map<String, Map<String, Object>> liblist,Object... objects) throws Exception {
		
		String targetSchema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		try {
			if (dataList != null && !dataList.isEmpty()) {
				// 2.组装查询SQL
				Set<String> cols = dataList.get(0).keySet();
				//cols.remove("COMMENTS");
				//cols.remove("DATAINPUT_ID");
				StringBuilder colSql = new StringBuilder();
				// 更新列
				colSql.append("update ").append(targetSchema).append(".").append(temp.getTableEnName() + " set ");//
				colSql.append(" COMMENTS=?, ");
				for (String col : cols) {
					//判断是否 可补录
					if(col.equals("COMMENTS"))
						continue;
					if(col.equalsIgnoreCase("DATAINPUT_ID")) continue;
					
					 if ("1".equals(tempColumns.get(col).getAllowInput())) {
						 colSql.append(col).append("=?,");
					 }
				}
				// 固定列
				colSql.append(UdipConstants.TAB_DATA_STATE).append("=?,")
				    .append(UdipConstants.TAB_OPER_DATE)
				    .append("=?,")
				    .append(UdipConstants.TAB_OPERATOR)
				    .append("=? ");
				
				/* 20190618 
				 * 	以DATAINPUT_ID为条件更新
				 * 	colSql.append("where DATAINPUT_ID=?");
				 * 20190906
				 * 	当dataList 中的 DATAINPUT_ID没有数据时，说明是页面导入功能修改的，这种情况以key为条件更新
				 * 	当dataList中的DATAINPUT_ID有数据时，说明是页面手动修改保存的，这种情况可能会修改key字段的值，所以要以DATAINPUT_ID为条件更新字段
				 * */
				Map<String, Object> dataMap = dataList.get(0);
				if(StringUtils.isBlank((String)dataMap.get("DATAINPUT_ID"))) {
					colSql.append("where 1=1 ");
					// 主键列
					for (String col : keyCols) {// 以主键为条件
						colSql.append(" and ").append(col).append("=? ");
					}
				}else {
					colSql.append("where DATAINPUT_ID=?");
				}
				
				//if (objects != null && objects.length > 5 && objects[5]!=null)
				//	colSql.append(" and ").append(UdipConstants.TAB_DATA_CASE).append("=? ");
				conn.setAutoCommit(false);
				Log.info("执行SQL："+colSql.toString());
				statement = conn.prepareStatement(colSql.toString());
				// 组装列值
				int count = 1;
				if(needCopy)
					copyDateBeforeUpdate( targetSchema, temp, dataList, keyCols,conn);
				for (Map<String, Object> data : dataList) {
					int index = 1;
					String dataInputId = (String)data.get("DATAINPUT_ID");
					Object obj = data.get("COMMENTS");
					statement.setString(index, obj==null?"":(String)obj);
					index++;
					// 更新列值
					//statement.setString(index++, UdipConstants.TASK_STATE_SAVE);
					for (String col : cols) {
						if(col.equals("COMMENTS"))
							continue;
						Object o = data.get(col) == null ? StringUtils.EMPTY : data.get(col);
						if(col.equals("DATAINPUT_ID"))continue;
						String type = tempColumns.get(col).getFieldType();
						//判断是否 可补录
						 if("1".equals(tempColumns.get(col).getAllowInput())){		
						     if(liblist.containsKey(col)){
						         setValue(statement, type, liblist.get(col).get(String.valueOf(o)), index++);
						     }else{
						         setValue(statement, type, o, index++);
						     }
						 }
						
					}
					// 固定列值
					// statement.setString(index++, objects[1].toString());// 数据状态
					statement.setString(index++, UdipConstants.TASK_STATE_SAVE);
					statement.setString(index++, objects[2].toString());// 补录日期
					statement.setString(index++, objects[3].toString());// 补录人员
					
					/*20190618 
					 * 	以DATAINPUT_ID为条件更新 
					  	statement.setString(index++, dataInputId);
					 *20190906
					 * 	当dataList 中的 DATAINPUT_ID没有数据时，说明是页面导入功能修改的，这种情况以key为条件更新
					 * 	当dataList中的DATAINPUT_ID有数据时，说明是页面手动修改保存的，这种情况可能会修改key字段的值，所以要以DATAINPUT_ID为条件更新字段 
					 */
					if(colSql.indexOf("DATAINPUT_ID") == -1) {
						// 主键列值
						for (String col : keyCols) {
							Object o = data.get(col) == null ? StringUtils.EMPTY : data.get(col);
							setValue(statement, tempColumns.get(col).getFieldType(), o, index++);
						}
					}else {
						statement.setString(index++, dataInputId);
					}
					
//					if (supportsBatchUpdates) {
						statement.addBatch();
						if (count % batchSize == 0 || count == dataList.size()) {// 每次更新batchSize条数据
							statement.executeBatch();// 批量更新
						}
						count++;
//					} else {
//						statement.execute();// 不支持批量操作,单条执行
//					}
				}
			}
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}finally{
			if(statement != null){
				try {
					statement.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		
	}
	/**
	 * 更新数据
	 * @param conn
	 * @param statement
	 * @param temp
	 * @param tempColumns
	 * @param dataList
	 * @param keyCols
	 * @param objects
	 * @throws SQLException
	 */
	private void update(Connection conn, PreparedStatement statement, RptInputLstTempleInfo temp,
			Map<String, RptInputLstTempleField> tempColumns, List<Map<String, Object>> dataList, List<String> keyCols,
			Map<String, Map<String, Object>> liblist,Object... objects) throws Exception {
		
		this.update(true, conn, statement, temp, tempColumns, dataList, keyCols,liblist, objects);
		
	}

	/**
	 * 更新数据状态<br>
	 * @param temp 补录模板
	 * @param dataList 需要保存的数据
	 * @param objects 可变参数<br>
	 *            1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，补录机构<br>
	 *            4.第四个参数，任务批次<br>
	 */
	@Transactional(readOnly = false)
	public boolean updateDataState(RptInputLstTempleInfo temp, Object... objects) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		try {

			String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来自数据库表
				// 1.获取数据库连接
				conn = this.dataSourceBS.getConnection(temp.getDsId());
				conn.setAutoCommit(false);
				// 2.组装查询SQL
				StringBuilder sql = new StringBuilder();

				sql.append("update ").append(schema).append(".").append(temp.getTableEnName());
				initConditionUpdState(temp, sql, null, objects);
				stmt = conn.createStatement();
				Log.info("执行SQL："+sql.toString());
				stmt.execute(sql.toString());
				conn.commit();
				return true;
			}
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			this.dataSourceBS.releaseConnection(null, stmt, conn);
		}
		return false;
	}

	/**
	 * 返回删除成功条数数量，用于前台提示
	 * @param conn
	 * @param statement
	 * @param temp
	 * @param colMap
	 * @param keyList
	 * @param rows
	 * @param objects
	 * @return
	 * @throws Exception
	 */
	private int delete(Connection conn, Statement statement, RptInputLstTempleInfo temp, Map<String, RptInputLstTempleField> colMap,
			List<String> keyList, List<Map<String, Object>> rows, Object... objects) throws Exception {
		String dbType = DialectUtils.getDataBaseType(conn, false);
		int count = 1;
		String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		// 2.组装查询SQL
		boolean supportsBatchUpdates = conn.getMetaData().supportsBatchUpdates();
		StringBuilder sb = new StringBuilder();
		initCondition(temp, sb, null, objects);// 其他条件
		int total = 0;
		try {
			for (Map<String, Object> row : rows) {
				StringBuilder sql = new StringBuilder();
				sql.append("delete from ").append(schema).append(".").append(temp.getTableEnName() + "  where 1=1 ");
				for (String col : keyList) {// 以主键为条件
					sql.append(" and ").append(col).append(" = ").append(setValue(dbType,colMap.get(col).getFieldType(), row.get(col)));
				}
				if (supportsBatchUpdates) {
					statement.addBatch(sql.append(sb).toString());
					if (count % batchSize == 0 || count == rows.size()) {// 每次删除batchSize条数据
						int[] result = statement.executeBatch();// 批量删除
						for(int i=0; i<result.length; i++){
							total = total + result[i];
						}
					}
					count++;
				} else {
					Log.info("执行SQL："+sql.toString());
					total = total + statement.executeUpdate(sql.toString());// 不支持批量操作,单条执行
				}
			}
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}finally{
			if(statement != null){
				try {
					statement.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return total;
	}

	/**
	 * 查询表在数据库中是否存在
	 * @param temp
	 * @return
	 * @throws Exception
	 */
	public boolean isInfoExist(RptInputLstTempleInfo temp) throws Exception{
		Connection conn=null;
		Statement statement=null;
		try {
			//获取数据库连接
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			statement = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			//判断数据库类型
			String dbType = DialectUtils.getDataBaseType(conn, false);
			if (dbType.equals("oracle")) {
				sql.append("select * from user_tables where table_name='").append(temp.getTableEnName()).append("'");//不加where 1=1了
			}else if(dbType.equals("mysql")){
				sql.append("SELECT * from INFORMATION_SCHEMA.TABLES where TABLE_NAME='").append(temp.getTableEnName()).append("'");
			}else if(dbType.equals("db2")){
				sql.append("SELECT * from syscat.tables where TABNAME='").append(temp.getTableEnName()).append("'");
			}else {
				sql.append("select * from user_tables where table_name='"+temp.getTableEnName()+"'");
			}
			ResultSet rs = statement.executeQuery(sql.toString());
			if (rs.next()) {
				//判断集合中是否有数据存在，有数据返回true,这里已经拿到集合中的第一条数据
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(conn);
		}
		//没有数据存在，返回false
		return false;
	}

	public void copyAndDeleteRecord(RptInputLstTempleInfo temp,List<String> taskNodeInsIds) throws Exception{
		String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());//获取数据源名称
		Connection conn = null;
		Statement statement = null;
		try {
			// 1.获取数据库连接
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			statement = conn.createStatement();
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			// 2.拷贝SQL
			sql = new StringBuilder();
			sql.append(" INSERT INTO ").append(schema).append(".").append(temp.getTableEnName()).append("_DELETE ");
			sql.append(" SELECT *FROM ").append(schema).append(".").append(temp.getTableEnName() );
			sql.append(" WHERE FLOW_NODE_ID IN  (");
			boolean isFirst = true;
			for(String nodeId:taskNodeInsIds){
				if(isFirst)
					isFirst = false;
				else
					sql.append(",");
				sql.append("'").append(nodeId).append("'");
			}
			sql.append(")");
			Log.info("执行SQL："+sql.toString());
			statement.execute(sql.toString());// 不支持批量操作,单条执行
			//3. 更新状态和时间
			sql = new StringBuilder();
			sql.append(" UPDATE ").append(schema).append(".").append(temp.getTableEnName()).append("_DELETE ");
			sql.append(" SET SYS_DATA_STATE ='delete'");
			sql.append(" WHERE FLOW_NODE_ID IN  (");
			isFirst = true;
			for(String nodeId:taskNodeInsIds){
				if(isFirst)
					isFirst = false;
				else
					sql.append(",");
				sql.append("'").append(nodeId).append("'");
			}
			sql.append(") AND SYS_DATA_STATE!='delete'");
			Log.info("执行SQL："+sql.toString());
			statement.execute(sql.toString());// 不支持批量操作,单条执行
			//4. 删除原表数据
			sql = new StringBuilder();
			sql.append("delete from ").append(schema).append(".").append(temp.getTableEnName());
			sql.append(" WHERE FLOW_NODE_ID IN  (");
			isFirst = true;
			for(String nodeId:taskNodeInsIds){
				if(isFirst)
					isFirst = false;
				else
					sql.append(",");
				sql.append("'").append(nodeId).append("'");
			}
			sql.append(")");
			Log.info("执行SQL："+sql.toString());
			statement.execute(sql.toString());// 不支持批量操作,单条执行
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw new Exception(e);
		} finally {		
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(conn);
		}
	}

//	/**
//	 * 组装sql，根据caseId删除数据
//	 * @param conn
//	 * @param statement
//	 * @param temp
//	 * @param objects
//	 * @throws SQLException
//	 */
//	private void delete(Connection conn, Statement statement, UdipTemple temp, Object... objects) throws SQLException {
//		// 2.组装查询SQL
//		StringBuilder sql = new StringBuilder();
//		sql.append("delete from ").append(temp.getTableName() + "  where 1=1 ");
//		initCondition(temp, sql, null, objects);// 其他条件
//		statement.addBatch(sql.toString());
//		statement.executeBatch();// 批量删除
//
//	}

	/**
	 * 根据条件生成过滤的SQL
	 * @param temp 补录模板
	 * @param sql
	 * @param condition
	 * @param objects 1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，机构代码<br>
	 *            4.第四个参数，操作人员<br>
	 *            5.第五个参数，任务批次<br>
	 */
	@SuppressWarnings("unchecked")
	private void initCondition(RptInputLstTempleInfo temp, StringBuilder sql, Map<String, String> condition, Object... objects) {
		if (objects != null && objects.length > 0 && objects[0] != null) {// 第一个参数，数据日期
			sql.append(" and ").append(UdipConstants.TAB_DATA_DATE).append(" ='").append(objects[0]).append("' ");
		}
		if (objects != null && objects.length > 1 && objects[1] != null) {// 第二个参数，数据状态
			if (objects[1] instanceof List) {// 多值
				sql.append(" and ").append(UdipConstants.TAB_DATA_STATE).append(" in(");
				sql.append(ArrayUtils.toDBString((List<String>) objects[1], false)).append(") ");
			} else if (objects[1] != null) {
				sql.append(" and ").append(UdipConstants.TAB_DATA_STATE).append(" ='").append(objects[1]).append("' ");
			}
		}
		if (objects != null && objects.length > 2 && StringUtils.isNotBlank(temp.getOrgColumn()) && objects[2] != null) {// 第三个参数，机构代码
			if (objects[2] instanceof List) {// 多值
				sql.append(" and ").append(temp.getOrgColumn()).append(" in(");
				sql.append(ArrayUtils.toDBString((List<String>) objects[2], false)).append(") ");
			} else {
				sql.append(" and ").append(temp.getOrgColumn()).append(" ='").append(objects[2]).append("' ");
			}
		}
		if (objects != null && objects.length > 4 && objects[4] != null) {// 第五个参数，任务批次
			sql.append(" and ").append(UdipConstants.TAB_DATA_CASE).append(" ='").append(objects[4]).append("' ");
		}
		initCondition(sql, condition);
	}

	/**
	 * 根据条件生成过滤的SQL
	 * @param temp 补录模板
	 * @param sql
	 * @param condition
	 * @param objects 第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 */
	@SuppressWarnings("unchecked")
	private void initConditionNoState(RptInputLstTempleInfo temp, StringBuilder sql, Map<String, String> condition, Object... objects) {
		if (objects != null && objects.length > 0 && objects[0] != null) {// 第一个参数，数据日期
			sql.append(" and ").append(UdipConstants.TAB_DATA_DATE).append(" ='").append(objects[0]).append("' ");
		}
		if (objects != null && objects.length > 1 && StringUtils.isNotBlank(temp.getOrgColumn())) {// 第三个参数，机构代码
			if (objects[1] instanceof List) {// 多值
				sql.append(" and ").append(temp.getOrgColumn()).append(" in(");
				sql.append(ArrayUtils.toDBString((List<String>) objects[1], false)).append(") ");
			} else {
				sql.append(" and ").append(temp.getOrgColumn()).append(" ='").append(objects[1]).append("' ");
			}
		}
		if (objects != null && objects.length > 3 && objects[3] != null) {// 第四个参数，任务批次
			sql.append(" and ").append(UdipConstants.TAB_DATA_CASE).append(" ='").append(objects[3]).append("' ");
		}
		initCondition(sql, condition);
	}

	/**
	 * 整批修改状态时拼接的sql语句
	 * @param temp 补录模板
	 * @param sql
	 * @param condition
	 * @param objects 1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，补录机构<br>
	 *            4.第四个参数，任务批次<br>
	 */
	@SuppressWarnings("unchecked")
	private void initConditionUpdState(RptInputLstTempleInfo temp, StringBuilder sql, Map<String, String> condition, Object... objects) {
		if (objects != null && objects.length > 1 && objects[1]!=null) {
			sql.append(" set ").append(UdipConstants.TAB_DATA_STATE).append(" ='").append(objects[1].toString()).append("'");
		}
		if(objects != null && (objects[0]!=null ||( objects.length > 2 && StringUtils.isNotBlank(temp.getOrgColumn()))) || ( objects.length > 3 && objects[3]!=null)){
			sql.append(" where 1=1 ");
		}
		if (objects != null && objects.length > 0 && objects[0]!=null) {// 第一个参数，数据日期
			sql.append(" and ").append(UdipConstants.TAB_DATA_DATE).append(" ='").append(objects[0]).append("' ");
		}
		if (objects != null && objects.length > 2 && StringUtils.isNotBlank(temp.getOrgColumn()) && CollectionUtils.isNotEmpty((List<String>) objects[2])) {// 第三个参数，机构代码
			if (objects[2] instanceof List &&UdipConstants.STATE_YES.equals(temp.getAllowInputLower()) ) {// 多值
				sql.append(" and ").append(temp.getOrgColumn()).append(" in(").append(ArrayUtils.toDBString((List<String>) objects[2], false)).append(") ");
			} else {
				sql.append(" and ").append(temp.getOrgColumn()).append(" ='").append((objects[2] instanceof List)?((List<String>)objects[2]).get(0):objects[2]).append("' ");
			}
		}
		if (objects != null && objects.length > 3 && objects[3]!=null) {// 第四个参数，任务批次
			sql.append(" and ").append(UdipConstants.TAB_DATA_CASE).append(" ='").append(objects[3]).append("' ");
		}
		initCondition(sql, condition);
	}

	/**
	 * 根据查询调整组装查询SQL
	 * @param sql
	 * @param condition
	 */
	private static void initCondition(StringBuilder sql, Map<String, String> condition) {
		if (condition != null) {// 其他查询条件
			for (String key : condition.keySet()) {
				String o = condition.get(key);
				if (o.indexOf(",") != -1) {// 多值
					sql.append(" and ").append(key).append(" in(");
					sql.append(ArrayUtils.toDBString((List<String>) ArrayUtils.asCollection(o), false)).append(") ");
				} else {
					sql.append(" and ").append(key).append(" ='").append(o).append("' ");
				}
			}
		}
	}

	/**
	 * 根据查询调整组装查询SQL
	 * @param sql
	 * @param condition
	 * @throws Exception
	 */
	private static void initConditionBefore(String dbType, StringBuilder sql, Map<String, Map<String, String>> condition, String flag) throws Exception {
		if (condition != null) {// 其他查询条件
			for (String key : condition.keySet()) {
				Map<String, String> o = condition.get(key);
				String type = o.get("type");
				String value = o.get("value");
				if (StringUtils.isNotBlank(flag) && "queryData".equals(flag)) {
					sql.append(" and ").append(key).append(setQueryValue(dbType,type, value));
				} else {
					sql.append(" and ").append(key).append("=").append(setValue(dbType,type, value));
				}
			}
		}
	}

	/**
	 * 根据条件判断是否存在数据
	 * @param dsId 数据源
	 * @param table 校验的数据库表
	 * @param values 条件值
	 * @return
	 */
	public Map<String, Object> exit(Connection conn, String table, List<RptInputLstTempleField> columns, Map<String, Object> values) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, Object> data = Maps.newHashMap();
		try {
			// 1.获取数据库连接
			stmt = conn.createStatement();
			String sql = "select * from " + table + " where 1 =1 ";
			String dbType = DialectUtils.getDataBaseType(conn, false);
			if (values != null) {
				for (int i = 0; i < columns.size(); i++) {
					RptInputLstTempleField column = columns.get(i);
					sql += " and " + column.getFieldEnName() + "=" + setValue(dbType,column.getFieldType(), values.get(column.getFieldEnName()));
				}
			}
			Log.info("执行SQL："+sql.toString());
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				for (int index = 1; index <= rs.getMetaData().getColumnCount(); index++) {
					data.put(rs.getMetaData().getColumnName(index),
							rs.getObject(index));
				}
				return data;
			}
			return null;
		} catch (Exception e) {
			throw (e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, null);
		}
	}

	/**
	 * 根据字段类型设置参数值
	 * @param statement
	 * @param type 字段类型
	 * @param value 字段值
	 * @param index 参数索引
	 * @throws Exception
	 */
	public static void setValue(PreparedStatement statement, String type, Object value, int index) throws Exception {
		if (ColumnType.DATE.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {

				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 
				if(value.toString().length()==10)
					value= value.toString()+" 00:00:00";
				java.util.Date date = sdf.parse(value.toString()); 
				statement.setDate(index, new Date(date.getTime()));
			} else {
				statement.setDate(index, null);
			}
		} else if (ColumnType.LONG.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				statement.setLong(index, Long.valueOf(value.toString()));
			} else {
				statement.setString(index, null);
			}
		} else if (ColumnType.BOOLEAN.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				statement.setBoolean(index, Boolean.valueOf(value.toString()));
			} else {
				statement.setString(index, null);
			}
		} else if (ColumnType.INTEGER.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				statement.setInt(index, Integer.valueOf(value.toString()));
			} else {
				statement.setString(index, null);
			}
		} else if (ColumnType.DECIMAL.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				BigDecimal bigDecimal = new BigDecimal(value.toString());
//				bigDecimal = bigDecimal.divide(new BigDecimal(1), 18, BigDecimal.ROUND_HALF_DOWN);
				statement.setBigDecimal(index, bigDecimal);
			} else {
				statement.setString(index, null);
			}
		}else if(ColumnType.TIMESTAMP.equals(type)){
			if(value != null && StringUtils.isNotBlank(value.toString())){
				Timestamp time = Timestamp.valueOf(value.toString());
				statement.setTimestamp(index, time);
			}else{
				statement.setString(index, null);
			}
		} else {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				//威海客户化需求,需要变为GBK的编码
				//value = new String(value.toString().getBytes("UTF-8"), "GBK");
				statement.setString(index, value.toString());
			} else {
				statement.setString(index, null);
			}
		}
	}

	/**
	 * 
	 * @param type
	 * @param column
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String getValue(String type, String column, Object value) throws Exception {
		if (value == null)
			return StringUtils.EMPTY;
		if (ColumnType.DATE.equals(type)) {
			return DateUtils.formatDate(DateUtils.getDateStart(value.toString()), "yyyy-MM-dd");
		} else {
			return value.toString();
		}
	}

	/**
	 * 字段值的翻译
	 * @param type
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String setValue(String dbType, String type, Object value) throws Exception {
		if (ColumnType.DATE.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				if("mysql".equals(dbType)){
					return "date_format('"+value.toString()+"','%Y-%m-%d')";
				}
				return "to_date('" + value.toString() + "','yyyy-mm-dd')";
			} else {
				return "";
			}
		} else if (ColumnType.BOOLEAN.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				return "" + Boolean.valueOf(value.toString()) + "";
			} else {
				return "";
			}
		} else if (ColumnType.INTEGER.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				return "" + Integer.valueOf(value.toString()) + "";
			} else {
				return "";
			}
		}else if (ColumnType.NUMBER.equals(type) || ColumnType.DECIMAL.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				return "" + value.toString() + "";
			} else {
				return "";
			}
		} else if(ColumnType.TIMESTAMP.equals(type)){
			if(value != null && StringUtils.isNotBlank(value.toString())){
				if("mysql".equals(dbType)){
					return "date_format('"+value.toString()+"','%Y-%m-%d %H:%i:%s.%f')";
				}
				return "to_timestamp('" + value.toString() + "','yyyy-mm-dd hh:mi:ss.ff')";
			}else{
				return "";
			}
		} else {
			return "'" + value.toString() + "'";
		}
	}

	/**
	 * 补录自定义查询字段值的翻译
	 * @param type
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String setQueryValue(String dbType, String type, Object value) throws Exception {
		StringBuilder sql = new StringBuilder();
		if (ColumnType.DATE.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				if("mysql".equals(dbType)){
					sql.append(" = date_format('" + value.toString() + "','%Y-%m-%d')");
				} else {
					sql.append(" = to_date('" + value.toString() + "','yyyy-mm-dd')");
				}
			} else {
				sql.append(" = '' ");
			}
		} else if (ColumnType.BOOLEAN.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				sql.append(" = " + Boolean.valueOf(value.toString()) + "");
			} else {
				sql.append(" = '' ");
			}
		} else if (ColumnType.INTEGER.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				sql.append(" = " + Integer.valueOf(value.toString()) + "");
			} else {
				sql.append(" = '' ");
			}
		} else if (ColumnType.NUMBER.equals(type) || ColumnType.DECIMAL.equals(type)) {
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				sql.append(" = " + value.toString() + "");
			} else {
				sql.append(" = '' ");
			}
		} else if(ColumnType.TIMESTAMP.equals(type)){
			if (value != null && StringUtils.isNotBlank(value.toString())) {
				if("mysql".equals(dbType)){
					sql.append(" = date_format('" + value.toString() + "','%Y-%m-%d %H:%i:%s.%f')");
				} else {
					sql.append(" = to_timestamp('" + value.toString() + "','yyyy-mm-dd hh:mi:ss.ff')");
				}
			} else {
				sql.append(" = '' ");
			}
		} else {
			sql.append(" like '%" + value.toString() + "%'");
		}
		return sql.toString();
	}
	
//	/**
//	 * 数据回退
//	 * @param temp 补录模板
//	 * @param condition 其他条件
//	 * @param dataLoadInfoList 装载条件列表
//	 * @param objects <br>
//	 *            1.第一个参数，数据日期,按日期回退<br>
//	 *            2.第二个参数，机构代码,按机构回退<br>
//	 *            3.第三个参数，任务批次,按批次回退<br>
//	 */
//	@Transactional(readOnly = false)
//	@SuppressWarnings("unchecked")
//	public boolean rollBack(UdipTemple temp, Map<String, String> condition, List<UdipDataLoadInfo> dataLoadInfoList,
//			Object... objects) throws Exception {
//		Connection conn = null;
//		Statement statement = null;
//		try {
//
//			if (StringUtils.isNotBlank(temp.getDsId())) {// 数据
//				// 1.获取数据库连接
//				conn = rdbConnectionManagerBS.getConnection(temp.getDsId());
//				statement = conn.createStatement();
//				// 2.先删掉补录进来的数据
//				StringBuilder deleteSql = new StringBuilder();
//				StringBuilder conditionSql = new StringBuilder();
//				deleteSql.append("delete from ").append(temp.getTableName() + "  where 1=1 ");
//
//				// 第一个参数，数据日期
//				if (objects != null && objects.length > 0) {
//					conditionSql.append(" and ").append(UdipConstants.TAB_DATA_DATE).append(" ='").append(objects[0]).append("' ");
//				}
//				// 第二个参数，机构代码
//				if (objects != null && objects.length > 1 && StringUtils.isNotBlank(temp.getOrgColumn())) {
//					if (objects[1] instanceof List) {// 多值
//						conditionSql.append(" and ").append(temp.getOrgColumn()).append(" in(");
//						conditionSql.append(ArrayUtils.toDBString((List<String>) objects[1], false)).append(") ");
//					} else {
//						conditionSql.append(" and ").append(temp.getOrgColumn()).append(" ='").append(objects[1]).append("' ");
//					}
//				}
//				// 其他条件
//				initCondition(conditionSql, condition);
//
//				statement.executeUpdate(deleteSql.append(conditionSql).toString());
//
//				// 3.初始化一份下发的数据
//				return loadData(temp, condition, dataLoadInfoList, objects);
//			}
//		} catch (Exception e) {
//			conn.rollback();
//			throw new Exception(e);
//		} finally {
//			RdbConnectionManagerBS.closeStatement(statement);
//			RdbConnectionManagerBS.closeConnection(conn);
//		}
//		return false;
//	}
//
//	/**
//	 * 数据装载,只有设置了UdipDataLoadInfo的补录模板,才会有数据装载
//	 * @param temp 补录模板
//	 * @param condition 其他条件
//	 * @param udipDataLoadInfo 加载条件
//	 * @param objects <br>
//	 *            1.第一个参数，数据日期,按日期回退<br>
//	 *            2.第二个参数，机构代码,按机构回退<br>
//	 *            3.第三个参数，任务批次,按批次回退<br>
//	 */
//	@SuppressWarnings("unchecked")
//	public boolean loadData(UdipTemple temp, Map<String, String> condition, List<UdipDataLoadInfo> dataLoadInfoList,
//			Object... objects) throws Exception {
//		Connection conn = null;
//		PreparedStatement statement = null;
//		ResultSet rs = null;
//		try {
//
//			if (StringUtils.isNotBlank(temp.getDsId())) {// 数据
//				// 1.获取数据库连接
//				conn = rdbConnectionManagerBS.getConnection(temp.getDsId());
//
//				// 2.加载预装载的数据,接口表统一加上INPUT_TEMP_TABLE后缀
//				StringBuilder selectSql = new StringBuilder();
//
//				selectSql.append("select * from ").append(temp.getTableName()).append(INPUT_TEMP_TABLE).append(" where 1=1");
//				for (UdipDataLoadInfo info : dataLoadInfoList) {
//					if (info.getConditionVal().equalsIgnoreCase(UdipConstants.TAB_DATA_DATE)) {
//						selectSql.append(" and ").append("to_char(").append(info.getConditionCol()).append(",'yyyymmdd')").append("='").append(objects[0].toString().replaceAll("-", "")).append("'");
//					}
//				}
//
//				// 按机构取
//				if (objects != null && objects.length > 1 && StringUtils.isNotBlank(temp.getOrgColumn())) {
//					if (objects[1] instanceof List) {// 多值
//						selectSql.append(" and ").append(temp.getOrgColumn()).append(" in(");
//						selectSql.append(ArrayUtils.toDBString((List<String>) objects[1], false)).append(") ");
//					} else {
//						selectSql.append(" and ").append(temp.getOrgColumn()).append(" ='").append(objects[1]).append("' ");
//					}
//				}
//				initCondition(selectSql, condition);
//				statement = conn.prepareStatement(selectSql.toString());
//				rs = statement.executeQuery();
//
//				// 3.初始化一份下发的数据
//				StringBuilder colSql = new StringBuilder();
//				StringBuilder valSql = new StringBuilder();
//				colSql.append("insert into ").append(temp.getTableName() + "(");
//				valSql.append("values(");
//				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
//					String col = rs.getMetaData().getColumnName(i);
//					colSql.append(col).append(",");
//					valSql.append("?,");
//				}
//				// 添加固定列
//				if (objects != null && objects.length > 2) {// 任务批次
//					colSql.append(UdipConstants.TAB_DATA_CASE).append(",");
//					valSql.append("?,");
//				}
//				colSql.append(UdipConstants.TAB_DATA_DATE).append(",").append(UdipConstants.TAB_DATA_STATE).append(")");
//				valSql.append("?,").append("?)");
//
//				statement = conn.prepareStatement(colSql.append(valSql).toString());
//				while (rs.next()) {
//					int index = 1;
//					for (; index <= rs.getMetaData().getColumnCount(); index++) {
//						Object value = rs.getObject(index);
//						String type = rs.getMetaData().getColumnTypeName(index);
//						setValue(statement, type, value, index);
//					}
//					if (objects != null && objects.length > 2) {// 任务批次
//						statement.setString(index++, objects[2].toString());
//					}
//					statement.setString(index++, objects[0].toString());// 数据日期
//					statement.setString(index++, UdipConstants.TASK_STATE_DISPATCH);// 数据状态
//					statement.addBatch();
//				}
//
//				statement.executeBatch();
//				return true;
//			}
//		} catch (Exception e) {
//			throw new Exception(e);
//		} finally {
//			RdbConnectionManagerBS.closeResultSet(rs);
//			RdbConnectionManagerBS.closeStatement(statement);
//			RdbConnectionManagerBS.closeConnection(conn);
//		}
//		return false;
//	}

	private void deleteInput(RptInputLstTempleInfo temp,List<String>ids, Statement statement)throws Exception{

		String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(schema).append(".").append(temp.getTableEnName() + "  where datainput_id IN(");
		boolean isFirst=true;
		for (String id : ids) {// 以主键为条件
			if(isFirst)isFirst=false;
			else sql.append(",");
			sql.append("'").append(id).append("'");
		}
		sql.append(")");
		Log.info("执行SQL："+sql.toString());
		statement.execute(sql.toString());// 不支持批量操作,单条执行
	}
	
	public boolean createTempTable(String taskNodeInstanceId, 
	        List<Map<String, Object>> dataAdd, List<Map<String, Object>> dataUdp, 
	        RptInputLstTempleInfo temp,  Map<String, RptInputLstTempleField> colMap, 
	        Map<String, Map<String, Object>> liblist,Object... objects) throws Exception {

		Connection conn = null;
		// Statement stmt = null;
		PreparedStatement statementAdd = null;
//		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来着数据库表
				// 1.获取数据库连接
				conn = this.dataSourceBS.getConnection(temp.getDsId());
				// stmt = conn.createStatement();
				// 添加数据
				dataAdd.addAll(dataUdp);
				save(taskNodeInstanceId,conn, statementAdd, temp, colMap, dataAdd, liblist,objects);
				conn.commit();
				return true;
			}
//		} catch (Exception e) {
//			conn.rollback();
//			throw new Exception(e);
//		} finally {
//			this.dataSourceBS.releaseConnection(null, stmt, conn);
//		}
		return false;
	
	}
	
	public Boolean deleteAndAddDataWeihai(String taskNodeInstanceId,int num, List<Map<String, Object>> dataAdd, 
	        List<String> dataOld, List<Map<String, Object>> dataUdp, RptInputLstTempleInfo temp, 
	        List<String> keyList, Map<String, RptInputLstTempleField> colMap,
	        Map<String, Map<String, Object>> liblist,Object ... objects) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement statementAdd = null;
		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来着数据库表
				// 1.获取数据库连接
				conn = this.dataSourceBS.getConnection(temp.getDsId());
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				if (CollectionUtils.isNotEmpty(dataOld)) {
					saveDeleteHistory(temp,dataOld,  stmt,conn,colMap);
					deleteInput(temp,dataOld,  stmt);
					/*
					if ("1".equals(temp.getAllowInputHist())) {
						delete(conn, stmt, temp, colMap, keyList, dataOld, null, objects[1], objects[5], objects[3], null);
					} else {
						delete(conn, stmt, temp, colMap, keyList, dataOld, objects[0], objects[1], objects[5], objects[3], objects[4]);
					}
					*/
				}
				// 添加数据
				if ("1".equals(temp.getAllowInputHist())) {
					update(conn, statementAdd, temp, colMap, dataUdp, keyList,liblist, objects[0], objects[1], objects[2], objects[3], null);
				} else {
					update(conn, statementAdd, temp, colMap, dataUdp, keyList,liblist, objects);
				}
				save(taskNodeInstanceId,conn, statementAdd, temp, colMap, dataAdd, liblist,objects);
				
				conn.commit();
				return true;
			}
		} catch (Exception e) {
			conn.rollback();
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(null, stmt, conn);
		}
		return false;
	}
	
	public boolean updateWeihaiValidateData(String taskFlowNodeId,int num, List<Map<String, Object>> dataAdd, 
	        List<String> dataOld, List<Map<String, Object>> dataUdp, RptInputLstTempleInfo temp, List<String> keyList,
	        Map<String, RptInputLstTempleField> colMap, 
	        Map<String, Map<String, Object>> liblist,Object... objects) throws Exception{
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement statementAdd = null;
		temp.setTableEnName(temp.getTableEnName()+"_VALIDATE");
//		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来着数据库表
				// 1.获取数据库连接
				conn = this.dataSourceBS.getConnection(temp.getDsId());
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				if (CollectionUtils.isNotEmpty(dataOld)) {
					if ("1".equals(temp.getAllowInputHist())) {
						deleteInput(temp,dataOld,  stmt);
					} else {
						deleteInput(temp,dataOld,  stmt);
					}
				}
				// 添加数据
				if ("1".equals(temp.getAllowInputHist())) {
					update(conn, statementAdd, temp, colMap, dataUdp, keyList,liblist, objects[0], objects[1], objects[2], objects[3], null);
				} else {
					update(false,conn, statementAdd, temp, colMap, dataUdp, keyList,liblist, objects);
				}
				save(taskFlowNodeId,conn, statementAdd, temp, colMap, dataAdd,liblist, objects);
				conn.commit();
				return true;
			}
//		} catch (Exception e) {
//			conn.rollback();
//			throw new Exception(e);
//		} finally {
//			this.dataSourceBS.releaseConnection(null, stmt, conn);
//		}
		return false;
	}
	
	public void deleteValidateTable(RptInputLstTempleInfo temp,List<String> ids) throws Exception{
		
		Connection conn = null;
		Statement stmt = null;
		try{

			conn = this.dataSourceBS.getConnection(temp.getDsId());
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
			//先删除记录
			StringBuilder sql = new StringBuilder(1000);
			String tableNm = temp.getTableEnName();
			if(!tableNm.endsWith("VALIDATE"))
				tableNm = tableNm +"_VALIDATE";
			sql.append("delete from ").append(schema).append(".").append(tableNm);
			sql.append(" WHERE FLOW_NODE_ID IN  (");
			boolean isFirst = true;
			for (String id : ids) {// 以主键为条件
				if(isFirst) isFirst=false;
				else sql.append(",");
				sql.append("'").append(id).append("'");
			}
			sql.append(" )  ");
			Log.info("执行SQL："+sql.toString());
			stmt.execute(sql.toString());// 不支持批量操作,单条执行
			conn.commit();
			
		} catch (Exception e) {
			conn.rollback();
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(null, stmt, conn);
		}
	}
	
	public void saveCurrentNodeData(RptInputLstTempleInfo temp,List<String> ids)throws Exception{
		
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			
			String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
			//先删除记录
			StringBuilder sql = new StringBuilder(1000);
			sql.append("delete from ").append(schema).append(".").append(temp.getTableEnName()+"_VALIDATE ");
			sql.append(" WHERE FLOW_NODE_ID IN  (");
			boolean isFirst = true;
			for (String id : ids) {// 以主键为条件
				if(isFirst) isFirst=false;
				else sql.append(",");
				sql.append("'").append(id).append("'");
			}
			sql.append(")");
			sql.append(" AND SYS_OPERATOR='").append(userObj.getLoginName()).append("'");
			Log.info("执行SQL："+sql.toString());
			stmt.execute(sql.toString());// 不支持批量操作,单条执行
			conn.commit();
			//将当前用户操作的所有数据进行更新
			sql = new StringBuilder(1000);
			sql.append(" INSERT INTO ").append(schema).append(".").append(temp.getTableEnName()+"_VALIDATE ");
			sql.append(" SELECT *FROM ").append(schema).append(".").append(temp.getTableEnName() );
			sql.append(" WHERE FLOW_NODE_ID IN  (");
			isFirst = true;
			for (String id : ids) {// 以主键为条件
				if(isFirst) isFirst=false;
				else sql.append(",");
				sql.append("'").append(id).append("'");
			}
			sql.append(")");
			Log.info("执行SQL："+sql.toString());
			stmt.execute(sql.toString());// 不支持批量操作,单条执行
			conn.commit();
			
		} catch (Exception e) {
			conn.rollback();
			// java.sql.SQLSyntaxErrorException: ORA-00942: 表或视图不存在
			//throw new Exception(e); //不再包装成java异常，方便页面提示数据库操作异常 20190902
			throw e;
		} finally {
			this.dataSourceBS.releaseConnection(null, stmt, conn);
		}
	}
	
	private void saveDeleteHistory(RptInputLstTempleInfo temp,List<String>ids, Statement statement,Connection conn,Map<String, RptInputLstTempleField>colMap) throws SQLException{

		String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		
		StringBuilder colBuff = new StringBuilder();
		Set<String>set = colMap.keySet();
		boolean isFirst=true;
		for(String s:set){
			if(s.equalsIgnoreCase("SYS_ORDER_NO"))
				continue;
			if(isFirst)
				isFirst=false;
			else
				colBuff.append(",");
			colBuff.append(colMap.get(s).getFieldEnName());
		}
		String sysOperDate = DateUtils.getYYYY_MM_DD_HH_mm_ss();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ").append(schema).append(".").append(temp.getTableEnName()+"_DELETE ");
		sql.append("(FLOW_NODE_ID,COMMENTS,SYS_DATA_DATE,SYS_DATA_CASE,SYS_OPERATOR,SYS_OPER_ORG,SYS_OPER_DATE,SYS_DATA_STATE,SYS_ORDER_NO,DATAINPUT_ID,");
		sql.append(colBuff);
		sql.append(")");
		sql.append(" SELECT FLOW_NODE_ID,COMMENTS,SYS_DATA_DATE,SYS_DATA_CASE,'").append(userObj.getLoginName()).append("','").append(userObj.getOrgNo()).append("','").append(sysOperDate).append("','delete',SYS_ORDER_NO,DATAINPUT_ID,");
		sql.append(colBuff);
		sql.append(" FROM ").append(schema).append(".").append(temp.getTableEnName()).append(" WHERE datainput_id IN (");
		isFirst = true;
		for (String id : ids) {// 以主键为条件
			if(isFirst) isFirst=false;
			else sql.append(",");
			sql.append("'").append(id).append("'");
			
		}
		sql.append(")");
		Log.info("执行SQL："+sql.toString());
		statement.execute(sql.toString());// 不支持批量操作,单条执行

		sql = new StringBuilder(1000);
		sql.append(" UPDATE ").append(schema).append(".").append(temp.getTableEnName()).append("_DELETE ");
		sql.append(" SET SYS_DATA_STATE =? ,sys_oper_date= ? ");
		sql.append(" WHERE FLOW_NODE_ID IN  (");
		isFirst = true;
		for (String id : ids) {// 以主键为条件
			if(isFirst) isFirst=false;
			else sql.append(",");
			sql.append("'").append(id).append("'");
		}
		sql.append(")");
		Log.info("执行SQL："+sql.toString());
		PreparedStatement statementAdd = conn.prepareStatement(sql.toString());
		statementAdd.setString(1, "delete");
		statementAdd.setString(2, DateUtils.getYYYY_MM_DD_HH_mm_ss());
		statementAdd.execute();
		conn.commit();
		try{
			JdbcUtils.closeStatement(statementAdd);
		}catch(Exception ex){
		}
	}
	

	
	public List<String> checkDateOfUpload( List<Map<String, Object>> dataAdd,RptInputLstTempleInfo temp, List<String> keyList,String flowNodeId) throws Exception{
		Connection conn = null;
		Statement st = null;
		try {
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			String sysOperator = BioneSecurityUtils.getCurrentUserInfo().getLoginName();
			String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
			StringBuilder buff;
			buff = new StringBuilder(1000);
			buff.append(" SELECT ");
			boolean isFirst = true;
			for(String key:keyList){
				if(key.equals("SYS_ORDER_NO"))
					continue;
				if(isFirst)
					isFirst = false;
				else
					buff.append(" ||','|| ");
				buff.append(key);
			}
			buff.append(" AS INFO FROM  ").append(schema).append(".").append(temp.getTableEnName() ).append(" WHERE ( " );
			boolean f = true;
			for(Map<String,Object>m:dataAdd){
				if(f)
					f=false;
				else
					buff.append(" OR ");
				
				buff.append(" ( ");
				isFirst = true;
				for(String key:keyList){
					if(key.equals("SYS_ORDER_NO"))
						continue;
					if(isFirst)
						isFirst = false;
					else
						buff.append(" AND ");
					buff.append(key).append(" = ").append(" '").append(m.get(key)).append("' ");
				}
				buff.append(" ) ");
			}
			buff.append(" ) ");
			buff.append(" AND SYS_OPERATOR != '").append(sysOperator).append("' AND ").append(" FLOW_NODE_ID != '").append(flowNodeId).append("'");
			st = conn.createStatement();
			Log.info("执行SQL："+buff.toString());
			ResultSet  rs = st.executeQuery(buff.toString());
			List<String>infos=Lists.newArrayList();
			while(rs.next()){
				infos.add(rs.getString("INFO")) ;
			}
			return 	infos;
		} catch (Exception e) {
			conn.rollback();
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(null, st, conn);
		}
	}
	
	/**
	 * 保存数据
	 * @param num
	 * @param dataAdd
	 * @param dataOld
	 * @param dataUdp
	 * @param temp
	 * @param keyList
	 * @param colMap
	 * @param objects 1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，补录日期<br>
	 *            4.第四个参数，操作人员<br>
	 *            5.第五个参数，任务批次<br>
	 *            6.第六个参数，机构代码<br>
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> deleteAndAddData(String taskFlowNodeId,int num, List<Map<String, Object>> dataAdd,
	        List<Map<String, Object>> dataOld, List<Map<String, Object>> dataUdp, 
	        RptInputLstTempleInfo temp, List<String> keyList, Map<String, RptInputLstTempleField> colMap
	        ,Map<String, Map<String, Object>> liblist,Object... objects) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement statementAdd = null;
		Map<String, Object> map = new HashMap<String, Object>();
		int deleteTotal = 0;
		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来着数据库表
				// 1.获取数据库连接
				conn = this.dataSourceBS.getConnection(temp.getDsId());
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				if (CollectionUtils.isNotEmpty(dataOld)) {
					if ("1".equals(temp.getAllowInputHist())) {
						deleteTotal = delete(conn, stmt, temp, colMap, keyList, dataOld, null, objects[1], objects[5], objects[3], null);
					} else {
						deleteTotal = delete(conn, stmt, temp, colMap, keyList, dataOld, objects[0], objects[1], objects[5], objects[3], objects[4]);
					}
				}
				// 添加数据
				if ("1".equals(temp.getAllowInputHist())) {
					update(conn, statementAdd, temp, colMap, dataUdp, keyList,liblist, objects[0], objects[1], objects[2], objects[3], null);
				} else {
					update(conn, statementAdd, temp, colMap, dataUdp, keyList,liblist, objects);
				}
				deleteDateByPrimaryKey(conn,dataAdd,temp,keyList,taskFlowNodeId);
				save(taskFlowNodeId,conn, statementAdd, temp, colMap, dataAdd,liblist, objects);
				//根据主键排序
//				reOrderInput( taskFlowNodeId, conn, temp, keyList);
				conn.commit();
				map.put("chack", true);
				map.put("deleteTotal", deleteTotal);
				return map;
			}
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
			map.put("deleteTotal", deleteTotal);
			return map;
		} finally {
			this.dataSourceBS.releaseConnection(null, stmt, conn);
			this.dataSourceBS.releaseConnection(null, statementAdd, null);
		}
		map.put("chack", false);
		return map;
	}
		
	private void deleteDateByPrimaryKey(Connection conn ,List<Map<String, Object>> dataAdd,
			RptInputLstTempleInfo temp, List<String> keyList,String taskFlowNodeId) throws Exception{
		if(keyList==null||keyList.isEmpty())
			return ;
		String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		StringBuilder buff;
		
		Statement st = null;
		int i = 0;
		try {
			st = conn.createStatement();
			for(Map<String,Object>m:dataAdd){
				buff = new StringBuilder();
				buff.append(" DELETE FROM  ").append(schema).append(".").append(temp.getTableEnName() ).append(" WHERE 1=1 " );
				for(String key:keyList){
					if(key.equals("SYS_ORDER_NO"))
						continue;
					
					buff.append(" AND ");
					buff.append(key).append("=").append("'").append(m.get(key)).append("'");
				}
				buff.append(" AND FLOW_NODE_ID = '").append(taskFlowNodeId).append("'");
				Log.info("执行SQL："+buff.toString());
				st.addBatch(buff.toString());
				if(i++ % 500 == 0){
					st.executeBatch();
				}
			}
			st.executeBatch();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}finally{
			if(null != st){
				try {
					st.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 根据object信息查找该模版导入批次
	 * @param temp
	 * @param condition
	 * @param objects <br>
	 *            1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，机构代码<br>
	 *            4.第四个参数，操作人员<br>
	 *            5.第五个参数，任务批次<br>
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> findDataFileRecords(RptInputLstTempleInfo temp, Map<String, String> condition, Object... objects) throws Exception {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		List<Map<String, Object>> data = Lists.newArrayList();
		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来自数据库表
				// 1.获取数据库连接
				conn = dataSourceBS.getConnection(temp.getDsId());
				statement = conn.createStatement();
				// 2.组装查询SQL
				StringBuilder sql = new StringBuilder();
				sql.append("select " + UdipConstants.TAB_OPER_DATE + " from " + temp.getTableEnName() + "  where 1=1 ");// 组成返回字段
				initCondition(temp, sql, condition, objects);
				sql.append(" group by " + UdipConstants.TAB_OPER_DATE);
				Log.info("执行SQL："+sql.toString());
				// 3.返回查询结果
				rs = statement.executeQuery(sql.toString());
				while (rs.next()) {
					Map<String, Object> map = Maps.newHashMap();
					map.put(UdipConstants.TAB_OPER_DATE, rs.getObject(UdipConstants.TAB_OPER_DATE));
					data.add(map);
				}
				return data;
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, statement, conn);
		}
	}

	/**
	 * 根据时间删除数据的导入批次
	 * @param temp
	 * @param dataTime
	 * @param condition
	 * @param objects 1.第一个参数，数据日期<br>
	 *            2.第二个参数，数据状态<br>
	 *            3.第三个参数，机构代码<br>
	 *            4.第四个参数，操作人员<br>
	 *            5.第五个参数，任务批次<br>
	 * @return
	 * @throws Exception
	 */
	public List<String> deleteDataFileRecords(RptInputLstTempleInfo temp, String dataTime, Map<String, String> condition, Object... objects) throws Exception {
		Connection conn = null;
		ResultSet rs = null;
		List<String> data = Lists.newArrayList();
		conn = this.dataSourceBS.getConnection(temp.getDsId());
		Statement stmt = conn.createStatement();
		boolean supportsBatchUpdates = conn.getMetaData().supportsBatchUpdates();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from ").append(temp.getTableEnName() + "  where "+UdipConstants.TAB_OPER_DATE+" = '" + dataTime + "' ");
			initCondition(temp, sql, null, objects);// 其他条件
			Log.info("执行SQL："+sql.toString());
			if (supportsBatchUpdates) {
				stmt.addBatch(sql.toString());
				stmt.executeBatch();// 批量删除
			} else {
				stmt.execute(sql.toString());// 不支持批量操作,单条执行
			}
		} catch (Exception e) {
			conn.rollback();
			data.add(e.getMessage());
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return data;
	}

//	/**
//	 * 根据CaseId删除数据
//	 * @param temp
//	 * @param objects 1.第一个参数，数据日期<br>
//	 *            2.第二个参数，数据状态<br>
//	 *            3.第三个参数，机构代码<br>
//	 *            4.第四个参数，操作人员<br>
//	 *            5.第五个参数，任务批次<br>
//	 * @return
//	 * @throws Exception
//	 */
//	public Boolean deleteByCaseId(UdipTemple temp, Object... objects) throws Exception {
//		Connection conn = null;
//		Statement statement = null;
//		try {
//
//			if (StringUtils.isNotBlank(temp.getDsId())) {// 来着数据库表
//				// 1.获取数据库连接
//				conn = rdbConnectionManagerBS.getConnection(temp.getDsId());
//				statement = conn.createStatement();
//				delete(conn, statement, temp, objects);
//				return true;// 3.返回查询结果
//			}
//		} catch (Exception e) {
//			conn.rollback();
//			throw new Exception(e);
//		} finally {
//			RdbConnectionManagerBS.closeStatement(statement);
//			RdbConnectionManagerBS.closeConnection(conn);
//		}
//		return false;
//	}
//
//	public RdbConnectionManagerBS getRdbConnectionManagerBS() {
//		return rdbConnectionManagerBS;
//	}
//
//	public void setRdbConnectionManagerBS(RdbConnectionManagerBS rdbConnectionManagerBS) {
//		this.rdbConnectionManagerBS = rdbConnectionManagerBS;
//	}
//
//	/**
//	 * 根据补录模板的查询条件获取模板数据
//	 * @param temp
//	 * @param keyCol
//	 * @param condition
//	 * @param colList
//	 * @return
//	 * @throws Exception
//	 */
//	public List<Map<String, Object>> getData(UdipTemple temp, List<String> keyCol,
//			Map<String, Map<String, String>> condition, List<String> colList) throws Exception {
//
//		Connection conn = null;
//		Statement statement = null;
//		ResultSet rs = null;
//		List<Map<String, Object>> data = Lists.newArrayList();
//		try {
//
//			if (StringUtils.isNotBlank(temp.getDsId())) {// 来自数据库表
//				// 1.获取数据库连接
//				conn = rdbConnectionManagerBS.getConnection(temp.getDsId());
//				statement = conn.createStatement();
//				// 2.组装查询SQL
//				StringBuilder sql = new StringBuilder();
//				sql.append("select ").append(ArrayUtils.toString(colList)).append(" from " + temp.getTableName() + "  where 1=1 ");// 组成返回字段
//				DataUtils.initConditionBefore(sql, condition,"");
//
//				if (keyCol != null && !keyCol.isEmpty()) {
//					sql.append(" order by ").append(ArrayUtils.toString(keyCol));
//				}
//				// 3.返回查询结果
//				rs = statement.executeQuery(sql.toString());
//				while (rs.next()) {
//					Map<String, Object> map = Maps.newHashMap();
//					for (String column : colList) {
//						map.put(column, rs.getObject(column));
//					}
//					data.add(map);
//				}
//				return data;
//			}
//			return data;
//		} catch (Exception e) {
//			throw new Exception(e);
//		} finally {
//			RdbConnectionManagerBS.closeResultSet(rs);
//			RdbConnectionManagerBS.closeStatement(statement);
//			RdbConnectionManagerBS.closeConnection(conn);
//		}
//	}
	
	
	/**
	 * 获取所有主键唯一性约束的
	 * @param temp 模板
	 * @param colMap 所有列对象
	 * @param keyList 所有唯一性约束对象
	 * @param colList 所有唯一性约束的列
	 * @return
	 * @throws Exception
	 */
	public String getKeyData(RptInputLstTempleInfo temp, Map<String, RptInputLstTempleField> colMap,
			List<RptInputLstTempleConst> keyList, Collection<String> colList,
			String taskFlowNodeId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, Map<String, Map<String, Object>>> keyDataMap = Maps.newHashMap();
		String cacheKey = RandomUtils.uuid2();
		EhcacheUtils.put(cacheKey, cacheKey, keyDataMap);
		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来自数据库表
				String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
				// 1.获取数据库连接
				conn = this.dataSourceBS.getConnection(temp.getDsId());
				stmt = conn.createStatement();
				// 2.组装查询SQL
				StringBuilder sql = new StringBuilder();
				sql.append("select ").append(ArrayUtils.toString(colList));
			    sql.append(" from " +schema+"."+ temp.getTableEnName() + "  where 1=1 ");// 组成返回字段
			    sql.append(" and FLOW_NODE_ID ='" + taskFlowNodeId + "' ");
			    Log.info("执行SQL："+sql.toString());
				// 3.返回查询结果
				rs = stmt.executeQuery(sql.toString());

				Map<String, String[]> keysMap = Maps.newHashMap();
				for (RptInputLstTempleConst utp : keyList) {
					keyDataMap.put(utp.getKeyId(), new HashMap<String, Map<String, Object>>());
					keysMap.put(utp.getKeyId(), utp.getKeyColumn().split(";"));
				}
				while (rs.next()) {
					for (String keyId : keysMap.keySet()) {
						Map<String, Object> km = Maps.newHashMap();
						String keys[] = keysMap.get(keyId);
					
						String k = "";
						for (String key : keys) {
							Object value = rs.getObject(key);
							if (value == null)
								value = "";
							km.put(key, value);
							k += value.toString() + DataFormatValidate.keysplit;
						}
						if (k.length() > 0) {
							k = k.substring(0, k.length() - 1);
						}
						keyDataMap.get(keyId).put(k, km);
					}
				}
			}
			return cacheKey;
		} catch (Exception e) {
            e.printStackTrace();
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
	}
	

	/**
	 * 获取所有主键唯一性约束的
	 * @param temp 模板
	 * @param colMap 所有列对象
	 * @param keyList 所有唯一性约束对象
	 * @param colList 所有唯一性约束的列
	 * @return
	 * @throws Exception
	 */
	public String getKeyDataweihai(RptInputLstTempleInfo temp, Map<String, RptInputLstTempleField> colMap, List<RptInputLstTempleConst> keyList, Collection<String> colList) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, Map<String, Map<String, Object>>> keyDataMap = Maps.newHashMap();
		String cacheKey = RandomUtils.uuid2();
		EhcacheUtils.put(cacheKey, cacheKey, keyDataMap);
		try {
			if (StringUtils.isNotBlank(temp.getDsId())) {// 来自数据库表
				String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
				// 1.获取数据库连接
				conn = this.dataSourceBS.getConnection(temp.getDsId());
				stmt = conn.createStatement();
				// 2.组装查询SQL
				StringBuilder sql = new StringBuilder();
				sql.append("select datainput_id,").append(ArrayUtils.toString(colList)).append(" from " +schema+"."+ temp.getTableEnName() + "  where 1=1 ");// 组成返回字段
				Log.info("执行SQL："+sql.toString());
				// 3.返回查询结果
				rs = stmt.executeQuery(sql.toString());

				Map<String, String[]> keysMap = Maps.newHashMap();
				for (RptInputLstTempleConst utp : keyList) {
					//keyDataMap.put(utp.getKeyId(), new HashMap<String, Map<String, Object>>());
					keysMap.put(utp.getKeyId(), utp.getKeyColumn().split(";"));
				}
				while (rs.next()) {
					String datainputId = rs.getString("datainput_id");
					keyDataMap.put(datainputId, new HashMap<String, Map<String, Object>>());
					for (String keyId : keysMap.keySet()) {
						Map<String, Object> km = Maps.newHashMap();
						String keys[] = keysMap.get(keyId);
						String k = "";
						for (String key : keys) {
							Object value = rs.getObject(key);
							if (value == null)
								value = "";
							km.put(key, value);
							k += value.toString() + DataFormatValidate.keysplit;
						}
						if (k.length() > 0) {
							k = k.substring(0, k.length() - 1);
						}
						keyDataMap.get(datainputId).put(k, km);
					}
				}
			}
			return cacheKey;
		} catch (Exception e) {
            e.printStackTrace();
			throw new Exception(e);
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
	}
	
	/**
	 * 根据规则子项获取指定条数
	 * 
	 * @param temp
	 *            模版
	 * @param info
	 *            规则
	 * @param obj
	 *            可变参数<br>
	 *            第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 * @return 所有列的数据
	 * @throws Exception
	 */
	public List<String> getTableSpace(String dsId,String tableSpaceNm)  {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<String>list=Lists.newArrayList();
		try {
			conn = this.dataSourceBS.getConnection(dsId);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(getTbsQuerySqlByDBType(conn,tableSpaceNm));
			while (rs.next()) {
				list.add(rs.getString("tbspace"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, conn);
		}
		return list;
	}
	
	private String getTbsQuerySqlByDBType(Connection conn,String tableSpaceNm){

		DBMS dbms = DBMS.valueOf(DialectUtils.getDataBaseType(conn, false).toUpperCase());
		StringBuilder buff = new StringBuilder();
		if(dbms==DBMS.ORACLE){
			buff.append("select tablespace_name as tbspace from user_tablespaces where contents='PERMANENT'");
			if(StringUtils.isNotEmpty(tableSpaceNm)){
				buff.append(" and tablespace_name like  '%").append(tableSpaceNm.toUpperCase()).append("%' ");
			}
		}else if(dbms==DBMS.DB2){
			buff.append( "select tbspace from syscat.TABLESPACES ");
			if(StringUtils.isNotEmpty(tableSpaceNm)){
				buff.append(" WHERE tbspace like  '%").append(tableSpaceNm.toUpperCase()).append("%' ");
			}
		}
		else return null;
		return buff.toString();
	}
}