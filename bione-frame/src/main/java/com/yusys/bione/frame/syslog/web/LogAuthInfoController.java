package com.yusys.bione.frame.syslog.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.syslog.entity.BioneLogAuthDetail;
import com.yusys.bione.frame.syslog.service.LogAuthInfoBS;

@Controller
@RequestMapping("/bione/syslog/auth")
public class LogAuthInfoController extends BaseController {
	@Autowired
	private LogAuthInfoBS authBS;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/frame/access/log-auth-index");
	}

	@RequestMapping(value = "/list")
	@ResponseBody
	public Map<String, Object> list(Pager pager, String userName, String startDate, String endDate) {
		return  this.authBS.list(
				pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSearchCondition(), pager.getSortname(),
				pager.getSortorder(), userName, startDate, endDate);
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public ModelAndView detail(String logId, String authObjNo){
		Map<String, String> map = new HashMap<String, String>();
		map.put("logId", StringUtils2.javaScriptEncode(logId));
		map.put("authObjNo", StringUtils2.javaScriptEncode(authObjNo));
		return new ModelAndView("/frame/access/log-auth-detail", map);
	}
	
	@RequestMapping(value = "/detailList")
	@ResponseBody
	public List<BioneLogAuthDetail> detailList(String logId){
		return this.authBS.detailList(logId);
	}
}
