/**
 * 
 */
package com.yusys.bione.frame.excel;

import java.lang.reflect.Field;
import java.util.Map;

import javax.persistence.EmbeddedId;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Maps;
import com.yusys.bione.frame.excel.annotations.EmbeddedExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

/**
 * @author tanxu
 *
 */
public final class ColumnBean {
	
	private Class<?> clazz;
	
	private ExcelSheet excelSheet;
	
	private Map<String, FieldBean> fieldBeans = Maps.newHashMap();
	
//	@SuppressWarnings("unchecked")
	public ColumnBean(Class<?> clazz, ExcelSheet excelSheet) {
		this.clazz = clazz;
		this.excelSheet = excelSheet;
		createFieldMapping(this.clazz);
	}
	
	private final void createFieldMapping(Class<?> clazz, Field... parentFields) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(ExcelColumn.class)) {
				ExcelColumn colmun = field.getAnnotation(ExcelColumn.class);
				if (colmun.isExport()) {
					addField(colmun.index(), colmun, ArrayUtils.add(parentFields, field));
				}
			} else if (field.isAnnotationPresent(EmbeddedId.class)) {
				createFieldMapping(field.getType(), ArrayUtils.add(parentFields, field));
			} else if(field.isAnnotationPresent(EmbeddedExcelColumn.class)) {
				createFieldMapping(field.getType(), ArrayUtils.add(parentFields, field));
			}
		}
	} 
	
	private void addField(String fieldName, ExcelColumn colmun, Field... allFields) {
		fieldBeans.put(fieldName, new FieldBean(colmun, allFields));
	}

	public FieldBean getFieldBean(String fieldIndex) {
		return fieldBeans.get(fieldIndex);
	}
	
	public Iterable<String> fieldIndexes() {
		return fieldBeans.keySet();
	}

	public Class<?> getClazz() {
		return this.clazz;
	}
	
	public ExcelSheet getExcelSheet() {
		return this.excelSheet;
	}
	
}
