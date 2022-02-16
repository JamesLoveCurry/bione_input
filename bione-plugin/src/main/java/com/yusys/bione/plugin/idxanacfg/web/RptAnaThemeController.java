package com.yusys.bione.plugin.idxanacfg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaThemeBS;

@Controller
@RequestMapping("/cabin/analysis/theme")
public class RptAnaThemeController {
	
	@Autowired
	private RptAnaThemeBS anaThemeBS;
	
	/**
	 * 跳转指标组树弹出页
	 * 
	 * */
	@RequestMapping(value = "/themeTree")
	public String themeTree() {
		return "/plugin/idxanacfg/idx-analysis-themeTree";
	}
	
	/**
	 * 获取主题树
	 * 
	 * */
	@RequestMapping(value = "/getTheTree")
	@ResponseBody
	public List<CommonTreeNode> getTheTree() {
		return this.anaThemeBS.getTheTree();
	}
}
