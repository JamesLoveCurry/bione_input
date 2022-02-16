/**
 * 
 */
package com.yusys.bione.plugin.design.util.excelFormula.func;

import java.util.List;
import java.util.Map;

import com.greenpineyu.fel.parser.FelNode;
import com.yusys.bione.plugin.design.util.excelFormula.FormulaException;
import com.yusys.bione.plugin.design.util.excelFormula.oper.IOper;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public interface IFunc {

	/**
	 * 获取函数名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 表达式转换
	 * 
	 * @param funcNode
	 * @return key : srcIndexNo ; value : 来源指标号集合 key : formulaContent ; value :
	 *         转换后的表达式
	 */
	public Map<String, Object> parse(FelNode funcNode,
			Map<String, String> cellIdxsMap, List<IFunc> funcs,
			List<IOper> opers) throws FormulaException;

	/**
	 * 判断函数名字是否一致
	 * 
	 * @param targetNm
	 * @return
	 */
	public boolean nameEqual(String targetNm);

}
