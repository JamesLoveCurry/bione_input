package com.yusys.bione.plugin.rptidx.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptidx.service.RptIdxCompGrpBS;

@Controller
@RequestMapping("/rpt/frame/idx/compgrp")
public class IdxCompGrpController extends BaseController {
	@Autowired
	private RptIdxCompGrpBS grpBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String indexNo) {
		indexNo = StringUtils2.javaScriptEncode(indexNo);
		return new ModelAndView("/plugin/rptidx/compgrp/idx-compgrp-edit", "indexNo", indexNo);
	}
	
	@RequestMapping("/getCompGrpIndex.*")
	@ResponseBody
	public Map<String, Object> getCompGrpIndex(String indexNo) {
		return this.grpBS.getCompGrpIndex(indexNo);
	}

	@RequestMapping("/saveCompGrp")
	@ResponseBody
	public Map<String, String> saveCompGrp(String ids,String indexNo){
		return this.grpBS.saveCompGrp(ids, indexNo);
	}
	
	@RequestMapping("/getGrpIndex")
	@ResponseBody
	public List<CommonTreeNode> getGrpIndex(String indexNo){
		return this.grpBS.getGrpIndex(indexNo);
	}
	
}
