/**
 * 
 */
package com.yusys.bione.plugin.design.util.excelFormula;

/**
 * <pre>
 * Title:公式异常通用类
 * Description: 公式异常通用类 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class FormulaException extends Exception{

	private static final long serialVersionUID = 2855374785737080761L;
	
	public FormulaException(Exception ex){
		super(ex.getMessage() , ex);
	}
	
	public FormulaException(String msg){
		super(msg);
	}

}
