package com.yusys.bione.frame.security.authobj;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.JPABaseDAO;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IAuthObject;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component
public class UserAuthObjImpl implements IAuthObject {

	private String icon_user = "images/classics/icons/user.gif";
	private String icon_org = "images/classics/icons/organ.gif";

	@Autowired
	private AuthBS authBS;
	
	@Autowired
	protected JPABaseDAO<T, Serializable> baseDAO;

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#getAuthObjDefNo()
	 */

	public String getAuthObjDefNo() {

		return AUTH_OBJ_DEF_ID_USER;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#doGetAuthObjectInfo()
	 */

	public List<CommonTreeNode> doGetAuthObjectInfo(String... searchText) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		if (GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(BioneSecurityUtils
				.getCurrentUserInfo().getAuthTypeNo())
				|| GlobalConstants4frame.AUTH_TYPE_LOCAL_CAS
						.equals(BioneSecurityUtils.getCurrentUserInfo()
								.getAuthTypeNo())) {
			// 若是超级逻辑系统或者是本地CAS认证系统，从超级逻辑系统中获取用户信息
			logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
		}
		boolean isHierarchicalAuth = BioneSecurityUtils
				.checkIsHierarchicalAuth(logicSysNo);
		List<BioneUserInfo> users = null;
		if (searchText.length > 0 && StringUtils.isNotEmpty(searchText[0])) {
			users = this.authBS.findValidAuthUserObjOfUser(logicSysNo,
					isHierarchicalAuth, searchText[0]);
		} else {
			users = this.authBS.findValidAuthUserObjOfUser(logicSysNo,
					isHierarchicalAuth);
		}
		if (users == null) {
			return nodes;
		}
		for (int i = 0; i < users.size(); i++) {
			BioneUserInfo user = users.get(i);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_USER);
			CommonTreeNode node = new CommonTreeNode();
			node.setId(user.getUserId());
			node.setText(user.getUserName() + "(账号：" + user.getUserNo() + ")");
			node.setData(user);
			paramMap.put("id", user.getUserId());
			paramMap.put("realNo", user.getUserNo());
			node.setParams(paramMap);
			node.setIcon(icon_user);
			nodes.add(node);
		}
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#doGetAuthObjectIdListOfUser()
	 */

	public List<String> doGetAuthObjectIdListOfUser() {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		if (BioneSecurityUtils.getCurrentUserInfo().getAuthTypeNo()
				.equals(GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER)) {
			logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
		}
		List<String> authUserIdList = this.authBS
				.findValidAuthUserIdOfUser(logicSysNo);

		return authUserIdList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yusys.bione.frame.security.IAuthObject#doGetAuthObjectInfoAsync
	 * (com.yusys.bione.comp.common.CommonTreeNode)
	 */
	@Override
	public List<CommonTreeNode> doGetAuthObjectInfoAsync(
			CommonTreeNode parentNode, String... searchText) {
		//带机构搜索查询
		if (searchText.length > 0) {
			return this.doGetAuthObjectInfo(searchText);
		}
		
		if(!StringUtils.isEmpty(parentNode.getId())){
			String test=parentNode.getId().replaceAll("AUTH_OBJ_ORG_", "");
			parentNode.setId(test);
		}
		
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		BioneLogicSysInfo logicInfo = this.authBS.getEntityByProperty(
				BioneLogicSysInfo.class, "logicSysNo",
				userObj.getCurrentLogicSysNo());
		String logicNo_org = userObj.getCurrentLogicSysNo();
		if (GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(logicInfo
				.getAuthTypeNo())) {
			// 若该系统使用基线机构
			logicNo_org = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
		}
		// 是否使用分级授权
		boolean isHierarchicalAuth = true;
		List<BioneOrgInfo> orgInfoList = new ArrayList<BioneOrgInfo>();
		String jql = "select org from BioneOrgInfo org where org.logicSysNo=?0 ";
		if(userObj.isSuperUser()){ //超级管理员
			isHierarchicalAuth = false;
			jql = jql+" and org.upNo='"+parentNode.getId()+"' ";
			orgInfoList = this.baseDAO.findWithIndexParam(jql, logicNo_org);
			for (int i = 0; i < orgInfoList.size(); i++) {
				BioneOrgInfo obj = orgInfoList.get(i);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
				CommonTreeNode node = new CommonTreeNode();
				node.setId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + obj.getOrgNo());
				node.setText(obj.getOrgName()+ "(机构号：" + obj.getOrgNo() + ")");
				if (CommonTreeNode.ROOT_ID.equals(obj.getUpNo())) {
					node.setUpId(obj.getUpNo());
				} else {
					node.setUpId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + obj.getUpNo());
				}
				node.setData(obj);
				node.setIsParent(true);
				paramMap.put("id", obj.getOrgId());
				paramMap.put("realId", obj.getOrgNo());
				// 有这个属性的节点不允许点击
				paramMap.put("cantClick", "1");
				node.setParams(paramMap);
				node.setIcon(icon_org);
				nodes.add(node);
			}
		}else if(!userObj.isSuperUser() && "Y".equals(userObj.getIsManager())){ //普通管理员
			isHierarchicalAuth = true;
			if("0".equals(parentNode.getId())){
				// 获取用户授权机构,将授权机构挂在到
				List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
				jql = "select org from BioneOrgInfo org where 1=1 and org.logicSysNo = ?0 and org.orgSts = ?1 and org.orgNo in ?2";
				orgInfoList = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), GlobalConstants4frame.COMMON_STATUS_VALID, authOrgNos);
				for (int i = 0; i < orgInfoList.size(); i++) {
					BioneOrgInfo obj = orgInfoList.get(i);
					if(!authOrgNos.contains(obj.getUpNo())){
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
						CommonTreeNode node = new CommonTreeNode();
						node.setId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + obj.getOrgNo());
						node.setText(obj.getOrgName()+ "(机构号：" + obj.getOrgNo() + ")");
						if (CommonTreeNode.ROOT_ID.equals(obj.getUpNo())) {
							node.setUpId(obj.getUpNo());
						} else {
							node.setUpId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + obj.getUpNo());
						}
						node.setData(obj);
						node.setIsParent(true);
						paramMap.put("id", obj.getOrgId());
						paramMap.put("realId", obj.getOrgNo());
						// 有这个属性的节点不允许点击
						paramMap.put("cantClick", "1");
						node.setParams(paramMap);
						node.setIcon(icon_org);
						nodes.add(node);
					}
				}
			}else{
				jql += " and org.upNo = ?1 order by org.orgNo";
				orgInfoList = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), parentNode.getId());
				for (int i = 0; i < orgInfoList.size(); i++) {
					BioneOrgInfo obj = orgInfoList.get(i);
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
					CommonTreeNode node = new CommonTreeNode();
					node.setId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + obj.getOrgNo());
					node.setText(obj.getOrgName()+ "(机构号：" + obj.getOrgNo() + ")");
					if (CommonTreeNode.ROOT_ID.equals(obj.getUpNo())) {
						node.setUpId(obj.getUpNo());
					} else {
						node.setUpId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + obj.getUpNo());
					}
					node.setData(obj);
					node.setIsParent(true);
					paramMap.put("id", obj.getOrgId());
					paramMap.put("realId", obj.getOrgNo());
					// 有这个属性的节点不允许点击
					paramMap.put("cantClick", "1");
					node.setParams(paramMap);
					node.setIcon(icon_org);
					nodes.add(node);
				}
			}
		}
		// 获取用户信息
		String logicNo = userObj.getCurrentLogicSysNo();
		if (logicInfo != null && GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(logicInfo.getAuthTypeNo())) {
			// 若该系统使用基线部门
			logicNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
		}
		String upOrgNo=parentNode.getId();
		if(StringUtils.isEmpty(upOrgNo)){
			if(isHierarchicalAuth){
				upOrgNo=BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
			}else{
				upOrgNo="0";
			}
		}	
		List<BioneUserInfo> userList = this.authBS.findValidAuthUserObjOfUser(logicNo, upOrgNo);
		if (userList == null) {
			return nodes;
		}
		for (int j = 0; j < userList.size(); j++) {
			BioneUserInfo user = userList.get(j);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_USER);
			CommonTreeNode node = new CommonTreeNode();
			node.setId(user.getUserId());
			node.setText(user.getUserName() + "(用户账号：" + user.getUserNo() + ")");
			if (CommonTreeNode.ROOT_ID.equals(user.getOrgNo())) {
				// 若是最上层机构
				node.setUpId(user.getOrgNo());
			} else {
				node.setUpId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + user.getOrgNo());
			}
			node.setData(user);
			paramMap.put("id", user.getUserId());
			paramMap.put("realNo", user.getUserNo());
			node.setParams(paramMap);
			node.setIcon(icon_user);
			nodes.add(node);
		}
		return nodes;
	}
	
	public String getAuthObjNameById(String id){
		List<BioneUserInfo> list = this.authBS.getEntityListByProperty(BioneUserInfo.class, "userId", id);
		if(list != null && list.size() > 0){
			return list.get(0).getUserName();
		}
		return "";
	}

}
