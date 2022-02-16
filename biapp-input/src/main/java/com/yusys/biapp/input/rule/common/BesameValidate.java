/**
 * 
 */
package com.yusys.biapp.input.rule.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.singularsys.jep.Jep;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.biapp.input.rule.service.RuleItemsBS;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.bione.comp.repository.jdbc.entity.BioneColumnMetaData;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.frame.mtool.service.DataSourceBS;

/**
 * 表间一致性校验<br>
 * 1.内表的列值必须在外表中<br>
 * 2.内表各列汇总值与外表汇总值进行比较
 * @author tobe
 */
@Component(value = "besameValidate")
public class BesameValidate implements IValidate {

	@Autowired
	private RuleItemsBS ruleItemsBS;
	@Autowired
	private DataUtils dataUtils;
	@Autowired
	private DataSourceBS dataSourceBS;
	
	public static String keySplit = "####";
	
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
			List<Map<String, Object>> dataList,Object... obj) {

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
	public List<RptInputLstValidateLog> validate(RptTskIns taskCase, RptInputLstTempleInfo temp, List<RptInputListDataRuleInfo> ruleInfoList,
			Object... obj) {
		List<RptInputLstValidateLog> resultList = Lists.newArrayList();
		RptInputListDataRuleInfo udipDataRulesInfo = null;
		try {
			List<String> keyCol = Lists.newArrayList();
			List<RptInputLstTempleConst>  udipTemplePrimaryList = ruleItemsBS.findByPropertys(RptInputLstTempleConst.class, new String[]{"templeId","keyType"}, new Object[]{temp.getTempleId(),UdipConstants.TAB_PRIMARY});
			
			if (!udipTemplePrimaryList.isEmpty()) {
				RptInputLstTempleConst utp = udipTemplePrimaryList.get(0);
				keyCol = ArrayUtils.asList(utp.getKeyColumn(), ";");
			}
			
			for (RptInputListDataRuleInfo ruleInfo : ruleInfoList) {
				udipDataRulesInfo = ruleInfo ;
				/** 1.取出检验规则的子项 **/
				List<RptInputListRuleItemInfo> items = ruleItemsBS.findEntityListByProperty("ruleId", ruleInfo.getRuleId());
				if (items == null || items.isEmpty()) {
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验规则【" + ruleInfo.getRuleNm() + "】有误，无子项！",0));
					continue;
				}
				StringBuilder left = new StringBuilder();
				StringBuilder right = new StringBuilder();
				Object rightObj = null;
				Object leftObj = null;
				
				/** 2.取子项结果 **/
				for (RptInputListRuleItemInfo item : items) {
					Object o = null;
					if (item.getLeftOrRight().equals("left")) {
						o = dataUtils.getDataByRuleItem(taskCase, ruleInfo, item, null, keyCol, obj);
						if (o instanceof Map) {
							leftObj = o;
						} else {
							left.append(item.getCalCode()).append(o);
						}
					} else {
						Map<String,String> map = Maps.newHashMap();;
						if (isInputTable(item)) {//如果外表是补录表,那么只能拿补录通过的数据进行校验
							map.put(UdipConstants.TAB_DATA_STATE, UdipConstants.TASK_STATE_SUCESS);
						}
						o = dataUtils.getDataByRuleItem(taskCase, ruleInfo, item, map, null);
						if (RuleUtils.LOGIC_CODE_IN.equals(ruleInfo.getLogicSysNo())) {// 如果是in的情况
							rightObj = o;
						} else {
							right.append(item.getCalCode()).append(o.toString().replaceAll("\\[|\\]", ""));// 非in的情况，就是汇总
						}
					}
				}
				if (left.length() == 1) {// 无数据，校验通过
					//resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[左边无数值]",0));
				} else if ( (rightObj != null && StringUtils.isBlank(rightObj.toString())) || ( right.toString().length()==1 ) || (rightObj != null && rightObj.toString() == "[]") ){
					resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "校验失败！[右边无数值]",0));
				} else {
					/** 3.组成表达，执行表达式 **/
					StandardConfigurableParser cp = new StandardConfigurableParser();
					Jep jep = new Jep();
					jep.setComponent(cp);
					if (RuleUtils.LOGIC_CODE_IN.equals(ruleInfo.getLogicSysNo())) {// 如果是in的情况
						Object leftResult = null;
						if (leftObj == null) {//去前头横杠（优化后不执行）
							jep.parse(left.toString());
							leftResult = jep.evaluate();
							String s = left.toString().replaceAll("\\+|\\-|\\*|\\/|\\(|\\)", "");
							if (NumberUtils.isNumber(s)) {
								String leftInfo = left.toString().replaceAll("\\+|\\*|\\/|\\(|\\)", "");
								if (leftInfo.startsWith("-")) {
									leftResult = leftInfo.replace("-", "");
								} else {
									leftResult = leftInfo;
								}
							}
						}
						if (rightObj instanceof List) {// 右边是个结果集
							@SuppressWarnings("unchecked")
							List<String> rightSet = (List<String>) rightObj;
							@SuppressWarnings("unchecked")
							Map<String, String> map = (Map<String, String>) leftObj;
							Set<String> leftSet = map.keySet();
							if (leftSet != null && !leftSet.isEmpty()) {
								for (String value : leftSet) {
									String array []= value.split(keySplit);
									if (!rightSet.contains(array[0]))
										resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验失败！[" + ruleInfo.getErrorTip() + "],运算符[" + ruleInfo.getLogicSysNo() + "],左边：" + array[0] + "所在行主键["+map.get(value)+"],右边：" + ArrayUtils.toString(rightSet),0));
								}
							} else {//左边无数据
								if (leftResult!=null&&!rightSet.contains(leftResult)) {
									resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验失败！[" + ruleInfo.getErrorTip() + "],运算符[" + ruleInfo.getLogicSysNo() + "],左边：" + leftResult + ",右边：" + ArrayUtils.toString(rightSet),0));
								}
							}
						} else {// 右边是一个值
							if (!leftResult.equals(rightObj)) {
								resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验失败！[" + ruleInfo.getErrorTip() + "],运算符[" + ruleInfo.getLogicSysNo() + "],左边：" + leftResult + ",右边：" + rightObj,0));
							}
						}
					} else {
						jep.parse(left.toString() + ruleInfo.getLogic() + right);
						if (jep.evaluate().equals(false)) {
							resultList.add(resultList.size(), RptInputLstValidateLog.createLog(ruleInfo, "检验失败！[" + ruleInfo.getErrorTip() + "],运算符[" + ruleInfo.getLogic() + "],左边：" + left + ",右边：" + right,0));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultList.add(resultList.size(), RptInputLstValidateLog.createLog(udipDataRulesInfo, "校验出错！" + e.getMessage(),0));
		}
		/** 4.返回校验结果 ,如果校验成功，map的result为空，如果失败，result中返回错误信息 **/
		return resultList;
	}

	/**
	 * 判断一个补录规则里面的表是否是补录表
	 * @param ruleInfo
	 * @return
	 * @throws Exception 
	 */
	public boolean isInputTable(RptInputListRuleItemInfo item) throws Exception {
		List<BioneColumnMetaData> cols = this.dataSourceBS.getColumnMetaData(item.getDsId(), item.getTableEnName());
		List<String> colName = Lists.newArrayList();
		for (BioneColumnMetaData col : cols) {
			colName.add(col.getTableName().toUpperCase());
		}
		if (colName.contains(UdipConstants.TAB_DATA_STATE) && colName.contains(UdipConstants.TAB_DATA_CASE)) {//包含补录表的标识字段
			return true ;
		}
		return false;
	}
}
