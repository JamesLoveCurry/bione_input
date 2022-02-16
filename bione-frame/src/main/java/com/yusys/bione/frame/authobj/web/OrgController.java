package com.yusys.bione.frame.authobj.web;

import java.sql.Timestamp;
import java.util.Date;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.auth.service.AuthObjBS;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.service.DeptBS;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.service.UserBS;

/**
 * 
 * <pre>
 * Title:监管系统机构管理Action
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author yangyf1
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */

@Controller
@RequestMapping("/bione/admin/org")
public class OrgController extends BaseController {
	@Autowired
	private OrgBS orgBS;
	@Autowired
	private DeptBS deptBS;

	@Autowired
	private UserBS userBS;
	@Autowired
	private AuthBS authBS;
	@Autowired
	private AuthObjBS authObjBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/frame/org/org-index");
	}

	@RequestMapping("/list.*")
	@ResponseBody
	public List<CommonTreeNode> list(String nodeId) {
		List<CommonTreeNode> orgTreeList = orgBS.getOrgTree(nodeId);
		return orgTreeList;
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{id}/up", method = RequestMethod.GET)
	@ResponseBody
	public BioneOrgInfo showUp(@PathVariable("id") String id) {
		BioneOrgInfo orgInfo = this.orgBS.getEntityById(id);
		return this.orgBS.findOrgInfoByOrgNo(orgInfo.getUpNo());
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/show.*", method = RequestMethod.GET)
	@ResponseBody
	public BioneOrgInfo show(String id) {
		return this.orgBS.getEntityById(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneOrgInfo model) {
		model.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		model.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
		model.setLastUpdateTime(new Timestamp(new Date().getTime()));
		if (model.getOrgId() != null) {
			List<BioneOrgInfo> orgList = Lists.newArrayList();
			this.orgBS.findLowerOrgInfosByOrgId(model.getOrgNo(), orgList);
			for (BioneOrgInfo org : orgList) {
				org.setOrgSts(model.getOrgSts());
				updateDeptStsByOrgInfo(org);
				orgBS.updateEntity(org);
			}
		}
		if(model.getOrgId()==null||model.getOrgId().equals("")){
			model.setOrgId(model.getOrgNo());
		}
		String namespace = "/"+model.getOrgNo()+"/";
		if(!StringUtils.isEmpty(model.getUpNo()) && !CommonTreeNode.ROOT_ID.equals(model.getUpNo())){
			BioneOrgInfo upOrg = this.orgBS.getEntityByProperty(BioneOrgInfo.class, "orgNo", model.getUpNo());
			if(upOrg != null){
				if(StringUtils.isNotBlank(upOrg.getNamespace())) {
					String endString = upOrg.getNamespace().substring(upOrg.getNamespace().length() - 1);
					if("/".equals(endString)) {
						namespace = upOrg.getNamespace().substring(0, upOrg.getNamespace().length() - 1) + namespace;
					}else {
						namespace = upOrg.getNamespace()+namespace;
					}
				}
			}
		}
		model.setNamespace(namespace);
		orgBS.updateEntity(model);
		updateDeptStsByOrgInfo(model);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew(String orgNo,String upName){
		ModelMap mm = new ModelMap();
		mm.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		return new ModelAndView("/frame/org/org-editNew", mm);
	}
	
	@RequestMapping(value = "/{deptId}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("deptId") String deptId,String orgNo,String upName,String orgName) {
		ModelMap mm=new ModelMap();
		mm.put("id", StringUtils2.javaScriptEncode(deptId));
		mm.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		mm.put("orgName", StringUtils2.javaScriptEncode(orgName));
		return new ModelAndView("/frame/org/org-edit", mm);
	}
	
	private void updateDeptStsByOrgInfo(BioneOrgInfo org) {
		List<BioneDeptInfo> deptList = deptBS.findDeptInfoByOrg(null, "", org.getOrgNo());
		for (BioneDeptInfo dept : deptList) {
			dept.setDeptSts(org.getOrgSts());
			deptBS.updateEntity(dept);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public Map<String,String> destroy(@PathVariable("id") String id) {
		Map<String,String> resultMap=Maps.newHashMap();
		boolean flag=this.userBS.checkIsOrgBeUsedByUser(id);
		if(flag){
			resultMap.put("msg", "1");
			return resultMap;
		}
		flag=this.authObjBS.checkIsObjBeUsedByRes(id, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
		if(flag){
			resultMap.put("msg", "2");
			return resultMap;
		}
		flag=this.authBS.checkIsObjBeUsedByUser(id, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
		if(flag){
			resultMap.put("msg", "3");
			return resultMap;
		}
		this.orgBS.removeOrgAndDept(id);
		resultMap.put("msg", "0");
		return resultMap;
	}

	@RequestMapping(value = "/testOrgNo")
	@ResponseBody
	public boolean testOrgNo(String orgNo) {
		BioneOrgInfo org = this.orgBS.findOrgInfoByOrgNo(orgNo);
		if (org != null) {
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/testOrgNm")
	@ResponseBody
	public boolean testOrgNm(String orgName, String oldOrgNm) {
		if(StringUtils.isNotBlank(oldOrgNm) && oldOrgNm.equals(orgName)) {
			return true;
		}
		List<BioneOrgInfo> org = this.orgBS.findOrgInfoByOrgNm(orgName);
		if ((org != null) && org.size() > 0) {
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/getOrgTree")
	@ResponseBody
	public List<CommonTreeNode> getOrgTree(String upId){
		return this.orgBS.getOrgTreeTemplate(upId, this.getContextPath());
	}
	
}