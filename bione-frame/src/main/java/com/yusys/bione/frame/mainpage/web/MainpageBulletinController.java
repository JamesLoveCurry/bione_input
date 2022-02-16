package com.yusys.bione.frame.mainpage.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.frame.base.web.BaseController;

@Controller
@RequestMapping("/bione/mainpage/bulletin")
public class MainpageBulletinController extends BaseController {
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest req) {
		return new ModelAndView("/frame/mainpage/mainpage-m-exmaple");
	}
}
