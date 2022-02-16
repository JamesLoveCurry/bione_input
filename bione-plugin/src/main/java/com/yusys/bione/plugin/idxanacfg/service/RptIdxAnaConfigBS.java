package com.yusys.bione.plugin.idxanacfg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.entity.RptCabinFormulaInfo;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsDateRel;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsFormulaRel;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsInfo;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsOrgRel;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsThemeRel;

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

@Service
@Transactional(readOnly = true)
public class RptIdxAnaConfigBS extends BaseBS<Object>{
	
	@Autowired
	private RptAnaChartsInfoBS anachartsinfoBS;
	
	@Autowired
	private RptAnaChartsFormulaRelBS anachaformularelBS;
	
	@Autowired
	private RptAnaChartsOrgRelBS anachartorgrelBS;
	
	@Autowired
	private RptAnaChartsDateRelBS anachartsdaterelBS;
	
	@Autowired
	private RptAnaChartsThemeRelBS anachartsthemerelBS;
	/**
	 * 删除图表
	 * @param chartId
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> deleteCha(String chartId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String tipText = "已删除指标概要图";
		if(StringUtils.isNotBlank(chartId)){
			RptAnaChartsInfo anaChartsInfo = anachartsinfoBS.getEntityById(chartId);
			if(anaChartsInfo != null){
				anachartsinfoBS.removeEntityById(chartId);
				List<RptAnaChartsFormulaRel> formulaList = this.anachaformularelBS.getEntityListByProperty(RptAnaChartsFormulaRel.class, "chartId", chartId);
				if(formulaList.size()>0){
					anachaformularelBS.removeEntityByProperty("chartId", chartId);
				}
				if(GlobalConstants4plugin.ORG.equals(anaChartsInfo.getChartType())){
					RptAnaChartsOrgRel chartsOrg = this.anachartorgrelBS.getEntityById(chartId);
					if(chartsOrg != null){
						tipText = "已删除机构信息图";
						anachartorgrelBS.removeEntityById(chartId);
					}
				}else if(GlobalConstants4plugin.TREND.equals(anaChartsInfo.getChartType())){
					RptAnaChartsDateRel chartsDate = this.anachartsdaterelBS.getEntityById(chartId);
					if(chartsDate != null){
						tipText = "已删除趋势分析图";
						anachartsdaterelBS.removeEntityById(chartId);
					}
				}else if(GlobalConstants4plugin.RELATIONSHIP.equals(anaChartsInfo.getChartType())){
					tipText = "已删除结构解析图";
				}else if(GlobalConstants4plugin.STRUCTURE.equals(anaChartsInfo.getChartType())){
					RptAnaChartsThemeRel chartsTheme = anachartsthemerelBS.getEntityById(chartId);
					if(chartsTheme != null){
						tipText = "已删除关系解析图";
						anachartsthemerelBS.removeEntityById(chartId);
					}
				}
			}
		}
		resultMap.put("tipText", tipText);
		return resultMap;
	}
	
	/**
	 * 根据模板频度获取公式
	 * @param templateFreq  模板频度
	 * @return
	 */
	public Map<String, Object> getInitFormula(String templateFreq){
		Map<String, Object> formulaMap = new HashMap<String, Object>();
		formulaMap.put("pointFa", this.conBoxNode(this.getFormula(templateFreq, GlobalConstants4plugin.POINT_FA)));//时点公式
		formulaMap.put("cumulativeFa", this.conBoxNode(this.getFormula(templateFreq, GlobalConstants4plugin.CUMULATIVE_FA)));//累计公式
		formulaMap.put("averageFa", this.conBoxNode(this.getFormula(templateFreq, GlobalConstants4plugin.AVERAGE_FA)));//日均公式
		formulaMap.put("groupFa", this.conBoxNode(this.getFormula(templateFreq, GlobalConstants4plugin.GROUP_FA)));//组合公式
		formulaMap.put("formulaMap",this.getFaMap());
		return formulaMap;
	}
	
	/**
	 * 构造机构分析文本配置特殊公式
	 * @param formulaType
	 * @return
	 */
	public Map<String, String> getFaByType(String formulaType){
		Map<String, String> formulaMap = new HashMap<String, String>();
		formulaMap.put("formula", JSON.toJSONString(this.getFormula("01", formulaType)));
		return formulaMap;
	}
	
	/**
	 * 根据模板频度和公式类型获取公式
	 * @param templateFreq 模板频度
	 * @param formulaType 公式类型
	 * @return
	 */
	private List<RptCabinFormulaInfo> getFormula(String templateFreq,String formulaType){
		List<RptCabinFormulaInfo> formulas = new ArrayList<RptCabinFormulaInfo>();
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "select formula from RptCabinFormulaInfo formula where formula.formulaType in (:formulaType)";
		params.put("formulaType", formulaType);
		if(!"01".equals(templateFreq)){
			jql += "and formula.formulaFreq in (:formulaFreq)";
			params.put("formulaFreq", templateFreq);
		}
		jql += "order by formula.orderNum";
		formulas = this.baseDAO.findWithNameParm(jql,params);
		return formulas;
	}
	
	/**
	 * 构造成复选框数据模型
	 * @param formulas
	 * @return
	 */
	private List<CommonComboBoxNode> conBoxNode(List<RptCabinFormulaInfo> formulas){
		List<CommonComboBoxNode> boxNodes = new ArrayList<CommonComboBoxNode>();
		if(formulas != null && formulas.size() > 0){
			for(RptCabinFormulaInfo formula : formulas){
				CommonComboBoxNode boxNode = new CommonComboBoxNode();
				boxNode.setId(formula.getFormulaId());
				boxNode.setText(formula.getFormulaNm());
				boxNodes.add(boxNode);
			}
		}
		return boxNodes;
	}
	
	/**
	 * 构造图表文字配置的公式
	 * @return
	 */
	private Map<String, Object> getFaMap(){
		Map<String, Object> formulaMap = new HashMap<String, Object>();
		String jql = "select formula from RptCabinFormulaInfo formula";
		List<RptCabinFormulaInfo> formulas = this.baseDAO.findWithIndexParam(jql);
		if(formulas != null && formulas.size() > 0){
			for(RptCabinFormulaInfo formula : formulas){
				formulaMap.put(formula.getFormulaId(), "#" + formula.getDisplayContent() + "#");
			}
		}
		return formulaMap;
	}

}
