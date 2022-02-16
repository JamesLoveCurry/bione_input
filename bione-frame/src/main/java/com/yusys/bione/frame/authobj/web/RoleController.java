package com.yusys.bione.frame.authobj.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.auth.service.AuthObjBS;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfoExt;
import com.yusys.bione.frame.authobj.service.RoleBS;
import com.yusys.bione.frame.authobj.util.BioneAuthObjNotifier;
import com.yusys.bione.frame.authobj.web.vo.BioneRoleInfoExtVO;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.common.OrgInfoHolder;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;

@Controller
@RequestMapping("/bione/admin/role")
public class RoleController extends BaseController {
	@Autowired
	private RoleBS roleBS;
	@Autowired
	private AuthBS authBS;
	@Autowired
	private AuthObjBS authObjBS;
	@Autowired
	private UserBS userBS;
	@Autowired
	private BioneAuthObjNotifier authObjNotifier;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		ModelMap mm = new ModelMap();
		mm.put("isSuperUser", BioneSecurityUtils.getCurrentUserInfo().isSuperUser());
		mm.put("isManager", BioneSecurityUtils.getCurrentUserInfo().getIsManager());
		mm.put("userId", BioneSecurityUtils.getCurrentUserInfo().getUserId());
		return new ModelAndView("/frame/role/role-index", mm);
	}

	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		SearchResult<BioneRoleInfo> searchResult = roleBS.getRoleList(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> roleMap = new HashMap<String, Object>();
		roleMap.put("Rows", searchResult.getResult());
		roleMap.put("Total", searchResult.getTotalCount());
		return roleMap;
	}

	/**
	 * 查看角色授权用户关系
	 *
	 * @return 视图跳转
	 */
	@RequestMapping(value = "/{id}/roleUserRel", method = RequestMethod.GET)
	public ModelAndView roleUserRel(@PathVariable("id") String id) {
		logger.info("查看角色授权用户关系ID为：" + id);
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/role/role-user-relation", "id", id);
	}

	/**
	 * 查看角色授权资源关系
	 *
	 * @return 视图跳转
	 */
	@RequestMapping(value = "/{id}/roleResource", method = RequestMethod.GET)
	public ModelAndView roleResource(@PathVariable("id") String id) {
		logger.info("查看角色授权资源关系ID为：" + id);
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/role/role-resource-relation", "id", id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneRoleInfo model) {
		logger.info("保存的帐号是:" + model.getRoleId());
		model.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		model.setLastUpdateTime(new Timestamp(new Date().getTime()));
		
		String lastUpdateUser = model.getLastUpdateUser();
		if (StringUtils.isNotBlank(lastUpdateUser)) {
			model.setLastUpdateUser(lastUpdateUser);
		} else {
			model.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
		}
		// 角色名称后追加当前用户所属机构
		String roleName = model.getRoleName();
		if (StringUtils.isNotBlank(roleName)) {
			model.setRoleName(roleName + "_" + BioneSecurityUtils.getCurrentUserInfo().getOrgName());
		}
		
		if(model.getRoleId()==null||model.getRoleId().equals("")){
			model.setRoleId(RandomUtils.uuid2());
		}
		
		roleBS.updateEntity(model);
		authObjNotifier.alterRoleNotify(BioneAuthObjNotifier.OP_ADD, model);
	}

	/**
	 * 保存&推送
	 * @param model
	 */
	@RequestMapping(value = "/addPush", method = RequestMethod.POST)
	public void createAndPush(String formObj) {
		logger.info("保存的帐号是:" + formObj);
		JSONObject sObj = JSON.parseObject(formObj);
		BioneRoleInfo roleTmp = sObj.toJavaObject(BioneRoleInfo.class);
		roleTmp.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		roleTmp.setLastUpdateTime(new Timestamp(new Date().getTime()));
		
		String lastUpdateUser = roleTmp.getLastUpdateUser();
		if (StringUtils.isNotBlank(lastUpdateUser)) {
			roleTmp.setLastUpdateUser(lastUpdateUser);
		} else {
			roleTmp.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
		}
		// 角色名称后追加当前用户所属机构
		String roleName = roleTmp.getRoleName();
		if (StringUtils.isNotBlank(roleName)) {
			roleTmp.setRoleName(roleName + "_" + BioneSecurityUtils.getCurrentUserInfo().getOrgName());
		}
		
		if(roleTmp.getRoleId() == null || roleTmp.getRoleId().equals("")){
			roleTmp.setRoleId(RandomUtils.uuid2());
		}
		
		roleBS.updateEntity(roleTmp);
		authObjNotifier.alterRoleNotify(BioneAuthObjNotifier.OP_ADD, roleTmp);
		
		// 同步推送
		List<BioneUserInfo> userList = this.userBS.getUserList("Y");
		if (userList != null && userList.size() > 0) {
			for (BioneUserInfo userInfo : userList) {
				// 判断是否存在，如果存在，则跳过
				boolean flag = this.roleBS.checkIsRoleNoExist(roleTmp.getRoleNo(), userInfo.getUserId());
				// flag=true，不存在
				if (flag) {
					// 根据机构号获取机构名称
					String orgName = OrgInfoHolder.getOrgName(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), userInfo.getOrgNo());
					
					roleTmp.setRoleName(roleName + "_" + orgName);
					roleTmp.setLastUpdateTime(new Timestamp(new Date().getTime()));
					roleTmp.setLastUpdateUser(userInfo.getUserId());
					roleTmp.setRoleId(RandomUtils.uuid2());
					
					roleBS.updateEntity(roleTmp);
					authObjNotifier.alterRoleNotify(BioneAuthObjNotifier.OP_ADD, roleTmp);
				}
			}
		}
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew() {
		boolean isSuperUser = BioneSecurityUtils.getCurrentUserInfo().isSuperUser();
		return new ModelAndView("/frame/role/role-editNew", "isSuperUser", isSuperUser);
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		logger.info("修改的角色ID为：" + id);
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/role/role-edit", "id", id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneRoleInfo show(@PathVariable("id") String id) {
		return roleBS.getEntityById(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void destroy(@PathVariable("id") String id) {
		roleBS.removeEntityById(id);
	}

	@RequestMapping("/destroyOwn.*")
	@ResponseBody
	public Map<String, String> destroyOwn(String ids, boolean type) {
		Map<String,String> msgMap=Maps.newHashMap();
		if (ids != null && !"".equals(ids)) {
			String[] idArray = StringUtils.split(ids, ',');
			String[] idAllArray = {};
			
			//首先判断选中的角色信息是否与资源信息关联，或者包含用户。只要有一个角色信息有这种情况，全部打回不允许删除。
			//--author huangye
			boolean flag=false;
			for(String id:idArray){
				boolean flags=this.authBS.checkIsObjBeUsedByUser(id, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
				if(flags){
					flag=true;
					break;
				}
			}
			if(flag){
				msgMap.put("msg","1");
				return msgMap;
			}
			for(String id:idArray){
				boolean flags=this.authObjBS.checkIsObjBeUsedByRes(id, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
				if(flags){
					flag=true;
					break;
				}
			}
			if(flag){
				msgMap.put("msg", "2");
				return msgMap;
			}
			
			// 如果是超级管理员，选择级联删除
			if (type) {
				List<BioneRoleInfo> listAll = new ArrayList<BioneRoleInfo>();
				for (String id : idArray){
					BioneRoleInfo roleInfo = roleBS.findUniqueEntityByProperty("roleId", id);
					String roleNo = roleInfo.getRoleNo();
					List<BioneRoleInfo> list = roleBS.getRoleByRoleNo(roleNo);
					listAll.addAll(list);
				}
				
				if (listAll != null && listAll.size() > 0) {
					idAllArray = new String[listAll.size()];
					for (int i = 0; i<listAll.size(); i++) {
						idAllArray[i] = listAll.get(i).getRoleId();
					}
					
					this.roleBS.deleteRolesByIds(idAllArray);
				}
			}

			this.roleBS.deleteRolesByIds(idArray);
			BioneRoleInfo roleInfo = new BioneRoleInfo();
			for (String id : idArray) {
				roleInfo.setRoleId(id);
				authObjNotifier.alterRoleNotify(BioneAuthObjNotifier.OP_REMOVE, roleInfo);
			}
			
			for (String id : idAllArray) {
				roleInfo.setRoleId(id);
				authObjNotifier.alterRoleNotify(BioneAuthObjNotifier.OP_REMOVE, roleInfo);
			}
			
			msgMap.put("msg","0");
			
			return msgMap;
		}
		
		return msgMap;
	}
	
	@RequestMapping("/testRoleNo")
	@ResponseBody
	public boolean testRoleNo(String roleNo) {
		return this.roleBS.checkIsRoleNoExist(roleNo);
	}
	
	@RequestMapping(value = "/changeRoleSts", method = RequestMethod.POST)
	public void changeRoleSts(String roleId, String sts) {
		if (StringUtils.isNotEmpty(roleId) && StringUtils.isNotEmpty(sts)) {
			this.roleBS.changeRoleSts(roleId, sts);
		}
	}

	@RequestMapping("/getBioneRoleInfoExt")
	@ResponseBody
	public  List<BioneRoleInfoExtVO> getBioneRoleInfoExt(){
		  List<BioneRoleInfoExt> list = roleBS.getBioneRoleInfoExt();
		  List<BioneRoleInfoExtVO> roleList = new ArrayList<BioneRoleInfoExtVO>();
		  for(BioneRoleInfoExt roles: list){
			  BioneRoleInfoExtVO role = new BioneRoleInfoExtVO();
			  role.setId(roles.getCode());
			  role.setText(roles.getCodeDesc());
			  roleList.add(role);
		  }
		return roleList;
	}
}
