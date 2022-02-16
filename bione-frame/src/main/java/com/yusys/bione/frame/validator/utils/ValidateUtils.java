/**
 * 
 */
package com.yusys.bione.frame.validator.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;

/**
 * <pre>
 * Title:校验接口过程中用到的工具方法
 * Description: 校验接口过程中用到的工具方法
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class ValidateUtils {

	/**
	 * 利用反射获取指定field对应的值
	 * 
	 * @param obj
	 * @param field
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static Object getField(Object obj, String field)
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {
		String firstLetter = field.substring(0, 1).toUpperCase();
		String getMethodName = "get" + firstLetter + field.substring(1);
		Method getMethod = obj.getClass().getMethod(getMethodName);
		Object returnValue = getMethod.invoke(obj);
		return returnValue;
	}

	/**
	 * 利用反射设置指定field的值
	 * 
	 * @param <T>
	 * @param obj
	 * @param field
	 * @param val
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static <T> void setField(Object obj, String field, T val)
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {
		String firstLetter = field.substring(0, 1).toUpperCase();
		String setMethodName = "set" + firstLetter + field.substring(1);
		Class<?> paramClass = val.getClass();
		// 对通用集合类参数特殊处理
		if (List.class.isAssignableFrom(val.getClass())) {
			paramClass = List.class;
		} else if (Set.class.isAssignableFrom(val.getClass())) {
			paramClass = Set.class;
		} else if (Map.class.isAssignableFrom(val.getClass())) {
			paramClass = Map.class;
		}
		Method setMethod = obj.getClass().getMethod(setMethodName, paramClass);
		setMethod.invoke(obj, val);
	}

	/**
	 * 获取指定列对应的excel列号
	 * 
	 * @param field
	 * @return
	 */
	public static String getFieldExcelCol(Field field) {
		String colNo = "";
		if (field != null && field.isAnnotationPresent(ExcelColumn.class)) {
			ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
			colNo = excelColumn.index();
		}
		return colNo;
	}

	/**
	 * 获取obj对象对应的field值，field可以是类似id.name的多级表达式
	 * 
	 * @param fieldExpr
	 * @param obj
	 * @return
	 */
	public static Object getValueByFieldExpr(String fieldExpr, Object obj) {
		Object val = null;
		if (!StringUtils.isEmpty(fieldExpr)) {
			String[] fieldDetails = StringUtils.split(fieldExpr, '.');
			val = obj;
			for (int i = 0; i < fieldDetails.length; i++) {
				if (fieldDetails[i] == null
						|| "".equals(fieldDetails[i].trim())) {
					continue;
				}
				try {
					val = ValidateUtils.getField(val, fieldDetails[i].trim());
				} catch (Exception e) {
					e.printStackTrace();
					val = null;
				}
			}
		}
		return val;
	}
	
	/**
	 * 获取指定实体对应的主键名称
	 * @param domain
	 * @return
	 */
	public static String getPkNameByEntity(Object domain){
		String pkName = "";
		if(domain != null){
			Field[] fields = domain.getClass().getDeclaredFields();
			for(Field field : fields){
				int modify = field.getModifiers();
				if (modify == 26) {
					// if field -> private(2) + static(8) + final(16)
					continue;
				}
				if(field.isAnnotationPresent(Id.class)
						|| field.isAnnotationPresent(EmbeddedId.class)){
					pkName = field.getName();
					break;
				}
			}
		}
		return pkName;
	}
	
	public static String to123(String posiStr) {
		int length=posiStr.length();
		int result=0;
		for(int i=0;i<length;i++){
			int num=posiStr.charAt(i)-64;
			result+=num*Math.pow(26, length-i-1);
		}
		return String.valueOf(result);
	}

}
