/**
 * 
 */
package com.yusys.bione.plugin.design.util.excelFormula;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import com.greenpineyu.fel.parser.ConstNode;
import com.greenpineyu.fel.parser.FelNode;
import com.greenpineyu.fel.parser.FunNode;
import com.greenpineyu.fel.parser.VarAstNode;
import com.yusys.bione.frame.util.excel.ExcelAnalyseUtils;
import com.yusys.bione.plugin.design.util.excelFormula.func.IFunc;
import com.yusys.bione.plugin.design.util.excelFormula.func.If;
import com.yusys.bione.plugin.design.util.excelFormula.func.Sum;
import com.yusys.bione.plugin.design.util.excelFormula.oper.IOper;
import com.yusys.bione.plugin.design.util.excelFormula.oper.LowestLevelOper;
import com.yusys.bione.plugin.design.util.excelFormula.oper.Not;
import com.yusys.bione.plugin.design.util.excelFormula.oper.UpperLevelOper;
import com.yusys.bione.plugin.valid.utils.AntlrParseUtils;

/**
 * <pre>
 * Title: EXCEL公式转换成引擎分析公式类
 * Description: EXCEL公式转换成引擎分析公式类
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
public class ExcelFormula implements Serializable {

	private static final long serialVersionUID = -7427726401546655414L;

	private AntlrParseUtils parseUtils;

	// 操作符
	private List<IOper> opers = new ArrayList<IOper>();
	// 函数
	private List<IFunc> funcs = new ArrayList<IFunc>();

	public ExcelFormula() {
		parseUtils = new AntlrParseUtils();
		// 低优先级操作符
		opers.add(new LowestLevelOper("+"));
		opers.add(new LowestLevelOper("-"));
		// 高优先级操作符
		opers.add(new UpperLevelOper("*"));
		opers.add(new UpperLevelOper("/"));
		opers.add(new UpperLevelOper("%"));
		opers.add(new UpperLevelOper(">"));
		opers.add(new UpperLevelOper("<"));
		opers.add(new UpperLevelOper("=="));
		opers.add(new UpperLevelOper(">="));
		opers.add(new UpperLevelOper("<="));
		opers.add(new UpperLevelOper("!="));
		opers.add(new UpperLevelOper("||"));
		opers.add(new UpperLevelOper("&&"));
		opers.add(new Not());
		// 函数
		funcs.add(new If());
		//funcs.add(new Max());
		funcs.add(new Sum());
	}

	/**
	 * 解析excel公式为平台公式
	 * 
	 * @param formula
	 * @param cellIdxsMap
	 *            单元格位置: A1 , 指标标识 映射map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> parse(String formula,
			Map<String, String> cellIdxsMap) throws FormulaException {
		Map<String, String> returnMap = new HashMap<String, String>();
		// 分析公式
		formula = formula.trim();
		if(formula.indexOf("=") == 0){
			// 公式以=号开头，去掉=号
			formula = formula.substring(1);
		}
		// 1.解析形如A1:A3这样的表达式，替换成A1+A2+A3
		formula = formulaFormat(formula);
		// 2.使用fel进行运算节点分析
		FelNode fNode = parseUtils.parse(getPerFormula(formula));
		String formulaContent = "";
		List<String> indexs = new ArrayList<String>();
		Map<String, Object> parseMap = new HashMap<String, Object>();
		if (fNode instanceof FunNode) {
			boolean parseOver = false;
			for (IFunc funcTmp : funcs) {
				if (funcTmp.nameEqual(fNode.getText())) {
					parseMap = funcTmp.parse(fNode, cellIdxsMap, funcs, opers);
					parseOver = true;
					break;
				}
			}
			if (!parseOver) {
				for (IOper operTmp : opers) {
					if (operTmp.nameEqual(fNode.getText())) {
						parseMap = operTmp.parse(fNode, cellIdxsMap, funcs,
								opers);
						parseOver = true;
						break;
					}
				}
			}
		} else if (fNode instanceof ConstNode) {
			// 常量
			formulaContent = fNode.getText();
		} else if (fNode instanceof VarAstNode) {
			// A1这样的变量，会被当做单元格分析，若不存在于cellIdxsMap，该公式会被认为不合法
			String fText = fNode.getText().trim().toUpperCase();
			if (fText.equals("TRUE") || fText.equals("FALSE")) {
				formulaContent = fNode.getText();
			} else {
				Matcher m = ExcelAnalyseUtils.getDollarCellNoPattern().matcher(fText);
				if (m.matches()) {
					fText = m.group(1) + m.group(2);
				}
				if (!cellIdxsMap.containsKey(fText)) {
					throw new FormulaException(fText + "单元格不是报表指标");
				}
				StringBuilder sb = new StringBuilder("");
				sb.append("I('").append(cellIdxsMap.get(fText))
						.append("')");
				formulaContent = sb.toString();
				if (!indexs.contains(cellIdxsMap.get(fText))) {
					indexs.add(cellIdxsMap.get(fText));
				}
			}
		}
		StringBuilder srcIndexNo = new StringBuilder("");
		if(parseMap.containsKey("srcIndexNo")){
			indexs = (List<String>) parseMap.get("srcIndexNo");
		}
		if (indexs != null) {
			for (String indexNoTmp : indexs) {
				if (!StringUtils.isEmpty(srcIndexNo.toString())) {
					srcIndexNo.append(",");
				}
				srcIndexNo.append(indexNoTmp);
			}
		}
		if (parseMap.containsKey("formulaContent")) {
			formulaContent = (String) parseMap.get("formulaContent");
		}
		returnMap.put("srcIndexNo", srcIndexNo.toString());
		returnMap
				.put("formulaContent", formulaContent);
		return returnMap;
	}

	/** Private Method begin **/

	/**
	 * 1.解析形如A1:A3这样的表达式，替换成A1+A2+A3
	 * 2.解析形如A1=1这样的表达式 ，替换成A1==1
	 * 
	 * @param formula
	 * @return
	 */
	private String formulaFormat(String formula) {
		int loopCount = 0;
		while (formula.indexOf(":") != -1) {
			int index = formula.indexOf(":");
			if (loopCount >= 1000 && index != 0
					&& index != formula.length() - 1) {
				break;
			}
			int beginIndex = 0;
			int endIndex = formula.length() - 1;
			beginLoop: for (int i = index - 1; i >= 0; i--) {
				// 从冒号index向前遍历，获取冒号表达式起点
				if (! CharUtils.isAsciiAlphanumeric(formula.charAt(i))) {
					beginIndex = i + 1;
					break beginLoop;
				}
			}
			endLoop: for (int j = index + 1; j <= formula.length() - 1; j++) {
				// 从冒号index向后遍历，获取冒号表达式终点
				if (! CharUtils.isAsciiAlphanumeric(formula.charAt(j))) {
					endIndex = j - 1;
					break endLoop;
				}
			}
			if (beginIndex < endIndex) {
				String exprTmp = formula.substring(beginIndex, endIndex + 1);
				String formatStr = analysePosiExpr(exprTmp);
				formula = StringUtils.replace(formula, exprTmp, formatStr);
			}
			loopCount++;
		}
		loopCount = 0;
		int fromIndex = 0;
		Map<String , String> replaceMap = new HashMap<String , String>();
		while (formula.indexOf("=" , fromIndex) != -1) {
			int index = formula.indexOf("=" , fromIndex);
			if (loopCount >= 1000 
					&& fromIndex > formula.length() - 1 
					&& index != 0
					&& index != formula.length() - 1) {
				break;
			}
			if(index == 0
					|| index == (formula.length() - 1)){
				// =号位于公式头或公式尾
				fromIndex = (index+1);
				loopCount++;
				continue;
			}
			Pattern pattern = Pattern.compile("[A-Za-z0-9\'\"]");
			String frontStrTmp = String.valueOf(formula.charAt(index-1));
			String lastStrTmp = String.valueOf(formula.charAt(index+1));
			Matcher frontMatcher = pattern.matcher(frontStrTmp);
			Matcher lastMatcher = pattern.matcher(lastStrTmp);
			StringBuilder strTmp = new StringBuilder(frontStrTmp).append("=").append(lastStrTmp);
			if(frontMatcher.matches()
					&& lastMatcher.matches()
					&& !replaceMap.containsKey(strTmp.toString())){
				// 若是等于运算表达式(=)
				StringBuilder replaceTmp = new StringBuilder(frontStrTmp).append("==").append(lastStrTmp);
				replaceMap.put(strTmp.toString() , replaceTmp.toString());
			}
			fromIndex = (index+2);
			loopCount++;
		}
		Iterator<String> keyIt = replaceMap.keySet().iterator();
		while(keyIt.hasNext()){
			String keyTmp = keyIt.next();
			String replaceStr = replaceMap.get(keyTmp);
			if(StringUtils.isEmpty(keyTmp)
					|| StringUtils.isEmpty(replaceStr)){
				continue;
			}
			formula = StringUtils.replace(formula, keyTmp, replaceStr);
		}
		return formula;
	}

	/**
	 * 将形如A1:A10的表达式，转换成A1+A2+...+A10
	 * 
	 * @param posiExpr
	 * @return
	 */
	private static String analysePosiExpr(String posiExpr) {
		StringBuilder formatExpr = new StringBuilder("");
		if (!StringUtils.isEmpty(posiExpr)) {
			posiExpr = posiExpr.trim().toUpperCase();
			Pattern pattern = Pattern.compile("[A-Z]+\\d+:[A-Z]+\\d+");
			Matcher matcher = pattern.matcher(posiExpr);
			if (matcher.matches()) {
				// 形如 A1:B10
				String[] posis = StringUtils.split(posiExpr, ':');
				if (posis.length == 2) {
					int[] beginRowNoColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(posis[0]);
					int[] endRowNoColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(posis[1]);
					for (int r = beginRowNoColumnIdx[0]; r <= endRowNoColumnIdx[0]; r++) {
						for (int c = beginRowNoColumnIdx[1]; c <= endRowNoColumnIdx[1]; c++) {
							String posiTmp = ExcelAnalyseUtils.toABC(r, c);
							if (!"".equals(formatExpr.toString())) {
								formatExpr.append("+");
							}
							formatExpr.append(posiTmp);
						}
					}
				}
			}
		}
		return formatExpr.toString();
	}

	public static String getPerFormula( String formula ) {
		if ( StringUtils.isEmpty( formula ) ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Pattern pattern = Pattern.compile("(-)?\\d+(\\.\\d+)?%");
		Matcher matcher = pattern.matcher(formula);
		int startPos = 0;
		while (matcher.find()) {
			sb.append(formula.substring(startPos, matcher.start()));
			sb.append('(').append(formula.substring(matcher.start(), matcher.end() - 1)).append("/100)");
			startPos = matcher.end();
		}
		sb.append(formula.substring(startPos));
		return sb.toString();
	}
}
