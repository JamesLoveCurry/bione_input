package com.yusys.bione.plugin.detailtmp.web;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.FilesUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.message.entity.BioneLogAttachRel;
import com.yusys.bione.frame.message.entity.BioneLogAttachRelPK;
import com.yusys.bione.frame.message.entity.BioneMsgAttachInfo;
import com.yusys.bione.frame.message.entity.BioneMsgLog;
import com.yusys.bione.frame.message.entity.BioneMsgUserState;
import com.yusys.bione.frame.message.entity.BioneMsgUserStatePK;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.service.RptTmpBS;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpConfig;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpFilter;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpInfo;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpSearch;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpSort;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpSql;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpSum;
import com.yusys.bione.plugin.detailtmp.service.DetailTmpBS;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.spreadjs.utils.CsvExportUtil;
import com.yusys.bione.plugin.spreadjs.utils.ExcelExportUtil;

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
@RequestMapping("/report/frame/datashow/detail")
public class DetailShowController extends BaseController {

	@Autowired
	private DetailTmpBS detailBS;
	
	@Autowired
	private RptTmpBS rptTmpBS;
	
	/**
	 * 首页
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/datashow/detailshow/detail-show-index");
	}
	
	/**
	 * 排序配置
	 * @return
	 */
	@RequestMapping(value = "sortconfig",method = RequestMethod.GET)
	public ModelAndView sortconfig() {
		return new ModelAndView("/plugin/datashow/detailshow/detail-show-sort");
	}
	
	/**
	 * 列排序配置
	 * @return
	 */
	@RequestMapping(value = "colsortconfig",method = RequestMethod.GET)
	public ModelAndView colsortconfig() {
		return new ModelAndView("/plugin/datashow/detailshow/detail-show-colsort");
	}
	
	/**
	 * 列属性配置
	 * @return
	 */
	@RequestMapping(value = "attrconfig",method = RequestMethod.GET)
	public ModelAndView attrconfig(String cfgId) {
		cfgId = StringUtils2.javaScriptEncode(cfgId);
		return new ModelAndView(
				"/plugin/datashow/detailshow/detail-show-config", "cfgId", cfgId);
	}
	
	/**
	 * 查询配置
	 * @return
	 */
	@RequestMapping(value = "paramconfig",method = RequestMethod.GET)
	public ModelAndView paramconfig(String uuid) {
		uuid = StringUtils2.javaScriptEncode(uuid);
		return new ModelAndView(
				"/plugin/datashow/detailshow/detail-show-search", "uuid", uuid);
	}
	
	/**
	 * Sql查询框
	 * @return
	 */
	@RequestMapping(value = "sql",method = RequestMethod.GET)
	public ModelAndView sql() {
		return new ModelAndView(
				"/plugin/datashow/detailshow/detail-show-sql");
	}
	
	/**
	 * 筛选配置
	 * @return
	 */
	@RequestMapping(value = "filterconfig",method = RequestMethod.GET)
	public ModelAndView filterconfig() {
		return new ModelAndView("/plugin/datashow/detailshow/detail-show-filter");
	}
	
	/**
	 * 筛选配置
	 * @return
	 */
	@RequestMapping(value = "sumconfig",method = RequestMethod.GET)
	public ModelAndView sumconfig() {
		return new ModelAndView("/plugin/datashow/detailshow/detail-show-sum");
	}
	
	/**
	 * 模板信息
	 * @return
	 */
	/**
	 * 模板信息
	 * @return
	 */
	@RequestMapping(value = "tmpinfo",method = RequestMethod.GET)
	public ModelAndView tmpinfo(String templateId,String templateType,String defSrc) {
		ModelMap mm = new ModelMap();
		mm.put("templateId", StringUtils2.javaScriptEncode(templateId));
		mm.put("templateType", StringUtils2.javaScriptEncode(templateType));
		mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		return new ModelAndView(
				"/plugin/datashow/detailshow/detail-show-info", mm);
	}
	
	/**
	 * 目录信息
	 * @return
	 */
	@RequestMapping(value = "cataloginfo",method = RequestMethod.GET)
	public ModelAndView cataloginfo(String catalogId) {
		catalogId = StringUtils2.javaScriptEncode(catalogId);
		return new ModelAndView(
				"/plugin/datashow/detailshow/detail-show-catalog", "catalogId", catalogId);
	}
	
	/**
	 * 模板查询信息
	 * @return
	 */
	@RequestMapping(value = "showinfo",method = RequestMethod.GET)
	public ModelAndView showinfo(String templateId) {
		templateId = StringUtils2.javaScriptEncode(templateId);
		return new ModelAndView(
				"/plugin/datashow/detailshow/detail-show-show", "templateId", templateId);
	}
	
	/**
	 * 获取目录树
	 * @return
	 */
	@RequestMapping(value = "getCatalogTree",method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getCatalogTree() {
		return this.detailBS.getCatalogTree(this.getContextPath());
	}
	
	/**
	 * 获取模板目录树
	 * @return
	 */
	@RequestMapping(value = "getDetailTree",method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getDetailTree(String searchNm,String isSts) {
		boolean showSts = false;
		if(StringUtils.isNotBlank(isSts)){
			showSts = true;
		}
		return this.detailBS.getDetailTree(this.getContextPath(),searchNm,false,false,showSts, GlobalConstants4plugin.DETAIL_DEF_SRC_LIB);
	}
	
	
	/**
	 * 获取模板目录树
	 * @return
	 */
	@RequestMapping(value = "getAuthDetailTree",method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getAuthDetailTree(String searchNm,String defSrc,String isSts) {
		if(StringUtils.isEmpty(defSrc)){
			defSrc = GlobalConstants4plugin.DETAIL_DEF_SRC_LIB;
		}
		boolean showSts = false;
		if(StringUtils.isNotBlank(isSts)){
			showSts = true;
		}
		return this.detailBS.getDetailTree(this.getContextPath(),searchNm,false,true,showSts,defSrc);
	}
	
	/**
	 * 校验报表编号
	 * @param rptNum
	 * @param rptId
	 * @param defSrc
	 * @return
	 */
	@RequestMapping(value = "/checkRptNum", method = RequestMethod.POST)
	@ResponseBody
	public String checkRptNum(String rptNum, String rptId,String defSrc) {
		if (!StringUtils.isEmpty(rptNum)) {
			Map<String, Object> params = new HashMap<String, Object>();
			if(StringUtils.isNotBlank(defSrc)){
				params.put("defSrc", defSrc);
				if(GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)){
					params.put("defUser", BioneSecurityUtils.getCurrentUserId());
				}
			}
			else{
				params.put("defSrc", GlobalConstants4plugin.RPT_DEF_SRC_USER);
				params.put("defUser", BioneSecurityUtils.getCurrentUserId());
			}
			if (StringUtils.isEmpty(rptId)) {
				params.put("rptNum", rptNum);
				List<RptMgrReportInfo> rpts = this.rptTmpBS
						.getRptsByParams(params);
				if (rpts != null && rpts.size() > 0) {
					return "false";
				}
			} else {
				params.put("notRptId", rptId);
				params.put("rptNum", rptNum);
				List<RptMgrReportInfo> rpts = this.rptTmpBS
						.getRptsByParams(params);
				if (rpts != null && rpts.size() > 0) {
					return "false";
				}
			}
		}
		return "true";
	}
	
	/**
	 * 校验报表名称
	 * @param rptNm
	 * @param rptId
	 * @param defSrc
	 * @return
	 */
	@RequestMapping(value = "/checkRptNm", method = RequestMethod.POST)
	@ResponseBody
	public String checkRptNm(String rptNm, String rptId,String defSrc) {
		if (!StringUtils.isEmpty(rptNm)) {
			Map<String, Object> params = new HashMap<String, Object>();
			if(StringUtils.isNotBlank(defSrc)){
				params.put("defSrc", defSrc);
				if(GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)){
					params.put("defUser", BioneSecurityUtils.getCurrentUserId());
				}
			}
			else{
				params.put("defSrc", GlobalConstants4plugin.RPT_DEF_SRC_USER);
				params.put("defUser", BioneSecurityUtils.getCurrentUserId());
			}
			if (StringUtils.isEmpty(rptId)) {
				params.put("rptNm", rptNm);
				List<RptMgrReportInfo> rpts = this.rptTmpBS
						.getRptsByParams(params);
				if (rpts != null && rpts.size() > 0) {
					return "false";
				}
			} else {
				params.put("notRptId", rptId);
				params.put("rptNm", rptNm);
				List<RptMgrReportInfo> rpts = this.rptTmpBS
						.getRptsByParams(params);
				if (rpts != null && rpts.size() > 0) {
					return "false";
				}
			}
		}
		return "true";
	}
	/**
	 * 获取排列序号
	 * @param catalogId
	 * @param defSrc
	 * @return
	 */
	@RequestMapping(value = "/getRankOrder", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getRankOrder(String catalogId,String defSrc) {
		return this.detailBS.getRankOrder(catalogId,defSrc);
	}
	/**
	 * 获取模板目录树
	 * @return
	 */
	@RequestMapping(value = "getTmpInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getTmpInfo(String templateId) {
		Map<String,Object> result = new HashMap<String, Object>();
		RptDetailTmpInfo tmp = this.detailBS.getEntityById(RptDetailTmpInfo.class, templateId);
		List<RptDetailTmpConfig> configs = this.detailBS.getEntityListByProperty(RptDetailTmpConfig.class, "id.templateId", templateId,"orderno",false);
		List<RptDetailTmpSearch> searchs = this.detailBS.getEntityListByProperty(RptDetailTmpSearch.class, "id.templateId", templateId);
		List<RptDetailTmpSort> sorts = this.detailBS.getEntityListByProperty(RptDetailTmpSort.class, "id.templateId", templateId,"sortOrderno",false);
		List<RptDetailTmpSum> sums = this.detailBS.getEntityListByProperty(RptDetailTmpSum.class, "id.templateId", templateId);
		RptDetailTmpFilter filter = this.detailBS.getEntityById(RptDetailTmpFilter.class, templateId);
		RptDetailTmpSql sql = this.detailBS.getEntityById(RptDetailTmpSql.class, templateId);
		result.put("tmp", tmp);
		result.put("configs", configs);
		result.put("searchs", searchs);
		result.put("sorts", sorts);
		result.put("sums", sums);
		result.put("filter", filter);
		result.put("sql", sql);
		if(sql != null && StringUtils.isNotBlank(sql.getParamtmpId())){
			RptParamtmpInfo info = this.detailBS.getEntityById(RptParamtmpInfo.class, sql.getParamtmpId());
			result.put("param", info);
		}
		return result;
	}
	
		
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "showView",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> showView(Pager pager,String searchInfo,String filterInfo,String config,String sort,String sum,String newPageSize) {
		List<Map<String, Object>> searchAttr= new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(searchInfo)){
			searchAttr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchInfo);
		}
		Map<String, Object> filterMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(filterInfo)){
			filterMap = JSON.parseObject(filterInfo);
		}
		List<RptDetailTmpConfig> configs =new ArrayList<RptDetailTmpConfig>();
		List<RptDetailTmpSort> sorts =new ArrayList<RptDetailTmpSort>();
		List<RptDetailTmpSum> sums =new ArrayList<RptDetailTmpSum>();
		
		if(StringUtils.isNotBlank(config)){
			configs = JSON.parseArray(config, RptDetailTmpConfig.class);
		}
		if(StringUtils.isNotBlank(sort)){
			sorts = JSON.parseArray(sort, RptDetailTmpSort.class);
		}
		if(StringUtils.isNotBlank(sum)){
			sums = JSON.parseArray(sum, RptDetailTmpSum.class);
		}
		 Map<String,Object>  res = this.detailBS.showView(
				pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition(),searchAttr,configs,sorts,filterMap, sums);
		 if(StringUtils.isNotBlank(newPageSize)){
			int pageTotal = Integer.parseInt(newPageSize);
			int total = Integer.parseInt(res.get("Total").toString());
			if((pager.getPageFirstIndex()+pager.getPagesize())>pageTotal){
				int pageSize = pageTotal%pager.getPagesize();
				List<Map<String,Object>> rows = (List<Map<String, Object>>) res.get("Rows");
				res.put("Rows",rows.subList(0, pageSize) );
			}
			if(total > pageTotal)
				res.put("Total", pageTotal);
		 }
		 return res;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "showSql",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> showSql(Pager pager,String sql,String dsId,String searchArgs) {
		if(StringUtils.isNotBlank(sql)){
			List<Map<String, Object>> searchAttr= new ArrayList<Map<String,Object>>();
			sql = sql.trim();
			if(sql.endsWith(";")){
				sql = sql.substring(0, sql.length()-1);
			}
			if(StringUtils.isNotBlank(searchArgs)){
				searchAttr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchArgs);
				sql = changeSqlByParams(sql, searchAttr); 
			}
			return this.detailBS.showSql(
					pager.getPageFirstIndex(), pager.getPagesize(),
					pager.getSortname(), pager.getSortorder(),
					pager.getSearchCondition(),sql,dsId);
		}
		return new HashMap<String, Object>();
	}
	
	@RequestMapping(value = "getColumnInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getColumnInfo(String dsId,String sql) {
		String nsql = "";
		sql = sql.trim();
		if(sql.endsWith(";")){
			sql = sql.substring(0, sql.length()-1);
		}
		if(sql.toUpperCase().indexOf("WHERE")>=0){
			nsql = sql.substring(0,sql.toUpperCase().indexOf("WHERE"));
			nsql += "where 1=2";
			if(sql.toUpperCase().indexOf("GROUP BY")!=-1)
				nsql += " "+sql.substring(sql.toUpperCase().indexOf("GROUP BY"),sql.length());
		}
		else{
			nsql = sql.substring(0,(sql.toUpperCase().indexOf("GROUP BY")!=-1)?sql.toUpperCase().indexOf("GROUP BY"):sql.length());
			nsql += " where 1=2";
			if(sql.toUpperCase().indexOf("GROUP BY")!=-1)
				nsql += " "+sql.substring(sql.toUpperCase().indexOf("GROUP BY"),sql.length());
		}
		return this.detailBS.getColumnNativeSQL(dsId,nsql);
	}
	
	private String changeSqlByParams(String sql,List<Map<String, Object>> searchAttr){
		String tag = "#";
		Map<String,String> strVarMap = new HashMap<String, String>();
		if(searchAttr != null && searchAttr.size() > 0){
			for(Map<String,Object> src : searchAttr){
				strVarMap.put(src.get("name").toString(), changeValueByType(src.get("value")));
			}
		}
		if(StringUtils.isNotBlank(sql)){
			String s = new String(sql);
			String flag = StringUtils.substringBetween(s, tag);
			List<String> strList = Lists.newArrayList();
			while (flag != null) {
				strList.add(flag);
				s = StringUtils.replace(s, tag + flag + tag, "");
				flag = StringUtils.substringBetween(s, tag);
			}
			for (String val : strVarMap.keySet()) {
				sql = StringUtils.replace(sql, tag + val + tag, strVarMap.get(val) == null?"":strVarMap.get(val));
			}
		}
		return sql;
	}
	
	/**
	 * 根据参数类型赋值
	 * @param valueObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String changeValueByType(Object valueObj){
		String value = "";
		if(valueObj instanceof java.util.List){
			List<String> valueList = (List<String>) valueObj;
			StringBuilder valBuilder = new StringBuilder();
			for(String val : valueList){
				valBuilder.append("'" + val + "',");
			}
			value = valBuilder.substring(0, valBuilder.length() - 1);
		}else{
			value = valueObj.toString();
		}
		return value;
	}
	
	@RequestMapping(value = "saveTmp",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveTmp(RptDetailTmpInfo tmp,String params,String cols,String colSorts,String colSums,String filterInfo,String sql,String dsId){
		Map<String,Object> result = new HashMap<String, Object>();
		String templateId = tmp.getTemplateId();
		if(StringUtils.isEmpty(templateId)){
			PropertiesUtils pUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String autoFlag = pUtils.getProperty("autoRptNum");
			if(autoFlag.equals("Y")){
				String detail = pUtils.getProperty("detail");
				String numlength = pUtils.getProperty("numLength");
				templateId = this.detailBS.getAutoRptNum(detail, Integer.parseInt(numlength));
			}
			else{
				templateId = RandomUtils.uuid2();
				tmp.setIsCabin("0");
			}
			tmp.setTemplateId(templateId);
		}
		List<RptDetailTmpConfig> configs = new ArrayList<RptDetailTmpConfig>();
		if(StringUtils.isNotBlank(cols)){
			configs = JSON.parseArray(cols, RptDetailTmpConfig.class);
			if(configs != null && configs.size() > 0){
				for(RptDetailTmpConfig config : configs){
					config.getId().setTemplateId(templateId);
				}
			}
		}
		List<RptDetailTmpSort> sorts =new ArrayList<RptDetailTmpSort>();
		if(StringUtils.isNotBlank(colSorts)){
			sorts = JSON.parseArray(colSorts, RptDetailTmpSort.class);
			if(sorts != null && sorts.size() > 0){
				for(RptDetailTmpSort sort : sorts){
					sort.getId().setTemplateId(templateId);
				}
			}
		}
		List<RptDetailTmpSum> sums =new ArrayList<RptDetailTmpSum>();
		if(StringUtils.isNotBlank(colSums)){
			sums = JSON.parseArray(colSums, RptDetailTmpSum.class);
			if(sums != null && sums.size() > 0){
				for(RptDetailTmpSum sum : sums){
					sum.getId().setTemplateId(templateId);
				}
			}
		}
		
		List<RptDetailTmpSearch> searchs =new ArrayList<RptDetailTmpSearch>();
		if(StringUtils.isNotBlank(params)){
			searchs = JSON.parseArray(params, RptDetailTmpSearch.class);
			if(searchs != null && searchs.size() > 0){
				for(RptDetailTmpSearch search : searchs){
					search.getId().setTemplateId(templateId);
				}
			}
		}
		RptDetailTmpSql sqlInfo = null;
		if(StringUtils.isNotBlank(sql)){
			sqlInfo = JSON.parseObject(sql, RptDetailTmpSql.class);
			sqlInfo.setTemplateId(templateId);
		}
		RptDetailTmpFilter filter = null;
		if(StringUtils.isNotBlank(filterInfo)){
			filter = new RptDetailTmpFilter();
			filter.setTemplateId(templateId);
			filter.setFiltInfo(filterInfo);
		}
		tmp.setDsId(dsId);
		tmp.setDefSrc(GlobalConstants4plugin.DETAIL_DEF_SRC_LIB);
		this.detailBS.saveTmp(tmp, configs, sorts,sums, searchs,filter,sqlInfo);
		return result;
	}
	
	@RequestMapping(value = "storeTmp",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> storeTmp(RptDetailTmpInfo tmp,String params,String cols,String colSorts,String colSums,String filterInfo,String sql,String dsId){
		Map<String,Object> result = new HashMap<String, Object>();
		String templateId = tmp.getTemplateId();
		if(StringUtils.isEmpty(templateId)){
			PropertiesUtils pUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String autoFlag = pUtils.getProperty("autoRptNum");
			if(autoFlag.equals("Y")){
				String detail = pUtils.getProperty("store");
				String numlength = pUtils.getProperty("numLength");
				templateId = this.detailBS.getAutoRptNum(detail, Integer.parseInt(numlength));
			}
			else{
				templateId = RandomUtils.uuid2();
				tmp.setIsCabin("0");
			}
			tmp.setTemplateId(templateId);
		}
		List<RptDetailTmpConfig> configs = new ArrayList<RptDetailTmpConfig>();
		if(StringUtils.isNotBlank(cols)){
			configs = JSON.parseArray(cols, RptDetailTmpConfig.class);
			if(configs != null && configs.size() > 0){
				int i = 0;
				for(RptDetailTmpConfig config : configs){
					config.getId().setTemplateId(templateId);
					if(config.getOrderno() == null){
						config.setOrderno(new BigDecimal(i));
					}
				}
				i++;
			}
		}
		List<RptDetailTmpSort> sorts =new ArrayList<RptDetailTmpSort>();
		if(StringUtils.isNotBlank(colSorts)){
			sorts = JSON.parseArray(colSorts, RptDetailTmpSort.class);
			if(sorts != null && sorts.size() > 0){
				for(RptDetailTmpSort sort : sorts){
					sort.getId().setTemplateId(templateId);
				}
			}
		}
		List<RptDetailTmpSum> sums =new ArrayList<RptDetailTmpSum>();
		if(StringUtils.isNotBlank(colSums)){
			sums = JSON.parseArray(colSums, RptDetailTmpSum.class);
			if(sums != null && sums.size() > 0){
				for(RptDetailTmpSum sum : sums){
					sum.getId().setTemplateId(templateId);
				}
			}
		}
		
		List<RptDetailTmpSearch> searchs =new ArrayList<RptDetailTmpSearch>();
		if(StringUtils.isNotBlank(params)){
			searchs = JSON.parseArray(params, RptDetailTmpSearch.class);
			if(searchs != null && searchs.size() > 0){
				for(RptDetailTmpSearch search : searchs){
					search.getId().setTemplateId(templateId);
				}
			}
		}
		RptDetailTmpSql sqlInfo = null;
		if(StringUtils.isNotBlank(sql)){
			sqlInfo = JSON.parseObject(sql, RptDetailTmpSql.class);
			sqlInfo.setTemplateId(templateId);
		}
		RptDetailTmpFilter filter = null;
		if(StringUtils.isNotBlank(filterInfo)){
			filter = new RptDetailTmpFilter();
			filter.setTemplateId(templateId);
			filter.setFiltInfo(filterInfo);
		}
		tmp.setCatalogId("0");
		tmp.setDefSrc(GlobalConstants4plugin.DETAIL_DEF_SRC_USER);
		tmp.setSrcId(BioneSecurityUtils.getCurrentUserId());
		tmp.setDsId(dsId);
		this.detailBS.saveTmp(tmp, configs, sorts,sums, searchs,filter,sqlInfo);
		return result;
	}
	
	
	
	@RequestMapping(value = "updateTmp",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> updateTmp(String templateId,String params,String cols,String colSorts,String colSums,String filterInfo,String sql){
		Map<String,Object> result = new HashMap<String, Object>();
		List<RptDetailTmpConfig> configs = new ArrayList<RptDetailTmpConfig>();
		if(StringUtils.isNotBlank(cols)){
			configs = JSON.parseArray(cols, RptDetailTmpConfig.class);
		}
		List<RptDetailTmpSort> sorts =new ArrayList<RptDetailTmpSort>();
		if(StringUtils.isNotBlank(colSorts)){
			sorts = JSON.parseArray(colSorts, RptDetailTmpSort.class);
		}
		List<RptDetailTmpSum> sums =new ArrayList<RptDetailTmpSum>();
		if(StringUtils.isNotBlank(colSums)){
			sums = JSON.parseArray(colSums, RptDetailTmpSum.class);
		}
		List<RptDetailTmpSearch> searchs =new ArrayList<RptDetailTmpSearch>();
		if(StringUtils.isNotBlank(params)){
			searchs = JSON.parseArray(params, RptDetailTmpSearch.class);
		}
		RptDetailTmpSql sqlInfo = null;
		if(StringUtils.isNotBlank(sql)){
			sqlInfo = JSON.parseObject(sql, RptDetailTmpSql.class);
		}
		RptDetailTmpFilter filter = null;
		if(StringUtils.isNotBlank(filterInfo)){
			filter = new RptDetailTmpFilter();
			filter.setTemplateId(templateId);
			filter.setFiltInfo(filterInfo);
		}
		this.detailBS.updateTmp(templateId, configs, sorts,sums, searchs,filter,sqlInfo);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/getExpDataCount")
	@ResponseBody
	public Map<String,Integer> getExpDateCount(HttpServletResponse response,String searchInfo,String filterInfo,String config,String sort,String sum,String sql,String dsId) {
		List<Map<String, Object>> searchAttr= new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(searchInfo)){
			searchAttr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchInfo);
		}
		Map<String, Object> filterMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(filterInfo)){
			filterMap = JSON.parseObject(filterInfo);
		}
		List<RptDetailTmpConfig> configs =new ArrayList<RptDetailTmpConfig>();
		List<RptDetailTmpSort> sorts =new ArrayList<RptDetailTmpSort>();
		List<RptDetailTmpSum> sums =new ArrayList<RptDetailTmpSum>();
		if(StringUtils.isNotBlank(config)){
			configs = JSON.parseArray(config, RptDetailTmpConfig.class);
		}
		if(StringUtils.isNotBlank(sort)){
			sorts = JSON.parseArray(sort, RptDetailTmpSort.class);
		}
		if(StringUtils.isNotBlank(sort)){
			sums = JSON.parseArray(sum, RptDetailTmpSum.class);
		}
		return this.detailBS.getExpDataCount(searchAttr, configs, sorts, filterMap, sums, sql, dsId);
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/resultExport")
	@ResponseBody
	public Map<String,Object> resultExport(HttpServletResponse response,String searchInfo,String filterInfo,String config,String sort,String sum,String isBigData,String pagesize) {
		Map<String,Object> result = new HashMap<String, Object>();
//		File file = new File(this.getRealPath() + "/export/frame/detail/");
		File file = new File(this.findShareCatalogPath() + "/export/frame/detail/");
		if(!file.exists()){
			file.mkdirs();
		}
		List<Map<String, Object>> searchAttr= new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(searchInfo)){
			searchAttr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchInfo);
		}
		Map<String, Object> filterMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(filterInfo)){
			filterMap = JSON.parseObject(filterInfo);
		}
		List<RptDetailTmpConfig> configs =new ArrayList<RptDetailTmpConfig>();
		List<RptDetailTmpSort> sorts =new ArrayList<RptDetailTmpSort>();
		List<RptDetailTmpSum> sums =new ArrayList<RptDetailTmpSum>();
		
		if(StringUtils.isNotBlank(config)){
			configs = JSON.parseArray(config, RptDetailTmpConfig.class);
		}
		if(StringUtils.isNotBlank(sort)){
			sorts = JSON.parseArray(sort, RptDetailTmpSort.class);
		}
		if(StringUtils.isNotBlank(sort)){
			sums = JSON.parseArray(sum, RptDetailTmpSum.class);
		}
		LinkedHashMap<String,String> columnInfo = new LinkedHashMap<String, String>();
		if(configs != null && configs.size()>0){
			for(RptDetailTmpConfig configInfo : configs){
				columnInfo.put(configInfo.getId().getCfgId(),configInfo.getDisplayNm());
			}
		}
		List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
		String afterPath = "/export/frame/detail/" + RandomUtils.uuid2() + ".xlsx";
		ExcelExportUtil util = null;
		if(StringUtils.isNotBlank(pagesize)){
			rows = (List<Map<String, Object>>) this.detailBS.showView(0,Integer.parseInt(pagesize),null,null,null,searchAttr, configs, sorts, filterMap,sums).get("Rows");
			util = new ExcelExportUtil(response, rows,columnInfo ,this.findShareCatalogPath() + afterPath);
			result.put("path", util.createFile("明细查询"));
		}
		else{
			util = new ExcelExportUtil(response, rows,columnInfo ,this.findShareCatalogPath() + afterPath);
			util.startCreate("明细查询");
			this.detailBS.showView(searchAttr, configs, sorts, filterMap,sums,util);
			result.put("path", util.finish());
		}
		this.creAttach(this.findShareCatalogPath() + afterPath,"xlsx",isBigData);
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/csvExport")
	@ResponseBody
	public Map<String,Object> csvExport(HttpServletResponse response,String searchInfo,String filterInfo,String config,String sort,String sum,String isBigData,String pagesize) {
		Map<String,Object> result = new HashMap<String, Object>();
//		File file = new File(this.getRealPath() + "/export/frame/detail/");
		File file = new File(this.findShareCatalogPath() + "/export/frame/detail/");
		if(!file.exists()){
			file.mkdirs();
		}
		List<Map<String, Object>> searchAttr= new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(searchInfo)){
			searchAttr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchInfo);
		}
		Map<String, Object> filterMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(filterInfo)){
			filterMap = JSON.parseObject(filterInfo);
		}
		List<RptDetailTmpConfig> configs =new ArrayList<RptDetailTmpConfig>();
		List<RptDetailTmpSort> sorts =new ArrayList<RptDetailTmpSort>();
		List<RptDetailTmpSum> sums =new ArrayList<RptDetailTmpSum>();
		if(StringUtils.isNotBlank(config)){
			configs = JSON.parseArray(config, RptDetailTmpConfig.class);
		}
		if(StringUtils.isNotBlank(sort)){
			sorts = JSON.parseArray(sort, RptDetailTmpSort.class);
		}
		if(StringUtils.isNotBlank(sort)){
			sums = JSON.parseArray(sum, RptDetailTmpSum.class);
		}
		LinkedHashMap<String,String> columnInfo = new LinkedHashMap<String, String>();
		if(configs != null && configs.size()>0){
			for(RptDetailTmpConfig configInfo : configs){
				columnInfo.put(configInfo.getId().getCfgId(),configInfo.getDisplayNm());
			} 
		}
		String afterPath = "/export/frame/detail/" + RandomUtils.uuid2() + ".csv";
		//2020 lcy 【后台管理】任意文件类型下载 代码优化
		FilesUtils.checkFilePathAndName(afterPath);
		
		CsvExportUtil util = null;
		List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(pagesize)){
			rows = (List<Map<String, Object>>) this.detailBS.showView(0,Integer.parseInt(pagesize),null,null,null,searchAttr, configs, sorts, filterMap,sums).get("Rows");
			util = new CsvExportUtil(response, rows,columnInfo ,this.findShareCatalogPath() + afterPath);
			result.put("path", util.createFile("明细查询"));
		}
		else{
			util = new CsvExportUtil(response, rows,columnInfo ,this.findShareCatalogPath() + afterPath);
			util.createCsv("明细查询");
			this.detailBS.showView(searchAttr, configs, sorts, filterMap,sums,util);
			result.put("path", util.finish());
		}
		this.creAttach(afterPath,"csv",isBigData);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/sqlExport")
	@ResponseBody
	public Map<String,Object> sqlExport(HttpServletResponse response,String sql,String searchArgs,String dsId,String isBigData,String pagesize) {
//		File file = new File(this.getRealPath() + "/export/frame/detail/");
		File file = new File(this.findShareCatalogPath() + "/export/frame/detail/");
		if(!file.exists()){
			file.mkdirs();
		}
		Map<String,Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> searchAttr= new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(searchArgs)){
			searchAttr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchArgs);
			sql = changeSqlByParams(sql, searchAttr); 
		}
		List<String> columns = (List<String>) this.detailBS.getColumnNativeSQL(dsId, sql).get("columns");
		LinkedHashMap<String,String> columnInfo = new LinkedHashMap<String, String>();
		if(columns != null && columns.size()>0){
			int i = 0;
			for(String column : columns){
				columnInfo.put("param"+i,column);
				i++;
			}
		}
		List<Map<String,Object>> rows  = new ArrayList<Map<String,Object>>();
		String afterPath = "/export/frame/detail/" + RandomUtils.uuid2() + ".xlsx";
		ExcelExportUtil util = null;
		if(StringUtils.isNotBlank(pagesize)){
			rows = (List<Map<String, Object>>) this.detailBS.showSql(0,Integer.parseInt(pagesize),null,null,null,sql,dsId);
			util = new ExcelExportUtil(response, rows,columnInfo ,this.findShareCatalogPath() + afterPath);
			result.put("path", util.createFile("明细查询"));
		}
		else{
			util = new ExcelExportUtil(response, rows,columnInfo ,this.findShareCatalogPath() + afterPath);
			util.startCreate("明细查询");
			this.detailBS.showSql(sql, dsId,util);
			result.put("path", util.finish());
		}
		this.creAttach(afterPath,"xlsx",isBigData);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/sqlCsvExport")
	@ResponseBody
	public Map<String,Object> sqlCsvExport(HttpServletResponse response,String sql,String searchArgs,String dsId,String isBigData,String pagesize) {
//		File file = new File(this.getRealPath() + "/export/frame/detail/");
		File file = new File(this.findShareCatalogPath() + "/export/frame/detail/");
		if(!file.exists()){
			file.mkdirs();
		}
		Map<String,Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> searchAttr= new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(searchArgs)){
			searchAttr = (List<Map<String, Object>>)(List<?>)JSON.parseArray(searchArgs);
			sql = changeSqlByParams(sql, searchAttr); 
		}
		List<String> columns = (List<String>) this.detailBS.getColumnNativeSQL(dsId, sql).get("columns");
		LinkedHashMap<String,String> columnInfo = new LinkedHashMap<String, String>();
		if(columns != null && columns.size()>0){
			int i = 0;
			for(String column : columns){
				columnInfo.put("param"+i,column);
				i++;
			}
		}
		String afterPath = "/export/frame/detail/" + RandomUtils.uuid2() + ".csv";
		List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
		CsvExportUtil util = null;
		if(StringUtils.isNotBlank(pagesize)){
			rows = (List<Map<String, Object>>) this.detailBS.showSql(0,Integer.parseInt(pagesize),null,null,null,sql,dsId);
			util = new CsvExportUtil(response, rows,columnInfo ,this.findShareCatalogPath() + afterPath);
			result.put("path", util.createFile("明细查询"));
		}
		else{
			util = new CsvExportUtil(response, rows,columnInfo ,this.findShareCatalogPath() + afterPath);
			util.createCsv("明细查询");
			this.detailBS.showSql(sql, dsId,util);
			result.put("path", util.finish());
		}
		this.creAttach(afterPath,"csv",isBigData);
		return result;
	}
	
	@RequestMapping("/download")
	public void download(HttpServletResponse response,String path,String attachId) {
		if (FilepathValidateUtils.validateFilepath(path)) {
			File file=new File(path);
			try {
				DownloadUtils.download(response, file);
			}
			catch(Exception e){
				
			}
			finally{
				file.delete();
				this.detailBS.delAttachRel(attachId);
			}
		}
	}
	/**
	 * 生成附件和消息及附件关系
	 * @param path
	 */
	public void creAttach(String afterPath,String fileType,String isBigData) {
		if("Y".equals(isBigData)){
			String msgId = RandomUtils.uuid2();
			String attachId = RandomUtils.uuid2();
			BioneMsgLog msg = new BioneMsgLog();
			msg.setMsgId(msgId);
			msg.setSendTypeNo("f39e865d7b214ea5afb0f2f9bb9a5cbe");
			msg.setMsgDetail("明细灵活查询导出"+fileType+"文件");
			msg.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			msg.setMsgTitle("导出"+fileType+"文件");
			msg.setSendTime(new Timestamp(System.currentTimeMillis()));
			msg.setViewSts("1");//未读
			msg.setMsgType("03");//03下载
			msg.setReceiveUser(BioneSecurityUtils.getCurrentUserId());
			msg.setSendUser(BioneSecurityUtils.getCurrentUserId());
			BioneMsgAttachInfo att = new BioneMsgAttachInfo();
			att.setAttachId(attachId);
			att.setAttachName(fileType+"数据附件");
			att.setAttachPath(afterPath);
			att.setAttachSts("1");
			att.setAttachType(fileType);
			BioneLogAttachRel rel = new BioneLogAttachRel();
			BioneLogAttachRelPK relPk = new BioneLogAttachRelPK();
			relPk.setAttachId(attachId);
			relPk.setMsgId(msgId);
			rel.setId(relPk);
			BioneMsgUserState sta = new BioneMsgUserState();
			BioneMsgUserStatePK staPk = new BioneMsgUserStatePK();
			staPk.setUserId(BioneSecurityUtils.getCurrentUserId());
			staPk.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			sta.setId(staPk);
			sta.setMsgSts("1");
			this.detailBS.saveEntity(msg);
			this.detailBS.saveEntity(att);
			this.detailBS.saveEntity(rel);
			this.detailBS.saveOrUpdateEntity(sta);
		}
	}

	/**
	 * 获取
	 * @return
	 */
	@RequestMapping(value = "getSetId",method = RequestMethod.POST)
	@ResponseBody
	public List<String> getSetId(String setIds) {
		return this.detailBS.getSetId(setIds);
	}
	
	/**
	 * 获取setId对应的srcCode
	 * @return
	 */
	@RequestMapping(value = "getSrcCode",method = RequestMethod.GET)
	public Map<String, String> getSrcCode(String setId) {
		return this.detailBS.getSrcCode(setId);
	}
	
	/**
	 * 读取文件上传路径配置文件
	 */
	public String findShareCatalogPath(){
		PropertiesUtils path = PropertiesUtils.get("fileupload.properties");
		String dstAllPath = "/udas2_nas";
		if(null != path.getProperty("filePath")){
			dstAllPath = path.getProperty("filePath");
		}
		return dstAllPath;
	}
}
