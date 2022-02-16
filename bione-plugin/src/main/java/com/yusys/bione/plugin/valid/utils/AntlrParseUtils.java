/**
 * 
 */
package com.yusys.bione.plugin.valid.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.common.Callable;
import com.greenpineyu.fel.exception.ParseException;
import com.greenpineyu.fel.function.operator.Dot;
import com.greenpineyu.fel.parser.FelLexer;
import com.greenpineyu.fel.parser.FelNode;
import com.greenpineyu.fel.parser.FelParser;
import com.greenpineyu.fel.parser.FunNode;
import com.greenpineyu.fel.parser.NodeAdaptor;
import com.greenpineyu.fel.parser.Parser;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class AntlrParseUtils implements Parser{
	public static final	Callable<Boolean, FelNode> funFilter = new Callable<Boolean, FelNode>() {
		@Override
		public Boolean call(FelNode... node) {
			FelNode n = node[0];
			if(n == null){
				return false;
			}
			boolean isFun = n instanceof FunNode;
			if(isFun){
				if (n instanceof CommonTree) {
					CommonTree treeNode = (CommonTree) n;
					CommonTree p = treeNode.parent;
					if(p!=null){
						if(Dot.DOT.equals(p.getText())){
							// 点运算符后的函数节点不是真正意义上的变量节点。
							isFun = p.getChildren().get(0)==n;
						}
					}
				}
			}
			return isFun;
		}
	};
	
	@SuppressWarnings("unused")
	private final FelEngine engine;
	private final NodeAdaptor adaptor;
	
	public AntlrParseUtils(){
		this(null, null);
	}

	public AntlrParseUtils(FelEngine engine, NodeAdaptor adaptor) {
		this.engine = engine;
		if (adaptor == null) {
			adaptor = new NodeAdaptor();
		}
		this.adaptor = adaptor;
	}
	
	public FelNode parse(String exp) {
		if (exp == null || "".equals(exp)) {
			return null;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(exp.getBytes());
		ANTLRInputStream input = null;
		try {
			input = new ANTLRInputStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		FelLexer lexer = new FelLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FelParser parser = new FelParser(tokens);
		parser.setTreeAdaptor(adaptor);
		ParserRuleReturnScope r = null;
		try {
			r = parser.program();
		} catch (RecognitionException e) {
			throw new ParseException(e.getMessage() + ": " + exp, e);
		}
		if (r != null) {
			Object tree = r.getTree();
			if (tree instanceof FelNode) {
				//initFun((FelNode)tree);
				return (FelNode) tree;
			}
		}
		return null;
	}
	
//	public void initFun(FelNode node){
//		List<FelNode> nodes = AbstFelNode.getNodes(node, funFilter);
//		if(nodes!=null){
//			for (FelNode n : nodes) {
//				FunNode funNode = (FunNode)n;
//				funNode.initFun(this.engine.getFunMgr());
//				Function fun = funNode.getFun();
//				if (fun instanceof Dot) {
//					Dot dot = (Dot) fun;
//					dot.setSecurityMgr(this.engine.getSecurityMgr());
//				}
//			}
//		}
//	}

	@Override
	public boolean verify(String exp) {
		try {
			parse(exp);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
