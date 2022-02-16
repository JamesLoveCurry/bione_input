package com.yusys.bione.plugin.design.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfoPK;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDimTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDsVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcFormulaVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcTextVO;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

public class CrossDesignAnalysis extends AbstractDesignSrcAnalysis{

	private String[] orgNo;
	private List<Map<String, Object>> searchArgs;
	private List<Map<String, Object>> rowSearchArgs;
	private List<Object> dimNos;
	private List<Map<String, Object>> columnMaps;
	private List<Map<String, Object>> sum;
	private Map<String, Object> sort = null;
	private String templateType = "";
	private String dateFre;
	private boolean orgDimFlag=false;
	private boolean orgLvlFlag=false;

	public CrossDesignAnalysis(SpreadSchema scheme, RptDesignTmpInfo tmp,
			List<RptDesignCellInfo> cells, List<RptDesignSrcIdxVO> idxCells,
			List<RptDesignSrcDsVO> dsCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells,
			List<Map<String, Object>> searchParams, String dataDate,String unit,
			String busiType, List<String> cellNos,
			Map<String, String> validates, String fileName, boolean isExtend,boolean isCache) {
		super(scheme, tmp, cells, idxCells, dsCells, formulaCells, textCells,
				idxTabCells, dimTabCells, searchParams, dataDate,unit, busiType,
				cellNos, validates, fileName, isExtend, null, true,isCache);
	}

	@Override
	protected void createSelectJson() {
		rowSearchArgs = new ArrayList<Map<String, Object>>();
		templateType = tmp.getTemplateType();
		dealParams();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("QueryType", "index");
		if (this.orgNo != null)
			map.put("OrgNo", this.orgNo);
		map.put("DimNo", this.dimNos);
		map.put("Colums", this.columnMaps);
		map.put("Sum", this.sum);
		if (StringUtils.isNotBlank(this.dateFre)) {
			map.put("DateFre", this.dateFre);
		}
		if (sort != null)
			map.put("Sort", this.sort);

		// 添加机构权限
		if(orgDimFlag){
			List<Map<String, Object>> searchArg = setOrgValidate(this.getSearchParams());
			if (searchArg != null && !orgLvlFlag) {
				rowSearchArgs.addAll(searchArg);
			}
			if (rowSearchArgs.size() > 0)
				map.put("SearchArg", rowSearchArgs);
		}
		if(isCache){
			map.put("ExecNode", "11");
		}
		this.setIdxDetialSelectParams(JSON.toJSONString(map));
	}

	private String getExcelInfo(String cellNo) {
		for (RptDesignCellInfoPK id : this.formulaCellMaps.keySet()) {
			if (id.getCellNo().equals(cellNo))
				return formulaCellMaps.get(id).getExcelFormula();
		}
		return null;

	}

	private String getExcelFormula(String formula) {
		Pattern pattern = Pattern.compile("[A-Z]+\\d+");
		Matcher matcher = pattern.matcher(formula);
		StringBuffer sb = new StringBuffer("");
		while (matcher.find()) {
			String tmp = matcher.group(0);
			String excelFormula = this.getExcelInfo(tmp);
			if (excelFormula != null)
				matcher.appendReplacement(sb, "("
						+ getExcelFormula(excelFormula) + ")");
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private Map<String, Object> getSearchInfo(String dimTypeNo){
		if(this.rowSearchArgs!= null && this.rowSearchArgs.size()>0){
			for(Map<String,Object> search : this.rowSearchArgs){
				if(search.get("DimNo").equals(dimTypeNo)){
					if(search.get("Alias").toString().indexOf("getDimLvl")>=0){
						return search;
					}
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void dealParams() {
		// PropertiesUtils pro=new
		// PropertiesUtils("bione-plugin/extension/report-common.properties");
		dimAlias = new HashMap<String, String>();
		searchArgs = new ArrayList<Map<String, Object>>();
		dimNos = new ArrayList<Object>();
		columnMaps = new ArrayList<Map<String, Object>>();
		sum = new ArrayList<Map<String, Object>>();

		for (RptDesignCellInfoPK key : this.formulaCellMaps.keySet()) {
			RptDesignSrcFormulaVO vo = this.formulaCellMaps.get(key);
			if (StringUtils.isNotBlank(vo.getSortMode())
					&& !vo.getSortMode().equals("01")) {
				sort = new HashMap<String, Object>();
				sort.put("SortFormula", getExcelFormula(vo.getExcelFormula()));
				sort.put("SortType", vo.getSortMode());

			}
		}
		Map<Integer, String> dimOrderMap = new LinkedHashMap<Integer, String>();

		if (dimTabCellMaps != null && dimTabCellMaps.size() > 0) {
			for (RptDesignCellInfoPK key : this.dimTabCellMaps.keySet()) {
				int num = getUniqueDim(this.dimTabCellMaps.get(key)
						.getDimTypeNo(), dimNos);
				if(this.dimTabCellMaps.get(key)
							.getDimTypeNo().equals("ORG")){
					this.orgDimFlag = true;
				}
				if (num > 0) {
					Map<String, Object> dim = new HashMap<String, Object>();
					dim.put("DimNo", this.dimTabCellMaps.get(key)
							.getDimTypeNo());
					dim.put("Alias", this.dimTabCellMaps.get(key)
							.getDimTypeNo() + num);
					dimAlias.put(this.dimTabCellMaps.get(key).getId()
							.getCellNo(), this.dimTabCellMaps.get(key)
							.getDimTypeNo() + num);
					if ("N".equals(this.dimTabCellMaps.get(key).getIsConver())) {
						dim.put("IsChange", "N");
					}
					if (StringUtils.isNotBlank(this.dimTabCellMaps.get(key)
							.getDisplayLevel())) {
						if ("DATE".equals(this.dimTabCellMaps.get(key)
								.getDimTypeNo())) {
							this.dateFre = this.dimTabCellMaps.get(key)
									.getDisplayLevel();
						}
						if(this.dimTabCellMaps.get(key)
							.getDisplayLevel().indexOf("N")<0)
							dim.put("Formula",
									"chgDimVal('"
											+ (this.dimTabCellMaps.get(key)
													.getDimTypeNo())
											+ "','"
											+ this.dimTabCellMaps.get(key)
													.getDisplayLevel() + "')");
						if (!"DATE".equals(this.dimTabCellMaps.get(key)
								.getDimTypeNo())) {
							Map<String, Object> search = getSearchInfo(this.dimTabCellMaps.get(key)
									.getDimTypeNo());
							if(this.dimTabCellMaps.get(key).getDisplayLevel().indexOf("N")<0){
								if(search != null){
									if(Integer.parseInt(this.dimTabCellMaps.get(key).getDisplayLevel())>Integer.parseInt(search.get("Value").toString()))
										search.put("Value", this.dimTabCellMaps.get(key).getDisplayLevel());
								}
								else{
									search = new HashMap<String, Object>();
									search.put("Op", "=");
									search.put("DimNo", this.dimTabCellMaps.get(key)
											.getDimTypeNo());
									search.put("Value", this.dimTabCellMaps.get(key)
											.getDisplayLevel());
									search.put("Alias", "getDimLvl("
											+ this.dimTabCellMaps.get(key)
													.getDimTypeNo() + ")");
									this.rowSearchArgs.add(search);
								}
							}
							else{
								String displayLvl = this.dimTabCellMaps.get(key)
										.getDisplayLevel();
								String flag = displayLvl.indexOf("B")>=0 ? "N" : "Y";
								String lvl = StringUtils.substring(displayLvl, displayLvl.indexOf("N")+1, displayLvl.length());
								String value ="";
								if(!getDimSearch(this.dimTabCellMaps.get(key)
										.getDimTypeNo(),this.getSearchParams())){
									if(this.dimTabCellMaps.get(key)
										.getDimTypeNo().equals("ORG")){
										value = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
									}
								}
								else{
									for (Map<String, Object> searchArg : this.getSearchParams()) {
										String dimNo = String.valueOf(searchArg.get("DimNo"));
										if(dimNo.equals(this.dimTabCellMaps.get(key)
												.getDimTypeNo())){
											this.getSearchParams().remove(searchArg);
											if(searchArg.get("Value") instanceof String){
												value = searchArg.get("Value").toString();
											}
											else{
												List<Object> values = (List<Object>) searchArg.get("Value");
												if(values!=null && values.size()>0){
													for(Object val : values){
														if(StringUtils.isNotBlank(val.toString())){
															value += val.toString() +",";
														}
													}
												}
												value = StringUtils.substring(value, 0,value.length()-1);
											}
											break;
										}
									}
								}
								search=new HashMap<String, Object>();
								search.put("DimNo", this.dimTabCellMaps.get(key)
										.getDimTypeNo());
								search.put("Op", "IN");
								search.put("Value", "getChildDimVal('"+this.dimTabCellMaps.get(key)
										.getDimTypeNo()+"','"+value+"','"+flag+"','"+lvl+"')");
								if(this.dimTabCellMaps.get(key)
										.getDimTypeNo().equals("ORG")){
									orgLvlFlag = true;
								}
								this.rowSearchArgs.add(search);
							}
						}
					}
					dimNos.add(dim);
				} else if ("N".equals(this.dimTabCellMaps.get(key)
						.getIsConver())
						|| StringUtils.isNotBlank(this.dimTabCellMaps.get(key)
								.getDisplayLevel())) {
					Map<String, Object> dim = new HashMap<String, Object>();
					dim.put("DimNo", this.dimTabCellMaps.get(key)
							.getDimTypeNo());
					if ("N".equals(this.dimTabCellMaps.get(key).getIsConver()))
						dim.put("IsChange", "N");
					if (StringUtils.isNotBlank(this.dimTabCellMaps.get(key)
							.getDisplayLevel())) {
						if ("DATE".equals(this.dimTabCellMaps.get(key)
								.getDimTypeNo())) {
							this.dateFre = this.dimTabCellMaps.get(key)
									.getDisplayLevel();
						}
						if(this.dimTabCellMaps.get(key)
								.getDisplayLevel().indexOf("N")<0)
						dim.put("Formula",
								"chgDimVal('"
										+ (this.dimTabCellMaps.get(key)
												.getDimTypeNo())
										+ "','"
										+ this.dimTabCellMaps.get(key)
												.getDisplayLevel() + "')");
						if (!"DATE".equals(this.dimTabCellMaps.get(key)
								.getDimTypeNo())) {
							Map<String, Object> search = getSearchInfo(this.dimTabCellMaps.get(key)
									.getDimTypeNo());
							if(this.dimTabCellMaps.get(key).getDisplayLevel().indexOf("N")<0){
								if(search != null){
									if(Integer.parseInt(this.dimTabCellMaps.get(key).getDisplayLevel())>Integer.parseInt(search.get("Value").toString()))
										search.put("Value", this.dimTabCellMaps.get(key).getDisplayLevel());
								}
								else{
									search = new HashMap<String, Object>();
									search.put("Op", "=");
									search.put("DimNo", this.dimTabCellMaps.get(key)
											.getDimTypeNo());
									search.put("Value", this.dimTabCellMaps.get(key)
											.getDisplayLevel());
									search.put("Alias", "getDimLvl("
											+ this.dimTabCellMaps.get(key)
													.getDimTypeNo() + ")");
									this.rowSearchArgs.add(search);
								}
							}
							else{
								String displayLvl = this.dimTabCellMaps.get(key)
										.getDisplayLevel();
								String flag = displayLvl.indexOf("B")>=0 ? "N" : "Y";
								String lvl = StringUtils.substring(displayLvl, displayLvl.indexOf("N")+1, displayLvl.length());
								String value ="";
								if(!getDimSearch(this.dimTabCellMaps.get(key)
										.getDimTypeNo(),this.getSearchParams())){
									if(this.dimTabCellMaps.get(key)
										.getDimTypeNo().equals("ORG")){
										value = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
									}
								}
								else{
									for (Map<String, Object> searchArg : this.getSearchParams()) {
										String dimNo = String.valueOf(searchArg.get("DimNo"));
										if(dimNo.equals(this.dimTabCellMaps.get(key)
												.getDimTypeNo())){
											this.getSearchParams().remove(searchArg);
											if(searchArg.get("Value") instanceof String){
												value = searchArg.get("Value").toString();
											}
											else{
												List<Object> values = (List<Object>) searchArg.get("Value");
												if(values!=null && values.size()>0){
													for(Object val : values){
														if(StringUtils.isNotBlank(val.toString())){
															value += val.toString() +",";
														}
													}
												}
												value = StringUtils.substring(value, 0,value.length()-1);
											}
											break;
										}
									}
								}
								search=new HashMap<String, Object>();
								search.put("DimNo", this.dimTabCellMaps.get(key)
										.getDimTypeNo());
								search.put("Op", "IN");
								search.put("Value", "getChildDimVal('"+this.dimTabCellMaps.get(key)
										.getDimTypeNo()+"','"+value+"','"+flag+"','"+lvl+"')");
								if(this.dimTabCellMaps.get(key)
										.getDimTypeNo().equals("ORG")){
									orgLvlFlag = true;
								}
								this.rowSearchArgs.add(search);
							}
						}
					}
					dimNos.add(dim);
				} else {
					dimNos.add(this.dimTabCellMaps.get(key).getDimTypeNo());
				}
				int index = 0;
				if (templateType.equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V)) {
					index = ExcelLetterIntTrans.lettrToCell(key.getCellNo())
							.get("col");
				}
				if (templateType.equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)) {
					index = ExcelLetterIntTrans.lettrToCell(key.getCellNo())
							.get("row");
				}
				dimOrderMap.put(index, this.dimTabCellMaps.get(key)
						.getDimTypeNo());
			}
			for (RptDesignCellInfoPK key : this.dimTabCellMaps.keySet()) {
				if (this.dimTabCellMaps.get(key).getIsTotal()
						.equals(GlobalConstants4plugin.COMMON_BOOLEAN_YES))
					sum.add(getSumInfo(key.getCellNo(), dimOrderMap));
			}

		}
		if (idxTabCellMaps != null && idxTabCellMaps.size() > 0) {
			for (RptDesignCellInfoPK key : this.idxTabCellMaps.keySet()) {
				RptDesignSrcIdxTabVO vo = idxTabCellMaps.get(key);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ColumNo", key.getCellNo());
				params.put("IndexNo", vo.getIndexNo());
				params.put("SearchArg", this.searchArgs);
				if (StringUtils.isNotBlank(vo.getSortMode()))
					params.put("SortType", vo.getSortMode());
				columnMaps.add(params);
			}
		}

		for (Map<String, Object> map : this.getSearchParams()) {
			if ("ORG".equals(map.get("DimNo"))){
				this.orgDimFlag = true;
			}
			if (map.get("Value") instanceof String) {
				List<Object> value = new ArrayList<Object>();
				value.add(map.get("Value"));
				map.put("Value", value);
			} else
				map.put("Value", map.get("Value"));
			this.searchArgs.add(map);
		}
	}

	@SuppressWarnings("unchecked")
	private int getUniqueDim(String dimNo, List<Object> dimNos) {
		int i = 0;
		for (Object obj : dimNos) {
			if (obj instanceof String) {
				if (dimNo.equals(obj)) {
					i++;
				}
			} else {
				Map<String, Object> map = (Map<String, Object>) obj;
				if (dimNo.equals(map.get("DimNo"))) {
					i++;
				}
			}
		}
		return i;
	}

	private Map<String, Object> getSumInfo(String key,
			Map<Integer, String> dimOrderMap) {
		Map<String, Object> sum = new HashMap<String, Object>();
		int index = 0;
		if (templateType.equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V)) {
			index = ExcelLetterIntTrans.lettrToCell(key).get("col");
		}
		if (templateType.equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)) {
			index = ExcelLetterIntTrans.lettrToCell(key).get("row");
		}
		boolean sumFlag = true;
		List<String> dimTypeNos = new ArrayList<String>();
		if (dimOrderMap != null && dimOrderMap.size() > 0) {
			for (int i : dimOrderMap.keySet()) {
				if (i < index) {
					sumFlag = false;
					dimTypeNos.add(dimOrderMap.get(i));
				}
			}
		}
		if (sumFlag) {
			sum.put("Type", GlobalConstants4plugin.SUM_TYPE_TOTAL);
		} else {
			sum.put("Type", GlobalConstants4plugin.SUM_TYPE_SUB);
			sum.put("DimNo", dimTypeNos);
		}
		return sum;
	}
}
