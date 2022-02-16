package com.yusys.bione.plugin.idxanacfg.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.plugin.base.entity.RptCabinFormulaInfo;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsFormulaRel;
/**
 * <pre>
 * Title:指标分析图表公式关系BS
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
public class RptAnaChartsFormulaRelBS extends BaseBS<RptAnaChartsFormulaRel>{

	/**
	 * 获取图表勾选的公式
	 * @param chartId
	 * @return
	 */
	public Map<String, String> getChartFa(String chartId){
		Map<String, String> formula = new HashMap<String, String>();
		Map<String, Object> orderNumMap = new HashMap<String, Object>();
		String formulas = "";
		List<RptAnaChartsFormulaRel> anaChartsMeaRellist = this.findEntityListByProperty("chartId", chartId);
		if(anaChartsMeaRellist != null && anaChartsMeaRellist.size() > 0){
			for(RptAnaChartsFormulaRel anaChartsMeaRel : anaChartsMeaRellist){
				formulas += anaChartsMeaRel.getFormulaId() + ";";
				orderNumMap.put(anaChartsMeaRel.getFormulaId(), anaChartsMeaRel.getOrderNum());
			}
		}
		formula.put("chaFormula", formulas);
		formula.put("formulaNum", JSON.toJSONString(orderNumMap));
		return formula;
	}
	
	/**
	 * 保存具体图形勾选的公式
	 * @param chartId
	 * @param selectFa
	 */
	@Transactional(readOnly = false)
	public void saveChaFa(String chartId,String selectFa){
		if(StringUtils.isNotBlank(chartId)){
			this.removeEntityByProperty("chartId", chartId);
		}
		if(StringUtils.isNotBlank(selectFa)){
			String[] selectFas = StringUtils.split(selectFa, ';');
			if(selectFas != null && selectFas.length > 0){
				for (String formula : selectFas) {
					if(formula != null && formula.length() > 0){
						String[] formulas = StringUtils.split(formula, ':');
						RptCabinFormulaInfo forInfo = getEntityById(RptCabinFormulaInfo.class,formulas[0]);
						if(forInfo!=null){
							RptAnaChartsFormulaRel chaForRel = new RptAnaChartsFormulaRel();
							chaForRel.setRelId(RandomUtils.uuid2());
							chaForRel.setChartId(chartId);
							chaForRel.setFormulaId(forInfo.getFormulaId());
							chaForRel.setFormulaNm(forInfo.getFormulaNm());
							chaForRel.setFormulaContent(forInfo.getFormulaContent());
							chaForRel.setFormulaType(forInfo.getFormulaType());
							if(formulas.length == 2){
								BigDecimal num = new BigDecimal(formulas[1]);
								chaForRel.setOrderNum(num);
							}
							this.saveOrUpdateEntity(chaForRel);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 复制图表选择的公式
	 * @param chartId
	 * @param newCharsId
	 */
	@Transactional(readOnly = false)
	public void copyCharts(String chartId, String newCharsId){
		List<RptAnaChartsFormulaRel> oldChartsMea = this.findEntityListByProperty("chartId", chartId);
		if(oldChartsMea != null && oldChartsMea.size() > 0){
			for(RptAnaChartsFormulaRel chartMea : oldChartsMea){
				RptAnaChartsFormulaRel newChartFor = new RptAnaChartsFormulaRel();
				newChartFor.setChartId(newCharsId);
				newChartFor.setRelId(RandomUtils.uuid2());
				newChartFor.setFormulaContent(chartMea.getFormulaContent());
				newChartFor.setFormulaId(chartMea.getFormulaId());
				newChartFor.setFormulaNm(chartMea.getFormulaNm());
				newChartFor.setFormulaType(chartMea.getFormulaType());
				newChartFor.setOrderNum(chartMea.getOrderNum());
				this.saveEntity(newChartFor);
			}
		}
	}
}
