package com.yusys.bione.comp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <pre>
 * Title: 数字处理类
 * Description: 
 * </pre>
 * @author lizy6 
 * @version 1.00.00
 */
public class NumberUtils {
	
	/**
	 * 截取数字
	 * @param content
	 * @return
	 */
	public static Integer getNumbers(String content) {  
	    Pattern pattern = Pattern.compile("\\d+");  
	    Matcher matcher = pattern.matcher(content);  
	    while (matcher.find()) {  
	       return Integer.valueOf(matcher.group(0));  
	    }  
	    return 0;  
	}  
	  
	/**
	 * 截取非数字 
	 * @param content
	 * @return
	 */
	public static String splitNotNumber(String content) {  
	    Pattern pattern = Pattern.compile("\\D+");  
	    Matcher matcher = pattern.matcher(content);  
	    while (matcher.find()) {  
	        return matcher.group(0);  
	    }  
	    return "";  
	}
	  
	/**
	 * 判断一个字符串是否含有数字
	 * @param content
	 * @return
	 */
	public static boolean HasDigit(String content) {
	    boolean flag = false;
	    Pattern p = Pattern.compile(".*\\d+.*");
	    Matcher m = p.matcher(content);
	    if (m.matches()) {
	        flag = true;
	    }
	    return flag;
	}
	
	/**
	 * 判断一个字符串是为数字
	 * @param content
	 * @return
	 */
	public static boolean isNumeric(String content){
		//?:0或1个, *:0或多个, +:1或多个 
	    Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
	    return pattern.matcher(content).matches();   
	}
}
