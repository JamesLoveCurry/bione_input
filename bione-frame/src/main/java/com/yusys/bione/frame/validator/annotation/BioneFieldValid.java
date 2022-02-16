/**
 * 
 */
package com.yusys.bione.frame.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yusys.bione.frame.validator.utils.enums.FieldTypes;

/**
 * <pre>
 * Title:基础校验，数据类型
 * Description: 包含类型、长度、精度、小数、布尔等字段属性
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BioneFieldValid {
	
	// 数据类型
	FieldTypes type() default FieldTypes.STRING;
	// 长度
	int length() default -1;
	// 小数
	int scale() default -1;
	// 值域范围
	String[] comboVals() default {};
	// 非空
	boolean nullable() default true;
	// 数据类型校验
	boolean dataType() default false;
	// 布尔取值范围(type=BOOLEAN时有效)
	String[] booleanVals() default {"Y","N"};
	// 日期字符串格式(type=DATESTR时有效)
	String[] dateFormats() default {"yyyy-MM-dd","yyyy/MM/dd","yyyyMMdd"};
	
}
