/**
 * 
 */
package com.yusys.bione.frame.util.excel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * Title:提供excel分析用工具功能
 * Description: 提供excel分析用工具功能
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
public class ExcelAnalyseUtils {

	private static Pattern dollarCellNoPattern = Pattern.compile("\\$([a-zA-Z]+)\\$(\\d+)");

	/**
	 * 解析excel公式为平台公式（支持sum、+、-）
	 *  -- 已过时，由ExcelFormula.parse方法替代
	 * 
	 * @param formula
	 * @param cellIdxsMap
	 *            单元格位置: A1 , 指标标识 映射map
	 * @return
	 */
	public static Map<String, String> analyseExcelFormula(String formula,
			Map<String, String> cellIdxsMap) {
		// 支持的运算符集合
		List<String> allowOper = new ArrayList<String>();
		allowOper.add("+");
		allowOper.add("-");

		Map<String, String> returnMap = new HashMap<String, String>();
		if (cellIdxsMap == null) {
			cellIdxsMap = new HashMap<String, String>();
		}
		// 来源指标
		StringBuilder srcIndexNo = new StringBuilder("");
		if (!StringUtils.isEmpty(formula)) {
			formula = formula.toUpperCase().trim();
			// 将"=SUM(A1,B1+100)"处理成"SUM(A1,B1+100)"
			if (formula.startsWith("=")) {
				formula = formula.substring(1).trim().toUpperCase();
			}
			// 1、移除"sum("、")"
			formula = StringUtils.remove(formula, "SUM(");
			formula = StringUtils.remove(formula, ')');
			// 2、将","转换为"+"
			formula = formula.replace(',', '+');
			// 3、解析带":"的表达式，如："A1:B10"
			int loopCount = 0;
			while (formula.indexOf(":") != -1) {
				int index = formula.indexOf(":");
				if (loopCount >= 1000 && index != 0
						&& index != formula.length() - 1) {
					break;
				}
				int beginIndex = 0;
				int endIndex = formula.length() - 1;
				beginLoop: for (int i = index - 1; i > 0; i--) {
					// 从冒号index向前遍历，获取冒号表达式起点
					String cTmp = String.valueOf(formula.charAt(i));
					if (allowOper.contains(cTmp)) {
						beginIndex = i + 1;
						break beginLoop;
					}
				}
				endLoop: for (int j = index + 1; j < formula.length() - 1; j++) {
					// 从冒号index向后遍历，获取冒号表达式终点
					String cTmp = String.valueOf(formula.charAt(j));
					if (allowOper.contains(cTmp)) {
						endIndex = j - 1;
						break endLoop;
					}
				}
				if (beginIndex < endIndex) {
					String exprTmp = formula
							.substring(beginIndex, endIndex + 1);
					String formatStr = analysePosiExpr(exprTmp);
					formula = StringUtils.replace(formula, exprTmp, formatStr);
				}
				loopCount++;
			}
			// 4、分析计算项是否都是报表指标
			List<String> items = new ArrayList<String>();
			String analyseStr = formula;
			for (String operTmp : allowOper) {
				analyseStr = StringUtils.replace(analyseStr, operTmp, ";");
			}
			String[] itemArray = StringUtils.split(analyseStr, ';');
			for (String itemArrayTmp : itemArray) {
				Pattern pattern = Pattern.compile("[A-Z]+\\d+");
				Matcher matcher = pattern.matcher(itemArrayTmp);
				if (matcher.matches() && !items.contains(itemArrayTmp)) {
					items.add(itemArrayTmp);
				}
			}
			Collections.sort(items, new Comparator<String>() {

				@Override
				public int compare(String item1, String item2) {
					Map<String, Object> firstMap = getRowColByCellPosi(item1);
					Integer row1 = (Integer) firstMap.get("row");
					Integer col1 = (Integer) firstMap.get("col");
					Map<String, Object> secMap = getRowColByCellPosi(item2);
					Integer row2 = (Integer) secMap.get("row");
					Integer col2 = (Integer) secMap.get("col");
					// 将形如 A1 , A11 , EA11 的表达式排序成 EA11 , A11 , A1
					if (col2 != col1) {
						return col2 - col1;
					} else if (row2 != row1) {
						return row2 - row1;
					} else {
						return 0;
					}
				}

			});
			transLoop: for (String itemTmp : items) {
				if (cellIdxsMap.containsKey(itemTmp)) {
					String indexNoTmp = cellIdxsMap.get(itemTmp);
					if (StringUtils.isEmpty(indexNoTmp)) {
						// 公式中单元格不是报表指标，无法构建计算公式
						formula = "";
						srcIndexNo = new StringBuilder("");
						break transLoop;
					}
					String transExpr = "I('" + indexNoTmp + "')";
					formula = StringUtils.replace(formula, itemTmp, transExpr);
					if (!"".equals(srcIndexNo.toString())) {
						srcIndexNo.append(",");
					}
					srcIndexNo.append(indexNoTmp);
				} else {
					// 公式中单元格不是报表指标，无法构建计算公式
					formula = "";
					srcIndexNo = new StringBuilder("");
					break transLoop;
				}
			}
		}
		returnMap.put("srcIndexNo", srcIndexNo.toString());
		returnMap.put("formulaContent", formula);
		return returnMap;
	}

	/**
	 * 计算单元格行索引值和列索引值（均从0开始）
	 * 
	 * @param cellNo 单元格号
	 * @return 行索引值和列索引值数组，下标0是行索引值，下标1是列索引值；出错时返回null
	 */
	public static int[] getRowNoColumnIdx(String cellNo) {
		int[] rowNoColumnIdx = new int[2];
		return getRowNoColumnIdx(cellNo, rowNoColumnIdx) ? rowNoColumnIdx : null;
	}

	/**
	 * 计算单元格行索引值和列索引值（均从0开始）
	 * 
	 * @param cellNo 单元格号
	 * @param rowNoColumnIdx 返回的行索引值和列索引值数组，下标0是行索引值，下标1是列索引值
	 * @return 计算成功时返回true，否则返回false
	 */
	public static boolean getRowNoColumnIdx(String cellNo, int[] rowNoColumnIdx) {
		int rowNo = 0;
		int columnNo = 0;
		try {
			for (int i = 0; i < cellNo.length(); i ++) {
				char ch = cellNo.charAt(i);
				if (ch >= 'A' && ch <= 'Z') {
					columnNo = columnNo * 26 + ch - 'A' + 1;
				} else {
					rowNo = Integer.parseInt(cellNo.substring(i));
					break;
				}
			}
		} catch (NumberFormatException e) {
			return false;
		}
		if (rowNo <= 0 || columnNo <= 0) {
			return false;
		}
		rowNoColumnIdx[0] = rowNo - 1;
		rowNoColumnIdx[1] = columnNo - 1;
		return true;
	}

	public static Map<String, Object> getRowColByCellPosi(String posiStr) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		int[] rowNoColumnIdx = getRowNoColumnIdx(posiStr);
		if (rowNoColumnIdx != null) {
			returnMap.put("row", new Integer(rowNoColumnIdx[0]));
			returnMap.put("col", new Integer(rowNoColumnIdx[1]));
		}
		return returnMap;
	}

	/**
	 * 获取由Excel行索引值和列索引值定位的单元格号
	 * 
	 * @param rowIdx 行索引值，由0开始 
	 * @param columnIdx 列索引值，由0开始
	 * @return 单元格号
	 */
	public static String toABC(int rowIdx, int columnIdx) {
		StringBuilder sb = new StringBuilder();
		while (true) {
			int remainder = columnIdx % 26;
			sb.insert(0, (char)('A' + remainder));
			columnIdx = (columnIdx - remainder) / 26;
			if (columnIdx == 0) {
				break;
			}
			columnIdx --;
		}
		sb.append(rowIdx + 1);
		return sb.toString();
	}

	/** Private method begin **/

	/**
	 * 将形如A1:A10的表达式，转换成A1+A2+...+A10
	 * 
	 * @param posiExpr
	 * @return
	 */
	private static String analysePosiExpr(String posiExpr) {
		StringBuilder formatExpr = new StringBuilder("");
		if (!StringUtils.isEmpty(posiExpr)) {
			Pattern pattern = Pattern.compile("[A-Z]+\\d+:[A-Z]+\\d+");
			Matcher matcher = pattern.matcher(posiExpr);
			if (matcher.matches()) {
				// 形如 A1:B10
				String[] posis = StringUtils.split(posiExpr, ':');
				if (posis.length == 2) {
					Map<String, Object> beginMap = getRowColByCellPosi(posis[0]);
					Map<String, Object> endMap = getRowColByCellPosi(posis[1]);
					Integer beginRow = (Integer) beginMap.get("row");
					Integer beginCol = (Integer) beginMap.get("col");
					Integer endRow = (Integer) endMap.get("row");
					Integer endCol = (Integer) endMap.get("col");
					for (int r = beginRow; r <= endRow; r++) {
						for (int c = beginCol; c <= endCol; c++) {
							String posiTmp = toABC(r, c);
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

	public static Pattern getDollarCellNoPattern() {
		return dollarCellNoPattern;
	}
}
