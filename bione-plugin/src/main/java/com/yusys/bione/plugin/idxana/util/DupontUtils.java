package com.yusys.bione.plugin.idxana.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.comp.common.CommonDupontNode;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.idxana.service.RptAnaIdxBankInfoBS;
import com.yusys.bione.plugin.rptbank.entity.RptIdxBankInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptidx.service.IdxResultAnlyBS;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;

/**
 * <pre>
 * Title:杜邦图生成工具
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

public class DupontUtils {
	
	private static String CURRENCY = GlobalConstants4plugin.DIM_TYPE_CURRENCY_NAME;
	
	/**
	 * 根据维度生成杜邦图节点的工具函数
	 * @param idxno 指标号
	 * @param idxNm 指标名称
	 * @param DimMap 维度集合 （Map<维度类型,维度值>）例：DimMap.put("DATE","20140831");
	 * @param ReturnDimkey 杜邦关系生成的依据度 例："DATE"
	 * @param dataAccuracy 数据精度  例：1，2，3
	 * @param dataUnit 数据单位 （01：元，02：百元，03：千元，04：万元，05：亿元，06：个，07：百个，08：千个，09：万个，10：亿个）
	 * @return
	 */
	public static CommonDupontNode GenerateDupontNode(String idxno ,String idxNm ,Map<String,List<String>> dimMap,String returnDimkey, BigDecimal dataAccuracy, String dataUnit, Map<String,String> formulaMap,String formulaNm){
		IdxResultAnlyBS idxresultanlyBS  = SpringContextHolder.getBean("idxResultAnlyBS");
		CommonDupontNode dupontNode = new CommonDupontNode();
		List<String> idxlist = new ArrayList<String>();
		Map<String,String> formulaUnit = new HashMap<String, String>();
		List<CommonDupontNode> dupontNodes = new ArrayList<CommonDupontNode>();
		String datavalue ="";
		String incre = "";
		BigDecimal hundred = new BigDecimal(100);
		BigDecimal zero = new BigDecimal (0);
		BigDecimal increase = new BigDecimal(0);
		BigDecimal increment = new BigDecimal(0);
		idxlist.add(idxno);
		if(formulaMap != null){
			if(formulaMap.size() == 2){
				formulaUnit.put("increment", dataUnit);
				formulaUnit.put("increase", "00");
			}else if(formulaMap.size() == 1){
				formulaUnit.put("increment", dataUnit);
			}
		}
		Map<String,Map<String,String>> returnData= FetchDataUtils.FetchDataMap(idxlist,"03",true,null,null,dimMap,formulaMap,returnDimkey,dataUnit,dataAccuracy,false,formulaUnit,true);//要改
		Map<String, String> params = new HashMap<String, String>();
		params.put("indexNo", idxno);
		params.put("dimTypeNo", returnDimkey);
		dupontNode.setId("0");
		dupontNode.setUpId("-1");
		dupontNode.setText(idxNm);
		dupontNode.setValue("Na");
		dupontNode.setPercent("100");
		dupontNode.setUnit("");
		dupontNode.setParams(params);
		dupontNodes.add(dupontNode);
		List<RptDimItemInfo> rptDimItemInfolist = idxresultanlyBS.getDimItems(returnDimkey);
		if(rptDimItemInfolist.size()>0){
			for(RptDimItemInfo rptDimItemInfo : rptDimItemInfolist){//将数据放入杜邦节点
				String imsymbil = "";//减价符号
				String iasymbil = "";//减价符号
				Map<String,String> datamap =  returnData.get(rptDimItemInfo.getId().getDimItemNo());
				datavalue = "0";
				increase = new BigDecimal(0);
				increment = new BigDecimal(0);
				if(datamap != null){
					datavalue = datamap.get(idxno);
					if("Na".equals(datavalue)){
						datavalue = "0";
					}
					if(!"Na".equals(datamap.get(idxno + "increment")) && datamap.get(idxno + "increment") != null){
						increment = new BigDecimal(datamap.get(idxno + "increment"));//增量
					}
					if(!"Na".equals(datamap.get(idxno + "increase")) && datamap.get(idxno + "increase") != null){
						increase = new BigDecimal(datamap.get(idxno + "increase"));//增幅
					}
					if(increment.compareTo(zero) >=  0){
						imsymbil = "+";
					}
					if(increase.compareTo(zero) >=  0){
						iasymbil = "+";
					}
					//上面已经做过百分比转换，这个地方不用再乘100了
					//increase = (increase).multiply(hundred);
					increase = increase.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP);
					incre = increase.toString();
					dupontNode = new CommonDupontNode();//实例化CommonTreeNode
					if("#ytec_root#".equals(rptDimItemInfo.getUpNo())){
						dupontNode.setUpId("0");
					}else{
						dupontNode.setUpId(rptDimItemInfo.getUpNo());//folder.png
					}
					params = new HashMap<String, String>();
					params.put("indexNo", idxno);
					params.put("dimTypeNo", returnDimkey);
					params.put("dimItemNo", rptDimItemInfo.getId().getDimItemNo());
					dupontNode.setId(rptDimItemInfo.getId().getDimItemNo());
					dupontNode.setText(rptDimItemInfo.getDimItemNm());
					dupontNode.setPercent("30");
					dupontNode.setUnit("");
					dupontNode.setIcon(datavalue);
					if(datamap.get(idxno + "increment") != null && datamap.get(idxno + "increase") != null){
						dupontNode.setValue("<b>"+ datavalue + "</b>" +"<br>" + formulaNm + imsymbil + increment + "," + iasymbil + incre + "%");
					}else if(datamap.get(idxno + "increment") != null){
						dupontNode.setValue("<b>"+ datavalue + "</b>" +"<br>"+ formulaNm + imsymbil + increment);//+ getdataUnit(dataUnit)
					}else{
						dupontNode.setValue("<b>"+ datavalue + "</b>");
					}
					dupontNode.setParams(params);
					dupontNodes.add(dupontNode);
				}else{
					dupontNode = new CommonDupontNode();//实例化CommonTreeNode
					if("#ytec_root#".equals(rptDimItemInfo.getUpNo())){
						dupontNode.setUpId("0");
					}else{
						dupontNode.setUpId(rptDimItemInfo.getUpNo());//folder.png
					}
					params = new HashMap<String, String>();
					params.put("indexNo", idxno);
					params.put("dimTypeNo", returnDimkey);
					params.put("dimItemNo", rptDimItemInfo.getId().getDimItemNo());
					dupontNode.setId(rptDimItemInfo.getId().getDimItemNo());
					dupontNode.setText(rptDimItemInfo.getDimItemNm());
					dupontNode.setPercent("30");
					dupontNode.setUnit("");
					dupontNode.setIcon(datavalue);
					dupontNode.setValue("<b>Na</b>" +"<br>" + formulaNm + imsymbil + " Na," + iasymbil + " Na%");
					dupontNode.setParams(params);
					dupontNodes.add(dupontNode);
				}
			}
			
			for(CommonDupontNode childNode :dupontNodes ){//构造父子关系和生成子节点
				for(CommonDupontNode ptreeNode :dupontNodes ){
					if(ptreeNode.getId().equals(childNode.getUpId())){
						ptreeNode.addChildNode(childNode);
					}
				}
			}
			
			for(CommonDupontNode dupontNode1 :dupontNodes ){//找到根节点
				if("-1".equals(dupontNode1.getUpId())){
					BigDecimal favalue = new BigDecimal(0);
					for(int i = 0; i < dupontNode1.getChildren().size(); i++){
						CommonDupontNode childNode = dupontNode1.getChildren().get(i);
						favalue = favalue.add(new BigDecimal(childNode.getIcon()));
					}
					dupontNode1.setIcon(favalue.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP).toString());
					dupontNode1.setValue(dupontNode1.getIcon());;
					dupontNode = getdataPercent(dupontNode1,dataAccuracy.intValue());
					break;
				}
			}
			
		}
		return dupontNode;
	}

	/**
	 * 根据指标关系生成指标关系杜邦图的工具函数
	 * @param idxno 根指标标示（指标库中的指标标示，非指标编号）
	 * @param idxNm 根指标名称
	 * @param DimMap 维度集合 （Map<维度类型,维度值>）例：DimMap.put("DATE","20140831");
	 * @param ReturnDimkey 维度集合中的任意维度 例："DATE"
	 * @param themeId 杜邦关系的生成依据的指标组编号 
	 * @param dataAccuracy 数据精度  例：1，2，3
	 * @param dataUnit 数据单位 （01：元，02：百元，03：千元，04：万元，05：亿元，06：个，07：百个，08：千个，09：万个，10：亿个）
	 * @param useCurrency 是否关联指标库币种
	 * @return
	 */
	public static CommonDupontNode GenRptDupontNode(String idxno ,String idxNm ,Map<String,List<String>> DimMap,String ReturnDimkey ,String themeId, BigDecimal dataAccuracy, String dataUnit, Boolean useCurrency,Map<String,String> formulaMap,String formulaNm, String orgType){
		RptAnaIdxBankInfoBS peculiaridxbankinfoBS  = SpringContextHolder.getBean("rptAnaIdxBankInfoBS");
		RptOrgInfoBS rptorginfoBS = SpringContextHolder.getBean("rptOrgInfoBS");
		CommonDupontNode DupontNode = new CommonDupontNode();
		List<CommonDupontNode> DupontNodes = new ArrayList<CommonDupontNode>();
		Map<String,Map<String,String>> returnData = new HashMap<String, Map<String,String>>();
		List<String> idxlist = new ArrayList<String>();
		Map<String,String> formulaUnit = new HashMap<String, String>();
		Map<String,List<String>> idxCurrenyMap = new HashMap<String, List<String>>();
		List<RptIdxBankInfo> idxBanklist =  new ArrayList<RptIdxBankInfo>();
		List<RptIdxBankInfo> peculiarIdxBanklist =  new ArrayList<RptIdxBankInfo>();
		Map<String, String> params = new HashMap<String, String>();
		List<String> orglist = new ArrayList<String>();
		String orgNo = "";
		String incre = "";
		String increment = "";//增量
		BigDecimal increase = new BigDecimal(0);//增幅
		BigDecimal hundred = new BigDecimal(100);
		BigDecimal zero = new BigDecimal (0);
		if(formulaMap != null){
			if(formulaMap.size() == 2){
				formulaUnit.put("increment", dataUnit);
				formulaUnit.put("increase", "00");
			}else if(formulaMap.size() == 1){
				formulaUnit.put("increment", dataUnit);
			}
		}
		params.put("indexNo", idxno);
		DupontNode.setId(idxno);
		DupontNode.setText(idxNm);
		DupontNode.setValue("Na");
		DupontNode.setUnit("");
		DupontNode.setPercent("100");
		DupontNode.setParams(params);
		RptIdxBankInfo mainIndex = peculiaridxbankinfoBS.getMainIdx(idxno, themeId);
		if(mainIndex != null){
			peculiarIdxBanklist = getIdxlist(idxno,themeId);
			peculiarIdxBanklist.add(mainIndex);
			orglist = DimMap.get("ORG");
			if(orglist != null && orglist.size() > 0){
				if(orglist.size() == 1){
					orgNo = orglist.get(0);
					List<RptOrgInfo> rptOrgInfos = rptorginfoBS.getEntityListByProperty(RptOrgInfo.class, "id.orgNo", orgNo);
					if(rptOrgInfos != null && rptOrgInfos.size() > 0){
						RptOrgInfo rptOrgInfo = new RptOrgInfo();
						for(RptOrgInfo org : rptOrgInfos){
							if(org.getId().getOrgType().equals(orgType)){
								rptOrgInfo = org;
								break;
							}
						}
						if("0".equals(rptOrgInfo.getUpOrgNo())){
							for(RptIdxBankInfo PeculiarIdxBankInfo : peculiarIdxBanklist){
								List<String> idxCurrenylist = new ArrayList<String>();
								String currency = PeculiarIdxBankInfo.getCurrency();
								if(StringUtils.isNotEmpty(currency)){
									idxCurrenylist.add(PeculiarIdxBankInfo.getCurrency());
									idxlist.add(PeculiarIdxBankInfo.getMainNo()+ "|" +PeculiarIdxBankInfo.getCurrency());
									idxCurrenyMap.put(PeculiarIdxBankInfo.getMainNo()+ "|" + PeculiarIdxBankInfo.getCurrency(), idxCurrenylist);
								}else{
									idxlist.add(PeculiarIdxBankInfo.getMainNo());
								}
								idxBanklist.add(PeculiarIdxBankInfo);
							}
						}else{
							for(RptIdxBankInfo PeculiarIdxBankInfo : peculiarIdxBanklist){
								List<String> idxCurrenylist = new ArrayList<String>();
								String currency = PeculiarIdxBankInfo.getCurrency();
								if(StringUtils.isNotEmpty(currency)){
									idxCurrenylist.add(PeculiarIdxBankInfo.getCurrency());
									idxlist.add(PeculiarIdxBankInfo.getMainNo()+ "|" +PeculiarIdxBankInfo.getCurrency());
									idxCurrenyMap.put(PeculiarIdxBankInfo.getMainNo()+ "|" + PeculiarIdxBankInfo.getCurrency(), idxCurrenylist);
								}else{
									idxlist.add(PeculiarIdxBankInfo.getMainNo());
								}
								idxBanklist.add(PeculiarIdxBankInfo);
							}
						}
						if(useCurrency){
							returnData= FetchDataUtils.FetchDataMap(idxlist,"03",true,null,null,DimMap,formulaMap,ReturnDimkey,dataUnit,dataAccuracy,CURRENCY,idxCurrenyMap,false,formulaUnit);
						}else{
							returnData= FetchDataUtils.FetchDataMap(idxlist,"03",true,null,null,DimMap,formulaMap,ReturnDimkey,dataUnit,dataAccuracy,false,formulaUnit,true);
						}
						Map<String,String> data = returnData.get(StringUtils.remove(DimMap.get(ReturnDimkey).get(0), '"'));
						if(data != null){
							for(RptIdxBankInfo peculiarIdxBankInfo : idxBanklist){
								String symbil = "";//减价符号
								String idxvo = "Na";
								DupontNode = new CommonDupontNode ();
								BigDecimal yuan = new BigDecimal (0);
								BigDecimal last = new BigDecimal (0);
								if(StringUtils.isNotEmpty(peculiarIdxBankInfo.getCurrency())){
									increment = data.get(peculiarIdxBankInfo.getMainNo() + "|" + peculiarIdxBankInfo.getCurrency() + "increment");
									incre = data.get(peculiarIdxBankInfo.getMainNo() + "|" + peculiarIdxBankInfo.getCurrency() + "increase");
									if(!"Na".equals(data.get(peculiarIdxBankInfo.getMainNo() + "|" + peculiarIdxBankInfo.getCurrency()))){
										yuan = new BigDecimal(data.get(peculiarIdxBankInfo.getMainNo() + "|" + peculiarIdxBankInfo.getCurrency()));
										idxvo = yuan.toString();
									}
								}else{
									increment = data.get(peculiarIdxBankInfo.getMainNo()  + "increment");
									incre = data.get(peculiarIdxBankInfo.getMainNo() + "increase");
									if(!"Na".equals(data.get(peculiarIdxBankInfo.getMainNo() ))){
										yuan = new BigDecimal(data.get(peculiarIdxBankInfo.getMainNo()));
										idxvo = yuan.toString();
									}
								}
								if(!"Na".equals(increment) && increment != null){
									last = new BigDecimal(increment);
									if(last.compareTo(zero) >=  0){
										symbil = "+";
									}
								}
								if(!"Na".equals(incre) && incre != null){
									increase = new BigDecimal(incre);
									increase = (increase).multiply(hundred);
									increase = increase.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP);
									incre = increase.toString();
								}
								params = new HashMap<String, String>();
								params.put("indexNo", peculiarIdxBankInfo.getMainNo());
								if(StringUtils.isNotEmpty(peculiarIdxBankInfo.getCurrency())){
									params.put(CURRENCY, peculiarIdxBankInfo.getCurrency());
								}
								DupontNode.setParams(params);
								DupontNode.setId(peculiarIdxBankInfo.getId().getIndexId());
								DupontNode.setText(peculiarIdxBankInfo.getIndexNm());
								if(incre != null && increment != null){
									DupontNode.setValue("<b>" + idxvo + "</b>" +"<br>"+ formulaNm + symbil + increment + "," + symbil + incre + "%");//+ getdataUnit(dataUnit)
								}else if(increment != null){
									DupontNode.setValue("<b>" + idxvo + "</b>" +"<br>"+ formulaNm + symbil + increment);//+ getdataUnit(dataUnit)
								}else{
									DupontNode.setValue("<b>" + idxvo + "</b>");
								}
								DupontNode.setIcon(yuan.toString());
								DupontNode.setUnit("");
								DupontNode.setPercent("100");
								DupontNode.setUpId(peculiarIdxBankInfo.getUpNo());
								DupontNodes.add(DupontNode);
							}
						}
						
						for(CommonDupontNode ChildNode :DupontNodes ){//构造父子关系和生成子节点
							for(CommonDupontNode ptreeNode :DupontNodes ){
								if(ptreeNode.getId().equals(ChildNode.getUpId())){
									ptreeNode.addChildNode(ChildNode);
								}
							}
						}
						
						for(CommonDupontNode DupontNode1 :DupontNodes ){//找到根节点
							if(mainIndex.getId().getIndexId().equals(DupontNode1.getId())){
								DupontNode = getdataPercent(DupontNode1,dataAccuracy.intValue());
								break;
							}
						}
					}
				}
			}
		}else{
			idxlist.add(idxno);
			returnData= FetchDataUtils.FetchDataMap(idxlist,"03",true,null,null,DimMap,formulaMap,ReturnDimkey,dataUnit,dataAccuracy,false,formulaUnit,true);
			Map<String,String> data = returnData.get(StringUtils.remove(DimMap.get(ReturnDimkey).get(0), '"'));
			if(data != null){
				String symbil = "";//减价符号
				String idxvo = "Na";
				BigDecimal yuan = new BigDecimal (0);
				BigDecimal last = new BigDecimal (0);
				incre = data.get(idxno + "increase");
				increment = data.get(idxno + "increment");
				if(!"Na".equals(data.get(idxno))){
					yuan = new BigDecimal (data.get(idxno));
					idxvo = yuan.toString();
				}
				if(!"Na".equals(increment) && increment != null){
					last = new BigDecimal (increment);
					if(last.compareTo(zero) >=  0){
						symbil = "+";
					}
				}
				if(!"Na".equals(incre) && incre != null){
					increase = new BigDecimal(incre);
					increase = (increase).multiply(hundred);
					increase = increase.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP);
					incre = increase.toString();
				}
				if(incre != null && increment != null){
					DupontNode.setValue("<b>" + idxvo + "</b>" +"<br>"+ formulaNm + symbil + increment + "," + symbil + incre + "%");//+ getdataUnit(dataUnit)
				}else if(increment != null){
					DupontNode.setValue("<b>" + idxvo + "</b>" +"<br>"+ formulaNm + symbil + increment);//+ getdataUnit(dataUnit)
				}else{
					DupontNode.setValue("<b>" + idxvo + "</b>");
				}
			}
		}
		return DupontNode;
	}
	
	/**
	 * 获取当前指标号下的所有指标
	 * @param idxno
	 * @return
	 */
	public static List<RptIdxBankInfo> getIdxlist(String idxno,String themeId){
		RptAnaIdxBankInfoBS peculiaridxbankinfoBS  = SpringContextHolder.getBean("rptAnaIdxBankInfoBS");
		List<RptIdxBankInfo> rptlist = peculiaridxbankinfoBS.getIdxlist(idxno, themeId);
		List<RptIdxBankInfo> rptChrlist = new ArrayList<RptIdxBankInfo>();
		if(rptlist != null && rptlist.size()>0){
			for(int i = 0 ; i < rptlist.size() ; i++){
				rptChrlist.addAll(getIdxlist(rptlist.get(i).getId().getIndexId(),themeId));
			}
		}
		rptlist.addAll(rptChrlist);
		return rptlist;
	}
	
	/**
	 * 根据单位进制变换
	 * @param dataUnit
	 * @return
	 */
	public static BigDecimal getdataChange(String dataUnit){
		BigDecimal dataChange = new BigDecimal(1);
		if("01".equals(dataUnit) || "06".equals(dataUnit)){
			dataChange = new BigDecimal(1);
		}else if("02".equals(dataUnit) || "07".equals(dataUnit)){
			dataChange = new BigDecimal(100);
		}else if("03".equals(dataUnit) || "08".equals(dataUnit)){
			dataChange = new BigDecimal(1000);
		}else if("04".equals(dataUnit) || "09".equals(dataUnit)){
			dataChange = new BigDecimal(10000);
		}else if("05".equals(dataUnit) || "10".equals(dataUnit)){
			dataChange = new BigDecimal(100000000);
		}
		return dataChange;	
	}
	
	/**
	 * 生成数据单位//01 02 03 
	 * @param dataUnit
	 * @return
	 */
	public static String getdataUnit(String dataUnit){
		if("01".equals(dataUnit)){
			dataUnit = "元";
		}else if("06".equals(dataUnit)){
			dataUnit = "个";
		}else if("02".equals(dataUnit) || "07".equals(dataUnit)){
			dataUnit = "百";
		}else if("03".equals(dataUnit) || "08".equals(dataUnit)){
			dataUnit = "千";
		}else if("04".equals(dataUnit) || "09".equals(dataUnit)){
			dataUnit = "万";
		}else if("05".equals(dataUnit) || "10".equals(dataUnit)){
			dataUnit = "亿";
		}
		return dataUnit;	
	}
	
	/**
	 * 获取数据精度
	 * @param Accuracy
	 * @return
	 */
	public static BigDecimal getdataAccuracy(String Accuracy){
		BigDecimal dataAccuracy  = new BigDecimal(0);
		if("1".equals(Accuracy)){
			dataAccuracy = new BigDecimal(1);
		}else if("2".equals(Accuracy)){
			dataAccuracy = new BigDecimal(2);
		}else if("3".equals(Accuracy)){
			dataAccuracy = new BigDecimal(3);
		}else if("4".equals(Accuracy)){
			dataAccuracy = new BigDecimal(4);
		}else if("5".equals(Accuracy)){
			dataAccuracy = new BigDecimal(5);
		}
		return dataAccuracy;	
	}
	
	/**
	 * 生成子节点所占的百分比
	 * @param DupontNode
	 * @return
	 */
	public static CommonDupontNode getdataPercent(CommonDupontNode DupontNode,int dataAccuracy){
		if(DupontNode != null && DupontNode.getChildren() != null && DupontNode.getChildren().size()>0){//判断是否为空
			BigDecimal favalue = new BigDecimal(1);
			for(CommonDupontNode ChildNode : DupontNode.getChildren()){
				BigDecimal chvalue = new BigDecimal(ChildNode.getIcon().toString());
				if(new BigDecimal(DupontNode.getIcon().toString()).doubleValue()!=0){
					favalue = new BigDecimal(DupontNode.getIcon().toString()); 
				}
				chvalue = chvalue.divide(favalue, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
				chvalue = chvalue.setScale(dataAccuracy, BigDecimal.ROUND_HALF_UP);
				ChildNode.setPercent(chvalue.toString());
				ChildNode = getdataPercent(ChildNode,dataAccuracy);
			}
		}
		return DupontNode;	
	}
}
