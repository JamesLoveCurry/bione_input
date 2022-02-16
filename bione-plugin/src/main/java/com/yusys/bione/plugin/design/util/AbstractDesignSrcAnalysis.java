package com.yusys.bione.plugin.design.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.util.excel.WorkbookWrapper;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfoPK;
import com.yusys.bione.plugin.design.entity.RptDesignComcellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.web.vo.CellInfo;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDimTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDsVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcFormulaVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcTextVO;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import com.yusys.bione.plugin.rptsys.service.RptSysVarAnalysisBS;
import com.yusys.bione.plugin.spreadjs.entity.Data;
import com.yusys.bione.plugin.spreadjs.entity.DataTable;
import com.yusys.bione.plugin.spreadjs.entity.DataTableProperty;
import com.yusys.bione.plugin.spreadjs.entity.DataTablePropertyProperty;
import com.yusys.bione.plugin.spreadjs.entity.Gridline;
import com.yusys.bione.plugin.spreadjs.entity.SheetSize;
import com.yusys.bione.plugin.spreadjs.entity.SheetsProperty;
import com.yusys.bione.plugin.spreadjs.entity.Span;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;
import com.yusys.bione.plugin.spreadjs.entity.Style;

/**
 * <pre>
 * Title: 模板查询抽象类
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author weijx weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public abstract class AbstractDesignSrcAnalysis {
	private static Logger logger = LoggerFactory
			.getLogger(AbstractDesignSrcAnalysis.class);
	private String searchJson = "";
	private String fileName = "";
	private SheetsProperty sheetP = new SheetsProperty();
	private DataTable newDataTable = new DataTable();
	private List<CellInfo> cellInfos = new ArrayList<CellInfo>();
	private LinkedHashMap<String, LinkedHashMap<String, List<Object>>> cellDsValues;
	private LinkedHashMap<String, Object> cellIdxValues;
	private LinkedHashMap<String, List<Map<String, Object>>> tabIdxValues;
	private List<Map<String, Object>> tabIdxLists;
	private String returnJson = "";
	private String idxSelectParams = "";
	private String dsSelectParams = "";
	private String idxDetialSelectParams = "";
	private String dsId = "";
	private SpreadSchema schema = null;
	private DataTable dataTable = null;
	private Data data = null;
	private String busiType = "";
	protected RptDesignTmpInfo tmp;
	private String unit;
	private long total;
	private String addr = "";
	private String fileNm = "";
	protected int extIndex = 0;
	protected Map<String, RptDesignCellInfo> cellMaps = new HashMap<String, RptDesignCellInfo>();
	protected Map<String, RptDesignComcellInfo> comcellMaps = new HashMap<String, RptDesignComcellInfo>();
	protected Map<RptDesignCellInfoPK, RptDesignSrcIdxVO> idxCellMaps = new HashMap<RptDesignCellInfoPK, RptDesignSrcIdxVO>();
	protected Map<RptDesignCellInfoPK, RptDesignSrcDsVO> dsCellMaps = new HashMap<RptDesignCellInfoPK, RptDesignSrcDsVO>();
	protected Map<RptDesignCellInfoPK, RptDesignSrcFormulaVO> formulaCellMaps = new HashMap<RptDesignCellInfoPK, RptDesignSrcFormulaVO>();
	protected Map<RptDesignCellInfoPK, RptDesignSrcTextVO> textCellMaps = new HashMap<RptDesignCellInfoPK, RptDesignSrcTextVO>();
	protected Map<RptDesignCellInfoPK, RptDesignSrcIdxTabVO> idxTabCellMaps = new HashMap<RptDesignCellInfoPK, RptDesignSrcIdxTabVO>();
	protected Map<RptDesignCellInfoPK, RptDesignSrcDimTabVO> dimTabCellMaps = new LinkedHashMap<RptDesignCellInfoPK, RptDesignSrcDimTabVO>();
	protected List<RptDesignSrcDimTabVO> dimTabCellList = new ArrayList<RptDesignSrcDimTabVO>();
	protected List<RptDesignSrcDsVO> dsCellList = new ArrayList<RptDesignSrcDsVO>();
	
	protected String fCellNo = "";
	private boolean adjColRowFlag = false;
	private Map<String, Object> sumResult = new HashMap<String, Object>();
	
	private Map<String, Span> spanMap = new HashMap<String, Span>();
	private List<Map<String, String>> spanCells = new ArrayList<Map<String, String>>();
	private List<Map<String, Object>> searchParams;
	private List<String> pricellNos = new ArrayList<String>();
	private Map<String, Map<String, Object>> params = new HashMap<String, Map<String, Object>>();
	private Map<String, String> validates = new HashMap<String, String>();
	private Map<Integer, Integer> subArea = new HashMap<Integer, Integer>();
	private boolean isExtend = false;
	protected boolean isDownLoad = false;
	protected boolean isCache = false;
	protected String srcSysCode = "";
	private String templateUnit = "";
	private String isUpt = "";
	private String dataDate = "";
	protected boolean isInit = true;
	private Map<String, Map<String, Object>> cellExtInfos = new HashMap<String, Map<String, Object>>();
	private Map<String, List<Object>> keyValues = new HashMap<String, List<Object>>();
	private Map<String, Map<String, Object>> dsCellInfos = new HashMap<String, Map<String, Object>>();
	private Map<String, Object> idxValueMap = new HashMap<String, Object>();
	private List<Map<String, Object>> rowDimList = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> colDimList = new ArrayList<Map<String, Object>>();
	private String crossIdxlNo = "";
	protected Map<String, String> dimAlias = new HashMap<String, String>();
	private Map<String,Map<String,Object>> idxTabValMaps = Maps.newHashMap();
	private List<String> dimTabLists = new ArrayList<String>();
	public AbstractDesignSrcAnalysis(SpreadSchema scheme, RptDesignTmpInfo tmp,
			List<RptDesignCellInfo> cells, List<RptDesignSrcIdxVO> idxCells,
			List<RptDesignSrcDsVO> dsCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells,
			List<Map<String, Object>> searchParams, String dataDate,String unit,
			String busiType, List<String> cellNos,
			Map<String, String> validates, String fileName, boolean isExtend,
			Boolean isDownLoad, Boolean isInit,Boolean isCache) {
		this.setTmp(tmp);
		this.schema = scheme;
		this.setSearchParams(searchParams);
		this.isExtend = isExtend;
		this.validates = validates;
		this.pricellNos = cellNos;
		this.busiType = busiType;
		this.fileName = fileName;
		this.isInit = isInit;
		this.isDownLoad = (isDownLoad != null) ? isDownLoad : false;
		this.templateUnit = tmp.getTemplateUnit();
		this.isUpt = tmp.getIsUpt();
		this.unit = unit;
		this.isCache = isCache;
		if (dsCells != null && dsCells.size() > 0)
			this.dsId = dsCells.get(0).getDsId();
		this.setDataDate(dataDate);
		this.dimTabCellList = dimTabCells;
		this.dsCellList = dsCells;
		createCellInfoMap(cells, idxCells, dsCells, formulaCells, textCells,
				idxTabCells, dimTabCells);
		createSelectJson();
		createSchema();
	}
	
	public Map<String, Object> getDataInfo() {
		Map<String, Object> result = new HashMap<String, Object>();
		if (schema != null && dataTable != null) {
			if (returnJson.equals("error")) {
				result.put("error", "报表信息查询异常！");
			} else {
				String error = analysisInfo();
				if (error.equals("")) {
					result.put("cellInfo", cellExtInfos);
					result.put("gridInfo", setGridInfo());
					result.put("isAutoAdj", tmp.getIsAutoAdj());
				} else if (error.equals("download")) {
					Gridline gd = new Gridline();
					gd.setShowHorizontalGridline(false);
					gd.setShowVerticalGridline(false);
					this.sheetP.setGridline(gd);
					this.sheetP.setActiveCol(null);
					this.sheetP.setActiveRow(null);
					this.sheetP.setAllowCellOverflow(true);
					this.sheetP.setSelections(null);
					// result.put("json", JsonObjTrans.ObjectToJson(schema));
					// result.put("cellInfo", cellExtInfos);
					// result.put("dsInfo", dsCellInfos);
					result.put("total", total);
					result.put("error", error);
					// result.put("ds", fileNm);
					result.put("fileName", fileNm);
					result.put("addr", addr);
				} else if (error.equals("noinit")) {
					result.put("error", error);
					Map<String, List<String>> formulas = new HashMap<String, List<String>>();
					if (this.formulaCellMaps != null
							&& this.formulaCellMaps.size() > 0) {
						for (RptDesignCellInfoPK id : formulaCellMaps.keySet()) {
							formulas.put(id.getCellNo(),
									getFormulaInfo(formulaCellMaps.get(id)));
						}
					}
					result.put("templateType", tmp.getTemplateType());
					result.put("searchjson", this.searchJson);
					result.put("formula", formulas);
					result.put("cellDsInfo", this.cellDsValues);
					result.put("templateUnit", templateUnit);//

				} else
					result.put("error", error);
			}
		} else {
			result.put("error", "所选报表的模板信息配置异常！");
		}
		return result;
	}
	
	private Map<String,Object> setGridInfo(){
		DataTable dt = this.newDataTable;
		Map<String,Object> info = new HashMap<String, Object>();
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		Map<String,DataTableProperty> rows = dt.getAdditionalProperties();
		List<Map<String,Object>> columns = new ArrayList<Map<String,Object>>();
		int maxcol = 0;
		List<SheetSize> cols = this.sheetP.getColumns();
		List<SheetSize> rs = this.sheetP.getRows();
		List<Style> styleSet = new ArrayList<Style>(this.schema.getNamedStyles());
		Map<String,Style> ostyleMap = new HashMap<String, Style>();
		for(Style s : styleSet){
			ostyleMap.put(s.getName(),s );
		}
		Map<String,Object> styleMap = new HashMap<String, Object>();
		
		if(rows!= null && rows.size()>0){
			Set<String> rowMap = rows.keySet();
			int[] rowArr = new int[rowMap.size()];
			int i = 0;
			for(String rowKey : rowMap){
				rowArr[i] = NumberUtils.toInt(rowKey);
				i++;
			}
			int maxrow = NumberUtils.max(rowArr);
			for(int x=0;x<=maxrow;x++){
				data.add(new HashMap<String, Object>());
			}
			int j = 0;
			for(String rowk : rows.keySet()){
				Map<String, Object> dtp = rows.get(rowk).getAdditionalProperties();
				Map<String,Object> row =data.get(NumberUtils.toInt(rowk));
				if(dtp != null && dtp.size() > 0){
					for(String colKey : dtp.keySet()){
						int col = NumberUtils.toInt(colKey);
						if(col > maxcol){
							maxcol = col;
						}
						Object obj = dtp.get(colKey);
						if(obj != null && obj instanceof DataTablePropertyProperty){
							DataTablePropertyProperty pro = (DataTablePropertyProperty)obj;
							if(StringUtils.isNotBlank(pro.getFormula())){
								row.put(colKey, "公式错误");
								row.put(colKey+"_style", pro.getStyle());
								if(pro.getStyle()!=null){
									Style st = ostyleMap.get(pro.getStyle().toString());
									styleMap.put(pro.getStyle().toString(), ostyleMap.get(pro.getStyle().toString()));
									while(StringUtils.isNotBlank(st.getParentName())){
										st = ostyleMap.get(st.getParentName());
										styleMap.put(pro.getStyle().toString(), ostyleMap.get(st.getParentName()));
									}
								}
								else{
									row.put(colKey+"_style", pro.getStyle());
								}
								
							}
							else{
								row.put(colKey, pro.getValue());
								row.put(colKey+"_style", pro.getStyle());
								if(pro.getStyle()!=null){
									Style st = ostyleMap.get(pro.getStyle().toString());
									styleMap.put(pro.getStyle().toString(), ostyleMap.get(pro.getStyle().toString()));
									while(StringUtils.isNotBlank(st.getParentName())){
										st = ostyleMap.get(st.getParentName());
										styleMap.put(pro.getStyle().toString(), ostyleMap.get(st.getParentName()));
									}
								}
								else{
									row.put(colKey+"_style", pro.getStyle());
								}
							}
						}
						else{
							row.put(colKey.toString(), obj);
						}
					}
				}
				Object obj= null; 
				if(j<rs.size()){
					obj= rs.get(j); 
				}
				if(obj!= null && obj instanceof SheetSize){
					SheetSize size =(SheetSize) obj;
					row.put("rowHeight", size.getSize());
				}
				else{
					row.put("rowHeight", obj);
				}
				j++;
			}
		}
		
		for(int i=0;i<=maxcol;i++){
			Map<String,Object> column = new HashMap<String, Object>();
			StringBuilder letter = new StringBuilder(""); 
			ExcelLetterIntTrans.intToLetter((i+1), letter);
			column.put("name", String.valueOf(i));
			column.put("display", letter.toString());
			Object col = null;
			if(i<cols.size())
				col = cols.get(i);
			if(col!=null && col instanceof SheetSize){
				SheetSize colSize = (SheetSize) col;
				column.put("width", colSize.getSize());
			}
			else{
				if(col!=null)
					column.put("width", col);
			}
			columns.add(column);
		}
		
		Map<String,Object> frozen = new HashMap<String, Object>();
		frozen.put("col", this.sheetP.getFrozenColCount());
		frozen.put("row", this.sheetP.getFrozenRowCount());
		Map<String,Object> visable = new HashMap<String, Object>();
		List<Integer> colVs = new ArrayList<Integer>();
		List<Integer> rowVs = new ArrayList<Integer>();
		for(int i=0;i<cols.size();i++){
			Object col = cols.get(i);
			if(col!=null && col instanceof SheetSize){
				SheetSize colSize = (SheetSize) col;
				if(colSize.getVisible() !=null && colSize.getVisible() == false){
					colVs.add(i);
				}
			}
		}
		for(int i=0;i<rs.size();i++){
			Object r = rs.get(i);
			if(r!=null && r instanceof SheetSize){
				SheetSize rowSize = (SheetSize) r;
				if(rowSize.getVisible() !=null && rowSize.getVisible() == false){
					rowVs.add(i);
				}
			}
		}
		
		
		visable.put("col", colVs);
		visable.put("row", rowVs);
		info.put("columns", columns);
		info.put("data", data);
		info.put("span", this.sheetP.getSpans());
		info.put("style",styleMap);
		info.put("frozen",frozen);
		info.put("visable",visable);
		return info;
	}
	public Map<String, Object> analysisSourceInfo() {
		Map<String, Object> result = new HashMap<String, Object>();
		if (schema != null && dataTable != null) {
			if (returnJson.equals("error")) {
				result.put("error", "报表信息查询异常！");
			} else {
				String error = analysisInfo();
				if (error.equals("")) {
					Gridline gd = new Gridline();
					gd.setShowHorizontalGridline(false);
					gd.setShowVerticalGridline(false);
					this.sheetP.setGridline(gd);
					this.sheetP.setActiveCol(null);
					this.sheetP.setActiveRow(null);
					this.sheetP.setAllowCellOverflow(true);
					this.sheetP.setSelections(null);
					this.sheetP.setFrozenLineShowFlag(false);
					result.put("searchjson", this.searchJson);
					result.put("json", JSON.toJSONString(schema, SerializerFeature.DisableCircularReferenceDetect));
					result.put("cellInfo", cellExtInfos);
					result.put("dsInfo", dsCellInfos);
					result.put("templateType", tmp.getTemplateType());
					result.put("total", total);
					result.put("isAutoAdj", tmp.getIsAutoAdj());//
					result.put("templateUnit", templateUnit);//
				} else if (error.equals("download")) {
					Gridline gd = new Gridline();
					gd.setShowHorizontalGridline(false);
					gd.setShowVerticalGridline(false);
					this.sheetP.setGridline(gd);
					this.sheetP.setActiveCol(null);
					this.sheetP.setActiveRow(null);
					this.sheetP.setAllowCellOverflow(true);
					this.sheetP.setSelections(null);
					// result.put("json", JsonObjTrans.ObjectToJson(schema));
					// result.put("cellInfo", cellExtInfos);
					// result.put("dsInfo", dsCellInfos);
					result.put("total", total);
					result.put("error", error);
					// result.put("ds", fileNm);
					result.put("fileName", fileNm);
					result.put("addr", addr);
				} else if (error.equals("noinit")) {
					result.put("error", error);
					Map<String, List<String>> formulas = new HashMap<String, List<String>>();
					if (this.formulaCellMaps != null
							&& this.formulaCellMaps.size() > 0) {
						for (RptDesignCellInfoPK id : formulaCellMaps.keySet()) {
							formulas.put(id.getCellNo(),
									getFormulaInfo(formulaCellMaps.get(id)));
						}
					}
					result.put("templateType", tmp.getTemplateType());
					result.put("searchjson", this.searchJson);
					result.put("formula", formulas);
					result.put("cellDsInfo", this.cellDsValues);
					result.put("templateUnit", templateUnit);//

				} else
					result.put("error", error);
			}
		} else {
			result.put("error", "所选报表的模板信息配置异常！");
		}
		return result;
	}

	private void createCellInfoMap(List<RptDesignCellInfo> cells,
			List<RptDesignSrcIdxVO> idxCells, List<RptDesignSrcDsVO> dsCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells) {
		if (cells != null && cells.size() > 0) {
			for (RptDesignCellInfo cell : cells) {
				this.cellMaps.put(cell.getRowId().intValue() + ","
						+ cell.getColId().intValue(), cell);
			}
		}
		if (idxCells != null && idxCells.size() > 0) {
			for (RptDesignSrcIdxVO idxCell : idxCells) {
				this.idxCellMaps.put(idxCell.getId(), idxCell);
			}
		}
		if (dsCells != null && dsCells.size() > 0) {
			for (RptDesignSrcDsVO dsCell : dsCells) {
				this.dsCellMaps.put(dsCell.getId(), dsCell);
			}
		}
		if (formulaCells != null && formulaCells.size() > 0) {
			for (RptDesignSrcFormulaVO formulaCell : formulaCells) {
				this.formulaCellMaps.put(formulaCell.getId(), formulaCell);
			}
		}
		if (textCells != null && textCells.size() > 0) {
			for (RptDesignSrcTextVO textCell : textCells) {
				this.textCellMaps.put(textCell.getId(), textCell);
			}
		}
		if (idxTabCells != null && idxTabCells.size() > 0) {
			for (RptDesignSrcIdxTabVO idxTabCell : idxTabCells) {
				this.idxTabCellMaps.put(idxTabCell.getId(), idxTabCell);
			}
		}
		if (dimTabCells != null && dimTabCells.size() > 0) {
			for (RptDesignSrcDimTabVO dimTabCell : dimTabCells) {
				this.dimTabCellMaps.put(dimTabCell.getId(), dimTabCell);
			}
		}
	}

	public AbstractDesignSrcAnalysis(SpreadSchema scheme,
			Map<String, Map<String, Object>> cellExtInfos) {
		this.schema = scheme;
		this.cellExtInfos = cellExtInfos;
	}

	public Map<String, Object> dealExtendInfo() {
		Map<String, Object> result = new HashMap<String, Object>();
		createSchema();
		dealInfo();
		result.put("json", JSON.toJSONString(schema, SerializerFeature.DisableCircularReferenceDetect));

		return result;
	}

	protected List<String> creatDimNos(List<Map<String, Object>> searchArgs) {
		List<String> dimNos = new ArrayList<String>();
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		dimNos.add(propertiesUtils.getProperty("dateDimTypeNo"));
		/*for (Map<String, Object> searchArg : searchArgs) {
			if(!searchArg.get("DimNo").toString().equals("tempUnit"))
				dimNos.add(searchArg.get("DimNo").toString());
		}*/
		return dimNos;
	}

	protected List<Map<String, Object>> setOrgValidate(
			List<Map<String, Object>> searchArgs) {
		List<Map<String, Object>> searchs = new ArrayList<Map<String, Object>>();
		
		RptSysVarAnalysisBS rptSysBS = SpringContextHolder
				.getBean("rptSysVarAnalysisBS");
		RptOrgInfoBS rptOrgBS = SpringContextHolder.getBean("rptOrgInfoBS");

		if (tmp.getTemplateType().equals("01")) {
			Map<String, List<String>> validateMap = rptOrgBS
					.getValidateOrgInfo();
			List<String> validateOrg = validateMap.get("validate");
			List<String> enEms = rptSysBS.getEnNmByDsId("ORG", dsId);
			if (enEms != null && enEms.size() > 0) {
				for (String enEm : enEms) {
					if (!getDimSearch(enEm, searchArgs)) {
						//增加!=null的判断,因为从管驾访问来的是null,然后报错了! xch【2017-02-14】
						if (BioneSecurityUtils.getCurrentUserInfo()!=null && !BioneSecurityUtils.getCurrentUserInfo()
								.isSuperUser()) {
							Map<String, Object> search = new HashMap<String, Object>();
							search.put("DimNo", enEm);
							search.put("Op", "IN");
							search.put("Value", validateOrg);
							searchs.add(search);
						}
					}
				}
			}
		} else {
			List<String> validateOrg = new ArrayList<String>();
			//增加!=null的判断,因为从管驾访问来的是null,然后报错了! xch【2017-02-14】
			if (BioneSecurityUtils.getCurrentUserInfo()!=null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				Map<String, List<String>> validateMap = rptOrgBS
						.getValidateOrgInfo();
				validateOrg = validateMap.get("validate");
				if (!getDimSearch("ORG", searchArgs)) {
					Map<String, Object> search = new HashMap<String, Object>();
					search.put("DimNo", "ORG");
					search.put("Op", "IN");
					search.put("Value", validateOrg);
					searchs.add(search);
				}
			} 
		}
		
		return searchs;
	}

	public List<String> getValidateOrgs(){
		RptOrgInfoBS rptOrgBS = SpringContextHolder.getBean("rptOrgInfoBS");
		List<String> validateOrg = new ArrayList<String>();
		if (BioneSecurityUtils.getCurrentUserInfo()!= null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			Map<String, List<String>> validateMap = rptOrgBS
					.getValidateOrgInfo();
			validateOrg = validateMap.get("validate");
		} 
		return validateOrg;
	}
	
	@SuppressWarnings("unchecked")
	protected boolean getDimSearch(String dimNo,
			List<Map<String, Object>> searchArgs) {
		for (Map<String, Object> searchArg : searchArgs) {
			String key = String.valueOf(searchArg.get("DimNo"));
			if (dimNo.equals(key)) {
				if (searchArg.get("Value") instanceof String) {
					if (StringUtils.isNotBlank(String.valueOf(searchArg
							.get("Value")))) {
						return true;
					}
					return false;
				} else {
					List<Object> values = (List<Object>) searchArg.get("Value");
					if (values != null && values.size() > 0) {
						for (Object value : values) {
							if (StringUtils.isNotBlank(value.toString())) {
								return true;
							}
						}
						return false;
					}
				}

			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getDimSearchInfo(String dimNo,
			List<Map<String, Object>> searchArgs) {
		for (Map<String, Object> searchArg : searchArgs) {
			String key = String.valueOf(searchArg.get("DimNo"));
			if (dimNo.equals(key)) {
				if (searchArg.get("Value") instanceof String) {
					if (StringUtils.isNotBlank(String.valueOf(searchArg
							.get("Value")))) {
						return searchArg;
					}
					return null;
				} else {
					List<Object> values = (List<Object>) searchArg.get("Value");
					if (values != null && values.size() > 0) {
						for (Object value : values) {
							if (StringUtils.isNotBlank(value.toString())) {
								return searchArg;
							}
						}
						return null;
					}
				}

			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void createParam() {
		RptSysVarAnalysisBS rptSysBS = SpringContextHolder
				.getBean("rptSysVarAnalysisBS");
		if (!"".equals(dataDate)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isDim", true);
			map.put("dimTypeNo",
					GlobalConstants4plugin.RPT_INNER_SYS_VAR_DATE.toUpperCase());
			List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("op", "=");
			data.put("value", dataDate);
			params.add(data);
			map.put("data", params);
			this.params.put(
					GlobalConstants4plugin.RPT_INNER_SYS_VAR_DATE.toUpperCase(), map);
		}
		for (Map<String, Object> param : this.searchParams) {
			String key = String.valueOf(param.get("DimNo"));
			String dimTypeNo = "";
			boolean isDim = true;
			if (!"".equals(dsId)) {
				dimTypeNo = rptSysBS.getDimInfoByDsId(key, dsId);
				if (!StringUtils.isNotBlank(dimTypeNo)) {
					isDim = false;
				}
			} else {
				dimTypeNo = key;
			}
			key = key.toUpperCase();
			if (key.equals("DATE")) {
				if (!"".equals(dataDate))
					continue;
			}
			String Op = String.valueOf(param.get("Op"));
			Object Value = param.get("Value");
			if (this.params.get(key) != null) {
				List<Map<String, Object>> datas = (List<Map<String, Object>>) this.params
						.get(key).get("data");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("op", Op);
				map.put("value", Value);
				datas.add(map);
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("isDim", isDim);
				map.put("dimTypeNo", dimTypeNo);
				List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("op", Op);
				data.put("value", Value);
				params.add(data);
				map.put("data", params);
				this.params.put(key, map);
			}
		}
	}

	private boolean getCellNoInfo(int row, int cell) {
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cell + 1, cellNmBf);
		String cellNo = cellNmBf.toString() + (row + 1);
		if (this.cellExtInfos.get(cellNo) != null) {
			return true;
		} else {
			return false;
		}
	}

	private void dealInfo() {
		Map<String, DataTableProperty> dtps = dataTable
				.getAdditionalProperties();
		for (String rowIndex : dtps.keySet()) {
			Map<String, Object> dtpps = dtps.get(rowIndex)
					.getAdditionalProperties();
			for (String colIndex : dtpps.keySet()) {
				DataTablePropertyProperty pro = (DataTablePropertyProperty) dtpps
						.get("colIndex");
				if (getCellNoInfo(NumberUtils.toInt(rowIndex),
						NumberUtils.toInt(colIndex))) {
					setProperty(rowIndex, colIndex, pro);
				}
			}
		}
		setDataTable(newDataTable);
	}

	private RptDesignCellInfo getCell(int rowNum, int cellNum) {
		return this.cellMaps.get(rowNum + "," + cellNum);
	}

	private RptDesignSrcDsVO getDsInfo(RptDesignCellInfo info) {
		return this.dsCellMaps.get(info.getId());
	}

	private RptDesignSrcIdxVO getIdxInfo(RptDesignCellInfo info) {
		return this.idxCellMaps.get(info.getId());
	}

	private RptDesignSrcFormulaVO getFormulaInfo(RptDesignCellInfo info) {
		return this.formulaCellMaps.get(info.getId());
	}

	public RptDesignSrcTextVO getTextInfo(RptDesignCellInfo info) {
		return this.textCellMaps.get(info.getId());
	}

	private RptDesignSrcDimTabVO getDimTabInfo(RptDesignCellInfo info) {
		return this.dimTabCellMaps.get(info.getId());
	}

	private RptDesignSrcIdxTabVO getIdxTabInfo(RptDesignCellInfo info) {
		return this.idxTabCellMaps.get(info.getId());
	}

	@SuppressWarnings("unchecked")
	private String getIdxJson() {
		Object obj = null;
		try {
			// System.out.println(time);
			obj = CommandRemote.sendSync(idxSelectParams,
					CommandRemoteType.QUERY);
			this.searchJson = idxSelectParams;
		} catch (Throwable e) {
			logger.debug("查询引擎连接超时");
			return "查询引擎连接超时";
		}
		if (obj instanceof String)
			returnJson = obj.toString();
		else {
			logger.debug("查询引擎异常");
			return "查询引擎异常，";
		}

		String error = "";
		try {
			JSONObject jsonMap = JSON.parseObject(returnJson);
			String result = jsonMap.get("Code").toString();
			if ("0000".equals(result)) {
				this.cellIdxValues = ((LinkedHashMap<String, Object>) jsonMap
						.get("Msg"));
			} else {
				logger.debug(jsonMap.get("Msg").toString());
				return "查询引擎异常";
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return "查询引擎异常";
		}
		return error;
	}

	
	private void setIdxTabVal(List<String> dims,Map<String, Object> tabIdxList,String rtype){
		String keyDim = "";
		Map<String,Object> idxValMap = Maps.newHashMap();
		for (RptDesignCellInfoPK id : this.idxTabCellMaps
				.keySet()) {
			String key = id.getCellNo();
			idxValMap.put(key, tabIdxList.get(key));
		}
		keyDim = getKeyDim(dims, tabIdxList);
		this.idxTabValMaps.put(keyDim, idxValMap);
	}
	
	private String getKeyDim(List<String> dims,Map<String, Object> tabIdxList){
		String keyDim = "";
		for(int i = 0; i < dims.size() ; i++){
			if(tabIdxList.get(dims.get(i))==null){
				keyDim += "" + "|";
			}
			else{
				if(!tabIdxList.get("$"+dims.get(i)).toString().equals(""))
					keyDim += tabIdxList.get("$"+dims.get(i))+"|";
				else{
					keyDim += "-"+"|";
				}
			}
		}
		return keyDim;
	}
	
	private int dealTabDataInfo(List<Map<String, Object>> tabIdxLists){
		int index = 0 ;
		tabIdxValues = new LinkedHashMap<String, List<Map<String, Object>>>();
		List<String> dims = new ArrayList<String>(); 
		List<String> cols = new ArrayList<String>(); 
		for(int k = 0 ; k < this.dimTabCellList.size();k++){
			String dimTypeNo = this.dimAlias.get(dimTabCellList
					.get(k).getId().getCellNo());
			if (dimTypeNo == null)
				dimTypeNo = dimTabCellList.get(k)
						.getDimTypeNo();
			cols.add(dimTabCellList
					.get(k).getId().getCellNo());
			dims.add(dimTypeNo);
		}
		Map<String,Map<String,Object>> keyDims = Maps.newHashMap();
		if (tabIdxLists != null && tabIdxLists.size() > 0) {
			int t=0;
			for (;t<tabIdxLists.size();t++) {
				Map<String,Object> tabIdxList = tabIdxLists.get(t);
				if(tabIdxList.get("rtype").equals(GlobalConstants4plugin.IDXTAB_TOTAL)){
					setIdxTabVal(dims, tabIdxList,GlobalConstants4plugin.IDXTAB_TOTAL);
				}
				else if(tabIdxList.get("rtype").equals(GlobalConstants4plugin.IDXTAB_SUBTOTAL)){
					setIdxTabVal(dims, tabIdxList,GlobalConstants4plugin.IDXTAB_SUBTOTAL);
				}
				else{
					setIdxTabVal(dims, tabIdxList,GlobalConstants4plugin.IDXTAB_DETAILS);
					for(int k = this.dimTabCellList.size()-1;k>=0;k--){
						String keyDim = "";
						String keyVal = "";
						String key = dimTabCellList
								.get(k).getId().getCellNo();
						String dimTypeNo = this.dimAlias.get(dimTabCellList
								.get(k).getId().getCellNo());
						if (dimTypeNo == null)
							dimTypeNo = dimTabCellList.get(k)
									.getDimTypeNo();
						
						for(int i = 0; i < k ; i++){
							if(StringUtils.isNotBlank(tabIdxList.get("$"+dims.get(i)).toString()))
								keyDim += tabIdxList.get("$"+dims.get(i))+"|";
							else
								keyDim += "-"+"|";
							if(StringUtils.isNotBlank(tabIdxList.get(dims.get(i)).toString()))
								keyVal += tabIdxList.get(dims.get(i))+"|";
							else
								keyVal += "-"+"|";
						}
						if(keyDims.get(key)==null){
							Map<String,Object> keyMap = new HashMap<String, Object>();
							keyMap.put("keyDim", keyDim);
							keyMap.put("keyVal", keyVal);
							keyMap.put("sRow", index);
							keyDims.put(key, keyMap);
						}
						else{
							if(!keyDims.get(key).get("keyDim").equals(keyDim)){
								Map<String, String> spanCell = new HashMap<String, String>();
								if(dimTabCellList
										.get(k).getIsTotal().equals("Y")){
									String dimKAr[] =StringUtils.split(keyDims.get(key).get("keyDim").toString(),"|");
									String dimVAr[] =StringUtils.split(keyDims.get(key).get("keyVal").toString(),"|");
									Map<String, Object> tinf = new HashMap<String, Object>();
									tinf.put("srcValue",
											"");
									String tkeyDims = "";
									for(int h = 0;h<k;h++){
										tkeyDims += dimKAr[h] +"|";
									}
									for(int f = k ;f < dims.size() ; f++){
										tkeyDims += "" +"|";
									}
									this.dimTabLists.add(tkeyDims);
									if(k==0){
										tinf.put("value", "合计");
										tinf.put("totalType", GlobalConstants4plugin.IDXTAB_TOTAL);
									}
									else{
										tinf.put("value", "小计");
										tinf.put("totalType", GlobalConstants4plugin.IDXTAB_SUBTOTAL);
									}
									tabIdxValues.get(key).add(tinf);
									for(int h = 0; h < k ; h++){
										Map<String, Object> oinf = new HashMap<String, Object>();
										oinf.put("srcValue",
												 dimKAr[h]);
										oinf.put("value", dimVAr[h]);
										oinf.put("totalType", GlobalConstants4plugin.IDXTAB_SUBTOTAL);
										tabIdxValues.get(cols.get(h)).add(oinf);
									}
									for(int f = k+1; f < cols.size() ; f++){
										int sRow = Integer.parseInt(keyDims.get(cols.get(f)).get("sRow").toString())+1;
										keyDims.get(cols.get(f)).put("sRow", sRow);
										Map<String, Object> oinf = new HashMap<String, Object>();
										oinf.put("srcValue",
												 "");
										if(k==0){
											oinf.put("value", "合计");
											oinf.put("totalType", GlobalConstants4plugin.IDXTAB_TOTAL);
										}
										else{
											oinf.put("value", "小计");
											oinf.put("totalType", GlobalConstants4plugin.IDXTAB_SUBTOTAL);
										}
										tabIdxValues.get(cols.get(f)).add(oinf);
									}
									index++;
									Map<String, String> tspanCell = new HashMap<String, String>();
									boolean tspanFlag = false;
									if (tmp.getTemplateType().equals("04")) {
										tspanCell.put("startCell",
												ExcelLetterIntTrans.addRow(key,index-1));
										int sCol = ExcelLetterIntTrans
												.lettrToCell(key).get("col");
										tspanCell.put("endCell",
												ExcelLetterIntTrans.addRow(key,index-1));
										tspanCell.put("type",
												GlobalConstants4plugin.EXT_DIRECTION_H);
										for(int h = k+1;h<cols.size();h++){
											int cCol = ExcelLetterIntTrans
													.lettrToCell(cols.get(h)).get("col");
											if(cCol -sCol > 1){
												tspanFlag = true;
												spanCells.add(tspanCell);
											}
											else{
												sCol = cCol;
												tspanCell.put("endCell",
														ExcelLetterIntTrans.addRow(cols.get(h),index-1));
											}
										}
										
									}
									if (tmp.getTemplateType().equals("05")) {
										tspanCell.put("startCell",
												ExcelLetterIntTrans.addCol(cols.get(0),index-1));
										tspanCell.put("endCell",
												ExcelLetterIntTrans.addCol(cols.get(0),index-1));
										tspanCell.put("type",
												GlobalConstants4plugin.EXT_DIRECTION_V);
										int sRow = ExcelLetterIntTrans
												.lettrToCell(key).get("row");
										for(int h = k+1;h<cols.size();h++){
											int cRow = ExcelLetterIntTrans
													.lettrToCell(cols.get(h)).get("row");
											if(cRow -sRow > 1){
												tspanFlag = true;
												spanCells.add(tspanCell);
											}
											else{
												sRow = cRow;
												tspanCell.put("endCell",
														ExcelLetterIntTrans.addCol(cols.get(h),index-1));
											}
										}
									}
									if(!tspanFlag){
										spanCells.add(tspanCell);
									}
									
								}
								if(k>0){
									if (tmp.getTemplateType().equals("04")) {
										spanCell.put("startCell",
												ExcelLetterIntTrans.addRow(cols.get(k-1),(Integer)keyDims.get(key).get("sRow")));
										spanCell.put("endCell",
												ExcelLetterIntTrans.addRow(cols.get(k-1),
														index-1));
										spanCell.put("type",
												GlobalConstants4plugin.EXT_DIRECTION_V);
									}
									if (tmp.getTemplateType().equals("05")) {
										spanCell.put("startCell",
												ExcelLetterIntTrans.addCol(cols.get(k-1),(Integer)keyDims.get(key).get("sRow")));
										spanCell.put("endCell",
												ExcelLetterIntTrans.addCol(cols.get(k-1),
														index-1));
										spanCell.put("type",
												GlobalConstants4plugin.EXT_DIRECTION_H);
									}
									this.spanCells.add(spanCell);
								}
								Map<String,Object> keyMap = new HashMap<String, Object>();
								keyMap.put("keyDim", keyDim);
								keyMap.put("keyVal", keyVal);
								keyMap.put("sRow", index);
								keyDims.put(key, keyMap);
							}
						}
					}
					for(int k = 0;k<this.dimTabCellList.size();k++){
						String key = dimTabCellList
								.get(k).getId().getCellNo();
						String dimTypeNo = this.dimAlias.get(dimTabCellList
								.get(k).getId().getCellNo());
						if (dimTypeNo == null)
							dimTypeNo = dimTabCellList.get(k)
									.getDimTypeNo();
						Map<String, Object> inf = new HashMap<String, Object>();
						if(StringUtils.isNotBlank(tabIdxList.get("$" + dimTypeNo).toString()))
							inf.put("srcValue",
									tabIdxList.get("$" + dimTypeNo));
						else
							inf.put("srcValue",
									"");
						if(StringUtils.isNotBlank(tabIdxList.get(dimTypeNo).toString()))
							inf.put("value", tabIdxList.get(dimTypeNo));
						else
							inf.put("value", "-");
						inf.put("totalType", tabIdxList.get("rtype"));
						if (tabIdxValues.get(key) == null) {
							List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
							list.add(inf);
							tabIdxValues.put(key, list);
						} else {
							tabIdxValues.get(key).add(inf);
						}
						
					}
					this.dimTabLists.add(getKeyDim(dims, tabIdxList));
					index++;
				}
			}
			for(int k = this.dimTabCellList.size()-1;k>=0;k--){
				String key = dimTabCellList
						.get(k).getId().getCellNo();
				Map<String, String> spanCell = new HashMap<String, String>();
				
				if(dimTabCellList
						.get(k).getIsTotal().equals("Y")){
					String dimKAr[] =StringUtils.split(keyDims.get(key).get("keyDim").toString(),"|");
					String dimVAr[] =StringUtils.split(keyDims.get(key).get("keyVal").toString(),"|");
					
					Map<String, Object> tinf = new HashMap<String, Object>();
					tinf.put("srcValue",
							"");
					String tkeyDims = "";
					for(int h = 0;h<k;h++){
						tkeyDims += dimKAr[h] +"|";
					}
					for(int f = k ;f < dims.size() ; f++){
						tkeyDims += "" +"|";
					}
					this.dimTabLists.add(tkeyDims);
					if(k==0){
						tinf.put("value", "合计");
						tinf.put("totalType", GlobalConstants4plugin.IDXTAB_TOTAL);
					}
					else{
						tinf.put("value", "小计");
						tinf.put("totalType", GlobalConstants4plugin.IDXTAB_SUBTOTAL);
					}
					tabIdxValues.get(key).add(tinf);
					
					for(int h = 0; h < k ; h++){
						Map<String, Object> oinf = new HashMap<String, Object>();
						oinf.put("srcValue",
								 dimKAr[h]);
						oinf.put("value", dimVAr[h]);
						oinf.put("totalType", GlobalConstants4plugin.IDXTAB_SUBTOTAL);
						tabIdxValues.get(cols.get(h)).add(oinf);
					}
					for(int f = k+1; f < cols.size() ; f++){
						Map<String, Object> oinf = new HashMap<String, Object>();
						oinf.put("srcValue",
								 "");
						if(k==0){
							oinf.put("value", "合计");
							oinf.put("totalType", GlobalConstants4plugin.IDXTAB_TOTAL);
						}
						else{
							oinf.put("value", "小计");
							oinf.put("totalType", GlobalConstants4plugin.IDXTAB_SUBTOTAL);
						}
						tabIdxValues.get(cols.get(f)).add(oinf);
					}
					index++;
					Map<String, String> tspanCell = new HashMap<String, String>();
					boolean tspanFlag = false;
					if (tmp.getTemplateType().equals("04")) {
						tspanCell.put("startCell",
								ExcelLetterIntTrans.addRow(key,index-1));
						int sCol = ExcelLetterIntTrans
								.lettrToCell(key).get("col");
						tspanCell.put("endCell",
								ExcelLetterIntTrans.addRow(key,index-1));
						tspanCell.put("type",
								GlobalConstants4plugin.EXT_DIRECTION_H);
						for(int h = k+1;h<cols.size();h++){
							int cCol = ExcelLetterIntTrans
									.lettrToCell(cols.get(h)).get("col");
							if(cCol -sCol > 1){
								tspanFlag = true;
								spanCells.add(tspanCell);
							}
							else{
								sCol = cCol;
								tspanCell.put("endCell",
										ExcelLetterIntTrans.addRow(cols.get(h),index-1));
							}
						}
						
					}
					if (tmp.getTemplateType().equals("05")) {
						tspanCell.put("startCell",
								ExcelLetterIntTrans.addCol(cols.get(0),index-1));
						tspanCell.put("endCell",
								ExcelLetterIntTrans.addCol(cols.get(0),index-1));
						tspanCell.put("type",
								GlobalConstants4plugin.EXT_DIRECTION_V);
						int sRow = ExcelLetterIntTrans
								.lettrToCell(key).get("row");
						for(int h = k+1;h<cols.size();h++){
							int cRow = ExcelLetterIntTrans
									.lettrToCell(cols.get(h)).get("row");
							if(cRow -sRow > 1){
								tspanFlag = true;
								spanCells.add(tspanCell);
							}
							else{
								sRow = cRow;
								tspanCell.put("endCell",
										ExcelLetterIntTrans.addCol(cols.get(h),index-1));
							}
						}
					}
					if(!tspanFlag){
						spanCells.add(tspanCell);
					}
				}
				if(k>0){
					if (tmp.getTemplateType().equals("04")) {
						spanCell.put("startCell",
								ExcelLetterIntTrans.addRow(cols.get(k-1),(Integer)keyDims.get(key).get("sRow")));
						spanCell.put("endCell",
								ExcelLetterIntTrans.addRow(cols.get(k-1),
										index-1));
						spanCell.put("type",
								GlobalConstants4plugin.EXT_DIRECTION_V);
					}
					if (tmp.getTemplateType().equals("05")) {
						spanCell.put("startCell",
								ExcelLetterIntTrans.addCol(cols.get(k-1),(Integer)keyDims.get(key).get("sRow")));
						spanCell.put("endCell",
								ExcelLetterIntTrans.addCol(cols.get(k-1),
										index-1));
						spanCell.put("type",
								GlobalConstants4plugin.EXT_DIRECTION_H);
					}
					this.spanCells.add(spanCell);
				}
			}
		}
		return index;
	}

	
	@SuppressWarnings("unchecked")
	private String getIdxTabJson() {
		Object obj = null;
		String error = "";
		try {
			// System.out.println(time);
			logger.debug(idxDetialSelectParams);
			System.out.println();
			obj = CommandRemote.sendSync(idxDetialSelectParams,
					CommandRemoteType.QUERY);
		} catch (Throwable e) {
			logger.debug("查询引擎连接超时");
			return "查询引擎连接超时";
		}
		if (obj instanceof String)
			returnJson = obj.toString();
		else {
			logger.debug("查询引擎异常");
			return "查询引擎异常";
		}

		try {
			logger.debug(returnJson);
			JSONObject jsonMap = JSON.parseObject(returnJson);
			String result = jsonMap.get("Code").toString();
			if ("0000".equals(result)) {
				tabIdxValues = new LinkedHashMap<String, List<Map<String, Object>>>();
				this.tabIdxLists = (List<Map<String, Object>>) jsonMap
						.get("Msg");
				int exIndex = 0;
				if (tabIdxLists != null && tabIdxLists.size() > 0) {
					exIndex = this.dealTabDataInfo(tabIdxLists);
					if (tmp.getTemplateType().equals(
							GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V)
							&& this.sheetP.getRows().size() >= exIndex) {
						List<SheetSize> rows1 = new ArrayList<SheetSize>();
						List<SheetSize> rows2 = new ArrayList<SheetSize>();
						for (int h = 0; h < exIndex; h++) {
							rows1.add(this.sheetP.getRows().get(h));
						}
						for (int h = exIndex; h < this.sheetP.getRows().size(); h++) {
							rows2.add(this.sheetP.getRows().get(h));
						}

						if (tabIdxLists != null && tabIdxLists.size() > 0) {
							for (int i = 0; i < tabIdxLists.size(); i++) {
								rows1.add(this.sheetP.getRows()
										.get(exIndex - 1));
							}
						}
						if (rows2 != null && rows2.size() > 0)
							rows1.addAll(rows2);
						this.sheetP.setRows(rows1);
					}
					if (tmp.getTemplateType().equals(
							GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)
							&& this.sheetP.getColumns().size() >= exIndex) {
						List<SheetSize> cols1 = new ArrayList<SheetSize>();
						List<SheetSize> cols2 = new ArrayList<SheetSize>();
						for (int h = 0; h < exIndex; h++) {
							cols1.add(this.sheetP.getColumns().get(h));
						}
						for (int h = exIndex; h < this.sheetP.getRows().size(); h++) {
							cols2.add(this.sheetP.getColumns().get(h));
						}

						if (tabIdxLists != null && tabIdxLists.size() > 0) {
							for (int i = 0; i < tabIdxLists.size(); i++) {
								cols1.add(this.sheetP.getColumns().get(
										exIndex - 1));
							}
						}
						if (cols2 != null && cols2.size() > 0)
							cols1.addAll(cols2);
						this.sheetP.setColumns(cols1);
					}
				}
				extIndex = exIndex;
			} else {
				logger.debug(jsonMap.get("Msg").toString());
				return "查询引擎异常";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "查询引擎异常";
		}
		return error;
	}

	@SuppressWarnings("unchecked")
	private String getCrossJson() {
		Object obj = null;
		String error = "";
		try {
			// System.out.println(time);
			this.searchJson = idxDetialSelectParams;
			obj = CommandRemote.sendSync(idxDetialSelectParams,
					CommandRemoteType.QUERY);
		} catch (Throwable e) {
			logger.debug("查询引擎连接超时");
			return "查询引擎连接超时";
		}
		if (obj instanceof String)
			returnJson = obj.toString();
		else {
			logger.debug("查询引擎异常");
			return "查询引擎异常";
		}

		try {
			JSONObject jsonMap = JSON.parseObject(returnJson);
			String result = jsonMap.get("Code").toString();
			if ("0000".equals(result)) {
				tabIdxValues = new LinkedHashMap<String, List<Map<String, Object>>>();
				this.tabIdxLists = (List<Map<String, Object>>) jsonMap
						.get("Msg");
				List<String> rowDims = new ArrayList<String>(); // 行扩展维度
				List<String> colDims = new ArrayList<String>(); // 列扩展维度
				for (RptDesignCellInfoPK id : this.dimTabCellMaps.keySet()) {
					String dimTypeNo = this.dimAlias.get(dimTabCellMaps.get(id)
							.getId().getCellNo());
					if (dimTypeNo == null)
						dimTypeNo = dimTabCellMaps.get(id).getDimTypeNo();
					if (this.dimTabCellMaps.get(id).getExtDirection()
							.equals(GlobalConstants4plugin.EXT_DIRECTION_V)) {
						rowDims.add(dimTypeNo);
					}
					if (this.dimTabCellMaps.get(id).getExtDirection()
							.equals(GlobalConstants4plugin.EXT_DIRECTION_H)) {
						colDims.add(dimTypeNo);
					}
				}
				for (RptDesignCellInfoPK id : this.idxTabCellMaps.keySet()) {
					crossIdxlNo = id.getCellNo();
				}

				List<String> rowDimInfoList = new ArrayList<String>();
				List<String> colDimInfoList = new ArrayList<String>();

				for (Map<String, Object> tabIdxList : tabIdxLists) {
					String rowInfo = "";
					Map<String, Object> rowmaps = new HashMap<String, Object>();
					Map<String, Object> colmaps = new HashMap<String, Object>();
					for (String rowDim : rowDims) {
						rowInfo += tabIdxList.get(rowDim).toString() + " ";
						rowmaps.put(rowDim, tabIdxList.get(rowDim));
						rowmaps.put("$" + rowDim, tabIdxList.get("$" + rowDim));
					}
					if (!rowDimInfoList.contains(rowInfo)) {
						rowmaps.put("$info$", rowInfo);
						rowDimList.add(rowmaps);
						rowDimInfoList.add(rowInfo);
					}
					String colInfo = "";
					for (String colDim : colDims) {
						colInfo += tabIdxList.get(colDim).toString() + " ";
						colmaps.put(colDim, tabIdxList.get(colDim));
						colmaps.put("$" + colDim, tabIdxList.get("$" + colDim));
					}
					if (!colDimInfoList.contains(colInfo)) {
						colmaps.put("$info$", colInfo);
						colDimList.add(colmaps);
						colDimInfoList.add(colInfo);
					}
					this.idxValueMap.put(rowInfo + colInfo,
							tabIdxList.get(crossIdxlNo));
				}
			} else {
				logger.debug(jsonMap.get("Msg").toString());
				return "查询引擎异常";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "查询引擎异常";
		}
		return error;
	}

	@SuppressWarnings("unchecked")
	private String getDsJson() {
		Object obj = null;
		try {
			obj = CommandRemote.sendSync(dsSelectParams,
					CommandRemoteType.QUERY);
			this.searchJson = dsSelectParams;
		} catch (Throwable e) {
			return "连接查询引擎超时异常";
		}
		if (obj instanceof String)
			returnJson = obj.toString();
		else
			return "查询引擎异常";
		String error = "";
		JSONObject jsonMap = JSON.parseObject(returnJson);
		String result = jsonMap.get("Code").toString();
		if ("0000".equals(result)) {
			if (isDownLoad) {
				this.cellDsValues = ((LinkedHashMap<String, LinkedHashMap<String, List<Object>>>) jsonMap
						.get("Msg"));
				LinkedHashMap<String, List<Object>> cellInfo = cellDsValues
						.get(cellDsValues.keySet().toArray()[0]);
				List<Object> keys = (List<Object>) cellInfo.get("FileName");
				this.fileNm = keys.get(0).toString();
				keys = (List<Object>) cellInfo.get("Addr");
				this.addr = keys.get(0).toString();
				keys = (List<Object>) cellInfo.get("Total");
				this.total = Long.parseLong(keys.get(0).toString());
				this.cellDsValues = new LinkedHashMap<String, LinkedHashMap<String, List<Object>>>();
				return "download";
			}
			if (!isInit) {
				this.cellDsValues = ((LinkedHashMap<String, LinkedHashMap<String, List<Object>>>) jsonMap
						.get("Msg"));
				for (String key : cellDsValues.keySet()) {
					LinkedHashMap<String, List<Object>> cellInfo = cellDsValues
							.get(key);
					for (String info : cellInfo.keySet()) {
						if (!info.equals("Total") && !info.equals("Key")) {
							RptDesignCellInfoPK id = new RptDesignCellInfoPK();
							id.setTemplateId(tmp.getId().getTemplateId());
							id.setVerId(tmp.getId().getVerId());
							id.setCellNo(info);
							RptDesignSrcDsVO dsvo = this.dsCellMaps.get(id);
							if (dsvo.getDisplayFormat().equals(
									GlobalConstants4plugin.CELL_DISPLAY_FORMAT_ORG)
									|| dsvo.getDisplayFormat()
											.equals(GlobalConstants4plugin.CELL_DISPLAY_FORMAT_COUNT)) {
								String unit = dsvo.getDataUnit();
								if (!StringUtils.isNotBlank(unit)) {
									unit = templateUnit;
								}
								if(StringUtils.isNotBlank(this.unit)&&!this.unit.equals("00")){
									unit = this.unit;
								}
								List<Object> values = new ArrayList<Object>();
								for (Object value : cellInfo.get(info)) {
									if (value != null
											&& StringUtils.isNotBlank(unit)) {
										value = addDiv(value.toString(), unit);
										// cellInfo.put(info, value);
									}
									values.add(value);
								}
								cellInfo.put(info, values);
							}
						}

					}
				}
				return "noinit";
			}
			this.cellDsValues = ((LinkedHashMap<String, LinkedHashMap<String, List<Object>>>) jsonMap
					.get("Msg"));
			for (String key : cellDsValues.keySet()) {
				this.dsId = key;
				LinkedHashMap<String, List<Object>> cellInfo = cellDsValues
						.get(key);
				if (cellInfo != null && cellInfo.size() > 0) {
					for (String info : cellInfo.keySet()) {
						if ("Total".equals(info)) {
							List<Object> keys = (List<Object>) cellInfo
									.get("Total");
							this.total = Long.parseLong(keys.get(0).toString());
						}
						if ("sumResult".equals(info)) {
							List<Object> sumResult = cellInfo.get("sumResult");
							if(sumResult != null && sumResult.size()>0){
								this.sumResult = (Map<String, Object>) sumResult.get(0);
							}
						}
					}
				}
				if (total < extIndex) {
					extIndex = (int) total;
				}
				List<Object> keys = (List<Object>) cellInfo.get("Key");
				keyValues.put(key, keys);
			}
		} else {
			logger.debug(jsonMap.get("Msg").toString());
			return "查询引擎异常";
		}
		return error;
	}

	protected abstract void createSelectJson();

	private void createSchema() {
		if (schema != null) {
			Map<String, SheetsProperty> sheetsTable = schema.getSheetsProperties();
			Object[] key = sheetsTable.keySet().toArray();
			data = sheetsTable.get(key[0]).getData();
			this.sheetP = sheetsTable.get(key[0]);
			if (data != null) {
				dataTable = data.getDataTable();
			}
		}
	}

	private void createRegion() {
		List<Span> spans = this.sheetP.getSpans();
		List<Span> newSpans = new ArrayList<Span>();
		if (spans != null && spans.size() > 0) {
			for (Span span : spans) {
				getSpanInfo(span);
			}
		}
		dealSpanMap(spanMap);
		if (spanMap != null) {
			for (String key : spanMap.keySet()) {
				newSpans.add(spanMap.get(key));
			}
		}
		this.sheetP.setSpans(newSpans);
	}

	private List<Span> getSpanInfo(Span span) {
		List<Span> spans = new ArrayList<Span>();
		int row = span.getRow() + 1;
		int cell = span.getCol() + 1;
		StringBuilder cNm = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cell, cNm);
		String cName = cNm.append(row).toString();
		for (CellInfo cellInfo : cellInfos) {
			if (cellInfo.getONm().equals(cName)) {
				int colCount = span.getColCount();
				int rowCount = span.getRowCount();
				if (cellInfo.getExtDirection() != null
						&& !cellInfo.getExtDirection().equals("")) {
					if (cellInfo.getExtDirection().equals(
							GlobalConstants4plugin.EXT_DIRECTION_H)) {
						for (int i = cellInfo.getRowStart(); i <= cellInfo
								.getRowEnd(); i++) {
							Span spanNew = new Span();
							spanNew.setColCount(colCount);
							spanNew.setRowCount(rowCount);
							spanNew.setCol(cellInfo.getCellStart());
							spanNew.setRow(i);
							StringBuilder HNm = new StringBuilder("");
							ExcelLetterIntTrans.intToLetter(
									cellInfo.getCellStart() + 1, HNm);
							String HName = HNm.append(i + 1).toString();
							spanMap.put(HName, span);
							spans.add(spanNew);
						}
					} else if (cellInfo.getExtDirection().equals(
							GlobalConstants4plugin.EXT_DIRECTION_V)) {
						for (int i = cellInfo.getCellStart(); i <= cellInfo
								.getCellEnd(); i++) {
							Span spanNew = new Span();
							spanNew.setColCount(colCount);
							spanNew.setRowCount(rowCount);
							spanNew.setRow(cellInfo.getRowStart());
							spanNew.setCol(i);
							StringBuilder VNm = new StringBuilder("");
							ExcelLetterIntTrans.intToLetter(i + 1, VNm);
							String VName = VNm.append(
									cellInfo.getRowStart() + 1).toString();
							spanMap.put(VName, span);
							spans.add(spanNew);
						}
					}
				} else {
					Span spanNew = new Span();
					spanNew.setColCount(colCount);
					spanNew.setRowCount(rowCount);
					spanNew.setRow(cellInfo.getRowStart());
					spanNew.setCol(cellInfo.getCellStart());
					StringBuilder Nm = new StringBuilder("");
					ExcelLetterIntTrans.intToLetter(
							cellInfo.getCellStart() + 1, Nm);
					String Name = Nm.append(cellInfo.getRowStart() + 1)
							.toString();
					spanMap.put(Name, span);
					spans.add(spanNew);
				}

			}
		}
		return spans;
	}

	private void dealSpanMap(Map<String, Span> spanMap) {
		if (spanCells != null && spanCells.size() > 0) {
			for (Map<String, String> map : spanCells) {
				Span span = new Span();
				String startCell = map.get("startCell");
				String endCell = map.get("endCell");
				String type = map.get("type");
				if (type.equals(GlobalConstants4plugin.EXT_DIRECTION_H)) {
					Map<String, Integer> sCell = ExcelLetterIntTrans
							.lettrToCell(startCell);
					span.setRow(sCell.get("row") - 1);
					span.setCol(sCell.get("col") - 1);
					Span oSpan = spanMap.get(startCell);
					if (oSpan != null) {
						span.setRowCount(oSpan.getRowCount());
					} else {
						span.setRowCount(1);
					}
					Map<String, Integer> eCell = ExcelLetterIntTrans
							.lettrToCell(endCell);
					oSpan = spanMap.get(endCell);
					if (oSpan != null) {
						span.setColCount(oSpan.getCol() + span.getColCount()
								- span.getCol());
					} else {
						span.setColCount(eCell.get("col") - span.getCol());
					}
				}
				if (type.equals(GlobalConstants4plugin.EXT_DIRECTION_V)) {
					Map<String, Integer> sCell = ExcelLetterIntTrans
							.lettrToCell(startCell);
					span.setRow(sCell.get("row") - 1);
					span.setCol(sCell.get("col") - 1);
					Span oSpan = spanMap.get(startCell);
					if (oSpan != null) {
						span.setColCount(oSpan.getColCount());
					} else {
						span.setColCount(1);
					}
					Map<String, Integer> eCell = ExcelLetterIntTrans
							.lettrToCell(endCell);
					oSpan = spanMap.get(endCell);
					if (oSpan != null) {
						span.setRowCount(oSpan.getRow() + span.getRowCount()
								- span.getRow());
					} else {
						span.setRowCount(eCell.get("row") - span.getRow());
					}
				}
				spanMap.put(startCell, span);
			}
		}
	}

	private void setRowInfo(int rowCount) {
		if (rowCount > (this.sheetP.getRowCount() == null ? 0 : this.sheetP
				.getRowCount())) {
			this.sheetP.setRowCount(rowCount);
			this.sheetP.getData().setRowCount(rowCount);
		}
	}

	private void setColInfo(int colCount) {
		if (colCount > (this.sheetP.getColumnCount() == null ? 0 : this.sheetP
				.getColumnCount())) {
			this.sheetP.setColumnCount(colCount);
			this.sheetP.getData().setColCount(colCount);
		}
	}

	protected void setDataTable(DataTable dataTable) {
		if (schema != null) {
			Map<String, SheetsProperty> sheetsTable = schema.getSheetsProperties();
			Object[] key = sheetsTable.keySet().toArray();
			data = sheetsTable.get(key[0]).getData();
			data.setDataTable(dataTable);
		}
	}

	private String checkRegion(int rowNum, int cellNum) {
		String result = "";
		if (!tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CELL)) {
			if (cellInfos != null && cellInfos.size() > 0) {
				for (CellInfo cellInfo : cellInfos) {
					String info = cellInfo.checkInfo(rowNum, cellNum);
					if (!info.equals("")) {
						if (info.equals("pass")) {
							return "pass";
						} else {
							String nums[] = StringUtils.split(info, ",");
							rowNum = NumberUtils.toInt(nums[0]);
							cellNum = NumberUtils.toInt(nums[1]);
							result = info;
						}
					}
				}
			}
		}
		return result;
	}

	private void setCellMap(Workbook workbook) {
		Map<String, DataTableProperty> dtps = dataTable
				.getAdditionalProperties();

		for (String rowIndex : dtps.keySet()) {
			Map<String, Object> dtpps = dtps.get(rowIndex)
					.getAdditionalProperties();
			for (String colIndex : dtpps.keySet()) {
				Object obj = dtpps.get(colIndex);
				RptDesignCellInfo cell = getCell(NumberUtils.toInt(rowIndex),
						NumberUtils.toInt(colIndex));
				String result = "";
				if (tmp.getTemplateType().equals(
						GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL)
						|| tmp.getTemplateType().equals(
								GlobalConstants4plugin.RPT_TMP_TYPE_COM)
						|| tmp.getTemplateType().equals(
								GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V)
						|| tmp.getTemplateType().equals(
								GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)
						|| tmp.getTemplateType().equals(
								GlobalConstants4plugin.RPT_TMP_TYPE_CROSS)) {
					result = checkRegion(NumberUtils.toInt(rowIndex),
							NumberUtils.toInt(colIndex));
				}
				if (cell == null) {
					
					dealResult(result, NumberUtils.toInt(rowIndex),
							NumberUtils.toInt(colIndex), obj);
				} else {
					if (cell.getCellDataSrc().equals(
							GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE)
							&& (tmp.getTemplateType().equals(
									GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL) || tmp
									.getTemplateType().equals(
											GlobalConstants4plugin.RPT_TMP_TYPE_COM))) {
						RptDesignSrcDsVO dsvo = getDsInfo(cell);
						if (dsvo != null) {
							dealResult(dsvo, result, workbook,
									NumberUtils.toInt(rowIndex),
									NumberUtils.toInt(colIndex), obj);
						}
					}
					if ((cell.getCellDataSrc().equals(
							GlobalConstants4plugin.RPT_CELL_SOURCE_IDX) || cell
							.getCellDataSrc().equals(
									GlobalConstants4plugin.RPT_CELL_SOURCE_RPTCALC))
							&& (tmp.getTemplateType().equals(
									GlobalConstants4plugin.RPT_TMP_TYPE_CELL) || tmp
									.getTemplateType().equals(
											GlobalConstants4plugin.RPT_TMP_TYPE_COM))) {
						RptDesignSrcIdxVO idxvo = getIdxInfo(cell);
						if (idxvo != null) {
							dealResult(idxvo, result, workbook,
									NumberUtils.toInt(rowIndex),
									NumberUtils.toInt(colIndex), obj);
						}
					}
					if (cell.getCellDataSrc().equals(
							GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA)) {
						RptDesignSrcFormulaVO formulavo = getFormulaInfo(cell);
						if (formulavo != null) {
							dealResult(formulavo, result,
									NumberUtils.toInt(rowIndex),
									NumberUtils.toInt(colIndex), obj);
						}
					}
					if (cell.getCellDataSrc().equals(
							GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT)) {
						RptDesignSrcTextVO textvo = getTextInfo(cell);
						if (textvo != null) {
							dealResult(textvo, result,
									NumberUtils.toInt(rowIndex),
									NumberUtils.toInt(colIndex), obj);
						}
					}
					if (cell.getCellDataSrc().equals(
							GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB)) {
						RptDesignSrcDimTabVO dimTabvo = getDimTabInfo(cell);
						if (dimTabvo != null) {
							dealResult(dimTabvo, result, workbook,
									NumberUtils.toInt(rowIndex),
									NumberUtils.toInt(colIndex), obj);
						}
					}
					if (cell.getCellDataSrc().equals(
							GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB)) {
						RptDesignSrcIdxTabVO idxTabvo = getIdxTabInfo(cell);
						if (idxTabvo != null) {
							dealResult(idxTabvo, result, workbook,
									NumberUtils.toInt(rowIndex),
									NumberUtils.toInt(colIndex), obj);
						}
					}
				}

			}
		}
	}

	private void adjRowCol(int start,int length,String type){
		if(!adjColRowFlag){
			if(type.equals("col"))
			{
				List<SheetSize> columns = this.sheetP.getColumns();
				if(start < columns.size()){
					for(int i =1 ;i < length; i++){
						columns.add(start,columns.get(start));
					}
				}
			}
			else if(type.equals("row"))
			{
				List<SheetSize> rows = this.sheetP.getRows();
				if(start < rows.size()){
					for(int i =1 ;i < length; i++){
						rows.add(start,rows.get(start));
					}
				}
			}
			adjColRowFlag = true;
		}
	}

	private void addDsInfo(Map<String, Object> ext) {
		Map<String, Object> exts = new HashMap<String, Object>();
		exts.putAll(ext);
		dsCellInfos.put(exts.get("cellNo").toString(), exts);
	}

	// private void addExtInfo(int row, int cell, Map<String, Object> ext,Object
	// value) {
	// StringBuilder cellNmBf = new StringBuilder("");
	// ExcelLetterIntTrans.intToLetter(cell + 1, cellNmBf);
	// String key = cellNmBf.toString() + (row + 1);
	// Map<String,Object> exts=new HashMap<String, Object>();
	// exts.put("cellNo", ext.get("cellNo"));
	// exts.put("value", value);
	// cellExtInfos.put(key, exts);
	// addDsInfo(ext);
	// }

	private Object addMun(String value, String unit) {
		BigDecimal val = new BigDecimal(0);
		if (StringUtils.isNotBlank(value)) {
			try {
				val = new BigDecimal(value != null ? value : "0");
			} catch (Exception e) {
				return value;
			}
		} else
			return val;
		try {
			if (unit != null) {

				if (unit.equals("01")) { // 元
					return val;
				} else if (unit.equals("02")) { // 百
					return val.multiply(new BigDecimal(100));
				} else if (unit.equals("03")) { // 千
					return val.multiply(new BigDecimal(1000));
				} else if (unit.equals("04")) { // 万
					return val.multiply(new BigDecimal(10000));
				} else if (unit.equals("05")) { // 亿
					return val.multiply(new BigDecimal(100000000));
				} else {
					return val;
				}
			}
		} catch (Exception e) {
			return val;
		}
		return val;

	}

	private Object addDiv(String value, String unit) {
		BigDecimal val = new BigDecimal(0);
		if (StringUtils.isNotBlank(value)) {
			try {
				val = new BigDecimal(value != null ? value : "0");
			} catch (Exception e) {
				return value;
			}
		} else
			return val;
		try {
			if (unit != null) {
				if (unit.equals("01")) { // 元
					return val;
				} else if (unit.equals("02")) { // 百
					return val.divide(new BigDecimal(100));
				} else if (unit.equals("03")) { // 千
					return val.divide(new BigDecimal(1000));
				} else if (unit.equals("04")) { // 万
					return val.divide(new BigDecimal(10000));
				} else if (unit.equals("05")) { // 亿
					return val.divide(new BigDecimal(100000000));
				} else {
					return val;
				}
			}
		} catch (Exception e) {
			return val;
		}
		return val;

	}

	private void addExtInfo(int row, int cell, Map<String, Object> ext,
			String dsId, int i, Object value, Object oldValue) {
		Map<String, Map<String, Object>> extinfo = new HashMap<String, Map<String, Object>>();
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cell + 1, cellNmBf);
		String key = cellNmBf.toString() + (row + 1);
		Map<String, Object> exts = new HashMap<String, Object>();
		exts.put("cellNo", ext.get("cellNo"));
		if (isExtend) {
			exts.put("key", this.keyValues.get(dsId) != null ? this.keyValues
					.get(dsId).get(i) : null);
		}
		if (value != null && ext.get("unit") != null
				&& !"".equals(ext.get("unit")) && !"00".equals(ext.get("unit"))) {
			exts.put("value",
					addMun(value.toString(), ext.get("unit").toString())
							.toString());
		} else
			exts.put("value", value);
		exts.put("oldValue", oldValue);
		extinfo.put(key, exts);
		cellExtInfos.put(key, exts);
	}

	private void addExtInfo(int row, int cell, Map<String, Object> ext,
			String indexNo, String dsId, Object value, Object oldValue) {
		// RptIdxDsRelBS rptIdxBS =
		// SpringContextHolder.getBean("rptIdxDsRelBS");
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cell + 1, cellNmBf);
		String key = cellNmBf.toString() + (row + 1);
		Map<String, Object> exts = new HashMap<String, Object>();
		exts.put("cellNo", ext.get("cellNo"));
		exts.put("value", value);
		exts.put("dsId", dsId);
		exts.put("oldValue", oldValue);
		exts.put("indexNo", indexNo);
		cellExtInfos.put(key, exts);
	}

	private void addExtInfo(int row, int cell, Map<String, Object> ext,
			Object value, Object oldValue, String dimTypeNo, Object srcValue) {
		// RptIdxDsRelBS rptIdxBS =
		// SpringContextHolder.getBean("rptIdxDsRelBS");
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cell + 1, cellNmBf);
		String key = cellNmBf.toString() + (row + 1);
		Map<String, Object> exts = new HashMap<String, Object>();
		exts.put("cellNo", ext.get("cellNo"));
		exts.put("value", value);
		exts.put("dsId", dsId);
		exts.put("oldValue", oldValue);
		exts.put("srcValue", srcValue);
		exts.put("dimTypeNo", dimTypeNo);
		cellExtInfos.put(key, exts);
	}

	private void addExtInfo(int row, int cell, Map<String, Object> ext,
			String indexNo, String dsId, Object value) {
		// RptIdxDsRelBS rptIdxBS =
		// SpringContextHolder.getBean("rptIdxDsRelBS");
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cell + 1, cellNmBf);
		String key = cellNmBf.toString() + (row + 1);
		Map<String, Object> exts = new HashMap<String, Object>();
		exts.put("cellNo", ext.get("cellNo"));
		exts.put("value", value);
		exts.put("indexNo", indexNo);
		ext.put("dsId", dsId);
		cellExtInfos.put(key, exts);
		addDsInfo(ext);
	}

	private void setCommonCellPro(CellInfo cellInfo) {
		String rowNum = String.valueOf(cellInfo.getRowStart());
		String cellNum = String.valueOf(cellInfo.getCellStart());
		setProperty(rowNum, cellNum, cellInfo.getDataTablePropertyProperty());
		this.setColInfo(NumberUtils.toInt(cellNum) + 2);
		this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
	}

	private void setTextCellPro(CellInfo cellInfo) {
		String rowNum = String.valueOf(cellInfo.getRowStart());
		String cellNum = String.valueOf(cellInfo.getCellStart());
		setProperty(rowNum, cellNum, cellInfo.getDataTablePropertyProperty());
		this.setColInfo(NumberUtils.toInt(cellNum) + 2);
		this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
		// addExtInfo(cellInfo.getRowStart(), cellInfo.getCellStart(),
		// cellInfo.getExtInfo(),cellInfo.getDataTablePropertyProperty().getValue());
	}

	private void setIdxCellPro(CellInfo cellInfo) {
		String rowNum = String.valueOf(cellInfo.getRowStart());
		String cellNum = String.valueOf(cellInfo.getCellStart());
		setProperty(rowNum, cellNum, cellInfo.getDataTablePropertyProperty());
		addExtInfo(cellInfo.getRowStart(), cellInfo.getCellStart(),
				cellInfo.getExtInfo(), cellInfo.getIndexNo(),
				cellInfo.getDsId(), cellInfo.getDataTablePropertyProperty()
						.getValue());
		this.setColInfo(NumberUtils.toInt(cellNum) + 2);
		this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
	}

	private void setDsCellPro(CellInfo cellInfo) {
		String rowNum = String.valueOf(cellInfo.getRowStart());
		String cellNum = String.valueOf(cellInfo.getCellStart());
		this.addDsInfo(cellInfo.getExtInfo());
		if (!cellInfo.getIsExt()) {
			DataTablePropertyProperty pro = cellInfo
					.getDataTablePropertyProperty();
			setProperty(rowNum, cellNum, pro);
			this.setColInfo(NumberUtils.toInt(cellNum) + 2);
			this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
			addExtInfo(cellInfo.getRowStart(), cellInfo.getCellStart(),
					cellInfo.getExtInfo(), cellInfo.getDsId(), 0,
					pro.getValue(), cellInfo.getOldValues(0));
		} else {
			if (isDownLoad) {
				for (int i = 0; i < cellInfo.getLength(); i++) {
					DataTablePropertyProperty pro = cellInfo
							.getDataTablePropertyProperty();
					setProperty(rowNum, cellNum, pro);
					this.setColInfo(NumberUtils.toInt(cellNum) + 2);
					this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
					addExtInfo(NumberUtils.toInt(rowNum),
							NumberUtils.toInt(cellNum), cellInfo.getExtInfo(),
							cellInfo.getDsId(), i, null, null);
					if (cellInfo.getExtDirection().equals(
							GlobalConstants4plugin.EXT_DIRECTION_H)) {
						cellNum = String
								.valueOf(NumberUtils.toInt(cellNum) + 1);
					}
					if (cellInfo.getExtDirection().equals(
							GlobalConstants4plugin.EXT_DIRECTION_V)) {
						rowNum = String.valueOf(NumberUtils.toInt(rowNum) + 1);
					}
				}
			} else {
				int i = 0;
				adjRowCol(Integer.parseInt(rowNum), cellInfo
						.getDataTablePropertyPropertys().size(), "row");
				for (DataTablePropertyProperty pro : cellInfo
						.getDataTablePropertyPropertys()) {
					setProperty(rowNum, cellNum, pro);
					this.setColInfo(NumberUtils.toInt(cellNum) + 2);
					this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
					addExtInfo(NumberUtils.toInt(rowNum),
							NumberUtils.toInt(cellNum), cellInfo.getExtInfo(),
							cellInfo.getDsId(), i, pro.getValue(),
							cellInfo.getOldValues(i));
					if (cellInfo.getExtDirection().equals(
							GlobalConstants4plugin.EXT_DIRECTION_H)) {
						cellNum = String
								.valueOf(NumberUtils.toInt(cellNum) + 1);
					}
					if (cellInfo.getExtDirection().equals(
							GlobalConstants4plugin.EXT_DIRECTION_V)) {
						rowNum = String.valueOf(NumberUtils.toInt(rowNum) + 1);
					}
					i++;
				}
			}
		}
	}

	
	private void setDimTabCellPro(CellInfo cellInfo) {
		String rowNum = String.valueOf(cellInfo.getRowStart());
		String cellNum = String.valueOf(cellInfo.getCellStart());
		if(tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V)){
			adjRowCol(Integer.parseInt(rowNum), cellInfo
					.getDataTablePropertyPropertys().size(), "row");
		}
		else{
			adjRowCol(Integer.parseInt(cellNum), cellInfo
					.getDataTablePropertyPropertys().size(), "col");
		}
		int i = 0;
		for (DataTablePropertyProperty pro : cellInfo
				.getDataTablePropertyPropertys()) {
			setProperty(rowNum, cellNum, pro);
			this.setColInfo(NumberUtils.toInt(cellNum) + 2);
			this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
			addExtInfo(NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum),
					cellInfo.getExtInfo(), pro.getValue(),
					cellInfo.getOldValues(i), cellInfo.getDimTypeNo(),
					cellInfo.getSrcValues(i));
			if (cellInfo.getExtDirection().equals(
					GlobalConstants4plugin.EXT_DIRECTION_H)) {
				cellNum = String.valueOf(NumberUtils.toInt(cellNum) + 1);
			}
			if (cellInfo.getExtDirection().equals(
					GlobalConstants4plugin.EXT_DIRECTION_V)) {
				rowNum = String.valueOf(NumberUtils.toInt(rowNum) + 1);
			}
			i++;
		}
	}

	private void setIdxTabCellPro(CellInfo cellInfo) {
		String rowNum = String.valueOf(cellInfo.getRowStart());
		String cellNum = String.valueOf(cellInfo.getCellStart());
		addDsInfo(cellInfo.getExtInfo());
		int i = 0;
		int colMax = colDimList.size();
		for (DataTablePropertyProperty pro : cellInfo
				.getDataTablePropertyPropertys()) {
			setProperty(rowNum, cellNum, pro);
			this.setColInfo(NumberUtils.toInt(cellNum) + 2);
			this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
			addExtInfo(NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum),
					cellInfo.getExtInfo(), cellInfo.getIndexNo(),
					cellInfo.getDsId(), pro.getValue(),
					cellInfo.getOldValues(i));
			if (cellInfo.getExtDirection().equals(
					GlobalConstants4plugin.EXT_DIRECTION_H)) {
				cellNum = String.valueOf(NumberUtils.toInt(cellNum) + 1);
			}
			if (cellInfo.getExtDirection().equals(
					GlobalConstants4plugin.EXT_DIRECTION_V)) {
				rowNum = String.valueOf(NumberUtils.toInt(rowNum) + 1);
			}
			if (cellInfo.getExtDirection().equals(
					GlobalConstants4plugin.EXT_DIRECTION_HV)) {
				cellNum = String.valueOf(NumberUtils.toInt(cellNum) + 1);
				if (NumberUtils.toInt(cellNum) > colMax) {
					cellNum = String.valueOf(cellInfo.getRowStart());
					rowNum = String.valueOf(NumberUtils.toInt(rowNum) + 1);
				}
			}
			i++;
		}

	}

	private void setFormulaCellPro(CellInfo cellInfo) {
		String rowNum = String.valueOf(cellInfo.getRowStart());
		String cellNum = String.valueOf(cellInfo.getCellStart());
		if (!tmp.getTemplateType().equals("02")) {
			if (GlobalConstants4plugin.FORMULA_ADD_EXT.equals(cellInfo.getExtInfo()
					.get("AnalyseExtType"))) {
				String formula = cellInfo.getFormula();
				if (String.valueOf(cellInfo.getExtInfo().get("isAnalyseExt"))
						.equals("Y")) {
					
					if (tmp.getTemplateType().equals("06")) {

						int row = ExcelLetterIntTrans.lettrToCell(crossIdxlNo)
								.get("row")-1;
						int col = ExcelLetterIntTrans.lettrToCell(crossIdxlNo)
								.get("col")-1;
						if (cellInfo.getRowStart() == row) {
							Pattern pattern = Pattern.compile("(\\$[A-Z]+\\$\\d+)");
							Matcher matcher = pattern.matcher(formula);
							StringBuffer sb = new StringBuffer("");
							Map<String, String> rMap = new HashMap<String, String>();

							while (matcher.find()) {
								String tmp = matcher.group(0);
								rMap.put(
										tmp,
										getExtFormulaInfo(StringUtils.split(tmp, "$")[0]
												+ StringUtils.split(tmp, "$")[1], ":",GlobalConstants4plugin.EXT_DIRECTION_H));
							}
							matcher.appendTail(sb);
							formula = sb.toString();
							pattern = Pattern.compile("([A-Z]+\\d+)");
							if (rMap != null && rMap.size() > 0) {
								for (String key : rMap.keySet()) {
									formula = StringUtils.replace(formula, key,
											rMap.get(key));
								}
							}
							for (int i = 0; i < this.rowDimList.size(); i++) {
								sb.setLength(0);
								matcher = pattern.matcher(formula);
								while (matcher.find()) {
									String tmp = matcher.group(0);
									matcher.appendReplacement(sb,
											ExcelLetterIntTrans.addRow(tmp, i));
								}
								matcher.appendTail(sb);
								String newformula = sb.toString();
								DataTablePropertyProperty pro = new DataTablePropertyProperty();
								pro.setStyle(cellInfo
										.getDataTablePropertyProperty()
										.getStyle());
								pro.setFormula(newformula);
								setProperty(
										String.valueOf(cellInfo.getRowStart()
												+ i),
										String.valueOf(cellInfo.getCellStart()),
										pro);
								this.setColInfo(cellInfo.getCellStart() + 2);
								this.setRowInfo(cellInfo.getRowStart() + i + 2);
							}

						}
						if (cellInfo.getCellStart() == col) {
							Pattern pattern = Pattern.compile("(\\$[A-Z]+\\$\\d+)");
							Matcher matcher = pattern.matcher(formula);
							StringBuffer sb = new StringBuffer("");
							Map<String, String> rMap = new HashMap<String, String>();

							while (matcher.find()) {
								String tmp = matcher.group(0);
								rMap.put(
										tmp,
										getExtFormulaInfo(StringUtils.split(tmp, "$")[0]
												+ StringUtils.split(tmp, "$")[1], ":",GlobalConstants4plugin.EXT_DIRECTION_V));
							}
							matcher.appendTail(sb);
							formula = sb.toString();
							if (rMap != null && rMap.size() > 0) {
								for (String key : rMap.keySet()) {
									formula = StringUtils.replace(formula, key,
											rMap.get(key));
								}
							}
							pattern = Pattern.compile("([A-Z]+\\d+)");
							for (int i = 0; i < this.colDimList.size(); i++) {
								sb.setLength(0);;
								matcher = pattern.matcher(formula);
								while (matcher.find()) {
									String tmp = matcher.group(0);
									matcher.appendReplacement(sb,
											ExcelLetterIntTrans.addCol(tmp, i));
								}
								matcher.appendTail(sb);
								String newformula = sb.toString();
								DataTablePropertyProperty pro = new DataTablePropertyProperty();
								pro.setStyle(cellInfo
										.getDataTablePropertyProperty()
										.getStyle());
								pro.setFormula(newformula);
								setProperty(
										String.valueOf(cellInfo.getRowStart()),
										String.valueOf(cellInfo.getCellStart()
												+ i), pro);
								this.setColInfo(cellInfo.getCellStart() + i + 2);
								this.setRowInfo(cellInfo.getRowStart() + 2);
							}
						}
					} else {
						Pattern pattern = Pattern.compile("(\\$[A-Z]+\\$\\d+)");
						Matcher matcher = pattern.matcher(formula);
						StringBuffer sb = new StringBuffer("");
						Map<String, String> rMap = new HashMap<String, String>();

						while (matcher.find()) {
							String tmp = matcher.group(0);
							rMap.put(
									tmp,
									getFormulaInfo(StringUtils.split(tmp, "$")[0]
											+ StringUtils.split(tmp, "$")[1], ","));
						}
						matcher.appendTail(sb);
						formula = sb.toString();
						pattern = Pattern.compile("([A-Z]+\\d+)");
						for (int i = 0; i < extIndex; i++) {
							sb.setLength(0);;
							matcher = pattern.matcher(formula);
							while (matcher.find()) {
								String tmp = matcher.group(0);
								if (this.tmp.getTemplateType().equals("04")
										|| this.tmp.getTemplateType().equals(
												"01"))
									matcher.appendReplacement(sb,
											ExcelLetterIntTrans.addRow(tmp, i));
								if (this.tmp.getTemplateType().equals("05"))
									matcher.appendReplacement(sb,
											ExcelLetterIntTrans.addCol(tmp, i));
							}
							matcher.appendTail(sb);
							String newformula = sb.toString();
							DataTablePropertyProperty pro = new DataTablePropertyProperty();
							pro.setStyle(cellInfo
									.getDataTablePropertyProperty().getStyle());
							if (rMap != null && rMap.size() > 0) {
								for (String key : rMap.keySet()) {
									newformula = StringUtils.replace(newformula, key,
											rMap.get(key));
								}
							}
							pro.setFormula(newformula);
							if (this.tmp.getTemplateType().equals("04")
									|| this.tmp.getTemplateType().equals("01")) {
								setProperty(
										String.valueOf(cellInfo.getRowStart()
												+ i),
										String.valueOf(cellInfo.getCellStart()),
										pro);
								this.setColInfo(cellInfo.getCellStart() + 2);
								this.setRowInfo(cellInfo.getRowStart() + i + 2);
							}
							if (this.tmp.getTemplateType().equals("05")) {
								setProperty(
										String.valueOf(cellInfo.getRowStart()),
										String.valueOf(cellInfo.getCellStart()
												+ i), pro);
								this.setColInfo(cellInfo.getCellStart() + i + 2);
								this.setRowInfo(cellInfo.getRowStart() + 2);
							}

						}
					}

				} else {
					cellInfo.getDataTablePropertyProperty().setFormula(formula);
					setProperty(rowNum, cellNum,
							cellInfo.getDataTablePropertyProperty());
					this.setColInfo(NumberUtils.toInt(cellNum) + 2);
					this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
				}
			} else {
				if (cellInfo.getExtInfo().get("indexNo") == null) {
					String formula = cellInfo.getFormula();
					if (String.valueOf(
							cellInfo.getExtInfo().get("isAnalyseExt")).equals(
							"Y")
							&& !tmp.getTemplateType().equals(
									GlobalConstants4plugin.RPT_TMP_TYPE_CELL)) {
						Pattern pattern = Pattern
								.compile("([A-Z]{0,}\\d{0,}:[A-Z]{0,}\\d{0,})|([A-Z]+\\d+(\\+|-))|((\\+|-)[A-Z]+\\d+)|([A-Z]+\\d+)");
						Matcher matcher = pattern.matcher(formula);
						StringBuffer sb = new StringBuffer();
						while (matcher.find()) {
							String tmp = matcher.group(0);
							if (tmp.contains(":")) {
								String cells[] = StringUtils.split(tmp, ":");
								matcher.appendReplacement(sb,
										getFormulaInfo(cells[0], cells[1], ","));
							} else if (tmp.contains("+")) {
								int i = tmp.indexOf("+");
								if (i == 0) {
									String cellNm = StringUtils.substring(tmp,
											1, tmp.length());
									cellNm = "+(" + getFormulaInfo(cellNm, "+")
											+ ")";
									matcher.appendReplacement(sb, cellNm);
								} else {
									String cellNm = StringUtils.substring(tmp,
											0, tmp.length() - 1);
									cellNm = "(" + getFormulaInfo(cellNm, "+")
											+ ")+";
									matcher.appendReplacement(sb, cellNm);
								}
							} else if (tmp.contains("|")) {
								int i = tmp.indexOf("|");
								if (i == 0) {
									String cellNm = StringUtils.substring(tmp,
											1, tmp.length());
									cellNm = "-(" + getFormulaInfo(cellNm, "+")
											+ ")";
									matcher.appendReplacement(sb, cellNm);
								} else {
									String cellNm = StringUtils.substring(tmp,
											0, tmp.length() - 1);
									cellNm = "(" + getFormulaInfo(cellNm, "+")
											+ ")-";
									matcher.appendReplacement(sb, cellNm);
								}
							} else {
								matcher.appendReplacement(sb,
										getFormulaInfo(tmp, ","));
							}
						}
						matcher.appendTail(sb);
						formula = sb.toString();
					}
					cellInfo.getDataTablePropertyProperty().setFormula(formula);
				} else {
					addExtInfo(cellInfo.getRowStart(), cellInfo.getCellStart(),
							cellInfo.getExtInfo(), cellInfo.getIndexNo(),
							cellInfo.getDsId(), cellInfo
									.getDataTablePropertyProperty().getValue());
				}

				setProperty(rowNum, cellNum,
						cellInfo.getDataTablePropertyProperty());
				this.setColInfo(NumberUtils.toInt(cellNum) + 2);
				this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
			}
		} else {
			String formula = cellInfo.getFormula();
			cellInfo.getDataTablePropertyProperty().setFormula(formula);
			setProperty(rowNum, cellNum,
					cellInfo.getDataTablePropertyProperty());
			this.setColInfo(NumberUtils.toInt(cellNum) + 2);
			this.setRowInfo(NumberUtils.toInt(rowNum) + 2);
		}
		// addExtInfo(cellInfo.getRowStart(), cellInfo.getCellStart(),
		// cellInfo.getExtInfo());
	}

	private String getFormulaInfo(String name, String tag) {
		for (CellInfo cellInfo : cellInfos) {
			if (cellInfo.getONm().equals(name)) {
				return cellInfo.getRegions(tag);
			}
		}
		return "";
	}
	
	private String getExtFormulaInfo(String name, String tag ,String extDirection) {
		for (CellInfo cellInfo : cellInfos) {
			if (cellInfo.getONm().equals(name)) {
				return cellInfo.getRegions(tag,extDirection);
			}
		}
		return "";
	}

	private String getFormulaInfo(String cellStart, String cellEnd, String tag) {
		String result = "";
		int i = 0;
		for (CellInfo cellInfo : cellInfos) {
			if (compareInfo(cellInfo.getONm(), cellStart) >= 0
					&& compareInfo(cellInfo.getONm(), cellEnd) <= 0) {
				if (i < cellInfos.size())
					result += cellInfo.getRegion() + tag;
			}
			i++;
		}
		if (!result.equals("")) {
			result = StringUtils.substring(result, 0, result.length() - 1);
		}
		return result;

	}

	private int compareInfo(String cell1, String cell2) {
		int i = 0;
		while (cell1.charAt(i) <= 'Z' && cell1.charAt(i) >= 'A') {
			i++;
		}
		int cellNum1 = ExcelLetterIntTrans.letterToInt(StringUtils.substring(
				cell1, 0, i));
		int rowNum1 = NumberUtils.toInt(StringUtils.substring(cell1, i,
				cell1.length()));
		i = 0;
		while (cell2.charAt(i) <= 'Z' && cell2.charAt(i) >= 'A') {
			i++;
		}
		if (i == 0) {
			int rowNum2 = NumberUtils.toInt(StringUtils.substring(cell1, i,
					cell1.length()));
			if (rowNum1 > rowNum2)
				return 1;
			else
				return -1;
		} else if (i >= cell2.length()) {
			int cellNum2 = ExcelLetterIntTrans.letterToInt(StringUtils
					.substring(cell2, 0, i));
			if (cellNum1 > cellNum2)
				return 1;
			else
				return -1;
		} else {
			int cellNum2 = ExcelLetterIntTrans.letterToInt(StringUtils
					.substring(cell2, 0, i));
			int rowNum2 = NumberUtils.toInt(StringUtils.substring(cell2, i,
					cell2.length()));
			if (cellNum1 > cellNum2)
				return 1;
			else if (cellNum1 == cellNum2) {
				if (rowNum1 > rowNum2)
					return 1;
				else if (rowNum1 == rowNum2)
					return 0;
				else
					return -1;
			} else {
				return -1;
			}
		}
	}

	public void setProperty(String rowNum, String cellNum,
			DataTablePropertyProperty pro) {
		if (newDataTable.getAdditionalProperties().get(rowNum) == null) {
			DataTableProperty tpro = new DataTableProperty();
			tpro.setAdditionalProperty(cellNum, pro);
			newDataTable.setAdditionalProperty(rowNum, tpro);
		} else {
			newDataTable.getAdditionalProperties().get(rowNum)
					.setAdditionalProperty(cellNum, pro);
		}
	}
	

	private void setProInfo() {
		for (CellInfo cellInfo : cellInfos) {
			// cellMaps.put(cellInfo.getONm(), cellInfo.getRegion());
			if (cellInfo.getType().equals("")) {
				setCommonCellPro(cellInfo);
			}
			if (cellInfo.getType().equals(GlobalConstants4plugin.RPT_CELL_SOURCE_IDX)) {
				setIdxCellPro(cellInfo);

			}
			if (cellInfo.getType().equals(
					GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE)) {
				setDsCellPro(cellInfo);
			}
			if (cellInfo.getType().equals(
					GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB)) {
				setDimTabCellPro(cellInfo);
			}
			if (cellInfo.getType().equals(
					GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB)) {
				setIdxTabCellPro(cellInfo);
			}
			if (cellInfo.getType().equals(
					GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA)) {
				setFormulaCellPro(cellInfo);
			}
			if (cellInfo.getType().equals(
					GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT)) {
				setTextCellPro(cellInfo);
			}
		}
	}
	

	private List<String> getFormulaInfo(RptDesignSrcFormulaVO vo) {
		List<String> formulas = new ArrayList<String>();
		String formula = vo.getExcelFormula();
		Pattern pattern = Pattern.compile("(\\$[A-Z]+\\$\\d+)");
		Matcher matcher = pattern.matcher(formula);
		StringBuffer sb = new StringBuffer("");
		Map<String, String> rMap = new HashMap<String, String>();

		while (matcher.find()) {
			String tmp = matcher.group(0);
			rMap.put(
					tmp,
					getFormulaInfo(
							StringUtils.split(tmp, "$")[0]
									+ StringUtils.split(tmp, "$")[1], ","));
		}
		matcher.appendTail(sb);
		formula = sb.toString();
		pattern = Pattern.compile("([A-Z]+\\d+)");
		for (int i = 0; i < extIndex; i++) {
			sb.setLength(0);;
			matcher = pattern.matcher(formula);
			while (matcher.find()) {
				String tmp = matcher.group(0);
				matcher.appendReplacement(sb,
						ExcelLetterIntTrans.addRow(tmp, i));
			}
			matcher.appendTail(sb);
			String newformula = sb.toString();
			if (rMap != null && rMap.size() > 0) {
				for (String key : rMap.keySet()) {
					newformula = StringUtils.replace(newformula, key, rMap.get(key));
				}
			}
			formulas.add(newformula);
		}
		return formulas;
	}

	private String analysisInfo() {
		String info = "";
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CELL)
				|| tmp.getTemplateType().equals(
						GlobalConstants4plugin.RPT_TMP_TYPE_COM)) {
			info = getIdxJson();
			if (!info.equals(""))
				return info;
		}

		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL)
				|| tmp.getTemplateType().equals(
						GlobalConstants4plugin.RPT_TMP_TYPE_COM)) {
			info = getDsJson();
			if (!info.equals("") || info.equals("download"))
				return info;
		}

		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V)
				|| tmp.getTemplateType().equals(
						GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)) {
			info = getIdxTabJson();
			if (!info.equals(""))
				return info;
		}

		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CROSS)) {
			info = getCrossJson();
			if (!info.equals(""))
				return info;
		}
		long time = System.currentTimeMillis();
		createParam();
		System.out.println("-------------------------createParam"+(System.currentTimeMillis()-time)+"------------------------");
		time = System.currentTimeMillis();
		Workbook workbook = null;
		try {
			workbook = WorkbookWrapper.openExcel(fileName);
			setCellMap(workbook);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(workbook);
		}
		System.out.println("-------------------------setCellMap"+(System.currentTimeMillis()-time)+"------------------------");
		time = System.currentTimeMillis();
		setProInfo();
		System.out.println("-------------------------setProInfo"+(System.currentTimeMillis()-time)+"------------------------");
		time = System.currentTimeMillis();
		createRegion();
		setDataTable(newDataTable);
		System.out.println("-------------------------createRegion"+(System.currentTimeMillis()-time)+"------------------------");
		time = System.currentTimeMillis();
		return info;
	}


	private DataTablePropertyProperty getProInfo(Object obj, Object value,
			String formula, boolean flag) {
		DataTablePropertyProperty pro = new DataTablePropertyProperty();
		if (obj != null && obj instanceof DataTablePropertyProperty) {
			DataTablePropertyProperty source = (DataTablePropertyProperty) obj;
			if (flag){
				pro.setFormula(formula);
			}
			else
				pro = source;
			if (source.getStyle() instanceof String) {
				String style = String.valueOf(source.getStyle());
				if(style.indexOf("\"")==0)
					style = style.substring(1, style.length() - 1);
				pro.setStyle(style);
			} else
				pro.setStyle(source.getStyle());
			pro.setValue(value);
			if (formula != null)
				pro.setFormula(formula);
		} else {
			pro.setValue(value);
			if (formula != null)
				pro.setFormula(formula);
		}
		return pro;
	}

	private DataTablePropertyProperty getProInfo(Object obj, String formula,
			boolean flag) {
		DataTablePropertyProperty pro = new DataTablePropertyProperty();
		if (obj != null && obj instanceof DataTablePropertyProperty) {
			DataTablePropertyProperty source = (DataTablePropertyProperty) obj;
			if (flag)
				BeanUtils.copy(source, pro);
			else
				pro = source;
			if (source.getStyle() instanceof String) {
				String style = String.valueOf(source.getStyle());
				style = style.substring(1, style.length() - 1);
				pro.setStyle(style);
			} else
				pro.setStyle(source.getStyle());
			pro.setValue(source.getValue());
			if (formula != null)
				pro.setFormula(formula);

		} else {
			pro.setValue(obj);
			if (formula != null)
				pro.setFormula(formula);
		}
		return pro;
	}

	/**
	 * 获取单元格数据
	 * 
	 * @param cell
	 *            单元格
	 * @return 单元格数据
	 */
	private Object getCellData(Cell cell) {
		Object cellValue = null;
		switch (cell.getCellTypeEnum()) { // 根据cell中的类型来输出数据
		case NUMERIC:
			cellValue = cell.getNumericCellValue();
			break;
		case STRING:
			cellValue = cell.getStringCellValue();
			break;
		case BOOLEAN:
			cellValue = cell.getBooleanCellValue();
			break;
		case FORMULA:
			try {
				cellValue = String.valueOf(cell.getStringCellValue());
			} catch (Exception e) {
				try {
					cellValue = String.valueOf(cell.getNumericCellValue());
				} catch (Exception e1) {
					cell.getErrorCellValue();
				}
			}
			break;
		case BLANK:
			cellValue = null;
			break;
		default:
			cellValue = cell.toString();
			break;
		}

		return cellValue;
	}

	private Object getCellVal(Workbook workbook, int row, int col) {
		if (workbook.getNumberOfSheets() >= 1) {
			Row r = workbook.getSheetAt(0).getRow(row);
			if (r != null) {
				Cell c = r.getCell(col);
				if (c != null) {
					Object value = getCellData(c);
					if (value == null) {
						return "0";
					} else {
						return value;
					}
				}

			}
		}
		return "0";
	}

	private void dealResult(String result, int row, int cell, Object obj) {
		DataTablePropertyProperty pro = getProInfo(obj, null, false);
		String rowNum = String.valueOf(row);
		String cellNum = String.valueOf(cell);
		String extDirection = "";
		if (!result.equals("pass")) {
			if (!result.equals("")) {
				String nums[] = StringUtils.split(result, ",");
				rowNum = nums[0];
				cellNum = nums[1];
				extDirection = nums[2];
			}
			CellInfo cellInfo = new CellInfo(NumberUtils.toInt(rowNum),
					NumberUtils.toInt(cellNum));
			cellInfo.addDataTablePropertyProperty(pro);
			cellInfo.setONm(row, cell);
			cellInfo.setInitDirection(extDirection);
			cellInfos.add(cellInfo);
		}
	}

	private void dealResult(RptDesignSrcFormulaVO formulaCell, String result,
			int row, int cell, Object obj) {
		DataTablePropertyProperty pro = getProInfo(obj, null, false);
		String rowNum = String.valueOf(row);
		String cellNum = String.valueOf(cell);
		String extDirection = "";
		if (!result.equals("pass")) {
			if (!result.equals("")) {
				String nums[] = StringUtils.split(result, ",");
				rowNum = nums[0];
				cellNum = nums[1];
				extDirection = nums[2];
			}
			Map<String, Object> extInfo = new HashMap<String, Object>();
			if (formulaCell.getIsRptIndex() != null
					&& formulaCell.getIsRptIndex().equals("Y")) {
				Object value = null;
				String indexNo = "", dsId = "";
				RptDesignSrcIdxVO vo = this.getIdxInfo(formulaCell);
				if (vo != null) {
					indexNo = vo.getIndexNo();
					dsId = vo.getDsId();
				}
				if (cellIdxValues != null) {
					value = this.cellIdxValues.get(vo.getId().getCellNo());
				}
				pro.setValue(value);
				extInfo.put("oldValue", value);
				extInfo.put("indexNo", indexNo);
				extInfo.put("dsId", dsId);
			}

			extInfo.put("AnalyseExtType", formulaCell.getAnalyseExtType());
			extInfo.put("isAnalyseExt", formulaCell.getIsAnalyseExt());
			extInfo.put("formula", formulaCell.getExcelFormula());
			CellInfo cellInfo = null;
			if(formulaCell.getIsAnalyseExt().equals("Y")){
				if (formulaCell.getAnalyseExtType().equals(
						GlobalConstants4plugin.FORMULA_ADD_EXT)) {
					extInfo.put("extDirection", GlobalConstants4plugin.EXT_DIRECTION_V);
					extInfo.put("extMode", GlobalConstants4plugin.EXT_MODE_INSERT);
					cellInfo = new CellInfo(formulaCell.getId().getCellNo(),
							NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum),
							extIndex, GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA,
							extInfo);
				} else {
					cellInfo = new CellInfo(formulaCell.getId().getCellNo(),
							NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum),
							1, GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA, extInfo);
				}
			}
			else{
				cellInfo = new CellInfo(formulaCell.getId().getCellNo(),
						NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum),
						1, GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA,
						extInfo);
			}
			dealExtInfo(pro, cellInfo, formulaCell, true);
			cellInfo.addDataTablePropertyProperty(pro);
			cellInfo.setONm(row, cell);
			cellInfo.setInitDirection(extDirection);
			checkPri(cellInfo);
			cellInfos.add(cellInfo);
		}
	}

	private void dealResult(RptDesignSrcTextVO textCell, String result,
			int row, int cell, Object obj) {
		RptSysVarAnalysisBS rptSysBS = SpringContextHolder
				.getBean("rptSysVarAnalysisBS");
		//params.put("tempUnit", this.unit);
		String tempUnit = unit;
		if(StringUtils.isEmpty(unit) || unit.equals("00")){
			tempUnit = this.templateUnit;
		}
		Object value = rptSysBS.getResult(textCell.getExpression(), params,
				busiType,tempUnit).get("str");
		DataTablePropertyProperty pro = getProInfo(obj, value, null, false);
		String rowNum = String.valueOf(row);
		String cellNum = String.valueOf(cell);
		String extDirection = "";
		if (!result.equals("pass")) {
			if (!result.equals("")) {
				String nums[] = StringUtils.split(",");
				rowNum = nums[0];
				cellNum = nums[1];
				extDirection = nums[2];
			}
			Map<String, Object> extInfo = new HashMap<String, Object>();
			extInfo.put("expression", textCell.getExpression());
			CellInfo cellInfo = new CellInfo(textCell.getId().getCellNo(),
					NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum), 1,
					GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT, extInfo);
			dealExtInfo(pro, cellInfo, textCell, true);
			cellInfo.addDataTablePropertyProperty(pro);
			cellInfo.setInitDirection(extDirection);
			cellInfo.setONm(row, cell);
			checkPri(cellInfo);
			cellInfos.add(cellInfo);
		}
	}

	private void dealResult(RptDesignSrcIdxVO idxCell, String result, Workbook workbook, int row,
			int cell, Object obj) {
		Object oldValue = null;
		Object value = null;
		boolean unitFlag = true;
		if (fileName != null && !fileName.equals("")) {
			value = this.getCellVal(workbook, row, cell);
			oldValue = this.cellIdxValues.get(idxCell.getId().getCellNo());
			unitFlag = false;
		} else if (cellIdxValues != null) {
			value = this.cellIdxValues.get(idxCell.getId().getCellNo());
			oldValue = value;
		}
		DataTablePropertyProperty pro = getProInfo(obj, value, null, false);
		String rowNum = String.valueOf(row);
		String cellNum = String.valueOf(cell);
		String extDirection = "";
		if (!result.equals("pass")) {
			if (!result.equals("")) {
				String nums[] = StringUtils.split(",");
				rowNum = nums[0];
				cellNum = nums[1];
				extDirection = nums[2];
			}
			Map<String, Object> extInfo = new HashMap<String, Object>();
			extInfo.put("indexNo", idxCell.getIndexNo());
			CellInfo cellInfo = new CellInfo(idxCell.getId().getCellNo(),
					NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum), 1,
					GlobalConstants4plugin.RPT_CELL_SOURCE_IDX, extInfo);
			dealExtInfo(pro, cellInfo, idxCell, unitFlag);
			cellInfo.putExtInfo("oldValue", oldValue);
			cellInfo.putExtInfo("dsId", idxCell.getDsId());
			cellInfo.addDataTablePropertyProperty(pro);
			cellInfo.setInitDirection(extDirection);
			cellInfo.setONm(row, cell);
			checkPri(cellInfo);
			cellInfos.add(cellInfo);
		}
	}

	private void dealResult(RptDesignSrcDimTabVO dimTabCell, String result, Workbook workbook,
			int row, int cell, Object obj) {
		String rowNum = String.valueOf(row);
		String cellNum = String.valueOf(cell);
		String extDirection = "";
		Map<String, Object> extInfo = new HashMap<String, Object>();
		if (tmp.getTemplateType().equals("04"))
			extInfo.put("extDirection", GlobalConstants4plugin.EXT_DIRECTION_V);
		if (tmp.getTemplateType().equals("05"))
			extInfo.put("extDirection", GlobalConstants4plugin.EXT_DIRECTION_H);
		if (tmp.getTemplateType().equals("06"))
			extInfo.put("extDirection", dimTabCell.getExtDirection());
		extInfo.put("extMode", GlobalConstants4plugin.EXT_MODE_INSERT);
		List<Object> oldValues = new ArrayList<Object>();
		List<Object> srcValues = new ArrayList<Object>();
		List<String> totalTypes = new ArrayList<String>();
		if (!result.equals("pass")) {
			if (!result.equals("")) {
				String nums[] = StringUtils.split(result, ",");
				rowNum = nums[0];
				cellNum = nums[1];
				extDirection = nums[2];
			}
			if (tmp.getTemplateType().equals("06")
					&& (rowDimList != null || colDimList != null)) {
				String dimTypeNo = this.dimAlias.get(dimTabCell.getId()
						.getCellNo());
				if (dimTypeNo == null)
					dimTypeNo = dimTabCell.getDimTypeNo();
				int length = 1;
				List<Map<String, Object>> dimList = new ArrayList<Map<String, Object>>();
				if (dimTabCell.getExtDirection().equals(
						GlobalConstants4plugin.EXT_DIRECTION_V)) {
					dimList = rowDimList;
				}
				if (dimTabCell.getExtDirection().equals(
						GlobalConstants4plugin.EXT_DIRECTION_H)) {
					dimList = colDimList;
				}
				length = dimList.size();
				CellInfo cellInfo = new CellInfo(
						dimTabCell.getId().getCellNo(),
						NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum),
						length, GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB, extInfo);
				List<DataTablePropertyProperty> pros = new ArrayList<DataTablePropertyProperty>();
				if (dimList == null || dimList.size() <= 0) {
					return;
				} else {
					obj = getProInfo(obj, "", null, false);
					dealExtInfo((DataTablePropertyProperty)obj, cellInfo, dimTabCell, true);
					for (Map<String, Object> dimTabValue : dimList) {
						DataTablePropertyProperty pro = getProInfo(obj,
								dimTabValue.get(dimTypeNo), null, true);
						oldValues.add(dimTabValue.get(dimTypeNo));
						srcValues.add(dimTabValue.get("$" + dimTypeNo));
						totalTypes.add("01");
						pros.add(pro);
					}
				}
				cellInfo.putExtInfo("dimTypeNo", dimTabCell.getDimTypeNo());
				cellInfo.putExtInfo("oldValues", oldValues);
				cellInfo.putExtInfo("srcValues", srcValues);
				cellInfo.putExtInfo("totalTypes", totalTypes);
				cellInfo.setInitDirection(extDirection);
				cellInfo.addDataTablePropertyProperty(pros);
				cellInfo.setONm(row, cell);
				checkPri(cellInfo);
				cellInfos.add(cellInfo);
			}
			if (!tmp.getTemplateType().equals("06") && tabIdxValues != null) {
				List<Map<String, Object>> dimTabValues = tabIdxValues
						.get(dimTabCell.getId().getCellNo());
				int length = 1;
				if (dimTabValues != null && dimTabValues.size() > 0) {
					length = dimTabValues.size();
				}
				CellInfo cellInfo = new CellInfo(
						dimTabCell.getId().getCellNo(),
						NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum),
						length, GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB, extInfo);
				cellInfo.putExtInfo("dateFormat", dimTabCell.getDateFormat());
				List<DataTablePropertyProperty> pros = new ArrayList<DataTablePropertyProperty>();
				if (dimTabValues == null || dimTabValues.size() <= 0) {
					return;
				} else {
					if (fileName != null && !fileName.equals("")) {
						int rol = cellInfo.getRowStart();
						int col = cellInfo.getCellStart();
						for (int i = 0; i < dimTabValues.size(); i++) {
							DataTablePropertyProperty pro = getProInfo(obj,
									this.getCellVal(workbook, rol, col), null, true);
							oldValues.add(dimTabValues.get(i).get("value"));
							srcValues.add(dimTabValues.get(i).get("srcValue"));
							totalTypes.add(dimTabValues.get(i).get("totalType")
									.toString());
							dealExtInfo(pro, cellInfo, dimTabCell, false);
							pros.add(pro);
							if (cellInfo.getExtDirection().equals(
									GlobalConstants4plugin.EXT_DIRECTION_H))
								cell++;
							if (cellInfo.getExtDirection().equals(
									GlobalConstants4plugin.EXT_DIRECTION_V))
								rol++;
						}
					} else {
						obj = getProInfo(obj, "", null, false);
						dealExtInfo((DataTablePropertyProperty)obj, cellInfo, dimTabCell, true);
						for (Map<String, Object> dimTabValue : dimTabValues) {
							String unit = "";
							if (dimTabCell.getDataUnit() != null
									&& !dimTabCell.getDataUnit().equals("")) {
								unit = dimTabCell.getDataUnit();
							} else {
								unit = this.templateUnit;
							}
							if(StringUtils.isNotBlank(this.unit)&&!this.unit.equals("00")){
								unit = this.unit;
							}
							Object value = dimTabValue.get("value");
							if(value == null)
								value = "";
							value = setValue(unit, value,
										dimTabCell.getDisplayFormat());
							
							DataTablePropertyProperty pro = getProInfo(obj,
									value, null, true);
							oldValues.add(dimTabValue.get("value"));
							srcValues.add(dimTabValue.get("srcValue"));
							totalTypes.add(dimTabValue.get("totalType")
									.toString());
							pros.add(pro);
						}
					}

				}
				cellInfo.putExtInfo("dimTypeNo", dimTabCell.getDimTypeNo());
				cellInfo.putExtInfo("oldValues", oldValues);
				cellInfo.putExtInfo("srcValues", srcValues);
				cellInfo.putExtInfo("totalTypes", totalTypes);
				cellInfo.setInitDirection(extDirection);
				cellInfo.addDataTablePropertyProperty(pros);
				cellInfo.setONm(row, cell);
				checkPri(cellInfo);
				cellInfos.add(cellInfo);
			}
		}
	}

	private void dealResult(RptDesignSrcIdxTabVO idxTabCell, String result, Workbook workbook,
			int row, int cell, Object obj) {
		String rowNum = String.valueOf(row);
		String cellNum = String.valueOf(cell);
		String extDirection = "";
		Map<String, Object> extInfo = new HashMap<String, Object>();
		if (tmp.getTemplateType().equals("04"))
			extInfo.put("extDirection", GlobalConstants4plugin.EXT_DIRECTION_V);
		if (tmp.getTemplateType().equals("05"))
			extInfo.put("extDirection", GlobalConstants4plugin.EXT_DIRECTION_H);
		if (tmp.getTemplateType().equals("06"))
			extInfo.put("extDirection", GlobalConstants4plugin.EXT_DIRECTION_HV);
		extInfo.put("extMode", GlobalConstants4plugin.EXT_MODE_INSERT);
		List<Object> oldValues = new ArrayList<Object>();
		List<String> totalTypes = new ArrayList<String>();
		if (!result.equals("pass")) {
			if (!result.equals("")) {
				String nums[] = StringUtils.split(result, ",");
				rowNum = nums[0];
				cellNum = nums[1];
				extDirection = nums[2];
			}
			if (tmp.getTemplateType().equals("06") && idxValueMap != null) {
				int length = rowDimList.size() * colDimList.size();
				CellInfo cellInfo = new CellInfo(
						idxTabCell.getId().getCellNo(),
						NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum), length,rowDimList.size(),
						colDimList.size(), 
						GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB, extInfo);
				List<DataTablePropertyProperty> pros = new ArrayList<DataTablePropertyProperty>();
				if (idxValueMap == null || idxValueMap.size() <= 0)
					return;
				obj = getProInfo(obj, 0, null, false);
				dealExtInfo((DataTablePropertyProperty)obj, cellInfo, idxTabCell, true);
				for (int r = 0; r < rowDimList.size(); r++) {
					for (int c = 0; c < colDimList.size(); c++) {
						String key = rowDimList.get(r).get("$info$").toString()
								+ colDimList.get(c).get("$info$").toString();
						DataTablePropertyProperty pro = getProInfo(
								obj,
								idxValueMap.get(key) != null ? idxValueMap
										.get(key) : 0, null, true);
						oldValues
								.add(idxValueMap.get(key) != null ? idxValueMap
										.get(key) : 0);
						totalTypes.add(GlobalConstants4plugin.IDXTAB_DETAILS);
						pros.add(pro);
					}
				}
				cellInfo.putExtInfo("dsId", idxTabCell.getDsId());
				cellInfo.putExtInfo("oldValues", oldValues);
				cellInfo.putExtInfo("indexNo", idxTabCell.getIndexNo());
				cellInfo.putExtInfo("totalTypes", totalTypes);
				cellInfo.setInitDirection(extDirection);
				cellInfo.addDataTablePropertyProperty(pros);
				cellInfo.setONm(row, cell);
				checkPri(cellInfo);
				cellInfos.add(cellInfo);
			}
			if (!tmp.getTemplateType().equals("06") && tabIdxValues != null) {
				int length = 1;
				if (dimTabLists != null && dimTabLists.size() > 0) {
					length = dimTabLists.size();
				}
				CellInfo cellInfo = new CellInfo(
						idxTabCell.getId().getCellNo(),
						NumberUtils.toInt(rowNum), NumberUtils.toInt(cellNum),
						length, GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB, extInfo);
				List<DataTablePropertyProperty> pros = new ArrayList<DataTablePropertyProperty>();
				if (dimTabLists == null || dimTabLists.size() <= 0) {
					return;
				} else {
					if (fileName != null && !fileName.equals("")) {
						int rol = cellInfo.getRowStart();
						int col = cellInfo.getCellStart();
						for (int i = 0; i < dimTabLists.size(); i++) {
							DataTablePropertyProperty pro = getProInfo(obj,
									this.getCellVal(workbook, rol, col), null, true);
							oldValues.add(this.idxTabValMaps.get(dimTabLists.get(i)).get(idxTabCell.getId().getCellNo()));
							dealExtInfo(pro, cellInfo, idxTabCell, false);
							pros.add(pro);
						}
					} else {
						obj = getProInfo(obj, 0, null, false);
						dealExtInfo((DataTablePropertyProperty)obj, cellInfo, idxTabCell, true);
						for (String dimKeys: this.dimTabLists) {
							DataTablePropertyProperty pro = null;
							String unit = "";
							if (idxTabCell.getDataUnit() != null
									&& !idxTabCell.getDataUnit().equals("")) {
								unit = idxTabCell.getDataUnit();
							} else {
								unit = this.templateUnit;
							}
							if(StringUtils.isNotBlank(this.unit)&&!this.unit.equals("00")){
								unit = this.unit;
							}
							Object value = null;
							if(this.idxTabValMaps.get(dimKeys)!=null)
								value = this.idxTabValMaps.get(dimKeys).get(idxTabCell.getId().getCellNo());
							if(value == null){
								value = 0;
							}
							value = setValue(unit, value,
									idxTabCell.getDisplayFormat());
							
							pro = getProInfo(obj, value,
									null, true);
							if(this.idxTabValMaps.get(dimKeys)!=null)
								oldValues.add(this.idxTabValMaps.get(dimKeys).get(idxTabCell.getId().getCellNo()));
							else{
								oldValues.add(null);
							}
							pros.add(pro);
						}

					}
				}
				cellInfo.putExtInfo("dsId", idxTabCell.getDsId());
				cellInfo.putExtInfo("oldValues", oldValues);
				cellInfo.putExtInfo("indexNo", idxTabCell.getIndexNo());
				cellInfo.putExtInfo("totalTypes", totalTypes);
				cellInfo.setInitDirection(extDirection);
				cellInfo.addDataTablePropertyProperty(pros);
				cellInfo.setONm(row, cell);
				checkPri(cellInfo);
				cellInfos.add(cellInfo);
			}
		}
	}

	@SuppressWarnings("unused")
	private Object getTotal(List<Map<String, Object>> idxTabValues,
			RptDesignCellInfo cellInfo) {
		String unit = "";
		if (cellInfo.getDataUnit() != null
				&& !cellInfo.getDataUnit().equals("")) {
			unit = cellInfo.getDataUnit();
		} else {
			unit = this.templateUnit;
		}
		if(StringUtils.isNotBlank(this.unit)&&!this.unit.equals("00")){
			unit = this.unit;
		}
		BigDecimal value = new BigDecimal(0);
		for (Map<String, Object> idxTabValue : idxTabValues) {
			if (idxTabValue.get("totalType").equals(
					GlobalConstants4plugin.IDXTAB_DETAILS)) {
				BigDecimal m = new BigDecimal(0);
				try {
					m = new BigDecimal(setValue(unit, idxTabValue.get("value"), cellInfo.getDisplayFormat())
							.toString());
					value = value.add(m);
				} catch (Exception e) {
				}
			}
		}
		return value.toString();
	}

	@SuppressWarnings("unused")
	private Object getSubTotal(List<Map<String, Object>> idxTabValues,
			RptDesignCellInfo cellInfo, int i) {
		int index = 0;
		if (tmp.getTemplateType().equals("04"))
			index = ExcelLetterIntTrans.letterToRow(cellInfo.getId()
					.getCellNo());
		if (tmp.getTemplateType().equals("05"))
			index = ExcelLetterIntTrans.letterToCol(cellInfo.getId()
					.getCellNo());
		String unit = "";
		if (cellInfo.getDataUnit() != null
				&& !cellInfo.getDataUnit().equals("")) {
			unit = cellInfo.getDataUnit();
		} else {
			unit = this.templateUnit;
		}
		if(StringUtils.isNotBlank(this.unit)&&!this.unit.equals("00")){
			unit = this.unit;
		}
		BigDecimal value = new BigDecimal(0);
		int start = i - 1;
		if (subArea.get(index + i) != null) {
			start = subArea.get(index + i) - index;
		}
		for (int k = i - 1; k >= start; k--) {
			Map<String, Object> idxTabValue = idxTabValues.get(k);
			if (idxTabValue.get("totalType").equals(
					GlobalConstants4plugin.IDXTAB_DETAILS)) {
				BigDecimal m = new BigDecimal(0);
				try {
					m = new BigDecimal(setValue(unit, idxTabValue.get("value"), cellInfo.getDisplayFormat())
							.toString());
					value = value.add(m);
				} catch (Exception e) {
				}
			}
		}
		return value.toString();
	}

	private void dealResult(RptDesignSrcDsVO dsCell, String result, Workbook workbook, int row,
			int cell, Object obj) {
		String rowNum = String.valueOf(row);
		String cellNum = String.valueOf(cell);
		String extDirection = "";
		Map<String, Object> extInfo = new HashMap<String, Object>();
		extInfo.put("extDirection", dsCell.getExtDirection());
		extInfo.put("extMode", dsCell.getExtMode());
		extInfo.put("dsId", dsCell.getDsId());
		extInfo.put("columnId", dsCell.getColumnId());
		List<Object> oldValues = new ArrayList<Object>();
		if (!result.equals("pass")) {
			if (!result.equals("")) {
				String nums[] = StringUtils.split(result, ",");
				rowNum = nums[0];
				cellNum = nums[1];
				extDirection = nums[2];
			}
			if (cellDsValues != null) {
				Map<String, List<Object>> disInfo = cellDsValues.get(dsCell
						.getDsId());
				if (disInfo != null || isDownLoad) {
					List<Object> cellValues = new ArrayList<Object>();
					cellValues.add("");
					if (!isDownLoad) {
						cellValues = disInfo.get(dsCell.getId().getCellNo());
						if(cellValues != null && cellValues.size()>0){
							if(this.sumResult.size() > 0){
								Object val =this.sumResult.get(dsCell.getId().getCellNo());
								if(val!=null){
									//cellValues.add(this.sumResult.get(dsCell.getId().getCellNo()));
									cellValues.add(val);
								}
								else if(dsCell.getId().getCellNo().equals(this.fCellNo)){
									cellValues.add("合计");
								}
								else {
									cellValues.add("");
								}
							}
						}
					}
					if (dsCell.getIsExt().equals(
							GlobalConstants4plugin.COMMON_BOOLEAN_NO)) {
						boolean unitFlag = true;
						DataTablePropertyProperty pro = new DataTablePropertyProperty();
						Object oldValue = null;
						CellInfo cellInfo = new CellInfo(dsCell.getId()
								.getCellNo(), NumberUtils.toInt(rowNum),
								NumberUtils.toInt(cellNum), 1,
								GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE, extInfo);
						if (fileName != null && !fileName.equals("")) {
							pro = getProInfo(obj, this.getCellVal(workbook,
									NumberUtils.toInt(rowNum),
									NumberUtils.toInt(cellNum)), null, true);
							oldValue = cellValues.get(0);
							unitFlag = false;
						} else if (cellValues != null && cellValues.size() > 0) {
							pro = getProInfo(obj, cellValues.get(0), null, true);
							oldValue = cellValues.get(0);
						} else {
							// pro = getProInfo(obj, null, null);
							return;
						}
						dealExtInfo(pro, cellInfo, dsCell, unitFlag);
						cellInfo.addDataTablePropertyProperty(pro);
						oldValues.add(oldValue);
						cellInfo.setONm(row, cell);
						cellInfo.setInitDirection(extDirection);
						checkPri(cellInfo);
						cellInfos.add(cellInfo);
					} else {
						int length = 1;
						if (isDownLoad) {
							length = (int) this.total;
						} else if (cellValues != null && cellValues.size() > 0) {
							length = cellValues.size();
						}
						CellInfo cellInfo = new CellInfo(dsCell.getId()
								.getCellNo(), NumberUtils.toInt(rowNum),
								NumberUtils.toInt(cellNum), length,
								GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE, extInfo);
						List<DataTablePropertyProperty> pros = new ArrayList<DataTablePropertyProperty>();
						if (!isDownLoad
								&& (cellValues == null || cellValues.size() <= 0)) {
							return;
						} else {
							if (fileName != null && !fileName.equals("")) {
								int rol = cellInfo.getRowStart();
								int col = cellInfo.getCellStart();
								for (int i = 0; i < cellValues.size(); i++) {
									DataTablePropertyProperty pro = getProInfo(
											obj, this.getCellVal(workbook, rol, col),
											null, true);
									oldValues.add(cellValues.get(i));
									dealExtInfo(pro, cellInfo, dsCell, false);
									pros.add(pro);
									if (cellInfo.getExtDirection().equals(
											GlobalConstants4plugin.EXT_DIRECTION_H))
										cell++;
									if (cellInfo.getExtDirection().equals(
											GlobalConstants4plugin.EXT_DIRECTION_V))
										rol++;
								}
							} else {
								if (isDownLoad) {
									DataTablePropertyProperty pro = getProInfo(
											obj, null, null, true);
									oldValues.add(null);
									dealExtInfo(pro, cellInfo, dsCell, true);
									pros.add(pro);
								} else {
									obj = getProInfo(obj,"", null, false);
									dealExtInfo((DataTablePropertyProperty)obj, cellInfo, dsCell, true);
									for (Object cellValue : cellValues) {
										String unit = "";
										if (dsCell.getDataUnit() != null
												&& !dsCell.getDataUnit().equals("")) {
											unit = dsCell.getDataUnit();
										} else {
											unit = this.templateUnit;
										}
										if(StringUtils.isNotBlank(this.unit)&&!this.unit.equals("00")){
											unit = this.unit;
										}
										cellValue = setValue(unit, cellValue,
												dsCell.getDisplayFormat());
										DataTablePropertyProperty pro = getProInfo(
												obj, cellValue, null, true);
										oldValues.add(cellValue);
										pros.add(pro);
									}
								}

							}

						}
						cellInfo.putExtInfo("oldValues", oldValues);
						cellInfo.setInitDirection(extDirection);
						cellInfo.addDataTablePropertyProperty(pros);
						cellInfo.setONm(row, cell);
						checkPri(cellInfo);
						cellInfos.add(cellInfo);
					}
				}
			}

		}
	}

	private Style getStyle(String name) {
		for (Style style : schema.getNamedStyles()) {
			if (style.getName().equals(name)) {
				return style;
			}
		}
		return null;
	}

	private Object setValue(String unit, Object value,
			String displayFormat) {
		BigDecimal dec;
		if(value != null && StringUtils.isNotBlank(value.toString())){
			try {
				if (unit != null) {
					if (unit.equals("01")) { // 元
						dec = new BigDecimal(String.valueOf(value));
						if (GlobalConstants4plugin.CELL_DISPLAY_FORMAT_ORG
								.equals(displayFormat)) {
						}
						return dec;
					} else if (unit.equals("02")) { // 百
						dec = new BigDecimal(String.valueOf(value));
						if (GlobalConstants4plugin.CELL_DISPLAY_FORMAT_ORG
								.equals(displayFormat)) {
						}
						return dec.divide(new BigDecimal(100));
					} else if (unit.equals("03")) { // 千
						dec = new BigDecimal(String.valueOf(value));
						if (GlobalConstants4plugin.CELL_DISPLAY_FORMAT_ORG
								.equals(displayFormat)) {
						}
						return dec.divide(new BigDecimal(1000));
					} else if (unit.equals("04")) { // 万
						dec = new BigDecimal(String.valueOf(value));
						if (GlobalConstants4plugin.CELL_DISPLAY_FORMAT_ORG
								.equals(displayFormat)) {
						}
						return dec.divide(new BigDecimal(10000));
					} else if (unit.equals("05")) { // 亿
						dec = new BigDecimal(String.valueOf(value));
						if (GlobalConstants4plugin.CELL_DISPLAY_FORMAT_ORG
								.equals(displayFormat)) {
						}
						return dec.divide(new BigDecimal(100000000));
					} else {
						return value;
					}
				}
			} catch (Exception e) {
				return value;
			}
		}
		return value;
	}

	private void dealExtInfo(DataTablePropertyProperty pro, CellInfo cell,
			RptDesignCellInfo cellInfo, boolean unitFlag) {
		Style style = null;
		if (pro.getStyle() instanceof Style)
			style = (Style) pro.getStyle();
		else
			style = getStyle(String.valueOf(pro.getStyle()));
		if (style == null)
			style = new Style();
		Style s = new Style();
		BeanUtils.copy(style, s);
		if(tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL))
			s.setWordWrap(true);
		cell.putExtInfo("busiNo", cellInfo.getBusiNo());
		cell.putExtInfo("cellNm", cellInfo.getCellNm());
		cell.putExtInfo("value", pro.getValue());
		if (GlobalConstants4plugin.COMMON_BOOLEAN_NO.equals(this.isUpt)) {
			// 若该模板是不可修改的，所有单元格均不可编辑
			cell.putExtInfo("isUpt", GlobalConstants4plugin.COMMON_BOOLEAN_NO);
		} else {
			cell.putExtInfo("isUpt", cellInfo.getIsUpt());
		}
		cell.putExtInfo("isNull", cellInfo.getIsNull());
		cell.putExtInfo("displayFormat", cellInfo.getDisplayFormat());
		cell.putExtInfo("dataLen", cellInfo.getDataLen());
		String unit = "";
		if (cellInfo.getDataUnit() != null
				&& !cellInfo.getDataUnit().equals("")) {
			unit = cellInfo.getDataUnit();
		} else {
			unit = this.templateUnit;
		}
		if(StringUtils.isNotBlank(this.unit)&&!this.unit.equals("00")){
			unit = this.unit;
		}
		cell.putExtInfo("unit", unit);
		if (cell.getExtInfo().get("dateFormat") != null) {
			cell.putExtInfo("unit", "");

		} else {
			if (GlobalConstants4plugin.CELL_DISPLAY_FORMAT_PER.equals(cellInfo
					.getDisplayFormat())) {
				// 百分比
				unit = "";
				cell.putExtInfo("unit", unit);
				s.setFormatter("0.00%");
			} else if (GlobalConstants4plugin.CELL_DISPLAY_FORMAT_ORG.equals(cellInfo
					.getDisplayFormat())) {
				// 金额
				String formatter = "#,##0.00";
				if (cellInfo.getDataPrecision() != null) {
					long pre = cellInfo.getDataPrecision().longValue();
					if (pre <= 0) {
						formatter = "#,##0";
					} else {
						formatter = "#,##0.";
						for (long i = 0; i < pre; i++) {
							formatter += "0";
						}
					}
				}
				if (!unitFlag)
					unit = "01";
				if (pro.getValue() != null)
					pro.setValue(setValue(unit, pro.getValue(),
							cellInfo.getDisplayFormat()));
				s.setFormatter(formatter);
			} else if (GlobalConstants4plugin.CELL_DISPLAY_FORMAT_COUNT
					.equals(cellInfo.getDisplayFormat())) {
				// 数值
				String formatter = "0";
				if (!unitFlag)
					unit = "01";
				if (pro.getValue() != null)
					pro.setValue(setValue(unit, pro.getValue(),
							cellInfo.getDisplayFormat()));
				s.setFormatter(formatter);
			} else {
				cell.putExtInfo("unit", unit);
				s.setFormatter("@");
			}
		}

		cell.putExtInfo("dataPrecision", cellInfo.getDataPrecision());
		cell.putExtInfo("isValidate", "Y");
		if (isExtend) {
			if (cell.getType().equals(GlobalConstants4plugin.RPT_CELL_SOURCE_IDX)||cell.getType().equals(GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB))
				s.setBackColor("#96C896");
			if (validates != null) {
				String color = validates.get(cell.getExtInfo().get("cellNo"));
				if (color != null && !color.equals("")) {
					s.setForeColor("#" + color);
					cell.putExtInfo("isValidate", "N");
				}
			}
		}
		s.setName(String.valueOf(this.schema.getNamedStyles().size() + 1));
		pro.setStyle(getStyleNm(s));
	}

	private String getStyleNm(Style style) {
		for (Style ostyle : schema.getNamedStyles()) {
			if (style.equals(ostyle)) {
				return ostyle.getName();
			}
		}
		this.schema.getNamedStyles().add(style);
		return style.getName();
	}

	private void checkPri(CellInfo cell) {
		if (pricellNos == null) {
			cell.setPri(true);
		} else if (pricellNos.contains(cell.getCellNo())) {
			cell.setPri(true);
		} else {
			cell.setPri(false);
		}
	}

	public RptDesignTmpInfo getTmp() {
		return tmp;
	}

	public void setTmp(RptDesignTmpInfo tmp) {
		this.tmp = tmp;
	}

	public List<Map<String, Object>> getSearchParams() {
		return searchParams;
	}

	public void setSearchParams(List<Map<String, Object>> searchParams) {
		this.searchParams = searchParams;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getIdxSelectParams() {
		return idxSelectParams;
	}

	public void setIdxSelectParams(String idxSelectParams) {
		this.idxSelectParams = idxSelectParams;
	}

	public String getDsSelectParams() {
		return dsSelectParams;
	}

	public void setDsSelectParams(String dsSelectParams) {
		this.dsSelectParams = dsSelectParams;
	}

	public void setIdxDetialSelectParams(String idxDetialSelectParams) {
		this.idxDetialSelectParams = idxDetialSelectParams;
	}

}
