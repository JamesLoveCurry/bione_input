package com.yusys.bione.plugin.yuformat.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.drivercfg.utils.DriverCfgUtils;
import com.yusys.bione.plugin.yuformat.service.YuFormatBS;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * 查询数据库的BS工具类
 * 
 */
public class YuFormatUtil {

	private static Logger logger = LoggerFactory.getLogger(YuFormatUtil.class);

	public static final String CommDMOClassName = "com.yusys.bione.plugin.yuformat.service.CommDMO";

	public static String getUserNo() {
		return BioneSecurityUtils.getCurrentUserInfo().getUserNo(); //
	}

	public static String getUserName() {
		return BioneSecurityUtils.getCurrentUserInfo().getUserName(); //
	}

	public static String getOrgNo() {
		return BioneSecurityUtils.getCurrentUserInfo().getOrgNo(); //
	}

	//取得所有系统表,如果发生异常,则返回null
	public String[][] getAllSysTables() {
		YuFormatBS yuFormatBS = SpringContextHolder.getBean("yuFormatBS");
		return yuFormatBS.getAllSysTables(); //
	}

	public HashVO[] getHashVOs(String _sql) {
		return getHashVOs(null, _sql, null, true); //
	}

	public HashVO[] getHashVOsByDS(String _ds, String _sql) {
		return getHashVOs(_ds, _sql, null, true); //
	}
	
	/**
	 * oracle专用的分页查找方法，其他数据库需要用这个方法则需要更改YuFormatBs中相应的方法
	 * @param _sql
	 * @param begin
	 * @param end
	 * @return
	 */
	public HashVO[] getHashVOsByPages(String _sql, int begin, int end) {
		_sql = "select * from (select a.*, rownum rn from ("+_sql+") a where rownum <= "+end+") where rn > "+begin;
		return getHashVOs(null, _sql, null, true); //
	}

	public HashVO[] getHashVOs(String _sql, boolean _isDebugLog) {
		return getHashVOs(null, _sql, null, _isDebugLog); //
	}

	public HashVO[] getHashVOs(String _sql, Object[] _pars) {
		return getHashVOs(null, _sql, _pars, true); //
	}

	// 指定数据源查询!
	public HashVO[] getHashVOsByDS(String _ds, String _sql, Object[] _pars) {
		return getHashVOs(_ds, _sql, _pars, true); //
	}

	public HashVO[] getHashVOs(String _ds, String _sql, Object[] _pars, boolean _isDebugLog) {
		YuFormatBS yuFormatBS = SpringContextHolder.getBean("yuFormatBS");
		return yuFormatBS.getHashVOs(_ds, _sql, _pars, _isDebugLog); //
	}

	public HashMap<String, String> getHashMap(String _sql) {
		return getHashMap(_sql, null, true); //
	}

	public HashMap<String, String> getHashMap(String _sql, boolean _isDebugLog) {
		return getHashMap(_sql, null, _isDebugLog); //
	}

	public HashMap<String, String> getHashMap(String _sql, Object[] _pars, boolean _isDebugLog) {
		HashVO[] hvs = getHashVOs(null, _sql, _pars, _isDebugLog); //
		if (hvs == null || hvs.length <= 0) {
			return null; //
		}

		HashMap<String, String> dataMap = new LinkedHashMap<String, String>(); //
		for (int i = 0; i < hvs.length; i++) {
			dataMap.put(hvs[i].getStringValue(0), hvs[i].getStringValue(1)); //
		}
		return dataMap; //
	}

	/**
	 * 返回带表头结构的数据
	 * 
	 * @param _sql
	 * @return
	 */
	public HashVOStruct getHashVOStruct(String _sql) {
		return getHashVOStruct(null, _sql, null, true); //
	}

	public HashVOStruct getHashVOStructByDS(String _ds, String _sql) {
		return getHashVOStruct(_ds, _sql, null, true); //
	}

	public HashVOStruct getHashVOStruct(String _sql, boolean _isDebugLog) {
		return getHashVOStruct(null, _sql, null, _isDebugLog); //
	}

	public HashVOStruct getHashVOStruct(String _sql, Object[] _pars) {
		return getHashVOStruct(null, _sql, _pars, true); //
	}

	public HashVOStruct getHashVOStruct(String _ds, String _sql, Object[] _pars, boolean _isDebugLog) {
		YuFormatBS yuFormatBS = SpringContextHolder.getBean("yuFormatBS");
		return yuFormatBS.getHashVOStruct(_ds, _sql, _pars, _isDebugLog); //
	}

	// 执行SQL
	public int executeUpdate(String _sql) throws Exception {
		YuFormatBS yuFormatBS = SpringContextHolder.getBean("yuFormatBS");
		return yuFormatBS.executeUpdate(_sql); //
	}

	// 执行SQL--根据数据源执行
	public int executeUpdate(String _ds, String _sql) throws Exception {
		YuFormatBS yuFormatBS = SpringContextHolder.getBean("yuFormatBS");
		return yuFormatBS.executeUpdate(_ds, _sql); //
	}

	// 批量执行SQL.
	public int[] executeUpdate(String[] _sqls) throws Exception {
		YuFormatBS yuFormatBS = SpringContextHolder.getBean("yuFormatBS");
		return yuFormatBS.executeUpdate(_sqls); //
	}

	// 批量执行SQL
	public int[] executeUpdate(List<?> _sqlList) throws Exception {
		String[] str_sqls = new String[_sqlList.size()]; //
		for (int i = 0; i < str_sqls.length; i++) {
			Object item = _sqlList.get(i); //
			if (item instanceof String) {
				str_sqls[i] = (String) item; //
			} else if (item instanceof InsertSQLBuilder) {
				str_sqls[i] = ((InsertSQLBuilder) item).getSQL(); //
			} else if (item instanceof UpdateSQLBuilder) {
				str_sqls[i] = ((UpdateSQLBuilder) item).getSQL(); //
			} else {
				str_sqls[i] = item.toString(); //
			}
		}
		return executeUpdate(str_sqls); //
	}

	/**
	 * 宏返回一个字特串中包括${dsdfsd}的符代码串列表
	 * 
	 * @return 字符串,将其中${dssss}专门挖出来形成一行! 如果是"abcd${123}dfwer${98432}kpsdf";
	 *         返回"abcd","${123}","dfwer","${98432}","kpsdf"
	 */
	public String[][] getMacroList(String _inittext, String _str1, String _str2) {
		List<String[]> al_text = new ArrayList<String[]>(); // //
		int li_type = 1; // 不断是指明是开始还是结束.
		String str_remain = _inittext; //
		int li_len2 = _str2.length(); // 第二个字符串的长度.
		for (;;) { // 死循环遍历!!
			if (li_type == 1) {
				int li_pos = str_remain.indexOf(_str1); // 如果找到
				if (li_pos >= 0) {
					if (li_pos > 0) {
						al_text.add(new String[] { str_remain.substring(0, li_pos), "N" }); // 加入
					}
					str_remain = str_remain.substring(li_pos, str_remain.length()); // 剩下的
					li_type = 2; // 指明下一循环是找}
				} else {
					break;
				}
			} else if (li_type == 2) { // 这才是真正的数据!
				int li_pos = str_remain.indexOf(_str2); // 如果找到
				if (li_pos >= 0) {
					al_text.add(new String[] { str_remain.substring(0, li_pos + li_len2), "Y" }); // 加入
					str_remain = str_remain.substring(li_pos + li_len2, str_remain.length()); //
					li_type = 1; // 指明下一循环找头
				} else {
					break;
				}
			}
		}
		if (!str_remain.equals("")) {
			al_text.add(new String[] { str_remain, "N" }); //
		}
		return al_text.toArray(new String[0][0]);
	}

	/**
	 * 从一个字符串找到以前后辍品配的一段宏代码,即宏代码参数 比如字符串 abcd{kk1}wee{kk2}pp,返回的就是数组kk1,kk2
	 * 
	 * @param _inittext
	 * @param _prefix
	 * @param _subfix
	 * @return
	 * @throws Exception
	 */
	public String[] getFormulaMacPars(String _inittext, String _prefix, String _subfix) {
		List<String> al_temp = new ArrayList<String>(); //
		String str_remain = _inittext;
		int li_pos_1 = str_remain.indexOf(_prefix); //
		while (li_pos_1 >= 0) {
			String str_leftsubfix = str_remain.substring(li_pos_1 + _prefix.length(), str_remain.length()); //
			int li_pos_2 = str_leftsubfix.indexOf(_subfix); // 必须有后辍!!!必须有一对!
			if (li_pos_2 < 0) {// {}也是允许的。把<=0改为<0。否则报错！[郝明2012-04-18]
				System.err.println("公式【" + _inittext + "】语句不合法,某个前辍没有对应的后辍!!!!!");
				return new String[0]; //
			}
			al_temp.add(str_leftsubfix.substring(0, li_pos_2)); // 截取!
			str_remain = str_leftsubfix.substring(li_pos_2 + _subfix.length(), str_leftsubfix.length()); //
			li_pos_1 = str_remain.indexOf(_prefix); //
		}
		return al_temp.toArray(new String[0]); //
	}

	// 把定义转换成Map, 以后所有定义都是这样，即【key=value】
	public HashMap<String, String> getDefineMap(String _define) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(); //
		if (_define == null || _define.trim().equals("")) { //如果为空则直接返回Map,而不是返回null,这样调用者少一个!=null的判断
			return map; //
		}
		String[] str_items = getFormulaMacPars(_define, "【", "】");
		for (int i = 0; i < str_items.length; i++) {
			int li_pos = str_items[i].indexOf("="); //
			if (li_pos > 0) {
				String str_key = str_items[i].substring(0, li_pos); //
				String str_value = str_items[i].substring(li_pos + 1, str_items[i].length()); //
				map.put(str_key, str_value); //
			}
		}

		return map; //
	}

	public HashMap<String, String> convertStrToHashMap(String _text) {
		return convertStrToHashMap(_text, ";", "="); // 这是最常用的一种语法
	}

	// 把一个字符串转换成HashMap
	public HashMap<String, String> convertStrToHashMap(String _text, String _str1, String _str2) {
		if (_text == null || _text.trim().equals("")) {
			return null; //
		}
		int li_len2 = _str2.length();
		HashMap<String, String> rtMap = new LinkedHashMap<String, String>(); // 返回Map
		String[] array_1 = split(_text, _str1); // 第一次分割..
		for (int i = 0; i < array_1.length; i++) {
			int li_pos = array_1[i].indexOf(_str2); //
			if (li_pos < 0) {
				rtMap.put(array_1[i].trim(), ""); //
			} else {
				String str_key = array_1[i].substring(0, li_pos); // key
				String str_value = array_1[i].substring(li_pos + li_len2, array_1[i].length()); // value
				rtMap.put(str_key.trim(), str_value.trim()); //
			}
		}
		return rtMap; //
	}

	public int betweenTwoDate(String _date1, String _date2) {
		return betweenTwoDate(_date1, _date2, true);
	}

	// 两个日期相隔天数
	public int betweenTwoDate(String _date1, String _date2, boolean _isGang) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(_isGang ? "yyyy-MM-dd" : "yyyyMMdd");
			java.util.Date date1 = sdf.parse(_date1);
			java.util.Date date2 = sdf.parse(_date2);

			int li_day = (int) ((date2.getTime() - date1.getTime()) / (3600000 * 24)); // 秒
			return li_day; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return 0;
		}
	}

	// 往后推几天
	public String getAfterDay(String _date, int _afters) {
		return getAfterDay(_date, _afters, true);
	}

	// 某一天往后推几天
	public String getAfterDay(String _date, int _afters, boolean _isGang) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(_isGang ? "yyyy-MM-dd" : "yyyyMMdd", Locale.SIMPLIFIED_CHINESE); //
			Calendar c = new GregorianCalendar();
			c.setTime(sdf.parse(_date)); //
			c.add(Calendar.DAY_OF_MONTH, _afters);
			return sdf.format(c.getTime());
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return _date; //
		}
	}

	// 当前时间!
	public String getCurrDate() {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(System.currentTimeMillis()));
	}

	public String getSubCurrDate() {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(System.currentTimeMillis()));
		c.add(Calendar.MONTH, -1);
		Date m = c.getTime();
		return sdf_curr.format(m);
	}

	public String getCurrDate1() {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(System.currentTimeMillis()));
	}

	// 得到秒,在数据复制时经常需要把原来名称加上时间到秒,这样就不容易重名了!
	public String getCurrDateSecond() {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(System.currentTimeMillis()));
	}

	public String getOneDateTime(long _time) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(_time));
	}

	public String getCurrDateSecond1() {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(System.currentTimeMillis()));
	}

	public String getCurrSecond() {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(System.currentTimeMillis()));
	}

	public String getCurrSecond2() {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HHmmss", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(System.currentTimeMillis()));
	}

	// 替换
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

	public String replaceAllEmpty(String str_par) {
		return replaceAllEmpty(str_par, false);
	}

	// 清空字符串中所有空格，换行符！比如流程环节名称中有换行，在提示显示时要清空之!
	public String replaceAllEmpty(String str_par, boolean _isReplaceSpace) {
		if (str_par == null) {
			return null; //
		}
		str_par = StringUtils.remove(str_par, '\r'); //
		str_par = StringUtils.remove(str_par, '\n'); //
		str_par = StringUtils.remove(str_par, '\t'); //
		str_par = str_par.trim(); //
		if (_isReplaceSpace) { // 是否替换里面的空格,有时并不一定想替换中间的空间,即可能中间的空格是原来数据的一部分!
			str_par = StringUtils.remove(str_par, ' '); //
		}
		return str_par; //
	}

	// 分割字符串
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
		List<String> al_temp = new ArrayList<String>(); //
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

		return al_temp.toArray(new String[0]); // //
	}

	// 每隔几位分隔一下!比如处理LinkCode时就需要!(每4位是一段!)
	public String[] split(String _par, int _itemLen) {
		if (_par == null || _par.equals("")) {
			return new String[0]; //
		}
		int li_strLen = _par.length(); // 字符长度
		int li_arraySize = li_strLen / _itemLen; //
		if (li_strLen % _itemLen != 0) {
			li_arraySize = li_arraySize + 1;
		}
		String[] str_return = new String[li_arraySize]; //
		for (int i = 0; i < str_return.length; i++) {
			int li_1 = i * _itemLen; // 0-4,4-8,8-12
			int li_2 = (i + 1) * _itemLen; //
			if (li_2 > li_strLen) {
				str_return[i] = _par.substring(li_1, li_strLen); //
			} else {
				str_return[i] = _par.substring(li_1, li_2); //
			}
		}
		return str_return;
	}

	/**
	 * 将一个大字符分割,指定一行有几列,每一列的宽度!它非常适用将一个大文本分割以行数据的方式存储在一张表中!!
	 * 
	 * @param _str
	 * @param _oneRowCols
	 * @param _oneColWidth
	 * @return
	 */
	public List<String[]> split(String _str, int _oneRowCols, int _oneColWidth) {
		// 压缩后将byte每4000个转换成一个16进制的字符串转换成字符串! 然后每50个拼成一条SQL! 最后插入到表中!!!
		int li_fileLength = _str.length(); // 宽度!
		int li_allColCount = li_fileLength / _oneColWidth; // 有多少列
		int li_left = li_fileLength % _oneColWidth; // 余数!
		if (li_left != 0) { // 如果有余数!
			li_allColCount = li_allColCount + 1; // 列数加1
		}

		int li_rows = li_allColCount / _oneRowCols; // 多少行!!
		if (li_allColCount % _oneRowCols != 0) {
			li_rows = li_rows + 1;
		}

		List<String[]> al_data = new ArrayList<String[]>(); //
		for (int i = 0; i < li_rows; i++) { // 遍历每一行!
			int li_beginCol = i * _oneRowCols; // 累加!
			int li_thisRowCols = 0; // 默认是50,即全的!
			if (i == li_rows - 1) { // 如果是最后一行,则结算该行的最后列!!
				li_thisRowCols = li_allColCount - li_beginCol; // 结尾列!!
			} else {
				li_thisRowCols = _oneRowCols; // 默认就是表的列数
			}
			String[] str_rowData = new String[li_thisRowCols]; // 这一行的数据!!!
			for (int j = 0; j < li_thisRowCols; j++) { // 遍历各列
				int li_substr_1 = (li_beginCol + j) * _oneColWidth; // 截取字符串的起始位置的绝对值!!!
				int li_substr_2 = 0;
				if (i == li_rows - 1 && j == li_thisRowCols - 1) { // 如果是最后一行的最后一列,则可能不满!!
					li_substr_2 = li_fileLength; // 则直接文件的结尾!
				} else {
					li_substr_2 = (li_beginCol + j + 1) * _oneColWidth; //
				}
				str_rowData[j] = _str.substring(li_substr_1, li_substr_2); //
			}
			al_data.add(str_rowData); // 加入这一行数据!!!
		}
		return al_data;
	}

	//拼SQL的in条件..
	public String getInCondition(JSONArray _jsy) {
		String[] str_ids = new String[_jsy.size()];
		for (int i = 0; i < str_ids.length; i++) {
			str_ids[i] = _jsy.getString(i); //
		}
		return getInCondition(str_ids);
	}

	// 转换成SQL条件
	public String getInCondition(String[] _allvalue) {
		if (_allvalue == null || _allvalue.length == 0) {
			return "'-99999'"; // 仿照PowerBuild的异常编码,即-5个9
		} else {
			List<String> list = Arrays.asList(_allvalue); // 转换成一个List
			return getInCondition(list); //
		}
	}

	// 转换成SQL的in条件...
	public String getInCondition(List<String> _sqllist) {
		try {
			if (_sqllist == null || _sqllist.size() == 0) {
				return "'-99999'"; //
			} else {
				String[] str_ids = _sqllist.toArray(new String[0]); //
				str_ids = distinctStrArray(str_ids); // 唯一性过滤
				if (str_ids == null || str_ids.length <= 0) { // 唯一性过滤后,该数组宽度完全可能是0,比如原来的参数就是一个String[]{""},兴业项目中后来就发生
																// 【where id in
																// ()】的情况,所以必须要有这个判断!!【xch/2012-04-28】
					return "'-99999'"; //
				}
				if (str_ids.length <= 999) { // 如果在999个以内,则直接拼起来返回!!!!!!即不会太长了,这样兼容直接in的情况!!!
					StringBuilder sb_alltext = new StringBuilder(); //
					for (int i = 0; i < str_ids.length; i++) { // //
						sb_alltext.append("'" + (str_ids[i] == null ? "" : str_ids[i]) + "'"); // 必须将null转化成"",否则会出现where
																								// id
																								// in
																								// ('12','null','15')，会报错
						if (i != str_ids.length - 1) { // 如果不是最后一个则加上逗号!!!!!
							sb_alltext.append(","); // 如果不是最后一个,则加逗号!!!!!
						}
					}
					return sb_alltext.toString(); //
				} else { // 如果超过1000个要拆成 多个 or in(0!!
					return "'-99997'"; //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "'-99998'"; //
		}
	}

	// 将一个字符串进行唯一性过滤
	public String[] distinctStrArray(String[] _str) {
		return distinctStrArray(_str, true); //
	}

	// 将一个字符串进行唯一性合并后返回
	public String[] distinctStrArray(String[] _str, boolean _isTrim) {
		if (_str == null || _str.length <= 0) {
			return _str;
		}
		Set<String> hst_str = new LinkedHashSet<String>(); //
		for (int i = 0; i < _str.length; i++) {
			if (_isTrim) { // 如果是忽略空格,并且自动去头尾的空格
				if (_str[i] != null && !_str[i].trim().equals("")) { // 如果不能是空值或空串!!!
					hst_str.add(_str[i].trim()); //
				}
			} else {
				if (_str[i] != null) {
					hst_str.add(_str[i]); //
				}
			}
		}
		return hst_str.toArray(new String[0]); //
	}

	// 为了调试方便,将
	public void writeHashToHtmlTableFile(HashVO[] _hvs, String _filename) {
		writeHashToHtmlTableFile(_hvs, _filename, null); //
	}

	public void writeHashToHtmlTableFile(HashVO _hvs[], String _filename, String[] _itemKeys) {
		PrintWriter print = null;
		try {
			print = new PrintWriter(new FileOutputStream(new File(_filename), false));
			StringBuilder sb_html = new StringBuilder(); //
			sb_html.append("<html>\r\n");
			sb_html.append("<head>\r\n");
			sb_html.append("<title>将HashVO数组生成Html</title>\r\n");
			sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=UTF-8\">\r\n"); //
			sb_html.append("<style   type=\"text/css\"> \r\n");
			sb_html.append("<!--   \r\n");
			sb_html.append(" table   {  border-collapse:   collapse; }   \r\n");
			sb_html.append("td   {  font-size: 12px; border:solid   1px   #888888;  }   \r\n");
			sb_html.append(" -->   \r\n");
			sb_html.append(" </style>   \r\n");
			sb_html.append("</head>\r\n");
			sb_html.append("<body>\r\n");
			print.println(sb_html.toString());

			if (_hvs.length > 0) {
				print.println("<table>");
				String[] str_keys = null;
				if (_itemKeys != null) {
					str_keys = _itemKeys;
				} else {
					Set<String> keySet = new LinkedHashSet<String>(); //
					for (int i = 0; i < _hvs.length; i++) { // 遍历各行数据
						String[] str_rowKeys = _hvs[i].getKeys(); //
						for (int j = 0; j < str_rowKeys.length; j++) {
							keySet.add(str_rowKeys[j]); //
						}
					}
					// 找出各行的全集!
					str_keys = keySet.toArray(new String[0]); //
				}
				StringBuilder sb_table = new StringBuilder();
				sb_table.append("<tr><td>序号</td>");
				for (int i = 0; i < str_keys.length; i++) {
					sb_table.append((new StringBuilder("<td>")).append(str_keys[i]).append("</td>").toString());
				}
				sb_table.append("</tr>\r\n");
				print.println(sb_table.toString());
				for (int i = 0; i < _hvs.length; i++) { // 遍历各行数据
					sb_table.setLength(0);
					;
					sb_table.append("<tr>");
					sb_table.append("<td>" + (i + 1) + "</td>");

					for (int j = 0; j < str_keys.length; j++) {
						String str_cellvalue = _hvs[i].getStringValue(str_keys[j], "&nbsp;"); //
						str_cellvalue = str_cellvalue.replace('<', '['); //
						str_cellvalue = str_cellvalue.replace('>', ']'); //
						// if (str_cellvalue.indexOf("<") >= 0 &&
						// str_cellvalue.indexOf(">") >= 0) {
						// str_cellvalue = "【<![CDATA[" + str_cellvalue +
						// "]]>】"; //
						// }
						sb_table.append((new StringBuilder("<td>")).append(str_cellvalue).append("</td>").toString());
					}

					sb_table.append("</tr>");
					print.println(sb_table.toString());
				}

				print.println("</table>");
				print.println("</body>");
				print.println("</html>");
				System.out.println("将一个HashVO[]数组的【" + _hvs.length + "】条记录输出至文件【" + _filename + "】成功!!!"); //
			} else {
				print.println("没有数据!");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(print);
		}
	}

	public static boolean isEmpty(String _value) {
		if (_value == null || _value.trim().equals("")) { // 如果为null或者为空字符串...
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 把double转换成字符串，因为大的double直接toString,会出现一个E,
	 * 另外有时会突然冒出小数点后面许多位!! 所以需要截取
	 * @param _double
	 * @return
	 */
	public String getDoubleToString(double _double) {
		return getDoubleToString(_double, true);
	}

	//有时大Double为null
	public String getDoubleToString(Double _double) {
		if (_double == null) {
			return "";
		} else {
			return getDoubleToString(_double.doubleValue());
		}
	}

	/**
	 * 将一个double转换成string,否则会出现科学计算法!!
	 * double的整数位最大为14位,好象超过14位就不准了!! 最后位数就变成零了,BigDecimal最大是15位!
	 * duble ld_a = 32142332248D,如果没有小数点位,则认为是一个int,int最多为9位,所以要在后面加一个D,即32142332248D
	 * @param _double
	 * @param _isTrimZero 如果为false,则永远有小数点两位,这样会对齐! 如果为true,则如果是整数,5.00，会把最后的零截掉
	 * @return
	 */
	public String getDoubleToString(double _double, boolean _isTrimZero) {
		String str_value = String.format("%.2f", _double);
		if (_isTrimZero) {
			if (str_value.endsWith(".00")) {
				str_value = str_value.substring(0, str_value.length() - 3); //
			} else if (str_value.indexOf(".") > 0 && str_value.endsWith("0")) {
				str_value = str_value.substring(0, str_value.length() - 1); //
			}
		}
		return str_value; //
	}

	/**
	 * 将一个异常的堆栈转换成字符串...
	 * 
	 * @return
	 */
	public String getExceptionStringBuffer(Throwable _ex) {
		StringBuilder sb_errBuffer = new StringBuilder(); //
		if (_ex instanceof java.lang.reflect.InvocationTargetException) { //如果是反射异常,则直接找根本原因.
			appendCauseStr(_ex, sb_errBuffer); // 引发异常的原因
		} else {
			sb_errBuffer.append(getExceptionStackStr(_ex)); //
			appendCauseStr(_ex, sb_errBuffer); // 引发异常的原因
		}
		return sb_errBuffer.toString(); //
	}

	private void appendCauseStr(Throwable _ex, StringBuilder sb_errBuffer) {
		Throwable cause = _ex.getCause();
		if (cause == null || cause == _ex) {
			return; //
		}
		sb_errBuffer.append("异常因为以下原因:\r\n"); //
		String str_errString = getExceptionStackStr(cause); //
		sb_errBuffer.append(str_errString); //
		appendCauseStr(cause, sb_errBuffer); // 继续递归找...
	}

	//
	private String getExceptionStackStr(Throwable _ex) {
		StringBuilder sb_errBuffer = new StringBuilder(); //
		sb_errBuffer.append(_ex.getClass().getName() + "-【" + _ex.getMessage() + "】<br>\r\n");
		StackTraceElement[] statcks = _ex.getStackTrace(); //
		for (int i = 0; i < statcks.length; i++) {
			String className = statcks[i].getClassName();// 类名
			if (className.indexOf("$$") > 0) {
				continue;
			}
			if (className.startsWith("com.yusys.") || className.startsWith("com.yuchengtech.")) { //只显示自己类吧,spring那一堆烦人的堆栈去掉算了!
				String methodName = statcks[i].getMethodName();// 方法名
				int lineNO = statcks[i].getLineNumber();// 行号
				String msg_ex = className + "(" + methodName + ")<" + lineNO + "><br>\r\n";// 拼接
				sb_errBuffer.append(msg_ex);
			}
		}
		return sb_errBuffer.toString(); //
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
				bout.close(); // 关闭输出流!!!
			} catch (Exception exx1) {
			}
			try {
				_ins.close(); // 关闭输入流!!
			} catch (Exception exx1) {
			}
		}
	}

	// 将64位码转换成实际字符串!
	public String convert64CodeToStr(String _64Code) {
		try {
			byte[] bytes = convert64CodeToBytes(_64Code); //
			if (bytes == null) {
				return null;
			}
			return new String(bytes, "GBK"); //
		} catch (Exception e) {
			e.printStackTrace(); //
			return null; //
		}
	}

	// 将64位码转换成byte[],经常需要用到!
	public byte[] convert64CodeToBytes(String _64Code) {
		try {
			byte[] bytes = new sun.misc.BASE64Decoder().decodeBuffer(_64Code); //
			return bytes; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	// 将字节码转换成64位码!经常需要用到!比如将文件进行字符串方式存储时! 比如将文件上传进数据库中!!
	public String convertBytesTo64Code(byte[] _bytes) {
		return Base64.encodeBase64String(_bytes); // 64位码
	}
	
	// 将32位md5码转为带横杠的md5码
	public String convertMd5ToMd5WithBar(String md5) {
		return md5.substring(0,8)+"-"+md5.substring(8,12)+"-"+md5.substring(12,16)+"-"+md5.substring(16,20)+"-"+md5.substring(20,32);
	}
	
	// 将布尔值转换为中文
	public String convertBooleanToCN(boolean flag) {
		if(flag) {
			return "是";
		}else {
			return "否";
		}
	}

	/**
	 * 对一个HashVO进行排序,使用方法是 sortHashVOs(hvs,new
	 * String[][]{{"code","N","N"},{"age","Y","Y"}});
	 * 
	 * @param _hvs
	 *            , 就是想要排序的哈希数组..
	 * @param _sortColumns
	 *            ,指定排序列,n行3列的数据, 第一个布尔值表示是否是倒序(Y=倒序,N=升序),
	 *            第二个布尔值表示是否是数字型(Y=数字,N=字符),比如： new
	 *            String[][]{{"code","N","N"},{"age","Y","Y"}}
	 *            //先对工号按升序排(字符型),如果相同再按年龄倒序排(数字型)
	 */
	public void sortHashVOs(HashVO[] _hvs, String[][] _sortColumns) {
		Arrays.sort(_hvs, new HashVOComparator(_sortColumns));
	}

	/**
	 * 对HashVO[]某个列中的值，根据指定的数组进行排序!!
	 * 
	 * @param _hvs
	 * @param
	 */
	public void sortHashVOs(HashVO[] _hvs, String _itemKey, String[] _orders) {
		Arrays.sort(_hvs, new HashVOComparator(_itemKey, _orders));
	}

	// 得到HashVO中某一列的所有数据..
	public String[] getHashVOItemValues(HashVO[] _hvs, String _itemKey) {
		List<String> dataList = new ArrayList<String>(); //
		for (int i = 0; i < _hvs.length; i++) {
			String str_value = _hvs[i].getStringValue(_itemKey); //
			if (str_value != null && !str_value.trim().equals("")) { //
				dataList.add(str_value); //
			}
		}
		return dataList.toArray(new String[0]); //
	}

	// 得到HashVO中某一列的所有数据,但做唯一性过滤(前面方法是不做唯一性过滤!).
	public String[] getHashVOItemValues2(HashVO[] _hvs, String _itemKey) {
		Set<String> dataList = new LinkedHashSet<String>(); //
		for (int i = 0; i < _hvs.length; i++) {
			String str_value = _hvs[i].getStringValue(_itemKey); //
			if (str_value != null && !str_value.trim().equals("")) { //
				dataList.add(str_value); //
			}
		}
		return dataList.toArray(new String[0]); //
	}

	// 模板信息自己的
	public String getTempletOptionSelf(HashVO parentVO, HashVO[] hvs_b) {
		StringBuilder sb_html = new StringBuilder(); //
		// 输出模板主表的全部信息
		sb_html.append("{\r\n");
		sb_html.append("templet_option:{\r\n");
		String[] str_keys = parentVO.getKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			String str_value = parentVO.getStringValue(str_keys[i], ""); //
			str_value = replaceAll(str_value, "\"", "\\\""); // 把所有双引号改成单引号,因为本身是双引号
			str_value = replaceAll(str_value, "\r", "");
			str_value = replaceAll(str_value, "\n", "");
			sb_html.append(str_keys[i] + ":\"" + str_value + "\"");
			if (i != str_keys.length - 1) {
				sb_html.append(",");
			}
			sb_html.append("\r\n");
		}
		sb_html.append("},\r\n");

		// 输出模板子表
		sb_html.append("templet_option_b:[\r\n");
		for (int i = 0; i < hvs_b.length; i++) {
			sb_html.append("{\r\n");
			String[] str_itemKeys = hvs_b[i].getKeys(); // 子表所有的列
			String str_thisItemTypeValue = hvs_b[i].getStringValue("itemtype", "");  //控件类型
			for (int j = 0; j < str_itemKeys.length; j++) { //所有字段
				String str_itemValue = hvs_b[i].getStringValue(str_itemKeys[j]); //
				if (str_thisItemTypeValue.equals("Label") && str_itemKeys[j].endsWith("itemdefine")) {  //如果是Label类型则itemdefine强行置空,因为有的配置有特殊字符
					str_itemValue = ""; //
				}
				str_itemValue = replaceAll(str_itemValue, "\"", "\\\""); // 把所有双引号改成单引号,因为本身是双引号
				str_itemValue = replaceAll(str_itemValue, "\r", "");
				str_itemValue = replaceAll(str_itemValue, "\n", "");
				sb_html.append(str_itemKeys[j] + ":\"" + str_itemValue + "\"");
				if (j != str_itemKeys.length - 1) {
					sb_html.append(",");
				}
				sb_html.append("\r\n");
			}
			sb_html.append("}");
			if (i != hvs_b.length - 1) {
				sb_html.append(",");
			}
			sb_html.append("\r\n");
		}
		sb_html.append("]\r\n"); // 子表结尾
		sb_html.append("}"); // 结尾
		return sb_html.toString(); //
	}

	// 输入JSP的Head
	public String getJspHeadHtml(HttpServletRequest _request) {
		String str_context = _request.getContextPath(); //
		boolean flag = false;
		//需要引入的js,insert与update
		String str_jsfile = _request.getParameter("js");//
		// 文件必须是js结尾并且只包含大小写字母 数字 下划线 斜杠 -和.
		if (!str_jsfile.endsWith(".js") || !StringUtils2.isPath(str_jsfile)) {
			flag = true;
		}
		//入参
		String str_OpenPars = _request.getParameter("OpenPars"); //
		String str_OpenPars_decode = null; //
		JSONObject jso_OpenPars = null; //
		boolean isDecodeError = false; //
		// 处理入参!
		if (str_OpenPars != null) {
			try {
				str_OpenPars_decode = URLDecoder.decode(URLDecoder.decode(str_OpenPars, "UTF-8"), "UTF-8"); //有可能有异常!
				if (!isJSON(str_OpenPars_decode)) {
					str_OpenPars_decode = "";
				}
				jso_OpenPars = JSON.parseObject(str_OpenPars_decode); //解析成JSON对象,因为用到这里面的参数
				if (jso_OpenPars == null) {
					str_OpenPars_decode = "";
				}
			} catch (Exception _ex) {
				_ex.printStackTrace(); //
				isDecodeError = true; //
			}
		}
		// // 防范 发射型XSS工具
		// if (jso_OpenPars != null && jso_OpenPars.size() == 0) {
		// 	logger.info("开始处理错误。。。。。。。。。。。。。。。。。。");
		// 	str_OpenPars_decode = "";
		// }
		StringBuilder sb_html = new StringBuilder();
		sb_html.append("<meta charset=\"UTF-8\">\r\n");
		sb_html.append("<meta http-equiv=\"Cache-Control\" content=\"no-cache\">\r\n");
		sb_html.append("<meta http-equiv=\"Pragma\" content=\"no-cache\">\r\n");
		sb_html.append("<meta http-equiv=\"Expires\" content=\"0\">\r\n");
		sb_html.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n");
		sb_html.append("<meta content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\" name=\"viewport\">\r\n");

		sb_html.append("<title></title>\r\n");
		//String str_url = _request.getRequestURI();
		//sb_html.append("<!-- " + str_url + " -->\r\n");

		long ll_currtime = System.currentTimeMillis(); //

		String str_jsversion = ""; // js的版本号,网上说如果带版本号则每次都下载新版本!
		if ("Y".equalsIgnoreCase(System.getProperty("yxdebug"))) {
			str_jsversion = "?version=" + ll_currtime;
		} else {
			String str_sysStartTime = System.getProperty("BIONE-StartTime"); //
			if (str_sysStartTime != null) {
				str_jsversion = "?version=" + str_sysStartTime;
			}
		}

		// jquery,easyui的css与js/biapp-frs-web/src/main/webapp/js/jqueryEasyUI/locale/easyui-lang-zh_CN.js
		sb_html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + str_context + "/js/jqueryEasyUI/themes/default/easyui.css\">\r\n");
		sb_html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + str_context + "/js/jqueryEasyUI/themes/icon.css\">\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/js/jqueryEasyUI/jquery.min.js\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/js/jqueryEasyUI/jquery.easyui.min.js\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/js/jqueryEasyUI/easyui-lang-zh_CN.js\"></script>\r\n");
		//		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/js/require/require.js\"></script>\r\n\r\n");

		//echarts图表
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/js/echarts4/js/echarts.js\"></script>\r\n");

		//jspdf
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/js/jspdf/jspdf.min.js\"></script>\r\n");
		//html2canvas
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/js/html2canvas/html2canvas.min.js\"></script>\r\n");

		sb_html.append("\r\n"); //
		// 自己的两个js
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/frame/js/yuformat/freeutil.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/frame/js/yuformat/jspfree.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/frame/js/yuformat/jsencrypt.min.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/yujs/safe/utils/safefreeutil.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/yujs/bfd/utils/bfdfreeutil.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/yujs/imas/utils/imasfreeutil.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/yujs/pscs/utils/pscsfreeutil.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/yujs/cr/utils/crfreeutil.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/yujs/east/utils/eastfreeutil.js" + str_jsversion + "\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "/yujs/crrs/utils/crrsfreeutil.js" + str_jsversion + "\"></script>\r\n");

		//如果有js2,即内部js，在新增与修改界面时,表单内部的联动需要js逻辑,但这种界面是平台封装死了的,没有办法写js
		//所以这种平台封装死的功能界面(比如卡片新增/修改),需要一种机制可以传入第二个js文件
		if (jso_OpenPars != null && jso_OpenPars.containsKey("innerjs2")) {
			String str_innerjs2 = jso_OpenPars.getString("innerjs2"); //
			sb_html.append("\r\n");
			sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "" + str_innerjs2 + "" + str_jsversion + "\"></script>\r\n");
		}

		// 如果传入了js文件,则是输出js
		if (str_jsfile != null) {
			sb_html.append("\r\n");
			sb_html.append("<!--  ★★★★★★★★★★★★★★★★★★★★★★  点击下面js文件链接查看该页面实际逻辑↓  ★★★★★★★★★★★★★★★  -->\r\n");
			sb_html.append("<script type=\"text/javascript\" src=\"" + str_context + "" + str_jsfile + "" + str_jsversion + "\"></script>\r\n");
		}

		sb_html.append("\r\n"); //

		// webcontext要传到JS端
		sb_html.append("<script type=\"text/javascript\">\r\n");
		sb_html.append("var v_context  = \"" + str_context + "\";\r\n");

		// 处理isopen参数
		String str_isopen = _request.getParameter("isopen"); //
		if ("Y".equals(str_isopen)) {
			sb_html.append("var v_isopen = \"Y\";\r\n");
		} else {
			sb_html.append("var v_isopen = \"N\";\r\n");
		}

		//是否是POC状态
		String str_isPOC = System.getProperty("isPOC"); //
		if ("Y".equals(str_isPOC)) {
			sb_html.append("var v_isPOC = \"Y\";\r\n");
		} else {
			sb_html.append("var v_isPOC = \"N\";\r\n");
		}

		//输入登录人员姓名与编号
		String str_loginUserCode = BioneSecurityUtils.getCurrentUserInfo().getUserNo();
		String str_loginUserName = BioneSecurityUtils.getCurrentUserInfo().getUserName();
		String str_loginUserOrgNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo(); //登录的人员内部标识机构号
		sb_html.append("var str_LoginUserCode = \"" + str_loginUserCode + "\";\r\n");
		sb_html.append("var str_LoginUserName = \"" + str_loginUserName + "\";\r\n");
		sb_html.append("var str_LoginUserOrgNo = \"" + str_loginUserOrgNo + "\";\r\n"); // 当前登录人机构号对应rpt_org_info中mgr_org_no，而非org_no

		//如果解析成功了参数
		if (StringUtils.isNotBlank(str_OpenPars_decode)) {
			sb_html.append("var jso_OpenPars = " + str_OpenPars_decode + ";\r\n");
		} else {
			if (isDecodeError) {
				sb_html.append("//解析OpenPars[" + str_OpenPars + "]发生异常\r\n");
			}else{
				sb_html.append("var jso_OpenPars ='';\r\n");
			}
		}

		// 如果从url中传入了其他参数,则也输出到前端!
		Enumeration nameList = _request.getParameterNames(); //
		while (nameList.hasMoreElements()) {
			String str_name = "" + nameList.nextElement(); //
			if (str_name.equalsIgnoreCase("isopen") || str_name.equals("OpenPars")) { // 这两个比较特殊,需要路过
				continue; //
			}
			String str_value = _request.getParameter(str_name); //
			if ("js".equalsIgnoreCase(str_name) && flag){
				continue;
			}
			sb_html.append("var v_" + str_name + "=\"" + str_value + "\";\r\n");
		}
		sb_html.append("</script>\r\n");

		return sb_html.toString(); //
	}
	
	public Map<String,String> getDataBaseparam(String...params){
		Map<String,String> map = new HashMap<String,String>();
		java.util.Properties configProp = new java.util.Properties();
    	try {
			configProp.load(this.getClass().getClassLoader().getResourceAsStream("database.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

    	for(String param : params){
    		String value = configProp.getProperty(param);
    		if(StringUtils.isEmpty(value) ){
    			throw new RuntimeException("请检查配置文件 database.properties中,是否配置“"+param+"” 具体值！");
        	}
    		if(param.indexOf(".") != -1){
    			map.put(param.substring(param.lastIndexOf(".")+1, param.length()), value);
    		}
    		else{
    			map.put(param, value);
    		}
    	}
		return map;
	}
	//做利率报备迁移时暂时
	public Map<String,String> getDataBaseparam(String properties,String...params){
		Map<String,String> map = new HashMap<String,String>();
		java.util.Properties configProp = new java.util.Properties();
		try {
			configProp.load(this.getClass().getClassLoader().getResourceAsStream(properties));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(String param : params){
			String value = configProp.getProperty(param);
			if(StringUtils.isEmpty(value) ){
				throw new RuntimeException("请检查配置文件"+properties+"中,是否配置“"+param+"” 具体值！");
			}
			if(param.indexOf(".") != -1){
				map.put(param.substring(param.lastIndexOf(".")+1, param.length()), value);
			}
			else{
				map.put(param, value);
			}
		}
		return map;
	}

	/**
	 * 获取错误明细表的，表字段
	 * @param table
	 * @param dsName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getTableInfo(String table, String dsName) {
		String dsType = DriverCfgUtils.getDriverType();
		
		List result = new ArrayList();

		Connection conn = null;
		DatabaseMetaData dbmd = null;

		try {
			conn = createDatabaseConnectionByDS(dsName);
			dbmd = conn.getMetaData();

			String newConn = getSchema(conn);
			
			if (dsType.equalsIgnoreCase("oracle")) {
				table = table.toUpperCase();
			} else if (dsType.equalsIgnoreCase("mysql")) {
				table = table.toUpperCase();
			} else if (dsType.equalsIgnoreCase("postgresql")) {
				table = table.toLowerCase();
				newConn = newConn.toLowerCase();
			}
			ResultSet resultSet = dbmd.getTables(null, "%", table, new String[] { "TABLE" });
			int i = 1;
			while (resultSet.next() && i == 1) {
				i = 2;
				String tableName = resultSet.getString("TABLE_NAME");
				if (dsType.equalsIgnoreCase("oracle")) {
					tableName = tableName.toUpperCase();
				} else if (dsType.equalsIgnoreCase("mysql")) {
					tableName = tableName.toUpperCase();
				} else if (dsType.equalsIgnoreCase("postgresql")) {
					tableName = tableName.toLowerCase();
				}
				
				if (tableName.equalsIgnoreCase(table)) {
					ResultSet rs = conn.getMetaData().getColumns(null, newConn, tableName, "%");
					
					while (rs.next()) {
						String colName = rs.getString("COLUMN_NAME");
						result.add(colName);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private static String getSchema(Connection conn) throws Exception {
		String schema;
		schema = conn.getMetaData().getUserName();
		if ((schema == null) || (schema.length() == 0)) {
			throw new Exception("ORACLE数据库模式不允许为空");
		}

		return schema.toUpperCase().toString();
	}
	/**
	 * 创建数据库连接
	 * @param _dsName
	 * @return
	 * @throws Exception
	 */
	public Connection createDatabaseConnectionByDS(String _dsName) throws Exception {
		if (org.apache.commons.lang.StringUtils.isBlank(_dsName)) {
			// 如果参数是null，则从database中获取数据源
			Properties props = new Properties();
			InputStream inputStream = null;
			try {
				inputStream = this.getClass().getClassLoader().getResourceAsStream("database.properties");
				props.load(inputStream);
				props.put("remarksReporting", "true");
				props.put("user", props.getProperty("jdbc.username"));
				props.put("password", props.getProperty("jdbc.password"));
				Class.forName(props.getProperty("jdbc.driverClassName"));
				Connection dbConn = DriverManager.getConnection(props.getProperty("jdbc.url"), props);
				return dbConn;
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		HashVO hvo = findDsVOByName(_dsName);

		String str_driver_name = hvo.getStringValue("driver_name"); //
		String str_conn_url = hvo.getStringValue("conn_url"); //
		String str_conn_user = hvo.getStringValue("conn_user"); //
		String str_conn_pwd = hvo.getStringValue("conn_pwd"); //

		Properties props = new Properties();
		props.put("user", str_conn_user);
		props.put("password", str_conn_pwd);

		Class.forName(str_driver_name);
		Connection dbConn = DriverManager.getConnection(str_conn_url, props);

		return dbConn;
	}

	/**
	 * 根据数据源名称取得数据源
	 * @param _dsName
	 * @return
	 */
	private HashVO findDsVOByName(String _dsName) {
		HashVO[] hvs_ds = getAllDataSourceVOs();
		for (int i = 0; i < hvs_ds.length; i++) {
			if (hvs_ds[i].getStringValue("ds_name").equals(_dsName)) {
				return hvs_ds[i];
			}
		}
		return null;
	}

	// 取得所有数据源.
	public HashVO[] getAllDataSourceVOs() {
		String str_sql = "select t1.ds_name,t2.driver_name,t1.conn_url,t1.conn_user,t1.conn_pwd from bione_ds_info t1,bione_driver_info t2 where t1.driver_id=t2.driver_id"; //
		HashVO[] hvs_data = getHashVOs(str_sql);
		return hvs_data;
	}

	// 数据库连接
	public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
		Properties props = new Properties();
		InputStream is = null;
		Connection dbConn = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream("database.properties");
			props.load(is);
			props.put("remarksReporting", "true");
			props.put("user", props.getProperty("jdbc.username"));
			props.put("password", props.getProperty("jdbc.password"));
			Class.forName(props.getProperty("jdbc.driverClassName"));
			dbConn = DriverManager.getConnection(props.getProperty("jdbc.url"), props);
			return dbConn;
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 根据数据源获取数据库类型
	 * @param
	 * @return
	 */
	public String getDsTypeByDsName(String dsname) {
		String sql = "select t1.ds_name,t2.driver_type as driver_type from bione_ds_info t1 left join bione_driver_info t2"
				+" on t1.driver_id=t2.driver_id where t1.ds_name='"+dsname+"'";
		HashVO[] vo = getHashVOs(sql);
		String dsName = "";
		if(vo.length>0) {
			dsName = vo[0].getStringValue("driver_type");
		}

		return dsName;
	}
	public static void main(String[] args) throws  Exception{
		String str_OpenPars_decode = "{\"ds_name\":\"frs1\",\"separate_num\":\"25\",\"execute_sql\":\"\",\"tab_name\":\"个人客户信息表\",\"ds_url\":\"jdbc:mysql://192.168.251.155:3306/frs1?useUnicode=true\"}";
		JSONObject jso_OpenPars = JSON.parseObject(str_OpenPars_decode); //解析成JSON对象,因为用到这里面的参数
	}

	/**
	 * @方法描述: 判断是否为json格式
	 * @创建人: miaokx@yusys.com.cn
	 * @创建时间: 2021/8/20 12:29
	 * @Param: str
	 * @return: boolean
	 */
	public static boolean isJSON(String str) {
		boolean result = false;
		try {
			Object obj= org.mortbay.util.ajax.JSON.parse(str);
			if (obj!= null && StringUtils.isNotBlank(obj.toString())) {
				result = true;
			}
		} catch (Exception e) {
			result=false;
		}
		return result;
	}

	/***
	 * @方法描述: 获取字段模板信息
	 * @创建人: miaokx@yusys.com.cn
	 * @创建时间: 2021/10/14 14:18
	 * @Param: hvs_cols
	 * @return: java.util.ArrayList<com.yusys.bione.plugin.yuformat.utils.HashVO>
	 */
	public ArrayList<HashVO> getTempVoList(HashVO[] hvs_cols) {
		ArrayList<HashVO> itemVOList = new ArrayList<HashVO>();
		for (int i = 0; i < hvs_cols.length; i++) {
			HashVO itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", hvs_cols[i].getStringValue("col_name_en").toLowerCase()); // 字段名英文
			itemVO.setAttributeValue("itemname", hvs_cols[i].getStringValue("col_name")); // 字段名-中文
			itemVO.setAttributeValue("itemtype", "文本框");

			String str_display_config = hvs_cols[i].getStringValue("display_config", "");
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(str_display_config)) { // 如果配置了数据,这里面是xml的格式数据
				appendOtherXmlConfig(str_display_config, itemVO);
			}
			itemVOList.add(itemVO);
		}
		return itemVOList;
	}

	//补充加入其他字段配置信息
	@SuppressWarnings("unchecked")
	private void appendOtherXmlConfig(String _display_config, HashVO _itemVO) {
		try {
			String str_display_config = "<root>" + _display_config + "</root>";
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document doc = reader.read(new StringReader(str_display_config));
			List<Element> itemList = doc.getRootElement().elements();
			for (int i = 0; i < itemList.size(); i++) {
				Element ele = itemList.get(i);
				String str_name = ele.getName();
				String str_text = ele.getText();
				if (str_text != null && !str_text.equals("")) {
					_itemVO.setAttributeValue(str_name.toLowerCase(), str_text);
				}
			}
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}
}
