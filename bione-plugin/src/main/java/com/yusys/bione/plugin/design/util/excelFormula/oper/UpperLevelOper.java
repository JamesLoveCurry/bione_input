/**
 * 
 */
package com.yusys.bione.plugin.design.util.excelFormula.oper;

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
import com.yusys.bione.plugin.design.util.excelFormula.func.IFunc;
import com.yusys.bione.plugin.design.util.excelFormula.func.Sum;

/**
 * <pre>
 * Title:四则运算
 * Description: 四则运算等优先级最低的运算
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
public class UpperLevelOper implements IOper, Serializable {

	private static final long serialVersionUID = -1012835902740879755L;

	public static final Integer ARG_SIZE = 2;

	private String operName = "";

	public UpperLevelOper() {
		super();
	}

	public UpperLevelOper(String operName) {
		super();
		this.operName = operName;
	}

	@Override
	public String getName() {
		return operName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> parse(FelNode funcNode,
			Map<String, String> cellIdxsMap, List<IFunc> funcs,
			List<IOper> opers) throws FormulaException{
		Map<String, Object> parseMap = new HashMap<String, Object>();
		List<String> srcIndexs = new ArrayList<String>();
		StringBuilder formulaContent = new StringBuilder("");
		List<FelNode> childNodes = funcNode.getChildren();
		if (childNodes.size() != ARG_SIZE) {
			throw new FormulaException("[ " + operName + " ]参数个数不合法");
		}
		for (FelNode fNode : childNodes) {
			if(!StringUtils.isEmpty(formulaContent.toString())){
				formulaContent.append(operName);
			}
			if (fNode instanceof ConstNode) {
				// 常量
				formulaContent.append(fNode.getText());
			} else if (fNode instanceof VarAstNode) {
				// A1这样的变量，会被当做单元格分析，若不存在于cellIdxsMap，该公式会被认为不合法
				String fText = fNode.getText().trim().toUpperCase();
				Matcher m = ExcelAnalyseUtils.getDollarCellNoPattern().matcher(fText);
				if (m.matches()) {
					fText = m.group(1) + m.group(2);
				}
				if (!cellIdxsMap.containsKey(fText)) {
					throw new FormulaException("[ " + operName + " ]运算中" + fText
							+ "单元格不是报表指标");
				}
				formulaContent.append("I('").append(cellIdxsMap.get(fText))
						.append("')");
				if (!srcIndexs.contains(cellIdxsMap.get(fText))) {
					srcIndexs.add(cellIdxsMap.get(fText));
				}
			} else if (fNode instanceof FunNode) {
				boolean isLowwestOper = false;
				boolean isSumFunc = false;
				boolean parseOver = false;
				Map<String, Object> parseMapTmp = new HashMap<String, Object>();
				for (IFunc funcTmp : funcs) {
					if (funcTmp.nameEqual(fNode.getText())) {
						parseMapTmp = funcTmp.parse(fNode, cellIdxsMap, funcs,
								opers);
						parseOver = true;
						if(funcTmp instanceof Sum){
							isSumFunc = true;
						}
						break;
					}
				}
				if (!parseOver) {
					for (IOper operTmp : opers) {
						if (operTmp.nameEqual(fNode.getText())) {
							parseMapTmp = operTmp.parse(fNode, cellIdxsMap,
									funcs, opers);
							parseOver = true;
							if(operTmp instanceof LowestLevelOper){
								isLowwestOper = true;
							}
							break;
						}
					}
				}
				List<String> idxsTmp = (List<String>) parseMapTmp
						.get("srcIndexNo");
				if(idxsTmp != null){					
					for (String idxTmp : idxsTmp) {
						if (!srcIndexs.contains(idxTmp)) {
							srcIndexs.add(idxTmp);
						}
					}
				}
				if(isLowwestOper || isSumFunc){
					formulaContent.append("(");
				}
				formulaContent.append(parseMapTmp.get("formulaContent"));
				if(isLowwestOper || isSumFunc){
					formulaContent.append(")");
				}
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
				&& targetNm.trim().toUpperCase().equals(operName)) {
			flag = true;
		}
		return flag;
	}
}
