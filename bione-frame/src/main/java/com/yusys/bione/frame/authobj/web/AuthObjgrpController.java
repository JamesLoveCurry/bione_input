package com.yusys.bione.frame.authobj.web;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.authobj.entity.BioneAuthObjgrpInfo;
import com.yusys.bione.frame.authobj.service.AuthObjgrpBS;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:部门处理Action类
 * Description: 实现部门视图中对应的增删改查功能，以及树形图展示。
 * </pre>
 * 
 * @author huangye huangye@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/authObjgrp")
public class AuthObjgrpController extends BaseController {

	@Autowired
	private AuthObjgrpBS authObjgrpBS;

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void destroy(@PathVariable("id") String id) {
		this.authObjgrpBS.removeEntityBatch(id);
	}

	@RequestMapping("/destroyOwn")
	public void destroyOwn(String delIds) {
		if (delIds != null && !"".equals(delIds)) {
			this.authObjgrpBS.removeEntityBatch(delIds);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/authObjGrp/auth-objgrp-index";
	}

	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		SearchResult<BioneAuthObjgrpInfo> searchResult = this.authObjgrpBS.getObjgrpInfoList(pager.getPageFirstIndex(),
				pager.getPagesize(), pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> authObjgrpMap = Maps.newHashMap();
		authObjgrpMap.put("Rows", searchResult.getResult());
		authObjgrpMap.put("Total", searchResult.getTotalCount());
		return authObjgrpMap;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/authObjGrp/auth-objgrp-editNew";
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/authObjGrp/auth-objgrp-edit", "id", id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneAuthObjgrpInfo objgrpInfo) {
		objgrpInfo.setLastUpdateTime(new Timestamp(new Date().getTime()));
		objgrpInfo.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
		objgrpInfo.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		objgrpInfo.setLastUpdateTime(new Timestamp(new Date().getTime()));
		if(objgrpInfo.getObjgrpId()==null||objgrpInfo.getObjgrpId().equals("")){
			objgrpInfo.setObjgrpId(RandomUtils.uuid2());
		}
		this.authObjgrpBS.updateEntity(objgrpInfo);
	}

	@RequestMapping("/testObjgrpNo")
	@ResponseBody
	public boolean testObjgrpNo(String objgrpNo) {
		return this.authObjgrpBS.checkIsObjgrpNoExist(objgrpNo);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneAuthObjgrpInfo show(@PathVariable("id") String id) {
		return this.authObjgrpBS.getEntityById(id);
	}

	
	@RequestMapping(value = "/changeGrpSts", method = RequestMethod.POST)
	public void changeUserSts(String objgrpId, String sts) {
		if (StringUtils.isNotEmpty(objgrpId) && StringUtils.isNotEmpty(sts)) {
			this.authObjgrpBS.changeGrpSts(objgrpId, sts);
		}
	}
}
