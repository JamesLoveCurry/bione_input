package com.yusys.bione.frame.user.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.auth.service.AuthObjBS;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.service.DeptBS;
import com.yusys.bione.frame.authobj.util.BioneAuthObjNotifier;
import com.yusys.bione.frame.base.common.CommonFormField;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.logicsys.service.AdminUserBS;
import com.yusys.bione.frame.passwd.IPwdSavaHisStrategy;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.lock.IUserLockValidator;
import com.yusys.bione.frame.user.entity.BioneUserAttr;
import com.yusys.bione.frame.user.entity.BioneUserAttrVal;
import com.yusys.bione.frame.user.entity.BioneUserAttrValPK;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserAttrValBS;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.frame.user.web.vo.BioneUserAttrGrpVO;
import com.yusys.bione.frame.user.web.vo.BioneUserAttrValVO;
import com.yusys.bione.frame.user.web.vo.BioneUserInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * <pre>
 * Title:CRUD操作演示
 * Description: 完成用户信息表的CRUD操作
 * </pre>
 * 
 * @author xuguangyuan xuguangyuansh@gmail.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：许广源		  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/user")
public class UserController extends BaseController {
	@Autowired
	private UserBS userBS;
	@Autowired
	private UserAttrValBS userAttrValBS;

	// --author huangye 判断用户是否可以删除，3种情况均不行
	@Autowired
	private AdminUserBS adminUserBS;
	@Autowired
	private AuthBS authBS;
	@Autowired
	private AuthObjBS authObjBS;

	@Autowired
	private DeptBS deptBS;
	@Autowired
	private IPwdSavaHisStrategy pwdSaveHis;

	@Autowired
	private BioneAuthObjNotifier authObjNotifier;
	
	public void setPwdSaveHis(IPwdSavaHisStrategy saveHis) {
		this.pwdSaveHis = saveHis;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		ModelMap mm = new ModelMap();
		mm.put("isSuperUser", BioneSecurityUtils.getCurrentUserInfo().isSuperUser());
		mm.put("isManager", BioneSecurityUtils.getCurrentUserInfo().getIsManager());
		return new ModelAndView("/frame/user/user-index", mm);
	}

	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager,String isSts, boolean isQuery) {
		Map<String, Object> userMap = Maps.newHashMap();
		SearchResult<Object[]> searchResult = userBS.getUserList(
				pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition(),isSts,isQuery);
		List<BioneUserInfoVO> list = new ArrayList<BioneUserInfoVO>();
		if(searchResult != null && searchResult.getResult() != null){
			for(Object[] object : searchResult.getResult()){
				BioneUserInfoVO vo = new BioneUserInfoVO();
				vo.setUserId(object[0] != null ? object[0].toString():"");
				vo.setUserNo(object[1] != null ? object[1].toString():"");
				vo.setUserName(object[3] != null ? object[3].toString():"");
				vo.setSex(object[4] != null ? object[4].toString():"");
				vo.setBirthday(object[5] != null ? object[5].toString():"");
				vo.setEmail(object[6] != null ? object[6].toString():"");
				vo.setAddress(object[7] != null ? object[7].toString():"");
				vo.setPostcode(object[8] != null ?object[8].toString():"");
				vo.setMobile(object[9] != null ?object[9].toString():"");
				vo.setTel(object[10] != null ?object[10].toString():"");
				vo.setOrgNo(object[11] != null ?object[11].toString():"");
				vo.setDeptNo(object[12] != null ?object[12].toString():"");
				vo.setUserSts(object[14] != null ?object[14].toString():"");
				vo.setIsBuiltin(object[15] != null ?object[15].toString():"");
				vo.setRemark(object[16] != null ?object[16].toString():"");
				vo.setLastPwdUpdateTime(object[17] != null ?(Timestamp)object[17]:null);
				vo.setLastUpdateUser(object[18] != null ?object[18].toString():"");
				vo.setLastUpdateTime(object[19] != null ?(Timestamp)object[19]:null);
				vo.setDeptName(object[21] != null ?object[21].toString():"");
				vo.setOrgName(object[22] != null ? object[22].toString():"");
				vo.setDeptId(object[23] != null ? object[23].toString():"");
				vo.setIsManager(object[25] != null ? object[25].toString():"");
				String idCard = (String)object[26];
				if(StringUtils.isNotBlank(idCard)){
					if (idCard.length() == 15){
						idCard = idCard.replaceAll("(\\w{2})\\w*(\\w{2})", "$1***********$2");
					}
					if (idCard.length() == 18){
						idCard = idCard.replaceAll("(\\w{2})\\w*(\\w{2})", "$1**************$2");
					}
				}
				vo.setIdCard(idCard);
				list.add(vo);
			}
		}
		userMap.put("Rows", list);
		userMap.put("Total", searchResult.getTotalCount());
		return userMap;
	}

	@RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
	@ResponseBody
	public boolean updatePwd(String userId, String userPwd_1, String userPwd_2, String userName, String email, String mobile) {
		if (userId.equals(BioneSecurityUtils.getCurrentUserId())
				|| BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			BioneUserInfo userInfo = this.userBS.getEntityById(BioneUserInfo.class , userId);
			Timestamp now = new Timestamp(System.currentTimeMillis());
			// 加密后的密码
			String pwdCrypt = BioneSecurityUtils
					.getHashedPasswordBase64(userPwd_1);
			/* @Revision 20130415171800-liuch 将密码同时备份到历史表 */
			/*
			 * @Revision 20130530152500-liuch 把这里改造成接口的方式，子工程中可以进行自有扩展
			 * 注意这里的配置是写在[biappframe/applicationContext-base.xml]中的
			 * pwdSaveHis的实现类以具体的配置文件为准！！
			 */
			String result = this.pwdSaveHis.saveHis(userId, pwdCrypt);
			if (!result.equals(IPwdSavaHisStrategy.STATUS_NORMAL)) {
				return false;
			}
			/* @Revision 20130530152500-liuch END */
			/* @Revision 20130415171800-liuch END */
			userInfo.setUserPwd(BioneSecurityUtils
					.getHashedPasswordBase64(userPwd_1));
			userInfo.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
			userInfo.setLastPwdUpdateTime(now);
			userInfo.setLastUpdateTime(now);
			if (StringUtils.isNotBlank(userName)) {
				userInfo.setUserName(userName);
			}
			if (StringUtils.isNotBlank(email)) {
				userInfo.setEmail(email);
			}
			if (StringUtils.isNotBlank(mobile)) {
				userInfo.setMobile(mobile);
			}
			userBS.updateEntity(userInfo);
			return true;
		} else {
			return false;

		}
	}

	/**
	 * 根据ID，加载数据
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneUserInfo show(@PathVariable("id") String id) {
		return this.userBS.getEntityById(BioneUserInfo.class , id);
	}

	// public void getUserEditEvents() {
	// this.responseWrite(this.userBS.getUserEditEvents());
	// }

	/**
	 * 执行修改前的数据加载
	 *
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/user/user-edit", "id", id);
	}

	/**
	 * 用户与授权对象关系
	 *
	 * @return 视图跳转
	 */
	@RequestMapping(value = "/{id}/objectUserRel", method = RequestMethod.GET)
	public ModelAndView objectUserRel(@PathVariable("id") String id) {
		logger.info("用户与授权对象关系ID为：" + id);
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/user/user-object-relation", "id", id);
	}

	/**
	 * 授权对象资源分配
	 *
	 * @return 视图跳转
	 */
	@RequestMapping(value = "/{id}/userResource", method = RequestMethod.GET)
	public ModelAndView userResource(@PathVariable("id") String id) {
		logger.info("授权对象资源分配关系ID为：" + id);
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/user/user-resource-relation", "id", id);
	}

	/**
	 * 执行添加前页面跳转
	 * 
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/user/user-edit";
	}

	/*
	 * 修改密码
	 * 
	 * @Revision 2013-6-5 liucheng2@yuchengtech.com 这个功能去掉了。 改成密码重置功能。@see
	 * resetPwd
	 */
	/*
	 * @RequestMapping(value = "/updatePwd") public ModelAndView
	 * updatePwd(String id) { return new ModelAndView("/frame/user/user-pwd",
	 * "id", id); }
	 */

	/**
	 * 重置密码
	 */
	@RequestMapping(value = "/{id}/resetPwd", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> resetPwd(@PathVariable("id") String id) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			retMap.put("msg", "非超级管理员不能使用重置密码功能");
			return retMap;
		}
		try {
			BioneUserInfo user = this.userBS.getEntityById(BioneUserInfo.class , id);
			user.setUserPwd(BioneSecurityUtils
					.getHashedPasswordBase64(GlobalConstants4frame.DEFAULT_PD));
			this.userBS.updateEntity(user);
			// 重置密码成功，记录修改历史
			this.pwdSaveHis.saveHis(user.getUserId(), BioneSecurityUtils
					.getHashedPasswordBase64(GlobalConstants4frame.DEFAULT_PD));
			retMap.put("msg", "S");
		} catch (Exception ex) {
			retMap.put("msg", "F");
			ex.printStackTrace();
		}
		return retMap;
	}

	/**
	 * 解锁用户
	 */
	@RequestMapping(value = "/unlockUser", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> unlockUser(String userId) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			retMap.put("msg", "非管理员不能使用解锁用户功能");
			return retMap;
		}
		try {
			BioneUserInfo user = this.userBS.getEntityById(BioneUserInfo.class , userId);
			IUserLockValidator lockValidator = SpringContextHolder
					.getBean(GlobalConstants4frame.USER_LOCK_VALIDATOR_NAME);
			if (lockValidator != null) {
				lockValidator.resetUserByNo(user.getUserNo(),
						BioneSecurityUtils.getCurrentUserInfo()
								.getCurrentLogicSysNo(), this.authBS
								.findAdminUserInfo(user.getUserNo(),
										BioneSecurityUtils.getCurrentUserInfo()
												.getCurrentLogicSysNo()));
			}
			retMap.put("msg", "S");
		} catch (Exception ex) {
			retMap.put("msg", "F");
			ex.printStackTrace();
		}
		return retMap;
	}

	/**
	 * 用户自行修改密码
	 */
	@RequestMapping("/updateCurPwd")
	public ModelAndView updateCurPwd(String isFirst) {
		String id = BioneSecurityUtils.getCurrentUserId();
		// 获取用户基本信息
		BioneUserInfo userInfo = this.userBS.getEntityById(
				BioneUserInfo.class, id);
		ModelAndView modelAndView = new ModelAndView("/frame/user/user-curpwd");
		modelAndView.addObject("id", id);
		modelAndView.addObject("isFirst", isFirst);
		if (userInfo != null) {
			modelAndView.addObject("oldUserName", userInfo.getUserName());
			modelAndView.addObject("oldEmail", userInfo.getEmail());
			modelAndView.addObject("oldMobile", userInfo.getMobile());
		}
		return modelAndView;
	}

	/**
	 * 验证原始密码是否有效
	 */
	@RequestMapping("/userPwdValid")
	@ResponseBody
	public boolean userPwdValid(String userPwd_old) {
		BioneUserInfo model = this.userBS.validUserPwd(
				BioneSecurityUtils.getCurrentUserId(),
				BioneSecurityUtils.getHashedPasswordBase64(userPwd_old));
		if (model != null && !StringUtils.isEmpty(model.getUserId())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证新密码是否与历史密码重复有效
	 */
	@RequestMapping("/userPwdHisValid")
	@ResponseBody
	public boolean userPwdHisValid(String userPwd_1, String diffRecentHis) {
		// 加密后的新密码
		String pwdCrypt = BioneSecurityUtils.getHashedPasswordBase64(userPwd_1);
		return this.pwdSaveHis.isPwdValid(
				BioneSecurityUtils.getCurrentUserId(), pwdCrypt, diffRecentHis);
	}

	/**
	 * 执行删除操作，可进行指删除
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> destroy(@PathVariable("id") String ids) {
		Map<String, String> msgMap = Maps.newHashMap();
		if (ids != null && !"".equals(ids)) {
			String[] idArray = StringUtils.split(ids, ',');
			// --author huangye
			boolean flag = false;

			// 添加管理员用户不允许删除，除非先将管理员属性删除。
			for (String id : idArray) {
				boolean flags = this.adminUserBS.checkIsUserBeAdmin(id);
				if (flags) {
					flag = true;
					break;
				}
			}
			if (flag) {
				msgMap.put("msg", "3");
				return msgMap;
			}
			//删除用户不需要判断是否有关联授权对象
//			for (String id : idArray) {
//				boolean flags = this.authBS.checkIsUserUsedObj(id);
//				if (flags) {
//					flag = true;
//					break;
//				}
//			}
			if (flag) {
				msgMap.put("msg", "1");
				return msgMap;
			}
			for (String id : idArray) {
				boolean flags = this.authObjBS.checkIsObjBeUsedByRes(id,
						GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER);
				if (flags) {
					flag = true;
					break;
				}
			}
			if (flag) {
				msgMap.put("msg", "2");
				return msgMap;
			}

			try {
				this.userBS.removeEntityBatch(ids);
				BioneUserInfo userInfo = new BioneUserInfo();
				for (String id : idArray) {
					userInfo.setUserId(id);
					authObjNotifier.alterUserNotify(BioneAuthObjNotifier.OP_REMOVE, userInfo);
				}
				msgMap.put("msg", "0");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msgMap;

	}

	/**
	 * 表单验证中的后台验证，验证用户标识是否已存在
	 */
	@RequestMapping("/userNoValid")
	@ResponseBody
	public boolean userNoValid(String userNo) {
		BioneUserInfo model = userBS.getUserInfo(BioneSecurityUtils
				.getCurrentUserInfo().getCurrentLogicSysNo(), userNo);
		if (model != null)
			return false;
		else
			return true;
	}

	/**
	 * 获取机构下拉框数据
	 */
	@RequestMapping("/getOrgComboxData.*")
	@ResponseBody
	public List<Map<String, String>> getOrgComboxData(String orgNo) {
		return this.userBS.getOrgComboxData(orgNo);
	}

	/**
	 * 获取部门下拉框数据
	 */
	@RequestMapping("/getDeptComboxData.*")
	@ResponseBody
	public List<Map<String, String>> getDeptComboxData(String orgNo,
			String deptNo) {
		return this.userBS.getDeptComboxData(orgNo, deptNo);
	}

	/**
	 * 获取头像列表
	 */
	@RequestMapping("/buildHeadIconList")
	public ModelAndView buildHeadIconList() {
		String iconsHTML = this.buildIconSelectHTML("usericons");
		return new ModelAndView("/frame/user/user-icons", "iconsHTML",
				iconsHTML);
	}

	/**
	 * 修改头像
	 */
	@RequestMapping("/updateHeadIcon")
	public void updateHeadIcon(String userIcon) {
		BioneUserInfo user = this.userBS.getEntityById(BioneUserInfo.class , BioneSecurityUtils
				.getCurrentUserId());
		user.setUserIcon(userIcon);
		this.userBS.updateEntity(user);
	}

	/**
	 * 保存动态用户属性方法 --edit by zhongqh
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void create(String submitObj) {
		if (submitObj != null && !"".equals(submitObj)) {
			String uId = this.getRequest().getParameter("userId");
			// 解析JSON
			JSONObject sObj = JSON.parseObject(submitObj);
			JSONArray extArray = sObj.getJSONArray("extArray");
			JSONObject unextObj = sObj.getJSONObject("unextObj");
			String userIdTmp = "";
			if (unextObj != null) {
				// 新增用户
				BioneUserInfo userTmp = unextObj.toJavaObject(BioneUserInfo.class);
				// 防xss注入
				String userName = userTmp.getUserName();
				String xssUserName = StringUtils2.striptXSS(userName);
				if (!userName.equals(xssUserName)) {
					return;
				}
				if (uId != null && !"".equals(uId)) {
					// 若是修改操作
					BioneUserInfo userInfo = this.userBS.getEntityById(
							BioneUserInfo.class, uId);
					userTmp.setUserNo(userInfo.getUserNo());
					userTmp.setUserIcon(userInfo.getUserIcon());
					userTmp.setUserPwd(userInfo.getUserPwd());
					userTmp.setLastPwdUpdateTime(userInfo
							.getLastPwdUpdateTime());

				} else {
					userTmp.setUserIcon("/images/classics/index/users/user0.png");
					String tmpPwd = userTmp.getUserPwd();
					if (tmpPwd == null || tmpPwd.length() <= 0) {
						tmpPwd = GlobalConstants4frame.DEFAULT_PD;
					}
					userTmp.setUserPwd(BioneSecurityUtils
							.getHashedPasswordBase64(tmpPwd));
					userTmp.setLastPwdUpdateTime(new Timestamp(new Date()
							.getTime()));
				}
				userTmp.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
				userTmp.setLastUpdateTime(new Timestamp(new Date().getTime()));
				userTmp.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
						.getCurrentLogicSysNo());
				userTmp.setIsBuiltin(GlobalConstants4frame.COMMON_STATUS_INVALID);
				userIdTmp = userBS.saveUserInfo(userTmp);
				if (StringUtils.isEmpty(uId)) {
					authObjNotifier.alterUserNotify(BioneAuthObjNotifier.OP_ADD, userTmp);
				} else {
					authObjNotifier.alterUserNotify(BioneAuthObjNotifier.OP_MODIFY, userTmp);
				}
			}
			if (extArray != null && userIdTmp != null) {
				// 保存用户属性扩展 , 先清空扩展属性
				this.userBS.deleteUserAttrsByUId(userIdTmp);
				for (int i = 0; i < extArray.size(); i ++) {
					JSONObject objTmp = extArray.getJSONObject(i);
					String attrId = (String) objTmp.get("attrId");
					String attrValue = (String) objTmp.get("attrValue");
					BioneUserAttrVal valTmp = new BioneUserAttrVal();
					BioneUserAttrValPK valPK = new BioneUserAttrValPK();
					valPK.setAttrId(attrId);
					valPK.setUserId(userIdTmp);
					valTmp.setId(valPK);
					valTmp.setAttrValue(attrValue);
					this.userAttrValBS.saveOrUpdateEntity(valTmp);
				}
			}
		}
	}

	/**
	 * 修改动态用户时的属性初始化方法
	 */
	@RequestMapping("/setWhenUpdate.*")
	@ResponseBody
	public Map<String, Object> setWhenUpdate(String userId) {
		Map<String, Object> fieldsMap = new HashMap<String, Object>();
		String userIdTmp = userId;
		if (userIdTmp != null && !"".equals(userIdTmp)) {
			// 获取用户基本信息
			BioneUserInfo userInfo = this.userBS.getEntityById(
					BioneUserInfo.class, userIdTmp);
			if (userInfo != null) {
				fieldsMap.put("userInfo", userInfo);
				// 获取扩展属性
				List<BioneUserAttrValVO> vals = this.userBS
						.getAttrValsByUser(userIdTmp);
				if (vals != null) {
					fieldsMap.put("attrVals", vals);
				}
				// 获取机构中文名
				if (userInfo.getOrgNo() != null
						&& !"".equals(userInfo.getOrgNo())) {
					BioneOrgInfo org = this.userBS.getEntityByProperty(
							BioneOrgInfo.class, "orgNo", userInfo.getOrgNo());
					if (org != null) {
						fieldsMap.put("orgName", org.getOrgName());
					}
				}
				// 获取部门中文名
				if (userInfo.getDeptNo() != null
						&& !"".equals(userInfo.getDeptNo())) {
					// BioneDeptInfo dept = this.userBS
					// .getEntityByProperty(BioneDeptInfo.class, "deptNo",
					// userInfo.getDeptNo());
					// 部门在不同的机构下面会有相同的部门编号，因此查询部门时应该结合机构一起查询 author huangye
					BioneDeptInfo dept = this.deptBS
							.findDeptInfoByOrgNoandDeptNo(userInfo.getOrgNo(),
									userInfo.getDeptNo());
					if (dept != null) {
						fieldsMap.put("deptName", dept.getDeptName());
					}
				}
			}
		}
		return fieldsMap;
	}

	/**
	 * 动态获取用户属性表单
	 */
	@RequestMapping("/generateUserForm.*")
	@ResponseBody
	public Map<String, Object> generateUserForm(String userId) {
		String defaultGroupicon = "images/classics/icons/communication.gif";
		String userIdTmp = userId;
		Map<String, Object> fieldsMap = new HashMap<String, Object>();
		List<CommonFormField> fields = new ArrayList<CommonFormField>();
		// 扩展属性list
		List<BioneUserAttr> extFields = new ArrayList<BioneUserAttr>();
		// 非扩展属性list
		List<BioneUserAttr> unextFields = new ArrayList<BioneUserAttr>();
		// 获取所有有效属性
		List<BioneUserAttrGrpVO> grpList = this.userBS.getAttrsAndGrps();
		if (grpList != null) {
			for (int i = 0; i < grpList.size(); i++) {
				BioneUserAttrGrpVO grpTmp = grpList.get(i);
				List<BioneUserAttr> attrsTmp = grpTmp.getAttrs();
				if (attrsTmp == null || attrsTmp.size() <= 0) {
					continue;
				}
				// 构建表单
				// 记录非隐藏组件数量
				int visibleCount = 0;
				for (int j = 0; j < attrsTmp.size(); j++) {
					BioneUserAttr aTmp = attrsTmp.get(j);
					// 将属性放入map
					if (GlobalConstants4frame.COMMON_STATUS_INVALID.equals(aTmp
							.getIsExt())) {
						// 如是非扩展属性
						if (!unextFields.contains(aTmp)) {
							unextFields.add(aTmp);
						}
					} else {
						// 扩展属性
						if (!extFields.contains(aTmp)) {
							extFields.add(aTmp);
						}
					}
					CommonFormField fTmp = new CommonFormField();
					if (visibleCount == 0
							&& !GlobalConstants4frame.BIONE_ATTR_ELEMENT_TYPE_HIDDEN
									.equals(aTmp.getElementType())) {
						// 若是一个分组的第一个非隐藏组件，加上分组相关属性
						fTmp.setGroup(grpTmp.getGrpName());
						String iconTmp = "";
						if (grpTmp.getGrpIcon() != null
								&& !"".equals(grpTmp.getGrpIcon())) {
							iconTmp = getContextPath() + "/"
									+ grpTmp.getGrpIcon();
						} else {
							iconTmp = getContextPath() + "/" + defaultGroupicon;
						}
						fTmp.setGroupicon(iconTmp);
					}
					// 隐藏域、下拉框和多行文本输入域得特殊处理
					if (GlobalConstants4frame.BIONE_ATTR_ELEMENT_TYPE_SELECT
							.equals(aTmp.getElementType())) {
						visibleCount++;
						// 若是下拉框
						fTmp.setDisplay(aTmp.getLabelName());
						fTmp.setName(aTmp.getFieldName());
						fTmp.setComboboxName(aTmp.getFieldName().concat("ID"));
						fTmp.setNewline(GlobalConstants4frame.COMMON_STATUS_INVALID
								.equals(aTmp.getIsNewline()) ? false : true);
						fTmp.setType("select");
						// 构造下拉框options
						Map<String, Object> optionsTmp = new HashMap<String, Object>();
						String combData = aTmp.getCombDs();
						JSONArray arrayTmp = null;
						if (combData != null) {
							try {
								arrayTmp = JSON.parseArray(combData);
							} catch (Exception e) {
								logger.warn("【用户管理】动态表单中，存在不合法的下拉框内容定义");
							}
							optionsTmp.put("data", arrayTmp == null ? ""
									: arrayTmp);
							if (aTmp.getInitValue() != ""
									&& !"".equals(aTmp.getInitValue())) {
								if (userIdTmp != null && !"".equals(userIdTmp)) {
									// 若是修改操作,查看该控件是否有设置值，若有，则不初始化initValue属性
									if (GlobalConstants4frame.COMMON_STATUS_INVALID
											.equals(aTmp.getIsExt())) {
										String fieldValue = this.userBS
												.getFieldValueByUserId(
														userIdTmp,
														aTmp.getFieldName());
										if (fieldValue == null
												|| "".equals(fieldValue)) {
											optionsTmp.put("initValue",
													aTmp.getInitValue());
										}
									} else {
										// 是扩展属性
										BioneUserAttrVal valTmp = this.userBS
												.getValByUserAndAttr(userIdTmp,
														aTmp.getAttrId());
										if (valTmp == null
												|| valTmp.getAttrValue() == null
												|| "".equals(valTmp
														.getAttrValue())) {
											optionsTmp.put("initValue",
													aTmp.getInitValue());
										}
									}
								} else {
									// 新增操作，直接初始化
									optionsTmp.put("initValue",
											aTmp.getInitValue());
								}
							}
						}

						fTmp.setOptions(optionsTmp);
					} else if (GlobalConstants4frame.BIONE_ATTR_ELEMENT_TYPE_HIDDEN
							.equals(aTmp.getElementType())) {
						// 若是隐藏域
						fTmp.setType("hidden");
						fTmp.setName(aTmp.getFieldName());
					} else if (GlobalConstants4frame.BIONE_ATTR_ELEMENT_TYPE_TEXTAREA
							.equals(aTmp.getElementType())) {
						visibleCount++;
						// 若是多行文本输入框
						fTmp.setDisplay(aTmp.getLabelName());
						fTmp.setName(aTmp.getFieldName());
						fTmp.setNewline(GlobalConstants4frame.COMMON_STATUS_INVALID
								.equals(aTmp.getIsNewline()) ? false : true);
						fTmp.setType("textarea");
						if (aTmp.getElementWidth() != null
								&& aTmp.getElementWidth() != BigDecimal.ZERO) {
							fTmp.setWidth(String.valueOf(aTmp.getElementWidth()));
						}
						Map<String, String> attrMap = new HashMap<String, String>();
						attrMap.put("style", "resize: none;");
						fTmp.setAttr(attrMap);
					} else if (GlobalConstants4frame.BIONE_ATTR_ELEMENT_TYPE_DATE
							.equals(aTmp.getElementType())) {
						visibleCount++;
						// 若是日期输入框
						fTmp.setDisplay(aTmp.getLabelName());
						fTmp.setName(aTmp.getFieldName());
						fTmp.setNewline(GlobalConstants4frame.COMMON_STATUS_INVALID
								.equals(aTmp.getIsNewline()) ? false : true);
						fTmp.setType("date");
					} else if (GlobalConstants4frame.BIONE_ATTR_ELEMENT_TYPE_PASSWORD
							.equals(aTmp.getElementType())) {
						visibleCount++;
						// 若是密码输入框
						fTmp.setDisplay(aTmp.getLabelName());
						fTmp.setName(aTmp.getFieldName());
						fTmp.setNewline(GlobalConstants4frame.COMMON_STATUS_INVALID
								.equals(aTmp.getIsNewline()) ? false : true);
						fTmp.setType("password");
					} else {
						visibleCount++;
						// 其他情况，default为文本域
						fTmp.setDisplay(aTmp.getLabelName());
						fTmp.setName(aTmp.getFieldName());
						fTmp.setNewline(GlobalConstants4frame.COMMON_STATUS_INVALID
								.equals(aTmp.getIsNewline()) ? false : true);
						fTmp.setType("text");
					}
					// 统一处理校验相关属性
					Map<String, Object> validateMap = new HashMap<String, Object>();
					if (aTmp.getIsAllowNull() != null) {
						if (GlobalConstants4frame.COMMON_STATUS_INVALID.equals(aTmp
								.getIsAllowNull())) {
							validateMap.put("required", true);
						}
					}
					if (aTmp.getFieldLength() != null
							&& !"".equals(aTmp.getFieldLength())
							&& !"0".equals(aTmp.getFieldLength())) {
						validateMap.put("maxlength", aTmp.getFieldLength());
					}
					fTmp.setValidate(validateMap);

					fields.add(fTmp);
				}
			}
		}
		fieldsMap.put("fields", fields);
		fieldsMap.put("extFields", extFields);
		fieldsMap.put("unextFields", unextFields);
		return fieldsMap;
	}

	@RequestMapping(value = "/getOrgName", method = RequestMethod.POST)
	@ResponseBody
	public String getOrgName(String paramValue) {
		String result = userBS.getOrgName(BioneSecurityUtils
				.getCurrentUserInfo().getCurrentLogicSysNo(), paramValue);
		if (result == null) {
			return "";
		} else {
			return result;
		}
	}

	@RequestMapping("/getDeptName")
	@ResponseBody
	public String getDeptName(String paramValue) {
		String result = userBS.getDeptName(BioneSecurityUtils
				.getCurrentUserInfo().getCurrentLogicSysNo(), paramValue);
		if (result == null) {
			return "";
		} else {
			return result;
		}
	}
	
	@RequestMapping(value = "/changeUserSts", method = RequestMethod.POST)
	public void changeUserSts(String userId, String sts) {
		if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(sts)) {
			this.userBS.changeUserSts(userId, sts);
		}
	}

	@RequestMapping(value = "/changeUserHeadImage", method = RequestMethod.POST)
	public void changeUserHeadImage(String curImageUrl) {
		if (StringUtils.isNotEmpty(curImageUrl)) {
			BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			this.userBS.changeUserHeadImage(userObj.getUserId(), curImageUrl);
		}
	}
	
	/**
	 * 更改是否管理者
	 * @param userId
	 * @param isManager
	 */
	@RequestMapping(value = "/changeIsManager", method = RequestMethod.POST)
	public void changeIsManager(String userId, String isManager) {
		if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(isManager)) {
			this.userBS.changeIsManager(userId, isManager);
		}
	}

	/***
	 * @方法描述: 用户名规范性验证，无法输入html标签等防止xss注入
	 * @创建人: miaokx@yusys.com.cn
	 * @创建时间: 2021/8/20 14:52
	 * @Param: userNo
	 * @return: boolean
	 */
	@RequestMapping("/userNameValid")
	@ResponseBody
	public boolean userNameValid(String userName) {
		String xssUserName = StringUtils2.striptXSS(userName);
		if (!userName.equals(xssUserName)) {
			return false;
		} else {
			return true;
		}
	}
}
