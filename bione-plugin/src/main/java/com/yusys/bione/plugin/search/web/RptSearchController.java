package com.yusys.bione.plugin.search.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;

@Controller
@RequestMapping("/rpt/frame/rptSearch")
public class RptSearchController extends BaseController{
	@RequestMapping("/highSearch")
	public ModelAndView highSearch(String searchObj) {
		searchObj = StringUtils2.javaScriptEncode(searchObj);
		return new ModelAndView("/plugin/search/high-search", "searchObj", searchObj);
	}
	
	@RequestMapping("/labelhighSearch")
	public ModelAndView labelhighSearch(String type) {
		type = StringUtils2.javaScriptEncode(type);
		return new ModelAndView("/plugin/search/label-high-search", "type", type);
	}
}
