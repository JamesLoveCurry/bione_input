package com.yusys.bione.plugin.regulation.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.util.excel.ExcelAnalyseUtils;
import com.yusys.bione.frame.util.excel.ExcelEventListener;
import com.yusys.bione.frame.util.excel.ExcelEventReader;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.design.service.RptTmpBS;
import com.yusys.bione.plugin.idxana.service.RptIdxDimRelBS;
import com.yusys.bione.plugin.regulation.enums.FilterMode;
import com.yusys.bione.plugin.regulation.util.LoadData;
import com.yusys.bione.plugin.regulation.vo.Data;
import com.yusys.bione.plugin.regulation.vo.FormulaIndex;
import com.yusys.bione.plugin.regulation.vo.ReportInfoDetailsVO;
import com.yusys.bione.plugin.regulation.vo.Template;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.service.RptMgrInfoBS;
import com.yusys.bione.plugin.spreadjs.service.ExcelReaderFactory;
import com.yusys.bione.plugin.spreadjs.service.IExcelReader;
import com.yusys.bione.plugin.valid.service.ValidWarnBS;
import com.yusys.bione.plugin.valid.web.vo.ValidCfgextWarnVO;

public abstract class ImportRptTool implements ExcelEventListener{

	protected LoadData static_loadData;

	protected Data static_data;
	
	protected Template static_template;
	
	protected Map<String,String> static_rptUpgrade = new HashMap<String, String>(); //缓存报表升级概要码值
	
	private Map<String, String> static_rptBaseMap = new HashMap<String, String>();//报表信息中英文对应map
	
	private Map<String, String> static_idxCellMap = new HashMap<String, String>();//指标单元格中英文对应map
	
	private Map<String, String> static_filtInfoMap = new HashMap<String, String>();//维度过滤公式中英文对应map
	
	private Map<String, String> static_fmaCellMap = new HashMap<String, String>();//公式单元格中英文对应map
	
	private Map<String, String> static_expCellMap = new HashMap<String, String>();//表达式单元格中英文对应map
	
	private Map<String, String> static_comCellMap = new HashMap<String, String>();//一般单元格中英文对应map
	
	private Map<String, String> static_modelCellMap = new HashMap<String, String>();//数据模型单元格中英文对应map
	
	private Map<String, String> static_calcCellMap = new HashMap<String, String>();//表间取数单元格中英文对应map
	
	private Map<String, String> static_rptLogicMap = new HashMap<String, String>();//逻辑校验公式中英文对应map
	
	private Map<String, String> static_rptWarnMap = new HashMap<String, String>();//警戒公式中英文对应map
	
	private Map<String, String> static_warnLevelMap = new HashMap<String, String>();//警戒公式等级中英文对应 map
	
	private Map<String, RptIdxInfo> static_idxInfo = new HashMap<String, RptIdxInfo>(); //缓存所有非报表指标信息
	
	private Map<String, String> static_newRptIdxInfo = new HashMap<String, String>(); //缓存新增报表指标标号信息
	
	private Map<String, String> static_catalogUrlMap = new HashMap<String, String>(); //缓存报表目录信息map(key:catalogUrl, val:catalogId)
	
	private Map<String, String> static_catalogIdMap = new HashMap<String, String>(); //缓存报表目录信息map(key:catalogUpId+catalogNm, val:catalogId)
	
	private Map<String, RptSysModuleInfo> static_module = new HashMap<String, RptSysModuleInfo>(); //缓存所有明细模型信息
	
	private Map<String, RptSysModuleCol> static_moduleCol = new HashMap<String, RptSysModuleCol>(); //缓存所有明细模型字段信息
	
	private String DICTIONARY_RPTBASE = "报表信息";
	
	@SuppressWarnings("unused")
	private String DICTIONARY_TEMPLATEBASE = "模板信息";
	
	private String DICTIONARY_CELLINFO = "单元格信息";
	
	private String DICTIONARY_IDXCELL = "指标单元格";
	
	private String DICTIONARY_DIMTABCELL = "维度列表单元格";
	
	private String DICTIONARY_IDXTABCELL = "指标列表单元格";
	
	private String DICTIONARY_FORMULACELL = "公式单元格";
	
	private String DICTIONARY_EXPRECELLCELL = "表达式单元格";
	
	private String DICTIONARY_COMMONCELL = "一般单元格";
	
	private String DICTIONARY_MODELCELL = "字段单元格";
	
	private String DICTIONARY_CALCCELL = "表间取数单元格";
	
	private String DICTIONARY_RPTLOGIC = "校验公式";
	
	private String DICTIONARY_RPTWARN = "预警校验";
	
	private String DICTIONARY_FACTDIM = "查询条件";
	
	private String DICTIONARY_IDXFILTERDIM = "指标过滤信息";
	
	private String DICTIONARY_DIMTYPE = "维度类型";
	
	private String DICTIONARY_CELLNO = "单元格编号";
	
	private String DICTIONARY_FILTERVAL = "过滤信息";
	
	private String DICTIONARY_FILTERTYPE = "过滤类型";
	
	private String RPT_TMP_TYPE_DETAIL = GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL;//明细类报表
	
	private String RPT_TMP_TYPE_CELL = GlobalConstants4plugin.RPT_TMP_TYPE_CELL;//单元格类报表
	
	private String RPT_TMP_TYPE_COM = GlobalConstants4plugin.RPT_TMP_TYPE_COM;//综合类报表
	
	private String dynamic_columnType;

	private Map<String, String> save_tempDataMap = new HashMap<String, String>(); //储存报表信息map
	
	private List<Map<String,Object>> save_cellsArray = new ArrayList<Map<String,Object>>(); //储存一般单元格信息list
	
	private List<Map<String,Object>> save_idxsArray = new ArrayList<Map<String,Object>>(); //储存指标单元格信息list
	
	private List<Map<String,Object>> save_detailArray = new ArrayList<Map<String,Object>>(); //储存明细单元格信息list
	
	private List<Map<String,Object>> save_formulaArray = new ArrayList<Map<String,Object>>(); //储存公式单元格信息list
	
	private List<Map<String,Object>> save_expArray = new ArrayList<Map<String,Object>>(); //储存表达式单元格信息list
	
	private List<Map<String,Object>> save_calcArray = new ArrayList<Map<String,Object>>(); //储存表间取数单元格信息list
	
	private Map<String, List<Map<String, String>>> save_filterMap = new HashMap<String, List<Map<String,String>>>();//指标过滤信息map
	
	private Map<Map<String, String>, List<Map<String, String>>> save_warnMap = new HashMap<Map<String, String>, List<Map<String, String>>>(); //储存预警校验的map
	
	public ImportRptTool(LoadData loadData) {
		this.static_loadData = loadData;
		this.static_data = loadData.getData();
		this.strBaseMap();
		this.strIdxCellMap();
		this.strModelCellMap();
		this.strFmaCellMap();
		this.strExpCellMap();
		this.strComCellMap();
		this.strLogicMap();
		this.strWarnMap();
		this.strCalcCellMap();
		this.cacheIdxInfoMap();
		this.cacheCalalogMap();
		this.cacheModuleMap();
	}

	/**
	 * 保存报表相关配置信息的方法
	 * @throws IOException
	 */
	public abstract void saveRpt() throws IOException;
	
	/**
	 * 读取报表xml信息的方法
	 */
	protected void readData(Entry<String, ExcelEventReader> entry) throws IOException {
		if(entry != null) {
			String filePath = entry.getKey();
			ExcelEventReader excelEventReader = entry.getValue();
			excelEventReader.setExcelEventListener(this);
			this.static_template = this.static_loadData.getTemplate(excelEventReader);
			//读报表信息
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_RPTBASE);
			//读取查询条件
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_FACTDIM);
			//读取指标过滤信息
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_IDXFILTERDIM);
			//读取一般单元格
			this.dynamic_columnType = this.DICTIONARY_COMMONCELL;
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_CELLINFO);
			//读取指标单元格
			this.dynamic_columnType = this.DICTIONARY_IDXCELL;
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_CELLINFO);
			//读取数据模型单元格
			this.dynamic_columnType = this.DICTIONARY_MODELCELL;
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_CELLINFO);
			//读取维度列表单元格
			this.dynamic_columnType = this.DICTIONARY_DIMTABCELL;
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_CELLINFO);
			//读取指标列表单元格
			this.dynamic_columnType = this.DICTIONARY_IDXTABCELL;
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_CELLINFO);
			//读取公式单元格
			this.dynamic_columnType = this.DICTIONARY_FORMULACELL;
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_CELLINFO);
			//读取表达式单元格
			this.dynamic_columnType = this.DICTIONARY_EXPRECELLCELL;
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_CELLINFO);
			//读取表间取数单元格
			this.dynamic_columnType = this.DICTIONARY_CALCCELL;
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_CELLINFO);
			this.dynamic_columnType = null;
/*			制度包不导入逻辑校验了，先去掉
 * 			ValidLogicBS logicBs  = SpringContextHolder.getBean("validLogicBS");
			logicBs.deletelogicByTmpId(this.static_template.getTemplateId());//保存新的逻辑校验前，先把之前的删除
*/			//读取逻辑校验信息
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_RPTLOGIC);
			//读取预警校验信息
			excelEventReader.startReadSheet(filePath, this.DICTIONARY_RPTWARN);
			this.saveWarn();//保存警戒值校验
		}
	}

	@Override
	public void rowDataReady(ExcelEventReader excelEventReader, int rowIdx, Map<Integer, String> rowDataMap) {
		String sheetName = excelEventReader.getSheetName();
		//报表信息sheet页处理
		if (this.DICTIONARY_RPTBASE.equals(sheetName)) {
			if ((rowDataMap == null) && (rowIdx == 0) && (this.save_tempDataMap != null)) {
				this.static_loadData.translateDictionary(this.save_tempDataMap);
				this.save_tempDataMap = this.strRptBaseVO();
			} else {
				if (this.save_tempDataMap == null) {
					this.save_tempDataMap = new HashMap<String, String>();
				}
				String columnName = rowDataMap.get(Integer.valueOf(1));
				this.save_tempDataMap.put(columnName, rowDataMap.get(Integer.valueOf(2)));
			}
		} else if (this.DICTIONARY_CELLINFO.equals(sheetName)) {//单元格sheet页处理
			if (rowDataMap == null || rowIdx <= 1) {
				return;
			}
			Map<String, String> columnDataMap = this.static_loadData.parseRowDataMap(this.static_template, this.dynamic_columnType, rowIdx, rowDataMap);
			this.static_loadData.translateDictionary(columnDataMap);
			if((columnDataMap != null) && (columnDataMap.size() > 0)) {
				if (this.DICTIONARY_COMMONCELL.equals(this.dynamic_columnType)) {
					columnDataMap.put("单元格类型", "01");
					columnDataMap.put("单元格类型名称", "一般单元格");
					columnDataMap.put("是否数据修改", "Y");
					columnDataMap.put("是否为空", "Y");
					columnDataMap.put("是否汇总", "N");
					save_cellsArray.add(this.strComMap(columnDataMap));
				} else if (this.DICTIONARY_IDXCELL.equals(this.dynamic_columnType)) {
					columnDataMap.put("单元格类型", "03");
					columnDataMap.put("单元格类型名称", "指标单元格");
					columnDataMap.put("是否数据修改", "Y");
					columnDataMap.put("是否为空", "Y");
					columnDataMap.put("是否减维", "N");
					columnDataMap.put("是否筛选维度", "N");
					columnDataMap.put("URL", "/report/frame/design/cfg/dbclk/module/idx");
					columnDataMap.put("宽度", "70%");
					columnDataMap.put("高度", "65%");
					columnDataMap.put("指标版本号", this.save_tempDataMap.get("verId"));//报表指标版本根据报表版本走
					save_idxsArray.add(this.strIdxMap(columnDataMap));
				} else if (this.DICTIONARY_MODELCELL.equals(this.dynamic_columnType)) {
					columnDataMap.put("单元格类型", "02");
					columnDataMap.put("单元格类型名称", "数据模型单元格");
					columnDataMap.put("是否数据修改", "Y");
					columnDataMap.put("是否为空", "Y");
					columnDataMap.put("URL", "/report/frame/design/cfg/dbclk/module");
					columnDataMap.put("宽度", "60%");
					columnDataMap.put("高度", "60%");
					save_detailArray.add(this.strDetalMap(columnDataMap));
				} else if (this.DICTIONARY_DIMTABCELL.equals(this.dynamic_columnType)) {
				} else if (this.DICTIONARY_IDXTABCELL.equals(this.dynamic_columnType)) {
				} else if (this.DICTIONARY_FORMULACELL.equals(this.dynamic_columnType)) {
					columnDataMap.put("单元格类型", "04");
					columnDataMap.put("单元格类型名称", "excel公式");
					columnDataMap.put("是否数据修改", "Y");
					columnDataMap.put("是否为空", "Y");
					columnDataMap.put("排序方式", "01");
					save_formulaArray.add(this.strFmaMap(columnDataMap));
				} else if (this.DICTIONARY_EXPRECELLCELL.equals(this.dynamic_columnType)) {
					columnDataMap.put("单元格类型", "06");
					columnDataMap.put("单元格类型名称", "表达式");
					columnDataMap.put("是否数据修改", "Y");
					columnDataMap.put("是否为空", "Y");
					columnDataMap.put("是否汇总", "N");
					columnDataMap.put("URL", "/report/frame/design/cfg/sysVarSet");
					columnDataMap.put("宽度", "80%");
					columnDataMap.put("高度", "82%");
					save_expArray.add(this.strExpMap(columnDataMap));
				} else if(this.DICTIONARY_CALCCELL.equals(this.dynamic_columnType)) {
					columnDataMap.put("单元格类型", "05");
					columnDataMap.put("单元格类型名称", "表间计算");
					columnDataMap.put("是否数据修改", "Y");
					columnDataMap.put("是否为空", "Y");
					columnDataMap.put("URL", "/report/frame/valid/logic/getData");
					columnDataMap.put("宽度", "85%");
					columnDataMap.put("高度", "86%");
					save_calcArray.add(this.strCalcMap(columnDataMap));
				}
			}
		} else if (this.DICTIONARY_IDXFILTERDIM.equals(sheetName)) {//指标过滤信息sheet页处理
			if (rowIdx >= 1) {
				Map<String, String> columnDataMap = this.static_loadData.parseRowDataMap(this.static_template, sheetName, rowIdx, rowDataMap);
				this.translateDictionary(sheetName, columnDataMap);
				if (columnDataMap != null && StringUtils.isNotEmpty(columnDataMap.get(this.DICTIONARY_CELLNO))) {
					String cellNo = columnDataMap.get(this.DICTIONARY_CELLNO);
					List<Map<String, String>> list = this.save_filterMap.get(cellNo);
					if (list == null) {
						list = new ArrayList<Map<String, String>>();
						this.save_filterMap.put(cellNo, list);
					}
					list.add(columnDataMap);
				}
			}
		} else if (this.DICTIONARY_FACTDIM.equals(sheetName)) {//查询维度sheet页处理
			if (rowIdx >= 1) {
				Map<String, String> columnDataMap = this.static_loadData.parseRowDataMap(this.static_template, sheetName, rowIdx, rowDataMap);
				if (columnDataMap != null && StringUtils.isNotEmpty(columnDataMap.get("维度编号"))) {
				}
			}
		} else if (this.DICTIONARY_RPTLOGIC.equals(sheetName)) {//逻辑校验sheet页处理，去除，逻辑校验走单独的校验功能
/*			if (rowIdx >= 1) {
				Map<String, String> columnDataMap = this.static_loadData.parseRowDataMap(this.static_template, sheetName, rowIdx, rowDataMap);
				if (columnDataMap != null && StringUtils.isNotEmpty(columnDataMap.get("公式"))) {
					Map<String, String> logicMap = new HashMap<String, String>();
					Map<String, String> logicFmaMap = new HashMap<String, String>();
					RptValidCfgextLogic logic = new RptValidCfgextLogic();
					ValidLogicBS logicBs  = SpringContextHolder.getBean("validLogicBS");
					FormulaCell cell = new FormulaCell();
					cell.setTemplate(this.static_template);
					for (Entry<String, String> entry : this.static_rptLogicMap.entrySet()) {
						String key = entry.getKey().toString();
						String value = entry.getValue().toString();
						logicMap.put(value, columnDataMap.get(key));
					}
					cell.setFormula(columnDataMap.get("公式"));
					cell.setExcelCellNo(columnDataMap.get("_REGULATION_公式"));
					logicFmaMap = this.strLogicFma(this.static_loadData.parseFormula(this.static_template, cell, null, null),columnDataMap.get("公式"), columnDataMap.get("逻辑运算类型"), columnDataMap.get("开始日期"));
					if("Y".equals(logicFmaMap.get("isSuccess"))) {//如果校验公式构造成功就进行保存
						logicMap.putAll(logicFmaMap);
					}
					if((logicFmaMap != null) && (logicFmaMap.size() > 0)){
						logic = ((JSONObject)JSON.toJSON(logicMap)).toJavaObject(RptValidCfgextLogic.class);
						if(StringUtils.isNotBlank(logic.getIsPre()) && ("是").equals(logic.getIsPre())){
							logic.setIsPre("1");
						}else{
							logic.setIsPre("0");
						}
						logicBs.saveLogic(logic, this.static_template.getTemplateId(), logicMap.get("leftFormulaIndex"), logicMap.get("rightFormulaIndex"), "", "");
					}
				}
			}*/
		} else if (this.DICTIONARY_RPTWARN.equals(sheetName)) {//预警校验sheet页处理
			if (rowIdx >= 1) {
				Map<String, String> columnDataMap = this.static_loadData.parseRowDataMap(this.static_template, sheetName, rowIdx, rowDataMap);
				if (columnDataMap != null && StringUtils.isNotEmpty(columnDataMap.get("单元格编号"))) {
					Map<String, String> warnMap = new HashMap<String, String>();
					Map<String, String> levenMap = new HashMap<String, String>();
					List<Map<String, String>> levenMapList = new ArrayList<Map<String,String>>();
					columnDataMap.put("模板ID", this.static_template.getTemplateId());
					//支持新增报表指标直接配置校验
					String realIndexNo =  this.static_loadData.getIdxNoByCacheData(save_tempDataMap.get("rptNm"), columnDataMap.get("单元格编号"));//从报表里取报表指标编号
					String cellNo = columnDataMap.get("单元格编号");
					if(StringUtils.isBlank(realIndexNo)) {
						realIndexNo = static_newRptIdxInfo.get(cellNo);//证明是新增的报表指标，从之前的缓存里取
					}
					columnDataMap.put("报表指标编号", realIndexNo);
					for (Entry<String, String> entry : columnDataMap.entrySet()) {
						String key = entry.getKey().toString();
						String value = entry.getValue().toString();
						if(static_rptWarnMap.get(key) != null){
							warnMap.put(static_rptWarnMap.get(key), value);
						}
						if(static_warnLevelMap.get(key) != null){
							levenMap.put(static_warnLevelMap.get(key), value);
						}
					}
					if(save_warnMap.get(warnMap) != null) {
						levenMapList = save_warnMap.get(warnMap);
					}
					levenMapList.add(levenMap);
					save_warnMap.put(warnMap, levenMapList);
				}
			}
		}
	}
	
	/**
	 * 获取报表基本信息
	 * @param templateName
	 * @param templateId
	 * @return
	 */
	protected Map<String, String> getRptBaseVO(){
		Map<String, String> baseMap = new HashMap<String, String>();
		if((this.save_tempDataMap != null) && (this.save_tempDataMap.size() > 0)) {
			baseMap = this.save_tempDataMap;
			this.save_tempDataMap = new HashMap<String, String>();
		}
		return baseMap;
	}
	
	/**
	 * 构造报表详细配置信息
	 * @param templateName
	 * @param templateId
	 * @param templateType
	 * @return
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	protected String strRptDetailVO(Map<String, String> baseMap) throws IOException{
		ReportInfoDetailsVO rptDetailsVO = new ReportInfoDetailsVO();
		rptDetailsVO.setTemplateId(this.static_template.getTemplateId());
		rptDetailsVO.setIdxsArray(this.strIdxsArray(this.static_template.getRptNm(), baseMap.get("templateType")));
		rptDetailsVO.setCellsArray(this.save_cellsArray);
		rptDetailsVO.setTmpJson(this.strTempJson(this.static_template.getFilePath()));
		this.emptySavevariable();
		return JSON.toJSONString(rptDetailsVO);
	}
	
	/**
	 * 清空缓存信息
	 * @param template
	 * @return
	 */
	protected void emptyCache(){
		this.static_idxInfo = new HashMap<String, RptIdxInfo>();
		this.static_rptUpgrade = new HashMap<String, String>();
		this.static_newRptIdxInfo = new HashMap<String, String>();
		this.static_catalogUrlMap = new HashMap<String, String>();
		this.static_catalogIdMap = new HashMap<String, String>();
		this.static_module = new HashMap<String, RptSysModuleInfo>();
		this.static_moduleCol = new HashMap<String, RptSysModuleCol>();
	}
	
	/**
	 * 清空新增报表指标缓存信息
	 * @param template
	 * @return
	 */
	protected void emptyRptIdxCache(){
		this.static_newRptIdxInfo = new HashMap<String, String>();
	}
	
	/**
	 * 构造报表基本信息
	 * @param templateName
	 * @param templateId
	 * @return
	 */
	private Map<String, String> strRptBaseVO(){
		Map<String, String> baseMap = this.save_tempDataMap;
		baseMap.put("定义类型", "01");
		baseMap.put("扩展类型", "02");
		baseMap.put("是否主表", "Y");
		baseMap.put("是否数据修改", "Y");
		baseMap.put("排列顺序", "1");
		baseMap.put("报表类型", "02");
		baseMap.put("最大版本", baseMap.get("版本号"));
		baseMap.put("模板ID", this.static_template.getTemplateId());
		String catalogUrl = baseMap.get("报表目录");//excel中的报表目录
		String catalogId = this.static_template.getCatalogId();
		
		//判断报表目录是否已存在，存在-沿用，不存在-新建 maojin2 20200323
		String rptCatalogId = this.getCatalogIdByRptId(this.static_template.getRptId());
		if(StringUtils.isBlank(rptCatalogId)){//报表不存在，按照excel中的内容创建目录
			if(StringUtils.isNotBlank(catalogUrl)) {
				catalogId = static_catalogUrlMap.get(catalogUrl);
				if(StringUtils.isBlank(catalogId)) {
					catalogId = this.getCatalogId(catalogUrl);
					static_catalogUrlMap.put(catalogUrl, catalogId);
				}
			}
		}else{
			catalogId = rptCatalogId;
		}
		baseMap.put("报表目录ID", catalogId);
		baseMap.put("报表ID", this.static_template.getRptId());
		Map<String, String> rptVoMap = new HashMap<String, String>();
		if (baseMap != null) {
			for (Entry<String, String> entry : this.static_rptBaseMap.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				rptVoMap.put(value, StringUtils.isEmpty(baseMap.get(key)) ? "" : baseMap.get(key));
			}
			String rptUpgrade = this.static_rptBaseMap.get("报表升级描述");
			rptVoMap.put(rptUpgrade, static_rptUpgrade.get(baseMap.get("报表升级描述")));
		}
		return rptVoMap;
	}
	
	/**
	 * 根据报表id查询目录，用于判断导入的报表是否已存在于某个目录中
	 * @param rptId
	 * @return
	 */
	private String getCatalogIdByRptId(String rptId) {
		String catalogId = "";
		RptMgrInfoBS rptMgrInfoBS  = SpringContextHolder.getBean("rptMgrInfoBS");		
		RptMgrReportInfo rptMgrReportInfo = rptMgrInfoBS.getEntityById(RptMgrReportInfo.class, rptId);
		if(rptMgrReportInfo != null){
			catalogId = rptMgrReportInfo.getCatalogId();
		}
		return catalogId;
	}

	/**
	 * 清空所有保存信息
	 * @param template
	 * @return
	 */
	private void emptySavevariable(){
		this.save_filterMap = new HashMap<String, List<Map<String,String>>>();
		this.save_cellsArray = new ArrayList<Map<String,Object>>();
		this.save_idxsArray = new ArrayList<Map<String,Object>>();
		this.save_detailArray = new ArrayList<Map<String,Object>>();
		this.save_formulaArray = new ArrayList<Map<String,Object>>();
		this.save_expArray = new ArrayList<Map<String,Object>>();
		this.save_calcArray = new ArrayList<Map<String,Object>>();
	}
	
	/**
	 * 保存警戒值校验
	 * @param template
	 * @return
	 */
	private void saveWarn(){
		if((this.save_warnMap != null) && (this.save_warnMap.size() > 0)) {
			ValidWarnBS warnBs = SpringContextHolder.getBean("validWarnBS");
			//warnBs.delete(null, this.static_template.getTemplateId());
			for(Entry<Map<String, String>, List<Map<String, String>>> entry : save_warnMap.entrySet()) {
				Map<String, String> warnMap = entry.getKey();
				List<Map<String, String>> levenMap = entry.getValue();
				ValidCfgextWarnVO warn = new ValidCfgextWarnVO();
				warn.setRptTemplateId(warnMap.get("rptTemplateId"));
				warn.setIndexNo(warnMap.get("indexNo"));
				warn.setIsFrs(warnMap.get("isFrs"));
				warn.setCompareType(warnMap.get("compareType"));
				warn.setStartDate(warnMap.get("startDate"));
				warn.setEndDate(warnMap.get("endDate"));
				warn.setRemark(warnMap.get("remark"));
				warn.setMinusRangeVal(new BigDecimal(levenMap.get(0).get("minusRangeVal")));
				warn.setPostiveRangeVal(new BigDecimal(levenMap.get(0).get("postiveRangeVal")));
				warn.setRptCycle(save_tempDataMap.get("rptCycle"));
				//RptValidCfgextWarn warn = ((JSONObject)JSON.toJSON(warnMap)).toJavaObject(RptValidCfgextWarn.class);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("verId", save_tempDataMap.get("maxVerId"));
				params.put("busiType", save_tempDataMap.get("busiType"));
				warnBs.saveWarn(warn, params);
			}
			this.save_warnMap = new HashMap<Map<String, String>, List<Map<String, String>>>(); 
		}
	}
	
	/**
	 * 构造逻辑校验的公式
	 * @param formulaList
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, String> strLogicFma(FormulaIndex[] formulaList, String logicFam, String logicOperType, String startDate){
		Map<String, String> logicFmaMap = new HashMap<String, String>();
		String isSuccess = "Y";
		if(formulaList != null && formulaList.length > 0){
			String[] formulas = logicFam.split(logicOperType);
			String expressionShortDesc = "";
			if(formulas.length == 2){
				String leftExpression = formulas[0];
				String rightExpression = formulas[1];
				Map<String, String> FmaNmMap = new HashMap<String, String>();
				Map<String, String> cellNmMap = new HashMap<String, String>();
				List<String> leftFormulaIndexs = new ArrayList<String>();
				StringBuffer leftFormulaIndex = new StringBuffer();
				List<String> rightFormulaIndexs = new ArrayList<String>();
				StringBuffer rightFormulaIndex = new StringBuffer();
				for(int i = 0; i < formulaList.length; i++){
					FormulaIndex fma = formulaList[i];
					String indexNo = fma.getIndex().getIndexNo();
					String indexNm = fma.getIndex().getIndexNm();
					//支持新增报表指标直接配置校验
					if(StringUtils.isBlank(indexNo)) {
						indexNo = static_newRptIdxInfo.get(this.static_template.getTemplateId() + "." + indexNm);
					}
					FmaNmMap.put(logicFam.substring(fma.getStartOffset(), fma.getEndOffset()), indexNo);
					cellNmMap.put(logicFam.substring(fma.getStartOffset(), fma.getEndOffset()), indexNm);
					if(fma.getStartOffset() < leftExpression.length()){
						if(!leftFormulaIndexs.contains(indexNo)) {
							leftFormulaIndexs.add(indexNo);
							leftFormulaIndex.append(indexNo + ",");
						}
					}else{
						if(!rightFormulaIndexs.contains(indexNo)) {
							rightFormulaIndexs.add(indexNo);
							rightFormulaIndex.append(indexNo + ",");
						}
					}
				}
				String rightSourceIdx = rightFormulaIndex.toString();
				String leftSourceIdx = leftFormulaIndex.toString();
				if(rightSourceIdx.endsWith(",")){
					rightSourceIdx = rightSourceIdx.substring(0, rightSourceIdx.length() - 1);
				}
				if(leftSourceIdx.endsWith(",")){
					leftSourceIdx = leftSourceIdx.substring(0, leftSourceIdx.length() - 1);
				}
				logicFmaMap.put("rightFormulaIndex", rightSourceIdx);
				logicFmaMap.put("leftFormulaIndex", leftSourceIdx);
				if(FmaNmMap != null){
					leftExpression = leftExpression.substring(1, leftExpression.length() - 1);
					rightExpression = rightExpression.substring(1, rightExpression.length() - 1);
					expressionShortDesc = leftExpression + logicOperType + rightExpression;
					for(Entry<String, String> entry : FmaNmMap.entrySet()) {
						if((null == entry.getKey()) || (null == entry.getValue())) {
							isSuccess = "N";
							break;
						}
						String key = entry.getKey().toString();
						String value = entry.getValue().toString();
						rightExpression = StringUtils.replace(rightExpression, key, value);
						leftExpression = StringUtils.replace(leftExpression, key, value);
						expressionShortDesc = StringUtils.replace(expressionShortDesc, key, cellNmMap.get(key));
					}
					expressionShortDesc = StringUtils.replace(expressionShortDesc, "('", "[");
					expressionShortDesc = StringUtils.replace(expressionShortDesc, "')", "]");
					logicFmaMap.put("leftExpression", leftExpression);
					logicFmaMap.put("rightExpression", rightExpression);
				}
				logicFmaMap.put("expressionShortDesc", expressionShortDesc.toString());
				logicFmaMap.put("isSuccess", isSuccess);
			}
		}
		return logicFmaMap;
	} 
	
	/**
	 * 构造维度过滤map
	 * @param excelMap
	 * @return
	 */
	private List<Map<String,Object>> strDimMap(String cellNo, String indexNo){
		List<Map<String,Object>> filtInfos = new ArrayList<Map<String,Object>>();
		List<Map<String, String>> excelMapList = this.save_filterMap.get(cellNo);
		if (excelMapList != null && excelMapList.size() > 0) {
			for(int i = 0; i < excelMapList.size(); i++){
				Map<String, String> excelMap = excelMapList.get(i);
				Map<String,Object> dimMap = new HashMap<String, Object>();
				for (Entry<String, String> entry : this.static_filtInfoMap.entrySet()) {
					String key = entry.getKey().toString();
					String value = entry.getValue().toString();
					if(excelMap.get(key) != null){
						dimMap.put("indexNo", indexNo);
						dimMap.put(value, excelMap.get(key));
					}
				}
				filtInfos.add(dimMap);
			}
		}
		return filtInfos;
	}
	
	/**
	 * 统一转码方法将传进来的MAP的VALUE转成相对应的码值
	 * 
	 */
	private void translateDictionary(String sheetName, Map<String, String> willTransMap) {
		if(willTransMap == null){
			return;
		}else if(this.DICTIONARY_IDXFILTERDIM.equals(sheetName)) {
			String dimTypeNo = this.static_data.getDimTypeMap().get(willTransMap.get(this.DICTIONARY_DIMTYPE));
			if(StringUtils.isNotBlank(dimTypeNo)) {
				willTransMap.put(this.DICTIONARY_DIMTYPE, dimTypeNo);
			}
			String dimItemName = willTransMap.get(this.DICTIONARY_FILTERVAL);
			if (StringUtils.isNotBlank(dimItemName)){
				String[] dimItemNames = StringUtils.split(dimItemName, ',');
				for (int i = 0; i < dimItemNames.length; i ++) {
					String name = StringUtils.trimToEmpty(dimItemNames[i]);
					int pos1 = name.indexOf('(');
					if (pos1 > 0) {
						int pos2 = name.indexOf(')', pos1);
						if (pos2 > 0) {
							name = StringUtils.trimToEmpty(name.substring(pos1 + 1, pos2));
							willTransMap.put(this.DICTIONARY_FILTERVAL, name);
						}
					}
				}
			}
			String filterType = willTransMap.get(this.DICTIONARY_FILTERTYPE);
			if (StringUtils.isNotBlank(filterType)) {
				willTransMap.put(this.DICTIONARY_FILTERTYPE, FilterMode.get(filterType).toString());
			}
		}
	}
	
	/**
	 * 构造单元格信息
	 * @param templateName
	 * @return
	 */
	private List<Map<String,Object>> strIdxsArray(String templateName, String templateType){
		List<Map<String,Object>> idexArray = new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(templateType)){
			if(RPT_TMP_TYPE_DETAIL.equals(templateType)){
				idexArray = this.save_detailArray;
			}else if(RPT_TMP_TYPE_CELL.equals(templateType)){
				idexArray = this.save_idxsArray;
			}else if(RPT_TMP_TYPE_COM.equals(templateType)){
				idexArray = this.save_detailArray;
				idexArray.addAll(this.save_idxsArray);
			}
			idexArray.addAll(this.save_formulaArray);
			idexArray.addAll(this.save_expArray);
			idexArray.addAll(this.save_calcArray);
		}
		return idexArray;
	}
	
	/**
	 * 构造一般单元格信息
	 * @param excelMap
	 * @return
	 */
	private Map<String,Object> strComMap(Map<String,String> excelMap){
		Map<String,Object> comMap = new HashMap<String, Object>();
		if (excelMap != null) {
			for (Entry<String, String> entry : static_comCellMap.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				comMap.put(value, StringUtils.isEmpty(excelMap.get(key)) ? "" : excelMap.get(key));
			}
			comMap.putAll(this.ponConversion((String) comMap.get("cellNo")));
		}
		return comMap;
	}
	
	/**
	 * 构造指标单元格信息
	 * @param excelMap
	 * @return
	 */
	private Map<String,Object> strIdxMap(Map<String,String> excelMap){
		Map<String,Object> idxMap = new HashMap<String, Object>();
		if (excelMap != null) {
			for (Entry<String, String> entry : static_idxCellMap.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				idxMap.put(value, StringUtils.isEmpty(excelMap.get(key)) ? "" : excelMap.get(key));
			}
			if(idxMap.get("indexNo") != null) {
				String srcIdxNo = idxMap.get("indexNo").toString();
				int pos1 = srcIdxNo.indexOf('.');
				if (pos1 > 0) {//总账指标的度量不一样
					idxMap.put("measureNo", StringUtils.trimToEmpty(srcIdxNo.substring(pos1 + 1, srcIdxNo.length())));
					srcIdxNo = StringUtils.trimToEmpty(srcIdxNo.substring(0, pos1));
				}else {//其余指标的度量是INDEX_VAL
					idxMap.put("measureNo", "INDEX_VAL");
				}
				idxMap.put("indexNo", srcIdxNo);
				RptIdxInfo idxInfo = this.static_idxInfo.get(srcIdxNo);
				if(idxInfo != null) {
					idxMap.put("statType", idxInfo.getStatType());
				}
			}
			idxMap.putAll(this.ponConversion((String) idxMap.get("cellNo")));
			//如果是新增报表指标，生成唯一标识
			String realIndexNo = (String) idxMap.get("realIndexNo");
			String cellNo = (String) idxMap.get("cellNo");
			String cellNm = (String) idxMap.get("cellNm");
			if(StringUtils.isBlank(realIndexNo)) {
				realIndexNo = RandomUtils.uuid2();
				idxMap.put("realIndexNo", realIndexNo);
				static_newRptIdxInfo.put(cellNo, realIndexNo);//缓存下来，后面警戒值校验公式用得到
				static_newRptIdxInfo.put(this.static_template.getTemplateId() + "." + cellNm, realIndexNo);//缓存下来，后面逻辑校验公式用得到
			}
			idxMap.put("filtInfos", this.strDimMap(cellNo, realIndexNo));
			idxMap.put("allDims", this.getAllDim((String) idxMap.get("indexNo")));
			idxMap.put("factDims", GlobalConstants4plugin.DIM_TYPE_DATE_NAME + "," + GlobalConstants4plugin.DIM_TYPE_ORG_NAME + "," + GlobalConstants4plugin.DIM_TYPE_INDEXNO_NAME);//目前监管报表只有这三个查询维度
		}
		return idxMap;
	}
	
	/**
	 * 构造明细单元格信息
	 * @param excelMap
	 * @return
	 */
	private Map<String,Object> strDetalMap(Map<String,String> excelMap){
		Map<String,Object> detailMap = new HashMap<String, Object>();
		if (excelMap != null) {
			for (Entry<String, String> entry : static_modelCellMap.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				detailMap.put(value, StringUtils.isEmpty(excelMap.get(key)) ? "" : excelMap.get(key));
			}
			detailMap.putAll(this.ponConversion((String) detailMap.get("cellNo")));
			String dsName = (String) detailMap.get("dsName");
			RptSysModuleInfo module = this.static_module.get(dsName);
			if(null != module) {
				String columnName = (String) detailMap.get("columnName");
				detailMap.put("dsId", module.getSetId());
				RptSysModuleCol moduleCol = this.static_moduleCol.get(module.getSetId() + "_" + columnName);
				if(null != moduleCol) {
					detailMap.put("columnId", moduleCol.getColId());
				}
			}
		}
		return detailMap;
	}
	
	/**
	 * 构造公式单元格信息
	 * @param excelMap
	 * @return
	 */
	private Map<String,Object> strFmaMap(Map<String,String> excelMap){
		Map<String,Object> fmaMap = new HashMap<String, Object>();
		if (excelMap != null) {
			for (Entry<String, String> entry : static_fmaCellMap.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				fmaMap.put(value, StringUtils.isEmpty(excelMap.get(key)) ? "" : excelMap.get(key));
			}
			//如果是新增报表指标，生成唯一标识
			String realIndexNo = (String) fmaMap.get("realIndexNo");
			String cellNo = (String) fmaMap.get("cellNo");
			String cellNm = (String) fmaMap.get("cellNm");
			String isRptIndex = (String) fmaMap.get("isRptIndex");
			if(StringUtils.isBlank(realIndexNo) && "Y".equals(isRptIndex)) {
				realIndexNo = RandomUtils.uuid2();
				fmaMap.put("realIndexNo", realIndexNo);
				static_newRptIdxInfo.put(cellNo, realIndexNo);//缓存下来，后面警戒值校验公式用得到
				static_newRptIdxInfo.put(this.static_template.getTemplateId() + "." + cellNm, realIndexNo);//缓存下来，后面逻辑校验公式用得到
			}
			fmaMap.putAll(this.ponConversion(cellNo));
		}
		return fmaMap;
	}
	
	/**
	 * 构造表达式单元格信息
	 * @param excelMap
	 * @return
	 */
	private Map<String,Object> strExpMap(Map<String,String> excelMap){
		Map<String,Object> expMap = new HashMap<String, Object>();
		if (excelMap != null) {
			for (Entry<String, String> entry : static_expCellMap.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				expMap.put(value, StringUtils.isEmpty(excelMap.get(key)) ? "" : excelMap.get(key));
			}
			expMap.putAll(this.ponConversion((String) expMap.get("cellNo")));
		}
		return expMap;
	}
	
	/**
	 * 构造表间取数单元格
	 * @param excelMap
	 * @return
	 */
	private Map<String,Object> strCalcMap(Map<String,String> excelMap){
		Map<String,Object> calcMap = new HashMap<String, Object>();
		if (excelMap != null) {
			for (Entry<String, String> entry : static_calcCellMap.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				calcMap.put(value, StringUtils.isEmpty(excelMap.get(key)) ? "" : excelMap.get(key));
			}
			calcMap.put("factDims", GlobalConstants4plugin.DIM_TYPE_DATE_NAME + "," + GlobalConstants4plugin.DIM_TYPE_ORG_NAME + "," + GlobalConstants4plugin.DIM_TYPE_INDEXNO_NAME);//目前监管报表只有这三个查询维度
			//如果是新增报表指标，生成唯一标识
			String realIndexNo = (String) calcMap.get("realIndexNo");
			String cellNo = (String) calcMap.get("cellNo");
			String cellNm = (String) calcMap.get("cellNm");
			if(StringUtils.isBlank(realIndexNo)) {
				realIndexNo = RandomUtils.uuid2();
				calcMap.put("realIndexNo", realIndexNo);
				static_newRptIdxInfo.put(cellNo, realIndexNo);//缓存下来，后面警戒值校验公式用得到
				static_newRptIdxInfo.put(this.static_template.getTemplateId() + "." + cellNm, realIndexNo);//缓存下来，后面逻辑校验公式用得到
			}
			calcMap.putAll(this.ponConversion(cellNo));
		}
		return calcMap;
	}
	
	/**
	 * 根据单元格编号生成位置
	 * @param cellNo
	 * @return
	 */
	private Map<String,String> ponConversion(String cellNo){
		Map<String,String> fmaMap = new HashMap<String, String>();
		if(StringUtils.isNotBlank(cellNo)){
			int[] idxList = ExcelAnalyseUtils.getRowNoColumnIdx(cellNo);
			if(idxList != null && idxList.length == 2){
				fmaMap.put("rowId", Integer.toString(idxList[0]));
				fmaMap.put("colId", Integer.toString(idxList[1]));
			}
		}
		return fmaMap;
	}
	
	/**
	 * 根据指标编号获取全部维度
	 * @param indexNo
	 * @return
	 */
	private String getAllDim(String indexNo){
		String allDims = "";
		if(StringUtils.isNotBlank(indexNo)){
			int pos1 = indexNo.indexOf('.');
			if (pos1 > 0)//总账指标需要去除度量
				indexNo = StringUtils.trimToEmpty(indexNo.substring(0, pos1));
			RptIdxDimRelBS idxDimRel = SpringContextHolder.getBean("rptIdxDimRelBS");
			List<RptIdxDimRel> dimList = idxDimRel.getRptIdxDimRelByIdxno("id.indexNo", indexNo);
			if(dimList != null && dimList.size() >0){
				StringBuilder dim = new StringBuilder();
				for(RptIdxDimRel dimRel : dimList){
					dim.append(dimRel.getId().getDimNo() + ",");
				}
				allDims = dim.toString();
				allDims.substring(0, allDims.length() - 1);
			}
		}
		return allDims;
	}
	
	/**
	 * 构造报表模板
	 * @param templateName
	 * @return
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	private String strTempJson(String filePath) throws IOException{
		File file = null;
		file = new File(filePath);
		IExcelReader reader = ExcelReaderFactory.createReader(file);
		String ret = reader.readTmpToString();
		//String ret = static_loadData.getExcelEventReaderMap().get(filePath).getSheetJson(filePath, this.DICTIONARY_TEMPLATEBASE);
		return ret;
	}
	
	/**
	 * 缓存所有非报表指标信息
	 */
	private void cacheIdxInfoMap() {
		IdxInfoBS idxInfoBS  = SpringContextHolder.getBean("idxInfoBS");
		String[] propertyNames =  {"endDate","isRptIndex"};
		String[] values =  {"29991231","N"};
		List<RptIdxInfo> idxList = idxInfoBS.findByPropertys(RptIdxInfo.class, propertyNames, values);
		if(idxList != null) {
			for(RptIdxInfo idx : idxList) {
				this.static_idxInfo.put(idx.getId().getIndexNo(), idx);
			}
		}
	}
	
	/**
	 * 缓存所有报表目录信息
	 */
	private void cacheCalalogMap() {
		RptTmpBS rptTmpBS  = SpringContextHolder.getBean("rptTmpBS");
		List<RptMgrReportCatalog> catalogList = rptTmpBS.getCatalogByParam(null, null, null, GlobalConstants4plugin.RPT_TYPE_DESIGN, null);
		if((null != catalogList) && (catalogList.size() > 0)) {
			for(RptMgrReportCatalog catalog : catalogList) {
				static_catalogIdMap.put(catalog.getUpCatalogId() + catalog.getCatalogNm(), catalog.getCatalogId());
			}
		}
	}
	
	/**
	 * 缓存所有明细模型信息
	 */
	private void cacheModuleMap() {
		IdxInfoBS idxInfoBS  = SpringContextHolder.getBean("idxInfoBS");
		String[] propertyNames =  {"setType"};
		String[] values =  {"00"};
		List<RptSysModuleInfo> moduleList = idxInfoBS.findByPropertys(RptSysModuleInfo.class, propertyNames, values);
		List<String> setIdList = new ArrayList<String>();
		if(moduleList != null) {
			for(RptSysModuleInfo module : moduleList) {
				setIdList.add(module.getSetId());
				this.static_module.put(module.getSetNm(), module);//缓存所有明细模型信息
			}
		}
		if(setIdList.size() > 0) {
			List<RptSysModuleCol> moduleColList = idxInfoBS.getEntityListByProperty(RptSysModuleCol.class, "setId", setIdList);
			if(moduleColList != null) {
				for(RptSysModuleCol moduleCol : moduleColList) {
					this.static_moduleCol.put(moduleCol.getSetId() + "_" + moduleCol.getCnNm(), moduleCol);//缓存所有明细模型字段信息
				}
			}
		}
	}
	
	/**
	 * 根据报表全路径获取报表目录ID
	 * @param catalogUrl
	 * @return
	 */
	private String getCatalogId(String catalogUrl) {
		String[] catalogs =  catalogUrl.split("/");
		if(catalogs.length > 0) {
			String catalogId = "0";
			for(int i=0; i < catalogs.length; i++) {
				String upCatalogId = catalogId;
				catalogId = static_catalogIdMap.get(upCatalogId + catalogs[i]);
				if(StringUtils.isBlank(catalogId)) {
					catalogId = saveCatalog(catalogs[i], upCatalogId);
				}
			}
			return catalogId;
		}
		return "";
	}
	
	/**
	 * 保存系统目前不存在的目录
	 * @param catalogNm
	 * @param upCatalogId
	 * @return
	 */
	private String saveCatalog(String catalogNm, String upCatalogId) {
		String catalogId = RandomUtils.uuid2();
		RptMgrReportCatalog catalog = new RptMgrReportCatalog();
		catalog.setCatalogId(catalogId);
		catalog.setUpCatalogId(upCatalogId);
		catalog.setCatalogNm(catalogNm);
		catalog.setExtType(GlobalConstants4plugin.RPT_TYPE_DESIGN);
		catalog.setDefSrc(GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		RptMgrInfoBS rptMgrInfoBS  = SpringContextHolder.getBean("rptMgrInfoBS");
		rptMgrInfoBS.saveOrUpdateEntity(catalog);
		static_catalogIdMap.put(catalog.getUpCatalogId() + catalog.getCatalogNm(), catalog.getCatalogId());
		return catalogId;
	}
	
	/**
	 * 构造报表信息中英文对应map
	 */
	private void strBaseMap(){
		static_rptBaseMap.put("报表编号", "rptNum");
		static_rptBaseMap.put("报表名称", "rptNm");
		static_rptBaseMap.put("版本号", "verId");
		static_rptBaseMap.put("报表目录", "catalogNm");
		static_rptBaseMap.put("报表目录ID", "catalogId");
		static_rptBaseMap.put("创建时间戳", "createTimeStr");
//		static_rptBaseMap.put("创建时间", "createTime");
		static_rptBaseMap.put("定义类型", "defSrc");
		static_rptBaseMap.put("扩展类型", "extType");
		static_rptBaseMap.put("模板类型", "templateType");
		static_rptBaseMap.put("模板ID", "templateId");
		static_rptBaseMap.put("升级概况", "templateNm");
		static_rptBaseMap.put("报表单位", "templateUnit");
		static_rptBaseMap.put("启用日期", "verStartDate");
		static_rptBaseMap.put("报表周期", "rptCycle");
		static_rptBaseMap.put("报表状态", "rptSts");
		static_rptBaseMap.put("报表描述", "rptDesc");
		static_rptBaseMap.put("业务类型", "busiType");
		static_rptBaseMap.put("是否下发提交", "isReleaseSubmit");
		static_rptBaseMap.put("是否数据修改", "isUpt");
		static_rptBaseMap.put("是否主表", "isMainRpt");
		static_rptBaseMap.put("对应主表名称", "mainRptNm");
		static_rptBaseMap.put("最大版本", "maxVerId");
		static_rptBaseMap.put("排列顺序", "rankOrder");
		static_rptBaseMap.put("上报编码", "reportCode");
		static_rptBaseMap.put("报表ID", "rptId");
		static_rptBaseMap.put("报表类型", "rptType");
		static_rptBaseMap.put("报送主体", "submitMain");
		static_rptBaseMap.put("报送范围", "submitRange");
		static_rptBaseMap.put("定义部门", "deptName");
		static_rptBaseMap.put("填报说明", "fillDesc");
		static_rptBaseMap.put("制度表样版本", "tmpVersionId");
		static_rptBaseMap.put("报表业务名称", "rptBusiNm");
	}
	
	/**
	 * 构造指标单元格信息中英文对应map
	 */
	private void strIdxCellMap(){
		static_idxCellMap.put("单元格类型", "cellType");
		static_idxCellMap.put("单元格类型名称", "cellTypeNm");
		static_idxCellMap.put("单元格编号", "cellNo");
		static_idxCellMap.put("单元格名称", "cellNm");
		static_idxCellMap.put("是否数据修改", "isUpt");//Y
		static_idxCellMap.put("是否为空", "isNull");//Y
		static_idxCellMap.put("是否汇总", "isSum");
		static_idxCellMap.put("数据长度", "dataLen");//
		static_idxCellMap.put("数据精度", "dataPrecision");
		static_idxCellMap.put("业务口径", "caliberExplain");//
		static_idxCellMap.put("技术口径", "caliberTechnology");//
		static_idxCellMap.put("行号", "rowId");
		static_idxCellMap.put("列号", "colId");
		static_idxCellMap.put("显示格式", "displayFormat");
		static_idxCellMap.put("数据单位", "dataUnit");
		static_idxCellMap.put("报表指标编号", "realIndexNo");//
		static_idxCellMap.put("来源指标编号", "indexNo");
		static_idxCellMap.put("来源指标名称", "indexNm");
		static_idxCellMap.put("指标版本号", "indexVerId");
		static_idxCellMap.put("度量编号", "measureNo");//
		static_idxCellMap.put("统计类型", "statType");//来源指标统计类型
		static_idxCellMap.put("是否减维", "isSubDim");//N
		static_idxCellMap.put("是否筛选维度", "isFiltDim");//N
		static_idxCellMap.put("全部维度", "allDims");//来源指标关联维度
		static_idxCellMap.put("实际维度", "factDims");//指标维度关联表
		static_idxCellMap.put("维度过滤明细 JSON", "filtInfos");
		static_idxCellMap.put("计算规则", "ruleId");
		static_idxCellMap.put("时间度量", "timeMeasureId");
		static_idxCellMap.put("取值方式", "modeId");
		static_idxCellMap.put("URL", "dbClkUrl");//   /report/frame/design/cfg/dbclk/module/idx
		static_idxCellMap.put("宽度", "dialogWidth");//70%
		static_idxCellMap.put("高度", "dialogHeight");//65%
		static_idxCellMap.put("数据类型", "dataType");//
		static_idxCellMap.put("真实指标名称", "realIndexNm");//
		static_idxCellMap.put("是否落地", "isSave");//
		static_idxCellMap.put("业务类型", "busiType");//
		static_idxCellMap.put("数据源ID", "dsId");//
		static_idxCellMap.put("公式", "excelFormula");//
		static_idxCellMap.put("是否跨年", "isPassyear");
		static_idxCellMap.put("人行编码", "busiNo");//
		static_idxCellMap.put("备注说明", "remark");
		static_filtInfoMap.put("维度类型", "dimNo");
		static_filtInfoMap.put("过滤类型", "filterMode");
		static_filtInfoMap.put("过滤信息", "filterVal");
	}
	
	/**
	 * 构造明细单元格信息中英文对应map
	 */
	private void strModelCellMap(){
		static_modelCellMap.put("单元格类型", "cellType");
		static_modelCellMap.put("单元格类型名称", "cellTypeNm");
		static_modelCellMap.put("单元格编号", "cellNo");
		static_modelCellMap.put("人行编码", "busiNo");
		static_modelCellMap.put("单元格名称", "cellNm");
		static_modelCellMap.put("是否数据修改", "isUpt");//Y
		static_modelCellMap.put("是否为空", "isNull");//Y
		static_modelCellMap.put("是否汇总", "isSum");
		static_modelCellMap.put("数据长度", "dataLen");//
		static_modelCellMap.put("数据精度", "dataPrecision");
		static_modelCellMap.put("业务口径", "caliberExplain");//
		static_modelCellMap.put("技术口径", "caliberTechnology");//
		static_modelCellMap.put("行号", "rowId");
		static_modelCellMap.put("列号", "colId");
		static_modelCellMap.put("seq", "seq");
		static_modelCellMap.put("显示格式", "displayFormat");
		static_modelCellMap.put("数据单位", "dataUnit");
		static_modelCellMap.put("数据源ID", "dsId");//
		static_modelCellMap.put("模型名称", "dsName");
		static_modelCellMap.put("列ID", "columnId");
		static_modelCellMap.put("字段名称", "columnName");
		static_modelCellMap.put("是否扩展", "isExt");
		static_modelCellMap.put("扩展方向", "extDirection");
		static_modelCellMap.put("扩展方式", "extMode");
		static_modelCellMap.put("是否排序", "isSort");
		static_modelCellMap.put("排序方式", "sortMode");
		static_modelCellMap.put("排序顺序", "sortOrder");
		static_modelCellMap.put("排序字段类型", "sortDbType");
		static_modelCellMap.put("过滤", "filter");
		static_modelCellMap.put("是否转码", "isConver");
		static_modelCellMap.put("URL", "dbClkUrl");//   /report/frame/design/cfg/dbclk/module/idx
		static_modelCellMap.put("宽度", "dialogWidth");//70%
		static_modelCellMap.put("高度", "dialogHeight");//65%
		static_modelCellMap.put("数据类型", "dataType");//
		static_modelCellMap.put("汇总方式", "sumMode");
		static_modelCellMap.put("公式", "excelFormula");//
		static_modelCellMap.put("备注说明", "remark");
	}
	
	/**
	 * 构造公式单元格信息中英文对应map
	 */
	private void strFmaCellMap(){
		static_fmaCellMap.put("单元格类型", "cellType");
		static_fmaCellMap.put("单元格类型名称", "cellTypeNm");
		static_fmaCellMap.put("单元格编号", "cellNo");
		static_fmaCellMap.put("人行编码", "busiNo");//
		static_fmaCellMap.put("单元格名称", "cellNm");
		static_fmaCellMap.put("是否数据修改", "isUpt");//Y
		static_fmaCellMap.put("是否为空", "isNull");//Y
		static_fmaCellMap.put("是否汇总", "isSum");//N
		static_fmaCellMap.put("数据长度", "dataLen");//
		static_fmaCellMap.put("数据精度", "dataPrecision");
		static_fmaCellMap.put("业务口径", "caliberExplain");//
		static_fmaCellMap.put("技术口径", "caliberTechnology");//
		static_fmaCellMap.put("行号", "rowId");
		static_fmaCellMap.put("列号", "colId");
		static_fmaCellMap.put("显示格式", "displayFormat");
		static_fmaCellMap.put("数据单位", "dataUnit");
		static_fmaCellMap.put("排序方式", "sortMode");//01
		static_fmaCellMap.put("计算公式", "excelFormula");
		static_fmaCellMap.put("是否分析扩展", "isAnalyseExt");
		static_fmaCellMap.put("扩展类型", "analyseExtType");
		static_fmaCellMap.put("是否报表指标", "isRptIndex");
		static_fmaCellMap.put("报表指标编号", "realIndexNo");
		static_fmaCellMap.put("数据类型", "dataType");//
		static_fmaCellMap.put("指标名称", "realIndexNm");//
		static_fmaCellMap.put("备注说明", "remark");
	}
	
	/**
	 * 构造表达式单元格信息中英文对应map
	 */
	private void strExpCellMap(){
		static_expCellMap.put("单元格类型", "cellType");
		static_expCellMap.put("单元格类型名称", "cellTypeNm");
		static_expCellMap.put("单元格编号", "cellNo");
		static_expCellMap.put("人行编码", "busiNo");//
		static_expCellMap.put("单元格名称", "cellNm");
		static_expCellMap.put("是否数据修改", "isUpt");//Y
		static_expCellMap.put("是否为空", "isNull");//Y
		static_expCellMap.put("是否汇总", "isSum");//N
		static_expCellMap.put("数据长度", "dataLen");//
		static_expCellMap.put("数据精度", "dataPrecision");//
		static_expCellMap.put("业务口径", "caliberExplain");//
		static_expCellMap.put("技术口径", "caliberTechnology");//
		static_expCellMap.put("行号", "rowId");
		static_expCellMap.put("列号", "colId");
		static_expCellMap.put("表达式内容", "expression");
		static_expCellMap.put("URL", "dbClkUrl");// /report/frame/design/cfg/sysVarSet
		static_expCellMap.put("宽度", "dialogWidth");//80%
		static_expCellMap.put("高度", "dialogHeight");//82%
		static_expCellMap.put("数据类型", "dataType");//
		static_expCellMap.put("显示格式", "displayFormat");//
		static_expCellMap.put("数据单位", "dataUnit");//
		static_expCellMap.put("公式", "excelFormula");//
	}

	/**
	 * 构造一般单元格信息中英文对应map
	 */
	private void strComCellMap(){
		static_comCellMap.put("单元格类型", "cellType");
		static_comCellMap.put("单元格类型名称", "cellTypeNm");
		static_comCellMap.put("单元格编号", "cellNo");
		static_comCellMap.put("人行编码", "busiNo");//
		static_comCellMap.put("单元格名称", "cellNm");
		static_comCellMap.put("是否数据修改", "isUpt");//Y
		static_comCellMap.put("是否为空", "isNull");//Y
		static_comCellMap.put("是否汇总", "isSum");//N
		static_comCellMap.put("数据长度", "dataLen");//
		static_comCellMap.put("数据精度", "dataPrecision");//
		static_comCellMap.put("业务口径", "caliberExplain");//
		static_comCellMap.put("技术口径", "caliberTechnology");//
		static_comCellMap.put("行号", "rowId");
		static_comCellMap.put("列号", "colId");
		static_comCellMap.put("单元格属性", "typeId");
		static_comCellMap.put("单元格内容", "content");
	}
	
	/**
	 * 构造表间计算单元格信息中英文对应map
	 */
	private void strCalcCellMap() {
		static_calcCellMap.put("单元格类型", "cellType");
		static_calcCellMap.put("单元格类型名称", "cellTypeNm");
		static_calcCellMap.put("单元格编号", "cellNo");
		static_calcCellMap.put("单元格名称", "cellNm");
		static_calcCellMap.put("是否数据修改", "isUpt");//Y
		static_calcCellMap.put("是否为空", "isNull");//Y
		static_calcCellMap.put("是否汇总", "isSum");
		static_calcCellMap.put("数据精度", "dataPrecision");
		static_calcCellMap.put("业务口径", "caliberExplain");//
		static_calcCellMap.put("技术口径", "caliberTechnology");//
		static_calcCellMap.put("行号", "rowId");
		static_calcCellMap.put("列号", "colId");
		static_calcCellMap.put("显示格式", "displayFormat");
		static_calcCellMap.put("数据单位", "dataUnit");
		static_calcCellMap.put("报表指标编号", "realIndexNo");//
		static_calcCellMap.put("来源指标编号", "indexNo");
		static_calcCellMap.put("seq", "seq");
		static_calcCellMap.put("实际维度", "formulaDims");//指标维度关联表
		static_calcCellMap.put("URL", "dbClkUrl");//   
		static_calcCellMap.put("宽度", "dialogWidth");//
		static_calcCellMap.put("高度", "dialogHeight");//
		static_calcCellMap.put("数据类型", "dataType");//
		static_calcCellMap.put("公式", "excelFormula");//
		static_calcCellMap.put("人行编码", "busiNo");//
		static_calcCellMap.put("公式信息", "formulaDesc");//
		static_calcCellMap.put("公式配置信息", "formulaContent");//
		static_calcCellMap.put("备注说明", "remark");
	}
	
	/**
	 * 构造逻辑校验公式中英文对应map
	 */
	private void strLogicMap(){
		static_rptLogicMap.put("容差值", "floatVal");
		static_rptLogicMap.put("开始日期", "startDate");
		static_rptLogicMap.put("结束日期", "endDate");
		static_rptLogicMap.put("是否预校验", "isPre");
		static_rptLogicMap.put("校验规则ID", "checkId");
		static_rptLogicMap.put("完整校验公式", "expressionDesc");
		static_rptLogicMap.put("逻辑运算类型", "logicOperType");
		static_rptLogicMap.put("左侧公式明细", "leftExpression");
		static_rptLogicMap.put("右侧公式明细", "rightExpression");
		static_rptLogicMap.put("公式", "expressionDesc");
	}
	
	/**
	 * 构造警戒校验公式中英文对应map
	 */
	private void strWarnMap(){
		//static_rptWarnMap.put("开始日期", "startDate");
		//static_rptWarnMap.put("结束日期", "endDate");
		static_rptWarnMap.put("备注", "remark");
		static_rptWarnMap.put("预警类型", "compareType");
		static_rptWarnMap.put("监管要求", "isFrs");
		/*static_rptWarnMap.put("比较值类型", "compareValType");
		static_rptWarnMap.put("幅度类型", "rangeType");*/
		static_rptWarnMap.put("模板ID", "rptTemplateId");
		static_rptWarnMap.put("报表指标编号", "indexNo");
		static_rptWarnMap.put("校验规则ID", "checkId");
		static_warnLevelMap.put("ID", "id");
		/*static_warnLevelMap.put("提醒名称", "levelNm");
		static_warnLevelMap.put("警戒值类型", "levelType");*/
		static_warnLevelMap.put("最小比率", "minusRangeVal");
		static_warnLevelMap.put("最大比率", "postiveRangeVal");
		/*static_warnLevelMap.put("提醒颜色", "remindColor");
		static_warnLevelMap.put("通过条件", "isPassCond");*/
	}
	
}
