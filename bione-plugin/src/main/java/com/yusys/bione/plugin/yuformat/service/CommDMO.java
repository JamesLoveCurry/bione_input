package com.yusys.bione.plugin.yuformat.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.yusys.bione.plugin.drivercfg.utils.DriverCfgUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.HashVOStruct;
import com.yusys.bione.plugin.yuformat.utils.InsertSQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.UpdateSQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;
import com.yusys.bione.plugin.yuformat.web.BillCardHtmlJSBuilder;

/**
 * 保存卡片数据,保存列表数据等常用平台方法
 * 
 * @author xch
 *
 */
public class CommDMO {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private YuFormatUtil bsUtil = new YuFormatUtil(); //
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

	private static HashVO[] allDatabaseHvs = null; //

	private BillCardHtmlJSBuilder cardBuilder = null; // 查询时需要使用下拉框的处理,在这个方法中

	//
	public JSONObject getUUID(JSONObject _par) throws Exception {
		String str_uuid = UUID.randomUUID().toString(); //

		JSONObject jso_rt = new JSONObject(); //
		jso_rt.put("UUID", str_uuid); //

		return jso_rt; //
	}

	// 直接根据SQL查询数据,返回数组
	public JSONObject getHashVOsBySQL(JSONObject _par) throws Exception {
		String str_sql = _par.getString("SQL"); // SQL语句
		HashVO[] hvs_data = bsUtil.getHashVOs(str_sql); // 返回数据

		JSONArray jsy_data = new JSONArray(); // json数组对象..
		if (hvs_data != null) {
			for (int i = 0; i < hvs_data.length; i++) {
				String[] str_keys = hvs_data[i].getKeys(); //
				JSONObject jso_rowData = new JSONObject(); //
				for (int j = 0; j < str_keys.length; j++) {
					jso_rowData.put(str_keys[j], hvs_data[i].getStringValue(str_keys[j], "")); // 设置数据!
				}
				jsy_data.add(jso_rowData); // 加入
			}
		}

		JSONObject jso_rt = new JSONObject(); // 创建返回对象
		jso_rt.put("DATA", jsy_data); // 返回数据
		return jso_rt; //
	}
	
	// 直接根据SQL查询数据,返回数组
	public JSONObject getHashVOsBySQLAndDs(JSONObject _par) throws Exception {
		String str_sql = _par.getString("SQL"); // SQL语句
		String str_ds = _par.getString("DS"); // 数据源
		HashVO[] hvs_data = bsUtil.getHashVOsByDS(str_ds, str_sql); // 查询数据

		JSONArray jsy_data = new JSONArray(); // json数组对象..
		if (hvs_data != null) {
			for (int i = 0; i < hvs_data.length; i++) {
				String[] str_keys = hvs_data[i].getKeys(); //
				JSONObject jso_rowData = new JSONObject(); //
				for (int j = 0; j < str_keys.length; j++) {
					jso_rowData.put(str_keys[j], hvs_data[i].getStringValue(str_keys[j], "")); // 设置数据!
				}
				jsy_data.add(jso_rowData); // 加入
			}
		}

		JSONObject jso_rt = new JSONObject(); // 创建返回对象
		jso_rt.put("DATA", jsy_data); // 返回数据
		return jso_rt; //
	}

	// 直接通过类名反射查询列表数据,替代SQL的思想，但又想使用表格控件!
	public JSONObject getBillListDataByClass(JSONObject _par) throws Exception {
		String str_templetcode = _par.getString("templetcode"); //
		String str_className = _par.getString("className"); // 类名
		String str_methodName = _par.getString("methodName"); // 方法名,这个方法入参是JSONObject,返回是HashVO[]
		JSONObject jso_par = _par.getJSONObject("pars"); // 参数

		// 构建模板!
		Object[] templetVO = bsUtil2.getTempletVO(str_templetcode); //
		HashVO templetVO_1 = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] templetVO_1_item = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		// 反射查询数据
		Class cls = Class.forName(str_className); //
		Method mhd = cls.getMethod(str_methodName, new Class[] { JSONObject.class }); //
		HashVO[] hvs_data = (HashVO[]) mhd.invoke(cls.newInstance(), new Object[] { jso_par }); // 执行

		// ★★★★处理加载公式★★★★
		dealAfterLoad(templetVO_1_item, hvs_data);

		// 构建返回数据
		JSONObject jso_rt = new JSONObject(); // 创建返回对象
		jso_rt.put("total", hvs_data.length); // 总共多少记录数!!这是EasyUI处理分页时必须要的参数!

		JSONArray jsoArray = convertHashVOsToJSOArray(hvs_data, templetVO_1_item); //
		jso_rt.put("rows", jsoArray); // 数据!

		return jso_rt;

	}

	// 唯一性校验!
	public JSONObject validateOnlyOneByInsert(JSONObject _par) throws Exception {
		String str_table = _par.getString("table"); // 表名
		String str_colname = _par.getString("colname"); // 字段名
		String str_colvalue = _par.getString("colvalue"); // 字段值
		JSONArray validatefields = _par.getJSONArray("validatefields");
		JSONObject fieldValues = _par.getJSONObject("fieldValues");

		StringBuilder str_sql = new StringBuilder(
				"select count(*) c1 from " + str_table + " where " + str_colname + "='" + str_colvalue + "'");
		for (int i = 0; i < validatefields.size(); i++) {
			str_sql.append(" and " + validatefields.getString(i) + " = '" + fieldValues.get(validatefields.getString(i))
					+ "'");
		}

		boolean isSucess = true; //
		HashVO[] hvs_data = bsUtil.getHashVOs(str_sql.toString());
		if (hvs_data.length > 0) {
			int li_count = hvs_data[0].getIntegerValue("c1", 0); //
			if (li_count >= 1) {
				isSucess = false; // 新增时,只要有一条,就不能重复插入!
			}
		}

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("result", isSucess); // 失败!

		return jso_rt; //
	}

	// 唯一性校验!(修改时)
	public JSONObject validateOnlyOneByUpdate(JSONObject _par) throws Exception {
		String str_table = _par.getString("table"); // 表名
		String str_colname = _par.getString("colname"); // 字段名
		String str_colvalue = _par.getString("colvalue"); // 字段值
		String str_whereSQL = _par.getString("whereSQL"); // where条件
		JSONArray validatefields = _par.getJSONArray("validatefields");
		JSONObject fieldValues = _par.getJSONObject("fieldValues");

		//替换关键字，防止sql注入
		/*if(SqlValidateUtils.validateStr(str_whereSQL)) {
			str_whereSQL = SqlValidateUtils.replaceKeyword(str_whereSQL);
		}*/
		
		String str_sql_1 = "select " + str_colname + " from " + str_table + " where " + str_whereSQL;

		StringBuilder str_sql_2 = new StringBuilder(
				"select count(*) c1 from " + str_table + " where " + str_colname + "='" + str_colvalue + "'");
		for (int i = 0; i < validatefields.size(); i++) {
			str_sql_2.append(" and " + validatefields.getString(i) + " = '"
					+ fieldValues.get(validatefields.getString(i)) + "'");
		}

		boolean isSucess = true; //

		HashVO[] hvs_data_1 = bsUtil.getHashVOs(str_sql_1); //
		HashVO[] hvs_data_2 = bsUtil.getHashVOs(str_sql_2.toString()); //
		if (hvs_data_2.length > 0) {
			int li_count = hvs_data_2[0].getIntegerValue("c1", 0); //
			if (li_count >= 1) {
				String str_oldValue = hvs_data_1[0].getStringValue(str_colname, ""); // 旧的值
				// 如果旧的值与新的值不一样,然后新的值已存在,则才认为是失败!
				// 反之，如果旧值与新值一样,这里查询出来的就是本条,则即使有一条也是允许的!
				if (!str_oldValue.equals(str_colvalue)) {
					isSucess = false; // 新增时,只要有一条,就不能重复插入!
				}
			}
		}

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("result", isSucess); // 失败!

		return jso_rt; //
	}
	
	// 数字校验，大精度问题，js超过16位精度失真，通过后台进行数据转换
	public JSONObject validateBigNumber(JSONObject _par) throws Exception {
		String value = _par.getString("value"); // 值
		String index = _par.getString("index"); // 保留位数
		
		BigDecimal bigDecimal = new BigDecimal(value);
		  
		String f = "";
		if (StringUtils.isEmpty(index)) {
			f = "#0.00";
		} else {
			String ind = "";
			for (int i=0;i<Integer.valueOf(index);i++) {
				ind += "0";
			}
			if (Integer.valueOf(index) == 0) {
				f = "#0";
			} else {
				f = "#0." + ind;
			}
		}
		DecimalFormat dfm = new DecimalFormat(f);

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("value", dfm.format(bigDecimal));
		
		return jso_rt;
	}
	

	// 插入数据库
	public JSONObject insertBillCardData(JSONObject _par) throws Exception {
		String str_ds = _par.getString("ds"); // 数据源
		String str_templetcode = _par.getString("templetcode"); //
		JSONObject jso_formdata = _par.getJSONObject("formdata"); //

		Object[] templetVO = bsUtil2.getTempletVO(str_templetcode); //
		HashVO parentVO = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs
		String str_savetable = parentVO.getStringValue("savetable"); //

		InsertSQLBuilder isql = new InsertSQLBuilder(str_savetable); //
		for (int i = 0; i < hvs_b.length; i++) {
			if (hvs_b[i].getBooleanValue("issave", false)) { // 只有保存才处理
				String str_itemkey = hvs_b[i].getStringValue("itemkey"); //
				String str_itemtype = hvs_b[i].getStringValue("itemtype"); //
				String str_itemvalue = jso_formdata.getString(str_itemkey); //
				if (str_itemtype.equals("数字框") && str_itemvalue != null) {
					str_itemvalue = bsUtil.replaceAll(str_itemvalue, ",", "");
				}
				isql.putFieldValue(str_itemkey, str_itemvalue);
			}
		}
		String str_sql = isql.getSQL(); //
		// System.err.println(str_sql); //

		bsUtil.executeUpdate(str_ds, str_sql); //

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("msg", "新增数据成功!"); //
		return jso_rt; //
	}

	// 保存数据
	public JSONObject updateBillCardData(JSONObject _par) throws Exception {
		String dsType = DriverCfgUtils.getDriverType();
		String[] mysqlKeyword = DriverCfgUtils.getMysqlKeyword();
		String str_ds = _par.getString("ds"); // 数据源
		String str_templetcode = _par.getString("templetcode"); //
		JSONObject jso_formdata = _par.getJSONObject("formdata"); //
		String str_sqlWhere = _par.getString("SQLWhere"); // where条件,因为有可能主键本身也会修改,这时就需要使用旧的数据作为where条件

		//替换关键字，防止sql注入
		/*if(SqlValidateUtils.validateStr(str_sqlWhere)) {
			str_sqlWhere = SqlValidateUtils.replaceKeyword(str_sqlWhere);
		}*/
		
		Object[] templetVO = bsUtil2.getTempletVO(str_templetcode); //
		HashVO parentVO = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		String str_savetable = parentVO.getStringValue("savetable"); //

		UpdateSQLBuilder isql = new UpdateSQLBuilder(str_savetable, str_sqlWhere); //
		for (int i = 0; i < hvs_b.length; i++) {
			if (hvs_b[i].getBooleanValue("issave", false)) {
				String str_itemkey = hvs_b[i].getStringValue("itemkey"); //
				String str_itemtype = hvs_b[i].getStringValue("itemtype"); //
				String str_itemvalue = jso_formdata.getString(str_itemkey); //
				
				// 判断key是否存在，如果不存在，直接跳过
				// update by wangxy31 2021-10-11
				if (jso_formdata.containsKey(str_itemkey)) {
					if (str_itemtype.equals("数字框") && str_itemvalue != null) {
						str_itemvalue = bsUtil.replaceAll(str_itemvalue, ",", "");
						//替换关键字，防止sql注入
						/*if(SqlValidateUtils.validateStr(str_itemvalue)) {
							str_itemvalue = SqlValidateUtils.replaceKeyword(str_itemvalue);
						}*/
					}
					// 当数据库类型为mysql，并且字段为关键字
					if (dsType.equalsIgnoreCase("mysql") && (Arrays.asList(mysqlKeyword).contains(str_itemkey))) {
						str_itemkey = "`" +str_itemkey+"`";
					}
					
					// 更新数据，去除主键
					if (!str_itemkey.equalsIgnoreCase("RID")) {
						isql.putFieldValue(str_itemkey, str_itemvalue);
					}
				}
			}
		}

		String str_sql = isql.getSQL(); //

		bsUtil.executeUpdate(str_ds, str_sql); //

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("msg", "保存数据成功!"); //
		return jso_rt; //
	}

	// 删除表格数据
	public JSONObject deleteBillListData(JSONObject _par) throws Exception {
		String str_ds = _par.getString("ds"); //
		// String str_templetcode = _par.getString("templetcode"); //
		String str_savetable = _par.getString("savetable"); //
		String str_sqlWhere = _par.getString("SQLWhere"); // where条件,因为有可能主键本身也会修改,这时就需要使用旧的数据作为where条件

		String str_sql = "delete from " + str_savetable + " where " + str_sqlWhere;
		if (str_ds != null && !str_ds.equals("")) {
			bsUtil.executeUpdate(str_ds, str_sql); //
		} else {
			bsUtil.executeUpdate(str_sql); //
		}

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("msg", "删除数据成功!"); //
		return jso_rt; //
	}


	// 查询卡片数据,与查询表格一条记录是一回事,所以直接转调
	public JSONObject getBillCardData(JSONObject _jso) throws Exception {
		return getBillListOneRowData(_jso); // 就是转调列表中查询一条数据!
	}

	// 只取一行数据,在修改列表数据,弹出卡片,然后保存回来后,只刷新一行数据,需要重新取一下!
	// 因为需要处理加载公式,所以卡片与列表查询最后都要走到这里!
	public JSONObject getBillListOneRowData(JSONObject _jso) throws Exception {
		String str_ds = _jso.getString("ds"); //
		String str_templetcode = _jso.getString("templetcode"); //
		String str_sqlwhere = _jso.getString("SQLWhere"); //
		String str_afterloadclass = _jso.getString("AfterLoadClass"); // 后续处理类
		
		/*//替换关键字，防止sql注入
		if(SqlValidateUtils.validateStr(str_sqlwhere)) {
			str_sqlwhere = SqlValidateUtils.replaceKeyword(str_sqlwhere);
		}*/
		
		return getBillDataBySQLWhere(str_ds, str_templetcode, str_sqlwhere, str_afterloadclass);
	}

	// 根据Where条件只查询一条数据,卡片查询，与列表刷新某一行，都是转调这个函数!!
	private JSONObject getBillDataBySQLWhere(String str_ds, String _templetCode, String _SQLWhere,
			String _afterloadclass) throws Exception {
		Object[] templetObjs = bsUtil2.getTempletVO(_templetCode); //
		HashVO templetVO = (HashVO) templetObjs[0]; // 第一个主表VO
		HashVO[] templetVOs_b = (HashVO[]) templetObjs[1]; // 第二个是子表VOs
		String str_table = templetVO.getStringValue("fromtable"); // 表名

		String str_sql = "select * from " + str_table + " where " + _SQLWhere; // 卡片查数据,根据主键应该就一条记录
		HashVO[] hvs = bsUtil.getHashVOsByDS(str_ds, str_sql); // 从哪个表取数!
		if (hvs.length == 0) {
			return null;
		}

		// 先处理加载公式,因为后来发现,必须是加载公式处理后的数据进行
		dealAfterLoad(templetVOs_b, hvs); // ★★★★处理加载公式★★★★

		// 后执行主表定义后续处理类
		if (_afterloadclass != null && !_afterloadclass.equals("")) {
			dealAfterLoadClass(hvs, _afterloadclass, templetVO, templetVOs_b); //
		}

		JSONObject jso_rt = convertHashVOsToJSObject(hvs[0], templetVOs_b); //

		// System.err.println(jso_rt);
		return jso_rt; // 返回数据
	}

	// 查询数据.
	// 根据SQL查询数据,EasyUI的界面!有分页,最重要的取数逻辑...
	public JSONObject getBillListData(JSONObject _jso) throws Exception {
		String str_ds = _jso.getString("DataSourceName"); //
		String str_templetCode = _jso.getString("templetcode"); //
		String str_sql = _jso.getString("SQL"); //
		String str_afterloadclass = _jso.getString("AfterLoadClass"); // 后续处理类

		boolean isPager = _jso.getBooleanValue("isPager"); //
		Integer li_currpage = _jso.getInteger("currPage"); // 当前页
		Integer li_onePageSize = _jso.getInteger("onePageSize"); // 每页多少条数据
		String str_pagerType = _jso.getString("pagerType"); // 分页类型，即1-在原来外面包一层;2-直接返回所有结果，但过滤一下;3-跳转游标!

		// System.err.println("列表数据源:" + str_ds); //

		// 先把SQL中的宏代码替换掉..
		int li_pos_1 = str_sql.indexOf("Class:"); // 如果有Class类转换SQL，则要进行较复杂的替换.
		if (li_pos_1 > 0) {
			String str_prefix = str_sql.substring(0, li_pos_1); //
			String str_remain = str_sql.substring(li_pos_1 + 6, str_sql.length()); //
			int li_pos2 = str_remain.indexOf(" "); //
			String str_className = str_remain.substring(0, li_pos2); //
			String str_sqlLeft = str_remain.substring(li_pos2, str_remain.length()); //

			AbstractQuerySQL sqlBuilder = (AbstractQuerySQL) Class.forName(str_className).newInstance();
			String str_userCode = YuFormatUtil.getUserNo();
			String str_userOrgNo = YuFormatUtil.getOrgNo();
			HashMap<String, Object> otherMap = new HashMap<String, Object>(); //

			String str_sqlByClass = sqlBuilder.getSQLCondition(str_userCode, str_userOrgNo, otherMap); //
			str_sql = str_prefix + str_sqlByClass + str_sqlLeft; //

			System.err.println("发现根据Calss类处理查询条件[" + str_className + "],转换后的SQL是[" + str_sql + "]");
		}
		str_sql = replaceMacroValue(str_sql);

		// 创建模板!
		Object[] templetVOs = bsUtil2.getTempletVO(str_templetCode); //

		// 模板主表
		HashVO templetVO = (HashVO) templetVOs[0]; // 模板子表信息
		HashVO[] templetVOs_b = (HashVO[]) templetVOs[1]; // 模板子表信息

		// 如果是分页,则较复杂!
		JSONObject jso_rt = new JSONObject(); //
		if (isPager) { // 如果分页!
			int li_beginRowNum = (li_currpage - 1) * li_onePageSize; // 如果是第2页,则是40
			int li_endRowNum = li_currpage * li_onePageSize; // 如果是第2页,则是60,即20-60

			if (str_pagerType == null || str_pagerType.equals("1")) { // 如果是第一种分页方式!
				// 实际SQL,即分页SQL
				StringBuilder sb_sql = new StringBuilder(); //

				// 获取数据库配置
				Properties props = new Properties();
				props.load(this.getClass().getClassLoader().getResourceAsStream("database.properties"));
				String str_dbType = props.getProperty("database.type");

				if (str_ds != null) {// 数据源不为空
					str_dbType = getDBType(str_ds);
					// System.out.println("根据数据源名称[" + str_ds + "]计算出数据库类型[" + str_dbType + "]"); //
				}

				String str_sql_convert = null; //
				// 判断数据库类型
				if (str_dbType.equalsIgnoreCase("ORACLE") || str_dbType.equalsIgnoreCase("ORACLE-RAC") || str_dbType.equalsIgnoreCase("DB2")) {
					sb_sql.append("with tt1 as ("); //
					sb_sql.append(str_sql); //
					sb_sql.append(") "); //
					if (li_currpage == 1) { // 如果是第一页则又可以特殊处理!即可以少一层子查询!从而提高性能!!
						sb_sql.append("select * from tt1 where rownum<=" + li_onePageSize); //
					} else {
						sb_sql.append("select * from (select rownum as tt1_rownum,tt1.* from tt1 where rownum<="
								+ li_endRowNum + ") tt2 where tt1_rownum>" + li_beginRowNum); //
					}
					str_sql_convert = sb_sql.toString(); // 包三层的SQL,如果没有定义order by 可以少一层!
				} else if (str_dbType.equalsIgnoreCase("MySQL") || str_dbType.equalsIgnoreCase("Inceptor")) {
					str_sql_convert = str_sql + " limit " + li_beginRowNum + "," + li_onePageSize; // 使用limit控制!!
				} else if (str_dbType.equalsIgnoreCase("Netezza")) {
					str_sql_convert = str_sql + " limit " + li_endRowNum + "," + li_beginRowNum; // 使用limit控制!!
				} else if (str_dbType.equalsIgnoreCase("Hive")) {
					// 获取主键,使用主键作为排序条件，原来组装的sql存在问题
					String pkName = templetVO.getM_hData().get("pkname").toString();
					pkName = pkName.replaceAll(",", " desc, ") + " desc ";
					str_sql_convert = "select * from (select row_number() over (order by " + pkName + " ) as rownum, t.* from (" + str_sql + ") t) tt " +
							"where rownum between " + li_beginRowNum + " and " + li_endRowNum;
				} else if (str_dbType.equalsIgnoreCase("postgresql")) {
					str_sql_convert = str_sql + " limit " + li_onePageSize + " offset " + li_beginRowNum; // 使用limit控制!!
				}

				// 实际查询数据★★★
				HashVO[] hvs = bsUtil.getHashVOsByDS(str_ds, str_sql_convert, null); // 从哪个表取数!

				// 先处理加载公式
				dealAfterLoad(templetVOs_b, hvs); // ★★★★处理加载公式★★★★

				// 后执行主表定义后续处理类
				if (str_afterloadclass != null && !str_afterloadclass.equals("")) {
					dealAfterLoadClass(hvs, str_afterloadclass, templetVO, templetVOs_b); //
				}

				JSONArray jsoArray = convertHashVOsToJSOArray(hvs, templetVOs_b);

				jso_rt.put("rows", jsoArray); // 数据!

				// 计算总数
				String str_countSQL = "select count(*) c1 from (" + str_sql + ") t";

				// 查询记录总数!★★★
				HashVO[] hvs_count = bsUtil.getHashVOsByDS(str_ds, str_countSQL); //
				int li_count = hvs_count[0].getIntegerValue("c1", 0); // 记录总数
				jso_rt.put("total", li_count); //
				jso_rt.put("realSQL", str_sql_convert); //
				jso_rt.put("realSQL3", str_sql); //
			} else if (str_pagerType.equals("2")) { // 如果是第2种分页方式!
				// 实际查询数据★★★
				int li_count = 0;
				String new_str_sql = str_sql;
				
				HashVO[] hvs = bsUtil.getHashVOsByDS(str_ds, new_str_sql, null); // 先直接取数!
				li_count = hvs.length; //

				// 根据分页截取中间页的数据!
				ArrayList<HashVO> currPageList = new ArrayList<HashVO>();
				for (int i = li_beginRowNum; i < li_endRowNum; i++) { // 从起始行号到结束行号
					if (i < hvs.length) {
						currPageList.add(hvs[i]); //
					}
				}
				hvs = currPageList.toArray(new HashVO[0]); // 把当前页的List替换返回数据!!

				// 先处理加载公式
				dealAfterLoad(templetVOs_b, hvs); // ★★★★处理加载公式★★★★

				// 后执行主表定义后续处理类
				if (str_afterloadclass != null && !str_afterloadclass.equals("")) {
					dealAfterLoadClass(hvs, str_afterloadclass, templetVO, templetVOs_b); //
				}

				JSONArray jsoArray = convertHashVOsToJSOArray(hvs, templetVOs_b);

				jso_rt.put("rows", jsoArray); // 数据!
				jso_rt.put("total", li_count); //
				jso_rt.put("realSQL", str_sql); //
				jso_rt.put("realSQL3", str_sql); //
			} else if (str_pagerType.equals("3")) { // 如果是第3种分页方式，即跳游标!!
				// 以后开发...
			}
		}  else { // 不分页!
			HashVO[] hvs = bsUtil.getHashVOsByDS(str_ds, str_sql, null); // 从哪个表取数!

			jso_rt.put("total", hvs.length); // 总共多少记录数!!这是EasyUI处理分页时必须要的参数!

			// 先处理加载公式
			dealAfterLoad(templetVOs_b, hvs); // ★★★★处理加载公式★★★★

			// 后执行主表定义后续处理类
			if (str_afterloadclass != null && !str_afterloadclass.equals("")) {
				dealAfterLoadClass(hvs, str_afterloadclass, templetVO, templetVOs_b); //
			}

			JSONArray jsoArray = convertHashVOsToJSOArray(hvs, templetVOs_b);

			jso_rt.put("rows", jsoArray); // 数据!
			jso_rt.put("realSQL", str_sql); //
			jso_rt.put("realSQL3", str_sql); //
		}

		return jso_rt;
	}
	
	
	/**
	 * 这个方法是通过改造getBillListData而来，原先getBillListData方法专门是前端所用，返回json
	 * 改造的原因是某些界面的表格不是单纯由sql查询出来的，而是经过一系列处理得来（这里面最重要的处理就是dealAfterLoad）
	 * 改造后的方法是专门给后端用的，调用此方法可获得一个HashVO数组，获得界面上面的数据
	 * @param _jso 包含了数据源名称，模板code，sql，后续处理类
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getBillListDataForBack(JSONObject _jso) throws Exception {
		String str_ds = _jso.getString("DataSourceName"); //
		String str_templetCode = _jso.getString("templetcode"); //
		String str_sql = _jso.getString("SQL"); //
		String str_afterloadclass = _jso.getString("AfterLoadClass"); // 后续处理类

		// 创建模板!
		Object[] templetVOs = bsUtil2.getTempletVO(str_templetCode); //

		// 模板主表
		HashVO templetVO = (HashVO) templetVOs[0]; // 模板子表信息
		HashVO[] templetVOs_b = (HashVO[]) templetVOs[1]; // 模板子表信息

		// 如果是分页,则较复杂!
		JSONObject jso_rt = new JSONObject(); //
		 // 不分页!
		HashVO[] hvs = bsUtil.getHashVOsByDS(str_ds, str_sql, null); // 从哪个表取数!

		jso_rt.put("total", hvs.length); // 总共多少记录数!!这是EasyUI处理分页时必须要的参数!

		// 先处理加载公式
		dealAfterLoad(templetVOs_b, hvs); // ★★★★处理加载公式★★★★

		// 后执行主表定义后续处理类
		if (str_afterloadclass != null && !str_afterloadclass.equals("")) {
			dealAfterLoadClass(hvs, str_afterloadclass, templetVO, templetVOs_b); //
		}

		return hvs;
	}

	// 处理后续类,异常怎么处理?是吃掉还是重抛?
	// 暂时先听吃掉.
	private void dealAfterLoadClass(HashVO[] _hvs, String _afterloadclass, HashVO _templetVO, HashVO[] _templetVOs_b) {
		try {
			long ll_1 = System.currentTimeMillis(); //
			String[] str_templetItemKeys = new String[_templetVOs_b.length]; // 模板中所有列
			for (int i = 0; i < _templetVOs_b.length; i++) {
				str_templetItemKeys[i] = _templetVOs_b[i].getStringValue("itemkey"); //
			}

			// 在数据HashVO[]中补全字段,即如果模板中有某个字段,而数据库中没有,则要加上
			// 否则处理校验结果时，增加颜色时,会因为没有该字段而跳过!
			for (int i = 0; i < _hvs.length; i++) {
				String[] str_itemKeys = _hvs[i].getKeys(); // 所有字段
				for (int j = 0; j < str_templetItemKeys.length; j++) {
					if (!isExistIn(str_templetItemKeys[j], str_itemKeys)) { // 如果模板中的某一列在数据中不存在
						_hvs[i].setAttributeValue(str_templetItemKeys[j], ""); //
					}
				}
			}

			// 反射处理..
			AbstractAfterLoadClass loadClass = (AbstractAfterLoadClass) Class.forName(_afterloadclass).newInstance(); //
			HashMap<String, Object> confMap = new HashMap<String, Object>(); //
			confMap.put("templetvo", _templetVO); // 把模板送入,因为可能实现类会根据不同的模板或表名进行不同的处理!
			loadClass.afterLoadDeal(_hvs, confMap); //
			long ll_2 = System.currentTimeMillis(); //
			System.err.println("成功执行后续处理类【" + _afterloadclass + "】,耗时[" + (ll_2 - ll_1) + "]");
		} catch (Exception _ex) {
			logger.error("后续处理类【" + _afterloadclass + "】执行发生异常", _ex); //
		}
	}

	// 是否存在
	private boolean isExistIn(String _itemkey, String[] _hashVODataKeys) {
		for (int i = 0; i < _hashVODataKeys.length; i++) {
			if (_hashVODataKeys[i].equalsIgnoreCase(_itemkey)) {
				return true; //
			}
		}
		return false;
	}

	// XML模板中的自动查询条件与查询条件，可以支持${}的特殊变量，然后进行替换处理,比如登录人员帐号,所在机构,所在的rpt机构，等等
	private String replaceMacroValue(String _sql) {
		String str_sql = _sql; // 原始SQL
		String[] str_keys = bsUtil.getFormulaMacPars(str_sql, "${", "}"); // 找出其中所有的：${变量名}
		// 循环替换各个变量
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equalsIgnoreCase("CURRDATE")) {
				str_sql = bsUtil.replaceAll(str_sql, "${" + str_keys[i] + "}", bsUtil.getCurrDate()); //
			} else if (str_keys[i].equalsIgnoreCase("SUBCURRDATE")) {
				str_sql = bsUtil.replaceAll(str_sql, "${" + str_keys[i] + "}", bsUtil.getSubCurrDate()); //
			} else if (str_keys[i].equalsIgnoreCase("CURRDATE8")) {
				str_sql = bsUtil.replaceAll(str_sql, "${" + str_keys[i] + "}", bsUtil.getCurrDate1()); //
			} else if (str_keys[i].equalsIgnoreCase("LoginUserCode")) {
				str_sql = bsUtil.replaceAll(str_sql, "${" + str_keys[i] + "}", YuFormatUtil.getUserNo()); //
			} else if (str_keys[i].equalsIgnoreCase("LoginUserName")) {
				str_sql = bsUtil.replaceAll(str_sql, "${" + str_keys[i] + "}", YuFormatUtil.getUserName()); //
			} else if (str_keys[i].equalsIgnoreCase("LoginUserOrgNo")) {
				str_sql = bsUtil.replaceAll(str_sql, "${" + str_keys[i] + "}", YuFormatUtil.getOrgNo()); //
			} else if (str_keys[i].startsWith("LoginUserRptOrgNo_")) { // 如果是取rpt_org_info中的org_no
				String str_org_no = YuFormatUtil.getOrgNo(); // 先取出bione_org_info中的org_no
				String str_orgtype = str_keys[i].substring(str_keys[i].indexOf("_") + 1, str_keys[i].length()); // 截取后辍,即org_type
				String str_rptOrgSql = "select org_no from rpt_org_info where mgr_org_no='" + str_org_no
						+ "' and org_type='" + str_orgtype + "'"; // 重新查询rpt_org_info中的org_no
				HashVO[] hvs_rpt_org = bsUtil.getHashVOs(str_rptOrgSql); // 查询数据库
				if (hvs_rpt_org != null && hvs_rpt_org.length > 0) {
					str_sql = bsUtil.replaceAll(str_sql, "${" + str_keys[i] + "}",
							hvs_rpt_org[0].getStringValue("org_no")); //
				}
			} else {
				// 如果没匹配上,则不处理..
			}
		}
		return str_sql;
	}

	// 查询树的数据
	public JSONObject getBillTreeDataOption(JSONObject _par) throws Exception {
		String str_templetcode = _par.getString("templetcode"); // 模板编码
		Object[] templetVO = bsUtil2.getTempletVO(str_templetcode); //
		String refwheresql = _par.getString("refwheresql"); // 拼接的where条件
		String autocondition = _par.getString("autocondition");
		String querycontion = _par.getString("querycontion");
		HashVO parentVO = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs
		if (StringUtils.isNotEmpty(refwheresql)) {
			parentVO.setAttributeValue("refwheresql", refwheresql);
		}
		parentVO.setAttributeValue("autocondition", autocondition);
		parentVO.setAttributeValue("querycontion", querycontion);
		String str_treeData = getBillTreeDataOption(parentVO, hvs_b); // 先计算出字符串
		JSONObject jso_tree = JSONObject.parseObject(str_treeData); // 把字符串转成JSON对象

		JSONArray jso_array = new JSONArray(); // 创建数组,因为前端的树控件需要的是个数组对象
		jso_array.add(jso_tree); // 加入

		// 创建返回对象!
		JSONObject jso_rt = new JSONObject();
		jso_rt.put("TreeData", jso_array); //
		return jso_rt; //
	}

	// 根据tree_type类型不同，实现不同机制的树..
	private String getBillTreeDataOption(HashVO parentVO, HashVO[] hvs_b) throws Exception {
		String str_tree_type = parentVO.getStringValue("tree_type"); // 类型
		if (str_tree_type.equalsIgnoreCase("group")) {
			return getBillTreeDataOptionByGroup(parentVO, hvs_b);
		} else if (str_tree_type.equalsIgnoreCase("parent")) {
			return getBillTreeDataOptionByParent(parentVO, hvs_b);
		} else {
			StringBuilder sb_html = new StringBuilder();
			sb_html.append("{\r\n"); // 根结点
			sb_html.append("id : \"-99999ROOT\",\r\n"); // 根结点
			sb_html.append("text : \"tree_type不对(group或parent)\"\r\n"); // 根结点的名称
			sb_html.append("}\r\n");
			return sb_html.toString(); //
		}

	}

	// 计算树型结构,单表分组
	private String getBillTreeDataOptionByGroup(HashVO parentVO, HashVO[] hvs_b) throws Exception {
		String str_templetName = parentVO.getStringValue("templetname"); // 模板名称
		String str_fromtable = parentVO.getStringValue("fromtable"); // 从哪个表取数
		String str_querycontion = parentVO.getStringValue("querycontion"); // 查询条件
		String str_orderbys = parentVO.getStringValue("orderbys"); // 排序条件

		String str_tree_groupcols = parentVO.getStringValue("tree_groupcols"); // 树的分组统计列!
		String str_tree_id = parentVO.getStringValue("tree_id"); // 树的id字段
		String str_tree_text = parentVO.getStringValue("tree_text"); // 树的text字段
		String str_where_sql = parentVO.getStringValue("refwheresql");
		String str_sql = "select * from " + str_fromtable; //
		if (str_querycontion != null && !str_querycontion.equals("")) {
			if (str_querycontion.startsWith("Class:")) {
				str_querycontion = getSQLByClass(str_querycontion); //
			}

			str_sql = str_sql + " where " + str_querycontion;
		}
		if (StringUtils.isNotEmpty(str_where_sql)){
			str_sql = str_sql + " and " + str_where_sql;
		}

		if (str_orderbys != null && !str_orderbys.trim().equals("")) {
			str_sql = str_sql + " order by " + str_orderbys;
		}

		// 替换其中的宏代码
		str_sql = replaceMacroValue(str_sql); //

		HashVO[] hvs_data = bsUtil.getHashVOs(str_sql); // 先计算出数据!

		// 先加载公式处理.
		dealAfterLoad(hvs_b, hvs_data); //

		StringBuilder sb_html = new StringBuilder();

		// 如果没有定义分组,其实就是一层
		if (str_tree_groupcols == null || str_tree_groupcols.trim().equals("")) {
			sb_html.append("{\r\n"); // 根结点
			sb_html.append("id : \"-99999ROOT\",\r\n"); // 根结点
			sb_html.append("text : \"" + str_templetName + "\",\r\n"); // 根结点的名称
			sb_html.append("children:[\r\n");

			for (int i = 0; i < hvs_data.length; i++) {
				sb_html.append("{\r\n");
				if (str_tree_id == null || str_tree_id.equals("")) {
					sb_html.append("id:" + (i + 1) + ",\r\n");
				} else {
					sb_html.append("id:\"" + hvs_data[i].getStringValue(str_tree_id, "") + "\",\r\n");
				}
				sb_html.append("text:\"" + hvs_data[i].getStringValue(str_tree_text, "") + "\",\r\n");

				// 所有属性!
				sb_html.append("attributes : { \r\n");
				for (int j = 0; j < hvs_b.length; j++) {
					String str_itemkey = hvs_b[j].getStringValue("itemkey"); //
					String str_itemvalue = hvs_data[i].getStringValue(str_itemkey, ""); //
					if (j == hvs_b.length - 1) {
						sb_html.append(str_itemkey + " : \"" + str_itemvalue + "\"\r\n"); //
					} else {
						sb_html.append(str_itemkey + " : \"" + str_itemvalue + "\",\r\n"); //
					}
				}

				sb_html.append("}\r\n");

				if (hvs_data.length == 1 || i == hvs_data.length - 1) {
					sb_html.append("}\r\n");
				} else {
					sb_html.append("},\r\n");
				}
			}

			sb_html.append("]\r\n");
			sb_html.append("}\r\n");
		} else { // 分层!复杂就复杂在这里!
			ArrayList<DefaultMutableTreeNode> allNodeList = new ArrayList<DefaultMutableTreeNode>();
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(
					new BillTreeGroupVO("-99999ROOT", str_templetName)); //
			allNodeList.add(root); //

			String[] str_groups = bsUtil.split(str_tree_groupcols, ";"); // 可能有多层组!比如:roles_name;createdate

			// 处理各层级,是一层一层的处理!
			for (int i = 0; i < str_groups.length; i++) { // 遍历各层组
				String str_thisGroupColName = str_groups[i]; // 列名
				// System.err.println("当前列名" + str_thisGroupColName); //
				DefaultMutableTreeNode[] levelNodes = findNodesByLevel(allNodeList, i); // 寻找第几层的!第一次只找到root一个
				// System.err.println("找出第【" + i + "】层的结点共[" + levelNodes.length + "]个");//

				// 遍历所有这一层的数据!比如第2层,第3层..
				for (int j = 0; j < levelNodes.length; j++) { // 遍历各层级
					TreeNode[] toRootPathNodes = levelNodes[j].getPath(); // 到根结点的路径
					// System.err.println("第【" + i + "】层的第【" + j + "】个结点的父亲路径共【" +
					// toRootPathNodes.length + "】个");

					// 拼接where条件
					HashMap<String, String> whereMap = new HashMap<String, String>();
					for (int k = 1; k < toRootPathNodes.length; k++) {
						BillTreeGroupVO groupVO = (BillTreeGroupVO) ((DefaultMutableTreeNode) toRootPathNodes[k])
								.getUserObject(); //
						whereMap.put(groupVO.getColName(), groupVO.getColValue()); // 加上一个where条件
					}

					// 寻找出本层级的数据
					String[] str_thisGroupColValues = findThisGroupValues(whereMap, hvs_data, str_thisGroupColName);
					for (int k = 0; k < str_thisGroupColValues.length; k++) {
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
								new BillTreeGroupVO(str_thisGroupColName, str_thisGroupColValues[k])); //
						levelNodes[j].add(newNode); // 先勾连起来!!
						allNodeList.add(newNode); // 也加入，为了后面计算方便!
					}
				}
			}

			// System.out.println(root);

			// 处理最后一层,最后一层很特别，挂载的不是目录,而是实际数据
			DefaultMutableTreeNode[] lastLevelNodes = findNodesByLevel(allNodeList, str_groups.length); // 寻找第几层的!第一次只找到root一个
			for (int j = 0; j < lastLevelNodes.length; j++) { // 遍历各层级
				TreeNode[] toRootPathNodes = lastLevelNodes[j].getPath(); // 到根结点的路径

				// 拼接where条件
				HashMap<String, String> whereMap = new HashMap<String, String>();
				for (int k = 1; k < toRootPathNodes.length; k++) {
					BillTreeGroupVO groupVO = (BillTreeGroupVO) ((DefaultMutableTreeNode) toRootPathNodes[k])
							.getUserObject(); //
					whereMap.put(groupVO.getColName(), groupVO.getColValue()); // 加上一个where条件
				}

				// 寻找出这最一个最后一层挂载的所有HashVO
				HashVO[] hvs_dataThisLevel = findLastGroupHashVOs(whereMap, hvs_data);
				for (int k = 0; k < hvs_dataThisLevel.length; k++) {
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(hvs_dataThisLevel[k]); //
					lastLevelNodes[j].add(newNode); // 先勾连起来!!
					allNodeList.add(newNode); // 也加入，为了后面计算方便!
				}
			}

			StringBuilder sb_tree = new StringBuilder(); //
			printTreeAsJsonStr(root, sb_tree, str_tree_id, str_tree_text, hvs_b, true);
			// System.out.println(sb_tree.toString()); //

			sb_html.append(sb_tree.toString());
		}

		return sb_html.toString();
	}

	private String getSQLByClass(String _querycontion) throws Exception {
		String str_className = _querycontion.substring(6, _querycontion.length()); //
		System.err.println("实际类名:【" + str_className + "】"); //

		AbstractQuerySQL sqlBuilder = (AbstractQuerySQL) Class.forName(str_className).newInstance();
		String str_userCode = YuFormatUtil.getUserNo();
		String str_userOrgNo = YuFormatUtil.getOrgNo();
		HashMap<String, Object> otherMap = new HashMap<String, Object>(); //

		String str_sqlByClass = sqlBuilder.getSQLCondition(str_userCode, str_userOrgNo, otherMap); //

		return str_sqlByClass; //
	}

	// 根据上下级关系构建树.
	private String getBillTreeDataOptionByParent(HashVO parentVO, HashVO[] hvs_b) throws Exception {
		String str_templetName = parentVO.getStringValue("templetname"); // 模板名称
		String str_fromtable = parentVO.getStringValue("fromtable"); // 从哪个表取数
		String str_querycontion = parentVO.getStringValue("querycontion"); // 查询条件
		String str_orderbys = parentVO.getStringValue("orderbys"); // 排序条件

		String str_tree_id = parentVO.getStringValue("tree_id"); // 树的id字段
		String str_tree_text = parentVO.getStringValue("tree_text"); // 树的text字段
		String str_tree_parentid = parentVO.getStringValue("tree_parentid"); // 树的父字段

		String str_sql = "select * from " + str_fromtable; //
		if (str_querycontion != null && !str_querycontion.equals("")) {
			if (str_querycontion.startsWith("Class:")) {
				str_querycontion = getSQLByClass(str_querycontion); //
			}
			str_sql = str_sql + " where " + str_querycontion;
		}

		if (str_orderbys != null && !str_orderbys.trim().equals("")) {
			str_sql = str_sql + " order by " + str_orderbys;
		}

		// 替换其中的宏代码
		str_sql = replaceMacroValue(str_sql);

		HashVO[] hvs_data = bsUtil.getHashVOs(str_sql); // 先计算出数据!

		// 先加载公式处理.
		dealAfterLoad(hvs_b, hvs_data); //

		// 构造树结构对象
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new BillTreeGroupVO("-99999ROOT", str_templetName)); //

		// 先把所有的结点创建
		DefaultMutableTreeNode[] allNodes = new DefaultMutableTreeNode[hvs_data.length];
		HashMap<String, DefaultMutableTreeNode> nodeMap = new HashMap<String, DefaultMutableTreeNode>(); //
		for (int i = 0; i < hvs_data.length; i++) {
			String str_pkValue = hvs_data[i].getStringValue(str_tree_id); // 主键值
			allNodes[i] = new DefaultMutableTreeNode(hvs_data[i]);
			nodeMap.put(str_pkValue, allNodes[i]); //
		}

		// 找父亲,构建勾连关系..
		for (int i = 0; i < allNodes.length; i++) {
			HashVO itemVO = (HashVO) allNodes[i].getUserObject(); //
			String str_parentPKValue = itemVO.getStringValue(str_tree_parentid); // 主键值
			DefaultMutableTreeNode parentNode = nodeMap.get(str_parentPKValue); //
			if (parentNode == null) {
				root.add(allNodes[i]);
			} else {
				parentNode.add(allNodes[i]);
			}
		}

		// 使用工具类将一颗树生成JSON的文本样式
		StringBuilder sb_tree = new StringBuilder(); //
		printTreeAsJsonStr(root, sb_tree, str_tree_id, str_tree_text, hvs_b, true);

		// System.err.println(sb_tree);

		StringBuilder sb_html = new StringBuilder();
		sb_html.append(sb_tree);

		return sb_html.toString();
	}

	// 找出所有第?层的结点!
	private DefaultMutableTreeNode[] findNodesByLevel(ArrayList<DefaultMutableTreeNode> _allList, int _level) {
		ArrayList<DefaultMutableTreeNode> list = new ArrayList<DefaultMutableTreeNode>(); //
		for (int i = 0; i < _allList.size(); i++) {
			DefaultMutableTreeNode node = _allList.get(i); //
			if (node.getLevel() == _level) {
				list.add(node); // 加入
			}
		}
		return list.toArray(new DefaultMutableTreeNode[0]); //
	}

	// 计算出满足条件的新的维度清单!
	private String[] findThisGroupValues(HashMap<String, String> _whereMap, HashVO[] _hvs, String _thisGroupColName) {
		LinkedHashSet<String> thisColValueSet = new LinkedHashSet<String>(); //
		String[] str_keys = _whereMap.keySet().toArray(new String[0]); // 所有的列!
		for (int i = 0; i < _hvs.length; i++) {
			boolean isEquals = true; // 是否相等
			for (int j = 0; j < str_keys.length; j++) {
				String str_itemValue_1 = _hvs[i].getStringValue(str_keys[j], ""); //
				if (str_itemValue_1.equals("")) {
					str_itemValue_1 = "【空值】";
				}
				String str_itemValue_2 = _whereMap.get(str_keys[j]); // 可能是【空值】
				if (!str_itemValue_1.equals(str_itemValue_2)) {
					isEquals = false; // 只要有一个不相等,则就不算进来
					break; //
				}
			}

			// 如果完全匹配成功,则加入!
			if (isEquals) {
				String str_thisGroupColValue = _hvs[i].getStringValue(_thisGroupColName, ""); //
				if (str_thisGroupColValue.equals("")) {
					str_thisGroupColValue = "【空值】";
				}
				thisColValueSet.add(str_thisGroupColValue);
			}
		}

		return thisColValueSet.toArray(new String[0]);
	}

	// 寻找出最后一层的所有HashVO[]
	private HashVO[] findLastGroupHashVOs(HashMap<String, String> _whereMap, HashVO[] _hvs) {
		ArrayList<HashVO> list = new ArrayList<HashVO>(); //
		String[] str_keys = _whereMap.keySet().toArray(new String[0]); // 所有的列!
		for (int i = 0; i < _hvs.length; i++) {
			boolean isEquals = true; // 是否相等
			for (int j = 0; j < str_keys.length; j++) {
				String str_itemValue_1 = _hvs[i].getStringValue(str_keys[j], ""); //
				if (str_itemValue_1.equals("")) {
					str_itemValue_1 = "【空值】";
				}
				String str_itemValue_2 = _whereMap.get(str_keys[j]); // 可能是【空值】
				if (!str_itemValue_1.equals(str_itemValue_2)) {
					isEquals = false; // 只要有一个不相等,则就不算进来
					break; //
				}
			}

			// 如果完全匹配成功,则加入!
			if (isEquals) {
				list.add(_hvs[i]);
			}
		}

		return list.toArray(new HashVO[0]);
	}

	// 递归输入树型结构..
	private void printTreeAsJsonStr(DefaultMutableTreeNode _node, StringBuilder _sbText, String str_tree_id,
			String str_tree_text, HashVO[] hvs_b, boolean _isLast) {
		Object nodeObj = _node.getUserObject(); // 对象!!这个对象可能是目录，也可能是HashVO
		BillTreeGroupVO treeGroupVO = null; //
		HashVO hashVO = null; //
		if (nodeObj instanceof BillTreeGroupVO) {
			treeGroupVO = (BillTreeGroupVO) nodeObj;
		} else if (nodeObj instanceof HashVO) {
			hashVO = (HashVO) nodeObj;
		}

		// 节点类型
		int li_level = _node.getLevel(); //
		String str_span = "";
		for (int i = 0; i < li_level; i++) {
			str_span = str_span + "  ";
		}

		_sbText.append(str_span + "{\r\n"); //
		if (treeGroupVO != null) {
			String str_id = "";
			if (_node.isRoot()) {
				str_id = treeGroupVO.getColName();
			} else {
				str_id = treeGroupVO.getColName() + "|" + treeGroupVO.getColValue();
			}

			_sbText.append(str_span + "id : \"" + str_id + "\",\r\n"); //
			_sbText.append(str_span + "text : \"" + treeGroupVO.getColValue() + "\",\r\n"); //
		} else if (hashVO != null) {
			_sbText.append(str_span + "id : \"" + hashVO.getStringValue(str_tree_id, "") + "\",\r\n"); //
			String str_itemType = findItemType(hvs_b, str_tree_text);
			if ("下拉框".equals(str_itemType)) {
				_sbText.append(
						str_span + "text : \"" + hashVO.getStringValue(str_tree_text + "_◆view◆", "") + "\",\r\n"); //
			} else {
				_sbText.append(str_span + "text : \"" + hashVO.getStringValue(str_tree_text, "") + "\",\r\n"); //
			}
		}

		int li_count = _node.getChildCount(); //
		if (li_count > 0) {
			_sbText.append(str_span + "children : [ \r\n"); // 输出所有子结点!
			for (int i = 0; i < li_count; i++) {
				if (li_count == 1 || i == li_count - 1) {
					printTreeAsJsonStr((DefaultMutableTreeNode) _node.getChildAt(i), _sbText, str_tree_id,
							str_tree_text, hvs_b, true);
				} else {
					printTreeAsJsonStr((DefaultMutableTreeNode) _node.getChildAt(i), _sbText, str_tree_id,
							str_tree_text, hvs_b, false);
				}
			}
			_sbText.append(str_span + "],\r\n"); //
		}

		// 输出所有属性
		_sbText.append(str_span + "attributes : { \r\n");
		if (treeGroupVO != null) {
			_sbText.append(str_span + "colname : \"" + treeGroupVO.getColName() + "\",\r\n"); //
			_sbText.append(str_span + "colvalue : \"" + treeGroupVO.getColValue() + "\"\r\n"); //
		} else if (hashVO != null) {
			for (int i = 0; i < hvs_b.length; i++) {
				String str_itemkey = hvs_b[i].getStringValue("itemkey"); //
				String str_itemvalue = hashVO.getStringValue(str_itemkey, ""); //
				if (i == hvs_b.length - 1) {
					_sbText.append(str_span + str_itemkey + " : \"" + str_itemvalue + "\"\r\n"); //
				} else {
					_sbText.append(str_span + str_itemkey + " : \"" + str_itemvalue + "\",\r\n"); //
				}
			}
		}
		_sbText.append(str_span + "}\r\n");

		if (_isLast) {
			_sbText.append(str_span + "}\r\n"); //
		} else {
			_sbText.append(str_span + "},\r\n"); //
		}
	}

	private static String findItemType(HashVO[] hvs_b, String str_tree_text) {
		for (int i = 0; i < hvs_b.length; i++) {
			if (hvs_b[i].getStringValue("itemkey").equalsIgnoreCase(str_tree_text)) {
				return hvs_b[i].getStringValue("itemtype"); //
			}
		}
		return null;
	}

	// 数据查询后的再处理,主要是处理下拉与加载公式那些把id显示成name的问题
	// 这个是在后台处理..
	public void dealAfterLoad(HashVO[] _templetVOs_b, HashVO[] _hvs_data) {
		dealComboboxViewName(_templetVOs_b, _hvs_data); // 先处理下拉框!
		execLoadFormula(_templetVOs_b, _hvs_data); // 执行加载公式
	}

	// 处理下拉框的view,因为下拉框要显示值,所以要根据配置数据重新计算!
	// 现在有个问题是,假如下拉框的定义是SQL类型，则在构造页面时已经计算出了下拉框的内容了,然后在查询时又查询了一次,这样其实性能很慢!
	// 所以一种办法是在服务器端做缓存,一种办法是从前端专门把下拉框的配置再传到服务器端
	private void dealComboboxViewName(HashVO[] _templetVOs_b, HashVO[] _hvs_data) {
		// 先把所有下拉框的配置计算出来,
		HashMap<String, HashMap<String, String>> allComboBoxDataMap = new HashMap<String, HashMap<String, String>>();
		for (int k = 0; k < _templetVOs_b.length; k++) {
			String str_itemkey = _templetVOs_b[k].getStringValue("itemkey"); //
			String str_itemtype = _templetVOs_b[k].getStringValue("itemtype"); // 类型
			String str_define = _templetVOs_b[k].getStringValue("itemdefine"); //
			if (str_itemtype.equals("下拉框") && (str_define != null && !str_define.trim().equals(""))) {
				if (cardBuilder == null) {
					cardBuilder = new BillCardHtmlJSBuilder(); //
				}
				HashMap<String, String> comBoxDataMap = cardBuilder.getComboBoxData(str_define); //
				allComboBoxDataMap.put(str_itemkey, comBoxDataMap); //
			}
		}

		// 先计算出所有下拉框的定义生成的数据,是一个Map，然后再由实际值匹配Map，得到应该显示的数据!
		if (_hvs_data != null) {
			for (int i = 0; i < _hvs_data.length; i++) {
				for (int j = 0; j < _templetVOs_b.length; j++) { // 遍历各列
					String str_itemkey = _templetVOs_b[j].getStringValue("itemkey"); //
					String str_itemtype = _templetVOs_b[j].getStringValue("itemtype"); // 类型
					if (str_itemtype.equals("下拉框")) {
						String str_itemValue = _hvs_data[i].getStringValue(str_itemkey);
						String str_viewValue = null;
						if (allComboBoxDataMap.containsKey(str_itemkey)) {
							HashMap<String, String> itemDataMap = allComboBoxDataMap.get(str_itemkey); //
							str_viewValue = itemDataMap.get(str_itemValue); // 有可能取不到,为null
						}
						if (str_viewValue == null) {
							str_viewValue = str_itemValue; // 如果没匹配上,则还是原来的值
						}
						if (str_viewValue == null) {
							str_viewValue = "";
						}
 						_hvs_data[i].setAttributeValue(str_itemkey + "_◆view◆", str_viewValue); //
					}
				}
			}
		}
	}

	private void execLoadFormula(HashVO[] _templetVOs_b, HashVO[] _hvs_data) {
		// 处理加载公式..遍历模板各子表信息,如果有加载公式,则要批量取下数!
		Map<String, String> lf_classMap = new LinkedHashMap<String, String>(); // 自定义类
		Map<String, String> lf_oherFieldMap = new LinkedHashMap<String, String>(); //
		Map<String, String[]> lf_sqlMap = new LinkedHashMap<String, String[]>(); //
		Map<String, Map<String, String>> lf_rowBgMap = new LinkedHashMap<String, Map<String, String>>(); // 行背景色

		// 遍历所有模板
		for (int i = 0; i < _templetVOs_b.length; i++) { // 遍历模板子表!
			String str_itemkey = _templetVOs_b[i].getStringValue("itemkey"); //
			String str_loadformula = _templetVOs_b[i].getStringValue("loadformula"); //
			if (str_loadformula == null || str_loadformula.trim().equals("")) {
				continue; // 如果没有定义加载公式,则跳过..
			}

			HashMap<String, String> defineMap = bsUtil.getDefineMap(str_loadformula);
			String str_type = defineMap.get("type");
			if (str_type == null || str_type.equals("") || str_type.equalsIgnoreCase("SQL")
			 || str_type.equalsIgnoreCase("SPLITSQL")) {// 新增需要拆分的sql种类
				String str_sql = defineMap.get("SQL"); //
				execLoadFormulaBySQL(str_itemkey, _hvs_data, str_sql, str_type); //添加一个type参数 2020-05-27 miaokx
			} else if (str_type.equals("CLASS")) { // 如果是Class类
				String str_className = defineMap.get("ClassName"); //
				execLoadFormulaByClass(str_itemkey, _hvs_data, str_className);
			}
		}
	}

	// tale=aaa;getName=name,id=aa,wherefield=itemname,ofthercons = ''

	// 使用SQL方式处理.
	private void execLoadFormulaBySQL(String _itemKey, HashVO[] _hvs_data, String _sql, String str_type) {
		String str_sql = _sql; //
		int li_where = str_sql.indexOf(" where "); // where的位置
		String str_sql_1 = str_sql.substring(0, li_where); // 前面的SQL,即:select rolename from xch_role
		int li_select = str_sql_1.indexOf("select "); //
		int li_from = str_sql_1.indexOf(" from "); //
		String str_getCol = str_sql_1.substring(li_select + 7, li_from).trim(); //
		String str_tableName = str_sql_1.substring(li_from + 5, str_sql_1.length()).trim(); //

		String str_sql_whereAll = str_sql.substring(li_where + 7, str_sql.length()); // where后面的整个条件,比如【
																						// code_type='CUST_TYPE' and
																						// item=${cust_type}】或【
																						// item=${cust_type} and
																						// code_type='CUST_TYPE'】
		int li_dengyu = str_sql_whereAll.indexOf("=${"); // 找出等于号的位置!
		String str_whereCol_prefix = " " + str_sql_whereAll.substring(0, li_dengyu); // 等于号前面的内容,比如【
																						// code_type='CUST_TYPE' and
																						// item】或【 item】

		// 找出真正有列名
		String str_whereCol = str_whereCol_prefix
				.substring(str_whereCol_prefix.lastIndexOf(" "), str_whereCol_prefix.length()).trim(); // 真正的列名!

		// 从本表取数据的字段,即${}里面的字段名,即本表中的某个字段名!!
		String str_whereValueCol2 = str_sql_whereAll
				.substring(str_sql_whereAll.indexOf("${") + 2, str_sql_whereAll.indexOf("}")).trim(); //

		String[] str_itemValues = bsUtil.getHashVOItemValues2(_hvs_data, str_whereValueCol2);
		
		// 拼出的in条件!!!
		String str_whereInSQL = "";
		
		//弹框多选无法回显，单独处理in条件。2020-05-27 miaokx
		//如果是购物车str_itemValues = [BOP;FAL, BOP;JSH, BOP] 这里需要把所有元素放到一个list里面
		ArrayList<String> list_itemValues = new ArrayList<String>();
		if (str_type.equalsIgnoreCase("SPLITSQL")) {
			for(int i=0; i<str_itemValues.length; i++) {
				String[] ss = str_itemValues[i].split(";");
				for(int j=0; j<ss.length; j++) {
					list_itemValues.add(ss[j]);
				}
			}
			
			str_whereInSQL = " in (" + bsUtil.getInCondition(list_itemValues) + ")"; //
		}else {
			str_whereInSQL = " in (" + bsUtil.getInCondition(str_itemValues) + ")"; //
		}

		// 替换整个Where,即把原来item=${},替换成item in
		String str_whereNewSQL = bsUtil.replaceAll(str_sql_whereAll, "=${" + str_whereValueCol2 + "}", str_whereInSQL); //

		// 最终的SQL，即把col=${itemValue}替换成col in ('','')的样子!
		String str_querySQL = "select " + str_whereCol + "," + str_getCol + " from " + str_tableName + " where "
				+ str_whereNewSQL; //

		System.out.println("◆◆◆加载公式执行的SQL【" + str_querySQL + "】"); //

		HashMap<String, String> dataMap = bsUtil.getHashMap(str_querySQL);
		if (dataMap == null) {
			dataMap = new HashMap<String, String>();
		}

		// 循环处理所有行的数据,补充数据
		for (int i = 0; i < _hvs_data.length; i++) {
			String str_itemValue = _hvs_data[i].getStringValue(str_whereValueCol2); //
			if (str_itemValue == null || str_itemValue.trim().equals("")) {
				_hvs_data[i].setAttributeValue(_itemKey, null); // 设置空值!
			} else {
				String str_mapValue = ""; //
				// 如果是SPLITSQL单独处理。需要拆分后分别从map中取值  2020-05-27 miaokx
				if (str_type.equalsIgnoreCase("SPLITSQL")) {
					String[] str_items = bsUtil.split(str_itemValue,";");
					// 组装结果
					for (String str_item : str_items) {
						str_mapValue += dataMap.get(str_item) + ";";
					}
					// 处理结果，删除最后的";"  2020-05-27 miaokx
					str_mapValue = str_mapValue.substring(0, str_mapValue.length()-1);
				} else {
					str_mapValue = dataMap.get(str_itemValue); //
				}

				if (str_mapValue == null) { // 有可能匹配不上
					_hvs_data[i].setAttributeValue(_itemKey, null); // 设置空值!
				} else {
					_hvs_data[i].setAttributeValue(_itemKey, str_mapValue); // 如果匹配上了,则加上数据
				}
			}
		}
	}

	// 使用反射类处理
	private void execLoadFormulaByClass(String _itemKey, HashVO[] _hvs_data, String _className) {
		try {
			AbstractLoadFormula loadFormula = (AbstractLoadFormula) Class.forName(_className).newInstance();
			loadFormula.dealLoadData(_hvs_data, _itemKey); //
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}
	
	// 把HashVO[]转换成JSONArray
	private JSONArray convertHashVOsToJSOArray(HashVO[] _hvs, HashVO[] templetVOs_b) {
		JSONArray rowDataArray = new JSONArray(); //
		for (int i = 0; i < _hvs.length; i++) { //
			if (_hvs[i].isVisible() == true) {
				JSONObject jso_rowdata = convertHashVOsToJSObject(_hvs[i], templetVOs_b); //
				rowDataArray.add(jso_rowdata);
			}
		}
		return rowDataArray;
	}

	// 返回对象.
	private JSONObject convertHashVOsToJSObject(HashVO _hashVO, HashVO[] templetVOs_b) {
		JSONObject jso_row = new JSONObject(); //

		// 有一些特殊的附加的字段,要补上!
		String[] str_appendCols = new String[] { "_◆view◆", "◆warncolor", "◆warnmsg", "◆warnvisible" };

		// 遍历模板中的各列!
		for (int r = 0; r < templetVOs_b.length; r++) {
			String str_itemkey = templetVOs_b[r].getStringValue("itemkey"); // 名称
			String str_formatvalidate = templetVOs_b[r].getStringValue("formatvalidate"); // 格式
			String str_value = _hashVO.getStringValue(str_itemkey, ""); // 取值
			if (str_formatvalidate != null 
					&& str_formatvalidate != "" 
					&& str_formatvalidate.indexOf("数字文本") >= 0) {
				if (str_value != null && str_value != "" && str_value.indexOf("E") >= 0) {
					BigDecimal bd = new BigDecimal(str_value);
					jso_row.put(str_itemkey, bd.toPlainString().toString());
				} else {
					jso_row.put(str_itemkey, str_value); // 加入
				}
			} else {
				jso_row.put(str_itemkey, str_value); // 加入
			}
			
			// 处理附加的字段
			for (int j = 0; j < str_appendCols.length; j++) {
				String str_itemkey2 = str_itemkey + str_appendCols[j];
				if (_hashVO.containsKeyIgnoreCasel(str_itemkey2)) {
					jso_row.put(str_itemkey2, _hashVO.getStringValue(str_itemkey2));
				}
			}
		}

		return jso_row;
	}

	// 计算出一个模板的所有字段
	public HashVO[] getOneTempletAllItems(JSONObject _par) throws Exception {
		String str_templetCode = _par.getString("templetcode"); // 模板编码

		Object[] templetVO = bsUtil2.getTempletVO(str_templetCode); //
		HashVO templetVO_1 = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] templetVO_1_item = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		HashVO[] hvs_data = new HashVO[templetVO_1_item.length];
		for (int i = 0; i < hvs_data.length; i++) {
			hvs_data[i] = new HashVO(); //
			hvs_data[i].setAttributeValue("itemkey", templetVO_1_item[i].getStringValue("itemkey"));
			hvs_data[i].setAttributeValue("itemname", templetVO_1_item[i].getStringValue("itemname"));

			if (templetVO_1_item[i].getBooleanValue("query_isshow", false)
					|| templetVO_1_item[i].getBooleanValue("query2_isshow", false)) {
				hvs_data[i].setAttributeValue("isquery", "Y");
			} else {
				hvs_data[i].setAttributeValue("isquery", "N");
			}
		}
		return hvs_data;
	}

	// 直接返回模板VO
	public JSONObject getTempletVO(JSONObject _par) throws Exception {
		String str_templetCode = _par.getString("templetcode"); // 模板编码
		Object[] templetVO = bsUtil2.getTempletVO(str_templetCode); //
		HashVO templetVO_1 = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] templetVO_1_item = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		String str_templetVO = bsUtil.getTempletOptionSelf(templetVO_1, templetVO_1_item);
		return JSONObject.parseObject(str_templetVO);
	}

	// 右键查看DB数据时,返回的Html
	public JSONObject getDBDataByHtmlTable(JSONObject _par) throws Exception {
		String str_sql = _par.getString("SQL"); //
		JSONObject jso_itenKeyNames = _par.getJSONObject("ItemKeyNames"); //

		HashVOStruct hvst = bsUtil.getHashVOStruct(str_sql); //
		String[] str_cols = hvst.getHeaderName(); // 列名
		String[] str_colTypes = hvst.getHeaderTypeName(); // 列中文名
		int[] li_precision = hvst.getPrecision(); //
		// int[] li_lens = hvst.getHeaderLength(); //
		int[] li_Scale = hvst.getScale(); //

		HashVO[] hvs_data = hvst.getHashVOs(); //

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append(str_sql + "<br>\r\n");
		sb_html.append("<table border=1 style='width:95%'>\r\n");

		String str_style = "text-align:center;background:#0FFF0F"; //
		sb_html.append("<tr><td style=\"" + str_style + "\">列名</td><td style=\"" + str_style
				+ "\">列名中文</td><td style=\"" + str_style + "\">字段类型</td><td style=\"" + str_style
				+ "\">字段宽度</td><td style=\"" + str_style + "\">字段值</td></tr>\r\n");

		for (int i = 0; i < str_cols.length; i++) {
			sb_html.append("<tr>\r\n");
			sb_html.append("<td>" + str_cols[i] + "</td>");
			String str_colName = jso_itenKeyNames.getString(str_cols[i]); //
			if (str_colName == null) {
				str_colName = ""; //
			}
			sb_html.append("<td>" + str_colName + "</td>"); // 列名中文
			sb_html.append("<td style=\"text-align:center\">" + str_colTypes[i] + "</td>"); // 字段类型
			if (li_Scale[i] == 0) {
				sb_html.append("<td style=\"text-align:center\">" + li_precision[i] + "</td>"); // 字段宽度
			} else {
				sb_html.append("<td style=\"text-align:center\">" + li_precision[i] + "," + li_Scale[i] + "</td>"); // 字段宽度
			}
			if (hvs_data != null && hvs_data.length > 0) {
				sb_html.append("<td>" + hvs_data[0].getStringValue(str_cols[i], "") + "</td>"); // 字段值
			}
			sb_html.append("</tr>\r\n");
		}
		sb_html.append("</table>\r\n");

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	// 取得数据库类型
	private String getDBType(String _ds) {
		if (CommDMO.allDatabaseHvs == null) {
			CommDMO.allDatabaseHvs = getAllHashVO();
		}

		for (int i = 0; i < CommDMO.allDatabaseHvs.length; i++) {
			if (CommDMO.allDatabaseHvs[i].getStringValue("ds_name").equals(_ds)) {
				return CommDMO.allDatabaseHvs[i].getStringValue("driver_type");
			}
		}

		return null; //
	}

	public HashVO[] getAllHashVO() {
		String str_sql = "select t1.ds_name,t2.driver_type from bione_ds_info t1,bione_driver_info t2 where t1.driver_id=t2.driver_id";
		HashVO[] hvs = bsUtil.getHashVOs(str_sql); //
		return hvs; //
	}

	/**
	 * 获取数据库中某字段长度，进行校验
	 * @param _par
	 * @return
	 */
	public JSONObject getColumLengthByDS(JSONObject _par) {
		String str_tab_name_en = _par.getString("tab_name_en");
		
		Map<String,String> columLengths = getTableInfo(str_tab_name_en.toUpperCase());
		
		JSONObject jso_rt = new JSONObject();
		jso_rt.put("msg", "OK");
		jso_rt.put("columLengths", columLengths);
		return jso_rt;
	}
	
	public Map<String,String> getTableInfo(String tab_name_en) {
		Map<String,String> result = new HashMap<String,String>();

		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			rs = conn.getMetaData().getColumns(null, getSchema(conn), tab_name_en, "%");
				
			while (rs.next()) {
				result.put(rs.getString("COLUMN_NAME"), rs.getString("COLUMN_SIZE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	private Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
		Properties props = new Properties() ;
		
		props.load(this.getClass().getClassLoader().getResourceAsStream("database.properties")) ;
		props.put("remarksReporting", "true"); // 注意这里
		props.put("user", props.getProperty("jdbc.username"));
		props.put("password", props.getProperty("jdbc.password"));
		Class.forName(props.getProperty("jdbc.driverClassName"));
		Connection dbConn = DriverManager.getConnection(props.getProperty("jdbc.url"), props);

		return dbConn;
	}
	
	private static String getSchema(Connection conn) throws Exception {
		String schema;
		schema = conn.getMetaData().getUserName();
		if ((schema == null) || (schema.length() == 0)) {
			throw new Exception("ORACLE数据库模式不允许为空");
		}
		return schema.toUpperCase().toString();

	}
}

//树的目录分类结点
class BillTreeGroupVO implements java.io.Serializable {

	private String colName; // 列表
	private String colValue; // 列有值

	public BillTreeGroupVO(String _colName, String _colValue) {
		this.colName = _colName;
		this.colValue = _colValue;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColValue() {
		return colValue;
	}

	public void setColValue(String colValue) {
		this.colValue = colValue;
	}

	@Override
	public String toString() {
		return colValue;
	}

}