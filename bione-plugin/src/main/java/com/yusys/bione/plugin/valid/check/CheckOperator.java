/**
 * 
 */
package com.yusys.bione.plugin.valid.check;

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
public class CheckOperator implements Function {
	private String funcName;

	public String getName() {
		return funcName;
	}

	public CheckOperator(String funcName) {
		super();
		this.funcName = funcName;
	}

	public Object call(FelNode node, FelContext context) {

		List<FelNode> args = node.getChildren();

		FelNode leftNode = args.get(0);
		FelNode rightNode = args.get(1);
		// 左
		Object leftObject = leftNode.eval(context);
		// 右
		Object rightObject = rightNode.eval(context);

		if (!(leftObject instanceof Number)) {
			throw new BizException("存在非数值公式项:'" + args.get(0) + "'");
		}

		if (!(rightObject instanceof Number)) {
			throw new BizException("存在非数值公式项:'" + args.get(1) + "'");
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
