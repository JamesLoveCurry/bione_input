/**
 * 
 */
package com.yusys.bione.plugin.design.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCalcRule;
import com.yusys.bione.plugin.rptidx.entity.RptIdxTimeMeasure;
import com.yusys.bione.plugin.rptidx.entity.RptIdxValType;

/**
 * <pre>
 * Title:指标公式相关工具类
 * Description: 指标公式相关工具类
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
public class IdxFormulaUtils {

	/**
	 * 生成维度过滤表达式
	 * 
	 * @param filterModels
	 *            key-dimTypeNo ; value-过滤方式
	 * @param isSum
	 *            指标是否汇总
	 * @param indexFilterVal
	 *            key-dimTypeNo ; value-过滤值1,过滤值2,过滤值3集合
	 * @param dimTypeMap
	 *            key-dimTypeNo；value-维度类型对象
	 * @return
	 */
	public static String generateByDimFilter(Map<String, String> filterModels,
			String isSum, Map<String, List<String>> indexFilterVal,
			Map<String, RptDimTypeInfo> dimTypeMap) {
		StringBuilder formulaContent = new StringBuilder("");
		if (StringUtils.isEmpty(isSum)) {
			isSum = GlobalConstants4plugin.COMMON_BOOLEAN_YES;
		}
		if (indexFilterVal == null) {
			return formulaContent.toString();
		}
		Iterator<String> it = indexFilterVal.keySet().iterator();
		while (it.hasNext()) {
			String dimNo = it.next();
			String filterModel = GlobalConstants4plugin.RPT_IDX_FILTER_MODE_IN;
			if (filterModels != null && filterModels.size() > 0) {
				filterModel = filterModels.get(dimNo);
			}
			List<String> filterVal = indexFilterVal.get(dimNo);
			if (filterVal == null || filterVal.size() <= 0
					|| StringUtils.isEmpty(dimNo)
					|| StringUtils.isEmpty(filterModel)) {
				continue;
			}
			// 构造组合公式
			if (!"".equals(formulaContent.toString())) {
				formulaContent.append(" && ");
			}
			if (filterVal.size() > 1) {
				formulaContent.append("(");
			}
			StringBuilder sb = new StringBuilder("");
			String splitStr = "||";
			String operStr = "==";
			String sumOperStr = "LIKE";
			if (GlobalConstants4plugin.RPT_IDX_FILTER_MODE_NOT_IN.equals(filterModel)) {
				splitStr = "&&";
				operStr = "!=";
				sumOperStr = "NOTLIKE";
			}
			RptDimTypeInfo dimTypeInfo = dimTypeMap.get(dimNo);
			if (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(isSum)
					&& dimTypeInfo != null
					&& GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE.equals(dimTypeInfo
							.getDimTypeStruct())) {
				// 汇总指标,且维度类型是树形
				for (String valTmp : filterVal) {
					if (!StringUtils.isEmpty(sb.toString())) {
						sb.append(splitStr);
					}
					sb.append(sumOperStr).append("($").append(dimNo)
							.append(",'").append(valTmp).append("%')");
				}
			} else {
				// 非汇总指标
				for (String valTmp : filterVal) {
					if (!StringUtils.isEmpty(sb.toString())) {
						sb.append(splitStr);
					}
					sb.append("$").append(dimNo).append(operStr).append("'")
							.append(valTmp).append("'");
				}
			}
			formulaContent.append(sb.toString());
			if (filterVal.size() > 1) {
				formulaContent.append(")");
			}
		}
		return formulaContent.toString();
	}

	/**
	 * 指标列表中，生成派生公式
	 * 
	 * @param indexNo
	 * @param calcRule
	 * @param timeMeasure
	 * @param valType
	 * @param filterFormula
	 * @return eg: I('indexNo') A('indexNo','M') -
	 *         LastMonthEnd(A('indexNo','M')) 累计 A('indexNo','M','Y') -
	 *         LastMonthEnd(A('indexNo','M','Y')) 均值
	 *         I('indexNo[$dimTypeNo==#dimNo#]') -
	 *         Yesterday('indexNo[$dimTypeNo==#dimNo#]') 带过滤的公式
	 */
	public static String generateColCalcFormula(String indexNo,
			RptIdxCalcRule calcRule, RptIdxTimeMeasure timeMeasure,
			RptIdxValType valType,String filterFormula) {
		String formulaContent = "";
		String replaceKey1 = "$1";
		String replaceKey2 = "$2";
		String replaceKey3 = "$3";
		String replaceStr3 = "LastMonth($1)";
		if (StringUtils.isNotEmpty(filterFormula)) {
			filterFormula = filterFormula.replace('\'', '#');
		}
		String idxTarget = StringUtils.isEmpty(filterFormula) ? indexNo
				: indexNo + "[" + filterFormula + "]";
		String replaceStr1 = idxTarget;
		if (StringUtils.isNotEmpty(indexNo)) {
			// 1、计算规则
			if (calcRule != null
					&& StringUtils.isNotEmpty(calcRule.getRuleTemplate())) {
				formulaContent = calcRule.getRuleTemplate();
				formulaContent = StringUtils.replace(formulaContent, replaceKey1, idxTarget);
				replaceStr1 = formulaContent;
			} else {
				formulaContent = "I('" + idxTarget + "')";
				replaceStr1 = formulaContent;
			}
			// 2、时间度量
			if (timeMeasure != null
					&& StringUtils.isNotEmpty(timeMeasure.getMeasureTemplate())) {
				String replaceStr = formulaContent;
				// weijx 特殊处理取当日的情况
				if (calcRule.getRuleType().equals("01")) {
					replaceStr = "'" + idxTarget + "'";
				}
				formulaContent = timeMeasure.getMeasureTemplate();
				formulaContent = StringUtils.replace(formulaContent, replaceKey1, replaceStr);
				replaceStr3 = StringUtils.replace(replaceStr3, replaceKey1, replaceStr);
			}
			// 3、取值方式
			if (valType != null
					&& StringUtils.isNotEmpty(valType.getModeTemplate())) {

				String replaceStr2 = formulaContent;
				formulaContent = valType.getModeTemplate();
				formulaContent = StringUtils.replace(formulaContent, replaceKey1, replaceStr1);
				formulaContent = StringUtils.replace(formulaContent, replaceKey2, replaceStr2);
				formulaContent = StringUtils.replace(formulaContent, replaceKey3, replaceStr3);
			}
		}
		return formulaContent;
	}
}
