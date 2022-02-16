/**
 * 
 */
package com.yusys.bione.frame.validator.handler;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EmbeddedId;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;
import com.yusys.bione.frame.validator.common.ValidateConstants;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.frame.validator.utils.ValidateUtils;
import com.yusys.bione.frame.validator.utils.enums.DbDataTypes;

/**
 * <pre>
 * Title:基础类校验具体实现类
 * Description: 基础类校验具体实现类
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
@Component
public class BioneFieldValidHandler implements IValidateHandler {

	private BioneFieldValid fieldValid;

	private AnnotationValidable rootDomain;

	private Field field;

	private Object fieldValue;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yuchengtech.autoetl.validator.annovalidator.ValidateHandler#validate
	 * (com.yuchengtech.autoetl.validator.annovalidator.AnnotationValidable,
	 * java.lang.reflect.Field)
	 */
	public void validate(AnnotationValidable domain,
			AnnotationValidable rootDomain, Field field)
			throws ValidateException {
		if (domain != null && field != null
				&& field.isAnnotationPresent(BioneFieldValid.class)) {
			if (rootDomain != null) {
				this.rootDomain = rootDomain;
			} else {
				this.rootDomain = domain;
			}
			fieldCheck(domain, field);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yuchengtech.autoetl.validator.annovalidator.ValidateHandler#validate
	 * (com.yuchengtech.autoetl.validator.annovalidator.AnnotationValidable,
	 * java.lang.String)
	 */
	public List<ValidErrorInfoObj> validate(AnnotationValidable domain,
			AnnotationValidable rootDomain) {
		// no use in field validate come so far
		return null;
	}

	/******** Private Method *********/
	// 全局检查入口
	private void fieldCheck(AnnotationValidable domain, Field field)
			throws ValidateException {
		BioneFieldValid fieldValid = field.getAnnotation(BioneFieldValid.class);
		Object fieldValue = null;
		try {
			fieldValue = ValidateUtils.getField(domain, field.getName());
		} catch (Exception e) {
			ValidErrorInfoObj errorObj = generateErrorObj("校验过程出错",
					"校验过程中获取对象属性失败");
			throw new ValidateException(errorObj);
		}
		this.fieldValid = fieldValid;
		this.field = field;
		this.fieldValue = fieldValue;
		// 按一定顺序进行校验
		try {
			// 1.空值
			nullValidate();
			// 2.类型
			typeValidate();
			// 3.数据类型
			dataTypeValidate();
			// 4.特殊值域校验
			comboValsValidate();
			// 5.长度&&精度
			precisionValidate();
		} catch (ValidateException ve) {
			throw ve;
		}
	}

	// 空值检查方法
	private void nullValidate() throws ValidateException {
		if (!fieldValid.nullable()) {
			if (fieldValue == null) {
				// 值为空，校验肯定不能通过
				ValidErrorInfoObj errorObj = generateErrorObj("空值检查", "不允许为空");
				throw new ValidateException(errorObj);
			}
			if (field.isAnnotationPresent(EmbeddedId.class)) {
				// 若是联合主键，得判断主键对象中每个值是否有空
				Field[] fs = fieldValue.getClass().getDeclaredFields();
				if (fs != null) {
					for (int i = 0; i < fs.length; i++) {
						int modify = fs[i].getModifiers();
						if (modify == 26) {
							// if field -> private(2) + static(8) + final(16)
							continue;
						}
						Object valTmp = null;
						try {
							valTmp = ValidateUtils.getField(fieldValue,
									fs[i].getName());
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (valTmp == null
								|| (valTmp instanceof String && ""
										.equals(valTmp))) {
							ValidErrorInfoObj errorObj = generateErrorObj(
									"空值检查", "联合主键中不允许出现空值");
							throw new ValidateException(errorObj);
						}
					}
				}
			} else if (fieldValue instanceof String && "".equals(fieldValue)) {
				// 若是普通字符串字段，判断是否为空
				ValidErrorInfoObj errorObj = generateErrorObj("空值检查", "不允许为空");
				throw new ValidateException(errorObj);
			}
		}
	}

	// 类型检查方法
	private void typeValidate() throws ValidateException {
		if (fieldValue == null) {
			// 空值不处理
			return;
		}
		String fieldValueStr = "";
		switch (fieldValid.type()) {
		case NUMBERSTR:
			fieldValueStr = fieldValue.toString();
			try {
				new BigDecimal(fieldValueStr);
			} catch (Exception e) {
				ValidErrorInfoObj errorObj = generateErrorObj("类型检查", "["
						+ fieldValueStr + "]不是数字类型");
				throw new ValidateException(errorObj);
			}
			break;
		case BOOLEAN:
			if (fieldValue instanceof String && "".equals(fieldValue)) {
				// 字符串，布尔类型，为空时不进行布尔取值校验
				return;
			}
			// 获取布尔取值范围
			fieldValueStr = fieldValue.toString();
			String[] booleanVals = fieldValid.booleanVals();
			boolean isBoolean = false;
			for (String val : booleanVals) {
				if (fieldValueStr.equals(val)) {
					isBoolean = true;
					break;
				}
			}
			if (!isBoolean) {
				ValidErrorInfoObj errorObj = generateErrorObj("类型检查", "["
						+ fieldValueStr + "]不符合布尔取值范围");
				throw new ValidateException(errorObj);
			}
			break;
		case DATESTR:
			if(fieldValue instanceof String && "".equals(fieldValue)){
				// 字符串，日期类型，为空时不进行布尔取值校验
				return;
			}
			// 获取日期格式
			fieldValueStr = fieldValue.toString();
			String[] dateFormats = fieldValid.dateFormats();
			boolean isDate = false;
			for(String dateFormat : dateFormats){
				try {
					if(dateFormat.length() != fieldValueStr.length()){
						continue;
					}
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					sdf.parse(fieldValueStr);
					isDate = true;
				} catch (Exception e) {}
			}
			if(!isDate){
				ValidErrorInfoObj errorObj = generateErrorObj("类型检查", "["
						+ fieldValueStr + "]不符合日期字符串格式");
				throw new ValidateException(errorObj);
			}
			break;
		default:
			// 默认，当做字符串来处理，FiledTypes.STRING
			// 字符串类型此处不做校验
		}
	}

	// 长度检查方法
	@SuppressWarnings("incomplete-switch")
	private void precisionValidate() throws ValidateException {
		if (fieldValue == null) {
			return;
		}
		int length = fieldValid.length();
		int scale = fieldValid.scale();
		if (length > 0) {
			// -1是缺省值，小于0时，不进行长度校验
			String fieldValueStr = fieldValue.toString();
			ValidErrorInfoObj errorObj = null;
			switch (fieldValid.type()) {
			case NUMBERSTR:
				BigDecimal decimal = new BigDecimal(fieldValueStr);
				if (decimal.precision() > length) {
					errorObj = generateErrorObj("类型检查", "[" + fieldValueStr
							+ "]长度不能超过" + length);
				}
				if (decimal.scale() > scale) {
					errorObj = generateErrorObj("类型检查", "[" + fieldValueStr
							+ "]小数位不能超过" + scale);
				}
				break;
			case STRING:
				if (fieldValueStr.getBytes().length > length) {
					errorObj = generateErrorObj("类型检查", "[" + fieldValueStr
							+ "]长度不能超过" + length);
				}
			}
			if (errorObj != null) {
				throw new ValidateException(errorObj);
			}
		}
	}

	// 数据类型校验
	private void dataTypeValidate() throws ValidateException {
		if (fieldValue == null || !fieldValid.dataType()) {
			return;
		}
		String fieldValueStr = fieldValue.toString();
		String[] dataTypeSplit = StringUtils.split(fieldValueStr, '(');
		try {
			// 判断类型是否在【数据库类型枚举】中定义
			DbDataTypes.valueOf(dataTypeSplit[0].toUpperCase());
		} catch (java.lang.IllegalArgumentException ae) {
			// 类型未定义
			throw new ValidateException(this.generateErrorObj("数据类型检查", "["
					+ dataTypeSplit[0] + "]不是合法的数据类型"));
		}
	}
	
	// 特殊值域校验
	private void comboValsValidate() throws ValidateException{
		if (fieldValue == null) {
			return;
		}
		String fieldValueStr = fieldValue.toString();
		String[] fieldValues = fieldValid.comboVals();
		if(fieldValues.length <= 0){
			return ;
		}
		boolean isValid = false;
		for(String fieldValTmp : fieldValues){
			if(fieldValueStr.equals(fieldValTmp)){
				isValid = true;
				break;
			}
		}
		if(!isValid){
			throw new ValidateException(this.generateErrorObj("值域检查", "["
					+ fieldValueStr + "]不在有效值域范围内"));
		}
	}

	// 生成基础field校验通用错误对象
	private ValidErrorInfoObj generateErrorObj(String validateType,
			String errorMsg) {
		ValidErrorInfoObj errorObj = new ValidErrorInfoObj(
				ValidateConstants.VALIDATE_TYPE_BASIC,
				ValidateConstants.VALIDATE_TYPE_BASIC_NM, validateType,
				errorMsg, rootDomain, field.getName());
		// 获取错误信息在excel中对应的行号和excel对应的工作表名称
		Integer rowNo = null;
		String sheetName = null;
		try {
			rowNo = (Integer) ValidateUtils.getField(rootDomain,
					ValidateConstants.VALIDATE_EXCEL_ROWNO_FIELDNM);
			sheetName = (String) ValidateUtils.getField(rootDomain,
					ValidateConstants.VALIDATE_EXCEL_SHEETNM_FIELDNM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获取错误信息在excel中对应的列号
		String colNo = ValidateUtils.getFieldExcelCol(field);
		if (rowNo != null && sheetName != null) {
			StringBuilder errorLocation = new StringBuilder("【工作表】:")
					.append(sheetName).append("，【行】:").append(rowNo);
			errorObj.setSheetName(sheetName);
			errorObj.setExcelRowNo(rowNo);
			if (!StringUtils.isEmpty(colNo)) {
				errorLocation.append("，【列】:").append(colNo);
				String colNum = ValidateUtils.to123(colNo);
				if(!StringUtils.isEmpty(colNum)){					
					errorObj.setExcelColNo(Integer.valueOf(colNum));
				}
			}
			errorObj.setErrorLocation(errorLocation.toString());
		}
		return errorObj;
	}

}
