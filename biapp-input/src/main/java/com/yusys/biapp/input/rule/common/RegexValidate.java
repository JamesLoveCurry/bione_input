/**
 * 
 */
package com.yusys.biapp.input.rule.common;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.singularsys.jep.Jep;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;

/**
 * 正则表达式校验
 * 
 * @author tobe
 */
@Component(value = "regexValidate")
public class RegexValidate implements IValidate {

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
	@SuppressWarnings("unchecked")
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList,
			List<Map<String, Object>> dataList, Object... obj) {
		Map<String, RptInputLstTempleField> colMap = (Map<String, RptInputLstTempleField>)obj[1];
		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		RptInputListDataRuleInfo RptInputListDataRuleInfo = null;
		try {
			if (dataList == null || dataList.size() == 0) {
				// resultList.add(resultList.size(), RptInputLstValidateLog.createLog(null, "没有需要校验的数据！",0));
				return resultList;
			}
			// int start = 1;
			// if (obj != null && obj.length > 0) {
			// 	start = Integer.valueOf(obj[0].toString());
			// }
			/** 1.组成表达式 **/
			Jep jep = new Jep();
			jep.setComponent(new StandardConfigurableParser());

			/** 2.数据校验 **/
			for (int k = 0; k < dataList.size(); k++) {
				Map<String, Object> data = dataList.get(k);
				for (RptInputListDataRuleInfo ruleInfo : ruleInfoList) {
					RptInputListDataRuleInfo = ruleInfo;
					Object o = data.get(ruleInfo.getFieldNm());
					RptInputLstTempleField field = colMap.get(ruleInfo.getFieldNm());
					int num = -1;
					Object noObj = data.get("SYS_ORDER_NO");
					if(noObj instanceof String){
						num=Integer.parseInt((String)noObj);
					}else if(noObj instanceof Long){
						num=((Long)noObj).intValue();
					}else if(noObj instanceof Integer){
						num=((Integer)noObj).intValue();
					}
					if ("1".equals(field.getAllowNull())&&( o == null || StringUtils.isBlank(o.toString()))) {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + num + "]行数据，在进行[" + ruleInfo.getRuleNm() + "]校验时失败, 数据为空！", num));
						continue;
					}

					if (o != null &&!Pattern.matches(ruleInfo.getRegex(), o.toString())) {
						resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "第[" + num + "]行校验失败. 字段["+ruleInfo.getFieldNm()+"]的值"+o+"不满足规则"+ruleInfo.getRuleNm() + " ! " + ruleInfo.getErrorTip(), num));
					}
				}
			}
		} catch (Exception e) {
			resultList.add(resultList.size(), RptInputLstValidateLog.createLog(RptInputListDataRuleInfo, "校验出错！" + e.getMessage(), 0));
		}
		return resultList;
	}

	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList, Object... obj) {
		return null;
	}

}
