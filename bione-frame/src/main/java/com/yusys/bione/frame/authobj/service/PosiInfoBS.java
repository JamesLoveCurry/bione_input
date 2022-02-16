package com.yusys.bione.frame.authobj.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.entity.BionePosiInfo;
import com.yusys.bione.frame.authobj.web.RptException;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;

@Service("posiInfoBS")
@Transactional(readOnly = true)
public class PosiInfoBS extends BaseBS<BionePosiInfo> {

	/**
	 * 通过机构编号查找岗位信息
	 * 
	 * @param orgNo
	 * @param logicSysNo
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BionePosiInfo> findPosiInfoByOrg(String orgNo,
			String logicSysNo, int firstResult, int pageSize, String orderBy,
			String orderType, Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder("");

		jql.append("SELECT posi FROM BionePosiInfo posi WHERE posi.logicSysNo=:logicSysNo");
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		// values.put("orgNo", orgNo);
		values.put("logicSysNo", logicSysNo);

		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		} else {
			if (orgNo != null && !"".equals(orgNo) && !"0".equals(orgNo)) {
				jql.append(" and posi.orgNo=:orgNo");
				values.put("orgNo", orgNo);
			}
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by posi." + orderBy + " " + orderType);
		}

		SearchResult<BionePosiInfo> posiList = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql.toString(),
						values);

		return posiList;
	}

	/**
	 * 删除
	 * --2013-12-05 by yunlei 一期项目 将mstr同步相关代码删除
	 * @param ids
	 */
	@Transactional(readOnly = false)
	public void removeEntityBatch(String ids) throws RptException {
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		String[] idArray = StringUtils.split(ids, ',');
		for (String id : idArray) {
			this.removeEntityById(id);
		}
	}

	/**
	 * 增加或修改
	 * 
	 * --2013-12-05 by yunlei 一期项目 将mstr同步相关代码删除
	 * 
	 * @param model
	 */
	@Transactional(readOnly = false)
	public void create(BionePosiInfo model) throws RptException {

		model.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		model.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
		model.setLastUpdateTime(new Timestamp(new Date().getTime()));
		if (model.getPosiId() != null && !"".equals(model.getPosiId())) {// 修改
			this.updateEntity(model);
		} else {// 新增
			model.setPosiId(RandomUtils.uuid2());
			this.saveEntity(model);
		}
	}

	/**
	 * 根据机构编号和岗位编号查找岗位信息
	 * 
	 * @param posiNo
	 * @return
	 */
	public BionePosiInfo findPosiInfoByOrgNoandPosiNo(String posiNo) {
		String sysNo = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();

		String jql = "SELECT posi FROM BionePosiInfo posi where posi.posiNo=?0 and posi.logicSysNo=?1";
		List<BionePosiInfo> posi = this.baseDAO.findWithIndexParam(jql, posiNo,
				sysNo);
		if (posi != null && posi.size() > 0) {
			return posi.get(0);
		} else {
			return null;
		}
	}

	/**
	 * author --huangye 通过机构查找机构下各岗位信息
	 * 
	 * @param orgNo
	 * @return
	 */
	public List<BionePosiInfo> findPosiInfoByOrgInfo(String orgNo) {
		String jql = "SELECT posi FROM BionePosiInfo posi WHERE posi.orgNo=?0 AND posi.logicSysNo=?1 AND posi.posiSts=?2";
		List<BionePosiInfo> posiInfoList = this.baseDAO.findWithIndexParam(jql,
				orgNo, BioneSecurityUtils.getCurrentUserInfo()
						.getCurrentLogicSysNo(),
				GlobalConstants4frame.COMMON_STATUS_VALID);
		return posiInfoList;
	}
	
	/**
	 * --author --yunlei 根据ID 获取岗位信息
	 * 
	 * @param posiList
	 * @return
	 */
	public List<BionePosiInfo> findPosiByPosiIds(List<String> posiList) {
		String jql = "SELECT posi FROM BionePosiInfo posi WHERE posi.posiId in (?0)";
		List<BionePosiInfo> posiInfoList = this.baseDAO.findWithIndexParam(jql,
				posiList);
		return posiInfoList;
	}

	/**
	 * 判断机构下是否包含岗位信息 ,不判断逻辑系统 --author huangye 20130703 08:34
	 * 
	 * @param orgId
	 *            机构ID
	 * @return 机构下有岗位返回true,否则false
	 */
	public boolean checkIsPosiInOrg(String orgId) {
		boolean flag = false;
		BioneOrgInfo orgInfo = this.getEntityById(BioneOrgInfo.class, orgId);
		String jql = "SELECT posi FROM BionePosiInfo posi WHERE posi.orgNo=?0";
		List<BionePosiInfo> posiList = this.baseDAO.findWithIndexParam(jql,
				orgInfo.getOrgNo());
		if (posiList != null && posiList.size() > 0) {
			flag = true;
		}
		return flag;
	}

}
