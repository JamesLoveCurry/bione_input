package com.yusys.bione.plugin.database;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.service.AbstractDownloadFile;

/**
 * 导出所有表的初始化数据的SQL
 * @author xch
 *
 */
public class ExportAllInitDataSQLDMO extends AbstractDownloadFile {

	private DataBaseDMO dataBaseDMO = new DataBaseDMO();

	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.DownLoadAsString;
	}

	@Override
	public String getDownLoadContentAsString(JSONObject _par) throws Exception {
		String str_type = _par.getString("type"); //

		//先找索引xml，再找所有xml
		Document doc = readXml(this.getClass().getResourceAsStream("/com/yusys/bione/plugin/database/1_filelist.xml"));
		//ArrayList<String> allXmlTabNameList = new ArrayList<String>(); //
		List<Element> list = doc.getRootElement().elements("xmlfile"); //所有文件内容

		StringBuilder sb_sql = new StringBuilder(); //
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_xmlfileName = ele.attributeValue("initdataxml"); //初始化数据的SQL,是【bione_data.xml】 的样子!
			if (str_xmlfileName == null || str_xmlfileName.equals("")) {
				continue;
			}

			String str_xmlfileName_prefix = str_xmlfileName.substring(0, str_xmlfileName.indexOf(".")); //
			String str_filePath = "/com/yusys/biapp/frs/base/database/" + str_xmlfileName_prefix + "_" + str_type + ".xml"; //文件
			//System.err.println("加载文件路径【" + str_filePath + "】"); //

			Document doc_item = readXml(this.getClass().getResourceAsStream(str_filePath)); //读取文件内容
			if (doc_item == null) {
				continue;
			}

			//表结构的xml
			String str_tabxml = ele.attributeValue("tabxml"); //表结构的xml
			Document doc_tabStruct = readXml(this.getClass().getResourceAsStream("/com/yusys/biapp/frs/base/database/" + str_tabxml)); //表结构

			List<Element> recordList = doc_item.getRootElement().elements("record"); //
			HashSet<String> tableSet = new HashSet<String>(); //
			for (int j = 0; j < recordList.size(); j++) {
				Element recordEle = recordList.get(j); //初始化的值
				String str_tableName = recordEle.attributeValue("tabname"); //表名!
				if (!tableSet.contains(str_tableName)) {
					if (tableSet.size() > 0) {
						sb_sql.append("\r\n");
					}
					sb_sql.append("delete from " + str_tableName + ";\r\n"); //
					tableSet.add(str_tableName); //
				}

				Element tabStructElement = findTableStructElement(doc_tabStruct, str_tableName); //找出这个表的结构的element

				sb_sql.append(dataBaseDMO.getCreateInitDataSQLFromXml(recordEle, tabStructElement, "MYSQL") + ";"); //从数据库计算出该表的初始化SQL
				sb_sql.append("\r\n");
			}

			sb_sql.append("\r\n");
		}

		return sb_sql.toString();
	}

	//找出一个表的结构的xml
	private Element findTableStructElement(Document doc_tabStruct, String _tableName) {
		List<Element> tabList = doc_tabStruct.getRootElement().elements("table"); //
		for (int i = 0; i < tabList.size(); i++) {
			Element tabEle = tabList.get(i); //
			String str_tabname = tabEle.attributeValue("name"); //
			if (str_tabname.equalsIgnoreCase(_tableName)) { //
				return tabEle;
			}
		}
		return null;
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
