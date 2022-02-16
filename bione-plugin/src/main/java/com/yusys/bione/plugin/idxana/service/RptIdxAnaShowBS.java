package com.yusys.bione.plugin.idxana.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonDupontNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.entity.RptCabinOrgInfo;
import com.yusys.bione.plugin.datashow.service.IdxShowBS;
import com.yusys.bione.plugin.design.repository.RptTmpDataDao;
import com.yusys.bione.plugin.idxana.charts.EchartsConstructor;
import com.yusys.bione.plugin.idxana.util.DateUtils;
import com.yusys.bione.plugin.idxana.util.DupontUtils;
import com.yusys.bione.plugin.idxana.util.FetchDataUtils;
import com.yusys.bione.plugin.idxanacfg.entity.*;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsDateRelBS;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsFormulaRelBS;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsInfoBS;
import com.yusys.bione.plugin.rptbank.entity.RptIdxBankInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.*;
import com.yusys.bione.plugin.rptidx.service.IdxResultAnlyBS;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <pre>
 * Title:驾驶舱指标分析前台展现
 * Description: 
 * </pre>
 * 
 * @author yangyf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */

@Service
@Transactional(readOnly = true)
public class RptIdxAnaShowBS extends BaseBS<Object>{
	
	@Autowired
	private RptOrgInfoBS rptOrgInfoBS;
	
	@Autowired
	private IdxShowBS idxshowBS;

	@Autowired
	private IdxResultAnlyBS idxresultanlyBS;
	
	@Autowired
	private RptAnaChartsInfoBS rptanachartsinfoBS;
	
	@Autowired
	private RptAnaChartsFormulaRelBS rptanachartsformularelBS;
	
	@Autowired
	private RptAnaIdxBankInfoBS rptanaidxbankinfoBS;
	
	@Autowired
	private RptAnaChartsDateRelBS rptanachartsdaterelBS;
	
	@Autowired
	private RptCabinIdxFavourBS rptcabinidxfavourBS;
	
	@Autowired
	private RptTmpDataDao rptTmpDAO;
	
	private static String CURRENCY = GlobalConstants4plugin.DIM_TYPE_CURRENCY_NAME;
	
	private static String DATE = GlobalConstants4plugin.DIM_TYPE_DATE_NAME;
	
	private static String ORG = GlobalConstants4plugin.DIM_TYPE_ORG_NAME;
	
	private static String INDEXNO = GlobalConstants4plugin.DIM_TYPE_INDEXNO_NAME;
	
	/**
	 * 获取管驾指标树（异步）
	 * @param upId
	 * @param userId
	 * @return
	 */
	public List<CommonTreeNode> getCabinIdxTree(String upId){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		Map<String,String> menu = new HashMap<String, String>();
		menu.put("nodeType", "idxCatalog");
		if (StringUtils.isBlank(upId)) {//添加根文件夹
			CommonTreeNode treeNode = new CommonTreeNode();
			upId = "0";
			treeNode.setId(upId);
			treeNode.setUpId("-1");
			treeNode.setText("全部");
			treeNode.setParams(menu);
			treeNodes.add(treeNode);
			this.getAllIdx();
			this.getAllMea();
		}
		treeNodes.addAll(this.getIdxLog(upId));
		return treeNodes;
	}
	
	/**
	 * 获取全部的指标和指标目录
	 */
	private void getAllIdx(){
		String jql = "select idx from RptIdxInfo idx where idx.isCabin = ?0 and idx.indexSts = ?1 and idx.endDate = ?2 and idx.isRptIndex = ?3 order by idx.id.indexNo";
		List<RptIdxInfo> rptIdxInfoList = this.baseDAO.findWithIndexParam(jql,"1","Y","29991231","N");//查询全部指标
		EhcacheUtils.put(GlobalConstants4plugin.INDEX_ANALYSIS_DATA_KEY,GlobalConstants4plugin.INDEX_ANALYSIS_ALL_IDX,rptIdxInfoList);
		jql = "select log from RptIdxCatalog log";
		List<RptIdxCatalog> rptIdxlogList = this.baseDAO.findWithIndexParam(jql);//查询全部指标目录
		EhcacheUtils.put(GlobalConstants4plugin.INDEX_ANALYSIS_DATA_KEY,GlobalConstants4plugin.INDEX_ANALYSIS_ALL_IDXLOG,rptIdxlogList);
	}
	
	/**
	 * 获取总账指标去度量的关系
	 */
	private void getAllMea(){
		String jql = "select meaRel from RptIdxMeasureRel meaRel";
		List<RptIdxMeasureRel> rptIdxMeaRelList = this.baseDAO.findWithIndexParam(jql);//查询全部指标度量关系
		jql = "select mea from RptIdxMeasureInfo mea";
		List<RptIdxMeasureInfo> rptIdxMeaList = this.baseDAO.findWithIndexParam(jql);//查询全部度量
		Map<String,List<RptIdxMeasureInfo>> rptIdxMeaMap = new HashMap<String,List<RptIdxMeasureInfo>>();
		for(RptIdxMeasureRel idxMeaRel : rptIdxMeaRelList){
			List<RptIdxMeasureInfo> idxMeaList = rptIdxMeaMap.get(idxMeaRel.getId().getIndexNo());
			if(idxMeaList == null){
				idxMeaList = new ArrayList<RptIdxMeasureInfo>();
			}
			for(RptIdxMeasureInfo idxMea : rptIdxMeaList){
				if(idxMeaRel.getId().getMeasureNo().equals(idxMea.getMeasureNo())){
					idxMeaList.add(idxMea);
				}
			}
			rptIdxMeaMap.put(idxMeaRel.getId().getIndexNo(), idxMeaList);
		}
		EhcacheUtils.put(GlobalConstants4plugin.INDEX_ANALYSIS_DATA_KEY,GlobalConstants4plugin.INDEX_ANALYSIS_ALL_MEASURE,rptIdxMeaMap);
	}
	
	/**
	 * 获取一个指标目录下的指标和目录
	 * @param logId
	 * @return
	 */
	private List<CommonTreeNode> getIdxLog(String logId){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		List<RptIdxCatalog> rptIdxlogList = this.getIdxChLog(logId);
		List<RptIdxInfo> rptIdxInfoList = this.getIdxCh(logId);
		treeNodes.addAll(this.giveIdxTreeNode(rptIdxInfoList));
		treeNodes.addAll(this.giveLogTreeNode(rptIdxlogList));
		return treeNodes;
	}
	
	/**
	 * 获取指标目录下的一级子目录
	 * @param upId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<RptIdxCatalog> getIdxChLog(String logId){
		List<RptIdxCatalog> rptIdxlogList = new ArrayList<RptIdxCatalog>();
		List<RptIdxCatalog> rptIdxlogAllList = (List<RptIdxCatalog>) EhcacheUtils.get(GlobalConstants4plugin.INDEX_ANALYSIS_DATA_KEY,GlobalConstants4plugin.INDEX_ANALYSIS_ALL_IDXLOG);
		for(RptIdxCatalog idxCatalog : rptIdxlogAllList){
			if(logId.equals(idxCatalog.getUpNo())){
				rptIdxlogList.add(idxCatalog);
			}
		}
		return rptIdxlogList;
	}
	
	/**
	 * 获取指标目录下的指标
	 * @param logId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<RptIdxInfo> getIdxCh(String logId){
		List<RptIdxInfo> rptIdxInfoList = new ArrayList<RptIdxInfo>();
		List<RptIdxInfo> rptIdxAllList = (List<RptIdxInfo>) EhcacheUtils.get(GlobalConstants4plugin.INDEX_ANALYSIS_DATA_KEY,GlobalConstants4plugin.INDEX_ANALYSIS_ALL_IDX);
		for(RptIdxInfo idxInfo : rptIdxAllList){
			if(logId.equals(idxInfo.getIndexCatalogNo())){
				rptIdxInfoList.add(idxInfo);
			}
		}
		return rptIdxInfoList;
	}
	
	/**
	 * 获取总账指标下的度量
	 * @param logId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<RptIdxMeasureInfo> getIdxMea(String idxNo){
		Map<String,List<RptIdxMeasureInfo>> rptIdxMeaMap = (Map<String, List<RptIdxMeasureInfo>>) EhcacheUtils.get(GlobalConstants4plugin.INDEX_ANALYSIS_DATA_KEY,GlobalConstants4plugin.INDEX_ANALYSIS_ALL_MEASURE);
		List<RptIdxMeasureInfo> rptIdxMeasureInfoList = rptIdxMeaMap.get(idxNo);
		return rptIdxMeasureInfoList;
	}
	
	/**
	 * 构造指标树节点
	 * @param rptIdxlogList
	 * @return
	 */
	private List<CommonTreeNode> giveIdxTreeNode(List<RptIdxInfo> rptIdxInfoList){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		Map<String,String> menu = new HashMap<String, String>();
		menu.put("nodeType", "idxInfo");
		for(RptIdxInfo rptIdx : rptIdxInfoList){
			CommonTreeNode node = new CommonTreeNode();
			node.setId(rptIdx.getId().getIndexNo());
			node.setText(rptIdx.getIndexNm());
			node.setUpId(rptIdx.getIndexCatalogNo());
			node.setData(rptIdx);
			node.setParams(menu);
			if("05".equals(rptIdx.getIndexType())){
				treeNodes.addAll(this.giveMeaTreeNode(rptIdx.getId().getIndexNo(), this.getIdxMea(rptIdx.getId().getIndexNo())));
			}
			treeNodes.add(node);
		}
		return treeNodes;
	}
	
	/**
	 * 构造指标目录树节点
	 * @param rptIdxlogList
	 * @return
	 */
	private List<CommonTreeNode> giveLogTreeNode(List<RptIdxCatalog> rptIdxlogList){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		Map<String,String> menu = new HashMap<String, String>();
		menu.put("nodeType", "idxCatalog");
		for(RptIdxCatalog rptLog : rptIdxlogList){
			CommonTreeNode node = new CommonTreeNode();
			node.setId(rptLog.getIndexCatalogNo());
			node.setText(rptLog.getIndexCatalogNm());
			node.setUpId(rptLog.getUpNo());
			node.setData(rptLog);
			node.setParams(menu);
			node.setIsParent(true);
			treeNodes.add(node);
		}
		return treeNodes;
	}
	
	/**
	 * 构造度量树节点
	 * @param rptIdxMeasureList
	 * @return
	 */
	private List<CommonTreeNode> giveMeaTreeNode(String idxNo ,List<RptIdxMeasureInfo> rptIdxMeasureList){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		Map<String,String> menu = new HashMap<String, String>();
		menu.put("nodeType", "idxMeasure");
		for(RptIdxMeasureInfo rptMea : rptIdxMeasureList){
			CommonTreeNode node = new CommonTreeNode();
			node.setId(idxNo + "." + rptMea.getMeasureNo());
			node.setText(rptMea.getMeasureNm());
			node.setUpId(idxNo);
			node.setData(rptMea);
			node.setParams(menu);
			treeNodes.add(node);
		}
		return treeNodes;
	}
	
	/**
	 * 维度查询
	 * @param dimType
	 * @return
	 */
	public List<RptDimItemInfo> getDim(String dimType){		
		List<RptDimItemInfo> dimInfo = new ArrayList<RptDimItemInfo>();
		if(StringUtils.isNotBlank(dimType)){
			String jql = "select dim from RptDimItemInfo dim where dim.id.dimTypeNo = ?0";
			dimInfo = this.baseDAO.findWithIndexParam(jql,dimType);
		}		
		return dimInfo;
	}
	
	
	/**
	 * 获取管驾机构中数据
	 * @return
	 */
	public List<CommonComboBoxNode> getOrgInfo(String orgNo){
		List<CommonComboBoxNode> boxNodes = new ArrayList<CommonComboBoxNode>();
		RptOrgInfo rptOrgInfo = this.rptOrgInfoBS.getEntityByProperty(RptOrgInfo.class, "id.orgNo", orgNo);
		if(rptOrgInfo != null){
			if("0".equals(rptOrgInfo.getUpOrgNo())){
				String sql = "select org from RptCabinOrgInfo org order by org.orderNum";
				List<RptCabinOrgInfo> cabinOrgList = this.baseDAO.findWithIndexParam(sql);
				if(cabinOrgList != null && cabinOrgList.size() > 0){
					for(RptCabinOrgInfo rptCabinOrgInfo : cabinOrgList){
						CommonComboBoxNode boxNode = new CommonComboBoxNode();
						boxNode.setId(rptCabinOrgInfo.getOrgNo());
						boxNode.setText(rptCabinOrgInfo.getOrgNm());
						boxNodes.add(boxNode);
					}
				}
			}else{
				String sql = "select org from RptCabinOrgInfo org where org.orgNo = ?0";
				RptCabinOrgInfo rptCabinOrgInfo = this.baseDAO.findUniqueWithIndexParam(sql,orgNo);
				if(rptCabinOrgInfo != null){
					CommonComboBoxNode treeNode = new CommonComboBoxNode();
					treeNode.setId(rptCabinOrgInfo.getOrgNo());
					treeNode.setText(rptCabinOrgInfo.getOrgNm());
					boxNodes.add(treeNode);
				}
			}
		}
		return boxNodes;
	}
	
	/**
	 * 获取指标翻牌日期
	 * @param idxNo
	 * @return
	 */
	public String getDrawDate(String idxNo){
		String drawDate = "";
		if(StringUtils.isNotBlank(idxNo)){
			String[] idxNolist = StringUtils.split(idxNo, ".");
			String jql = "select idx from RptIdxInfo idx where idx.indexSts = ?0 and idx.endDate = ?1 and idx.isRptIndex = ?2 and idx.id.indexNo = ?3";
			RptIdxInfo RptIdxInfo = this.baseDAO.findUniqueWithIndexParam(jql,"Y","29991231","N",idxNolist[0]);
			drawDate = idxshowBS.getIdxDrawDate(idxNolist[0],RptIdxInfo.getCalcCycle());
		}
		return drawDate;
	}
	
	
	/**
	 * 根据指标id查询模版下的图表
	 * @param idxNo 指标编号
	 * @return
	 */
	public Map<String,Object> initchartList(String idxNo){
		Map<String,Object> chartMap = new HashMap<String, Object>();
		List<RptAnaChartsInfo> charts = new ArrayList<RptAnaChartsInfo>();
		RptAnaTmpInfo anaTmpInfo = this.getTmpInfo(idxNo);
		if(anaTmpInfo != null){
			String jql = "select ana from RptAnaChartsInfo ana where ana.templateId = ?0 order by ana.orderNum";
			charts = this.baseDAO.findWithIndexParam(jql, anaTmpInfo.getTemplateId());
		}
		chartMap.put("chartslist", charts);
		return chartMap;
	}
	
	/**
	 *根据指标编号获取模板信息 
	 * @param idxNo
	 * @return
	 */
	private RptAnaTmpInfo getTmpInfo(String idxNo){
		RptAnaTmpInfo tmpInfo = new RptAnaTmpInfo();
		String[] idxNolist = StringUtils.split(idxNo, ".");
		String jql = "select idx from RptAnaTmpIdxRel idx where idx.indexNo = (?0)";
		RptAnaTmpIdxRel idxTmp = this.baseDAO.findUniqueWithIndexParam(jql, idxNolist[0]);
		if(idxTmp == null){
			jql = "select idx from RptAnaTmpInfo idx where idx.isDefault = ?0";
			tmpInfo = this.baseDAO.findUniqueWithIndexParam(jql, "Y");//如果没有使用默认模版
		}else{
			jql = "select idx from RptAnaTmpInfo idx where idx.templateId = ?0";
			tmpInfo = this.baseDAO.findUniqueWithIndexParam(jql, idxTmp.getTemplateId());
		}
		return tmpInfo;
	}
	/**
	 * 根据图表类型生成具体图表
	 * @param idxNo
	 * @param chartId
	 * @param chartType
	 * @param showType
	 * @param idxNm
	 * @param dataUnit
	 * @param date
	 * @param org
	 * @param currency
	 * @param unit
	 * @return
	 */
	public Map<String,Object> initEchart(String idxNo, String chartId, String chartType, String showType, String idxNm, String dataUnit, String date, String org, String currency, String unit, String orgType){
		Map<String,Object> chart = new HashMap<String, Object>();
		JSONObject option = null;
		if(GlobalConstants4plugin.OUTLINE.equals(chartType)){//指标概要图
			option = this.initOutlineCh(idxNo, chartId, chartType, showType, idxNm, dataUnit, date, org, currency, unit);
		}else if(GlobalConstants4plugin.ORG.equals(chartType)){//机构信息图
			option = this.initOrgCh(idxNo, chartId, chartType, showType, idxNm, dataUnit, date, org, currency, unit, orgType);
		}else if(GlobalConstants4plugin.TREND.equals(chartType)){//趋势分析图
			option = this.initTrendCh(idxNo, chartId, chartType, showType, idxNm, dataUnit, date, org, currency, unit);
		}
		chart.put("option", option);
		return chart;
	}
	
	/**
	 * 构建指标概要图
	 * @param idxNo
	 * @param chartId
	 * @param showType
	 * @param idxNm
	 * @param dataUnit
	 * @param date
	 * @param org
	 * @param currency
	 * @param unit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject initOutlineCh(String idxNo, String chartId, String chartType, String showType, String idxNm, String dataUnit, String date, String org, String currency, String unit){
		JSONObject option = null;
		Map<String,Object> formulaRelevant = this.obtainFormula(chartId);
		Map<String,String> formulaMap = (Map<String, String>) formulaRelevant.get("formulaMap");//公式map
		List<RptAnaChartsFormulaRel> formulalist = (List<RptAnaChartsFormulaRel>) formulaRelevant.get("formulalist");//公式list
		Map<String,List<String>> dimMap = this.strDimMap(date, org, currency);//维度map
		Map<String,Object> idxMap = this.strIdxMap(idxNo,idxNm);
		List<String> idxNoList = (List<String>) idxMap.get("idxNoList");//指标编号list
		Map<String,Object> chartCfgMap = this.obtainChartCfg(chartId);//图表配置信息
		Map<String,Object> chartCfg = new HashMap<String,Object>();//构造echarts图表
		Map<String,Object> legendMap = new HashMap<String, Object>();//图例map
		List<String> legendData = (List<String>)idxMap.get("idxNmList");//图例数据list
		legendMap.put("data", legendData);
		String unitNo = getdataUnit(chartId);//单位编号
		if(StringUtils.isNotBlank(unit)){//如果传入单位编码覆盖原单位
			dataUnit = DupontUtils.getdataUnit(unit);
			unitNo = unit;
		}
		Map<String,Map<String,String>> data= FetchDataUtils.FetchDataMap(idxNoList, "03", true, null, null, dimMap, formulaMap, DATE, unitNo, getdataAccuracy(chartId), true);//引擎取数
		if(data != null){
			chartCfg.put("series", this.strOutlineSeries(data.get(date), idxNoList, (Map<String, String>) idxMap.get("idxNmMap"), chartCfgMap,formulalist, showType));
		}
		List<Map<String,Object>> xAxis = Arrays.asList(this.strxAxis(formulaRelevant));//x轴
		List<Map<String,Object>> yAxis = this.stryAxis(idxNm, "单位(" + dataUnit + ")", chartCfgMap);//y轴
		chartCfg.put("color", chartCfgMap.get("color"));
		chartCfg.put("xAxis", xAxis);
		chartCfg.put("yAxis", yAxis);
		chartCfg.put("legend", this.strLegend(legendMap));
		option = EchartsConstructor.getChartsOption(showType, chartCfg, null);
		return option;
	}
	
	/**
	 * 构造图例
	 * @param Data
	 * @return
	 */
	private Map<String,Object> strLegend(Map<String,Object> legendMap){
		Map<String,Object> legend = new HashMap<String,Object>();
		if(legendMap.get("data") != null){
			legend.put("data", legendMap.get("data"));
		}
		return legend;
	}
	
	/**
	 * 构造柱折图x轴
	 * @param chartCfgMap
	 * @return
	 */
	private Map<String,Object> strxAxis(Map<String,Object> chartCfgMap){
		Map<String,Object> xAxis = new HashMap<String,Object>();
		xAxis.put("type", "category");
		xAxis.put("data", chartCfgMap.get("xAxisdata"));
		return xAxis;
	}
	
	/**
	 * 构造柱折图x轴
	 * @param chartCfgMap
	 * @return
	 */
	private Map<String,Object> strxAxis(List<String> xAxisdata){
		Map<String,Object> xAxis = new HashMap<String,Object>();
		xAxis.put("type", "category");
		xAxis.put("data", xAxisdata);
		return xAxis;
	}
	
	/**
	 * 构造柱折图y轴
	 * @param yAxisName
	 * @return
	 */
	private List<Map<String,Object>> stryAxis(String yAxisLName, String yAxisRName, Map<String,Object> chartCfgMap){
		List<Map<String,Object>> yAxisList = new ArrayList<Map<String,Object>>();
		Map<String,Object> yAxis = new HashMap<String,Object>();
		yAxis.put("type", "value");
		yAxis.put("name", yAxisLName);
		yAxisList.add(yAxis);
		if(!("none").equals(chartCfgMap.get("rightType"))){
			yAxis = new HashMap<String,Object>();
			yAxis.put("type", "value");
			yAxis.put("name", yAxisRName);
			yAxisList.add(yAxis);
		}
		return yAxisList;
	}
	
	/**
	 * 构造指标概要series
	 * @param data
	 * @param idxNoList
	 * @param idxNm
	 * @param chartCfgMap
	 * @param formulalist
	 * @return
	 */
	private List<Map<String,Object>> strOutlineSeries(Map<String,String> data, List<String> idxNoList, Map<String,String> idxNm, Map<String,Object> chartCfgMap, List<RptAnaChartsFormulaRel> formulalist, String showType){
		List<Map<String,Object>> seriesList = new ArrayList<Map<String,Object>>();
		if(data != null){
			for(String idxNo : idxNoList){
				List<String> seriesBarData = new ArrayList<String>();
				List<Object> seriesPieData = new ArrayList<Object>();
				for(RptAnaChartsFormulaRel formula : formulalist){
					Map<String,Object> pieData = new HashMap<String, Object>();
					pieData.put("name", formula.getFormulaNm());
					BigDecimal bdpai = new BigDecimal(0);
					if(!"Na".equals(data.get(idxNo + formula.getRelId()))){
						bdpai = new BigDecimal(data.get(idxNo + formula.getRelId()));
						seriesBarData.add(bdpai.toString());
						pieData.put("value", bdpai.toString());
					}else{
						seriesBarData.add("Na");
						pieData.put("value", "Na");
					}
					seriesPieData.add(pieData);
				}
				if("04".equals(showType)){
					seriesList.add(this.strBarSeries(idxNm.get(idxNo), chartCfgMap, seriesBarData, "leftType"));
				}else if("02".equals(showType)){
					seriesList.add(this.strPieSeries(idxNm.get(idxNo), chartCfgMap, seriesPieData));
				}
			}
		}
		return seriesList;
	}
	
	/**
	 * 根据图表ID获取图表关联的公式
	 * @param chartId
	 * @return
	 */
	private Map<String,Object> obtainFormula(String chartId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> formulaMap = new HashMap<String,Object>();
		List<String> xAxisdata = new ArrayList<String>();
		String jql = "select mea from RptAnaChartsFormulaRel mea where mea.chartId = ?0 order by mea.orderNum";
		List<RptAnaChartsFormulaRel> formulalist =  this.baseDAO.findWithIndexParam(jql,chartId);
		for(RptAnaChartsFormulaRel formula : formulalist){
			formulaMap.put(formula.getRelId(), formula.getFormulaContent());
			xAxisdata.add(formula.getFormulaNm());
		}
		returnMap.put("formulaMap", formulaMap);
		returnMap.put("formulalist", formulalist);
		returnMap.put("xAxisdata", xAxisdata);
		return returnMap;
	}
	
	/**
	 * 构造维度Map
	 * @param date
	 * @param org
	 * @param currency
	 * @return
	 */
	private Map<String,List<String>> strDimMap(String date, String org, String currency){
		Map<String,List<String>> dimMap = new HashMap<String,List<String>>();
		List<String> dateList = Arrays.asList(date);
		List<String> orgList = Arrays.asList(org);
		List<String> currencyList = Arrays.asList(currency);
		if(StringUtils.isNotEmpty(date)){
			dimMap.put(DATE, dateList);
		}
		if(StringUtils.isNotEmpty(org)){
			dimMap.put(ORG, orgList);
		}
		if(StringUtils.isNotEmpty(currency)){
			dimMap.put(CURRENCY, currencyList);
		}
		return dimMap;
	}
	
	/**
	 * 构造指标map
	 * @param idxNo
	 * @return
	 */
	private Map<String,Object> strIdxMap(String idxNo, String idxNm){
		Map<String,Object> idxMap = new HashMap<String, Object>();
		String[] idxNos = StringUtils.split(idxNo, ";");
		Map<String,String> idxNmMap = new HashMap<String, String>();
		List<String> idxNoList = new ArrayList<String>(Arrays.asList(idxNos));//数组转list，可能含有指标加度量的形式
		List<String> idxnoList = new ArrayList<String>(Arrays.asList(idxNos));//数组转list，纯指标编号
		for(String idxno : idxNoList ){
			String[] idxnos = StringUtils.split(idxno, ".");
			idxnoList.add(idxnos[0]);
		}
		String jql = "select idx from RptIdxInfo idx where idx.id.indexNo in ?0 and idx.indexSts = ?1 and idx.endDate = ?2";
		List<RptIdxInfo> idxlist =  this.baseDAO.findWithIndexParam(jql,idxnoList,"Y","29991231");
		List<String> idxNmList = new ArrayList<String>();
		for(RptIdxInfo idx : idxlist){
			for(String idxno : idxNoList ){
				String[] idxnos = StringUtils.split(idxno, ".");
				if(idxnos[0].equals(idx.getId().getIndexNo())){
					idxNmMap.put(idxno, idx.getIndexNm());
				};
			}
			idxNmList.add(idx.getIndexNm());
		}
		idxMap.put("idxNoList", idxNoList);
		idxMap.put("idxNmList", idxNmList);
		idxMap.put("idxNmMap", idxNmMap);
		return idxMap;
	}
	
	/**
	 * 获取图表配置
	 * @param chartId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> obtainChartCfg(String chartId){
		Map<String,Object> chartCfgMap = new HashMap<String,Object>();
		RptAnaChartsInfo chartsInfo = rptanachartsinfoBS.getEntityById(RptAnaChartsInfo.class, chartId);//查询图表配置
		if(chartsInfo != null){
			if(StringUtils.isNotBlank(chartsInfo.getChartCfg())){
				String chartCfg = chartsInfo.getChartCfg();
				Map<String, String> chartCfgs = JSON.parseObject(chartCfg, Map.class);
				chartCfgMap = this.strChartCfg(chartCfgs);
			}
			if(StringUtils.isNotBlank(chartsInfo.getChartColor())){
				List<String> colorlists = ArrayUtils.asList(chartsInfo.getChartColor(), ";");
				chartCfgMap.put("color", colorlists);
			}
		}
		return chartCfgMap;
	}
	
	/**
	 * 构造图表配置项
	 * @param chartCfgs
	 * @return
	 */
	private Map<String,Object> strChartCfg(Map<String, String> chartCfgs){
		Map<String,Object> chartCfgMap = new HashMap<String,Object>();
		boolean smooth = true;//是否平滑显示
		Map<String,Object> markLine = new HashMap<String,Object>();
		if(chartCfgs != null){
			String leftType = this.chartYCfg((String) chartCfgs.get("leftY"));
			String rightType = this.chartYCfg((String) chartCfgs.get("rightY"));
			chartCfgMap.put("leftType", leftType);
			chartCfgMap.put("rightType", rightType);
			if("00".equals(chartCfgs.get("smooth"))){
				smooth = false;
			}
			chartCfgMap.put("smooth", smooth);
			if("01".equals(chartCfgs.get("markPoint")) || "03".equals(chartCfgs.get("markPoint"))){
				markLine.put("bar", this.strMarkLine());
			}
			if("01".equals(chartCfgs.get("markPoint")) || "02".equals(chartCfgs.get("markPoint"))){
				markLine.put("line", this.strMarkLine());
			}
			chartCfgMap.put("markLine", markLine);
		}
		return chartCfgMap;
	}
	
	/**
	 * 构造极值
	 * @return
	 */
	private Map<String,Object> strMarkLine(){
		Map<String,Object> markLine = new HashMap<String,Object>();
		Map<String,Object> markLineMin = new HashMap<String, Object>();
		Map<String,Object> markLineMax = new HashMap<String, Object>();
		List<Object> datalist = new ArrayList<Object>();
		markLineMin.put("type", "min");
		markLineMin.put("name", "最小值");
		datalist.add(markLineMin);
		markLineMax.put("type", "max");
		markLineMax.put("name", "最大值");
		datalist.add(markLineMax);
		markLine.put("data", datalist);
		return markLine;
	}
	
	/**
	 * 根据图表配置修改图形
	 * @param chartYCfg
	 * @return
	 */
	public String chartYCfg(String chartYCfg){
		String yType = "none";
		if("01".equals(chartYCfg)){
			yType = "bar";
		}else if("02".equals(chartYCfg)){
			yType = "line";
		}
		return yType;
	}
	
	/**
	 * 构建机构信息图
	 * @param idxNo
	 * @param chartId
	 * @param showType
	 * @param idxNm
	 * @param dataUnit
	 * @param date
	 * @param org
	 * @param currency
	 * @param unit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject initOrgCh(String idxNo, String chartId, String chartType, String showType, String idxNm, String dataUnit, String date, String org, String currency, String unit, String orgType){
		JSONObject option = null;
		Map<String,Object> orgRelevant = this.obtainOrg(chartId, org, orgType);//机构相关
		Map<String,Object> formulaRelevant = this.obtainFormula(chartId);//公式相关
		Map<String,String> formulaMap = (Map<String, String>) formulaRelevant.get("formulaMap");//公式map
		List<RptAnaChartsFormulaRel> formulalist = (List<RptAnaChartsFormulaRel>) formulaRelevant.get("formulalist");//公式list
		Map<String,List<String>> dimMap = this.strDimMap(date, org, currency);//维度map
		Map<String,Object> idxMap = this.strIdxMap(idxNo,idxNm);
		List<String> idxNoList = (List<String>) idxMap.get("idxNoList");//指标编号list
		List<String> idxNmList = (List<String>) idxMap.get("idxNmList");//指标名称list
		List<String> xAxisdata = new ArrayList<String>();//x轴数据
		List<String> formulaNmList = (List<String>) formulaRelevant.get("xAxisdata");//公式名称list
		idxNmList.addAll(formulaNmList);
		Map<String,Object> chartCfgMap = this.obtainChartCfg(chartId);//图表配置信息
		Map<String,Object> chartCfg = new HashMap<String,Object>();//构造echarts图表
		Map<String,Object> legendMap = new HashMap<String, Object>();//图例map
		legendMap.put("data", idxNmList);
		dimMap.put(ORG, (List<String>) orgRelevant.get("orgNoList"));
		String unitNo = getdataUnit(chartId);//单位编号
		if(StringUtils.isNotBlank(unit)){//如果传入单位编码覆盖原单位
			dataUnit = DupontUtils.getdataUnit(unit);
			unitNo = unit;
		}
		Map<String,Map<String,String>> data= FetchDataUtils.FetchDataMap(idxNoList,"03",true,null,null,dimMap,formulaMap,ORG,unitNo,getdataAccuracy(chartId),true,null,true);//引擎取数
		if(data != null){
			chartCfg.put("series", this.strOrgSeries(data, idxNoList, (Map<String, String>) idxMap.get("idxNmMap"), chartCfgMap, formulalist, orgRelevant, showType, dataUnit, xAxisdata));
		}
		List<Map<String,Object>> xAxis = Arrays.asList(this.strxAxis(xAxisdata));//x轴
		List<Map<String,Object>> yAxis = this.stryAxis(idxNm, "单位(" + dataUnit + ")", chartCfgMap);//y轴
		chartCfg.put("color", chartCfgMap.get("color"));
		chartCfg.put("xAxis", xAxis);
		chartCfg.put("yAxis", yAxis);
		chartCfg.put("legend", this.strLegend(legendMap));
		option = EchartsConstructor.getChartsOption(showType, chartCfg, null);
		return option;
	}
	
	/**
	 * 构造机构信息柱折图series
	 * @param data
	 * @param idxNoList
	 * @param idxNm
	 * @param chartCfgMap
	 * @param formulalist
	 * @return
	 */
	@SuppressWarnings({ "unchecked"})
	private List<Map<String,Object>> strOrgSeries(Map<String,Map<String,String>> data, List<String> idxNoList, Map<String,String> idxNm,Map<String,Object> chartCfgMap, List<RptAnaChartsFormulaRel> formulalist, Map<String,Object> orgRelevant, String showType, String dataUnit, List<String> xAxisdata){
		List<Map<String,Object>> seriesList = new ArrayList<Map<String,Object>>();
		Map<String,Object> orgNmMap = (Map<String, Object>) orgRelevant.get("orgNmMap");
		Set<String> orgKeylist = data.keySet();       
		if(orgKeylist != null && orgKeylist.size() > 0){
			for(String idxNo : idxNoList){
				List<Object> seriesPieData = new ArrayList<Object>();//饼图数据
				List<String> seriesLdata = new ArrayList<String>();//左侧轴数据
				List<String> seriesRdata = new ArrayList<String>();//右侧轴数据
				for(String orgNo : orgKeylist){
					Map<String,Object> treeData = new HashMap<String, Object>();
					Map<String,String> datamap =  data.get(orgNo);
					if(datamap != null){
						treeData.put("name", orgNmMap.get(orgNo) + ":\n" + datamap.get(idxNo) + dataUnit);
						treeData.put("value", datamap.get(idxNo));
						for(RptAnaChartsFormulaRel formula : formulalist){
							seriesLdata.add(datamap.get(idxNo));
							seriesRdata.add(datamap.get(idxNo + formula.getRelId()));
						}
					}
					seriesPieData.add(treeData);
					xAxisdata.add((String) orgNmMap.get(orgNo));
				}
				if("01".equals(showType)){
					seriesList.add(this.strBarSeries(idxNm.get(idxNo), chartCfgMap, seriesLdata, "leftType"));
					if((idxNoList.size() == 1) && (formulalist.size() == 1) && (!("none").equals(chartCfgMap.get("rightType")))){
						seriesList.add(this.strBarSeries(formulalist.get(0).getFormulaNm(), chartCfgMap, seriesRdata, "rightType"));
					}
				}else if("02".equals(showType) || "05".equals(showType)){
					seriesList.add(this.strPieSeries(idxNm.get(idxNo), chartCfgMap, seriesPieData));
				}
			}
		}
		return seriesList;
	}
	
	/**
	 * 构造柱折图通用series
	 * @param idxNm
	 * @param chartCfgMap
	 * @param seriesData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> strBarSeries(String idxNm, Map<String,Object> chartCfgMap, List<String> seriesData, String yAxisType){
		Map<String,Object> series = new HashMap<String, Object>();
		String yAxisIndex = "0";
		if("rightType".equals(yAxisType)){
			yAxisIndex = "1";
		}
		Map<String,Object> markLine = (Map<String, Object>) chartCfgMap.get("markLine");
		series.put("name", idxNm);
		series.put("type", chartCfgMap.get(yAxisType));
		series.put("data", seriesData);
		series.put("yAxisIndex", yAxisIndex);
		if(markLine.get(chartCfgMap.get(yAxisType)) != null){
			series.put("markLine", markLine.get(chartCfgMap.get(yAxisType)));
		}
		return series;
	}
	
	/**
	 * 构造饼图矩形树图通用series
	 * @param idxNm
	 * @param chartCfgMap
	 * @param seriesData
	 * @return
	 */
	private Map<String,Object> strPieSeries(String idxNm, Map<String,Object> chartCfgMap, List<Object> seriesData){
		Map<String,Object> series = new HashMap<String, Object>();
		series.put("name", idxNm);
		series.put("data", seriesData);
		return series;
	}
	
	/**
	 * 根据图表ID获取图表关联的机构
	 * @param chartId
	 * @return
	 */
	private Map<String,Object> obtainOrg(String chartId, String org, String orgType){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> orgNmMap = new HashMap<String, Object>();
		List<String> orgNoList = new ArrayList<String>();
		List<RptOrgInfo> orgList = rptOrgInfoBS.findOrgChild(org, orgType, false, false);
		if(orgList != null && orgList.size() > 0){
			for(RptOrgInfo orgInfo : orgList){
				orgNoList.add(orgInfo.getId().getOrgNo());
				orgNmMap.put(orgInfo.getId().getOrgNo(), orgInfo.getOrgNm());
			}
		}
		returnMap.put("orgNoList", orgNoList);
		returnMap.put("orgNmMap", orgNmMap);
		return returnMap;
	}
	
	/**
	 * 构建趋势分析图
	 * @param idxNo
	 * @param chartId
	 * @param showType
	 * @param idxNm
	 * @param dataUnit
	 * @param date
	 * @param org
	 * @param currency
	 * @param unit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject initTrendCh(String idxNo, String chartId, String chartType, String showType, String idxNm, String dataUnit, String date, String org, String currency, String unit){
		JSONObject option = null;
		Map<String,Object> dateRelevant = this.obtainDate(chartId, date);
		Map<String,Object> formulaRelevant = this.obtainFormula(chartId);
		Map<String,String> formulaMap = (Map<String, String>) formulaRelevant.get("formulaMap");//公式map
		List<RptAnaChartsFormulaRel> formulalist = (List<RptAnaChartsFormulaRel>) formulaRelevant.get("formulalist");//公式list
		Map<String,List<String>> dimMap = this.strDimMap(date, org, currency);//维度map
		Map<String,Object> idxMap = this.strIdxMap(idxNo,idxNm);
		List<String> idxNoList = (List<String>) idxMap.get("idxNoList");//指标编号list
		Map<String,Object> chartCfgMap = this.obtainChartCfg(chartId);//图表配置信息
		Map<String,Object> chartCfg = new HashMap<String,Object>();//构造echarts图表
		Map<String,Object> legendMap = new HashMap<String, Object>();//图例map
		List<String> legendData = (List<String>) formulaRelevant.get("xAxisdata");//图例数据list
		legendData.addAll((Collection<? extends String>) idxMap.get("idxNmList"));
		legendMap.put("data", legendData);
		dimMap.put(DATE, (List<String>) dateRelevant.get("xAxisdata"));
		String unitNo = getdataUnit(chartId);//单位编号
		if(StringUtils.isNotBlank(unit)){//如果传入单位编码覆盖原单位
			dataUnit = DupontUtils.getdataUnit(unit);
			unitNo = unit;
		}
		Map<String,Map<String,String>>  data= FetchDataUtils.FetchDataMap(idxNoList,"03",true,null,null,dimMap,formulaMap,DATE,unitNo,getdataAccuracy(chartId),true,null,true);
		if(data != null){
			chartCfg.put("series", this.strDateSeries(data, idxNoList, (Map<String, String>) idxMap.get("idxNmMap"), chartCfgMap, formulalist, (List<String>) dateRelevant.get("xAxisdata")));
		}
		List<Map<String,Object>> xAxis = Arrays.asList(this.strxAxis(dateRelevant));//x轴
		List<Map<String,Object>> yAxis = this.stryAxis(idxNm, "单位(" + dataUnit + ")", chartCfgMap);//y轴
		chartCfg.put("color", chartCfgMap.get("color"));
		chartCfg.put("xAxis", xAxis);
		chartCfg.put("yAxis", yAxis);
		chartCfg.put("legend", this.strLegend(legendMap));
		option = EchartsConstructor.getChartsOption(showType, chartCfg, null);
		return option;
	}
	
	/**
	 * 构造趋势分析柱折图series
	 * @param data
	 * @param idxNoList
	 * @param idxNm
	 * @param chartCfgMap
	 * @param formulalist
	 * @return
	 */
	private List<Map<String,Object>> strDateSeries(Map<String,Map<String,String>> data, List<String> idxNoList, Map<String,String> idxNm,Map<String,Object> chartCfgMap, List<RptAnaChartsFormulaRel> formulalist, List<String> dateList){
		List<Map<String,Object>> seriesList = new ArrayList<Map<String,Object>>();
		for(String idxNo : idxNoList){
			List<String> seriesLdata = new ArrayList<String>();
			List<String> seriesRdata = new ArrayList<String>();
			for(String date : dateList){
				Map<String,String> datamap =  data.get(date);
				if(datamap != null){
					seriesLdata.add(datamap.get(idxNo));
					for(RptAnaChartsFormulaRel formula : formulalist){
						seriesRdata.add(datamap.get(idxNo + formula.getRelId()));
					}
				}else{
					seriesLdata.add("Na");
					seriesRdata.add("Na");
				}
			}
			seriesList.add(this.strBarSeries(idxNm.get(idxNo), chartCfgMap, seriesLdata, "leftType"));
			if(idxNoList.size() == 1 && (!("none").equals(chartCfgMap.get("rightType")))){
				seriesList.add(this.strBarSeries(formulalist.get(0).getFormulaNm(), chartCfgMap, seriesRdata, "rightType"));
			}
		}
		return seriesList;
	}
	
	/**
	 * 根据图表ID获取图表关联的日期
	 * @param chartId
	 * @param date
	 * @return
	 */
	private Map<String,Object> obtainDate(String chartId, String date){
		Map<String,Object> returnMap = new HashMap<String, Object>();
		String startDate = "";
		String endDate = "";
		SimpleDateFormat matter=new SimpleDateFormat("yyyyMMdd");
		Date today = new Date();
		try {
			today = matter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		RptAnaChartsDateRel chartsData = rptanachartsdaterelBS.getEntityById(chartId);
		if(chartsData != null){
			if("01".equals(chartsData.getShowType())){//显示范围当月
				startDate = matter.format(DateUtils.getStartDateOfMonth(today));
				endDate = matter.format(today);
			}else if("02".equals(chartsData.getShowType())){//显示范围当季
				startDate = matter.format(DateUtils.getStartDateOfSeason(today));
				endDate = matter.format(today);
			}else if("03".equals(chartsData.getShowType())){//显示范围当年
				startDate = matter.format(DateUtils.getStartDateOfYear(today));
				endDate = matter.format(today);
			}else if("04".equals(chartsData.getShowType())){//显示范围自定义
				startDate = chartsData.getStartDate();
				endDate = chartsData.getEndDate();
			}else if("05".equals(chartsData.getShowType())){//显示范围，最近12个月
				startDate = matter.format(DateUtils.getEndDateOfMonth(DateUtils.getPastDate(365, today)));
				endDate = matter.format(today);
			}else if("06".equals(chartsData.getShowType())){//显示范围，最近7天
				startDate = matter.format(DateUtils.getPastDate(6, today));
				endDate = matter.format(today);
			}else if("07".equals(chartsData.getShowType())){//显示范围，最近30天
				startDate = matter.format(DateUtils.getPastDate(29, today));
				endDate = matter.format(today);
			}
			if(startDate.equals(matter.format(today))){
				endDate = startDate;
			}
			returnMap.put("xAxisdata", this.getDatelist(startDate, endDate, chartsData.getDateFreq()));
		}
		return returnMap;
	}
	
	
	/**
	 * 获取日期集合
	 * @param Start 开始日期
	 * @param End 结束日期
	 * @param DisplayFreq 显示频度
	 * @return
	 */
	private List<String> getDatelist(String start,String end,String dateFreq){
		List<String> datelist = new ArrayList<String>();
		Date StartDate=new Date();
		Date EndDate=new Date();
		SimpleDateFormat matter=new SimpleDateFormat("yyyyMMdd");
		try {
			StartDate = matter.parse(start);
			EndDate = matter.parse(end);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if("01".equals(dateFreq)){//显示频度 日
			while(!(matter.format(StartDate).equals(matter.format(EndDate)))){
				datelist.add(matter.format(StartDate));
				StartDate = DateUtils.getFutureDate(1,StartDate);
			}
			datelist.add(matter.format(EndDate));
		}else if("02".equals(dateFreq)){//显示频度月
			while(!(matter.format(DateUtils.getStartDateOfMonth(StartDate)).equals(matter.format(DateUtils.getStartDateOfMonth(EndDate))))){
				datelist.add(matter.format(DateUtils.getEndDateOfMonth(StartDate)));
				StartDate = DateUtils.getStartDateOfNextMonth(StartDate);
			}
			datelist.add(matter.format(EndDate));
		}else if("03".equals(dateFreq)){//显示频度季
			while(!(matter.format(DateUtils.getStartDateOfSeason(StartDate)).equals(matter.format(DateUtils.getStartDateOfSeason(EndDate))))){
				datelist.add(matter.format(DateUtils.getEndDateOfLastSeason(StartDate)));
				StartDate = DateUtils.getStartDateOfNextMonth(DateUtils.getEndDateOfLastSeason(StartDate));
			}
			datelist.add(matter.format(EndDate));
		}else if("04".equals(dateFreq)){//显示频度年
			while(!(matter.format(DateUtils.getStartDateOfYear(StartDate)).equals(matter.format(DateUtils.getStartDateOfYear(EndDate))))){
				datelist.add(matter.format(DateUtils.getEndDateOfYear(StartDate)));
				StartDate = DateUtils.getStartDateOfNextMonth(DateUtils.getEndDateOfYear(StartDate));
			}
			datelist.add(matter.format(EndDate));
		}
		return datelist;
	}
	
	/**
	 * 查询指标映射模板信息
	 * @param idxNo 指标编号
	 * @param date 日期
	 * @param org 机构
	 * @param currency 币种
	 * @param unit 单位编号
	 * @return
	 */
	public Map<String,Object> initTitle(String idxNo, String date, String org, String currency, String unit){
		Map<String,Object> menu = new HashMap<String,Object>();
		RptAnaTmpInfo anaTmpInfo = this.getTmpInfo(idxNo);
		if(anaTmpInfo != null){
			if(StringUtils.isNotBlank(unit)){
				anaTmpInfo.setDataUnit(unit);
			}
			String dataUnit = anaTmpInfo.getDataUnit();
			anaTmpInfo.setDataUnit(getUnit(anaTmpInfo));
			String jql = "select ana from RptAnaTmpFormulaRel ana where ana.templateId = ?0 order by ana.orderNum";
			List<RptAnaTmpFormulaRel> tmpFaList = this.baseDAO.findWithIndexParam(jql, anaTmpInfo.getTemplateId());//查询模版下的公式
			menu = this.getTitleData(tmpFaList, idxNo, date, org, currency, dataUnit, anaTmpInfo.getDataUnit(), anaTmpInfo.getDataPrecision());
		}
		menu.put("tmpInfo", anaTmpInfo);
		return menu;
	}
	
	/**
	 * 获取模板公式值
	 * @param tmpFoList
	 * @param idxNo
	 * @param date
	 * @param org
	 * @param currency
	 * @param dataUnit
	 * @param dataAccuracy
	 * @return
	 */
	private Map<String,Object> getTitleData(List<RptAnaTmpFormulaRel> tmpFoList, String idxNo, String date, String org, String currency, String dataUnit, String unitNm, BigDecimal dataAccuracy){
		Map<String,Object> menu = new HashMap<String,Object>();
		List<String> idxlist = new ArrayList<String>();//指标list
		Map<String,List<String>> dimMap = new HashMap<String, List<String>>();
		Map<String,String> formulaMap = new HashMap<String, String>();
		String idxVolue = "";
		idxlist.add(idxNo);
		if(StringUtils.isNotEmpty(date)){
			dimMap.put(DATE,Arrays.asList(date));
		}
		if(StringUtils.isNotEmpty(org)){
			dimMap.put(ORG,Arrays.asList(org));
		}
		if(StringUtils.isNotEmpty(currency)){
			dimMap.put(CURRENCY,Arrays.asList(currency));
		}
		if(tmpFoList != null && tmpFoList.size() > 0){
			for(RptAnaTmpFormulaRel formula : tmpFoList){
				formulaMap.put(formula.getRelId(), formula.getFormulaContent());
			}
			Map<String,Map<String,String>> data= FetchDataUtils.FetchDataMap(idxlist,"03",true,null,null,dimMap,formulaMap,DATE,dataUnit,dataAccuracy,true,null,true);//引擎取数
			Map<String,String> datamap =  data.get(date);
			for(RptAnaTmpFormulaRel formula :tmpFoList){
				if(datamap == null){//返回数据处理
					formula.setFormulaContent("Na");
					idxVolue = "Na";
				}else{
					formula.setFormulaContent(datamap.get(idxNo + formula.getRelId()));
					idxVolue = datamap.get(idxNo);
				}
			}
		}
		menu.put("formula", tmpFoList);
		menu.put("idxVolue", idxVolue);
		return menu;
	}
	
	/**
	 * 获取指标与其同类组的维度
	 * @param idxNo
	 * @return
	 */
	public List<RptDimTypeInfo> queryIdxDim(String idxNo){
		String[] idxNolist = StringUtils.split(idxNo, ".");
		idxNo = idxNolist[0];
		List<RptDimTypeInfo> RptDimTypeInfolist = new ArrayList<RptDimTypeInfo>();
		Map<String,RptDimTypeInfo> RptDimTypeInfoMap = new HashMap<String, RptDimTypeInfo>();
		List<RptIdxDimRel> RptIdxDimRellist = this.baseDAO.findWithIndexParam("select idx from RptIdxDimRel idx where idx.id.indexNo= ?0 ",idxNo);
		if(RptIdxDimRellist.size() > 0){
			for(RptIdxDimRel RptIdxDimRel : RptIdxDimRellist){
				RptDimTypeInfo RptDimTypeInfo =  (com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo) this.baseDAO.getObjectById(RptDimTypeInfo.class, RptIdxDimRel.getId().getDimNo());
				if(RptDimTypeInfo != null ){
					if(!(RptDimTypeInfo.getDimTypeNo().equals(INDEXNO) || RptDimTypeInfo.getDimTypeNo().equals(ORG) || RptDimTypeInfo.getDimTypeNo().equals(DATE))){
						RptDimTypeInfoMap.put(RptDimTypeInfo.getDimTypeNo(), RptDimTypeInfo);
						RptDimTypeInfo.setDimTypeEnNm(RptDimTypeInfo.getDimTypeNo() +";"+ idxNo);
						RptDimTypeInfolist.add(RptDimTypeInfo);
					}
				}
			}
		}
		//指标同类组
		List<RptIdxSimilarGrp> RptIdxSimilarGrpList = this.baseDAO.findWithIndexParam("select idx from RptIdxSimilarGrp idx where idx.id.indexNo= ?0 ",idxNo);//查询指标所属的同类组
		if(RptIdxSimilarGrpList.size() > 0){
			for(RptIdxSimilarGrp RptIdxSimilar : RptIdxSimilarGrpList){
				List<RptIdxSimilarGrp> RptIdxSimilarGrpList1 = this.baseDAO.findWithIndexParam("select idx from RptIdxSimilarGrp idx where idx.id.simigrpId = ?0 ",RptIdxSimilar.getId().getSimigrpId());//查询这些同类组下的指标
				if(RptIdxSimilarGrpList.size() > 0){
					for(RptIdxSimilarGrp RptIdxSimilarGrp : RptIdxSimilarGrpList1){
						if(!RptIdxSimilarGrp.getId().getIndexNo().equals(idxNo)){
							//查询该指标编号映射的指标
							List<RptIdxInfo> RptIdxInfoList = this.baseDAO.findWithIndexParam("select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.isCabin = ?1 and idx.indexSts = ?2 and idx.endDate = ?3",RptIdxSimilarGrp.getId().getIndexNo(),"1","Y","29991231");
							if(RptIdxInfoList.size() > 0){
								for(RptIdxInfo RptIdxInfo : RptIdxInfoList){
									//查询这个指标对应的维度
									List<RptIdxDimRel> RptIdxDimRellist1 = this.baseDAO.findWithIndexParam("select idx from RptIdxDimRel idx where idx.id.indexNo= ?0 ",RptIdxInfo.getId().getIndexNo());
									for(RptIdxDimRel RptIdxDimRel : RptIdxDimRellist1){
										//查询这个维度编号对应的维度
										RptDimTypeInfo RptDimTypeInfo =  (com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo) this.baseDAO.getObjectById(RptDimTypeInfo.class, RptIdxDimRel.getId().getDimNo());
										if(RptDimTypeInfo != null ){
											if(!(RptDimTypeInfo.getDimTypeNo().equals(INDEXNO) || RptDimTypeInfo.getDimTypeNo().equals(ORG) || RptDimTypeInfo.getDimTypeNo().equals(DATE))){
												if(RptDimTypeInfoMap.get(RptDimTypeInfo.getDimTypeNo()) == null){//保证这个维度之前没有
													RptDimTypeInfoMap.put(RptDimTypeInfo.getDimTypeNo(), RptDimTypeInfo);
													RptDimTypeInfo.setDimTypeNm(RptDimTypeInfo.getDimTypeNm() + "(" + RptIdxInfo.getIndexNm() + ")");
													RptDimTypeInfo.setDimTypeEnNm(RptDimTypeInfo.getDimTypeNo() +";"+ RptIdxInfo.getId().getIndexNo());
													RptDimTypeInfolist.add(RptDimTypeInfo);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return RptDimTypeInfolist;
	}
	
	/**
	 * 生成结构解析杜邦图
	 * @param idxNo 指标编号
	 * @param idxNm 指标名称
	 * @param date 日期
	 * @param org 机构
	 * @param currency 币种
	 * @param chartId 图表id
	 * @param dataUnit 数据单位（中文）
	 * @param ReturnDimkey 生成杜邦维度
	 * @param unit 数据单位（编号）
	 * @return
	 */
	public CommonDupontNode initReldupont(String idxNo, String idxNm, String date, String org, String currency, String chartId, String dataUnit, String ReturnDimkey, String unit){
		CommonDupontNode DupontNode = new CommonDupontNode();
		List<String> datelist = new ArrayList<String>();
		List<String> orglist = new ArrayList<String>();
		List<String> currencylist = new ArrayList<String>();
		List<String> dimlist = new ArrayList<String>();
		Map<String,String> formulaMap = new HashMap<String, String>();
		Map<String,List<String>> dimMap = new HashMap<String, List<String>>();
		String dataunit = "01";
		String forNm = "";
		datelist.add(date);
		orglist.add(org);
		currencylist.add(currency);
		if(StringUtils.isNotEmpty(date)){
			dimMap.put(DATE,Arrays.asList(date));
		}
		if(StringUtils.isNotEmpty(org)){
			dimMap.put(ORG,Arrays.asList(org));
		}
		if(StringUtils.isNotEmpty(currency)){
			dimMap.put(CURRENCY,Arrays.asList(currency));
		}
		List<RptDimItemInfo> RptDimItemInfolist = idxresultanlyBS.getDimItems(ReturnDimkey);
		RptAnaChartsInfo AnaChartsInfo = rptanachartsinfoBS.getEntityById(chartId);
		RptAnaChartsFormulaRel anaChartsMeaRel = rptanachartsformularelBS.findUniqueEntityByProperty("chartId", chartId);
		if(AnaChartsInfo != null){
			String jql = "select idx from RptAnaTmpInfo idx where idx.templateId = (?0)";
			RptAnaTmpInfo AnaTmpInfo = this.baseDAO.findUniqueWithIndexParam(jql,AnaChartsInfo.getTemplateId());
			if(AnaTmpInfo != null){
				dataunit = AnaTmpInfo.getDataUnit();
				if(StringUtils.isNotBlank(unit)){
					dataUnit = unit;
				}
			}
		}
		if(anaChartsMeaRel != null){
			forNm = anaChartsMeaRel.getFormulaNm();
			formulaMap.put("increment", "I('$1')-" + anaChartsMeaRel.getFormulaContent());
			formulaMap.put("increase", "(I('$1')-" + anaChartsMeaRel.getFormulaContent() + ")/" + anaChartsMeaRel.getFormulaContent());
		}
		if(RptDimItemInfolist.size()>0){
			for(RptDimItemInfo RptDimItemInfo : RptDimItemInfolist){
				dimlist.add(RptDimItemInfo.getId().getDimItemNo());
			}
			dimMap.put(ReturnDimkey , dimlist);
			DupontNode = DupontUtils.GenerateDupontNode(idxNo, idxNm, dimMap, ReturnDimkey, getdataAccuracy(chartId), dataunit,formulaMap,forNm);
		}else{
			DupontNode = DupontUtils.GenerateDupontNode(idxNo, idxNm, dimMap, CURRENCY, getdataAccuracy(chartId), dataunit,formulaMap,forNm);
		}
		return DupontNode;
	}
	
	
	/**
	 * 获取单位
	 * @param AnaTmpInfo
	 * @return
	 */
	private String getUnit(RptAnaTmpInfo AnaTmpInfo){
		String dataUnit = "";
		if(AnaTmpInfo != null){
			if(("02").equals(AnaTmpInfo.getDataFormat())){//百分比
				dataUnit = "%";
			}else if(("01").equals(AnaTmpInfo.getDataFormat())){//金额
				if(("01").equals(AnaTmpInfo.getDataUnit())){
					dataUnit = "元";
				}else if(("02").equals(AnaTmpInfo.getDataUnit())){
					dataUnit = "百元";
				}else if(("03").equals(AnaTmpInfo.getDataUnit())){
					dataUnit = "千元";
				}else if(("04").equals(AnaTmpInfo.getDataUnit())){
					dataUnit = "万";
				}else if(("05").equals(AnaTmpInfo.getDataUnit())){
					dataUnit = "亿";
				}
			}else if(("03").equals(AnaTmpInfo.getDataFormat())){//数值
				if(("01").equals(AnaTmpInfo.getDataFormat())){
					dataUnit = "个";
				}else if(("02").equals(AnaTmpInfo.getDataFormat())){
					dataUnit = "百";
				}else if(("03").equals(AnaTmpInfo.getDataFormat())){
					dataUnit = "千";
				}else if(("04").equals(AnaTmpInfo.getDataFormat())){
					dataUnit = "万";
				}else if(("05").equals(AnaTmpInfo.getDataFormat())){
					dataUnit = "亿";
				}
			}
		}
		return dataUnit;	
	}
	
	/**
	 * 获取数据精度
	 * @param chartId
	 * @return
	 */
	private BigDecimal getdataAccuracy(String chartId){
		RptAnaChartsInfo AnaChartsInfo = rptanachartsinfoBS.getEntityById(chartId);
		String jql = "select idx from RptAnaTmpInfo idx where idx.templateId = (?0)";
		RptAnaTmpInfo AnaTmpInfo = this.baseDAO.findUniqueWithIndexParam(jql,AnaChartsInfo.getTemplateId());
		BigDecimal dataAccuracy  = AnaTmpInfo.getDataPrecision();
		return dataAccuracy;	
	}
	
	
	/**
	 * 获取单位码
	 * @param chartId
	 * @return
	 */
	private String getdataUnit(String chartId){
		String dataChange = "01";
		RptAnaChartsInfo AnaChartsInfo = rptanachartsinfoBS.getEntityById(chartId);
		if(AnaChartsInfo != null){
			String jql = "select idx from RptAnaTmpInfo idx where idx.templateId = (?0)";
			RptAnaTmpInfo AnaTmpInfo = this.baseDAO.findUniqueWithIndexParam(jql,AnaChartsInfo.getTemplateId());
			if(AnaTmpInfo != null){
				dataChange = AnaTmpInfo.getDataUnit();
			}
		}
		return dataChange;	
	}
	
	/**
	 * 生成关系解析杜邦图
	 * @param idxNo 指标编号
	 * @param chartId 图表id
	 * @param idxNm 指标名称
	 * @param date 日期
	 * @param org 机构
	 * @param currency 币种
	 * @param unit 单位编号
	 * @return
	 */
	public CommonDupontNode initStrdupont(String idxNo, String chartId, String idxNm, String date, String org, String currency, String unit, String orgType){
		CommonDupontNode DupontNode = new CommonDupontNode();
		List<String> datelist = new ArrayList<String>();
		List<String> orglist = new ArrayList<String>();
		List<String> currencylist = new ArrayList<String>();
		Map<String,List<String>> dimMap = new HashMap<String, List<String>>();
		Map<String,String> formulaMap = new HashMap<String, String>();
		String themeId = "";
		String forNm = "";
		String chartUnit = getdataUnit(chartId);
		if(StringUtils.isNotBlank(unit)){
			chartUnit = unit;
		}
		datelist.add(date);
		orglist.add(org);
		currencylist.add(currency);
		if(StringUtils.isNotEmpty(date)){
			dimMap.put(DATE, datelist);
		}
		if(StringUtils.isNotEmpty(org)){
			dimMap.put(ORG, orglist);
		}
		if(StringUtils.isNotEmpty(currency)){
			dimMap.put(CURRENCY, currencylist);
		}
		RptAnaChartsInfo AnaChartsInfo = rptanachartsinfoBS.getEntityById(chartId);
		RptAnaChartsFormulaRel anaChartsMeaRel = rptanachartsformularelBS.findUniqueEntityByProperty("chartId", chartId);
		if(AnaChartsInfo != null){
			String jql = "select idx from RptAnaChartsThemeRel idx where idx.chartId = (?0)";
			RptAnaChartsThemeRel AnaChartsStrInfo = this.baseDAO.findUniqueWithIndexParam(jql,chartId);
			if(AnaChartsStrInfo != null){
				themeId = AnaChartsStrInfo.getThemeId();
				RptIdxBankInfo peculiar = rptanaidxbankinfoBS.getMainIdx(idxNo, themeId, currency);
				if(peculiar != null){
					idxNo = peculiar.getId().getIndexId();
				}
				if(anaChartsMeaRel != null){
					forNm = anaChartsMeaRel.getFormulaNm();
					formulaMap.put("increment", "I('$1')-" + anaChartsMeaRel.getFormulaContent());
					formulaMap.put("increase", "(I('$1')-" + anaChartsMeaRel.getFormulaContent() + ")/" + anaChartsMeaRel.getFormulaContent());
				}
				DupontNode = DupontUtils.GenRptDupontNode(idxNo, idxNm, dimMap,DATE,themeId,getdataAccuracy(chartId), chartUnit, true, formulaMap, forNm, orgType);
			}
		}
		return DupontNode;
	}
	
	/**
	 * 获取用户收藏的指标
	 * @param userId
	 * @return
	 */
	public Map<String,Object> getFavourIdx(String userId){
		Map<String,Object> idxMap = new HashMap<String, Object>();
		String jql = "select idx from RptCabinIdxFavour idx where idx.id.userId = ?0";
		List<RptCabinIdxFavour> idxList = this.baseDAO.findWithIndexParam(jql, userId);//查询用户收藏的指标
		for(RptCabinIdxFavour idx : idxList){
			idxMap.put(idx.getId().getIndexNo(), idx);
		}
		return idxMap;
	}
	
	/**
	 * 指标收藏
	 * @param userId
	 * @param idxNo
	 * @param idxNm
	 * @return
	 */
	@Transactional(readOnly = false)
	public String idxCollect(String userId, String idxNo, String idxNm, String currency){
		String returnType = "";
		BigDecimal one = new BigDecimal(1);
		BigDecimal maxNo = this.baseDAO.findUniqueWithIndexParam("select max(idx.orderNum) from RptCabinIdxFavour idx where idx.id.userId = ?0",userId);
		RptCabinIdxFavour idx = this.baseDAO.findUniqueWithIndexParam("select idx from RptCabinIdxFavour idx where idx.id.userId = ?0 and idx.id.indexNo = ?1",userId,idxNo);
		if(idx != null){
			this.rptcabinidxfavourBS.removeEntity(idx);
			returnType = "delete";
		}else{
			RptCabinIdxFavour idxFavour = new RptCabinIdxFavour();
			RptCabinIdxFavourPK idxFavourPK = new RptCabinIdxFavourPK();
			Timestamp date = new Timestamp(new Date().getTime());
			idxFavourPK.setIndexNo(idxNo);
			idxFavourPK.setUserId(userId);
			idxFavour.setId(idxFavourPK);
			idxFavour.setIndexNm(idxNm);
			idxFavour.setFavourDate(date);
			idxFavour.setIndexType("1");
			idxFavour.setIndexCurrency(currency);
			if(maxNo == null){
				idxFavour.setOrderNum(one);
			}else{
				idxFavour.setOrderNum(maxNo.add(one));
			}
			this.rptcabinidxfavourBS.saveOrUpdateEntity(idxFavour);
			returnType = "save";
		}
		return returnType;
	}
	
	/**
	 * 获取指标对比组
	 * @param idxNo
	 * @return
	 */
	public List<CommonTreeNode> getRptGrp(String idxNo, String isCabin){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		List<String> idxGrpNoList = new ArrayList<String>();
		Map<String,String> menu = new HashMap<String, String>();
		List<RptIdxInfo> idxList  = new ArrayList<RptIdxInfo>();
		CommonTreeNode treeNode = new CommonTreeNode();
		menu.put("nodeType", "idxCatalog");
		treeNode.setId("-1");
		treeNode.setText("对比组");
		treeNode.setIsParent(true);
		treeNode.setUpId("0");
		treeNode.setParams(menu);
		treeNodes.add(treeNode);
		String[] idxNolist = StringUtils.split(idxNo, ".");
		idxNo = idxNolist[0];
		//指标对比组
		List<RptIdxCompGrp> idxGrpList = this.baseDAO.findWithIndexParam("select idx from RptIdxCompGrp idx where idx.id.mainIndexNo = ?0",idxNo);
		if(idxGrpList.size() > 0){
			menu = new HashMap<String, String>();
			menu.put("nodeType", "idxInfoCm");
			for(RptIdxCompGrp idxGrp : idxGrpList){
				idxGrpNoList.add(idxGrp.getId().getIndexNo());
			}
			if("1".equals(isCabin)){
				String jql = "select idx from RptIdxInfo idx where idx.id.indexNo in ?0 and idx.indexSts = ?1 and idx.endDate = ?2 and idx.isCabin = ?3";
				idxList = this.baseDAO.findWithIndexParam(jql,idxGrpNoList,"Y","29991231",isCabin);
			}else{
				String jql = "select idx from RptIdxInfo idx where idx.id.indexNo in ?0 and idx.indexSts = ?1 and idx.endDate = ?2";
				idxList = this.baseDAO.findWithIndexParam(jql,idxGrpNoList,"Y","29991231");
			}
			if(idxList.size() > 0){
				for(RptIdxInfo idx : idxList){
					CommonTreeNode treeNode1 = new CommonTreeNode();
					treeNode1.setId(idx.getId().getIndexNo());
					treeNode1.setText(idx.getIndexNm());
					treeNode1.setUpId("-1");
					treeNode1.setData(idx);
					treeNode1.setParams(menu);
					treeNodes.add(treeNode1);
					if("05".equals(idx.getIndexType())){
						treeNodes.addAll(getMeaTree(idx.getId().getIndexNo()));
					}
				}
			}
		}
		return treeNodes;
	}
	
	/**
	 * 获取一个总账指标的度量
	 * @param idxNo
	 * @return
	 */
	private List<CommonTreeNode> getMeaTree(String idxNo){//总账指标需要特殊处理一下
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		List<String> measureNoList = new ArrayList<String>();
		Map<String,String> menu = new HashMap<String, String>();
		menu.put("nodeType", "idxMeasure");
		String jql = "select mea from RptIdxMeasureRel mea where mea.id.indexNo = ?0";
		List<RptIdxMeasureRel> rptIdxMeasureRelList = this.baseDAO.findWithIndexParam(jql,idxNo);
		if(rptIdxMeasureRelList != null && rptIdxMeasureRelList.size() > 0){
			for(RptIdxMeasureRel rptIdxMeasureRel : rptIdxMeasureRelList){
				measureNoList.add(rptIdxMeasureRel.getId().getMeasureNo());
			}
			jql = "select mea from RptIdxMeasureInfo mea where mea.measureNo in ?0";
			List<RptIdxMeasureInfo> rptIdxMeasureInfoList = this.baseDAO.findWithIndexParam(jql, measureNoList);
			if(rptIdxMeasureInfoList != null && rptIdxMeasureInfoList.size() > 0){
				for(RptIdxMeasureInfo rptIdxMeasureInfo : rptIdxMeasureInfoList){
					CommonTreeNode treeNode = new CommonTreeNode();
					treeNode.setId(idxNo + "." + rptIdxMeasureInfo.getMeasureNo());
					treeNode.setUpId(idxNo);
					treeNode.setText(rptIdxMeasureInfo.getMeasureNm());
					treeNode.setParams(menu);
					treeNode.setData(rptIdxMeasureInfo);
					treeNodes.add(treeNode);
				}
			}
		}
		return treeNodes;
	}
	
	//获取指标名称
	public String queryIdxname(String idxNo){
		String idxNm = "";
		if(idxNo != null && idxNo.length()>0){
			String jql = "select idx from RptIdxInfo idx where idx.isCabin = ?0 and idx.indexSts = ?1 and idx.endDate = ?2 and idx.isRptIndex = ?3 and idx.id.indexNo = ?4";
			RptIdxInfo RptIdxInfo = this.baseDAO.findUniqueWithIndexParam(jql,"1","Y","29991231","N",idxNo);
			if(RptIdxInfo != null){
				idxNm = RptIdxInfo.getIndexNm();
			}
		}
		return idxNm;
	}
	
	/**
	 * 获取指标上期值
	 * @param dataDate
	 * @param orgNo
	 * @param rptIndexNo
	 * @param dataUnit
	 * @param dataPrecision
	 * @return
	 */
	public Map<String, Object> getIdxLastData(String dataDate, String orgNo, String rptIndexNo, String dataUnit, String dataPrecision) {
		Map<String, Object> rptStsMap = Maps.newHashMap();
		Map<String, String> dateMap = Maps.newHashMap();
		//没有单位默认为元
		if("null".equals(dataUnit)) {
			dataUnit = GlobalConstants4plugin.DATA_UNIT_YUAN;
		}
		if(StringUtils.isNotBlank(dataDate) && StringUtils.isNotBlank(orgNo) && StringUtils.isNotBlank(rptIndexNo) && StringUtils.isNotBlank(dataUnit) && StringUtils.isNotBlank(dataPrecision)) {
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.startDate <= ?1 and idx.endDate >= ?2";
			RptIdxInfo RptIdxInfo = this.baseDAO.findUniqueWithIndexParam(jql, rptIndexNo, dataDate, dataDate);
			dateMap.put("本期", dataDate);
			if(RptIdxInfo != null){
				String calcCycle = RptIdxInfo.getCalcCycle();
				SimpleDateFormat matter=new SimpleDateFormat("yyyyMMdd");
				Date today = new Date();
				try {
					today = matter.parse(dataDate);//获取查询日期
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Map<String, String> lastFlaMap = new HashMap<String, String>(); 
				lastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_DAY, "Yesterday('$1','Y')");//日
				lastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_MONTH, "LastMonth('$1','Y')");//月
				lastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_SEASON, "LastQuarterEnd('$1','Y')");//季
				lastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_YEAR, "LastYearEnd('$1','Y')");//年
				lastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_HALF_YEAR, "HalfYearEnd('$1','Y')");//半年
				Map<String, String> lastLastFlaMap = new HashMap<String, String>(); 
				lastLastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_DAY, "Yesterday(Yesterday('$1','Y'))");//日
				lastLastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_MONTH, "LastMonth(LastMonth('$1','Y'))");//月
				lastLastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_SEASON, "LastQuarterEnd(LastQuarterEnd('$1','Y'))");//季
				lastLastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_YEAR, "LastYearEnd(LastYearEnd('$1','Y'))");//年
				lastLastFlaMap.put(GlobalConstants4plugin.CALC_CYCLE_HALF_YEAR, "HalfYearEnd(HalfYearEnd('$1','Y'))");//半年
				List<String> idxNoList = new ArrayList<String>();
				idxNoList.add(rptIndexNo);
				Map<String,String> formulaMap = new HashMap<String, String>();
				String lastYear = matter.format(DateUtils.getDateOfLastYear(today));//获取去年同期值
				dateMap.put("去年同期", lastYear);
				RptIdxInfo = this.baseDAO.findUniqueWithIndexParam(jql, rptIndexNo, lastYear, lastYear);//查找去年同期指标有没有生效
				if(null != RptIdxInfo) {
					formulaMap.put("lastYear", "LastYear('$1','Y')");//去年同期
					formulaMap.put("thisYearFirst", "ThisYearFirst('$1','Y')");//年初
				}else{
					String thisYearFirst = matter.format(DateUtils.getFristDateOfCurrYear(today));//获取年初值
					dateMap.put("年初", thisYearFirst);
					RptIdxInfo = this.baseDAO.findUniqueWithIndexParam(jql, rptIndexNo, thisYearFirst, thisYearFirst);//查找年初指标有没有生效
					if(null != RptIdxInfo) {
						formulaMap.put("thisYearFirst", "ThisYearFirst('$1','Y')");//年初
					}
				}
				String lastDay = matter.format(DateUtils.getLastDayByCalcCycle(today, calcCycle));//获取上期日期
				dateMap.put("上期", lastDay);
				RptIdxInfo = this.baseDAO.findUniqueWithIndexParam(jql, rptIndexNo, lastDay, lastDay);//查找上期指标有没有生效
				if(null != RptIdxInfo) {
					formulaMap.put("lastDate", lastFlaMap.get(calcCycle));//上期
					String lastLastDay = matter.format(DateUtils.getLastDayByCalcCycle(DateUtils.getLastDayByCalcCycle(today, calcCycle), calcCycle));//获取上上期日期
					dateMap.put("上上期", lastLastDay);
					RptIdxInfo = this.baseDAO.findUniqueWithIndexParam(jql, rptIndexNo, lastLastDay, lastLastDay);//查找上上期指标有没有生效
					if(null != RptIdxInfo) {
						formulaMap.put("lastLastDate", lastLastFlaMap.get(calcCycle));//上上期
					}
				}
				Map<String,List<String>> dimMap  = new HashMap<String, List<String>>();
				List<String> orgNoList = new ArrayList<String>();
				orgNoList.add(orgNo);
				dimMap.put(GlobalConstants4plugin.DIM_TYPE_ORG_NAME, orgNoList);
				List<String> dateList = new ArrayList<String>();
				dateList.add(dataDate);
				dimMap.put(GlobalConstants4plugin.DIM_TYPE_DATE_NAME, dateList);
				Map<String,Map<String,String>>  data = new HashMap<String,Map<String,String>>();
				if(!"null".equals(dataPrecision)) {
					BigDecimal dataAccuracy = new BigDecimal(dataPrecision);
					data= FetchDataUtils.FetchDataMap(idxNoList, "03", true, null, null, dimMap, formulaMap, DATE, dataUnit, dataAccuracy, false);
				}else {
					data= FetchDataUtils.FetchDataMap(idxNoList, "03", true, null, null, dimMap, formulaMap, DATE, dataUnit, null, false);
				}
				if(null != data.get(dataDate)) {
					rptStsMap.put("lastYear", data.get(dataDate).get(rptIndexNo + "lastYear") == null ? "0" : data.get(dataDate).get(rptIndexNo + "lastYear"));
					rptStsMap.put("thisYearFirst", data.get(dataDate).get(rptIndexNo + "thisYearFirst") == null ? "0" : data.get(dataDate).get(rptIndexNo + "thisYearFirst"));
					rptStsMap.put("lastLastDate", data.get(dataDate).get(rptIndexNo + "lastLastDate") == null ? "0" : data.get(dataDate).get(rptIndexNo + "lastLastDate"));
					rptStsMap.put("lastDate", data.get(dataDate).get(rptIndexNo + "lastDate") == null ? "0" : data.get(dataDate).get(rptIndexNo + "lastDate"));
					rptStsMap.put("nowDate", data.get(dataDate).get(rptIndexNo) == null ? "0" : data.get(dataDate).get(rptIndexNo));
				}
			}
		}
		rptStsMap.put("dateMap", dateMap);
		return rptStsMap;
	}
	
	/**
	 * 机构数据下钻
	 * @param dataDate
	 * @param orgNo
	 * @param indexNo
	 * @param dataUnit
	 * @param dataPrecision
	 * @param orgType
	 * @param searchArgs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getOrgDrillData(String dataDate, String orgNo, String indexNo, String dataUnit, String dataPrecision, String orgType, String parentOrgNm) {
		Map<String, Object> rptStsMap = Maps.newHashMap();
		if(StringUtils.isNotBlank(parentOrgNm)) {
			String jql = "select org from RptOrgInfo org where org.orgNm = ?0 and org.id.orgType = ?1";
			RptOrgInfo rptOrgInfo = this.baseDAO.findUniqueWithIndexParam(jql, parentOrgNm, orgType);
			if(null != rptOrgInfo) {
				orgNo = rptOrgInfo.getId().getOrgNo();
			}else {
				return null;
			}
		}
		Map<String,Object> orgRelevant = this.obtainOrg(null, orgNo, orgType);//机构相关
		List<String> chOrgNos = new ArrayList<String>();
		chOrgNos = (List<String>) orgRelevant.get("orgNoList");
		Map<String,Object> orgNmMap = (Map<String, Object>) orgRelevant.get("orgNmMap");
		List<String> idxNoList = Arrays.asList(indexNo);
		if((null == chOrgNos) || (chOrgNos.size() == 0)) {
			chOrgNos = Arrays.asList(orgNo);
			orgNmMap.put(orgNo, parentOrgNm);
			rptStsMap.put("error", "error");
		}
		Map<String,List<String>> dimMap  = new HashMap<String, List<String>>();
		dimMap.put(GlobalConstants4plugin.DIM_TYPE_ORG_NAME, chOrgNos);
		List<String> dateList = Arrays.asList(dataDate);
		dimMap.put(GlobalConstants4plugin.DIM_TYPE_DATE_NAME, dateList);
		BigDecimal dataAccuracy = new BigDecimal(dataPrecision);
		Map<String,Map<String,String>> data= FetchDataUtils.FetchDataMap(idxNoList, "03", true, null, null, dimMap, null, ORG, dataUnit, dataAccuracy, true, null, false);//引擎取数
		Set<String> orgKeylist = orgNmMap.keySet();       
		if(orgKeylist != null && orgKeylist.size() > 0){
			List<Object> seriesPieData = new ArrayList<Object>();//饼图数据
			List<Object> xAxisData = new ArrayList<Object>();//X轴数据
			List<Object> seriesData = new ArrayList<Object>();//Y轴数据
			for(String idxNo : idxNoList){
				for(String orgKey : orgKeylist){
					Map<String,Object> treeData = new HashMap<String, Object>();
					Map<String,String> datamap =  data.get(orgKey);
					treeData.put("name", orgNmMap.get(orgKey));
					if(datamap != null){
						treeData.put("value", datamap.get(idxNo));
						if ((int)Float.parseFloat(datamap.get(idxNo)) !=0) {
							seriesData.add(datamap.get(idxNo));
						}
					}else {
						treeData.put("value", "0");
//						seriesData.add("0");
					}
					if ((int)Float.parseFloat((String)treeData.get("value")) !=0) {
						xAxisData.add(orgNmMap.get(orgKey));
						seriesPieData.add(treeData);
					}
				}
			}
			rptStsMap.put("pieData", seriesPieData);
			rptStsMap.put("xAxisData", xAxisData);
			rptStsMap.put("seriesData", seriesData);
			rptStsMap.put("orgNo", orgNo);
		}
		return rptStsMap;
	}
	
	/**
	 * 指标结构下钻
	 * @param dataDate
	 * @param orgNo
	 * @param indexNo
	 * @param dataUnit
	 * @param dataPrecision
	 * @param orgType
	 * @param parentOrgNm
	 * @return
	 */
	public Map<String, Object> getIdxStructureData(String dataDate, String orgNo, String indexNo, String dataUnit, String dataPrecision, String indexVerId) {
		Map<String, Object> rptStsMap = Maps.newHashMap();
		if(StringUtils.isNotBlank(indexNo)) {
			String jql = "select idx from RptIdxSrcRelInfo idx where idx.id.indexNo = ?0 and idx.id.indexVerId = ?1";
			long verId = Long.parseLong(indexVerId);
			List<RptIdxSrcRelInfo> srcIdxList = this.baseDAO.findWithIndexParam(jql, indexNo, verId);
			List<String> idxNoList = new ArrayList<String>();
			Map<String,List<String>> dimMap  = new HashMap<String, List<String>>();
			for(RptIdxSrcRelInfo srcIdxInfo: srcIdxList) {
				idxNoList.add(srcIdxInfo.getId().getSrcIndexNo());
			}
			if(srcIdxList.size() == 0) {//没有来源指标，可能是空指标，那就展示指标自己
				idxNoList.add(indexNo);
			}
			jql = "select idx from RptIdxInfo idx where idx.id.indexNo in ?0 and idx.endDate = ?1";
			List<RptIdxInfo> srcIdxInfoList = this.baseDAO.findWithIndexParam(jql, idxNoList, "29991231");
			List<String> orgNoList = Arrays.asList(orgNo);
			List<String> dateList = Arrays.asList(dataDate);
			dimMap.put(GlobalConstants4plugin.DIM_TYPE_ORG_NAME, orgNoList);
			dimMap.put(GlobalConstants4plugin.DIM_TYPE_DATE_NAME, dateList);
			BigDecimal dataAccuracy = new BigDecimal(dataPrecision);
			Map<String,Map<String,String>>  data= FetchDataUtils.FetchDataMap(idxNoList, "03", true, null, null, dimMap, null, DATE, dataUnit, dataAccuracy, false);
			Map<String,String> datamap =  data.get(dataDate);
			List<Object> seriesPieData = new ArrayList<Object>();//饼图数据
			List<Object> xAxisData = new ArrayList<Object>();//X轴数据
			for(int i = 0; i < srcIdxInfoList.size(); i++) {
				RptIdxInfo idxInfo = srcIdxInfoList.get(i);
				Map<String,Object> treeData = new HashMap<String, Object>();
				treeData.put("name", idxInfo.getIndexNm());
				treeData.put("idxNo", idxInfo.getId().getIndexNo());
				treeData.put("verId", idxInfo.getId().getIndexVerId());
				xAxisData.add(idxInfo.getIndexNm());
				if(null != datamap) {
					treeData.put("value", datamap.get(idxInfo.getId().getIndexNo()));
				}else {
					treeData.put("value", "0");
				}
				seriesPieData.add(treeData);
			}
			rptStsMap.put("xAxisData", xAxisData);
			rptStsMap.put("pieData", seriesPieData);
			
		}
		return rptStsMap;
	}
	
	/**
	 * 指标明细下钻
	 * @param dataDate
	 * @param orgNo
	 * @param indexNo
	 * @param dataUnit
	 * @param dataPrecision
	 * @param indexVerId
	 * @return
	 */
	public Map<String, Object> getIdxDetailed(String dataDate, String orgNo, String indexNo, String dataUnit, String dataPrecision, String indexVerId) {
		Map<String, Object> rptStsMap = Maps.newHashMap();
		if(StringUtils.isNotBlank(indexNo)) {
			String jql = "select idx from RptIdxDetailDrill idx where idx.id.indexNo = ?0 and idx.id.indexVerId = ?1";
			long verId = Long.parseLong(indexVerId);
			RptIdxDetailDrill rptIdxDetailDrill = this.baseDAO.findUniqueWithIndexParam(jql, indexNo, verId);
			if(null != rptIdxDetailDrill) {
				String drillSql = rptIdxDetailDrill.getDrillSql();
				if(StringUtils.isNotBlank(drillSql)) {
					drillSql = StringUtils.replace(drillSql, "{indexNo}", indexNo);//替换指标编号
					drillSql = StringUtils.replace(drillSql, "{orgNo}", orgNo);//替换机构编号
					drillSql = StringUtils.replace(drillSql, "{dataDate}", dataDate);//替换日期
					List<Map<String, Object>> list = rptTmpDAO.queryDrillSql(drillSql);
					if(null != list && list.size() > 0) {
						List<Object> columns = new ArrayList<Object>();//列头
						Map<String, Object> detail = list.get(0);
						for(String key : detail.keySet()) {
							columns.add(key);
						}
						rptStsMap.put("columns", columns);
						rptStsMap.put("drillData", list);
					}
				}
			}
		}
		return rptStsMap;
	}
}
