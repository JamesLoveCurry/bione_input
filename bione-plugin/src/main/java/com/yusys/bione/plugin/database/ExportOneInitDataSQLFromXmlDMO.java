package com.yusys.bione.plugin.database;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.service.AbstractDownloadFile;

/**
 * 导出一个模块的初始化数据为xml
 * @author xch
 *
 */
public class ExportOneInitDataSQLFromXmlDMO extends AbstractDownloadFile {

	private DataBaseDMO dataBaseDMO = new DataBaseDMO();

	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.DownLoadAsString;
	}

	@Override
	public String getDownLoadContentAsString(JSONObject _par) throws Exception {
		String str_xmlfile = _par.getString("xmlfile"); //
		String str_type = _par.getString("type"); //

		String str_xmlfile_prefix = str_xmlfile.substring(0, str_xmlfile.indexOf(".")); //是【bione_data】

		String str_filePath = "/bione-plugin/database/" + str_xmlfile_prefix + "_" + str_type + ".xml"; //
		//System.err.println("加载文件路径【" + str_filePath + "】"); //

		Document doc_item = readXml(this.getClass().getResourceAsStream(str_filePath)); //读取文件内容
		if (doc_item == null) {
			throw new Exception("文件【" + str_filePath + "】不存在!"); //
		}

		StringBuilder sb_sql = new StringBuilder(); //

		//所有记录清单
		List<Element> recordList = doc_item.getRootElement().elements("record"); //

		HashSet<String> tableSet = new HashSet<String>(); //

		//遍历所有记录,然后反向生成SQL
		for (int j = 0; j < recordList.size(); j++) {
			Element recordEle = recordList.get(j); //
			String str_tableName = recordEle.attributeValue("tabname"); //表名
			if (!tableSet.contains(str_tableName)) {
				if (tableSet.size() > 0) {
					sb_sql.append("\r\n");
				}
				sb_sql.append("delete from " + str_tableName + ";\r\n"); //
				tableSet.add(str_tableName); //
			}
			sb_sql.append(dataBaseDMO.getCreateInitDataSQLFromXml(recordEle, null, "MYSQL")); //从数据库计算出该表的初始化SQL
			sb_sql.append(";\r\n");
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
