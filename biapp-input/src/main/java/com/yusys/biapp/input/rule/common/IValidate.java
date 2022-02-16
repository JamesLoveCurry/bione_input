/**
 * 
 */
package com.yusys.biapp.input.rule.common;

import java.util.List;
import java.util.Map;

import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;

/**
 * 数据校验接口<br>
 * 入库后的校验，主要是逻辑校验，包括校表内，表外等校验规则<br>
 * @author tobe
 */
public interface IValidate {

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
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList, List<Map<String,Object>> dataList ,Object... obj);
	
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
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList,Object... obj);

}
