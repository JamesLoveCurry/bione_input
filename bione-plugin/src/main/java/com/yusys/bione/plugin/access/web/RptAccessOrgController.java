package com.yusys.bione.plugin.access.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.plugin.access.service.RptAccessOrgBS;

/**
 * 
 * <pre>
 * Description:
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/access/org")
public class RptAccessOrgController {
	@Autowired
	private RptAccessOrgBS orgBs;
	
	@RequestMapping(value = "/getOrgTree")
	@ResponseBody
	public List<CommonTreeNode> getOrgTree(String id){
		return this.orgBs.getOrgTree(id);
	}
	
	@RequestMapping(value = "/getOrgPage", method = RequestMethod.GET)
	public ModelAndView getOrgPage(){
		return new ModelAndView("/plugin/access/access-choose-org");
	}
}
