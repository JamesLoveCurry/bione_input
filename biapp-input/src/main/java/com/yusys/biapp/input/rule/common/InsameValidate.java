/**
 * 
 */
package com.yusys.biapp.input.rule.common;

import java.util.List;
import java.util.Map;

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
import com.yusys.bione.comp.utils.ArrayUtils;

/**
 * 表内一致性校验<br>
 * 1.多对一，如一个账户只能对应一个用户<br>
 * @author tobe
 */
@Component(value = "insameValidate")
public class InsameValidate implements IValidate {

	@Autowired
	private RuleItemsBS ruleItemsBS;
	@Autowired
	private DataUtils dataUtils;

	/*
	 * (non-Javadoc)
	 * @see com.yuchengtech.datainput.rule.common.IValidate#validate(com.yuchengtech.datainput.temple.entity.RptInputLstTempleInfo, java.util.List,
	 * java.util.List, java.lang.Object[])
	 */
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList,
			List<Map<String, Object>> dataList, Object... obj) {

		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		RptInputListDataRuleInfo udipDataRulesInfo = null;
		try {
			for (RptInputListDataRuleInfo ruleInfo : ruleInfoList) {
				udipDataRulesInfo = ruleInfo;
				/** 1.取出检验规则的子项 **/
				List<RptInputListRuleItemInfo> items = ruleItemsBS.findEntityListByProperty("ruleId", ruleInfo.getRuleId());

				if (items == null || items.isEmpty()) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验规则【" + ruleInfo.getRuleNm() + "】有误，无子项！",0));
					continue;
				}

				StringBuilder left = new StringBuilder();
				StringBuilder right = new StringBuilder();
				Object rightObj = new Object();

				/** 2.取子项结果 **/
				for (RptInputListRuleItemInfo item : items) {
					Object o = dataUtils.getDataByRuleItem(taskCase,ruleInfo,item, null,null,obj);
					if (item.getLeftOrRight().equals("left")) {
						left.append(item.getCalCode()).append(o);
					} else {
						if (RuleUtils.LOGIC_CODE_IN.equals(ruleInfo.getLogicSysNo())) {// 如果是in的情况
							rightObj = o;
						} else {
							right.append(item.getCalCode()).append(o);// 非in的情况，就是汇总
						}
					}
				}

				/** 3.组成表达，执行表达式 **/
				StandardConfigurableParser cp = new StandardConfigurableParser();
				Jep jep = new Jep();
				jep.setComponent(cp);
				if (RuleUtils.LOGIC_CODE_IN.equals(ruleInfo.getLogicSysNo())) {// 如果是in的情况
					jep.parse(left.toString());
					Object leftResult = jep.evaluate();
					if (rightObj instanceof List) {// 右边是个结果集
						@SuppressWarnings("unchecked")
						List<String> set = (List<String>) rightObj;
						if (!set.contains(leftResult)) {
							resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验失败！[" + ruleInfo.getErrorTip() + "],运算符[" + ruleInfo.getLogicSysNo() + "],左边：" + leftResult + ",右边：" + ArrayUtils.toString(set),0));
						}
					} else {// 右边是一个值
						if (!leftResult.equals(rightObj)) {
							resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验失败！[" + ruleInfo.getErrorTip() + "],运算符[" + ruleInfo.getLogicSysNo() + "],左边：" + leftResult + ",右边：" + rightObj,0));
						}
					}
				} else {
					jep.parse(left.toString() + ruleInfo.getLogicSysNo() + right);
					if (jep.evaluate().equals(false)) {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验失败！[" + ruleInfo.getErrorTip() + "],运算符[" + ruleInfo.getLogicSysNo() + "],左边：" + left + ",右边：" + right,0));
					}
				}
			}

		} catch (Exception e) {
			resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipDataRulesInfo, "校验出错！" + e.getMessage(),0));
		}
		/** 4.返回校验结果 ,如果校验成功，map的result为空，如果失败，result中返回错误信息 **/
		return resultList;
	}

	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList,
			Object... obj) {
		return null;
	}

}
