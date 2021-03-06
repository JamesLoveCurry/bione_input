package com.yusys.bione.frame.security.realm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.AuthenticationNotSupportedException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfoLdap;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.service.DeptBS;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;
import com.yusys.bione.frame.security.token.BioneAuthenticationToken;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;

@Component
public class LdapAuthorizingRealm extends JndiLdapRealm {
	@Autowired
	public AuthBS authBS;
	@Autowired
	public AuthConfigBS authConfigBS;
	@Autowired
	public UserBS userBS;
	@Autowired
	public OrgBS orgBS;
	@Autowired
	public DeptBS deptBS;

	public LdapAuthorizingRealm() {
		super();
		setAuthenticationTokenClass(BioneAuthenticationToken.class);
	}

	/***
	 * ??????
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		AuthenticationInfo info;
		try {
			info = queryForAuthenticationInfo(token, getContextFactory());
		} catch (AuthenticationNotSupportedException e) {
			String msg = "Unsupported configured authentication mechanism";
			throw new AuthenticationException(msg);
		} catch (javax.naming.AuthenticationException e) {
			String msg = "LDAP authentication failed.";
			throw new AuthenticationException(msg);
		} catch (NamingException e) {
			String msg = "LDAP naming error while attempting to authenticate user.";
			throw new AuthenticationException(msg);
		} catch (UnknownAccountException e) {
			String msg = "UnknownAccountException";
			throw new UnknownAccountException(msg);
		} catch (IncorrectCredentialsException e) {
			String msg = "IncorrectCredentialsException";
			throw new IncorrectCredentialsException(msg);
		}
		return info;
	}

	protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token,
			LdapContextFactory ldapContextFactory) throws NamingException {
		BioneAuthenticationToken bioneToken = (BioneAuthenticationToken) token;
		Object principal = bioneToken.getPrincipal();
		Object credentials = bioneToken.getCredentials();
		String logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
		String username = bioneToken.getUsername();
		BioneAuthInfoLdap authLdap = (BioneAuthInfoLdap) this.authConfigBS.getEntityList(BioneAuthInfoLdap.class).get(0);
		JndiLdapContextFactory jndiLdapContextFactory = (JndiLdapContextFactory) ldapContextFactory;
		jndiLdapContextFactory.setUrl("ldap://" + authLdap.getIpAddress() + ":" + authLdap.getServerPort());
		// ???????????????????????????
		boolean isSuperUser = authBS.findAdminUserInfo(username, logicSysNo);
		BioneUserInfo user = userBS.getUserInfo(logicSysNo, username);
		if (user == null) {
			throw new UnknownAccountException("????????????????????????????????????!");
		}
		BioneUser bioneUser = new BioneUser();
		bioneUser.setUserNo(user.getUserNo());
		bioneUser.setUserId(user.getUserId());
		bioneUser.setUserName(user.getUserName());
		bioneUser.setLoginName(username);
		bioneUser.setCurrentLogicSysNo(bioneToken.getLogicSysNo());
		bioneUser.setSuperUser(isSuperUser);
		bioneUser.setAuthTypeNo(GlobalConstants4frame.AUTH_TYPE_LDAP);
		bioneUser.setOrgNo(user.getOrgNo());
		bioneUser.setDeptNo(user.getDeptNo());
		bioneUser.setPwdStr(bioneToken.getPasswordstr());
		// ??????UserDNTemplate?????????????????????????????????????????????
		if (authLdap.getUserdnTemplate() != null) {
			// ????????????????????????????????????
			this.setUserDnTemplate(authLdap.getUserdnTemplate());
			principal = getLdapPrincipal(token);
			ldapContextFactory.getLdapContext(principal, credentials);
			return new SimpleAuthenticationInfo(bioneUser, user.getUserPwd(), getName());
		} else {
			// ???????????????????????????,????????????LDAP??????
			jndiLdapContextFactory.setSystemUsername(authLdap.getBaseName());
			jndiLdapContextFactory.setSystemPassword(authLdap.getBasePwd());
			LdapContext systemCtx = ldapContextFactory.getSystemLdapContext();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// ??????LDAP???????????????????????????????????????,??????????????????????????????????????????,????????????????????????,???????????????
			NamingEnumeration<SearchResult> results = systemCtx.search(authLdap.getBaseName(),
					authLdap.getUserFindAttr() + "=" + principal, constraints);
			while (results.hasMore()) {
				SearchResult si = (SearchResult) results.next();
				principal = si.getName() + "," + authLdap.getBaseName();
			}
			ldapContextFactory.getLdapContext(principal, credentials);
			return new SimpleAuthenticationInfo(bioneUser, user.getUserPwd(), getName());
		}

	}

	/**
	 * ?????????????????????????????????
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		// ????????????????????????????????????
		if (!StringUtils.isBlank(bioneUser.getOrgNo())) {
			BioneOrgInfo orgInfo = this.orgBS.findOrgInfoByOrgNo(bioneUser.getOrgNo(),
					GlobalConstants4frame.SUPER_LOGIC_SYSTEM);
			if (orgInfo != null) {
				bioneUser.setOrgNo(orgInfo.getOrgNo());
				bioneUser.setOrgName(orgInfo.getOrgName());
			}
		}

		if (!StringUtils.isBlank(bioneUser.getDeptNo())) {
			BioneDeptInfo deptInfo = this.deptBS.findDeptInfoByOrgNoandDeptNo(bioneUser.getOrgNo(),
					bioneUser.getDeptNo(), GlobalConstants4frame.SUPER_LOGIC_SYSTEM);
			if (deptInfo != null) {
				bioneUser.setDeptNo(deptInfo.getDeptNo());
				bioneUser.setDeptName(deptInfo.getDeptName());
			}
		}
		// ?????????????????????????????????
		bioneUser.setAuthObjMap(this.authBS.findAuthObjUserRelMap(bioneUser.getCurrentLogicSysNo(),
				bioneUser.getUserId()));

		Set<String> allPermissions = Sets.newHashSet();
		List<BioneAuthObjResRel> authObjResRelList = Lists.newArrayList();

		// ???????????????????????????????????????
		List<String> objIdList = new ArrayList<String>();
		objIdList.add(bioneUser.getUserId());
		List<BioneAuthObjResRel> userResRelList = this.authBS.findCurrentUserAuthObjResRels(
				bioneUser.getCurrentLogicSysNo(), GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER, objIdList);

		if (userResRelList != null)
			authObjResRelList.addAll(userResRelList);

		// ?????????????????????
		List<String> authGrpIdList = this.authBS.findAuthGroupIdOfCurrentUser(bioneUser.getCurrentLogicSysNo());

		List<BioneAuthObjResRel> grpResRelList = this.authBS.findCurrentUserAuthObjResRels(
				bioneUser.getCurrentLogicSysNo(), GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP, authGrpIdList);

		if (grpResRelList != null)
			authObjResRelList.addAll(grpResRelList);

		// ??????????????????????????????????????????
		Map<String, List<String>> userAuthObjMap = bioneUser.getAuthObjMap();
		Iterator<String> it = userAuthObjMap.keySet().iterator();

		String objDefNo = null;
		List<String> authObjIdList = null;
		while (it.hasNext()) {

			objDefNo = it.next();
			authObjIdList = userAuthObjMap.get(objDefNo);

			if (authObjIdList != null) {
				List<BioneAuthObjResRel> objDefResRelList = this.authBS.findCurrentUserAuthObjResRels(
						bioneUser.getCurrentLogicSysNo(), objDefNo, authObjIdList);
				if (objDefResRelList != null)
					authObjResRelList.addAll(objDefResRelList);
			}
		}

		// ?????????????????????????????????????????????????????????
		Map<String, List<BioneAuthObjResRel>> resDefGroupMap = Maps.newHashMap();

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

			BioneAuthResDef adminAuthResDef = this.authBS.getEntityByProperty(BioneAuthResDef.class, "resDefNo",
					resDefNo);
			if (adminAuthResDef != null) {
				try {
					IResObject reObj = (IResObject) SpringContextHolder.getBean(adminAuthResDef.getBeanName());
					List<String> resSermissions = reObj.doGetResPermissions(authObjResList);

					if (resSermissions != null)
						allPermissions.addAll(resSermissions);
				} catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
					// ???????????????????????????????????????bean
					continue;
				}
			}

		}

		authzInfo.setStringPermissions(allPermissions);

		return authzInfo;

	}
}