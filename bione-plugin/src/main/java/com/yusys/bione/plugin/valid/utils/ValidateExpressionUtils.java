/**
 * 
 */
package com.yusys.bione.plugin.valid.utils;

import org.apache.commons.lang3.StringUtils;

import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.RecognitionException;

import com.greenpineyu.fel.exception.EvalException;
import com.greenpineyu.fel.exception.ParseException;
import com.yusys.bione.plugin.valid.check.BizException;
import com.yusys.bione.plugin.valid.check.ExpressionEngine;
import com.yusys.bione.plugin.valid.check.ValidateEngine;
import com.yusys.bione.plugin.valid.check.ValidateException;

/**
 * @author songxf
 * 
 */
public class ValidateExpressionUtils {

	public static Object testFomula(String fomula) {
		ExpressionEngine expressionEngine = new ExpressionEngine();
		try {
			Object result = expressionEngine.eval(fomula);
			if (result == null) {
				throw new BizException("计算公式未得到正确结果");
			}
			return result;
		} catch (Exception e) {
			if (e.getCause() != null
					&& e.getCause() instanceof RecognitionException) {
				RecognitionException reExp = (RecognitionException) e
						.getCause();
				if (reExp instanceof NoViableAltException) {
					throw new BizException(":运算符前后不匹配");
				} else if (reExp instanceof MismatchedTokenException) {
					throw new BizException(reExp.getLocalizedMessage()
							+ ":括号前后不匹配");
				}
			} else if (e instanceof EvalException) {
				String funcName = StringUtils.substringBetween(e.getMessage(),
						"[", "]");
				int startPosition = fomula.indexOf(funcName);
				int endPosition = fomula.indexOf(funcName) + funcName.length();
				throw new BizException(startPosition + ":" + endPosition
						+ ":函数不存在");
			} else if (e instanceof ParseException) {
				if (e.getCause().getClass().getName()
						.equals("org.antlr.runtime.MismatchedTokenException")) {
					org.antlr.runtime.MismatchedTokenException me = (org.antlr.runtime.MismatchedTokenException) e
							.getCause();
					throw new BizException("第"
							+ (me.token.getCharPositionInLine() + 1)
							+ "个字符:无法匹配");
				} else if (e.getCause().getClass().getName()
						.equals("org.antlr.runtime.NoViableAltException")) {
					org.antlr.runtime.NoViableAltException nae = (org.antlr.runtime.NoViableAltException) e
							.getCause();
					throw new BizException("第" + (nae.charPositionInLine)
							+ "个字符:运算符前后不匹配");
				}

			} else
				throw new BizException(e.getMessage());
		}
		return null;

	}
	
	public static void validateFomula(String fomula) throws ValidateException {
		ValidateEngine engine = new ValidateEngine();
		engine.validate(fomula);
	}
}
