package com.yusys.biapp.input.catalog.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.biapp.input.catalog.service.DataInputCatalogBS;
import com.yusys.biapp.input.dict.entity.RptInputListCatalogInfo;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;

@Controller
@RequestMapping("/rpt/input/catalog")
public class DataInputCatalogController extends BaseController {
	
	@Autowired
	private DataInputCatalogBS dataInputCatalogBS;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/input/catalog/dict-catalog";
	}
	/**
	 * 执行修改前的页面跳转
	 * 
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/{catalogType}", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("catalogType") String type) {
		type = StringUtils2.javaScriptEncode(type);
		return new ModelAndView("/input/catalog/dict-catalog", "dirType", type);
	}
	/**
	 * 获取生成树数据，以树显示功能树
	 */
	@RequestMapping("/{type}/list.*")
	@ResponseBody
	public List<CommonTreeNode> list(@PathVariable("type") String type) {
		List<CommonTreeNode> list = dataInputCatalogBS.getByType(type, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		/**有权限看的*/
		/*List<String> idList = Lists.newArrayList();
		List<CommonTreeNode> treeList = Lists.newArrayList();
		boolean auth = false ; 
		if(UdipConstants.DIR_TYPE_LIB.equals(type)){
			LibResImpl	libResImpl = SpringContextHolder.getBean(LibResImpl.name);
			idList = libResImpl.getLibId();
			auth = true ;
		}
		
		if(UdipConstants.DIR_TYPE_TEMP.equals(type)){
			TempResImpl	tempResImpl = SpringContextHolder.getBean(TempResImpl.name);
			idList = tempResImpl.getTempId();
			auth = true ;
		}
		if(auth){
			for(CommonTreeNode node : list){
				if(idList.contains(node.getId())){
					treeList.add(node);
				}
			}
			return treeList ;
		}*/
		
		return list;
	}
	/**
	 * 根据ID，加载数据
	 */
	@RequestMapping(value = "/{catalogType}/name", method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String typeName(@PathVariable("catalogType") String type) {
		return UdipConstants.DirType.get(type);
	}
	/**
	 * 用于添加，或修改时的保存对象
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> create(RptInputListCatalogInfo model) {
		// String saveMark = "03";
		if (model.getCatalogId() == null || "".equals(model.getCatalogId())) {
			// saveMark="01";
			model.setCatalogId(RandomUtils.uuid2());
		}
		if (StringUtils.isBlank(model.getUpCatalog())) {
			model.setUpCatalog("0");
		}
		model.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		model.setCreateTime(DateUtils.getYYYY_MM_DD_HH_mm_ss());
		dataInputCatalogBS.saveOrUpdateEntity(model);
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils
//				.getCurrentUserInfo().getCurrentLogicSysNo(),
//				BioneSecurityUtils.getCurrentUserInfo().getUserId(),
//				"添加或者修改目录【" + model.getCatalogName() + "】");
		/******添加日志记录******/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]").append(saveMark.equals("01") ? "新增" : "修改")
//			.append("了一个").append(model.getCatalogType().equalsIgnoreCase("2")?"数据字典":"明细补录模板").append("目录，目录的名称为:").append(model.getCatalogName());
//		dataInputCatalogBS.saveLog(saveMark, model.getCatalogType().equalsIgnoreCase("2")?"数据字典":"明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
		Map<String, String> map = Maps.newHashMap();
		map.put("id", model.getCatalogId());
		return map;
	}
	/**
	 * 执行删除操作，依据指定ID
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void destroy(@PathVariable("id") String id) {
//		RptInputListCatalogInfo udipDirInfo = dataInputCatalogBS.getEntityByProperty(RptInputListCatalogInfo.class, "catalogId", id);
		// RptInputListCatalogInfo cat = dataInputCatalogBS.getEntityById(id);
		dataInputCatalogBS.removeEntityAndChild(id);
		/******添加日志记录******/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]").append("删除")
//			.append("了一个").append(cat.getCatalogType().equalsIgnoreCase("2")?"数据字典":"明细补录模板").append("目录，目录的名称为:").append(cat.getCatalogName());
//		dataInputCatalogBS.saveLog("02", cat.getCatalogType().equalsIgnoreCase("2")?"数据字典":"明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
//		logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
//				BioneSecurityUtils.getCurrentUserInfo().getUserId(), "【删除目录" + udipDirInfo.getCatalogName() + "】");
	}
	/**
	 * 查找dirId与dirName
	 */
	@RequestMapping(value = "/findDirIdAndName/{id}")
	@ResponseBody
	public RptInputListCatalogInfo findDirIdAndName(@PathVariable("id") String id) {
		RptInputListCatalogInfo model = dataInputCatalogBS.getEntityById(id);
		return model;
	}

	/**
	 * 根据ID，加载数据
	 */
	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RptInputListCatalogInfo show(@PathVariable("id") String id) {
		RptInputListCatalogInfo info = new RptInputListCatalogInfo();
		if ("0".equals(id)) {
			info.setCatalogName("补录字典目录");
			info.setCatalogId("0");
		} else if (id != null) {
			info = this.dataInputCatalogBS.getEntityById(id);
		}
		return info;
	}

	/**
	 * 补录模板的树节点
	 * 
	 * @return
	 */
	@RequestMapping("/templelistTree.*")
	@ResponseBody
	public List<CommonTreeNode> listTree(String nodeId) {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		String userName = BioneSecurityUtils.getCurrentUserInfo().getLoginName();

		List<String> roles = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
		return dataInputCatalogBS.buildTempleTree(nodeId,userName, logicSysNo, roles, UdipConstants.ROLE_TYPE_MANAGE);
	}
	
	/**
	 * 新增目录
	 * @param indexCatalogNo
	 * @return
	 */
	@RequestMapping(value = "/newCatalog", method = RequestMethod.GET)
	public ModelAndView newCatalog(String upNo,String upName){
		try {
			upName = URLDecoder.decode(URLDecoder.decode(upName,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		ModelMap mm = new ModelMap();
		mm.put("upNo", StringUtils2.javaScriptEncode(upNo));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		return new ModelAndView("/input/temple/temple-catalog-edit", mm);
	}
	
	/**
	 * 校验同名目录
	 */
	@RequestMapping(value = "/testSameInputCatalogNm", method = RequestMethod.POST)
	@ResponseBody
	public boolean testSameInputCatalogNm(String upNo,String catalogName,String catalogId,String catalogType){
		return  this.dataInputCatalogBS.testSameIndexCatalogNm( upNo, catalogName, catalogId,catalogType);
	}
	
	/**
	 * 保存目录
	 * @param model
	 */
	@RequestMapping(value = "/editTempleCatalog.*",method = RequestMethod.POST)
	public void createIdxInputCatalog(String catalogType,String catalogName,String orderNo,String upNo,String  catalogId) {
		this.dataInputCatalogBS.createTskCatalog(catalogType,catalogId,catalogName, orderNo, upNo);
	}
	
	
	/**
	 * 修改目录
	 * @param model
	 */
	@RequestMapping(value = "/initUpdateCatalog", method = RequestMethod.GET)
	public ModelAndView initUpdateCatalog(String catalogId,String upName){
		RptInputListCatalogInfo catalog = dataInputCatalogBS.getEntityById(catalogId);
		try {
			upName = URLDecoder.decode(URLDecoder.decode(upName,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		ModelMap mm = new ModelMap();
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalog.getCatalogId()));
		mm.put("catalogName", StringUtils2.javaScriptEncode(catalog.getCatalogName()));
		mm.put("orderNo", StringUtils2.javaScriptEncode(catalog.getOrderNo().toString()));
		mm.put("upNo", StringUtils2.javaScriptEncode(catalog.getUpCatalog()));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		return new ModelAndView("/input/temple/temple-catalog-edit", mm);
	}
	
	
	/***
	 *  判断该目录下面是否有模板
	 * @param  indexCatalogNo
	 * @return result
	 */
	@RequestMapping(value = "/canDeleteCatalog", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String canDeleteCatalog(String catalogType,String catalogId) {
		List<RptInputListCatalogInfo> funcList =  this.dataInputCatalogBS.getTempleByCatalogid(catalogId);
		return this.dataInputCatalogBS.deleteCatalog(catalogType,funcList);
	}
	/**
	 * 新增字典目录
	 */
	@RequestMapping(value = "/newDictCatalog", method = RequestMethod.GET)
	public ModelAndView newDictCatalog(String catalogType,String upNo,String upName){
		try {
			upName = URLDecoder.decode(URLDecoder.decode(upName,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		ModelMap mm = new ModelMap();
		mm.put("upNo", StringUtils2.javaScriptEncode(upNo));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		mm.put("catalogType", StringUtils2.javaScriptEncode(catalogType));
		return new ModelAndView("/input/library/lib-catalog-edit", mm);
	}
	/**
	 * 修改字典目录
	 */
	@RequestMapping(value = "/updateDictCatalog", method = RequestMethod.GET)
	public ModelAndView updateDictCatalog(String catalogType,String catalogId,String upName){
		RptInputListCatalogInfo catalog = dataInputCatalogBS.getEntityById(catalogId);
		try {
			upName = URLDecoder.decode(URLDecoder.decode(upName,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		ModelMap mm = new ModelMap();
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalog.getCatalogId()));
		mm.put("catalogName", StringUtils2.javaScriptEncode(catalog.getCatalogName()));
		mm.put("orderNo", StringUtils2.javaScriptEncode(catalog.getOrderNo().toString()));
		mm.put("upNo", StringUtils2.javaScriptEncode(catalog.getUpCatalog()));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		mm.put("catalogType", StringUtils2.javaScriptEncode(catalogType));
		return new ModelAndView("/input/library/lib-catalog-edit", mm);
	}
	
}
