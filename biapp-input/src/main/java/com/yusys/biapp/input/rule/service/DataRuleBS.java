package com.yusys.biapp.input.rule.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.repository.DataRuleDao;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;

@Service
@Transactional
public class DataRuleBS extends BaseBS<RptInputListDataRuleInfo> {

	@Autowired
	private DataRuleDao dataRuleDao;
	
	@SuppressWarnings("unchecked")
	public SearchResult<RptInputListDataRuleInfo> getRuleForTemplelist(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap , String templeId , String catalogId) {
		StringBuilder jql = new StringBuilder("");
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		if (StringUtils.isNotBlank(templeId) && !"0".equals(templeId)) {
			jql.append("select temple from RptInputListDataRuleInfo temple where temple.ruleId in(select t.ruleId from RptInputLstTempleRule t where t.templeId=:templeId)");
			values.put("templeId", templeId);
		} else {
//			TempResImpl	tempResImpl = SpringContextHolder.getBean(TempResImpl.name);
//			List<String> idList = tempResImpl.getTempId();
//			jql.append("select temple from RptInputListDataRuleInfo temple where temple.ruleId in(select t.ruleId from RptInputLstTempleRule t where t.templeId in (:tid))");
			jql.append("select temple from RptInputListDataRuleInfo temple where temple.ruleId in(select t.ruleId from RptInputLstTempleRule t )");
//			values.put("tid", idList);
		}
	/*	if(StringUtils.isNotEmpty(catalogId)){
			jql.append(" and temple. ")
		}
		*/
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by temple." + orderBy + " " + orderType);
		}
		SearchResult<RptInputListDataRuleInfo> authObjDefList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
		return authObjDefList;
	}
	
	public List<String> findOrgChildrenInf(String orgNo) {
		Map<String, Object> values = new HashMap<String, Object>();
		String jql = "select org.orgNo from BioneOrgInfo org where org.namespace like :namespace";
		values.put("namespace", "%/" + orgNo + "/%");
		return this.baseDAO.findWithNameParm(jql, values);
	}
	
	/**
	 * 根据规则ID删除规则信息
	 * @param ruleIds
	 */
	@Transactional(readOnly = false)
	public void removeEntityByRuleId(String... ruleIds) {
		List<String> ruleIdList = Arrays.asList(ruleIds);
		this.baseDAO.batchExecuteWithIndexParam("delete from RptInputListDataRuleInfo where ruleId in (?0)", ruleIdList);
	}

	/**
	 * 规则信息
	 * @param idList
	 * @return
	 */
	public List<RptInputListDataRuleInfo> findByIdList(List<String> idList) {
		return this.baseDAO.findWithIndexParam("select rul from RptInputListDataRuleInfo rul where rul.ruleId in (?0)", idList);
	}

	/**
	 * 规则信息
	 * @param idList
	 * @return
	 */
	public boolean canAddRule(String ruleId,String name,String tempId) {
		
		if ("".equals(ruleId)) {
			String[] param1 = new String[]{name,tempId};
			List<Map<String, Object>> rs = this.jdbcBaseDAO.find(" SELECT count(1) as total  FROM RPT_INPUT_LIST_DATA_RULE_INFO inf,RPT_INPUT_LST_TEMPLE_RULE rule where inf.rule_Id=rule.rule_Id and rule_Nm=? and rule.temple_Id=?", param1);
			return Integer.parseInt(rs.get(0).get("TOTAL").toString())==0;
		} else {
			String[] param2 = new String[]{ruleId,name,tempId};
			List<Map<String, Object>> rs = this.jdbcBaseDAO.find(" SELECT count(1) as total  FROM RPT_INPUT_LIST_DATA_RULE_INFO inf,RPT_INPUT_LST_TEMPLE_RULE rule where inf.rule_Id=rule.rule_Id and inf.rule_Id !=? and rule_Nm=? and rule.temple_Id=?", param2);
			return Integer.parseInt(rs.get(0).get("TOTAL").toString())==0;
		}
	}
	/**
	 * 规则信息
	 * @param ruleId
	 * @return
	 */
	
	public RptInputListDataRuleInfo getRptInputListDataRuleInfoByRuleId(String ruleId){
		if(StringUtils.isEmpty(ruleId))
			return null;
		return dataRuleDao.getRptInputListDataRuleInfoByRuleId(ruleId);
	}

}
