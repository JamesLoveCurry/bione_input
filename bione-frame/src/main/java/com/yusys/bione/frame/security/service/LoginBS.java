package com.yusys.bione.frame.security.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.ExDateUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.auth.service.AuthInfoBS;
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfo;
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfoCAS;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.authres.service.MenuBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.common.LogicSysInfoHolder;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.lock.IUserLockValidator;
import com.yusys.bione.frame.security.lock.entity.BioneLockInfoBO;
import com.yusys.bione.frame.security.token.BioneAuthenticationToken;
import com.yusys.bione.frame.syslog.entity.BioneLogLogin;
import com.yusys.bione.frame.syslog.service.BioneLogLoginBS;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;

/**
 * @author songxuefeng 登录认证服务，包括登录页面直接登录的认证、单点登录的认证
 */
@Service
public class LoginBS {
	@Autowired
	BioneLogLoginBS logloginBS;
	@Autowired
	private AuthInfoBS authInfoBS;
	@Autowired
	private AuthBS authBS;
	@Autowired
	private DefaultWebSecurityManager securityManager;
	@Autowired
	private MenuBS menuBS;
	@Autowired
	private AuthConfigBS authConfigBS;
	@Autowired
	private CasAuthBS casAuthBS;
	protected Logger logger = LoggerFactory.getLogger(LoginBS.class);
	@Autowired
	private UserBS userInfoBS;

	/**
	 * 正常登录逻辑
	 *
	 * @param username
	 * @param password
	 * @param logicSysNo
	 * @param ticket
	 * @param tnt
	 *            CAS服务用登录票据
	 * @param loginIp
	 * @param pwdVerify
	 *            是否进行密码安全校验 true -> 是；false -> 否
	 * @return
	 */
	public String login(String username, String password, String logicSysNo,
						String ticket, String tnt, String loginIp,String sessionId, boolean pwdVerify) {

		/*try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] digest = md.digest(password.getBytes());
			password = new BigInteger(1,digest).toString(16).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			logger.error("md5加密异常{}",e);
		}*/

		// 返回登录页面的认证失败提示信息
		String loginFailInfo = null;
		// 认证类名称
		String beanName = "";
		// 获取逻辑系统信息
		BioneLogicSysInfo logicSysInfo = LogicSysInfoHolder
				.getCurrentLogicSysInfo(logicSysNo);
		// 判断当前登录的逻辑系统的认证方式
		String authTypeNo = logicSysInfo.getAuthTypeNo();
		// 判断当前登录用户是否是逻辑系统管理员
		boolean isSuperUser = authBS.findAdminUserInfo(username, logicSysNo);
		// 当用户为超级系统认证时
		//20140425 修改逻辑系统管理员登陆逻辑
		if(isSuperUser){

			beanName = "localSysAuthorizingRealm";
		}else{
			// 获取逻辑系统的认证实体对象
			BioneAuthInfo authInfo = authInfoBS.getEntityById(authTypeNo);
			// 获取认证方式的实现类
			beanName = authInfo.getBeanName();

		}

		// 加载认证实现类
		Realm realm = SpringContextHolder.getBean(beanName);
		securityManager.setRealm(realm);
		// 初始化BioneAuthenticationToken
		BioneAuthenticationToken token = new BioneAuthenticationToken(username,
				password, logicSysNo, isSuperUser, ticket, tnt);
		// 认证
		// 获取认证主题
		Subject subject = BioneSecurityUtils.getSubject();
		// 获取客户端IP
		try {
			if (subject.isAuthenticated()) {
				subject.logout();
			}
			subject.login(token);

			logger.info("帐号[{}]于{}提交登录系统请求!", username,
					ExDateUtils.getCurrentStringDateTime());
		} catch (UnknownAccountException uae) {
			logger.info("登录失败," + uae.getMessage());
			loginFailInfo = "帐号[" + username + "]在系统中不存在!";
		} catch (IncorrectCredentialsException ice) {
			logger.info("登录失败,帐号[" + username + "]用户名密码错误!");
			loginFailInfo = "用户名密码错误,请重新输入!";
			if (pwdVerify) {
				IUserLockValidator lockValidator = SpringContextHolder
						.getBean(GlobalConstants4frame.USER_LOCK_VALIDATOR_NAME);
				if (lockValidator != null) {
					BioneLockInfoBO lockInfo = lockValidator
							.manageWhenPwdError(username, logicSysNo, loginIp,
									isSuperUser);
					if (lockInfo != null) {
						StringBuilder msg = new StringBuilder("登陆密码错误");
						if (lockInfo.getLockSts()) {
							msg.append("，账户已锁定!");
						} else if (lockInfo.getRestErrorTimes() > 0) {
							msg.append("，还剩余")
									.append(lockInfo.getRestErrorTimes())
									.append("次机会 !");
						}
						loginFailInfo = msg.toString();
					}
				}
			}
		} catch (LockedAccountException lae) {
			logger.info("登录失败,帐号[" + username + "]已经被锁定!");
			loginFailInfo = lae.getMessage();
		} catch (DisabledAccountException dae) {
			logger.info("登录失败,帐号[" + username + "]处于停用状态!");
			loginFailInfo = "帐号[" + username + "]处于停用状态!";
		} catch (AuthenticationException ae) {
			logger.error("系统登录时发生异常,认证实现代码和远程认证服务连接是否正常!");
			ae.printStackTrace();
			loginFailInfo = "系统发生未知异常,请联系管理员!";
		}
		// 登录后的页面跳转处理
		// 如果登录成功
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
		String isOpenCache = propertiesUtils.getProperty("isOpenCache");
		if("Y".equals(isOpenCache)) {//是否走缓存模式，缓存模式就少更新一些表，少写一些日志表
			subject.hasRole("");
			logger.info("进入权限缓存模式！！！");
			return loginFailInfo;
		}
		if (subject.isAuthenticated()) {
			// 初始化一下权限列表
			subject.hasRole("");
			logger.info("帐号[{}]登录验证通过.", username);
			if (pwdVerify) {
				// 登陆成功，重置该用户的密码错误次数
				IUserLockValidator lockValidator = SpringContextHolder
						.getBean(GlobalConstants4frame.USER_LOCK_VALIDATOR_NAME);
				if (lockValidator != null) {
					lockValidator.resetUserByNo(username, logicSysNo,
							isSuperUser);
				}
			}

			//修改其他的session，将其改为当前的时间
			this.logloginBS.getLogLogin(sessionId);
			//如果登录成功，添加用户登陆信息
			BioneLogLogin login=new BioneLogLogin();
			login.setLogId(RandomUtils.uuid2());
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
			String loginDate=ft.format(new Date());
			login.setUserId(BioneSecurityUtils.getCurrentUserId());
			login.setLoginDate(loginDate);
			login.setLoginTime(System.currentTimeMillis());
			login.setLogoutTime(System.currentTimeMillis());
			login.setLoginIp(loginIp);
			login.setSessionId(sessionId);
			logloginBS.save(login);

			BioneUser user = (BioneUser)subject.getPrincipal();
			BioneUserInfo userInfo = userInfoBS.getEntityById(BioneUserInfo.class,user.getUserId());
			propertiesUtils = PropertiesUtils.get(
					"bione-frame/index/index.properties");
			String lastDay = propertiesUtils.getProperty("login_last_day");
			if (!isSuperUser && StringUtils.isNotBlank(lastDay) && StringUtils.isNumeric(lastDay)) {
				try{
					int day = Integer.parseInt(lastDay);
					if(day>0){
						long time = 86400000 * day;
						long lastTime = login.getLoginTime() - userInfo.getLastUpdateTime().getTime();
						if (time <= lastTime) {
							return "超出试用日期";
						}
					}
				}
				catch(Exception e){
					return "超出试用日期";
				}
			}
			// 如果登录成功，但是没有该逻辑系统的菜单权限，自动退出登录
			int count = this.getPermissionMenuByLogicsys(logicSysNo);
			if (count <= 0) {
				loginFailInfo = "帐号[" + username + "]没有该逻辑系统的操作权限!";
				logger.info("帐号[{}]没有逻辑系统[{}]的操作权限!", username, logicSysNo);
				subject.logout();
			}
		}
		else{
			logger.info("帐号[{}]登录验证没有通过!", username);
		}
		return loginFailInfo;
	}

	/*
	 * 查询用户在某逻辑系统下的可用菜单
	 *
	 * @param logicSysNo 逻辑系统标识
	 *
	 * @return
	 */
	public int getPermissionMenuByLogicsys(String logicSysNo) {
		int count = 0;
		List<CommonTreeNode> menuInfoList = menuBS.buildMenuTree(logicSysNo,
				"0", false);
		if (menuInfoList != null && !menuInfoList.isEmpty()) {
			count = menuInfoList.size();
		}
		return count;
	}

	/**
	 * 登出当前用户
	 */
	public void logout() {
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		if (bioneUser != null
				&& GlobalConstants4frame.AUTH_TYPE_LOCAL_CAS.equals(bioneUser
				.getAuthTypeNo())) {
			// 若是CAS单点认证
			BioneAuthInfoCAS authInfoCas = authConfigBS.getEntityByProperty(
					BioneAuthInfoCAS.class, "authTypeNo",
					GlobalConstants4frame.AUTH_TYPE_LOCAL_CAS);
			String protocol = "";
			if (authInfoCas.getServerProtocol().equals("02")) {
				protocol = "http";
			} else {
				protocol = "https";
			}
			String serverUrl = protocol + "://" + authInfoCas.getIpAddress()
					+ ":" + authInfoCas.getServerPort() + "/"
					+ authInfoCas.getServerName() + "/v1/tickets";
			try {
				casAuthBS.logout(serverUrl,bioneUser.getTnt());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Subject subject = BioneSecurityUtils.getSubject();
		if (subject != null) {
			subject.logout();
		}
	}

	/**
	 * 获取指定逻辑系统认证类型
	 *
	 * @param logicSysNo
	 * @return
	 */
	public String getAuthNoBySys(String logicSysNo) {
		String authNo = "";
		if (!StringUtils.isEmpty(logicSysNo)) {
			BioneLogicSysInfo logic = this.authBS.getEntityByProperty(
					BioneLogicSysInfo.class, "logicSysNo", logicSysNo);
			if (logic != null) {
				authNo = logic.getAuthTypeNo();
			}
		}
		return authNo;
	}
}
