package com.yusys.biapp.input.task.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.greenpineyu.fel.common.StringUtils;
import com.yusys.biapp.input.task.repository.TaskAuthDao;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;

@Service
@Transactional(readOnly = true)
public class TaskAuthBS extends BaseBS<Object> {
	

	@Autowired
	private TaskAuthDao taskAuthDao;
	
	
	public List<Map<String,Object>>getUserInfoByOrg(String orgNo){
		List<String>orgList=Lists.newArrayList();
		orgList.add(orgNo);
		Map<String,Object>map=Maps.newHashMap();
		map.put("childOrgList", orgList);
		List<Map<String,Object>> userInfos = taskAuthDao.getUserInfoByOrgs(map);
		return userInfos;
	}
	
	public List<CommonTreeNode> getUserTreeNode(String objDefNo,String type,String searchText,String selectedText,String selectedId,String isyg){
		BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
		List<String>childOrgList=Lists.newArrayList();
		if(type==null){
			if(selectedText.equals("业务管理员"))
				type="1";
		}
		//补录填报员和补录审批员
		if(type!=null&&type.equals("1"))
		{	
			//业务管理员
			getChildrenOrg(childOrgList,userInfo.getOrgNo(),false);
		}else{
			childOrgList.add(userInfo.getOrgNo());
			getChildrenOrg(childOrgList,userInfo.getOrgNo(),true);
		}
		if(childOrgList.size()!=0)
		{
			List<CommonTreeNode>nodes=Lists.newArrayList();
			Map<String,Object>map=Maps.newHashMap();
			if(StringUtils.isNotEmpty(searchText))
				map.put("searchText", "%"+searchText+"%");
			map.put("childOrgList", childOrgList);
			if(StringUtils.isNotEmpty(isyg))
			map.put("isyg", isyg);
			List<Map<String,Object>> userInfos = taskAuthDao.getUserInfoByOrgs(map);
			for(Map<String,Object> info : userInfos){
				if(info.get("USER_NO").equals("bione_super")
					||info.get("USER_NO").equals("admin"))
					continue;
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER);
				CommonTreeNode node = new CommonTreeNode();
				
				node.setId((String)info.get("USER_ID"));
				//node.setText((String)info.get("USER_NAME")+"("+(String)info.get("ORG_NAME")+")");
				node.setText((String)info.get("USER_NAME")+"("+(String)info.get("USER_NO")+")");
				node.setData(info);
				paramMap.put("realNo", (String)info.get("USER_NO"));
				paramMap.put("id", (String)info.get("USER_ID"));
				node.setParams(paramMap);
				node.setIcon(icon);

				nodes.add(node);
			}
			return nodes;
		}
		return null;
	}
	
	public void getChildrenOrg(List<String>childOrgList,String orgNo,boolean allChild){
		List<String>orgList = taskAuthDao.getChildOrgs(orgNo);
		if(orgList==null||orgList.size()==0)
			return;
		for(String childOrgNo:orgList){
			childOrgList.add(childOrgNo);
			if(allChild)
				getChildrenOrg(childOrgList,childOrgNo,allChild);
		}
	}
	
	private String icon = "images/classics/icons/role.gif";
	
	
	public List<CommonTreeNode>getAuthObjTree(){
		
		List<String>list=Lists.newArrayList();
		list.add("补录填报员");
		list.add("补录审批员");
		BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
		if(userInfo.getOrgNo().equals("817000000")){
			list.add("业务管理员");
		}
		List<BioneRoleInfo> roleList = taskAuthDao.getTaskAuthRole(list);
		
		List<CommonTreeNode>nodes=Lists.newArrayList();
		for (int i = 0; i < roleList.size(); i++) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
			CommonTreeNode node = new CommonTreeNode();
			BioneRoleInfo role = roleList.get(i);
			node.setId(role.getRoleId());
			node.setText(role.getRoleName());
			node.setData(role);
			paramMap.put("realNo", role.getRoleNo());
			paramMap.put("id", role.getRoleId());
			node.setParams(paramMap);
			node.setIcon(icon);

			nodes.add(node);
		}
		return nodes;
	}
}
