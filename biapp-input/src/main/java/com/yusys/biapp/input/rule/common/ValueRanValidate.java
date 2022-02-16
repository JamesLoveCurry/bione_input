package com.yusys.biapp.input.rule.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.singularsys.jep.Jep;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.utils.DateUtils;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.StringUtils2;

/**
 * 值范围校验<br>
 * 1.数值范围<br>
 * 2.日期范围<br>
 * 
 * @author tobe
 */
@Component(value = "valueRanValidate")
public class ValueRanValidate implements IValidate {
	
	/**
	 * 校验方法,校验提供的数据
	 * @param taskCase 任务实例
	 * @param temp 模板实体
	 * @param ruleInfoList 补录规则
	 * @param dataList 需要校验的数据
	 * @param obj 可变参数<br>
	 *            第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 * @return 返回校验结果
	 */
	@Override
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList,
			List<Map<String, Object>> dataList, Object... obj) {

		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		RptInputListDataRuleInfo RptInputListDataRuleInfo = null;
		try {
			if (dataList == null || dataList.size() == 0) {
				// resultList.add(resultList.size(), RptInputLstValidateLog.createLog(null, "没有需要校验的数据！", 0));
				return resultList;
			}
			int start = 1;
			if (obj != null && obj.length > 0) {
				start = 1;// Integer.valueOf(obj[0].toString());
			}
			/** 1.组成表达式 **/
			Jep jep = new Jep();
			jep.setComponent(new StandardConfigurableParser());
			/** 2.数据校验 **/
			for (RptInputListDataRuleInfo ruleInfo : ruleInfoList) {
				RptInputListDataRuleInfo = ruleInfo;
				String min = StringUtils.EMPTY, max = StringUtils.EMPTY;

				if (StringUtils.isNotBlank(ruleInfo.getTimeRange())) {	// 日期频率
					checkDateRand(taskCase, ruleInfo, dataList, resultList, start);
					continue;
				}
				if (StringUtils.isNotBlank(ruleInfo.getMinVal())) {
					min = StringUtils2.removeChars(ruleInfo.getMinVal(), "[]{}|");
				}
				if (StringUtils.isNotBlank(ruleInfo.getMaxVal())) {
					max = StringUtils2.removeChars(ruleInfo.getMaxVal(), "[]{}|");
				}

				if (StringUtils.isNotBlank(min)) {
					if (min.indexOf("@") > -1) {	// 日期
						checkDate(taskCase, ruleInfo, dataList, resultList);
					} else if (NumberUtils.isNumber(min)) {
						checkNumber(taskCase, ruleInfo, dataList, resultList, jep);
					} else {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验规则[" + ruleInfo.getRuleNm() + "]中的最小值不是数字：" + min, 0));
					}
				} else if (StringUtils.isNotBlank(max)) {
					if (max.indexOf("@") > -1) {	// 日期
						checkDate(taskCase, ruleInfo, dataList, resultList);
					} else if (NumberUtils.isNumber(max)) {
						checkNumber(taskCase, ruleInfo, dataList, resultList, jep);
					} else {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验规则[" + ruleInfo.getRuleNm() + "]中的最小值不是数字：" + max, 0));
					}
				} else if (StringUtils.isNotBlank(ruleInfo.getValueSet())) {
					checkNumber(taskCase, ruleInfo, dataList, resultList, jep);
				} else {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验规则[" + ruleInfo.getRuleNm() + "],最大值最小值同时为空!", 0));
				}
			}
		} catch (Exception e) {
			resultList.add(resultList.size(), RptInputLstValidateLog.createLog(RptInputListDataRuleInfo, "校验出错！" + e.getMessage(), 0));
		}
		return resultList;
	}

	/**
	 * 数值范围校验
	 * 
	 * @param ruleInfo
	 * @param dataList
	 * @param resultList
	 * @param jep
	 * @param start
	 * @throws Exception
	 */
	private void checkNumber(RptTskIns taskCase, RptInputListDataRuleInfo ruleInfo, List<Map<String, Object>> dataList, List<RptInputLstValidateLog> resultList, Jep jep) throws Exception {
		for (int k = 0; k < dataList.size(); k++) {
			Map<String, Object> data = dataList.get(k);
			Object o = data.get(ruleInfo.getFieldNm());
			int start = -1;
			Object obj = data.get("SYS_ORDER_NO");
			if(obj instanceof String){
				start=Integer.parseInt((String)obj);
			}else if(obj instanceof Long){
				start=((Long)obj).intValue();
			}else if(obj instanceof Integer){
				start=((Integer)obj).intValue();
			}
			if (o == null || StringUtils.isBlank(o.toString())) {
				resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + ( start) + "]行数据，在进行[" + ruleInfo.getRuleNm() + "]校验时,校验失败,数据为空！",  start));
				continue;
			}
			if (!NumberUtils.isNumber(o.toString())) {
				resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + ( start) + "]行数据，在进行[" + ruleInfo.getRuleNm() + "]校验时,校验失败,数据[" + o + "]非数值类型！", start));
				continue;
			}

			if (StringUtils.isNotBlank(ruleInfo.getValueSet())) {// 数值集合
				if (!ArrayUtils.asCollection(ruleInfo.getValueSet()).contains(o.toString())) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + ( start) + "]行校验失败,数据[" + o + "]不存在值集合[" + ruleInfo.getValueSet() + "]中！",  start));
					continue;
				}
			}

			StringBuilder sb = new StringBuilder();

			// 最小值判断
			if (StringUtils.isNotBlank(ruleInfo.getMinVal())) {
				if (ruleInfo.getMinVal().indexOf("[") != -1) {// 闭区间
					sb.append(StringUtils2.removeChars(ruleInfo.getMinVal(), "[]|")).append(RuleUtils.LOGIC_CODE_SMALL).append(o);
				} else {
					sb.append(StringUtils2.removeChars(ruleInfo.getMinVal(), "{}|")).append(RuleUtils.LOGIC_CODE_SMALLER).append(o);
				}

				jep.parse(sb.toString());

				if (jep.evaluate().equals(false)) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + start + "]行校验失败！[" + o + "]不满足" + ruleInfo.getErrorTip(),  start));
					continue;
				}
			}
			// 最大值判断
			if (StringUtils.isNotBlank(ruleInfo.getMaxVal())) {
				sb.delete(0, sb.length());
				if (ruleInfo.getMaxVal().indexOf("[") != -1) {// 闭区间
					sb.append(StringUtils2.removeChars(ruleInfo.getMaxVal(), "[]|")).append(RuleUtils.LOGIC_CODE_BIG).append(o);
				} else {// 开区间
					sb.append(StringUtils2.removeChars(ruleInfo.getMaxVal(), "{}|")).append(RuleUtils.LOGIC_CODE_BIGGER).append(o);
				}
				jep.parse(sb.toString());

				if (jep.evaluate().equals(false)) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + start + "]行校验失败！[" + o + "]不满足" + ruleInfo.getErrorTip(),  start));
				}
			}
		}
	}

	/**
	 * 日期范围校验
	 * 
	 * @param ruleInfo
	 * @param dataList
	 * @param resultList
	 * @param jep
	 * @param start
	 * @throws Exception
	 */
	private void checkDate(RptTskIns taskCase, RptInputListDataRuleInfo ruleInfo, List<Map<String, Object>> dataList, List<RptInputLstValidateLog> resultList)
			throws Exception {
		for (int k = 0; k < dataList.size(); k++) {
			Map<String, Object> data = dataList.get(k);
			Object o = data.get(ruleInfo.getFieldNm());
			int start = -1;
			Object objNo = data.get("SYS_ORDER_NO");
			if(objNo instanceof String){
				start = Integer.parseInt((String)objNo);
			}else if(objNo instanceof Long){
				start = ((Long)objNo).intValue();
			}else if(objNo instanceof Integer){
				start = ((Integer)objNo).intValue();
			}
			if (o == null || StringUtils.isBlank(o.toString())) {
				resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + ( start) + "]行数据，[" + ruleInfo.getFieldNm() + "]校验失败,数据为空！", start));
				continue;
			}
			if (DateUtils.getDateStart(o.toString()) == null) {
				resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + ( start) + "]行数据，[" + ruleInfo.getFieldNm() + "]校验失败,数据[" + o + "]非日期类型！", start));
				continue;
			}

			if (StringUtils.isNotBlank(ruleInfo.getValueSet())) {// 数值集合
				if (!ArrayUtils.asCollection(ruleInfo.getValueSet()).contains(o)) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + ( start) + "]行校验失败,数据[" + o + "]不存在值集合[" + ruleInfo.getValueSet() + "]中！", start));
					continue;
				}
			}

			// 最小日期判断
			if (StringUtils.isNotBlank(ruleInfo.getMinVal())) {
//				String startd = SysFunction.replaceDate(ruleInfo.getMinVal().replaceAll("[\\[|\\]|\\{|\\}]", ""), taskCase.getDataDate(), taskCase.getEndTime());
//				Date date1 = DateUtils.getDateStart(startd);
//				Date date2 = DateUtils.getDateStart(o.toString());
//				if (date1 == null || date2 == null) {
//					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + data.get("SYS_ORDER_NO") + "]行校验失败！" + ruleInfo.getFieldNm() + "字段日期格式不对!", start));
//					continue;
//				}
//				if (ruleInfo.getMinVal().indexOf("[") != -1) {// 闭区间
//					if (DateUtils.getDateStart(startd).compareTo(DateUtils.getDateStart(o.toString())) == 1) {
//						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + data.get("SYS_ORDER_NO") + "]行校验失败！[" + o + "]不满足" + SysFunction.replaceDate(ruleInfo.getErrorTip(), taskCase.getDataDate(), taskCase.getEndTime()), start));
//						continue;
//					}
//				} else {
//					if (date1.compareTo(date2) > -1) {
//						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + data.get("SYS_ORDER_NO") + "]行校验失败！[" + o + "]不满足" + SysFunction.replaceDate(ruleInfo.getErrorTip(), taskCase.getDataDate(), taskCase.getEndTime()), start));
//						continue;
//					}
//				}
			}

			// 最大日期判断
			if (StringUtils.isNotBlank(ruleInfo.getMaxVal())) {
//				String endd = SysFunction.replaceDate(ruleInfo.getMaxVal().replaceAll("[\\[|\\]|\\{|\\}]", ""), taskCase.getDataDate(), taskCase.getEndTime());
//				Date date1 = DateUtils.getDateStart(endd);
//				Date date2 = DateUtils.getDateStart(o.toString());
//				if (date1 == null || date2 == null) {
//					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + data.get("SYS_ORDER_NO") + "]行校验失败！" + ruleInfo.getFieldNm() + "字段日期格式不对!", start));
//					continue;
//				}
//
//				if (ruleInfo.getMaxVal().indexOf("[") != -1) {// 闭区间
//					if (date1.compareTo(date2) == -1) {// 比結束时间大
//						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + data.get("SYS_ORDER_NO") + "]行校验失败！[" + o + "]不满足" + SysFunction.replaceDate(ruleInfo.getErrorTip(), taskCase.getDataDate(), taskCase.getEndTime()), start));
//						continue;
//					}
//				} else {// 开区间
//					if (date1.compareTo(date2) < 1) {// 大于等于结束日期
//						resultList.add(resultList.size(), RptInputLstValidateLog.createLog( ruleInfo, "第[" + data.get("SYS_ORDER_NO") + "]行校验失败！[" + o + "]不满足" + SysFunction.replaceDate(ruleInfo.getErrorTip(), taskCase.getDataDate(), taskCase.getEndTime()), start));
//						continue;
//					}
//				}
			}
		}
	}

	/**
	 * 校验补录日期范围
	 * 
	 * @param taskCase	任务实例
	 * @param ruleInfo	规则信息
	 * @param dataList	待校验数据
	 * @param resultList	校验结果
	 * @param start	
	 * @throws Exception
	 */
	private void checkDateRand(RptTskIns taskCase, RptInputListDataRuleInfo ruleInfo, List<Map<String, Object>> dataList, List<RptInputLstValidateLog> resultList,
			int start) throws Exception {
		for (int k = 0; k < dataList.size(); k++) {
			Map<String, Object> data = dataList.get(k);
			Object o = data.get(ruleInfo.getFieldNm());
			if (o == null) {
				resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + data.get("SYS_ORDER_NO") + "]行,字段[" + ruleInfo.getFieldNm() + "]校验失败,数据为空！", start));
				continue;
			}
		}
	}

	@Override
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList, Object... obj) {
		return null;
	}

 }
