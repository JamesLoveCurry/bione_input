package com.yusys.bione.plugin.yuformat.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;

/**
 * 工具2,放一些反射执行的方法! var v_sql = {_sql:"delete from ..."}; // 即var v_rt =
 * CABIN.doClassMethodCall
 * ("com.yuchengtech.cabin.base.utils.CabinBSUtil2","testMethod",v_sql);
 * 
 * @author kf0612
 *
 */
public class YuFormatUtil2 {
	private YuFormatUtil bsUtil = new YuFormatUtil(); //

	/**
	 * 从前端传入了这样一个参数,有数组,有特殊字符串: var v_sql = { p1 : [{_sql_1:
	 * "delete from';<!\"$#,'';\" ..",
	 * _sql_2:"delete from ..."},{_key1:"aaa",_key2:"bbb"}]}; //
	 * 
	 * @param _parObj
	 * @return
	 */
	public JSONObject testMethod(JSONObject _parObj) throws Exception {
		System.out.println("执行了....");
		JSONArray array = _parObj.getJSONArray("p1"); //
		for (int j = 0; j < array.size(); j++) {
			JSONObject itemObj = array.getJSONObject(j); //
			String[] str_keys = (String[]) itemObj.keySet().toArray(new String[0]); //
			for (int k = 0; k < str_keys.length; k++) {
				System.out.println("[" + str_keys[k] + "]=[" + itemObj.getString(str_keys[k]) + "]");
			}
		}

		HashVO[] hvs = bsUtil.getHashVOs("select templetname from rpt_cabin_panel_templet"); //
		for (int i = 0; i < hvs.length; i++) {
			System.out.println(hvs[i].getStringValue("templetname")); //
		}
		JSONObject rtObj = new JSONObject(); //
		rtObj.put("msg", "执行成功了..."); //
		return rtObj; //
	}

	// 直接在前端拼好SQL,送到后台!
	public JSONObject executeSQL(JSONObject _parObj) throws Exception {
		List<String> sqlList = new ArrayList<String>(); //
		JSONArray array = _parObj.getJSONArray("_sqls"); //
		for (int i = 0; i < array.size(); i++) {
			JSONObject sqlItem = array.getJSONObject(i); //
			String str_sql = sqlItem.getString("_sql"); //
			sqlList.add(str_sql); //
		}
		bsUtil.executeUpdate(sqlList); // 提交数据库!
		JSONObject rtObj = new JSONObject(); //
		rtObj.put("msg", "删除数据成功!"); //
		return rtObj; //
	}

	//SQL
	public String getSQLWhereByPK(String _pkName, JSONObject _jso) {
		String[] str_pkitems = split(_pkName, ",");
		if (str_pkitems.length == 1) { //如果只有一个
			return " " + str_pkitems[0] + "='" + _jso.getString(str_pkitems[0].toLowerCase()) + "'";
		} else {
			StringBuilder sb_sql = new StringBuilder(); //
			for (int i = 0; i < str_pkitems.length; i++) {
				if (i == 0) {
					sb_sql.append(" " + str_pkitems[i] + "='" + _jso.getString(str_pkitems[i].toLowerCase()) + "' "); //
				} else {
					sb_sql.append(" and " + str_pkitems[i] + "='" + _jso.getString(str_pkitems[i].toLowerCase()) + "' "); //
				}
			}
			return sb_sql.toString(); //
		}

	}

	// 得到登录人员信息.
	public JSONObject getLoginUserInfo(JSONObject par) throws Exception {
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		String str_userId = bioneUser.getUserId(); //
		String str_userName = bioneUser.getUserName(); //
		// System.out.println("[" + str_userId + "][" + str_userName + "]");

		JSONObject jso_rt = new JSONObject();
		jso_rt.put("userid", str_userId); //
		jso_rt.put("username", str_userName); //

		String str_currtime = bsUtil.getCurrDateSecond(); // UI端许多地方要当前时间，按道理应该取server端时间!
															// 因为UI端时间各不一样,无法控制，客户可以自己更改(甚至是恶意的)!
		jso_rt.put("currtime", str_currtime); //

		return jso_rt; //
	}

	// 根据数据生成一个表格的html,经常有【查看相关】的需求!然后不想再弹出iframe的方式，而直接使用Dom弹出一个div，显示一个表格算了!最简洁!
	// 可以一性性的把这个表格弄漂亮点,以后重用很方便,专门用来弹出一个表格显示相关数据!
	public String getTableHtmlByHashVOs(HashVOStruct hvst) {
		String[] str_cols = hvst.getHeaderName(); //
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<center>");
		sb_html.append("<table border=\"1\" style=\"border:1px solid #AAAAAA\" width=\"85%\">");

		// 表头
		sb_html.append("<tr height=\"33px\">");
		sb_html.append("<td>&nbsp;</td>");
		for (int i = 0; i < str_cols.length; i++) {
			sb_html.append("<td style=\"text-align:center;background:#21498F;color:#FFFFFF\">" + str_cols[i] + "</td>");
		}
		sb_html.append("</tr>");

		// 表体
		HashVO[] hvs = hvst.getHashVOs(); //
		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("<tr height=\"27px\">");
			sb_html.append("<td style=\"background:#EEEEEE;text-align:center\">" + (i + 1) + "</td>");
			for (int j = 0; j < str_cols.length; j++) {
				sb_html.append("<td cellspaceing=\"2px\">" + hvs[i].getStringValue(str_cols[j], "&nbsp;") + "</td>");
			}
			sb_html.append("</tr>");
		}

		sb_html.append("</table>");
		sb_html.append("</center>");

		return sb_html.toString();
	}

	// 计算被引用资料的工具方法
	// _thisField 本数据中的哪个字段, chart_id
	// _refTable 哪个表引用他的 rpt_cabin_panel_data
	// reftableField 引用表中关联的字段 chart_id
	// _thisCountField 本HashVO中需要设置count值的字段名! usedcount
	public void computeUsedCount(HashVO[] _hvs, String _thisField, String _refTable, String _refTableField, String _thisCountField) {
		String[] str_ids = bsUtil.getHashVOItemValues2(_hvs, _thisField); // 所有的主键值!
		Map<String, String> countMap = bsUtil.getHashMap("select " + _refTableField + ",count(*) from " + _refTable + " where " + _refTableField + " in (" + bsUtil.getInCondition(str_ids) + ") group by " + _refTableField);
		if (countMap == null || countMap.size() <= 0) {
			System.err.println("根据表中【" + _thisField + "】字段值去基础表【" + _refTable + "】的【" + _refTableField + "】关联查询竟然没有找到一条数据?"); //
			return;
		}
		for (int i = 0; i < _hvs.length; i++) {
			String str_chart_id = _hvs[i].getStringValue(_thisField); //
			String str_count = countMap.get(str_chart_id); //
			if (str_count != null) { //
				_hvs[i].setAttributeValue(_thisCountField, str_count);
			} else {
				_hvs[i].setAttributeValue(_thisCountField, "0");
			}
		}
	}

	// 从一个HashVO[]中找到某个字段的某个值等于指定的值
	public HashVO[] findHashVOs(HashVO[] _hvs, String _itemKey, String _itemValue) {
		return findHashVOs(_hvs, _itemKey, _itemValue, false);
	}

	// 从一个HashVO[]中找到某个字段的某个值等于指定的值
	public HashVO[] findHashVOs(HashVO[] _hvs, String _itemKey, String _itemValue, boolean _isLike) {
		ArrayList<HashVO> dataList = new ArrayList<HashVO>();
		for (int i = 0; i < _hvs.length; i++) {
			String str_value = _hvs[i].getStringValue(_itemKey); //
			if (str_value != null && !str_value.trim().equals("")) { //
				if (_isLike) { // 如果是like
					if (str_value.trim().indexOf(_itemValue) >= 0) { // 如果包含...
						dataList.add(_hvs[i]); //
					}
				} else { // 如果相等
					if (str_value.trim().equals(_itemValue)) {
						dataList.add(_hvs[i]); //
					}
				}

			}
		}
		return dataList.toArray(new HashVO[0]); //
	}

	//返回公式参照的Demo数据
	public HashVO[] getFormulaDemoData(JSONObject _par) {
		System.err.println(_par); //
		try {
			InputStream ins = this.getClass().getResourceAsStream(_par.getString("dataxml")); //
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document doc = reader.read(ins);
			List<Element> list = doc.getRootElement().elements("item"); //取得所有结点!

			HashVO[] hvs_data = new HashVO[list.size()];
			for (int i = 0; i < hvs_data.length; i++) {
				Element ele = list.get(i); //
				String str_name = ele.attributeValue("name"); //
				String str_descr = ele.attributeValue("descr"); //
				String str_text = ele.getText();

				hvs_data[i] = new HashVO(); //
				hvs_data[i].setAttributeValue("name", str_name);
				hvs_data[i].setAttributeValue("datademo", str_text);
				hvs_data[i].setAttributeValue("descr", str_descr);
			}

			return hvs_data;
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * 根据页面模板编码取得Format模板的内容
	 * @param _fileName
	 * @param _code
	 * @return
	 * @throws Exception
	 */
	public HashVO getFormatFrameVO(String _code) throws Exception {
		InputStream ins = null; //
		try {
			ins = this.getClass().getResourceAsStream("/com/yusys/bione/plugin/yuformat/xml/_FormatFrame.xml"); // 	
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document doc = reader.read(ins);

			List list = doc.getRootElement().elements("FormatFrame"); //取得所有结点!
			Iterator itr = list.iterator(); //

			while (itr.hasNext()) {
				Element ele = (Element) itr.next(); //
				Element codeEle = ele.element("formatcode"); //取得FormatCode
				String str_code = codeEle.getText(); //
				//System.err.println("【" + str_code + "】"); //
				if (str_code.equalsIgnoreCase(_code)) {
					return converDomElementToVO(ele); //
				}
			}

			return null; //
		} catch (Exception _ex) {
			throw _ex;
		} finally {
			if (ins != null) {
				try {
					ins.close(); //
				} catch (Exception _exx) {
					_exx.printStackTrace(); //
				}
			}
		}
	}

	//取得模板内容!
	public String getTemplelXmlText(String _templetCode) throws Exception {
		String str_fileName = ""; //
		if (_templetCode.toLowerCase().endsWith(".xml")) {
			str_fileName = _templetCode; //如果是绝对路径
		} else {
			str_fileName = "/biapp-east/freexml/" + _templetCode + ".xml"; //后来移位置了
		}

		InputStream ins = null; //
		try {
			ins = this.getClass().getResourceAsStream(str_fileName); //
			if (ins == null) {
				throw new Exception("模板XML文件不存在【" + str_fileName + "】");
			}

			byte[] bys = bsUtil.readFromInputStreamToBytes(ins); //
			return new String(bys, "UTF-8"); //
		} catch (Exception _ex) {
			System.err.println("解析【" + str_fileName + "】失败!"); //
			throw _ex;
		} finally {
			if (ins != null) {
				try {
					ins.close(); //
				} catch (Exception _exx) {
					_exx.printStackTrace(); //
				}
			}
		}
	}

	//取得模板内容!
	public String getTempletItemXmlText(String _templetCode, String _itemkey) throws Exception {
		String str_fileName = ""; //
		if (_templetCode.toLowerCase().endsWith(".xml")) {
			str_fileName = _templetCode; //如果是绝对路径
		} else {
			str_fileName = "/biapp-east/freexml/" + _templetCode + ".xml"; //后来移位置了
		}

		InputStream ins = null; //
		try {
			ins = this.getClass().getResourceAsStream(str_fileName); //
			if (ins == null) {
				throw new Exception("模板XML文件不存在【" + str_fileName + "】");
			}

			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document doc = reader.read(ins);

			try {
				ins.close(); //立即关闭,提高性能
				ins = null;
			} catch (Exception _ex1) {
				_ex1.printStackTrace(); //
			}

			StringBuilder sb_xml = new StringBuilder(); //
			sb_xml.append("<templet_item>\r\n"); //
			Iterator itr = doc.getRootElement().elements("templet_item").iterator(); //取得所有结点!
			while (itr.hasNext()) {
				Element ele = (Element) itr.next(); //
				HashVO hvo_item = converDomElementToVO(ele); //
				if (hvo_item.getStringValue("itemkey").equalsIgnoreCase(_itemkey)) {
					String[] str_itemkeys = hvo_item.getKeys(); //
					for (int i = 0; i < str_itemkeys.length; i++) {
						sb_xml.append("  <" + str_itemkeys[i] + ">" + hvo_item.getStringValue(str_itemkeys[i], "") + "<" + str_itemkeys[i] + "/>\r\n"); //
					}
					break; //
				}
			}
			sb_xml.append("</templet_item>\r\n"); //
			return sb_xml.toString(); //
		} catch (Exception _ex) {
			System.err.println("解析【" + str_fileName + "】失败!"); //
			throw _ex;
		} finally {
			if (ins != null) {
				try {
					ins.close(); //
				} catch (Exception _exx) {
					_exx.printStackTrace(); //
				}
			}
		}
	}

	//根据类名创建!
	private Object[] getTempletVOByClass(String _templetCode) throws Exception {
		String str_classAll = _templetCode.substring(6, _templetCode.indexOf("(")); //
		String str_className = str_classAll.substring(0, str_classAll.lastIndexOf(".")); //真正的类名!
		String str_methodName = str_classAll.substring(str_classAll.lastIndexOf(".") + 1, str_classAll.length()); //方法名!
		String str_pars = _templetCode.substring(_templetCode.indexOf("(") + 1, _templetCode.indexOf(")")); //参数!
		str_pars = bsUtil.replaceAll(str_pars, "'", ""); //
		String[] str_items = bsUtil.split(str_pars, ","); //
		Class cls = Class.forName(str_className); //
		//System.err.println(str_className+ "||" + str_methodName); //

		Method mhd = cls.getMethod(str_methodName, new Class[] { String[].class }); //
		Object[] objs = (Object[]) mhd.invoke(cls.newInstance(), (Object) str_items); //执行这个方法!
		((HashVO) objs[0]).setAttributeValue("templetcode", _templetCode); //补上模板编码
		return objs; //
	}

	//根据模板编码取得模板VO
	public Object[] getTempletVO(String _templetCode) throws Exception {
		if (_templetCode.startsWith("Class:")) { //类名!!
			Object[] templetVO = getTempletVOByClass(_templetCode); //
			return templetVO; //
		}

		String str_fileName = ""; //
		if (_templetCode.toLowerCase().endsWith(".xml")) {
			str_fileName = _templetCode; //如果是绝对路径
		} else {
			str_fileName = "/biapp-east/freexml/" + _templetCode + ".xml"; //后来移位置了
		}

		InputStream ins = null; //
		try {
			ins = this.getClass().getResourceAsStream(str_fileName); //
			if (ins == null) {
				throw new Exception("模板XML文件不存在【" + str_fileName + "】");
			}
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document doc = reader.read(ins);

			try {
				ins.close(); //立即关闭,提高性能
				ins = null;
			} catch (Exception _ex1) {
				_ex1.printStackTrace(); //
			}

			//模板主表
			Element templetEle = doc.getRootElement().element("templet"); //取得所有结点!
			HashVO hvo_tempelt = converDomElementToVO(templetEle); //
			hvo_tempelt.setAttributeValue("templetcode", _templetCode); //补上模板编码

			ArrayList<HashVO> itemVOList = new ArrayList<HashVO>(); //
			Iterator itr = doc.getRootElement().elements("templet_item").iterator(); //取得所有结点!
			while (itr.hasNext()) {
				Element ele = (Element) itr.next(); //
				HashVO hvo_item = converDomElementToVO(ele); //
				itemVOList.add(hvo_item); //
			}
			HashVO[] hvs_tempelt_b = itemVOList.toArray(new HashVO[0]); //

			//表单的Js代码.
			Element js_cardEle = doc.getRootElement().element("jscode_card"); //取得所有结点!
			String str_jsCode = null; //JS代码
			if (js_cardEle != null) {
				str_jsCode = js_cardEle.getText(); //JS代码
			}
			return new Object[] { hvo_tempelt, hvs_tempelt_b, str_jsCode }; //返回主子表模板VO
		} catch (Exception _ex) {
			System.err.println("解析【" + str_fileName + "】失败!"); //
			throw _ex;
		} finally {
			if (ins != null) {
				try {
					ins.close(); //
				} catch (Exception _exx) {
					_exx.printStackTrace(); //
				}
			}
		}
	}

	//将Dom的element转换成VO
	private HashVO converDomElementToVO(Element _element) {
		HashVO hvo = new HashVO(); //
		Iterator itr = _element.elements().iterator(); //
		while (itr.hasNext()) {
			Element ele = (Element) itr.next(); //
			String str_name = ele.getName(); //
			String str_value = ele.getText(); //
			//System.err.println("[" + str_name + "]=[" + str_value + "]"); //
			if (str_value != null && (str_name.equalsIgnoreCase("pkname") || str_name.equalsIgnoreCase("itemkey"))) {
				hvo.setAttributeValue(str_name, str_value.toLowerCase()); //主键与itemkey要转小写!
			} else {
				hvo.setAttributeValue(str_name, str_value); //
			}
		}

		return hvo;
	}

	//读取数据..
	public String readFromInputStreamToStr(InputStream _ins, String _encoding) {
		try {
			byte[] bys = readFromInputStreamToBytes(_ins); //
			return new String(bys, _encoding); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	// 读取一个输入流,返回其所有字节! 比如读文件
	public byte[] readFromInputStreamToBytes(InputStream _ins) {
		if (_ins == null) {
			return null;
		}
		ByteArrayOutputStream bout = null; //
		try {
			bout = new ByteArrayOutputStream(); // Java官方网站强烈建议使用该对象读流数据,说是更健壮,更平缓,更稳定!!!因为它是一步步读的!对内存与硬盘消耗均友好!
			byte[] bys = new byte[2048]; //
			int pos = -1;
			while ((pos = _ins.read(bys)) != -1) { // 通过循环读取,更流畅,更稳定!!节约内存!
				bout.write(bys, 0, pos); //
			}
			byte[] returnBys = bout.toByteArray(); //
			return returnBys; //
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				if (bout != null) {
					bout.close(); // 关闭输出流!!!
				}
			} catch (Exception exx1) {
			}
			try {
				if (_ins != null) {
					_ins.close(); // 关闭输入流!!
				}
			} catch (Exception exx1) {
			}
		}
	}

	//
	public void writeStrToOutputStream(OutputStream _out, String _str, String _enCoding) {
		try {
			writeBytesToOutputStream(_out, _str.getBytes(_enCoding)); // 中文的!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	// 将一个字节数组写到文件中去!
	public void writeBytesToOutputStream(OutputStream _out, byte[] _bys) {
		ByteArrayInputStream bins = null; //
		try {
			bins = new ByteArrayInputStream(_bys); // Java官方网站强烈建议使用该对象读流数据,说是更健壮,更平缓,更稳定!!!因为它是一步步读的!对内存与硬盘消耗均友好!
			byte[] tmpbys = new byte[2048]; //
			int pos = -1; //
			while ((pos = bins.read(tmpbys)) != -1) { // 循环读入
				_out.write(tmpbys, 0, pos); // 写入
			}
		} catch (Exception ex) { //
			ex.printStackTrace(); //
		} finally {
			try {
				_out.close(); // 关闭输入流!!
			} catch (Exception exx1) {
			}
			try {
				bins.close(); // 关闭输出流!!!
			} catch (Exception exx1) {
			}
		}
	}

	//替换
	public String replaceAll(String str_par, String old_item, String new_item) {
		if (str_par == null) {
			return null;
		}

		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
		return str_return;
	}

	//分割
	public String[] split(String _par, String _separator) {
		if (_par == null) {
			return null;
		}
		if (_par.trim().equals("")) {
			return new String[0];
		}
		if (_par.indexOf(_separator) < 0) {
			return new String[] { _par };
		}
		ArrayList al_temp = new ArrayList(); //
		String str_remain = _par; //
		int li_pos = str_remain.indexOf(_separator); //
		while (li_pos >= 0) {
			String str_1 = str_remain.substring(0, li_pos); //
			if (str_1 != null && !str_1.trim().equals("")) {
				al_temp.add(str_1); // 加入!!!
			}
			str_remain = str_remain.substring(li_pos + _separator.length(), str_remain.length()); //
			li_pos = str_remain.indexOf(_separator); //
		}

		if (str_remain != null && !str_remain.trim().equals("")) {
			al_temp.add(str_remain); //
		}

		return (String[]) al_temp.toArray(new String[0]); // //
	}

	//替换Html中的双引号，前面加上反义字符,就是有时先写好一个html，然后用这个工具翻译
	public void replaceHtmlFile(String _fileName, String _toFile) {
		try {
			String str_html = readFromInputStreamToStr(new FileInputStream(_fileName), "GBK"); //
			//System.err.println(str_html);
			String[] str_rows = split(str_html, "\r\n"); //

			StringBuffer sb_html = new StringBuffer(); //
			for (int i = 0; i < str_rows.length; i++) {
				String str_item = str_rows[i]; //
				str_item = replaceAll(str_item, "\"", "\\\"");

				String str_newRow = "sb_html.append(\"" + str_item + "\\r\\n\");\r\n";
				sb_html.append(str_newRow); //
				System.out.print(str_newRow); //
			}
			writeStrToOutputStream(new FileOutputStream(_toFile), sb_html.toString(), "GBK"); //
			System.err.println("成功将文件【" + _fileName + "】转换成【" + _toFile + "】"); //
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}

	//把HashVO转换成JSOBject
	public JSONObject convertHashVOToJSO(HashVO _hvo) {
		String[] str_keys = _hvo.getKeys(); //

		JSONObject jso_rt = new JSONObject(); //
		for (int i = 0; i < str_keys.length; i++) {
			jso_rt.put(str_keys[i].toLowerCase(), _hvo.getStringValue(str_keys[i], "")); //
		}
		return jso_rt;
	}

	/**
	 * 取前一日的日期
	 * 格式yyyyMMdd
	 * @return
	 */
	public static String getYestday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		return sdf.format(calendar.getTime());
	}

	/**
	 * 
	 * 数字月份转中文
	 * @return
	 */
	public static String monthDigit2Chinese(String num) {
		if (num.equals("01") || num.equals("1")) {
			return "一月";
		} else if (num.equals("02") || num.equals("2")) {
			return "二月";
		} else if (num.equals("03") || num.equals("3")) {
			return "三月";
		} else if (num.equals("04") || num.equals("4")) {
			return "四月";
		} else if (num.equals("05") || num.equals("5")) {
			return "五月";
		} else if (num.equals("06") || num.equals("6")) {
			return "六月";
		} else if (num.equals("07") || num.equals("7")) {
			return "七月";
		} else if (num.equals("08") || num.equals("8")) {
			return "八月";
		} else if (num.equals("09") || num.equals("9")) {
			return "九月";
		} else if (num.equals("10") || num.equals("10")) {
			return "十月";
		} else if (num.equals("11") || num.equals("11")) {
			return "十一月";
		} else if (num.equals("12") || num.equals("12")) {
			return "十二月";
		}
		return "";
	}
}
