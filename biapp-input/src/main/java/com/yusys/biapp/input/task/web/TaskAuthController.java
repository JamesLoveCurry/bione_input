package com.yusys.biapp.input.task.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yusys.biapp.input.task.service.TaskAuthBS;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRelPK;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.auth.service.AuthUsrRelBS;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;

@Controller
@RequestMapping("/rpt/input/taskauth")
public class TaskAuthController extends BaseController {

	@Autowired
	private TaskAuthBS taskAuthBS;

	@Autowired
	private AuthBS authBS;
	@Autowired
	private AuthUsrRelBS relBS;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		List<BioneAuthObjDef> authObjDefList = this.authBS.getAuthObjDefBySys(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		// 将授权对象定义传至页面
		return new ModelAndView("/input/task/task-auth-index", "authObjDefs", 
				StringUtils2.javaScriptEncode(JSON.toJSONString(authObjDefList)));
	}

	

	// 获取指定授权对象树
	@RequestMapping("/getAuthObjTree.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthObjTree(String objDefNo, CommonTreeNode node) {
		List<CommonTreeNode>pageShowTree = taskAuthBS.getAuthObjTree();
		if (pageShowTree != null) {
			for (int i = 0; i < pageShowTree.size(); i++) {
				(pageShowTree.get(i)).setIcon(this.getRequest()
						.getContextPath()
						+ "/"
						+ (pageShowTree.get(i)).getIcon());
			}
		}
		return pageShowTree;
	}
	@RequestMapping("/getAuthObjDefTree.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthObjDefTree(String objDefNo, CommonTreeNode node, String searchText,String type,String selectedText,String selectedId,String isyg) {
		return taskAuthBS.getUserTreeNode(objDefNo,type,searchText,selectedText,selectedId,isyg);
	}
	// 获取用户与授权对象关系
	@RequestMapping("/getObjUserRel.*")
	@ResponseBody
	public Object getObjUserRel(String objId) {
		if (objId != null && !"".equals(objId)) {
			Object[] objs = new Object[2];
			BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
			List<Map<String,Object>> userInfos = taskAuthBS.getUserInfoByOrg(userInfo.getOrgNo());
			List<String>users=Lists.newArrayList();
			for(Map<String,Object> map : userInfos){
				users.add((String)map.get("USER_ID"));
			}
			List<BioneAuthObjUserRel> relList =  this.relBS.getObjUserRelByObjId(
					userInfo.getCurrentLogicSysNo(), objId);
			objs[0] = relList;
			objs[1] = users;
			return objs;
		}
		return null;
	}
	// 保存对象用户关系
	@RequestMapping("/saveObjUserRel")
	public void saveObjUserRel(String relObjs) {
		if (relObjs != null && !"".equals(relObjs)) {
			// 当前用户信息
			BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			JSONObject rels = JSON.parseObject(relObjs);
			String objDefNo = rels.getString("objDefNo");
			String objId = rels.getString("objId");
			String userIds = rels.getString("userIds");
			String unSelectedObjIds = rels.getString("unObjs");
			// 新关系集合
			List<BioneAuthObjUserRel> newRels = new ArrayList<BioneAuthObjUserRel>();
			List<String>oldList = 	Lists.newArrayList();
			for (String  userId : StringUtils.split(unSelectedObjIds, ','))
				oldList.add(userId);
			// 获取旧关系
			List<BioneAuthObjUserRel> oldRels = this.relBS.getObjUserRelByIds(objId, oldList);
			
			for (String  userId : StringUtils.split(userIds, ',')) {
				BioneAuthObjUserRel newRel = new BioneAuthObjUserRel();
				BioneAuthObjUserRelPK pk = new BioneAuthObjUserRelPK();
				pk.setLogicSysNo(userObj.getCurrentLogicSysNo());
				pk.setObjDefNo(objDefNo);
				pk.setUserId(userId);
				pk.setObjId(objId);
				newRel.setId(pk);
				
				newRels.add(newRel);
			}
			
			
			/************添加日志记录**********/
//			StringBuilder buff = new StringBuilder();
//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			buff.append("用户[").append(user.getLoginName()).append("]修改了");
//			BioneRoleInfo roleInfo = roleBS.getEntityById(objId);
//			buff.append(roleInfo.getRoleName()).append("的权限,授权的用户为");
//			boolean isFirst=true;
//			List<BioneUserInfo>userList=userBS.getUserInofByIds(Arrays.asList(userIds.split(",")));
//			for (BioneUserInfo  selectedUser :userList) 
//			{
//				if(isFirst)
//					isFirst=false;
//				else
//					buff.append(",");
//				buff.append("[").append(selectedUser.getUserName()).append(",").append(selectedUser.getUserNo()).append("]");
//			}
//			buff.append(",未选中的用户为:");
//			isFirst=true;
//			List<BioneUserInfo>unSelecteduUserList=userBS.getUserInofByIds(oldList);
//			for (BioneUserInfo  unselectedUser :unSelecteduUserList) 
//			{
//				if(isFirst)
//					isFirst=false;
//				else
//					buff.append(",");
//				buff.append("[").append(unselectedUser.getUserName()).append(",").append(unselectedUser.getUserNo()).append("]");
//			}
//			buff.append(",其它用户未做变更");
//			relBS.saveLog("03", "补录授权", buff.toString(), user.getUserId(), user.getLoginName());
			
			this.relBS.updateRelBatch(oldRels, newRels, null);
		}
	}
}
