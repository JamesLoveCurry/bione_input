package com.yusys.bione.frame.user.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.service.DeptTreeBS;

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
@RequestMapping("/bione/admin/depttree")
public class DeptTreeController extends BaseController {
	@Autowired
	private DeptTreeBS deptTreeBS;

	/**
	 * 部门显示视树，初始方法
	 * 
	 * @return 
	 * 		RestFul的增强结果类型
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/dept/dept-tree-index", "id", id);
	}

	/**
	 * 获取条线树信息
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public List<CommonTreeNode> list(Pager pager, String orgNo) {
		if (orgNo != null) {
			List<CommonTreeNode> list = deptTreeBS.buildDeptTree(BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo(), true, pager.getSearchCondition(), orgNo);
			return list;
		}
		return null;
	}
	
	@RequestMapping("/getDeptTree")
	@ResponseBody
	public List<CommonTreeNode> getDeptTree(String id, String search, String orgNo){
		if(orgNo != null){
			return this.deptTreeBS.getDeptTree(id, search, orgNo);
		}
		return null;
	}

}
