package com.yusys.bione.comp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StringUtils2 {

	/**
	 * 正则表达式匹配
	 * 
	 * @param strtext
	 * 
	 * @param strPattern
	 * 
	 * @return
	 */
	public static boolean like(String strtext, String strPattern) {
		String inputstrp = StringUtils.replaceEach(
				strPattern, new String[]{".", "*", "%"}, new String[]{"\\x2e", ".*", ".*"});
		String oldinputstrp;
		do {
			oldinputstrp = inputstrp;
			inputstrp = StringUtils.replace(oldinputstrp, "?", "\\w");
		} while (!oldinputstrp.equals(inputstrp));
		return Pattern.matches(inputstrp, strtext);
	}

	/**
	 * 格式化信息，例如 String source = "用户{0}操作了{1}功能，用户{0}没权限"; String dest =
	 * StringUtil.format(source, new String[] {"张三","新增机构"} )
	 * 
	 * 执行结果：dest成了 用户张三操作了新增机构功能，用户张三没权限
	 */
	public static String formate(String source, String[] params) {
		if (source == null || params == null)
			return source;
		String dest = "";
		int paramLength = params.length;
		StringBuilder regStrSB = new StringBuilder();
		for (int i = 0; i < paramLength; i++) {
			regStrSB.setLength(0);
			regStrSB.append('{').append(i).append('}');
			// 替换异常信息中的变量
			source = StringUtils.replace(source, regStrSB.toString(), params[i]);
		}
		dest = source;
		return dest;
	}

	/** trim */
	public static String trimNull(String source) {
		return trimNull(source, "");
	}

	/** trim */
	public static String trimNull(String source, String deft) {
		if (source == null || source.trim().length() <= 0) {
			return deft;
		}
		return source.trim();
	}

	public static String formate(String source, int len) {
		return formate(source, len, ' ');
	}

	public static String formate(String source, int len, char c) {
		if (source == null || len <= 0)
			return source;
		String dest = source;
		while (dest.length() < len) {
			dest = c + dest;
		}
		return dest;
	}
	
	/**
     * Check if two strings are equal. Here, null is equal to null.
     *
     * @param a the first value
     * @param b the second value
     * @return true if both are null or both are equal
     */
    public static boolean equalsWithNull(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    /**
     * Convert a string to uppercase using the English locale.
     *
     * @param s the test to convert
     * @return the uppercase text
     */
    public static String toUpperEnglish(String s) {
        return s.toUpperCase(Locale.ENGLISH);
    }

    /**
     * Convert a string to lowercase using the English locale.
     *
     * @param s the text to convert
     * @return the lowercase text
     */
    public static String toLowerEnglish(String s) {
        return s.toLowerCase(Locale.ENGLISH);
    }

	/**
	 * 从字符串s中删除chars中的每个字符
	 */
	public static String removeChars(String s, String chars) {
		char[] charArray = chars.toCharArray();
		Arrays.sort(charArray);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.length(); i ++) {
			char ch = s.charAt(i);
			if (Arrays.binarySearch(charArray, ch) < 0) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 防范XSS攻击，对HTML文本进行编码<br>
	 * 合法字符：a-z A-Z 0-9 SPACE , .
	 */
	public static String htmlEncode(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		int len = str.length();
		StringBuilder sb = new StringBuilder((int)(len * 1.5));
		for (int cnt = 0; cnt < len; cnt ++) {
			char ch = str.charAt(cnt);
			if(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' ||
					ch == ' ' || ch == ',' || ch == '.') {
				sb.append(ch);
			} else {
				sb.append("&#").append((int)ch).append(';');
			}
		}
		return sb.toString();
	}

	/**
	 * 防范XSS攻击，对HTML属性值进行编码<br>
	 * 合法字符：a-z A-Z 0-9
	 */
	public static String htmlAttributeEncode(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		int len = str.length();
		StringBuilder sb = new StringBuilder((int)(len * 1.5));
		for (int cnt = 0; cnt < len; cnt ++) {
			char ch = str.charAt(cnt);
			if(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9') {
				sb.append(ch);
			} else {
				sb.append("&#").append((int)ch).append(';');
			}
		}
		return sb.toString();
	}

	/**
	 * 防范XSS攻击，对JavaScript脚本中的字符串值进行编码<br>
	 * 合法字符：a-z A-Z 0-9 SPACE , .
	 */
	public static String javaScriptEncode(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		int len = str.length();
		StringBuilder sb = new StringBuilder((int)(len * 1.5));
		for (int cnt = 0; cnt < len; cnt ++) {
			char ch = str.charAt(cnt);
			if(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' ||
					ch == ' ' || ch == ',' || ch == '.') {
				sb.append(ch);
			} else if (ch <= 127) {
				sb.append("\\x");
				String hex = Integer.toString(ch, 16);
				if (hex.length() < 2) {
					sb.append('0');
				}
				sb.append(hex);
			} else {
				sb.append("\\u");
				String hex = Integer.toString(ch, 16);
				for (int i = hex.length(); i < 4; i ++) {
					sb.append('0');
				}
				sb.append(hex);
			}
		}
		return sb.toString();
	}
	
	
    public static String ClobToString(Clob clob){    
    	java.io.Reader is  = null;
    	String reString = "";     
    	try{
    		is = clob.getCharacterStream();// 得到流        
    		BufferedReader br = new BufferedReader(is);         
    		String s = br.readLine();         
    		StringBuffer sb = new StringBuffer();         
    		while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING            
    			sb.append(s);             
    			s = br.readLine();         
    		}         
    		reString = sb.toString();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
		finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		}
		return reString;    
	}

	/**
	 * @方法描述: 存储型xss
	 * @创建人: miaokx@yusys.com.cn
	 * @创建时间: 2021/8/19 17:35
	 * @Param: value
	 * @return: java.lang.String
	 */
	public static String striptXSS(String value) {
		if (value != null) {

			value = value.replaceAll("", "");
			Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile(".*<.*", Pattern.CASE_INSENSITIVE );
			value = scriptPattern.matcher(value).replaceAll("");
		}
		return value;
	}

	/**
	 * 防范XSS攻击，路径进行操作
	 * 合法字符：a-z A-Z 0-9  - _ .
	 */
	public static boolean isPath(String str) {
		boolean flag = true;
		if (StringUtils.isEmpty(str)) {
			return flag;
		}
		int len = str.length();
		for (int cnt = 0; cnt < len; cnt ++) {
			char ch = str.charAt(cnt);
			if(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' ||
					ch == '/' || ch == '-' || ch == '.' || ch == '_') {
			} else {
				flag = false;
			}
		}
		return flag;
	}
}