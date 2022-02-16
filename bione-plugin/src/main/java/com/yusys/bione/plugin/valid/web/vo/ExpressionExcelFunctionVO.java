package com.yusys.bione.plugin.valid.web.vo;

import java.io.Serializable;

/**
 * <pre>
 * Title: 表达式中 Excel函数逻辑实体列
 * Description: 
 * </pre>
 * @author lizy6 
 * @version 1.00.00
 */
public class ExpressionExcelFunctionVO implements Serializable{

	private static final long serialVersionUID = 8904608981215210147L;

	/**
	 * Excel函数 key
	 */
	private String excelFuncKey;
	
	/**
	 * Excel函数 类型 MAX MIN SUM
	 */
	private String excelFuncType;
	
	/**
	 * Excel函数 表达式
	 */
	private String excelFuncFormula;

	public String getExcelFuncKey() {
		return excelFuncKey;
	}

	public void setExcelFuncKey(String excelFuncKey) {
		this.excelFuncKey = excelFuncKey;
	}

	public String getExcelFuncType() {
		return excelFuncType;
	}

	public void setExcelFuncType(String excelFuncType) {
		this.excelFuncType = excelFuncType;
	}

	public String getExcelFuncFormula() {
		return excelFuncFormula;
	}

	public void setExcelFuncFormula(String excelFuncFormula) {
		this.excelFuncFormula = excelFuncFormula;
	}
	
	public String toString() {
		
		return excelFuncType + ":" +excelFuncKey + ":" + excelFuncFormula;
	}
}
