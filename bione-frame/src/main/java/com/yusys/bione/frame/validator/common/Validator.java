/**
 * 
 */
package com.yusys.bione.frame.validator.common;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.handler.IValidateHandler;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.frame.validator.utils.ValidateUtils;

/**
 * <pre>
 * Title:平台开放的注解校验入口类
 * Description: 执行对某个类，或对一个集合中所有类进行校验
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
public class Validator implements Serializable {

	private static final long serialVersionUID = 5264125863876842907L;

	/**
	 * 校验一个注解对象，校验不通过抛出异常
	 * 
	 * @param domain
	 * @throws ValidateException
	 */
	public static void validate(AnnotationValidable domain)
			throws ValidateException {
		if (domain == null) {
			return;
		}
		// 此次查询中共用的数据库比对记录
		try {
			validateObjEntrance(domain, domain);
		} catch (ValidateException ve) {
			throw ve;
		}
	}

	/**
	 * 批量校验，不通过的错误信息以List形式返回
	 * 
	 * @param domains
	 * @return
	 */
	public static List<ValidErrorInfoObj> validateBatch(Object... domains) {
		List<ValidErrorInfoObj> errorInfos = new ArrayList<ValidErrorInfoObj>();
		if (domains != null) {
			for (Object analyseObj : domains) {
				if (!AnnotationValidable.class.isAssignableFrom(analyseObj
						.getClass())) {
					// 不是可校验的对象
					continue;
				}
				Field[] fields = analyseObj.getClass().getDeclaredFields();
				for (Field field : fields) {
					int modify = field.getModifiers();
					if (modify == 26) {
						// if field -> private(2) + static(8) + final(16)
						continue;
					}
					Object fieldValue = null;
					try {
						fieldValue = ValidateUtils.getField(analyseObj,
								field.getName());
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					if (fieldValue != null && fieldValue instanceof List) {
						@SuppressWarnings("unchecked")
						List<Object> objList = ((List<Object>) fieldValue);
						for (int i = 0; i < objList.size(); i++) {
							Object objTmp = objList.get(i);
							if (!AnnotationValidable.class
									.isAssignableFrom(objTmp.getClass())) {
								// 若对象不是需要校验的实体
								continue;
							}
							try {
								validateObjEntrance(
										(AnnotationValidable) objTmp,
										(AnnotationValidable) objTmp);
							} catch (ValidateException ve) {
								errorInfos.addAll(ve.getErrorInfoObjs());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return errorInfos;
	}

	/************************* Private Method Begin ***************************/

	// 校验一个对象的具体实现
	private static void validateObjEntrance(AnnotationValidable domain,
			AnnotationValidable rootDomain) throws ValidateException {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		Class<?> currClass = domain.getClass();
		//long beginTime = System.currentTimeMillis();
		while (currClass != null) {
			// 1.判断对象是否配置有类验证(target = type)，目前只支持简单field校验
			try {
				objValidateByHandler(domain, rootDomain);
			} catch (ValidateException ve) {
				if (ve.getErrorInfoObjs() != null) {
					errors.addAll(ve.getErrorInfoObjs());
				}
			}
			// 2.轮询对象的属性
			Field[] fields = currClass.getDeclaredFields();
			for (Field field : fields) {
				int modify = field.getModifiers();
				if (modify == 26) {
					// if field -> private(2) + static(8) + final(16)
					continue;
				}
				try {
					fieldValidateByHandler(domain, rootDomain, field);
				} catch (ValidateException ve) {
					if (ve.getErrorInfoObjs() != null) {
						errors.addAll(ve.getErrorInfoObjs());
					}
				}
			}
			// 3.若superclass仍是AnnotationValidable，迭代继续校验
			currClass = AnnotationValidable.class.isAssignableFrom(currClass
					.getSuperclass()) ? currClass.getSuperclass() : null;
		}
		/*System.out.println(domain.getClass().getSimpleName() + " cost : "
				+ (System.currentTimeMillis() - beginTime) + "ms");*/
		if (errors.size() > 0) {
			throw new ValidateException(errors);
		}
	}

	// 调用对象校验接口
	private static void objValidateByHandler(AnnotationValidable domain,
			AnnotationValidable rootDomain) throws ValidateException {
		if (domain == null) {
			return;
		}
		Annotation[] annotations = domain.getClass().getAnnotations();
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation
						.annotationType()
						.getSimpleName()
						.startsWith(
								ValidateConstants.VALIDATE_ANNOTATION_PREFIX)) {
					// 若是平台管理范围内的注解(以Bione开头)
					IValidateHandler handler = null;
					List<ValidErrorInfoObj> errorObjs = new ArrayList<ValidErrorInfoObj>();
					String handlerName = annotation.annotationType()
							.getSimpleName()
							+ ValidateConstants.VALIDATE_ANNOTATION_HANDLER_SUFFIX;
					try {
						handler = (IValidateHandler) Class.forName(
								ValidateConstants.VALIDATE_HANDLER_PATH
										+ handlerName).newInstance();
					} catch (Exception e) {
						e.printStackTrace();
						// 未定义校验实现类
						return;
					}
					errorObjs = handler.validate(domain, rootDomain);
					if (errorObjs != null && errorObjs.size() > 0) {
						throw new ValidateException(errorObjs);
					}
				}
			}
		}
	}

	// 调用属性校验接口
	private static void fieldValidateByHandler(AnnotationValidable domain,
			AnnotationValidable rootDomain, Field field)
			throws ValidateException {
		if (domain == null || field == null) {
			return;
		}
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		if (AnnotationValidable.class.isAssignableFrom(field.getType())) {
			// 若field本身是一个AnnotationValidable对象
			Object fieldObj = null;
			try {
				fieldObj = ValidateUtils.getField(domain, field.getName());
				// 循环校验
				if (fieldObj != null) {
					validateObjEntrance((AnnotationValidable) fieldObj,
							rootDomain);
				}
			} catch (ValidateException ve) {
				if (ve.getErrorInfoObjs() != null) {
					errors.addAll(ve.getErrorInfoObjs());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Annotation[] annotations = field.getAnnotations();
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation
						.annotationType()
						.getSimpleName()
						.startsWith(
								ValidateConstants.VALIDATE_ANNOTATION_PREFIX)) {
					// 若是平台管理范围内的注解(以Bione开头)
					IValidateHandler handler = null;
					try {
						String handlerName = annotation.annotationType()
								.getSimpleName()
								+ ValidateConstants.VALIDATE_ANNOTATION_HANDLER_SUFFIX;
						handler = (IValidateHandler) Class.forName(
								ValidateConstants.VALIDATE_HANDLER_PATH
										+ handlerName).newInstance();
						handler.validate(domain, rootDomain, field);
					} catch (ValidateException ve) {
						if (ve.getErrorInfoObjs() != null) {
							errors.addAll(ve.getErrorInfoObjs());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (errors.size() > 0) {
			throw new ValidateException(errors);
		}
	}
}
