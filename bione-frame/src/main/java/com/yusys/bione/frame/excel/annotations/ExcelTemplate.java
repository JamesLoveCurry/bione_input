/**
 * 
 */
package com.yusys.bione.frame.excel.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author tanxu
 *
 */
@Target( { TYPE })
@Retention(RUNTIME)
public @interface ExcelTemplate {

	
	/**
	 * 数据开始的行号.
	 */
	int firstRow() default 1;
	
	/**
	 * 数据开始的行号.
	 */
	int endRow() default -1;
	
	/**
	 * 通过文档模板生成excel，设置文档的路径.
	 */
	String path() default "";
	
}
