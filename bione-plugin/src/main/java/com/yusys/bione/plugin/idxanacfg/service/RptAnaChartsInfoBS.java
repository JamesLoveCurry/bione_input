package com.yusys.bione.plugin.idxanacfg.service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsInfo;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaTmpInfo;
/**
 * <pre>
 * Title:指标分析
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
public class RptAnaChartsInfoBS extends BaseBS<RptAnaChartsInfo>{

	@Autowired
	private RptAnaChartsFormulaRelBS anachartsfarelBS;
	
	@Autowired
	private RptAnaChartsOrgRelBS anachartsorgrelBS;
	
	@Autowired
	private RptAnaChartsDateRelBS anachartsdaterelBS;
	
	@Autowired
	private RptAnaChartsThemeRelBS anachartsthemerelBS;
	
	@Autowired
	private RptAnaTmpInfoBS anatmpinfoBS;
	
	/**
	 * 查询图表顺序是否重复
	 * 
	 */
	public Boolean getOrder(String orderNum,String tempId){
		if(orderNum != null && orderNum.length() > 0){
			BigDecimal Precision = new BigDecimal(orderNum);
			List<RptAnaChartsInfo> AnaChartsInfoList = this.baseDAO.findWithIndexParam("select rpt from RptAnaChartsInfo rpt where rpt.templateId=(?0)",tempId);//原表已有全部模板
			for(RptAnaChartsInfo AnaChartsInfo : AnaChartsInfoList){
				if(Precision.equals(AnaChartsInfo.getOrderNum())){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 查询模板下的全部图表
	 * @param tempId
	 * @return
	 */
	public List<RptAnaChartsInfo> queryCharts(String tempId){
		List<RptAnaChartsInfo> chartsList = this.baseDAO.findWithIndexParam("select rpt from RptAnaChartsInfo rpt where rpt.templateId=(?0) order by rpt.orderNum",tempId);
		return chartsList;
	}
	
	/**
	 * 获取图表信息
	 * @param chartId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getChartInfo(String chartId,String templateId){
		Map<String, String> chartInfo = new HashMap<String, String>();
		Map<String, Object> chartCfgMap = new HashMap<String, Object>();
		RptAnaChartsInfo anaChartsInfo = this.getEntityById(chartId);
		RptAnaTmpInfo anaTmpInfo = anatmpinfoBS.getEntityById(templateId);
		if(anaChartsInfo != null){
			chartInfo.put("chartId", chartId);
			if((GlobalConstants4plugin.OUTLINE).equals(anaChartsInfo.getChartType())){
				chartInfo.put("chartType", "指标概要图");
			}else if((GlobalConstants4plugin.ORG).equals(anaChartsInfo.getChartType())){
				chartInfo.put("chartType", "机构信息图");
			}else if((GlobalConstants4plugin.TREND).equals(anaChartsInfo.getChartType())){
				chartInfo.put("chartType", "趋势分析图");
			}else if((GlobalConstants4plugin.RELATIONSHIP).equals(anaChartsInfo.getChartType())){
				chartInfo.put("chartType", "结构解析图");
			}else if((GlobalConstants4plugin.STRUCTURE).equals(anaChartsInfo.getChartType())){
				chartInfo.put("chartType", "关系解析图");
			}
			chartInfo.put("showType", anaChartsInfo.getShowType());
			chartInfo.put("textCfg", anaChartsInfo.getTextCfg());
			if(StringUtils.isNotBlank(anaChartsInfo.getChartCfg())){
				chartCfgMap = JSON.parseObject(anaChartsInfo.getChartCfg(), Map.class);
			};
			chartInfo.put("chartCfg", JSON.toJSONString(chartCfgMap));
			chartInfo.putAll(this.anachartsfarelBS.getChartFa(chartId));
		}
		if(anaTmpInfo != null){
			chartInfo.put("templateFreq", anaTmpInfo.getTemplateFreq());
		}
		return chartInfo;
	}
	
	/**
	 * 保存图表配置信息
	 * @param chartId
	 * @param showType
	 * @param textCfg
	 * @param chartCfg
	 * @return
	 */
	@Transactional(readOnly = false)
	public RptAnaChartsInfo saveCharts(String chartId,String showType,String textCfg,String chartCfg){
		RptAnaChartsInfo charts = new RptAnaChartsInfo();
		if(StringUtils.isNotBlank(chartId)){
			RptAnaChartsInfo anaChartsInfo = this.getEntityById(chartId);
			if(anaChartsInfo != null){
				anaChartsInfo.setShowType(showType);
				anaChartsInfo.setTextCfg(textCfg);
				anaChartsInfo.setChartCfg(chartCfg);
				charts = this.saveOrUpdateEntity(anaChartsInfo);
			}
		}
		return charts;
	}
	
	/**
	 * 复制图表
	 * @param tempId
	 * @param newTmpId
	 */
	@Transactional(readOnly = false)
	public void copyCharts(String tempId, String newTmpId){
		List<RptAnaChartsInfo> oldCharts = this.findEntityListByProperty("templateId", tempId);
		if(oldCharts != null && oldCharts.size() > 0){
			for(RptAnaChartsInfo rptCharts : oldCharts){
				RptAnaChartsInfo newChars = new RptAnaChartsInfo();
				String newCharsId = RandomUtils.uuid2();
				newChars.setChartId(newCharsId);
				newChars.setTemplateId(newTmpId);
				newChars.setChartCfg(rptCharts.getChartCfg());
				newChars.setChartColor(rptCharts.getChartColor());
				newChars.setChartNm(rptCharts.getChartNm());
				newChars.setChartType(rptCharts.getChartType());
				newChars.setOrderNum(rptCharts.getOrderNum());
				newChars.setRemark(rptCharts.getRemark());
				newChars.setShowType(rptCharts.getShowType());
				newChars.setTextCfg(rptCharts.getTextCfg());
				this.saveEntity(newChars);
				anachartsfarelBS.copyCharts(rptCharts.getChartId(), newCharsId);
				anachartsorgrelBS.copyCharts(rptCharts.getChartId(), newCharsId);
				anachartsdaterelBS.copyCharts(rptCharts.getChartId(), newCharsId);
				anachartsthemerelBS.copyCharts(rptCharts.getChartId(), newCharsId);
			}
		}
	}
	
}
