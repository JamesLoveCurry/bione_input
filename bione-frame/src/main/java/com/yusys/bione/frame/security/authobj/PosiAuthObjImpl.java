package com.yusys.bione.frame.security.authobj;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.AUTH_OBJ_DEF_ID_POSI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.entity.BionePosiInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IAuthObject;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

@Component
public class PosiAuthObjImpl implements IAuthObject {

	private String icon_org = "images/classics/icons/organ.gif";
	private String icon_posi = "images/classics/icons/cur_activity_none.gif";

	@Autowired
	private AuthBS authBS;

	@Override
	public String getAuthObjDefNo() {
		return AUTH_OBJ_DEF_ID_POSI;
	}

	@Override
	public List<CommonTreeNode> doGetAuthObjectInfo(String... searchText) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		String logicNoOrg = userObj.getCurrentLogicSysNo();
		// 是否使用分级授权
		boolean isHierarchicalAuth = BioneSecurityUtils
				.checkIsHierarchicalAuth(logicNoOrg);
		// 获取机构信息
		List<BioneOrgInfo> orgInfoList = this.authBS.findValidAuthOrgOfUser(
				logicNoOrg, isHierarchicalAuth);
		if (orgInfoList == null) {
			return nodes;
		}
		for (int i = 0; i < orgInfoList.size(); i++) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_ORG);
			CommonTreeNode node = new CommonTreeNode();
			BioneOrgInfo obj = orgInfoList.get(i);
			node.setId(AUTH_OBJ_DEF_ID_ORG + "_" + obj.getOrgNo());
			node.setText(obj.getOrgName());
			if (CommonTreeNode.ROOT_ID.equals(obj.getUpNo())) {
				node.setUpId(obj.getUpNo());
			} else {
				node.setUpId(AUTH_OBJ_DEF_ID_ORG + "_" + obj.getUpNo());
			}
			node.setData(obj);
			paramMap.put("id", obj.getOrgId());
			paramMap.put("realId", obj.getOrgNo());
			// 有这个属性的节点不允许点击
			paramMap.put("cantClick", "1");
			node.setParams(paramMap);
			node.setIcon(icon_org);

			nodes.add(node);
		}

		// 将岗位信息只显示对应机构的岗位信息 --author huangye 同时添加对应的方法
		List<BionePosiInfo> posiInfoList = this.authBS
				.findValidAuthPosiOfUser(isHierarchicalAuth);
		if (posiInfoList == null || posiInfoList.size() == 0) {
			return nodes;
		}
		for (int i = 0; i < posiInfoList.size(); i++) {
			BionePosiInfo posi = posiInfoList.get(i);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_POSI);
			CommonTreeNode node = new CommonTreeNode();
			node.setId(posi.getPosiNo());
			node.setText(posi.getPosiName());
			node.setUpId(AUTH_OBJ_DEF_ID_ORG + "_" + posi.getOrgNo());
			node.setData(posi);
			paramMap.put("id", posi.getPosiId());
			paramMap.put("realNo", posi.getPosiNo());
			node.setParams(paramMap);
			node.setIcon(icon_posi);
			nodes.add(node);
		}
		return nodes;
	}

	@Override
	public List<String> doGetAuthObjectIdListOfUser() {
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<String> authObjIdList = this.authBS.findValidAuthPosiIdOfUser(
				userObj.getCurrentLogicSysNo(), this.getAuthObjDefNo(),
				userObj.getUserId());

		return authObjIdList;
	}

	public List<CommonTreeNode> doGetauthObjectInfoByUserId(String userId) {
		if (StringUtils.isBlank(userId)) {
			return this.doGetAuthObjectInfo();
		}
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		// 获取用户信息
		BioneUserInfo user = this.authBS.getEntityById(BioneUserInfo.class,
				userId);
		// 获取机构信息
		BioneOrgInfo org = this.authBS.getEntityByProperty(BioneOrgInfo.class,
				"orgNo", user.getOrgNo());

		if (org == null) {
			return nodes;
		}
		// 构造机构节点
		Map<String, String> orgMap = Maps.newHashMap();
		orgMap.put("objDefNo", AUTH_OBJ_DEF_ID_ORG);
		CommonTreeNode orgNode = new CommonTreeNode();
		orgNode.setId(AUTH_OBJ_DEF_ID_ORG + "_" + org.getOrgNo());
		orgNode.setText(org.getOrgName());
		orgNode.setUpId(AUTH_OBJ_DEF_ID_ORG + "_" + org.getUpNo());
		orgNode.setData(org);
		orgMap.put("id", org.getOrgId());
		orgMap.put("realId", org.getOrgNo());
		// 有这个属性的节点不允许点击
		orgMap.put("cantClick", "1");
		orgNode.setParams(orgMap);
		orgNode.setIcon(icon_org);
		nodes.add(orgNode);

		// 将岗位信息只显示对应机构的岗位信息 --author huangye 同时添加对应的方法
		List<BionePosiInfo> posiInfoList = this.authBS
				.findValidAuthPosiOfUser(org.getOrgNo());
		if (posiInfoList == null || posiInfoList.size() == 0) {
			return nodes;
		}
		for (int i = 0; i < posiInfoList.size(); i++) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_POSI);
			CommonTreeNode node = new CommonTreeNode();
			BionePosiInfo posi = posiInfoList.get(i);
			node.setId(posi.getPosiNo());
			node.setText(posi.getPosiName());
			node.setUpId(AUTH_OBJ_DEF_ID_ORG + "_" + posi.getOrgNo());
			node.setData(posi);
			paramMap.put("id", posi.getPosiId());
			paramMap.put("realNo", posi.getPosiNo());
			node.setParams(paramMap);
			node.setIcon(icon_posi);
			nodes.add(node);
		}
		return nodes;
	}

	/* (non-Javadoc)
	 * @see com.yusys.bione.frame.security.IAuthObject#doGetAuthObjectInfoAsync(com.yusys.bione.comp.common.CommonTreeNode)
	 */
	@Override
	public List<CommonTreeNode> doGetAuthObjectInfoAsync(
			CommonTreeNode parentNode, String... searchText) {
		if(!StringUtils.isEmpty(parentNode.getId())){
			String test = StringUtils.remove(parentNode.getId(), "AUTH_OBJ_ORG_");
			parentNode.setId(test);
		}
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		String logicNoOrg = userObj.getCurrentLogicSysNo();
		// 是否使用分级授权
		boolean isHierarchicalAuth = BioneSecurityUtils
				.checkIsHierarchicalAuth(logicNoOrg);
		// 获取机构信息
		List<BioneOrgInfo> orgInfoList = this.authBS.findValidAuthOrgOfUser(
				logicNoOrg, isHierarchicalAuth,parentNode.getId());
		if (orgInfoList == null) {
			return nodes;
		}
//		List<String>isParent=authBS.checkIsParent(parentNode.getId(), isHierarchicalAuth, logicNoOrg);

		for (int i = 0; i < orgInfoList.size(); i++) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_ORG);
			CommonTreeNode node = new CommonTreeNode();
			BioneOrgInfo obj = orgInfoList.get(i);
			node.setId(AUTH_OBJ_DEF_ID_ORG + "_" + obj.getOrgNo());
			node.setText(obj.getOrgName());
			if (CommonTreeNode.ROOT_ID.equals(obj.getUpNo())) {
				node.setUpId(obj.getUpNo());
			} else {
				node.setUpId(AUTH_OBJ_DEF_ID_ORG + "_" + obj.getUpNo());
			}
			node.setData(obj);
			paramMap.put("id", obj.getOrgId());
			paramMap.put("realId", obj.getOrgNo());
			// 有这个属性的节点不允许点击
			paramMap.put("cantClick", "1");
			node.setParams(paramMap);
			node.setIcon(icon_org);
			/*if(isParent.contains(obj.getOrgNo())){
				node.setIsParent(true);
			}
			else{
				node.setIsParent(false);
			}*/
			node.setIsParent(true);
			nodes.add(node);
		}

		String upOrgNo=parentNode.getId();
		if(StringUtils.isEmpty(upOrgNo)){
			if(isHierarchicalAuth){
				upOrgNo=BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
			}
			else{
				upOrgNo="0";
			}
			}	
		// 将岗位信息只显示对应机构的岗位信息 --author huangye 同时添加对应的方法
		List<BionePosiInfo> posiInfoList = this.authBS
				.findValidAuthPosiOfUser(isHierarchicalAuth,upOrgNo);
		if (posiInfoList == null || posiInfoList.size() == 0) {
			return nodes;
		}
		for (int i = 0; i < posiInfoList.size(); i++) {
			BionePosiInfo posi = posiInfoList.get(i);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_POSI);
			CommonTreeNode node = new CommonTreeNode();
			node.setId(posi.getPosiNo());
			node.setText(posi.getPosiName());
			node.setUpId(AUTH_OBJ_DEF_ID_ORG + "_" + posi.getOrgNo());
			node.setData(posi);
			paramMap.put("id", posi.getPosiId());
			paramMap.put("realNo", posi.getPosiNo());
			node.setParams(paramMap);
			node.setIcon(icon_posi);
			nodes.add(node);
		}
		return nodes;
	}

	@Override
    public String getAuthObjNameById(String id){
		List<BionePosiInfo> list = this.authBS.getEntityListByProperty(BionePosiInfo.class, "posiId", id);
		if(list != null && list.size() > 0){
			return list.get(0).getPosiName();
		}
		return "";
	}
}
