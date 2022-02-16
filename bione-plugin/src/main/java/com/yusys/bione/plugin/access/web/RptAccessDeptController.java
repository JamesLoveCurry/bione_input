package com.yusys.bione.plugin.access.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.access.service.RptAccessDeptBS;

/**
 * 
 * <pre>
 * Description:
 * </pre>
 * 
 * @author sunyuming sunym@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/accuser/dept")
public class RptAccessDeptController extends BaseController{
	
	@Autowired 
	private RptAccessDeptBS rptAccessDeptBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String orgNo){
		orgNo = StringUtils2.javaScriptEncode(orgNo);
		return new ModelAndView("/plugin/access/access-by-user-dept-index", "orgNo", orgNo);
	}
	
	@RequestMapping(value = "getTreeNode")
	@ResponseBody
	public List<CommonTreeNode> getTree(String orgNo){
		List<CommonTreeNode> list = this.rptAccessDeptBS.getTree(getContextPath(),orgNo);
		return list;
	}
}
