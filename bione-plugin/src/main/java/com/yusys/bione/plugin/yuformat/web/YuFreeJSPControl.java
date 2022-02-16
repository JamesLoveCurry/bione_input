package com.yusys.bione.plugin.yuformat.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yusys.bione.frame.base.web.BaseController;

/**
 * 配置路径直接跳转到某个jsp,比如【/frs/yufreejsp?jsp=/xch/xchfree.jsp】
 * 
 * @author kf0612
 *
 */
@Controller
@RequestMapping("/frs/yufreejsp")
public class YuFreeJSPControl extends BaseController {

	//@SuppressWarnings("unused")
	//private String contextPath = this.getContextPath(); // web路径

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		String str_jsp = this.getRequest().getParameter("jsp"); // 先从菜单入口得到编码
		if (str_jsp.endsWith(".jsp")) {
			str_jsp = str_jsp.substring(0, str_jsp.length() - 4); //
		}
		return str_jsp; //直接跳转支某个jsp,比如【/xch/xchfree】
	}

}
