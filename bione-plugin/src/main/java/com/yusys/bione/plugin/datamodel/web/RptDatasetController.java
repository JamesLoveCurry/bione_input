package com.yusys.bione.plugin.datamodel.web;

import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.MEASURE_TYPE_STEADY;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.MEASURE_TYPE_SUM;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SET_TYPE_DETAIL;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SET_TYPE_GENERIC;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SET_TYPE_MUTI_DIM;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SET_TYPE_REPORT;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SET_TYPE_SUM;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.service.RptDatasetBS;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.service.RptDimBS;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;
/**
 * 
 * <pre>
 * Title:?????????????????????
 * Description: ????????????????????????????????????????????????
 * </pre>
 * 
 * @author fanll fanll@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * ????????????
 *    ???????????????:     ????????????  ????????????:     ????????????:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/dataset")
public class RptDatasetController extends BaseController {

	@Autowired
	private RptDatasetBS rptDatasetBS;
	@Autowired
	private RptDimBS rptDimBS; 
	/**
	 * ??????
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/datamodel/data-set-index";
	}

	/**
	 * ????????????????????????
	 */
	@RequestMapping(value = "/catalog")
	public ModelAndView catalog(String catalogId, String upId) {
		ModelMap map = new ModelMap();
		map.addAttribute("catalogId", StringUtils2.javaScriptEncode(catalogId));
		map.addAttribute("upId", StringUtils2.javaScriptEncode(upId));
		return new ModelAndView("/plugin/datamodel/data-set-catalog", map);
	}

	/**
	 * ??????????????????
	 * 
	 * @param catalogId
	 *            ??????Id
	 * @param catalogName
	 *            ????????????
	 * @return
	 */
	@RequestMapping(value = "/grid")
	public String grid(String catalogId, String catalogName) {
		return "/plugin/datamodel/data-set-grid";
	}

	/**
	 * ????????????????????????
	 * 
	 * @param catalogId
	 *            ??????Id
	 * @return
	 */
	@RequestMapping(value = "/infoFrame")
	public ModelAndView infoFrame(String datasetId, String catalogId,String show) {
		ModelMap map = new ModelMap();
		map.addAttribute("datasetId", StringUtils2.javaScriptEncode(datasetId));
		// 20150506?????? by lujs ??????
		// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		if(datasetId != null && !datasetId.isEmpty()){
			RptSysModuleInfo info = this.rptDatasetBS.getModuleInfoById(datasetId);
			if (info != null && !info.getCatalogId().isEmpty() ){
				RptSysModuleCatalog catalogInfo = this.rptDatasetBS.getCatalogInfo(info.getCatalogId());
				if(catalogInfo != null){
					map.addAttribute("catalogId", StringUtils2.javaScriptEncode(catalogInfo.getCatalogId()));
					map.addAttribute("catalogName", StringUtils2.javaScriptEncode(catalogInfo.getCatalogNm()));
				}
			}
		}else{
			RptSysModuleCatalog catalogInfo = this.rptDatasetBS.getCatalogInfo(catalogId);
			map.addAttribute("catalogId", StringUtils2.javaScriptEncode(catalogId));
			map.addAttribute("catalogName", StringUtils2.javaScriptEncode(catalogInfo.getCatalogNm()));
		}
		map.addAttribute("show", StringUtils2.javaScriptEncode(show));
		// 20150506 ????????????
		return new ModelAndView("/plugin/datamodel/data-set-infoFrame", map);
	}

	/**
	 * ??????????????????
	 * 
	 * @param catalogId
	 *            ??????Id
	 * @return
	 */
	@RequestMapping(value = "/info")
	public ModelAndView info(String show) {
		show = StringUtils2.javaScriptEncode(show);
		return new ModelAndView("/plugin/datamodel/data-set-info", "show", show) ;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tablePage")
	public ModelAndView tablePage(String dsId,String datasetId) {
		ModelMap map = new ModelMap();
		map.addAttribute("dsId", StringUtils2.javaScriptEncode(dsId));
		map.addAttribute("datasetId", StringUtils2.javaScriptEncode(datasetId));
		return new ModelAndView("/plugin/datamodel/data-set-table", map);
	}
	
	/**
	 * ?????????????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dimOrMeasuretreeSelect")
	public ModelAndView dimOrMeasuretreeSelect(String type,String  currVal,String setType,String selected) {
		ModelMap map = new ModelMap();
		map.addAttribute("type", StringUtils2.javaScriptEncode(type));
		map.addAttribute("currVal", StringUtils2.javaScriptEncode(currVal));   
		map.addAttribute("selected", StringUtils2.javaScriptEncode(selected));  
		map.addAttribute("setType", StringUtils2.javaScriptEncode(setType));   
		return new ModelAndView("/plugin/datamodel/data-set-cols-selecttree", map);
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sysvarPage")
	public String sysvarPage() {
		return "/plugin/datamodel/data-set-sysvar";
	}

	/**
	 * ??????????????????
	 */
	@RequestMapping(value = "/cols")
	public ModelAndView cols(String show) {
		PropertiesUtils  propertiesUtils = PropertiesUtils.get("bione-plugin/extension/report-common.properties");
		ModelMap map = new ModelMap();
		map.addAttribute("orgDimTypeNo", StringUtils2.javaScriptEncode(propertiesUtils.getProperty("orgDimTypeNo")));//org
		map.addAttribute("dateDimTypeNo", StringUtils2.javaScriptEncode(propertiesUtils.getProperty("dateDimTypeNo")));//date
		map.addAttribute("currencyDimTypeNo", StringUtils2.javaScriptEncode(propertiesUtils.getProperty("currencyDimTypeNo")));//currency
		map.addAttribute("indexDimTypeNo", StringUtils2.javaScriptEncode(propertiesUtils.getProperty("indexDimTypeNo")));//line
		List<String>   dimTypeNoSysList  =   Lists.newArrayList();
		dimTypeNoSysList.add(propertiesUtils.getProperty("orgDimTypeNo"));
		dimTypeNoSysList.add(propertiesUtils.getProperty("dateDimTypeNo"));
		dimTypeNoSysList.add(propertiesUtils.getProperty("currencyDimTypeNo"));
		dimTypeNoSysList.add(propertiesUtils.getProperty("indexDimTypeNo"));
		List<RptDimTypeInfo>   types =  rptDimBS.findDimTypeInfoByIds(dimTypeNoSysList);
		for(int  i=0;i<types.size();i++){
			RptDimTypeInfo typeinfo  =  types.get(i);
			if(typeinfo.getDimTypeNo().equals(propertiesUtils.getProperty("orgDimTypeNo"))){
				map.addAttribute("orgDimTypeName", StringUtils2.javaScriptEncode(typeinfo.getDimTypeNm()));
			}else  if(typeinfo.getDimTypeNo().equals(propertiesUtils.getProperty("dateDimTypeNo"))){
				map.addAttribute("dateDimTypeName", StringUtils2.javaScriptEncode(typeinfo.getDimTypeNm()));
			}else if(typeinfo.getDimTypeNo().equals(propertiesUtils.getProperty("currencyDimTypeNo"))){
				map.addAttribute("currencyDimTypeName", StringUtils2.javaScriptEncode(typeinfo.getDimTypeNm()));
			}else if(typeinfo.getDimTypeNo().equals(propertiesUtils.getProperty("indexDimTypeNo"))){
				map.addAttribute("indexDimTypeName", StringUtils2.javaScriptEncode(typeinfo.getDimTypeNm()));
			}
		}
		Map<String,Object>  params  =   Maps.newHashMap();
		List<RptIdxMeasureInfo>   allMeasures  =   this.rptDatasetBS.getAllMeasures(params);
		List<RptDimTypeInfo> allDims  =  this.rptDimBS.getAllRptDimTypeInfos();
		map.put("allMeasures", StringUtils2.javaScriptEncode(JSON.toJSONString(allMeasures)));
		
		//////////////////////////////////////////////////////////////////////////////
		// ????????????No????????????????????????eval???????????????????????????????????????????????????????????????
		//map.put("allDims", JSONArray.fromObject(allDims).toString());
		List<String> dimTypeNos = Lists.newArrayList();
		List<String> dimTypeNms = Lists.newArrayList();
		for(RptDimTypeInfo dimType : allDims){
			dimTypeNos.add(dimType.getDimTypeNo());
			dimTypeNms.add(dimType.getDimTypeNm());
		}
		map.put("dimTypeNos", dimTypeNos);
		map.put("dimTypeNms", dimTypeNms);
		//////////////////////////////////////////////////////////////////////////////
		map.put("SET_TYPE_DETAIL", SET_TYPE_DETAIL);
		map.put("SET_TYPE_MUTI_DIM", SET_TYPE_MUTI_DIM);
		map.put("SET_TYPE_GENERIC", SET_TYPE_GENERIC);
		map.put("SET_TYPE_SUM", SET_TYPE_SUM);
		map.put("SET_TYPE_REPORT", SET_TYPE_REPORT);
		map.put("show", StringUtils2.javaScriptEncode(show));
		return new ModelAndView("/plugin/datamodel/data-set-cols", map);
	}

	/**
	 * ??????????????????
	 * 
	 * @param catalogId
	 *            ??????Id
	 * @return
	 */
	@RequestMapping(value = "/preview")
	public ModelAndView preview(String datasetId) {
		ModelMap map = new ModelMap();
		map.addAttribute("datasetId", StringUtils2.javaScriptEncode(datasetId));
		map.addAttribute("formStructure","");
//				this.rptDatasetBS.getSearchStruct(datasetId));
		map.addAttribute("gridStructure", StringUtils2.javaScriptEncode(this.rptDatasetBS.getGridStruct(datasetId)));
		return new ModelAndView("/plugin/datamodel/data-set-preview", map);
	}

	/**
	 * ????????????????????????
	 */
	@RequestMapping(value = "/getTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTree(String realId) {
		return this.rptDatasetBS.getDatasetCatalogTree(realId, this.getContextPath());
	}
	
	
	@RequestMapping("/listCatalogTree.*")
	@ResponseBody
	public List<CommonTreeNode> listCatalogTree() {
		List<CommonTreeNode> nodes = rptDatasetBS.getCatalogTree();
		return nodes;
	}
	
	/**
	 * ????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/catalogTree")
	public ModelAndView catalogTreeSelect(String catalogId) {
		catalogId = StringUtils2.javaScriptEncode(catalogId);
		return new ModelAndView("/plugin/datamodel/data-set-catalog-selecttree","catalogId",catalogId );
	}
	
	/**
	 * ?????????????????????
	 */
	@RequestMapping(value = "/listmeasures.*")
	@ResponseBody
	public List<CommonTreeNode> listMeasures(String  measureNo,String  setType) {
		String basePath = this.getContextPath();
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId("0");
		baseNode.setText("??????");
		baseNode.setUpId("0");
		baseNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
		treeNodes.add(baseNode);
		Map<String,Object>   params =   Maps.newHashMap();
		if(setType!=null&&setType.equals(SET_TYPE_SUM)){
		 params.put("measureType",MEASURE_TYPE_SUM );
		}else{
			params.put("measureType", MEASURE_TYPE_STEADY);
		}
		List<RptIdxMeasureInfo>    measures = this.rptDatasetBS.getAllMeasures(params);
		for(RptIdxMeasureInfo   info :measures){
			CommonTreeNode node = new CommonTreeNode();
			node.setId(info.getMeasureNo());
			node.setText(info.getMeasureNm());
			if(info.getMeasureNo()!=null&&measureNo!=null&&info.getMeasureNo().equals(measureNo)){
				node.setIschecked(true);
			}
			node.setIcon(basePath +"/"+GlobalConstants4frame.ICON_URL+"/list-items.gif");
			baseNode.addChildNode(node);
		}
		baseNode.getParams().put("open", "true");
		return treeNodes;        
	}

	/**
	 * ????????????????????????
	 */
	@RequestMapping(value = "/catalogNameCanUse*")
	@ResponseBody
	public boolean catalogNameCanUse(String catalogId, String upId,
			String catalogNm) {
		return this.rptDatasetBS.catalogNameCanUse(catalogId, upId, catalogNm);
	}

	/**
	 *????????????????????????????????????????????????
	 */
	@RequestMapping(value = "/datasetNameCanUse*")
	@ResponseBody
	public boolean datasetNameCanUse(String catalogId, String setNm,String setId) {
		return this.rptDatasetBS.datasetNameCanUse(catalogId, setNm ,setId);
	}
	
	/**
	 * ??????????????????
	 */
	@RequestMapping(value = "/getCatalogInfo.*")
	@ResponseBody
	public RptSysModuleCatalog getCatalogInfo(String catalogId) {
		return this.rptDatasetBS.getCatalogInfo(catalogId);
	}

	/**
	 * ??????????????????
	 */
	@RequestMapping(value = "/saveCatalog")
	public void saveCatalog(RptSysModuleCatalog catalog) {
		if (catalog.getCatalogId() == null || "".equals(catalog.getCatalogId())) {
			catalog.setCatalogId(RandomUtils.uuid2());
			this.rptDatasetBS.saveCatalog(catalog);
		} else {
			this.rptDatasetBS.updateCatalog(catalog);
		}
	}

	/**
	 * ??????????????????
	 */
	@RequestMapping("/deleteCatalog")
	@ResponseBody
	public boolean deleteCatalog(String catalogId) {
		return this.rptDatasetBS.deleteCatalog(catalogId);
	}

	/**
	 * ?????????????????????
	 * 
	 * @param pager
	 *            PageBean
	 * @param catalogId
	 *            ??????Id
	 * @return
	 */
	@RequestMapping(value = "/setList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> datasets(Pager pager, String catalogId) {
		return this.rptDatasetBS.getDatasets(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition(),catalogId,"");
	}
	
	/**
	 * ?????????????????????
	 * 
	 * @param pager
	 *            PageBean
	 * @param catalogId
	 *            ??????Id
	 * @return
	 */
	@RequestMapping(value = "/rptSetList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> rptSetList(Pager pager) {
		return this.rptDatasetBS.getDatasets(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition(),null,"");
	}
	
	/**
	 * ???????????????????????????
	 * 
	 * @param pager
	 *            PageBean
	 * @param catalogId
	 *            ??????Id
	 * @return
	 */
	@RequestMapping(value = "/setAll.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> datasets(Pager pager) {
		return this.rptDatasetBS.getDatasets(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition(),"","");
	}

	/**
	 * ???????????????
	 * 
	 * @param datasetId
	 *            ?????????Id
	 * @return
	 */
	@RequestMapping(value = "/datasetInfo.*", method = RequestMethod.POST)
	@ResponseBody
	public RptSysModuleInfo datasetInfo(String datasetId) {
		return this.rptDatasetBS.getModuleInfoById(datasetId);
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param datasetId
	 *            ?????????Id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/colList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> datacols(String from, String datasetId,
			String dsId, String table,String  setType,String  synch) {
		RptSysModuleInfo   moduleInfo   =  this.rptDatasetBS.getModuleInfoById(datasetId);
		Map<String,Object>   colsMap =  null;
		Map<String,Object>   tableColsMap =  null;
		if (from != null && "dataset".equals(from.toLowerCase())&&(moduleInfo!=null&&moduleInfo.getSetType().equals(setType))) {
			colsMap  = this.rptDatasetBS.getDatacolsOfDataset(datasetId);// ?????????????????????
			if(StringUtils.isNotEmpty(synch)){
				tableColsMap  = this.rptDatasetBS.getFieldsOfTable(dsId, table);// ???????????????????????????
				List<RptSysModuleCol> tableCols  =(List<RptSysModuleCol>) tableColsMap.get("Rows");
				List<RptSysModuleCol> cols  =(List<RptSysModuleCol>) colsMap.get("Rows");
				List<RptSysModuleCol> finalCols  =Lists.newArrayList();
				
				Map<String,RptSysModuleCol>  tableColMap  =  Maps.newHashMap();
				Map<String,String>  finalColMap  =  Maps.newHashMap();
				
				for(int i=0;i<tableCols.size();i++){
					RptSysModuleCol  tableCol =  tableCols.get(i);
					tableColMap.put(tableCol.getEnNm(),tableCol);
				}
				
				for(int i=0;i<cols.size();i++){
					RptSysModuleCol  col =  cols.get(i);
					if(tableColMap.containsKey(col.getEnNm())){
						col.setCnNm(tableColMap.get(col.getEnNm()).getCnNm());
						col.setDbType(tableColMap.get(col.getEnNm()).getDbType());
						col.setLen(tableColMap.get(col.getEnNm()).getLen());
						col.setPrecision2(tableColMap.get(col.getEnNm()).getPrecision2());
						finalCols.add(col);
					}				
				}
				for(int i =0;i<finalCols.size();i++){
					RptSysModuleCol  finalCol =  finalCols.get(i);
					finalColMap.put(finalCol.getEnNm(),null);
				}
				Set<String>  keys =  tableColMap.keySet();
				for(String  key:keys){
					if(!finalColMap.containsKey(key)){
						finalCols.add(tableColMap.get(key));
					}
				}
				colsMap.put("Rows",finalCols);
			}
		} else {
			colsMap  = this.rptDatasetBS.getFieldsOfTable(dsId, table);// ???????????????????????????
		}
		List<RptSysModuleCol> cols  =(List<RptSysModuleCol>) colsMap.get("Rows");
		List<RptDimTypeInfo>    allTypes =  this.rptDimBS.getAllRptDimTypeInfos();
		Map<String,String>   typeInfoMap  = Maps.newHashMap();
		for(RptDimTypeInfo dimTypeInfo :allTypes){
			typeInfoMap.put(dimTypeInfo.getDimTypeNo(), dimTypeInfo.getDimTypeNm());
		}
		Map<String, Object> params = Maps.newHashMap();
		List<RptIdxMeasureInfo>    measures = this.rptDatasetBS.getAllMeasures(params);
		
		Map<String,String>   measureMap  = Maps.newHashMap();
		for(RptIdxMeasureInfo idxMeasureInfo :measures){
			measureMap.put(idxMeasureInfo.getMeasureNo(), idxMeasureInfo.getMeasureNm());
		}
		
		for(RptSysModuleCol col :cols){
				  if(col.getColType().equals("01")){//????????????
					    if(col.getMeasureNo()!=null&&measureMap.get(col.getMeasureNo())!=null){
							col.setMeasureName(measureMap.get(col.getMeasureNo()));
						}
				  }else if(col.getColType().equals("02")){//????????????
					  if(col.getDimTypeNo()!=null&&typeInfoMap.get(col.getDimTypeNo())!=null){
							col.setDimTypeName(typeInfoMap.get(col.getDimTypeNo()));
						}
				  }
		}
		colsMap.put("Rows",cols);
		return  colsMap;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param datasetId
	 *            ?????????Id
	 * @return
	 */
	@RequestMapping(value = "/dsList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> dsList() {
		return this.rptDatasetBS.getDataSources();
	}

	/**
	 * ???????????????????????????
	 * @return
	 */
	@RequestMapping(value = "/setTypeList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> setTypeList() {
		return this.rptDatasetBS.getSetTypeList();
	}
	
	
		/**
	 * ?????????????????????????????????
	 * 
	 * @param dsId
	 *            ?????????Id
	 * @return
	 */
	@RequestMapping(value = "/tables.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> tables(Pager pager, String dsId) {
		Map<String, Object> attributes = this.getAttributes(pager
				.getCondition());
		return this.rptDatasetBS.getTablesOfDs(
				dsId,
				attributes.get("tableName") == null ? null : attributes.get(
						"tableName").toString(),
				attributes.get("tableComment") == null ? null : attributes
						.get("tableComment").toString().toString(),
				pager.getPageFirstIndex(), pager.getPagesize());
	}
	/**
	 *  ?????????????????????????????????????????????????????????????????????
	 * @param  indexCatalogNo
	 * @return result
	 */
	@RequestMapping(value = "/sameTableEnNameCheck.*", method = RequestMethod.POST)
	@ResponseBody
	public String sameTableEnNameCheck(String dsId,String tableEnName,String datasetId) {
		Map<String, Object>  map  =  Maps.newHashMap();
		map.put("dsId",dsId );
		map.put("tableEnName", tableEnName);
		if(StringUtils.isNotEmpty(datasetId)){
			map.put("datasetId",datasetId );
		}
		return this.rptDatasetBS.sameTableEnNameCheck(map);
	}
	/**
	 * ??????
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(RptSysModuleInfo dataset,Boolean isAdd, String datacolsJsonStr) {
		this.rptDatasetBS.save(dataset,isAdd, datacolsJsonStr);
	}

	/**
	 * ?????????????????????
	 */
	@RequestMapping(value = "/deleteDataset", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> deleteDataset(String ids) {
		Map<String,Object>  param  =  Maps.newHashMap();
		Map<String,Object>  result  =  Maps.newHashMap();
		List<String> idList = Arrays.asList(StringUtils.split(ids, ','));
		if (idList != null && idList.size() > 0) {
			param.put("list",idList);
			boolean  flag = rptDatasetBS.checkHasBeenCascaded(param);
			if(flag){
				result.put("msg","??????????????????????????????????????????????????????????????????????????????????????????");
			}else{
			   this.rptDatasetBS.deleteBatch(idList);
			}
		}
		return result;
	}

	/**
	 * ?????????????????????
	 */
	@RequestMapping(value = "/previewData.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> previewData(Pager pager, String datasetId) {
		return this.rptDatasetBS.datasetPreview(datasetId, 0, 101,
				this.getAttributes(pager.getCondition()), true,"");
	}


	// ??????????????????????????????
	private Map<String, Object> getAttributes(String condition) {
		Map<String, Object> attributes = Maps.newHashMap();
		if (condition != null && !condition.equals("")) {
			JSONObject groupJson = JSON.parseObject(condition);
			JSONArray rulesJson = groupJson.getJSONArray("rules");
			for (int i = 0; i < rulesJson.size(); i ++) {
				JSONObject rule = rulesJson.getJSONObject(i);
				String field = (String) rule.get("field");
				String value = (String) rule.get("value");
				attributes.put(field, value);
			}
		}
		return attributes;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param datasetId
	 *            ?????????Id
	 * @return
	 */
	@RequestMapping(value = "/dsListNone.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> dsListNone() {
		return this.rptDatasetBS.getDataSourcesNone();
	}
	
	
	/**
	 * ????????????????????????
	 * @param searchNm
	 * @return
	 */
	@RequestMapping(value = "/getDataModuleTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getDataModuleTree(String searchNm,String  setType){
		return this.rptDatasetBS.getDataModuleTree(this.getContextPath(), searchNm,setType);
	}
	
	/**
	 * ??????????????????????????????
	 * @author sunym@yuchengtech.com
	 * @return
	 */
	@RequestMapping(value="/getDataSyncTree")
	@ResponseBody
	public List<CommonTreeNode> getDataSyncTree(String setType){
		return this.rptDatasetBS.getDataSyncTree(this.getContextPath(),setType);
	}
	
	/**
	 * ????????????????????????
	 * @return
	 */
	@RequestMapping(value = "/busiTypeList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> busiTypeList() {
		return this.rptDatasetBS.getBusiTypeList(null, null);
	}
	
	/**
	 * ???????????????ID????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/testSameSetId", method = RequestMethod.POST)
	@ResponseBody
	public Boolean testSameSetId(String setId, Boolean isAdd, String oldSetId) {
		return this.rptDatasetBS.testSameSetId(setId, isAdd, oldSetId);
	}
	
}
