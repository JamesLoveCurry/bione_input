package com.yusys.bione.plugin.detailtmp.service;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.dialect.DBMS;
import com.yusys.bione.comp.repository.mybatis.dialect.Dialect;
import com.yusys.bione.comp.repository.mybatis.dialect.DialectClient;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.detailtmp.entity.*;
import com.yusys.bione.plugin.detailtmp.web.vo.RptDetailTmpInfoVO;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import com.yusys.bione.plugin.spreadjs.utils.CsvExportUtil;
import com.yusys.bione.plugin.spreadjs.utils.ExcelExportUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class DetailTmpBS extends BaseBS<Object> {
	@Autowired
	private ExcelBS excelBS;
	@Autowired
	private DataSourceBS dsBS;
	@Autowired
	private RptOrgInfoBS rptOrgBs;

	// 树根节点图标
	private String treeRootIcon = "/images/classics/icons/bricks.png";

	// 树文件夹节点图标
	private String treeFolderIcon = "/images/classics/icons/f1.gif";

	// 明细模板节点图标
	private String treeTmpIcon = "/images/classics/icons/application_view_list.png";

	private String rootNode = "01";
	private String catalogNode = "02";
	private String tmpNode = "03";

	public List<CommonTreeNode> getCatalogTree(String contextPath) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(generateRootNode(contextPath));
		List<RptDetailTmpCatalog> catalogs = this.getAllEntityList(
				RptDetailTmpCatalog.class, "catalogNm", false);
		if (catalogs != null && catalogs.size() > 0) {
			for (RptDetailTmpCatalog catalog : catalogs) {
				nodes.add(generateCatalogNode(contextPath, catalog));
			}
		}
		return nodes;
	}

	
	
	

	private void getAllCatalogInfo(String contextPath, List<String> catalogId,
			List<CommonTreeNode> nodes) {
		if (catalogId != null && catalogId.size() > 0) {
			String jql = "select catalog from RptDetailTmpCatalog catalog where catalog.catalogId in ?0";
			List<RptDetailTmpCatalog> catalogs = this.baseDAO
					.findWithIndexParam(jql, catalogId);
			if (catalogs != null && catalogs.size() > 0) {
				List<String> upIds = new ArrayList<String>();
				for (RptDetailTmpCatalog catalog : catalogs) {
					if (!catalog.getUpId().equals("0"))
						upIds.add(catalog.getUpId());
					nodes.add(generateCatalogNode(contextPath, catalog));
				}
				getAllCatalogInfo(contextPath, upIds, nodes);
			}
		}
	}

	private CommonTreeNode generateRootNode(String contextPath) {
		CommonTreeNode node = new CommonTreeNode();
		node.setId("0");
		node.setText("模板目录");
		node.setIcon(contextPath + treeRootIcon);
		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeType", this.rootNode);
		node.setParams(params);
		return node;
	}

	private CommonTreeNode generateCatalogNode(String contextPath,
			RptDetailTmpCatalog catalog) {
		CommonTreeNode node = new CommonTreeNode();
		node.setId(catalog.getCatalogId());
		node.setText(catalog.getCatalogNm());
		node.setIcon(contextPath + treeFolderIcon);
		node.setUpId(catalog.getUpId());
		node.setData(catalog);
		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeType", this.catalogNode);
		node.setParams(params);
		return node;
	}
	
	/**
	 * 设置机构权限
	 * @param cols
	 * @param searchAttr
	 */
	private void setOrgValidate(List<RptSysModuleCol> cols,List<Map<String, Object>> searchAttr) {
		List<String> validateOrgs = new ArrayList<String>();
		if (BioneSecurityUtils.getCurrentUserInfo() != null
				&& !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			Map<String, List<String>> validateMap = this.rptOrgBs
					.getValidateOrgInfo();
			validateOrgs = validateMap.get("validate");
			if (cols != null && cols.size() > 0) {
				for (RptSysModuleCol col : cols) {
					Map<String,Object> attr = new HashMap<String, Object>();
					String  enNm = col.getEnNm();
					attr.put("name", enNm);
					attr.put("colId", col.getColId());
					attr.put("value", validateOrgs);
					searchAttr.add(attr);
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> showView(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap,
			List<Map<String, Object>> searchAttr,
			List<RptDetailTmpConfig> configs, List<RptDetailTmpSort> sorts,
			Map<String, Object> filterMap, List<RptDetailTmpSum> sumInfos) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String,RptDetailTmpSum> sumMap = new HashMap<String, RptDetailTmpSum>();
		List<String> sumCol = new ArrayList<String>();
		if(sumInfos != null && sumInfos.size() > 0){
			for(RptDetailTmpSum sumInfo : sumInfos){
				sumMap.put(sumInfo.getId().getCfgId(), sumInfo);
			}
		}
		
		String dsId = "";
		if (configs != null && configs.size() > 0) {
			List<String> setIds = new ArrayList<String>();
			Map<String, RptSysModuleCol> cols = new HashMap<String, RptSysModuleCol>();
			Map<String, RptDetailTmpConfig> configMap = new HashMap<String, RptDetailTmpConfig>();
			Map<String, String> colEms = new HashMap<String, String>();
			Map<String, String> tableNms = new HashMap<String, String>();
			String tbNms = "";
			for (RptDetailTmpConfig config : configs) {
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						config.getColId());
				if (col != null) {
					setIds.add(col.getSetId());
				}
				configMap.put(config.getId().getCfgId(), config);
			}
			if (setIds != null && setIds.size() > 0) {
				String jql = "select info from RptSysModuleInfo info where info.setId in ?0";
				List<RptSysModuleInfo> infos = this.baseDAO.findWithIndexParam(
						jql, setIds);
				if (infos != null && infos.size() > 0) {
					for (RptSysModuleInfo info : infos) {
						dsId = info.getSourceId();
						tbNms += info.getTableEnNm() + ",";
						tableNms.put(info.getSetId(), info.getTableEnNm());
						jql = "select col from RptSysModuleCol col where col.setId=?0 and col.dimTypeNo = ?1";
						List<RptSysModuleCol> orgcols = this.baseDAO.findWithIndexParam(jql, info.getSetId(),"ORG");
						this.setOrgValidate(orgcols, searchAttr);
					}
					if (tbNms.length() > 0)
						tbNms = tbNms.substring(0, tbNms.length() - 1);
				}
			}
			int i = 0;
			int k = 0;
			String enNms = "";
			String toEnNms = "";
			Map<Integer, Integer> toCols = new HashMap<Integer, Integer>();
			Map<String,String> enNum = new HashMap<String, String>();
			for (RptDetailTmpConfig config : configs) {
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						config.getColId());
				if (col != null) {
					cols.put(col.getColId(), col);
					colEms.put(config.getId().getCfgId(), col.getEnNm());
					String enNm = tableNms.get(col.getSetId()) + "."
							+ col.getEnNm();
					if (config.getDataType().equals(
							GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)
							&& !config.getDisplayFormat().equals(
									GlobalConstants4plugin.DATA_TYPE_PERCENT)
							&& config.getIsSum().equals("Y")) {
						toEnNms += "SUM(" + enNm + ")" + " as \"" + i + "\",";
						toCols.put(i, k);
						k++;
					}
					RptDetailTmpSum s= sumMap.get(config.getId().getCfgId());
					if(s!=null){
						if(s.getColType().equals(GlobalConstants4plugin.VAL_COL_TYPE)){
							String func = "SUM(";
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_SUM)){
								func = "SUM(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_AVG)){
								func = "AVG(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_MAX)){
								func = "MAX(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_MIN)){
								func = "MIN(";
							}
							enNm = func+enNm+")";
						}
						else{
							sumCol.add(enNm);
						}
					}
					enNms += enNm + " as \"" + i + "\",";
					enNum.put(config.getId().getCfgId(),enNm);
					setIds.add(col.getSetId());
				}
				i++;
			}
			if (enNms.length() > 0)
				enNms = enNms.substring(0, enNms.length() - 1);
			if (toEnNms.length() > 0)
				toEnNms = toEnNms.substring(0, toEnNms.length() - 1);
			if (StringUtils.isNotBlank(enNms) && StringUtils.isNotBlank(tbNms)) {
				String sql = "select " + enNms + " from " + tbNms
						+ " where 1=1 ";
				String tsql = "select " + toEnNms + " from " + tbNms
						+ " where 1=1 ";
				Map<String, Object> wmap = createParams(searchAttr, tableNms);
				Map<String, Object> condition = new HashMap<String, Object>();
				int paramNo = 0;
				if (wmap != null) {
					condition = (Map<String, Object>) wmap.get("condition");
					sql += wmap.get("where").toString();
					tsql += wmap.get("where").toString();
					paramNo = (Integer) wmap.get("no");
				}
				if (filterMap != null && filterMap.size() > 0) {
					String where = createFilter(filterMap, tableNms, condition,
							cols, paramNo);
					if (StringUtils.isNotBlank(where)) {
						sql += " and " + where;
						tsql += " and " + where;
					}
				}
				
				if(sumCol.size() > 0){
					sql += " group by ";
					int s = 0 ;
					for(String col : sumCol){
						sql += col;
						if(s < sumCol.size()-1){
							sql += ",";
						}
						else{
							sql += " ";
						}
						s++;
					}
				}
				if (StringUtils.isNotBlank(orderBy)) {
					if (colEms.get(orderBy) != null) {
						String tbNm = tableNms.get(configMap.get(orderBy)
								.getSetId());
						sql += " order by " + tbNm + "." + colEms.get(orderBy)
								+ " " + orderType;
					}

				} else {
					if (sorts != null && sorts.size() > 0) {
						String order = " order by ";
						for (RptDetailTmpSort sort : sorts) {
							order += enNum.get(sort.getId().getCfgId()) + " "
									+ sort.getSortMode() + ",";
						}
						order = order.substring(0, order.length() - 1);
						sql += order;
					}
				}

				Map<String, Object> srs = this.getPageWithNameParamByNativeSQL(
						dsId, firstResult, pageSize, sql, condition);
				List<Object[]> srList = (List<Object[]>) srs.get("list");
				List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
				Map<String, Map<String, String>> dimMap = new HashMap<String, Map<String, String>>();
				if (srList != null && srList.size() > 0) {
					for (Object[] objs : srList) {
						Map<String, Object> row = new HashMap<String, Object>();
						for (int o = 0; o < objs.length; o++) {
							if (o < configs.size()) {
								RptDetailTmpConfig config = configs.get(o);
								if (config.getDataType().equals(
										GlobalConstants4plugin.LOGIC_DATA_TYPE_DATE)) {
									String formate = "yyyy年MM月dd日";
									if (StringUtils.isNotBlank(config
											.getDisplayFormat())) {
										formate = config.getDisplayFormat();
									}
									SimpleDateFormat df = new SimpleDateFormat(
											formate);
									if (objs[o] instanceof Timestamp) {
										Timestamp ts = (Timestamp) objs[o];
										Date date = new Date(ts.getTime());
										objs[o] = df.format(date);
									} else if (objs[o] != null
											&& objs[o]
													.getClass()
													.getName()
													.toLowerCase()
													.equals("oracle.sql.timestamp")) {
										String val = objs[o].toString();
										SimpleDateFormat odf = new SimpleDateFormat(
												"yyyy-MM-dd hh:mm:ss.SSS");
										Date date;
										try {
											date = odf.parse(val);
											objs[o] = df.format(date);
										} catch (ParseException e) {
											e.printStackTrace();
										}
										objs[o] = val;
									} else if (objs[o] instanceof Date) {
										objs[o] = df.format(objs[o]);
									}
								}
								
								if (StringUtils.isNotBlank(config
										.getDimTypeNo())
										&& config.getIsConver().equals("Y")) {
									objs[o] = changeValue(objs[o],
											config.getDimTypeNo(), dimMap);
								}
								if (config.getDataType().equals(
										GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)) {
									objs[o] = this
											.changeNumber(config, objs[o]);
								}
								row.put(config.getId().getCfgId(), objs[o]);
							}

						}
						rows.add(row);
					}
				}
				if (StringUtils.isNotBlank(toEnNms)) {
					Object[] sum = null;
					List<Object[]> sums = this.getWithNameParamByNativeSQL(
							dsId, tsql, condition);
					sum = sums.get(0);
					int l = 0;
					Map<String, Object> row = new HashMap<String, Object>();
					for (RptDetailTmpConfig config : configs) {
						if (l == 0) {
							row.put(config.getId().getCfgId(), "合计");
						}
						if (toCols.get(l) != null) {
							Object value = sum[toCols.get(l)];
							if (config.getDataType().equals(
									GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)) {
								value = this.changeNumber(config, value);
							}
							row.put(config.getId().getCfgId(), value);
						}
						l++;
					}
					rows.add(row);

				}

				result.put("Rows", rows);
				result.put("Total", srs.get("total"));
			}
		}
		return result;
	}
	
	
	private Map<String,Object> createRow(Object[] objs){
		Map<String, Object> row = new HashMap<String, Object>();
		for (int h = 0; h < objs.length; h++) {
			row.put("param" + String.valueOf(h),
					objs[h] != null ? objs[h].toString() : null);
		}
		return row;
	}
	private Map<String,Object> createRow(Object[] objs,List<RptDetailTmpConfig> configs,Map<String, Map<String, String>> dimMap){
		Map<String, Object> row = new HashMap<String, Object>();
		for (int o = 0; o < objs.length; o++) {
			if (o < configs.size()) {
				RptDetailTmpConfig config = configs.get(o);
				if (config.getDataType().equals(
						GlobalConstants4plugin.LOGIC_DATA_TYPE_DATE)) {
					String formate = "yyyy年MM月dd日";
					if (StringUtils.isNotBlank(config
							.getDisplayFormat())) {
						formate = config.getDisplayFormat();
					}
					SimpleDateFormat df = new SimpleDateFormat(
							formate);
					if (objs[o] instanceof Timestamp) {
						Timestamp ts = (Timestamp) objs[o];
						Date date = new Date(ts.getTime());
						objs[o] = df.format(date);
					} else if (objs[o] != null
							&& objs[o]
									.getClass()
									.getName()
									.toLowerCase()
									.equals("oracle.sql.timestamp")) {
						String val = objs[o].toString();
						SimpleDateFormat odf = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss.SSS");
						Date date;
						try {
							date = odf.parse(val);
							objs[o] = df.format(date);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						objs[o] = val;
					} else if (objs[o] instanceof Date) {
						objs[o] = df.format(objs[o]);
					}
				}
				
				if (StringUtils.isNotBlank(config
						.getDimTypeNo())
						&& config.getIsConver().equals("Y")) {
					objs[o] = changeValue(objs[o],
							config.getDimTypeNo(), dimMap);
				}
				if (config.getDataType().equals(
						GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)) {
					objs[o] = this
							.changeNumber(config, objs[o]);
				}
				row.put(config.getId().getCfgId(), objs[o]);
			}
		}
		return row;
	}
	@SuppressWarnings("unchecked")
	public Map<String,Integer> getExpDataCount(List<Map<String, Object>> searchAttr,
			List<RptDetailTmpConfig> configs, List<RptDetailTmpSort> sorts,
			Map<String, Object> filterMap,List<RptDetailTmpSum> sumInfos,String sql,String dsId) {
		Map<String, Integer> rows = new HashMap<String, Integer>();
		Integer dataNum = 0;
		if(StringUtils.isNotBlank(sql)){
			sql = sql.trim();
			sql = sql.replace('\n', ' ');
			if(sql.endsWith(";")){
				sql = sql.substring(0,sql.length()-1);
			}
			sql = sql.toLowerCase();
			sql = sql.substring(sql.indexOf("from")+4);
			sql = "select count(1) from (select 1 from " + sql + " )";
			if (StringUtils.isNotBlank(dsId)) {
				List<Object[]> srList = this.getWithNameParamByNativeSQL(dsId, sql,null);
				if(null != srList && srList.size() > 0){
					Object[] obj = srList.get(0);
					dataNum = Integer.parseInt(obj[0].toString());
				}
			}
		}else if (configs != null && configs.size() > 0) {
			List<String> setIds = new ArrayList<String>();
			Map<String, RptSysModuleCol> cols = new HashMap<String, RptSysModuleCol>();
			Map<String, RptDetailTmpConfig> configMap = new HashMap<String, RptDetailTmpConfig>();
			Map<String, String> colEms = new HashMap<String, String>();
			Map<String, String> tableNms = new HashMap<String, String>();
			Map<String,RptDetailTmpSum> sumMap = new HashMap<String, RptDetailTmpSum>();
			List<String> sumCol = new ArrayList<String>();
			if(sumInfos != null && sumInfos.size() > 0){
				for(RptDetailTmpSum sumInfo : sumInfos){
					sumMap.put(sumInfo.getId().getCfgId(), sumInfo);
				}
			}
			String tbNms = "";
			for (RptDetailTmpConfig config : configs) {
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						config.getColId());
				if (col != null) {
					setIds.add(col.getSetId());
				}
				configMap.put(config.getId().getCfgId(), config);
			}
			if (setIds != null && setIds.size() > 0) {
				String jql = "select info from RptSysModuleInfo info where info.setId in ?0";
				List<RptSysModuleInfo> infos = this.baseDAO.findWithIndexParam(
						jql, setIds);
				if (infos != null && infos.size() > 0) {
					for (RptSysModuleInfo info : infos) {
						dsId = info.getSourceId();
						tbNms += info.getTableEnNm() + ",";
						tableNms.put(info.getSetId(), info.getTableEnNm());
					}
					if (tbNms.length() > 0)
						tbNms = tbNms.substring(0, tbNms.length() - 1);
				}
			}
			int i = 0;
			int k = 0;
			String enNms = "";
			String toEnNms = "";
			Map<Integer, Integer> toCols = new HashMap<Integer, Integer>();
			for (RptDetailTmpConfig config : configs) {
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						config.getColId());
				if (col != null) {
					cols.put(col.getColId(), col);
					colEms.put(config.getId().getCfgId(), col.getEnNm());
					String enNm = tableNms.get(col.getSetId()) + "."
							+ col.getEnNm();
					if (config.getDataType().equals(
							GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)
							&& !config.getDisplayFormat().equals(
									GlobalConstants4plugin.DATA_TYPE_PERCENT)
							&& config.getIsSum().equals("Y")) {
						toEnNms += "SUM(" + enNm + ")" + " as \"" + i + "\",";
						toCols.put(i, k);
						k++;
					}
					RptDetailTmpSum s= sumMap.get(config.getId().getCfgId());
					if(s!=null){
						if(s.getColType().equals(GlobalConstants4plugin.VAL_COL_TYPE)){
							String func = "SUM(";
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_SUM)){
								func = "SUM(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_AVG)){
								func = "AVG(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_MAX)){
								func = "MAX(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_MIN)){
								func = "MIN(";
							}
							enNm = func+enNm+")";
						}
						else{
							sumCol.add(enNm);
						}
					}
					enNms += enNm + " as \"" + i + "\",";
					setIds.add(col.getSetId());
				}
				i++;
			}
			if (enNms.length() > 0)
				enNms = enNms.substring(0, enNms.length() - 1);
			if (toEnNms.length() > 0)
				toEnNms = toEnNms.substring(0, toEnNms.length() - 1);
			if (StringUtils.isNotBlank(enNms) && StringUtils.isNotBlank(tbNms)) {
				String msql = "select 1 from " + tbNms
						+ " where 1=1 ";
				String tsql = "select 1 from " + tbNms
						+ " where 1=1 ";
				Map<String, Object> wmap = createParams(searchAttr, tableNms);
				Map<String, Object> condition = new HashMap<String, Object>();
				int paramNo = 0;
				if (wmap != null) {
					condition = (Map<String, Object>) wmap.get("condition");
					msql += wmap.get("where").toString();
					tsql += wmap.get("where").toString();
					paramNo = (Integer) wmap.get("no");
				}
				if (filterMap != null && filterMap.size() > 0) {
					String where = createFilter(filterMap, tableNms, condition,
							cols, paramNo);
					if (StringUtils.isNotBlank(where)) {
						msql += " and " + where;
						tsql += " and " + where;
					}
				} 
				if(sumCol.size() > 0){
					msql += " group by ";
					int s = 0 ;
					for(String col : sumCol){
						msql += col;
						if(s < sumCol.size()-1){
							msql += ",";
						}
						else{
							msql += " ";
						}
						s++;
					}
				}
				if (sorts != null && sorts.size() > 0) {
					String order = " order by ";
					for (RptDetailTmpSort sort : sorts) {
						String tbNm = tableNms.get(configMap.get(
								sort.getId().getCfgId()).getSetId());
						order += tbNm + "."
								+ colEms.get(sort.getId().getCfgId()) + " "
								+ sort.getSortMode() + ",";
					}
					order = order.substring(0, order.length() - 1);
					msql += order;
				}
				msql = msql.toLowerCase();
				msql = msql.substring(msql.indexOf("from")+4);
				msql = "select count(1) from (select 1 from " + msql + " )";
				List<Object[]> srList = this.getWithNameParamByNativeSQL(dsId, msql, condition);
				if (srList != null && srList.size() > 0) {
					Object[] obj = srList.get(0);
					dataNum += Integer.parseInt(obj[0].toString());
				}
				tsql = tsql.toLowerCase();
				tsql = tsql.substring(tsql.indexOf("from")+4);
				tsql = "select count(1) from (select 1 from " + tsql + " )";
				if (StringUtils.isNotBlank(toEnNms)) {
					List<Object[]> sums = this.getWithNameParamByNativeSQL(dsId, tsql, condition);
					if (sums != null && sums.size() > 0) {
						Object[] obj = srList.get(0);
						dataNum += Integer.parseInt(obj[0].toString());
					}
				}
			}
		}
		rows.put("dataNum", dataNum);
		return rows;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> showView(
			List<Map<String, Object>> searchAttr,
			List<RptDetailTmpConfig> configs, List<RptDetailTmpSort> sorts,
			Map<String, Object> filterMap,List<RptDetailTmpSum> sumInfos,ExcelExportUtil util) {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		String dsId = "";
		if (configs != null && configs.size() > 0) {
			List<String> setIds = new ArrayList<String>();
			Map<String, RptSysModuleCol> cols = new HashMap<String, RptSysModuleCol>();
			Map<String, RptDetailTmpConfig> configMap = new HashMap<String, RptDetailTmpConfig>();
			Map<String, String> colEms = new HashMap<String, String>();
			Map<String, String> tableNms = new HashMap<String, String>();
			Map<String,RptDetailTmpSum> sumMap = new HashMap<String, RptDetailTmpSum>();
			List<String> sumCol = new ArrayList<String>();
			if(sumInfos != null && sumInfos.size() > 0){
				for(RptDetailTmpSum sumInfo : sumInfos){
					sumMap.put(sumInfo.getId().getCfgId(), sumInfo);
				}
			}
			String tbNms = "";
			for (RptDetailTmpConfig config : configs) {
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						config.getColId());
				if (col != null) {
					setIds.add(col.getSetId());
				}
				configMap.put(config.getId().getCfgId(), config);
			}
			if (setIds != null && setIds.size() > 0) {
				String jql = "select info from RptSysModuleInfo info where info.setId in ?0";
				List<RptSysModuleInfo> infos = this.baseDAO.findWithIndexParam(
						jql, setIds);
				if (infos != null && infos.size() > 0) {
					for (RptSysModuleInfo info : infos) {
						dsId = info.getSourceId();
						tbNms += info.getTableEnNm() + ",";
						tableNms.put(info.getSetId(), info.getTableEnNm());
					}
					if (tbNms.length() > 0)
						tbNms = tbNms.substring(0, tbNms.length() - 1);
				}
			}
			int i = 0;
			int k = 0;
			String enNms = "";
			String toEnNms = "";
			Map<Integer, Integer> toCols = new HashMap<Integer, Integer>();
			Map<String,String> enNum = new HashMap<String,String>();
			for (RptDetailTmpConfig config : configs) {
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						config.getColId());
				if (col != null) {
					cols.put(col.getColId(), col);
					colEms.put(config.getId().getCfgId(), col.getEnNm());
					String enNm = tableNms.get(col.getSetId()) + "."
							+ col.getEnNm();
					if (config.getDataType().equals(
							GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)
							&& !config.getDisplayFormat().equals(
									GlobalConstants4plugin.DATA_TYPE_PERCENT)
							&& config.getIsSum().equals("Y")) {
						toEnNms += "SUM(" + enNm + ")" + " as \"" + i + "\",";
						toCols.put(i, k);
						k++;
					}
					RptDetailTmpSum s= sumMap.get(config.getId().getCfgId());
					if(s!=null){
						if(s.getColType().equals(GlobalConstants4plugin.VAL_COL_TYPE)){
							String func = "SUM(";
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_SUM)){
								func = "SUM(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_AVG)){
								func = "AVG(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_MAX)){
								func = "MAX(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_MIN)){
								func = "MIN(";
							}
							enNm = func+enNm+")";
						}
						else{
							sumCol.add(enNm);
						}
					}
					enNum.put(config.getId().getCfgId(), enNm);
					enNms += enNm + " as \"" + i + "\",";
					setIds.add(col.getSetId());
				}
				i++;
			}
			if (enNms.length() > 0)
				enNms = enNms.substring(0, enNms.length() - 1);
			if (toEnNms.length() > 0)
				toEnNms = toEnNms.substring(0, toEnNms.length() - 1);
			if (StringUtils.isNotBlank(enNms) && StringUtils.isNotBlank(tbNms)) {
				String sql = "select " + enNms + " from " + tbNms
						+ " where 1=1 ";
				String tsql = "select " + toEnNms + " from " + tbNms
						+ " where 1=1 ";
				Map<String, Object> wmap = createParams(searchAttr, tableNms);
				Map<String, Object> condition = new HashMap<String, Object>();
				int paramNo = 0;
				if (wmap != null) {
					condition = (Map<String, Object>) wmap.get("condition");
					sql += wmap.get("where").toString();
					tsql += wmap.get("where").toString();
					paramNo = (Integer) wmap.get("no");
				}
				if (filterMap != null && filterMap.size() > 0) {
					String where = createFilter(filterMap, tableNms, condition,
							cols, paramNo);
					if (StringUtils.isNotBlank(where)) {
						sql += " and " + where;
						tsql += " and " + where;
					}
				} 
				if(sumCol.size() > 0){
					sql += " group by ";
					int s = 0 ;
					for(String col : sumCol){
						sql += col;
						if(s < sumCol.size()-1){
							sql += ",";
						}
						else{
							sql += " ";
						}
						s++;
					}
				}
				if (sorts != null && sorts.size() > 0) {
					String order = " order by ";
					for (RptDetailTmpSort sort : sorts) {
						order += enNum.get(sort.getId().getCfgId()) + " "
								+ sort.getSortMode() + ",";
					}
					order = order.substring(0, order.length() - 1);
					sql += order;
				}
				this.getWithNameParamByNativeSQL(dsId,
						sql, condition,configs,util);
			
				if (StringUtils.isNotBlank(toEnNms)) {
					Object[] sum = null;
					List<Object[]> sums = this.getWithNameParamByNativeSQL(
							dsId, tsql, condition);
					sum = sums.get(0);
					int l = 0;
					Map<String, Object> row = new HashMap<String, Object>();
					for (RptDetailTmpConfig config : configs) {
						if (l == 0) {
							row.put(config.getId().getCfgId(), "合计");
						}
						if (toCols.get(l) != null) {
							Object value = sum[toCols.get(l)];
							if (config.getDataType().equals(
									GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)) {
								value = this.changeNumber(config, value);
							}
							row.put(config.getId().getCfgId(), value);
						}
						l++;
					}
					util.addRow(row);

				}
			}
		}
		return rows;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> showView(
			List<Map<String, Object>> searchAttr,
			List<RptDetailTmpConfig> configs, List<RptDetailTmpSort> sorts,
			Map<String, Object> filterMap,List<RptDetailTmpSum> sumInfos,CsvExportUtil util) {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		String dsId = "";
		if (configs != null && configs.size() > 0) {
			List<String> setIds = new ArrayList<String>();
			Map<String, RptSysModuleCol> cols = new HashMap<String, RptSysModuleCol>();
			Map<String, RptDetailTmpConfig> configMap = new HashMap<String, RptDetailTmpConfig>();
			Map<String, String> colEms = new HashMap<String, String>();
			Map<String, String> tableNms = new HashMap<String, String>();
			Map<String,RptDetailTmpSum> sumMap = new HashMap<String, RptDetailTmpSum>();
			List<String> sumCol = new ArrayList<String>();
			if(sumInfos != null && sumInfos.size() > 0){
				for(RptDetailTmpSum sumInfo : sumInfos){
					sumMap.put(sumInfo.getId().getCfgId(), sumInfo);
				}
			}
			String tbNms = "";
			for (RptDetailTmpConfig config : configs) {
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						config.getColId());
				if (col != null) {
					setIds.add(col.getSetId());
				}
				configMap.put(config.getId().getCfgId(), config);
			}
			if (setIds != null && setIds.size() > 0) {
				String jql = "select info from RptSysModuleInfo info where info.setId in ?0";
				List<RptSysModuleInfo> infos = this.baseDAO.findWithIndexParam(
						jql, setIds);
				if (infos != null && infos.size() > 0) {
					for (RptSysModuleInfo info : infos) {
						dsId = info.getSourceId();
						tbNms += info.getTableEnNm() + ",";
						tableNms.put(info.getSetId(), info.getTableEnNm());
					}
					if (tbNms.length() > 0)
						tbNms = tbNms.substring(0, tbNms.length() - 1);
				}
			}
			int i = 0;
			int k = 0;
			String enNms = "";
			String toEnNms = "";
			Map<Integer, Integer> toCols = new HashMap<Integer, Integer>();
			Map<String,String> enNum = new HashMap<String,String>();
			for (RptDetailTmpConfig config : configs) {
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						config.getColId());
				if (col != null) {
					cols.put(col.getColId(), col);
					colEms.put(config.getId().getCfgId(), col.getEnNm());
					String enNm = tableNms.get(col.getSetId()) + "."
							+ col.getEnNm();
					if (config.getDataType().equals(
							GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)
							&& !config.getDisplayFormat().equals(
									GlobalConstants4plugin.DATA_TYPE_PERCENT)
							&& config.getIsSum().equals("Y")) {
						toEnNms += "SUM(" + enNm + ")" + " as \"" + i + "\",";
						toCols.put(i, k);
						k++;
					}
					RptDetailTmpSum s= sumMap.get(config.getId().getCfgId());
					if(s!=null){
						if(s.getColType().equals(GlobalConstants4plugin.VAL_COL_TYPE)){
							String func = "SUM(";
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_SUM)){
								func = "SUM(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_AVG)){
								func = "AVG(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_MAX)){
								func = "MAX(";
							}
							if(s.getSumMode().equals(GlobalConstants4plugin.DATA_SUM_MIN)){
								func = "MIN(";
							}
							enNm = func+enNm+")";
						}
						else{
							sumCol.add(enNm);
						}
					}
					enNum.put(config.getId().getCfgId(), enNm);
					enNms += enNm + " as \"" + i + "\",";
					setIds.add(col.getSetId());
				}
				i++;
			}
			if (enNms.length() > 0)
				enNms = enNms.substring(0, enNms.length() - 1);
			if (toEnNms.length() > 0)
				toEnNms = toEnNms.substring(0, toEnNms.length() - 1);
			if (StringUtils.isNotBlank(enNms) && StringUtils.isNotBlank(tbNms)) {
				String sql = "select " + enNms + " from " + tbNms
						+ " where 1=1 ";
				String tsql = "select " + toEnNms + " from " + tbNms
						+ " where 1=1 ";
				Map<String, Object> wmap = createParams(searchAttr, tableNms);
				Map<String, Object> condition = new HashMap<String, Object>();
				int paramNo = 0;
				if (wmap != null) {
					condition = (Map<String, Object>) wmap.get("condition");
					sql += wmap.get("where").toString();
					tsql += wmap.get("where").toString();
					paramNo = (Integer) wmap.get("no");
				}
				if (filterMap != null && filterMap.size() > 0) {
					String where = createFilter(filterMap, tableNms, condition,
							cols, paramNo);
					if (StringUtils.isNotBlank(where)) {
						sql += " and " + where;
						tsql += " and " + where;
					}
				} 
				if(sumCol.size() > 0){
					sql += " group by ";
					int s = 0 ;
					for(String col : sumCol){
						sql += col;
						if(s < sumCol.size()-1){
							sql += ",";
						}
						else{
							sql += " ";
						}
						s++;
					}
				}
				if (sorts != null && sorts.size() > 0) {
					String order = " order by ";
					for (RptDetailTmpSort sort : sorts) {
						order += enNum.get(sort.getId().getCfgId()) + " "
								+ sort.getSortMode() + ",";
					}
					order = order.substring(0, order.length() - 1);
					sql += order;
				}
				this.getWithNameParamByNativeSQL(dsId,
						sql, condition,configs,util);
			
				if (StringUtils.isNotBlank(toEnNms)) {
					Object[] sum = null;
					List<Object[]> sums = this.getWithNameParamByNativeSQL(
							dsId, tsql, condition);
					sum = sums.get(0);
					int l = 0;
					Map<String, Object> row = new HashMap<String, Object>();
					for (RptDetailTmpConfig config : configs) {
						if (l == 0) {
							row.put(config.getId().getCfgId(), "合计");
						}
						if (toCols.get(l) != null) {
							Object value = sum[toCols.get(l)];
							if (config.getDataType().equals(
									GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)) {
								value = this.changeNumber(config, value);
							}
							row.put(config.getId().getCfgId(), value);
						}
						l++;
					}
					util.addRow(row);

				}
			}
		}
		return rows;
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> showSql(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap,
			String sql, String dsId) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		
		if (StringUtils.isNotBlank(dsId) && StringUtils.isNotBlank(sql)) {
			sql = sql.trim();
			sql = sql.replace('\n', ' ');
			if(sql.endsWith(";")){
				sql = sql.substring(0,sql.length()-1);
			}
			Map<String, Object> srs = this.getPageWithNameParamByNativeSQL(
					dsId, firstResult, pageSize, sql, null);
			List<Object[]> srList = (List<Object[]>) srs.get("list");
			if (srList != null && srList.size() > 0) {
				for (Object[] objs : srList) {
					Map<String, Object> row = new HashMap<String, Object>();
					for (int h = 0; h < objs.length; h++) {
						row.put("param" + String.valueOf(h),
								objs[h] != null ? objs[h].toString() : null);
					}
					rows.add(row);
				}
			}
			result.put("error", srs.get("error"));
			result.put("column", srs.get("column"));
			result.put("Total", srs.get("total"));
		}
		result.put("Rows", rows);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> showSql(String sql, String dsId) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		
		if (StringUtils.isNotBlank(dsId) && StringUtils.isNotBlank(sql)) {
			sql = sql.trim();
			sql = sql.replace('\n', ' ');
			if(sql.endsWith(";")){
				sql = sql.substring(0,sql.length()-1);
			}
			Map<String, Object> srs = this.getPageWithNameParamByNativeSQL(
					dsId,sql, null);
			List<Object[]> srList = (List<Object[]>) srs.get("list");
			if (srList != null && srList.size() > 0) {
				for (Object[] objs : srList) {
					Map<String, Object> row = new HashMap<String, Object>();
					for (int h = 0; h < objs.length; h++) {
						row.put("param" + String.valueOf(h),
								objs[h] != null ? objs[h].toString() : null);
					}
					rows.add(row);
				}
			}
			result.put("error", srs.get("error"));
			result.put("column", srs.get("column"));
		}
		
		result.put("Rows", rows);
		return result;
	}


	public  void showSql(String sql, String dsId, ExcelExportUtil util) {
		sql = sql.trim();
		sql = sql.replace('\n', ' ');
		if(sql.endsWith(";")){
			sql = sql.substring(0,sql.length()-1);
		}
		if (StringUtils.isNotBlank(dsId) && StringUtils.isNotBlank(sql)) {
			this.getWithNameParamByNativeSQL(dsId, sql,
					null,null,util);
		}
	}
	
	public  void showSql(String sql, String dsId,CsvExportUtil util) {
		sql = sql.trim();
		sql = sql.replace('\n', ' ');
		if(sql.endsWith(";")){
			sql = sql.substring(0,sql.length()-1);
		}
		if (StringUtils.isNotBlank(dsId) && StringUtils.isNotBlank(sql)) {
			this.getWithNameParamByNativeSQL(dsId, sql,
					null,null,util);
		}
	}

	public HashMap<String, Object> getColumnNativeSQL(String dsId, String sql) {
		sql = sql.trim();
		sql = sql.replace('\n', ' ');
		if(sql.endsWith(";")){
			sql = sql.substring(0,sql.length()-1);
		}
		HashMap<String, Object> res = new HashMap<String, Object>();
		List<String> columns = new ArrayList<String>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = this.dsBS.getConnection(dsId);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			int record = rs.getMetaData().getColumnCount();
			for (int k = 0; k < record; k++) {
				columns.add(rs.getMetaData().getColumnName(k + 1));
			}
			rs.close();
			res.put("columns", columns);
		} catch (Exception e) {
			res.put("error", e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(pst);
			JdbcUtils.closeConnection(conn);
		}
		return res;
	}

	private List<Object[]> getWithNameParamByNativeSQL(String dsId, String jql,
			Map<String, Object> condition) {
		List<Object[]> list = new ArrayList<Object[]>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = this.dsBS.getConnection(dsId);
			Pattern pattern = Pattern.compile(":[^ ]+");
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pattern.matcher(jql);
			List<Object> params = new ArrayList<Object>();
			while (matcher.find()) {
				String param = matcher.group(0).substring(1,
						matcher.group(0).length());
				Object parVal = condition.get(param);
				String reStr = "?";
				if (parVal instanceof Collection
						&& ((Collection<?>) parVal).size() > 0) {
					reStr = "(";
					for (int h = 0; h < ((Collection<?>) parVal).size(); h++) {
						if (h != ((Collection<?>) parVal).size() - 1)
							reStr += "?,";
						else
							reStr += "?) ";
						params.add(((Collection<?>) parVal).toArray()[h]);
					}
				} else {
					params.add(parVal);
				}
				matcher.appendReplacement(sb, reStr);
			}
			matcher.appendTail(sb);
			jql = sb.toString();
			pst = conn.prepareStatement(jql);
			for (int l = 0; l < params.size(); l++) {
				pst.setObject(l + 1, params.get(l));
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				int record = rs.getMetaData().getColumnCount();
				Object objs[] = new Object[record];
				for (int h = 0; h < record; h++) {
					objs[h] = rs.getObject(h + 1);
				}
				list.add(objs);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(conn);
			JdbcUtils.closeStatement(pst);
			JdbcUtils.closeResultSet(rs);
		}
		return list;
	}

	private void getWithNameParamByNativeSQL(String dsId, String jql,
			Map<String, Object> condition,List<RptDetailTmpConfig> configs,ExcelExportUtil util) {
		Map<String, Map<String, String>> dimMap = new HashMap<String, Map<String, String>>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = this.dsBS.getConnection(dsId);
			Pattern pattern = Pattern.compile(":[^ ]+");
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pattern.matcher(jql);
			List<Object> params = new ArrayList<Object>();
			while (matcher.find()) {
				String param = matcher.group(0).substring(1,
						matcher.group(0).length());
				Object parVal = condition.get(param);
				String reStr = "?";
				if (parVal instanceof Collection
						&& ((Collection<?>) parVal).size() > 0) {
					reStr = "(";
					for (int h = 0; h < ((Collection<?>) parVal).size(); h++) {
						if (h != ((Collection<?>) parVal).size() - 1)
							reStr += "?,";
						else
							reStr += "?) ";
						params.add(((Collection<?>) parVal).toArray()[h]);
					}
				} else {
					params.add(parVal);
				}
				matcher.appendReplacement(sb, reStr);
			}
			matcher.appendTail(sb);
			jql = sb.toString();
			pst = conn.prepareStatement(jql);
			for (int l = 0; l < params.size(); l++) {
				pst.setObject(l + 1, params.get(l));
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				int record = rs.getMetaData().getColumnCount();
				Object objs[] = new Object[record];
				for (int h = 0; h < record; h++) {
					objs[h] = rs.getObject(h + 1);
				}
				if(configs!= null)
					util.addRow(createRow(objs, configs, dimMap));
				else{
					util.addRow(createRow(objs));
				}
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(conn);
			JdbcUtils.closeStatement(pst);
			JdbcUtils.closeResultSet(rs);
		}
	}
	
	private void getWithNameParamByNativeSQL(String dsId, String jql,
			Map<String, Object> condition,List<RptDetailTmpConfig> configs,CsvExportUtil util) {
		Map<String, Map<String, String>> dimMap = new HashMap<String, Map<String, String>>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = this.dsBS.getConnection(dsId);
			Pattern pattern = Pattern.compile(":[^ ]+");
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pattern.matcher(jql);
			List<Object> params = new ArrayList<Object>();
			while (matcher.find()) {
				String param = matcher.group(0).substring(1,
						matcher.group(0).length());
				Object parVal = condition.get(param);
				String reStr = "?";
				if (parVal instanceof Collection
						&& ((Collection<?>) parVal).size() > 0) {
					reStr = "(";
					for (int h = 0; h < ((Collection<?>) parVal).size(); h++) {
						if (h != ((Collection<?>) parVal).size() - 1)
							reStr += "?,";
						else
							reStr += "?) ";
						params.add(((Collection<?>) parVal).toArray()[h]);
					}
				} else {
					params.add(parVal);
				}
				matcher.appendReplacement(sb, reStr);
			}
			matcher.appendTail(sb);
			jql = sb.toString();
			pst = conn.prepareStatement(jql);
			for (int l = 0; l < params.size(); l++) {
				pst.setObject(l + 1, params.get(l));
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				int record = rs.getMetaData().getColumnCount();
				Object objs[] = new Object[record];
				for (int h = 0; h < record; h++) {
					objs[h] = rs.getObject(h + 1);
				}
				if(configs!=null)
					util.addRow(createRow(objs, configs, dimMap));
				else
					util.addRow(createRow(objs));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(conn);
			JdbcUtils.closeStatement(pst);
			JdbcUtils.closeResultSet(rs);
		}
	}

	private Map<String, Object> getPageWithNameParamByNativeSQL(String dsId,
			int firstResult, int pageSize, String jql,
			Map<String, Object> condition) {
		String dsjql = "select ds, dr from BioneDsInfo ds, BioneDriverInfo dr where ds.dsId = ?0 and ds.driverId = dr.driverId";
		Object[] obj = this.baseDAO.findUniqueWithIndexParam(dsjql, dsId);
//		BioneDsInfo dsInfo = (BioneDsInfo) obj[0];
		BioneDriverInfo driverInfo = (BioneDriverInfo)obj[1];
		List<String> columns = new ArrayList<String>();
		Map<String, Object> result = new HashMap<String, Object>();
		Dialect dialect = getDialect(dsId);
		String sql = dialect.getLimitString(jql, firstResult, pageSize);
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = this.dsBS.getConnection(dsId);
			Pattern pattern = Pattern.compile(":[^ ]+");
			int total = 0;
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pattern.matcher(sql);
			List<Object> params = new ArrayList<Object>();
			while (matcher.find()) {
				String param = matcher.group(0).substring(1,
						matcher.group(0).length());
				Object parVal = condition.get(param);
				String reStr = "?";
				if (parVal instanceof Collection
						&& ((Collection<?>) parVal).size() > 0) {
					reStr = "(";
					for (int h = 0; h < ((Collection<?>) parVal).size(); h++) {
						if (h != ((Collection<?>) parVal).size() - 1)
							reStr += "?,";
						else
							reStr += "?) ";
						params.add(((Collection<?>) parVal).toArray()[h]);
					}
				} else {
					params.add(parVal);
				}
				matcher.appendReplacement(sb, reStr);
			}
			matcher.appendTail(sb);
			sql = sb.toString();

			pattern = Pattern.compile(":[^ ]+");
			StringBuffer ssb = new StringBuffer();
			matcher = pattern.matcher(jql);
			while (matcher.find()) {
				String param = matcher.group(0).substring(1,
						matcher.group(0).length());
				Object parVal = condition.get(param);
				String reStr = "?";
				if (parVal instanceof Collection
						&& ((Collection<?>) parVal).size() > 0) {
					reStr = "(";
					for (int h = 0; h < ((Collection<?>) parVal).size(); h++) {
						if (h != ((Collection<?>) parVal).size() - 1)
							reStr += "?,";
						else
							reStr += "?) ";
					}
				}
				matcher.appendReplacement(ssb, reStr);
			}
			matcher.appendTail(ssb);
			jql = ssb.toString();

			pst = conn.prepareStatement(sql);
			for (int l = 0; l < params.size(); l++) {
				pst.setObject(l + 1, params.get(l));
			}
			rs = pst.executeQuery();
			int record = rs.getMetaData().getColumnCount() -1;
			if(GlobalConstants4plugin.BIONE_DRIVER_INCEPTOR.equals(driverInfo.getDriverId())
					|| GlobalConstants4plugin.BIONE_DRIVER_GBASE.equals(driverInfo.getDriverId())
					|| GlobalConstants4plugin.BIONE_DRIVER_MYSQL.equals(driverInfo.getDriverId())
			        || GlobalConstants4frame.POSTGRESQL_DATA_SOURCE.equals(driverInfo.getDriverId())){
				record += 1;
			}
			if(columns.size()<=0){
				for (int k = 0; k < record; k++) {
					columns.add(rs.getMetaData().getColumnName(k + 1));
				}
			}
			List<Object[]> list = new ArrayList<Object[]>();
			while (rs.next()) {
				Object objs[] = new Object[record];
				for (int h = 0; h < record; h++) {
					objs[h] = rs.getObject(h + 1);
				}
				list.add(objs);
			}
			pst.close();
			rs.close();
			jql = "select count(1) from ("+jql+" )t";
			pst = conn.prepareStatement(jql);

			for (int l = 0; l < params.size(); l++) {
				pst.setObject(l + 1, params.get(l));
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				total = rs.getInt(1);
			}
			rs.close();
			result.put("column", columns);
			result.put("list", list);
			result.put("total", total);
		} catch (Exception e) {
			result.put("error", e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(pst);
			JdbcUtils.closeConnection(conn);
		}

		return result;
	}
	
	private Map<String, Object> getPageWithNameParamByNativeSQL(String dsId, String jql,
			Map<String, Object> condition) {
//		String dsjql = "select ds, dr from BioneDsInfo ds, BioneDriverInfo dr where ds.dsId = ?0 and ds.driverId = dr.driverId";
//		Object[] obj = this.baseDAO.findUniqueWithIndexParam(dsjql, dsId);
//		BioneDsInfo dsInfo = (BioneDsInfo) obj[0];
//		BioneDriverInfo driverInfo = (BioneDriverInfo)obj[1];
		List<String> columns = new ArrayList<String>();
		Map<String, Object> result = new HashMap<String, Object>();
		Dialect dialect = getDialect(dsId);
		String sql = dialect.getLimitString(jql, 0, 2000);
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = this.dsBS.getConnection(dsId);
			Pattern pattern = Pattern.compile(":[^ ]+");
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pattern.matcher(sql);
			List<Object> params = new ArrayList<Object>();
			while (matcher.find()) {
				String param = matcher.group(0).substring(1,
						matcher.group(0).length());
				Object parVal = condition.get(param);
				String reStr = "?";
				if (parVal instanceof Collection
						&& ((Collection<?>) parVal).size() > 0) {
					reStr = "(";
					for (int h = 0; h < ((Collection<?>) parVal).size(); h++) {
						if (h != ((Collection<?>) parVal).size() - 1)
							reStr += "?,";
						else
							reStr += "?) ";
						params.add(((Collection<?>) parVal).toArray()[h]);
					}
				} else {
					params.add(parVal);
				}
				matcher.appendReplacement(sb, reStr);
			}
			matcher.appendTail(sb);
			sql = sb.toString();

			pattern = Pattern.compile(":[^ ]+");
			StringBuffer ssb = new StringBuffer();
			matcher = pattern.matcher(jql);
			while (matcher.find()) {
				String param = matcher.group(0).substring(1,
						matcher.group(0).length());
				Object parVal = condition.get(param);
				String reStr = "?";
				if (parVal instanceof Collection
						&& ((Collection<?>) parVal).size() > 0) {
					reStr = "(";
					for (int h = 0; h < ((Collection<?>) parVal).size(); h++) {
						if (h != ((Collection<?>) parVal).size() - 1)
							reStr += "?,";
						else
							reStr += "?) ";
					}
				}
				matcher.appendReplacement(ssb, reStr);
			}
			matcher.appendTail(ssb);
			jql = ssb.toString();

			pst = conn.prepareStatement(sql);
			for (int l = 0; l < params.size(); l++) {
				pst.setObject(l + 1, params.get(l));
			}
			rs = pst.executeQuery();
			List<Object[]> list = new ArrayList<Object[]>();
			while (rs.next()) {
				int record = rs.getMetaData().getColumnCount();
				if(columns.size()<=0){
					for (int k = 0; k < record; k++) {
						columns.add(rs.getMetaData().getColumnName(k + 1));
					}
				}
				Object objs[] = new Object[record];
				for (int h = 0; h < record; h++) {
					objs[h] = rs.getObject(h + 1);
				}
				list.add(objs);
			}
			pst.close();
			rs.close();
			result.put("column", columns);
			result.put("list", list);
		} catch (Exception e) {
			result.put("error", e.getMessage());
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(pst);
			JdbcUtils.closeConnection(conn);
		}

		return result;
	}
	

	private Dialect getDialect(String dsId) {
		BioneDsInfo ds = this.getEntityById(BioneDsInfo.class, dsId);
		String dialectType = "mysql";
		if(GlobalConstants4plugin.BIONE_DRIVER_ORACLE_RAC.equals(ds.getDriverId()) || GlobalConstants4plugin.BIONE_DRIVER_ORACLE.equals(ds.getDriverId())){
			dialectType = "oracle";
		} else if (GlobalConstants4plugin.BIONE_DRIVER_DB2.equals(ds.getDriverId())) {
			dialectType = "db2";
		} else if (GlobalConstants4plugin.BIONE_DRIVER_H2.equals(ds.getDriverId())) {
			dialectType = "h2";
		} else if (GlobalConstants4plugin.BIONE_DRIVER_MYSQL.equals(ds.getDriverId()) || GlobalConstants4plugin.BIONE_DRIVER_GBASE.equals(ds.getDriverId())) {
			dialectType = "mysql";
		}

		DBMS dbms = DBMS.valueOf(dialectType.toUpperCase());
		Dialect dialect = DialectClient.getDbmsDialect(dbms);
		return dialect;
	}

	private Object changeNumber(RptDetailTmpConfig config, Object value) {
		if (config.getDisplayFormat().equals(GlobalConstants4plugin.DATA_TYPE_NULL)) {

		}
		if (config.getDisplayFormat().equals(GlobalConstants4plugin.DATA_TYPE_MONEY)) {
			String dataUnit = config.getDataUnit();
			int dataPrecision = config.getDataPrecision().intValue();
			value = setValue(dataUnit, value, dataPrecision, true);
		}
		if (config.getDisplayFormat().equals(GlobalConstants4plugin.DATA_TYPE_VALUE)) {
			String dataUnit = config.getDataUnit();
			int dataPrecision = config.getDataPrecision().intValue();
			value = setValue(dataUnit, value, dataPrecision, false);
		}
		if (config.getDisplayFormat().equals(GlobalConstants4plugin.DATA_TYPE_PERCENT)) {
			int dataPrecision = config.getDataPrecision().intValue();
			try {
				BigDecimal bd = new BigDecimal(String.valueOf(value)).multiply(
						new BigDecimal(100)).setScale(dataPrecision,
						BigDecimal.ROUND_HALF_UP);
				value = bd.toString() + "%";
			} catch (Exception e) {

			}
		}
		return value;
	}

	private Object changeValue(Object value, String dimTypeNo,
			Map<String, Map<String, String>> dimMap) {
		if (StringUtils.isNotBlank(dimTypeNo)) {
			if (dimTypeNo.equals("ORG")) {
				Map<String, String> dim = dimMap.get("ORG");
				if (dim == null) {
					dim = new HashMap<String, String>();
					String jql = "select org from RptOrgInfo org where 1=1 and org.id.orgType = ?0";
					List<RptOrgInfo> orgs = this.baseDAO.findWithIndexParam(
							jql, GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
					if (orgs != null && orgs.size() > 0) {
						for (RptOrgInfo org : orgs) {
							dim.put(org.getId().getOrgNo(), org.getOrgNm());
						}
					}
					dimMap.put("ORG", dim);
				}

				String name = dim.get(value);
				if (name != null) {
					return name;
				} else {
					return value;
				}
			} else if (dimTypeNo.equals("DATE")) {
				return value;
			} else {
				Map<String, String> dim = dimMap.get(dimTypeNo);
				if (dim == null) {
					dim = new HashMap<String, String>();
					String jql = "select item from RptDimItemInfo item where item.id.dimTypeNo = ?0 ";
					List<RptDimItemInfo> items = this.baseDAO
							.findWithIndexParam(jql, dimTypeNo);
					if (items != null && items.size() > 0) {
						for (RptDimItemInfo item : items) {
							dim.put(item.getId().getDimItemNo(),
									item.getDimItemNm());
						}
					}
					dimMap.put(dimTypeNo, dim);
				}
				String name = dim.get(value);
				if (name != null) {
					return name;
				} else {
					return value;
				}
			}

		}
		return value;
	}

	private Object setValue(String unit, Object value, int dataPrecision,
			boolean moneyFlag) {
		BigDecimal dec = null;
		try {
			if (unit != null) {
				if (unit.equals("01")) { // 元
					dec = new BigDecimal(String.valueOf(value));
				} else if (unit.equals("02")) { // 百
					dec = new BigDecimal(String.valueOf(value));
					dec = dec.divide(new BigDecimal(100));
				} else if (unit.equals("03")) { // 千
					dec = new BigDecimal(String.valueOf(value));
					dec = dec.divide(new BigDecimal(1000));
				} else if (unit.equals("04")) { // 万
					dec = new BigDecimal(String.valueOf(value));
					dec = dec.divide(new BigDecimal(10000));
				} else if (unit.equals("05")) { // 亿
					dec = new BigDecimal(String.valueOf(value));
					dec = dec.divide(new BigDecimal(100000000));
				}
			}
		} catch (Exception e) {
			return value;
		}
		if (dec != null) {
			value = dec.doubleValue();
			if (moneyFlag) {
				String formate = "###,###";
				if (dataPrecision > 0) {
					formate += ".";
					for (int i = 0; i < dataPrecision; i++) {
						formate += "0";
					}
				}
				DecimalFormat formater = new DecimalFormat(formate);
				value = formater.format(value);
				if(value.toString().startsWith(".")){
					value = "0"+value.toString();
				}
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> createParams(List<Map<String, Object>> argArr,
			Map<String, String> tbNms) {
		Map<String, Object> res = new HashMap<String, Object>();
		String wheres = "";
		Map<String, Object> condition = new HashMap<String, Object>();
		if (argArr != null && argArr.size() > 0) {
			int i = 0;
			int j = 0;
			for (i = 0; i < argArr.size(); i++) {

				Map<String, Object> arrObjTmp = argArr.get(i);

				Object value = arrObjTmp.get("value");
				String colId = arrObjTmp.get("colId").toString();
				RptSysModuleCol col = this.getEntityById(RptSysModuleCol.class,
						colId);
				if (col != null) {
					String where = " ";
					String preWhere = " and " + tbNms.get(col.getSetId()) + "." + arrObjTmp.get("name") + " ";
//					String where = " and " + tbNms.get(col.getSetId()) + "."
//							+ arrObjTmp.get("name") + " ";
					if (col.getDbType().equals(
							GlobalConstants4plugin.LOGIC_DATA_TYPE_DATE)) {
						SimpleDateFormat ds = new SimpleDateFormat("yyyyMMdd");
						try {
							value = new java.sql.Date(ds
									.parse(value.toString()).getTime());
						} catch (ParseException e) {
						}
					}
					condition.put("param" + j, value);
					if (arrObjTmp.containsKey("begin")) {
						where += preWhere + " >= :param" + j;
					} else if (arrObjTmp.containsKey("end")) {
						where += preWhere + " <= :param" + j;
					} else if (arrObjTmp.containsKey("op")) {
						where += preWhere + " " + arrObjTmp.get("op") + " :param" + j;
						if (arrObjTmp.get("op").equals("like")) {
							condition.put("param" + j, "%" + value + "%");
						}
					} else {
						if (value instanceof String || value instanceof Date)
							where += preWhere + " = :param" + j;
						else {
							try {
								String values[] = (String[]) value;
								if (values.length > 1) {
									if(values.length > 1000) {//oracle 超一千特殊处理
										List<List<String>> strs = ReBuildParam.toDbList(Arrays.asList(values));
										where += " and ( ";
										for (int k = 0; k < strs.size(); k++) {
											condition.put("param" + j, strs.get(k));
											if(k == (strs.size()-1)) {
												where += tbNms.get(col.getSetId()) + "." + arrObjTmp.get("name") + " in :param" + j;
											}else {
												where += tbNms.get(col.getSetId()) + "." + arrObjTmp.get("name") + " in :param" + j + " or ";
												j++;
											}
										}
										where += " ) ";
									}else {
										where += preWhere + " in (:param" + j + ")";
									}
								} else {
									where += preWhere + " = :param" + j;
								}
							} catch (Exception e) {
								List<String> values = (List<String>) value;
								if (values.size() > 1) {
									if(values.size() > 1000) {//oracle 超一千特殊处理
										List<List<String>> strs = ReBuildParam.toDbList(values);
										where += " and ( ";
										for (int k = 0; k < strs.size(); k++) {
											condition.put("param" + j, strs.get(k));
											if(k == (strs.size()-1)) {
												where += tbNms.get(col.getSetId()) + "." + arrObjTmp.get("name") + " in :param" + j;
											}else {
												where += tbNms.get(col.getSetId()) + "." + arrObjTmp.get("name") + " in :param" + j + " or ";
												j++;
											}
										}
										where += " ) ";
									}else {
										where += preWhere + " in :param" + j;
									}
								} else {
									where += preWhere + " = :param" + j;
								}
							}

						}
					}
					wheres += where;
				}
				j++;
			}
			res.put("where", wheres);
			res.put("no", j);
			res.put("condition", condition);
			return res;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private String createFilter(Map<String, Object> filters,
			Map<String, String> tbNms, Map<String, Object> conditions,
			Map<String, RptSysModuleCol> cols, int paramNo) {
		String where = "( ";
		String op = filters.get("op").toString();
		List<Map<String, Object>> rules = (List<Map<String, Object>>) filters
				.get("rules");
		if (rules != null && rules.size() > 0) {
			int i = 0;
			for (Map<String, Object> rule : rules) {
				String colId = rule.get("field").toString();
				String rop = rule.get("op").toString();
				Object value = rule.get("value");

				RptSysModuleCol col = cols.get(colId);
				if (col != null) {
					String setId = col.getSetId();
					String tableNm = tbNms.get(setId);
					if (col.getDbType().equals(
							GlobalConstants4plugin.LOGIC_DATA_TYPE_DATE)) {
						SimpleDateFormat ds = new SimpleDateFormat("yyyyMMdd");
						try {
							value = new java.sql.Date(ds
									.parse(value.toString()).getTime());
						} catch (ParseException e) {
						}
					}
					if (i != 0)
						where += " " + op + " " + tableNm + "." + col.getEnNm()
								+ " " + rop + " " + ":param" + paramNo;
					else {
						where += " " + tableNm + "." + col.getEnNm() + " "
								+ rop + " " + ":param" + paramNo;
					}
					if (rop.equals("like")) {
						value = "%" + value + "%";
					}
					conditions.put("param" + paramNo, value);
					paramNo++;
				}
				i++;
			}
		}
		List<Map<String, Object>> groups = (List<Map<String, Object>>) filters
				.get("groups");
		if (groups != null && groups.size() > 0) {
			for (Map<String, Object> group : groups) {
				where += " " + op + " "
						+ createFilter(group, tbNms, conditions, cols, paramNo)
						+ " ";
			}
		}
		if (where.equals("( ")) {
			where = "";
		} else {
			where += " )";
		}
		return where;
	}

	@Transactional(readOnly = false)
	public void saveTmp(RptDetailTmpInfo tmp, List<RptDetailTmpConfig> configs,
			List<RptDetailTmpSort> sorts, List<RptDetailTmpSum> sums,List<RptDetailTmpSearch> searchs,
			RptDetailTmpFilter filter, RptDetailTmpSql sqlInfo) {
		this.saveOrUpdateEntity(tmp);
		if (filter != null) {
			this.saveOrUpdateEntity(filter);
		}
		if (sqlInfo != null) {
			this.saveOrUpdateEntity(sqlInfo);
		}
		this.excelBS.saveEntityJdbcBatch(configs);
		this.excelBS.saveEntityJdbcBatch(sorts);
		this.excelBS.saveEntityJdbcBatch(sums);
		this.excelBS.saveEntityJdbcBatch(searchs);
	}

	@Transactional(readOnly = false)
	public void updateTmp(String templateId, List<RptDetailTmpConfig> configs,
			List<RptDetailTmpSort> sorts, List<RptDetailTmpSum> sums,List<RptDetailTmpSearch> searchs,
			RptDetailTmpFilter filter, RptDetailTmpSql sqlInfo) {
		String jql = "delete from RptDetailTmpConfig where id.templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpFilter where templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpSearch where id.templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpSql where templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpSort where id.templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpSum where id.templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		if (filter != null) {
			this.saveOrUpdateEntity(filter);
		}
		if (sqlInfo != null) {
			this.saveOrUpdateEntity(sqlInfo);
		}
		this.excelBS.saveEntityJdbcBatch(configs);
		this.excelBS.saveEntityJdbcBatch(sorts);
		this.excelBS.saveEntityJdbcBatch(searchs);
		this.excelBS.saveEntityJdbcBatch(sums);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> list(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap,
			String catalogId) {
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		String jql = "select tmp from RptDetailTmpInfo tmp where 1=1 and tmp.defSrc = :defSrc ";
		values.put("defSrc", GlobalConstants4plugin.DETAIL_DEF_SRC_LIB);
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}
		if (StringUtils.isNotBlank(catalogId)) {
			jql += " and catalogId = :catalogId ";
			values.put("catalogId", catalogId);
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by tmp." + orderBy + " " + orderType;
		}
		SearchResult<RptDetailTmpInfo> tmpList = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql, values);
		res.put("Rows", tmpList.getResult());
		res.put("Total", tmpList.getTotalCount());
		return res;
	}

	@Transactional(readOnly = false)
	public void changeSts(String templateId, String templateSts) {
		RptDetailTmpInfo tmp = this.getEntityById(RptDetailTmpInfo.class,
				templateId);
		if (tmp != null) {
			tmp.setTemplateSts(templateSts);
			this.saveOrUpdateEntity(tmp);
		}
	}

	@Transactional(readOnly = false)
	public void deleteCatalog(String catalogId) {
		String jql = "delete from RptDetailTmpCatalog catalog where catalogId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, catalogId);
	}

	public RptDetailTmpInfoVO getTmpInfo(String templateId) {
		String jql = "select new com.yusys.bione.plugin.detailtmp.web.vo.RptDetailTmpInfoVO(tmp,catalog.catalogNm) from RptDetailTmpInfo tmp,RptDetailTmpCatalog catalog where tmp.templateId = ?0 and tmp.catalogId = catalog.catalogId";
		return this.baseDAO.findUniqueWithIndexParam(jql, templateId);
	}

	@Transactional(readOnly = false)
	public void deleteTmpInfo(String templateId) {
		String jql = "delete from RptDetailTmpInfo where templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpConfig where id.templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpFilter where templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpSearch where id.templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpSql where templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
		jql = "delete from RptDetailTmpSort where id.templateId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, templateId);
	}
	
	/**
	 * 
	 * @param prefix 前缀
	 * @param length 长度
	 * @return 报表编号
	 */
	public String getAutoRptNum(String prefix,int length){
		String jql = "select info from RptDetailTmpInfo info where info.templateId like ?0 order by info.templateId desc";
		List<RptDetailTmpInfo> rptinfos = this.baseDAO.findWithIndexParam(jql, prefix+"%");
		if(rptinfos !=null && rptinfos.size()>0){
			String templateId = rptinfos.get(0).getTemplateId();
			templateId = templateId.substring(prefix.length(), templateId.length());
			int num = NumberUtils.toInt(templateId)+1;
			String nums = String.valueOf(num);
			templateId = prefix;
			for(int i = 0;i < length-nums.length();i++){
				templateId +="0";
			}
			templateId += nums;
			return templateId;
		}
		else{
			String rptNum = prefix;
			for(int i = 0;i < length;i++){
				rptNum +="0";
			}
			return rptNum;
		}
	}
	/**
	 * 下载完附件，删除附件对应关系
	 */
	public void delAttachRel(String attachId) {
		if(StringUtils.isNotBlank(attachId)){
			String jql = "delete from BioneMsgAttachInfo att where att.attachId = ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql, attachId);
			jql = "delete from BioneLogAttachRel att where att.id.attachId = ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql, attachId);
		}
	}
	public Map<String, String> getSrcCode(String setId){
		Map<String, String> map = new HashMap<String, String>();
 		String srcCode = "01";
		String jql = " select t from RptSysModuleInfo t where t.setId = ?0";
		RptSysModuleInfo rptSysModuleInfo = this.baseDAO.findUniqueWithIndexParam(jql, setId);
		if(null != rptSysModuleInfo){
			srcCode = rptSysModuleInfo.getBusiType();
		}
		map.put("srcCode", srcCode);
		return map; 
	}

	public List<String> getSetId(String setIds){
		List<String> srcSetId = ArrayUtils.asList(setIds, ",");
		List<String> setId = new ArrayList<String>();
		setId.addAll(srcSetId);
		String jql = "select rel.tgtSetId from RptSysModuleRelInfo rel where rel.srcSetId in ?0";
		List<String> tgtSetId = this.baseDAO.findWithIndexParam(jql, srcSetId);
		setId.addAll(tgtSetId);
		return setId;
	}
	
	/*public List<CommonTreeNode> getDetailTree(String contextPath,
			String searchNm, boolean showCatalog, boolean isAuth ,boolean showSts,String defSrc) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(generateRootNode(contextPath));
		String jql = "select new com.yusys.bione.plugin.detailtmp.web.vo.RptDetailTmpInfoVO(rpt,tmp) from RptMgrReportInfo rpt,RptDetailTmpInfo tmp where 1=1 and rpt.cfgId = tmp.templateId and rpt.rptType = :rptType ";
		Map<String, Object> condtiton = new HashMap<String, Object>();
		condtiton.put("rptType", GlobalConstants4plugin.RPT_TYPE_DETAIL);
		if (StringUtils.isNotBlank(searchNm)) {
			jql += " and rpt.rptNm like :searchNm";
			condtiton.put("searchNm", "%" + searchNm + "%");
		}
		if(showSts){
			jql += " and rpt.rptSts = :rptSts";
			condtiton.put("rptSts", "Y");
		}
		if (isAuth && defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_LIB)) {
			if (BioneSecurityUtils.getCurrentUserInfo() != null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				List<String> rptIds = BioneSecurityUtils
						.getResIdListOfUser(GlobalConstants4plugin.RPT_VIEW);
				if (rptIds.size() <= 0) {
					return nodes;
				} else {
					jql += " and rpt.rptId in :rptId";
					condtiton.put("rptId", rptIds);
				}
			}
		}
		jql += " and rpt.defSrc = :defSrc";
		condtiton.put("defSrc", defSrc);
		if(defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_USER)){
			jql += " and rpt.defUser = :defUser";
			condtiton.put("defUser", BioneSecurityUtils.getCurrentUserId());
		}
		jql += " order by rpt.rankOrder ";
		List<RptDetailTmpInfoVO> rpts = this.baseDAO.findWithNameParm(jql,
				condtiton);
		List<String> catalogIds = new ArrayList<String>();
		if (rpts != null && rpts.size() > 0) {
			for (RptDetailTmpInfoVO rpt : rpts) {
				catalogIds.add(rpt.getCatalogId());
				nodes.add(generateTmpNode(contextPath, rpt));
			}
		}
		if(defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_LIB)){
			if (showCatalog && !StringUtils.isNotBlank(searchNm)) {
				List<RptMgrReportCatalog> catalogs = this.getAllEntityList(
						RptMgrReportCatalog.class, "catalogOrder", false);
				if (catalogs != null && catalogs.size() > 0) {
					for (RptMgrReportCatalog catalog : catalogs) {
						nodes.add(generateCatalogNode(contextPath, catalog));
					}
				}
			} else {
				getAllCatalogInfo(contextPath, catalogIds, nodes);
			}
		}
	
		return nodes;
	}*/
	
	public List<CommonTreeNode> getDetailTree(String contextPath,
			String searchNm, boolean showCatalog, boolean isAuth ,boolean showSts,String defSrc) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(generateRootNode(contextPath));
		Map<String, Object> condtiton = new HashMap<String, Object>();
		String catalogjql = "select catalog from RptDetailTmpCatalog catalog";
		List<RptDetailTmpCatalog> catalogs = this.baseDAO.findWithNameParm(catalogjql,condtiton);
		if(catalogs != null && catalogs.size() > 0){
			for(RptDetailTmpCatalog catalog : catalogs){
				nodes.add(generateRootNode(contextPath, catalog));
			}
		}
		
		String tempjql = "select temp from RptDetailTmpInfo temp where 1=1";
		if (StringUtils.isNotBlank(searchNm)) {
			tempjql += " and temp.templateNm like :searchNm";
			condtiton.put("searchNm", "%" + searchNm + "%");
		}
		
		if (isAuth && defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_LIB)) {
			if (BioneSecurityUtils.getCurrentUserInfo() != null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				List<String> templateIds = BioneSecurityUtils
						.getResIdListOfUser(GlobalConstants4plugin.DETAIL_VIEW);
				if (templateIds.size() <= 0) {
					return nodes;
				} else {
					tempjql += " and temp.templateId in :templateId";
					condtiton.put("templateId", templateIds);
				}
			}
		}
		/*tempjql += " and temp.defSrc = :defSrc";
		condtiton.put("defSrc", defSrc);
		if(defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_USER)){
			tempjql += " and rpt.defUser = :defUser";
			condtiton.put("defUser", BioneSecurityUtils.getCurrentUserId());
		}*/
		
		List<RptDetailTmpInfo> temps = this.baseDAO.findWithNameParm(tempjql,condtiton);
		if(temps != null && temps.size() > 0){
			for(RptDetailTmpInfo temp : temps){
				nodes.add(generateRootNode(contextPath, temp));
			}
		}
		return nodes;
	}
	
	private CommonTreeNode generateRootNode(String contextPath,
			RptDetailTmpInfo temp) {
		CommonTreeNode node = new CommonTreeNode();
		node.setId(temp.getTemplateId());
		node.setText(temp.getTemplateNm());
		node.setIcon(contextPath + treeFolderIcon);
		node.setUpId(temp.getCatalogId());
		node.setData(temp);
		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeType", this.tmpNode);
		params.put("resId", temp.getTemplateId());
		params.put("resType",GlobalConstants4plugin.RES_TYPE_CATA);
		params.put("resDefNo", GlobalConstants4plugin.DETAIL_RES_NO);
		node.setParams(params);
		return node;
	}

	private CommonTreeNode generateRootNode(String contextPath, RptDetailTmpCatalog catalog) {
		CommonTreeNode node = new CommonTreeNode();
		node.setId(catalog.getCatalogId());
		node.setText(catalog.getCatalogNm());
		node.setIcon(contextPath + treeFolderIcon);
		node.setUpId(catalog.getUpId());
		node.setData(catalog);
		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeType", this.catalogNode);
		params.put("resId", catalog.getCatalogId());
		params.put("resType",GlobalConstants4plugin.RES_TYPE_CATA);
		params.put("resDefNo", GlobalConstants4plugin.DETAIL_RES_NO);
		node.setParams(params);
		return node;
	}

	/*private CommonTreeNode generateCatalogNode(String contextPath,
			RptMgrReportCatalog catalog) {
		CommonTreeNode node = new CommonTreeNode();
		node.setId(catalog.getCatalogId());
		node.setText(catalog.getCatalogNm());
		node.setIcon(contextPath + treeFolderIcon);
		node.setUpId(catalog.getUpCatalogId());
		node.setData(catalog);
		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeType", this.catalogNode);
		params.put("resId", catalog.getCatalogId());
		params.put("resType",GlobalConstants4plugin.RES_TYPE_CATA);
		params.put("resDefNo", GlobalConstants4plugin.DETAIL_RES_NO);
		node.setParams(params);
		return node;
	}*/
	
	/**
	 * 获取排列顺序信息
	 * 
	 * @return
	 */
	public Map<String, String> getRankOrder(String catalogId,String defSrc) {
		Map<String, String> param = Maps.newHashMap(); 
		BigDecimal rankOrder = new BigDecimal(0);
		if(defSrc.equals(GlobalConstants4plugin.RPT_DEF_SRC_LIB)){
			String jql = " select max(t.rankOrder) from RptMgrReportInfo t where t.catalogId = ?0";
			rankOrder = this.baseDAO.findUniqueWithIndexParam(jql, catalogId);
		}
		if(defSrc.equals(GlobalConstants4plugin.RPT_DEF_SRC_USER)){
			String jql = " select max(t.rankOrder) from RptMgrReportInfo t where defSrc = ?0 and defUser = ?1";
			rankOrder = this.baseDAO.findUniqueWithIndexParam(jql, GlobalConstants4plugin.RPT_DEF_SRC_USER,BioneSecurityUtils.getCurrentUserId());
		}
		if(rankOrder != null){
			Integer data = rankOrder.intValue()+1;
			param.put("rankOrder", String.valueOf(data));
		}else{
			param.put("rankOrder", "");
		}
		return param;
	}
	
	@Transactional(readOnly = false)
	public void saveTmp(RptMgrReportInfo info,RptDetailTmpInfo tmp, List<RptDetailTmpConfig> configs,
			List<RptDetailTmpSort> sorts, List<RptDetailTmpSum> sums,List<RptDetailTmpSearch> searchs,
			RptDetailTmpFilter filter, RptDetailTmpSql sqlInfo) {
		this.saveOrUpdateEntity(info);
		this.saveOrUpdateEntity(tmp);
		if (filter != null) {
			this.saveOrUpdateEntity(filter);
		}
		if (sqlInfo != null) {
			this.saveOrUpdateEntity(sqlInfo);
		}
		this.excelBS.saveEntityJdbcBatch(configs);
		this.excelBS.saveEntityJdbcBatch(sorts);
		this.excelBS.saveEntityJdbcBatch(sums);
		this.excelBS.saveEntityJdbcBatch(searchs);
	}
	
	public List<CommonTreeNode> getDetailTree(String contextPath,
			String searchNm, boolean showCatalog, boolean isAuth,List<String> tmpId,String defSrc) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(generateRootNode(contextPath));
		String jql = "select tmp from RptDetailTmpInfo tmp where 1=1 ";
		Map<String, Object> condtiton = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(searchNm)) {
			jql += " and tmp.templateNm like :searchNm";
			condtiton.put("searchNm", "%" + searchNm + "%");
		}
		if (isAuth) {
			if (BioneSecurityUtils.getCurrentUserInfo() != null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				List<String> templateIds = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_DETAIL");
				if (templateIds.size() <= 0) {
					return nodes;
				} else {
					jql += " and tmp.templateId in :templateIds ";
					condtiton.put("templateIds", templateIds);
				}
			}
		}
		
		if(tmpId != null && tmpId.size() >0 ){
			jql += " and tmp.templateId in :tmpIds ";
			condtiton.put("tmpIds", tmpId);
		}
		else{
			return nodes;
		}
		jql += " and tmp.defSrc = : defSrc";
		condtiton.put("defSrc", defSrc);
		if(defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_USER)){
			jql += " and tmp.srcId = :srcId";
			condtiton.put("srcId", BioneSecurityUtils.getCurrentUserId());
		}
		List<RptDetailTmpInfo> tmps = this.baseDAO.findWithNameParm(jql,
				condtiton);
		List<String> catalogIds = new ArrayList<String>();
		if (tmps != null && tmps.size() > 0) {
			for (RptDetailTmpInfo tmp : tmps) {
				catalogIds.add(tmp.getCatalogId());
				nodes.add(generateTmpNode(contextPath, tmp));
			}
		}
		if(defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_LIB)){
			if (showCatalog || !StringUtils.isNotBlank(searchNm)) {
				List<RptDetailTmpCatalog> catalogs = this.getAllEntityList(
						RptDetailTmpCatalog.class, "catalogNm", false);
				if (catalogs != null && catalogs.size() > 0) {
					for (RptDetailTmpCatalog catalog : catalogs) {
						nodes.add(generateCatalogNode(contextPath, catalog));
					}
				}
			} else {
				getAllCatalogInfo(contextPath, catalogIds, nodes);
			}
		}
		return nodes;
	}
	
	private CommonTreeNode generateTmpNode(String contextPath,
			RptDetailTmpInfo tmp) {
		CommonTreeNode node = new CommonTreeNode();
		node.setId(tmp.getTemplateId());
		node.setText(tmp.getTemplateNm());
		node.setIcon(contextPath + treeTmpIcon);
		node.setUpId(tmp.getCatalogId());
		node.setData(tmp);
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", tmp.getTemplateId());
		params.put("nodeType", this.tmpNode);
		params.put(
				"resDefNo",
				com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DETAIL_RES_NO);
		node.setParams(params);
		return node;
	}
	public List<CommonTreeNode> getDetailTree(String contextPath,
			String searchNm, boolean showCatalog, boolean isAuth ,String defSrc,String isCabin) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(generateRootNode(contextPath));
		String jql = "select tmp from RptDetailTmpInfo tmp where 1=1 ";
		Map<String, Object> condtiton = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(searchNm)) {
			jql += " and tmp.templateNm like :searchNm";
			condtiton.put("searchNm", "%" + searchNm + "%");
		}
		if (StringUtils.isNotBlank(isCabin)) {
			jql += " and tmp.isCabin l= :isCabin";
			condtiton.put("isCabin", isCabin);
		}
		if (isAuth && defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_LIB)) {
			if (BioneSecurityUtils.getCurrentUserInfo() != null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				List<String> templateIds = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_DETAIL");
				if (templateIds.size() <= 0) {
					return nodes;
				} else {
					jql += "and tmp.templateId in :templateIds";
					condtiton.put("templateIds", templateIds);
				}
			}
		}
		jql += " and tmp.defSrc = :defSrc";
		condtiton.put("defSrc", defSrc);
		if(defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_USER)){
			jql += " and tmp.srcId = :srcId";
			condtiton.put("srcId", BioneSecurityUtils.getCurrentUserId());
		}
		List<RptDetailTmpInfo> tmps = this.baseDAO.findWithNameParm(jql,
				condtiton);
		List<String> catalogIds = new ArrayList<String>();
		if (tmps != null && tmps.size() > 0) {
			for (RptDetailTmpInfo tmp : tmps) {
				catalogIds.add(tmp.getCatalogId());
				nodes.add(generateTmpNode(contextPath, tmp));
			}
		}
		if(defSrc.equals(GlobalConstants4plugin.DETAIL_DEF_SRC_LIB)){
			if (showCatalog || !StringUtils.isNotBlank(searchNm)) {
				List<RptDetailTmpCatalog> catalogs = this.getAllEntityList(
						RptDetailTmpCatalog.class, "catalogNm", false);
				if (catalogs != null && catalogs.size() > 0) {
					for (RptDetailTmpCatalog catalog : catalogs) {
						nodes.add(generateCatalogNode(contextPath, catalog));
					}
				}
			} else {
				getAllCatalogInfo(contextPath, catalogIds, nodes);
			}
		}
	
		return nodes;
	}
}
