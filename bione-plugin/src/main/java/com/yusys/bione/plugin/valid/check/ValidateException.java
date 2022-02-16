/**
 * 
 */
package com.yusys.bione.plugin.valid.check;

/**
 * <pre>
 * Title:校验异常通用类
 * Description: 校验异常通用类 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class ValidateException extends Exception{

	private static final long serialVersionUID = 2855374785737080761L;
	
	public ValidateException(Exception ex){
		super(ex.getMessage() , ex);
	}
	
	public ValidateException(String msg){
		super(msg);
	}

}
