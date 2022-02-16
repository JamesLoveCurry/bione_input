package com.yusys.bione.plugin.rptidx.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.common.CommonDupontNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.DupontExcelExporter;
import com.yusys.bione.plugin.rptidx.service.RptIdxAnalysisBS;

@Controller
@RequestMapping("rpt/frame/idx/idxanalysis")
public class RptIdxAnalysisController extends BaseController {
	@Autowired
	private RptIdxAnalysisBS rptAlyBS;

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/idxanalysis/idx-analysis-index";
	}
	
	@RequestMapping(value="new",method = RequestMethod.GET)
	public String newindex() {
		return "/plugin/idxanalysis/idx-analysis-index-new";
	}

	@RequestMapping(value = "/tab", method = RequestMethod.GET)
	public ModelAndView tab(String id, String type) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("type", StringUtils2.javaScriptEncode(type));
		return new ModelAndView("/plugin/idxanalysis/idx-analysis-tab",
				map);
	}
	
	@RequestMapping(value = "/relTab", method = RequestMethod.GET)
	public ModelAndView relTab(String id, String type,String flag) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("type", StringUtils2.javaScriptEncode(type));
		map.put("flag", StringUtils2.javaScriptEncode(flag));
		return new ModelAndView("/plugin/idxanalysis/idx-analysis-rel",
				map);
	}


	/**
	 * 获取关联报表信息
	 * 
	 * @return
	 * @throws SQLException 
	 */
	@RequestMapping(value = "/rptList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> rptList(String id, String type) throws SQLException {
		return this.rptAlyBS.getRptList(id, type);
	}
	
	/**
	 * 获取关联指标信息
	 * 
	 * @return
	 * @throws SQLException 
	 */
	@RequestMapping(value = "/idxList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> idxList(Pager pager, String id, String type,String flag) throws SQLException {
		return this.rptAlyBS.getIdxList(pager, id, type, flag, null, null, null, null, null);
	}
	
	/**
	 * 获取关联维度信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dimList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> dimList(String id, String type) {
		return this.rptAlyBS.getDimList(id, type);
	}
	
	

	@RequestMapping(value = "idxView", method = RequestMethod.GET)
	public ModelAndView idxTab(String id) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("type", "idx");
		map.put("flag", "rel");
		return new ModelAndView("/plugin/idxanalysis/idx-analysis-viewidx",
				map);
	}
	
	@RequestMapping(value = "idxDetail", method = RequestMethod.GET)
	public ModelAndView idxDetail(String indexNo) {
		indexNo = StringUtils2.javaScriptEncode(indexNo);
		return new ModelAndView("/plugin/idxanalysis/idx-analysis-detailidx",
				"id", indexNo);
	}
	
	
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public Map<String,String> export(String dupontNode) {
		Map<String,String> result = new HashMap<String, String>();
		CommonDupontNode node = JSON.parseObject(dupontNode, CommonDupontNode.class);
		result.put("fileName", exportExcel(node));
		result.put("name", node.getText());
		return result;
	}
	
	@RequestMapping(value = "download")
	public void download(String fileName,String name,HttpServletResponse response) {
		if (FilepathValidateUtils.validateFilepath(fileName)) {
			File file = new File(fileName);
			try {
				name = URLDecoder.decode(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				name = "指标追溯";
			}
			try {
				DownloadUtils.download(response, file,  name+".xlsx");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			file.delete();
		}
	}
	
	
	private String exportExcel(CommonDupontNode node){
		File file = new File(this.getRealPath() + "/export/frame/idxaly/");
		if(!file.exists()){
			file.mkdirs();
		}
		String path = this.getRealPath()+ "/export/frame/idxaly/"+ RandomUtils.uuid2() + ".xlsx";
		DupontExcelExporter ex;
		try {
			ex = new DupontExcelExporter(path, node);
			ex.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return path;
	}
	/**
	 * 获取关联指标信息
	 * 
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/idxViewList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> idxView(Pager pager, String id, String type, String searchArgs, String json, String flag, String dataDate, String dataUnit, String dataPrecision) throws SQLException {
		List<Map<String, Object>> argArr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchArgs);
		return this.rptAlyBS.getIdxList(pager, id, type, flag, argArr, json, dataDate, dataUnit, dataPrecision);
	}
	
	/**
	 * 获取关联指标明细
	 * 
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/idxDetailList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> idxDetail(Pager pager, String id, String type,String searchArgs) throws SQLException {
		List<Map<String, Object>> argArr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchArgs);
		return this.rptAlyBS.getIdxDetailList(pager, id, argArr);
	}
	
	@RequestMapping(value = "idxTab", method = RequestMethod.GET)
	public ModelAndView idxView(String id, String type,String flag) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("type", StringUtils2.javaScriptEncode(type));
		map.put("flag", StringUtils2.javaScriptEncode(flag));
		return new ModelAndView("/plugin/idxanalysis/idx-analysis-idx",
				map);
	}

	@RequestMapping(value = "rptTab", method = RequestMethod.GET)
	public ModelAndView rptTab(String id, String type,String flag) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("type", StringUtils2.javaScriptEncode(type));
		map.put("flag", StringUtils2.javaScriptEncode(flag));
		return new ModelAndView("/plugin/idxanalysis/idx-analysis-rpt",
				map);
	}

	@RequestMapping(value = "dimTab", method = RequestMethod.GET)
	public ModelAndView dimTab(String id, String type,String flag) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("type", StringUtils2.javaScriptEncode(type));
		map.put("flag", StringUtils2.javaScriptEncode(flag));
		return new ModelAndView("/plugin/idxanalysis/idx-analysis-dim",
				map);
	}
	
	@RequestMapping(value = "modelTab", method = RequestMethod.GET)
	public ModelAndView modelTab(String id, String type,String flag) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("type", StringUtils2.javaScriptEncode(type));
		map.put("flag", StringUtils2.javaScriptEncode(flag));
		return new ModelAndView("/plugin/idxanalysis/idx-analysis-model",
				map);
	}
}
