package com.yusys.biapp.input.input.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.task.entity.RptTskExeobjRel;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
@Service
@Transactional(readOnly=true)
public class RptTskExeobjRelBS extends BaseBS<RptTskExeobjRel> {
	/**
	 * 查找taskId关联的信息
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @param id
	 * @return
	 */
	public SearchResult<RptTskExeobjRel> getAllTempleIdList(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap,String id) {
		StringBuilder jql = new StringBuilder("");
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		if(id!=null || !"0".equals(id)){
			jql.append("select temple from UdipTaskTempleInfo temple where temple.taskId=:taskId");
			values.put("taskId", id);
		}
		SearchResult<RptTskExeobjRel> authObjDefList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
		return authObjDefList;
	}
}
