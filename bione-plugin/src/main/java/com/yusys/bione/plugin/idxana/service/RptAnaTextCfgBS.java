package com.yusys.bione.plugin.idxana.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.entity.RptCabinFormulaInfo;
import com.yusys.bione.plugin.idxana.util.DupontUtils;
import com.yusys.bione.plugin.idxana.util.FetchDataUtils;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaChartsInfo;
import com.yusys.bione.plugin.idxanacfg.entity.RptAnaTmpInfo;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaChartsInfoBS;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
/**
 * <pre>
 * Title:机构信息文本BS
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
public class RptAnaTextCfgBS extends BaseBS<Object>{
	
	@Autowired
	private RptOrgInfoBS rptorgBS;
	
	@Autowired
	private RptAnaChartsInfoBS anachartsinfoBS;
	
	private static String CURRENCY = GlobalConstants4plugin.DIM_TYPE_CURRENCY_NAME;
	
	private static String DATE = GlobalConstants4plugin.DIM_TYPE_DATE_NAME;
	
	private static String ORG = GlobalConstants4plugin.DIM_TYPE_ORG_NAME;
	
	/**
	 * 获取指标分析文本内容
	 * @param idxno 指标编号
	 * @param chartId 图表IDD
	 * @param idxNm 指标名称
	 * @param dataUnit 数据单位（文字）
	 * @param date 日期
	 * @param org 机构
	 * @param currency 币种
	 * @param unit 数据单位（编号）
	 * @return
	 */
	public String initChartText(String idxno, String chartId, String chartType, String idxNm, String dataUnit, String date, String org, String currency, String unit, String orgType){
		Map<String,String> formulaMap = new HashMap<String, String>();
		Map<String,String> formulaUnit = new HashMap<String, String>();
		Map<String,String> formulaType = new HashMap<String, String>();
		Map<String,List<String>> dimMap = new HashMap<String, List<String>>();
		List<String> datelist = new ArrayList<String>();
		List<String> orglist = new ArrayList<String>();
		List<String> contentlist = new ArrayList<String>();
		List<String> currencylist = new ArrayList<String>();
		List<String> idxlist = new ArrayList<String>();
		String anaText = "";
		idxlist.add(idxno);
		BigDecimal hundred = new BigDecimal(100);
		BigDecimal zero = new BigDecimal(0);
		String chartUnit = getdataUnit(chartId);
		if(StringUtils.isNotBlank(unit)){
			chartUnit = unit;
		}
		dataUnit = DupontUtils.getdataUnit(chartUnit);
		RptAnaChartsInfo anaChartsInfo = anachartsinfoBS.getEntityById(chartId);
		if(anaChartsInfo != null){
			String textContent = anaChartsInfo.getTextCfg();
			if(textContent != null && textContent.length() > 0){
				textContent = StringUtils.replace(textContent, "\n", "<br>");
				textContent = StringUtils.replace(textContent, " ", "&nbsp");
				textContent = StringUtils.replace(textContent, "#idxNm#", idxNm);
				if("02".equals(chartType)){
					String textVal = textContent;
					List<RptOrgInfo> orgInfoList = rptorgBS.findOrgChild(org, orgType, false, false);
					if(orgInfoList != null && orgInfoList.size() > 0){
						orglist.add(org);
						for(RptOrgInfo orgNo : orgInfoList){
							orglist.add(orgNo.getId().getOrgNo());
						}
						datelist.add(date);
						currencylist.add(currency);
						if(StringUtils.isNotEmpty(date)){
							dimMap.put(DATE, datelist);
						}
						if(StringUtils.isNotEmpty(currency)){
							dimMap.put(CURRENCY, currencylist);
						}
						if(StringUtils.isNotEmpty(org)){
							dimMap.put(ORG, orglist);
						}
						Map<String,Map<String,String>> dataMapTop = FetchDataUtils.FetchDataMap(idxlist, "03", true, null, null, dimMap, formulaMap, ORG, chartUnit, getdataAccuracy(chartId), true);
						Map<String,Map<String,String>> dataMapBottom = FetchDataUtils.FetchDataMap(idxlist, "02", true, null, null, dimMap, formulaMap, ORG, chartUnit, getdataAccuracy(chartId), true);
						if(dataMapTop != null){
							BigDecimal forgValue = new BigDecimal(0);
							Map<String,String> fDatamap =  dataMapTop.get(org);
							if(fDatamap != null){
								if(!"Na".equals(fDatamap.get(idxno))){
									forgValue = new BigDecimal(fDatamap.get(idxno));
								}
								dataMapTop.remove(org);
								dataMapBottom.remove(org);
							}
							Map<String,Map<String,String>> orgNameMap = new HashMap<String, Map<String,String>>();
							String orgColor = "#0065d2";
							while(textContent.contains("#")){
								String content = StringUtils.substringBetween(textContent,"#");
								if(content != null && content.length() > 0){
									contentlist.add(content);
								}
								textContent = StringUtils.replace(textContent, "#"+ content +"#", "");
							}
							if(contentlist!= null && contentlist.size() > 0){
								for(String content : contentlist){
									if(content.equals("idxNm"))
										continue;
									String textValue = "";
									String orgValst = "";
									BigDecimal orgValueset = new BigDecimal(0);
									BigDecimal orgValend = new BigDecimal(0);
										String param = StringUtils.substringBetween(content, "(", ")");
										int rankNumber = 3;
										String type = "01";
										if(StringUtils.isNotBlank(param)){
											String params[] = StringUtils.split(param,",");
											if(params.length == 2){
												try{
													rankNumber = Integer.parseInt(params[0]);
												}
												catch(Exception e){
													continue;
												}
												type = params[1];
											}
										}
										else{
											continue;
										}
										if(rankNumber > dataMapTop.size() - 1){
											rankNumber = dataMapTop.size() - 1;
										}
										content = StringUtils.remove(content, param);
										if("rankTop()".equals(content)){
											int i = 0;
											orgNameMap = dataMapTop;
											for(String key :dataMapTop.keySet()){
												if(rankNumber > i){
													Map<String,String> datamap =  dataMapTop.get(key);
													if(datamap != null){
														i++;
														if(!"Na".equals(datamap.get(idxno))){
															orgValueset = orgValueset.add(new BigDecimal(datamap.get(idxno)));
														}
													}
												}
											}
											if(forgValue.compareTo(zero) == 0){
												orgValst = "Na";
											}else{
												orgValend = (orgValueset.divide(forgValue,5,BigDecimal.ROUND_HALF_UP)).multiply(hundred).setScale(getdataAccuracy(chartId).intValue(),BigDecimal.ROUND_HALF_UP);
												orgValst = orgValend.toString();
											}
											textValue += "<span style='color:#0065d2'>" +orgValueset + "</span>" + dataUnit + "，";
											if(type.equals("01")||type.equals("02")){
												textValue += ",占比为：" + "<span style='color:#0065d2'>" + orgValst + "</span>" + "%";
											}
											if(type.equals("01")||type.equals("03")){
												i = 0;
												List<String> orgNolist = new ArrayList<String>();
												for(String key :orgNameMap.keySet()){
													if(rankNumber > i){
														i++;
														orgNolist.add(key);
													}
												}
												String jql = "select org from RptOrgInfo org where org.id.orgType = ?0 and org.id.orgNo in ?1";
												if(orgNolist.size()>0){
													List<RptOrgInfo> rptOrgInfolist = this.baseDAO.findWithIndexParam(jql, orgType, orgNolist);
													if(rptOrgInfolist != null && rptOrgInfolist.size() > 0){
														for(String orgNo : orgNolist){
															for(RptOrgInfo rptOrgInfo : rptOrgInfolist){
																if(orgNo.equals(rptOrgInfo.getId().getOrgNo())){
																	textValue += "  " + "<span style='color:"+ orgColor +"'>" +rptOrgInfo.getOrgNm() + "</span>";
																	break;
																}
															}
														}
														textValue = StringUtils.replaceOnce(textValue, ",", "");
													}
												}
											}
											textVal = StringUtils.replaceOnce(textVal, "#rankTop("+ param +")#", textValue);
										}
										else if("rankBottom()".equals(content)){
											int i = 0;
											orgNameMap = dataMapBottom;
											orgColor = "#ed5565";
											for(String key :dataMapBottom.keySet()){
												if(rankNumber > i){
													Map<String,String> datamap =  dataMapBottom.get(key);
													if(datamap != null){
														i++;
														if(!"Na".equals(datamap.get(idxno))){
															orgValueset = orgValueset.add(new BigDecimal(datamap.get(idxno)));
														}
													}
												}
											}
											if(forgValue.compareTo(zero) == 0){
												orgValst = "Na";
											}else{
												orgValend = (orgValueset.divide(forgValue,5,BigDecimal.ROUND_HALF_UP)).multiply(hundred).setScale(getdataAccuracy(chartId).intValue(),BigDecimal.ROUND_HALF_UP);
												orgValst = orgValend.toString();
											}
											textValue += "<span style='color:#ed5565'>" + orgValueset + "</span>" + dataUnit + "，";
											if(type.equals("01")||type.equals("02")){
												textValue += ",占比为：" + "<span style='color:#ed5565'>" + orgValst + "</span>" + "%";
											}
											if(type.equals("01")||type.equals("03")){
												i = 0;
												List<String> orgNolist = new ArrayList<String>();
												for(String key :orgNameMap.keySet()){
													if(rankNumber > i){
														i++;
														orgNolist.add(key);
													}
												}
												String jql = "select org from RptOrgInfo org where org.id.orgType = ?0 and org.id.orgNo in ?1";
												if(orgNolist.size()>0){
													List<RptOrgInfo> rptOrgInfolist = this.baseDAO.findWithIndexParam(jql, orgType, orgNolist);
													if(rptOrgInfolist != null && rptOrgInfolist.size() > 0){
														for(String orgNo : orgNolist){
															for(RptOrgInfo rptOrgInfo : rptOrgInfolist){
																if(orgNo.equals(rptOrgInfo.getId().getOrgNo())){
																	textValue += "  " + "<span style='color:"+ orgColor +"'>" +rptOrgInfo.getOrgNm() + "</span>";
																	break;
																}
															}
														}
														textValue = StringUtils.replaceOnce(textValue, ",", "");
													}
												}
											}
											textVal = StringUtils.replaceOnce(textVal, "#rankBottom("+ param +")#", textValue);
										}
								}
							}
							
						}
					}
					textContent = textVal;
				}else{
					while(textContent.contains("#")){
						String content = StringUtils.substringBetween(textContent,"#");
						if(content != null && content.length() > 0){
							contentlist.add(content);
						}
						textContent = StringUtils.replaceOnce(textContent, "#"+ content +"#", "");
					}
					if(contentlist != null && contentlist.size() > 0){
						String jql = "select formula from RptCabinFormulaInfo formula where formula.displayContent in ?0";
						List<RptCabinFormulaInfo> rptAnaFormulaInfolist = this.baseDAO.findWithIndexParam(jql, contentlist);
						for(RptCabinFormulaInfo rptAnaFormulaInfo : rptAnaFormulaInfolist){
							if(!("I".equals(rptAnaFormulaInfo.getDisplayContent()))){
								formulaMap.put(rptAnaFormulaInfo.getDisplayContent() + "ment","I('$1')-"+rptAnaFormulaInfo.getFormulaContent());
								formulaMap.put(rptAnaFormulaInfo.getDisplayContent() + "ase","(I('$1')-"+rptAnaFormulaInfo.getFormulaContent()+")/"+rptAnaFormulaInfo.getFormulaContent());
								formulaUnit.put(rptAnaFormulaInfo.getDisplayContent() + "ment",chartUnit);
								formulaUnit.put(rptAnaFormulaInfo.getDisplayContent() + "ase","00");
							}
							formulaMap.put(rptAnaFormulaInfo.getDisplayContent(), rptAnaFormulaInfo.getFormulaContent());
							formulaUnit.put(rptAnaFormulaInfo.getDisplayContent(),chartUnit);
							if("1".equals(rptAnaFormulaInfo.getFormulaType())){
								formulaType.put(rptAnaFormulaInfo.getDisplayContent(), "Y");
							}
						}
						datelist.add(date);
						currencylist.add(currency);
						orglist.add(org);
						if(StringUtils.isNotEmpty(date)){
							dimMap.put(DATE, datelist);
						}
						if(StringUtils.isNotEmpty(currency)){
							dimMap.put(CURRENCY, currencylist);
						}
						if(StringUtils.isNotEmpty(org)){
							dimMap.put(ORG, orglist);
						}
						Map<String,Map<String,String>> dataMap= FetchDataUtils.FetchDataMap(idxlist,"03",true,null,null,dimMap,formulaMap,ORG,chartUnit,getdataAccuracy(chartId),false,formulaUnit,true);
						if(dataMap != null){
							Map<String,String> datamap =  dataMap.get(org);
							if(datamap != null){
								textContent = anaChartsInfo.getTextCfg();
								textContent = StringUtils.replace(textContent, "\n", "<br>");
								textContent = StringUtils.replace(textContent, " ", "&nbsp");
								textContent = StringUtils.replace(textContent, "#idxNm#", idxNm);
								for(String content : contentlist){
									if(content != null && content.length() > 0){
										String textValue = "";
										String formulaValue = datamap.get(idxno + content);
										String formulaValuement = datamap.get(idxno + content + "ment");
										String formulaValuease = datamap.get(idxno + content + "ase");
										if(formulaValue != null){
											if(!"Na".equals(formulaValue)){
												BigDecimal value = new BigDecimal(formulaValue);
												if(value.compareTo(zero) >= 0){
													textValue += "<span style='color:#0065d2'>" + formulaValue + "</span>" + dataUnit;
												}else{
													textValue += "<span style='color:#ed5565'>" + formulaValue + "</span>" + dataUnit;
												}
											}else{
												textValue += "<span style='color:#0065d2'>" + formulaValue + "</span>" + dataUnit;
											}
										}
										if(formulaType.get(content) != null){
											if(formulaValuement != null){
												if(!"Na".equals(formulaValuement)){
													BigDecimal value = new BigDecimal(formulaValuement);
													if(value.compareTo(zero) >= 0){
														textValue += ",增量为：" + "<span style='color:#0065d2'>" + formulaValuement + "</span>" + dataUnit;
													}else{
														textValue += ",增量为：" + "<span style='color:#ed5565'>" + formulaValuement + "</span>" + dataUnit;
													}
												}else{
													textValue += ",增量为：" + "<span style='color:#0065d2'>" + formulaValuement + "</span>" + dataUnit;
												}
											}
											if(formulaValuease != null){
												if(!"Na".equals(formulaValuease)){
													BigDecimal aseValue = new BigDecimal(formulaValuease);
													aseValue = aseValue.multiply(hundred).setScale(getdataAccuracy(chartId).intValue(), BigDecimal.ROUND_HALF_UP);
													if(aseValue.compareTo(zero) >= 0){
														textValue += ",增幅为：" + "<span style='color:#0065d2'>" + aseValue + "</span>" + "%";
													}else{
														textValue += ",增幅为：" + "<span style='color:#ed5565'>" + aseValue + "</span>" + "%";
													}
												}else{
													textValue += ",增幅为：" + "<span style='color:#0065d2'>" + formulaValuease + "</span>" + "%";
												}
											}
										}
										textContent = StringUtils.replace(textContent, "#"+ content +"#", textValue);
									}
								}
							}
						}
					}
				}
				anaText = textContent;
			}
		}
		return anaText;
	}
	
	/**
	 * 获取单位
	 * @param chartId
	 * @return
	 */
	public String getdataUnit(String chartId){
		String dataChange = "01";
		RptAnaChartsInfo AnaChartsInfo = anachartsinfoBS.getEntityById(chartId);
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
	 * 获取数据精度
	 * @param chartId
	 * @return
	 */
	public BigDecimal getdataAccuracy(String chartId){
		RptAnaChartsInfo AnaChartsInfo = anachartsinfoBS.getEntityById(chartId);
		String jql = "select idx from RptAnaTmpInfo idx where idx.templateId = (?0)";
		RptAnaTmpInfo AnaTmpInfo = this.baseDAO.findUniqueWithIndexParam(jql,AnaChartsInfo.getTemplateId());
		BigDecimal dataAccuracy  = AnaTmpInfo.getDataPrecision();
		return dataAccuracy;	
	}
	
}
