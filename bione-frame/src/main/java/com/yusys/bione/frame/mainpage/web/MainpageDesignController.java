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
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignFunc;
import com.yusys.bione.frame.mainpage.entity.BioneMpModuleInfo;
import com.yusys.bione.frame.mainpage.service.MainpageDesignBS;
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
@RequestMapping("/bione/mainpage/design")
public class MainpageDesignController extends BaseController {
	protected static Logger log = LoggerFactory
			.getLogger(MainpageDesignController.class);


	@Autowired
	private MainpageDesignBS mainpageDesignBS;

	/**
	 * 首页功能模块配置
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/mainpage/mainpage-design-index";
	}
	
	/**
	 * 保存模块配置
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void save(BioneMpDesignFunc info) {
		if(info.getDesignId() == null || info.getDesignId().equals("")){
			String designId = RandomUtils.uuid2();
			info.setDesignId(designId);
			info.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		}
		this.mainpageDesignBS.saveModule(info);
	}
	
	/**
	 * 首页功能模块删除
	 */
	@RequestMapping(value = "/deDesign",method = RequestMethod.POST)
	@ResponseBody
	public  Map<String,Object> delete(String designIds) {
		return this.mainpageDesignBS.deleteDesign(designIds);
	}
	
	/**
	 * 首页信息页面
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.GET)
	public ModelAndView edit(String designId) {
		designId = StringUtils2.javaScriptEncode(designId);
		return new ModelAndView("/frame/mainpage/mainpage-design-edit", "designId", designId);
	}

	/**
	 * 首页配置页面
	 */
	@RequestMapping(value = "/config",method = RequestMethod.GET)
	public ModelAndView config(String designId) {
		designId = StringUtils2.javaScriptEncode(designId);
		return new ModelAndView("/frame/mainpage/mainpage-public-design", "designId", designId);
	}
	
	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		Map<String, Object> map = Maps.newHashMap();
		SearchResult<BioneMpModuleInfo> searchResult = mainpageDesignBS.getMpModuleList(
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
	@RequestMapping("/getDesign")
	@ResponseBody
	public BioneMpDesignFunc getDesign(String designId) {
		return this.mainpageDesignBS.getDesign(designId);
	}

}
