/**
 * 
 */
package com.yusys.bione.plugin.valid.utils;

import com.yusys.bione.plugin.valid.check.CompareEngine;
import com.yusys.bione.plugin.valid.check.ValidateException;

/**
 * @author songxf
 * 
 */
public class CompareExpressionUtils {

	
	public static void compareFomula(String fomula) throws ValidateException {
		CompareEngine engine = new CompareEngine();
		engine.compare(fomula);
	}
}
