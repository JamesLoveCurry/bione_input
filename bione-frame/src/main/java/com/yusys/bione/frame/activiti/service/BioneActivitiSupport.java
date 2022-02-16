package com.yusys.bione.frame.activiti.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.activiti.ActivitiDelegate;
import com.yusys.bione.frame.activiti.ActivitiDelegate.AlterIdentity;
import com.yusys.bione.frame.activiti.ActivitiSupport;
import com.yusys.bione.frame.authobj.util.BioneAuthObjAlterListener;
import com.yusys.bione.frame.authobj.util.BioneAuthObjNotifier;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;

@Service
@Transactional(readOnly = true)
public class BioneActivitiSupport extends BaseBS<Object>
		implements ActivitiSupport, BioneAuthObjAlterListener, InitializingBean {

	@Autowired
	private BioneAuthObjNotifier bioneAuthObjNotifier;

	@Autowired
	private IdentityService identityService;
	
	@Override
	public String getCurrentUserId() {
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		return user.getUserId();
	}

	@Override
	public boolean isCurrentUserSuperUser() {
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		return user.isSuperUser();
	}

	/**
	 * 获取指定机构和角色的用户ID列表
	 * 
	 * @param logicSysNo 逻辑系统编号
	 * @param orgNo 机构号
	 * @param roleId 角色ID
	 * @return 用户ID列表
	 */
	private List<String> getUserIDInRole(String roleId, Map<String, Object> parameter) {
		String logicSysNo = (String)parameter.get("logicSysNo");
		String orgNo = (String)parameter.get("orgNo");

		// 目前前台填写的是角色名称，不是角色ID
		StringBuilder sb = new StringBuilder();
		if (orgNo == null) {
			// 查找管理员
			sb.append("select user.userId from BioneUserInfo user,BioneAdminUserInfo admin,BioneLogicSysInfo sys");
			sb.append(" where sys.logicSysNo=?0 and admin.id.logicSysId=sys.logicSysId and admin.userSts=?1");
			sb.append(" and user.userId=admin.id.userId and user.logicSysNo=?2");
			return this.baseDAO.findWithIndexParam(sb.toString(),
					logicSysNo, GlobalConstants4frame.COMMON_STATUS_VALID, GlobalConstants4frame.SUPER_LOGIC_SYSTEM);
		} else {
			sb.append("select t.userId from BioneUserInfo t where t.orgNo=?0 and exists (");
			sb.append("select role.roleId from BioneAuthObjUserRel rel,BioneRoleInfo role");
			sb.append(" where rel.id.logicSysNo=?1 and rel.id.objDefNo=?2 and rel.id.userId=t.userId");
			sb.append(" and role.roleId=rel.id.objId and role.roleName=?3 and role.roleSts=?4 and role.logicSysNo=?5");
			sb.append(")");
			return this.baseDAO.findWithIndexParam(sb.toString(),
					orgNo, logicSysNo, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE,
					roleId, GlobalConstants4frame.COMMON_STATUS_VALID, logicSysNo);
		}
	}

	@Override
	public List<String> calCandidateUser(String roleId, Map<String, Object> parameter) {
		List<String> list = getUserIDInRole(roleId, parameter);
		return list;
	}

	private AlterIdentity getAlterIdentity(int opType) {
		switch (opType) {
		case BioneAuthObjAlterListener.OP_ADD:
			return AlterIdentity.OP_ADD;
		case BioneAuthObjAlterListener.OP_MODIFY:
			return AlterIdentity.OP_MODIFY;
		case BioneAuthObjAlterListener.OP_REMOVE:
			return AlterIdentity.OP_REMOVE;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void onAlterUser(int opType, String userId, String userNo, String userName, Map<String, String> parameter) {
		alterUser(getAlterIdentity(opType), userId, userNo, userName, parameter);
	}
	@Transactional(readOnly = false)
	public void alterUser(AlterIdentity opType, String userId, String userNo, String userName, Map<String, String> parameter) {
		switch (opType) {
			case OP_ADD:
			case OP_MODIFY:
				User user = identityService.createUserQuery().userId(userId).singleResult();
				if (user == null) {
					user = identityService.newUser(userId);
				}
				user.setFirstName(userNo);
				user.setLastName(userName);
				identityService.saveUser(user);
				break;
			case OP_REMOVE:
				identityService.deleteUser(userId);
				break;
			default:
				throw new UnsupportedOperationException();
		}
	}
	@Override
	public void onAlterOrg(int opType, String orgId, String orgNo, String orgName, Map<String, String> parameter) {
		alterOrg(getAlterIdentity(opType), orgId, orgNo, orgName, parameter);
	}
	@Transactional(readOnly = false)
	public void alterOrg(AlterIdentity opType, String orgId, String orgNo, String orgName, Map<String, String> parameter) {
	}
	@Override
	public void onAlterRole(int opType, String roleId, String roleName, Map<String, String> parameter) {
		alterRole(getAlterIdentity(opType), roleId, roleName, parameter);
	}
	@Transactional(readOnly = false)
	public void alterRole(AlterIdentity opType, String roleId, String roleName, Map<String, String> parameter) {
		switch (opType) {
			case OP_ADD:
			case OP_MODIFY:
				Group group = identityService.createGroupQuery().groupId(roleId).singleResult();
				if (group == null) {
					group = identityService.newGroup(roleId);
				}
				group.setName(roleName);
				identityService.saveGroup(group);
				break;
			case OP_REMOVE:
				identityService.deleteGroup(roleId);
				break;
			default:
				throw new UnsupportedOperationException();
		}
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		bioneAuthObjNotifier.register(this);
	}
}
