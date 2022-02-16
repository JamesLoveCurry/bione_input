/**
 * 
 */
package com.yusys.bione.frame.excel.annotations;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author tanxu
 *
 */
@Target( { FIELD })
@Retention(RUNTIME)
public @interface ExcelColumn {

	/**
	 * Excel中的列名
	 */
	String name() default "";

	/**
	 * 配置列的序号,对应A,B,C,D....
	 */
	String index();
	
	/**
	 * 配置列的宽度，-1为默认宽度
	 */
	int width() default -1;

	/**
	 * 设置只能选择不能输入的列内容.
	 */
	String[] combox() default {};
	
	String type() default "text";
	
	String formate() default "";
	
	String[] relDs() default {};
	
	String[] value() default {};
	
	String[] text() default {};
	
	String initVal() default "";
	
	String colTitle() default "";
	
	String formatter() default "";

	/**
	 * 是否导出数据,应对需求:有时我们需要导出一份模板,这是标题需要但内容需要用户手工填写.
	 */
	boolean isExport() default true;

	//设置字体
	String fontName() default "宋体";

	//设置字号
	short fontHeightInPoint() default 12;

	//设置对齐方式
	HorizontalAlignment alignment() default HorizontalAlignment.CENTER;
}
