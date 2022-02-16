package com.yusys.bione.plugin.rptorg.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.plugin.rptorg.service.RptOrgSumRelBS;
/**
 * <pre>
 * Description: 功能描述
 * </pre>
 * @author sunyuming  sunym@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容: 
 * </pre>
 */

@Controller
@RequestMapping("/rpt/frame/rptorgsum")
public class RptOrgSumRelController {
	
	@Autowired
	private RptOrgSumRelBS rptOrgSumRelBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/rptorg/rpt-org-sum-index";
	}
	
	@RequestMapping(value="/getNewTree" , method = RequestMethod.GET)
	public ModelAndView getNewTree(String orgType,String orgNo){
		ModelMap model = new ModelMap();
		model.put("orgType", StringUtils2.javaScriptEncode(orgType));
		model.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		return new ModelAndView("/plugin/rptorg/rpt-org-sum-grid",model);
	}
	
	@RequestMapping(value="findCheck" ,method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> findCheck(String orgType,String orgNo){
		return this.rptOrgSumRelBS.findCheck(orgType,orgNo);
	}
	
	@RequestMapping(value="saveCollect",method=RequestMethod.POST)
	@ResponseBody
	public String saveCollect(String checkedAll,String orgNo,String orgType,boolean cascade){
		String save = this.rptOrgSumRelBS.saveInfo(checkedAll,orgNo,orgType,cascade);
		return save;
	}
}
