package com.yusys.bione.frame.user.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.user.entity.BioneUserAttr;
import com.yusys.bione.frame.user.entity.BioneUserAttrGrp;
import com.yusys.bione.frame.user.service.UserAttrBS;
import com.yusys.bione.frame.user.service.UserAttrGrpBS;

@Controller
@RequestMapping("/bione/admin/userattrgrp")
public class UserAttrGrpController extends BaseController {

	@Autowired
	private UserAttrGrpBS userAttrGrpBS;
	@Autowired
	private UserAttrBS userAttrBS;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/user/user-attr-grp-index";
	}

	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(String grpId, Pager pager) {
		Map<String, Object> rowData = Maps.newHashMap();
		if (grpId != null) {
			SearchResult<BioneUserAttr> page = userAttrBS.getAttrPageByGrpId(
					pager.getPageFirstIndex(), pager.getPagesize(),
					pager.getSortname(), pager.getSortorder(),
					pager.getSearchCondition(), grpId);
			rowData.put("Rows", page.getResult());
			rowData.put("Total", page.getTotalCount());
		} else {
			SearchResult<BioneUserAttr> page = userAttrBS.getUserAttrInfoList(
					pager.getPageFirstIndex(), pager.getPagesize(),
					pager.getSortname(), pager.getSortorder(),
					pager.getSearchCondition());
			rowData.put("Rows", page.getResult());
			rowData.put("Total", page.getTotalCount());
		}
		return rowData;

	}

	@RequestMapping("/listgrps.*")
	@ResponseBody
	public List<CommonTreeNode> listgrps(String id) {
		List<CommonTreeNode> grps = userAttrGrpBS.getGrpTree();
		return grps;
	}

	@RequestMapping("/info.*")
	@ResponseBody
	public BioneUserAttrGrp info(String id) {
		BioneUserAttrGrp model = userAttrGrpBS.getEntityById(id);
		return model;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public boolean destroy(@PathVariable("id") String id) {
		if (id != null) {
			userAttrGrpBS.deleteAttrGrpById(id);
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew(String t, String grpId) {
		if ("attr".equals(t)) {
			grpId = StringUtils2.javaScriptEncode(grpId);
			return new ModelAndView("/frame/user/user-attr-grp-userattredit",
					"grpId", grpId);
		}
		return new ModelAndView("/frame/user/user-attr-grp-editgrp");
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id, String t) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		if ("attr".equals(t)) {
			BioneUserAttr attr = userAttrBS.getEntityById(id);
			if (attr != null) {
				if (GlobalConstants4frame.COMMON_STATUS_INVALID.equals(attr
						.getIsAllowUpdate())) {
					map.put("update", "not");
				}
			}
			return new ModelAndView("/frame/user/user-attr-grp-userattredit", map);
		}
		return new ModelAndView("/frame/user/user-attr-grp-editgrp", map);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneUserAttrGrp model) {
		if (model.getGrpId() == null || model.getGrpId().equals("")) {
			model.setGrpId(RandomUtils.uuid2());
		}
		userAttrGrpBS.updateEntity(model);
	}

	@RequestMapping(value = "/userattr", method = RequestMethod.GET)
	public String userattr() {
		return "/frame/user/user-attr-grp-userattr";
	}

	/**
	 * 验证属性名称
	 */
	@RequestMapping(value = "/checkAttrName", method = RequestMethod.POST)
	@ResponseBody
	public boolean checkAttrName(String attrId, String fieldName) {
		String attrIdTmp = attrId;
		List<BioneUserAttr> attrs = this.userAttrBS.getAttrsByFieldName(
				fieldName, attrIdTmp);
		boolean flag = false;
		if (attrs != null && attrs.size() > 0) {
			if (attrs.get(0).getFieldName().equals(fieldName)) {
				flag = true;
			}
		}
		if (flag) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 验证分组名称
	 */
	@RequestMapping(value = "/checkGrpName", method = RequestMethod.POST)
	@ResponseBody
	public boolean checkGrpName(String grpId, String grpName) {
		String grpIdTmp = grpId;
		List<BioneUserAttrGrp> grps = this.userAttrBS.getGrpsByGrpName(grpName,
				grpIdTmp);
		boolean flag = false;
		if (grps != null && grps.size() > 0) {
			if (grps.get(0).getGrpName().equals(grpName)) {
				flag = true;
			}
		}
		if (flag) {
			return false;
		} else {
			return true;
		}
	}

}
