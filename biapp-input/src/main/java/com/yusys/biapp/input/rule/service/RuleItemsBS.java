/**
 * 
 */
package com.yusys.biapp.input.rule.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;


/**
 * @author dada
 *
 */
@Service("ruleItemsBS")
@Transactional(readOnly = true)
public class RuleItemsBS extends BaseBS<RptInputListRuleItemInfo>{

	public SearchResult<RptInputListRuleItemInfo> getRuleItemsList(int firstResult,
			int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap, String id) {
		StringBuilder jql = new StringBuilder("");
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		if (StringUtils.isNotBlank(id)) {
			jql.append("select temple from UdipRuleItemsInfo temple where temple.ruleId=:ruleId");
			values.put("ruleId", id);
		} 
		else {
			jql.append("select temple from UdipRuleItemsInfo temple where 1=1");
		}

		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by Temple." + orderBy + " " + orderType);
		}

		SearchResult<RptInputListRuleItemInfo> authObjDefList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
		return authObjDefList;
	}

}
