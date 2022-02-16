package com.yusys.bione.frame.security.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.ExDateUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.frame.authres.service.MenuBS;
import com.yusys.bione.frame.authres.util.MenuUtils;
import com.yusys.bione.frame.authres.web.vo.BioneMenuInfoVO;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.common.LogicSysInfoHolder;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.logicsys.service.LogicSysBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.service.LoginBS;
import com.yusys.bione.frame.user.service.UserBS;

/**
 * <pre>
 * Title:用户登录Action
 * Description: 用户登录相关的业务处理
 * </pre>
 *
 * @author mengzx
 * @version 1.00.00
 *
 *          <pre>
 * 修改记录
 *    修改后版本:1.01    修改人： songxf  修改日期:2012-07-05     修改内容:增加逻辑系统支持
 *    修改后版本:1.02    修改人： songxf  修改日期:2013-10-15     修改内容:重新梳理认证体系代码
 * </pre>
 */
@Controller
public class LoginController extends BaseController {

	@Autowired
	private LoginBS loginBS;
	//	@Autowired
//	private LogBS logBS;
	@Autowired
	private LogicSysBS logicSysBS;
	@Autowired
	private MenuBS menuBS;
	@Autowired
	private UserBS userBS;
	/**
	 * 登录页面
	 *
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView index(HttpServletResponse response,HttpSession session) {
		ModelAndView mav = new ModelAndView();
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
		String isOpenCache = propertiesUtils.getProperty("isOpenCache");
		if("Y".equals(isOpenCache)) {//缓存模式走一个极简页面，应对压测
			mav.setViewName("login-min");
		}else {
			mav.setViewName("login");
		}
		mav.addObject("items", getLogicSysOption());
		mav.addObject("username", GlobalConstants4frame.SUPER_USER_NO);
		mav.addObject("password", GlobalConstants4frame.SUPER_USER_PWD);
		mav.addObject("isFullScreen", true);
		return mav;
	}

	/**
	 * 系统管理员登录页面跳转
	 *
	 * @return
	 */
	@RequestMapping(value = "/loginAdmin", method = RequestMethod.GET)
	public ModelAndView loginAdmin() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("loginAdmin");
		mav.addObject("items", getLogicSysOption());
		mav.addObject("username", GlobalConstants4frame.SUPER_USER_NO);
		mav.addObject("password", GlobalConstants4frame.SUPER_USER_PWD);
		mav.addObject("isFullScreen", true);
		return mav;
	}

	@RequestMapping(value = "/logonAdmin", method = RequestMethod.GET)
	public ModelAndView logonAdmin(String username, String password,
								   String logicSysNo, boolean isFullScreen,
								   HttpServletResponse response, HttpSession session) {
		// 返回登录页面的认证失败提示信息
		String loginIp = this.getRequest().getRemoteAddr();
		String loginFailInfo = loginBS.login(username, password, logicSysNo,
				null, null, loginIp, session.getId(), true);
		if (loginFailInfo == null) {
			if (isFullScreen) {
				openFullScreenWin(logicSysNo, response);
				return null;
			} else {
				PropertiesUtils propertiesUtils = PropertiesUtils.get(
						"bione-frame/index/index.properties");
				String indexType = propertiesUtils.getProperty("index_type");
				boolean loop = true;
				String indexConfig = "index/index";
				if(indexType.equals("1")){
					loop = false;
					indexConfig = "index/index";
				}
				else{
					loop = true;
					indexConfig = "index/index1";
				}
				ModelAndView mav = new ModelAndView();
				mav.addObject("indexUrl", "index/welcome");
				BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
				BioneLogicSysInfo logicsys = logicSysBS.getBioneLogicSysInfoByLogicSysNo(bioneUser.getCurrentLogicSysNo());
				mav.addObject("logicSysName", logicsys.getLogicSysName());
				List<CommonTreeNode> menuInfoList = this.menuBS.buildMenuTree(bioneUser.getCurrentLogicSysNo(), "0", loop);// 只查找第一层的节点
				if (menuInfoList != null) {
					for (CommonTreeNode node : menuInfoList) {
						BioneMenuInfoVO menuInfo = (BioneMenuInfoVO) node.getData();
						if (menuInfo != null && menuInfo.getIndexSts() != null
								&& menuInfo.getIndexSts().equals(GlobalConstants4frame.COMMON_STATUS_VALID)) {
							mav.addObject("indexUrl", menuInfo.getNavPath());
							mav.addObject("indexNm",menuInfo.getFuncName());
							mav.addObject("indexId",menuInfo.getMenuId());
						}
					}
					String menuInfoHTML = MenuUtils.list2HeaderMenu(menuInfoList);
					mav.addObject("menuInfoHTML", menuInfoHTML);
				}
				StringBuilder strBuilder = new StringBuilder();

				// 获取当前用户相关的信息
				strBuilder.append("欢迎您:" + bioneUser.getUserName());

				// 用户所属的机构和部门信息
				if (bioneUser.getOrgName() != null) {

					strBuilder.append("&nbsp;|&nbsp;所属机构:");
					strBuilder.append(bioneUser.getOrgName());
				}

				if (bioneUser.getDeptName() != null) {

					strBuilder.append("&nbsp;|&nbsp;所属部门:");
					strBuilder.append(bioneUser.getDeptName());
				}

				strBuilder.append("&nbsp;|&nbsp;登录时间:" + ExDateUtils.getCurrentStringDateTime());

				String userInfo = strBuilder.toString();

				String userIcon = this.userBS.getUserIcon(bioneUser.getUserId());
				// 系统首页
				mav.setViewName(indexConfig);
				/* @Revision 2013-5-10 添加了[userId]和[userName]两个属性。 */
				mav.addObject("userId", bioneUser.getUserId());
				mav.addObject("userName", bioneUser.getUserName());
				mav.addObject("userInfo", userInfo);
				mav.addObject("userIcon", userIcon);
				mav.addObject("logicSysNo", bioneUser.getCurrentLogicSysNo());
				mav.addObject("items", getLogicSysOption());
				return mav;
			}
		} else {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("login");
			mav.addObject("LOGIN_FAIL_INFO", loginFailInfo);
			mav.addObject("items", getLogicSysOption());
			mav.addObject("username", username);
			mav.addObject("password", password);
			mav.addObject("logicSysNo", logicSysNo);
			mav.addObject("isFullScreen", isFullScreen);
			return mav;
		}
	}


	/**
	 * 超级管理员登录页面
	 *
	 * @return
	 */
	@RequestMapping(value = "/super", method = RequestMethod.GET)
	public ModelAndView superindex() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("super");
		mav.addObject("logicSysNo", GlobalConstants4frame.SUPER_LOGIC_SYSTEM);
		mav.addObject("isFullScreen", true);
		return mav;
	}

	/**
	 * CAS认证入口
	 *
	 * @return
	 */
	@RequestMapping(value = "/logonCAS", method = RequestMethod.POST)
	public ModelAndView logonCAS(HttpServletResponse response, HttpServletRequest re, HttpSession session) {
		//根据 HttpServletRequest对象 获取用户名
		String username = re.getAttribute("fr_username").toString();
		//根据 HttpServletRequest对象 CAS 票证信息
		String tnt = re.getAttribute("fr_fs_auth_key").toString();
		
		String pwd = "123456";
		String logicSysNo = "FRS";
		boolean isFullScreen = false;
		
		// 返回登录页面的认证失败提示信息
		String loginIp = this.getRequest().getRemoteAddr();
		String loginFailInfo = loginBS.login(username, pwd, logicSysNo, null, tnt, loginIp, session.getId(),
				true);
		if (loginFailInfo == null) {
			if (isFullScreen) {
				openFullScreenWin(logicSysNo, response);
				return null;
			} else {
				return new ModelAndView("redirect:/index");
			}
		} else {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("login");
			mav.addObject("LOGIN_FAIL_INFO", loginFailInfo);
			mav.addObject("items", getLogicSysOption());
			mav.addObject("username", username);
			mav.addObject("password", pwd);
			mav.addObject("logicSysNo", logicSysNo);
			mav.addObject("isFullScreen", isFullScreen);
			return mav;
		}
	}
	/**
	 * 用户登陆验证
	 *
	 * @return
	 */
	@RequestMapping(value = "/logon", method = RequestMethod.POST)
	public ModelAndView logon(String username, String password,
							  String logicSysNo, boolean isFullScreen,
							  HttpServletResponse response, HttpSession session) {
		// 返回登录页面的认证失败提示信息
		String loginIp = this.getRequest().getRemoteAddr();
		String loginFailInfo = loginBS.login(username, password, logicSysNo,
				null, null, loginIp, session.getId(), true);
		if (loginFailInfo == null) {
			if (isFullScreen) {
				openFullScreenWin(logicSysNo, response);
				return null;
			} else {
				return new ModelAndView("redirect:/index");
			}
		} else {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("login");
			mav.addObject("LOGIN_FAIL_INFO", loginFailInfo);
			mav.addObject("items", getLogicSysOption());
			mav.addObject("username", username);
			mav.addObject("password", password);
			mav.addObject("logicSysNo", logicSysNo);
			mav.addObject("isFullScreen", isFullScreen);
			return mav;
		}
	}



	/**
	 * 逻辑系统切换
	 *
	 * @return
	 */
	@RequestMapping(value = "/loginSSO", method = RequestMethod.POST)
	public ModelAndView loginSSO(String logicSysNo,
								 HttpServletResponse response, HttpSession session) {
		String targetAuthNo = loginBS.getAuthNoBySys(logicSysNo);
		String loginIp = this.getRequest().getRemoteAddr();
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		if (targetAuthNo != null && targetAuthNo.equals(user.getAuthTypeNo())) {
			// 若当前逻辑系统认证方式和目标逻辑系统认证方式相同，直接进行登录认证；
			// 返回登录页面的认证失败提示信息
			String loginFailInfo = loginBS.login(user.getLoginName(),
					user.getPwdStr(), logicSysNo, user.getTicket(),
					user.getTnt(), loginIp, session.getId(), false);
			if (loginFailInfo == null) {
				return new ModelAndView("redirect:/index");
			} else {
				// 若该用户当前凭证由于某种原因不能使用与目标系统
				// 先注销
				loginBS.logout();
				// 跳转到登录页自行登录
				ModelAndView mav = new ModelAndView();
				mav.setViewName("login");
				mav.addObject("LOGIN_FAIL_INFO", loginFailInfo);
				mav.addObject("items", getLogicSysOption());
				mav.addObject("username", user.getLoginName());
				mav.addObject("password", "");
				mav.addObject("logicSysNo", logicSysNo);
				return mav;
			}
		} else {
			// 若认证不一样
			// 先注销
			loginBS.logout();
			// 跳转到登录页自行登录
			ModelAndView mav = new ModelAndView();
			mav.setViewName("login");
			mav.addObject("items", getLogicSysOption());
			mav.addObject("username", user.getLoginName());
			mav.addObject("password", "");
			mav.addObject("logicSysNo", logicSysNo);
			return mav;
		}
	}

	/**
	 * 在全屏窗口中打开首页
	 */
	private void openFullScreenWin(String logicsysno,
								   HttpServletResponse response) {
		// 打开全屏窗口
		StringBuilder jsBuilder = new StringBuilder();

		jsBuilder.append("<script type=\"text/javascript\">");
		jsBuilder
				.append("var params = 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,';");
		jsBuilder.append("var newwin=window.open('"
				+ this.getRequest().getContextPath() + "/index?logicsysno="
				+ logicsysno + "','bione_index',params);\n");
		jsBuilder.append("if(!window.opener){");
		String agent = this.getRequest().getHeader("user-agent");
		if (agent.contains("MSIE 6.0")) {
			jsBuilder.append("window.opener=null;");
		} else {
			jsBuilder.append("window.open('','_self','');");
		}
		jsBuilder.append("setTimeout(\"window.close();\",1000);}");
		jsBuilder.append("</script>");

		this.renderHtml(jsBuilder.toString(), response);
	}

	/**
	 * 注销用户
	 *
	 * @return
	 */
	@RequestMapping("/logout")
	public ModelAndView logout(String quit, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		// 清除逻辑系统缓存
		logger.info("用户[{}]于{}退出系统.",
				BioneSecurityUtils.getCurrentUserInfo() == null ? ""
						: BioneSecurityUtils.getCurrentUserInfo()
						.getLoginName(), ExDateUtils
						.getCurrentStringDateTime());
//		String loginIp = this.getRequest().getRemoteAddr();
		// 清除逻辑系统缓存
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		if (bioneUser != null) {
//			logBS.addLog(loginIp, bioneUser.getCurrentLogicSysNo(),
//					bioneUser.getLoginName(), "用户: " + bioneUser.getUserName()
//							+ "于" + ExDateUtils.getCurrentStringDateTime()
//							+ "退出系统");
		}
		loginBS.logout();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		if ("true".equals(quit)) {// 注销并关闭窗口
			return null;
		} else {// 注销
			mav.setViewName("login");
			mav.addObject("items", getLogicSysOption());
			mav.addObject("isFullScreen", true);
			return mav;
		}
	}


	@RequestMapping("/getLogicSysOption")
	@ResponseBody
	public List<BioneLogicSysInfo> getLogicSysOption() {
		List<BioneLogicSysInfo> logicSysList = LogicSysInfoHolder
				.getLogicSysInfo();
		List<BioneLogicSysInfo> newlist = new ArrayList<BioneLogicSysInfo>();
		newlist.addAll(logicSysList);
		BioneLogicSysInfo logicSysInfo = null;
		for (BioneLogicSysInfo logicSys : newlist) {
			if (logicSys.getLogicSysNo().equals(
					GlobalConstants4frame.SUPER_LOGIC_SYSTEM)) {
				logicSysInfo = logicSys;
			}
		}
		newlist.remove(logicSysInfo);
		return newlist;
	}
}