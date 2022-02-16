package com.yusys.bione.frame.user.web;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.service.OrgTreeBS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Title:CRUD操作演示
 * Description: 完成用户信息表的CRUD操作 
 * </pre>
 * @author xuguangyuan  xuguangyuansh@gmail.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：许广源		  修改日期:     修改内容: 
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/orgtree")
public class OrgTreeController extends BaseController {

	@Autowired
	private OrgTreeBS orgTreeBS;

	/**
	 * 部门显示视树，初始方法
	 * 
	 * @return 
	 * 		RestFul的增强结果类型
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/org/org-tree-index";
	}
	/**
	 * 部门显示视树，初始方法
	 * 
	 * @return 
	 * 		RestFul的增强结果类型
	 */
	@RequestMapping("asyncOrgTree")
	public ModelAndView asyncOrgTree() {
		return new ModelAndView("/frame/org/org-asyuc-tree",
				"objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
	}

	/**
	 * 获取机构树信息
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public List<CommonTreeNode> list(Pager pager) {
		List<CommonTreeNode> list = orgTreeBS.buildOrgTree(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo(), true, pager.getSearchCondition());
		return list;
	}
	
	@RequestMapping("/getOrgTree")
	@ResponseBody
	public List<CommonTreeNode> getOrgTree(String id, String search){
		return this.orgTreeBS.getOrgTree(id, search);
	}

	/**
	 * 获取机构树
	 * @return
	 */
	@RequestMapping("/getAllOrgTree")
	@ResponseBody
	public Map<String,Object> getAllOrgTree(){
		Map<String,Object> map = new HashMap<>();
		map.put("treeInfo", this.orgTreeBS.getAllOrgTree());
		map.put("treeData", this.orgTreeBS.getAllOrgData());
		return map;
	}
}
