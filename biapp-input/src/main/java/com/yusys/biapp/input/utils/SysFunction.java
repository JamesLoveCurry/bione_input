package com.yusys.biapp.input.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:系统函数,系统变量
 * Description: 系统函数,系统变量
 * </pre>
 * @author caijiufa
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 * 
 * </pre>
 */
public class SysFunction {

	public final static String start_date = "@start_date";//任务开始日期
	public final static String end_date = "@end_date";//任务结束日期
	public final static String org_code = "@org_code";//当前机构
	public final static String last_mon_end = "@last_mon_end";// 上月末
	public final static String last_sea_end = "@last_sea_end";// 上季末
	public final static String last_month = "@last_month";// 上月
	public final static String last_year = "@last_year";// 上年
	public final static String last_day = "@last_day";// 前一天 t-1
	public final static String today = "@today";// 当天
	
	public static List<String> sysParams = Lists.newArrayList();

	static {
		sysParams.add(sysParams.size(), start_date);
		sysParams.add(sysParams.size(), end_date);
		sysParams.add(sysParams.size(), org_code);
	}

	/**
	 * 赋值
	 * @param string
	 * @param map
	 * @return
	 */
	public String replace(String string, Map<String, String> map) {
		for (String key : map.keySet()) {
			string = string.replaceAll(key, map.get(key));
		}
		return string;
	}

	/**
	 * 给字符串中所有系统参数赋值
	 * @param string 需要赋值的字符串
	 * @param startDate 开始日期如 2012-12-12
	 * @param life 任务生命周期，用来计算结束日期
	 * @return
	 */
	public static String replaceALL(String string, String startDate, int life) {
		string = replaceDate(string, startDate, life); // 赋值开始结束日期
		
		if (BioneSecurityUtils.getCurrentUserInfo() != null) {// 赋值机构ID
			String currentOrg = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
			if (StringUtils.isNotBlank(currentOrg))
				string = string.replaceAll(org_code, currentOrg);
		}
		
		Date date = new Date();
		String lastMonEnd = DateUtils.formatDate(DateUtils.getEndDateOfLastMonth(date), DateUtils.yyyy_MM_dd);// 上月末
		string = string.replaceAll(last_mon_end, lastMonEnd);

		String lastSeaEnd = DateUtils.formatDate(DateUtils.getEndDateOfLastSeason(date), DateUtils.yyyy_MM_dd);// 上季末
		string = string.replaceAll(last_sea_end, lastSeaEnd);

		String lastMonth = DateUtils.formatDate(DateUtils.getEndDateOfLastMonth(date), DateUtils.yyyy_MM);// 上月
		string = string.replaceAll(last_month, lastMonth);

		String lastYear = DateUtils.formatDate(date, DateUtils.yyyy);// 上年
		string = string.replaceAll(last_year, (Integer.valueOf(lastYear) - 1) + StringUtils.EMPTY);
		
		String lastDay = DateUtils.formatDate(DateUtils.getPastDate(1, date), DateUtils.yyyy_MM_dd);// 前一天 t-1
		string = string.replaceAll(last_day, lastDay);
		
		String now = DateUtils.formatDate(date, DateUtils.yyyy_MM_dd);// 当天
		string = string.replaceAll(today, now);
		
		return string;
	}

	/**
	 * 给字符串中所有开始结束日期的系统参数赋值
	 * @param string 需要赋值的字符串
	 * @param startDate 开始日期如 2012-12-12
	 * @param life 任务生命周期，用来计算结束日期
	 * @return
	 */
	public static String replaceDate(String string, String startDate, int life) {
		if (StringUtils.isNotBlank(string) && StringUtils.isNotBlank(startDate)) {
			string = string.replaceAll(SysFunction.start_date, startDate);// 替换掉开始日期
			string = string.replaceAll(SysFunction.end_date, getEndDate(startDate, life));
		}
		return string;
	}

	/**
	 * 根据开始时间和有限天数获取结束日期
	 * @param startDate 开始时间 yyyy-MM-dd
	 * @param life 有限天数
	 * @return
	 */
	public static String getEndDate(String startDate, int life) {
		return DateUtils.formatDate(DateUtils.getPastDate(-life, DateUtils.getDateStart(startDate)), DateUtils.yyyy_MM_dd);
	}
}
