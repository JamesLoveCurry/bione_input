package com.yusys.bione.plugin.paramtmp.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.design.util.DataDealUtils;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpCatalog;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpInfo;
import com.yusys.bione.plugin.paramtmp.service.ParamTempBS;
import com.yusys.bione.plugin.paramtmp.web.vo.RptParamtmpVO;

/**
 * 
 * <pre>
 * Title:报表参数模板配置
 * Description: 提供对动态参数模板自定义配置
 * </pre>
 * 
 * @author fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/report/frame/param/templates")
public class ParamTempController extends BaseController {

	@Autowired
	private ParamTempBS tempBS;

	
	private static String UPLOAD_ATTACH_DIR = GlobalConstants4frame.IMPORT_PATH
			+ "/rpt/require";
	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/paramtmp/param-temp-index";
	}
	/**
	 * 新增参数模板
	 * 
	 * @param model
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void create(RptParamtmpVO model) {
		RptParamtmpInfo info = new RptParamtmpInfo();
		info.setCatalogId(model.getCatalogId());
		info.setParamtmpId(model.getParamtmpId());
		info.setParamtmpNm(model.getParamtmpName());
		info.setRemark(model.getRemark());
		info.setTemplateType(model.getTemplateType());
		info.setTemplateUrl(model.getTemplateUrl());
		if (info.getTemplateType().equals("static")) {// 固定模板
			if (info.getParamtmpId() == null
					|| info.getParamtmpId().equals("")) {//新增
				info.setParamtmpId(RandomUtils.uuid2());
				tempBS.saveParamtmpInfo(info);
			}
			tempBS.updateParamtmpInfo(info);
		}
		else if(info.getTemplateType().equals("custom"))//自定义模板
		{
			
			tempBS.save(info,model.getParamJson());
		}
	}
	/**
	 * 获取数据模板的数据, 用于填充 form
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String show(@PathVariable("id") String id,String type) {
		RptParamtmpVO vo = this.tempBS.show(id,type);
		return JSON.toJSONString(vo);
	}
	
	/**
	 * 删除当前数据模板的信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{ids}", method = RequestMethod.POST)
	public void deleteParamtmp(@PathVariable("ids") String ids) {
		String[] id = StringUtils.split(ids, ',');
		if (id != null && id.length > 0) {
			for (String temp : id) {
				tempBS.deleteTemp(temp,BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			}
		}
	}
	
	
	/**
	 * 参数模板目录信息页
	 * @param catalogId
	 * @param upId
	 * @return
	 */
	@RequestMapping(value = "/catalog")
	public ModelAndView catalog(String catalogId, String upId) {
		ModelMap map = new ModelMap();
		map.addAttribute("catalogId", StringUtils2.javaScriptEncode(catalogId));
		map.addAttribute("upId", StringUtils2.javaScriptEncode(upId));
		return new ModelAndView("/plugin/paramtmp/param-temp-catalog", map);
	}
	

	/**
	 * 参数模板列表页
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param catalogName
	 *            目录名称
	 * @return
	 */
	@RequestMapping(value = "/grid")
	public String grid(String catalogId, String catalogName) {
		return "/plugin/paramtmp/param-temp-grid";
	}

	/**
	 * 参数模板信息页
	 * 
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@RequestMapping(value = "/infoPage")
	public ModelAndView infoPage(String paramtmpId, String catalogId) {

		ModelMap map = new ModelMap();
		map.addAttribute("paramtmpId", StringUtils2.javaScriptEncode(paramtmpId));
		map.addAttribute("catalogId", StringUtils2.javaScriptEncode(catalogId));
		return new ModelAndView("/plugin/paramtmp/template_edit", map);

	}
	/**
	 * 跳转到模板设计页面
	 * @param paramJson
	 * @return
	 */
	@RequestMapping(value = "/design")
	public ModelAndView design ( String paramJson) {
		paramJson = StringUtils2.javaScriptEncode(paramJson);
		return new ModelAndView("/plugin/paramtmp/template_design", "paramJson", paramJson);
	}
	
	@RequestMapping(value = "/selectOptions")
	public String selectOptions () {
		return "/plugin/paramtmp/template_select_options";
	}
	
	@RequestMapping(value = "/treeOptions")
	public String treeOptions () {
		return "/plugin/paramtmp/template_tree_options";
	}
	
	@RequestMapping(value = "/popupOptions")
	public String popupOptions () {
		return "/plugin/paramtmp/template_popup_options";
	}
	
	@RequestMapping(value = "/validator")
	public String validator () {
		return "/plugin/paramtmp/template_validator";
	}
	
	@RequestMapping(value = "/popupTree")
	public ModelAndView popupTree (String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/plugin/paramtmp/template_popup_tree", "id", id);
	}
	
	@RequestMapping(value = "/convertMacro", method = RequestMethod.POST)
	@ResponseBody
	public String convertMacro (String macroValue) {
		return tempBS.replace(macroValue);
	}
	
	@RequestMapping(value = "/view")
	public String view () {
		return "/plugin/paramtmp/template_view";
	}

	/**
	 * 系统参数选择页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sysparamPage")
	public String sysparamPage() {
		return "/plugin/paramtmp/param-temp-sysparams";
	}

	/**
	 * 数据集选择页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/datasetPage")
	public String datasetPage() {
		return "/plugin/paramtmp/param-temp-datasets";
	}
	/**
	 * 预览参数模板
	 * @param id 要预览的参数模板的ID
	 * @return
	 */
	@RequestMapping(value = "/view/{id}")
	public ModelAndView view(@PathVariable("id") String id){
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/plugin/paramtmp/template_view", "paramtmpId", id);
	}
	/**
	 * 未设定参数模板跳转页面
	 * @return
	 */
	@RequestMapping(value = "/empty")
	public String empty(){
		return "/plugin/paramtmp/param-temp-empty";
	}
	/**
	 * 获取参数模板目录树
	 * @param realId
	 * @return
	 */
	@RequestMapping(value = "/getTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTree(String upId) {
//		return this.tempBS.getAyncTree(this.getContextPath());
		return this.tempBS.getDatasetCatalogTree(upId, this.getContextPath());
	}
	/**
	 * 获取参数模板目录-参数模板树
	 * @return
	 */
	@RequestMapping(value = "/getAyncTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getAyncTree(){
		return this.tempBS.getAyncTree(this.getContextPath());
	}
	
	@RequestMapping(value = "/getTreeForReport.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTreeForReport(){
		List<CommonTreeNode> list = this.tempBS.getAyncTree(this.getContextPath());
		CommonTreeNode nullNode = new CommonTreeNode();
		nullNode.setText("空参数模板");
		nullNode.setId("");
		nullNode.setUpId("0");
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", "paramtmp");
		nullNode.setParams(map);
		nullNode.setIcon(this.getContextPath() + GlobalConstants4frame.DATA_TREE_NODE_ICON_REPORT);
		
		CommonTreeNode paramtNode = new CommonTreeNode();
		paramtNode.setText("参数模板目录");
		paramtNode.setId("ROOT");
		paramtNode.setChildren(list);
		paramtNode.setIcon(this.getContextPath() + GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("type", "catalog");
		paramtNode.setParams(map1);
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		resultList.add(nullNode);
		resultList.add(paramtNode); 
		return resultList;
		
	}
	
	/**
	 * 目录名称重复验证
	 * @param catalogId
	 * @param upId
	 * @param catalogName
	 * @return
	 */
	@RequestMapping(value = "/catalogNameCanUse*")
	@ResponseBody
	public boolean catalogNameCanUse(String catalogId, String upId,
			String catalogNm) {
		return this.tempBS.catalogNameCanUse(catalogId, upId, catalogNm);
	}

	/**
	 * 获取目录信息
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/getCatalogInfo.*")
	@ResponseBody
	public RptParamtmpCatalog getCatalogInfo(String catalogId) {
		return this.tempBS.getUniqueParamtmpCatalogById(catalogId);
	}

	/**
	 * 保存目录信息
	 * @param catalog 要保存的目录信息
	 */
	@RequestMapping(value = "/saveCatalog")
	public void saveCatalog(RptParamtmpCatalog catalog) {
		if (catalog.getCatalogId() == null || "".equals(catalog.getCatalogId())) {
			catalog.setCatalogId(RandomUtils.uuid2());
			this.tempBS.saveParamtmpCatalog(catalog);
		} else {
			this.tempBS.updateParamtmpCatalog(catalog);
		}
	}

	/**
	 * 删除目录信息
	 * @param catalogId 要删除的模板目录
	 * @return
	 */
	@RequestMapping("/deleteCatalog")
	@ResponseBody
	public boolean deleteCatalog(String catalogId) {
		return this.tempBS.deleteCatalog(catalogId);
	}

	/**
	 *  获取目录下的参数模板
	 * @param pager
	 * @param catalogId
	 * @return
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> getParamTemps(Pager pager, String catalogId) {
		
		SearchResult<RptParamtmpInfo> result = this.tempBS.getParamTemps(pager, catalogId);

		Map<String, Object> map = Maps.newHashMap();
		if (result != null) {
			map.put("Rows", result.getResult());
			map.put("Total", result.getTotalCount());
		}
		return map;
	}

	/**
	 * 获取当前逻辑系统下的所有参数类型
	 * @param pager
	 * @return
	 */
	@RequestMapping("/sysParams.*")
	@ResponseBody
	public Map<String, Object> getSysParams(Pager pager) {
		return this.tempBS.getSysParams(pager);
	}

	
	/**
	 * 根据模板名称，得到该模板的属性
	 * @param paramtmpId
	 * @return
	 */
	@RequestMapping("/getRealCode.*")
	@ResponseBody
	public RptParamtmpInfo getRealCode(String paramtmpId) {
		return this.tempBS.getUniqueParamtmpById(paramtmpId);
	}

	/**
	 * 获取所有系统数据集
	 * @param pager
	 * @return
	 */
	@RequestMapping("/datasets.*")
	@ResponseBody
	public Map<String, Object> getDatasets(Pager pager) {
		return this.tempBS.getDatasets(pager);
	}

	/**
	 * 保存前模板名称查重
	 * @param catalogId
	 * @param paramtmpName
	 * @param paramtmpId
	 * @return
	 */
	@RequestMapping("/tmpNameCanUse")
	@ResponseBody
	public boolean tmpNameCanUse(String catalogId, String paramtmpName,
			String paramtmpId) {
		return this.tempBS.tmpNameCanUse(catalogId, paramtmpName, paramtmpId,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
	}

	/**
	 * 保存参数模板
	 * @param temp 参数模板信息
	 * @param paramsJsonStr 参数模板参数组成的字符串
	 */
	@RequestMapping("/save")
	public void save(RptParamtmpInfo temp, String paramsJsonStr) {
		 this.tempBS.save(temp, paramsJsonStr);
	}

	/**
	 * 根据参数模板ID得到其参数信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/getEnity/{id}")
	@ResponseBody
	public RptParamtmpInfo getEnity(@PathVariable("id") String id){
		RptParamtmpInfo tmp = (RptParamtmpInfo) this.tempBS.getUniqueParamtmpById(id);
		return tmp;
	}
	
	/**
	 * 查找指定模版ID的已关联的报表数量
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/relRpt", method = RequestMethod.GET)
	@ResponseBody
	public String relRpt(String ids) {
		String[] id = StringUtils.split(ids, ',');
		List<String> idList = Lists.newArrayList();
		for(String temp : id){
			idList.add(temp);
		}
		String result =  String.valueOf(tempBS.getRelReport(idList, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo()));
		return result;
	}
	
	/**
	 * 重置指定模版ID所关联报表的的参数模版项为空
	 * @param ids
	 */
	@RequestMapping(value = "/updateParamtmpIds", method = RequestMethod.GET)
	public void updateParamtmpIds(String ids){
		String[] id = StringUtils.split(ids, ',');
		List<String> idList = Lists.newArrayList();
		for(String temp : id){
			idList.add(temp);
		}
		this.tempBS.updateRptParamtmpId(idList, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
	}
	
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id, ModelMap modelMap) {
		if (id == null || id.equals("")) {
			return new ModelAndView("/plugin/paramtmp/param-report-view");
		}
		RptParamtmpInfo tmp = (RptParamtmpInfo) this.tempBS.getUniqueParamtmpById(id);
		if (tmp != null) {
			if (tmp.getTemplateType().equals("static")) {
				return new ModelAndView(tmp.getTemplateUrl());
			} else {
				id = StringUtils2.javaScriptEncode(id);
				return new ModelAndView("/plugin/paramtmp/param-report-view",
						"paramtmpId", id);
			}
		} else {
			return new ModelAndView("/plugin/paramtmp/param-report-view");
		}
	}
	
	@RequestMapping(value = "/exportParam", method = RequestMethod.GET)
	public ModelAndView exportParam(){
		return new ModelAndView("/plugin/paramtmp/param-temp-export");
	}
	
	@RequestMapping(value = "/importParam", method = RequestMethod.GET)
	public ModelAndView importParam(){
		return new ModelAndView("/plugin/paramtmp/param-temp-imp-index");
	}
	
	@RequestMapping(value = "/impGrid", method = RequestMethod.GET)
	public ModelAndView impGrid(){
		return new ModelAndView("/plugin/paramtmp/param-temp-imp-grid");
	}
	
	@RequestMapping(value = "/impUpload", method = RequestMethod.GET)
	public ModelAndView impUpload(){
		return new ModelAndView("/plugin/paramtmp/param-temp-imp-upload");
	}
	
	@RequestMapping(value ="/exportTmp")
	public Map<String,Object> exportTmp(String paramIds) throws IOException{
		return DataDealUtils.exportImport(paramIds, this.getRealPath(),"exportParam");
	}
	
	@RequestMapping("/exportTmpInfo")
	public void exportTmp(HttpServletResponse response,String filepath) throws Exception {
		if (FilepathValidateUtils.validateFilepath(filepath)) {
			File file=new File(filepath); 
			DownloadUtils.download(response, file,"参数模板导出数据.txt");
			file.delete();
		}
	}
	
	@RequestMapping("/upload")
	@ResponseBody
	public String upload(Uploader uploader, HttpServletResponse response)
			throws Exception {
		File file = null;
		try {
			file = this.uploadFile(
					uploader,
					UPLOAD_ATTACH_DIR
							+ File.separatorChar, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		if (file != null) {
			logger.info("文件[" + file.getName() + "]上传完成");
			List<String> fieldNms=new ArrayList<String>();
			fieldNms.add("paramtmpNm");
			return DataDealUtils.getDataInfo(file, RptParamtmpInfo.class.getName(), fieldNms);
		}
		return null;
	}
	

	@RequestMapping("/saveImport")
	@ResponseBody
	public Map<String,Object> saveImport(String pathname) throws IOException, ClassNotFoundException{
		// 2020 lcy 【后台管理】目录跨越 代码优化
		Map<String,Object> rtMap=new HashMap<String, Object>();
		if(pathname.contains(UPLOAD_ATTACH_DIR)) {
			rtMap = DataDealUtils.saveImport(pathname,null);
		}else {
			rtMap.put("isSucess", false);
		}
		return rtMap;
	}
	
	@RequestMapping("/getParamGird.*")
	@ResponseBody
	public Map<String,Object> getParamGird(String paramTmpId){
		return this.tempBS.getParamGird(paramTmpId);
	}
}
