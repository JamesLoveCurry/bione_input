package com.yusys.bione.plugin.idxanacfg.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsThemeRel;
import com.yusys.bione.plugin.rptbank.entity.RptIdxThemeInfo;
/**
 * <pre>
 * Title:指标分析图表主题关系BS
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
public class RptAnaChartsThemeRelBS extends BaseBS<RptAnaChartsThemeRel>{
	
	@Autowired
	private RptAnaThemeBS rptanathemeBS;
	/**
	 * 保存图表主题关系
	 * @param chartId
	 * @param themeId
	 * @return
	 */
	@Transactional(readOnly = false)
	public RptAnaChartsThemeRel saveTheRel(String chartId,String themeId){
		RptAnaChartsThemeRel chartsTheme = new RptAnaChartsThemeRel();
		chartsTheme.setChartId(chartId);
		chartsTheme.setThemeId(themeId);
		return this.saveOrUpdateEntity(chartsTheme);
	}

	/**
	 * 获取图表关联的主题
	 * @param chartId
	 * @return
	 */
	public Map<String, String> getTheme(String chartId){
		Map<String, String> returnMap = new HashMap<String, String>();
		if(StringUtils.isNotBlank(chartId)){
			RptAnaChartsThemeRel chartTheme = this.getEntityById(chartId);
			if(chartTheme != null){
				returnMap.put("themeId", chartTheme.getThemeId());
				RptIdxThemeInfo themeInfo = this.rptanathemeBS.getEntityById(chartTheme.getThemeId());
				if(themeInfo != null){
					returnMap.put("themeNm", themeInfo.getThemeNm());
				}
			}
		}
		return returnMap;
	}
	
	/**
	 * 复制图表
	 * @param tempId
	 * @param newTmpId
	 */
	@Transactional(readOnly = false)
	public void copyCharts(String charsId, String newCharsId){
		RptAnaChartsThemeRel oldCharts = this.getEntityById(charsId);
		if(oldCharts != null){
			RptAnaChartsThemeRel newCharts = new RptAnaChartsThemeRel();
			newCharts.setChartId(newCharsId);
			newCharts.setThemeId(oldCharts.getThemeId());
			this.saveEntity(newCharts);
		}
	}
	
}
