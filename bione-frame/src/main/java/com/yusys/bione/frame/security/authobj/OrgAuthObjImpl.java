/**
 * 
 */
package com.yusys.bione.frame.security.authobj;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.SUPER_LOGIC_SYSTEM;

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

/**
 * <pre>
 * Title:机构授权对象实现类
 * Description: 授权对象接口的机构实现
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component
public class OrgAuthObjImpl implements IAuthObject {

	private String icon = "images/classics/icons/organ.gif";

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

		return GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#doGetAuthObjectInfo()
	 */

	public List<CommonTreeNode> doGetAuthObjectInfo(String... searchText) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		BioneLogicSysInfo logicInfo = this.authBS.getEntityByProperty(BioneLogicSysInfo.class, "logicSysNo", userObj.getCurrentLogicSysNo());
		String logicNo_org = userObj.getCurrentLogicSysNo();
		if (logicInfo != null && GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(logicInfo.getAuthTypeNo())) {
			// 若该系统使用基线机构
			logicNo_org = SUPER_LOGIC_SYSTEM;
		}
		// 是否使用分级授权
		boolean isHierarchicalAuth = BioneSecurityUtils.checkIsHierarchicalAuth(logicNo_org);
		List<BioneOrgInfo> orgInfoList = this.authBS.findValidAuthOrgOfUser(logicNo_org, isHierarchicalAuth);
		if (orgInfoList == null) {
			return nodes;
		}
		for (int i = 0; i < orgInfoList.size(); i++) {
			BioneOrgInfo org = orgInfoList.get(i);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			CommonTreeNode node = new CommonTreeNode();
			node.setId(org.getOrgNo());
			node.setText(org.getOrgName());
			node.setUpId(org.getUpNo());
			paramMap.put("id", org.getOrgId());
			paramMap.put("realNo", org.getOrgNo());
			node.setParams(paramMap);
			node.setData(org);
			node.setIcon(icon);
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

		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<String> authObjIdList = this.authBS.findValidAuthOrgIdOfUser(
				userObj.getCurrentLogicSysNo(), this.getAuthObjDefNo(),
				userObj.getUserId());

		return authObjIdList;
	}

	/* (non-Javadoc)
	 * @see com.yusys.bione.frame.security.IAuthObject#doGetAuthObjectInfoAsync(com.yusys.bione.comp.common.CommonTreeNode)
	 */
	@Override
	public List<CommonTreeNode> doGetAuthObjectInfoAsync(CommonTreeNode parentNode, String... searchText) {
		if (!StringUtils.isEmpty(parentNode.getId())) {
			String test = StringUtils.remove(parentNode.getId(), "AUTH_OBJ_ORG_");
			parentNode.setId(test);
		}
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		BioneLogicSysInfo logicInfo = this.authBS.getEntityByProperty(BioneLogicSysInfo.class, "logicSysNo", userObj.getCurrentLogicSysNo());
		String logicNo_org = userObj.getCurrentLogicSysNo();
		if (logicInfo != null && GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(logicInfo.getAuthTypeNo())) {
			// 若该系统使用基线机构
			logicNo_org = SUPER_LOGIC_SYSTEM;
		}
		List<BioneOrgInfo> orgInfoList = new ArrayList<BioneOrgInfo>();
		String jql = "select org from BioneOrgInfo org where org.logicSysNo=?0 ";
		boolean isHierarchicalAuth = true;
		if(userObj.isSuperUser()){ //超级管理员
			isHierarchicalAuth = false;
			jql = jql+" and org.upNo='"+parentNode.getId()+"' ";
			orgInfoList = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			List<String> isParent = new ArrayList<String>();
			if (searchText.length <= 0 || StringUtils.isEmpty(searchText[0])) {
				isParent = authBS.checkIsParent(parentNode.getId(), isHierarchicalAuth, logicNo_org);
			}
			for (int i = 0; i < orgInfoList.size(); i++) {
				BioneOrgInfo org = orgInfoList.get(i);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
				CommonTreeNode node = new CommonTreeNode();
				node.setId(org.getOrgNo());
				if (isParent.contains(org.getOrgNo())) {
					node.setIsParent(true);
				} else {
					node.setIsParent(false);
				}
				node.setText(org.getOrgName());
				if (searchText.length <= 0 || StringUtils.isEmpty(searchText[0])) {
					node.setUpId(org.getUpNo());
				} else {
					node.setUpId(CommonTreeNode.ROOT_ID);
				}
				paramMap.put("id", org.getOrgId());
				paramMap.put("realNo", org.getOrgNo());
				node.setParams(paramMap);
				node.setData(org);
				node.setIcon(icon);
				nodes.add(node);
			}
		}else if(!userObj.isSuperUser() && "Y".equals(userObj.getIsManager())){ //普通管理员
			isHierarchicalAuth = true;
			// 获取用户授权机构,将授权机构挂在到
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			if("0".equals(parentNode.getId())){
				jql = "select org from BioneOrgInfo org where 1=1 and org.logicSysNo = ?0 and org.orgSts = ?1 and org.orgNo in ?2";
				orgInfoList = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), GlobalConstants4frame.COMMON_STATUS_VALID, authOrgNos);
				for (int i = 0; i < orgInfoList.size(); i++) {
					BioneOrgInfo org = orgInfoList.get(i);
					if(!authOrgNos.contains(org.getUpNo())){
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
						CommonTreeNode node = new CommonTreeNode();
						node.setId(org.getOrgNo());
						node.setIsParent(true);
						node.setText(org.getOrgName());
						if (searchText.length <= 0 || StringUtils.isEmpty(searchText[0])) {
							node.setUpId(org.getUpNo());
						} else {
							node.setUpId(CommonTreeNode.ROOT_ID);
						}
						paramMap.put("id", org.getOrgId());
						paramMap.put("realNo", org.getOrgNo());
						node.setParams(paramMap);
						node.setData(org);
						node.setIcon(icon);
						nodes.add(node);
					}
				}
			}else{
				jql += " and org.upNo = ?1 order by org.orgNo";
				orgInfoList = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), parentNode.getId());
				List<String> isParent = new ArrayList<String>();
				if (searchText.length <= 0 || StringUtils.isEmpty(searchText[0])) {
					isParent = authBS.checkIsParent(parentNode.getId(), isHierarchicalAuth, logicNo_org);
				}
				for (int i = 0; i < orgInfoList.size(); i++) {
					BioneOrgInfo org = orgInfoList.get(i);
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
					CommonTreeNode node = new CommonTreeNode();
					node.setId(org.getOrgNo());
					if (isParent.contains(org.getOrgNo())) {
						node.setIsParent(true);
					} else {
						node.setIsParent(false);
					}
					node.setText(org.getOrgName());
					if (searchText.length <= 0 || StringUtils.isEmpty(searchText[0])) {
						node.setUpId(org.getUpNo());
					} else {
						node.setUpId(CommonTreeNode.ROOT_ID);
					}
					paramMap.put("id", org.getOrgId());
					paramMap.put("realNo", org.getOrgNo());
					node.setParams(paramMap);
					node.setData(org);
					node.setIcon(icon);
					nodes.add(node);
				}
			}
		}
		return nodes;
	}

	
	public String getAuthObjNameById(String id){
		List<BioneOrgInfo> list = this.authBS.getEntityListByProperty(BioneOrgInfo.class, "orgId", id);
		if(list != null && list.size() > 0){
			return list.get(0).getOrgName();
		}
		return "";
	}
}
