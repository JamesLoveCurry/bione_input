/**
 * 
 */
package com.yusys.bione.frame.mainpage.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mainpage.entity.BioneMpModuleInfo;
import com.yusys.bione.frame.mainpage.service.MainpageModuleBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:基础首页相关维护功能controller
 * Description: 基础首页相关维护功能controller
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/mainpage/module")
public class MainpageModuleController extends BaseController {
	protected static Logger log = LoggerFactory
			.getLogger(MainpageModuleController.class);


	@Autowired
	private MainpageModuleBS mainpageModuleBS;

	/**
	 * 首页功能模块配置
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/mainpage/mainpage-module-index";
	}
	
	/**
	 * 保存模块配置
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void save(BioneMpModuleInfo info) {
		info.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		this.mainpageModuleBS.saveModule(info);
	}
	
	/**
	 * 首页功能模块删除
	 */
	@RequestMapping(value = "/deModule",method = RequestMethod.POST)
	@ResponseBody
	public  Map<String,Object> delete(String moduelIds) {
		return this.mainpageModuleBS.deleteModule(moduelIds);
	}
	
	/**
	 * 首页功能模块配置
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView edit(String moduleId) {
		moduleId = StringUtils2.javaScriptEncode(moduleId);
		return new ModelAndView("/frame/mainpage/mainpage-module-edit", "moduleId", moduleId);
	}
	
	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		Map<String, Object> map = Maps.newHashMap();
		SearchResult<BioneMpModuleInfo> searchResult = mainpageModuleBS.getMpModuleList(
				pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition());
		map.put("Rows", searchResult.getResult());
		map.put("Total", searchResult.getTotalCount());
		return map;
	}
	
	/**
	 * 获取首页模块信息
	 */
	@RequestMapping("/getModule")
	@ResponseBody
	public BioneMpModuleInfo getModuleInfo(String moduleId) {
		return this.mainpageModuleBS.getModuleInfo(moduleId);
	}

	/**
	 * 校验首页模块Id
	 */
	@RequestMapping("/validate")
	@ResponseBody
	public Boolean validate(String moduleId) {
		return this.mainpageModuleBS.getModuleInfo(moduleId)!=null?false:true;
	}
	
	

	
}
