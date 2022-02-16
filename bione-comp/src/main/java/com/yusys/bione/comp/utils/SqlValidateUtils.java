package com.yusys.bione.comp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * <pre>
 * Title:防sql注入 校验工具
 * Description: 防sql注入 校验工具
 * </pre>
 * 
 * @author lcy lizy6@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class SqlValidateUtils {
	
	//词语  oracle 防止sql注入 爆破
	private static final String[] sqlInStrArray = {"ctxsys.drithsx.sn"};
	
	/**
	 * 过滤敏感词和特殊字符 
	 * 防止sql注入
	 * @param str
	 * @return str
	 */
	public static String validateSql(String str) {
		if(StringUtils.isNotBlank(str)){
			//统一转为小写
			String str2 = str.toLowerCase();
			for (String sqlInStr : sqlInStrArray) {
				if (str2.indexOf(sqlInStr)>=0) {
					//正则替换词语，无视大小写
					str = str.replaceAll("(?i)"+sqlInStr,"");
				}
			}
		}
		return str;
	}
	
	/**
	 * 通过正则表达式
	 * 判断字符串当中是否有特殊符号
	 * @param str
	 * @return true 存在
	 * 		   false 不存在
	 */
	public static boolean validateStr(String str) {
		boolean isExit = false;
		if(StringUtils.isNotBlank(str)) {
			String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
			Pattern p=Pattern.compile(regEx);
			Matcher m=p.matcher(str);
			isExit = m.find();
		}
		return isExit;
	}
	
	/**
	 * 过滤敏感词和特殊字符 
	 * 防止sql注入
	 * @param str
	 * @return str
	 */
	public static String replaceValue(String str) {
		//统一转为小写
		String str2 = str.toLowerCase();
		//词语
		String[] SqlStr1 = {"and","exec","execute","insert","select","delete","update","count","drop","chr","mid","master","truncate","char","declare","sitename","net user","xp_cmdshell","like","and","exec","execute","insert","create","drop","table","from","grant","use","group_concat","column_name","information_schema.columns","table_schema","union","where","select","delete","update","order","by","count","chr","mid","master","truncate","char","declare","or"};
		//特殊字符
		String[] SqlStr2 = {"*","'",";","or","-","--","+","//","/","%","#"};

		for (int i = 0; i < SqlStr1.length; i++) {
			if (str2.indexOf(SqlStr1[i])>=0) {
				//正则替换词语，无视大小写
				str = str.replaceAll("(?i)"+SqlStr1[i],"");
			}
		}
		for (int i = 0; i < SqlStr2.length; i++) {
			if (str2.indexOf(SqlStr2[i]) >= 0) {
				str = str.replaceAll(SqlStr2[i],"");
			}
		}
		return str;
	}
	
	/**
	 * 过滤敏感词
	 * 防止sql注入
	 * @param str
	 * @return str
	 */
	public static String replaceKeyword(String str) {
		//统一转为小写
		String str2 = str.toLowerCase();
		//关键字
		String[] SqlStr1 = {"and","exec","execute","insert","select","delete","update","count","drop","chr","mid","master","truncate","char","declare","sitename","net user","xp_cmdshell","like","and","exec","execute","insert","create","drop","table","from","grant","use","group_concat","column_name","information_schema.columns","table_schema","union","where","select","delete","update","order","by","count","chr","mid","master","truncate","char","declare","or"};

		for (int i = 0; i < SqlStr1.length; i++) {
			if (str2.indexOf(SqlStr1[i])>=0) {
				//正则替换词语，无视大小写
				str = str.replaceAll("(?i)"+SqlStr1[i],"");
			}
		}
		
		return str;
	}
	
	/**
	 * 过滤条件值校验
	 * 防止SQL注入
	 * @return value
	 */
	public static String validateValue(String value){
		if(SqlValidateUtils.validateStr(value)){
			value = SqlValidateUtils.replaceValue(value);
		}
		return value;	
	}
}
