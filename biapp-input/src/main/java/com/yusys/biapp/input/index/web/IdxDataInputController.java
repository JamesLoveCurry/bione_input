package com.yusys.biapp.input.index.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.index.entity.RptInputCatalog;
import com.yusys.biapp.input.index.entity.RptInputIdxValidate;
import com.yusys.biapp.input.index.service.IdxDataInputBS;
import com.yusys.biapp.input.index.util.DataInputExportUtil;
import com.yusys.biapp.input.index.util.vo.TableGridVO;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.biapp.input.utils.CommonTreeNode;

@Controller
@RequestMapping("/rpt/input/idxdatainput")
public class IdxDataInputController extends BaseController {


	@Autowired
	private IdxDataInputBS idxDataInputBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/input/index/idx-datainput-index";
	}
	
	@RequestMapping(value = "/getTemplateTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTemplateTree(String searchNm,String nodeId){
		if(searchNm==null&&nodeId==null)
			return idxDataInputBS.getRootRptTskInfoNode(this.getContextPath());
		else
			return idxDataInputBS.getRptTskInfoNode(this.getContextPath(),searchNm, nodeId);
	}
	/**
	 * 新增指标目录
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
		return new ModelAndView("/input/index/idx-datainput-catalog-edit", mm);
	}
	
	/**
	 * 新增指标目录
	 * @param indexCatalogNo
	 * @return
	 */
	@RequestMapping(value = "/initUpdateCatalog", method = RequestMethod.GET)
	public ModelAndView initUpdateCatalog(String catalogId,String upName){
		
		RptInputCatalog catalog = idxDataInputBS.getRptTskCatalogByCatalogId(catalogId);
		try {
			upName = URLDecoder.decode(URLDecoder.decode(upName,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		
		ModelMap mm = new ModelMap();
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalog.getCatalogId()));
		mm.put("catalogName", StringUtils2.javaScriptEncode(catalog.getCatalogNm()));
		mm.put("remark", StringUtils2.javaScriptEncode(catalog.getRemark()));
		mm.put("upNo", StringUtils2.javaScriptEncode(catalog.getUpNo()));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		return new ModelAndView("/input/index/idx-datainput-catalog-edit", mm);
	}
	
	/**
	 * 保存指标目录
	 * @param model
	 */
	@RequestMapping(value = "/editIdxInputCatalog.*",method = RequestMethod.POST)
	public void createIdxInputCatalog(String catalogName,String remark,String upNo,String  catalogId) {
		this.idxDataInputBS.createTskCatalog(catalogId,catalogName, remark, upNo);
	}
	
	@RequestMapping(value = "/testSameInputCatalogNm", method = RequestMethod.POST)
	@ResponseBody
	public boolean testSameInputCatalogNm(String upNo,String catalogName,String catalogId){
		return  this.idxDataInputBS.testSameIndexCatalogNm( upNo, catalogName, catalogId);
	}	
	
	
	@RequestMapping(value = "/testTemplateNm", method = RequestMethod.POST)
	@ResponseBody
	public boolean testTemplateNm(String catalogId,String templateId,String templateNm){
		return  this.idxDataInputBS.testTemplateNm( catalogId, templateId, templateNm);
	}
	

	@RequestMapping(value = "/getCheckVal", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getCheckVal(String cfgId){
		return  this.idxDataInputBS.getCheckVal(cfgId);
	}
	/***
	 *  判断该指标目录下面是否具有指标
	 * @param  indexCatalogNo
	 * @return result
	 */
	@RequestMapping(value = "/canDeleteCatalog.*", method = RequestMethod.POST)
	@ResponseBody
	public String canDeleteCatalog(String catalogId) {
		return this.idxDataInputBS.canDeleteCatalog(catalogId);
	}
	
	/**
	 * 删除指标目录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delCatalog/{id}", method = RequestMethod.POST)
	public Map<String,String> delCatalog(@PathVariable("id") String id) {
		Map<String,String> resultMap=Maps.newHashMap();
		this.idxDataInputBS.batchDelCatalog(id);
		resultMap.put("msg", "0");
		return resultMap;
	}
	

	@RequestMapping(value = "/templateInfos", method = RequestMethod.GET)
	public ModelAndView templateInfos(String  nodeId,String nodeType,String templateId) {
		//初始化为根节点
		if(StringUtils.isEmpty(nodeId))
		{
			nodeType = IdxDataInputBS.CATALOG_TYPE;
			nodeId = IdxDataInputBS.DEFAULT_ROOT;
		}
		if(nodeType.equals(IdxDataInputBS.TEMPLATE_TYPE))
		{
			//查询目录下的任务信息
			templateId = StringUtils2.javaScriptEncode(templateId);
			return new ModelAndView("/input/index/idx-input-template", "templateId", templateId);
		}else{
			//如果查询的是单个任务
			return new ModelAndView("/input/index/idx-datainput-template-list");//deployTaskBS.getRptTskInfoList(pager, nodeId,null));
		}
		
	}
	@RequestMapping(value = "/getTemplateList", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getTemplateList(Pager pager,String catalogId) {
		return idxDataInputBS.getShowTemplateInfoList(pager,catalogId);
	}
	

	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/idxInputFrame" , method = RequestMethod.GET)
	public  ModelAndView idxInputFrame(String templateId,String catalogId,String catalogNm,boolean canEdit){
		
		ModelMap mm = new ModelMap();
		mm.put("canEdit",canEdit);
		try {
			if(templateId!=null)
			{
				templateId = URLDecoder.decode(URLDecoder.decode(templateId,"UTF-8"),"UTF-8");
				mm.put("templateInfo", JSON.toJSONString(idxDataInputBS.getTemplateVOs(templateId)));
			}
			if(catalogId!=null)
			{
				catalogId = URLDecoder.decode(URLDecoder.decode(catalogId,"UTF-8"),"UTF-8");
				mm.put("catalogId", StringUtils2.javaScriptEncode(catalogId));
			}
			if(catalogNm!=null)
			{
				catalogNm = URLDecoder.decode(URLDecoder.decode(catalogNm,"UTF-8"),"UTF-8");
				mm.put("catalogNm", StringUtils2.javaScriptEncode(catalogNm));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("/input/index/idx-datainput-template-frame", mm);
		
	}

	/**
	 * 删除指标目录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getTemplateTypeById.*", method = RequestMethod.POST)
	@ResponseBody
	public String   getTemplateTypeById(String templateId) {
		return  idxDataInputBS.getTemplateInfos(null,templateId, null).get(0).getTemplateType();
	}
	/**
	 * 保存维度类型
	 * @param model
	 */
	@RequestMapping(value = "/updateTemplate"  ,method = RequestMethod.POST)
	public void updateTemplate(String templateVO){

		idxDataInputBS.updateTemplate( templateVO);
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/dataInputBase" , method = RequestMethod.GET)
	public  String  dataInputBase(String templateId,String catalogId,String catalogNm){
		return  "/input/index/idx-datainput-template-base";
		
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/dataInputConfig" , method = RequestMethod.GET)
	public  String  dataInputConfig(String templateId,String catalogId,String catalogNm){
		
		return "/input/index/idx-datainput-template-config";
		
	}
	
	

	@RequestMapping(value = "/updateCfgBox" , method = RequestMethod.GET)
	public  ModelAndView updateCfgBox(String idxVal){
		idxVal = StringUtils2.javaScriptEncode(idxVal);
		return new ModelAndView("input/index/idx-datainput-template-config-dtl", "idxVal", idxVal);
	}
	
	public ModelAndView editIdxDims(String dimVal){
		dimVal = StringUtils2.javaScriptEncode(dimVal);
		return new ModelAndView("/input/index/idx-datainput-template-config-editDims", "dimVal", dimVal);
	}
	

	/**
	 * 跳转配置触发器
	 * @return
	 */
	@RequestMapping("/selectIdx")
	public String selectIdx() {
		return  "/input/index/idx-datainput-template-config-selectIdx";
	}
	/**
	 * 跳转机构选择
	 * @return
	 */
	@RequestMapping("/selectOrg")
	public ModelAndView selectOrg(String orgInfos,String rownum) {
		ModelMap map=new ModelMap();
		map.put("rownum", StringUtils2.javaScriptEncode(rownum));
		if(!StringUtils.isEmpty(orgInfos)){
			try {
				orgInfos = URLDecoder.decode(URLDecoder.decode(orgInfos,"UTF-8"),"UTF-8");
				map.put("orgInfos", StringUtils2.javaScriptEncode(orgInfos));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return  new ModelAndView("/input/index/idx-datainput-template-deptrel",map);
	}
	
	@RequestMapping(value = "/idxdimfliter", method = RequestMethod.GET)
	public ModelAndView idxdimfliter(String indexNo,String indexVerId,String type) {
		ModelMap map=new ModelMap();
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		map.put("type", StringUtils2.javaScriptEncode(type));
		return  new ModelAndView("/input/index/idx-datainput-template-config-dimfilter",map);
	}
	@RequestMapping(value = "/idxdim", method = RequestMethod.GET)
	public ModelAndView idxdim(String indexNo,String indexVerId) {
		ModelMap map=new ModelMap();
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		return  new ModelAndView("/input/index/idx-datainput-template-config-dim",map);
	}
	@RequestMapping(value = "/dimGrid", method = RequestMethod.GET)
	public String dimGrid() {
		return  "/input/index/idx-datainput-template-config-dimgrid";
	}


	/**
	 * 保存维度类型
	 * @param model
	 */
	@RequestMapping(value = "/saveTemplate"  ,method = RequestMethod.POST)
	public void saveTemplate(String templateVO){
		idxDataInputBS.saveTemplate( templateVO);
	}

	/**
	 * 删除指标目录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delTemplate/{id}", method = RequestMethod.POST)
	@ResponseBody
	public boolean delTemplate(@PathVariable("id") String id) {
		return this.idxDataInputBS.deleteTemplateById(id);
	}

	/**
	 * 删除指标目录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getOperTemplate.*", method = RequestMethod.POST)
	@ResponseBody
	public List<TableGridVO>   getOperTemplate(String templateId,String taskInstanceId) {
		return  idxDataInputBS.getTemplateToJson(templateId,taskInstanceId);
	}
	

	/**
	 * 跳转配置触发器
	 * @return
	 */
	@RequestMapping("/selectDim")
	public ModelAndView selectDim(String dimInfo,String filterInfo) {
		ModelMap map = new ModelMap();
		map.put("dimInfo", StringUtils2.javaScriptEncode(dimInfo));
		map.put("filterInfo", StringUtils2.javaScriptEncode(filterInfo));
		return  new ModelAndView("/input/index/idx-datainput-selectdim",map);
	}
	
	

	@RequestMapping(value="/getDimInfoTree")
	@ResponseBody
	public List<CommonTreeNode> getDataInputDimTree(String dimInfo,String filterInfo) {
		return this.idxDataInputBS.getDataInputDimTree(dimInfo,filterInfo,this.getContextPath());
	}

	@RequestMapping(value="/saveDataInputData")
	@ResponseBody
	public String saveDataInputData(String data){
		
		return idxDataInputBS.saveDataInputData(data);
	}

	/**
	 * 跳转到选择机构页面 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/chooseOrg", method = RequestMethod.GET)
	public ModelAndView chooseOrg(String checkbox,String rownum) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("checkbox", StringUtils2.javaScriptEncode(checkbox));
		map.put("rownum", StringUtils2.javaScriptEncode(rownum));
		return new ModelAndView(
				"/input/index/idx-datainput-choose-org", map);
	}
	
	/**
	 * 跳转到选择机构页面 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/chooseRule", method = RequestMethod.GET)
	public ModelAndView chooseRule(String indexNo,String rownum,String ruleId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("rownum", StringUtils2.javaScriptEncode(rownum));
		map.put("ruleId", StringUtils2.javaScriptEncode(ruleId));
		return new ModelAndView(
				"/input/index/idx-datainput-choose-rule", map);
	}
	
	/**
	 * 跳转到选择机构页面 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/listOrg", method = RequestMethod.GET)
	public ModelAndView listOrg(String orgFilter) {
		orgFilter = StringUtils2.javaScriptEncode(EncodeUtils.urlDecode(EncodeUtils.urlDecode(orgFilter)));
		return new ModelAndView(
				"/input/index/idx-datainput-list-org", "orgFilter", orgFilter);
	}
	/**
	 * 跳转到选择机构页面 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/selectCurr", method = RequestMethod.GET)
	public String  selectCurr() {
		return "/input/index/idx-datainput-choose-curr";
	}
	

	/**
	 * 跳转到选择机构页面 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/comment", method = RequestMethod.GET)
	public ModelAndView comment(String content) {
		Map<String, String> map = new HashMap<String, String>();
		if(content!=null){

			try {
				content = URLDecoder.decode(URLDecoder.decode(content,"UTF-8"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put("content", StringUtils2.javaScriptEncode(content));
		}
		return new ModelAndView(
				"/input/index/idx-datainput-comment", map);
//		return new ModelAndView(
//				"/input/index/idx-datainput-list-org", map);
	}
	

	
	@RequestMapping(value = "/getResult.*", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String,Object>> getResult(String taskInstanceId,String templateId){
		return idxDataInputBS.getResultInfo(taskInstanceId,templateId);
	}

	@SuppressWarnings("static-access")
	@RequestMapping(value = "/resultExport")
	@ResponseBody
	public void resultExport(HttpServletResponse response,String storeId) {
		if(storeId == null )return ;
		String dataInputExportSheetVO = DataInputExportUtil.getInstance().getAndRemoveCacheInfo(storeId);
		try {
			dataInputExportSheetVO = URLDecoder.decode(URLDecoder.decode(dataInputExportSheetVO,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idxDataInputBS.exportData(response,dataInputExportSheetVO,this.getRealPath());
	}

	@SuppressWarnings("static-access")
	@RequestMapping(value = "/saveExportSheetInfo", method = RequestMethod.POST)
	@ResponseBody
	public String  saveExportSheetInfo(String exportInfo){
		if(StringUtils.isNotEmpty(exportInfo))
		{
			try {
				exportInfo = URLDecoder.decode(URLDecoder.decode(exportInfo,"UTF-8"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String key = DataInputExportUtil.getInstance().saveCacheInfo(exportInfo);
			return JSON.toJSONString(key);
		}
		return null;
	}
	

	/**
	 * 导入逻辑系统信息
	 * 
	 * @return
	 */
	@RequestMapping("/importdatainput")
	public ModelAndView importdatainput(String templeId) {
		templeId = StringUtils2.javaScriptEncode(templeId);
		return new ModelAndView("/input/index/idx-datainput-file-upload", "templeId", templeId);
	}

	private static String UPLOAD_ATTACH_DIR = GlobalConstants4frame.IMPORT_PATH
			+ "/rpt/datainput";
	@RequestMapping("/upload")
	@ResponseBody
	public List<Map<String, Object>> upload(Uploader uploader, HttpServletResponse response)
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
			return idxDataInputBS.dealExcel(file.getAbsolutePath());
		}
		return null;
	}
	

	@RequestMapping(value = "/getAsyncTreeWithIdxType.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getAsyncTreeWithIdxType(	String id,String indexType,String childCatalogs){
			return this.idxDataInputBS.getAsyncTreeWithIdxType(this.getContextPath(), id, indexType, childCatalogs);
	}
	

	@RequestMapping(value = "/getRuleTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getRuleTree(String indexNo){
			return this.idxDataInputBS.getRuleTree(this.getContextPath(),indexNo);
	}

	
	@RequestMapping(value = "/saveRule"  ,method = RequestMethod.POST)
	@ResponseBody
	public String  saveRule(@RequestBody RptInputIdxValidate rptInputIdxValidate){
		
		return  idxDataInputBS.saveRptInputIdxValidate( rptInputIdxValidate);
	}
	
	@RequestMapping(value = "/deleteRule"  ,method = RequestMethod.POST)
	public void  deleteRule(String ruleId){
		 idxDataInputBS.deleteRptInputIdxValidate( ruleId);
	}
}
