package com.yusys.bione.plugin.valid.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.common.FelBuilder;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.parser.FelNode;
import com.yusys.bione.comp.utils.NumberUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.valid.web.vo.ExpressionBatchVO;
import com.yusys.bione.plugin.valid.web.vo.ExpressionExcelFunctionVO;

/**
 * <pre>
 *   Title: 表达式解析工具类
 *   Description:  解析 明细报表 校验配置 表达式
 * </pre>
 * 
 * @author lcy	lizy6@yusys.com.cn
 * @version 1.00.00
 * 
 * <pre>
 *  修改记录
 *     修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class ExpressionAnalyticUtils {
	
	/**
	 * 解析计算公式 中 变量元素
	 * @param expression      
	 * @param antlrParseUtils 解析工具对象
	 * @return List<String> 变量元素
	 */
	public static List<String> paresFormula(String formula, AntlrParseUtils antlrParseUtils) {
		List<String> ctxKeys = Lists.newArrayList();
		paresExp(formula, antlrParseUtils, ctxKeys);
		return ctxKeys;
	}
	
	/**
	 * 解析表达式 中 变量元素
	 * @param exp
	 * @param ap
	 * @param ctxKeys
	 */
	private static void paresExp(String exp, AntlrParseUtils ap, List<String> ctxKeys) {
		FelNode fn = ap.parse(exp);
		if(AntlrParseUtils.funFilter.call(fn)) {
			String[] childExps = StringUtils.split(exp, fn.getText());
			for(String childExp : childExps) {
				paresExp(childExp, ap, ctxKeys);
			}
		}else {
			ctxKeys.add(fn.getText());
		}
	}
	
    /**
     * 使用变量 计算表达式
     * 
     */
    public static Object getFormulaValueUseVariable(Map<String, Object> contextMap, String exp) {
    	 FelEngine fel = FelBuilder.engine();
         FelContext ctx = fel.getContext();
         for(String ctxKey : contextMap.keySet()) {
        	 if(contextMap.get(ctxKey) != null && NumberUtils.isNumeric(contextMap.get(ctxKey).toString())) {
        		 if(contextMap.get(ctxKey).toString().length() > 1 
        				 && contextMap.get(ctxKey).toString().substring(0, 1).equals("0") 
        				 && contextMap.get(ctxKey).toString().indexOf(".") == -1){// 排除01,02等字符串
        			 ctx.set(ctxKey, contextMap.get(ctxKey));
        		 }else{
        			 ctx.set(ctxKey, new BigDecimal(contextMap.get(ctxKey).toString()));
        		 }
 			}else{
 				ctx.set(ctxKey, contextMap.get(ctxKey));
 			}
         }
         Object result = fel.eval(exp);
         return result;
    }
    
	/**
	 * 可解析的 excel 函数
	 */
	private static String [] EXPRESSION_CFG_EXCEL_FUNCTIONS = {"SUM","MIN","MAX"};

	/**
	 * 根据左右表达式 判断是否批量
	 * @param lefExpression
	 * @param rightExpression
	 * @return
	 */
	public static boolean isBashValidCfg(String lefExpression, String rightExpression) {
		boolean isBatch = false;
		//左右表达式均为批量公式 则为批量
		if(isBashValidExpressionCfg(lefExpression) 
				|| isBashValidExpressionCfg(rightExpression)) {
			isBatch = true;
		}
		return isBatch;
	}

	/**
	 * 判断表达式 是否为批量公式
	 * @param expression
	 * @return
	 */
	public static boolean isBashValidExpressionCfg(String expression) {
		boolean isBatch = false;
		if(StringUtils.isNotBlank(expression)){
			if(expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_LEFT) == 0
					&& expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_RIGHT) > 0) {
				isBatch = true;
			}
		}
		return isBatch;
	}

	/**
	 * 校验批量公式
	 * @param expression
	 * @return
	 */
	public static boolean validBashExpression(String expression) {
		boolean isPass = true;
		int leftIndex = expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_LEFT);
		int rightIndex = expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_RIGHT);
		if((leftIndex > -1 || rightIndex > -1)) {
			isPass = false;
		}
		return isPass;
	}
	
	/**
	 * 解析 批量表达式 封装逻辑对象 
	 * @param expression
	 * @param n
	 * @return
	 */
	public static ExpressionBatchVO getExpressionBatchVO(String expression, int n) {
		//封装逻辑对象
		ExpressionBatchVO expBatchVO = null;
		//TODO 正则表达式 校验 expression [1-10]F+D
		if(ExpressionAnalyticUtils.isBashValidExpressionCfg(expression)) {
			//批量处理[1-10]F+G 公式  F+G
			String batchFormula = expression.substring(expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_RIGHT) + 1);
			//批量处理[1-10]F+G 索引字符 1-10
			String batchIndexStr = expression.substring(expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_LEFT)  + 1,
														expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_RIGHT));
			//批量处理[1-10]F+G 开始索引 1
			String batchBeginIndexStr = expression.substring(expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_LEFT) + 1, 
															expression.indexOf(GlobalConstants4plugin.COMMON_STR_MINUS_SIGN));
			int batchBeginIndex = Integer.valueOf(batchBeginIndexStr);
			//批量处理[1-n]F+G 是否包含n 包含则代表不固定行
			boolean isIncludeN = true;
			//批量处理[1-10]F+G 结束索引 10
			int batchEndIndex = batchBeginIndex + n - 1;
			//批量处理[1-10]F+G 批量处理次数 10 次
			int batchCount = n;
			String batchEndIndexStr = expression.substring(expression.indexOf(GlobalConstants4plugin.COMMON_STR_MINUS_SIGN) + 1, 
															expression.indexOf(GlobalConstants4plugin.COMMON_STR_BRACKETS_RIGHT));
			//判断是否包含N
			if(!StringUtils.equalsIgnoreCase(GlobalConstants4plugin.COMMON_STR_N, batchEndIndexStr)) {
				isIncludeN = false;
				batchEndIndex = Integer.valueOf(batchEndIndexStr);
				int count = batchEndIndex - batchBeginIndex + 1;
				//不包含N需要 判断配置开始行号与结束行号差值 与 记录数大小 小的值为批量处理次数
				if(count < batchCount) {
					batchCount = count;
				}
			}
			//是否需要批量处理
			boolean isBatch = true;
			expBatchVO = new ExpressionBatchVO(batchFormula, 
					batchIndexStr, batchBeginIndex, batchEndIndex, batchCount, isIncludeN, isBatch);
		}else {
			//是否需要批量处理
			boolean isBatch = false;
			expBatchVO = new ExpressionBatchVO();
			expBatchVO.setBatchFormula(expression);
			expBatchVO.setBatchCount(n);
			expBatchVO.setBatch(isBatch);
		}
		return expBatchVO;
	}
	
	/**
	 * 判断表达式 是否包含Excel函数
	 * @param expression
	 * @return
	 */
	public static boolean isIncludeExcelFunctions(String expression) {
		boolean isInclude = false;
		for(String excelFuncStr : EXPRESSION_CFG_EXCEL_FUNCTIONS) {
			isInclude = isIncludeExcelFunctions(excelFuncStr ,expression);
			if(isInclude) {
				break;
			}
		}
		return isInclude;
	}
	
	/**
	 * 判断表达式 是否包含Excel函数
	 * @param excelFuncStr  Excel函数 名称
	 * @param expression    表达式
	 * @return
	 */
	private static boolean isIncludeExcelFunctions(String excelFuncStr, String expression) {
		boolean isInclude = false;
		if(expression.indexOf(excelFuncStr) > -1) {
			isInclude = true;
		}
		return isInclude;
	}

	/**
	 * 解析 Excel公式
	 * @param expression
	 * @param excelFuncList
	 * @return
	 */
	public static String getExcelFunctions(String expression, List<ExpressionExcelFunctionVO> excelFuncList) throws Exception {
		int funcIndex=0;
		//解析SUM
		//解析MIN MAX
		for(String excelFuncType : EXPRESSION_CFG_EXCEL_FUNCTIONS) {
			expression = getExcelSumFunctions(expression, funcIndex, excelFuncType, excelFuncList);
		}
		return expression;
	}

	/**
	 * 解析Excel 表达式
	 * @param expression
	 * @param funcIndex
	 * @param excelFuncType
	 * @param excelFuncList
	 * @return
	 */
	private static String getExcelSumFunctions(String expression, int funcIndex, String excelFuncType, List<ExpressionExcelFunctionVO> excelFuncList) throws Exception {
		// 解析SUM
		String excelFuncStr = excelFuncType+GlobalConstants4plugin.COMMON_STR_PARENTHESES_LEFT;
		if (isIncludeExcelFunctions(excelFuncStr, expression)) {
			funcIndex ++;
			int beginIndex = expression.lastIndexOf(excelFuncStr);
			// 前缀
			String prefix = expression.substring(0, beginIndex);
			String expTmp = expression.substring(beginIndex + 4);
			int excelFuncEndIndex = expTmp.indexOf(GlobalConstants4plugin.COMMON_STR_PARENTHESES_RIGHT);
			String excelFuncFormula = expTmp.substring(0, excelFuncEndIndex);
			//替换KEY
			String excelFuncKey = excelFuncType + funcIndex;
			//封装配置信息
			if(excelFuncList != null) {
				ExpressionExcelFunctionVO excelFuncVO = new ExpressionExcelFunctionVO();
				excelFuncVO.setExcelFuncType(excelFuncType);
				excelFuncVO.setExcelFuncKey(excelFuncKey);
				excelFuncVO.setExcelFuncFormula(excelFuncFormula);
				excelFuncList.add(excelFuncVO);
			}
			//拼接替换后的字符串
			expression = prefix + excelFuncKey + expTmp.substring(excelFuncEndIndex + 1);
			//迭代获取
			expression = getExcelSumFunctions(expression, funcIndex, excelFuncType, excelFuncList);
		}
		return expression;
	}
	
	/**
	 * 正则表达式 校验字符串
	 * @param regex
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	
	
	/**
	 * Excel 的 SUM() 函数 转换为可计算公式
	 * SUM(D1:D4)
	 * SUM(D9:H9)
	 * SUM(C9,F10,D21)
	 * SUM([9-n]C)
	 * @param sumFormula D1:D4
	 * @return  D1+D2+D3+D4
	 */
	public static String paresExpSUM(String sumFormula, List<String> cellNmList, int rowCount) {
		//TODO 正则表达式校验 公式
		//TODO 异常处理
		StringBuffer sumExpression = new StringBuffer();
		
		if(sumFormula.indexOf(GlobalConstants4plugin.COMMON_STR_COLON) > 1) {
			//D1:D4 处理
			String[] cellNms = sumFormula.split(GlobalConstants4plugin.COMMON_STR_COLON);
			
			String beginCellNm = cellNms[0];
			Integer beginCellColumnNum = excelColStrToNum(beginCellNm);
			Integer beginCellRowNum = getNumbers(beginCellNm);
			
			String endCellNm = cellNms[1];
			Integer endCellColumnNum = excelColStrToNum(endCellNm);
			Integer endCellRowNum = getNumbers(endCellNm);
			//循环 列
			String colStr="";
			for(int colIndex = beginCellColumnNum; colIndex <= endCellColumnNum; colIndex++) {
				colStr = excelColIndexToStr(colIndex);
				for(int rowIndex = beginCellRowNum; rowIndex <= endCellRowNum; rowIndex++) {
					//循环 行
					sumExpression.append(colStr+rowIndex);
					sumExpression.append(GlobalConstants4plugin.COMMON_STR_PLUS_SIGN);
					
					if(cellNmList != null) {
						cellNmList.add(colStr+rowIndex);
					}
				}
			}
			//D1+D2+D3+D4
			sumExpression.deleteCharAt(sumExpression.length() - 1);
			
		}else if(sumFormula.indexOf(GlobalConstants4plugin.COMMON_STR_COMMA) > 1) {
			//C9,F10,D21  处理
			String[] cellNms = sumFormula.split(GlobalConstants4plugin.COMMON_STR_COMMA);
			for(String cellNm : cellNms) {
				sumExpression.append(cellNm);
				sumExpression.append(GlobalConstants4plugin.COMMON_STR_PLUS_SIGN);
				
				if(cellNmList != null) {
					cellNmList.add(cellNm);
				}
			}
			//C9+F10+D21
			sumExpression.deleteCharAt(sumExpression.length() - 1);
		}else if(isBashValidExpressionCfg(sumFormula)) {
			//SUM([9-n]C)
			ExpressionBatchVO expBatchVO = getExpressionBatchVO(sumFormula, rowCount);
			for(int batchIndex = 0; batchIndex < expBatchVO.getBatchCount(); batchIndex ++) {
				int rowIndex = expBatchVO.getBatchBeginIndex() + batchIndex;
				String cellNm = expBatchVO.getBatchFormula() + rowIndex;
				cellNmList.add(cellNm);
				sumExpression.append(cellNm);
				sumExpression.append(GlobalConstants4plugin.COMMON_STR_PLUS_SIGN);
			}
			//C9+C10+C11...Cn
			sumExpression.deleteCharAt(sumExpression.length() - 1);
		}
		
		return sumExpression.toString();
	}
	

    /**
	 * 该方法用来将Excel中的ABCD列转换成具体的数据
	 * @param column:ABCD列名称
	 * @return integer：将字母列名称转换成数字
	 * **/
    public static int excelColStrToNum(String column) {
    	column = splitNotNumber(column);
        int num = 0;
        int result = 0;
        int length =column.length(); 
        for(int i = 0; i < length; i++) {
            char ch = column.charAt(length - i - 1);
            num = (int)(ch - 'A' + 1) ;
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }
 
    /**
	 * 该方法用来将具体的数据转换成Excel中的ABCD列
	 * @param int：需要转换成字母的数字
	 * @return column:ABCD列名称
	 * **/
    public static String excelColIndexToStr(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }
	
	/**
	 * 截取数字
	 * @param content
	 * @return
	 */
	public static Integer getNumbers(String content) {  
	    Pattern pattern = Pattern.compile("\\d+");  
	    Matcher matcher = pattern.matcher(content);  
	    while (matcher.find()) {  
	       return Integer.valueOf(matcher.group(0));  
	    }  
	    return 0;  
	}  
	  
	/**
	 * 截取非数字 
	 * @param content
	 * @return
	 */
	public static String splitNotNumber(String content) {  
	    Pattern pattern = Pattern.compile("\\D+");  
	    Matcher matcher = pattern.matcher(content);  
	    while (matcher.find()) {  
	        return matcher.group(0);  
	    }  
	    return "";  
	}
	  
	/**
	 * 判断一个字符串是否含有数字
	 * @param content
	 * @return
	 */
	public static boolean HasDigit(String content) {
	    boolean flag = false;
	    Pattern p = Pattern.compile(".*\\d+.*");
	    Matcher m = p.matcher(content);
	    if (m.matches()) {
	        flag = true;
	    }
	    return flag;
	}
	
	
}
