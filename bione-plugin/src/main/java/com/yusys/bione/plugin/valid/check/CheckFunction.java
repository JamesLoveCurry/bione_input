/**
 * 
 */
package com.yusys.bione.plugin.valid.check;

import java.util.ArrayList;
import java.util.List;

import com.greenpineyu.fel.compile.InterpreterSourceBuilder;
import com.greenpineyu.fel.compile.SourceBuilder;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.function.Function;
import com.greenpineyu.fel.parser.FelNode;

/**
 * <pre>
 * Title: 指标校验函数
 * </pre>
 * 
 * @author songxf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版:     修改人:       修改日期:     修改内容:
 * </pre>
 */
public class CheckFunction implements Function {
	private String funcName;
	private int paramNum;

	public String getName() {
		return funcName;
	}

	public CheckFunction(String funcName, int paramNum) {
		super();
		this.funcName = funcName;
		this.paramNum = paramNum;
	}

	@SuppressWarnings("unused")
	public Object call(FelNode node, FelContext context) {
		List<FelNode> args = node.getChildren();
		List<Object> out = new ArrayList<Object>();
		if(args != null){
			for(FelNode tmp : args){
				out.add(tmp.eval(context));
			}
		}
		if (out == null) {
			throw new BizException("存在函数" + this.funcName + ":参数不能为空");
		} else if (out.size() != paramNum) {
			throw new BizException("存在函数" + this.funcName + ":参数数量不正确");
		}
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.greenpineyu.fel.function.Function#toMethod(com.greenpineyu.fel.parser
	 * .FelNode, com.greenpineyu.fel.context.FelContext)
	 */

	public SourceBuilder toMethod(FelNode node, FelContext ctx) {

		return InterpreterSourceBuilder.getInstance();
	}

}
