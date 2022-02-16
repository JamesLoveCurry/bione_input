/**
 * 
 */
package com.yusys.bione.frame.validator.handler;

import java.lang.reflect.Field;
import java.util.List;

import com.yusys.bione.frame.validator.common.AnnotationValidable;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;

/**
 * <pre>
 * Title:通用校验注解实现类接口
 * Description: 通用校验注解实现类接口
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
public interface IValidateHandler {

	// 执行某对象下属性校验
	public void validate(AnnotationValidable domain,
			AnnotationValidable rootDomain, Field field)
			throws ValidateException;

	// 执行某对象校验
	public List<ValidErrorInfoObj> validate(AnnotationValidable domain,
			AnnotationValidable rootDomain);

}
