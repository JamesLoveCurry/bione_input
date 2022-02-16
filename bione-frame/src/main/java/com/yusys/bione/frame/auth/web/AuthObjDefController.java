package com.yusys.bione.frame.auth.web;

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
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.service.AuthObjDefBS;
import com.yusys.bione.frame.base.web.BaseController;

/**
 * <pre>
 * Title:CRUD操作演示
 * Description: 完成用户信息表的CRUD操作
 * </pre>
 * 
 * @author xuguangyuan xuguangyuansh@gmail.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：许广源		  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/authObjDef")
public class AuthObjDefController extends BaseController {
	@Autowired
	private AuthObjDefBS authObjDefBS;

	// GET /menu
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/authObjDef/auth-obj-def-index";
	}

	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf) {
		SearchResult<BioneAuthObjDef> searchResult = this.authObjDefBS.getAuthObjDefList(rf);
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", searchResult.getResult());
		objDefMap.put("Total", searchResult.getTotalCount());
		return objDefMap;
	}

	/**
	 * 用于添加，或修改时的保存对象
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneAuthObjDef model) {
		this.authObjDefBS.saveOrUpdateEntity(model);
	}

	/**
	 * 根据ID，加载数据
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneAuthObjDef show(@PathVariable("id") String id) {
		BioneAuthObjDef model = this.authObjDefBS.getEntityById(id);
		return model;
	}

	/**
	 * 执行修改前的数据加载
	 * 
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/authObjDef/auth-obj-def-edit";
	}

	/**
	 * 执行添加前页面跳转
	 * 
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/authObjDef/auth-obj-def-edit", "id", id);
	}

	/**
	 * 执行删除操作，可进行指删除
	 */
	// DELETE /admin/id
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String destroy(@PathVariable("id") String id) {
		String[] ids = StringUtils.split(id, ',');
		if (ids.length > 1) {
			this.authObjDefBS.removeEntityByProperty("objDefNo", id);
		} else {
			this.authObjDefBS.removeEntityById(id);
		}
		return "true";
	}

	/**
	 * 表单验证中的后台验证，验证模块标识是否已存在
	 */
	@RequestMapping("/objDefNoValid")
	@ResponseBody
	public String objDefNoValid(String objDefNo) {
		BioneAuthObjDef model = authObjDefBS.findUniqueEntityByProperty("objDefNo", objDefNo);
		if (model != null)
			return "false";
		else
			return "true";
	}

}
