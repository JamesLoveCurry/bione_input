package com.yusys.biapp.input.supplement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.task.entity.RptTskCatalog;
import com.yusys.biapp.input.task.entity.RptTskInfo;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;

@Component
public class SupResImpl implements IResObject {
	
	@Autowired
	private SupDao supDao;
	
	public static final String AUTH_RES_SUPPLEMENT = "AUTH_RES_SUPPLEMENT";
	public static final String TASK_TYPE = "taskinfo";
	public static final String PARAM_TYPE_NO = "reportorgtype";

	public String getResObjDefNo() {
		return AUTH_RES_SUPPLEMENT;
	}

	public List<CommonTreeNode> doGetResInfo() {
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId(CommonTreeNode.ROOT_ID);
		treeNode.setText("根目录");
		treeNode.getParams().put("nodeType", "catalog");
		nodeList.add(treeNode);
		
		Map<String, Object> map = Maps.newHashMap();
//		map.put("parentCatalogId", CommonTreeNode.ROOT_ID);
        map.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
        map.put("objDefNo", "AUTH_OBJ_ROLE");
        map.put("resDefNo", "AUTH_RES_SUPPLEMENT");
        List<String> objRole = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get("AUTH_OBJ_ROLE");
        
        if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
            map.put("objIds", objRole);
        }
        
		List<RptTskCatalog> catalogList = supDao.getRptTskCatalog(map);
		for (RptTskCatalog catalog : catalogList) {
			Map<String, String> tableParam = new HashMap<String, String>();
			CommonTreeNode node = new CommonTreeNode();
			node.setId(catalog.getCatalogId());
			node.setText(catalog.getCatalogNm());
			node.setIsParent(true);
			node.setUpId(catalog.getUpNo());
			node.setIsexpand(false);
			node.getParams().put("nodeType", "catalog");
			node.setParams(tableParam);
			tableParam.put("realId", catalog.getCatalogId());
			tableParam.put("resType", GlobalConstants4frame.RES_TYPE_NODE);
			tableParam.put("resDefNo", AUTH_RES_SUPPLEMENT);
			tableParam.put("resId", catalog.getCatalogId());
			map.put("paramTypeNo", PARAM_TYPE_NO);
			nodeList.add(node);
		}
		
		Map<String, Object> resMap = Maps.newHashMap();
		resMap.put("paramTypeNo", PARAM_TYPE_NO);
		resMap.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		resMap.put("objDefNo", "AUTH_OBJ_ROLE");
		resMap.put("resDefNo", "AUTH_RES_SUPPLEMENT");
        List<String> objRoles = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get("AUTH_OBJ_ROLE");
        if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
        	resMap.put("objIds", objRoles);
        }
		List<RptTskInfo> rptTskInfoList = supDao.getTaskInfos(resMap);
		for (RptTskInfo info : rptTskInfoList) {
			Map<String, String> tableParam = new HashMap<String, String>();
			CommonTreeNode childNode = new CommonTreeNode();
			childNode.setId(info.getTaskId());
			childNode.setText(info.getTaskNm());
			childNode.setIsParent(false);
			childNode.getParams().put("nodeType", TASK_TYPE);
			childNode.setUpId(info.getCatalogId());
			childNode.setParams(tableParam);
			tableParam.put("resType", GlobalConstants4frame.RES_TYPE_NODE);
			tableParam.put("resDefNo", AUTH_RES_SUPPLEMENT);
			tableParam.put("resId", info.getTaskId());
			tableParam.put("realId", info.getTaskId());
			nodeList.add(childNode);
		}
		
		return nodeList;
	}

//	@Override
	public List<CommonTreeNode> doGetResInfo(List<String> ids) {
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId(CommonTreeNode.ROOT_ID);
		treeNode.setText("根目录");
		treeNode.setIsParent(true);
		treeNode.setUpId(null);
		treeNode.setIsexpand(true);
		treeNode.getParams().put("nodeType", "catalog");
		nodeList.add(treeNode);
		
		boolean isHierarchicalAuth = BioneSecurityUtils.checkIsHierarchicalAuth(userObj.getCurrentLogicSysNo());
		// 是否启用分级授权
		List<String> resIds = null;
		if(isHierarchicalAuth){
			resIds = BioneSecurityUtils.getResIdListOfUser(AUTH_RES_SUPPLEMENT);
		}
		if(resIds == null){
			resIds = new ArrayList<String>();
		}

		Map<String, Object> map = Maps.newHashMap();
		List<RptTskCatalog> catalogList = supDao.getRptTskCatalog(map);
		for (RptTskCatalog catalog : catalogList) {
			if(isHierarchicalAuth
					&& !resIds.contains(catalog)){
				// 若启用分级授权，且当前资源并不在当前用户权限内，则不展示
				continue;
			}
			CommonTreeNode node = new CommonTreeNode();
			node.setId(catalog.getCatalogId());
			node.setText(catalog.getCatalogNm());
			node.setIsParent(true);
			node.setUpId(catalog.getUpNo());
			node.setIsexpand(false);
			node.getParams().put("nodeType", "catalog");
			map.put("paramTypeNo", PARAM_TYPE_NO);
			nodeList.add(node);
			if(ids.contains(catalog.getCatalogId())){
			    node.getParams().put("checked", "Y");
			}
			else{
			    node.getParams().put("checked", "N");
			}
		}
		List<RptTskInfo> rptTskInfoList = supDao.getTaskInfos(map);
		for (RptTskInfo info : rptTskInfoList) {
			if(isHierarchicalAuth
					&& !resIds.contains(info)){
				// 若启用分级授权，且当前资源并不在当前用户权限内，则不展示
				continue;
			}
			CommonTreeNode childNode = new CommonTreeNode();
			childNode.setId(info.getTaskId());
			childNode.setText(info.getTaskNm());
			childNode.setIsParent(false);
			childNode.getParams().put("nodeType", TASK_TYPE);
			childNode.setUpId(info.getCatalogId());
			nodeList.add(childNode);
			
			if(ids.contains(info.getTaskId())){
			    childNode.getParams().put("checked", "Y");
			}
			else{
			    childNode.getParams().put("checked", "N");
			}
		}
		
		return nodeList;
	}

	public List<String> doGetResPermissions(
			List<BioneAuthObjResRel> authObjResRelList) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CommonTreeNode> doGetResOperateInfo(Long resId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CommonTreeNode> doGetResDataRuleInfo(Long resId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<BioneResOperInfo> findResOperList(String resDefNo,
			List<String> resIdList) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public List<String> initResIds(List<BioneAuthObjResRel> rels) {
		List<String> resIds = new ArrayList<String>();
		if(rels != null && rels.size()>0){
			for(BioneAuthObjResRel rel : rels){
				if(!GlobalConstants4frame.RES_TYPE_NODE.equals(rel.getResType())){
					resIds.add(rel.getId().getResId());
				}
			}
		}
		return resIds;

	}

//	@Override
	public String getAuthResNameById(String resId) {
		// TODO Auto-generated method stub
		return null;
	}

}
