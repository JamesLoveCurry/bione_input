package com.yusys.bione.plugin.database;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.service.AbstractDownloadFile;

/**
 * 导出所有建表SQL
 * @author xch
 *
 */
public class ExportAllCreateSQLDMO extends AbstractDownloadFile {

	private DataBaseDMO dataBaseDMO = new DataBaseDMO();

	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.DownLoadAsString;
	}

	@Override
	public String getDownLoadContentAsString(JSONObject _par) throws Exception {
		//先找索引xml，再找所有xml
		Document doc = readXml(this.getClass().getResourceAsStream("/com/yusys/bione/plugin/database/1_filelist.xml"));
		ArrayList<String> allXmlTabNameList = new ArrayList<String>(); //
		List<Element> list = doc.getRootElement().elements("xmlfile"); //所有文件内容
		
		String database_type = _par.getString("database_type");//获取数据库类型

		StringBuilder sb_sql = new StringBuilder(); //
		for (int i = 0; i < list.size(); i++) {
			Element ele = list.get(i); //
			String str_xmlfileName = ele.attributeValue("tabxml"); //表结构的XML文件
			String str_filePath = "/com/yusys/biapp/frs/base/database/" + str_xmlfileName;
			System.err.println("加载文件路径【" + str_filePath + "】"); //

			Document doc_item = readXml(this.getClass().getResourceAsStream(str_filePath)); //读取文件内容
			if (doc_item == null) {
				continue;
			}

			List<Element> tabList = doc_item.getRootElement().elements("table"); //
			for (int j = 0; j < tabList.size(); j++) {
				Element tabEle = tabList.get(j); //
				sb_sql.append(dataBaseDMO.getCreateTableSQL(tabEle, database_type, false)); //计算出该表的建表SQL
				sb_sql.append("\r\n");
			}
		}

		return sb_sql.toString();
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
