package com.yusys.bione.plugin.detailtmp.web;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.FilesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.design.util.DataDealUtils;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpCatalog;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpInfo;
import com.yusys.bione.plugin.detailtmp.service.DetailTmpBS;
import com.yusys.bione.plugin.detailtmp.web.vo.RptDetailTmpInfoVO;

/**
 * <pre>
 * Title: RptShowController
 * Descriptsion:
 * </pre>
 * 
 * @author weijx weijx@yusys.com.cn
 * @version 1.00.00
 */
@Controller
@RequestMapping("/report/frame/detailtmp")
public class DetailTmpController extends BaseController {
	private static String UPLOAD_ATTACH_DIR = GlobalConstants4frame.IMPORT_PATH
			+ "/rpt/require";
	
	@Autowired
	private DetailTmpBS detailBS;
	
	/**
	 * 首页
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView(
				"/plugin/detailtmp/detail-tmp-index");
	}
	
	/**
	 * 列表页面
	 */
	@RequestMapping(value = "expDetail" , method = RequestMethod.GET)
	public ModelAndView expDetail() {
		return new ModelAndView(
				"/plugin/detailtmp/detail-come-export");
	}
	
	/**
	 * 列表页面
	 */
	@RequestMapping(value = "impDetail" , method = RequestMethod.GET)
	public ModelAndView impDetail() {
		return new ModelAndView(
				"/plugin/detailtmp/detail-imp-index");
	}
	

	/**
	 * 导入数据(文件上传)
	 * 
	 */
	@RequestMapping("/impUpload")
	public ModelAndView impUpload() {
		return new ModelAndView("/plugin/detailtmp/detail-imp-upload");
	}

	/**
	 * 导入数据(grid展示)
	 * 
	 */
	@RequestMapping("/impGrid")
	public ModelAndView impGrid() {
		return new ModelAndView("/plugin/detailtmp/detail-imp-grid");
	}

	/**
	 * 导入数据(数据源选择展示)
	 * 
	 */
	@RequestMapping(value = "/dataSet", method = RequestMethod.GET)
	public ModelAndView dataSet() {
		return new ModelAndView("/plugin/detailtmp/detail-imp-dataset");
	}
	
	/**
	 * 导入
	 * 
	 * @return
	 */
	@RequestMapping(value = "/upload", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String upload(Uploader uploader, HttpServletResponse response)
			throws Exception {
		File file = null;
		try {
			file = this.uploadFile(uploader, UPLOAD_ATTACH_DIR
					+ File.separatorChar, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		if (file != null) {
			logger.info("文件[" + file.getName() + "]上传完成");
			List<String> fieldNms = new ArrayList<String>();
			fieldNms.add("templateNm");
			return DataDealUtils.getDataInfo(file,
					RptDetailTmpInfo.class.getName(), fieldNms);
		}
		return null;
	}
	
	@RequestMapping("/saveImport")
	@ResponseBody
	public Map<String, Object> saveImport(String pathname, String dsId)
			throws IOException, ClassNotFoundException {
		return DataDealUtils.saveImport(pathname, dsId);
	}
	
	/**
	 * 列表页面
	 */
	@RequestMapping(value = "content" , method = RequestMethod.GET)
	public ModelAndView content() {
		return new ModelAndView(
				"/plugin/detailtmp/detail-tmp-content");
	}
	
	
	/**
	 * 列表页面
	 */
	@RequestMapping(value = "add" , method = RequestMethod.GET)
	public ModelAndView add(String templateId,String catalogId) {
		ModelMap mm = new ModelMap();
		if(StringUtils.isNotBlank(catalogId)){
			RptDetailTmpCatalog catalog = this.detailBS.getEntityById(RptDetailTmpCatalog.class,catalogId);
			if(catalog !=null){
				mm.put("catalogId", StringUtils2.javaScriptEncode(catalogId));
				mm.put("catalogNm", StringUtils2.javaScriptEncode(catalog.getCatalogNm()));
			}
		}
		mm.put("templateId", StringUtils2.javaScriptEncode(templateId));
		return new ModelAndView(
				"/plugin/detailtmp/detail-tmp-add", mm);
	}
	
	@RequestMapping("/exportTmpInfo")
	public void exportTmp(HttpServletResponse response, String filepath)
			throws Exception {
		//2020 lcy 【后台管理】任意文件类型下载 代码优化
		if (FilepathValidateUtils.validateFilepath(filepath)) {
			File file = new File(filepath);
			DownloadUtils.download(response, file, "明细导出数据.txt");
			file.delete();
		}
		
	}
	
	/**
	 * 目录编辑
	 */
	@RequestMapping(value = "catalogEdit",method = RequestMethod.GET)
	public ModelAndView catalogEdit(String catalogId) {
		catalogId = StringUtils2.javaScriptEncode(catalogId);
		return new ModelAndView(
				"/plugin/detailtmp/detail-tmp-catalog", "catalogId", catalogId);
	}
	
	/**
	 * 获取目录信息
	 */
	@RequestMapping(value="getCatalogInfo")
	@ResponseBody
	public RptDetailTmpCatalog getCatalogInfo(String catalogId) {
		return this.detailBS.getEntityById(RptDetailTmpCatalog.class, catalogId);
	}
	
	@RequestMapping(value="saveCatalog" ,method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveCatalog(RptDetailTmpCatalog catalog){
		Map<String,Object> res = new HashMap<String, Object>();
		if(!StringUtils.isNotBlank(catalog.getCatalogId())){
			String catalogId = RandomUtils.uuid2();
			catalog.setCatalogId(catalogId);
		}
		this.detailBS.saveOrUpdateEntity(catalog);
		res.put("catalogId", catalog.getCatalogId());
		return res;
	}
	
	@RequestMapping(value="deleteCatalog" ,method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> deleteCatalog(String catalogId){
		Map<String,Object> res = new HashMap<String, Object>();
		List<RptDetailTmpInfo> list = this.detailBS.getEntityListByProperty(RptDetailTmpInfo.class, "catalogId", catalogId);
		if(list.size()>0){
			res.put("msg", "error");
		}
		else{
			this.detailBS.deleteCatalog(catalogId);
			res.put("msg", "ok");
		}
		return res;
	}
	
	@RequestMapping(value="changeSts" )
	public void changeSts(String templateId,String templateSts){
		this.detailBS.changeSts(templateId, templateSts);
	}
	
	@RequestMapping(value = "list",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> list(Pager pager,String catalogId) {
		return this.detailBS.list(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition(),catalogId);
	}
	
	@RequestMapping(value = "getTmpInfo",method = RequestMethod.POST)
	@ResponseBody
	public RptDetailTmpInfoVO getTmpInfo(String templateId) {
		RptDetailTmpInfoVO tmp = this.detailBS.getTmpInfo(templateId);
		if(tmp == null)
			tmp = new RptDetailTmpInfoVO(new RptDetailTmpInfo(),"");
		return tmp;
	}
	
	@RequestMapping(value = "deleteTmpInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> deleteTmpInfo(String templateId) {
		Map<String,Object> result = new HashMap<String, Object>();
		try{
			String templates[] = StringUtils.split(templateId, ",");
			for(int i = 0; i < templates.length; i++){
				this.detailBS.deleteTmpInfo(templates[i]);
			}
			result.put("msg", "ok");
		}
		catch(Exception e){
			result.put("msg", "error");
		}
		return result;
		
	}

	@RequestMapping("/exportTmp")
	@ResponseBody
	public Map<String, Object> exportTmp(String rptIds) throws Exception {
		return DataDealUtils.exportImport(rptIds, this.getRealPath(),
				"exportDetail");
	}
}
