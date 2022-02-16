/**
 * 
 */
package com.yusys.bione.frame.excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.yusys.bione.frame.excel.annotations.ExcelSheet;

/**
 * @author tanxu
 *
 */
public final class ExcelAnnotationUtil {

	public static final String getExcelSheetName(ExcelSheet excelSheet) {
		return excelSheet.index() + ExcelSheet.SP + excelSheet.name();
	}

	public static final String getRealExcelSheetName(ExcelSheet excelSheet) {
		return excelSheet.name();
	}

	/**
	 * 
	 * @param excelSheet
	 * @param i
	 * @return
	 */
	public static final String getExcelSheetName(ExcelSheet excelSheet,int i) {
		return excelSheet.index() + ExcelSheet.SP + excelSheet.name()+ ExcelSheet.SP+i;
	}
	/**
	 * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
	 * 
	 * @param col
	 */
	public static final int getExcelCol(String col) {
		if( NumberUtils.isNumber(col)){
			return Integer.parseInt(col);
		}
		col = col.toUpperCase();
		// 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
		int count = -1;
		char[] cs = col.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
		}
		return count;
	}
	
	public static final String getSheetIndex(String sheetName) {
		return StringUtils.substringBeforeLast(sheetName, ExcelSheet.SP);
	}
	
	public static String getExcelColName(String cellLocation) {
		if (StringUtils.isNotEmpty(cellLocation)) {
			Pattern pattern = Pattern.compile("([^\\d]+)");
			Matcher matcher = pattern.matcher(cellLocation);
			while (matcher.find()) {
				return matcher.group(1);
			}
		}
		return "";
	} 
	
//	@Deprecated
//	public static <T, S extends T> FieldBean<T> getFieldBean(Class<S> clazz, String fieldName, final Class<T> originalClass) {
//		try {
//			Field field = clazz.getDeclaredField(fieldName);
//			return new FieldBean<T>(field);
//		} catch (NoSuchFieldException e) {
//			if (clazz.getSuperclass() != null) {
//				return getFieldBean(clazz.getSuperclass(), fieldName, originalClass);
//			}
//			throw new MappingException("No such field found " + originalClass.getName() + "." + fieldName, e);
//		}
//	}
}
