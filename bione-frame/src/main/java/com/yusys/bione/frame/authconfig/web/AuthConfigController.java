package com.yusys.bione.frame.authconfig.web;

import java.util.Map;

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
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfo;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.base.web.BaseController;

/**
 * 
 * <pre>
 *     Title:
 *     Description:
 * </pre>
 * 
 * @author yunlei yunlei@yunchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 *  修改记录
 *  修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/authConfig")
public class AuthConfigController extends BaseController {

	@Autowired
	private AuthConfigBS authConfigBS;

	/**
	 * 认证管理页
	 * 
	 * @return
	 */
	@RequestMapping
	public String index() {
		return "/frame/authConfig/authConfig-index";
	}

	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> getList(Pager rf) {
		Map<String, Object> moduleMap = Maps.newHashMap();
		SearchResult<BioneAuthInfo> searchResult = authConfigBS.findResults(rf.getPageFirstIndex(), rf.getPagesize(),
				rf.getSortname(), rf.getSortorder(), rf.getSearchCondition());
		moduleMap.put("Rows", searchResult.getResult());
		moduleMap.put("Total", searchResult.getTotalCount());
		return moduleMap;
	}

	/**
	 * 跳转 新增
	 * 
	 * @return
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/authConfig/authConfig-edit";
	}

	/**
	 * 跳转修改
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/authConfig/authConfig-edit", "id", id);
	}

	/**
	 * 修改。加载页面
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneAuthInfo show(@PathVariable("id") String id) {
		BioneAuthInfo authInfo = authConfigBS.getEntityById(BioneAuthInfo.class, id);
		return authInfo;
	}

	/**
	 * 保存
	 * 
	 * @param authInfo
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneAuthInfo authInfo) {
		if (authInfo.getAuthTypeNo() == null || "".equals(authInfo.getAuthTypeNo())) {
			authInfo.setAuthTypeNo(RandomUtils.uuid2());
		}
		authConfigBS.updateEntity(authInfo);
	}

	/**
	 * 跳转授权资源实现类配置
	 * 
	 * @param authInfo
	 * @return
	 */
	@RequestMapping(value = "/getResInfo/{authTypeNo}")
	public ModelAndView getResInfo(@PathVariable("authTypeNo") String authTypeNo) {
		authTypeNo = StringUtils2.javaScriptEncode(authTypeNo);
		return new ModelAndView("/frame/authConfig/authConfig-res", "authTypeNo", authTypeNo);
	}

	/**
	 * 跳转授权对象实现类配置
	 * 
	 * @param authInfo
	 * @return
	 */
	@RequestMapping(value = "/getObjInfo/{authTypeNo}")
	public ModelAndView getObjInfo(@PathVariable("authTypeNo") String authTypeNo) {
		authTypeNo = StringUtils2.javaScriptEncode(authTypeNo);
		return new ModelAndView("/frame/authConfig/authConfig-obj", "authTypeNo", authTypeNo);
	}

}
