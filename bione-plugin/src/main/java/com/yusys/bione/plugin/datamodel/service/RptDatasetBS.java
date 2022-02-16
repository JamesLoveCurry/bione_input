package com.yusys.bione.plugin.datamodel.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.bigdata.utils.HiveJdbcUtil;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.repository.mybatis.RptDataSetDao;
import com.yusys.bione.plugin.datamodel.vo.RptDatasetVO;
import com.yusys.bione.plugin.datamodel.vo.RptSqlFilterVO;
import com.yusys.bione.plugin.datamodel.vo.RptTableVO;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.LOGIC_DATA_TYPE_DATE;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.LOGIC_DATA_TYPE_NUMBER;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.LOGIC_DATA_TYPE_OBJECT;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.LOGIC_DATA_TYPE_TEXT;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.*;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.*;

/**
 * 
 * <pre>
 * Title:数据集业务类
 * Description: 提供对数据集及数据项的自定义配置
 * </pre>
 * 
 * @author fanll fanll@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptDatasetBS extends BaseBS<Object> {

	@Autowired
	private RptDataSetDao rptDataSetDao;
	// 数据源类型
	@SuppressWarnings("unused")
	private static final String LOGIC_SOURCE_TYPE_SQL = "02";// 标准SQL

    private static final String COL_TYPE_COMMON = "00";// 字段类型00为一般字段即数据库字段，01为度量字段
                                                        // ，02为维度字段

	// 平台逻辑数据项类型(text,number,date,object)
	private static final String[] textTypes = { "char", "varchar", "varchar2",
			"nchar", "nvarchar2", "long varchar", "graphics", "vargraphics",
			"string", "long vargraphic" };
	private final String[] numberTypes = { "int", "long", "smallint", "double",
			"float", "numeric", "decimal", "rowid", "nrowid", "number",
			"integer", "real" };
	private final String[] dateTypes = { "timestamp", "date", "time" };
	private final String[] objectTypes = { "text", "blob", "clob", "nclob",
			"bfile", "raw", "long raw" };

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 通过过滤条件查询数据集的数据
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @param firstResult
	 *            分页开始
	 * @param pageSize
	 *            每页条数
	 * @param condition
	 *            过滤条件（name:value）
	 * @param appendFiltorSql
	 *            自定义过滤条件
	 * @return 查询的分页数据(结果map中结构：{Rows:分页数据,Total:数据总量,errorMsg:错误信息})
	 */
	public Map<String, Object> getDatasetData(String datasetId,
			int firstResult, int pageSize, Map<String, Object> condition,
			String appendFiltorSql) {
		return this.datasetPreview(datasetId, firstResult, pageSize, condition,
				false, appendFiltorSql);
	}

	/*--------------------      功能方法        ----------------------*/

	/**
	 * 数据集目录树
	 * 
	 * @param upId
	 *            上级Id
	 * @param basePath
	 *            工程路径
	 * @return
	 */
	public List<CommonTreeNode> getDatasetCatalogTree(String upId,
			String basePath) {
		List<CommonTreeNode> nodes = Lists.newArrayList();
		if (!notEmpty(upId)) {
			// 创建根节点
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId("ROOT");
			treeNode.setText("数据集");
			treeNode.setIcon(basePath + DATA_TREE_NODE_ICON_ROOT);
			Map<String, String> params = new HashMap<String, String>();
			params.put("realId", "0");
			params.put("isParent", "true");
			treeNode.setParams(params);
			nodes.add(treeNode);
		} else {
			List<RptSysModuleCatalog> catalogs = this.rptDataSetDao
					.getCatalogListByUpId(upId);
			if (notEmpty(catalogs)) {
				for (int i = 0; i < catalogs.size(); i++) {
					RptSysModuleCatalog cl = catalogs.get(i);
					CommonTreeNode treeNode = new CommonTreeNode();
					treeNode.setId(cl.getCatalogId());
					treeNode.setText(cl.getCatalogNm());
					treeNode.setUpId(cl.getUpId());
					treeNode.setIcon(basePath + DATA_TREE_NODE_ICON_CATALOG);
					Map<String, String> params = new HashMap<String, String>();
					params.put("realId", cl.getCatalogId());
					params.put("isParent", "true");
					treeNode.setParams(params);
					nodes.add(treeNode);
				}
			}
		}
		return nodes;
	}

	/**
	 * 判断同路径下是否已存在同名目录
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param upId
	 *            上级目录Id
	 * @param catalogName
	 *            目录名称
	 * @return
	 */
	public boolean catalogNameCanUse(String catalogId, String upId,
			String catalogNm) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("upId", upId);
		params.put("catalogNm", catalogNm);
		if (notEmpty(catalogId)) {
			params.put("catalogId", catalogId);
		}
		List<RptSysModuleCatalog> list = this.rptDataSetDao
				.catalogNameCanUse(params);
		return (notEmpty(list)) ? false : true;
	}

	/**
	 * 判断同路径下是否已存在同名数据集合
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param upId
	 *            上级目录Id
	 * @param catalogName
	 *            目录名称
	 * @return
	 */
	public boolean datasetNameCanUse(String catalogId, String setNm,
			String setId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("setNm", setNm);
		params.put("catalogId", catalogId);
		if (notEmpty(setId)) {
			params.put("setId", setId);
		}
		List<RptSysModuleInfo> list = this.rptDataSetDao
				.datasetNameCanUse(params);
		return (notEmpty(list)) ? false : true;
	}

	/**
	 * 删除目录
	 * 
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean deleteCatalog(String catalogId) {
		if (!notEmpty(catalogId))
			return false;

		List<String> sonIds = new ArrayList<String>();
		sonIds.add(catalogId);
		// 获取所有子目录
		this.getSonsByType(catalogId, sonIds);

		// 删除前检查该目录下是否有配置数据集
		List<RptSysModuleInfo> list = this.rptDataSetDao
				.getModuleInfoListByCatalogIds(sonIds);
		if (notEmpty(list))
			return false;

		this.rptDataSetDao.batchDelCatalog(sonIds);
		return true;
	}

	/**
	 * 递归查找目录下的所有子目录
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param sonIds
	 *            子目录Id集合
	 */
	private void getSonsByType(String catalogId, List<String> sonIds) {
		List<RptSysModuleCatalog> children = this.rptDataSetDao
				.getCatalogListByUpId(catalogId);
		if (notEmpty(children)) {
			for (RptSysModuleCatalog cl : children) {
				sonIds.add(cl.getCatalogId());
				this.getSonsByType(cl.getCatalogId(), sonIds);
			}
		}
	}

	/**
	 * 获取当前逻辑系统下的所有数据源
	 */
	public List<CommonComboBoxNode> getDataSources() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		List<BioneDsInfo> list = this.rptDataSetDao
				.getDataSources(BioneSecurityUtils.getCurrentUserInfo()
						.getCurrentLogicSysNo());
		if (notEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(list.get(i).getDsId());
				node.setText(list.get(i).getDsName());
				nodes.add(node);
			}
		}
		return nodes;
	}

	public List<CommonComboBoxNode> getSetTypeList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = new CommonComboBoxNode();
		node.setId(SET_TYPE_DETAIL);
		node.setText("明细模型");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(SET_TYPE_MUTI_DIM);
		node.setText("多维模型");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(SET_TYPE_GENERIC);
		node.setText("泛化模型");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(SET_TYPE_SUM);
		node.setText("总账模型");
		nodes.add(node);
		return nodes;
	}

	/**
	 * 获取所有逻辑数据集
	 * 
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDatasets(int firstResult,
			int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap,String catalogId,String setType) {
		if("".equals(catalogId)){
			catalogId = "0";
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		String jql = "select sys from RptSysModuleInfo sys where 1=1 ";
		if(StringUtils.isNotBlank(catalogId)){
			List<String> catalogIdsList = Lists.newArrayList();
			this.catalogIdSearch(catalogId,catalogIdsList); 
			catalogIdsList.add(catalogId);
			values.put("catalogIds", catalogIdsList);
			jql += " and sys.catalogId in :catalogIds ";
		}
		if(StringUtils.isNotBlank(setType)){
			values.put("setType", setType);
			jql += " and sys.setType = :setType ";
		}
		
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by sys." + orderBy + " " + orderType;
		}
		SearchResult<RptSysModuleInfo> sr = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql, values);
		List<RptSysModuleInfo> datasets = sr.getResult();
		List<RptDatasetVO> vos = Lists.newArrayList();

		if (notEmpty(datasets)) {
			for (int i = 0; i < datasets.size(); i++) {
				RptSysModuleInfo dset = datasets.get(i);
				RptDatasetVO vo = new RptDatasetVO();
				try {
					com.yusys.bione.comp.utils.BeanUtils.copy(dset, vo);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				BioneDsInfo ds = this.rptDataSetDao.findDataSourceById(dset
						.getSourceId());
				if (ds != null) {
					vo.setDsName(ds.getDsName());
				}
				vos.add(vo);
			}
		}

		Map<String, Object> result = Maps.newHashMap();
		result.put("Rows", vos);
		result.put("Total", sr.getTotalCount());
		return result;
	}
	
	private List<String> catalogIdSearch(String catalogId,List<String> catalogIdsList){
		String sql = " select t.catalog_id from  RPT_SYS_MODULE_CATALOG t where t.up_id = ?0";
		List<Object[]> catalogIds = this.baseDAO.findByNativeSQLWithIndexParam(sql, catalogId);
		if(catalogIds.size()>0){
			for(Object cataId : catalogIds){
				catalogIdsList.add(cataId.toString());
			}
			for(Object cataId : catalogIds){
				catalogIdSearch(cataId.toString(),catalogIdsList);
			}
		}
		return  null;
	}

	/**
	 * 获取逻辑数据集下的所有逻辑数据项
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @return
	 */
	public Map<String, Object> getDatacolsOfDataset(String datasetId) {
		Map<String, Object> map = Maps.newHashMap();
		if (!notEmpty(datasetId))
			return map;
		List<RptSysModuleCol> cols = this.rptDataSetDao
				.getDatacolsOfDataset(datasetId);
		cols = pkToFirst(cols);
		for (RptSysModuleCol col : cols) {
			if (col.getColType() == null) {
				col.setColType(COL_TYPE_COMMON);
			}
		}
		map.put("Rows", cols);
		map.put("Total", cols.size());
		return map;
	}

	/**
	 * 查询数据源下的所有物理表
	 * 
	 * @param dsId
	 *            数据源ID
	 * @return
	 */
	public Map<String, Object> getTablesOfDs(String dsId, String tableName,
			String tableComment, int start, int pagesize) {
		Map<String, Object> map = Maps.newHashMap();
		if (!notEmpty(dsId))
			return map;
		BioneDsInfo ds = this.rptDataSetDao.findDataSourceById(dsId);
		boolean isOracle = (ORACLE_DATA_SOURCE.equals(ds.getDriverId()) || ORACLE_RAC_DATA_SOURCE.equals(ds.getDriverId()) || ORACLE_RAC_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isH2 = (H2_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isMySql = (MYSQL_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isODPS = (ODPS_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isHIVE = (HIVE_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isELK = (ELK_DATA_SOURCE.equals(ds.getDriverId()));
        boolean isInceptor = (INCEPTOR_DATA_SOURCE.equals(ds.getDriverId()));
        boolean isGbase = (GBASE_DATA_SOURCE.equals(ds.getDriverId()));
        boolean isGaussdb = (POSTGRESQL_DATA_SOURCE.equals(ds.getDriverId()));
		String sql = "";
		String appendSql = "";
		String countSql = "";
		if (isHIVE||isInceptor) {
			Connection conn = this.getConnOfDs(ds);
			if (conn != null) {
				sql = "show tables";
				try {
					int startRow = start;
					int endRow = startRow + pagesize;
					Statement stm;
					stm = conn.createStatement();
					ResultSet rs = stm.executeQuery(sql);
					List<RptTableVO> tables = Lists.newArrayList();
					while (rs.next()) {
                        RptTableVO table = new RptTableVO();
                        table.setTableName(rs.getString(1));// 表名
                        table.setTableComment("");// 表注释
                        /*
                         *    20170701 chenhx
                         *    增加筛选条件以及修复分页bug 
                         */
                        if((tableName != null && table.getTableName().indexOf(tableName) != -1)
                                || (tableComment != null && table.getTableComment().indexOf(tableComment) != -1)){
                            tables.add(table);
                        }else if(tableName == null && tableComment == null){
                            tables.add(table);
                        }
                    }
					map.put("Total", tables.size());
					if (startRow > tables.size()) {
						tables = Lists.newArrayList();
					} else if (endRow > tables.size()) {
						tables = tables.subList(startRow, tables.size());
					} else {
						tables = tables.subList(startRow, endRow);
					}
					map.put("Rows", tables);
				} catch (SQLException e) {
					e.printStackTrace();
					return map;
				} finally {
					// 释放连接
					JdbcUtils.closeConnection(conn);
				}
			}
		} else if (isODPS) {
			// ODPS实现，暂屏蔽掉
			// String userId = ds.getConnUser();
			// String passwd = ds.getConnPwd();
			// String url =StringUtils.split(ds.getConnUrl(),"$")[0];
			// String prj = StringUtils.split(ds.getConnUrl(),"$")[1];
			// Account account = new AliyunAccount( userId, passwd );
			// Odps odps = new Odps(account);
			// odps.setEndpoint(url);
			// odps.setDefaultProject(prj);
			// List<RptTableVO> tables = Lists.newArrayList();
			// Tables tb=odps.tables();
			// if(StringUtils.isNotBlank(tableName)||StringUtils.isNotBlank(tableComment)){
			// int i=0;
			// Iterator<Table> tbi=tb.iterator(prj);
			// while (tbi.hasNext()) {
			// Table t = tbi.next();
			// if(i>start+pagesize){
			// break;
			// }
			// RptTableVO table = new RptTableVO();
			// table.setTableName(t.getName());// 表名
			// table.setTableComment(t.getComment());// 表注释
			// boolean flag=false;
			// if(StringUtils.isNotBlank(tableName)){
			// if(table.getTableName().indexOf(tableName)>-1)
			// flag=true;
			// else
			// flag=false;
			// }
			// if(StringUtils.isNotBlank(tableComment)){
			// if(table.getTableComment().indexOf(tableComment)>-1)
			// flag=true;
			// else
			// flag=false;
			// }
			// if(flag){
			// if(i>start)
			// tables.add(table);
			// i++;
			// }
			// }
			// }
			// else{
			// Iterator<Table> tbi=tb.iterator(prj);
			// int i=0;
			// while (tbi.hasNext()) {
			// Table t = tbi.next();
			// if(i>start+pagesize){
			// break;
			// }
			// if(i>=start){
			// RptTableVO table = new RptTableVO();
			// table.setTableName(t.getName());// 表名
			// table.setTableComment(t.getComment());// 表注释
			// tables.add(table);
			// }
			// i++;
			// }
			// }
			//
			// map.put("Rows", tables);
			//
			//
		} else {
			Connection conn = this.getConnOfDs(ds);
			// 1.查询物理表
			if (conn != null) {
				int startRow = start + 1;
				int endRow = startRow + pagesize - 1;
				if (isOracle) {// ORACLE/DB2两种查表SQL语句
					if (notEmpty(tableName)) {
						appendSql += " AND UUT.TABLE_NAME LIKE '%"
								+ tableName.toUpperCase() + "%' ";
					}
					if (notEmpty(tableComment)) {
						appendSql += " AND UUT.COMMENTS LIKE '%" + tableComment
								+ "%' ";
					}
					sql = "SELECT TABLE_NAME,COMMENTS FROM (SELECT TABLE_NAME,COMMENTS,ROWNUM AS RN FROM (SELECT UUT.TABLE_NAME,UUT.COMMENTS FROM(SELECT U.TABLE_NAME,UT.COMMENTS FROM USER_TABLES U LEFT JOIN USER_TAB_COMMENTS UT ON U.TABLE_NAME = UT.TABLE_NAME UNION ALL SELECT U.VIEW_NAME,UT.COMMENTS FROM USER_VIEWS U LEFT JOIN USER_TAB_COMMENTS UT ON U.VIEW_NAME = UT.TABLE_NAME ) UUT where 1=1";
					sql += appendSql;
					sql += " ORDER BY UUT.TABLE_NAME ASC) WHERE ROWNUM<="
							+ endRow + ") WHERE RN>=" + startRow;
					countSql = "SELECT COUNT(TABLE_NAME) FROM (SELECT TABLE_NAME from USER_TABLES UNION ALL SELECT VIEW_NAME AS TABLE_NAME FROM USER_VIEWS) UUT WHERE 1=1 "
							+ appendSql;
				} else if (isH2) {// H2查表SQL语句
					if (notEmpty(tableName)) {
						appendSql += " AND TABLE_NAME LIKE '%"
								+ tableName.toUpperCase() + "%' ";
					}
					if (notEmpty(tableComment)) {
						appendSql += " AND REMARKS LIKE '%" + tableComment
								+ "%' ";
					}
					sql = "SELECT TABLE_NAME,REMARKS FROM (SELECT TABLE_NAME,REMARKS,ROWNUM AS RN FROM (SELECT TABLE_NAME ,REMARKS FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='TABLE' ";
					sql += appendSql;
					sql += " ORDER BY TABLE_NAME ASC) WHERE ROWNUM<=" + endRow
							+ ") WHERE RN>=" + startRow;
					countSql = "SELECT COUNT(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='TABLE' "
							+ appendSql;
				} else if(isELK){
                    if (notEmpty(tableName)) {
                        appendSql += " AND TABLE_NAME LIKE '%"
                                + tableName.toLowerCase() + "%' ";
                    }
                    if (notEmpty(tableComment)) {
                        appendSql += " AND COMMENTS LIKE '%" + tableComment + "%' ";
                    }
                    sql = "SELECT TABLE_NAME,COMMENTS FROM (SELECT TABLE_NAME,COMMENTS,row_number() over() AS RN FROM (SELECT relname AS TABLE_NAME , obj_description(oid) AS COMMENTS FROM pg_class WHERE relkind = 'r' ";
                    sql += appendSql;
                    sql += " ORDER BY TABLE_NAME ASC) "
                            + ") WHERE RN>=" + startRow +" AND RN<="+endRow;
                    countSql = "select count(TABLE_NAME) from (select relname as TABLE_NAME,obj_description(oid) as COMMENTS from pg_class where relkind = 'r') where 1=1 "
                            + appendSql;
                } else if (isMySql || isGbase) { //mysql和gbase语法
					if (notEmpty(tableName)) {
						appendSql += " AND TABLE_NAME LIKE '%"
								+ tableName.toUpperCase() + "%' ";
					}
					if (notEmpty(tableComment)) {
						appendSql += " AND TABLE_COMMENT LIKE '%"
								+ tableComment + "%' ";
					}
					sql = "SELECT TABLE_NAME,TABLE_COMMENT FROM information_schema.tables WHERE table_schema=database() and table_type='BASE TABLE' ";
					sql += appendSql;
					sql += "ORDER BY TABLE_NAME ASC limit " + (startRow - 1)
							+ "," + (endRow - startRow + 1);
					countSql = "SELECT COUNT(TABLE_NAME) FROM information_schema.tables WHERE table_schema=database() and table_type='BASE TABLE'  "
							+ appendSql;
				} else if (isGaussdb) { //高斯语法
					if (notEmpty(tableName)) {
						appendSql += " and tablename LIKE '%"
								+ tableName.toLowerCase() + "%' ";
					}
					if (notEmpty(tableComment)) {
						appendSql += " and obj_description(relfilenode, 'pg_class') LIKE '%"
								+ tableComment + "%' ";
					}
					sql = "select tablename as TABLE_NAME, obj_description(relfilenode, 'pg_class') as COLUMN_COMMENT from pg_tables a, pg_class b where a.tablename = b.relname and a.schemaname = current_schema()";
					sql += appendSql;
					sql += "ORDER BY a.TABLENAME ASC limit " + (endRow - startRow + 1) + " OFFSET " + (startRow - 1);
					countSql = "SELECT COUNT(*) from pg_tables a, pg_class b where a.tablename = b.relname and a.schemaname = current_schema()"
							+ appendSql;
				} else {
					if (notEmpty(tableName)) {
						appendSql += " AND TABNAME LIKE '%"
								+ tableName.toUpperCase() + "%' ";
					}
					if (notEmpty(tableComment)) {
						appendSql += " AND REMARKS LIKE '%" + tableComment
								+ "%' ";
					}
					sql = "SELECT TABNAME,REMARKS FROM (SELECT TABNAME,REMARKS,ROWNUMBER() OVER() AS rowid FROM (SELECT TABNAME,REMARKS FROM SYSCAT.TABLES WHERE (TYPE='T' OR TYPE='V') AND TABSCHEMA=CURRENT SCHEMA ";
					sql += appendSql;
					sql += "ORDER BY TABNAME ASC)) WHERE rowid>=" + startRow
							+ " AND rowid <=" + endRow;
					countSql = "SELECT COUNT(TABNAME) FROM SYSCAT.TABLES WHERE (TYPE='T' OR TYPE='V') AND TABSCHEMA=CURRENT SCHEMA "
							+ appendSql;
				}
				Statement stm;
				try {
					stm = conn.createStatement();
					ResultSet rs = stm.executeQuery(sql);
					List<RptTableVO> tables = Lists.newArrayList();
					while (rs.next()) {
						RptTableVO table = new RptTableVO();
						table.setTableName(rs.getString(1));// 表名
						table.setTableComment(rs.getString(2));// 表注释
						tables.add(table);
					}
					map.put("Rows", tables);
				} catch (SQLException e) {
					e.printStackTrace();
					return map;
				} finally {
					// 释放连接
					JdbcUtils.closeConnection(conn);
				}
			}
		}

		// 2.查询物理表总数
		if (isHIVE||isInceptor) {

		} else if (isODPS) {

		} else {
			Connection conn = this.getConnOfDs(ds);
			if (conn != null) {
				Statement stm;
				try {
					stm = conn.createStatement();
					ResultSet rs = stm.executeQuery(countSql);
					if (rs.next()) {
						map.put("Total", rs.getString(1));
					} else {
						map.put("Total", 0);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return map;
				} finally {
					// 释放连接
					JdbcUtils.closeConnection(conn);
				}
			}
		}
		return map;
	}

	public String sameTableEnNameCheck(Map<String, Object> map) {
		Integer count = this.rptDataSetDao.sameTableEnNameCheck(map);
		if (count > 0) {// 表示存在
			return "0";
		}
		return "1";
	}

	/**
	 * 查询物理表中的所有物理字段
	 * 
	 * @param dsId
	 *            数据源ID
	 * @param tableName
	 *            表名
	 * @return
	 */
	public Map<String, Object> getFieldsOfTable(String dsId, String tableName) {
		Map<String, Object> map = Maps.newHashMap();
		if (!notEmpty(dsId))
			return map;
		BioneDsInfo ds = this.rptDataSetDao.findDataSourceById(dsId);
		boolean isOracle = (ORACLE_DATA_SOURCE.equals(ds.getDriverId()) || ORACLE_RAC_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isH2 = (H2_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isMySql = (MYSQL_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isODPS = (ODPS_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isHIVE = (HIVE_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isELK = (ELK_DATA_SOURCE.equals(ds.getDriverId()));
        boolean isInceptor = (INCEPTOR_DATA_SOURCE.equals(ds.getDriverId()));
        boolean isGbase = (GBASE_DATA_SOURCE.equals(ds.getDriverId()));
        boolean isGaussdb = (POSTGRESQL_DATA_SOURCE.equals(ds.getDriverId()));
		String dbType = "other";
		if (isOracle) {
			dbType = "oracle";
		} else if (isH2) {
			dbType = "h2";
		} else if (isMySql||isInceptor) {
			dbType = "mysql";
		} else if (isODPS) {
			dbType = "odps";
		} else if (isHIVE) {
			dbType = "hive";
		} else if (isGbase){
			dbType = "gbase";
		} else if (isGaussdb){
			dbType = "postgresql";
		}
		if (isHIVE||isInceptor) {

			Connection conn = this.getConnOfDs(ds);
			if (conn != null) {
				String sql = "desc " + tableName;
				try {
					Statement stm;
					stm = conn.createStatement();
					ResultSet rs = stm.executeQuery(sql);
					List<RptSysModuleCol> cols = Lists.newArrayList();
					while (rs.next()) {
						RptSysModuleCol col = new RptSysModuleCol();
						col.setEnNm(rs.getString("col_name"));// 列名
						col.setCnNm("");// 列注释
						col.setDbType(getLogicDataType(rs
								.getString("data_type")));
						col.setLen(new BigDecimal(0));
						try {
							col.setPrecision2(new BigDecimal(0));// 精度
						} catch (Exception e) {
							col.setPrecision2(new BigDecimal(0));// 精度
						}
						col.setIsNull("Y");
						col.setIsPk(COMMON_BOOLEAN_NO);
						col.setIsUse(COMMON_BOOLEAN_YES);
						cols.add(col);
					}
					cols = this.getPK(tableName, cols, ds, dbType);
					cols = pkToFirst(cols);
					for (RptSysModuleCol col : cols) {
						if (col.getColType() == null) {
							col.setColType(COL_TYPE_COMMON);
						}
					}
					map.put("Rows", cols);
					map.put("Total", cols.size());
					return map;
				} catch (SQLException e) {
					e.printStackTrace();
					return map;
				} finally {
					// 释放连接
					JdbcUtils.closeConnection(conn);
				}
			}
		} else if (isODPS) {
			// ODPS实现，暂屏蔽掉
			// String userId = ds.getConnUser();
			// String passwd = ds.getConnPwd();
			// String url =StringUtils.split(ds.getConnUrl(),"$")[0];
			// String prj = StringUtils.split(ds.getConnUrl(),"$")[1];
			// Account account = new AliyunAccount( userId, passwd );
			// Odps odps = new Odps(account);
			// odps.setEndpoint(url);
			// odps.setDefaultProject(prj);
			// Tables tb=odps.tables();
			// Table t=tb.get(tableName);
			// List<RptSysModuleCol> cols = Lists.newArrayList();
			// List<Column> colnms=t.getSchema().getColumns();
			// if(colnms!=null&&colnms.size()>0){
			// for(Column colnm:colnms){
			// RptSysModuleCol col = new RptSysModuleCol();
			// col.setEnNm(colnm.getName());// 列名
			// col.setCnNm(colnm.getComment());// 列注释
			// col.setDbType(getOdpsDataType(colnm.getType()));
			// try{
			// col.setLen(new BigDecimal(colnm.getLabel()));
			// }
			// catch(Exception e){
			// col.setLen(new BigDecimal(0));
			// }
			// col.setPrecision(new BigDecimal(0));// 精度
			// col.setIsNull(COMMON_BOOLEAN_YES);
			// col.setIsPk(COMMON_BOOLEAN_NO);
			// col.setIsUse(COMMON_BOOLEAN_YES);
			// cols.add(col);
			// }
			// }
			// for(RptSysModuleCol col:cols ){
			// if(col.getColType()==null){
			// col.setColType(COL_TYPE_COMMON);
			// }
			// }
			// map.put("Rows", cols);
			// map.put("Total", cols.size());
			// return map;
		} else {
			Connection conn = this.getConnOfDs(ds);
			Statement stm = null;
			if (conn != null) {
				String sql = "";
				if (isOracle) {// ORACLE/DB2两种查列SQL语句
					sql = "SELECT col.COLUMN_NAME,cmt.COMMENTS,col.DATA_TYPE,NVL(col.DATA_PRECISION,col.DATA_LENGTH),NVL(col.DATA_SCALE,'0'),col.NULLABLE FROM USER_TAB_COLUMNS col,USER_COL_COMMENTS cmt WHERE col.COLUMN_NAME=cmt.COLUMN_NAME AND col.TABLE_NAME='"
							+ tableName
							+ "' AND cmt.TABLE_NAME='"
							+ tableName
							+ "'";
				} else if (isH2) {
					sql = "SELECT COLUMN_NAME,REMARKS,TYPE_NAME,NUMERIC_PRECISION,NUMERIC_SCALE,IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='"
							+ tableName + "'";
				} else if (isMySql || isGbase) { //mysql和gbase语法
					sql = "SELECT  COLUMN_NAME, COLUMN_COMMENT,DATA_TYPE,CASE WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION "
							+ "ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH,NUMERIC_SCALE,IS_NULLABLE FROM information_schema.columns WHERE table_schema=database() AND table_name='"
							+ tableName + "'";
				} else if (isELK) {
                    sql = " SELECT "
                        +" s.column_name                                                      AS COLUMN_NAME, "
                        +" COALESCE(col_description(c.oid,s.ordinal_position) ,s.column_name) AS COMMENTS , "
                        +" s.data_type , "
                        +" CASE "
                        +" WHEN a.atttypid='1700' "
                        +" THEN information_schema._pg_numeric_precision(a.atttypid,a.atttypmod) "
                        +" ELSE t.data_length "
                        +" END AS PRECISION, "
                        +" CASE "
                        +" WHEN a.atttypid='1700' "
                        +" THEN information_schema._pg_numeric_scale(a.atttypid,a.atttypmod) "
                        +" ELSE NULL "
                        +" END AS scale, "
                        +" s.is_nullable "
                        +" FROM "
                        +" information_schema.columns s, "
                        +" pg_class c, "
                        +" pg_attribute a, "
                        +" all_tab_columns t "
                        +" WHERE "
                        +" s.table_name = '"
                        +tableName
                        +"' AND a.attrelid=c.oid "
                        +" AND s.table_name = c.relname "
                        +" AND s.table_name = t.table_name "
                        +" AND s.column_name = t.column_name "
                        +" AND s.column_name=a.attname "
                        +" AND s.table_schema = current_schema() ";
			    } else if (isGaussdb) {
//					sql = "SELECT  COLUMN_NAME, '' as COLUMN_COMMENT,DATA_TYPE,CASE WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION "
//							+ "ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH,NUMERIC_SCALE,IS_NULLABLE FROM information_schema.columns WHERE table_schema=current_schema() AND table_name='"
//							+ tableName + "'";

					sql = " SELECT "
							+ " s.column_name                                                      AS COLUMN_NAME, "
							+ " COALESCE(col_description(c.oid,s.ordinal_position) ,s.column_name) AS COMMENTS , "
							+ " s.data_type , " + " CASE " + " WHEN a.atttypid='1700' "
							+ " THEN information_schema._pg_numeric_precision(a.atttypid,a.atttypmod) "
							+ " ELSE t.character_maximum_length " + " END AS PRECISION, " + " CASE "
							+ " WHEN a.atttypid='1700' "
							+ " THEN information_schema._pg_numeric_scale(a.atttypid,a.atttypmod) " + " ELSE NULL "
							+ " END AS scale, " + " s.is_nullable " + " FROM " + " information_schema.columns s, "
							+ " pg_class c, " + " pg_attribute a, " + " information_schema.columns t " + " WHERE "
							+ " s.table_name = '" + tableName + "' AND a.attrelid=c.oid "
							+ " AND s.table_name = c.relname " + " AND s.table_name = t.table_name "
							+ " AND s.column_name = t.column_name " + " AND s.column_name=a.attname "
							+ " AND s.table_schema = current_schema() ";
				} else {
					sql = "SELECT COLNAME,REMARKS,TYPENAME,LENGTH,SCALE,NULLS FROM SYSCAT.COLUMNS WHERE TABNAME='"
							+ tableName + "' AND TABSCHEMA=CURRENT SCHEMA";
				}
				try {
					stm = conn.createStatement();
					ResultSet rs = stm.executeQuery(sql);
					List<RptSysModuleCol> cols = Lists.newArrayList();
					while (rs.next()) {
						RptSysModuleCol col = new RptSysModuleCol();
						col.setEnNm(rs.getString(1));// 列名
						col.setCnNm(rs.getString(2));// 列注释
						col.setDbType(getLogicDataType(rs.getString(3)));
						col.setLen(new BigDecimal(rs.getString(4)));
						try {
							col.setPrecision2(new BigDecimal(rs.getString(5)));// 精度
						} catch (Exception e) {
							col.setPrecision2(new BigDecimal(0));// 精度
						}
						col.setIsNull(("Y"
								.equals(rs.getString(6).toUpperCase()))
								|| ("YES".equals(rs.getString(6).toUpperCase())) ? COMMON_BOOLEAN_YES
								: COMMON_BOOLEAN_NO);
						col.setIsPk(COMMON_BOOLEAN_NO);
						col.setIsUse(COMMON_BOOLEAN_YES);
						cols.add(col);
					}
					cols = this.getPK(tableName, cols, ds, dbType);
					cols = pkToFirst(cols);
					for (RptSysModuleCol col : cols) {
						if (col.getColType() == null) {
							col.setColType(COL_TYPE_COMMON);
						}
					}
					map.put("Rows", cols);
					map.put("Total", cols.size());
					return map;
				} catch (SQLException e) {
					e.printStackTrace();
					return map;
				} finally {
					// 释放连接
					JdbcUtils.closeConnection(conn);
				}
			}
		}

		return map;
	}

	/**
	 * 查询物理表的所有列
	 * 
	 * @param dsId
	 * @param tableList
	 *            物理表集合
	 * @return 方娟
	 */
	public Map<String, List<RptSysModuleCol>> getFieldsOfTableList(String dsId,
			List<String> tableList) {
		if (!notEmpty(dsId))
			return null;
		BioneDsInfo ds = this.rptDataSetDao.findDataSourceById(dsId);
		boolean isOracle = (ORACLE_DATA_SOURCE.equals(ds.getDriverId()) || ORACLE_RAC_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isH2 = (H2_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isMySql = (MYSQL_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isODPS = (ODPS_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isHIVE = (HIVE_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isELK = (ELK_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isGbase = (GBASE_DATA_SOURCE.equals(ds.getDriverId()));
		boolean isGaussdb = (POSTGRESQL_DATA_SOURCE.equals(ds.getDriverId()));
		if (isHIVE) {
			Connection conn = this.getConnOfDs(ds);
			if (conn != null) {
				try {
					Statement stm = conn.createStatement();
					Map<String, List<RptSysModuleCol>> map = new HashMap<String, List<RptSysModuleCol>>();
					for (String tableName : tableList) {
						String sql = "desc " + tableName;
						ResultSet rs = stm.executeQuery(sql);
						List<RptSysModuleCol> list = new ArrayList<RptSysModuleCol>();;
						while (rs.next()) {
							RptSysModuleCol col = new RptSysModuleCol();
							col.setEnNm(rs.getString("col_name"));// 列名
							col.setCnNm("");// 列注释
							col.setDbType(getLogicDataType(rs.getString("data_type")));
							col.setLen(new BigDecimal(0));
							col.setPrecision2(new BigDecimal(0));// 精度
							col.setIsNull("Y");
							col.setIsPk(COMMON_BOOLEAN_NO);
							col.setIsUse(COMMON_BOOLEAN_YES);
							list.add(col);
						}
						map.put(tableName, list);
					}
					return map;
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				} finally {
					// 释放连接
					JdbcUtils.closeConnection(conn);
				}
			}
		} else if (isODPS) {
			// ODPS实现，暂屏蔽掉
			// String userId = ds.getConnUser();
			// String passwd = ds.getConnPwd();
			// String url =StringUtils.split(ds.getConnUrl(),"$")[0];
			// String prj = StringUtils.split(ds.getConnUrl(),"$")[1];
			// Account account = new AliyunAccount( userId, passwd );
			// Odps odps = new Odps(account);
			// odps.setEndpoint(url);
			// odps.setDefaultProject(prj);
			// Tables tb=odps.tables();
			// Map<String, List<RptSysModuleCol>> map = new HashMap<String,
			// List<RptSysModuleCol>>();
			// if(tableList != null && tableList.size() > 0){
			// for(int i=0; i<tableList.size(); i++){
			// Table t=tb.get(tableList.get(i));
			// List<Column> colnms=t.getSchema().getColumns();
			// if(colnms!=null&&colnms.size()>0){
			// for(Column colnm:colnms){
			// List<RptSysModuleCol> list ;
			// if(map.get(tableList.get(i)) == null){
			// list = new ArrayList<RptSysModuleCol>();
			// }else{
			// list = (List<RptSysModuleCol>)map.get(tableList.get(i));
			// }
			// RptSysModuleCol col = new RptSysModuleCol();
			// col.setEnNm(colnm.getName());// 列名
			// col.setCnNm(colnm.getComment());// 列注释
			// col.setDbType(getOdpsDataType(colnm.getType()));
			// try{
			// col.setLen(new BigDecimal(colnm.getLabel()));
			// }
			// catch(Exception e){
			// col.setLen(new BigDecimal(0));
			// }
			// col.setPrecision(new BigDecimal(0));// 精度
			// col.setIsNull(COMMON_BOOLEAN_YES);
			// col.setIsPk(COMMON_BOOLEAN_NO);
			// col.setIsUse(COMMON_BOOLEAN_YES);
			// list.add(col);
			// map.put(tableList.get(i), list);
			// }
			// }
			// }
			// return map;
			// }else{
			// return new HashMap<String, List<RptSysModuleCol>>();
			// }
		} else {
			Connection conn = this.getConnOfDs(ds);
			Statement stm = null;
			String tableNames = "";
			if (tableList != null && tableList.size() > 0) {
				for (int i = 0; i < tableList.size(); i++) {
					tableNames += "'" + tableList.get(i) + "'"
							+ (i == tableList.size() - 1 ? "" : ",");
				}
			} else {
				return new HashMap<String, List<RptSysModuleCol>>();
			}
			if (conn != null) {
				String sql = "";
				if (isOracle) {// ORACLE/DB2两种查列SQL语句
					sql = " SELECT t.column_name,t.comments,t.data_type,t.length,t.scale,t.nullable,t.table_name,t1.isPk "
							+ "FROM "
							+ "( SELECT col.column_name, cmt.comments, col.data_type, NVL(col.Data_precision,col.data_length) as length, NVL(col.data_scale,'0') as scale, col.nullable, col.table_name FROM user_tab_columns col, user_col_comments cmt WHERE col.column_name = cmt.column_name and col.table_name = cmt.table_name order by col.table_name,col.column_name)t "
							+ "LEFT JOIN ( SELECT col.table_name, col.column_name, 'Y' AS isPk FROM user_constraints c, user_cons_columns col WHERE c.constraint_name = col.constraint_name AND c.constraint_type = 'P') t1 "
							+ "ON ( t.table_name = t1.table_name AND t.column_name = t1.column_name) "
							+ " where t.table_name in ("
							+ tableNames
							+ ") order by table_name,column_name";
				} else if (isH2) {
					sql = "select t.column_name ,t.remarks ,t.type_name ,t.numeric_precision ,t.numeric_scale ,t.is_nullable ,t.table_name ,t1.column_list||','  from (select column_name ,remarks ,type_name ,numeric_precision ,numeric_scale ,is_nullable ,table_name from information_schema.columns )t left join (select table_name,column_list from information_schema.constraints )t1 on(t.table_name = t1.table_name) WHERE t.TABLE_NAME in("
							+ tableNames
							+ ")  order by table_name, column_name ";
				} else if (isMySql || isGbase) { //mysql和gbase语法
					sql = "SELECT  COLUMN_NAME, COLUMN_COMMENT,DATA_TYPE,CASE WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION "
							+ "ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH,NUMERIC_SCALE,IS_NULLABLE,TABLE_NAME,COLUMN_KEY FROM information_schema.columns WHERE table_schema=database() AND table_name in ("
							+ tableNames + ") order by table_name,column_name";
				} else if (isELK) {
                    sql = "SELECT "
                        +"s.column_name AS COLUMN_NAME, "
                        +"COALESCE(col_description(c.oid,ordinal_position) ,s.column_name) AS COMMENTS, "
                        +"s.data_type, "
                        +"CASE "
                        +"WHEN s.CHARACTER_MAXIMUM_LENGTH IS NULL "
                        +"THEN s.NUMERIC_PRECISION "
                        +"ELSE s.CHARACTER_MAXIMUM_LENGTH "
                        +"END AS LENGTH, "
                        +"s.NUMERIC_SCALE, "
                        +"s.IS_NULLABLE, "
                        +"s.TABLE_NAME, "
                        +"CASE "
                        +"WHEN s.column_name IN "
                        +"( "
                        +"SELECT "
                        +"pg_attribute.attname AS colname "
                        +"FROM "
                        +"pg_constraint "
                        +"INNER JOIN "
                        +"pg_class "
                        +"ON "
                        +"pg_constraint.conrelid = pg_class.oid "
                        +"INNER JOIN "
                        +"pg_attribute "
                        +"ON "
                        +"pg_attribute.attrelid = pg_class.oid "
                        +"AND pg_attribute.attnum = pg_constraint.conkey[1] "
                        +"WHERE "
                        +"pg_constraint.contype='p' "
                        +"AND pg_table_is_visible(pg_class.oid)) "
                        +"THEN 'Y' "
                        +"ELSE 'N' "
                        +"END AS LENGTH "
                        +"FROM "
                        +"information_schema.columns s, "
                        +"pg_class c "
                        +"WHERE "
                        +"s.table_name = c.relname "
                        +"AND s.table_schema = current_schema() ";
                } else if (isGaussdb) {
					sql = "SELECT  t1.COLUMN_NAME, COALESCE(col_description(t3.oid,ordinal_position),t1.column_name) as COLUMN_COMMENT,t1.DATA_TYPE,CASE WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION "
							+ "ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH,t1.NUMERIC_SCALE,t1.IS_NULLABLE,t1.TABLE_NAME,t2.COLUMN_KEY FROM information_schema.columns t1 " +
							"left join ( select table_schema,table_name,string_agg(column_name, ',') as COLUMN_KEY from information_schema.key_column_usage " +
							"GROUP BY(table_schema,table_name)) t2 " +
							"on t1.table_schema = t2.table_schema and t1.table_name = t2.table_name left join pg_class t3 on t1.table_name = t3.relname WHERE t1.table_schema=current_schema() AND t1.table_name in ("
							+ tableNames.toLowerCase() + ") order by t1.table_name,t1.column_name";
//					sql = "SELECT " + "s.column_name AS COLUMN_NAME, "
//							+ "COALESCE(col_description(c.oid,ordinal_position) ,s.column_name) AS COMMENTS, "
//							+ "s.data_type, " + "CASE " + "WHEN s.CHARACTER_MAXIMUM_LENGTH IS NULL "
//							+ "THEN s.NUMERIC_PRECISION " + "ELSE s.CHARACTER_MAXIMUM_LENGTH " + "END AS LENGTH, "
//							+ "s.NUMERIC_SCALE, " + "s.IS_NULLABLE, " + "s.TABLE_NAME, " + "CASE "
//							+ "WHEN s.column_name IN " + "( " + "SELECT " + "pg_attribute.attname AS colname " + "FROM "
//							+ "pg_constraint " + "INNER JOIN " + "pg_class " + "ON "
//							+ "pg_constraint.conrelid = pg_class.oid " + "INNER JOIN " + "pg_attribute " + "ON "
//							+ "pg_attribute.attrelid = pg_class.oid "
//							+ "AND pg_attribute.attnum = pg_constraint.conkey[1] " + "WHERE "
//							+ "pg_constraint.contype='p' " + "AND pg_table_is_visible(pg_class.oid) AND pg_class.relname in( "+tableNames.toLowerCase()+")) " + "THEN 'Y' "
//							+ "ELSE 'N' " + "END AS COLUMN_KEY " + "FROM " + "information_schema.columns s, "
//							+ "pg_class c " + "WHERE " + "s.table_name = c.relname "
//							+ "AND s.table_schema = current_schema() AND s.table_name in("+tableNames.toLowerCase()+") order by s.table_name,s.column_name";
				} else {
					sql = "SELECT t.COLNAME,t.REMARKS,t.TYPENAME,t.LENGTH,t.SCALE,t.NULLS,t.tabName,t1.isPk "
							+ "FROM ( SELECT COLNAME, REMARKS, TYPENAME, LENGTH, SCALE, NULLS, tabName FROM SYSCAT.COLUMNS WHERE TABSCHEMA=CURRENT SCHEMA)t LEFT JOIN ( SELECT colname, tabName, 'Y' AS isPk FROM SYSCAT.KEYCOLUSE WHERE TABSCHEMA=CURRENT SCHEMA )t1 "
							+ "ON t.COLNAME = t1.colname AND t.tabName = t1.tabName where t.tabName in("
							+ tableNames + ") order by tabName,colname";
				}
				try {
					stm = conn.createStatement();
					ResultSet rs = stm.executeQuery(sql);
					Map<String, List<RptSysModuleCol>> map = new HashMap<String, List<RptSysModuleCol>>();
					while (rs.next()) {
						List<RptSysModuleCol> list;
						if (map.get(rs.getString(7)) == null) {
							list = new ArrayList<RptSysModuleCol>();
						} else {
							list = (List<RptSysModuleCol>) map.get(rs
									.getString(7));
						}
						RptSysModuleCol col = new RptSysModuleCol();
						col.setEnNm(rs.getString(1));// 列名
						col.setCnNm(rs.getString(2) == null ? rs.getString(1)
								: rs.getString(2));// 列注释
						col.setDbType(getLogicDataType(rs.getString(3)));
						try {
							col.setLen(new BigDecimal(rs.getString(4)));
						} catch (Exception e1) {
							col.setLen(new BigDecimal(0));// 长度
						}
						try {
							col.setPrecision2(new BigDecimal(rs.getString(5)));// 精度
						} catch (Exception e) {
							col.setPrecision2(new BigDecimal(0));// 精度
						}
						col.setIsNull(("Y"
								.equals(rs.getString(6).toUpperCase()))
								|| ("YES".equals(rs.getString(6).toUpperCase())) ? COMMON_BOOLEAN_YES
								: COMMON_BOOLEAN_NO);
						if (isH2) {
							col.setIsPk(rs.getString(8).contains(
									rs.getString(1) + ",") ? COMMON_BOOLEAN_YES
									: COMMON_BOOLEAN_NO);
						} else if (isGaussdb) {
							if(StringUtils.isBlank(rs.getString(8))){
								col.setIsPk(COMMON_BOOLEAN_NO);
							} else {
								col.setIsPk(rs.getString(8).contains(
										rs.getString(1)) ? COMMON_BOOLEAN_YES
										: COMMON_BOOLEAN_NO);
							}
						} else {
							col.setIsPk(StringUtils.isBlank(rs.getString(8)) ? COMMON_BOOLEAN_NO
									: COMMON_BOOLEAN_YES);
						}
						col.setIsUse(COMMON_BOOLEAN_YES);
						list.add(col);
						map.put(rs.getString(7), list);
					}
					return map;
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				} finally {
					// 释放连接
					JdbcUtils.closeConnection(conn);
				}
			}
		}

		return new HashMap<String, List<RptSysModuleCol>>();
	}

	private List<RptSysModuleCol> getPK(String tableName,
			List<RptSysModuleCol> columns, BioneDsInfo ds, String dbType) {
		String sql = null;
		if (dbType.equals("oracle") ||dbType.equals("incetpor")) {
			sql = "select column_name from user_constraints c,user_cons_columns col where c.constraint_name=col.constraint_name and c.constraint_type='P'and c.table_name='"
					+ tableName + "'";
		} else if (dbType.equals("h2")) {
			sql = "SELECT    COLUMN_LIST  FROM   INFORMATION_SCHEMA.CONSTRAINTS WHERE TABLE_NAME ='"
					+ tableName + "'";
		} else if (dbType.equals("elk")) {
            sql = "select "
                +"pg_attribute.attname as colname "
                +"from "
                +"pg_constraint "
                +"inner join pg_class on pg_constraint.conrelid = "
                +"pg_class.oid "
                +"inner join pg_attribute on pg_attribute.attrelid = "
                +"pg_class.oid and "
                +"pg_attribute.attnum = "
                +"pg_constraint.conkey[1] "
                +"where pg_constraint.contype='p' "
                +"and pg_table_is_visible(pg_class.oid) ";
      	} else {
			sql = "select   colname   from   SYSCAT.KEYCOLUSE   where   TABSCHEMA=CURRENT SCHEMA   and   TABNAME='"
					+ tableName + "'";
		}
		Connection conn = this.getConnOfDs(ds);
		Statement stm = null;
		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				if (notEmpty(rs.getString(1))) {
					String pkName = rs.getString(1).toUpperCase();
					String[] pkNameArr = StringUtils.split(pkName, ',');
					for (int i = 0; i < pkNameArr.length; i++) {
						for (RptSysModuleCol col : columns) {
							if (col.getEnNm().toUpperCase()
									.equals(pkNameArr[i])) {
								col.setIsPk(COMMON_BOOLEAN_YES);
							}
						}
					}
				}
			}
			return columns;
		} catch (SQLException e) {
			return columns;
		} finally {
			// 释放连接
			JdbcUtils.closeConnection(conn);
		}
	}

	/**
	 * 保存配置完成的逻辑数据集和逻辑数据项
	 * 
	 * @param dataset
	 *            数据集
	 * @param datacolsJsonStr
	 *            数据项集合json
	 */
	@Transactional(readOnly = false)
	public void save(RptSysModuleInfo dataset, Boolean isAdd, String datacolsJsonStr) {
		String datasetId = dataset.getSetId();
		// 保存数据集
		//dataset.setBusiType(GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC);//目前所有模型都是公共业务模型
		if (isAdd) {// 新增
			this.saveOrUpdateEntity(dataset);
			// 删除原关联的数据项
			this.rptDataSetDao.batchDelModuleColBySetId(dataset.getSetId());

			// 添加新的关联数据项
			if (notEmpty(datacolsJsonStr)) {
				// 解析json
				JSONObject jsonObject = JSON.parseObject(datacolsJsonStr);
				JSONArray jsonArray = jsonObject.getJSONArray("fields");
				for (int i = 0; i < jsonArray.size(); i++) {
					RptSysModuleCol col = jsonArray.getObject(i, RptSysModuleCol.class);
					if (col != null) {
						col.setColId(RandomUtils.uuid2());
						if (StringUtils.isEmpty(col.getCnNm())) {
							col.setCnNm(col.getEnNm());
						}
						col.setSetId(datasetId);
						//this.baseDAO.merge(col);//字段有关键字，不适用于mysql
						this.rptDataSetDao.saveModuleCol(col);
					}
				}
			}
		} else {// 修改
			// 获取数据库中的原数据
			RptSysModuleInfo datasetDB = this.getModuleInfoById(dataset
					.getSetId());
			if (datasetDB.getTableEnNm().equals(dataset.getTableEnNm())) {// 如果关联表名没有被修改
				if (notEmpty(datacolsJsonStr)) {
					// 解析json
					JSONObject jsonObject = JSON.parseObject(datacolsJsonStr);
					JSONArray jsonArray = jsonObject.getJSONArray("fields");
					// 仅录入新增加的数据项列和旧数据项列的交集
					Map<String, Object> existColMap = Maps.newHashMap();
					// 待删除的字段ids
					List<String> delIdList = Lists.newArrayList();
					List<RptSysModuleCol> tableCols = this.rptDataSetDao
							.getDatacolsOfDataset(datasetId);
					for (int i = 0; i < jsonArray.size(); i++) {
						RptSysModuleCol col = jsonArray.getObject(i, RptSysModuleCol.class);
						if (col != null) {
							col.setSetId(datasetId);
							if (col.getColId() == null) {
								col.setColId(RandomUtils.uuid2());
								if (StringUtils.isEmpty(col.getCnNm())) {
									col.setCnNm(col.getEnNm());
								}
								//this.baseDAO.merge(col);//字段有关键字，不适用于mysql
								this.rptDataSetDao.saveModuleCol(col);
							} else {
								//this.baseDAO.merge(col);//字段有关键字，不适用于mysql
								this.rptDataSetDao.updateModuleCol(col);
								existColMap.put(col.getColId(), null);
							}
						}
					}
					int tableColsSize = tableCols.size();
					for (int i = 0; i < tableColsSize; i++) {
						RptSysModuleCol tableCol = tableCols.get(i);
						String colId = tableCol.getColId();
						if (!existColMap.containsKey(colId)) {
							delIdList.add(colId);
						}
					}
					if (delIdList.size() > 0) {
						Map<String, Object> params = Maps.newHashMap();
						params.put("list", delIdList);
						this.rptDataSetDao.batchDelModuleColByColIds(params);
					}
				}
			} else {// 表名已经被修改了
					// 删除原关联的数据项
				this.rptDataSetDao.batchDelModuleColBySetId(dataset.getSetId());

				// 添加新的关联数据项
				if (notEmpty(datacolsJsonStr)) {
					// 解析json
					JSONObject jsonObject = JSON.parseObject(datacolsJsonStr);
					JSONArray jsonArray = jsonObject.getJSONArray("fields");
					for (int i = 0; i < jsonArray.size(); i++) {
						RptSysModuleCol col = jsonArray.getObject(i, RptSysModuleCol.class);
						if (col != null) {
							col.setColId(RandomUtils.uuid2());
							col.setSetId(datasetId);
							this.baseDAO.merge(col);
						}
					}
				}
			}
			this.rptDataSetDao.updateModuleInfo(dataset);
		}
	}

	/**
	 * 批量删除数据集
	 * 
	 * @param ids
	 *            数据集Id集合
	 */
	@Transactional(readOnly = false)
	public void deleteBatch(List<String> ids) {
		this.rptDataSetDao.batchDelModuleCols(ids);
		this.rptDataSetDao.batchDelModuleInfos(ids);
	}

	/**
	 * 生成数据集预览查询Form结构
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @return
	 */
	public String getSearchStruct(String datasetId) {
		StringBuilder scs = new StringBuilder();
		if (notEmpty(datasetId)) {
			// 数据项
			Map<String, Object> params = Maps.newHashMap();
			params.put("setId", datasetId);
			params.put("isQueryCol", COMMON_BOOLEAN_YES);
			List<RptSysModuleCol> cols = this.rptDataSetDao
					.getSearchStruct(params);
			if (notEmpty(cols)) {
				scs.append("[");
				for (int i = 0; i < cols.size(); i++) {
					RptSysModuleCol col = cols.get(i);
					scs.append("{display:'");
					scs.append(notEmpty(col.getCnNm()) ? col.getCnNm() : col
							.getEnNm());
					scs.append("',");
					scs.append("name:'");
					scs.append(col.getEnNm());
					scs.append("_name',");
					scs.append("type:'");
					if (LOGIC_DATA_TYPE_DATE.equals(col.getColType())) {
						scs.append("date");
					} else {
						scs.append("text");
					}
					scs.append("',newline:");
					scs.append(i % 2 == 0);
					scs.append(",cssClass:'field',");
					if (LOGIC_DATA_TYPE_NUMBER.equals(col.getColType())) {
						scs.append("options:{digits : true},");
					}
					scs.append("attr : {field :'");
					scs.append(col.getEnNm());
					scs.append("',op : 'like'}}");
					if (i != cols.size() - 1) {
						scs.append(",");
					}
				}
				scs.append("]");
			}
		}
		return scs.toString();
	}

	/**
	 * 生成数据集预览Grid列结构
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @return
	 */
	public String getGridStruct(String datasetId) {
		StringBuilder columns = new StringBuilder();
		if (notEmpty(datasetId)) {
			// 数据项
			Map<String, Object> params = Maps.newHashMap();
			params.put("setId", datasetId);
			params.put("isUse", COMMON_BOOLEAN_YES);
			List<RptSysModuleCol> cols = this.rptDataSetDao
					.getGridStruct(params);
			cols = pkToFirst(cols);
			if (notEmpty(cols)) {
				columns.append("[");
				for (int i = 0; i < cols.size(); i++) {
					RptSysModuleCol col = cols.get(i);
					columns.append("{display:'");
					columns.append(notEmpty(col.getCnNm()) ? col.getCnNm()
							: col.getEnNm());
					columns.append("',");
					columns.append("name:'");
					columns.append(col.getEnNm());
					columns.append("',");
					columns.append("width:'20%'}");
					if (i != cols.size() - 1) {
						columns.append(",");
					}
				}
				columns.append("]");
			}
		}
		return columns.toString();
	}

	/**
	 * 数据集预览信息
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @param firstResult
	 * @param pageSize
	 * @param condition
	 *            过滤条件
	 * @param isStringParam
	 *            过滤条件值是否为页面传入的字符串型参数
	 * @return
	 */
	public Map<String, Object> datasetPreview(String datasetId,
			int firstResult, int pageSize, Map<String, Object> condition,
			boolean isStringParam, String appendFiltorSql) {
		Map<String, Object> preView = Maps.newHashMap();
		if (!notEmpty(datasetId)) {
			preView.put("errorMsg", "数据集不存在。");
			return preView;
		}
		RptSysModuleInfo dataset = this.rptDataSetDao
				.findModuleInfoById(datasetId);
		if (dataset == null) {
			preView.put("errorMsg", "数据集不存在。");
			return preView;
		}

		// 数据源
		BioneDsInfo ds = this.rptDataSetDao.findDataSourceById(dataset
				.getSourceId() == null ? "" : dataset.getSourceId());
		if (ds == null) {
			preView.put("errorMsg", "数据源不存在。");
			return preView;
		}
		// 数据集合法
		if (!notEmpty(dataset.getTableEnNm())) {
			preView.put("errorMsg", "未正确配置物理表。");
			return preView;
		}
		// 数据项
		Map<String, Object> params = Maps.newHashMap();
		params.put("setId", datasetId);
		params.put("isUse", COMMON_BOOLEAN_YES);
		List<RptSysModuleCol> cols = this.rptDataSetDao.getGridStruct(params);
		if (!notEmpty(cols)) {
			preView.put("errorMsg", "数据集未配置有效数据项。");
			return preView;
		}
		StringBuilder colNamesBuff = new StringBuilder();// 字段名集合
		for (int i = 0; i < cols.size(); i++) {
			colNamesBuff.append(cols.get(i).getEnNm());
			colNamesBuff.append(",");
		}
		String colNames = colNamesBuff.toString().substring(0,
				colNamesBuff.length() - 1);

		// 查询数据
		Connection conn = this.getConnOfDs(ds);
		if (conn == null) {
			preView.put("errorMsg", "数据源配置不正确。");
			return preView;
		}
		String sql = "";
		int start = firstResult;
		int end = start + pageSize - 1;
		// 加工过滤条件
		RptSqlFilterVO filter = this.getFilter(datasetId, condition,
				isStringParam, appendFiltorSql);

		PreparedStatement stm = null;
		ResultSet rs = null;

		// 数据库表型
		sql = this.getSql(ds.getDriverId(), colNames, dataset.getTableEnNm(),
				start, end, filter.getFilterSql());
		try {
			stm = conn.prepareStatement(sql);
			for (int i = 0; i < filter.getParams().size(); i++) {
				stm.setObject(i + 1, filter.getParams().get(i));
			}
			rs = stm.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null)
				JdbcUtils.closeConnection(conn);
			preView.put("errorMsg", "数据库中相关表结构发生变化。");
			return preView;
		}

		List<Map<String, String>> grid = Lists.newArrayList();
		try {
			while (rs.next()) {
				Map<String, String> row = Maps.newHashMap();
				for (int i = 0; i < cols.size(); i++) {
					row.put(cols.get(i).getEnNm(),
							rs.getString(i + 1) == null ? "" : rs
									.getString(i + 1));
				}
				grid.add(row);
			}
		} catch (SQLException e) {
			if (conn != null)
				JdbcUtils.closeConnection(conn);
			preView.put("errorMsg", "未查询到有效的结果。");
			return preView;
		}
		preView.put("Rows", grid);
		preView.put("Total", grid.size());
		// 释放连接
		if (conn != null)
			JdbcUtils.closeConnection(conn);
		return preView;
	}

	/**
	 * oracle/db2的分页SQL
	 * 
	 * @param dbType
	 *            数据库类型(oracle/db2)
	 * @param tableName
	 *            表名
	 * @param cols
	 *            查询的字段字符串
	 * @param start
	 *            分页开始
	 * @param end
	 *            分页结束
	 * @return
	 */
	private String getSql(String dbType, String cols, String sourceTable,
			int start, int end, String filter) {
		StringBuilder sqlBuff = new StringBuilder();
		if (ORACLE_DATA_SOURCE.equals(dbType)  || ORACLE_RAC_DATA_SOURCE.equals(dbType) || H2_DATA_SOURCE.equals(dbType)) {
			sqlBuff.append("SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM (SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(",ROWNUM AS RN FROM ");
			sqlBuff.append("(SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM ");
			sqlBuff.append(sourceTable);
			sqlBuff.append(" WHERE 1=1 ");
			if (notEmpty(filter)) {
				sqlBuff.append(" AND ");
				sqlBuff.append(filter);
			}
			sqlBuff.append(")");
			sqlBuff.append(" WHERE ROWNUM<=");
			sqlBuff.append(end);
			sqlBuff.append(")WHERE RN>=");
			sqlBuff.append(start);
		} else if(ELK_DATA_SOURCE.equals(dbType)){
            sqlBuff.append("SELECT ");
            sqlBuff.append(cols);
            sqlBuff.append(" FROM (SELECT ");
            sqlBuff.append(cols);
            sqlBuff.append(",ROW_NUMBER() OVER() AS RN FROM ");
            sqlBuff.append("(SELECT ");
            sqlBuff.append(cols);
            sqlBuff.append(" FROM ");
            sqlBuff.append(sourceTable);
            sqlBuff.append(" WHERE 1=1 ");
            if (notEmpty(filter)) {
                sqlBuff.append(" AND ");
                sqlBuff.append(filter);
            }
            sqlBuff.append(")");
//          sqlBuff.append(" WHERE ROWNUM<=");
//          sqlBuff.append(end);
            sqlBuff.append(") WHERE RN <='"+end+"' AND RN>=");
            sqlBuff.append(start);
        }else if(INCEPTOR_DATA_SOURCE.equals(dbType)){
            /*
             * 修改下星环sql ROW_NUMBER函数
             */
            sqlBuff.append("SELECT ");
            sqlBuff.append(cols);
            sqlBuff.append(" FROM (SELECT ");
            sqlBuff.append(cols);
            sqlBuff.append(",ROW_NUMBER() OVER() AS rowid FROM ");
            sqlBuff.append("(SELECT ");
            sqlBuff.append(cols);
            sqlBuff.append(" FROM ");
            sqlBuff.append(sourceTable);
            sqlBuff.append(" WHERE 1=1 ");
            if (notEmpty(filter)) {
                sqlBuff.append(" AND ");
                sqlBuff.append(filter);
            }
            sqlBuff.append(")) WHERE rowid>=");
            sqlBuff.append(start);
            sqlBuff.append(" AND rowid <=");
            sqlBuff.append(end);
        }  else if(MYSQL_DATA_SOURCE.equals(dbType) || GBASE_DATA_SOURCE.equals(dbType)){//mysql语法
        	sqlBuff.append("SELECT ");
        	sqlBuff.append(cols);
        	sqlBuff.append(" FROM ");
			sqlBuff.append(sourceTable);
			sqlBuff.append(" WHERE 1=1 ");
			if (notEmpty(filter)) {
				sqlBuff.append(" AND ");
				sqlBuff.append(filter);
			}
			sqlBuff.append(" limit ");
			sqlBuff.append(start);
			sqlBuff.append(",");
			sqlBuff.append(end);
        } else if (POSTGRESQL_DATA_SOURCE.equals(dbType)){
			sqlBuff.append("SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM ");
			sqlBuff.append(sourceTable);
			sqlBuff.append(" WHERE 1=1 ");
			if (notEmpty(filter)) {
				sqlBuff.append(" AND ");
				sqlBuff.append(filter);
			}
			sqlBuff.append(" limit ");
			sqlBuff.append((end - start));
			sqlBuff.append(" offset ");
			sqlBuff.append(start);
		} else {
			sqlBuff.append("SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM (SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(",ROWNUMBER() OVER() AS rowid FROM ");
			sqlBuff.append("(SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM ");
			sqlBuff.append(sourceTable);
			sqlBuff.append(" WHERE 1=1 ");
			if (notEmpty(filter)) {
				sqlBuff.append(" AND ");
				sqlBuff.append(filter);
			}
			sqlBuff.append(")) WHERE rowid>=");
			sqlBuff.append(start);
			sqlBuff.append(" AND rowid <=");
			sqlBuff.append(end);
		}
		return sqlBuff.toString();
	}

	/**
	 * 根据查询condition获取过滤VO
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @param condition
	 *            查询条件字符串
	 * @return
	 */
	private RptSqlFilterVO getFilter(String datasetId,
			Map<String, Object> condition, boolean isStringParam,
			String appendFiltorSql) {
		RptSqlFilterVO filter = new RptSqlFilterVO();
		StringBuilder filterSql = new StringBuilder();

		List<String> names = Lists.newArrayList();
		List<Object> values = Lists.newArrayList();
		Set<String> keys = condition.keySet();
		for (String k : keys) {
			names.add(k);
			values.add(condition.get(k));
		}

		Map<String, RptSysModuleCol> cols = null;
		if (notEmpty(names)) {
			cols = this.getColType(datasetId, names);
		}

		for (int i = 0; i < names.size(); i++) {
			if (cols == null || cols.get(names.get(i)) == null) {
				continue;
			}
			String n = names.get(i);
			Object v = values.get(i);
			String t = cols.get(n) != null ? cols.get(n).getColType()
					: LOGIC_DATA_TYPE_TEXT;
			String op = cols.get(n) != null ? cols.get(n).getQueryLogic() : "=";
			filterSql.append(n);
			if (LOGIC_DATA_TYPE_DATE.equals(t)
					|| LOGIC_DATA_TYPE_NUMBER.equals(t)) {
				if ("like".equals(op) || op == null) {
					filterSql.append("=? ");
				} else {
					filterSql.append(op);
					filterSql.append("? ");
				}
			} else {
				if ("=".equals(op)) {
					filterSql.append("=? ");
				} else {
					filterSql.append(" like ? ");
				}
			}
			if (i != (names.size() - 1)) {
				filterSql.append(" AND ");
			}

			Object p = null;
			if (isStringParam) {
				String vStr = v.toString();
				try {
					if (LOGIC_DATA_TYPE_DATE.equals(t)) {
						long tt = sdf.parse(vStr).getTime();
						p = new Timestamp(tt);
					} else if (LOGIC_DATA_TYPE_NUMBER.equals(t)) {
						p = new BigDecimal(vStr);
					} else if (LOGIC_DATA_TYPE_TEXT.equals(t)) {
						p = ("=".equals(op)) ? vStr : ("%" + vStr + "%");
					} else {
						p = vStr;
					}
				} catch (Exception e) {
					if (LOGIC_DATA_TYPE_DATE.equals(t)) {
						p = new Timestamp(new Date().getTime());
					} else if (LOGIC_DATA_TYPE_NUMBER.equals(t)) {
						p = new BigDecimal(0);
					} else if (LOGIC_DATA_TYPE_TEXT.equals(t)) {
						p = ("=".equals(op)) ? vStr : ("%" + vStr + "%");
					} else {
						p = vStr;
					}
				}
			} else {
				p = v;
			}
			filter.addParam(p);
		}
		if (notEmpty(appendFiltorSql)) {
			filterSql.append(" AND ");
			filterSql.append(appendFiltorSql);
		}
		filter.setFilterSql(filterSql.toString());
		return filter;
	}

	// 获取数据项数据类型
	private Map<String, RptSysModuleCol> getColType(String datasetId,
			List<String> colNames) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("setId", datasetId);
		params.put("list", colNames);
		List<RptSysModuleCol> cols = this.rptDataSetDao
				.findModuleColsByParams(params);
		Map<String, RptSysModuleCol> map = Maps.newHashMap();
		for (RptSysModuleCol col : cols) {
			map.put(col.getEnNm(), col);
		}
		return map;
	}

	/**
	 * 获取指定数据源的一个连接
	 * 
	 * @param ds
	 *            数据源
	 * @return
	 */
	private Connection getConnOfDs(BioneDsInfo ds) {
		if (ds == null)
			return null;
		BioneDriverInfo dv = this.rptDataSetDao.findDriverInfoById(ds
				.getDriverId());
		if (dv == null)
			return null;
		// 获取数据源连接
		String driverName = dv.getDriverName();
		String url = ds.getConnUrl();
		String user = ds.getConnUser();
		String pwd = ds.getConnPwd();
		Connection conn = null;
		if (notEmpty(driverName) && notEmpty(url) && notEmpty(user)) {
			try {
			    if(dv.getDriverType().equals("Inceptor")){
//                  String jdbcURL; //= "jdbc:hive2://192.168.251.158:10000/hisdb;principal=hive/ycdata158@TDH";
//                  jdbcURL = url+";principal="+user;
//                  conn = HiveJdbcUtil.getConnection(jdbcURL,driverName);
                    conn = HiveJdbcUtil.getConnection(url,driverName,user,pwd);
                }else{
                    // 注册驱动
                    Driver driver = (Driver) Class.forName(driverName)
                            .newInstance();
                    Properties p = new Properties();
                    p.put("user", user);
                    p.put("password", pwd);
                    // 获取连接
                    conn = driver.connect(url, p);
                }
				if (conn != null) {
					return conn;
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 将一个物理字段类型转换为平台逻辑字段类型
	 * 
	 * @param physicalDataType
	 *            物理数据类型
	 * @return 逻辑数据类型
	 */
	private String getLogicDataType(String physicalDataType) {
		if (!notEmpty(physicalDataType))
			return "";
		if (typeContains(textTypes, physicalDataType.toLowerCase()))
			return LOGIC_DATA_TYPE_TEXT;
		else if (typeContains(numberTypes, physicalDataType.toLowerCase()))
			return LOGIC_DATA_TYPE_NUMBER;
		else if (typeContains(dateTypes, physicalDataType.toLowerCase()))
			return LOGIC_DATA_TYPE_DATE;
		else if (typeContains(objectTypes, physicalDataType.toLowerCase()))
			return LOGIC_DATA_TYPE_OBJECT;
		else
			return "";
	}

	/**
	 * 将一个物理字段类型转换为平台逻辑字段类型
	 * 
	 * @param physicalDataType
	 *            物理数据类型
	 * @return 逻辑数据类型
	 */
	// private String getOdpsDataType(OdpsType type) {
	// if (type==null)
	// return "";
	// if (type.equals(OdpsType.STRING))
	// return LOGIC_DATA_TYPE_TEXT;
	// else if ( type.equals(OdpsType.BIGINT) || type.equals(OdpsType.DECIMAL)
	// || type.equals(OdpsType.DOUBLE))
	// return LOGIC_DATA_TYPE_NUMBER;
	// else if (type.equals(OdpsType.DATETIME))
	// return LOGIC_DATA_TYPE_DATE;
	// else if (type.equals(OdpsType.ARRAY) || type.equals(OdpsType.MAP) ||
	// type.equals(OdpsType.BOOLEAN))
	// return LOGIC_DATA_TYPE_OBJECT;
	// else
	// return "";
	// }

	private boolean typeContains(String[] types, String str) {
		for (int i = 0; i < types.length; i++) {
			if (str.indexOf(types[i]) >= 0)
				return true;
		}
		return false;
	}

	// 主键列置前
	private List<RptSysModuleCol> pkToFirst(List<RptSysModuleCol> orderCols) {
		List<RptSysModuleCol> pks = Lists.newArrayList();
		List<RptSysModuleCol> cols = Lists.newArrayList();
		for (int i = 0; i < orderCols.size(); i++) {
			if (COMMON_BOOLEAN_YES.equals(orderCols.get(i).getIsPk())) {
				pks.add(orderCols.get(i));
			} else {
				cols.add(orderCols.get(i));
			}
		}
		pks.addAll(cols);
		return pks;
	}

	// 非空判断
	private boolean notEmpty(String temp) {
		if (temp == null || "".equals(temp) || "null".equals(temp))
			return false;
		else
			return true;
	}

	private boolean notEmpty(List<?> temp) {
		if (temp == null || temp.size() == 0)
			return false;
		else
			return true;
	}

	/**
	 * 数据集预览信息
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @param condition
	 *            查询条件
	 * @param appendFiltorSql
	 *            追加的过滤sql串
	 * @param isStringParam
	 *            是否为string型参数
	 * @param isSearchAll
	 *            是否查询全部数据
	 * @param firstResult
	 *            开始索引
	 * @param pageSize
	 *            分页大小
	 * @return
	 */
	public Map<String, Object> datasetPreview(String datasetId,
			Map<String, Object> condition, String appendFiltorSql,
			boolean isStringParam, boolean isSearchAll, int firstResult,
			int pageSize) {
		Map<String, Object> preView = Maps.newHashMap();
		if (!notEmpty(datasetId)) {
			preView.put("errorMsg", "数据集不存在。");
			return preView;
		}
		RptSysModuleInfo dataset = this.rptDataSetDao
				.findModuleInfoById(datasetId);
		if (dataset == null) {
			preView.put("errorMsg", "数据集不存在。");
			return preView;
		}

		// 数据源
		BioneDsInfo ds = this.rptDataSetDao.findDataSourceById(dataset
				.getSourceId() == null ? "" : dataset.getSourceId());
		if (ds == null) {
			preView.put("errorMsg", "数据源不存在。");
			return preView;
		}
		// 数据集合法
		if (!notEmpty(dataset.getTableEnNm())) {
			preView.put("errorMsg", "未正确配置物理表。");
			return preView;
		}
		// 数据项
		Map<String, Object> params = Maps.newHashMap();
		params.put("setId", datasetId);
		params.put("isUse", COMMON_BOOLEAN_YES);
		List<RptSysModuleCol> cols = this.rptDataSetDao.getGridStruct(params);
		if (!notEmpty(cols)) {
			preView.put("errorMsg", "数据集未配置有效数据项。");
			return preView;
		}
		StringBuilder colNamesBuff = new StringBuilder();// 字段名集合
		for (int i = 0; i < cols.size(); i++) {
			colNamesBuff.append(cols.get(i).getEnNm());
			colNamesBuff.append(",");
		}
		String colNames = colNamesBuff.toString().substring(0,
				colNamesBuff.length() - 1);

		// 查询数据
		Connection conn = this.getConnOfDs(ds);
		if (conn == null) {
			preView.put("errorMsg", "数据源配置不正确。");
			return preView;
		}
		String sql = "";
		int start = firstResult;
		int end = start + pageSize - 1;
		// 加工过滤条件
		RptSqlFilterVO filter = this.getFilter(datasetId, condition,
				isStringParam, appendFiltorSql);

		PreparedStatement pstm = null;
		ResultSet rs = null;

		// 数据库表型
		if (isSearchAll) {
			sql = this.getSearchAllSql(ds.getDriverId(), colNames,
					dataset.getTableEnNm(), filter.getFilterSql());
		} else {
			sql = this.getPageSql(ds.getDriverId(), colNames,
					dataset.getTableEnNm(), start, end, filter.getFilterSql());
		}
		try {
			pstm = conn.prepareStatement(sql);
			for (int i = 0; i < filter.getParams().size(); i++) {
				pstm.setObject(i + 1, filter.getParams().get(i));
			}
			rs = pstm.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null)
				JdbcUtils.closeConnection(conn);
			preView.put("errorMsg", "数据库中相关表结构发生变化。");
			return preView;
		}
		List<Map<String, String>> grid = Lists.newArrayList();
		try {
			while (rs.next()) {
				Map<String, String> row = Maps.newHashMap();
				for (int i = 0; i < cols.size(); i++) {
					row.put(cols.get(i).getEnNm(),
							rs.getString(i + 1) == null ? "" : rs
									.getString(i + 1));
				}
				grid.add(row);
			}
		} catch (SQLException e) {
			if (conn != null)
				JdbcUtils.closeConnection(conn);
			preView.put("errorMsg", "未查询到有效的结果。");
			return preView;
		}
		preView.put("Rows", grid);
		preView.put("Total", grid.size());
		// 释放连接
		if (conn != null)
			JdbcUtils.closeConnection(conn);
		return preView;
	}

	/**
	 * oracle/db2的查询所有SQL
	 * 
	 * @param dbType
	 *            数据库类型(oracle/db2)
	 * @param tableName
	 *            表名
	 * @param cols
	 *            查询的字段字符串
	 * @param start
	 *            分页开始
	 * @param end
	 *            分页结束
	 * @return
	 */
	private String getSearchAllSql(String dbType, String cols,
			String sourceTable, String filter) {
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append("SELECT ");
		sqlBuff.append(cols);
		sqlBuff.append(" FROM ");
		sqlBuff.append(sourceTable);
		sqlBuff.append(" WHERE 1=1 ");
		if (notEmpty(filter)) {
			sqlBuff.append(" AND ");
			sqlBuff.append(filter);
		}
		return sqlBuff.toString();
	}

	/**
	 * oracle/db2的分页SQL
	 * 
	 * @param dbType
	 *            数据库类型(oracle/db2)
	 * @param tableName
	 *            表名
	 * @param cols
	 *            查询的字段字符串
	 * @param start
	 *            分页开始
	 * @param end
	 *            分页结束
	 * @return
	 */
	private String getPageSql(String dbType, String cols, String sourceTable,
			int start, int end, String filter) {
		StringBuilder sqlBuff = new StringBuilder();
		if (ORACLE_DATA_SOURCE.equals(dbType) || ORACLE_RAC_DATA_SOURCE.equals(dbType)) {
			sqlBuff.append("SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM (SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(",ROWNUM AS RN FROM ");
			sqlBuff.append("(SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM ");
			sqlBuff.append(sourceTable);
			sqlBuff.append(" WHERE 1=1 ");
			if (notEmpty(filter)) {
				sqlBuff.append(" AND ");
				sqlBuff.append(filter);
			}
			sqlBuff.append(")");
			sqlBuff.append(" WHERE ROWNUM<=");
			sqlBuff.append(end);
			sqlBuff.append(")WHERE RN>=");
			sqlBuff.append(start);
		} else {
			sqlBuff.append("SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM (SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(",ROWNUMBER() OVER() AS rowid FROM ");
			sqlBuff.append("(SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM ");
			sqlBuff.append(sourceTable);
			sqlBuff.append(" WHERE 1=1 ");
			if (notEmpty(filter)) {
				sqlBuff.append(" AND ");
				sqlBuff.append(filter);
			}
			sqlBuff.append(")) WHERE rowid>=");
			sqlBuff.append(start);
			sqlBuff.append(" AND rowid <=");
			sqlBuff.append(end);
		}
		return sqlBuff.toString();
	}

	/**
	 * 获取当前逻辑系统下的所有数据源 带空参数
	 */
	public List<CommonComboBoxNode> getDataSourcesNone() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		// 增加默认空选项
		CommonComboBoxNode defaultComBox = new CommonComboBoxNode();
		defaultComBox.setId("");
		defaultComBox.setText("无");
		nodes.add(defaultComBox);
		List<BioneDsInfo> list = this.rptDataSetDao
				.getDataSources(BioneSecurityUtils.getCurrentUserInfo()
						.getCurrentLogicSysNo());
		if (notEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(list.get(i).getDsId());
				node.setText(list.get(i).getDsName());
				nodes.add(node);
			}
		}
		return nodes;
	}

	/**
	 * 根据catalogId获取一条记录
	 * 
	 * @param catalogId
	 * @return
	 */
	public RptSysModuleCatalog getCatalogInfo(String catalogId) {
		return this.rptDataSetDao.getCatalogInfo(catalogId);
	}

	/**
	 * 新增数据集目录
	 * 
	 * @param catalog
	 */
	public void saveCatalog(RptSysModuleCatalog catalog) {
		this.rptDataSetDao.saveCatalog(catalog);
	}

	/**
	 * 修改数据集目录
	 * 
	 * @param catalog
	 */
	public void updateCatalog(RptSysModuleCatalog catalog) {
		this.rptDataSetDao.updateCatalog(catalog);
	}

	public RptSysModuleInfo getModuleInfoById(String id) {
		return this.getEntityById(RptSysModuleInfo.class, id);
	}

	public List<RptIdxMeasureInfo> getAllMeasures(Map<String, Object> params) {
		return this.rptDataSetDao.getAllMeasures(params);
	}

	public List<CommonTreeNode> getDataModuleTree(String basePath,
			String searchNm, String setType) {
		Map<String, Object> condition = new HashMap<String, Object>();
		Map<String, Object> catalogMap = new HashMap<String, Object>();
		List<RptSysModuleCatalog> catalogList = this.rptDataSetDao
				.getAllCatalogs();
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		List<RptSysModuleInfo> infoList = new ArrayList<RptSysModuleInfo>();
		if (!StringUtils.isEmpty(searchNm)) {
			// 带有查询条件
			condition.put("setNm", "%" + searchNm + "%");
		}
		if (!StringUtils.isEmpty(setType)) {
			condition.put("setType", setType);
		}
		infoList = this.rptDataSetDao.getModulesInfosWithCondition(condition);

		for (int i = 0; i < catalogList.size(); i++) {
			RptSysModuleCatalog moduleCatalog = catalogList.get(i);
			CommonTreeNode catalog = new CommonTreeNode();
			catalog.setId(moduleCatalog.getCatalogId());
			catalog.setText(moduleCatalog.getCatalogNm());
			catalog.setData(moduleCatalog);
			catalog.setUpId(moduleCatalog.getUpId());
			catalog.setIcon(basePath
					+ GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
			catalog.getParams().put("type", "setCatalog");
			List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();

			for (int j = 0; j < infoList.size(); j++) {
				RptSysModuleInfo moduleInfo = infoList.get(j);
				if (moduleCatalog.getCatalogId().equals(
						moduleInfo.getCatalogId())) {
					CommonTreeNode tmp = new CommonTreeNode();
					tmp.setId(moduleInfo.getSetId());
					tmp.setText(moduleInfo.getSetNm());
					tmp.setData(moduleInfo);
					tmp.setUpId(catalog.getId());
					tmp.setIcon(basePath
							+ GlobalConstants4frame.DATA_TREE_NODE_ICON_REPORT);
					tmp.getParams().put("type", "setInfo");
					childList.add(tmp);
				}
			}
			catalog.setChildren(childList);
			resultList.add(catalog);
		}
		for (CommonTreeNode catalog : resultList) {
			catalogMap.put(catalog.getId(), catalog);
		}
		if (!StringUtils.isEmpty(searchNm)) {
			List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
			for (CommonTreeNode tmp : resultList) {
				if (tmp.getChildren() != null && tmp.getChildren().size() > 0) {
					list.add(tmp);
					searchParent(list, catalogMap, tmp);
				}
			}
			return this.createTreeNode(list, false, null, basePath, setType);
		}
		return this.createTreeNode(resultList, true, catalogMap, basePath,
				setType);
	}

	private void searchParent(List<CommonTreeNode> list,
			Map<String, Object> catalogMap, CommonTreeNode node) {
		CommonTreeNode treeNode = (CommonTreeNode) catalogMap.get(node
				.getUpId());
		boolean flag = list.contains(treeNode);
		if (treeNode != null && !flag) {
			list.add(treeNode);
			searchParent(list, catalogMap, treeNode);
		}
	}

	public List<CommonTreeNode> createTreeNode(List<CommonTreeNode> nodes,
			boolean setHasInfosFlag, Map<String, Object> catalogMap,
			String basePath, String setType) {
		List<CommonTreeNode> result = Lists.newArrayList();
		Map<String, CommonTreeNode> cache = createMapCache(nodes);
		CommonTreeNode parent = null;
		for (CommonTreeNode node : nodes) {
			parent = cache.get(node.getUpId());
			if (parent == null) {
				result.add(node);
			} else {
				parent.addChildNode(node);
			}
		}
		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		baseNode.setText("全部");
		if (setType != null) {
			if (setType.equals(SET_TYPE_SUM)) {
				baseNode.setText("总账数据集");
			} else if (setType.equals(SET_TYPE_GENERIC)) {
				baseNode.setText("泛化数据集");
			}
		}
		baseNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
		RptSysModuleCatalog catalogBase = new RptSysModuleCatalog();
		catalogBase
				.setCatalogId(GlobalConstants4frame.TREE_ROOT_NO);
		baseNode.setData(catalogBase);
		baseNode.setChildren(result);
		result = Lists.newArrayList();
		result.add(baseNode);
		return result;
	}

	private Map<String, CommonTreeNode> createMapCache(
			List<CommonTreeNode> nodes) {
		Map<String, CommonTreeNode> result = Maps.newHashMap();
		for (CommonTreeNode node : nodes) {
			result.put(node.getId(), node);
		}
		return result;
	}

	public Map<String, Object> checkHasMeasures(String setId, String setType) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("flag", "0");
		List<RptSysModuleCol> cols = this.rptDataSetDao
				.getDatacolsOfDataset(setId);
		for (int i = 0; i < cols.size(); i++) {
			RptSysModuleCol col = cols.get(i);
			if (col.getMeasureNo() != null
					&& col.getColType().equals(COL_TYPE_MEASURE)) {
				if (setType.equals(SET_TYPE_SUM)) {
					if (!col.getMeasureNo().trim().equals("")) {
						params.put("flag", "1");
						break;
					}
				} else {
					params.put("flag", "1");
					break;
				}
			}
		}
		return params;
	}

	/**
	 * 获取所有数据集目录树
	 * 
	 * @param params
	 * @return
	 */
	public List<CommonTreeNode> getCatalogTree() {
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		List<RptSysModuleCatalog> allCatalogs = this.rptDataSetDao
				.getAllCatalogs();
		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId("0");
		baseNode.setText("全部");
		baseNode.setUpId("0");
		RptSysModuleCatalog catalogBase = new RptSysModuleCatalog();
		catalogBase.setCatalogId("0");
		catalogBase.setUpId("0");
		baseNode.setData(catalogBase);
		baseNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
		treeNodes.add(baseNode);
		for (int i = 0; i < allCatalogs.size(); i++) {
			RptSysModuleCatalog catalog = allCatalogs.get(i);
			CommonTreeNode treeChildNode = new CommonTreeNode();
			treeChildNode.setData(catalog);
			treeChildNode.setId(catalog.getCatalogId());
			treeChildNode.setText(catalog.getCatalogNm());
			treeChildNode.setUpId(catalog.getUpId());
			treeChildNode.getParams().put("type", "datasetCatalog");
			treeChildNode.setIcon(basePath
					+ GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
			treeNodes.add(treeChildNode);
		}
		return treeNodes;
	}

	public RptSysModuleInfo getInfoByInfo(String sourceId,String tableEnNm,String type){
		String jql = "select sys from RptSysModuleInfo sys where sys.sourceId = ?0 and sys.tableEnNm = ?1 and sys.setType = ?2";
		List<RptSysModuleInfo> info = this.baseDAO.findWithIndexParam(jql, sourceId,tableEnNm,type);
		if(info != null && info.size() > 0){
			return info.get(0);
		}
		return null;
		
	}
	// 获取指定数据模型标识集合对应的名称集合
	public List<String> getDataSetNamesByParams(Map<String, Object> params) {
		return this.rptDataSetDao.getDataSetNamesByParams(params);
	}

	// 获取指定数据模型集合
	public List<RptSysModuleInfo> getDataSetsByParams(Map<String, Object> params) {
		return this.rptDataSetDao.getDataSetsByParams(params);
	}

	// 获取所有的数据项
	public List<RptSysModuleCol> getDatacolsByParams(Map<String, Object> params) {
		return this.rptDataSetDao.getDatacolsByParams(params);
	}

	public List<CommonTreeNode> getDataSyncTree(String contextPath,
			String setType) {
		List<CommonTreeNode> treeNode = getCatalogTree();
		Map<String, Object> map = new HashMap<String, Object>();
/*		map.put("setType",
				GlobalConstants4plugin.SET_TYPE_MUTI_DIM);*/
		List<RptSysModuleInfo> list = this.rptDataSetDao
				.getModulesInfosWithCondition(map);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getCatalogId() != null
					&& !list.get(i).getCatalogId().equals("")
					&& !list.get(i).getSetId().equals("RPT_REPORT_RESULT")
					&& !list.get(i).getSetId().equals("RPT_IDX_RESULT")) {
				CommonTreeNode tmp = new CommonTreeNode();
				tmp.setId(list.get(i).getSetId());
				tmp.setText(list.get(i).getSetNm());
				tmp.setData(list.get(i));
				tmp.setUpId(list.get(i).getCatalogId());
				tmp.setIcon(contextPath
						+ GlobalConstants4frame.DATA_TREE_NODE_ICON_REPORT);
				tmp.getParams().put("type", "setInfo");
				treeNode.add(tmp);
			}
		}
		return treeNode;
	}

	// 检测数据集是否被关联
	public boolean checkHasBeenCascaded(Map<String, Object> param) {
		Integer num = this.rptDataSetDao.getRelatedCountByDatasetId(param);
		if (num == null || num == 0)
			return false;
		return true;
	}
	
	/**
	 * 获取全部监管机构类型
	 * @return
	 */
	public List<CommonComboBoxNode> getBusiTypeList(Boolean isInclude, List<String> busiTypelist) {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		String jql = "select param from BioneParamInfo param where param.paramTypeNo = ?0 and param.logicSysNo = ?1";
		List<BioneParamInfo> params = new ArrayList<BioneParamInfo>();
		if(busiTypelist != null && busiTypelist.size() > 0) {
			if(true == isInclude) {
				jql += " and param.paramValue in ?2"; 
			}else{
				jql += " and param.paramValue not in ?2"; 
			}
			params = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.TREE_PARAM_TYPE, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), busiTypelist);
		}else {
			params = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.TREE_PARAM_TYPE, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		}
		
		if(params != null && params.size() > 0){
			for(BioneParamInfo param : params){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(param.getParamValue());
				node.setText(param.getParamName());
				nodes.add(node);
			}
		}
		return nodes;
	}

	/**
	 * 获取全部归属地
	 * @return
	 */
	public List<CommonComboBoxNode> getAddrList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		Map<String, Object> dimMap = new HashMap<String, Object>();
		dimMap.put("rpt_type","04");
		dimMap.put("codeType", "EAST_ADDR");
		String sql = "SELECT ITEM ID,ITEM_NAME NAME FROM RPT_STD_CODE_INFO WHERE RPT_TYPE=:rpt_type AND CODE_TYPE=:codeType ORDER BY ORDER_NO";
		List<Object[]> dimList = this.baseDAO.findByNativeSQLWithNameParam(sql, dimMap);
		if(null!=dimList && !dimList.isEmpty()){
			for(Object[] dim : dimList){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(dim[0] != null?dim[0].toString():"");
				node.setText(dim[1] != null?dim[1].toString():"");
				nodes.add(node);
			}

		}
		return nodes;
	}

	public void saveModuleCol(RptSysModuleCol col) {
		rptDataSetDao.saveModuleCol(col);
	}

	public void saveModuleInfo(RptSysModuleInfo idxInfo) {
		rptDataSetDao.saveModuleInfo(idxInfo);
		
	}
	
	/**
	 * 判断数据集ID是否重复
	 * @param setId
	 * @return
	 */
	public Boolean testSameSetId(String setId, Boolean isAdd, String oldSetId){
		Boolean flag = true;
		if(StringUtils.isNotBlank(oldSetId) && oldSetId.equals(setId)){
			return flag;
		}
		String jql = " select t.setId from RptSysModuleInfo t where t.setId = ?0";
		List<String> checkList = this.baseDAO.findWithIndexParam(jql, setId);
		if(isAdd){
			if(checkList.size()>0){
				flag = false;
			}
		}else{
			if(checkList.size()>1){
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 获取指标类报送任务类型
	 * @return
	 */
	public List<CommonComboBoxNode> getIdxTaskType() {

		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		String jql = "select param from BioneParamInfo param where param.paramTypeNo = ?0 and param.logicSysNo = ?1";
		List<BioneParamInfo> params = new ArrayList<BioneParamInfo>();
		params = this.baseDAO.findWithIndexParam(jql, GlobalConstants4frame.IDX_TASK_TYPE_PARAM_TYPE_NO, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if(params != null && params.size() > 0){
			for(BioneParamInfo param : params){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(param.getParamValue());
				node.setText(param.getParamName());
				nodes.add(node);
			}
		}
		return nodes;
	}
}
