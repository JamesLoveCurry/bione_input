package com.yusys.bione.plugin.regulation.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.DefaultHandler;

import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.util.excel.ExcelAnalyseUtils;
import com.yusys.bione.frame.util.excel.ExcelEventListener;
import com.yusys.bione.frame.util.excel.ExcelEventReader;
import com.yusys.bione.plugin.regulation.enums.BusiType;
import com.yusys.bione.plugin.regulation.enums.CellStatus;
import com.yusys.bione.plugin.regulation.enums.CompareType;
import com.yusys.bione.plugin.regulation.enums.DataUnit;
import com.yusys.bione.plugin.regulation.enums.DisplayFormat;
import com.yusys.bione.plugin.regulation.enums.ElementType;
import com.yusys.bione.plugin.regulation.enums.ExtendDirection;
import com.yusys.bione.plugin.regulation.enums.ExtendMode;
import com.yusys.bione.plugin.regulation.enums.ExtendType;
import com.yusys.bione.plugin.regulation.enums.FilterMode;
import com.yusys.bione.plugin.regulation.enums.IsFrs;
import com.yusys.bione.plugin.regulation.enums.RptCycle;
import com.yusys.bione.plugin.regulation.enums.SortMode;
import com.yusys.bione.plugin.regulation.enums.SumMode;
import com.yusys.bione.plugin.regulation.enums.TemplateType;
import com.yusys.bione.plugin.regulation.enums.YesOrNo;
import com.yusys.bione.plugin.regulation.enums.ComCellType;
import com.yusys.bione.plugin.regulation.vo.Data;
import com.yusys.bione.plugin.regulation.vo.FormulaCell;
import com.yusys.bione.plugin.regulation.vo.FormulaIndex;
import com.yusys.bione.plugin.regulation.vo.Index;
import com.yusys.bione.plugin.regulation.vo.IndexBaseCell;
import com.yusys.bione.plugin.regulation.vo.IndexCell;
import com.yusys.bione.plugin.regulation.vo.Template;

public class LoadData extends DefaultHandler implements Closeable, ExcelEventListener {

	private static Logger logger = LoggerFactory.getLogger(LoadData.class);

	private String excelFileDir;

	private Data data;

	private StringBuilder errorMessageBuilder;

	private Set<String> errorModelCellNoSet;

	private int errorCount;

	private Pattern cellNoRegionPattern = Pattern.compile("([a-zA-Z]+\\d+)\\s*:\\s*([a-zA-Z]+\\d+)");

	private Pattern indexNoPattern = Pattern.compile("I\\('([^']+)'\\)|I\\(\"([^\"]+)\"\\)");

	/**
	 * 是否允许报表公式和校验公式中引用空单元格或一般单元格
	 */
	private boolean emptyCellInFormula = true;

	/**
	 * 是否允许报表公式和校验公式中引用空指标单元格
	 */
	private boolean emptyIndexInFormula = true;

	/**
	 * 是否允许对空单元格或一般单元格进行预警校验
	 */
	private boolean emptyCellInVerifyWarn = true;

	private Map<String, ExcelEventReader> excelEventReaderMap = new HashMap<String, ExcelEventReader>();

	/**
	 * filePath=>Template
	 */
	private Map<String, Template> templateFileMap = new HashMap<String, Template>();

	/**新增报表map
	 * filePath=>Template
	 */
	private Map<String, Template> newTemplateMap = new HashMap<String, Template>();
	
	/**
	 * 单元格信息页的列信息：Template=>单元格类型=>列索引号=>列名称<br>
	 * 校验公式等信息页的列信息：Template=>表单名称=>列索引号=>列名称
	 */
	private Map<Template, Map<String, Map<Integer, String>>> allColumnMap = new HashMap<Template, Map<String, Map<Integer, String>>>();

	private String columnType;

	/**
	 * 加载Excel时的中间临时数据，指标列表，以indexNo排序
	 */
	private List<Index> indexList;

	/**
	 * 加载Excel时的中间临时数据，可复用的原报表指标单元格列表
	 */
	private List<IndexCell> updatingIndexCellList;
	
	/**
	 * 加载Excel时的中间临时数据，可复用的原报表列表指标单元格列表
	 */
	private List<IndexCell> updatingListCellList;

	/**
	 * 加载Excel时的中间临时数据，可复用的原报表公式单元格列表
	 */
	private List<FormulaCell> updatingFormulaCellList;

	/**
	 * 加载Excel时的中间临时数据，键值"rowCount"和"columnCount"对应到模板信息页行数和列数，其他是模板信息页cellNo=>单元格内容
	 */
	private Map<String, String> modelSheetMap;

	/**
	 * 加载Excel时的中间临时数据，单元格信息页的SPAN单元格列表，格式诸如"A1:B2"等等
	 */
	private List<String> spanRegionList;

	/**
	 * 加载Excel时的中间临时数据，列索引值=>单元格值
	 */
	private Map<Integer, String> tempColumnIdxToDataMap;

	/**
	 * 加载Excel报表信息页时的中间临时数据，第2列单元格值=>第3列单元格值
	 */
	private Map<String, String> tempDataMap;

	public LoadData(String excelFileDir, Map<String, String> optionMap) {
		emptyCellInFormula = StringUtils.isNotEmpty(optionMap.get("emptyCellInFormula"));
		emptyIndexInFormula = StringUtils.isNotEmpty(optionMap.get("emptyIndexInFormula"));
		emptyCellInVerifyWarn = StringUtils.isNotEmpty(optionMap.get("emptyCellInVerifyWarn"));
		errorMessageBuilder = new StringBuilder();
		errorModelCellNoSet = new HashSet<String>();
		this.excelFileDir = excelFileDir;
		this.data = new Data();
	}

	/**
	 * 打印警告信息
	 * 
	 * @param message 警告信息
	 * @param template 模板对象
	 * @param cellNo Excel文件单元格号
	 */
	private void logWarn(String message, Template template, String excelCellNo) {
		StringBuilder sb = new StringBuilder();
		sb.append("[WARN]");
		sb.append(message);
		if (template != null) {
			sb.append('[').append(template.getRptNm()).append(']');
		}
		if (excelCellNo == null) {
			if (template.getFilePath() != null) {
				sb.append("缺少配置");
			}
		} else {
			sb.append('[').append(excelCellNo).append(']');
		}
		logger.warn(sb.toString());
	}

	/**
	 * 报告错误信息
	 * 
	 * @param message 出错信息
	 * @param template 模板对象
	 * @param cellNo Excel文件单元格号
	 * @param refTemplate 被引用的模板
	 * @param modelCellNo 模板sheet页上对应单元格
	 */
	public void reportError(String message, Template template, String excelCellNo, Template refTemplate,
			String modelCellNo) {
		if (errorCount >= 1024) {
			return;
		}
		if (StringUtils.isNotEmpty(modelCellNo)) {
			String key = null;
			if (refTemplate != null) {
				key = refTemplate.getTemplateId() + modelCellNo;
			} else if (template != null) {
				key = template.getTemplateId() + modelCellNo;
			}
			if (key != null) {
				if (errorModelCellNoSet.contains(key)) {
					return;
				}
				errorModelCellNoSet.add(key);
			}
		}
		errorCount ++;
		StringBuilder sb = new StringBuilder();
		sb.append("[ERROR]");
		sb.append(message);
		if (template != null) {
			sb.append('[').append(template.getRptNm()).append(']');
		}
		if (excelCellNo == null) {
			if (template != null && template.getFilePath() != null) {
				sb.append("缺少配置");
			}
		} else {
			sb.append('[').append(excelCellNo).append(']');
		}
		errorMessageBuilder.append("<li>").append(sb).append("</li>");
		logger.error(sb.toString());
	}

	/**
	 * 报告错误信息
	 * 
	 * @param message 出错信息
	 * @param template 模板对象
	 * @param cellNo Excel文件单元格号
	 * @param modelCellNo 模板sheet页上对应单元格
	 */
	public void reportError(String message, Template template, String excelCellNo, String modelCellNo) {
		reportError(message, template, excelCellNo, null, modelCellNo);
	}

	/**
	 * 报告错误信息
	 * 
	 * @param message 出错信息
	 * @param template 模板对象
	 * @param map Excel行值
	 * @param keyName 出错处键值
	 * @param refTemplate 被引用的模板
	 * @param modelCellNo 模板sheet页上对应单元格
	 */
	public void reportError(String message, Template template, Map<String, String> map, String keyName,
			Template refTemplate, String modelCellNo) {
		reportError(message, template, map.get("_REGULATION_" + keyName), refTemplate, modelCellNo);
	}

	/**
	 * 报告错误信息
	 * 
	 * @param message 出错信息
	 * @param template 模板对象
	 * @param map Excel行值
	 * @param keyName 出错处键值
	 * @param modelCellNo 模板sheet页上对应单元格
	 */
	public void reportError(String message, Template template, Map<String, String> map, String keyName,
			String modelCellNo) {
		reportError(message, template, map, keyName, null, modelCellNo);
	}

	/**
	 * 报告错误信息
	 * 
	 * @param message 出错信息
	 * @param template 模板对象
	 * @param map Excel行值
	 * @param keyName 出错处键值
	 */
	public void reportError(String message, Template template, Map<String, String> map, String keyName) {
		reportError(message, template, map, keyName, null, null);
	}

	private String buildIndexNm(Template template, String cellNo, String cellNm) {
		if(StringUtils.isNotBlank(cellNm)) {
			return cellNm;
		}
		//如果单元格名称没填，默认是报表编号.单元格编号
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.isEmpty(template.getRptNum()) ? template.getRptNm() : template.getRptNum());
		sb.append('.');
		sb.append(cellNo);
		return sb.toString();
	}

	/**
	 * 加载数据字典
	 * @param conn 数据库链接
	 * @throws SQLException
	 */
	public void initDictionary(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		Map<String, String> busiLineMap = new HashMap<String, String>();
		ResultSet rst = st.executeQuery("SELECT line_id,line_nm FROM rpt_mgr_busi_line");
		while (rst.next()) {
			busiLineMap.put(StringUtils.trimToEmpty(rst.getString("line_nm")),
					StringUtils.trimToEmpty(rst.getString("line_id")));
		}
		rst.close();
		busiLineMap.put("", "");
		data.setBusiLineMap(busiLineMap);

		Map<String, String> comCellTypeMap = new HashMap<String, String>();
		rst = st.executeQuery("SELECT type_id,type_nm FROM rpt_design_comcell_type");
		while (rst.next()) {
			comCellTypeMap.put(StringUtils.trimToEmpty(rst.getString("type_nm")),
					StringUtils.trimToEmpty(rst.getString("type_id")));
		}
		rst.close();
		comCellTypeMap.put("", "");
		data.setComCellTypeMap(comCellTypeMap);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT t.dim_type_no,t.dim_type_nm,i.dim_item_no,i.dim_item_nm FROM rpt_dim_type_info t");
		sb.append(" LEFT JOIN rpt_dim_item_info i ON i.dim_type_no=t.dim_type_no");
		Map<String, String> dimTypeMap = new HashMap<String, String>();
		Map<String, Map<String, String>> dimItemMap = new HashMap<String, Map<String, String>>();
		rst = st.executeQuery(sb.toString());
		while (rst.next()) {
			String dimTypeNo = StringUtils.trimToEmpty(rst.getString("dim_type_no"));
			String dimTypeNm = StringUtils.trimToEmpty(rst.getString("dim_type_nm"));
			String dimItemNo = StringUtils.trimToEmpty(rst.getString("dim_item_no"));
			String dimItemNm = StringUtils.trimToEmpty(rst.getString("dim_item_nm"));
			if (! dimTypeMap.containsKey(dimTypeNm)) {
				dimTypeMap.put(dimTypeNm, dimTypeNo);
			}
			if (StringUtils.isNotEmpty(dimItemNm)) {
				Map<String, String> itemMap = dimItemMap.get(dimTypeNo);
				if (itemMap == null) {
					itemMap = new HashMap<String, String>();
					dimItemMap.put(dimTypeNo, itemMap);
				}
				itemMap.put(dimItemNo, dimItemNm);
			}
		}
		rst.close();
		data.setDimTypeMap(dimTypeMap);
		data.setDimItemMap(dimItemMap);

		rst = st.executeQuery("SELECT rule_id,rule_nm FROM rpt_idx_calc_rule");
		while (rst.next()) {
			data.getIdxCalcRuleMap().put(rst.getString("rule_nm"), rst.getString("rule_id"));
		}
		rst.close();
		data.getIdxCalcRuleMap().put("", "");

		rst = st.executeQuery("SELECT time_measure_id,measure_nm FROM rpt_idx_time_measure");
		while (rst.next()) {
			data.getIdxTimeMeasureMap().put(rst.getString("measure_nm"), rst.getString("time_measure_id"));
		}
		rst.close();
		data.getIdxTimeMeasureMap().put("", "");
		
		rst = st.executeQuery("SELECT mode_id,mode_nm FROM rpt_idx_val_type");
		while (rst.next()) {
			data.getIdxValTypeMap().put(rst.getString("mode_nm"), rst.getString("mode_id"));
		}
		rst.close();
		data.getIdxValTypeMap().put("", "");

		rst = st.executeQuery("SELECT measure_no FROM rpt_idx_measure_info");
		while (rst.next()) {
			data.getMeasureNoSet().add(rst.getString("measure_no"));
		}
		rst.close();

		st.close();
	}

	/**
	 * 从数据库中加载报表模板，并初始化Data的templateMap、cellNoToCellMap和cellNmToCellMap
	 * 
	 * @param conn 数据库链接
	 * @return 报表模板数组，以templateId排序
	 * @throws SQLException
	 */
	private Template[] loadTemplateFromDB(Connection conn) throws SQLException {
		Map<String, Template> templateMap = new HashMap<String, Template>();
		Map<Template, Map<String, IndexBaseCell>> cellNoToCellMap = 
				new HashMap<Template, Map<String, IndexBaseCell>>();
		Map<Template, Map<String, IndexBaseCell>> cellNmToCellMap = 
				new HashMap<Template, Map<String, IndexBaseCell>>();

		Statement st = conn.createStatement();
		StringBuilder sb = new StringBuilder();

		List<Template> templateList = new ArrayList<Template>();
		// 相同模板号，只取最高版本的模板
		// 按照templateId排序
		sb.append("SELECT t.template_id,t.ver_id,r.rpt_nm,r.rpt_num,r.catalog_id,r.rpt_id");
		sb.append(" FROM rpt_design_tmp_info t,rpt_mgr_report_info r,(");
		sb.append("SELECT template_id,MAX(ver_id) AS ver_id FROM rpt_design_tmp_info GROUP BY template_id");
		sb.append(") m");
		sb.append(" WHERE t.template_id=m.template_id AND t.ver_id=m.ver_id");
		sb.append(" AND r.cfg_id=t.template_id ORDER BY template_id");
		ResultSet rst = st.executeQuery(sb.toString());
		while (rst.next()) {
			String rptNm = StringUtils.trimToEmpty(rst.getString("rpt_nm"));
			String rptNum = StringUtils.trimToEmpty(rst.getString("rpt_num"));
			String templateId = StringUtils.trimToEmpty(rst.getString("template_id"));
			Template template = new Template();
			template.setTemplateId(templateId);
			template.setVerId(rst.getInt("ver_id"));
			template.setRptNm(rptNm);
			template.setRptNum(rptNum);
			template.setCatalogId(StringUtils.trimToEmpty(rst.getString("catalog_id")));
			template.setRptId(StringUtils.trimToEmpty(rst.getString("rpt_id")));
			templateList.add(template);
			if (StringUtils.isNotEmpty(rptNm)) {
				if (templateMap.containsKey(rptNm)) {
					logWarn("数据库记录，报表名称、报表编号重复", template, null);
				} else {
					templateMap.put(rptNm, template);
				}
			}
			if (StringUtils.isNotEmpty(rptNum) && ! rptNum.equals(rptNm)) {
				if (templateMap.containsKey(rptNum)) {
					logWarn("数据库记录，报表名称、报表编号重复", template, null);
				} else {
					templateMap.put(rptNum, template);
				}
			}
			cellNoToCellMap.put(template, new HashMap<String, IndexBaseCell>());
			cellNmToCellMap.put(template, new HashMap<String, IndexBaseCell>());
		}
		rst.close();
		st.close();
		data.setTemplateMap(templateMap);
		data.setCellNoToCellMap(cellNoToCellMap);
		data.setCellNmToCellMap(cellNmToCellMap);
		Template[] templates = templateList.toArray(new Template[0]);
		Arrays.sort(templates);
		return templates;
	}

	/**
	 * 从数据库中加载报表目录信息
	 * @param conn
	 * @throws SQLException
	 */
	private void loadRptCataLogFromDB(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		StringBuilder sb = new StringBuilder();
		Map<String, String> rptCataLogMap= new HashMap<String, String>();
		sb.append("SELECT t.catalog_id,t.catalog_nm");
		sb.append(" FROM rpt_mgr_report_catalog t");
		sb.append(" WHERE t.ext_type = '02'");
		ResultSet rst = st.executeQuery(sb.toString());
		while (rst.next()) {
			String catalogId = StringUtils.trimToEmpty(rst.getString("catalog_id"));
			String catalogNm = StringUtils.trimToEmpty(rst.getString("catalog_nm"));
			rptCataLogMap.put(catalogNm, catalogId);
		}
		rst.close();
		st.close();
		data.setRptCataLogMap(rptCataLogMap);
	}
	
	/**
	 * 从数据库中加载指标信息
	 * 
	 * @param conn 数据库链接
	 * @return 指标列表，以indexNo排序
	 * @throws SQLException
	 */
	private List<Index> loadIndexFromDB(Connection conn) throws SQLException {
		List<Index> indexList = new ArrayList<Index>();
		Statement st = conn.createStatement();
		// 相同指标号，只取最高版本的指标
		// 按照indexNo排序
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT i.* FROM rpt_idx_info i, (");
		sb.append("SELECT index_no,MAX(index_ver_id) AS index_ver_id FROM rpt_idx_info WHERE index_sts='Y' GROUP BY index_no");
		sb.append(") t WHERE i.index_no=t.index_no AND i.index_ver_id=t.index_ver_id");
		sb.append(" ORDER BY i.index_no");
		ResultSet rst = st.executeQuery(sb.toString());
		while (rst.next()) {
			Index index = new Index();
			index.setIndexNo(rst.getString("index_no"));
			index.setIndexVerId(rst.getInt("index_ver_id"));
			index.setIndexNm(StringUtils.trimToEmpty(rst.getString("index_nm")));
			index.setTemplateId(StringUtils.trimToEmpty(rst.getString("template_id")));
			index.setSrcIndexNoes(StringUtils.split(StringUtils.trimToEmpty(rst.getString("src_index_no")), ','));
			Arrays.sort(index.getSrcIndexNoes());
			index.setIsRptIndex(YesOrNo.get(rst.getString("is_rpt_index")));
			indexList.add(index);
		}
		rst.close();
		st.close();
		return indexList;
	}

	/**
	 * 从数据库中加载指标单元格和指标列表单元格，并填充cellNoToCellMap和cellNmToCellMap
	 * 
	 * @param conn 数据库链接
	 * @param templates 报表模板数组，以templateId排序
	 * @param indexList 指标列表，以indexNo排序
	 * @throws SQLException
	 */
	private void loadIndexCellFromDB(Connection conn, Template[] templates, List<Index> indexList) throws SQLException {
		Template searchForTemplate = new Template();
		Index searchForIndex = new Index();

		StringBuilder sb = new StringBuilder();
		// 按照templateId和verId排序
		sb.append("SELECT * FROM (");
		sb.append("SELECT c.cell_no,c.ver_id,c.cell_nm,c.cell_data_src,s.index_no,t.template_id");
		sb.append(",s.time_measure_id,s.rule_id,s.mode_id");
		sb.append(" FROM rpt_design_cell_info c,rpt_design_source_idx s,rpt_design_tmp_info t");
		sb.append(" WHERE t.template_type<>'01' AND c.template_id=t.template_id");
		sb.append(" AND c.cell_data_src='03' AND c.ver_id=t.ver_id");
		sb.append(" AND s.cell_no=c.cell_no AND s.template_id=c.template_id AND s.ver_id=c.ver_id");
		sb.append(" AND s.index_no IS NOT NULL");
		sb.append(" UNION");
		sb.append(" SELECT c.cell_no,c.ver_id,c.cell_nm,c.cell_data_src,s.index_no,t.template_id");
		sb.append(",s.time_measure_id,s.rule_id,s.mode_id");
		sb.append(" FROM rpt_design_cell_info c,rpt_design_source_tabidx s,rpt_design_tmp_info t");
		sb.append(" WHERE t.template_type<>'01' AND c.template_id=t.template_id");
		sb.append(" AND c.cell_data_src='07' AND c.ver_id=t.ver_id");
		sb.append(" AND s.cell_no=c.cell_no AND s.template_id=c.template_id AND s.ver_id=c.ver_id");
		sb.append(" AND s.index_no IS NOT NULL");
		sb.append(") a ORDER BY a.template_id, a.ver_id DESC");

		Statement st = conn.createStatement();
		ResultSet rst = st.executeQuery(sb.toString());
		while (rst.next()) {
			String templateId = StringUtils.trimToEmpty(rst.getString("template_id"));
			if (StringUtils.isEmpty(templateId)) {
				continue;
			}
			// templates以templateId排序
			searchForTemplate.setTemplateId(templateId);
			int idx = Arrays.binarySearch(templates, searchForTemplate);
			if (idx < 0) {
				continue;
			}
			Template template = templates[idx];
			int verId = rst.getInt("ver_id");
			if (template.getVerId() != verId) {
				continue;
			}
			String cellNo = StringUtils.trimToEmpty(rst.getString("cell_no"));
			String cellNm = StringUtils.trimToEmpty(rst.getString("cell_nm"));
			String dataSrc = rst.getString("cell_data_src");
			Index index = null;
			searchForIndex.setIndexNo(StringUtils.trimToEmpty(rst.getString("index_no")));
			idx = Collections.binarySearch(indexList, searchForIndex);
			if (idx < 0) {
				logWarn("数据库记录，指标单元格和指标列表单元格找不到对应指标", template, cellNo);
				continue;
			}
			index = indexList.get(idx);
			if (StringUtils.isNotEmpty(index.getTemplateId()) &&
				! index.getTemplateId().equals(index.getTemplateId())) {
				logWarn("数据库记录，单元格模板号和指标的模板号对不上", template, cellNo);
			}
			IndexCell cell = new IndexCell();
			cell.setTemplate(template);
			cell.setCellNo(cellNo);
			cell.setCellNm(cellNm);
			cell.setIndex(index);
			cell.setTimeMeasureId(Byte.parseByte(StringUtils.defaultIfBlank(rst.getString("time_measure_id"), "0")));
			cell.setRuleId(Byte.parseByte(StringUtils.defaultIfBlank(rst.getString("rule_id"), "0")));
			cell.setModeId(Byte.parseByte(StringUtils.defaultIfBlank(rst.getString("mode_id"), "0")));
			Map<String, IndexBaseCell> cellNoToCellMap = data.getCellNoToCellMap().get(template);
			Map<String, IndexBaseCell> cellNmToCellMap = data.getCellNmToCellMap().get(template);
			if (template.getFilePath() != null) {
				if ("03".equals(dataSrc)) {
					updatingIndexCellList.add(cell);
				} else if ("07".equals(dataSrc)) {
					updatingListCellList.add(cell);
				}
			} else {
				if (cellNoToCellMap.containsKey(cellNo)) {
					cell.setStatus(CellStatus.FAIL);
					reportError("数据库记录，单元格号重复", template, cellNo, null);
				} else {
					cell.setStatus(CellStatus.OK);
					cellNoToCellMap.put(cellNo, cell);
				}
				if (StringUtils.isNotEmpty(cellNm)) {
					if (cellNmToCellMap.containsKey(cellNm)) {
						logWarn("数据库记录，单元格名称重复", template, cellNo);
					} else {
						cellNmToCellMap.put(cellNm, cell);
					}
				}
			}
		}
		rst.close();
		st.close();
	}

	/**
	 * 从数据库中加载公式单元格<br>
	 * 加载的单元格可分为三类：<br>
	 * 1）将在Excel中导入，不是报表指标单元格的，忽略；<br>
	 * 2）将在Excel中导入，是报表指标单元格的，放入updatingFormulaCellList，并初始化FormulaCell信息；<br>
	 * 3）不在Excel中导入，初始化FormulaCell信息(不解析公式)，填充cellNoToCellMap和cellNmToCellMap
	 * 
	 * @param conn 数据库链接
	 * @param templates 报表模板数组，以templateId排序
	 * @param indexList 指标列表，以indexNo排序
	 * @throws SQLException
	 */
	private void loadFormulaCellFromDB(Connection conn, Template[] templates, List<Index> indexList) throws SQLException {
		Template searchForTemplate = new Template();
		Index searchForIndex = new Index();

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT c.cell_no,c.ver_id,c.cell_nm,c.cell_data_src,s.index_no,t.template_id");
		sb.append(",s.time_measure_id,s.rule_id,s.mode_id,f.is_rpt_index,f.excel_formula");
		sb.append(" FROM rpt_design_cell_info c");
		sb.append(" INNER JOIN rpt_design_tmp_info t ON c.template_id=t.template_id");
		sb.append(" INNER JOIN rpt_design_source_formula f ON f.cell_no=c.cell_no AND f.template_id=c.template_id AND f.ver_id=c.ver_id");
		sb.append(" LEFT JOIN rpt_design_source_idx s ON s.cell_no=c.cell_no AND s.template_id=c.template_id AND s.ver_id=c.ver_id");
		sb.append(" AND s.index_no IS NOT NULL");
		sb.append(" WHERE c.cell_data_src IN ('04', '05') AND c.ver_id=t.ver_id");
		sb.append(" ORDER BY template_id,ver_id DESC");
		Statement st = conn.createStatement();
		ResultSet rst = st.executeQuery(sb.toString());
		while (rst.next()) {
			String templateId = StringUtils.trimToEmpty(rst.getString("template_id"));
			if (StringUtils.isEmpty(templateId)) {
				continue;
			}
			// templates以templateId排序
			searchForTemplate.setTemplateId(templateId);
			int idx = Arrays.binarySearch(templates, searchForTemplate);
			if (idx < 0) {
				continue;
			}
			Template template = templates[idx];
			int verId = rst.getInt("ver_id");
			if (template.getVerId() != verId) {
				continue;
			}
			YesOrNo isRptIndex = YesOrNo.get(rst.getString("is_rpt_index"));
			if (template.getFilePath() != null && isRptIndex != YesOrNo.YES) {
				// 将在Excel中导入，不是报表指标单元格的，忽略
				continue;
			}
			String cellNo = StringUtils.trimToEmpty(rst.getString("cell_no"));
			String cellNm = StringUtils.trimToEmpty(rst.getString("cell_nm"));
			String formula = StringUtils.trimToEmpty(rst.getString("excel_formula"));
			Index index = null;
			if (isRptIndex == YesOrNo.YES) {
				searchForIndex.setIndexNo(StringUtils.trimToEmpty(rst.getString("index_no")));
				idx = Collections.binarySearch(indexList, searchForIndex);
				if (idx < 0) {
					logWarn("数据库记录，公式单元格找不到对应指标", template, cellNo);
					continue;
				}
				index = indexList.get(idx);
				if (StringUtils.isNotEmpty(index.getTemplateId()) &&
					! index.getTemplateId().equals(index.getTemplateId())) {
					logWarn("数据库记录，单元格模板号和指标的模板号对不上", template, cellNo);
				}
			}
			FormulaCell cell = new FormulaCell();
			cell.setTemplate(template);
			cell.setCellNo(cellNo);
			cell.setCellNm(StringUtils.trimToEmpty(rst.getString("cell_nm")));
			cell.setIndex(index);
			cell.setFormula(formula);
			cell.setIsRptIndex(isRptIndex);
			Map<String, IndexBaseCell> cellNoToCellMap = data.getCellNoToCellMap().get(template);
			Map<String, IndexBaseCell> cellNmToCellMap = data.getCellNmToCellMap().get(template);
			if (template.getFilePath() != null) {
				updatingFormulaCellList.add(cell);
			} else {
				if (cellNoToCellMap.containsKey(cellNo)) {
					reportError("数据库记录，单元格号重复", template, cellNo, null);
				} else {
					cellNoToCellMap.put(cellNo, cell);
				}
				if (StringUtils.isNotEmpty(cellNm)) {
					if (cellNmToCellMap.containsKey(cellNm)) {
						logWarn("数据库记录，单元格名称重复", template, cellNo);
					} else {
						cellNmToCellMap.put(cellNm, cell);
					}
				}
			}
		}
		rst.close();
		st.close();
	}

	private void loadTemplateFromExcel(String filePath) {
		String rptNum = tempDataMap.get("报表编号");
		if (StringUtils.isEmpty(rptNum)) {
			reportError("报表信息页，报表编号配置错误(" + filePath + ")", null, tempDataMap, "报表编号");
			return;
		}
		Map<String, Template> templateMap = data.getTemplateMap();
		Template template = templateMap.get(rptNum);
		if (template == null) {
			Map<String, String> rptCataLogMap = data.getRptCataLogMap();
			template = new Template();
			template.setRptNum(rptNum);
			template.setTemplateId(RandomUtils.uuid2());
			template.setRptId(RandomUtils.uuid2());
			String catalogId = rptCataLogMap.get(tempDataMap.get("报表目录"));
			if(StringUtils.isNotBlank(catalogId)) {
				template.setCatalogId(catalogId);
			}else {
				template.setCatalogId("0");
			}
			newTemplateMap.put(filePath, template);
			Map<Template, Map<String, IndexBaseCell>> cellNoToCellMap = data.getCellNoToCellMap();
			cellNoToCellMap.put(template, new HashMap<String, IndexBaseCell>());
			Map<Template, Map<String, IndexBaseCell>> cellNmToCellMap = data.getCellNmToCellMap();
			cellNmToCellMap.put(template, new HashMap<String, IndexBaseCell>());
		}
		template.setFilePath(filePath);
		templateFileMap.put(filePath, template);
		templateMap.put(rptNum, template);
		try {
			template.setVerId(Integer.parseInt(tempDataMap.get("版本号")));
		} catch (NumberFormatException e) {
			reportError("报表信息页，版本号格式错误", template, tempDataMap, "版本号");
		}
		String rptNm = StringUtils.defaultString(tempDataMap.get("报表名称"));
		if (templateMap.containsKey(rptNm) && templateMap.get(rptNm) != template) {
			reportError("报表信息页，已存在相同名称的其他报表(" + filePath + ")", null, tempDataMap, "报表名称");
		} else {
			if (StringUtils.isNotEmpty(template.getRptNm())) {
				templateMap.remove(template.getRptNm());
			}
			template.setRptNm(rptNm);
			templateMap.put(rptNm, template);
		}
		//目前通过版本号来控制开始结束日期，所以不用校验启用日期
/*		String startDate = tempDataMap.get("启用日期");
		if (StringUtils.isEmpty(startDate)) {
			reportError("报表信息页，启用日期不能为空", template, tempDataMap, "启用日期");
		} else {
			try {
				sdf.parse(StringUtils.trimToEmpty(startDate));
				template.setStartDate(startDate);
			} catch (ParseException e) {
				reportError("报表信息页，启用日期格式错误", template, tempDataMap, "启用日期");
			}
		}*/
	}

	private boolean checkModelCell(Template template, String cellNo, String cellType,
			Map<String, String> map, String content) {
		int[] rowNoColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(cellNo);
		if (rowNoColumnIdx == null) {
			reportError("单元格信息页，" + cellType + "单元格编号格式错误", template, map, "单元格编号", cellNo);
			return false;
		}
		if (rowNoColumnIdx[0] >= Integer.parseInt(modelSheetMap.get("rowCount")) ||
			rowNoColumnIdx[1] >= Integer.parseInt(modelSheetMap.get("columnCount"))) {
			reportError("模板信息页，找不到报表信息页的单元格编号", template, cellNo, cellNo);
			return false;
		}
		if (! modelSheetMap.containsKey(cellNo)) {
			if (StringUtils.isEmpty(content)) {
				return true;
			}
			reportError("模板信息页，找不到单元格信息页上的指定" + cellType, template, cellNo, cellNo);
			return false;
		}
		if ("公式单元格".equals(cellType)) {
			String formula = StringUtils.defaultString(modelSheetMap.get(cellNo));
			if (formula.startsWith("=") && ! content.equals(formula.substring(1)) ||
				! formula.startsWith("=") && ! content.equals(formula)) {
/*				reportError("模板信息页，单元格内容与单元格信息页上的" + cellType + "内容不一致",
						template, cellNo, cellNo);
				return false;*/
				modelSheetMap.put(cellNo, content);//去掉excel公式校验，应对office精简报错excel公式问题
			}
		} else if (! content.equals(modelSheetMap.get(cellNo))) {
			reportError("模板信息页，单元格内容与单元格信息页上的" + cellType + "内容不一致",
					template, cellNo, cellNo);
			return false;
		}
		return true;
	}

	private Index createIndex(Template template, String cellNo, String cellNm, String rptIdxNo, String[] srcIndexNoes) {
		Index index = new Index();
		index.setIndexNo(rptIdxNo);
		index.setIndexVerId(template.getVerId());
		index.setIndexNm(buildIndexNm(template, cellNo, cellNm));
		index.setTemplateId(template.getTemplateId());
		index.setSrcIndexNoes(srcIndexNoes);
		index.setIsRptIndex(YesOrNo.YES);
		index.setStartDate(template.getStartDate());
		return index;
	}

	private IndexCell createIndexCell(Template template, String cellNo, String cellNm, String rptIdxNo, String[] srcIndexNoes) {
		IndexCell cell = new IndexCell();
		cell.setIndex(createIndex(template, cellNo, cellNm, rptIdxNo, srcIndexNoes));
		cell.setTemplate(template);
		return cell;
	}

	/**
	 * 从Excel中加载指标单元格和指标列表单元格
	 * 
	 * @param template 报表模板
	 * @param filePath Excel文件路径
	 * @param columnType 单元格类型，取值"指标单元格"或"列表指标单元格"
	 * @param columnDataMap 一行中的单元格数据，列名=>单元格值
	 */
	private void loadIndexCellFromExcel(Template template, String filePath, String columnType, Map<String, String> columnDataMap) {
		IndexCell searchForCell = new IndexCell();
		searchForCell.setIndex(new Index());
		// Sheet modelSheet = getTemplateSheet(filePath);

		Map<String, IndexBaseCell> cellNoToCellMap = data.getCellNoToCellMap().get(template);
		Map<String, IndexBaseCell> cellNmToCellMap = data.getCellNmToCellMap().get(template);
		List<IndexCell> fromCellList = "指标单元格".equals(columnType) ? updatingIndexCellList : updatingListCellList;
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			reportError("单元格信息页，" + columnType + "单元格编号为空", template, columnDataMap, "单元格编号", cellNo);
			return;
		}
		if (cellNoToCellMap.containsKey(cellNo)) {
			reportError("单元格信息页，单元格编号重复", template, columnDataMap, "单元格编号", cellNo);
			return;
		}
		boolean haveError = ! checkModelCell(template, cellNo, columnType, columnDataMap,
				StringUtils.defaultString(columnDataMap.get("来源指标名称")));
		String cellNm = StringUtils.defaultString(columnDataMap.get("单元格名称"));
		if (cellNmToCellMap.containsKey(cellNm)) {
			reportError("单元格信息页，单元格名称重复", template, columnDataMap, "单元格名称", cellNo);
			haveError = true;
		}
		String srcIndexNo = columnDataMap.get("来源指标编号");
		String timeMeasureId = columnDataMap.get("时间度量");
		if (timeMeasureId == null) {
			reportError("单元格信息页，指标单元格时间度量设置错误", template, columnDataMap, "时间度量", cellNo);
			haveError = true;
		} else {
			try {
				searchForCell.setTimeMeasureId(timeMeasureId.length() > 0 ? Byte.parseByte(timeMeasureId) : 0);
			} catch (NumberFormatException e) {
				reportError("单元格信息页，指标单元格时间度量设置错误", template, columnDataMap, "时间度量", cellNo);
				haveError = true;
			}
		}
		String ruleId = columnDataMap.get("计算规则");
		if (ruleId == null) {
			reportError("单元格信息页，指标单元格计算规则设置错误", template, columnDataMap, "计算规则", cellNo);
			haveError = true;
		} else {
			try {
				searchForCell.setRuleId(ruleId.length() > 0 ? Byte.parseByte(ruleId) : 0);
			} catch (NumberFormatException e) {
				reportError("单元格信息页，指标单元格计算规则设置错误", template, columnDataMap, "计算规则", cellNo);
				haveError = true;
			}
		}
		String modeId = columnDataMap.get("取值方式");
		if (modeId == null) {
			reportError("单元格信息页，指标单元格取值方式设置错误", template, columnDataMap, "取值方式", cellNo);
			haveError = true;
		} else {
			try {
				searchForCell.setModeId(modeId.length() > 0 ? Byte.parseByte(modeId) : 0);
			} catch (NumberFormatException e) {
				reportError("单元格信息页，指标单元格取值方式设置错误", template, columnDataMap, "取值方式", cellNo);
				haveError = true;
			}
		}
		IndexCell cell = null;
		if (! haveError) {
			searchForCell.setTemplate(template);
			searchForCell.setCellNo(cellNo);
			if (StringUtils.isEmpty(srcIndexNo)) {
				searchForCell.getIndex().setSrcIndexNoes(new String[0]);
			} else {
				searchForCell.getIndex().setSrcIndexNoes(new String[]{srcIndexNo});
			}
			int idx = Collections.binarySearch(fromCellList, searchForCell);
			if (idx >= 0) {
				cell = fromCellList.remove(idx);
				Index index = cell.getIndex();
				if (index.getIndexVerId() != template.getVerId()) {
					// 指标版本升级
					index.setIndexVerId(template.getVerId());
					index.setStartDate(template.getStartDate());
				}
			} else {
				String rptIdxNo = columnDataMap.get("报表指标编号");
				// 全新单元格
				if (StringUtils.isEmpty(srcIndexNo)) {
					cell = createIndexCell(template, cellNo, cellNm, rptIdxNo, new String[0]);
				} else {
					// 检查来源指标是否是总账指标
					int idx2 = -1;
					idx = srcIndexNo.lastIndexOf('.');
					if (idx >= 0) {
						String part2 = srcIndexNo.substring(idx + 1);
						if (data.getMeasureNoSet().contains(part2)) {
							String part1 = srcIndexNo.substring(0, idx);
							searchForCell.getIndex().setIndexNo(part1);
							idx2 = Collections.binarySearch(indexList, searchForCell.getIndex());
						}
					}
					if (idx2 < 0) {
						searchForCell.getIndex().setIndexNo(srcIndexNo);
						idx2 = Collections.binarySearch(indexList, searchForCell.getIndex());
					}
					if (idx2 < 0) {
						reportError("单元格信息页，找不到来源指标", template, columnDataMap, "来源指标编号", cellNo);
						haveError = true;
					} else {
						Index srcIndex = indexList.get(idx2);
						if (srcIndex.getIsRptIndex() == YesOrNo.YES) {
							reportError("单元格信息页，来源指标不能是报表指标", template, columnDataMap, "来源指标编号", cellNo);
							haveError = true;
						} else {
							cell = createIndexCell(template, cellNo, cellNm, rptIdxNo, searchForCell.getIndex().getSrcIndexNoes());
						}
					}
				}
			}
		}
		if (cell == null) {
			cell = new IndexCell();
			cell.setTemplate(template);
			cell.setStatus(CellStatus.FAIL);
		} else {
			cell.setTimeMeasureId(searchForCell.getTimeMeasureId());
			cell.setRuleId(searchForCell.getRuleId());
			cell.setModeId(searchForCell.getModeId());
			cell.setStatus(CellStatus.OK);
		}
		cell.setCellNo(cellNo);
		cell.setCellNm(cellNm);
		cellNoToCellMap.put(cellNo, cell);
		if (StringUtils.isNotEmpty(cellNm) && ! cellNmToCellMap.containsKey(cellNm)) {
			cellNmToCellMap.put(cellNm, cell);
		}
	}

	/**
	 * 处理公式校验通过的公式单元格指标
	 * 
	 * @param template 报表模板
	 * @param cell 公式单元格
	 * @param formulaCellList 可复用的原报表单元格列表
	 * @param formulaIndexList 公式解析得到的源指标，为空时表示非报表指标单元格
	 * @param searchForCell 单元格搜索对象
	 */
	private void configureFormulaCellIndex(Template template, FormulaCell cell,
			List<FormulaCell> formulaCellList, List<FormulaIndex> formulaIndexList,
			FormulaCell searchForCell) {
		Set<String> srcIndexNoSet = new HashSet<String>();
		for (int i = 0; i < formulaIndexList.size(); i ++) {
			Index index = formulaIndexList.get(i).getIndex();
			if (index != null) {
				srcIndexNoSet.add(index.getIndexNo());
			}
		}
		String[] srcIndexNoes = srcIndexNoSet.toArray(new String[0]);
		searchForCell.setTemplate(template);
		searchForCell.getIndex().setSrcIndexNoes(srcIndexNoes);
		Index index;
		searchForCell.setCellNo(cell.getCellNo());
		int idx = Collections.binarySearch(formulaCellList, searchForCell);
		if (idx >= 0) {
			index = formulaCellList.remove(idx).getIndex();
			if (index.getIndexVerId() != template.getVerId()) {
				// 指标版本升级
				index.setIndexVerId(template.getVerId());
				index.setStartDate(template.getStartDate());
			}
		} else {
			// 新指标
			index = createIndex(template, cell.getCellNo(), cell.getCellNm(), cell.getIndex().getIndexNo(), srcIndexNoes);
		}
		cell.setIndex(index);
	}

	/**
	 * 从Excel中加载公式单元格
	 * 
	 * @param template 报表模板
	 * @param filePath Excel文件路径
	 * @param columnDataMap 一行中的单元格数据，列名=>单元格值
	 */
	private void loadFormulaCellFromExcel(Template template, String filePath, Map<String, String> columnDataMap) {
		Map<String, IndexBaseCell> cellNoToCellMap = data.getCellNoToCellMap().get(template);
		Map<String, IndexBaseCell> cellNmToCellMap = data.getCellNmToCellMap().get(template);

		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			reportError("单元格信息页，公式单元格单元格编号为空", template, columnDataMap, "单元格编号", cellNo);
			return;
		}
		if (cellNoToCellMap.containsKey(cellNo)) {
			reportError("单元格信息页，公式单元格单元格编号重复", template, columnDataMap, "单元格编号", cellNo);
			return;
		}
		String formula = columnDataMap.get("计算公式");
		boolean haveError = ! checkModelCell(template, cellNo, "公式单元格", columnDataMap, formula);
		String cellNm = StringUtils.defaultString(columnDataMap.get("单元格名称"));
		if (cellNmToCellMap.containsKey(cellNm)) {
			reportError("单元格信息页，公式单元格单元格名称重复", template, columnDataMap, "单元格名称", cellNo);
			haveError = true;
		}
		YesOrNo isRptIndex = YesOrNo.get(columnDataMap.get("是否报表指标"));
		if (isRptIndex == YesOrNo.ERROR) {
			reportError("单元格信息页，公式单元格是否报表指标设置错误", template, columnDataMap, "是否报表指标", cellNo);
			haveError = true;
		}
		
		Index excelIdx = new Index();
		String rptIdxNo = columnDataMap.get("报表指标编号");
		excelIdx.setIndexNo(rptIdxNo);
		
		FormulaCell formulaCell = new FormulaCell();
		formulaCell.setTemplate(template);
		formulaCell.setCellNo(cellNo);
		formulaCell.setCellNm(cellNm);
		formulaCell.setFormula(formula);
		formulaCell.setIsRptIndex(isRptIndex);
		formulaCell.setExcelCellNo(columnDataMap.get("_REGULATION_计算公式"));
		formulaCell.setIndex(excelIdx);
		if (haveError) {
			formulaCell.setStatus(CellStatus.FAIL);
		}
		cellNoToCellMap.put(cellNo, formulaCell);
		if (StringUtils.isNotEmpty(cellNm) && ! cellNmToCellMap.containsKey(cellNm)) {
			cellNmToCellMap.put(cellNm, formulaCell);
		}
	}

	
	/**
	 * 从Excel中加载表间取数单元格
	 * 
	 * @param template 报表模板
	 * @param filePath Excel文件路径
	 * @param columnDataMap 一行中的单元格数据，列名=>单元格值
	 */
	private void loadCalcCellFromExcel(Template template, String filePath, Map<String, String> columnDataMap) {
		Map<String, IndexBaseCell> cellNoToCellMap = data.getCellNoToCellMap().get(template);
		Map<String, IndexBaseCell> cellNmToCellMap = data.getCellNmToCellMap().get(template);

		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			reportError("单元格信息页，公式单元格单元格编号为空", template, columnDataMap, "单元格编号", cellNo);
			return;
		}
		if (cellNoToCellMap.containsKey(cellNo)) {
			reportError("单元格信息页，公式单元格单元格编号重复", template, columnDataMap, "单元格编号", cellNo);
			return;
		}
		String cellNm = StringUtils.defaultString(columnDataMap.get("单元格名称"));
		if (StringUtils.isEmpty(cellNm)) {
			reportError("单元格信息页，公式单元格单元格编号为空", template, columnDataMap, "单元格名称", cellNm);
			return;
		}
		if (cellNmToCellMap.containsKey(cellNm)) {
			reportError("单元格信息页，公式单元格单元格名称重复", template, columnDataMap, "单元格名称", cellNo);
			return;
		}
		String calcFma = StringUtils.defaultString(columnDataMap.get("公式信息"));
		if (StringUtils.isEmpty(calcFma)) {
			reportError("单元格信息页，表间取数单元格公式信息为空", template, columnDataMap, "公式信息", cellNo);
			return;
		}
		String calcFmaCfg = StringUtils.defaultString(columnDataMap.get("公式配置信息"));
		if (StringUtils.isEmpty(calcFmaCfg)) {
			reportError("单元格信息页，表间取数单元格公式配置信息为空", template, columnDataMap, "公式配置信息", cellNo);
			return;
		}
		Index calcIdx = new Index();
		String rptIdxNo = columnDataMap.get("报表指标编号");
		calcIdx.setIndexNo(rptIdxNo);
		calcIdx.setIndexVerId(template.getVerId());
		calcIdx.setIndexNm(buildIndexNm(template, cellNo, cellNm));
		calcIdx.setTemplateId(template.getTemplateId());
		String srcIndexNo = columnDataMap.get("来源指标编号");
		if(StringUtils.isNotBlank(srcIndexNo)) {
			calcIdx.setSrcIndexNoes(srcIndexNo.split(","));
		}
		calcIdx.setStartDate(template.getStartDate());
		IndexCell calcCell = new IndexCell();
		calcCell.setIndex(calcIdx);
		calcCell.setCellNo(cellNo);
		calcCell.setCellNm(cellNm);
		calcCell.setTemplate(template);
		cellNoToCellMap.put(cellNo, calcCell);
		if (StringUtils.isNotEmpty(cellNm) && ! cellNmToCellMap.containsKey(cellNm)) {
			cellNmToCellMap.put(cellNm, calcCell);
		}
	}
	
	/**
	 * 解析公式中的单元格号，如果公式单元格包含报表指标，插入到formulaIndexList
	 * 
	 * @param template 报表模板
	 * @param cell 公式单元格，单元格号为空时是校验公式单元格
	 * @param ident 公式中的待解析成单元格的单元格号
	 * @param startOffset 原始公式中ident的开始位移
	 * @param searchForCell 可复用的用于搜索的单元格缓存
	 * @param updatingFormulaCellList 可复用的原报表公式单元格列表
	 * @param formulaIndexList 公式中的指标列表
	 * @return 出错时返回false；否则返回true
	 */
	private boolean parseIndexInfoInFormula(Template template, FormulaCell cell,
			String ident, int startOffset, FormulaCell searchForCell,
			List<FormulaCell> updatingFormulaCellList, List<FormulaIndex> formulaIndexList) {
		ident = ident.trim();
		if (StringUtils.isEmpty(ident) || NumberUtils.isNumber(ident) ||
				"SUM".equalsIgnoreCase(ident) ||"AVERAGE".equalsIgnoreCase(ident) || "MAX".equalsIgnoreCase(ident) ||
				"MIN".equalsIgnoreCase(ident) ||"IF".equalsIgnoreCase(ident) || "ROUND".equalsIgnoreCase(ident) ||
				"TRUE".equalsIgnoreCase(ident) ||"FALSE".equalsIgnoreCase(ident)) {
			return true;
		}
		Template targetTemplate = null;
		int endOffset = startOffset + ident.length();
		int idx = ident.indexOf('.');
		if (idx >= 0) {
			targetTemplate = data.getTemplateMap().get(ident.substring(0, idx));
			if (targetTemplate != null) {
				ident = ident.substring(idx + 1);
				if (ident.startsWith("主模板.")) {
					ident = ident.substring("主模板.".length());
				}
			}
		}
		if (targetTemplate == null) {
			targetTemplate = template;
		}
		IndexBaseCell targetCell = null;
		if (data.getCellNmToCellMap().containsKey(targetTemplate)) {
			targetCell = data.getCellNmToCellMap().get(targetTemplate).get(ident);
		}
		if (targetCell == null && data.getCellNoToCellMap().containsKey(targetTemplate)) {
			targetCell = data.getCellNoToCellMap().get(targetTemplate).get(ident);
		}
		if (targetCell == null) {
			if (emptyCellInFormula) {
				return Boolean.TRUE;
			}
			if (StringUtils.isEmpty(cell.getCellNo())) {
				reportError("校验公式页，不能在公式中引用空单元格或一般单元格(" + ident + ")", template, cell.getExcelCellNo(),
						targetTemplate, ident);
			} else {
				reportError("单元格信息页，不能在公式中引用空单元格或一般单元格(" + ident + ")", template,
						cell.getExcelCellNo(), targetTemplate, ident);
			}
			return false;
		}
		if (targetCell.getStatus() == CellStatus.FAIL) {
			return false;
		}
		if (StringUtils.isNotEmpty(cell.getCellNo()) && (targetCell instanceof FormulaCell) &&
			parseFormula(targetTemplate, (FormulaCell)targetCell, searchForCell, updatingFormulaCellList, false) == null) {
			return false;
		}
		if (targetCell.getIndex() == null) {
			if (StringUtils.isNotEmpty(cell.getCellNo()) && cell.getIsRptIndex() != YesOrNo.YES) {
				return Boolean.TRUE;
			}
			if (StringUtils.isEmpty(cell.getCellNo())) {
				reportError("校验公式页，找不到单元格指标(" + ident + ")", template,
						cell.getExcelCellNo(), targetTemplate, ident);
			} else {
				reportError("单元格信息页，找不到单元格指标(" + ident + ")", template,
						cell.getExcelCellNo(), targetTemplate, ident);
			}
			return false;
		}
		if (ArrayUtils.isEmpty(targetCell.getIndex().getSrcIndexNoes()) && ! emptyIndexInFormula) {
			if (StringUtils.isEmpty(cell.getCellNo())) {
				reportError("校验公式页，不能在公式中引用空指标单元格(" + ident + ")", template,
						cell.getExcelCellNo(), targetTemplate, ident);
			} else {
				reportError("单元格信息页，不能在公式中引用空指标单元格(" + ident + ")", template,
						cell.getExcelCellNo(), targetTemplate, ident);
			}
			return false;
		}
		FormulaIndex formulaIndex = new FormulaIndex();
		formulaIndex.setStartOffset(startOffset);
		formulaIndex.setEndOffset(endOffset);
		formulaIndex.setIndex(targetCell.getIndex());
		formulaIndexList.add(formulaIndex);
		return true;
	}

	/**
	 * 解析公式中的表达式部分，表达式中不包括双引号括起来的常量，也不包括诸如"A8:B9"这样的单元格区间<br>
	 * 如果公式单元格包含报表指标，插入到formulaIndexList
	 * 
	 * @param template 报表模板
	 * @param cell 公式单元格，单元格号为空时是校验公式单元格
	 * @param expression 公式中的待解析的表达式部分
	 * @param startOffset 原始公式中expression的开始位移
	 * @param searchForCell 可复用的用于搜索的单元格缓存
	 * @param updatingFormulaCellList 可复用的原报表公式单元格列表
	 * @param formulaIndexList 公式中的指标列表
	 * @return 出错时返回false；否则返回true
	 */
	private boolean parseExpressionInFormula(Template template, FormulaCell cell,
			String expression, int startOffset, FormulaCell searchForCell,
			List<FormulaCell> updatingFormulaCellList, List<FormulaIndex> formulaIndexList) {
		String splitChars = "+-*/=><()[]{}'\":,%";
		String formulaPart = expression;
		while (true) {
			int idx = StringUtils.indexOfAnyBut(formulaPart, splitChars);
			if (idx < 0) {
				return true;
			}
			startOffset += idx;
			formulaPart = formulaPart.substring(idx);
			idx = StringUtils.indexOfAny(formulaPart, splitChars);
			if (idx < 0) {
				String ident = formulaPart;
				return parseIndexInfoInFormula(template, cell, ident, startOffset,
						searchForCell, updatingFormulaCellList, formulaIndexList);
			}
			int endOffset = startOffset + idx;
			String ident = formulaPart.substring(0, idx);
			if (! parseIndexInfoInFormula(template, cell, ident, startOffset,
					searchForCell, updatingFormulaCellList, formulaIndexList)) {
				return false;
			}
			startOffset = endOffset;
			formulaPart = formulaPart.substring(idx);
		}
	}

	/**
	 * 解析公式一部分，其中不包括双引号括起来的常量；如果公式单元格包含报表指标，插入到formulaIndexList
	 * 
	 * @param template 报表模板
	 * @param cell 公式单元格，单元格号为空时是校验公式单元格
	 * @param formulaPart 公式中的待解析的部分
	 * @param startOffset 原始公式中formulaPart的开始位移
	 * @param searchForCell 可复用的用于搜索的单元格缓存
	 * @param updatingFormulaCellList 可复用的原报表公式单元格列表
	 * @param formulaIndexList 公式中的指标列表
	 * @return 出错时返回false；否则返回true
	 */
	private boolean parsePartInFormula(Template template, FormulaCell cell,
			String formulaPart, int startOffset, FormulaCell searchForCell,
			List<FormulaCell> updatingFormulaCellList, List<FormulaIndex> formulaIndexList) {
		// 将诸如$C$120这样的单元格号替换成"C120  "
		StringBuilder sb = new StringBuilder();
		int pos = 0;
		Matcher m = ExcelAnalyseUtils.getDollarCellNoPattern().matcher(formulaPart);
		while (m.find()) {
			sb.append(formulaPart.substring(pos, m.start())).append(m.group(1)).append(m.group(2)).append("  ");
			pos = m.end();
		}
		sb.append(formulaPart.substring(pos));
		formulaPart = sb.toString();
		
		// 处理诸如"A8:B9"这样的单元格区间，以单元格区间分割公式
		m = cellNoRegionPattern.matcher(formulaPart);
		pos = 0;
		while (m.find()) {
			if (! parseExpressionInFormula(template, cell, formulaPart.substring(pos, m.start()), startOffset + pos,
					searchForCell, updatingFormulaCellList, formulaIndexList)) {
				return false;
			}
			int[] fromRowNoColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(m.group(1));
			int[] toRowNoColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(m.group(2));
			// 处理单元格区间内的每个单元格
			for (int rowIdx = fromRowNoColumnIdx[0]; rowIdx <= toRowNoColumnIdx[0]; rowIdx ++) {
				for (int columnIdx = fromRowNoColumnIdx[1]; columnIdx <= toRowNoColumnIdx[1]; columnIdx ++) {
					String ident = ExcelAnalyseUtils.toABC(rowIdx, columnIdx);
					if (! parseIndexInfoInFormula(template, cell, ident, startOffset + m.start(),
							searchForCell, updatingFormulaCellList, formulaIndexList)) {
						return false;
					}
				}
			}
			pos = m.end();
		}
		if (pos >= formulaPart.length()) {
			return true;
		}
		return parseExpressionInFormula(template, cell, formulaPart.substring(pos), startOffset + pos,
				searchForCell, updatingFormulaCellList, formulaIndexList);
	}

	/**
	 * 解析公式单元格
	 * 
	 * @param template 报表模板
	 * @param cell 公式单元格，单元格号为空时是校验公式单元格
	 * @param searchForCell 可复用的用于搜索的单元格缓存
	 * @param updatingFormulaCellList 可复用的原报表公式单元格列表
	 * @param parseIIndex 是否需要解析I('...')或者I("...")这样的指标字符串
	 * @return 出错时返回null；否则，返回解析后的FormulaIndex数组
	 * @throws IOException
	 */
	public FormulaIndex[] parseFormula(Template template, FormulaCell cell, FormulaCell searchForCell,
			List<FormulaCell> updatingFormulaCellList, boolean parseIIndex) {
		if (cell.getStatus() == CellStatus.PARSING) {
			reportError("单元格信息页，公式递归引用", template, cell.getExcelCellNo(), cell.getCellNo());
			cell.setStatus(CellStatus.FAIL);
			return null;
		}
		if (cell.getStatus() == CellStatus.FAIL) {
			return null;
		}
		if (cell.getStatus() == CellStatus.OK) {
			return cell.getFormulaIndexes();
		}
		cell.setStatus(CellStatus.PARSING);
		String formula = cell.getFormula();
		List<FormulaIndex> formulaIndexList = new ArrayList<FormulaIndex>();

		if (parseIIndex) {
			StringBuilder sb = new StringBuilder(formula);
			Matcher m = indexNoPattern.matcher(formula);
			while (m.find()) {
				String ident = m.group(1);
				int pos = m.start(1);
				if (StringUtils.isEmpty(ident)) {
					ident = m.group(2);
					pos = m.start(2);
				}
				if (! parseIndexInfoInFormula(template, cell, ident, pos, searchForCell,
						updatingFormulaCellList, formulaIndexList)) {
					cell.setStatus(CellStatus.FAIL);
					break;
				}
				// 将匹配的指标字符串以全空格替换，这样后继不用处理
				for (int i = m.start(); i < m.end(); i ++) {
					sb.setCharAt(i, ' ');
				}
			}
			formula = sb.toString();
		}

		// 解析公式，分析双引号括起来之外的部分
		int startOffset = 0;
		if (cell.getStatus() == CellStatus.PARSING) {
			while (true) {
				int idx = formula.indexOf('"', startOffset);
				if (idx < 0) {
					break;
				}
				int idx2 = formula.indexOf('"', idx + 1);
				if (idx2 < 0) {
					break;
				}
				if (! parsePartInFormula(template, cell, formula.substring(startOffset, idx), startOffset, 
						searchForCell, updatingFormulaCellList, formulaIndexList)) {
					cell.setStatus(CellStatus.FAIL);
					break;
				}
				startOffset = idx2 + 1;
			}
		}
		if (cell.getStatus() == CellStatus.PARSING && startOffset < formula.length() &&
			! parsePartInFormula(template, cell, formula.substring(startOffset), startOffset, 
					searchForCell, updatingFormulaCellList, formulaIndexList)) {
			cell.setStatus(CellStatus.FAIL);
		}
		if (cell.getStatus() == CellStatus.PARSING) {
			if (StringUtils.isNotEmpty(cell.getCellNo()) && CollectionUtils.isNotEmpty(formulaIndexList) && cell.getIsRptIndex() == YesOrNo.YES) {
				configureFormulaCellIndex(template, cell, updatingFormulaCellList, formulaIndexList, searchForCell);
			}
			cell.setFormulaIndexes(formulaIndexList.toArray(new FormulaIndex[0]));
			cell.setStatus(CellStatus.OK);
		}
		return cell.getFormulaIndexes();
	}

	/**
	 * 解析公式单元格
	 * 
	 * @param template 报表模板
	 * @param cell 公式单元格，单元格号为空时是校验公式单元格
	 * @param searchForCell 可复用的用于搜索的单元格缓存
	 * @param updatingFormulaCellList 可复用的原报表公式单元格列表
	 * @return 出错时返回null；否则，返回解析后的FormulaIndex数组
	 * @throws IOException
	 */
	public FormulaIndex[] parseFormula(Template template, FormulaCell cell, FormulaCell searchForCell,
			List<FormulaCell> updatingFormulaCellList) {
		return this.parseFormula(template, cell, searchForCell, updatingFormulaCellList, true);
	}

	/**
	 * 从数据库和Excel文件中加载模板信息、单元格信息和指标信息
	 * 
	 * @param conn 数据库连接
	 * @throws SQLException
	 */
	public void load(Connection conn) throws IOException, SQLException {
		updatingIndexCellList = new ArrayList<IndexCell>();
		updatingListCellList = new ArrayList<IndexCell>();
		updatingFormulaCellList = new ArrayList<FormulaCell>();

		File dir = new File(excelFileDir);
		File[] fs = dir.listFiles();

		Template[] templates = loadTemplateFromDB(conn);
		loadRptCataLogFromDB(conn);
		for (int i = 0; i < fs.length; i ++) {
			String filePath = fs[i].getPath();
			ExcelEventReader excelEventReader = new ExcelEventReader();
			excelEventReader.setExcelEventListener(this);
			excelEventReaderMap.put(filePath, excelEventReader);
			excelEventReader.startReadSheet(filePath, "报表信息");
		}
		
		int templatesSize = templates.length;
		int newTemplateMapSize = newTemplateMap.size();
		templates = java.util.Arrays.copyOf(templates, templatesSize + newTemplateMapSize);
		for(Entry<String, Template> entry : newTemplateMap.entrySet()) {
			templates[templatesSize] = entry.getValue();
			templatesSize++;
		}
		
		indexList = loadIndexFromDB(conn);
		loadIndexCellFromDB(conn, templates, indexList);
		loadFormulaCellFromDB(conn, templates, indexList);
		Collections.sort(updatingIndexCellList);
		Collections.sort(updatingListCellList);
		Collections.sort(updatingFormulaCellList);
		for (int i = 0; i < templates.length; i ++) {
			Template template = templates[i];
			String filePath = template.getFilePath();
			if (filePath == null) {
				continue;
			}
			// 获取模板信息页大小和内容
			modelSheetMap = excelEventReaderMap.get(filePath).getSheetData(filePath, "模板信息");
			// 获取单元格信息页的SPAN单元格列表
			spanRegionList = excelEventReaderMap.get(filePath).getSpanRegion(filePath, "单元格信息");
			// 从Excel加载一般单元格
			columnType = "一般单元格";
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "单元格信息");
			// 从Excel加载指标单元格，并根据单元格源指标，复用原报表的单元格
			columnType = "指标单元格";
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "单元格信息");
			// 从Excel加载列表指标单元格，并根据单元格源指标，复用原报表的单元格
			columnType = "列表指标单元格";
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "单元格信息");
			// 从Excel加载公式单元格
			columnType = "公式单元格";
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "单元格信息");
			// 从Excel加载表达式单元格
			columnType = "表达式单元格";
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "单元格信息");
			// 从Excel加载表间取数单元格
			columnType = "表间取数单元格";
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "单元格信息");
			modelSheetMap = null;
			spanRegionList = null;
			columnType = null;

			// 加载查询条件页、校验公式页、预警值校验页、指标过滤信息页的列名信息
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "查询条件", 1);
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "校验公式", 1);
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "预警校验", 1);
			excelEventReaderMap.get(filePath).startReadSheet(filePath, "指标过滤信息", 1);
		}
		indexList = null;
		updatingIndexCellList = null;
		updatingListCellList = null;

		// 递归解析导入Excel文件涉及到的公式单元格
		FormulaCell searchForCell = new FormulaCell();
		searchForCell.setIndex(new Index());
		for (int i = 0; i < templates.length; i ++) {
			Template template = templates[i];
			if (template.getFilePath() == null) {
				continue;
			}
			Map<String, IndexBaseCell> cellNoToCellMap = data.getCellNoToCellMap().get(template);
			for (Iterator<Entry<String, IndexBaseCell>> it = cellNoToCellMap.entrySet().iterator(); it.hasNext(); ) {
				IndexBaseCell cell = it.next().getValue();
				if (cell instanceof FormulaCell) {
					parseFormula(template, (FormulaCell)cell, searchForCell, updatingFormulaCellList);
				}
			}
		}
		updatingFormulaCellList = null;
	}

	/**
	 * 统一转码方法将传进来的MAP的VALUE转成相对应的码值
	 * 
	 */
	public void translateDictionary(Map<String, String> willTransMap){
		if(willTransMap == null){
			return;
		}
		if (willTransMap.containsKey("数据单位")) {
			willTransMap.put("数据单位", DataUnit.get(willTransMap.get("数据单位")).toString());
		}
		if (willTransMap.containsKey("报表单位")) {
			willTransMap.put("报表单位", DataUnit.get(willTransMap.get("报表单位")).toString());
		}
		if (willTransMap.containsKey("显示格式")) {
			willTransMap.put("显示格式", DisplayFormat.get(willTransMap.get("显示格式")).toString());
		}
		if (willTransMap.containsKey("控件类型")) {
			willTransMap.put("控件类型", ElementType.get(willTransMap.get("控件类型")).toString());
		}
		if (willTransMap.containsKey("扩展方向")) {
			willTransMap.put("扩展方向", ExtendDirection.get(willTransMap.get("扩展方向")).toString());
		}
		if (willTransMap.containsKey("扩展方式")) {
			willTransMap.put("扩展方式", ExtendMode.get(willTransMap.get("扩展方式")).toString());
		}
		if (willTransMap.containsKey("扩展类型")) {
			willTransMap.put("扩展类型", ExtendType.get(willTransMap.get("扩展类型")).toString());
		}
		if (willTransMap.containsKey("过滤类型")) {
			willTransMap.put("过滤类型", FilterMode.get(willTransMap.get("过滤类型")).toString());
		}
		if (willTransMap.containsKey("报表周期")) {
			willTransMap.put("报表周期", RptCycle.get(willTransMap.get("报表周期")).toString());
		}
		if (willTransMap.containsKey("排序方式")) {
			willTransMap.put("排序方式", SortMode.get(willTransMap.get("排序方式")).toString());
		}
		if (willTransMap.containsKey("汇总方式")) {
			willTransMap.put("汇总方式", SumMode.get(willTransMap.get("汇总方式")).toString());
		}
		if (willTransMap.containsKey("模板类型")) {
			willTransMap.put("模板类型", TemplateType.get(willTransMap.get("模板类型")).toString());
		}
		if (willTransMap.containsKey("是否预校验")) {
			willTransMap.put("是否预校验", YesOrNo.get(willTransMap.get("是否预校验")).toString());
		}
		if (willTransMap.containsKey("通过条件")) {
			willTransMap.put("通过条件", YesOrNo.get(willTransMap.get("通过条件")).toString());
		}
		if (willTransMap.containsKey("是否分析扩展")) {
			willTransMap.put("是否分析扩展", YesOrNo.get(willTransMap.get("是否分析扩展")).toString());
		}
		if (willTransMap.containsKey("是否报表指标")) {
			willTransMap.put("是否报表指标", YesOrNo.get(willTransMap.get("是否报表指标")).toString());
		}
		if (willTransMap.containsKey("是否汇总")) {
			willTransMap.put("是否汇总", YesOrNo.get(willTransMap.get("是否汇总")).toString());
		}
		if (willTransMap.containsKey("是否落地")) {
			willTransMap.put("是否落地", YesOrNo.get(willTransMap.get("是否落地")).toString());
		}
		if (willTransMap.containsKey("是否跨年")) {
			willTransMap.put("是否跨年", YesOrNo.get(willTransMap.get("是否跨年")).toString());
		}
		if (willTransMap.containsKey("是否扩展")) {
			willTransMap.put("是否扩展", YesOrNo.get(willTransMap.get("是否扩展")).toString());
		}
		if (willTransMap.containsKey("是否排序")) {
			willTransMap.put("是否排序", YesOrNo.get(willTransMap.get("是否排序")).toString());
		}
		if (willTransMap.containsKey("是否转码")) {
			willTransMap.put("是否转码", YesOrNo.get(willTransMap.get("是否转码")).toString());
		}
		if (willTransMap.containsKey("是否必填")) {
			willTransMap.put("是否必填", YesOrNo.get(willTransMap.get("是否必填")).toString());
		}
		if (willTransMap.containsKey("是否复选")) {
			willTransMap.put("是否复选", YesOrNo.get(willTransMap.get("是否复选")).toString());
		}
		if (willTransMap.containsKey("报表状态")) {
			willTransMap.put("报表状态", YesOrNo.get(willTransMap.get("报表状态")).toString());
		}
		if (willTransMap.containsKey("业务类型")) {
			willTransMap.put("业务类型", BusiType.get(willTransMap.get("业务类型")).toString());
		}
		if (willTransMap.containsKey("是否下发提交")) {
			willTransMap.put("是否下发提交", YesOrNo.get(willTransMap.get("是否下发提交")).toString());
		}
		if (willTransMap.containsKey("是否数据修改")) {
			willTransMap.put("是否数据修改", YesOrNo.get(willTransMap.get("是否数据修改")).toString());
		}
		if (willTransMap.containsKey("是否合计")) {
			willTransMap.put("是否合计", YesOrNo.get(willTransMap.get("是否合计")).toString());
		}
		/*if (willTransMap.containsKey("比较值类型")) {
			willTransMap.put("比较值类型", CompareValueType.get(willTransMap.get("比较值类型")).toString());
		}
		if (willTransMap.containsKey("幅度类型")) {
			willTransMap.put("幅度类型", RangeType.get(willTransMap.get("幅度类型")).toString());
		}
		if (willTransMap.containsKey("警戒值类型")) {
			willTransMap.put("警戒值类型", LeverType.get(willTransMap.get("警戒值类型")).toString());
		}*/
		if (willTransMap.containsKey("预警类型")) {
			willTransMap.put("预警类型", CompareType.get(willTransMap.get("预警类型")).toString());
		}
		if (willTransMap.containsKey("监管要求")) {
			willTransMap.put("监管要求", IsFrs.get(willTransMap.get("监管要求")).toString());
		}
		if (willTransMap.containsKey("单元格属性")) {
			willTransMap.put("单元格属性", ComCellType.get(willTransMap.get("单元格属性")).toString());
		}
		if (data != null) {
			if (willTransMap.containsKey("计算规则")) {
				willTransMap.put("计算规则", data.getIdxCalcRuleMap().get(willTransMap.get("计算规则")));
			}
			if (willTransMap.containsKey("时间度量")) {
				willTransMap.put("时间度量", data.getIdxTimeMeasureMap().get(willTransMap.get("时间度量")));
			}
			if (willTransMap.containsKey("取值方式")) {
				willTransMap.put("取值方式", data.getIdxValTypeMap().get(willTransMap.get("取值方式")));
			}
		}
	}

	@Override
	public void close() throws IOException {
		for (Iterator<Entry<String, ExcelEventReader>> it = excelEventReaderMap.entrySet().iterator(); it.hasNext(); ) {
			IOUtils.closeQuietly(it.next().getValue());
		}
	}

	@Override
	public void rowDataReady(ExcelEventReader excelEventReader, int rowIdx, Map<Integer, String> rowDataMap) {
		String sheetName = excelEventReader.getSheetName();
		if ("报表信息".equals(sheetName)) {
			if ((rowDataMap == null) && (tempDataMap != null)) {
				loadTemplateFromExcel(excelEventReader.getFilePath());
				tempDataMap = null;
			} else {
				if (tempDataMap == null) {
					tempDataMap = new HashMap<String, String>();
				}
				String columnName = rowDataMap.get(Integer.valueOf(1));
				tempDataMap.put(columnName, rowDataMap.get(Integer.valueOf(2)));
				tempDataMap.put("_REGULATION_" + columnName, ExcelAnalyseUtils.toABC(rowIdx, 2));
			}
		} else if ("查询条件".equals(sheetName) || "校验公式".equals(sheetName) ||
				"预警校验".equals(sheetName) || "指标过滤信息".equals(sheetName)) {
			Template template = getTemplate(excelEventReader);
			Map<String, Map<Integer, String>> columnTypeMap = allColumnMap.get(template);
			columnTypeMap.put(sheetName, rowDataMap);
		} else if ("单元格信息".equals(sheetName)) {
			if (rowDataMap == null) {
				return;
			}
			Template template = getTemplate(excelEventReader);
			if (rowIdx == 0) {
				if (! allColumnMap.containsKey(template)) {
					tempColumnIdxToDataMap = rowDataMap;
				}
			} else if (rowIdx == 1) {
				Map<String, Map<Integer, String>> columnTypeMap = allColumnMap.get(template);
				if (columnTypeMap == null) {
					columnTypeMap = new HashMap<String, Map<Integer, String>>();
					allColumnMap.put(template, columnTypeMap);
					for (int i = 0; i < spanRegionList.size(); i ++) {
						String region = spanRegionList.get(i);
						int pos = region.indexOf(':');
						int[] fromRowColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(region.substring(0, pos));
						int[] toRowColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(region.substring(pos + 1));
						if (fromRowColumnIdx[0] == 0) {
							Integer regionKey = Integer.valueOf(fromRowColumnIdx[1]);
							if (! tempColumnIdxToDataMap.containsKey(regionKey)) {
								continue;
							}
							Map<Integer, String> columnMap = new HashMap<Integer, String>();
							for (int k = fromRowColumnIdx[1]; k <= toRowColumnIdx[1]; k ++) {
								Integer columnIdxKey = Integer.valueOf(k);
								if (rowDataMap.containsKey(columnIdxKey)) {
									columnMap.put(columnIdxKey, rowDataMap.get(columnIdxKey));
								}
							}
							columnTypeMap.put(tempColumnIdxToDataMap.get(regionKey), columnMap);
						}
					}
					tempColumnIdxToDataMap = null;
				}
			} else {
				Map<String, String> columnDataMap = parseRowDataMap(template, columnType, rowIdx, rowDataMap);
				translateDictionary(columnDataMap);
				if (columnDataMap != null && columnDataMap.containsKey("单元格编号")) {
					if ("指标单元格".equals(columnType) || "列表指标单元格".equals(columnType)) {
						loadIndexCellFromExcel(template, template.getFilePath(), columnType, columnDataMap);
					} else if ("公式单元格".equals(columnType)) {
						loadFormulaCellFromExcel(template, template.getFilePath(), columnDataMap);
					}else if ("表间取数单元格".equals(columnType)) {
						loadCalcCellFromExcel(template, template.getFilePath(), columnDataMap);
					}
				}
			}
		}
	}

	public Map<String, String> parseRowDataMap(Template template, String sheetNameOrColumnType, int rowIdx, Map<Integer, String> rowDataMap) {
		Map<String, Map<Integer, String>> columnTypeMap = allColumnMap.get(template);
		if (columnTypeMap == null) {
			return null;
		}
		Map<Integer, String> columnMap = columnTypeMap.get(sheetNameOrColumnType);
		if (columnMap == null) {
			return null;
		}
		Map<String, String> columnDataMap = new HashMap<String, String>();
		for (Iterator<Entry<Integer, String>> it = columnMap.entrySet().iterator(); it.hasNext(); ) {
			Entry<Integer, String> entry = it.next();
			Integer columnIdxKey = entry.getKey();
			if (rowDataMap.containsKey(columnIdxKey)) {
				columnDataMap.put(entry.getValue(), rowDataMap.get(columnIdxKey));
				columnDataMap.put("_REGULATION_" + entry.getValue(), ExcelAnalyseUtils.toABC(rowIdx, columnIdxKey.intValue()));
			}
		}
		return columnDataMap;
	}

	public Map<String, String> getNextRow(String rptNm, String sheetNm, String cellType) {
		throw new UnsupportedOperationException();
	}

	public Map<String, String> getNextRow(Template template, String sheetNm, String cellType) {
		throw new UnsupportedOperationException();
	}

	public Template getTemplate(ExcelEventReader excelEventReader) {
		return templateFileMap.get(excelEventReader.getFilePath());
	}

	public Map<String, ExcelEventReader> getExcelEventReaderMap() {
		return excelEventReaderMap;
	}

	public String getErrorMessage() {
		return errorMessageBuilder.toString();
	}

	public Data getData() {
		return data;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public boolean isEmptyCellInVerifyWarn() {
		return emptyCellInVerifyWarn;
	}
	
	/**
	 * 根据报表名称和单元格编号获取报表指标编号
	 * @param rptNm
	 * @param cellNo
	 * @return
	 */
	public String getIdxNoByCacheData(String rptNm, String cellNo) {
		String rptIndexNo = "";
		if(StringUtils.isNotBlank(rptNm) && StringUtils.isNotBlank(cellNo)) {
			Template targetTemplate = null;
			targetTemplate = data.getTemplateMap().get(rptNm);
			if (targetTemplate != null) {
				if (data.getCellNoToCellMap().containsKey(targetTemplate)) {
					IndexBaseCell targetCell = data.getCellNoToCellMap().get(targetTemplate).get(cellNo);
					rptIndexNo = targetCell.getIndex().getIndexNo();
				}
			}
		}
		return rptIndexNo;
	}
}
