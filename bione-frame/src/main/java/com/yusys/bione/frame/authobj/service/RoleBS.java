package com.yusys.bione.frame.authobj.service;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfoExt;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Title:角色管理的业务逻辑类
 * Description: 提供角色管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author huangye huangye@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service("roleBS")
@Transactional(readOnly = true)
public class RoleBS extends BaseBS<BioneRoleInfo> {

	protected static Logger log = LoggerFactory.getLogger(RoleBS.class);

	/**
	 * 分页查询角色信息
	 * 
	 * @param firstResult
	 *            第一条记录
	 * @param pageSize
	 *            每页记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            参数列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneRoleInfo> getRoleList(int firstResult,
			int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		SearchResult<BioneRoleInfo> result = new SearchResult<BioneRoleInfo>();
		
		SearchResult<BioneRoleInfo> roleList = new SearchResult<BioneRoleInfo>();
		String jql = "select role, user.userName from BioneRoleInfo role, BioneUserInfo user "
				+ "WHERE role.lastUpdateUser=user.userId and role.logicSysNo=:logicSysNo ";
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() 
				&& "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){//普通管理员
			jql += " and role.lastUpdateUser='" + BioneSecurityUtils.getCurrentUserId() + "' ";
		}else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() 
				&& !"Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){//普通用户
			return roleList;
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by role." + orderBy + " " + orderType;
		}

		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());

		roleList = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql, values);
		
		List<BioneRoleInfo> list = new ArrayList<BioneRoleInfo>();
		if (roleList != null && roleList.getResult() != null) {
			for(Object object : roleList.getResult()) {
				Object[] obj = (Object[]) object;
				BioneRoleInfo vo = (BioneRoleInfo) obj[0];
				vo.setUserName((String) obj[1]);
				
				list.add(vo);
			}
		}
		result.setResult(list);
		result.setTotalCount(roleList.getTotalCount());
		return result;
	}

	public boolean checkIsRoleNoExist(String roleNo) {
		boolean flag = true;
		String jql = "select role FROM BioneRoleInfo role where roleNo=?0 AND logicSysNo=?1 AND lastUpdateUser=?2";
		List<BioneRoleInfo> list = this.baseDAO.findWithIndexParam(jql, roleNo,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
				BioneSecurityUtils.getCurrentUserInfo().getUserId());
		if (list != null && list.size() > 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 判断角色是否存在
	 * @param roleNo
	 * @return
	 */
	public boolean checkIsRoleNoExist(String roleNo, String lastUpdateUser) {
		boolean flag = true;
		String jql = "select role FROM BioneRoleInfo role where roleNo=?0 AND logicSysNo=?1 AND lastUpdateUser=?2";
		List<BioneRoleInfo> list = this.baseDAO.findWithIndexParam(jql, roleNo,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
				lastUpdateUser);
		if (list != null && list.size() > 0) {
			flag = false;
		}
		
		return flag;
	}

	/**
	 * 删除指定id的角色
	 * 
	 * @param ids
	 * 
	 * @return
	 */
	@Transactional(readOnly = false)
	public void deleteRolesByIds(String[] ids) {
		if (ids != null) {
			List<String> idList = new ArrayList<String>();
			for (int i = 0; i < ids.length; i++) {
				idList.add(ids[i]);
			}
			if (idList.size() > 0) {
				String jql = "delete from BioneRoleInfo role where role.roleId in (?0)";
				this.baseDAO.batchExecuteWithIndexParam(jql, idList);
			}
		}
	}
	
	/**
	 * 更改角色状态
	 * 
	 * @param rptId
	 * @param sts
	 */
	@Transactional(readOnly = false)
	public void changeRoleSts(String roleId, String sts) {
		if (StringUtils.isNotEmpty(roleId) && StringUtils.isNotEmpty(sts)) {
			String jql = "update BioneRoleInfo user set roleSts = ?0 where roleId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, sts, roleId);
		}
	}
	
	/**
	 * 平台角色
	 * @return
	 */
	public List<BioneRoleInfoExt> getBioneRoleInfoExt() {
		String hql = "select bione from BioneRoleInfoExt bione where 1=1";
		List<BioneRoleInfoExt> list = this.baseDAO.findWithIndexParam(hql);
		return list;
	}

	/**
	 * 
	 * 根据逻辑系统标识，角色编号查询角色列表
	 * @param orgNo
	 * @return
	 */
	public List<BioneRoleInfo> getRoleByRoleNo(String roleNo){
		List<BioneRoleInfo> list = new ArrayList<BioneRoleInfo>();
		if (roleNo != null && !"".equals(roleNo)) {
			String jql = "select role from BioneRoleInfo role where role.roleNo = ?0 and role.logicSysNo = ?1 ";
			list = this.baseDAO.findWithIndexParam(jql,
					roleNo, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		}
		return list;
	}
}
