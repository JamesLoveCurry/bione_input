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
public @interface ExcelSheet {
	
	public static final String SP = "-";
	
	/**
	 * Excel中的表名.
	 */
	String name() default "";
	
	boolean titleFlag() default false;
	
	/**
	 * Excel中表的编号.
	 */
	String index();
	
	String extType() default "01";

//	/**
//	 * 导出Excel需要的模板文件.
//	 */
//	ExcelTemplate template() default @ExcelTemplate;
	
	/**
	 * 数据开始的行号.
	 */
	int firstRow() default 1;
	
	/**
	 * 数据开始的列号.
	 */
	int firstCol() default 1;

	String insertType() default "01";
	
	String[] relInfo() default  {};
}
