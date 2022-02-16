/**
 * 
 */
package com.yusys.bione.frame.validator.exception;

import java.util.ArrayList;
import java.util.List;

import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;

/**
 * <pre>
 * Title:通用校验异常基础类
 * Description: 通用校验异常基础类
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
public class ValidateException extends Exception {

	private List<ValidErrorInfoObj> errorInfoObjs = new ArrayList<ValidErrorInfoObj>();

	/**
	 * generatored serial ID
	 */
	private static final long serialVersionUID = -5374157736257347033L;

	public ValidateException(Exception ex) {
		super(ex.getMessage(), ex);
	}

	public ValidateException(String message) {
		super(message);
	}

	public ValidateException(String message, Exception ex) {
		super(message, ex);
	}

	public ValidateException(ValidErrorInfoObj errorObj) {
		super(errorObj.getErrorMsg());
		this.errorInfoObjs.add(errorObj);
	}

	public ValidateException(List<ValidErrorInfoObj> errorObjs) {
		super();
		this.errorInfoObjs = errorObjs;
	}

	public List<ValidErrorInfoObj> getErrorInfoObjs() {
		return errorInfoObjs;
	}

	public void setErrorInfoObj(List<ValidErrorInfoObj> errorInfoObjs) {
		this.errorInfoObjs = errorInfoObjs;
	}

}
