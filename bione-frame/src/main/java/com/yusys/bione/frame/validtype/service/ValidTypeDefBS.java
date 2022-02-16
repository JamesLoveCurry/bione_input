package com.yusys.bione.frame.validtype.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.validtype.entity.BioneValidTypeDef;

@Service
@Transactional(readOnly = true)
public class ValidTypeDefBS extends BaseBS<BioneValidTypeDef>{
	
	@SuppressWarnings("unchecked")
	public SearchResult<BioneAuthResDef> getValidTypeDefList(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder("");
		jql.append("select def from BioneValidTypeDef def where 1=1");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by def." + orderBy + " " + orderType);
		}
		Map<String, ?> values = (Map<String, ?>) conditionMap.get("params");
		SearchResult<BioneAuthResDef> authResDefList = this.baseDAO.findPageWithNameParam(firstResult, pageSize,
				jql.toString(), values);
		return authResDefList;
	}
	
	public void delValidTypeDef(String ids) {
		String jql = "delete from BioneValidTypeDef def where def.objDefNo in (?0)";
		List<String> idlist = Arrays.asList(StringUtils.split(ids, ","));
		this.baseDAO.batchExecuteWithIndexParam(jql, idlist);
	}
}
