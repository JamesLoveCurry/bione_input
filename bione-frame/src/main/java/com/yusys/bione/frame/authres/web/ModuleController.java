package com.yusys.bione.frame.authres.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.authres.entity.BioneModuleInfo;
import com.yusys.bione.frame.authres.service.ModuleBS;
import com.yusys.bione.frame.base.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Title:CRUD操作演示
 * Description: 完成用户信息表的CRUD操作 
 * </pre>
 * @author xuguangyuan  xuguangyuansh@gmail.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：许广源		  修改日期:     修改内容: 
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/module")
public class ModuleController extends BaseController {
	@Autowired
	private ModuleBS moduleBS;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/module/module-index";
	}

	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf) {
		SearchResult<BioneModuleInfo> searchResult = moduleBS.getModuleList(rf.getPageFirstIndex(), rf.getPagesize(),
				rf.getSortname(), rf.getSortorder(), rf.getSearchCondition());
		Map<String, Object> moduleMap = Maps.newHashMap();
		moduleMap.put("Rows", searchResult.getResult());
		moduleMap.put("Total", searchResult.getTotalCount());
		return moduleMap;
	}

	/**
	 * 用于添加，或修改时的保存对象
	 */
	// POST /module/
	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneModuleInfo model) {
		if(model.getModuleId()==null||model.getModuleId().equals("")){
			model.setModuleId(RandomUtils.uuid2());
		}
		moduleBS.updateEntity(model);
	}

	/**
	 * 根据ID，加载数据
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneModuleInfo show(@PathVariable("id") String id) {
		BioneModuleInfo model = (BioneModuleInfo) this.moduleBS.getEntityById(id);
		return model;
	}

	/**
	 * 执行修改前的页面跳转
	 * 
	 * @return
	 * 		String	用于匹配结果页面
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/module/module-edit", "id", id);
	}

	/**
	 * 执行添加前的页面跳转
	 * 
	 * @return
	 * 		String	用于匹配结果页面
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/module/module-edit";
	}

	/**
	 * 执行删除操作，可进行批量删除
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String destroy(@PathVariable("id") String id) {
		String[] ids = StringUtils.split(id, ',');
		List<String> moduleNameList = Lists.newArrayList();
		for (String delid : ids) {
			String str = this.moduleBS.findUsedModuleName(delid);
			if (!StringUtils.isEmpty(str)) {
				moduleNameList.add(str);
				continue;
			}
			this.moduleBS.removeModuleById(id);
		}
		if (!moduleNameList.isEmpty()) {
			return StringUtils.join(moduleNameList, ", ");
		}
		return "";
	}

	/**
	 * 表单验证中的后台验证，验证模块标识是否已存在
	 */
	@RequestMapping("/moduleNoValid")
	@ResponseBody
	public boolean moduleNoValid(String moduleNo) {
		BioneModuleInfo model = moduleBS.findUniqueEntityByProperty("moduleNo", moduleNo);
		if (model != null)
			return false;
		else
			return true;
	}

	/**
	 * @方法描述: 复制模块跳转页面
	 * @创建人: huzq1 
	 * @创建时间: 2021/7/7 9:55
	  * @param 
	 * @return
	 **/
	@RequestMapping(value = "/copy", method = RequestMethod.GET)
	public String moduleCopy() {
		return "/frame/module/module-copy";
	}

	/*
	 * @方法描述: 获取监管类型
	 * @创建人: huzq1 
	 * @创建时间: 2021/7/7 14:07
	  * @param 
	 * @return
	 **/
	@ResponseBody
	@RequestMapping("/getOrgType")
	public List<CommonComboBoxNode> getOrgType(){
		return moduleBS.getOrgType();
	}

	/**
	 * @方法描述: 复制模块
	 * @创建人: huzq1 
	 * @创建时间: 2021/7/7 9:55
	  * @param params
	 * @return
	 **/
	@ResponseBody
	@RequestMapping("/copyModule")
	public Map<String, Object> copyModule(String  orgType, String funcType, String moduleName, String moduleNo, String remark){
		return moduleBS.copyModule(orgType,funcType,moduleName,moduleNo,remark);
	}
}
