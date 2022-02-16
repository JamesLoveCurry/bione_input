
package com.yusys.biapp.input.rule.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.dict.utils.ColumnType;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.service.DataRuleBS;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.entity.RptInputLstTempleRule;
import com.yusys.biapp.input.utils.RegexRules;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.FormatUtils;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * 数据格式校验，入库前校验<br>
 * 1.界面录入数据，点击保存时进行的校验<br>
 * 2.通过模板导入数据时进行校验<br>
 * </pre>
 * @author caijiufa
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 * 
 * </pre>
 */
@Component(value = "dataFormatValidate")
public class DataFormatValidate {
	private final Log logger = LogFactory.getLog(DataFormatValidate.class);
	public static int batchSize = 200;
	public static int maxSize = 10000000;
	public final static String keysplit = "|";

	@Autowired
	private DataUtils dataUtils;
	@Autowired
	private DataSourceBS dataSourceBS;
	@Autowired
	private DataRuleBS dataRuleBS;

	private RptInputListDataRuleInfo udipValidateLog = new RptInputListDataRuleInfo();

	/**
	 * 批量数据校验
	 * @param startNum 记录序号
	 * @param udipTaskCaseInfo 任务实例
	 * @param dataList 要校验的数据 数据map中必须包含新增或删除标记
	 * @param temp 补录模板
	 * @param colMap 列名：列对象
	 * @param keyList 主键约束列表
	 * @param ruleList 该模板的校验规则
	 * @param libList 使用数据字典的字段：数据字典数据
	 * @param flag 新增修改标志<br>
	 *            0:新增；1：修改
	 * @return 返回校验结果
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RptInputLstValidateLog> validate(int startNum, RptTskIns udipTaskCaseInfo,
			List<Map<String, Object>> dataList, RptInputLstTempleInfo temp, 
			Map<String, RptInputLstTempleField> colMap, 
			List<RptInputLstTempleConst> keyList, List<RptInputLstTempleRule> ruleList, 
			Map<String, Map<String, Object>> libList, String flag
			,String taskFlowNodeId) throws Exception {
		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		Map<String, List<String>> keyMap = Maps.newHashMap();// 保存所有主键值
		Collection<String> keyColumns = Lists.newArrayList();
		Collection<String> columns = new HashSet<String>();
		for (RptInputLstTempleConst utp : keyList) {
			keyMap.put(utp.getKeyId(), new ArrayList<String>());
			for (String key : utp.getKeyColumn().split(";")) {
				if (utp.getKeyType().equals(UdipConstants.TAB_PRIMARY)) {
					keyColumns.add(key.trim());
				}
				columns.add(key.trim());
			}
		}

        //如果没有设置物理主键，默认将数据日期、补录机构，任务实例设置为联合主键
        if (columns.size() == 0) {
            columns.add(UdipConstants.TAB_DATA_DATE);
            columns.add(UdipConstants.TAB_OPER_ORG);
            columns.add(UdipConstants.TAB_DATA_CASE);
        }

		//List<CommonTreeNode> orgList = authObjectUtils.getOrgList(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());// 所有用户机构
		Connection conn = this.dataSourceBS.getConnection(temp.getDsId());
		int count = dataUtils.getDataCount(temp, null, null);// 获取补录表的总数据量
		Map<String, Map<String, Map<String, Object>>> keyDataMap = null;
		if (count < maxSize) {// 数据量小于1千万的时候,直接取所有唯一性约束数据出来放内存,否则一条条到数据库去校验
			String cacheKey = dataUtils.getKeyData(temp, colMap, keyList, columns,taskFlowNodeId);
			keyDataMap = (Map<String, Map<String, Map<String, Object>>>) EhcacheUtils.get(cacheKey, cacheKey);
		}
		
		/**获取当前用户的本级以及下级机构**/
		String orgCode = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		List<String> childrens = this.dataRuleBS.findOrgChildrenInf(orgCode);
		//TreeUtils.childrensCode(orgList, orgCode, childrens);
		//childrens.add(orgCode);//当前用户的本级以及下级机构.
		try {
			for (Map<String, Object> data : dataList) {// 循环每条记录
				startNum++;
				Map<String, Object> m = Maps.newHashMap();
				for (String key : data.keySet()) {
					m.put(key, data.get(key));
				}
				// for(int i = 0 ;i <resultList.size();i++ ){
				// 	RptInputLstValidateLog log = resultList.get(i);
				// }
				/* 1.数据合法性校验 */
				List<RptInputLstValidateLog> result = validate(startNum , m, temp, colMap, ruleList, libList);
				for(String key : m.keySet()){
					data.put(key, m.get(key));
				}
				
				// for(int i = 0 ;i <result.size();i++ ){
				// 	RptInputLstValidateLog log = result.get(i);
				// }
				if (!result.isEmpty()) {// 数据合法校验有错,就不进行唯一性检
					resultList.addAll(resultList.size(), result);
				}
				/* 2.检查数据唯一性，包括主键和唯一性约束 */
				List<RptInputLstValidateLog> keyResult = primaryCheck(conn, keyDataMap, startNum , udipTaskCaseInfo, m, temp, colMap, keyMap, keyList, keyColumns, flag);
				// for(int i = 0 ;i <keyResult.size();i++ ){
				// 	RptInputLstValidateLog log = keyResult.get(i);
				// }
				if (!keyResult.isEmpty()) {
					resultList.addAll(resultList.size(), keyResult);
				}
				/* 3.机构字段校验 */
				List<RptInputLstValidateLog> orgResult = orgCodeCheck(startNum , m, temp, childrens, orgCode, colMap);				
				// for(int i = 0 ;i <orgResult.size();i++ ){
				// 	RptInputLstValidateLog log = orgResult.get(i);
				// }
				if (!orgResult.isEmpty()) {
					resultList.addAll(resultList.size(), orgResult);
				}			
				// for(int i = 0 ;i <resultList.size();i++ ){
				// 	RptInputLstValidateLog log = resultList.get(i);
				// }
			}
		} catch (Exception e) {
            e.printStackTrace();
			throw e;
		} finally {
			this.dataSourceBS.releaseConnection(null, null, conn);
		}
		return resultList;
	}

	/**
	 * 批量数据校验
	 * @param startNum 记录序号
	 * @param udipTaskCaseInfo 任务实例
	 * @param dataList 要校验的数据 数据map中必须包含新增或删除标记
	 * @param temp 补录模板
	 * @param colMap 列名：列对象
	 * @param keyList 主键约束列表
	 * @param ruleList 该模板的校验规则
	 * @param libList 使用数据字典的字段：数据字典数据
	 * @param flag 新增修改标志<br>
	 *            0:新增；1：修改
	 * @return 返回校验结果
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RptInputLstValidateLog> validateweihai(List<Map<String, Object>> dataList, String flag , Map<String, Object> getMap) throws Exception {
		logger.debug("validateweihai-----"+flag+"--------------------start:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		RptTskIns udipTaskCaseInfo = (RptTskIns) getMap.get("taskCase");
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp"); 
		//待提取到上一层
		Connection conn = (Connection)getMap.get("conn");
		int count = (Integer)getMap.get("count");// 获取补录表的总数据量
		
		/**获取当前用户的本级以及下级机构**/
		String orgCode = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		List<String> childrens = (List<String>)getMap.get("childrens");
		
		//待提取到上一层
		
		List<RptInputLstTempleConst> keyList = (List<RptInputLstTempleConst>) getMap.get("keyList");
		Map<String, RptInputLstTempleField> colMap = (Map<String, RptInputLstTempleField>) getMap.get("colMap");
		List<RptInputLstTempleRule> ruleList = (List<RptInputLstTempleRule>) getMap.get("ruleList");
		Map<String, Map<String, Object>> libList = (Map<String, Map<String, Object>>) getMap.get("libList");
		
		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		Map<String, List<String>> keyMap = Maps.newHashMap();// 保存所有主键值
		Collection<String> keyColumns = Lists.newArrayList();
		Collection<String> columns = new HashSet<String>();
		Map<String, Map<String, Map<String, Object>>> keyDataMap = null;
		
		for (RptInputLstTempleConst utp : keyList) {
			keyMap.put(utp.getKeyId(), new ArrayList<String>());
			for (String key : utp.getKeyColumn().split(";")) {
				if (utp.getKeyType().equals(UdipConstants.TAB_PRIMARY)) {
					keyColumns.add(key.trim());
				}
				columns.add(key.trim());
			}
		}

        //如果没有设置物理主键，默认将数据日期、补录机构，任务实例设置为联合主键
        if (columns.size() == 0) {
            columns.add(UdipConstants.TAB_DATA_DATE);
            columns.add(UdipConstants.TAB_OPER_ORG);
            columns.add(UdipConstants.TAB_DATA_CASE);
        }

		if (count < maxSize) {// 数据量小于1千万的时候,直接取所有唯一性约束数据出来放内存,否则一条条到数据库去校验
			String cacheKey = dataUtils.getKeyDataweihai(temp, colMap, keyList, columns);
			keyDataMap = (Map<String, Map<String, Map<String, Object>>>) EhcacheUtils.get(cacheKey, cacheKey);
		}
		
		try {
			logger.debug("validateweihai-----"+flag+"---------------------for-start:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
			for (Map<String, Object> data : dataList) {// 循环每条记录
				Map<String, Object> m = Maps.newHashMap();
				for (String key : data.keySet()) {
					m.put(key, data.get(key));
				}
				int lineNum = -1;
				Object noobj = m.get("SYS_ORDER_NO");
				if(noobj instanceof String){
					lineNum = Integer.parseInt((String)noobj);
				}else if(noobj instanceof Long){
					lineNum = ((Long)noobj).intValue();
				}else if(noobj instanceof Integer){
					lineNum = ((Integer)noobj).intValue();
				}
				/* 1.数据合法性校验 */
				List<RptInputLstValidateLog> result = validate(lineNum, m, temp, colMap, ruleList, libList);

				if (!result.isEmpty()) {// 数据合法校验有错,就不进行唯一性检
					resultList.addAll(resultList.size(), result);
				}
				/* 2.检查数据唯一性，包括主键和唯一性约束 */
				List<RptInputLstValidateLog> keyResult = primaryCheckweihai(conn, keyDataMap,lineNum, udipTaskCaseInfo, m, temp, colMap, keyMap, keyList, keyColumns, flag);

				if (!keyResult.isEmpty()) {
					resultList.addAll(resultList.size(), keyResult);
				}
				/* 3.机构字段校验 */
				List<RptInputLstValidateLog> orgResult = orgCodeCheck(lineNum, m, temp, childrens, orgCode, colMap);

				if (!orgResult.isEmpty()) {
					resultList.addAll(resultList.size(), orgResult);
				}
			}
			logger.debug("validateweihai-----"+flag+"---------------------for-end:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		} catch (Exception e) {
            e.printStackTrace();
			throw e;
		} 
		logger.debug("validateweihai-----"+flag+"----------------------end:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		return resultList;
	}
	/**
	 * 校验单条数据
	 * @param num 记录序号
	 * @param data 要校验的数据
	 * @param temp 补录模板
	 * @param colMap 列名：列对象
	 * @param ruleList 该模板的校验规则
	 * @param libList 使用数据字典的字段：数据字典数据
	 * @return 返回校验结果
	 */
	private List<RptInputLstValidateLog> validate(int num, Map<String, Object> data, RptInputLstTempleInfo temp, Map<String, RptInputLstTempleField> colMap, List<RptInputLstTempleRule> ruleList, Map<String, Map<String, Object>> libList) {
		udipValidateLog.setRuleNm("字段类型校验");
		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		RptInputListDataRuleInfo udipDataRulesInfo = null;
		try {
			if (data == null || data.keySet().size() == 0) {
//				resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "没有需要校验的数据！",num));
				return resultList;
			}
			for (String col : data.keySet()) {// 循环记录的每个字段
				if(col.equalsIgnoreCase("COMMENTS"))
					continue;
				if(col.equalsIgnoreCase("DATAINPUT_ID"))
					continue;
				Object v = data.get(col);
				if(v==null)
					v="";
				RptInputLstTempleField tc = colMap.get(col);
				String errorField =  StringUtils.isEmpty(tc.getFieldCnName())?tc.getFieldEnName():tc.getFieldCnName();
//				System.out.println(BILL_NO);
				/* 1.数据翻译，使用数据字典，将显示值翻译成码值，如机构名称变成机构代码 */
				if (libList != null && v != null && StringUtils.isNotBlank(v.toString()) && libList.containsKey(tc.getFieldEnName())) {// 如果使用数据字典
					Map<String, Object> map = libList.get(tc.getFieldEnName());
					if (!map.containsKey(v.toString())) {// 如果不存在在数据字典中,则报错
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],[" + v + "]不在字典范围!", num));
					}else{
						data.put(col, map.get(v.toString()));
					}
				}
				/* 2.检查数据合法性，包括：空值，类型，大小，长度等 */
				if ((v == null || StringUtils.isBlank(v.toString())) && "1".equals(tc.getAllowNull())) {// 不可为空
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],不能为空！", num));
					continue;
				}
				if (StringUtils.isNotBlank("" + v) && ColumnType.INTEGER.equals(tc.getFieldType()) && !Pattern.matches(RegexRules.integer, v.toString())) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],[" + v + "]不是整数!", num));
					continue;
				}
				if (StringUtils.isNotBlank("" + v) && ColumnType.NumberType.contains(tc.getFieldType()) && !NumberUtils.isNumber(v.toString())) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],[" + v + "]不是数字!", num));
					continue;
				}
				if (StringUtils.isNotBlank("" + v) && ColumnType.NumberType.contains(tc.getFieldType())) {
					if (v.toString().indexOf(".") > -1 && Integer.parseInt(tc.getDecimalLength()) != 0 && (v.toString().length() - v.toString().indexOf(".") - 1) > Integer.parseInt(tc.getDecimalLength())) {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],[" + v + "]小数位数大于[" + tc.getDecimalLength() + "]", num));
						continue;
					}
					if (Integer.parseInt(tc.getFieldLength()) < v.toString().length()) {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],[" + v + "]数值长度大于[" + tc.getFieldLength() + "]", num));
						continue;
					}
				}
				if (StringUtils.isNotBlank("" + v) && ColumnType.VARCHAR2.equals(tc.getFieldType()) && StringUtils.length(v.toString()) > Integer.parseInt(tc.getFieldLength())) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],[" + v + "]长度超出[" + tc.getFieldLength() + "]!", num));
					continue;
				}
				if (StringUtils.isNotBlank("" + v) && ColumnType.VARCHAR.equals(tc.getFieldType()) && StringUtils.length(v.toString()) > Integer.parseInt(tc.getFieldLength())) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],[" + v + "]长度超出[" + tc.getFieldLength() + "]!", num));
					continue;
				}
				if (StringUtils.isNotBlank("" + v) && ColumnType.CHAR.equals(tc.getFieldType()) && StringUtils.length(v.toString()) > Integer.parseInt(tc.getFieldLength())) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "],[" + v + "]长度超出[" + tc.getFieldLength() + "]!", num));
					continue;
				}
				if (StringUtils.isNotBlank("" + v) && ColumnType.DATE.equals(tc.getFieldType())) {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); 
						if(v.toString().length()==10)
							sdf = new SimpleDateFormat( "yyyyMMdd"); 
						//java.util.Date date = 
						sdf.parse(v.toString()); 
						//Date.valueOf((v.toString()));
					} catch (Exception e) {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "]," + v + "不是正确的日期格式!", num));
					}
					continue;
				}
				if (StringUtils.isNotBlank("" + v) && ColumnType.TIMESTAMP.equals(tc.getFieldType())) {
					String validateValue = v.toString();
					if(validateValue.length()==10)
						validateValue += " 00:00:00";
					try {
						Timestamp.valueOf(validateValue);
					} catch (Exception e) {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第 [" + num + "] 行记录，字段[" + errorField + "]," + v + "不是正确的时间戳格式(yyyy-mm-dd hh:mi:ss)!", num));
					}
					continue;
				}
				if (ColumnType.TIME.equals(tc.getFieldType())) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipDataRulesInfo, "校验出错！" + e.getMessage(),num));
		}
		return resultList;
	}

	/**
	 * 主键唯一性约束校验
	 * @param startNum
	 * @param data
	 * @param temp
	 * @param keyMap
	 * @param keyList
	 * @return
	 * @throws Exception
	 */
	private List<RptInputLstValidateLog> primaryCheck(Connection conn, Map<String, Map<String,
			Map<String, Object>>> keyDataMap, int startNum, RptTskIns udipTaskCaseInfo, 
			Map<String, Object> data, RptInputLstTempleInfo temp, Map<String, 
			RptInputLstTempleField> colMap, Map<String, List<String>> keyMap, 
			List<RptInputLstTempleConst> keyList, Collection<String> keyColumns ,
			String flag) throws Exception {
		udipValidateLog.setRuleNm("主键与唯一性约束校验");
		List<RptInputLstValidateLog> keyResult = Lists.newArrayList();
		for (RptInputLstTempleConst utp : keyList) {
			String cols[] = utp.getKeyColumn().split(";");
			String key = StringUtils.EMPTY;
			boolean stop = false;
			List<RptInputLstTempleField> columns = Lists.newArrayList();
			for (String col : cols) {
				Object v = "";
				if (!col.equals(UdipConstants.TAB_DATA_CASE)) {
					v = data.get(col);
					if (utp.getKeyType().equals(UdipConstants.TAB_PRIMARY) && (v == null || v.toString().equals(StringUtils.EMPTY))) {// 主键不能为空
						keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,主键约束[" + utp.getKeyName() + "]校验失败,主键[" + col + "]不能为空!",startNum));
						stop = true;
						continue;
					}
					columns.add(columns.size(), colMap.get(col));
				}else{
					v = udipTaskCaseInfo.getTaskInstanceId();
					data.put(UdipConstants.TAB_DATA_CASE, udipTaskCaseInfo.getTaskInstanceId());
					RptInputLstTempleField udipTempleColumns = new RptInputLstTempleField();
					udipTempleColumns.setFieldEnName(UdipConstants.TAB_DATA_CASE);
					udipTempleColumns.setFieldType(ColumnType.VARCHAR);
					columns.add(columns.size(), udipTempleColumns);
				}
				key += v.toString() + keysplit;
			}
			if (key.length() > 0) {
				key = key.substring(0, key.length() - 1);
			}
			if (stop) {// 进行下一个唯一性检查
				stop = false;
				continue ;
			}
			try {
				Map<String, Object> exit = null;
				if (keyDataMap != null) {
//					exit = keyDataMap.get(utp.getKeyId()).get(key);
					Map<String, Map<String, Object>> o =  keyDataMap.get(utp.getKeyId());
					if(o==null)
						exit = null;
					else
						exit = o.get(key);
				} else {
					exit = dataUtils.exit(conn, temp.getTableEnName(), columns, data);
				}
				if (flag.equals(UdipConstants.DATA_FLAG_SAVE)) {// 新增
					if (exit != null) {
						keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,[" + utp.getKeyName() + "]的值["+key+"]已经存在.请修改！",startNum));
						continue;
					}
				} else if (flag.equals(UdipConstants.DATA_FLAG_UPATE)){// 修改
					if (utp.getKeyType().equals(UdipConstants.TAB_PRIMARY)) {// 主键约束，更新时一定要存在该主键
						if (exit == null) {
							keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,[" + utp.getKeyName() + "]值为"+ key +"的记录不存在.无法修改！",startNum));
							continue;
						}
					} else {// 唯一性约束，如果已经存在唯一性约束，那么不能更新
						boolean in = false;
						if (exit != null) {
							for (String col : keyColumns) {	// 主键相等,说明是修改自己
								Object o = data.get(col);
								String type = StringUtils.EMPTY; 
								if (col.equals(UdipConstants.TAB_DATA_CASE)) {
									o = udipTaskCaseInfo.getTaskInstanceId();
									type = ColumnType.VARCHAR;
								} else {
									type = colMap.get(col).getFieldType();
								}
								if (!o.toString().equals(DataUtils.getValue(type, col, exit.get(col)))) {
									in = true;
								}
							}
							if (in) {
								keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,[" + utp.getKeyName() + "]的值"+key+"已经存在.请修改！",startNum));
								continue;
							}
						}
					}
				}
			} catch (Exception e) {
                e.printStackTrace();
				throw new Exception("校验出现异常：" + e);
			}
			if (keyMap.get(utp.getKeyId()).contains(key)) {// 已经存在
				keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,[" + utp.getKeyName() + "]存在重复数据.请修改!",startNum));
			} else {
				keyMap.get(utp.getKeyId()).add(key);
			}
		}
		return keyResult;
	}
	
	private String validateNew( int startNum, Connection conn,Map<String, Object> data,
			List<RptInputLstTempleConst> keyList,RptInputLstTempleInfo temp,
			RptTskIns udipTaskCaseInfo) {
		StringBuilder sqlBuff;
		String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
		Statement stmt = null;
		ResultSet rs = null;
		try {
			for (RptInputLstTempleConst cst : keyList) {
				sqlBuff = new StringBuilder();
				sqlBuff.append("SELECT COUNT(1) AS CNT FROM  ").append(schema).append(".").append(temp.getTableEnName())
						.append(" WHERE ").append(getQueryCondition(cst, data)).append(" AND flow_node_id = '")
						.append(udipTaskCaseInfo.getTaskNodeInstanceId()).append("' ");

				stmt = conn.createStatement();
				rs = stmt.executeQuery(sqlBuff.toString());
				if (rs.next()) {
					int cnt = rs.getInt("CNT");
					if (cnt == 0)
						return "1";
					else {
						StringBuilder keyBuff = new StringBuilder();
						StringBuilder valueBuff = new StringBuilder();
						String keyColumn = cst.getKeyColumn();
						String[] keys = keyColumn.split(";");
						boolean isFirst = true;
						for (String key : keys) {
							if (StringUtils.isNotEmpty(key)) {
								if (isFirst)
									isFirst = false;
								else {
									keyBuff.append(",");
									valueBuff.append(",");
								}
								keyBuff.append(key);
								valueBuff.append(data.get(key));
							}
						}
						return "第 [" + startNum + "] 行记录，字段[" + keyBuff.toString() + "]的值[" + valueBuff.toString()
								+ "]重复.请修改!";
					}
				}
				return "1";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.dataSourceBS.releaseConnection(rs, stmt, null);
		}
		return "1";
	}
	
	private String validateModify(int startNum, Connection conn,Map<String, Object> data,
			List<RptInputLstTempleConst> keyList,RptInputLstTempleInfo temp,
			RptTskIns udipTaskCaseInfo){

				StringBuilder sqlBuff ;
				String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
				Statement stmt = null ;
				try {
					String datainputId = (String) data.get("DATAINPUT_ID");
					for(RptInputLstTempleConst cst : keyList){
						sqlBuff = new StringBuilder();
						sqlBuff.append("SELECT COUNT(1) AS CNT FROM  ").append(schema).append(".").append(temp.getTableEnName()).append(" WHERE ")
						.append(getQueryCondition(cst,data)).append(" AND DATAINPUT_ID !='").append(datainputId).append("'")
						.append(" and flow_node_id = '").append(udipTaskCaseInfo.getTaskNodeInstanceId()).append("' ");
						
						stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(sqlBuff.toString());
							if(rs.next())
							{	int cnt = rs.getInt("CNT");
								if(cnt==0)
									return "1";
								else{
									StringBuilder keyBuff = new StringBuilder();
									StringBuilder valueBuff = new StringBuilder();
									String keyColumn = cst.getKeyColumn();
									String[] keys = keyColumn.split(";");
									boolean isFirst = true;
									for(String key:keys){
										if(StringUtils.isNotEmpty(key)){
											if(isFirst)
												isFirst = false;
											else
											{
												keyBuff.append(",");
												valueBuff.append(",");
											}
											keyBuff.append(key);
											valueBuff.append(data.get(key));
										}
									}
									return "第 [" + startNum + "] 行记录,字段[" + keyBuff.toString() + "]的值["+valueBuff.toString()+"]重复，请修改!";
									//return "第[" + startNum + "]条记录,约束[" + cst.getKeyName() + "]校验失败,字段[" + cst.getKeyColumn() + "]违反唯一性约束!";
								}
							}return "1";
					}			
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{

					try {
						if(stmt!=null)
						stmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return "1";
			
	}
	
	private String getQueryCondition(RptInputLstTempleConst con,Map<String, Object> data){
		String keyColumn = con.getKeyColumn();
		String[] keys = keyColumn.split(";");
		StringBuilder buff = new StringBuilder();
		boolean isFirst = true;
		for(String key:keys){
			if(StringUtils.isNotEmpty(key)){
				if(isFirst)
					isFirst = false;
				else
					buff.append(" AND ");
				buff.append(key).append("='").append(data.get(key)).append("' ");
			}
		}
		return buff.toString();
	}

	/**
	 * 主键唯一性约束校验
	 * @param startNum
	 * @param data
	 * @param temp
	 * @param keyMap
	 * @param keyList
	 * @return
	 * @throws Exception
	 */
	private List<RptInputLstValidateLog> primaryCheckweihai(Connection conn, 
			Map<String, Map<String, Map<String, Object>>> keyDataMap, 
			int startNum, RptTskIns udipTaskCaseInfo, Map<String, Object> data, 
			RptInputLstTempleInfo temp, Map<String, RptInputLstTempleField> colMap, 
			Map<String, List<String>> keyMap, List<RptInputLstTempleConst> keyList, 
			Collection<String> keyColumns ,String flag) throws Exception {

		List<RptInputLstValidateLog> keyResult = Lists.newArrayList();
		udipValidateLog.setRuleNm("主键与唯一性约束校验");
			//数据库校验
		if (flag.equals(UdipConstants.DATA_FLAG_SAVE)) {// 新增
			String msg = validateNew(startNum,conn,data, keyList, temp,udipTaskCaseInfo);
			if(!msg.equals("1")){
				keyResult.add(RptInputLstValidateLog.createLog(udipValidateLog,msg,startNum));
				//keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,主键约束[" + temp.getTempleName() + "]校验失败,主键[" + temp.get + "]不能为空!",startNum));
			}
		} else if (flag.equals(UdipConstants.DATA_FLAG_UPATE)){// 修改
			String msg = validateModify(startNum, conn,data, keyList, temp,udipTaskCaseInfo);
			if(!msg.equals("1")){
				keyResult.add(RptInputLstValidateLog.createLog(udipValidateLog,msg,startNum));
				//keyResult.add(validateModify.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,主键约束[" + temp.getTempleName() + "]校验失败,主键[" + temp.get + "]不能为空!",startNum));
			}
		}
			/**	//通过缓存信息校验
			for (int i =0;i<keyList.size();i++ ) {
				RptInputLstTempleConst utp = keyList.get(i);
				String dataInputId = (String) ((Map)data).get("DATAINPUT_ID");
				String cols[] = utp.getKeyColumn().split(";");
				String key = StringUtils.EMPTY;
				boolean stop = false;
				List<RptInputLstTempleField> columns = Lists.newArrayList();
				// 主键非空校验
				for (String col : cols) {
					Object v = "";
					if (!col.equals(UdipConstants.TAB_DATA_CASE)) {
						v = data.get(col);
						if (utp.getKeyType().equals(UdipConstants.TAB_PRIMARY) && (v == null || v.toString().equals(StringUtils.EMPTY))) {
							keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,主键约束[" + utp.getKeyName() + "]校验失败,主键[" + col + "]不能为空!",startNum));
							stop = true;
							continue;
						}
						columns.add(columns.size(), colMap.get(col));
					}else{
						v = udipTaskCaseInfo.getTaskInstanceId();
						data.put(UdipConstants.TAB_DATA_CASE, udipTaskCaseInfo.getTaskInstanceId());
						RptInputLstTempleField udipTempleColumns = new RptInputLstTempleField();
						udipTempleColumns.setFieldEnName(UdipConstants.TAB_DATA_CASE);
						udipTempleColumns.setFieldType(ColumnType.VARCHAR);
						columns.add(columns.size(), udipTempleColumns);
					}
					key += v.toString() + keysplit;
				}
				if (key.length() > 0) {
					key = key.substring(0, key.length() - 1);
				}
				if (stop) {
					//主键非空校验 未通过进行下一个唯一性检查
					stop = false;
					continue ;
				}
				try {
					Map<String, Object> exit = null;
					if (keyDataMap != null) {
						//exit = keyDataMap.get(utp.getKeyId()).get(key);
						Map<String, Map<String, Object>> kvMap = keyDataMap.get(dataInputId);
						exit = (Map<String, Object>)kvMap.get(0);
					} else {
						exit = dataUtils.exit(conn, temp.getTableEnName(), columns, data);
					}
					
					if (flag.equals(UdipConstants.DATA_FLAG_SAVE)) {// 新增
						if (exit != null) {
							keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,唯一性约束[" + utp.getKeyName() + "]校验失败,已经存在[" + key + "]！",startNum));
							continue;
						}
					} else if (flag.equals(UdipConstants.DATA_FLAG_UPATE)){// 修改
						if (utp.getKeyType().equals(UdipConstants.TAB_PRIMARY)) {//主键约束，更新时一定要存在该主键
//							if (exit == null) {
//								keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,主键约束[" + utp.getKeyName() + "]校验失败,记录["+key+"]不存在,无法修改！",startNum));
//								continue;
//							}
						} else {// 唯一性约束，如果已经存在唯一性约束，那么不能更新
							boolean in = false;
							if (exit != null) {
								for (String col : keyColumns) {	// 主键相等,说明是修改自己
									Object o = data.get(col);
									String type = StringUtils.EMPTY; 
									if (col.equals(UdipConstants.TAB_DATA_CASE)) {
										o = udipTaskCaseInfo.getTaskInstanceId();
										type = ColumnType.VARCHAR;
									} else {
										type = colMap.get(col).getFieldType();
									}
									if (!o.toString().equals(DataUtils.getValue(type, col, exit.get(col)))) {
										in = true;
									}
								}
								if (in) {
									keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,唯一性约束[" + utp.getKeyName() + "]校验失败,已经存在[" + key + "]！",startNum));
									continue;
								}
							}
						}
					}
				} catch (Exception e) {
	                e.printStackTrace();
					throw new Exception("校验出现异常：" + e);
				}
				
				// 存在重复数据
				if (keyMap.get(utp.getKeyId()).contains(key)) {
					keyResult.add(keyResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,唯一性约束[" + utp.getKeyName() + "]校验失败,存在重复数据!",startNum));
				} else {
					keyMap.get(utp.getKeyId()).add(key);
				}
			}
			
//		}**/
		return keyResult;
	}

	/**
	 * 机构字段校验
	 * @param startNum
	 * @param data
	 * @param temp
	 * @return
	 */
	private List<RptInputLstValidateLog> orgCodeCheck(int startNum, Map<String, Object> data, RptInputLstTempleInfo temp, List<String> orgList, String orgCode, Map<String, RptInputLstTempleField> colMap) {
		udipValidateLog.setRuleNm("机构字段校验");
		List<RptInputLstValidateLog> orgResult = Lists.newArrayList();
		if (StringUtils.isNotBlank(temp.getOrgColumn())) {// 有机构字段
			Object v = data.get(temp.getOrgColumn());
			if (UdipConstants.STATE_YES.equals(temp.getAllowInputLower())) {// 可以补录下级机构
				if (v != null && (!orgList.contains(orgCode) && !orgCode.equals(v.toString()))) {
					orgResult.add(orgResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,机构字段[" + colMap.get(temp.getOrgColumn()).getFieldCnName() + "]校验失败,只能补录本机构[" + orgCode + "]及下级机构!", startNum));
				}
			} else {// 不可以补录下级机构
				if (v != null && !orgCode.equals(v.toString())) {
					orgResult.add(orgResult.size(), RptInputLstValidateLog.createLog(udipValidateLog, "第[" + startNum + "]条记录,机构字段[" + colMap.get(temp.getOrgColumn()).getFieldCnName() + "]校验失败,只能补录本机构[" + orgCode + "]!", startNum));
				}
			}
		}
		return orgResult;
	}

	/**
	 * 
	 * @param listlog
	 * @param listlog2
	 * @param dataAdd
	 * @param dataUdp
	 * @param getMap
	 * @throws Exception 
	 */
	public void validate(List<RptInputLstValidateLog> listlog, List<RptInputLstValidateLog> listlog2,
			List<Map<String, Object>> dataAdd, List<Map<String, Object>> dataUdp, List<Map<String,Object>> dbDataList, Map<String, Object> getMap) throws Exception {
		
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp"); 
		//待提取到上一层
		Connection conn = this.dataSourceBS.getConnection(temp.getDsId());
		getMap.put("conn", conn);
		int count = dataUtils.getDataCount(conn,temp, null, null);// 获取补录表的总数据量
		getMap.put("count", count);
		/**获取当前用户的本级以及下级机构**/
		String orgCode = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		List<String> childrens = this.dataRuleBS.findOrgChildrenInf(orgCode);
		getMap.put("childrens", childrens);
		
		if (CollectionUtils.isNotEmpty(dataAdd)){
			//dataAdd = getParamStrAdd(dataAdd, temp);
			List<RptInputLstValidateLog> listlog1= this.validateweihai(dataAdd, UdipConstants.DATA_FLAG_SAVE, getMap);
			if(listlog1 != null && !listlog1.isEmpty()) {
				listlog.addAll(listlog1);
			}
		}
		if (CollectionUtils.isNotEmpty(dataUdp)) {
			listlog2 = this.validateweihai(dataUdp, UdipConstants.DATA_FLAG_UPATE, getMap);
		}
		if(dbDataList != null && (listlog2==null||listlog2.isEmpty())) {
			listlog2 = this.validateweihai(dbDataList, UdipConstants.DATA_FLAG_UPATE, getMap);
		}
		listlog.addAll(listlog2);
		this.dataSourceBS.releaseConnection(null, null, conn);
	}
	
}
