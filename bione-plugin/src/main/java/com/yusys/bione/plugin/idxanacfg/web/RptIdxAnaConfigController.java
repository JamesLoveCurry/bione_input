package com.yusys.bione.plugin.idxanacfg.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsInfo;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaTmpFormulaRel;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaTmpInfo;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsDateRelBS;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsFormulaRelBS;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsInfoBS;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsOrgRelBS;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsThemeRelBS;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaTmpFormulaRelBS;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaTmpInfoBS;
import com.yusys.bione.plugin.idxanacfg.service.RptIdxAnaConfigBS;

/**
 * <pre>
 * Title:指标分析后台配置
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
@Controller
@RequestMapping("/cabin/analysis/config")
public class RptIdxAnaConfigController extends BaseController {
	
	@Autowired
	private RptIdxAnaConfigBS idxAnaCfgBS;
	
	@Autowired
	private RptAnaTmpInfoBS anaTmpInfoBS;
	
	@Autowired
	private RptAnaChartsInfoBS anaChartsInfoBS;
	
	@Autowired
	private RptAnaTmpFormulaRelBS anaTmpFormulaRelBS;
	
	@Autowired
	private RptAnaChartsFormulaRelBS anaChaFormulaRelBS;
	
	@Autowired
	private RptAnaChartsOrgRelBS anaChartOrgRelBS;
	
	@Autowired
	private RptAnaChartsDateRelBS anaChartDateRelBS;
	
	@Autowired
	private RptAnaChartsThemeRelBS anaChartThemeRelBS;
	/**
	 * 跳转指标分析后台配置首页
	 */	
	@RequestMapping
	public String menuIndex() {
		return "/plugin/idxanacfg/idx-analysis-config-index";
	}
	
	/**
	 * 遍历资源树
	 */	
	@RequestMapping(value = "/getTmpTree")
	@ResponseBody
	public Map<String, Object> getTmpTree() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		resultList.addAll(anaTmpInfoBS.getTmpTree());
		resultMap.put("nodes", resultList);
		return resultMap;
	}
	
	/**
	 * 驾驶舱资源管理新建资源页面
	 * 
	 * */
	@RequestMapping(value = "/tmpBasic")
	public ModelAndView tmpBasic(String templateId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(templateId)){
			RptAnaTmpInfo tmpInfo = anaTmpInfoBS.getEntityById(templateId);
			if(tmpInfo != null){
				resultMap.put("templateId", StringUtils2.javaScriptEncode(templateId));
				resultMap.put("templateNm", StringUtils2.javaScriptEncode(tmpInfo.getTemplateNm()));
				resultMap.put("templateFreq", StringUtils2.javaScriptEncode(tmpInfo.getTemplateFreq()));
				resultMap.put("remark", StringUtils2.javaScriptEncode(tmpInfo.getRemark()));
			}
		}
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-tmp-basic",resultMap);
	}
	
	/**
	 * 设置默认模板
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/defaultTemp")
	@ResponseBody
	public Map<String,Object> defaultTemp(String templateId) {
		Map<String,Object> returnMap = new HashMap<String, Object>();
		returnMap.put("rptAnaTmpInfo", (Object)anaTmpInfoBS.defaultTemp(templateId));
		return returnMap;
	}
	
	/**
	 * 删除模板
	 * @param tempId
	 */
	@RequestMapping(value = "/deleteTmp")
	@ResponseBody
	public Map<String, Object> deleteTmp(String templateId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(templateId)){
			this.anaTmpInfoBS.removeEntityById(templateId);
			List<RptAnaTmpFormulaRel> anaTmpMeaRelList = this.anaTmpFormulaRelBS.getEntityListByProperty(RptAnaTmpFormulaRel.class, "templateId", templateId);
			if(anaTmpMeaRelList.size()>0){
				this.anaTmpFormulaRelBS.removeEntityByProperty("templateId",templateId);
			}
			List<RptAnaChartsInfo> anaChartsInfoList =  anaChartsInfoBS.getEntityListByProperty(RptAnaChartsInfo.class, "templateId", templateId);
			for(RptAnaChartsInfo anaChartsInfo : anaChartsInfoList){
				this.idxAnaCfgBS.deleteCha(anaChartsInfo.getChartId());
			}
		}
		return resultMap;
	}
	
	/**
	 * 复制模板
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/copyTemp")
	@ResponseBody
	public Map<String, Object> copyTemp(String templateId){
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String newTmpId = this.anaTmpInfoBS.copyTemp(templateId);
		this.anaTmpFormulaRelBS.copyTemplateconfig(templateId, newTmpId);
		this.anaChartsInfoBS.copyCharts(templateId, newTmpId);
		return returnMap;
	}
	
	/**
	 * 查询模板下的全部图表
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/queryCharts")
	@ResponseBody
	public Map<String, Object> queryCharts(String templateId) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("chartsList", anaChartsInfoBS.queryCharts(templateId));
		return result;
	}
	
	/**
	 * 保存模板基本信息
	 */	
	@RequestMapping(value = "/saveTmpBasic")
	@ResponseBody
	public RptAnaTmpInfo saveTmpBasic(String templateId,String templateNm,String templateFreq,String remark){
		RptAnaTmpInfo tmpInfo = new RptAnaTmpInfo();
		if(StringUtils.isNotBlank(templateId)){
			tmpInfo.setTemplateId(templateId);
			tmpInfo = this.anaTmpInfoBS.getEntityById(templateId); 
		}else{
			tmpInfo.setTemplateId(RandomUtils.uuid2());
			tmpInfo.setIsDefault("N");
		}
		tmpInfo.setTemplateNm(templateNm);
		tmpInfo.setTemplateFreq(templateFreq);
		tmpInfo.setRemark(remark);
		return this.anaTmpInfoBS.saveOrUpdateEntity(tmpInfo);
	}
	
	/**
	 * 跳转详细配置页
	 */	
	@RequestMapping(value = "/tmpDetail")
	public ModelAndView tmpDetail(String templateId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(templateId)){
			RptAnaTmpInfo tmpInfo = anaTmpInfoBS.getEntityById(RptAnaTmpInfo.class,templateId);
			if(tmpInfo!=null){
				returnMap.put("templateId", StringUtils2.javaScriptEncode(tmpInfo.getTemplateId()));
				returnMap.put("templateNm", StringUtils2.javaScriptEncode(tmpInfo.getTemplateNm()));
				returnMap.put("templateFreq", StringUtils2.javaScriptEncode(tmpInfo.getTemplateFreq()));
				returnMap.put("dataFormat", StringUtils2.javaScriptEncode(tmpInfo.getDataFormat()));
				returnMap.put("dataUnit", StringUtils2.javaScriptEncode(tmpInfo.getDataUnit()));
				returnMap.put("dataPrecision", tmpInfo.getDataPrecision());
				Map<String, String> dataMap = this.anaTmpFormulaRelBS.getTmpFormula(templateId);
				for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
					Entry<String, String> entry = it.next();
					returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
				}
			}
		}
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-tmp-detail",returnMap);
	}
	
	/**
	 * 根据模板频度获取公式	
	 * @param templateFreq
	 * @return
	 */
	@RequestMapping(value = "/getInitFormula")
	@ResponseBody
	public Map<String, Object> getInitFormula(String templateFreq) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.putAll(this.idxAnaCfgBS.getInitFormula(templateFreq));
		return resultMap;
	}
	
	/**
	 * 保存模板详细配置信息
	 * @param templateId
	 * @param dataFormat
	 * @param dataUnit
	 * @param dataPrecision
	 * @param selectFa
	 * @return
	 */
	@RequestMapping(value = "/saveTmpDetail")
	@ResponseBody
	public RptAnaTmpInfo saveTmpDetail(String templateId,String dataFormat,String dataUnit,String dataPrecision,String selectFa){
		this.anaTmpFormulaRelBS.saveTmpFaRel(templateId, selectFa);
		return this.anaTmpInfoBS.saveTmpDetail(templateId, dataFormat, dataUnit, dataPrecision);
	}
	
	/**
	 * 跳转图表信息页面
	 * @param templateId
	 * @param chartId
	 * @return
	 */
	@RequestMapping(value = "/chartCfg")
	public ModelAndView chartCg(String templateId,String chartId) {
		ModelMap menu = new ModelMap();
		menu.put("templateId", StringUtils2.javaScriptEncode(templateId));
		if(StringUtils.isNotBlank(chartId)){
			RptAnaChartsInfo anaChartsInfo = anaChartsInfoBS.getEntityById(chartId);
			if(anaChartsInfo != null){
				menu.put("chartId", StringUtils2.javaScriptEncode(chartId));
				menu.put("chartNm", StringUtils2.javaScriptEncode(anaChartsInfo.getChartNm()));
				menu.put("chartType", StringUtils2.javaScriptEncode(anaChartsInfo.getChartType()));
				menu.put("showType", StringUtils2.javaScriptEncode(anaChartsInfo.getShowType()));
				menu.put("orderNum", anaChartsInfo.getOrderNum());
				menu.put("remark", StringUtils2.javaScriptEncode(anaChartsInfo.getRemark()));
				menu.put("chartColor", StringUtils2.javaScriptEncode(anaChartsInfo.getChartColor()));
			}
		}
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-charts-config",menu);
	}
	
	/**
	 * 保存图表基本信息
	 */	
	@RequestMapping(value = "/saveChartInfo")
	@ResponseBody
	public Map<String, Object> saveChartInfo(String templateId,String chartId,String chartNm,String chartType,String showType,String orderNum,String remark,String chartColor){
		Map<String, Object> result = new HashMap<String, Object>();
		RptAnaChartsInfo chartsInfo = new RptAnaChartsInfo();
		if(StringUtils.isNotBlank(chartId)){
			chartsInfo = anaChartsInfoBS.getEntityById(chartId);
		}else{
			chartsInfo.setChartId(RandomUtils.uuid2());
			chartsInfo.setTemplateId(templateId);
		}
		chartsInfo.setChartType(chartType);
		chartsInfo.setShowType(showType);
		chartsInfo.setChartNm(chartNm);
		BigDecimal Precision = new BigDecimal(orderNum);
		chartsInfo.setOrderNum(Precision);
		chartsInfo.setRemark(remark);
		chartsInfo.setChartColor(chartColor);
		result.put("chartsInfo", this.anaChartsInfoBS.saveOrUpdateEntity(chartsInfo));
		return result;
	}
	
	/**
	 * 删除图表
	 * @param chartId
	 * @return
	 */
	@RequestMapping(value = "/deleteCha")
	@ResponseBody
	public Map<String, Object> deleteCha(String chartId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.putAll(this.idxAnaCfgBS.deleteCha(chartId));
		return resultMap;
	}
	
	/**
	 * 跳转指标概要配置页面
	 * @param chartId
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/outlineCht")
	public ModelAndView outlineCht(String chartId,String templateId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(chartId) && StringUtils.isNotBlank(templateId)){
			Map<String, String> dataMap = anaChartsInfoBS.getChartInfo(chartId,templateId);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
		}
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-charts-outline",returnMap);
	}
	
	/**
	 * 保存指标概要配置
	 * @param chartId
	 * @param showType
	 * @param selectFa
	 * @param textCfg
	 * @param chartCfg
	 * @return
	 */
	@RequestMapping(value = "/saveOutline")
	@ResponseBody
	public Map<String, Object> saveOutline(String chartId,String showType,String selectFa,String textCfg,String chartCfg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("charts", this.anaChartsInfoBS.saveCharts(chartId, showType, textCfg,chartCfg));
		this.anaChaFormulaRelBS.saveChaFa(chartId, selectFa);
		return resultMap;
	}
	
	/**
	 * 跳转机构信息配置页面
	 * @param chartId
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/orgCfg")
	public ModelAndView orgCfg(String chartId,String templateId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(chartId) && StringUtils.isNotBlank(templateId)){
			Map<String, String> dataMap = anaChartsInfoBS.getChartInfo(chartId,templateId);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
			dataMap = anaChartOrgRelBS.getOrgRel(chartId);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
			dataMap = idxAnaCfgBS.getFaByType(GlobalConstants4plugin.SPECIAL_FA);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
		}
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-charts-org",returnMap);
	}
	
	/**
	 * 保存机构信息配置
	 * @param chartId
	 * @param showType
	 * @param selectFa
	 * @param textCfg
	 * @param chartCfg
	 * @param orgNo
	 * @return
	 */
	@RequestMapping(value = "/saveOrgCfg")
	@ResponseBody
	public Map<String, Object> saveOrgCfg(String chartId,String showType,String selectFa,String textCfg,String chartCfg,String orgNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("charts", this.anaChartsInfoBS.saveCharts(chartId, showType, textCfg, chartCfg));
		this.anaChaFormulaRelBS.saveChaFa(chartId, selectFa);
		this.anaChartOrgRelBS.saveOrgRel(chartId, orgNo);
		return resultMap;
	}
	
	/**
	 * 跳转趋势分析配置页面
	 * @param chartId
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/trend")
	public ModelAndView trend(String chartId,String templateId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(chartId) && StringUtils.isNotBlank(templateId)){
			Map<String, String> dataMap = anaChartsInfoBS.getChartInfo(chartId,templateId);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
			dataMap = anaChartDateRelBS.getDataRel(chartId);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
		}
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-charts-trend", returnMap);
	}
	
	/**
	 * 保存趋势分析配置
	 * @param chartId
	 * @param showType
	 * @param selectFa
	 * @param textCfg
	 * @param chartCfg
	 * @param dateFreq
	 * @param dataType
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/saveTrend")
	@ResponseBody
	public Map<String, Object> saveTrend(String chartId,String showType,String selectFa,String textCfg,String chartCfg,String dateFreq,String dataType,String startDate,String endDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("charts", this.anaChartsInfoBS.saveCharts(chartId, showType, textCfg, chartCfg));
		this.anaChaFormulaRelBS.saveChaFa(chartId, selectFa);
		this.anaChartDateRelBS.saveDateRel(chartId, dateFreq, dataType, startDate, endDate);
		return resultMap;
	}
	
	/**
	 * 跳转结构解析配置页面
	 * @param chartId
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/structure")
	public ModelAndView structure(String chartId,String templateId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(chartId) && StringUtils.isNotBlank(templateId)){
			Map<String, String> dataMap = anaChartsInfoBS.getChartInfo(chartId,templateId);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
		}
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-charts-structure",returnMap);
	}
	
	/**
	 * 保存结构解析图
	 * @param chartId
	 * @param showType
	 * @param selectFa
	 * @return
	 */
	@RequestMapping(value = "/saveStructure")
	@ResponseBody
	public Map<String, Object> saveStructure(String chartId,String showType,String selectFa) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("charts", this.anaChartsInfoBS.saveCharts(chartId, showType, null, null));
		this.anaChaFormulaRelBS.saveChaFa(chartId, selectFa);
		return resultMap;
	}
	
	/**
	 * 跳转关系解析页面
	 * @param chartId
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/relationship")
	public ModelAndView relationship(String chartId,String templateId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(chartId) && StringUtils.isNotBlank(templateId)){
			Map<String, String> dataMap = anaChartsInfoBS.getChartInfo(chartId,templateId);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
			dataMap = this.anaChartThemeRelBS.getTheme(chartId);
			for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				returnMap.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
			}
		}
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-charts-relationship",returnMap);
	}
	
	/**
	 * 保存关系解析配置
	 * @param chartId
	 * @param showType
	 * @param selectFa
	 * @param themeId
	 * @return
	 */
	@RequestMapping(value = "/saveRela")
	@ResponseBody
	public Map<String, Object> saveRela(String chartId,String showType,String selectFa,String themeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("charts", this.anaChartsInfoBS.saveCharts(chartId, showType, null, null));
		this.anaChaFormulaRelBS.saveChaFa(chartId, selectFa);
		this.anaChartThemeRelBS.saveTheRel(chartId, themeId);
		return resultMap;
	}
}
