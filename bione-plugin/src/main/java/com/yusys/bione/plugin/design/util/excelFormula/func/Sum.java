/**
 * 
 */
package com.yusys.bione.plugin.design.util.excelFormula.func;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.greenpineyu.fel.common.StringUtils;
import com.greenpineyu.fel.parser.ConstNode;
import com.greenpineyu.fel.parser.FelNode;
import com.greenpineyu.fel.parser.FunNode;
import com.greenpineyu.fel.parser.VarAstNode;
import com.yusys.bione.frame.util.excel.ExcelAnalyseUtils;
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
public class Sum implements IFunc, Serializable {

	private static final long serialVersionUID = 4617064107797591125L;

	private static final String FUNC_NAME = "SUM";

	@Override
	public String getName() {
		return FUNC_NAME;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> parse(FelNode funcNode,
			Map<String, String> cellIdxsMap, List<IFunc> funcs,
			List<IOper> opers) throws FormulaException {
		Map<String, Object> parseMap = new HashMap<String, Object>();
		List<String> srcIndexs = new ArrayList<String>();
		StringBuilder formulaContent = new StringBuilder("");
		List<FelNode> childNodes = funcNode.getChildren();
		if (childNodes.size() <= 0) {
			throw new FormulaException("[ " + FUNC_NAME + " ]参数个数不合法");
		}
		for (FelNode fNode : childNodes) {
			if (!StringUtils.isEmpty(formulaContent.toString())) {
				formulaContent.append("+");
			}
			if (fNode instanceof ConstNode) {
				// 常量
				formulaContent.append(fNode);
			} else if (fNode instanceof VarAstNode) {
				// A1这样的变量，会被当做单元格分析，若不存在于cellIdxsMap，该公式会被认为不合法
				String fText = fNode.getText().trim().toUpperCase();
				Matcher m = ExcelAnalyseUtils.getDollarCellNoPattern().matcher(fText);
				if (m.matches()) {
					fText = m.group(1) + m.group(2);
				}
				if (!cellIdxsMap.containsKey(fText)) {
					throw new FormulaException("[ " + FUNC_NAME + " ]函数中"
							+ fText + "单元格不是报表指标");
				}
				formulaContent.append("I('").append(cellIdxsMap.get(fText))
						.append("')");
				if (!srcIndexs.contains(cellIdxsMap.get(fText))) {
					srcIndexs.add(cellIdxsMap.get(fText));
				}
			} else if (fNode instanceof FunNode) {
				boolean parseOver = false;
				Map<String, Object> parseMapTmp = new HashMap<String, Object>();
				for (IFunc funcTmp : funcs) {
					if (funcTmp.nameEqual(fNode.getText())) {
						parseMapTmp = funcTmp.parse(fNode, cellIdxsMap, funcs,
								opers);
						parseOver = true;
						break;
					}
				}
				if (!parseOver) {
					for (IOper operTmp : opers) {
						if (operTmp.nameEqual(fNode.getText())) {
							parseMapTmp = operTmp.parse(fNode, cellIdxsMap,
									funcs, opers);
							parseOver = true;
							break;
						}
					}
				}
				List<String> idxsTmp = (List<String>) parseMapTmp
						.get("srcIndexNo");
				if (idxsTmp != null) {
					for (String idxTmp : idxsTmp) {
						if (!srcIndexs.contains(idxTmp)) {
							srcIndexs.add(idxTmp);
						}
					}
				}
				formulaContent.append(parseMapTmp.get("formulaContent"));
			}
		}
		parseMap.put("srcIndexNo", srcIndexs);
		parseMap.put("formulaContent", formulaContent.toString());
		return parseMap;
	}

	@Override
	public boolean nameEqual(String targetNm) {
		boolean flag = false;
		if (!StringUtils.isEmpty(targetNm)
				&& targetNm.trim().toUpperCase().equals(FUNC_NAME)) {
			flag = true;
		}
		return flag;
	}
}
