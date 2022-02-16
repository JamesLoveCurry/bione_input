package com.yusys.biapp.input.rule.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.singularsys.jep.Jep;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;
import com.singularsys.jep.functions.strings.StringFunctionSet;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.biapp.input.rule.service.RuleItemsBS;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.utils.DateUtils;
import com.yusys.bione.comp.utils.ArrayUtils;

/**
 * 表内横向校验 <br>
 * 1.针对单条记录的校验，列之间四则运算结果的对比 <br>
 * 2.三元运算
 * 
 * @author tobe
 */
@Component(value = "alignValidate")
public class AlignValidate implements IValidate {

	@Autowired
	private RuleItemsBS ruleItemsBS;
	@Autowired
	private DataUtils dataUtils;

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
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList,
			List<Map<String, Object>> dataList, Object... obj) {
		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		RptInputListDataRuleInfo RptInputListDataRuleInfo = null;
		try {

			if (dataList == null || dataList.size() == 0) {
				// resultList.add(resultList.size(), RptInputLstValidateLog.createLog(null, "没有需要校验的数据！",0));
				return resultList;
			}

			List<String> keyCol = Lists.newArrayList("DATAINPUT_ID");
			/*List<RptInputLstTempleConst> RptInputLstTempleConstList = this.ruleItemsBS.findByPropertys(RptInputLstTempleConst.class,
					new String[] { "templeId", "keyType" }, new Object[] { temp.getTempleId(), UdipConstants.TAB_PRIMARY });
			if (!RptInputLstTempleConstList.isEmpty()) {
				RptInputLstTempleConst utp = RptInputLstTempleConstList.get(0);
				keyCol = ArrayUtils.asList(utp.getKeyColumn(), ";");
			}*/

			for (RptInputListDataRuleInfo ruleInfo : ruleInfoList) {
				RptInputListDataRuleInfo = ruleInfo;
				/** 1.取出检验规则的子项 **/
				List<RptInputListRuleItemInfo> items = this.ruleItemsBS.findEntityListByProperty("ruleId", ruleInfo.getRuleId());

				/** 2.取子项结果 **/
				StringBuilder left = new StringBuilder();
				StringBuilder right = new StringBuilder();

				for (RptInputListRuleItemInfo item : items) {
					String express = item.getAggregateFunc();
					if (item.getLeftOrRight().equals("left")) {
						left.append(item.getCalCode()).append(express);
					} else {
						right.append(item.getCalCode()).append(express);
					}
				}
				if (left.length() == 1) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[左边无数值]", 0));
				} else if (right.length() == 1) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[右边无数值]", 0));
				} else {
					Jep jep = new Jep();
					jep.setComponent(new StringFunctionSet());
					jep.setComponent(new StandardConfigurableParser());
					for (int k = 0; k < dataList.size(); k++) {
						Map<String, Object> data = dataList.get(k);
						/** 3.组成表达 **/
						for (String column : data.keySet()) {
							Object o = data.get(column);
							if (o != null && StringUtils.isNotBlank(o.toString())) {
								if (NumberUtils.isNumber(o.toString())) {
									String str = left.toString().toLowerCase();
									String str1 = right.toString().toLowerCase();
									if (!str.replaceAll("(substr|len|trim)+\\([\\w*(\\+|\\-)*]*" + column.toLowerCase(), "").equals(str)
											|| !str1.replaceAll("(substr|len|trim)+\\([\\w*(\\+|\\-)*]*" + column.toLowerCase(), "").equals(str1)) {
										jep.addVariable(column, o.toString());
									} else {
										jep.addVariable(column, Double.valueOf(o.toString()));
									}
								} else {
									if (DateUtils.isDate(o.toString())) {// 日期类型数据比较,把日期转成数据
										jep.addVariable(column, StringUtils.remove(o.toString(), '-'));
									} else {
										jep.addVariable(column, o.toString());
									}
								}
							} else {
								jep.addVariable(column, StringUtils.EMPTY);
							}
						}
						String keys = getKeyValue(keyCol, data);
						try {
							jep.parse(left.toString() + ruleInfo.getLogic() + right.toString());
							/** 4.数据校验 **/
							if (jep.evaluate().equals(false)) {
								resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "值为" + keys + "的记录校验失败，[" + ruleInfo.getErrorTip() + "]！", 0));
							}
						} catch (Exception e) {
							resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "值为" + keys + "的记录校验失败，[" + ruleInfo.getErrorTip() + "]！" + e.getMessage(), 0));
						}
					}
				}
			}
		} catch (Exception e) {
			resultList.add(resultList.size(), RptInputLstValidateLog.createLog(RptInputListDataRuleInfo, "校验出错！" + e.getMessage(), 0));
		}
		return resultList;
	}

	/**
	 * 校验方法,自取数据校验
	 * @param taskCase 任务实例
	 * @param temp 模板实体
	 * @param ruleInfoList 补录规则
	 * @param obj 可变参数<br>
	 *            第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 * @return 返回校验结果
	 */
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList, Object... obj) {
		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		RptInputListDataRuleInfo RptInputListDataRuleInfo = null;
		try {
			List<String> keyCol = Lists.newArrayList();
			List<RptInputLstTempleConst> templeConstList = ruleItemsBS.findByPropertys(RptInputLstTempleConst.class, new String[] { "templeId", "keyType" },
					new Object[] { temp.getTempleId(), UdipConstants.TAB_PRIMARY });
			if (!templeConstList.isEmpty()) {
				RptInputLstTempleConst utp = templeConstList.get(0);
				keyCol = ArrayUtils.asList(utp.getKeyColumn(), ";");
			}

			for (RptInputListDataRuleInfo ruleInfo : ruleInfoList) {
				RptInputListDataRuleInfo = ruleInfo;
				List<Map<String, Object>> dataList = this.dataUtils.getDataByRuleInfo(temp, ruleInfo, obj);
				if (dataList == null || dataList.size() == 0) {
					// resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "没有需要校验的数据！",0));
					continue;
				}
				/** 1.取出检验规则的子项 **/
				List<RptInputListRuleItemInfo> items = this.ruleItemsBS.findEntityListByProperty("ruleId", ruleInfo.getRuleId());

				/** 2.取子项结果 **/
				StringBuilder left = new StringBuilder();
				StringBuilder right = new StringBuilder();

				for (RptInputListRuleItemInfo item : items) {
					String express = item.getAggregateFunc();
					if (item.getLeftOrRight().equals("left")) {
						left.append(item.getCalCode()).append(express);
					} else {
						right.append(item.getCalCode()).append(express);
					}
				}

				if (left.length() == 1) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[左边无数值]", 0));
				} else if (right.length() == 1) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[右边无数值]", 0));
				} else {
					Jep jep = new Jep();
					jep.setComponent(new StringFunctionSet());
					jep.setComponent(new StandardConfigurableParser());

					for (int k = 0; k < dataList.size(); k++) {
						Map<String, Object> data = dataList.get(k);

						/** 3.组成表达 **/
						for (String column : data.keySet()) {
							Object o = data.get(column);
							if (o != null && StringUtils.isNotBlank(o.toString())) {
								if (NumberUtils.isNumber(o.toString())) {
									String str = left.toString().toLowerCase();
									if (!str.replaceAll("(substr|len|trim)+\\([\\w*(\\+|\\-)*]*" + column.toLowerCase(), "").equals(str)) {
										jep.addVariable(column, o.toString());
									} else {
										jep.addVariable(column, Double.valueOf(o.toString()));
									}
								} else {
									if (DateUtils.isDate(o.toString())) {// 日期类型数据比较,把日期转成数据
										jep.addVariable(column, StringUtils.remove(o.toString(), '-'));
									} else {
										jep.addVariable(column, o.toString());
									}
								}
							} else {
								jep.addVariable(column, StringUtils.EMPTY);
							}
						}
						String keys = getKeyValue(keyCol, data);
						try {
							jep.parse(left.toString() + ruleInfo.getLogic() + right.toString());
							/** 4.数据校验 **/
							if (jep.evaluate().equals(false)) {
								resultList.add(resultList.size(),
										RptInputLstValidateLog.createLog(ruleInfo, "值为[" + keys + "]的记录校验失败，[" + ruleInfo.getErrorTip() + "]！", 0));
							}
						} catch (Exception e) {
							resultList.add(resultList.size(),
									RptInputLstValidateLog.createLog(ruleInfo, "值为[" + keys + "]的记录校验失败，[" + ruleInfo.getErrorTip() + "]！", 0));
						}
					}
				}
			}
		} catch (Exception e) {
			resultList.add(resultList.size(), RptInputLstValidateLog.createLog(RptInputListDataRuleInfo, "校验出错！" + e.getMessage(), 0));
		}
		return resultList;
	}

	private String getKeyValue(List<String> keyCol, Map<String, Object> data) {
		/*
		String keys = "";
		for (String key : keyCol) {// 取主键值
			Object value = data.get(key);
			if (value == null)
				value = "";
			keys += value.toString() + DataFormatValidate.keysplit;
		}
		if (keys.length() > 0) {
			keys = keys.substring(0, keys.length() - 1);
		}
		return keys;
		*/
		return data.toString();
	}
}
