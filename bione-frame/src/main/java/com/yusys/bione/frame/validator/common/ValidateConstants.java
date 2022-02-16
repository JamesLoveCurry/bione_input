/**
 * 
 */
package com.yusys.bione.frame.validator.common;

/**
 * <pre>
 * Title:校验通用静态变量
 * Description: 校验通用静态变量
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
public class ValidateConstants {

	// 校验注解前缀
	public static final String VALIDATE_ANNOTATION_PREFIX = "Bione";
	// 校验注解实现类后缀（实现类类名：Annotation class name + suffix）
	public static final String VALIDATE_ANNOTATION_HANDLER_SUFFIX = "Handler";
	// 校验实现类路径
	public static final String VALIDATE_HANDLER_PATH = "com.yusys.bione.frame.validator.handler.";
	
	// 默认联合主键字段名
	public static final String UNION_PK_FIELD_NAME = "id";
	
	// 表外关联校验，表达式，逻辑运算符（目前只支持’=‘等于）
	public static final String REMOTE_EXPR_OPER_EQUAL = "=";
	// 表外关联校验，表达式，分隔符
	public static final String REMOTE_EXPR_SPLIT = ",";
	
	public static final String VALIDATE_TYPE_BASIC = "01";  // 基础校验类型
	public static final String VALIDATE_TYPE_BASIC_NM = "基础类";  // 基础校验名称
	
	public static final String VALIDATE_EXCEL_ROWNO_FIELDNM = "excelRowNo";  // 记录excel行号的字段名 
	public static final String VALIDATE_EXCEL_SHEETNM_FIELDNM = "sheetName"; // 记录excel工作表名的字段名
	
}
