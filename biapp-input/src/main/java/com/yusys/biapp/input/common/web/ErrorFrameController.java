package com.yusys.biapp.input.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yusys.bione.frame.base.web.BaseController;

@Controller
@RequestMapping("/rpt/input/errorFrame")
public class ErrorFrameController extends BaseController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/input/common/errorFrame";
	}
	
}
