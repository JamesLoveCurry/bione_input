/**
 * 
 */
package com.yusys.biapp.input.security.authres;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.IAuthObject;

/**
 * <pre>
 * Title:获取用户，机构，角色信息的工具类，与认证类型无关
 * Description: 获取用户，机构，角色信息的工具类，与认证类型无关
 * </pre>
 * 
 * @author caijf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 * 
 * </pre>
 */
@Component(value = "authObjectUtils")
public class AuthObjectUtils {

	@Autowired
	private AuthBS authBS;

	// @Autowired
	// private UdipRoleBS udipRoleBS;
	//
	// @Autowired
	// private UdipUserBS udipUserBS;

	/**
	 * 获取角色信息
	 * 
	 * @return
	 */
//	public List<CommonTreeNode> getRoleList(String logicsysno) {
//		BioneLogicSysInfo sys = (BioneLogicSysInfo) logicSysBS.getEntityByProperty(BioneLogicSysInfo.class, "logicSysNo", logicsysno);
//		String objDefNo = "";
//		if (!GlobalConstants.AUTH_TYPE_LOCAL.equals((sys.getAuthTypeNo()))) {
//			// objDefNo = GlobalConstants.UA_AUTH_OBJ_DEF_ID_ROLE;
//			objDefNo = GlobalConstants.LOCAL_AUTH_OBJ_DEF_ID_ROLE;
//		} else {
//			objDefNo = GlobalConstants.AUTH_OBJ_DEF_ID_ROLE;
//		}
//		List<CommonTreeNode> pageShowTree = Lists.newArrayList();
//		if (objDefNo != null && !"".equals(objDefNo)) {
//			List<String> beanNames = authBS.findAuthObjBeanNameByType(objDefNo);
//			if (beanNames != null && beanNames.size() > 0) {
//				String beanName = beanNames.get(0);
//				IAuthObject authObj = SpringContextHolder.getBean(beanName);
//				if (authObj != null) {
//					pageShowTree = authObj.doGetAuthObjectInfo();
//				}
//			}
//		}
//		return pageShowTree;
//	}

	/**
	 * 获取机构信息
	 * 
	 * @return
	 */
	public List<CommonTreeNode> getOrgList(String logicsysno) {
//		BioneLogicSysInfo sys = (BioneLogicSysInfo) logicSysBS.getEntityByProperty(BioneLogicSysInfo.class, "logicSysNo", logicsysno);
		String objDefNo = "";
//		if (!GlobalConstants.AUTH_TYPE_LOCAL.equals((sys.getAuthTypeNo()))) {
//			// objDefNo = GlobalConstants.UA_AUTH_OBJ_DEF_ID_ORG;
//			objDefNo = GlobalConstants.LOCAL_AUTH_OBJ_DEF_ID_ORG;
//		} else {
		objDefNo = GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG;
//		}
		List<CommonTreeNode> pageShowTree = Lists.newArrayList();
		if (StringUtils.isNotBlank(objDefNo)) {
			List<String> beanNames = authBS.findAuthObjBeanNameByType(objDefNo);
			if (beanNames != null && beanNames.size() > 0) {
				String beanName = beanNames.get(0);
				IAuthObject authObj = SpringContextHolder.getBean(beanName);
				if (authObj != null) {
					pageShowTree = authObj.doGetAuthObjectInfo();
				}
			}
		}
		return pageShowTree;
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
//	public List<CommonTreeNode> getUserList(String logicsysno) {
//		BioneLogicSysInfo sys = (BioneLogicSysInfo) logicSysBS.getEntityByProperty(BioneLogicSysInfo.class, "logicSysNo", logicsysno);
//
//		String objDefNo = "";
//		if (!GlobalConstants.AUTH_TYPE_LOCAL.equals((sys.getAuthTypeNo()))) {
//			// objDefNo = GlobalConstants.UA_AUTH_OBJ_DEF_ID_USER;
//			objDefNo = GlobalConstants.LOCAL_AUTH_OBJ_DEF_ID_USER;
//		} else {
//			objDefNo = GlobalConstants.AUTH_OBJ_DEF_ID_USER;
//		}
//		List<CommonTreeNode> pageShowTree = Lists.newArrayList();
//		if (objDefNo != null && !"".equals(objDefNo)) {
//			List<String> beanNames = authBS.findAuthObjBeanNameByType(objDefNo);
//			if (beanNames != null && beanNames.size() > 0) {
//				String beanName = beanNames.get(0);
//				IAuthObject authObj = SpringContextHolder.getBean(beanName);
//				if (authObj != null) {
//					pageShowTree = authObj.doGetAuthObjectInfo();
//				}
//			}
//		}
//		return pageShowTree;
//	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
//	public List<CommonTreeNode> getUserListByRoleCode(String logicsysno, String roleCode) {
//		BioneLogicSysInfo sys = (BioneLogicSysInfo) logicSysBS.getEntityByProperty(BioneLogicSysInfo.class, "logicSysNo", logicsysno);
//		List<CommonTreeNode> nodes = Lists.newArrayList();
//		if (!GlobalConstants.AUTH_TYPE_LOCAL.equals((sys.getAuthTypeNo()))) {
//			try {
//				/*
//				 * UserAccountBean[] userAccountBean =
//				 * UaAuthServiceUtil.getUaAuthService
//				 * ().getUserInfoByRoleCode(roleCode);
//				 * if(userAccountBean!=null){ for(UserAccountBean user :
//				 * userAccountBean){ Map<String, String> paramMap = new
//				 * HashMap<String, String>(); CommonTreeNode node = new
//				 * CommonTreeNode(); node.setId(user.getUserName());
//				 * node.setText
//				 * (user.getUserTrueName()+"["+user.getUserName()+"]");
//				 * node.setData(user); paramMap.put("type", "user");
//				 * node.setParams(paramMap);
//				 * node.setIcon("images/udip/icons/user.png");
//				 * 
//				 * nodes.add(node); } }
//				 */
//				// 根据角色代码获取对应角色Id
//				String roleId = "";
//				List<UdipRoleInfo> list = this.udipRoleBS.getEntityListByProperty(UdipRoleInfo.class, "roleCode", roleCode);
//				if (list != null && list.size() > 0) {
//					UdipRoleInfo userInfo = list.get(0);
//					roleId = userInfo.getRoleId();
//				}
//				// 根据角色Id获取该角色包括的所有用户ID
//				List<UdipUserRoleInfo> roleUserList = this.udipRoleBS.getEntityListByProperty(UdipUserRoleInfo.class, "roleId", roleId);
//				for (UdipUserRoleInfo roleUser : roleUserList) {
//					// roleList.add(userRole.getRoleId());
//					UdipUserInfo user = this.udipUserBS.getEntityById(UdipUserInfo.class, roleUser.getUserId());
//					Map<String, String> paramMap = new HashMap<String, String>();
//					if (user != null) {
//						CommonTreeNode node = new CommonTreeNode();
//						node.setId(user.getUserName());
//						node.setText(user.getTrueName() + "[" + user.getUserName() + "]");
//						node.setData(user);
//						paramMap.put("type", "user");
//						node.setParams(paramMap);
//						node.setIcon("images/udip/icons/user.png");
//						nodes.add(node);
//					}
//				}
//			} catch (Exception e) {
//			}
//		}
//		return nodes;
//	}
}
