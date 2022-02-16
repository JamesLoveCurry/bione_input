/**
 * 
 */
package com.yusys.biapp.input.rule.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.singularsys.jep.Jep;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.biapp.input.rule.service.RuleItemsBS;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;

/**
 * 表内纵向校验<br>
 * 1.列的汇总之后四则运算结果比较<br>
 * 2.表内列的汇总与某值比较
 * 
 * @author tobe
 */
@Component(value = "verticalValidate")
public class VerticalValidate implements IValidate {

	@Autowired
	private RuleItemsBS ruleItemsBS;
	@Autowired
	private DataUtils dataUtils;

	/**
	 * 校验方法,校验提供的数据
	 * 
	 * @param taskCase
	 *            任务实例
	 * @param temp
	 *            模板实体
	 * @param ruleInfoList
	 *            补录规则
	 * @param dataList
	 *            需要校验的数据
	 * @param obj
	 *            可变参数<br>
	 *            第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 * @return 返回校验结果
	 */
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList,
			List<Map<String, Object>> dataList, Object... obj) {

		return null;
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
			for (RptInputListDataRuleInfo ruleInfo : ruleInfoList) {
				RptInputListDataRuleInfo = ruleInfo;
				/** 1.取出检验规则的子项 **/
				List<RptInputListRuleItemInfo> items = ruleItemsBS.findEntityListByProperty("ruleId", ruleInfo.getRuleId());

				if (items == null || items.isEmpty()) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验规则[" + ruleInfo.getRuleNm() + "]有误，无子项！", 0));
				} else {
					StringBuilder left = new StringBuilder();
					StringBuilder right = new StringBuilder();
					/** 2.取子项结果 **/
					for (RptInputListRuleItemInfo item : items) {
						Object o = dataUtils.getDataByRuleItem(taskCase, ruleInfo, item, null, null, obj);
						if (item.getLeftOrRight().equals("left")) {
							left.append(item.getCalCode()).append(o);
						} else {
							right.append(item.getCalCode()).append(o);
						}
					}
					if (left.length() == 1) {// 无数据，校验通过
						// resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[左边无数值]",0));
					} else if (right.length() == 1) {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[右边无数值]", 0));
					} else {
						/** 3.组成表达，执行表达式 **/
						Jep jep = new Jep();
						jep.setComponent(new StandardConfigurableParser());
						jep.parse(left.toString() + ruleInfo.getLogic() + right.toString());
						if (StringUtils.isNotBlank(left.toString()) && StringUtils.isNotBlank(right.toString()) && jep.evaluate().equals(false)) {
							resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[" + ruleInfo.getErrorTip() + "]", 0));
						}
					}
				}
			}
		} catch (Exception e) {
			resultList.add(resultList.size(), RptInputLstValidateLog.createLog(RptInputListDataRuleInfo, "校验出错！" + e.getMessage(), 0));
		}
		/** 4.返回校验结果 ,如果校验成功，map的result为空，如果失败，result中返回错误信息 **/
		return resultList;
	}

}
