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
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaTmpFormulaRel;

/**
 * <pre>
 * Title:指标分析模版公式规则BS
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
public class RptAnaTmpFormulaRelBS extends BaseBS<RptAnaTmpFormulaRel>{
	
	/**
	 * 获取模板勾选的公式
	 * @param templateId
	 * @return
	 */
	public Map<String, String> getTmpFormula(String templateId){
		Map<String, String> formulaMap = new HashMap<String, String>();
		Map<String, Object> orderNumMap = new HashMap<String, Object>();
		String formulas = ""; 
		List<RptAnaTmpFormulaRel> formulaList = this.findEntityListByProperty("templateId", templateId);
		if(formulaList != null && formulaList.size() > 0){
			for(RptAnaTmpFormulaRel anaTmpFormula : formulaList){
				formulas += anaTmpFormula.getFormulaId() + ";";
				orderNumMap.put(anaTmpFormula.getFormulaId(), anaTmpFormula.getOrderNum().toString());
			}
		}
		formulaMap.put("tmpFormula", formulas);
		formulaMap.put("formulaNum", JSON.toJSONString(orderNumMap));
		return formulaMap;
	}

	/**
	 * 保存模板勾选的公式
	 * @param templateId 模板ID
	 * @param selectFa 勾选模板
	 */
	@Transactional(readOnly = false)
	public void saveTmpFaRel(String templateId,String selectFa){
		this.removeEntityByProperty("templateId",templateId);
		if(StringUtils.isNotBlank(selectFa)){
			String[] selectFas = StringUtils.split(selectFa, ';');
			if(selectFas != null && selectFas.length > 0){
				for (String formula : selectFas) {
					if(formula != null && formula.length() > 0){
						String[] formulas = StringUtils.split(formula, ':');
						RptCabinFormulaInfo forInfo = getEntityById(RptCabinFormulaInfo.class,formulas[0]);
						if(forInfo!=null){
							RptAnaTmpFormulaRel tmpMeaRel = new RptAnaTmpFormulaRel();
							tmpMeaRel.setRelId(RandomUtils.uuid2());
							tmpMeaRel.setTemplateId(templateId);
							tmpMeaRel.setFormulaId(forInfo.getFormulaId());
							tmpMeaRel.setFormulaNm(forInfo.getFormulaNm());
							tmpMeaRel.setFormulaContent(forInfo.getFormulaContent());
							tmpMeaRel.setFormulaType(forInfo.getFormulaType());
							if(formulas.length == 2){
								BigDecimal num = new BigDecimal(formulas[1]);
								tmpMeaRel.setOrderNum(num);
							}
							this.saveOrUpdateEntity(tmpMeaRel);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 复制模版勾选规则
	 * @param templateId
	 * @param newTemplateId
	 */
	@Transactional(readOnly = false)
	public void copyTemplateconfig(String templateId,String newTemplateId){
		List<RptAnaTmpFormulaRel> oldMeaRel = this.findEntityListByProperty("templateId", templateId);
		if(oldMeaRel != null && oldMeaRel.size() > 0){
			for(RptAnaTmpFormulaRel rptAnaTmpMeaRel : oldMeaRel){
				RptAnaTmpFormulaRel newMeaRel = new RptAnaTmpFormulaRel();
				newMeaRel.setRelId(RandomUtils.uuid2());
				newMeaRel.setTemplateId(newTemplateId);
				newMeaRel.setFormulaContent(rptAnaTmpMeaRel.getFormulaContent());
				newMeaRel.setFormulaId(rptAnaTmpMeaRel.getFormulaId());
				newMeaRel.setFormulaNm(rptAnaTmpMeaRel.getFormulaNm());
				newMeaRel.setFormulaType(rptAnaTmpMeaRel.getFormulaType());
				newMeaRel.setOrderNum(rptAnaTmpMeaRel.getOrderNum());
				this.saveEntity(newMeaRel);
			}
		}
	}
}
