package com.yusys.bione.plugin.yuformat.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yusys.bione.frame.base.web.BaseController;

/**
 * 直接使用js文件,jsp是一个通用的jsp,比如【/frs/yufreejs?js=/yujs/east/ruletype.js】
 * 
 * @author kf0612
 *
 */
@Controller
@RequestMapping("/frs/yufreejs")
public class YuFreeJSControl extends BaseController {

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		//?js=的参数会自动传入jsp的,因为这是服务器端跳转!
		return "/yuformatjsp/freejsp"; //直接跳转支某个jsp,比如【/xch/xchfree】+ "?js=" + str_jsfile
	}

}
