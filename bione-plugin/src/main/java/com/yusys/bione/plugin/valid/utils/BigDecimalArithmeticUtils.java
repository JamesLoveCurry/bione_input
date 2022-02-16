package com.yusys.bione.plugin.valid.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;

/**
 * <pre>
 *   Title: BigDecimal运算工具类
 *   Description:  提供 四则运算 基本方法
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
public class BigDecimalArithmeticUtils {

	/**
	 * 加法
	 * @param b1
	 * @param b2
	 * @return b1 + b2
	 */
	public static BigDecimal add(BigDecimal b1, BigDecimal b2) {
		BigDecimal rs = b1.add(b2);
		return rs;
	}
	
	/**
	 * 减法
	 * @param b1
	 * @param b2
	 * @return b1 - b2
	 */
	public static BigDecimal subtract(BigDecimal b1, BigDecimal b2) {
		if(b1 == null) {
			b1 = new BigDecimal(0);
		}
		if(b2 == null) {
			b2 = new BigDecimal(0);
		}
		BigDecimal rs = b1.subtract(b2);
		return rs;
	}
	
	/**
	 * 乘法
	 * @param b1
	 * @param b2
	 * @return b1 * b2
	 */
	public static BigDecimal multiply(BigDecimal b1, BigDecimal b2) {
		BigDecimal rs = b1.multiply(b2);
		return rs;
	}
	
	/**
	 * 除法
	 * @param b1
	 * @param b2
	 * @return b1 / b2
	 */
	public static BigDecimal divide(BigDecimal b1, BigDecimal b2) {
		BigDecimal rs = b1.divide(b2);
		return rs;
	}
	
	/**
	 * 逻辑校验处理
	 * @param leftDetailData  左公式数据
	 * @param rightDetailData 右公式数据
	 * @param preOperType     运算符
	 * @param floatValStr     容差值
	 * @return true 通过 false
	 */
	public static boolean LogicCheck(BigDecimal leftDetailData, BigDecimal rightDetailData, String operType, BigDecimal floatVal) {
		boolean passFlag = false;
		// 默认 左公式数据 右公式数据 均非空
		if(leftDetailData == null || rightDetailData == null) {
			return !passFlag;
		}
		//根据计算结果的小数位数，设置计算容差后的精确位数;(之前程序默认写死精确2位)
		int scale = leftDetailData.scale()==0?2:leftDetailData.scale();//精度为0默认保留两位，不为零时，根据实际值的精度
		if(floatVal == null) {
			floatVal = new BigDecimal(0);
		}
		switch (operType) {
			case GlobalConstants4plugin.FRS_OPER_TYPE_EQUAL:
				//等于的为差值的绝对值大于容差值
				passFlag = leftDetailData.subtract(rightDetailData).abs().setScale( scale, BigDecimal.ROUND_HALF_UP).compareTo( floatVal ) > 0;	
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_NOT_EQUAL:
				//不等于的为差值的绝对值小于等于容差值	
				passFlag = leftDetailData.subtract(rightDetailData).abs().setScale( scale, BigDecimal.ROUND_HALF_UP).compareTo( floatVal ) <= 0;
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_GREATER_OR_EQUAL:
				//大于等于的为差值加上容差值后小于零
				passFlag = leftDetailData.add( floatVal ).setScale( scale, BigDecimal.ROUND_HALF_UP).compareTo( rightDetailData ) < 0 ;
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_LESS_OR_EQUAL:
				//小于的为差值减去容差值后大于等于零
				passFlag = leftDetailData.subtract( floatVal ).setScale( scale, BigDecimal.ROUND_HALF_UP ).compareTo( rightDetailData ) > 0 ;
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_GREATER:
				//大于的为差值加上容差值后小于等于零	
				passFlag = leftDetailData.add( floatVal ).setScale( scale, BigDecimal.ROUND_HALF_UP).compareTo( rightDetailData ) <= 0 ;
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_LESS:
				//小于等于的为差值减去容差值后大于零	
				passFlag = leftDetailData.subtract( floatVal ).setScale( scale, BigDecimal.ROUND_HALF_UP ).compareTo( rightDetailData ) >= 0 ;
				break;
			default:
				break;
		}
		
		return !passFlag;
	}

	/**
	 * 单位换算
	 * @param val
	 * @param unit
	 * @return
	 */
	public static BigDecimal dealUnit(BigDecimal val, String unit) {
		try {
			if (unit != null) {
				if (unit.equals(GlobalConstants4plugin.DATA_UNIT_YUAN)) { // 元
					
				} else if (unit.equals(GlobalConstants4plugin.DATA_UNIT_HUNDRED)) { // 百
					//DecimalFormat df = new DecimalFormat("#.####");
					val = val.divide(new BigDecimal(100));
				} else if (unit.equals(GlobalConstants4plugin.DATA_UNIT_THOUSAND)) { // 千
					//DecimalFormat df = new DecimalFormat("#.#####");
					val = val.divide(new BigDecimal(1000));
				} else if (unit.equals(GlobalConstants4plugin.DATA_UNIT_TEN_THOUSAND)) { // 万
					//DecimalFormat df = new DecimalFormat("#.######");
					val = val.divide(new BigDecimal(10000));
				} else if (unit.equals(GlobalConstants4plugin.DATA_UNIT_HUNDRED_MILLION)) { // 亿
					//DecimalFormat df = new DecimalFormat("#.##########");
					val = val.divide(new BigDecimal(100000000));
				} else if (unit.equals(GlobalConstants4plugin.DATA_UNIT_PERCENT)) {// 百分比
					val = val.multiply(new BigDecimal(100));
				} else if (unit.equals(GlobalConstants4plugin.DATA_UNIT_EMPTY)) {// 无单位
					//DecimalFormat df = new DecimalFormat("#");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		DecimalFormat df = new DecimalFormat("#.##");
		df.format(val);
		return val;
	}
	
	public static String dealUnitInfo(String unit, boolean flag) {
		if (unit.equals(GlobalConstants4plugin.DATA_UNIT_YUAN)) {
			return "元";
		}
		if (unit.equals(GlobalConstants4plugin.DATA_UNIT_HUNDRED)) {
			return "百元";
		}
		if (unit.equals(GlobalConstants4plugin.DATA_UNIT_THOUSAND)) {
			return "千元";
		}
		if (unit.equals(GlobalConstants4plugin.DATA_UNIT_TEN_THOUSAND)) {
			return "万元";
		}
		if (unit.equals(GlobalConstants4plugin.DATA_UNIT_HUNDRED_MILLION)) {
			return "亿元";
		}
		if (unit.equals(GlobalConstants4plugin.DATA_UNIT_PERCENT)) {
			return "%";
		} else {
			if (flag)
				return "无单位";
			else
				return "";
		}
	}

	/**
	 * in/not in校验
	 * @param leftPreExpressionData
	 * @param rightPreExpressionDatas
	 * @param preOperType
	 * @param object
	 * @return
	 */
	public static boolean LogicCheckSpecial(Object leftPreExpressionData,
			String[] rightPreExpressionDatas, String preOperType, Object object) {
		boolean passFlag = false;
		List<String> rightPreDatas = Arrays.asList(rightPreExpressionDatas);
		// 将右表达式中的‘空’替换成‘’
		int index = rightPreDatas.indexOf("空");
		if (index != -1) {
			rightPreDatas.set(index, null);
		}
		// 校验in运算符
		if (preOperType.equals("in")) {
			// 右公式中不包含左公式值，则返回false
			if (!rightPreDatas.contains(leftPreExpressionData != null ? leftPreExpressionData.toString() : null)) {
				passFlag = false;
			}else {
				passFlag = true;
			}
		}else if (preOperType.equals("not in")) {// 校验in运算符
			// 右公式中不包含左公式值，则返回false
			if (rightPreDatas.contains(leftPreExpressionData != null ? leftPreExpressionData.toString() : null)) {
				passFlag = false;
			}else {
				passFlag = true;
			}
		} else {
			BigDecimal rightDetailData = null;
			switch (preOperType) {
				case GlobalConstants4plugin.FRS_OPER_TYPE_EQUAL:
					//等于
					if (StringUtils.isBlank(rightPreDatas.get(0))) {
						passFlag =  StringUtils.isBlank(leftPreExpressionData.toString());
					} else {
						passFlag = leftPreExpressionData.toString().equals(rightPreDatas.get(0));
					}	
					break;
				case GlobalConstants4plugin.FRS_OPER_TYPE_NOT_EQUAL:
					//不等于
					if (StringUtils.isBlank(rightPreDatas.get(0))) {
						passFlag = !StringUtils.isBlank(leftPreExpressionData.toString());
					} else {
						passFlag = !leftPreExpressionData.toString().equals(rightPreDatas.get(0));
					}
					break;
				case GlobalConstants4plugin.FRS_OPER_TYPE_GREATER_OR_EQUAL:
					//大于等于的为差值加上容差值后小于零
					rightDetailData = new BigDecimal(rightPreDatas.get(0));
					if(StringUtils.isNotBlank(leftPreExpressionData.toString())){
						passFlag = new BigDecimal(leftPreExpressionData.toString()).compareTo(rightDetailData) < 0;
					}else{
						passFlag = false;
					}
					break;
				case GlobalConstants4plugin.FRS_OPER_TYPE_LESS_OR_EQUAL:
					//小于的为差值减去容差值后大于等于零
					rightDetailData = new BigDecimal(rightPreDatas.get(0));
					if(StringUtils.isNotBlank(leftPreExpressionData.toString())){
						passFlag = new BigDecimal(leftPreExpressionData.toString()).compareTo(rightDetailData) > 0;
					}else{
						passFlag = false;
					}
					break;
				case GlobalConstants4plugin.FRS_OPER_TYPE_GREATER:
					//大于的为差值加上容差值后小于等于零	
					rightDetailData = new BigDecimal(rightPreDatas.get(0));
					if(StringUtils.isNotBlank(leftPreExpressionData.toString())){
						passFlag = new BigDecimal(leftPreExpressionData.toString()).compareTo(rightDetailData) <= 0;
					}else{
						passFlag = false;
					}
					break;
				case GlobalConstants4plugin.FRS_OPER_TYPE_LESS:
					//小于等于的为差值减去容差值后大于零	
					rightDetailData = new BigDecimal(rightPreDatas.get(0));
					if(StringUtils.isNotBlank(leftPreExpressionData.toString())){
						passFlag = new BigDecimal(leftPreExpressionData.toString()).compareTo(rightDetailData) >= 0;
					}else{
						passFlag = false;
					}
					
					break;
				default:
					break;
			}
		}
		return passFlag;
	}

	/**
	 * 正则表达式校验
	 * @param leftPreExpressionData
	 * @param rightPreExpressionData
	 * @param preOperType
	 * @param object
	 * @return
	 */
	public static boolean LogicCheckSpecialExp(Object leftPreExpressionData,
			String rightPreExpressionData, String preOperType, Object object) {
		boolean passFlag = false;
		boolean isMatch = Pattern.matches(rightPreExpressionData, leftPreExpressionData.toString());
		if (isMatch) {
			passFlag = true;
		}
		return passFlag;
	}

	/**
	 * 字段排序校验
	 * @param bigDecimal
	 * @param bigDecimal2
	 * @param logicOperType
	 * @return
	 */
	public static boolean LogicCheckSort(BigDecimal leftDetailData, BigDecimal rightDetailData, String logicOperType) {
		boolean passFlag = false;
		if(leftDetailData == null || rightDetailData == null){
			return passFlag;
		}
		switch (logicOperType) {
			case GlobalConstants4plugin.FRS_OPER_TYPE_EQUAL:
				//等于
				passFlag = leftDetailData.toString().equals(rightDetailData);
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_NOT_EQUAL:
				//不等于
				passFlag = !leftDetailData.toString().equals(rightDetailData);
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_GREATER_OR_EQUAL:
				//大于等于的为差值加上容差值后小于零 
				passFlag = leftDetailData.compareTo(rightDetailData) != -1;
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_LESS_OR_EQUAL:
				//小于的为差值减去容差值后大于等于零
				passFlag = leftDetailData.compareTo(rightDetailData) != 1;
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_GREATER:
				//大于的为差值加上容差值后小于等于零	
				passFlag = leftDetailData.compareTo(rightDetailData) == 1;
				break;
			case GlobalConstants4plugin.FRS_OPER_TYPE_LESS:
				//小于等于的为差值减去容差值后大于零	
				passFlag = leftDetailData.compareTo(rightDetailData) == -1;
				break;
			default:
				break;
		}
		return passFlag;
	}
}
