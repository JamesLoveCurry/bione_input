package com.yusys.bione.plugin.yuformat.xml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.yusys.bione.plugin.yuformat.utils.InsertSQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

/**
 * 客户风险
 * @author xch
 *
 */
public class CRRSRuleXmlDeal {

	public CRRSRuleXmlDeal() {

	}

	public void test() {
		try {
			Document doc = readXml(new FileInputStream("E:/012/rule.xml")); //
			List<Element> list = doc.getRootElement().elements();

			LinkedHashMap<String, LinkedHashSet<String>> map = new LinkedHashMap<String, LinkedHashSet<String>>(); //
			int li_count = 0; //
			for (int i = 0; i < list.size(); i++) {
				Element ele = list.get(i); //
				String str_nodename = ele.getName().toLowerCase(); //
				String str_id = ele.attributeValue("id");
				String str_name = ele.attributeValue("name");
				String str_tablename = ele.attributeValue("tablename");
				List<Element> ruleList = ele.elements(); //再次查询所有子结点,即规则
				for (int j = 0; j < ruleList.size(); j++) { //规则清单...
					Element ruleEle = ruleList.get(j); //
					List<Attribute> attrList = ruleEle.attributes(); //
					for (int k = 0; k < attrList.size(); k++) {
						Attribute attr = attrList.get(k); //
						String str_attrName = attr.getName(); //

						if (map.containsKey(str_nodename)) {
							LinkedHashSet set = map.get(str_nodename); //
							set.add(str_attrName); //
						} else {
							LinkedHashSet<String> set = new LinkedHashSet<String>(); //
							set.add(str_attrName); //
							map.put(str_nodename, set); //
						}
					}
				}

				//System.out.println(str_nodename + "[id=" + str_id + "][name=" + str_name + "][tablename=" + str_tablename + "][规则数量=" + ruleList.size() + "]"); //
				li_count = li_count + ruleList.size(); //
			}

			//<table name="crrs_bs_area_code" pkname="rid" descr="CRRS_BS_AREA_CODE">
			//<columns>

			StringBuilder sb_sql = new StringBuilder(); //
			StringBuilder sb_xml = new StringBuilder(); //
			//System.err.println(map);
			String[] str_keys = map.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				String str_tableName = "crrs_rule_" + str_keys[i]; //表名
				sb_xml.append("<table name=\"" + str_tableName + "\" pkname=\"rid\" descr=\"" + str_tableName + "\">\r\n"); //
				sb_xml.append("<columns>\r\n");
				sb_xml.append("  <col name=\"rid\"		type=\"varchar\"   length=\"40\"   descr=\"rid\"/>\r\n");

				sb_sql.append("create table  " + str_tableName + " (\r\n"); //
				sb_sql.append("  rid varchar2(40),\r\n");
				LinkedHashSet<String> set = map.get(str_keys[i]);
				String[] str_cols = set.toArray(new String[0]); //
				for (int j = 0; j < str_cols.length; j++) {
					sb_sql.append("  " + str_cols[j] + " varchar2(50),\r\n");
					sb_xml.append("  <col name=\"" + str_cols[j] + "\"		type=\"varchar\"   length=\"200\"   descr=\"" + str_cols[j] + "\"/>\r\n");
				}

				sb_sql.append("  constraint pk_" + str_tableName + " primary key (rid)\r\n");
				sb_sql.append(");\r\n\r\n");

				sb_xml.append("</columns>\r\n");
				sb_xml.append("</table>\r\n\r\n");
			}

			System.out.println(sb_xml.toString()); //
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}

	//基本类校验...
	public void createInsertSQL_R1_6() {
		try {
			StringBuilder sb_sql = new StringBuilder(); //
			Document doc = readXml(new FileInputStream("E:/012/yjhrule.xml")); //
			List<Element> list = doc.getRootElement().elements();
			for (int i = 0; i < list.size(); i++) {
				Element reportEle = list.get(i); //
				String str_id = reportEle.attributeValue("id"); //
				if (str_id.startsWith("R")) {
					//System.out.println(str_id); //
					String str_sql = getReportSQL("crrs_rule_sure", "基本类校验", reportEle); //
					sb_sql.append(str_sql); //加入SQL
				}
			}

			new YuFormatUtil2().writeStrToOutputStream(new FileOutputStream("E:/012/R1_6.sql"), sb_sql.toString(), "GBK"); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
		}
	}

	//完整性
	private void createInsertSQL_wzx() {
		try {
			StringBuilder sb_sql = new StringBuilder(); //
			Document doc = readXml(new FileInputStream("E:/012/yjhrule.xml")); //
			List<Element> list = doc.getRootElement().elements();
			for (int i = 0; i < list.size(); i++) {
				Element reportEle = list.get(i); //
				String str_id = reportEle.attributeValue("id"); //
				if (str_id.startsWith("wzx")) {
					String str_sql = getReportSQL2("crrs_rule_sure", "完整性校验", reportEle); //
					sb_sql.append(str_sql); //加入SQL
				}
			}

			new YuFormatUtil2().writeStrToOutputStream(new FileOutputStream("E:/012/wzx完整性.sql"), sb_sql.toString(), "GBK"); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
		}
	}

	//唯一性
	private void createInsertSQL_wyx() {
		try {
			StringBuilder sb_sql = new StringBuilder(); //
			Document doc = readXml(new FileInputStream("E:/012/yjhrule.xml")); //
			List<Element> list = doc.getRootElement().elements();
			for (int i = 0; i < list.size(); i++) {
				Element reportEle = list.get(i); //
				String str_id = reportEle.attributeValue("id"); //
				if (str_id.startsWith("wyx")) {
					String str_sql = getReportSQL2("crrs_rule_sure", "唯一性校验", reportEle); //
					sb_sql.append(str_sql); //加入SQL
				}
			}

			new YuFormatUtil2().writeStrToOutputStream(new FileOutputStream("E:/012/wyx唯一性.sql"), sb_sql.toString(), "GBK"); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
		}
	}

	//特殊类tsx
	private void createInsertSQL_tsx() {
		try {
			StringBuilder sb_sql = new StringBuilder(); //
			Document doc = readXml(new FileInputStream("E:/012/yjhrule.xml")); //
			List<Element> list = doc.getRootElement().elements();
			for (int i = 0; i < list.size(); i++) {
				Element reportEle = list.get(i); //
				String str_id = reportEle.attributeValue("id"); //
				if (str_id.startsWith("tsx")) {
					String str_sql = getReportSQL2("crrs_rule_sure", "特殊类校验", reportEle); //
					sb_sql.append(str_sql); //加入SQL
				}
			}

			new YuFormatUtil2().writeStrToOutputStream(new FileOutputStream("E:/012/tsx特殊类.sql"), sb_sql.toString(), "GBK"); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
		}
	}

	//各种预警(提示)校验
	private void createInsertSQL_warnAll(String _id, String _text) {
		try {
			StringBuilder sb_sql = new StringBuilder(); //
			Document doc = readXml(new FileInputStream("E:/012/yjhrule.xml")); //
			List<Element> list = doc.getRootElement().elements();
			for (int i = 0; i < list.size(); i++) {
				Element reportEle = list.get(i); //
				String str_id = reportEle.attributeValue("id"); //
				if (str_id.startsWith(_id)) { //预警校验...
					String str_sql = getReportSQL2("crrs_rule_warn", _text, reportEle); //提示性校验
					sb_sql.append(str_sql); //加入SQL
				}
			}

			new YuFormatUtil2().writeStrToOutputStream(new FileOutputStream("E:/012/" + _id + "_" + _text + ".sql"), sb_sql.toString(), "GBK"); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
		}
	}

	//一致性校验..
	private void createInsertSQL_yzx(String _id, String _text) {
		try {
			StringBuilder sb_sql = new StringBuilder(); //
			Document doc = readXml(new FileInputStream("E:/012/yjhrule.xml")); //
			List<Element> list = doc.getRootElement().elements();
			for (int i = 0; i < list.size(); i++) {
				Element reportEle = list.get(i); //
				String str_id = reportEle.attributeValue("id"); //
				if (str_id.startsWith(_id)) { //预警校验...
					String str_sql = getReportSQL2("crrs_rule_consistency", _text, reportEle); //提示性校验
					sb_sql.append(str_sql); //加入SQL
				}
			}

			new YuFormatUtil2().writeStrToOutputStream(new FileOutputStream("E:/012/" + _id + "_" + _text + ".sql"), sb_sql.toString(), "GBK"); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
		}
	}

	//code,
	private String getReportSQL(String _tableName, String _ruleType, Element _reportEle) {
		//映射的列
		String[][] str_cols = new String[][] { { "colname", "colname" }, { "lable", "lable" }, { "problemcode", "problemcode" }, { "method", "execmethod" }, //
				{ "param1", "param1" }, { "param2", "param2" }, { "msg", "msg" }, { "verifytype", "verifytype" }, { "level", "rulelevel" }, { "open", "isopen" }, { "flagone", "flagone" } // 
		};

		String str_idCode = _reportEle.attributeValue("id"); //编号
		String str_tableName = _reportEle.attributeValue("tablename"); //表名!

		StringBuilder sb_sql = new StringBuilder(); //
		//xml国的字段,colname, lable, problemcode, method, param1,param2, msg, verifytype, level, open,  flagone
		//数据库中有字段:rid,code,ruletype,tablename,colname,lable,problemcode,execmethod,param1,param2,msg,flagone,verifytype,rulelevel,isopen
		List<Element> list = _reportEle.elements(); //取得所有规则
		for (int i = 0; i < list.size(); i++) {
			InsertSQLBuilder isql = new InsertSQLBuilder(_tableName); //
			isql.putFieldValue("rid", UUID.randomUUID().toString()); //
			isql.putFieldValue("code", str_idCode); //编号
			isql.putFieldValue("tablename", str_tableName); //表名
			isql.putFieldValue("ruletype", _ruleType); //类型

			Element ruleEle = list.get(i); //
			for (int j = 0; j < str_cols.length; j++) {
				String str_value = ruleEle.attributeValue(str_cols[j][0]); //
				if (str_value != null) {
					isql.putFieldValue(str_cols[j][1], str_value); //设置值
				}
			}

			String str_sql = isql.getSQL() + ";"; //
			sb_sql.append(str_sql + "\r\n"); //
			System.out.println(str_sql); //
		}
		return sb_sql.toString(); //
	}

	private String getReportSQL2(String _tableName, String _ruleType, Element _reportEle) {
		//映射的列
		String[][] str_cols = new String[][] { { "tablename", "tablename" }, { "colname", "colname" }, { "lable", "lable" }, { "problemcode", "problemcode" }, { "method", "execmethod" }, //
				{ "param1", "param1" }, { "param2", "param2" }, { "msg", "msg" }, { "verifytype", "verifytype" }, { "level", "rulelevel" }, { "open", "isopen" }, { "flagone", "flagone" } // 
		};

		String str_idCode = _reportEle.attributeValue("id"); //编号
		String str_tableName = _reportEle.attributeValue("tablename"); //表名!

		StringBuilder sb_sql = new StringBuilder(); //
		//xml国的字段,colname, lable, problemcode, method, param1,param2, msg, verifytype, level, open,  flagone
		//数据库中有字段:rid,code,ruletype,tablename,colname,lable,problemcode,execmethod,param1,param2,msg,flagone,verifytype,rulelevel,isopen
		List<Element> list = _reportEle.elements(); //取得所有规则
		for (int i = 0; i < list.size(); i++) {
			InsertSQLBuilder isql = new InsertSQLBuilder(_tableName); //
			isql.putFieldValue("rid", UUID.randomUUID().toString()); //
			isql.putFieldValue("code", str_idCode); //编号
			isql.putFieldValue("tablename", str_tableName); //表名
			isql.putFieldValue("ruletype", _ruleType); //类型

			Element ruleEle = list.get(i); //
			for (int j = 0; j < str_cols.length; j++) {
				String str_value = ruleEle.attributeValue(str_cols[j][0]); //
				if (str_value != null) {
					isql.putFieldValue(str_cols[j][1], str_value); //设置值
				}
			}

			String str_sql = isql.getSQL() + ";"; //
			sb_sql.append(str_sql + "\r\n"); //
			System.out.println(str_sql); //
		}
		return sb_sql.toString(); //
	}

	//确定性校验结果
	public void getInsertCrrs_Result_Sure() {
		SecureRandom random = new SecureRandom(); //

		String[] str_date = new String[] { "2019-06-11", "2019-06-12", "2019-06-13" };
		String[] str_ruletype = new String[] { "唯一性校验", "基本类校验", "完整性校验", "特殊类校验" };
		String[] str_table = new String[] { "crrs_group_group_client", "crrs_group_actualcontroller", "crrs_group_executives", "crrs_group_members" };
		String[] str_cols = new String[] { "customer_code", "customer_name", "group_code", "member_type", "credit_bank_name", "balance_of_limit" };

		String[] str_customerCode = new String[] { "10000012380764", "10000008032015", "10000008231093", "10000004150444" };
		String[] str_customerName = new String[] { "安福县茂源粮油某某", "库尔勒金丰利某某", "苏州艾科尔化某某", "宝鸡市华海某某", "河南金信电力某某", "泉州路路通汽某某", "宁夏君星坊食某某", "哈尔滨今康源粮某某", "汕头市金株新某某" };

		String[] str_problemcode = new String[] { "JBL6-015", "JBL6-018", "WZX-002", "WZX-003", "WYX-024", "JBL1-010", "LJX-025", "JBL1-046", "JBL1-047", "LJX-007", "LJX-021" };
		String[] str_msg = new String[] { "除[授信号码]为[WNRSXYW]的,非空", "长度为8位字符,为日期型", "表1-PARTⅠ中,[授信到期日期]大于[授信起始日期]", "大于等于0,保留2位小数", "若[客户类型]为[同业客户],则为空或0" };

		String[] str_inst_code = new String[] { "1001", "1002", "1003" };
		String[] str_inst_name = new String[] { "北京分行", "上海分行", "广州分行" };

		String[] str_staff_code = new String[] { "25006", "26008", "29102" }; //
		String[] str_staff_name = new String[] { "李小萍", "张华", "王建国" }; //
		String[] str_busitype = new String[] { "个人业务", "对公业务" };

		String[] str_status = new String[] { "未发布", "已发布", "已分发", "已处理" };

		for (int i = 0; i < 100; i++) { //插入100条数据
			InsertSQLBuilder isql = new InsertSQLBuilder("crrs_result_sure"); //
			isql.putFieldValue("rid", UUID.randomUUID().toString()); //
			isql.putFieldValue("reportdate", str_date[random.nextInt(str_date.length)]);
			isql.putFieldValue("rulerid", UUID.randomUUID().toString());
			isql.putFieldValue("rulecode", "");
			isql.putFieldValue("ruletype", str_ruletype[random.nextInt(str_ruletype.length)]);
			isql.putFieldValue("customercode", str_customerCode[random.nextInt(str_customerCode.length)]);
			isql.putFieldValue("customername", str_customerName[random.nextInt(str_customerName.length)]);
			isql.putFieldValue("flag1", "18350000621812250000200");
			isql.putFieldValue("flag2", "YP350000621812250001");
			isql.putFieldValue("flag3", "0235000062181225002");
			isql.putFieldValue("tablename", str_table[random.nextInt(str_table.length)]);
			isql.putFieldValue("colname", str_cols[random.nextInt(str_cols.length)]);
			isql.putFieldValue("colvalue", "");
			isql.putFieldValue("pkcolname", "rid");
			isql.putFieldValue("pkvalue", "");
			isql.putFieldValue("problemcode", str_problemcode[random.nextInt(str_problemcode.length)]);
			isql.putFieldValue("problemmsg", str_msg[random.nextInt(str_msg.length)]);
			isql.putFieldValue("relbankcode", "");
			isql.putFieldValue("relbankname", "");
			isql.putFieldValue("inst_code", str_inst_code[random.nextInt(str_inst_code.length)]);
			isql.putFieldValue("inst_name", str_inst_name[random.nextInt(str_inst_name.length)]);
			isql.putFieldValue("staff_code", str_staff_code[random.nextInt(str_staff_code.length)]);
			isql.putFieldValue("staff_name", str_staff_name[random.nextInt(str_staff_name.length)]);
			isql.putFieldValue("business_type", str_busitype[random.nextInt(str_busitype.length)]);
			isql.putFieldValue("result_status", str_status[random.nextInt(str_status.length)]);

			System.out.println(isql.getSQL() + ";"); //
		}
	}

	//提示性校验结果
	public void getInsertCrrs_Result_warn() {
		SecureRandom random = new SecureRandom(); //

		String[] str_date = new String[] { "2019-06-11", "2019-06-12", "2019-06-13" };
		String[] str_ruletype = new String[] { "唯一性校验", "基本类校验", "完整性校验", "特殊类校验" };
		String[] str_table = new String[] { "crrs_group_group_client", "crrs_group_actualcontroller", "crrs_group_executives", "crrs_group_members" };
		String[] str_cols = new String[] { "customer_code", "customer_name", "group_code", "member_type", "credit_bank_name", "balance_of_limit" };

		String[] str_customerCode = new String[] { "10000012380764", "10000008032015", "10000008231093", "10000004150444" };
		String[] str_customerName = new String[] { "安福县茂源粮油某某", "库尔勒金丰利某某", "苏州艾科尔化某某", "宝鸡市华海某某", "河南金信电力某某", "泉州路路通汽某某", "宁夏君星坊食某某", "哈尔滨今康源粮某某", "汕头市金株新某某" };

		String[] str_problemcode = new String[] { "JBL6-015", "JBL6-018", "WZX-002", "WZX-003", "WYX-024", "JBL1-010", "LJX-025", "JBL1-046", "JBL1-047", "LJX-007", "LJX-021" };
		String[] str_msg = new String[] { "除[授信号码]为[WNRSXYW]的,非空", "长度为8位字符,为日期型", "表1-PARTⅠ中,[授信到期日期]大于[授信起始日期]", "大于等于0,保留2位小数", "若[客户类型]为[同业客户],则为空或0" };

		String[] str_inst_code = new String[] { "1001", "1002", "1003" };
		String[] str_inst_name = new String[] { "北京分行", "上海分行", "广州分行" };

		String[] str_staff_code = new String[] { "25006", "26008", "29102" }; //
		String[] str_staff_name = new String[] { "李小萍", "张华", "王建国" }; //
		String[] str_busitype = new String[] { "个人业务", "对公业务" };

		String[] str_status = new String[] { "未发布", "已发布", "已分发", "已处理" };

		for (int i = 0; i < 120; i++) { //插入100条数据
			InsertSQLBuilder isql = new InsertSQLBuilder("crrs_result_warn"); //
			isql.putFieldValue("rid", UUID.randomUUID().toString()); //
			isql.putFieldValue("reportdate", str_date[random.nextInt(str_date.length)]);
			isql.putFieldValue("rulerid", UUID.randomUUID().toString());
			isql.putFieldValue("rulecode", "");
			isql.putFieldValue("ruletype", str_ruletype[random.nextInt(str_ruletype.length)]);

			isql.putFieldValue("varify_num", "1"); //校验次数!

			isql.putFieldValue("customercode", str_customerCode[random.nextInt(str_customerCode.length)]);
			isql.putFieldValue("customername", str_customerName[random.nextInt(str_customerName.length)]);
			isql.putFieldValue("flag1", "18350000621812250000200");
			isql.putFieldValue("flag2", "YP350000621812250001");
			isql.putFieldValue("flag3", "0235000062181225002");
			isql.putFieldValue("tablename", str_table[random.nextInt(str_table.length)]);
			isql.putFieldValue("colname", str_cols[random.nextInt(str_cols.length)]);
			isql.putFieldValue("colvalue", "");
			isql.putFieldValue("pkcolname", "rid");
			isql.putFieldValue("pkvalue", "");
			isql.putFieldValue("problemcode", str_problemcode[random.nextInt(str_problemcode.length)]);
			isql.putFieldValue("problemmsg", str_msg[random.nextInt(str_msg.length)]);

			isql.putFieldValue("isremark", "Y");
			isql.putFieldValue("bank_remark", "请银监会复审");

			isql.putFieldValue("submit_bankcode", "");  //报送机构编码
			isql.putFieldValue("submit_bankname", "");  //报送机构名称
			isql.putFieldValue("submit_adminbankcode", ""); //报送机构的监管机构

			isql.putFieldValue("check_bankcode", ""); //核实举证银行代码
			isql.putFieldValue("check_bankname", ""); //
			isql.putFieldValue("check_adminbankcode", ""); //

			isql.putFieldValue("isdoubtdata", "Y"); //是否可疑数据
			isql.putFieldValue("cbrc_remark", "银监会备注"); //

			isql.putFieldValue("bank_checkresult", "银行检查结果"); //
			isql.putFieldValue("bank_datamodify", "银行数据修改"); //
			isql.putFieldValue("data_changeexplain", "数据修改说明"); //

			isql.putFieldValue("cbrc_suggestion", "银监会建议"); //

			isql.putFieldValue("inst_code", str_inst_code[random.nextInt(str_inst_code.length)]);
			isql.putFieldValue("inst_name", str_inst_name[random.nextInt(str_inst_name.length)]);
			isql.putFieldValue("staff_code", str_staff_code[random.nextInt(str_staff_code.length)]);
			isql.putFieldValue("staff_name", str_staff_name[random.nextInt(str_staff_name.length)]);
			isql.putFieldValue("business_type", str_busitype[random.nextInt(str_busitype.length)]);
			isql.putFieldValue("result_status", str_status[random.nextInt(str_status.length)]);

			System.out.println(isql.getSQL() + ";"); //
		}
	}

	//一致性校验结果!!
	public void getInsertCrrs_Result_consistency() {
		SecureRandom random = new SecureRandom(); //

		String[] str_date = new String[] { "2019-06-11", "2019-06-12", "2019-06-13" };
		String[] str_ruletype = new String[] { "唯一性校验", "基本类校验", "完整性校验", "特殊类校验" };
		String[] str_table = new String[] { "crrs_group_group_client", "crrs_group_actualcontroller", "crrs_group_executives", "crrs_group_members" };
		String[] str_cols = new String[] { "customer_code", "customer_name", "group_code", "member_type", "credit_bank_name", "balance_of_limit" };

		String[] str_customerCode = new String[] { "10000012380764", "10000008032015", "10000008231093", "10000004150444" };
		String[] str_customerName = new String[] { "安福县茂源粮油某某", "库尔勒金丰利某某", "苏州艾科尔化某某", "宝鸡市华海某某", "河南金信电力某某", "泉州路路通汽某某", "宁夏君星坊食某某", "哈尔滨今康源粮某某", "汕头市金株新某某" };

		String[] str_problemcode = new String[] { "JBL6-015", "JBL6-018", "WZX-002", "WZX-003", "WYX-024", "JBL1-010", "LJX-025", "JBL1-046", "JBL1-047", "LJX-007", "LJX-021" };
		String[] str_msg = new String[] { "除[授信号码]为[WNRSXYW]的,非空", "长度为8位字符,为日期型", "表1-PARTⅠ中,[授信到期日期]大于[授信起始日期]", "大于等于0,保留2位小数", "若[客户类型]为[同业客户],则为空或0" };

		String[] str_inst_code = new String[] { "1001", "1002", "1003" };
		String[] str_inst_name = new String[] { "北京分行", "上海分行", "广州分行" };

		String[] str_staff_code = new String[] { "25006", "26008", "29102" }; //
		String[] str_staff_name = new String[] { "李小萍", "张华", "王建国" }; //
		String[] str_busitype = new String[] { "个人业务", "对公业务" };

		String[] str_status = new String[] { "未发布", "已发布", "已分发", "已处理" };

		for (int i = 0; i < 180; i++) { //插入100条数据
			InsertSQLBuilder isql = new InsertSQLBuilder("crrs_result_consistency"); //
			isql.putFieldValue("rid", UUID.randomUUID().toString()); //
			isql.putFieldValue("reportdate", str_date[random.nextInt(str_date.length)]);
			isql.putFieldValue("rulerid", UUID.randomUUID().toString());
			isql.putFieldValue("rulecode", "");
			isql.putFieldValue("ruletype", str_ruletype[random.nextInt(str_ruletype.length)]);

			isql.putFieldValue("varify_num", "1"); //校验次数!

			isql.putFieldValue("customercode", str_customerCode[random.nextInt(str_customerCode.length)]);
			isql.putFieldValue("customername", str_customerName[random.nextInt(str_customerName.length)]);
			isql.putFieldValue("flag1", "18350000621812250000200");
			isql.putFieldValue("flag2", "YP350000621812250001");
			isql.putFieldValue("flag3", "0235000062181225002");
			isql.putFieldValue("tablename", str_table[random.nextInt(str_table.length)]);
			isql.putFieldValue("colname", str_cols[random.nextInt(str_cols.length)]);
			isql.putFieldValue("colvalue", "");
			isql.putFieldValue("pkcolname", "rid");
			isql.putFieldValue("pkvalue", "");
			isql.putFieldValue("problemcode", str_problemcode[random.nextInt(str_problemcode.length)]);
			isql.putFieldValue("problemmsg", str_msg[random.nextInt(str_msg.length)]);

			isql.putFieldValue("same_value", "相同值");
			isql.putFieldValue("lastdt_value", "上期最后值");

			isql.putFieldValue("relbankcode", "000999222"); //相关银行机构编码
			isql.putFieldValue("relbankname", "XX银行"); //相关银行机构名称

			isql.putFieldValue("submit_bankcode", "000999222"); //报送机构编码
			isql.putFieldValue("submit_bankname", "XX银行"); //报送机构名称
			isql.putFieldValue("submit_adminbankcode", "报送机构管辖机构代码"); //

			isql.putFieldValue("check_bankcode", "核实举证银行代码"); //
			isql.putFieldValue("check_bankname", "核实举证银行机构名称"); //
			isql.putFieldValue("check_adminbankcode", "核实举证银行机构的监管机构代码"); //

			isql.putFieldValue("bank_checkresult", "银行检查结果"); //
			isql.putFieldValue("bank_datamodify", "银行数据修改"); //
			isql.putFieldValue("data_changeexplain", "数据修改说明"); //

			isql.putFieldValue("cbrc_suggestion", "监管机构审核意见"); //

			isql.putFieldValue("inst_code", str_inst_code[random.nextInt(str_inst_code.length)]);
			isql.putFieldValue("inst_name", str_inst_name[random.nextInt(str_inst_name.length)]);
			isql.putFieldValue("staff_code", str_staff_code[random.nextInt(str_staff_code.length)]);
			isql.putFieldValue("staff_name", str_staff_name[random.nextInt(str_staff_name.length)]);
			isql.putFieldValue("business_type", str_busitype[random.nextInt(str_busitype.length)]);
			isql.putFieldValue("result_status", str_status[random.nextInt(str_status.length)]);

			System.out.println(isql.getSQL() + ";"); //
		}
	}

	//读文件!
	private Document readXml(InputStream _ins) {
		if (_ins == null) {
			return null;
		}
		try {
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document doc = reader.read(_ins);
			return doc; //
		} catch (Exception _ex) {
			_ex.printStackTrace();
			return null;
		} finally {
			try {
				if (_ins != null) {
					_ins.close(); //
				}
			} catch (Exception _exx) {
				_exx.printStackTrace(); //
			}
		}
	}
}
