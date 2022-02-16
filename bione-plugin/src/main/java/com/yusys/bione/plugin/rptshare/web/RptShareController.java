package com.yusys.bione.plugin.rptshare.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.plugin.rptshare.service.RptShareBS;

@Controller
@RequestMapping("rpt/frame/rptmgr/share")
public class RptShareController extends BaseController {
	
	@Autowired
	private RptShareBS shareBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView(
				"/plugin/share/share-index");
	}
	
	@RequestMapping(value = "mineinfo", method = RequestMethod.GET)
	public ModelAndView mine() {
		return new ModelAndView(
				"/plugin/share/share-info-mine");
	}
	
	@RequestMapping(value = "otherinfo", method = RequestMethod.GET)
	public ModelAndView other() {
		return new ModelAndView(
				"/plugin/share/share-info-other");
	}
	
	@RequestMapping(value = "config" , method = RequestMethod.GET)
	public ModelAndView config(String id,String objType) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("objType", StringUtils2.javaScriptEncode(objType));
		return new ModelAndView(
				"/plugin/share/share-config",map);
	}
	
	@RequestMapping(value="user",method = RequestMethod.GET)
	public ModelAndView user() {
		return new ModelAndView(
				"/plugin/share/share-user");
	}
	
	@RequestMapping(value="userlist",method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> userlist(String searchNm) {
		return this.shareBS.userlist(searchNm,this.getContextPath());
	}
	
	@RequestMapping(value="userllist",method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> userllist(String searchNm) {
		return this.shareBS.userllist(searchNm,this.getContextPath());
	}
	
	@RequestMapping(value="usermlist",method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> usermlist(String searchNm) {
		return this.shareBS.usermlist(searchNm,this.getContextPath());
	}
	
	@RequestMapping(value="saveshare",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveshare(String ids,String id,String name,String remark,String objType){
		Map<String,Object> result = new HashMap<String, Object>();
		this.shareBS.saveshare(ids, remark, id, name, objType);
		return result;
	}
	
	@RequestMapping(value="minelist",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> minelist(Pager pager){
		return this.shareBS.minelist(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
	}
	
	@RequestMapping(value="otherlist",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> otherlist(Pager pager){
		return this.shareBS.otherlist(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
	}
	
	@RequestMapping(value = "/changeShareSts", method = RequestMethod.POST)
	public void changeShareSts(String shareId, String sts ,String userId, String rptName) {
		if (StringUtils.isNotEmpty(shareId) && StringUtils.isNotEmpty(sts)) {
			this.shareBS.changeShareStsAndSaveMsg(shareId, sts, userId,rptName);
		}
	}
	
	@RequestMapping(value="getUserInfo",method = RequestMethod.POST)
	@ResponseBody
	public List<BioneUserInfo> getUserInfo(){
		return this.shareBS.getUserInfo();
	}
	

	
}
