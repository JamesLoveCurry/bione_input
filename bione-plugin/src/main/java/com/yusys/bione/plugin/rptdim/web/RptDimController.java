package com.yusys.bione.plugin.rptdim.web;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.DEFAULT_TREE_ROOT_NO;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.IMPORT_PATH;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COSTOMED_DIM_PREFIX;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DESIGN_EXPORT_PATH;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DIM_TYPE_BUSI;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DIM_TYPE_CURRENCY;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DIM_TYPE_DATE;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DIM_TYPE_ORG;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DIM_TYPE_STRUCT_LIST;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.EXPORT_DIM_TEMPLATE_PATH;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.INDEX_STS_START;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.ReflectionUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.XlsExcelTemplateExporter;
import com.yusys.bione.plugin.design.util.DataDealUtils;
import com.yusys.bione.plugin.rptdim.entity.RptDimCatalog;
import com.yusys.bione.plugin.rptdim.entity.RptDimHistoryInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.service.RptDimBS;
import com.yusys.bione.plugin.rptdim.web.vo.RptDimItemInfoVO;
import com.yusys.bione.plugin.wizard.web.vo.DimImportVO;
/**
 * <pre>
 * Title:维度维护
 * Description: 维度维护
 * </pre>
 * 
 * @author aman aman@yuchengtech.com
 * @version 1.00.00
 * 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：彭伟		  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/dimCatalog")
public class RptDimController extends BaseController {
	protected static Logger log = LoggerFactory.getLogger(RptDimController.class);
	@Autowired
	private RptDimBS rptDimBS;
	
	private  static  final  Map<String,String>   DIM_TYPE_STRUCT_MAP  =  Maps.newHashMap();
	
	private static String UPLOAD_ATTACH_DIR = IMPORT_PATH
			+ "/rpt/require";
	
	public   RptDimController(){
		DIM_TYPE_STRUCT_MAP.put(DIM_TYPE_STRUCT_LIST, "列表");
		DIM_TYPE_STRUCT_MAP.put(DIM_TYPE_STRUCT_TREE, "树形");
	}
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/rptdim/rpt-dim-catalog-index";
	}
	
	@RequestMapping(value="/dimItem",method = RequestMethod.GET)
	public ModelAndView dimItemIndex(String dimTypeNo,String flag) {
		ModelMap mm = new ModelMap();
      	mm.put("dimTypeNo", StringUtils2.javaScriptEncode(dimTypeNo));
      	if(flag!=null&&!flag.equals("")){
      		mm.put("flag", "noBtnFlag");
      	}
      	
      	RptDimTypeInfo  info  =   this.rptDimBS.findDimTypeInfoById(dimTypeNo);
      	if(info==null)  
      		return new ModelAndView("/plugin/rptdim/rpt-dim-item-index-list",mm);
      	if(info.getDimTypeStruct()!=null&&info.getDimTypeStruct().equals(DIM_TYPE_STRUCT_LIST))
      		mm.put("isList", "isList");
	     return new ModelAndView("/plugin/rptdim/rpt-dim-item-index-list",mm);
	}

	@RequestMapping("/listcatalogs.*")
	@ResponseBody
	public List<CommonTreeNode> listcatalogs(String searchText,String  exceptTypeIds,String  checkedTypeIds,String  getAllTypeInfo,String preview,String isAddRecordIndex) {
		if(searchText==null){
			searchText="";
		}
		if(exceptTypeIds==null){
			exceptTypeIds="";
		}
		if(checkedTypeIds==null){
			checkedTypeIds="";
		}
		Map<String,Object> params =   Maps.newHashMap();
		params.put("searchText","%"+searchText+"%");
		params.put("exceptTypeIds", exceptTypeIds);
		params.put("checkedTypeIds", checkedTypeIds);
		params.put("preview", preview);
		params.put("getAllTypeInfo", getAllTypeInfo);
		List<String>  dimTypeList  =  Lists.newArrayList();  
		if(getAllTypeInfo==null){
			dimTypeList.add(DIM_TYPE_BUSI);
			dimTypeList.add(DIM_TYPE_CURRENCY);
			if("1".equals(isAddRecordIndex)){
				dimTypeList.add(DIM_TYPE_ORG);
				dimTypeList.add(DIM_TYPE_DATE);
			}
		   params.put("dimTypeList", dimTypeList);
		}
		List<CommonTreeNode> nodes = rptDimBS.getDimTree(params);
     	if(getAllTypeInfo!=null){
     		nodes.get(0).getParams().put("open", "open");
		}
		return nodes;
	}
	
	
	@RequestMapping("/listCatalogTree.*")
	@ResponseBody
	public List<CommonTreeNode> listCatalogTree() {
		List<CommonTreeNode> nodes = rptDimBS.getCatalogTree();
		return nodes;
	}
	
	/**
	 * 构造树型列表数据
	 */
	@RequestMapping(value = "/findDimItemListByTypeNo.*")
	@ResponseBody
	public Map<String, Object> findDimItemListByTypeNo(Pager pager,  String dimTypeNo) {
		if(pager.getCondition()==null){
			pager.setCondition("{\"op\":\"and\",\"rules\":[{\"op\":\"=\",\"field\":\"dimTypeNo\",\"value\":\""+dimTypeNo+"\",\"type\":\"string\"}]}");
		}
		RptDimTypeInfo   info   =   this.rptDimBS.findDimTypeInfoById(dimTypeNo);
		if(info==null){
			return null;
		}
		if(info.getDimTypeStruct()!=null&&info.getDimTypeStruct().equals(DIM_TYPE_STRUCT_LIST)){
			SearchResult<RptDimItemInfo> searchResult =  rptDimBS.getItemInfoListByParams(pager);
			Map<String, Object> rowData = Maps.newHashMap();
			List<RptDimItemInfo>   list =searchResult.getResult();
			List<RptDimItemInfoVO>   voList =Lists.newArrayList();
			for(int  i=0;i<list.size();i++){
				RptDimItemInfo   info_  =   list.get(i);
				RptDimItemInfoVO   infoVo  = new  RptDimItemInfoVO();
				infoVo.setDimItemNm(info_.getDimItemNm());
				infoVo.setDimItemNo(info_.getId().getDimItemNo());
				infoVo.setDimTypeNo(info_.getId().getDimTypeNo());
				infoVo.setRemark(info_.getRemark());
				infoVo.setUpNo(info_.getUpNo());
				infoVo.setBusiDef(info_.getBusiDef());
				voList.add(infoVo);
			}
			rowData.put("Rows", voList);
			rowData.put("Total", searchResult.getTotalCount());
			return rowData;
		}
		return    rptDimBS.getDimItemTree(pager,dimTypeNo);
	}

	
	/**
	 * 获取类型结构
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @return
	 */
	@RequestMapping(value = "/strutList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> strutList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		Set<String>    keys   =   DIM_TYPE_STRUCT_MAP.keySet();
		for(String key:keys){
			CommonComboBoxNode  node  =  new CommonComboBoxNode();
			node.setId(key);
            node.setText(DIM_TYPE_STRUCT_MAP.get(key));
            nodes.add(node);
		}
		return  nodes;
	}


	/**
	 * 删除维度目录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteDim", method = RequestMethod.POST)
	public Map<String,String> destroy(String dimNo) {
		Map<String,String> resultMap=Maps.newHashMap();
		if(StringUtils.isNotBlank(dimNo)) {
			this.rptDimBS.cascadeDel(dimNo);
			resultMap.put("msg", "0");
		}
		return resultMap;
	}
     
	/**
	 * 删除维度类型
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/dimType/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String dimTypeDestroy(@PathVariable("id") String id) {
		Map<String,Object>  param  =  Maps.newHashMap();
		Map<String,Object>  result  =  Maps.newHashMap();
		String  ids  =id;
		if(ids!=null&&ids.length()>0){
			ids = ids.substring(0,id.length()-1);
		}
		List<String>  idList =  new  ArrayList<String>(); 
		if(id!=null&&id.length()>0){
			String[]  idSplit = StringUtils.split(ids, ',');
			for(int  i=0 ;i<idSplit.length;i++){
			   idList.add(idSplit[i]);
			}
		}
		if (idList != null && idList.size() > 0) {
			param.put("list",idList);
			boolean  flag = rptDimBS.checkHasBeenCascaded(param);
			if(flag){
				result.put("msg","所选维度中存在被指标关联或者被报表关联的维度，不能被删除！");
			}else{
				result.put("msg","1");
				this.rptDimBS.cascadeDimTypeDel(id);
			}
		}
		String  resultStr = JSON.toJSONString(result);
		return  resultStr;
	}
	
	/**
	 * 删除维度项
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/dimItem/{id}", method = RequestMethod.POST)
	@ResponseBody
	public boolean dimItemDestroy(@PathVariable("id") String id,String   dimTypeNo) {
		if(id!=null&&id.length()>0){
			id = id.substring(0,id.length()-1);
		}
		List<String>  idList =  new  ArrayList<String>(); 
		if(id!=null&&id.length()>0){
			String[]  idSplit = StringUtils.split(id, ',');
			for(int  i=0 ;i<idSplit.length;i++){
				idList.add(idSplit[i]);
			}
		}
		this.rptDimBS.cascadeDimItemDel(idList,dimTypeNo);
		return true;
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView dimCatalogNew(String catalogId){
		if("".equals(catalogId)){
			catalogId  =  DEFAULT_TREE_ROOT_NO;
		}
		RptDimCatalog catalog = this.rptDimBS.findDimCatalogById(catalogId);
		ModelMap mm = new ModelMap();
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalogId));
		mm.put("upName",catalog==null?"无":StringUtils2.javaScriptEncode(catalog.getCatalogNm()));
		return new ModelAndView("/plugin/rptdim/rpt-dim-catalog-editNew", mm);
	}
	
public   String   getNumStr(String  maxDimNo,String  maxCustNm,String type){
		
		int   unit = Integer.parseInt(maxCustNm);
		if(maxDimNo==null){
			String   unitStr  =  unit+"";
			char[]   unitCharArr  =  unitStr.toCharArray();
			unitCharArr[0]='0';
			if(unitCharArr.length>1){
				unitCharArr[unitCharArr.length-1]='1';
			}
			unitStr  =  new  String(unitCharArr);
			return   type+unitStr;
		}else{
			int   max  = unit*10-1;
			String  subMaxDimNoStr =  maxDimNo.substring(type.length());
			Integer subMaxDimNoInt   =  Integer.parseInt(subMaxDimNoStr);
			if(subMaxDimNoInt==max){
				throw  new  RuntimeException("维度自增序列已达到自增极限["+type+max+"],请联系管理人员！");
			}else{
				subMaxDimNoInt+=1;
				String  newStr  = "";
				while(subMaxDimNoInt/unit==0){
					   newStr+="0";
					   unit/=10;
				}
				newStr+=subMaxDimNoInt;
				return  type+newStr;
			}
		}
	}

	@RequestMapping(value = "/dimType/new", method = RequestMethod.GET)
	public ModelAndView dimTypeInfoNew(String catalogId){
		RptDimCatalog catalog = this.rptDimBS.findDimCatalogById(catalogId);
		ModelMap mm = new ModelMap();
		mm.addAttribute("DIM_STS_START", INDEX_STS_START);
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalogId));
		mm.put("catalogName", catalog==null?"无":StringUtils2.javaScriptEncode(catalog.getCatalogNm()));
		PropertiesUtils  propertiesUtils  =  PropertiesUtils.get("bione-plugin/extension/report-common.properties");
		String  isCustomed  = propertiesUtils.getProperty("isCustomed");
		String usedDeptParamTypeNo  = propertiesUtils.getProperty("usedDeptParamTypeNo");
		String defDeptParamTypeNo  = propertiesUtils.getProperty("defDeptParamTypeNo");
		mm.addAttribute("usedDeptParamTypeNo", StringUtils2.javaScriptEncode(usedDeptParamTypeNo));
		mm.addAttribute("defDeptParamTypeNo", StringUtils2.javaScriptEncode(defDeptParamTypeNo));
		if(isCustomed.equals("1")){
			String   maxDimNo  =  this.rptDimBS.getMaxDimNo(COSTOMED_DIM_PREFIX+"%");
			String  maxCustNm  = propertiesUtils.getProperty("maxCustNm");
			String   newMaxDimNo  =  getNumStr(maxDimNo,maxCustNm,COSTOMED_DIM_PREFIX);
			mm.addAttribute("isCustomed", StringUtils2.javaScriptEncode(isCustomed));
			mm.addAttribute("newMaxDimNo", StringUtils2.javaScriptEncode(newMaxDimNo));
		}
		 SimpleDateFormat  sdf  =  new SimpleDateFormat("yyyyMMdd");
		 Date  date = new Date();
		 Calendar  calendar  =  Calendar.getInstance();
    	 calendar.setTime(date);
    	 calendar.add(Calendar.DATE, 1);
    	 mm.put("startDateInit",sdf.format(date));
    	 mm.put("endDateInit",sdf.format(calendar.getTime()));
		return new ModelAndView("/plugin/rptdim/rpt-dim-typeInfo-editNew", mm);
	}
	
	@RequestMapping(value = "/dimItem/new", method = RequestMethod.GET)
	public ModelAndView dimItemInfoNew(String dimItemNo,String  dimTypeNo){
		RptDimTypeInfo  info  = this.rptDimBS.findDimTypeInfoById(dimTypeNo);
		String  typeStruct  =  info.getDimTypeStruct();
		
		ModelMap mm = new ModelMap();
		mm.addAttribute("ITEM_STS_START", INDEX_STS_START);
		PropertiesUtils  propertiesUtils  =  PropertiesUtils.get("bione-plugin/extension/report-common.properties");
		String usedDeptParamTypeNo  = propertiesUtils.getProperty("usedDeptParamTypeNo");
		String defDeptParamTypeNo  = propertiesUtils.getProperty("defDeptParamTypeNo");
		mm.addAttribute("usedDeptParamTypeNo", StringUtils2.javaScriptEncode(usedDeptParamTypeNo));
		mm.addAttribute("defDeptParamTypeNo", StringUtils2.javaScriptEncode(defDeptParamTypeNo));
		if(!dimItemNo.equals("")&&typeStruct.equals(DIM_TYPE_STRUCT_TREE)){
			Map<String,String>  params  =  Maps.newHashMap();
			params.put("dimItemNo", dimItemNo);
			params.put("dimTypeNo", dimTypeNo);
			RptDimItemInfo item = this.rptDimBS.findDimItemInfoByPkId(params);
			mm.put("upNo", StringUtils2.javaScriptEncode(dimItemNo));
			mm.put("upDimItemNm", StringUtils2.javaScriptEncode(item.getDimItemNm()));
		}else{
			mm.put("upNo",DEFAULT_TREE_ROOT_NO);
			mm.put("upDimItemNm","无");
		}
		mm.put("dimTypeNo", StringUtils2.javaScriptEncode(dimTypeNo));
		SimpleDateFormat  sdf  =  new SimpleDateFormat("yyyyMMdd");
		Date  date = new Date();
		Calendar  calendar  =  Calendar.getInstance();
	   	calendar.setTime(date);
	   	calendar.add(Calendar.DATE, 1);
	   	mm.put("startDateInit",sdf.format(date));
	   	mm.put("endDateInit",sdf.format(calendar.getTime()));
		return new ModelAndView("/plugin/rptdim/rpt-dim-itemInfo-editNew", mm);
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ModelAndView getCatalogUpdateInfo(String catalogId){
		RptDimCatalog catalog = this.rptDimBS.findDimCatalogById(catalogId);
		if(catalog==null){
			return   null;
		}
		RptDimCatalog pcatalog = this.rptDimBS.findDimCatalogById(catalog.getUpCatalogId());
		ModelMap mm = new ModelMap();
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalogId));
		mm.put("catalogNm", StringUtils2.javaScriptEncode(catalog.getCatalogNm()));
		mm.put("remark", StringUtils2.javaScriptEncode(catalog.getRemark()));
		mm.put("upName", pcatalog==null?"无":StringUtils2.javaScriptEncode(pcatalog.getCatalogNm()));
		mm.put("upCatalogId", StringUtils2.javaScriptEncode(catalog.getUpCatalogId()));
		return new ModelAndView("/plugin/rptdim/rpt-dim-catalog-update", mm);
	}
	
	@RequestMapping(value = "/dimType/update", method = RequestMethod.GET)
	public ModelAndView getTypeUpdateInfo(String dimTypeNo,String  flag){
		ModelMap mm = new ModelMap();
		if(flag!=null&&!flag.equals("")){
			mm.put("flag", StringUtils2.javaScriptEncode(flag));
		}
		mm.put("dimTypeNo", StringUtils2.javaScriptEncode(dimTypeNo));
		PropertiesUtils  propertiesUtils  =  PropertiesUtils.get("bione-plugin/extension/report-common.properties");
		String usedDeptParamTypeNo  = propertiesUtils.getProperty("usedDeptParamTypeNo");
		String defDeptParamTypeNo  = propertiesUtils.getProperty("defDeptParamTypeNo");
		mm.addAttribute("usedDeptParamTypeNo", StringUtils2.javaScriptEncode(usedDeptParamTypeNo));
		mm.addAttribute("defDeptParamTypeNo", StringUtils2.javaScriptEncode(defDeptParamTypeNo));
		
		RptDimTypeInfo typeInfo = this.rptDimBS.findDimTypeInfoById(dimTypeNo);
		if(typeInfo==null){
			return   null;
		}
		RptDimCatalog catalog = this.rptDimBS.findDimCatalogById(typeInfo.getCatalogId());
		mm.put("catalogName",catalog==null?"":StringUtils2.javaScriptEncode(catalog.getCatalogNm()));
		mm.put("catalogId",catalog==null?"":StringUtils2.javaScriptEncode(catalog.getCatalogId()));
		return new ModelAndView("/plugin/rptdim/rpt-dim-typeInfo-update", mm);
	}
	
	@RequestMapping(value = "/dimItem/update", method = RequestMethod.GET)
	public ModelAndView getItemUpdateInfo(String dimItemNo,String  dimTypeNo,String preview){
		Map<String,String>  params  =  Maps.newHashMap();
		params.put("dimItemNo", dimItemNo);
		params.put("dimTypeNo", dimTypeNo);
		RptDimItemInfo item = this.rptDimBS.findDimItemInfoByPkId(params);
		if(item==null){
			return   null;
		}
		ModelMap mm = new ModelMap();
		if(item.getUpNo()==null||item.getUpNo().equals(DEFAULT_TREE_ROOT_NO)||item.getUpNo().equals("")){
			mm.put("upDimItemNm","无");
		}else{
			params.put("dimItemNo", item.getUpNo());
			RptDimItemInfo pItem = this.rptDimBS.findDimItemInfoByPkId(params);
			mm.put("upDimItemNm", StringUtils2.javaScriptEncode(pItem==null?"无":pItem.getDimItemNm()));
		}
		mm.put("dimItemNo", StringUtils2.javaScriptEncode(item.getId().getDimItemNo()));
		mm.put("dimItemNm", StringUtils2.javaScriptEncode(item.getDimItemNm()));
		mm.put("dimTypeNo", StringUtils2.javaScriptEncode(item.getId().getDimTypeNo()));
		mm.put("upNo", StringUtils2.javaScriptEncode(item.getUpNo()));
		mm.put("remark", StringUtils2.javaScriptEncode(item.getRemark()));
		mm.put("itemSts", StringUtils2.javaScriptEncode(item.getItemSts()));
		mm.put("busiDef", StringUtils2.javaScriptEncode(item.getBusiDef()));
		mm.put("busiRule", StringUtils2.javaScriptEncode(item.getBusiRule()));
		mm.put("defDept", StringUtils2.javaScriptEncode(item.getDefDept()));
		mm.put("useDept", StringUtils2.javaScriptEncode(item.getUseDept()));
		mm.put("startDate", StringUtils2.javaScriptEncode(item.getStartDate()));
		mm.put("endDate", StringUtils2.javaScriptEncode(item.getEndDate()));
		mm.put("rankOrder", item.getRankOrder());
		PropertiesUtils  propertiesUtils  =  PropertiesUtils.get("bione-plugin/extension/report-common.properties");
		String usedDeptParamTypeNo  = propertiesUtils.getProperty("usedDeptParamTypeNo");
		String defDeptParamTypeNo  = propertiesUtils.getProperty("defDeptParamTypeNo");
		mm.addAttribute("usedDeptParamTypeNo", StringUtils2.javaScriptEncode(usedDeptParamTypeNo));
		mm.addAttribute("defDeptParamTypeNo", StringUtils2.javaScriptEncode(defDeptParamTypeNo));
		if(StringUtils.isNotEmpty(preview)){
			mm.put("preview", StringUtils2.javaScriptEncode(preview));
		}
		return new ModelAndView("/plugin/rptdim/rpt-dim-itemInfo-update", mm);
	}
	
	@RequestMapping(value = "/testRptDimCatalogNo")
	@ResponseBody
	public boolean testRptDimCatalogNo(String catalogId) {
		RptDimCatalog catalog = this.rptDimBS.findDimCatalogById(catalogId);
		if (catalog != null) {
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/testSameDimTypeNo")
	@ResponseBody
	public boolean testSameDimTypeNo(String   dimTypeNo) {
		Map<String,Object>   params =  Maps.newHashMap();
		params.put("dimTypeNo", dimTypeNo);
		Integer  sameEntityCount = this.rptDimBS.testSameDimTypeNo(params);
		if (sameEntityCount>0) {
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/testSameDimTypeNm")
	@ResponseBody
	public boolean testSameDimTypeNm(String   dimTypeNo,String   dimTypeNm) {
		Map<String,Object>   params =  Maps.newHashMap();
		params.put("dimTypeNm", dimTypeNm);
		params.put("dimTypeNo", dimTypeNo);
		Integer  sameEntityCount = this.rptDimBS.testSameDimTypeNm(params);
		if (sameEntityCount>0) {
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/testSameDimCatalogNm")
	@ResponseBody
	public boolean testSameDimCatalogNm(String   upCatalogId,String   catalogNm,String  catalogId) {
		Map<String,Object>   params =  Maps.newHashMap();
		params.put("upCatalogId", upCatalogId);
		params.put("catalogNm", catalogNm);
		if(catalogId!=null&&!catalogId.equals("")){
	  	    params.put("catalogId", catalogId);
		}
		
		Integer  sameEntityCount = this.rptDimBS.testSameDimCatalogNm(params);
		if (sameEntityCount>0) {
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/testRptDimItemNo")
	@ResponseBody
	public boolean testRptDimItemNo(RptDimItemInfo model) {
		String dimItemNo = model.getId().getDimItemNo();
		String dimTypeNo = model.getId().getDimTypeNo();
		Map<String,String>  params  =  Maps.newHashMap();
		params.put("dimItemNo", dimItemNo);
		params.put("dimTypeNo", dimTypeNo);
		RptDimItemInfo item = this.rptDimBS.findDimItemInfoByPkId(params);
		if (item != null) {
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/testRptDimItemNm")
	@ResponseBody
	public boolean testRptDimItemNm(RptDimItemInfo model,String  dimItemNm) {
		String dimItemNo = model.getId().getDimItemNo();
		String dimTypeNo = model.getId().getDimTypeNo();
		Map<String,Object>  params  =  Maps.newHashMap();
		params.put("dimItemNo", dimItemNo);
		params.put("dimTypeNo", dimTypeNo);
		params.put("dimItemNm", dimItemNm);
		Integer  sameEntityCount = this.rptDimBS.testSameDimItemNm(params);
		if (sameEntityCount>0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 保存维度目录
	 * @param model
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void createDimCatalog(RptDimCatalog model) {
		if(model.getCatalogId()==null||model.getCatalogId().equals("")){
			model.setCatalogId(RandomUtils.uuid2());
		}
		this.rptDimBS.createRptDimCatalog(model);
	}
	
	/**
	 * 保存维度类型
	 * @param model
	 */
	@RequestMapping(value = "/dimType"  ,method = RequestMethod.POST)
	public void createDimTypeInfo(RptDimTypeInfo model) {
		if(model.getDimTypeNo()==null||model.getDimTypeNo().equals("")){
	       model.setDimTypeNo(RandomUtils.uuid2());
	    }
		model.setDimType(DIM_TYPE_BUSI);
		model.setStartDate(model.getStartDate());
		model.setEndDate(model.getEndDate());
		this.rptDimBS.createRptDimTypeInfo(model);
	}
	
	/**
	 * 保存维度项
	 * @param model
	 */
	@RequestMapping(value = "/dimItem"  ,method = RequestMethod.POST)
	public void createDimItemInfo(RptDimItemInfo model) {
		if(model.getId().getDimItemNo().equals("")){
			model.getId().setDimTypeNo(RandomUtils.uuid2());
		}
		model.setStartDate(model.getStartDate());
		model.setEndDate(model.getEndDate());
		this.rptDimBS.createRptDimItemInfo(model);
	}
	
	@RequestMapping(value = "/updateCatalog" ,method = RequestMethod.POST)
	public void updateCatalog(RptDimCatalog model) {
		this.rptDimBS.updateRptDimCatalog(model);
	}
    
	@RequestMapping(value = "/updateType" ,method = RequestMethod.POST)
	public void updateType(RptDimTypeInfo model,String   flag) {
		model.setStartDate(model.getStartDate());
		model.setEndDate(model.getEndDate());
		if(flag==null)  this.rptDimBS.updateRptDimTypeInfo(model);
	}
	
	@RequestMapping(value = "/updateItem" ,method = RequestMethod.POST)
	public void updateItem(RptDimItemInfo model) {
		model.setStartDate(model.getStartDate());
		model.setEndDate(model.getEndDate());
		this.rptDimBS.updateRptDimItemInfo(model);
	}
	
	@RequestMapping(value = "/dimType", method = RequestMethod.GET)
	public ModelAndView dimType(String  catalogId,String  preview) {
		ModelMap mm = new ModelMap();
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalogId));
		mm.put("preview", StringUtils2.javaScriptEncode(preview));
		PropertiesUtils  propertiesUtils  =  PropertiesUtils.get("bione-plugin/extension/report-common.properties");
		String defDeptParamTypeNo  = propertiesUtils.getProperty("defDeptParamTypeNo");
		mm.put("defDeptParamTypeNo", StringUtils2.javaScriptEncode(defDeptParamTypeNo));
		return new ModelAndView("/plugin/rptdim/rpt-dim-catalog-typeinfo", mm);
	}
	
	/**
	 * 获取指定维度目录对应的维度类型信息列表
	 */
	@RequestMapping("/getTypeInfoList.*")
	@ResponseBody
	public Map<String, Object> getTypeInfoList(String catalogId,Pager pager) {
		if(pager.getCondition()==null){
			pager.setCondition("{\"op\":\"and\",\"rules\":[{\"op\":\"=\",\"field\":\"dim.catalogId\",\"value\":\""+catalogId+"\",\"type\":\"string\"}]}");
		}else if(pager.getCondition().equals("")){
			pager.setCondition("{\"op\":\"and\",\"rules\":[]}");
		}
//		String   customerDefineDimTypeConditionStr_ ="{\"op\":\"=\",\"field\":\"dimType\",\"value\":\""+DIM_TYPE_BUSI+"\",\"type\":\"string\"}";
//		JSONObject  json = null;
//    	json =   JSONObject.fromObject(pager.getCondition());
//    	JSONArray   array  =  json.getJSONArray("rules");
//    	array.add(customerDefineDimTypeConditionStr_);
//    	json.put("rules", array.toString());
//    	pager.setCondition(json.toString());
		SearchResult<RptDimTypeInfo> searchResult = rptDimBS.getTypeInfosByParams(pager);
		Map<String, Object> rowData = Maps.newHashMap();
		rowData.put("Rows", searchResult.getResult());
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}
	
	/**
	 * 进入维度目录选择树页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/catalogTree")
	public ModelAndView dimOrMeasuretreeSelect(String catalogId) {
		catalogId = StringUtils2.javaScriptEncode(catalogId);
		return new ModelAndView("/plugin/rptdim/rpt-dim-catalog-selecttree","catalogId",catalogId );
	}
	
	@SuppressWarnings("unused")
	private   String  getFormatDate(String date){
		if(date==null||date.equals("")){
			return  "";
		}
		SimpleDateFormat   format  =  new SimpleDateFormat("yyyy-MM-dd");
		if(date.length()==8){
			format  =  new SimpleDateFormat("yyyyMMdd");
		}
		Date newDate=null;
		try {
			newDate = format.parse(date);
			format=  new  SimpleDateFormat("yyyyMMdd");
			if(date.length()==8){
			  format=  new  SimpleDateFormat("yyyy-MM-dd");
			}
			return format.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return  "";
	}
	
	/**
	 * 导出
	 * 
	 * @return
	 */
	
	@RequestMapping(value="exportDim")
	public ModelAndView exportDim(){
		return new ModelAndView("/plugin/rptdim/dim-come-export");
	}
	
	@RequestMapping("/exportTmp")
	public Map<String,Object> exportTmp(String dimTypeNos) throws IOException{
		return DataDealUtils.exportImport(dimTypeNos, this.getRealPath(),"exportDim");
	}
	
	@RequestMapping("/exportTmpInfo")
	public void exportTmp(HttpServletResponse response,String filepath) throws Exception {
		if (FilepathValidateUtils.validateFilepath(filepath)) {
			File file=new File(filepath); 
			DownloadUtils.download(response, file,"维度导出数据.txt");
			file.delete();
		}
	}
	
	/**
	 * 导入
	 * 
	 * @return
	 */
	
	@RequestMapping("/deleteFile")
	public void deleteFile(String pathname){
		if (FilepathValidateUtils.validateFilepath(pathname)) {
			File file=new File(pathname);
			file.delete();
		}
	}
	
	@RequestMapping("/impDim")
	public ModelAndView impReports(){
		return new ModelAndView("/plugin/rptdim/dim-imp-index");
	}
	
	/**
	 * 导入数据(grid展示)
	 */
	@RequestMapping("/impGrid")
	public ModelAndView impGrid(){
		return new ModelAndView("/plugin/rptdim/dim-imp-grid");
	}
	
	@RequestMapping("/impUpload")
	public ModelAndView impUpload(){
		return new ModelAndView("/plugin/rptdim/dim-imp-upload");
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
			fieldNms.add("dimTypeNm");
			return DataDealUtils.getDataInfo(file, RptDimTypeInfo.class.getName(), fieldNms);
		}
		return null;
	}
	
	@RequestMapping("/saveImport")
	@ResponseBody
	public Map<String,Object> saveImport(String pathname) throws IOException, ClassNotFoundException{
		return DataDealUtils.saveImport(pathname,null);
	}
	
	
	/***
	 * 获取维度信息
	 * @param dimTypeNo
	 * @return
	 */
	@RequestMapping(value = "/getUpdateInfo.*", method = RequestMethod.POST)
	@ResponseBody
	public RptDimTypeInfo getUpdateInfo(String dimTypeNo) {
		RptDimTypeInfo typeInfo = this.rptDimBS.findDimTypeInfoById(dimTypeNo);
		if(typeInfo==null){
			return   null;
		}
		return  typeInfo;
	}
	
	/**
	 * @Title: checkDim
	 * @Description: 查看按钮
	 * @param dimTypeNo
	 * @param flag
	 * @return
	 */
	@RequestMapping(value = "/checkDim")
	public ModelAndView checkDim(String dimTypeNo,String flag){
		ModelMap mm = new ModelMap();
		mm.put("dimTypeNo", StringUtils2.javaScriptEncode(dimTypeNo));
		if(flag!=null&&!flag.equals("")){
			mm.put("flag", "noBtnFlag");
		}
		RptDimTypeInfo  info  =   this.rptDimBS.findDimTypeInfoById(dimTypeNo);
      	if(info!=null)  {
      		if(info.getDimTypeStruct()!=null&&info.getDimTypeStruct().equals(DIM_TYPE_STRUCT_LIST))
      			mm.put("isList", "isList");
      	}
		return new ModelAndView("/plugin/rptdim/rpt-dim-item-index-list",mm);	
	}
	
	/***
	 * 保存历史版本
	 * @param dimTypeNo
	 * @return
	 */
	@RequestMapping(value = "/saveAsHistoryVer.*", method = RequestMethod.POST)
	@ResponseBody
	public String saveAsHistoryVer(String dimTypeNo) {
	    return  this.rptDimBS.saveAsHistoryVer(dimTypeNo);
	}
	
	/**
	 * 维度信息版本查看页面
	 * @return
	 */
	@RequestMapping(value = "/dimInfoVerPre")
	public ModelAndView dimInfoVerPre(String  dimTypeNo) {
		ModelMap map = new ModelMap();
		RptDimTypeInfo  dim  =  this.rptDimBS.findDimTypeInfoById(dimTypeNo);
		map.addAttribute("dimTypeNo", StringUtils2.javaScriptEncode(dimTypeNo));
		map.addAttribute("dimTypeNm", StringUtils2.javaScriptEncode(dim.getDimTypeNm()));
		return   new ModelAndView("/plugin/rptdim/dim-info-version", map);
	}
	
	/***
	 * 获取对应维度dimTypeNo的所有版本信息
	 * @param dimTypeNo
	 * @return
	 */
	@RequestMapping(value = "/getDimInfoVerList.*")
	@ResponseBody
	public Map<String, Object> getDimInfoVerList(String  dimTypeNo) {
		Map<String, Object> rowData = Maps.newHashMap();
		List<RptDimHistoryInfo> list  = rptDimBS.getAllHistoryByDimTypeNo(dimTypeNo);
		rowData.put("Rows", list);
		return rowData;
	}
	
	/**
	 * 导出文件
	 * 
	 * @return
	 */
	@RequestMapping(value = "/exportFile")
	public void exportFile(HttpServletResponse response, String  hisId) {
		RptDimHistoryInfo  history  =  this.rptDimBS.getRptDimHistoryByHisId(hisId);
		String   hisInfo  =  history.getHisInfo();
		JSONObject jsonObject = JSON.parseObject(hisInfo);
	    JSONObject typeJson = jsonObject.getJSONObject("type");
	    JSONArray   catalogJson  =  jsonObject.getJSONArray("catalogs");
	    JSONArray   itemJson  =  jsonObject.getJSONArray("items");
	    
	    RptDimTypeInfo type = typeJson.toJavaObject(RptDimTypeInfo.class);
	    List<RptDimCatalog>  catalogs  = Lists.newArrayList();
	    List<RptDimItemInfo>  items  =  Lists.newArrayList();
	    for(int i=0;i<catalogJson.size();i++){
	    	catalogs.add(catalogJson.getObject(i, RptDimCatalog.class));
	    }
	    for(int  i=0;i<itemJson.size();i++){
	    	items.add(itemJson.getObject(i, RptDimItemInfo.class));
	    }
	    List<DimImportVO> vos = new ArrayList<DimImportVO>();
	    Map<String, RptDimCatalog> catalogMap = new HashMap<String, RptDimCatalog>();
	    if (catalogs != null && catalogs.size() > 0) {
			for (RptDimCatalog catalog : catalogs) {
				catalogMap.put(catalog.getCatalogId(), catalog);
			}
		}
	    String dimNos[] = new String[8];
		dimNos[0] = "";
		if (type != null) {
				boolean flag = false;
				DimImportVO vo = new DimImportVO();
				List<String> catalogNm = new ArrayList<String>();
				setCatalogNm(catalogNm, type.getCatalogId(), catalogMap);
				if (catalogNm.size() > 0) {
					vo.setFirstCatalog(catalogNm.get(catalogNm.size() - 1));
				}
				if (catalogNm.size() > 1) {
					vo.setSecondCatalog(catalogNm.get(catalogNm.size() - 2));
				}
				if (catalogNm.size() > 2) {
					vo.setThirdCatalog(catalogNm.get(catalogNm.size() - 3));
				}
				vo.setDimTypeNo(type.getDimTypeNo());
				vo.setDimTypeNm(type.getDimTypeNm());
				vo.setDimSts(type.getDimSts());
				vo.setStartDate(type.getStartDate());
				vo.setDimTypeDesc(type.getDimTypeDesc());
				vo.setDefDept(type.getDefDept());
				vo.setUseDept(type.getUseDept());
				vo.setBusiDef(type.getBusiDef());
				vo.setBusiRule(type.getBusiRule());
				vos.add(vo);
				Map<String, List<RptDimItemInfo>> itemMap = setDimInfo(items);
				setItemInfos(itemMap, vos, 1, DEFAULT_TREE_ROOT_NO, dimNos, flag);
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelTemplateExporter fe = null;
		String   realName =  type.getDimTypeNo()+"_"+history.getStartDate()+"_"+history.getEndDate()+".xls";
		String   fileName = this.getRealPath() + DESIGN_EXPORT_PATH
				+ File.separator + RandomUtils.uuid2() + ".xls";
		if (FilepathValidateUtils.validateFilepath(fileName)) {
			File file  = null;
			try {
				fe=new XlsExcelTemplateExporter(fileName, this.getRealPath()
						+ DESIGN_EXPORT_PATH + File.separator
						+ EXPORT_DIM_TEMPLATE_PATH, list);
				fe.run();
				file=new File(fileName);
				DownloadUtils.download(response, file,realName);
			} catch (Exception e) {
				fileName = "";
				e.printStackTrace();
			} finally {
				try {
					if (fe != null) {
						fe.destory();
					}
				} catch (BioneExporterException e) {
					e.printStackTrace();
				}
				try {
					if(file!=null){
						file.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void setItemInfos(Map<String, List<RptDimItemInfo>> itemMap,
			List<DimImportVO> vos, int index, String dimItemNo,
			String[] dimNos, boolean flag) {
		List<RptDimItemInfo> itemInfos = itemMap.get(dimItemNo);
		if (itemInfos != null && itemInfos.size() > 0) {
			for (RptDimItemInfo item : itemInfos) {
				vos.add(setRowInfo(dimNos, index, item));
				setItemInfos(itemMap, vos, index + 1, item.getId()
						.getDimItemNo(), dimNos, flag);
			}
		}
	}
	
	private void setCatalogNm(List<String> catalogNm, String catalogId,
			Map<String, RptDimCatalog> catalogMap) {
		RptDimCatalog catalog = catalogMap.get(catalogId);
		catalogNm.add(catalog.getCatalogNm());
		if (!DEFAULT_TREE_ROOT_NO.equals(catalog.getUpCatalogId())) {
			setCatalogNm(catalogNm, catalog.getUpCatalogId(), catalogMap);
		}
	}

	private Map<String, List<RptDimItemInfo>> setDimInfo(
			List<RptDimItemInfo> items) {
		Map<String, List<RptDimItemInfo>> dimMap = new HashMap<String, List<RptDimItemInfo>>();
		if (items != null && items.size() > 0) {
			for (RptDimItemInfo item : items) {
				if (dimMap.get(item.getUpNo()) == null) {
					List<RptDimItemInfo> infos = new ArrayList<RptDimItemInfo>();
					infos.add(item);
					dimMap.put(item.getUpNo(), infos);
				} else {
					dimMap.get(item.getUpNo()).add(item);
				}
			}
		}
		return dimMap;

	}
	
	private DimImportVO setRowInfo(String[] dimNos, int index,
			RptDimItemInfo item) {
		DimImportVO vo = new DimImportVO();
		dimNos[index] = item.getId().getDimItemNo();
		BeanUtils.copy(item, vo);
		vo.setStartDate("");
		ReflectionUtils.invokeSetter(vo, "dimNo" + index, StringUtils
				.substring(item.getId().getDimItemNo(),
						dimNos[index - 1].length(), item.getId().getDimItemNo()
								.length()));
		ReflectionUtils.invokeSetter(vo, "dimNm" + index, item.getDimItemNm());
		return vo;
	}
}

