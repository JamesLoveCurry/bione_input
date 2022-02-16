package com.yusys.bione.comp.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * 功能描述：ArrayUtils
 * @author liuch(liucheng2@yuchengtech.com)
 * @version 1.0 2013-1-9 下午1:11:09
 * @see
 * HISTORY
 * 2013-1-9 下午1:11:09 创建文件
 */
public class ArrayUtils {

    public static boolean isEmpty(Object[] array) {
        return null == array || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    /**
     * Return a collection from the comma delimited String.
     *
     * @param commaDelim the comma delimited String.
     * @return A collection from the comma delimited String. Returns <tt>null</tt> if the string is empty.
     */
    public static Collection<String> asCollection(String commaDelim) {
        if (commaDelim == null || commaDelim.trim().length() == 0) {
            return null;
        }
        return commaDelimitedStringToSet(commaDelim);
    }

    public static Set<String> asSet(String s, String split) {
		String array[] = StringUtils.split(s, split);
        Set<String> set = new HashSet<String>(array.length);
        for (int i = 0; i < array.length; i ++) {
        	set.add(array[i]);
        }
        return set;
    }
    
    /**
     * Returns a set from comma delimted Strings.
     * @param s The String to parse.
     * @return A set from comma delimted Strings.
     */
    public static Set<String> commaDelimitedStringToSet(String s) {
        Set<String> set = new HashSet<String>();
        String[] split = StringUtils.split(s, ',');
        for (String aSplit : split) {
            String trimmed = aSplit.trim();
            if (trimmed.length() > 0)
                set.add(trimmed);
        }
        return set;
    }
    
    /**
   	 * 将数组[a,b,c...]转换成字符串
   	 * @param obj
   	 * @return 字符串a,b,c...
   	 */
   	public static String toString(Object[] obj){
   		return StringUtils.remove(StringUtils.remove(org.apache.commons.lang3.ArrayUtils.toString(obj), '{'), '}');
   	}
    
    /**
	 * 将数据列表转换成字符串
	 * @param list
	 * @return 字符串a,b,c...
	 */
	public static String toString(Collection<String> list) {
		return toString(list.toArray(new String[list.size()]));
	}
    
    /**
	 * 将数据列表转换成字符串
	 * @param list
	 * @return 字符串a,b,c...
	 */
	public static String toString(List<String> list) {
		return toString(list.toArray(new String[list.size()]));
	}

    /**
	 * 将数组转换成数据库的in中的字符串
	 * @param obj
	 * @param change 是否加转义符
	 * @return 字符串'a','b','c'...
	 */
	public static String toDBString(List<String> list, boolean change) {
		for (int i = 0; i < list.size(); i++) {
			String obj = list.get(i);
			if (change) {
				list.set(i, "''" + obj + "''");
			} else {
				if (!obj.startsWith("'")) {
					list.set(i, "'" + obj + "'");
				}
			}
		}
		return toString(list);
	}
	
	public static List<String> asList(String s, String split) {
		String array[] = StringUtils.split(s, split);
		List<String> list = Arrays.asList(array);
		return list;
	}
}
