/**
 * 
 */
package com.yusys.bione.plugin.valid.check;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.RecognitionException;

import com.greenpineyu.fel.exception.EvalException;
import com.greenpineyu.fel.exception.ParseException;
import com.greenpineyu.fel.parser.FelNode;
import com.greenpineyu.fel.parser.FunNode;
import com.yusys.bione.plugin.valid.utils.AntlrParseUtils;

/**
 * <pre>
 * Title:表达式比较引擎
 * Description: 提供基本的表达式比较功能
 * </pre>
 * 
 * @author fangjuan fangjuan@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class CompareEngine {

	private AntlrParseUtils parseUtils;

	// 操作符
	private Map<String , Integer> opers = new HashMap<String , Integer>();

	// 函数
	private Map<String, Integer> funcs = new HashMap<String, Integer>();

	public CompareEngine() {
		parseUtils = new AntlrParseUtils();
		// 初始化操作符
		this.addOper("+");
		this.addOper("-");
		this.addOper("*");
		this.addOper("/");
		this.addOper("%");
		this.addOper(">");
		this.addOper("<");
		this.addOper("!" , 1);
		this.addOper(">=");
		this.addOper("<=");
		this.addOper("=");
		this.addOper("!=");
		this.addOper("||");
		this.addOper("&&");
		// 初始化函数
		this.addFunc("if", 3);
		this.addFunc("I", 1);
	}

	/** Public Methods **/

	/**
	 * 公式校验
	 * 
	 * @param fomula
	 */
	public void compare(String formula) throws ValidateException {
		if (StringUtils.isEmpty(formula)) {
			return;
		}
		formula = formula.trim();
		FelNode fNode = null;
		try {
			fNode = parseUtils.parse(formula);
			if (fNode != null) {
				if (fNode instanceof FunNode) {
					compareFunc((FunNode) fNode);
				}
			}
		} catch (Exception e) {
			if (e.getCause() != null
					&& e.getCause() instanceof RecognitionException) {
				RecognitionException reExp = (RecognitionException) e
						.getCause();
				if (reExp instanceof NoViableAltException) {
					throw new ValidateException(":运算符前后不匹配");
				} else if (reExp instanceof MismatchedTokenException) {
					throw new ValidateException(reExp.getLocalizedMessage()
							+ ":括号前后不匹配");
				}
			} else if (e instanceof EvalException) {
				String funcName = StringUtils.substringBetween(e.getMessage(),
						"[", "]");
				int startPosition = formula.indexOf(funcName);
				int endPosition = formula.indexOf(funcName) + funcName.length();
				throw new ValidateException(startPosition + ":" + endPosition
						+ ":函数不存在");
			} else if (e instanceof ParseException) {
				if (e.getCause().getClass().getName()
						.equals("org.antlr.runtime.MismatchedTokenException")) {
					org.antlr.runtime.MismatchedTokenException me = (org.antlr.runtime.MismatchedTokenException) e
							.getCause();
					throw new ValidateException("第"
							+ (me.token.getCharPositionInLine() + 1)
							+ "个字符:无法匹配");
				} else if (e.getCause().getClass().getName()
						.equals("org.antlr.runtime.NoViableAltException")) {
					org.antlr.runtime.NoViableAltException nae = (org.antlr.runtime.NoViableAltException) e
							.getCause();
					throw new ValidateException("第" + (nae.charPositionInLine)
							+ "个字符:运算符前后不匹配");
				}
			} else
				throw new ValidateException(e.getMessage());
		}
	}

	/** Private Methods **/
	
	private void addOper(String oper) {
		if (!StringUtils.isEmpty(oper) && !opers.containsKey(oper)) {
			opers.put(oper , 2);
		}
	}
	
	private void addOper(String oper , int argCount) {
		if (!StringUtils.isEmpty(oper) && !opers.containsKey(oper)) {
			opers.put(oper , argCount);
		}
	}

	private void addFunc(String func, int argCount) {
		if (!StringUtils.isEmpty(func) && !funcs.containsKey(func)) {
			funcs.put(func, argCount);
		}
	}

	private void compareFunc(FunNode fNode) throws ValidateException {
		List<FelNode> childNodes = fNode.getChildren();
		if (opers.containsKey(fNode.getText())) {
			// 若是操作符
			if (childNodes == null || childNodes.size() != opers.get(fNode.getText())) {
				throw new ValidateException("操作符[ " + fNode.getText()
						+ " ]:附近语法错误");
			}
		} else if (funcs.keySet().contains(fNode.getText())) {
			Integer argSize = funcs.get(fNode.getText());
			if (childNodes == null || (argSize != 0 &&  childNodes.size() != argSize)) {
				throw new ValidateException("函数[ " + fNode.getText()
						+ " ]:参数个数不合法");
			}
		} else {
			throw new ValidateException("[ " + fNode.getText()
					+ " ]:操作符或函数名未定义");
		}
		for (FelNode childNode : childNodes) {
			if (childNode instanceof FunNode) {
				this.compareFunc((FunNode) childNode);
			}
		}
	}

	/** Get & Set methods **/

	/**
	 * @return 返回 funcs。
	 */
	public Map<String, Integer> getFuncs() {
		return funcs;
	}

	/**
	 * @return 返回 opers。
	 */
	public Map<String, Integer> getOpers() {
		return opers;
	}

	/**
	 * @param opers 设置 opers。
	 */
	public void setOpers(Map<String, Integer> opers) {
		this.opers = opers;
	}

	/**
	 * @param funcs
	 *            设置 funcs。
	 */
	public void setFuncs(Map<String, Integer> funcs) {
		this.funcs = funcs;
	}
}
