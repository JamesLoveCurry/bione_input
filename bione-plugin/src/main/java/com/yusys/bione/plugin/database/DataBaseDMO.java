package com.yusys.bione.plugin.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.HashVOStruct;
import com.yusys.bione.plugin.yuformat.utils.InsertSQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

/**
 * 数据库处理
 * @author xch
 *
 */
public class DataBaseDMO {

	private YuFormatUtil bsUtil = new YuFormatUtil();
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2();

	//查询所有需要删除的表
	public JSONObject onQueryDropTable(JSONObject _jso) throws Exception {
		//先找出数据库中的所有表!
		String[][] str_DBTables = bsUtil.getAllSysTables(); //数据库中所有的表!

		//先找索引xml，再找所有xml
		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/1_filelist.xml"));
		ArrayList<String> allXmlTabNameList = new ArrayList<String>(); //
		List<Element> list = doc.getRootElement().elements("xmlfile");
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_xmlfileName = ele.attributeValue("tabxml"); //xml文件名!
			Document doc_item = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_xmlfileName));
			if (doc_item == null) {
				continue;
			}
			List<Element> tabList = doc_item.getRootElement().elements("table"); //
			for (int j = 0; j < tabList.size(); j++) {
				Element tabEle = tabList.get(j); //
				String str_tabName = tabEle.attributeValue("name", "").toLowerCase(); //
				allXmlTabNameList.add(str_tabName); //
			}
		}

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("/**所有在数据库中有,但xml中没有定义的表,请谨慎执行这些SQL*/<br>\r\n");
		for (int i = 0; i < str_DBTables.length; i++) {
			if (!allXmlTabNameList.contains(str_DBTables[i][0])) {
				sb_html.append("drop table " + str_DBTables[i][0] + ";<br>\r\n");
			}
		}

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	//取得所有文件清单
	public JSONObject getXmlFileList(JSONObject _jso) throws Exception {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<table border=1 style='width:60%'>"); //
		sb_html.append("<tr><td style=\"text-align:center\">说明</td><td style=\"text-align:center\">表结构xml文件</td><td style=\"text-align:center\">视图xml文件</td>");
		sb_html.append("<td style=\"text-align:center\">初始化与升级的XML数据</td><td style=\"text-align:center\">首次安装数据</td><td style=\"text-align:center\">升级更新数据</td></tr>"); //

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/1_filelist.xml"));

		List<Element> list = doc.getRootElement().elements("xmlfile"); //找到结点
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_descr = ele.getText(); //说明
			String str_tabxml = ele.attributeValue("tabxml"); //表结构的XML文件
			String str_viewxml = ele.attributeValue("viewxml"); //视图的XML文件
			str_viewxml = str_viewxml == null ? "" : str_viewxml; //

			String str_tabxml_prefix = str_tabxml.substring(0, str_tabxml.indexOf(".")); //
			String str_initdataxml = str_tabxml_prefix + "_data.xml"; //初始化数据
			String str_initdataxml_1 = str_tabxml_prefix + "_data_1.xml";
			String str_initdataxml_2 = str_tabxml_prefix + "_data_2.xml";

			if (this.getClass().getResource("/bione-plugin/database/" + str_initdataxml) == null) {
				str_initdataxml = "";
			}

			if (this.getClass().getResource("/bione-plugin/database/" + str_initdataxml_1) == null) {
				str_initdataxml_1 = "";
			}

			if (this.getClass().getResource("/bione-plugin/database/" + str_initdataxml_2) == null) {
				str_initdataxml_2 = "";
			}

			sb_html.append("<tr><td>" + str_descr + "</td>");
			sb_html.append("<td><a href=\"JavaScript:onLookTabHtml('" + str_tabxml + "');\">" + str_tabxml + "</a></td>");
			sb_html.append("<td><a href=\"JavaScript:onLookViewHtml('" + str_viewxml + "');\">" + str_viewxml + "</a></td>");
			sb_html.append("<td><a href=\"JavaScript:onLookInitDatawHtml('" + str_initdataxml + "','0');\">" + str_initdataxml + "</a></td>"); //表结构
			sb_html.append("<td><a href=\"JavaScript:onLookInitDatawHtml('" + str_initdataxml_1 + "','1');\">" + str_initdataxml_1 + "</a></td>"); //首次安装数据
			sb_html.append("<td><a href=\"JavaScript:onLookInitDatawHtml('" + str_initdataxml_2 + "','2');\">" + str_initdataxml_2 + "</a></td>"); //升级更新数据
			sb_html.append("</tr>"); //
		}

		sb_html.append("</table>"); //

		//sb_html.append("<a href=\"JavaScript:onExportAllDBDataToHtml();\">导出DB数据为Html</a><br>\r\n");

		sb_html.append("<p style=\"color:red\">\r\n");
		sb_html.append("升级说明(即比较表结构并更新、重建视图、升级数据，共三件事)：<br>\r\n");
		sb_html.append("1.点击【1.一键比较表结构】根据生成的SQL,人工判断并执行之,一般来说基本上都应该执行。<br>\r\n");
		sb_html.append("2.点击【2.一键重建所有视图】直接执行即可!有错误检查一下。<br>\r\n");
		sb_html.append("3.点击【3.一键导出数据升级SQL】导出SQL,然后执行,根据实际情况有的时候并一定必须做。<br>\r\n");

		sb_html.append("<br>\r\n");
		sb_html.append("首次安装(即建表、导入初始数据、建视图，共三件事)：<br>\r\n");
		sb_html.append("1：点击【A.导出所有建表SQL】,导出所有建表SQL语句,然后执行<br>\r\n");
		sb_html.append("2：点击【B.导出首次安装数据SQL】,导出所有初始化数据SQL,然后执行<br>\r\n");
		sb_html.append("3：点击【C.导出所有视图SQL】,导出所有视图定义SQL,然后执行<br>\r\n");
		sb_html.append("</p>\r\n");

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	//建表SQL
	public JSONObject buildCreateTableSQL(JSONObject _jso) throws Exception {
		String str_xmlFile = _jso.getString("xmlFile"); //xml文件
		String str_tableName = _jso.getString("tableName"); //表名
		String str_dbtype = _jso.getString("dbtype"); //数据库类型

		JSONObject jso_rt = new JSONObject(); //

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_xmlFile));
		if (doc == null) {
			jso_rt.put("html", "文件【" + str_xmlFile + "】不存在!"); //
			return jso_rt;
		}

		//找到这个表
		Element thisTabEle = null; //
		List<Element> list = doc.getRootElement().elements("table"); //
		for (int i = 0; i < list.size(); i++) {
			Element tabEle = list.get(i); //
			String str_tname = tabEle.attributeValue("name"); //
			if (str_tname.equalsIgnoreCase(str_tableName)) {
				thisTabEle = tabEle; //
				break; //
			}
		}

		//这个表的SQL!
		String str_sql = getCreateTableSQL(thisTabEle, str_dbtype); //
		jso_rt.put("html", str_sql); //
		return jso_rt;
	}

	//建表SQL
	public JSONObject buildAllCreateTableSQL(JSONObject _jso) throws Exception {
		String str_xmlFile = _jso.getString("xmlFile"); //xml文件
		String str_dbtype = _jso.getString("dbtype"); //数据库类型

		JSONObject jso_rt = new JSONObject(); //

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_xmlFile));
		if (doc == null) {
			jso_rt.put("html", "文件【" + str_xmlFile + "】不存在!"); //
			return jso_rt;
		}

		//找到这个表
		StringBuilder sb_html = new StringBuilder(); //
		List<Element> list = doc.getRootElement().elements("table"); //
		for (int i = 0; i < list.size(); i++) {
			Element tabEle = list.get(i); //
			String str_sql = getCreateTableSQL(tabEle, str_dbtype); //
			sb_html.append(str_sql); //
			sb_html.append("<br>\r\n");
		}

		//这个表的SQL!

		jso_rt.put("html", sb_html.toString()); //
		return jso_rt;
	}

	//得到某一个xml的内容!
	public JSONObject getOneXmlTable(JSONObject _jso) throws Exception {
		long ll_1 = System.currentTimeMillis();
		JSONObject jso_rt = new JSONObject(); //
		String str_file = _jso.getString("xmlfile"); //

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_file));
		if (doc == null) {
			jso_rt.put("html", "文件【" + str_file + "】不存在!"); //
			return jso_rt;
		}

		StringBuilder sb_html = new StringBuilder(); //

		//所有表格
		List<Element> list = doc.getRootElement().elements("table"); //
		sb_html.append("共【" + list.size() + "】张表&nbsp;<a href=\"JavaScript:onCompareOneXml('" + str_file + "');\">与实际库比较</a>&nbsp;&nbsp;");
		sb_html.append("<a href=\"JavaScript:onCreateAllSQLByOracle('" + str_file + "');\">整个建表SQL(Oracle语法)&nbsp;</a>&nbsp;&nbsp;");
		sb_html.append("<a href=\"JavaScript:onCreateAllSQLByDB2('" + str_file + "');\">整个建表SQL(DB2语法)</a>"); //
		sb_html.append("<br><br>");

		int li_row = list.size() / 5; //多少行
		int li_left = list.size() % 5; //剩余几个

		sb_html.append("<div style=\"margin-bottom:15px\">");
		sb_html.append("<table border=\"1\" style=\"width:1250px;table-layout:fixed;\" align=\"left\">"); //
		for (int i = 0; i < li_row; i++) {
			sb_html.append("<tr>\r\n");
			for (int j = 0; j < 5; j++) {
				int li_pos = i * 5 + j; //
				Element ele = list.get(li_pos); //
				String str_tableName = ele.attributeValue("name"); //
				String str_tableDesc = ele.attributeValue("descr"); //
				String str_bgcolor = ele.attributeValue("bgcolor"); //背景色

				String str_styleBgColro = "";
				if (str_bgcolor != null) {
					str_styleBgColro = "background:" + str_bgcolor;
				}

				sb_html.append("<td style=\"width:250px;word-wrap:break-word;padding-top:5px;padding-bottom:5px;" + str_styleBgColro + "\">");
				sb_html.append("<a href=\"#" + str_tableName + "\" style=\"text-decoration:none\">" + str_tableName + "(" + str_tableDesc + ")</a>");
				sb_html.append("</td>"); //
			}
			sb_html.append("</tr>\r\n");
		}

		if (li_left > 0) {
			sb_html.append("<tr>\r\n");
			for (int j = 0; j < 5; j++) {
				int li_pos = li_row * 5 + j; //
				if (li_pos >= list.size()) {
					sb_html.append("<td style=\"width:200px\">&nbsp;</td>"); //
				} else {
					Element ele = list.get(li_pos); //
					String str_tableName = ele.attributeValue("name"); //
					String str_tableDesc = ele.attributeValue("descr"); //
					sb_html.append("<td style=\"width:250px;word-wrap:break-word;padding-top:5px;padding-bottom:5px;\"><a href=\"#" + str_tableName + "\"  style=\"text-decoration:none\">" + str_tableName + "(" + str_tableDesc + ")</a></td>"); //
				}
			}
			sb_html.append("</tr>\r\n");
		}

		sb_html.append("</table>\r\n");
		sb_html.append("</div>\r\n");

		sb_html.append("<div style=\"clear:both\" /><br><br>\r\n");

		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_tableName = ele.attributeValue("name"); //表名
			String str_pkname = ele.attributeValue("pkname"); //主键
			String str_tableDesc = ele.attributeValue("descr"); //

			String[] str_pkname_array = bsUtil.split(str_pkname, ","); //主键数组

			sb_html.append("<div>\r\n");
			sb_html.append("<table id=\"" + str_tableName + "\" border=1 style=\"width:65%\">\r\n"); //
			sb_html.append("<tr>\r\n"); //
			sb_html.append("<td colspan=\"5\" style=\"background:#BFFFFF\">" + str_tableName + "(" + str_tableDesc + ")主键【" + str_pkname + "】&nbsp;");
			sb_html.append("<a href=\"JavaScript:onCreateSQLByOracle('" + str_file + "','" + str_tableName + "');\">建表SQL(Oracle语法)</a>&nbsp;");
			sb_html.append("<a href=\"JavaScript:onCreateSQLByDB2('" + str_file + "','" + str_tableName + "');\">建表SQL(DB2语法)</a></td>\r\n"); //
			sb_html.append("</tr>\r\n"); //

			sb_html.append("<tr><td style=\"width:20%;text-align:center\">列名</td><td style=\"width:10%;text-align:center\">类型</td><td style=\"width:10%;text-align:center\">宽度</td><td style=\"width:30%;text-align:center\">列名中文</td><td style=\"width:30%;text-align:center\">说明</td></tr>\r\n"); //

			List<Element> colList = ele.element("columns").elements("col"); //
			for (int j = 0; j < colList.size(); j++) {
				Element columnEle = colList.get(j); //
				String str_name = columnEle.attributeValue("name"); //
				String str_type = columnEle.attributeValue("type"); //
				String str_length = columnEle.attributeValue("length"); //
				String str_descr = columnEle.attributeValue("descr", ""); //
				String str_remark = columnEle.attributeValue("remark", ""); //

				if (isExistItemInArray(str_name, str_pkname_array)) {
					str_name = "<span style=\"color:red\">" + str_name + "</span>";
				}

				sb_html.append("<tr><td>" + str_name + "</td><td>" + str_type + "</td><td>" + str_length + "</td><td>" + str_descr + "</td><td>" + str_remark + "</td></tr>\r\n"); //
			}

			sb_html.append("</table>\r\n");
			sb_html.append("</div>\r\n");
			sb_html.append("<br>\r\n");
		}

		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	//得到某一个xml的内容!
	public JSONObject getOneXmlView(JSONObject _jso) throws Exception {
		long ll_1 = System.currentTimeMillis();
		JSONObject jso_rt = new JSONObject(); //
		String str_file = _jso.getString("xmlfile"); //视图的xml文件名

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_file));
		if (doc == null) {
			jso_rt.put("html", "文件【" + str_file + "】不存在!"); //
			return jso_rt;
		}

		StringBuilder sb_html = new StringBuilder(); //

		//所有表格
		List<Element> list = doc.getRootElement().elements("view"); //
		sb_html.append("共【" + list.size() + "】张视图&nbsp;<a href=\"JavaScript:onReCompileOneXmlAllView('" + str_file + "');\">一键重新编译所有视图</a>&nbsp;&nbsp;");
		sb_html.append("<br><br>");

		int li_row = list.size() / 5; //多少行
		int li_left = list.size() % 5; //剩余几个

		sb_html.append("<div style=\"margin-bottom:15px\">");
		sb_html.append("<table border=\"1\" style=\"width:1250px;table-layout:fixed;\" align=\"left\">"); //
		for (int i = 0; i < li_row; i++) {
			sb_html.append("<tr>\r\n");
			for (int j = 0; j < 5; j++) {
				int li_pos = i * 5 + j; //
				Element ele = list.get(li_pos); //
				String str_viewName = ele.attributeValue("name"); //视图名
				String str_viewDesc = ele.attributeValue("descr"); //
				String str_bgcolor = ele.attributeValue("bgcolor"); //背景色

				String str_styleBgColro = "";
				if (str_bgcolor != null) {
					str_styleBgColro = "background:" + str_bgcolor;
				}

				sb_html.append("<td style=\"width:250px;word-wrap:break-word;padding-top:5px;padding-bottom:5px;" + str_styleBgColro + "\">");
				sb_html.append("<a href=\"#" + str_viewName + "\" style=\"text-decoration:none\">" + str_viewName.toLowerCase() + "(" + str_viewDesc + ")</a>");
				sb_html.append("</td>"); //
			}
			sb_html.append("</tr>\r\n");
		}

		if (li_left > 0) {
			sb_html.append("<tr>\r\n");
			for (int j = 0; j < 5; j++) {
				int li_pos = li_row * 5 + j; //
				if (li_pos >= list.size()) {
					sb_html.append("<td style=\"width:200px\">&nbsp;</td>"); //
				} else {
					Element ele = list.get(li_pos); //
					String str_viewName = ele.attributeValue("name"); //
					String str_viewDesc = ele.attributeValue("descr"); //
					sb_html.append("<td style=\"width:250px;word-wrap:break-word;padding-top:5px;padding-bottom:5px;\"><a href=\"#" + str_viewName + "\"  style=\"text-decoration:none\">" + str_viewName.toLowerCase() + "(" + str_viewDesc + ")</a></td>"); //
				}
			}
			sb_html.append("</tr>\r\n");
		}

		sb_html.append("</table>\r\n");
		sb_html.append("</div>\r\n");

		sb_html.append("<div style=\"clear:both\" /><br><br>\r\n");

		//遍历所有!
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_viewName = ele.attributeValue("name"); //视图名
			String str_viewDesc = ele.attributeValue("descr"); //视图说明

			sb_html.append("<div>\r\n");
			sb_html.append("<table id=\"" + str_viewName + "\" border=1 style=\"width:75%\">\r\n"); //
			sb_html.append("<tr>\r\n"); //
			sb_html.append("<td style=\"background:#BFFFFF\">" + str_viewName.toLowerCase() + "(" + str_viewDesc + ")&nbsp;");
			sb_html.append("<a href=\"JavaScript:onReCompileOneView('" + str_file + "','" + str_viewName + "');\">重建视图</a>&nbsp;");
			sb_html.append("</td>\r\n"); //
			sb_html.append("</tr>\r\n"); //

			String str_SQL = ele.getText(); //

			//一定要用<textarea></textarea>包装一下，才会自动换行
			sb_html.append("<tr><td><textarea style=\"width:100%;height:250px;\" readonly=\"true\">" + str_SQL + "</textarea></td></tr>\r\n"); //

			sb_html.append("</table>\r\n");
			sb_html.append("</div>\r\n");
			sb_html.append("<br>\r\n");
		}

		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	//计算初始数据的xml
	public JSONObject getOneInitDataXML(JSONObject _jso) throws Exception {
		long ll_1 = System.currentTimeMillis();
		JSONObject jso_rt = new JSONObject(); //
		String str_file = _jso.getString("xmlfile"); //视图的xml文件名
		String str_type = _jso.getString("type"); //类型

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_file));
		if (doc == null) {
			jso_rt.put("html", "文件【" + str_file + "】不存在!"); //
			return jso_rt;
		}

		StringBuilder sb_html = new StringBuilder(); //
		if (str_type.equals("0")) {
			sb_html.append("<a href=\"JavaScript:exportOneInitDataSQLFromXmlDMO('" + str_file + "','1');\">把首次安装XML生成SQL</a>&nbsp;&nbsp;");
			sb_html.append("<a href=\"JavaScript:exportOneInitDataSQLFromXmlDMO('" + str_file + "','2');\">把升级XML数据生成SQL</a>&nbsp;&nbsp;");

			sb_html.append("<a href=\"JavaScript:exportOneInitDataXmlDMO('" + str_file + "','1');\">从数据库生成首次安装XML</a>&nbsp;&nbsp;");
			sb_html.append("<a href=\"JavaScript:exportOneInitDataXmlDMO('" + str_file + "','2');\">从数据库生成升级XML</a>&nbsp;&nbsp;");
			sb_html.append("<br>");
		}

		sb_html.append("<textarea style=\"width:90%;height:90%\" readonly=\"true\">\r\n");
		sb_html.append(doc.asXML());
		sb_html.append("</textarea>");

		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	//一键重新编译所有模块的视图!
	public JSONObject reCompileAllView(JSONObject _par) throws Exception {
		//先找索引xml，再找所有xml
		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/1_filelist.xml"));
		ArrayList<String> allXmlTabNameList = new ArrayList<String>(); //
		List<Element> list = doc.getRootElement().elements("xmlfile");

		StringBuilder sb_allHtml = new StringBuilder(); //
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_viewfileName = ele.attributeValue("viewxml"); //xml文件名!
			if (str_viewfileName == null || str_viewfileName.equals("")) {
				continue;
			}
			JSONObject jso_par = new JSONObject(); //
			jso_par.put("xmlfile", str_viewfileName); //
			JSONObject jso_oneXml = reCompileOneXmlAllView(jso_par); //某一个文件的内容
			sb_allHtml.append(jso_oneXml.getString("html")); //
			sb_allHtml.append("<br><br>\r\n");
		}

		JSONObject jso_rt = new JSONObject(); //
		jso_rt.put("html", sb_allHtml.toString()); //
		return jso_rt; //
	}

	//重新编译所有视图。。。
	public JSONObject reCompileOneXmlAllView(JSONObject _par) throws Exception {
		String str_file = _par.getString("xmlfile"); //视图的xml文件名

		JSONObject jso_rt = new JSONObject(); //

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_file));
		if (doc == null) {
			jso_rt.put("html", "文件【" + str_file + "】不存在!"); //
			return jso_rt;
		}

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("★★★开始编译【" + str_file + "】所有视图★★★<br>\r\n");

		//所有表格
		List<Element> list = doc.getRootElement().elements("view"); //

		//遍历所有!
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_viewName = ele.attributeValue("name"); //视图名
			String str_viewDesc = ele.attributeValue("descr"); //视图说明
			String str_SQL = ele.getText(); //

			str_SQL = str_SQL.trim();
			if (str_SQL.endsWith(";")) {
				str_SQL = str_SQL.substring(0, str_SQL.length() - 1); //
			}

			try {
				bsUtil.executeUpdate(str_SQL); //
				sb_html.append("<span style=\"color:blue\">编译视图【" + str_file + "】【" + str_viewName + "】【" + str_viewDesc + "】成功!!</span><br>\r\n"); //
			} catch (Exception _ex) {
				_ex.printStackTrace(); //
				sb_html.append("<span style=\"color:red\">编译视图【" + str_file + "】【" + str_viewName + "】【" + str_viewDesc + "】失败，单个编译可查看详细原因。</span><br>\r\n"); //
			}
			sb_html.append("<br>\r\n");
		}

		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	//重新编译某一个视图!!
	public JSONObject reCompileOneView(JSONObject _par) throws Exception {
		long ll_1 = System.currentTimeMillis();
		String str_file = _par.getString("xmlfile"); //视图的xml文件名
		String str_compileViewName = _par.getString("viewname"); //视图的xml文件名

		JSONObject jso_rt = new JSONObject(); //

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_file));
		if (doc == null) {
			jso_rt.put("html", "文件【" + str_file + "】不存在!"); //
			return jso_rt;
		}

		StringBuilder sb_html = new StringBuilder(); //

		//所有表格
		List<Element> list = doc.getRootElement().elements("view"); //

		//遍历所有!
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_viewName = ele.attributeValue("name"); //视图名
			String str_viewDesc = ele.attributeValue("descr"); //视图说明

			String str_SQL = ele.getText(); //
			if (str_SQL.endsWith(";")) {
				str_SQL = str_SQL.substring(0, str_SQL.length() - 1); //
			}

			//如果正好是要编译的!
			if (str_viewName.equalsIgnoreCase(str_compileViewName)) {
				try {
					bsUtil.executeUpdate(str_SQL); //
					sb_html.append("<span style=\"color:blue\">编译单一视图【" + str_viewName + "】【" + str_viewDesc + "】成功!!</span><br>"); //
				} catch (Exception _ex) {
					_ex.printStackTrace(); //
					String str_ex = bsUtil.getExceptionStringBuffer(_ex); //
					sb_html.append("<span style=\"color:red\">编译单一视图【" + str_viewName + "】【" + str_viewDesc + "】失败,原因:</span><br>"); //
					sb_html.append(str_ex + "<br>");
				}

				break;
			}
			sb_html.append("<br>\r\n");
		}

		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	//比较所有模块的XMl文件
	public JSONObject compareAllXml(JSONObject _jsoPar) {
		String database_type = _jsoPar.getString("database_type");//获取数据库类型

		//先找索引xml，再找所有xml
		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/1_filelist.xml"));
		ArrayList<String> allXmlTabNameList = new ArrayList<String>(); //
		List<Element> list = doc.getRootElement().elements("xmlfile");

		String[][] str_DBTables = bsUtil.getAllSysTables(); //数据库中所有的表!
		StringBuilder sb_allHtml = new StringBuilder(); //
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_xmlfileName = ele.attributeValue("tabxml"); //xml文件名!
			JSONObject jso_oneXml = compareOneXml(str_xmlfileName, str_DBTables, database_type); //某一个文件的内容
			sb_allHtml.append(jso_oneXml.getString("html")); //
			sb_allHtml.append("<br><br>\r\n");
		}

		JSONObject jso_rt = new JSONObject(); //
		jso_rt.put("html", sb_allHtml.toString()); //
		return jso_rt; //
	}

	//比较某一个模块的XMl文件
	public JSONObject compareOneXml(JSONObject _jsoPar) {
		String database_type = _jsoPar.getString("database_type");//获取数据库类型
		String str_xmlFile = _jsoPar.getString("xmlFile"); //
		String[][] str_DBTables = bsUtil.getAllSysTables(); //数据库中所有的表!
		return compareOneXml(str_xmlFile, str_DBTables, database_type); //
	}

	//比较一个xml
	public JSONObject compareOneXml(String _xmlFile, String[][] str_DBTables, String database_type) {
		long ll_1 = System.currentTimeMillis();
		JSONObject jso_rt = new JSONObject(); //

		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + _xmlFile)); //
		if (doc == null) {
			jso_rt.put("html", "/**[" + _xmlFile + "]文件不存在!*/<br>\r\n"); //
			return jso_rt; //
		}

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("/**★★★开始比较[" + _xmlFile + "]与实际库存的差异★★★*/<br>\r\n"); //

		//先计算出存在xml中,但不存在数据库中的表,即要做Create Table..
		sb_html.append("/**【" + _xmlFile + "】中多出的表*/<br>\r\n"); //输出
		List<Element> list = doc.getRootElement().elements("table"); //
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_tableName = ele.attributeValue("name"); //表名
			if (!isExistInDB(str_tableName, str_DBTables)) { //如果这个表在数据库中不存在,
				//sb_html.append("/**新增表" + str_tableName + "*/\r\n");
				sb_html.append(getCreateTableSQL(ele, database_type)); //根据xml结点创建create table 的SQL
			}
		}

		sb_html.append("/**【" + _xmlFile + "】与数据库表字段差异*/<br>\r\n"); //输出

		//然后计算出两者都有,但表结构不一样的,表结构又分为三种:
		//一种是xml中多的字段，要做add column，
		//第二种是表中有，但xml中没有.要做drop column
		//第三种是,两者一样但宽度或类型不一样,要做modify
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //某个表
			String str_tableName = ele.attributeValue("name"); //表名
			if (isExistInDB(str_tableName, str_DBTables)) { //如果这个表在数据库存在,则比较表结构
				String str_allCompareSQL = getCompareTableColSQL(ele, str_tableName, database_type); //根据xml结点创建create table 的SQL
				if (str_allCompareSQL.length() > 0) {
					sb_html.append("/**表[" + str_tableName + "]有差异*/<br>\r\n");
					sb_html.append(str_allCompareSQL); //
					sb_html.append("<br>\r\n");
				}
			}
		}

		long ll_2 = System.currentTimeMillis();
		sb_html.append("/**★★★比较[" + _xmlFile + "]结束,共耗时[" + (ll_2 - ll_1) + "]毫秒★★★*/<br>\r\n"); //

		jso_rt.put("html", sb_html.toString()); //
		return jso_rt; //
	}

	//把所有数据从数据库里反向生成xml
	public JSONObject exportAllInitDataXmlDMO(JSONObject _par) throws Exception {
		String str_type = _par.getString("type"); //类型
		String str_filepath = _par.getString("filepath"); //文件路径!

		JSONObject jso_rt = new JSONObject(); //
		File filePath = new File(str_filepath); //
		if (!filePath.exists()) {
			jso_rt.put("code", "error"); //
			jso_rt.put("msg", "目录【" + str_filepath + "】不存在,要先创建好!"); //
			return jso_rt;
		}

		//先找索引xml，再找所有xml
		Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/1_filelist.xml"));
		//ArrayList<String> allXmlTabNameList = new ArrayList<String>(); //
		List<Element> list = doc.getRootElement().elements("xmlfile"); //所有文件内容

		//遍历所有文件!
		String str_allFileName = "";
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_xmlfileName = ele.attributeValue("initdataxml"); //初始化数据的SQL
			if (str_xmlfileName == null || str_xmlfileName.equals("")) {
				continue;
			}

			String str_dataConfiXmlPath = "/bione-plugin/database/" + str_xmlfileName; //读取配置文件!
			Document doc_item = readXml(this.getClass().getResourceAsStream(str_dataConfiXmlPath)); //读取文件内容
			if (doc_item == null) { //有可能不存在!!
				continue; //
			}

			String str_xmlfileName_prefix = str_xmlfileName.substring(0, str_xmlfileName.indexOf(".")); //
			String sr_writeXmlFileName = str_xmlfileName_prefix + "_" + str_type + ".xml";
			String str_writeXmlPathName = str_filepath + "/" + sr_writeXmlFileName; //输出的文件!

			createXmlFromDB(doc_item, str_type, str_writeXmlPathName); //实际创建文件!
			str_allFileName = str_allFileName + sr_writeXmlFileName + ",";
		}

		if (str_allFileName.endsWith(",")) {
			str_allFileName = str_allFileName.substring(0, str_allFileName.length() - 1); //
		}

		jso_rt.put("code", "ok"); //
		jso_rt.put("msg", "在目录【" + str_filepath + "】下创建【" + str_allFileName + "】文件成功!"); //
		return jso_rt; //
	}

	//从数据库生成数据!
	public JSONObject exportOneInitDataXmlDMO(JSONObject _par) throws Exception {
		String str_xmlfile = _par.getString("xmlfile"); //配置文件!!
		String str_type = _par.getString("type"); //类型
		String str_filepath = _par.getString("filepath"); //文件路径!
		String str_dataConfiXmlPath = "/bione-plugin/database/" + str_xmlfile; //读取配置文件!

		String str_xmlfile_prefix = str_xmlfile.substring(0, str_xmlfile.indexOf(".")); //前辍

		//System.err.println("加载文件路径【" + str_filePath + "】"); //

		JSONObject jso_rt = new JSONObject(); //
		Document doc_item = readXml(this.getClass().getResourceAsStream(str_dataConfiXmlPath)); //读取文件内容
		if (doc_item == null) {
			jso_rt.put("code", "error"); //
			jso_rt.put("msg", "配置文件【" + str_dataConfiXmlPath + "】不存在,必须要定义表名!"); //
			return jso_rt;
		}

		File filePath = new File(str_filepath); //
		if (!filePath.exists()) {
			jso_rt.put("code", "error"); //
			jso_rt.put("msg", "目录【" + str_filepath + "】不存在,要先创建好!"); //
			return jso_rt;
		}

		String str_writeXmlPathName = str_filepath + "/" + str_xmlfile_prefix + "_" + str_type + ".xml"; //

		createXmlFromDB(doc_item, str_type, str_writeXmlPathName); //实际创建文件..

		jso_rt.put("code", "ok"); //
		jso_rt.put("msg", "创建【" + str_writeXmlPathName + "】文件成功!"); //
		return jso_rt; //
	}

	//实际创建一个文件!
	private void createXmlFromDB(Document doc_item, String str_type, String str_writeXmlPathName) throws Exception {
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sb_xml.append("<root>\r\n"); //

		List<Element> tabList = null; //
		if (str_type.equals("1")) {
			tabList = doc_item.getRootElement().element("firstinstall").elements("table"); //首次安装的表名
		} else if (str_type.equals("2")) {
			tabList = doc_item.getRootElement().element("upgrade").elements("table"); //升级的表名
		}

		//实际数据
		for (int j = 0; j < tabList.size(); j++) {
			Element tabEle = tabList.get(j); //
			String str_tabName = tabEle.attributeValue("name"); //表名!
			sb_xml.append(getCreateInitDataXMLFromDB(str_tabName)); //从数据库计算出该表的初始化SQL
			sb_xml.append("\r\n");
		}

		sb_xml.append("</root>\r\n"); //

		bsUtil2.writeStrToOutputStream(new FileOutputStream(str_writeXmlPathName), sb_xml.toString(), "UTF-8"); //
	}

	//判断xml中某个表名是否存在数据库中!
	private boolean isExistInDB(String str_tableName, String[][] str_DBTables) {
		for (int i = 0; i < str_DBTables.length; i++) {
			if (str_DBTables[i][0].equalsIgnoreCase(str_tableName)) {
				return true; //
			}
		}
		return false;
	}

	public String getCreateTableSQL(Element _ele, String _dbType) {
		return getCreateTableSQL(_ele, _dbType, true);
	}

	//建表SQL
	public String getCreateTableSQL(Element _ele, String _dbType, boolean _isHtmlStyle) {
		String str_tableName = _ele.attributeValue("name"); //
		String str_pkname = _ele.attributeValue("pkname"); //主键名称
		String str_desc = _ele.attributeValue("descr", ""); //表的注释!

		StringBuilder sb_sql = new StringBuilder(); //
		if (_isHtmlStyle) {
			sb_sql.append("<p style=\"color:blue\">");
		}
		sb_sql.append("create table " + str_tableName + "(" + (_isHtmlStyle ? "<br>" : "") + "\r\n"); //

		List<Element> colsList = _ele.element("columns").elements("col"); //
		for (int i = 0; i < colsList.size(); i++) {
			Element eleColumn = colsList.get(i); //
			String str_colName = eleColumn.attributeValue("name", ""); //
			String str_colType = eleColumn.attributeValue("type", ""); //
			String str_colLength = eleColumn.attributeValue("length", ""); //
			String str_colDescr = eleColumn.attributeValue("descr", "");//字段注释

			str_colType = convertXmlTypeToDBType(str_colType, _dbType); //把xml的类型转换成实际数据库类型
			if (str_colType.equalsIgnoreCase("clob") || str_colType.equalsIgnoreCase("blob") || str_colType.equalsIgnoreCase("text") || str_colType.equalsIgnoreCase("date")) { //这三种类型不要宽度
				str_colLength = ""; //
			} else if (str_colType.equalsIgnoreCase("timestamp")) {
				if (_dbType.equalsIgnoreCase("MYSQL")) {
					str_colLength = ""; //
				} else {
					str_colLength = "(" + str_colLength + ")";
				}
			} else {
				str_colLength = "(" + str_colLength + ")";
			}
			if ("MYSQL".equalsIgnoreCase(_dbType)) {//mysql字段添加注释
				sb_sql.append(str_colName + "  " + str_colType + "" + str_colLength + " comment '"+ str_colDescr +"', " + (_isHtmlStyle ? "<br>" : "") + "\r\n");
			} else {
				sb_sql.append(str_colName + "  " + str_colType + "" + str_colLength + ", " + (_isHtmlStyle ? "<br>" : "") + "\r\n"); //
			}
		}

		String str_pkSubFix = str_tableName; //
		if (str_pkSubFix.length() > 27) {
			str_pkSubFix = str_pkSubFix.substring(str_pkSubFix.length() - 27, str_pkSubFix.length()); //有的时候表名太长,导致产生的主键名超过30位而报错
		}
		String str_pkName = "pk_" + str_pkSubFix; //主键名
		sb_sql.append("constraint " + str_pkName + " primary key(" + str_pkname + ")" + (_isHtmlStyle ? "<br>" : "") + "\r\n"); //主键

		if ("MYSQL".equalsIgnoreCase(_dbType)) {//mysql给表添加注释
			sb_sql.append(")comment = '"+ str_desc +"';" + (_isHtmlStyle ? "<br>" : "") + "\r\n"); //
		}else{
			sb_sql.append(");" + (_isHtmlStyle ? "<br>" : "") + "\r\n"); //
			sb_sql.append("comment on table " + str_tableName + " is '" + str_desc + "';" + (_isHtmlStyle ? "<br>" : "") + "\r\n"); //
			for (int i = 0; i < colsList.size(); i++) {
				Element eleColumn = colsList.get(i); //
				String str_colName = eleColumn.attributeValue("name", ""); //
				String str_colDescr = eleColumn.attributeValue("descr", ""); //
				sb_sql.append("comment on column " + str_tableName + "." + str_colName + " is '" + str_colDescr + "';" + (_isHtmlStyle ? "<br>" : "") + "\r\n"); //
			}
		}

		if (_isHtmlStyle) {
			sb_sql.append("</p>\r\n");
		}
		return sb_sql.toString();
	}

	//初始化数据的SQL
	public String getCreateInitDataSQLFromDB(String _tabName) {
		HashVOStruct hvst = bsUtil.getHashVOStruct("select * from " + _tabName); //
		String[] str_cols = hvst.getHeaderName(); //
		HashVO[] hvs_data = hvst.getHashVOs(); //

		StringBuilder sb_sql = new StringBuilder(); //
		for (int i = 0; i < hvs_data.length; i++) {
			InsertSQLBuilder isql = new InsertSQLBuilder(_tabName); //
			for (int j = 0; j < str_cols.length; j++) {
				isql.putFieldValue(str_cols[j], hvs_data[i].getStringValue(str_cols[j], ""));
			}

			sb_sql.append(isql.getSQL()); //
			sb_sql.append(";\r\n");
		}

		return sb_sql.toString(); //
	}

	//初始化数据的SQL
	public String getCreateInitDataSQLFromXml(Element _recordEle, Element _tabStructEle, String _dbType) {
		String str_tabname = _recordEle.attributeValue("tabname"); //表名

		InsertSQLBuilder isql = new InsertSQLBuilder(str_tabname); //
		List<Element> colList = _recordEle.elements("col"); //
		for (int i = 0; i < colList.size(); i++) {
			Element colEle = colList.get(i); //
			String str_colName = colEle.attributeValue("name"); //字段名
			String str_colValue = colEle.getText(); //字段值
			String str_colDataType = findColDataType(str_colName, _tabStructEle); //找出这个字段的数据类型!

			//mysql曾经有违反关键保留字的情况..
//			if (_dbType.equalsIgnoreCase("MYSQL")) {
//				if (str_colName.equalsIgnoreCase("schema") || str_colName.equalsIgnoreCase("sql") || str_colName.equalsIgnoreCase("precision") || str_colName.equalsIgnoreCase("explain")) {
//					str_colName = str_colName + "2"; //
//				}
//			}

			//如果是Oracle类型,则日期值要特别处理!
			if (_dbType.equalsIgnoreCase("ORACLE")) {
				if (str_colValue != null && !str_colValue.equals("")) {
					if ("timestamp".equalsIgnoreCase(str_colDataType)) {
						str_colValue = "to_date('" + str_colValue + "','YYYY-MM-DD HH24:MI:SS')"; //
					} else if ("date".equalsIgnoreCase(str_colDataType)) {
						str_colValue = "to_date('" + str_colValue + "','YYYY-MM-DD')"; //
					}
				}
			}
			isql.putFieldValue(str_colName, str_colValue); //设置值
		}

		return isql.getSQL(); //
	}

	//计算字段类型
	private String findColDataType(String _colName, Element _tabStructEle) {
		if (_tabStructEle == null) {
			return null; //
		}
		List<Element> colList = _tabStructEle.element("columns").elements("col"); //遍历所有的列
		for (int i = 0; i < colList.size(); i++) {
			Element colEle = colList.get(i);
			String str_colName = colEle.attributeValue("name"); //
			String str_colType = colEle.attributeValue("type"); //
			if (str_colName.equalsIgnoreCase(_colName)) {
				return str_colType; //
			}
		}
		return null;
	}

	//初始化数据的SQL..
	public String getCreateInitDataXMLFromDB(String _tabName) {
		HashVOStruct hvst = bsUtil.getHashVOStruct("select * from " + _tabName); //
		String[] str_cols = hvst.getHeaderName(); //
		HashVO[] hvs_data = hvst.getHashVOs(); //

		StringBuilder sb_xml = new StringBuilder(); //

		for (int i = 0; i < hvs_data.length; i++) {
			sb_xml.append("<record tabname=\"" + _tabName + "\">\r\n"); //
			for (int j = 0; j < str_cols.length; j++) {
				String str_colValue = hvs_data[i].getStringValue(str_cols[j], ""); //
				if (str_colValue.indexOf("&") >= 0 || str_colValue.indexOf(">") >= 0 || str_colValue.indexOf("<") >= 0) {
					str_colValue = "<![CDATA[" + str_colValue + "]]>";
				}
				sb_xml.append("  <col name=\"" + str_cols[j] + "\">" + str_colValue + "</col>\r\n"); //
			}
			sb_xml.append("</record>\r\n\r\n");
		}

		return sb_xml.toString(); //
	}

	//比较表结构的SQL
	private String getCompareTableColSQL(Element _ele, String _tableName, String _dbType) {
		StringBuilder sb_sql = new StringBuilder(); //
		List<Element> colsList = _ele.element("columns").elements("col"); //

		HashVOStruct hvst = bsUtil.getHashVOStruct("select * from " + _tableName + " where 1=2"); //
		String[] str_cols_db = hvst.getHeaderName(); //列名
		String[] str_types_db = hvst.getHeaderTypeName(); //字段类型
		int[] li_per_db = hvst.getPrecision(); //宽度
		int[] li_scale_db = hvst.getScale(); //

		//计算出只存于xml中的字段!
		String[] str_cols_xml = new String[colsList.size()];
		HashMap<String, String> xmlColTypeMap = new HashMap<String, String>(); //xml中字段类型的Map
		HashMap<String, String> xmlColLengthMap = new HashMap<String, String>(); //xml中字段的宽度的Map

		for (int i = 0; i < colsList.size(); i++) {
			Element colEle = colsList.get(i); //
			str_cols_xml[i] = colEle.attributeValue("name", "").toLowerCase(); //字段名
			xmlColTypeMap.put(str_cols_xml[i], colEle.attributeValue("type", "")); //类型
			xmlColLengthMap.put(str_cols_xml[i], colEle.attributeValue("length", "")); //宽度

			if (!isExistItemInArray(str_cols_xml[i], str_cols_db)) { //如果xml中定义的这个列,不在数据库中!!
				String str_sql_allCol = getAlterAddColSQL(_tableName, colEle, _dbType); //根据xml创建新增列的SQL
				sb_sql.append(str_sql_allCol + "<br>\r\n"); //加上这个SQL
			}
		}

		//计算出只存于数据库中的字段
		for (int i = 0; i < str_cols_db.length; i++) {
			String str_colName = str_cols_db[i];
			if (!isExistItemInArray(str_colName, str_cols_xml)) { //如果数据库中的这个字段,不在xml中定义
				String str_sql_dropCol = "<span style=\"color:red\">alter table " + _tableName + " drop column " + str_colName + ";</span>"; //
				sb_sql.append(str_sql_dropCol + "<br>\r\n"); //加上这个SQL
			}
		}

		//找出差异列
		for (int i = 0; i < str_cols_db.length; i++) {
			String str_colName = str_cols_db[i];
			if (isExistItemInArray(str_colName, str_cols_xml)) { //如果数据库中的这个字段同时在xml中定义了
				String str_colType_db = convertType(str_types_db[i]); //把数据库的类型转换成xml中的“中间类型”
				String str_length_db = convertLength(str_colType_db, li_per_db[i], li_scale_db[i]); //把数据库的宽度,转换成xml的“中间宽度”
				String str_colType_xml = xmlColTypeMap.get(str_colName); //xml中定义的字段类型
				String str_colLen_xml = xmlColLengthMap.get(str_colName); //xml中定义的字段宽度

				//如果两者同时相等,则不做
				if (str_colType_db.equalsIgnoreCase(str_colType_xml)) {
					if(str_colType_db.equalsIgnoreCase("clob") || str_colType_db.equalsIgnoreCase("blob") || str_colType_db.equalsIgnoreCase("text") || str_colType_db.equalsIgnoreCase("date")){
					}else{
						if(str_colType_db.equalsIgnoreCase("timestamp")){
							if (_dbType.equalsIgnoreCase("MYSQL")) {
							}else{
								String str_sql_modifyCol = getAlterModifyColSQL(_tableName, str_colName, str_types_db[i], str_length_db, str_colType_xml, str_colLen_xml, _dbType); //根据xml创建新增列的SQL
								sb_sql.append(str_sql_modifyCol + "<br>\r\n"); //加上这个SQL
							}
						}else{
							if(!str_length_db.equalsIgnoreCase(str_colLen_xml)){
								String str_sql_modifyCol = getAlterModifyColSQL(_tableName, str_colName, str_types_db[i], str_length_db, str_colType_xml, str_colLen_xml, _dbType); //根据xml创建新增列的SQL
								sb_sql.append(str_sql_modifyCol + "<br>\r\n"); //加上这个SQL
							}
						}
					}
				} else { //
					String str_sql_modifyCol = getAlterModifyColSQL(_tableName, str_colName, str_types_db[i], str_length_db, str_colType_xml, str_colLen_xml, _dbType); //根据xml创建新增列的SQL
					sb_sql.append(str_sql_modifyCol + "<br>\r\n"); //加上这个SQL
				}
			}

		}
		hvst.getPrecision(); //
		return sb_sql.toString();
	}

	//增加列的SQL
	private String getAlterAddColSQL(String str_tableName, Element _eleCol, String _dbType) {
		String str_colName = _eleCol.attributeValue("name"); //
		String str_colType = _eleCol.attributeValue("type", ""); //
		String str_colLength = _eleCol.attributeValue("length", ""); //

		str_colType = convertXmlTypeToDBType(str_colType, _dbType); //把xml的类型转换成实际数据库类型

		if (str_colType.equalsIgnoreCase("clob") || str_colType.equalsIgnoreCase("blob") || str_colType.equalsIgnoreCase("text") || str_colType.equalsIgnoreCase("date")) { //这三种类型不要宽度
			str_colLength = ""; //
		} else if (str_colType.equalsIgnoreCase("timestamp")) {
			if (_dbType.equalsIgnoreCase("MYSQL")) {
				str_colLength = ""; //
			} else {
				str_colLength = "(" + str_colLength + ")";
			}
		} else {
			str_colLength = "(" + str_colLength + ")";
		}

		return "<span style=\"color:blue\">alter table " + str_tableName + " add " + str_colName + " " + str_colType + "" + str_colLength + ";</span>";
	}

	//修改列的SQL
	private String getAlterModifyColSQL(String _tableName, String _colName, String str_colType_db, String str_length_db, String str_colType_xml, String str_colLen_xml, String _dbType) {
		String str_colType = str_colType_xml; //
		String str_colLength = str_colLen_xml; //
		str_colType = convertXmlTypeToDBType(str_colType, _dbType); //把xml的类型转换成实际数据库类型
		if (str_colType.equalsIgnoreCase("clob") || str_colType.equalsIgnoreCase("blob") || str_colType.equalsIgnoreCase("text") || str_colType.equalsIgnoreCase("date")) { //这三种类型不要宽度
			str_colLength = ""; //
		} else if (str_colType.equalsIgnoreCase("timestamp")) {
			if (_dbType.equalsIgnoreCase("MYSQL")) {
				str_colLength = ""; //
			} else {
				str_colLength = "(" + str_colLength + ")";
			}
		} else {
			str_colLength = "(" + str_colLength + ")";
		}

		// /**原类型:" + str_colType_db + "(" + str_length_db + ")*/</span>";
		return "<span style=\"color:green\">alter table " + _tableName + " modify " + _colName + " " + str_colType + str_colLength + ";</span>"; //
	}

	//判断一个字符是否在数组中
	private boolean isExistItemInArray(String _item, String[] _array) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i].equalsIgnoreCase(_item)) {
				return true; //
			}
		}
		return false;
	}

	private HashMap<String, String> convertTabCommentMap(HashVO[] _hvs) {
		HashMap<String, String> map = new HashMap<String, String>(); //
		for (int i = 0; i < _hvs.length; i++) {
			map.put(_hvs[i].getStringValue("table_name").toLowerCase(), _hvs[i].getStringValue("comments")); //
		}
		return map;
	}

	private HashMap<String, String> convertColCommentMap(HashVO[] _hvs) {
		HashMap<String, String> map = new HashMap<String, String>(); //
		for (int i = 0; i < _hvs.length; i++) {
			map.put(_hvs[i].getStringValue("table_name").toLowerCase() + "#" + _hvs[i].getStringValue("column_name").toLowerCase(), _hvs[i].getStringValue("comments")); //
		}
		return map;
	}

	//创建xml
	public JSONObject getAllTableXML(JSONObject _jso) throws Exception {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"); //
		sb_html.append("<root>\r\n");

		HashVO[] hvs_tab_comment = bsUtil.getHashVOs("select table_name,comments from USER_TAB_COMMENTS"); //
		HashVO[] hvs_col_comment = bsUtil.getHashVOs("select table_name,column_name,comments from USER_COL_COMMENTS"); //

		HashMap<String, String> tabCommentMap = convertTabCommentMap(hvs_tab_comment);
		HashMap<String, String> colCommentMap = convertColCommentMap(hvs_col_comment);

		String str_tablePrefix = "ACT"; //

		long ll_1 = System.currentTimeMillis(); //
		HashVO[] hvs_data = bsUtil.getHashVOs("select * from tab where tname like '" + str_tablePrefix + "_%' order by tname"); //TABTYPE

		for (int i = 0; i < hvs_data.length; i++) {
			String str_tname = hvs_data[i].getStringValue("tname");
			String str_pks = getPkNames(str_tname);
			String str_tab_desc = tabCommentMap.get(str_tname.toLowerCase()); //
			if (str_tab_desc == null || str_tab_desc.indexOf("??") >= 0) {
				str_tab_desc = str_tname; //
			}
			sb_html.append("<table name=\"" + str_tname.toLowerCase() + "\" pkname=\"" + str_pks + "\" descr=\"" + str_tab_desc + "\">\r\n"); //
			sb_html.append("<columns>\r\n");
			HashVOStruct hvst = bsUtil.getHashVOStruct("select * from " + str_tname + " where 1=2"); //
			String[] str_cols = hvst.getHeaderName(); //
			String[] str_types = hvst.getHeaderTypeName(); //

			int[] li_per = hvst.getPrecision(); //
			int[] li_scale = hvst.getScale(); //

			for (int j = 0; j < str_cols.length; j++) {
				String str_colType = convertType(str_types[j]); //
				String str_length = convertLength(str_colType, li_per[j], li_scale[j]);
				String str_colDesc = colCommentMap.get(str_tname.toLowerCase() + "#" + str_cols[j].toLowerCase()); //

				if (str_colDesc == null) {
					str_colDesc = str_cols[j];
				}

				if (str_colDesc.indexOf("??") >= 0) {
					str_colDesc = str_cols[j];
				}

				sb_html.append("  <col name=\"" + str_cols[j] + "\"			type=\"" + str_colType + "\"   length=\"" + str_length + "\"   descr=\"" + str_colDesc + "\"/>\r\n");
			}

			sb_html.append("</columns>\r\n");
			sb_html.append("</table>\r\n");

			sb_html.append("\r\n");
		}

		sb_html.append("</root>\r\n");

		bsUtil2.writeStrToOutputStream(new FileOutputStream("K:/555/" + str_tablePrefix.toLowerCase() + ".xml"), sb_html.toString(), "UTF-8");

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("html", "计算成功!"); //

		long ll_2 = System.currentTimeMillis(); //
		System.err.println("创建xml文件成功,共耗时【" + (ll_2 - ll_1) + "】毫秒!"); //
		return jso_rt; //
	}

	//转换类型,把数据库的实际类型转换成中间类型
	private String convertType(String _type) {
		if (_type.equalsIgnoreCase("varchar") || _type.equalsIgnoreCase("varchar2")) {
			return "varchar";
		} else if (_type.equalsIgnoreCase("number") || _type.equalsIgnoreCase("decimal")) {
			return "decimal";
		} else {
			return _type.toLowerCase();
		}
	}

	//把xml的字段类型转换成数据库的实际字段类型
	private String convertXmlTypeToDBType(String _type, String _dbtype) {
		if (_type.equalsIgnoreCase("varchar") || _type.equalsIgnoreCase("varchar2") || _type.equalsIgnoreCase("nvarchar") || _type.equalsIgnoreCase("nvarchar2")) {
			if (_dbtype.equalsIgnoreCase("ORACLE")) {
				return "varchar2";
			} else if (_dbtype.equalsIgnoreCase("MYSQL")) {
				return "varchar";
			} else {
				return _type;
			}
		} else if (_type.equalsIgnoreCase("decimal") || _type.equalsIgnoreCase("number")) {
			if (_dbtype.equalsIgnoreCase("ORACLE")) {
				return "number";
			} else if (_dbtype.equalsIgnoreCase("DB2")) {
				return "decimal";
			} else if (_dbtype.equalsIgnoreCase("MYSQL")) {
				return "decimal";
			} else {
				return _type;
			}
		} else if (_type.equalsIgnoreCase("char")) {
			return "char"; //
		} else if (_type.equalsIgnoreCase("clob")) {
			if (_dbtype.equalsIgnoreCase("MYSQL")) {
				return "text";
			} else {
				return "clob"; //
			}
		} else if (_type.equalsIgnoreCase("blob")) {
			if (_dbtype.equalsIgnoreCase("MYSQL")) {
				return "blob";
			} else {
				return "blob"; //
			}
		} else {
			return _type;
		}
	}

	//把
	private String convertLength(String _type, int _len, int _scale) {
		if (_type.equalsIgnoreCase("timestamp")) {
			return "" + _scale;
		} else {
			if (_scale <= 0) {
				return "" + _len;
			} else {
				return "" + _len + "," + _scale;
			}
		}
	}

	private String getPkNames(String _table) {
		String str_sql = "select t2.column_name from USER_CONSTRAINTS t1,USER_CONS_COLUMNS t2 where t1.owner=t2.owner and t1.table_name=t2.table_name and t1.constraint_name=t2.constraint_name and t1.table_name='" + _table.toUpperCase() + "' and t1.constraint_type='P' order by position";
		HashVO[] hvs_data = bsUtil.getHashVOs(str_sql); //
		if (hvs_data == null || hvs_data.length <= 0) {
			return "";
		}

		String str_col = "";
		for (int i = 0; i < hvs_data.length; i++) {
			str_col = str_col + hvs_data[i].getStringValue("column_name") + ","; //
		}
		str_col = str_col.substring(0, str_col.length() - 1); //
		return str_col.toLowerCase();
	}

	//一次把所有表格数据查询出来看看!
	public JSONObject exportAllDBDataToHtml(JSONObject _par) throws Exception {
		long ll_1 = System.currentTimeMillis();
		String[] str_xmls = new String[] { "bione.xml", "rpt.xml" }; //
		for (int d = 0; d < str_xmls.length; d++) {
			Document doc = readXml(this.getClass().getResourceAsStream("/bione-plugin/database/" + str_xmls[d]));
			List<Element> tabList = doc.getRootElement().elements("table"); //
			for (int i = 0; i < tabList.size(); i++) {
				Element tabEle = tabList.get(i); //
				String str_tab = tabEle.attributeValue("name");
				HashVO[] hvs_count = bsUtil.getHashVOs("select count(*) c1 from " + str_tab);
				HashVO[] hvs_data = bsUtil.getHashVOs("select * from " + str_tab + " where rownum<=100");
				if (hvs_data != null && hvs_data.length > 0) {
					String str_count = hvs_count[0].getStringValue("c1"); //
					bsUtil.writeHashToHtmlTableFile(hvs_data, "E:/111/" + str_tab + "-" + str_count + ".html"); //
				}
			}
		}
		long ll_2 = System.currentTimeMillis();
		JSONObject jso_rt = new JSONObject();
		jso_rt.put("msg", "生成数据成功!耗时【" + (ll_2 - ll_1) + "】毫秒!"); //
		return jso_rt; //
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
