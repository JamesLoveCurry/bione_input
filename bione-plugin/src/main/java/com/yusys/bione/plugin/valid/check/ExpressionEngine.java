/**
 * 
 */
package com.yusys.bione.plugin.valid.check;

import com.greenpineyu.fel.FelEngineImpl;


/**
 * <pre>
 * Title: 表达式引擎类
 * Description: 继承自开源的Fel框架，增加指标常量、部分因算函数，四则运算的函数
 * </pre>
 * 
 * @author songxf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版:     修改人:     修改日期:     修改内容:
 * </pre>
 */
public class ExpressionEngine extends FelEngineImpl {

	public ExpressionEngine() {
		super();
		this.addFun(new CheckOperator("+"));
		this.addFun(new CheckOperator("-"));
		this.addFun(new CheckOperator("*"));
		this.addFun(new CheckOperator("/"));
		this.addFun(new CheckOperator("%"));
		this.addFun(new CheckOperator(">"));
		this.addFun(new CheckOperator("<"));
		this.addFun(new CheckOperator(">="));
		this.addFun(new CheckOperator("<="));
		this.addFun(new CheckOperator("="));
		this.addFun(new CheckOperator("!="));
		this.addFun(new CheckOperator("||"));
		this.addFun(new CheckOperator("&&"));
		this.addFun(new CheckFunction("I", 1));
		this.addFun(new CheckFunction("IF", 3));
		this.addFun(new CheckFunction("P", 1));
		this.addFun(new CheckFunction("C", 1));
		this.addFun(new CheckFunction("ABS", 1));
		this.addFun(new CheckFunction("Yesterday", 1));
		this.addFun(new CheckFunction("ThisMonthFirst", 1));
		this.addFun(new CheckFunction("LastMonthEnd", 1));
		this.addFun(new CheckFunction("LastMonth", 1));
		this.addFun(new CheckFunction("ThisQuarterFirst", 1));
		this.addFun(new CheckFunction("LastQuarterEnd", 1));
		this.addFun(new CheckFunction("ThisYearFirst", 1));
		this.addFun(new CheckFunction("LastYearEnd", 1));
		this.addFun(new CheckFunction("LastYear", 1));
		
		this.addFun(new CheckNotOperator("!"));

	}
}
