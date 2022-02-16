package com.yusys.biapp.input.rule.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.bione.comp.utils.SpringContextHolder;

/**
 * <pre>
 * Title: 校验工具类
 * Description: 校验工具类
 * </pre>
 * @author caijiufa
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 * 
 * </pre>
 */
public class ValidateUtils {

	/**
	 * 批量校验方法<br>
	 * 对传递过来的数据进行校验
	 * @param temp 模板对象
	 * @param ruleInfoMap 规则分类Map
	 * @param dataList 校验数据列表
	 * @param start 记录起始
	 * @return
	 */
	public static List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, Map<String, List<RptInputListDataRuleInfo>> ruleInfoMap,
			List<Map<String, Object>> dataList, int start,Map<String, RptInputLstTempleField> colMap) {
		List<RptInputLstValidateLog> result = Lists.newArrayList();
		
		if (dataList == null || dataList.size() == 0) {
//			result.add(result.size(), UdipValidateLog.createLog(null, "没有需要校验的数据！",0));
			return result;
		}
		for (String type : ruleInfoMap.keySet()) {
			IValidate validate = null;
			List<RptInputListDataRuleInfo> ruleList = ruleInfoMap.get(type);
			List<RptInputListDataRuleInfo> rules = ruleList;
			if (type.equals(UdipConstants.DATA_RULES_1)) {//值范围校验
				validate = SpringContextHolder.getBean("valueRanValidate");
			}
			if (type.equals(UdipConstants.DATA_RULES_2)) {//正则表达式校验 
				validate = SpringContextHolder.getBean("regexValidate");
			}
			if (type.equals(UdipConstants.DATA_RULES_3)) {//不带过滤条件的横向校验
				List<RptInputListDataRuleInfo> list  = Lists.newArrayList();
				validate = SpringContextHolder.getBean("alignValidate");
				for(RptInputListDataRuleInfo rule : ruleList){
					if(StringUtils.isBlank(rule.getFilterCondition())){
						list.add(rule);
					}
				}
				rules = list;
			}
			if (validate != null)
				result.addAll(result.size(), validate.validate(taskCase, temp, rules, dataList, start++,colMap));
		}
		return result;
	}

	/**
	 * 批量校验方法<br>
	 * 不需要提供数据，校验工具会自动去获取数据来校验
	 * @param taskCase 任务实例 
	 * @param temp 模板对象
	 * @param ruleInfoMap 规则分类Map
	 * @param obj 可变参数<br>
	 *            第一个是数据日期<br>
	 *            第二个是机构代码值,比如用户所属机构<br>
	 *            第三个是机构代码字段名，模板中配置的机构字段<br>
	 *            第四个任务批次，也就是实例ID<br>
	 * @return
	 */
	public static List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, Map<String, List<RptInputListDataRuleInfo>> ruleInfoMap,
			Object... obj) {

		List<RptInputLstValidateLog> result = Lists.newArrayList();
		for (String type : ruleInfoMap.keySet()) {
			IValidate validate = null;
			List<RptInputListDataRuleInfo> ruleList = ruleInfoMap.get(type);
			List<RptInputListDataRuleInfo> rules = ruleList;
			if (type.equals(UdipConstants.DATA_RULES_3)) {//带条件的横向校验
				List<RptInputListDataRuleInfo> list  = Lists.newArrayList();
				validate = SpringContextHolder.getBean("alignValidate");
				for(RptInputListDataRuleInfo rule : ruleList){
					if(StringUtils.isNotBlank(rule.getFilterCondition())){
						list.add(rule);
					}
				}
				rules = list;
			}
			if (type.equals(UdipConstants.DATA_RULES_4)) {//纵向校验
				validate = SpringContextHolder.getBean("verticalValidate");
			}
			if (type.equals(UdipConstants.DATA_RULES_5)) {//表间一致性校验
				validate = SpringContextHolder.getBean("besameValidate");
			}
			if (validate != null)
				result.addAll(result.size(), validate.validate(taskCase, temp, rules, obj));
		}
		return result;
	}
}
