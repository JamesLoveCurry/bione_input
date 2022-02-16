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
 * Title: 指标校验函数，校验单原子操作非
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
public class CheckNotOperator implements Function {
	private String funcName;

	public String getName() {
		return funcName;
	}

	public CheckNotOperator(String funcName) {
		super();
		this.funcName = funcName;
	}

	public Object call(FelNode node, FelContext context) {

		
		List<FelNode> args = node.getChildren();

		FelNode rightNode = args.get(1);
		// 右
		Object rightObject = rightNode.eval(context);


		if (!(rightObject instanceof Number)) {
			throw new BizException(node.getTokenStartIndex() + ":" + node.getTokenStopIndex() + ":存在非数值公式项");
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
