package com.yusys.bione.frame.authobj.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneAuthObjgrpInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;

@Service("authObjgrpBS")
@Transactional(readOnly = true)
public class AuthObjgrpBS extends BaseBS<BioneAuthObjgrpInfo> {

	/**
	 * 分页查询对象组信息列表
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneAuthObjgrpInfo> getObjgrpInfoList(int firstResult, int pageSize, String orderBy,
			String orderType, Map<String, Object> conditionMap) {
		String jql = "SELECT objgrp FROM BioneAuthObjgrpInfo objgrp WHERE objgrp.logicSysNo=:logicSysNo ";

		if (!conditionMap.get("jql").equals("")) {
			jql += "AND " + conditionMap.get("jql") + " ";
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql += "ORDER BY objgrp." + orderBy + " " + orderType;
		}

		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());

		SearchResult<BioneAuthObjgrpInfo> objgrpInfoList = this.baseDAO.findPageWithNameParam(firstResult, pageSize,
				jql, values);
		return objgrpInfoList;
	}

	public boolean checkIsObjgrpNoExist(String objgrpNo) {
		boolean flag = true;
		String jql = "select role FROM BioneAuthObjgrpInfo role where objgrpNo=?0 AND logicSysNo=?1";
		List<BioneAuthObjgrpInfo> list = this.baseDAO.findWithIndexParam(jql, objgrpNo, BioneSecurityUtils
				.getCurrentUserInfo().getCurrentLogicSysNo());
		if (list != null && list.size() > 0) {
			flag = false;
		}
		return flag;
	}

	@Transactional(readOnly = false)
	public void removeEntityBatch(String id) {
		if (id.endsWith(",")) {
			id = id.substring(0, id.length() - 1);
		}
		String[] ids = StringUtils.split(id, ',');
		for (String idd : ids) {
			removeEntityById(idd);
		}
	}
	
	/**
	 * 更改对象组状态
	 * 
	 * @param rptId
	 * @param sts
	 */
	@Transactional(readOnly = false)
	public void changeGrpSts(String objgrpId, String sts) {
		if (StringUtils.isNotEmpty(objgrpId) && StringUtils.isNotEmpty(sts)) {
			String jql = "update BioneAuthObjgrpInfo grp set objgrpSts = ?0 where objgrpId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, sts, objgrpId);
		}
	}
}
