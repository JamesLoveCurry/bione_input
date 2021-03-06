package com.yusys.bione.frame.security.realm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
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
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfoCAS;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.service.DeptBS;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;
import com.yusys.bione.frame.security.service.CasAuthBS;
import com.yusys.bione.frame.security.token.BioneAuthenticationToken;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;

@Component
public class CasAuthorizingRealm extends AuthorizingRealm {

	@Autowired
	public AuthConfigBS authConfigBS;
	@Autowired
	public CasAuthBS casAuthBS;
	@Autowired
	public UserBS userBS;
	@Autowired
	public AuthBS authBS;
	@Autowired
	public OrgBS orgBS;
	@Autowired
	public DeptBS deptBS;

	public CasAuthorizingRealm() {
		setAuthenticationTokenClass(BioneAuthenticationToken.class);
	}

	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		BioneAuthenticationToken bioneToken = (BioneAuthenticationToken) token;
		if (token == null) {
			return null;
		}
		boolean isSuperUser = bioneToken.getIsSuper();
		// ???????????????????????????
		BioneUserInfo user = userBS.getUserInfo(
				GlobalConstants4frame.SUPER_LOGIC_SYSTEM, bioneToken.getUsername());
		if (user == null) {
			throw new UnknownAccountException();
		}
		String ticket = (String) bioneToken.getTicket();
		String tnt = bioneToken.getTnt();
		BioneAuthInfoCAS authInfoCas = authConfigBS.getEntityByProperty(
				BioneAuthInfoCAS.class, "authTypeNo",
				GlobalConstants4frame.AUTH_TYPE_LOCAL_CAS);
		String protocol = "";
		if (authInfoCas.getServerProtocol().equals("02")) {
			protocol = "http";
		} else {
			protocol = "https";
		}
		if (!StringUtils.isBlank(ticket)) {
			// ??????????????????
			String serverUrl = protocol + "://" + authInfoCas.getIpAddress()
					+ ":" + authInfoCas.getServerPort() + "/"
					+ authInfoCas.getServerName() + "/serviceValidate";
			try {
				boolean isValidate = casAuthBS.validServiceTicket(serverUrl,
						ticket, "");
				if (!isValidate) {
					throw new IncorrectCredentialsException("????????????,?????????????????????");
				}
			} catch (IOException e) {
				throw new AuthenticationException("???????????????????????????" + e.getMessage());
			}
		} else {
			String serverUrl = protocol + "://" + authInfoCas.getIpAddress()
					+ ":" + authInfoCas.getServerPort() + "/"
					+ authInfoCas.getServerName() + "/v1/tickets";
			try {
				tnt = casAuthBS.getTicketGrantingTicket(serverUrl,
						bioneToken.getUsername(),
						String.valueOf(bioneToken.getPassword()));
				if (!StringUtils.isEmpty(tnt)) {
					ticket = casAuthBS.getServiceTicket(serverUrl, tnt,
							CasAuthBS.SERVICE_KEY);
				}
			} catch (IOException e) {
				throw new AuthenticationException("???????????????????????????" + e.getMessage());
			}
			if (StringUtils.isBlank(ticket)) {
				throw new IncorrectCredentialsException();
			}
		}
		BioneUser bioneUser = new BioneUser();
		bioneUser.setUserNo(user.getUserNo());
		bioneUser.setUserId(user.getUserId());
		bioneUser.setUserName(user.getUserName());
		bioneUser.setLoginName(user.getUserNo());
		bioneUser.setCurrentLogicSysNo(bioneToken.getLogicSysNo());
		bioneUser.setSuperUser(isSuperUser);
		bioneUser.setAuthTypeNo(GlobalConstants4frame.AUTH_TYPE_LOCAL_CAS);
		bioneUser.setTicket(ticket);
		bioneUser.setTnt(tnt);
		bioneUser.setOrgNo(user.getOrgNo());
		bioneUser.setDeptNo(user.getDeptNo());
		bioneUser.setPwdStr(bioneToken.getPasswordstr());
		return new SimpleAuthenticationInfo(bioneUser,
				bioneToken.getPassword(), getName());
	}

	/**
	 * ?????????????????????????????????
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {

		SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		// ????????????????????????????????????
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
		// ?????????????????????????????????
		bioneUser.setAuthObjMap(this.authBS.findAuthObjUserRelMap(
				bioneUser.getCurrentLogicSysNo(), bioneUser.getUserId()));

		Set<String> allPermissions = Sets.newHashSet();
		List<BioneAuthObjResRel> authObjResRelList = Lists.newArrayList();

		// ???????????????????????????????????????
		List<String> objIdList = new ArrayList<String>();
		objIdList.add(bioneUser.getUserId());
		List<BioneAuthObjResRel> userResRelList = this.authBS
				.findCurrentUserAuthObjResRels(
						bioneUser.getCurrentLogicSysNo(),
						GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER, objIdList);

		if (userResRelList != null)
			authObjResRelList.addAll(userResRelList);

		// ?????????????????????
		List<String> authGrpIdList = this.authBS
				.findAuthGroupIdOfCurrentUser(bioneUser.getCurrentLogicSysNo());

		List<BioneAuthObjResRel> grpResRelList = this.authBS
				.findCurrentUserAuthObjResRels(
						bioneUser.getCurrentLogicSysNo(),
						GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP, authGrpIdList);

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
				List<BioneAuthObjResRel> objDefResRelList = this.authBS
						.findCurrentUserAuthObjResRels(
								bioneUser.getCurrentLogicSysNo(), objDefNo,
								authObjIdList);
				if (objDefResRelList != null)
					authObjResRelList.addAll(objDefResRelList);
			}
		}

		// ?????????????????????????????????????????????????????????
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
					// ???????????????????????????????????????bean
					continue;
				}
			}

		}

		authzInfo.setStringPermissions(allPermissions);

		return authzInfo;

	}

}
