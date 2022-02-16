package com.yusys.bione.plugin.idxanacfg.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsDateRel;

/**
 * <pre>
 * Title:指标分析图表日期关系BS
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
public class RptAnaChartsDateRelBS extends BaseBS<RptAnaChartsDateRel>{

	/**
	 * 获取图表显示的时间
	 * @param chartId
	 * @return
	 */
	public Map<String, String> getDataRel(String chartId){
		Map<String, String> dateRelMap = new HashMap<String, String>();
		RptAnaChartsDateRel dateInfo = this.getEntityById(chartId);
		if(dateInfo != null){
			dateRelMap.put("dateFreq", dateInfo.getDateFreq());
			dateRelMap.put("dataType", dateInfo.getShowType());
			dateRelMap.put("startDate", dateInfo.getStartDate());
			dateRelMap.put("endDate", dateInfo.getEndDate());
		}
		return dateRelMap;
	}
	
	/**
	 * 保存图表要显示的日期
	 * @param chartId
	 * @param dateFreq
	 * @param dataType
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public RptAnaChartsDateRel saveDateRel(String chartId,String dateFreq,String dataType,String startDate,String endDate){
		RptAnaChartsDateRel chartsDate = new RptAnaChartsDateRel();
		chartsDate.setChartId(chartId);
		chartsDate.setDateFreq(dateFreq);;
		chartsDate.setShowType(dataType);
		chartsDate.setStartDate(startDate);
		chartsDate.setEndDate(endDate);
		return this.saveOrUpdateEntity(chartsDate);
	}
	
	/**
	 * 复制图表选择的日期
	 * @param charsId
	 * @param newCharsId
	 */
	@Transactional(readOnly = false)
	public void copyCharts(String charsId, String newCharsId){
		RptAnaChartsDateRel oldCharts = this.getEntityById(charsId);
		if(oldCharts != null){
			RptAnaChartsDateRel newCharts = new RptAnaChartsDateRel();
			newCharts.setChartId(newCharsId);
			newCharts.setDateFreq(oldCharts.getDateFreq());
			newCharts.setEndDate(oldCharts.getEndDate());
			newCharts.setStartDate(oldCharts.getStartDate());
			newCharts.setRemark(oldCharts.getRemark());
			newCharts.setShowType(oldCharts.getShowType());
			this.saveEntity(newCharts);
		}
	}
}
