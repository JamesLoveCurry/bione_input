package com.yusys.bione.frame.security.realm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.service.DeptBS;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;
import com.yusys.bione.frame.security.lock.IUserLockValidator;
import com.yusys.bione.frame.security.lock.entity.BioneLockInfoBO;
import com.yusys.bione.frame.security.token.BioneAuthenticationToken;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;

/**
 * 
 * <pre>
 * Title: 本地超级系统认证实现类
 * Description: 当逻辑系统的认证方式选择本地认证-超级系统能够认证时，使用该实现类进行用户登录认证
 * </pre>
 * 
 * @author songxf
 * @version 1.00.00
 * 
 * <pre>
 * </pre>
 */
@Component
public class LocalSuperAuthorizingRealm extends AuthorizingRealm {
	@Autowired
	public UserBS userBS;
	@Autowired
	public AuthBS authBS;
	@Autowired
	public OrgBS orgBS;
	@Autowired
	public DeptBS deptBS;

	public LocalSuperAuthorizingRealm() {
		setAuthenticationTokenClass(BioneAuthenticationToken.class);
		// 设置密码加密算法
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
		matcher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
		matcher.setStoredCredentialsHexEncoded(false);// base64 encoded
		setCredentialsMatcher(matcher);
	}

	/**
	 * 认证回调函数,获取用户认证信息(用户名，密码)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) {
		BioneAuthenticationToken token = (BioneAuthenticationToken) authcToken;
		String logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
		String username = token.getUsername();
		char[] password = token.getPassword();
		String ticket = token.getTicket();
		boolean isSuperUser = token.getIsSuper();
		// 判断当前用户是否是系统管理员，若是，从超级逻辑系统获取用户信息		
		// 如果用户名密码不能为空,如果为空，则抛出异常
		if (StringUtils.isBlank(ticket)
				&& (StringUtils.isBlank(username) || password == null || password.length == 0)) {
			throw new AccountException("帐号和密码不能为空!");
		}
		// 获取登录用户的信息
		BioneUserInfo user = userBS.getUserInfo(logicSysNo, username);
		// 如果没有找到信息，用户在系统中不存在，抛出UnknownAccountException异常
		if (user == null) {
			throw new UnknownAccountException("帐号[" + username + "]在系统中不存在!");
		}
		// 如果找到用户信息，用户的状态为停用，抛出DisabledAccountException异常
		if (GlobalConstants4frame.COMMON_STATUS_INVALID.equals(user.getUserSts())) {

			throw new DisabledAccountException("帐号[" + username + "]处于停用状态!");
		}
		// 判断用户是否被锁定
		IUserLockValidator lockValidator = SpringContextHolder
				.getBean(GlobalConstants4frame.USER_LOCK_VALIDATOR_NAME);
		Timestamp currTime = new Timestamp(new Date().getTime());
		if (lockValidator != null) {
			BioneLockInfoBO lockBO = lockValidator.getLockStsByUser(
					user.getUserId(), logicSysNo, currTime, isSuperUser);
			if (lockBO != null && lockBO.getLockSts()) {
				// 用户已被锁定
				StringBuilder tipMsg = new StringBuilder("帐号[" + username
						+ "]已经被锁定!");
				if (lockBO.getUnlockRestHours() > 0) {
					tipMsg.append("离解锁还剩").append(lockBO.getUnlockRestHours())
							.append("小时。");
				}
				throw new LockedAccountException(tipMsg.toString());
			}
		}
		BioneUser bioneUser = new BioneUser();
		bioneUser.setUserId(user.getUserId());
		bioneUser.setUserNo(user.getUserNo());
		bioneUser.setUserName(user.getUserName());
		bioneUser.setLoginName(username);
		bioneUser.setCurrentLogicSysNo(token.getLogicSysNo());
		bioneUser.setSuperUser(isSuperUser);
		bioneUser.setAuthTypeNo(GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER);
		bioneUser.setOrgNo(user.getOrgNo());
		bioneUser.setDeptNo(user.getDeptNo());
		bioneUser.setPwdStr(token.getPasswordstr());
		bioneUser.setTicket(BioneSecurityUtils.getHashedPasswordBase64(token
				.getUsername()));
		bioneUser.setLastUpdateTime(user.getLastUpdateTime().toString());
		String credentials = null;
		if (StringUtils.isBlank(ticket)) {
			credentials = user.getUserPwd();
		} else {
			token.setPassword(user.getUserName().toCharArray());
			credentials = ticket;
		}
		SimpleAuthenticationInfo authInfo = new SimpleAuthenticationInfo(
				bioneUser, credentials, getName());
		// 设置密码Hash散列的salt值，加强安全性，增加字典攻击的难度
		// 目前salt值使用的是固定值，可以每个用户独立绑定一个salt值
		authInfo.setCredentialsSalt(ByteSource.Util
				.bytes(GlobalConstants4frame.CREDENTIALS_SALT));
		return authInfo;

	}

	/**
	 * 获取当前用户的授权信息
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {

		SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		// 用户所属的机构和部门信息
		if (!StringUtils.isBlank(bioneUser.getOrgNo())) {
			BioneOrgInfo orgInfo = this.orgBS.findOrgInfoByOrgNo(
					bioneUser.getOrgNo(), GlobalConstants4frame.SUPER_LOGIC_SYSTEM);
			if (orgInfo != null) {
				bioneUser.setOrgNo(orgInfo.getOrgNo());
				bioneUser.setOrgName(orgInfo.getOrgName());
			}
		}

		if (!StringUtils.isBlank(bioneUser.getDeptNo())) {
			BioneDeptInfo deptInfo = this.deptBS.findDeptInfoByOrgNoandDeptNo(
					bioneUser.getOrgNo(), bioneUser.getDeptNo(),
					GlobalConstants4frame.SUPER_LOGIC_SYSTEM);
			if (deptInfo != null) {
				bioneUser.setDeptNo(deptInfo.getDeptNo());
				bioneUser.setDeptName(deptInfo.getDeptName());
			}
		}
		// 获取用户的授权对象信息
		bioneUser.setAuthObjMap(this.authBS.findAuthObjUserRelMap(
				bioneUser.getCurrentLogicSysNo(), bioneUser.getUserId()));

		Set<String> allPermissions = Sets.newHashSet();
		List<BioneAuthObjResRel> authObjResRelList = Lists.newArrayList();

		// 查询通过用户授权的资源列表
		List<String> objIdList = new ArrayList<String>();
		objIdList.add(bioneUser.getUserId());
		List<BioneAuthObjResRel> userResRelList = this.authBS
				.findCurrentUserAuthObjResRels(
						bioneUser.getCurrentLogicSysNo(),
						GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER, objIdList);

		if (userResRelList != null)
			authObjResRelList.addAll(userResRelList);

		// 获取授权组权限
		List<String> authGrpIdList = this.authBS
				.findAuthGroupIdOfCurrentUser(bioneUser.getCurrentLogicSysNo());

		List<BioneAuthObjResRel> grpResRelList = this.authBS
				.findCurrentUserAuthObjResRels(
						bioneUser.getCurrentLogicSysNo(),
						GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP, authGrpIdList);

		if (grpResRelList != null)
			authObjResRelList.addAll(grpResRelList);

		// 通过其他授权对象获取授权资源
		Map<String, List<String>> userAuthObjMap = bioneUser.getAuthObjMap();
		Iterator<String> it = userAuthObjMap.keySet().iterator();

		String objDefNo = null;
		List<String> authObjIdList = null;
		while (it.hasNext()) {

			objDefNo = it.next();
			authObjIdList = userAuthObjMap.get(objDefNo);

			if (authObjIdList != null) {
				List<BioneAuthObjResRel> objDefResRelList = this.authBS
						.findCurrentUserAuthObjResRels(
								bioneUser.getCurrentLogicSysNo(), objDefNo,
								authObjIdList);
				if (objDefResRelList != null)
					authObjResRelList.addAll(objDefResRelList);
			}
		}

		// 将用户所拥有权限的资源按照资源类型分组
		Map<String, List<BioneAuthObjResRel>> resDefGroupMap = Maps
				.newHashMap();

		String resDefNo = null;
		List<BioneAuthObjResRel> authObjResList = null;

		for (BioneAuthObjResRel authObjResRel : authObjResRelList) {

			resDefNo = authObjResRel.getId().getResDefNo();
			authObjResList = resDefGroupMap.get(resDefNo);

			if (authObjResList == null) {

				authObjResList = Lists.newArrayList();
				resDefGroupMap.put(resDefNo, authObjResList);
			}

			authObjResList.add(authObjResRel);
		}

		Iterator<String> resDefIt = resDefGroupMap.keySet().iterator();

		while (resDefIt.hasNext()) {

			resDefNo = resDefIt.next();
			authObjResList = resDefGroupMap.get(resDefNo);

			BioneAuthResDef adminAuthResDef = this.authBS.getEntityByProperty(
					BioneAuthResDef.class, "resDefNo", resDefNo);
			if (adminAuthResDef != null) {
				try {
					IResObject reObj = (IResObject) SpringContextHolder
							.getBean(adminAuthResDef.getBeanName());
					List<String> resSermissions = reObj
							.doGetResPermissions(authObjResList);

					if (resSermissions != null)
						allPermissions.addAll(resSermissions);
				} catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
					// 跳过不存在的已定义授权资源bean
					continue;
				}
			}

		}

		authzInfo.setStringPermissions(allPermissions);

		return authzInfo;

	}
}
