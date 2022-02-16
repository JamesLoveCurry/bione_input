/**
 * 
 */
package com.yusys.bione.frame.excel;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;

/**
 * @author tanxu
 *
 */
public final class FieldBean {
	
	private final Field[] fields;
	
	private final ExcelColumn colmun;
	
	private final int index;

	public FieldBean(ExcelColumn colmun, Field... fields) {
		this.colmun = colmun;
		this.fields = fields;
		this.index = ExcelAnnotationUtil.getExcelCol(colmun.index());
		for (Field field : this.fields) {
			field.setAccessible(true);
		}
	}

	public void set(Object instance, String value) {
		try {
			int len = fields.length, end = 0;
			Object obj = instance;
			while (end + 1 < len) {
				//obj = fields[end].get(obj);
				obj = getInnerObject(obj, fields[end]);
				end++;
			}
			fields[end].set(obj, coerceToPrimitiveValue(value, fields[end].getType()));
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	private Object getInnerObject(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Object innerObject = field.get(obj);
		if (innerObject == null) {
			innerObject = field.getType().newInstance();
			field.set(obj, innerObject);
		}
		return innerObject;
	}

	public Object get(Object instance) {
		Object obj = getObject(instance);
		return obj == null ? "" :obj;
	}
	
	public Object getObject(Object instance) {
		try {
			int len = fields.length, end = 0;
			Object obj = instance;
			while (end + 1 < len) {
				obj = getInnerObject(obj, fields[end]);
				//obj = fields[end].get(obj);
				end++;
			}
			return fields[end].get(obj);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	public ExcelColumn getExcelColumnAnnotation() {
		return this.colmun;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public String getName() {
		return this.fields[0].getName();
	}
	
	private Object coerceToPrimitiveValue(String value, Class<?> clazz) {
		if (clazz == Byte.class || clazz == Byte.TYPE) {
			return Byte.valueOf(value);
		} else if (clazz == Short.class || clazz == Short.TYPE) {
			return Short.valueOf(value);
		} else if (clazz == Integer.class || clazz == Integer.TYPE) {
			return Integer.valueOf(value);
		} else if (clazz == Long.class || clazz == Long.TYPE) {
			return Long.valueOf(value);
		} else if (clazz == Float.class || clazz == Float.TYPE) {
			return Float.valueOf(value);
		} else if (clazz == Double.class || clazz == Double.TYPE) {
			return Double.valueOf(value);
		} else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
			return Boolean.valueOf(value);
		} else if (clazz == BigInteger.class) {
			return new BigInteger( value );
		} else if (clazz == BigDecimal.class) {
			return new BigDecimal( value );
		} else if (clazz == Character.class || clazz == Character.TYPE) {
			if ((value != null) && (value.length() > 0)) {
				return Character.valueOf(value.charAt(0));
			}
		} else if (clazz == String.class) {
			return String.valueOf(value);
		}
		throw new IllegalArgumentException("Cannot convert String [" + value + "] to target class [" + clazz.getName() + "]");
	}
}
