/**
 *
 */
package com.yusys.bione.frame.mainpage.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignDetail;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignFunc;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignInfo;
import com.yusys.bione.frame.mainpage.entity.BioneMpLayoutInfo;
import com.yusys.bione.frame.mainpage.entity.BioneMpModuleInfo;
import com.yusys.bione.frame.mainpage.service.MainpageBS;
import com.yusys.bione.frame.mainpage.service.MainpageDesignBS;
import com.yusys.bione.frame.mainpage.web.vo.MainpageModelVO;
import com.yusys.bione.frame.mainpage.web.vo.MpDetailInfoVO;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:基础首页相关维护功能controller
 * Description: 基础首页相关维护功能controller
 * </pre>
 * getLayouts
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 *
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/mainpage")
public class MainpageController extends BaseController {
	protected static Logger log = LoggerFactory
			.getLogger(MainpageController.class);

	private String moduleTreeIcon = "/images/classics/icons/bricks.png";

	@Autowired
	private MainpageBS mainpageBS;

	@Autowired
	private MainpageDesignBS mainpageDesignBS;

	/**
	 * 首页功能入口
	 *
	 * @param isPreView
	 *            是否是预览模式 1 -> 是,0或其他 -> 否
	 */
	@RequestMapping(value = "mainpage", method = RequestMethod.GET)
	public ModelAndView mainPage(String isPreView) {
		Map<String, String> params = new HashMap<String, String>();
		//缓存模式走一个极简页面，应对压测
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
		String isOpenCache = propertiesUtils.getProperty("isOpenCache");
		if("Y".equals(isOpenCache)) {
			return new ModelAndView("/frame/mainpage/mainpage-index-min", params);
		}
		// 若是预览模式
		if (!StringUtils.isEmpty(isPreView) && "1".equals(isPreView)) {
			params.put("isPreView", StringUtils2.javaScriptEncode(isPreView));
			return new ModelAndView("/frame/mainpage/mainpage-index", params);
		} else {
			// 其他情况，当作首页模式
			String layoutId = "";
			String designId = "";
			String cssPath = "";
			// 获取当前用户布局信息
			Map<String, String> mapInfo = this.mainpageBS.getBasicInfoByUser(
					BioneSecurityUtils.getCurrentUserId(), BioneSecurityUtils
							.getCurrentUserInfo().getCurrentLogicSysNo());
			if (mapInfo == null || mapInfo.size() <= 0){
				 mapInfo = this.mainpageBS.getPublicInfoByUser(
							BioneSecurityUtils.getCurrentUserId(), BioneSecurityUtils
									.getCurrentUserInfo().getCurrentLogicSysNo());
			}
			if (mapInfo != null) {
				if (!StringUtils.isEmpty(mapInfo.get("designId"))) {
					designId = mapInfo.get("designId");
				}
				if (!StringUtils.isEmpty(mapInfo.get("layoutId"))) {
					layoutId = mapInfo.get("layoutId");
				}
				if (!StringUtils.isEmpty(mapInfo.get("cssPath"))) {
					cssPath = mapInfo.get("cssPath");
				}
			}
			params.put("layoutId", StringUtils2.javaScriptEncode(layoutId));
			params.put("designId", StringUtils2.javaScriptEncode(designId));
			params.put("cssPath", StringUtils2.javaScriptEncode(cssPath));
			return new ModelAndView("/frame/mainpage/mainpage-index", params);
		}
	}


	/**
	 * 首页定制功能入口
	 */
	@RequestMapping(value = "/pdesign", method = RequestMethod.GET)
	public String design() {
		return "/frame/mainpage/mainpage-design";
	}

	/**
	 * 获取首页可配置功能模块树形结构
	 */
	@RequestMapping(value = "getModules.json", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getModules(HttpServletRequest request) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		List<BioneMpModuleInfo> modules = mainpageBS
				.getModules(BioneSecurityUtils.getCurrentUserInfo()
						.getCurrentLogicSysNo());
		if (modules != null) {
			for (int i = 0; i < modules.size(); i++) {
				BioneMpModuleInfo mTmp = modules.get(i);
				// 构造树节点
				CommonTreeNode nodeTmp = new CommonTreeNode();
				nodeTmp.setId(mTmp.getModuleId());
				nodeTmp.setText(mTmp.getModuleName());
				// 统一设置节点图标
				nodeTmp.setIcon(request.getContextPath() + moduleTreeIcon);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("realId", mTmp.getModuleId());
				paramMap.put("picPath", mTmp.getPicPath());
				paramMap.put("labelPath", mTmp.getLabelPath());
				paramMap.put("moduleName", mTmp.getModuleName());
				paramMap.put("modulePath", mTmp.getModulePath());
				nodeTmp.setParams(paramMap);
				nodes.add(nodeTmp);
			}
		}
		return nodes;
	}

	/**
	 * 获取首页可选择布局信息
	 */
	@RequestMapping(value = "getLayouts.json", method = RequestMethod.POST)
	@ResponseBody
	public List<BioneMpLayoutInfo> getLayouts() {
		List<BioneMpLayoutInfo> layouts = new ArrayList<BioneMpLayoutInfo>();
		layouts = this.mainpageBS.getLayouts(BioneSecurityUtils
				.getCurrentUserInfo().getCurrentLogicSysNo());
		return layouts;
	}

	/**
	 * 保存首页布局信息
	 */
	@RequestMapping(value = "saveLayout.json", method = RequestMethod.POST)
	public void saveLayout(String designId, String layoutId, String relsString,Model model) {
		if (!StringUtils.isEmpty(layoutId) && !StringUtils.isEmpty(relsString)) {
			JSONArray rels = JSON.parseArray(relsString);
			List<BioneMpDesignDetail> details = new ArrayList<BioneMpDesignDetail>();
			if (rels != null) {
				for (int i = 0; i < rels.size(); i ++) {
					JSONObject objTmp = rels.getJSONObject(i);
					String posNo = objTmp.getString("posNo");
					String isDisplayLabel = objTmp.getString("isDisplayLabel");
					String moduleId = objTmp.getString("moduleId");
					// 封装对象
					BioneMpDesignDetail detail = new BioneMpDesignDetail();
					try {
						detail.setPosNo(BigDecimal.valueOf(Long.valueOf(posNo)));
					} catch (Exception e) {
						continue;
					}
					detail.setIsDisplayLabel(isDisplayLabel);
					detail.setModuleId(moduleId);
					details.add(detail);
				}
			}
			// 进行具体保存动作
			BioneMpDesignInfo designInfo = new BioneMpDesignInfo();
			designInfo.setLayoutId(layoutId);
			designInfo.setDesignId(designId);
			designInfo.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			designInfo.setUserId(BioneSecurityUtils.getCurrentUserId());
			this.mainpageBS.saveLayout(designInfo, details);
			model.addAttribute("designId", designId);
		}
	}

	/**
	 * 保存首页布局信息
	 */
	@RequestMapping(value = "savePublicLayout.json", method = RequestMethod.POST)
	public void savePublicLayout(String designId, String layoutId, String relsString) {
		if (!StringUtils.isEmpty(layoutId) && !StringUtils.isEmpty(relsString)) {
			JSONArray rels = JSON.parseArray(relsString);
			List<BioneMpDesignDetail> details = new ArrayList<BioneMpDesignDetail>();
			if (rels != null) {
				for (int i = 0; i < rels.size(); i ++) {
					JSONObject objTmp = rels.getJSONObject(i);
					String posNo = objTmp.getString("posNo");
					String isDisplayLabel = objTmp.getString("isDisplayLabel");
					String moduleId = objTmp.getString("moduleId");
					// 封装对象
					BioneMpDesignDetail detail = new BioneMpDesignDetail();
					try {
						detail.setPosNo(BigDecimal.valueOf(Long.valueOf(posNo)));
					} catch (Exception e) {
						continue;
					}
					detail.setIsDisplayLabel(isDisplayLabel);
					detail.setModuleId(moduleId);
					details.add(detail);
				}
			}
			// 进行具体保存动作
			BioneMpDesignFunc designInfo = this.mainpageDesignBS.getDesign(designId);
			designInfo.setLayoutId(layoutId);
			this.mainpageBS.savePublicLayout(designInfo, details);
		}
	}

	/**
	 * 获取当前用户布局信息
	 */
	@RequestMapping(value = "initUserDesign.json", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> initUserDesign() {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String designId = null;
		String layoutId = null;
		List<BioneMpDesignDetail> details = null;
		// 获取基本布局信息
		BioneMpDesignInfo info = this.mainpageBS.getUserLayoutInfo(
				BioneSecurityUtils.getCurrentUserId(), BioneSecurityUtils
						.getCurrentUserInfo().getCurrentLogicSysNo());
		if (info != null) {
			designId = info.getDesignId();
			layoutId = info.getLayoutId();
			// 获取布局明细信息
			details = this.mainpageBS.getDetailsByDesignId(designId);
		}
		returnMap.put("designId", designId);
		returnMap.put("layoutId", layoutId);
		returnMap.put("rels", details);
		return returnMap;
	}


	/**
	 * 获取当前用户布局信息
	 */
	@RequestMapping(value = "initPublicDesign.json", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> initPublicDesign(String designId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String layoutId = null;
		List<BioneMpDesignDetail> details = null;
		// 获取基本布局信息
		BioneMpDesignFunc info = this.mainpageBS.getPublicLayoutInfo(
				designId, BioneSecurityUtils
						.getCurrentUserInfo().getCurrentLogicSysNo());
		if (info != null) {
			designId = info.getDesignId();
			layoutId = info.getLayoutId();
			// 获取布局明细信息
			details = this.mainpageBS.getDetailsByDesignId(designId);
		}
		returnMap.put("designId", designId);
		returnMap.put("layoutId", layoutId);
		returnMap.put("rels", details);
		return returnMap;
	}

	/**
	 * 首页初始化用，布局明细信息
	 */
	@RequestMapping(value = "getDetailInfoById.json", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<MpDetailInfoVO>> getDetailInfoById(String designId) {
		Map<String, List<MpDetailInfoVO>> returnMap = new HashMap<String, List<MpDetailInfoVO>>();
		List<MpDetailInfoVO> details = this.mainpageBS
				.getDetailInfoById(designId);
		if (details != null) {
			returnMap.put("details", details);
		}
		return returnMap;
	}

	@RequestMapping(value = "cancelLayout.json", method = RequestMethod.POST)
	public void cancelLayout(){
		this.mainpageBS.cancelLayout();
	}

}
