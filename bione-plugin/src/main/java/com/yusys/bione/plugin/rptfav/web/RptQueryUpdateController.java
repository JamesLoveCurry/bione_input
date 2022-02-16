package com.yusys.bione.plugin.rptfav.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolder;
import com.yusys.bione.plugin.rptfav.service.RptQueryUpdateBS;

@Controller
@RequestMapping("/rpt/frame/index/update")
public class RptQueryUpdateController extends BaseController{
	@Autowired
	private RptQueryUpdateBS rptQueryUpdateBS;
	
	@RequestMapping(value="updat")
	public ModelAndView index(String instanceId,String createTime) {
		ModelMap map = new ModelMap();
		map.put("instanceId", StringUtils2.javaScriptEncode(instanceId));
		map.put("createTime", StringUtils2.javaScriptEncode(createTime));
		String userId = BioneSecurityUtils.getCurrentUserId();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("instanceId", instanceId);
		params.put("userId", userId);
		List<RptFavFolder> list = this.rptQueryUpdateBS.findFolderNm(instanceId,userId);
		if(list != null && list.size() > 0){
			map.put("folderNm", StringUtils2.javaScriptEncode(list.get(0).getFolderNm()));
			map.put("folderId", StringUtils2.javaScriptEncode(list.get(0).getFolderId()));
		}
		return new ModelAndView("/plugin/rptfav/query-update-index",map);
	}
	
	@RequestMapping(value="edit")
	@ResponseBody
	public Map<String,Object> update(String instanceId,String queryNm,String userName,String createTime,String remark,String menu){
		Map<String,Object> returnMap = Maps.newHashMap();
		this.rptQueryUpdateBS.update(instanceId,queryNm,userName,createTime,remark,menu);
		return returnMap;
	}
	
	@RequestMapping(value = "come")
	public ModelAndView come(String folderId){
		folderId = StringUtils2.javaScriptEncode(folderId);
		return new ModelAndView("/plugin/rptfav/menu-edit-come", "folderId", folderId);
	}
	
	@RequestMapping(value="getInfo")
	@ResponseBody
	public List<CommonTreeNode> getInfo(String folderId){
		List<CommonTreeNode> childeNodeList = this.rptQueryUpdateBS.getTreeNode(getContextPath(),folderId);
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setIcon(this.getContextPath()+"/"+GlobalConstants4frame.ICON_URL+"/house.png");
		treeNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		treeNode.setText("全部");
		List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
		list.add(treeNode);
		list.addAll(childeNodeList);
		return list;
	}
}
