package com.yusys.biapp.input.template.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.common.InputConstants;
import com.yusys.biapp.input.template.entity.RptInputLstTempleFile;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
@Service
@Transactional
public class TempleFileBS extends BaseBS<RptInputLstTempleFile> {

	public SearchResult<RptInputLstTempleFile> getTempleFileList(int firstResult,
			int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap, String id) {

		StringBuilder jql = new StringBuilder("");
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		if (StringUtils.isNotBlank(id)) {
			jql.append("select temple from RptInputLstTempleFile temple where temple.templeId=:templeId");
			values.put("templeId", id);
		} else {
			jql.append("select temple from RptInputLstTempleFile temple where 1=1");
		}

		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by Temple." + orderBy + " " + orderType);
		}
//		SearchResult<RptInputLstTempleFile> authObjDefList = this.baseDAO
//				.findPageWithNameParam(firstResult, -1, jql.toString(), values);
		List<RptInputLstTempleFile> fileList = this.baseDAO.findWithNameParm(jql.toString(), values);
		SearchResult<RptInputLstTempleFile> authObjDefList = new SearchResult<RptInputLstTempleFile>();
		if(fileList == null){
			fileList = new ArrayList<RptInputLstTempleFile>();
		}
		authObjDefList.setResult(fileList);
		authObjDefList.setTotalCount(fileList.size());
		return authObjDefList;
	}
	
	public void setFileDisable(String templeId) {
		String jql = "update RptInputLstTempleFile t set t.sts = ?0 where t.templeId = ?1";
		this.baseDAO.batchExecuteWithIndexParam(jql, InputConstants.TASK_STATE_STOP, templeId);
	}
}
