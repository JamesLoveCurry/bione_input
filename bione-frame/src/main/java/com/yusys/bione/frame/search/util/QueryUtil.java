/**
 * 
 */
package com.yusys.bione.frame.search.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * <pre>
 * Title:检索查询表达式的工具类，提供lucene检索语法的接口方法
 * Description:
 * </pre>
 * 
 * @author tanxu@yuchengtech.com
 * @version 
 * 
 * <pre>
 * 修改记录
 *    修改后版本:	修改人：		修改日期:	修改内容:
 * </pre>
 */
public class QueryUtil {

	/**
	 * 查询所有记录
	 * @return 				返回*:*
	 */
	public static String all(){
		return "*:*";
	}
	
	/**
	 * 查询以关键字开头的记录
	 * @return 				keywords*
	 */
	public static String start(String keywords){
		return keywords + "*";
	}
	
	/**
	 * 查询以关键字结尾的记录
	 * @return 				*keyword
	 */
	public static String end(String keyword){
		return "*" + keyword;
	}
	
	/**
	 * 查询与关键字相似的记录
	 * @return 				keyword~
	 */
	public static String similar(String keyword){
		return keyword + "~";
	}
	
	/**
	 * 查询所有field字段中在from和to之间的记录
	 * @return 				field:[from TO to]
	 */
	public static String fromTo(String field, String from, String to){
		return field + ":[" + from + " TO " + to + "]";
	}
	
	/**
	 * 查询所有field字段中等于关键字的记录
	 * @return 				field:"keyword"
	 * @deprecated			经测试后发现与like没有区别
	 */
	@Deprecated
	public static String eq(String field, String keyword){
		return field + ":\"" + keyword + "\"";
	}
	
	/**
	 * 查询所有field字段中包含关键字的记录
	 * @return 				field:keyword
	 */
	public static String like(String field, String keyword){
		return field + ":" + keyword;
	}
	
	/**
	 * 查询所有记录中不满足条件的记录
	 * @return 				NOT condition
	 */
	public static String not(String condition) {
		return " NOT " + condition;
	}
	
	/**
	 * 查询所有记录中满足任意一个条件的记录
	 * @return 				condition1 OR condition2 OR……
	 */
	public static String or(Iterable<String> conditions) {
		StringBuilder sb = new StringBuilder();
		for(String value : conditions){
			sb.append(value).append(" OR ");
		}
		return StringUtils.removeEnd(sb.toString(), " OR ");
	}
	
	/**
	 * 查询所有记录中同时满足包含多个条件的记录
	 * @return 				condition1 AND condition2 AND……
	 */
	public static String and(Iterable<String> conditions) {
		StringBuilder sb = new StringBuilder();
		for(String value : conditions){
			sb.append(value).append(" AND ");
		}
		return StringUtils.removeEnd(sb.toString(), " AND ");
	}
	
	/**
	 * 查询所有记录中同时满足包含多个条件的记录
	 * @return 				condition1 AND condition2 AND……
	 */
	public static String and(String condition1, String condition2) {
		return condition1 + " AND " + condition2;
	}
	
	/**
	 * 查询所有field字段中只要与某个关键字相等的记录
	 * @return 				field:"keyword1" OR field:"keyword2"……
	 * @deprecated			经测试后发现与like没有区别
	 */
	@Deprecated
	public static String eq_or(String field, Iterable<String> keywords) {
		List<String> eqs = Lists.newArrayList();
		for(String keyword : keywords){
			eqs.add(eq(field, keyword));
		}
		return or(eqs);
	}
	
	
	/**
	 * 查询所有field字段中只要包含任意一个关键字的记录
	 * @return 				field:keyword1 OR field:keyword2……
	 */
	public static String like_or(String field, Iterable<String> keywords) {
		List<String> eqs = Lists.newArrayList();
		for(String value : keywords){
			eqs.add(like(field, value));
		}
		return or(eqs);
	}
	
	/**
	 * 查询所有field字段中同时满足包含多个关键字的记录
	 * @return 				field:keyword1 AND field:keyword2……
	 */
	public static String like_and(String field, Iterable<String> keywords) {
		List<String> eqs = Lists.newArrayList();
		for(String value : keywords){
			eqs.add(like(field, value));
		}
		return and(eqs);
	}
	
	/**
	 * 用括号把条件括起来
	 * @return 				(condition)
	 */
	public static String wrap(String condition){
		return "(" +  condition + ")";
	}
}
