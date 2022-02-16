package com.yusys.bione.frame.security.realm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.citicbank.authservice.common.ClientUserAccountBean;
import com.citicbank.authservice.common.ConnectUaBean;
import com.citicbank.authservice.common.OrgBean;
import com.citicbank.authservice.common.RoleBean;
import com.citicbank.authservice.common.UaConstants;
import com.citicbank.authservice.common.UserAccountBean;
import com.citicbank.uaauthservice.UaAuthService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfoUa;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.authobj.service.DeptBS;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;
import com.yusys.bione.frame.security.token.BioneAuthenticationToken;
import com.yusys.bione.frame.user.service.UserBS;

@Component
public class UaAuthorizingRealm extends AuthorizingRealm {
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
	// 用户不存在代码
	private static String USER_NO_EXIST_CODE = "2";
	// 用户停用代码
	private static String USER_STOP_CODE = "1";
	// 密码错误代码
	private static String PASS_WORD_ERR_CODE = "3";
	// 验证成功返回码
	private static String USER_PASS_CODE = "0";

	public UaAuthorizingRealm() {
		setAuthenticationTokenClass(BioneAuthenticationToken.class);
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		AuthenticationInfo info;
		try {
			info = queryForAuthenticationInfo(token);
		} catch (UnknownAccountException e) {
			throw new UnknownAccountException(e);
		} catch (IncorrectCredentialsException e) {
			throw new IncorrectCredentialsException(e);
		} catch (DisabledAccountException e) {
			throw new DisabledAccountException(e);
		} catch (AuthenticationException e) {
			throw new AuthenticationException(e);
		}

		return info;
	}

	protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token) {
		String authResult = "";
		BioneUser bioneUser = new BioneUser();
		BioneAuthenticationToken bioneToken = (BioneAuthenticationToken) token;
		boolean isSuperUser = bioneToken.getIsSuper();
		// 获取UA服务器的配置信息
		BioneAuthInfoUa authUa = (BioneAuthInfoUa) this.authConfigBS.getEntityList(BioneAuthInfoUa.class).get(0);
		String password = null;
		try {
			password = byte2hex(MessageDigest.getInstance("SHA-1").digest(
					String.valueOf(bioneToken.getPassword()).getBytes()));
		} catch (NoSuchAlgorithmException e1) {
			throw new AuthenticationException(e1);
		}
		// 生成连接服务参数类，构造方法ConnectUaBean(ip地址，访问端口，认证系统代码)
		ConnectUaBean cub = new ConnectUaBean(authUa.getIpAddress(), authUa.getServerPort(), authUa.getAuthSystemNo());
		// 生成提供服务类
		UaAuthService uas = new UaAuthService(cub);
		// 生成客户端用户信息类
		ClientUserAccountBean cuab = new ClientUserAccountBean();
		// 设置用户登录字符串
		cuab.setUserDN(bioneToken.getUsername());
		// 设置用户登录类型为用户名登录
		cuab.setUserLoginType(UaConstants.UA_LOGIN_TYPE_USERNAME);
		// 设置用户密码
		cuab.setUserPassword(password);

		try {
			// 验证用户信息，并获得服务端用户信息类
			UserAccountBean uab = uas.getUserInfoByClientUserAccountBean(cuab);
			if (uab.isUaAuthResultBoolean()) {
				bioneUser.setUserId(bioneToken.getUsername());
				bioneUser.setUserNo(bioneToken.getUsername());
				bioneUser.setUserName(uab.getUserTrueName());
				bioneUser.setLoginName(bioneToken.getUsername());
				bioneUser.setCurrentLogicSysNo(bioneToken.getLogicSysNo());
				bioneUser.setSuperUser(isSuperUser);
				bioneUser.setAuthTypeNo(GlobalConstants4frame.AUTH_TYPE_UA);
				bioneUser.setTicket(password);
				// 获取用户的归属机构
				OrgBean[] obss = uas.getLocalOrgByUserName(bioneToken.getUsername());
				if (obss != null) {
					bioneUser.setOrgName(obss[0].getOrgName());
					bioneUser.setOrgNo(obss[0].getOrgCode());
				}
				//20140425 角色在认证的时候就已经获得,在这里直接添加进去
				RoleBean[] rbss = uab.getUserRole();
				if(rbss != null && rbss.length > 0){
					Map<String, List<String>> authObjUserRelMap = Maps.newHashMap();
					List<String> result = new ArrayList<String>();
					for (int i = 0; i < rbss.length; i++) {
						result.add(rbss[i].getRoleCode());
					}
					
					authObjUserRelMap.put("UA_AUTH_OBJ_ROLE", result);
					bioneUser.setAuthObjMap(authObjUserRelMap);
					
				}
				
				authResult = USER_PASS_CODE;
				return new SimpleAuthenticationInfo(bioneUser, bioneToken.getPassword(), getName());
			} else {
				authResult = uab.getUaAuthResultStr();
			}
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
		if (USER_STOP_CODE.equals(authResult)) {
			throw new DisabledAccountException();
		} else if (PASS_WORD_ERR_CODE.equals(authResult)) {
			throw new IncorrectCredentialsException();
		} else if (USER_NO_EXIST_CODE.equals(authResult)) {
			throw new UnknownAccountException();
		} else {
			throw new AuthenticationException("UA认证异常,错误码:" + authResult);
		}

	}

	/**
	 * 获取当前用户的授权信息
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();

		// 获取用户的授权对象信息
		//20140425 如果用户关联了UA角色,就不再使用本地角色了
		if(bioneUser.getAuthObjMap() == null){
			bioneUser.setAuthObjMap(this.authBS.findAuthObjUserRelMap(bioneUser.getCurrentLogicSysNo(),
					bioneUser.getUserId()));
		}
		
		Set<String> allPermissions = Sets.newHashSet();
		List<BioneAuthObjResRel> authObjResRelList = Lists.newArrayList();

		// 查询通过用户授权的资源列表
		List<String> objIdList = new ArrayList<String>();
		objIdList.add(bioneUser.getUserId());
		List<BioneAuthObjResRel> userResRelList = this.authBS.findCurrentUserAuthObjResRels(
				bioneUser.getCurrentLogicSysNo(), GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER, objIdList);

		if (userResRelList != null)
			authObjResRelList.addAll(userResRelList);

		// 获取授权组权限
		List<String> authGrpIdList = this.authBS.findAuthGroupIdOfCurrentUser(bioneUser.getCurrentLogicSysNo());

		List<BioneAuthObjResRel> grpResRelList = this.authBS.findCurrentUserAuthObjResRels(
				bioneUser.getCurrentLogicSysNo(), GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP, authGrpIdList);

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
				List<BioneAuthObjResRel> objDefResRelList = this.authBS.findCurrentUserAuthObjResRels(
						bioneUser.getCurrentLogicSysNo(), objDefNo, authObjIdList);
				if (objDefResRelList != null)
					authObjResRelList.addAll(objDefResRelList);
			}
		}

		// 将用户所拥有权限的资源按照资源类型分组
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
					// 跳过不存在的已定义授权资源bean
					continue;
				}
			}

		}

		authzInfo.setStringPermissions(allPermissions);

		return authzInfo;

	}

	/**
	 * 二进制转字符串
	 */
	private static String byte2hex(byte[] b) {

		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {

			stmp = (Integer.toHexString(b[n] & 0XFF));

			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + "";
		}
		return hs;
	}
}
