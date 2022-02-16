/**
 * 
 */
package com.yusys.bione.frame.excel;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.google.common.collect.Maps;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

/**
 * @author tanxu
 *
 */
public class ClassMapping {

	private static ClassMapping instance = new ClassMapping();
	
//	private static Map<String, Class<?>> classMapping = Maps.newHashMap();
	
	private static Map<Class<?>, ColumnBean> columnMapping = Maps.newHashMap();
	
	static {
		try {
			ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
			scanner.addIncludeFilter(new AnnotationTypeFilter(ExcelSheet.class));
			for (BeanDefinition bd : scanner.findCandidateComponents("com.yusys.*")) {
				Class<?> clazz = Class.forName(bd.getBeanClassName());
				for (Annotation annotation : clazz.getAnnotations()) {
					if (annotation instanceof ExcelSheet) {
//						putClassMapping(((ExcelSheet) annotation).index(), clazz);
						columnMapping.put(clazz, new ColumnBean(clazz, (ExcelSheet) annotation));
					}
					//……
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private ClassMapping() {
	}
	
	public static ClassMapping getInstance() {
		return instance;
	}
	
//	private static final void putClassMapping(String key, Class<?> clazz) {
//		if (classMapping.get(key) != null) {
//			throw new IllegalStateException("Excel中Sheet表名为'" + key + "'已存在，'" + classMapping.get(key).toString() +"'与'" + clazz.toString() + "'中的Sheet表名冲突，请修改表名避免导入Excel错误.");
//		}
//		classMapping.put(key, clazz);
//	}
	
//	public final Class<?> getMappingClass(String key) {
//		return classMapping.get(key);
//	}
	
	public final ColumnBean getMappingColumn(Class<?> clazz) {
		return columnMapping.get(clazz);
	}
	
}
