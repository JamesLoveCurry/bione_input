package com.yusys.biapp.input.inputTable.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.dict.service.InputDataDictBS;
import com.yusys.biapp.input.dict.utils.ColumnType;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.inputTable.entity.*;
import com.yusys.biapp.input.inputTable.repository.InputTableMybatisDao;
import com.yusys.biapp.input.inputTable.util.ReWriteUtil;
import com.yusys.biapp.input.inputTable.vo.InputTableVO;
import com.yusys.biapp.input.inputTable.vo.TableDataVO;
import com.yusys.biapp.input.inputTable.vo.TableSqlVO;
import com.yusys.biapp.input.inputTable.vo.TableVO;
import com.yusys.biapp.input.task.vo.DataLoadInfoVO;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.entity.RptInputRewriteFieldInfo;
import com.yusys.biapp.input.template.entity.RptInputRewriteTempleInfo;
import com.yusys.biapp.input.template.service.TempleFieldBS;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jdbc.entity.BioneTableMetaData;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.excel.AbstractExcelImport;
import com.yusys.bione.frame.excel.ExcelImporter;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.frame.util.SplitStringBy1000;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.spreadjs.utils.ExcelExportUtil;
import com.yusys.bione.plugin.wizard.web.vo.InputTableImportVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <pre>
 * Title:
 * Description:
 * </pre>
 * 
 * @author zhongqh zhongqh@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * ????????????
 *    ???????????????:     ????????????	  ????????????:     ????????????:
 * </pre>
 */
@Service
@Transactional(readOnly = false)
public class InputTableBS extends BaseBS<RptInputListTableInfo> {

	private final Log logger = LogFactory.getLog(InputTableBS.class);
	private final Log log = LogFactory.getLog(InputTableBS.class);

	@Autowired
	private DataSourceBS dataSourceBS;

	@Autowired
	private InputTableMybatisDao tableDao;
	
	@Autowired
	private TableConstrainBS priIndexBS;

	@Autowired
	private DataUtils dataUtils;
	
	@Autowired
	private TableFieldBS tableColBS;
	@Autowired
	private ReWriteUtil reWriteUtil;
	
	private String fuhao = ";;";
	private String douhao = ",,";
	
	@Autowired
	private TempleFieldBS templeFieldBS;

	@Autowired
	private InputDataDictBS inputDataDictBS;

	@Autowired
	private UserBS userBS;

	/**
	 * ????????????????????????????????????????????????
	 * 
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	public SearchResult<RptInputListTableInfo> getSearch(int firstResult,
			int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select t from RptInputListTableInfo t where t.logicSysNo =:logicSysNo ");

		@SuppressWarnings("unchecked")
		Map<String, String> values = (Map<String, String>) conditionMap.get("params");
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if (!conditionMap.get("jql").equals(StringUtils.EMPTY)) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by t." + orderBy + " " + orderType);
		}
		return this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
	}
	
	
	public RptListDataloadType getRptListDataloadTypeByTableId(String tableId){
		return tableDao.getRptListDataloadTypeByTableId(tableId);
	}
	
	public DataLoadInfoVO getRptListDataloadTypeByTempId(String tempId){
		return tableDao.getRptListDataloadTypeByTempId(tempId);
	}

	/**
	 * ?????????????????????????????????????????????
	 * 
	 * @param dsId
	 * @param tableName
	 * @return
	 */
	public List<String> getTableByDsIdAndTableName(String dsId, String tableName) {
		List<String> lists = Lists.newArrayList();
		List<BioneTableMetaData> tables = this.dataSourceBS.getTableMetaData(dsId, tableName.toUpperCase());
		for (BioneTableMetaData table : tables) {
			lists.add(table.getTableName());
		}
		return lists;
	}

	public List<CommonTreeNode> getTableTreeByDsId(String dsId, String tableNm) {
		if(StringUtils.isEmpty(dsId))
			return null;
		List<BioneTableMetaData> tables;
//		if (StringUtils.isEmpty(tableNm)) {
			tables = this.dataSourceBS.getTableMetaData(dsId, tableNm);
//		} else
//			tables = this.dataSourceBS.getTableMetaData(dsId, tableNm);
		if (tables == null || tables.isEmpty())
			return null;
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		for (BioneTableMetaData metaData : tables) {
			if((!StringUtils.isEmpty(tableNm)) && 
					metaData.getTableName().toUpperCase().indexOf(tableNm.toUpperCase()) == -1){
				continue;
			}
			Map<String, String> params = Maps.newHashMap();
			params.put("dsId", dsId);
			params.put("schemaNm", metaData.getSchemaName());
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(metaData.getTableName());
			treeNode.setText(metaData.getTableName());
			treeNode.setParams(params);
			treeNode.setIsParent(false);
			nodeList.add(treeNode);
		}
		return nodeList;
	}
	
	private String getColType(String fieldType,String fieldLength,String decimalLength){
		List<String> numberList = ColumnType.NumberType;
		List<String> intList = ColumnType.IntType;
		if(intList.indexOf(fieldType.toUpperCase())>=0){
			if(fieldType.toUpperCase().equals("timestamp".toUpperCase())){
				return fieldType+"("+fieldLength+")";
			}
			
			return fieldType;
		}
		else if(numberList.indexOf(fieldType.toUpperCase())>=0){
			return fieldType+"("+fieldLength+","+decimalLength+")";
		}
		else{
			return fieldType+"("+fieldLength+")";
		}
	}
	
	private String getCommentSql(String dbType){
		if(dbType.equals("oracle"))
			return getOracleCommentSql();
		else if (dbType.equals("db2"))
			return getDB2CommentSql();
		else if (dbType.equals("mysql"))
			return getMysqlCommentSql();
		else if (dbType.equals("postgresql")){
			return getPostgresqlCommentSql();
		}
		return null;
	}

	private String getPostgresqlCommentSql() {
		StringBuilder buff = new StringBuilder();
		buff.append("ERROR_MARK VARCHAR(2) ,").append("\n")
				.append("FLOW_NODE_ID VARCHAR(32) , ").append("\n")
				.append("COMMENTS VARCHAR(500) ").append("\n");
		return buff.toString();
	}

	/**
	 * ????????????sql mysql
	 * @return
	 */
	private String getMysqlCommentSql() {
		StringBuilder buff = new StringBuilder();
		
		buff.append("ERROR_MARK VARCHAR(2) ,").append("\n")
		.append("FLOW_NODE_ID VARCHAR(32) , ").append("\n")
		.append("COMMENTS VARCHAR(500) ").append("\n");
		
		return buff.toString();
	}


	/**
	 * ????????????sql db2
	 * @return
	 */
	private String getDB2CommentSql(){
		StringBuilder buff = new StringBuilder();
		
		buff.append("ERROR_MARK VARCHAR(2) ,").append("\n")
		.append("FLOW_NODE_ID VARCHAR(32) , ").append("\n")
		.append("COMMENTS VARCHAR(500) ").append("\n");
		
		return buff.toString();
		
	}
	
	/**
	 * ????????????sql oracle
	 * @return
	 */
	private String getOracleCommentSql(){
		StringBuilder buff = new StringBuilder();
		
		buff.append(" ERROR_MARK VARCHAR2(2) ,").append("\n")
		.append(" FLOW_NODE_ID VARCHAR2(32) , ").append("\n")
		.append(" COMMENTS VARCHAR2(500) ").append("\n");
		
		return buff.toString();
		
	}
	
	/**
	 * ????????????????????????????????????????????????????????????map????????????????????????list????????????????????????sql
	 * 
	 * @param tableInfo
	 * @param tableColMap
	 * @param priIndexList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getTableInfoSql(RptInputListTableInfo tableInfo, Map<String, RptInputListTableFieldInf> tableColMap, List<RptInputListTableConstraint> priIndexList) {

		String dbType = this.dataSourceBS.getDataSourceType(tableInfo.getDsId());
		/*****???????????????????????????ID mod by chenl******/
		RptInputListTableConstraint constraint = new RptInputListTableConstraint();
		constraint.setKeyType(UdipConstants.TAB_PRIMARY);
		constraint.setKeyName("pk_"+tableInfo.getTableEnName());
		constraint.setKeyColumn("DATAINPUT_ID");
		priIndexList.add(constraint);
		
		RptInputListTableFieldInf inf = new RptInputListTableFieldInf();
		inf.setAllowNull("false");
		inf.setFieldCnName("??????");
		inf.setFieldEnName("DATAINPUT_ID");
		inf.setFieldLength("32");
		if(dbType.equals("oracle"))
			inf.setFieldType("VARCHAR2");
		else
			inf.setFieldType("VARCHAR");
//		tableColMap.put("DATAINPUT_ID",inf);
		/************************/
		StringBuilder tableSql = new StringBuilder();	// ??????sql
		StringBuilder commentSql = new StringBuilder();	// ??????????????????sql
		StringBuilder constraintSql = new StringBuilder();	// ???????????????????????????sql
		String targetSchema = this.dataSourceBS.getSchemaByDsId(tableInfo.getDsId());
		if(StringUtils.equals("mysql", dbType)){
			if (tableInfo != null) {
				tableSql.append("create table "+ targetSchema + "." + tableInfo.getTableEnName() +" ( "+ inf.getFieldEnName() +" "+getColType(inf.getFieldType(), inf.getFieldLength(), inf.getDecimalLength())+" not null, ");
				Iterator iter = tableColMap.keySet().iterator();
				while (iter.hasNext()) {
					String colName = (String) iter.next();
					RptInputListTableFieldInf tableColInfo = tableColMap.get(colName);
					String colType = getColType(tableColInfo.getFieldType(), tableColInfo.getFieldLength(), tableColInfo.getDecimalLength());
					if (iter.hasNext()) {	// ??????????????????????????????
						if ("false".equals(tableColInfo.getAllowNull())) {
							tableSql.append("`"+colName+"` " + colType + " ");
							if(StringUtils.isNotEmpty(tableColInfo.getDefaultValue())
									&& !"null".equals(tableColInfo.getDefaultValue())
										&& "undefined".equals(tableColInfo.getDefaultValue())){
								tableSql.append(" default '").append(tableColInfo.getDefaultValue()).append("' ");
							}
							tableSql.append(" comment '" + tableColInfo.getFieldCnName() + "' ");
							tableSql.append(" not null ").append(" ,").append("\n");
						} else {
							tableSql.append("`"+colName+"` " + colType + " ");
							if(StringUtils.isNotEmpty(tableColInfo.getDefaultValue())
									&& !"null".equals(tableColInfo.getDefaultValue())
										&& "undefined".equals(tableColInfo.getDefaultValue())){
								tableSql.append(" default '").append(tableColInfo.getDefaultValue()).append("'   ");
							}
							tableSql.append(" comment '" + tableColInfo.getFieldCnName() + "' ");
							tableSql.append(" ,").append("\n");
						}
					} else {
						if ("false".equals(tableColInfo.getAllowNull())) {
							tableSql.append("`"+colName+"` " + colType + " ").append(" not null ").append(" comment '" + tableColInfo.getFieldCnName() + "' ").append(" ,").append("\n");
						} else {
							tableSql.append("`"+colName+"` " + colType + " ").append(" comment '" + tableColInfo.getFieldCnName() + "' ").append(" ,").append("\n");
						}
						tableSql.append(getCommentSql(dbType));
						tableSql.append(")");
						if (!StringUtils.EMPTY.equals(tableInfo.getTableCnName())) {
							tableSql.append("COMMENT = '"+ tableInfo.getTableCnName() +"';\n");
						}
						//tableSql.append(getTbsSql(dbType,tableInfo.getTableSpace())).append(";").append("\n");
					}
				}	
			}
			if (CollectionUtils.isNotEmpty(priIndexList)) {
				for (int i = 0; i < priIndexList.size(); i++) {
					RptInputListTableConstraint udipPriIndexInfo = priIndexList.get(i);
					if (UdipConstants.TAB_PRIMARY.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("alter table ").append(targetSchema + "." + tableInfo.getTableEnName()).append(" add constraint")
								.append(" primary key (").append(udipPriIndexInfo.getKeyColumn().replace(";", ","))
								.append(")");//.append("\n").append(" using index;").append("\n");
						constraintSql.append(";").append("\n");
					} else if (UdipConstants.TAB_UNIQUE.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("alter table ").append(targetSchema + "." +tableInfo.getTableEnName()).append(" add constraint ")
								.append(" unique (").append(udipPriIndexInfo.getKeyColumn().replace(";", ","))
								.append(")");//.append("\n").append(" using index;").append("\n");
						if(dbType.equals("oracle"))
							constraintSql.append("\n").append(" using index");
						constraintSql.append(";").append("\n");
					} else if (UdipConstants.TAB_INDEX.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("create index ").append(tableInfo.getTableEnName()).append(" on ").append(targetSchema + "." + tableInfo.getTableEnName().trim())
								.append(" (").append(udipPriIndexInfo.getKeyColumn().replace(";", ",")).append(");").append("\n");
					}
				}
			}
			return tableSql.append(constraintSql).toString();
		} else if ("postgresql".equals(dbType)) {
			if (tableInfo != null) {
				tableSql.append("create table ").append(targetSchema).append(".").append(tableInfo.getTableEnName()).append("\n").append("(").append("\n");
				tableSql.append(inf.getFieldEnName()).append(" ").append(getColType(inf.getFieldType(), inf.getFieldLength(), inf.getDecimalLength())).append(" not null ");
				tableSql.append(" ,").append("\n");
				if (!StringUtils.EMPTY.equals(tableInfo.getTableCnName())) {
					commentSql.append("comment on table ").append(targetSchema).append(".").append(tableInfo.getTableEnName()).append(" is ").append("'").append(tableInfo.getTableCnName()).append("'") .append(";").append("\n");
				}
			}
			Iterator iter = tableColMap.keySet().iterator();
			while (iter.hasNext()) {
				String colName = (String) iter.next();
				RptInputListTableFieldInf tableColInfo = tableColMap.get(colName);
				String colType = getColType(tableColInfo.getFieldType(), tableColInfo.getFieldLength(), tableColInfo.getDecimalLength());
				if (iter.hasNext()) {	// ??????????????????????????????
					if ("false".equals(tableColInfo.getAllowNull())) {
						tableSql.append(colName).append(" ").append(colType);
						if(StringUtils.isNotEmpty(tableColInfo.getDefaultValue())
								&& !"null".equals(tableColInfo.getDefaultValue())
								&& "undefined".equals(tableColInfo.getDefaultValue())){
							tableSql.append(" default '").append(tableColInfo.getDefaultValue()).append("' ");
						}

						tableSql.append(" not null ").append(" ,").append("\n");
					} else {
						tableSql.append(colName).append(" ").append(colType);
						if(StringUtils.isNotEmpty(tableColInfo.getDefaultValue())
								&& !"null".equals(tableColInfo.getDefaultValue())
								&& "undefined".equals(tableColInfo.getDefaultValue())){
							tableSql.append(" default '").append(tableColInfo.getDefaultValue()).append("'   ");
						}
						tableSql.append(" ,").append("\n");
					}
				} else {
					if ("false".equals(tableColInfo.getAllowNull())) {
						tableSql.append(colName).append(" ").append(colType).append(" not null ").append(" ,").append("\n");
					} else {
						tableSql.append(colName).append(" ").append(colType).append(" ,").append("\n");
					}
					tableSql.append(getCommentSql(dbType));
					tableSql.append(")").append(getTbsSql(dbType,tableInfo.getTableSpace())).append(";").append("\n");
				}
				if (!StringUtils.EMPTY.equals(tableColInfo.getFieldCnName())) {
					commentSql.append("comment on column ").append(targetSchema).append(".").append(tableInfo.getTableEnName().trim()).append(".").append(tableColInfo.getFieldEnName()).append(" is ").append("'").append(tableColInfo.getFieldCnName()).append("'").append(";").append("\n");
				}
			}
			if (CollectionUtils.isNotEmpty(priIndexList)) {
				for (int i = 0; i < priIndexList.size(); i++) {
					RptInputListTableConstraint udipPriIndexInfo = priIndexList.get(i);
					if (UdipConstants.TAB_PRIMARY.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("alter table ").append(targetSchema).append(".").append(tableInfo.getTableEnName()).append(" add constraint ")
								.append(udipPriIndexInfo.getKeyName()).append(" primary key (").append(udipPriIndexInfo.getKeyColumn().replace(";", ","))
								.append(");").append("\n");
					} else if (UdipConstants.TAB_UNIQUE.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("create index ").append(udipPriIndexInfo.getKeyName()).append(" on ").append(tableInfo.getTableEnName().trim())
								.append("(").append(udipPriIndexInfo.getKeyColumn().replace(";", ",")).append(");").append("\n");
					} else if (UdipConstants.TAB_INDEX.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("create index ").append(udipPriIndexInfo.getKeyName()).append(" on ").append(tableInfo.getTableEnName().trim())
								.append(" (").append(udipPriIndexInfo.getKeyColumn().replace(";", ",")).append(");").append("\n");
					}
				}
			}
		} else{
			//oracle,db2
			if (tableInfo != null) {
				tableSql.append("create table ").append(targetSchema).append(".").append(tableInfo.getTableEnName()).append("\n").append("(").append("\n");
				tableSql.append(inf.getFieldEnName()).append(" ").append(getColType(inf.getFieldType(), inf.getFieldLength(), inf.getDecimalLength())).append(" not null ");
				tableSql.append(" ,").append("\n");
				if (!StringUtils.EMPTY.equals(tableInfo.getTableCnName())) {
					commentSql.append("comment on table ").append(targetSchema).append(".").append(tableInfo.getTableEnName()).append(" is ").append("'").append(tableInfo.getTableCnName()).append("'") .append(";").append("\n");
				}
			}
			Iterator iter = tableColMap.keySet().iterator();
			while (iter.hasNext()) {
				String colName = (String) iter.next();
				RptInputListTableFieldInf tableColInfo = tableColMap.get(colName);
				String colType = getColType(tableColInfo.getFieldType(), tableColInfo.getFieldLength(), tableColInfo.getDecimalLength());
				if (iter.hasNext()) {	// ??????????????????????????????
					if ("false".equals(tableColInfo.getAllowNull())) {
						tableSql.append(colName).append(" ").append(colType);
						if(StringUtils.isNotEmpty(tableColInfo.getDefaultValue())
								&& !"null".equals(tableColInfo.getDefaultValue())
									&& "undefined".equals(tableColInfo.getDefaultValue())){
							tableSql.append(" default '").append(tableColInfo.getDefaultValue()).append("' ");
						}
						
						tableSql.append(" not null ").append(" ,").append("\n");
					} else {
						tableSql.append(colName).append(" ").append(colType);
						if(StringUtils.isNotEmpty(tableColInfo.getDefaultValue())
								&& !"null".equals(tableColInfo.getDefaultValue())
									&& "undefined".equals(tableColInfo.getDefaultValue())){
							tableSql.append(" default '").append(tableColInfo.getDefaultValue()).append("'   ");
						}
						tableSql.append(" ,").append("\n");
					}
				} else {
					if ("false".equals(tableColInfo.getAllowNull())) {
						tableSql.append(colName).append(" ").append(colType).append(" not null ").append(" ,").append("\n");
					} else {
						tableSql.append(colName).append(" ").append(colType).append(" ,").append("\n");
					}
					tableSql.append(getCommentSql(dbType));
					tableSql.append(")").append(getTbsSql(dbType,tableInfo.getTableSpace())).append(";").append("\n");
				}
				if (!StringUtils.EMPTY.equals(tableColInfo.getFieldCnName())) {
					commentSql.append("comment on column ").append(targetSchema).append(".").append(tableInfo.getTableEnName().trim()).append(".").append(tableColInfo.getFieldEnName()).append(" is ").append("'").append(tableColInfo.getFieldCnName()).append("'").append(";").append("\n");
				}
			}	
			if (CollectionUtils.isNotEmpty(priIndexList)) {
				for (int i = 0; i < priIndexList.size(); i++) {
					RptInputListTableConstraint udipPriIndexInfo = priIndexList.get(i);
					if (UdipConstants.TAB_PRIMARY.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("alter table ").append(targetSchema).append(".").append(tableInfo.getTableEnName()).append(" add constraint ")
								.append(udipPriIndexInfo.getKeyName()).append(" primary key (").append(udipPriIndexInfo.getKeyColumn().replace(";", ","))
								.append(")");//.append("\n").append(" using index;").append("\n");
						if(dbType.equals("oracle"))
							constraintSql.append("\n").append(" using index");
						constraintSql.append(";").append("\n");
					} else if (UdipConstants.TAB_UNIQUE.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("alter table ").append(targetSchema).append(".").append(tableInfo.getTableEnName()).append(" add constraint ")
								.append(udipPriIndexInfo.getKeyName()).append(" unique (").append(udipPriIndexInfo.getKeyColumn().replace(";", ","))
								.append(")");//.append("\n").append(" using index;").append("\n");
						if(dbType.equals("oracle"))
							constraintSql.append("\n").append(" using index");
						constraintSql.append(";").append("\n");
					} else if (UdipConstants.TAB_INDEX.equals(udipPriIndexInfo.getKeyType())) {
						constraintSql.append("create index ").append(targetSchema).append(".").append(tableInfo.getTableEnName()).append(" on ").append(tableInfo.getTableEnName().trim())
								.append(" (").append(udipPriIndexInfo.getKeyColumn().replace(";", ",")).append(");").append("\n");
					}
				}
			}
		}
		return tableSql.append(commentSql).append(constraintSql).toString();
	}

	private String getTbsSql(String dbType,String tableSpace){
		if(StringUtils.isEmpty(tableSpace))
			return "";
		StringBuilder buff = new StringBuilder();
		if(dbType.equalsIgnoreCase("oracle"))
			buff.append(" tablespace  ").append(tableSpace);
		else if(dbType.equalsIgnoreCase("db2"))
			buff.append(" IN ").append(tableSpace);
		return buff.toString();
	}
	
	/**
	 * ????????????sql
	 * 
	 * @param createTableSql
	 * @param dsId
	 * @throws SQLException
	 */
	public String createTableBySql(String createTableSql, String dsId)
			throws SQLException {
		if (createTableSql != null && !StringUtils.EMPTY.equals(createTableSql)) {
			Connection conn = null;
			Statement state = null;
			try {
				conn = this.dataSourceBS.getConnection(dsId);
				state = conn.createStatement();
				conn.setAutoCommit(false);
				String[] sql = createTableSql.replaceAll("\n", StringUtils.EMPTY).split(";");
				for (int i = 0; i < sql.length; i++) {
					state.execute(sql[i]);
				}
				conn.commit();
			} catch (Exception e) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.error(e1);
				}
				log.error(e);
				return e.toString();
			} finally {
				conn.close();
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param dsId
	 * @param tableName
	 * @return
	 */
	public List<RptInputListTableInfo> getTableInfoByDsIdAndTableName(
			String dsId, String tableName) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select t from RptInputListTableInfo t where t.dsId =:dsId ");
		jql.append("and upper(t.tableEnName) =:tableName ");
		Map<String, String> values = Maps.newHashMap();
		values.put("dsId", dsId);
		values.put("tableName", tableName.toUpperCase());
		return this.baseDAO.findWithNameParm(jql.toString(), values);
	}

	public List<RptInputListTableFieldInf> getTableColInfoByIdAndCol(String tableId, String colNames) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select s from RptInputListTableFieldInf s where tableId =:tableId ");
		jql.append("and upper(fieldEnName) in(" + colNames.toUpperCase() + ") ");
		Map<String, String> values = Maps.newHashMap();
		values.put("tableId", tableId);
		jql.append(" order by s.fieldEnName ");
		return this.baseDAO.findWithNameParm(jql.toString(), values);
	}

	public InputTableVO getTableInfoById(String tableId) {
		Map<String, Object> p = Maps.newHashMap();
		p.put("tableId", tableId);
		InputTableVO tableInfo = new InputTableVO();
		List<InputTableVO> results = this.tableDao.getTableInfoById(p);
		if (results.size() > 0) {
			tableInfo = results.get(0);
		}
		return tableInfo;
	}

	public List<RptInputListTableFieldInf> getTableColInfoById(String tableId,
			String colNames) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select s from RptInputListTableFieldInf s where tableId =:tableId ");
		if (StringUtils.isNotBlank(colNames)) {
			jql.append("and upper(fieldEnName) not in(" + colNames.toUpperCase() + ") ");
		}
		Map<String, String> values = Maps.newHashMap();
		values.put("tableId", tableId);
		jql.append(" order by s.orderNo ");
		return this.baseDAO.findWithNameParm(jql.toString(), values);
	}
	
	/**
	 * ????????????sql
	 * @param udipTableInfo ???????????????
	 * @param tableColInfo ?????????????????????
	 * @param tableIndexInfo ????????????????????????
	 * @param update ??????????????????????????????????????????
	 * @return
	 * @throws SQLException 
	 */
	public Map<String, String> updateTableSql(RptInputListTableInfo udipTableInfo, String tableColInfo, String tableIndexInfo, String update) {
		// ???????????????????????????????????????????????????????????????????????????
		StringBuilder resultErrInfo = new StringBuilder();
		Boolean isUpdatePri = false;
		Connection conn = null;
		Statement state = null;
		Map<String, String> resultMap = Maps.newHashMap();// ????????????????????????
		if (StringUtils.isNotBlank(udipTableInfo.getDsId()) && StringUtils.isNotBlank(udipTableInfo.getTableId())) {
			try {
				RptInputListTableInfo tableInfo = this.getEntityById(udipTableInfo.getTableId());
				// ?????????????????????????????????
				List<String> tableNameList = this.getTableByDsIdAndTableName(udipTableInfo.getDsId(), tableInfo.getTableEnName());
				Boolean hasTable = CollectionUtils.isNotEmpty(tableNameList);

				conn = dataSourceBS.getConnection(udipTableInfo.getDsId());
				state = conn.createStatement();
				String tableMessage = excuteTableInfo(tableInfo, udipTableInfo, update, state, resultErrInfo);
				if (StringUtils.isNotBlank(tableMessage)) {
					resultErrInfo.append("??????????????????").append(tableMessage).append("???");
					resultMap.put("result", resultErrInfo.toString());
					return resultMap;
				}
				StringBuilder tableIndexInfoGobal = new StringBuilder(tableIndexInfo);
				isUpdatePri = false;
				// ?????????????????????????????????
				String tableColMessage = excuteTableColInfo(tableInfo, udipTableInfo, update, state, tableColInfo, tableIndexInfoGobal, resultErrInfo, isUpdatePri, hasTable);
				if (StringUtils.isNotBlank(tableColMessage)) {
					resultErrInfo.append("??????????????????").append(tableColMessage).append("???");
					resultMap.put("result", resultErrInfo.toString());
					return resultMap;
				}
				// ???????????????????????????????????????
				String priIdexMessage = StringUtils.EMPTY;
				if (!isUpdatePri) {
					if (!tableIndexInfoGobal.toString().equalsIgnoreCase(tableIndexInfo)) {
						priIdexMessage = excutePriIndex(tableInfo, udipTableInfo, update, state, tableIndexInfoGobal.toString(), hasTable);
					} else {
						priIdexMessage = excutePriIndex(tableInfo, udipTableInfo, update, state, tableIndexInfo, hasTable);
					}

					if (StringUtils.isNotBlank(priIdexMessage)) {
						resultErrInfo.append("??????????????????").append(priIdexMessage).append("???");
						resultMap.put("result", resultErrInfo.toString());
						return resultMap;
					}
				}
			} catch (Exception e) {
				log.error(e);
				resultErrInfo.append("??????????????????").append(e.toString()).append("???");
				resultMap.put("result", resultErrInfo.toString());
				resultErrInfo = resultErrInfo.delete(0, resultErrInfo.length());
				return resultMap;
			} finally {
				this.dataSourceBS.releaseConnection(null, state, conn);
			}
		}
		if (resultMap != null && resultMap.size() > 0 && StringUtils.isNotBlank(resultMap.get("result"))) {
			resultErrInfo.append("??????????????????").append(resultMap.get("result")).append("???");
			resultMap.put("result", resultErrInfo.toString());
		} else {
			resultMap.put("result", StringUtils.EMPTY);
		}
		resultErrInfo = resultErrInfo.delete(0, resultErrInfo.length());
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param tableInfo
	 * @param udipTableInfo
	 * @param update
	 * @param state
	 * @return
	 */
	private String excuteTableInfo(RptInputListTableInfo tableInfo, RptInputListTableInfo udipTableInfo, String update, Statement state, StringBuilder resultErrInfo) {
		StringBuilder updateTableSql = new StringBuilder(); // ???????????????sql
		StringBuilder updateTempTableSql = new StringBuilder(); // ???????????????sql
		try {
			if (tableInfo != null && StringUtils.isNotBlank(tableInfo.getTableId())) {
				// ???????????????
				if (StringUtils.isNotBlank(udipTableInfo.getTableEnName()) && !udipTableInfo.getTableEnName().equalsIgnoreCase(tableInfo.getTableEnName())) {
					String tempTableName = DataUtils.INPUT_TEMP_TABLE + tableInfo.getTableEnName().trim();
					// ??????????????????????????????????????????
					List<String> list = getTableByDsIdAndTableName(tableInfo.getDsId(), tempTableName);
					if ("true".equalsIgnoreCase(update) && (list != null && list.size() > 0)) {
						if ((DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().trim()).length() > 30) {
							return "????????????????????????????????????????????????" + DataUtils.INPUT_TEMP_TABLE + "?????????????????????30?????????????????????????????????";
						}
						updateTempTableSql.append("alter table ").append(tempTableName).append(" rename to ")
								.append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().trim()).append(";").append("\n");
					}
					updateTableSql.append("alter table ").append(tableInfo.getTableEnName()).append(" rename to ")
							.append(udipTableInfo.getTableEnName()).append(";").append("\n");
				}
				// ??????????????????
				if ((StringUtils.isNotBlank(udipTableInfo.getTableCnName()) || StringUtils.isNotBlank(tableInfo.getTableCnName()))
						&& !udipTableInfo.getTableCnName().equalsIgnoreCase(tableInfo.getTableCnName())) {
					updateTableSql.append("comment on table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" is ")
							.append("'").append(udipTableInfo.getTableCnName()).append("'").append(";").append("\n");
					if ("true".equalsIgnoreCase(update)) {
						updateTempTableSql.append("comment on table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName()
								.toUpperCase().trim()).append(" is ").append("'").append(udipTableInfo.getTableCnName())
								.append("'").append(";").append("\n");
					}
				}
				tableInfo.setTableType(udipTableInfo.getTableType());
				tableInfo.setDsId(udipTableInfo.getDsId());
				tableInfo.setTableEnName(udipTableInfo.getTableEnName().toUpperCase());
				tableInfo.setTableCnName(udipTableInfo.getTableCnName());
			}
			if (StringUtils.isNotBlank(updateTableSql) && updateTableSql.length() > 0) {
				String[] sql = updateTableSql.toString().replaceAll("\n", StringUtils.EMPTY).split(";");
				for (int i = 0; i < sql.length; i++) {
					state.execute(sql[i]);
				}
				this.saveOrUpdateEntity(tableInfo);
				if (StringUtils.isNotBlank(updateTempTableSql.toString()) && updateTempTableSql.length() > 0) {
					String[] sqlTemp = updateTempTableSql.toString().replaceAll("\n", StringUtils.EMPTY).split(";");
					for (int i = 0; i < sql.length; i++) {
						state.execute(sqlTemp[i]);
					}
				}
				resultErrInfo.append("????????????").append(tableInfo.getTableEnName()).append("????????????????????????,");
			}
		} catch (SQLException e) {
			log.error(e);
			return e.toString();
		}
		return null;
	}
	
	/**
	 * ?????????????????????
	 * @param tableInfo
	 * @param udipTableInfo
	 * @param update
	 * @param state
	 * @param tableColInfo
	 * @return
	 */
	private String excuteTableColInfo(RptInputListTableInfo tableInfo, RptInputListTableInfo udipTableInfo, String update,
			Statement state, String tableColInfo, StringBuilder tableIndexInfoGobal, StringBuilder resultErrInfo, Boolean isUpdatePri, Boolean hasTable) {
		StringBuilder updateTableSql = new StringBuilder(); // ???????????????sql
		StringBuilder updateTempTableSql = new StringBuilder(); // ???????????????sql
		try {
			String[] tableColInfos = tableColInfo.split(fuhao); // ????????????????????????
			List<RptInputListTableFieldInf> tableColList = this.tableColBS.getEntityListByProperty(RptInputListTableFieldInf.class, "tableId", udipTableInfo.getTableId());
			// ??????????????????????????????????????????
			for (int i = 0; i < tableColInfos.length; i++) {
				String[] colInfos = tableColInfos[i].split(douhao,10);
				if (colInfos.length > 5 && StringUtils.isNotBlank(colInfos[5])) {// ???????????????????????????
					RptInputListTableFieldInf udipTableColInfos = this.tableColBS.getEntityById(colInfos[5]);// ?????????????????????????????????
					if (CollectionUtils.isNotEmpty(tableColList)) {// ???????????????????????????????????????????????????
						// tableColList ????????????????????????????????????, colInfos ?????????????????????????????????????????????, ???????????? ????????????????????????
						tableColList.remove(udipTableColInfos);
					}
				}
			}	
			if (UdipConstants.TAB_INPUT.equals(udipTableInfo.getTableType())) {
				// ????????????????????????????????????????????????????????????
				Map<String, RptInputListTableFieldInf> tableColMap = UdipConstants.colMaps.get(this.dataSourceBS.getDataSourceType(tableInfo.getDsId()).toUpperCase());
				@SuppressWarnings("rawtypes")
				Iterator iterator = tableColMap.keySet().iterator();
				// ???????????????????????????, colName ??????????????????
				while (iterator.hasNext()) {
					String colName = (String) iterator.next();
					for (int k = 0; k < tableColList.size(); k++) {
						RptInputListTableFieldInf udipTableCol = tableColList.get(k);
						if (colName.equalsIgnoreCase(udipTableCol.getFieldEnName())) {
							tableColList.remove(udipTableCol);
							break;
						}
					}
				}
			}
			if (CollectionUtils.isNotEmpty(tableColList)) {// ?????????????????????????????????
				for (int i = 0; i < tableColList.size(); i++) {
					RptInputListTableFieldInf udipTableCol = tableColList.get(i);
					// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
					if (StringUtils.isNotBlank(tableIndexInfoGobal)) {
						boolean modfiy = false;
						String[] tableIndexInfos = tableIndexInfoGobal.toString().split(fuhao);
						String paramStr = StringUtils.EMPTY;
						String[] indexInfos = null;
						for (int m = 0; m < tableIndexInfos.length; m++) {
							indexInfos = tableIndexInfos[m].split(douhao,10);
							String[] cols = indexInfos[1].split(";");
							String col = StringUtils.EMPTY;
							if (cols != null && cols.length == 1) {	// ????????????????????????????????????
								if (!udipTableCol.getFieldEnName().equalsIgnoreCase(cols[0])) {
									if (StringUtils.EMPTY.equals(paramStr)) {
										paramStr = indexInfos[0] + douhao + indexInfos[1];
									} else {
										paramStr += fuhao + indexInfos[0] + douhao + indexInfos[1];
									}
								} else {
									modfiy = true;
								}
							} else {
								for (int j = 0; j < cols.length; j++) {
									if (udipTableCol.getFieldEnName().equalsIgnoreCase(cols[j])) {
										modfiy = true;
									} else {
										if (StringUtils.EMPTY.equals(col)) {
											col = cols[j];
										} else {
											col = col + ";" + cols[j];
										}
									}
								}
								if (StringUtils.EMPTY.equals(paramStr)) {
									paramStr = indexInfos[0] + douhao + col;
								} else {
									paramStr = paramStr + fuhao + indexInfos[0] + douhao + col;
								}
							}
						}
						if (modfiy) {
							tableIndexInfoGobal = new StringBuilder(paramStr);
							excutePriIndex(tableInfo, udipTableInfo, update, state, tableIndexInfoGobal.toString(), hasTable);
							isUpdatePri = true;
						}
					}
					// ??????????????????
					updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName()).append(" drop column ").append(udipTableCol.getFieldEnName());
					if ("true".equalsIgnoreCase(update)) {	// ?????????????????????
						updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().trim()).append(" drop column ").append(udipTableCol.getFieldEnName());
					}
					if (StringUtils.isNotBlank(updateTempTableSql.toString()) && updateTempTableSql.length() > 0) {
						state.execute(updateTempTableSql.toString());
						updateTempTableSql = updateTempTableSql.delete(0, updateTempTableSql.length());	// ????????????????????????
					}
					if (StringUtils.isNotBlank(updateTableSql.toString()) && updateTableSql.length() > 0) {
						if (hasTable) {
							state.execute(updateTableSql.toString());
						}
						this.tableColBS.removeEntity(udipTableCol);
						updateTableSql = updateTableSql.delete(0, updateTableSql.length());	// ???????????????????????????
						resultErrInfo.append("????????????").append(udipTableCol.getFieldEnName()).append("????????????????????????,");
					}
				}
			}
			
			// ??????????????????????????????????????????
			RptInputListTableFieldInf udipTableColInfo = new RptInputListTableFieldInf();
			List<RptInputListTableFieldInf> tableColInfoList = Lists.newArrayList();
			for (int i = 0; i < tableColInfos.length; i++) {
				String[] colInfos = tableColInfos[i].split(douhao,10);
				if (colInfos.length > 5 && StringUtils.isNotBlank(colInfos[5])) {// ???????????????
					udipTableColInfo = new RptInputListTableFieldInf();
					RptInputListTableFieldInf udipTableColInfos = this.tableColBS.getEntityById(colInfos[5]);// ?????????????????????????????????
					// ?????????????????????????????????????????????????????????
					if (udipTableColInfos != null) {
						if (!colInfos[0].equalsIgnoreCase(udipTableColInfos.getFieldEnName()) || !colInfos[1].equalsIgnoreCase(
								udipTableColInfos.getFieldType()) || !colInfos[2].equalsIgnoreCase(udipTableColInfos.getAllowNull()) || 
								(!colInfos[3].equalsIgnoreCase(udipTableColInfos.getFieldCnName())) && (!StringUtils.EMPTY.equals(colInfos[3]) || 
								udipTableColInfos.getFieldCnName() != null) || !colInfos[4].equals(String.valueOf(udipTableColInfos.getOrderNo()))) {
							if (!colInfos[0].equalsIgnoreCase(udipTableColInfos.getFieldEnName())) {// ???????????????sql??????????????????????????????
								updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" rename column ")
										.append(udipTableColInfos.getFieldEnName()).append(" to ").append(colInfos[0]).append(";").append("\n");
								if ("true".equalsIgnoreCase(update)) {
									updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE+udipTableInfo.getTableEnName()
											.toUpperCase().trim()).append(" rename column ").append(udipTableColInfos.getFieldEnName()).append(" to ")
											.append(colInfos[0]).append(";").append("\n");
								}
								// ???????????????????????????????????????????????????????????????????????????????????????????????????
								if (StringUtils.isNotBlank(tableIndexInfoGobal)) {
									boolean modfiy = false;
									String[] tableIndexInfos = tableIndexInfoGobal.toString().split(fuhao);
									String paramStr = StringUtils.EMPTY;
									String[] indexInfos = null;
									for (int m = 0; m < tableIndexInfos.length; m++) {
										indexInfos = tableIndexInfos[m].split(douhao,10);
										String[] cols = indexInfos[1].split(";");
										String col = StringUtils.EMPTY;
										for (int j = 0; j < cols.length; j++) {
											if (udipTableColInfos.getFieldEnName().equalsIgnoreCase(cols[j])) {
												cols[j] = colInfos[0];
												modfiy = true;
											}
											if (j == 0) {
												col = cols[j];
											} else {
												col = col + ";" + cols[j];
											}
										}
										indexInfos[1] = col;
										if (m == tableIndexInfos.length - 1) {
											paramStr = paramStr + indexInfos[0] + douhao + indexInfos[1];
										} else {
											paramStr = paramStr + indexInfos[0] + douhao + indexInfos[1] + fuhao;
										}
									}
									if (modfiy) {
										tableIndexInfoGobal = new StringBuilder(paramStr);
									}
								}
							}
							if (!colInfos[1].equalsIgnoreCase(udipTableColInfos.getFieldType()) && colInfos[2].equalsIgnoreCase(udipTableColInfos.getAllowNull())) {// ????????????????????????
								updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" modify ").append(colInfos[0]).append(" ").append(colInfos[1]).append(";").append("\n");
								if ("true".equalsIgnoreCase(update)) {
									updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName()
									.toUpperCase().trim()).append(" modify ").append(colInfos[0]).append(" ").append(colInfos[1]).append(";").append("\n");
								}
							} else if (!colInfos[1].equalsIgnoreCase(udipTableColInfos.getFieldType()) && !colInfos[2].equalsIgnoreCase(
									udipTableColInfos.getAllowNull())) {// ????????????????????????????????????
								String isNull = StringUtils.EMPTY;
								// ?????????????????????????????????????????????????????????????????????????????????????????????Y????????????????????????N?????????????????????
								String nullable = checkNullByTable(state, udipTableInfo.getDsId(), udipTableInfo.getTableEnName(), colInfos[0]);
								if ("false".equals(colInfos[2]) && "Y".equalsIgnoreCase(nullable)) {
									isNull = "not null";
								} else if ("true".equals(colInfos[2]) && "N".equalsIgnoreCase(nullable)) {
									isNull = "null";
								}
								if (StringUtils.isNotBlank(isNull)) {
									updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" modify ")
											.append(colInfos[0]).append(" ").append(colInfos[1]).append(" ").append(isNull).append(";").append("\n");
									if ("true".equalsIgnoreCase(update)) {
										updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName()
												.toUpperCase().trim()).append(" modify ").append(colInfos[0]).append(" ").append(colInfos[1])
												.append(" ").append(isNull).append(";").append("\n");
									}
								}
							} else if (colInfos[1].equalsIgnoreCase(udipTableColInfos.getFieldType()) && !colInfos[2].equalsIgnoreCase(
									udipTableColInfos.getAllowNull())) {// ????????????????????????
								String isNull = StringUtils.EMPTY;
								// ?????????????????????????????????????????????????????????????????????????????????????????????Y????????????????????????N?????????????????????
								String nullable = checkNullByTable(state, udipTableInfo.getDsId(), udipTableInfo.getTableEnName(), colInfos[0]);
								if ("false".equals(colInfos[2]) && "Y".equalsIgnoreCase(nullable)) {
									isNull = "not null";
								} else if ("true".equals(colInfos[2]) && "N".equalsIgnoreCase(nullable)) {
									isNull = "null";
								}
								if (StringUtils.isNotBlank(isNull)) {
									updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" modify ")
											.append(colInfos[0]).append(" ").append(isNull).append(";").append("\n");
									if ("true".equalsIgnoreCase(update)) {
										updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName()
											.toUpperCase().trim()).append(" modify ").append(colInfos[0]).append(" ").append(isNull).append(";")
											.append("\n");
									}
								}
							}

							if (StringUtils.isNotBlank(colInfos[3]) && !colInfos[3].equals(udipTableColInfos.getFieldCnName())) {// ?????????????????????
								updateTableSql.append("comment on column ").append(udipTableInfo.getTableEnName().toUpperCase().trim()).append(".")
									.append(colInfos[0].toUpperCase()).append("\n").append("  is '").append(colInfos[3]).append("'").append(";")
									.append("\n");
								if ("true".equalsIgnoreCase(update)) {
									updateTempTableSql.append("comment on column ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName()
											.toUpperCase().trim()).append(".").append(colInfos[0].toUpperCase()).append("\n").append("  is '")
											.append(colInfos[3]).append("'").append(";").append("\n");
								}
							}

							udipTableColInfo.setFieldEnName(colInfos[0].toUpperCase());
							udipTableColInfo.setFieldType(colInfos[1].toUpperCase());
							udipTableColInfo.setAllowNull(colInfos[2]);	
							udipTableColInfo.setFieldLength(colInfos[6]);
							udipTableColInfo.setDecimalLength(colInfos[7]);
							udipTableColInfo.setDefaultValue(colInfos[8]);
							if (colInfos.length <= 3 || colInfos[3] == StringUtils.EMPTY) {
								udipTableColInfo.setFieldCnName(StringUtils.EMPTY);
							} else {
								udipTableColInfo.setFieldCnName(colInfos[3]);
							}
							udipTableColInfo.setTableId(tableInfo.getTableId());
							udipTableColInfo.setId(colInfos[5]);

							if (StringUtils.isNotBlank(updateTempTableSql.toString()) && updateTempTableSql.length() > 0) {
								String[] sql = updateTempTableSql.toString().replaceAll("\n", StringUtils.EMPTY).split(";");
								for (int n = 0; n < sql.length; n++) {
									state.execute(sql[n]);
								}
								updateTempTableSql = updateTempTableSql.delete(0, updateTempTableSql.length());// ????????????????????????
							}
							if (StringUtils.isNotBlank(updateTableSql) && updateTableSql.length() > 0) {
								if (hasTable) {
									String[] sql = updateTableSql.toString().replaceAll("\n", StringUtils.EMPTY).split(";");
									for (int n = 0; n < sql.length; n++) {
										state.execute(sql[n]);
									}
								}
								this.tableColBS.updateEntity(udipTableColInfo);
								updateTableSql = updateTableSql.delete(0, updateTableSql.length());// ???????????????????????????
								resultErrInfo.append("????????????").append(udipTableColInfo.getFieldEnName()).append("????????????????????????,");
							}
							if (StringUtils.isNotBlank(colInfos[4])) {
								udipTableColInfo.setOrderNo(Integer.parseInt(colInfos[4], 10));
								this.tableColBS.updateEntity(udipTableColInfo);
							}
						}
					}
				} else {// ???????????????
					udipTableColInfo = new RptInputListTableFieldInf();
					udipTableColInfo.setFieldEnName(colInfos[0].toUpperCase());
					udipTableColInfo.setFieldType(colInfos[1].toUpperCase());
					udipTableColInfo.setAllowNull(colInfos[2]);
					udipTableColInfo.setFieldLength(colInfos[6]);
					udipTableColInfo.setDecimalLength(colInfos[7]);
					String colType=getColType(udipTableColInfo.getFieldType(), udipTableColInfo.getFieldLength(), udipTableColInfo.getDecimalLength());
					if ("false".equals(colInfos[2])) {// ???????????????sql
						updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" add ").append(
								udipTableColInfo.getFieldEnName()).append(" ").append(colType).append(" not null;").append("\n");
						if ("true".equalsIgnoreCase(update)) {
							updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().toUpperCase()
									.trim()).append(" add ").append(udipTableColInfo.getFieldEnName()).append(" ").append(colType).append(" not null;")
									.append("\n");
						}
					} else {
						updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" add ").append(
								udipTableColInfo.getFieldEnName()).append(" ").append(colType).append(";").append("\n");
						if ("true".equalsIgnoreCase(update)) {
							updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().toUpperCase()
									.toUpperCase().trim()).append(" add ").append(udipTableColInfo.getFieldEnName()).append(" ").append(colType)
									.append(";").append("\n");
						}
					}
					if (colInfos.length <= 3 || StringUtils.isBlank(colInfos[3])) {
						udipTableColInfo.setFieldCnName(StringUtils.EMPTY);
						updateTableSql.append("comment on column ").append(udipTableInfo.getTableEnName().toUpperCase().trim()).append(".")
						.append(colInfos[0].toUpperCase()).append("\n").append("  is '").append(StringUtils.EMPTY).append("'").append(";").append("\n");
						if ("true".equalsIgnoreCase(update)) {
							updateTempTableSql.append("comment on column ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName()
									.toUpperCase().trim()).append(".").append(colInfos[0].toUpperCase()).append("\n").append("  is '").append(StringUtils.EMPTY)
									.append("'").append(";").append("\n");
						}
					} else {
						udipTableColInfo.setFieldCnName(colInfos[3]);
						updateTableSql.append("comment on column ").append(udipTableInfo.getTableEnName().toUpperCase().trim()).append(".").append(
								colInfos[0].toUpperCase()).append("\n").append("  is '").append(colInfos[3]).append("'").append(";").append("\n");
						if ("true".equalsIgnoreCase(update)) {
							updateTempTableSql.append("comment on column ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName()
									.toUpperCase().trim()).append(".").append(colInfos[0].toUpperCase()).append("\n").append("  is '").append(
											colInfos[3]).append("'").append(";").append("\n");
						}
					}
					if (StringUtils.isNotBlank(colInfos[4])) {
						udipTableColInfo.setOrderNo(Integer.parseInt(colInfos[4]));
					}
					udipTableColInfo.setTableId(tableInfo.getTableId());
					udipTableColInfo.setId(RandomUtils.uuid2());

					if (StringUtils.isNotBlank(updateTempTableSql.toString()) && updateTempTableSql.length() > 0) {
						String[] sql = updateTempTableSql.toString().replaceAll("\n", StringUtils.EMPTY).split(";");
						for (int n = 0; n < sql.length; n++) {
							state.execute(sql[n]);
						}
						updateTempTableSql = updateTempTableSql.delete(0, updateTempTableSql.length());// ????????????????????????
					}
					if (StringUtils.isNotBlank(updateTableSql.toString()) && updateTableSql.length() > 0) {
						if (hasTable) {
							String[] sql = updateTableSql.toString().replaceAll("\n", StringUtils.EMPTY).split(";");
							for (int n = 0; n < sql.length; n++) {
								state.execute(sql[n]);
							}
						}
						this.tableColBS.saveEntity(udipTableColInfo);
						updateTableSql = updateTableSql.delete(0, updateTableSql.length());// ???????????????????????????
						resultErrInfo.append("????????????").append(udipTableColInfo.getFieldEnName()).append("????????????????????????,");
					}
				}
				tableColInfoList.add(udipTableColInfo);
			}
		} catch (SQLException e) {
			log.error(e);
			if (e.toString().contains("ORA-01758")) {
				return "?????????????????????????????????????????????????????????????????????";
			}
			return e.toString();
		}
		return null;
	}
	
	/**
	 * ???????????????????????????
	 * @param tableInfo
	 * @param udipTableInfo
	 * @param update
	 * @param state
	 * @param tableIndexInfo
	 * @return
	 */
	private String excutePriIndex(RptInputListTableInfo tableInfo, RptInputListTableInfo udipTableInfo, String update, Statement state, String tableIndexInfo, Boolean hasTable) {
		StringBuilder updateTableSql = new StringBuilder(); // ???????????????sql
		StringBuilder updateTempTableSql = new StringBuilder(); // ???????????????sql
		try {
			// ???????????????????????????
			List<RptInputListTableConstraint> udipPriIndexList = this.priIndexBS.findEntityListByPropertyAndOrder("tableId", udipTableInfo.getTableId(), "keyType", true);
			// ??????????????????????????????
			if (CollectionUtils.isNotEmpty(udipPriIndexList)) {
				for (int i = 0; i < udipPriIndexList.size(); i++) {
					RptInputListTableConstraint priIndexInfo = udipPriIndexList.get(i);
					boolean priflag = true;
					for (int m = 0; m < UdipConstants.colPriType.size(); m++) {
						// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
						if (priIndexInfo.getKeyColumn().indexOf(UdipConstants.colPriType.get(m).get(UdipConstants.ID)) >= 0) {
							priflag = false;
							break;
						}
					}
					if (UdipConstants.TAB_INDEX.equalsIgnoreCase(priIndexInfo.getKeyType())) {
						updateTableSql.append("drop index " + priIndexInfo.getKeyName());
						if ("true".equalsIgnoreCase(update) && priflag) {
							updateTempTableSql.append("drop index ").append(DataUtils.INPUT_TEMP_TABLE + new StringBuilder(priIndexInfo.getKeyName().trim()));
						}
					} else if (UdipConstants.TAB_PRIMARY.equalsIgnoreCase(priIndexInfo.getKeyType())) {
						updateTableSql.append("alter table " + udipTableInfo.getTableEnName() + " drop primary key");
						if ("true".equalsIgnoreCase(update) && priflag) {
							updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().trim()).append("  drop primary key ");
						}
					} else {
						updateTableSql.append("alter table " + udipTableInfo.getTableEnName() + "  drop constraint " + priIndexInfo.getKeyName());
						if ("true".equalsIgnoreCase(update) && priflag) {
							updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().trim()).append("  drop constraint ").append(DataUtils.INPUT_TEMP_TABLE+priIndexInfo.getKeyName().trim());
						}
					}

					if (StringUtils.isNotBlank(updateTempTableSql.toString()) && updateTempTableSql.length() > 0) {
						state.execute(updateTempTableSql.toString());
						updateTempTableSql = updateTempTableSql.delete(0, updateTempTableSql.length());// ????????????????????????
					}
					
					if (StringUtils.isNotBlank(updateTableSql)) {
						if (hasTable) {
							state.execute(updateTableSql.toString());
						}
						if (UdipConstants.TAB_PRIMARY.equalsIgnoreCase(priIndexInfo.getKeyType())) {
							String[] columns = priIndexInfo.getKeyColumn().split(";");
							for (int n = 0; n < columns.length; n++) {
								List<RptInputListTableFieldInf> udipTableColInfoList = this.getTableColInfoByIdAndCol(udipTableInfo.getTableId(), "'" + columns[n] + "'");
								if (CollectionUtils.isNotEmpty(udipTableColInfoList)) {
									udipTableColInfoList.get(0).setAllowNull("true");
									this.tableColBS.saveOrUpdateEntity(udipTableColInfoList.get(0));	// ????????????????????????????????????????????????
								}
							}
						}
						this.priIndexBS.removeEntity(priIndexInfo);
					}
					updateTableSql.delete(0, updateTableSql.length());
				}
			}

			// ??????????????????????????????
			if (StringUtils.isNotEmpty(tableIndexInfo)) { // ????????????????????????
				String[] tableIndexInfos = tableIndexInfo.split(fuhao);
				for (int i = 0; i < tableIndexInfos.length; i++) {
					RptInputListTableConstraint udipPriIndexInfo = new RptInputListTableConstraint();
					String[] indexInfos = tableIndexInfos[i].split(douhao,10);
					List<Map<String, String>> keyType = UdipConstants.keyType;
					for (int k = 0; k < keyType.size(); k++) {
						Map<String, String> keyMap = keyType.get(k);
						if (keyMap.get(UdipConstants.ID).equals(indexInfos[0])) {
							String priName = keyMap.get(UdipConstants.TAB_BEFORE) + i + udipTableInfo.getTableEnName().toUpperCase();
							// ???????????????30??????????????????????????????????????????
							int leng = 30 - DataUtils.INPUT_TEMP_TABLE.trim().length();
							if (priName.length() > leng) {
								udipPriIndexInfo.setKeyName(priName.substring(0, leng));
							} else {
								udipPriIndexInfo.setKeyName(priName);
							}
							udipPriIndexInfo.setKeyType(keyMap.get(UdipConstants.ID));
							if (UdipConstants.TAB_PRIMARY.equals(indexInfos[0])) {
								indexInfos[1] += douhao + UdipConstants.TAB_DATA_DATE + douhao + UdipConstants.TAB_DATA_CASE + douhao + UdipConstants.TAB_OPER_ORG + douhao + UdipConstants.TAB_ORDER_NO;
							}
							indexInfos[1] = indexInfos[1].replaceAll(douhao, ",");
							break;
						}
					}
					boolean priflag = true;
					for (int m = 0; m < UdipConstants.colPriType.size(); m++) {
						// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
						if (indexInfos[1].indexOf(UdipConstants.colPriType.get(m).get(UdipConstants.ID)) >= 0) {
							priflag = false;
						}
					}
					if (UdipConstants.TAB_INDEX.equalsIgnoreCase(udipPriIndexInfo.getKeyType())) {
						updateTableSql.append("create index ").append(udipPriIndexInfo.getKeyName()).append(" on ").append(udipTableInfo.getTableEnName()).append(" (").append(indexInfos[1].replace(";", ",").toUpperCase()).append(")");
						if ("true".equalsIgnoreCase(update) && priflag) {
							updateTempTableSql.append("create index ").append(DataUtils.INPUT_TEMP_TABLE + udipPriIndexInfo.getKeyName().trim()).append(" on ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().trim()).append(" (").append(indexInfos[1].replace(";", ",").toUpperCase()).append(")");
						}
					} else if (UdipConstants.TAB_PRIMARY.equals(udipPriIndexInfo.getKeyType())) {
						updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" add constraint ").append(udipPriIndexInfo.getKeyName()).append(" ").append("primary key (").append(indexInfos[1].replace(";", ",").toUpperCase()).append(")");
						if ("true".equalsIgnoreCase(update) && priflag) {
							updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().toUpperCase().trim()).append(" add constraint ").append(DataUtils.INPUT_TEMP_TABLE+udipPriIndexInfo.getKeyName().trim()).append(" ").append("primary key (").append(indexInfos[1].replace(";", ",").toUpperCase()).append(")");
						}
					} else {
						updateTableSql.append("alter table ").append(udipTableInfo.getTableEnName().toUpperCase()).append(" add constraint ").append(udipPriIndexInfo.getKeyName()).append(" ").append(udipPriIndexInfo.getKeyType()).append(" (").append(indexInfos[1].replace(";", ",").toUpperCase()).append(")");
						if ("true".equalsIgnoreCase(update) && priflag) {
							updateTempTableSql.append("alter table ").append(DataUtils.INPUT_TEMP_TABLE + udipTableInfo.getTableEnName().toUpperCase().trim()).append(" add constraint ").append(DataUtils.INPUT_TEMP_TABLE+udipPriIndexInfo.getKeyName().trim()).append(" ").append(udipPriIndexInfo.getKeyType()).append(" (").append(indexInfos[1].replace(";", ",").toUpperCase()).append(")");
						}
					}
					udipPriIndexInfo.setKeyColumn(indexInfos[1].toUpperCase());
					udipPriIndexInfo.setTableId(tableInfo.getTableId());
					udipPriIndexInfo.setId(RandomUtils.uuid2());

					if (StringUtils.isNotBlank(updateTempTableSql.toString()) && updateTempTableSql.length() > 0) {
						state.execute(updateTempTableSql.toString());
						updateTempTableSql = updateTempTableSql.delete(0, updateTempTableSql.length());// ????????????????????????
					}
					if (StringUtils.isNotBlank(updateTableSql.toString()) && updateTableSql.length() > 0) {
						if (hasTable) {
							state.execute(updateTableSql.toString());
						}
						this.priIndexBS.saveEntity(udipPriIndexInfo);
						if (UdipConstants.TAB_PRIMARY.equals(indexInfos[0])) {
							String[] columns = indexInfos[1].split(";");
							for (int n = 0; n < columns.length; n++) {
								List<RptInputListTableFieldInf> udipTableColInfoList = this.getTableColInfoByIdAndCol(tableInfo.getTableId(), "'" + columns[n] + "'");
								if (udipTableColInfoList != null && udipTableColInfoList.size() > 0) {
									udipTableColInfoList.get(0).setAllowNull("false");
									this.tableColBS.saveOrUpdateEntity(udipTableColInfoList.get(0));// ????????????????????????????????????????????????
								}
							}
						}
						updateTableSql = updateTableSql.delete(0, updateTableSql.length());// ???????????????????????????
					}
				}
			}
		} catch (SQLException e) {
			log.error(e);
			return e.toString();
		}
		return null;
	}
	
	/**
	 * ??????????????????????????????????????????????????????????????????????????????????????????
	 * @param dsId
	 * @param tableName
	 * @param colName
	 * @return
	 */
	public String checkNullByTable(Statement state,String dsId,String tableName,String colName){
		//nullable???Y????????????????????????N?????????????????????
		String nullable=null;
		ResultSet rs = null;
		String sql="select nullable from user_tab_columns where upper(table_name) ='"+tableName.toUpperCase()+"' and upper(column_name)='"+colName.toUpperCase()+"'";
		try {
			rs = state.executeQuery(sql);
			if(rs!=null){
				while(rs.next()){
					nullable=rs.getString("nullable");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return nullable;
	}

	public List<CommonTreeNode> listtables(String tableName) {
		List<CommonTreeNode> result = new ArrayList<>();
		CommonTreeNode root = new CommonTreeNode();
		root.setId("0");
		root.setUpId("ROOT");
		root.setText("???????????????");
		result.add(root);

		Map<String,Object> params = new HashMap<>();
		params.put("tableName", "%" + tableName.toUpperCase() + "%");

		List<Map<String, Object>> list = tableDao.queryInputTable(params);
		for (Map<String, Object> map : list) {
			CommonTreeNode node = new CommonTreeNode();
			node.setId(map.get("TEMPLATE_ID").toString());
			node.setText(map.get("TABLE_CN_NAME").toString());
			node.setUpId("0");
			result.add(node);
		}
		return result;
	}

    private class fieldVO{
		private String updateFieldName;
		private String isId;
		public String getUpdateFieldName() {
			return updateFieldName;
		}
		public void setUpdateFieldName(String updateFieldName) {
			this.updateFieldName = updateFieldName;
		}
		public String getIsId() {
			return isId;
		}
		public void setIsId(String isId) {
			this.isId = isId;
		}
	}
	public Map<String, Object> getColumnInfoByDsAndTable(String dsId,String tableNm,String templeId){
		Map<String,fieldVO>fieldMap = null;
		List<RptInputLstTempleField> templeFieldList = null;
		
		if(StringUtils.isNotEmpty(templeId))
		{
			//???RPT_INPUT_REWRITE_FIELD_INFO ??????????????? ????????????????????????????????????????????? 20191028
			fieldMap = Maps.newHashMap();
			List<Map<String,Object>>tempMap = tableDao.getTableInfoByTempleId(templeId);
			for(Map<String,Object>objMap:tempMap){
				fieldVO vo = new fieldVO();
				vo.setIsId(objMap.get("IS_ID")==null?"":objMap.get("IS_ID").toString());
				vo.setUpdateFieldName(objMap.get("UPDATE_FIELD_NAME")==null?"":objMap.get("UPDATE_FIELD_NAME").toString());
				fieldMap.put(objMap.get("FIELD_NAME").toString(), vo);
			}
			//?????????????????????????????????????????????????????????
			if(fieldMap.size() == 0){
				templeFieldList = templeFieldBS.getTempleColumns(templeId);
			}
		}
		
		//?????????????????????????????????
		Map<String,Object>rsMap=Maps.newHashMap();
		BioneDsInfo dsInfo = dataSourceBS.findDataSourceById(dsId);
		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;
		try {
			conn = this.dataSourceBS.getConnection(dsId);
			state = conn.createStatement();
			String dbType = DialectUtils.getDataBaseType(conn, false);
			String querySql = getQueryColumnSql(dsInfo.getConnUser(), dbType,tableNm,dsInfo.getDbname());
			rs = state.executeQuery(querySql);
			List<Map<String,Object>> colList=Lists.newArrayList();
			while(rs.next()){
				Map<String,Object>map=Maps.newHashMap();
				String columnNm = (String) rs.getObject("COLUMN_NAME");
				map.put("columnNm",  columnNm);
				map.put("dataType",  rs.getObject("DATA_TYPE"));
				map.put("dataLength",  rs.getObject("DATA_LENGTH"));
				String dateType = (String) rs.getObject("DATA_TYPE");
                if ("int".equals(dateType) || "bigint".equals(dateType) || "tinyint".equals(dateType) || "smallint".equals(dateType)) {
                    String dateLength = (String) rs.getObject("DATA_LENGTH");
                    map.put("dataLength", getNumber(dateLength));
                }
				map.put("dataPercision", rs.getObject("DATA_PRECISION"));
				map.put("dataScan",  rs.getObject("DATA_SCALE"));
				map.put("nullAble",  rs.getObject("NULLABLE"));
				map.put("comments",  rs.getObject("COMMENTS"));
				map.put("isprimary",  rs.getObject("ISPRIMARY"));
				//??????????????????????????????????????????????????????
				if(fieldMap != null && fieldMap.size() >0){
					fieldVO fieldVO = fieldMap.get(columnNm);
					if(fieldVO!=null)
					{
						map.put("updFieldName", fieldVO.getUpdateFieldName());
						String isId = fieldVO.getIsId();
						map.put("isprimary",  isId);
					}
					//???????????????????????????????????????????????????????????????????????????????????????????????????????????????RPT_INPUT_REWRITE_FIELD_INFO 20191028
				}else if(fieldMap !=null && fieldMap.size()==0 && templeFieldList !=null && templeFieldList.size() > 0){
					for(RptInputLstTempleField templeField : templeFieldList) {
						if(columnNm.equals(templeField.getFieldEnName())) {
							map.put("updFieldName", templeField.getFieldEnName());
							map.put("updFieldType", templeField.getFieldType());
							map.put("isprimary",  "0");
						};
					}
				}
				colList.add(map);
			}
			rsMap.put("Rows", colList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.dataSourceBS.releaseConnection(rs, state, conn);
		}
		return rsMap;
	}

	private int getNumber(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return Integer.valueOf(m.replaceAll("").trim());
	}
	
	private String getQueryColumnSql(String  schemaNm,String dbType,String tableNm, String dbName){
		if(dbType.equals("oracle"))
			return getOracleColumnSql(tableNm);
		else if(dbType.equals("db2"))
			return getDB2ColumnSql(schemaNm,tableNm);
		else if(dbType.equals("mysql"))
			return getMysqlColumnSql(tableNm,dbName);
		else if(dbType.equals("postgresql"))
			return getPostgresqlColumnSql(tableNm,dbName);
		return null;
	}

	private String getPostgresqlColumnSql(String tableNm, String dbName){
		tableNm = tableNm.toLowerCase();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT s.column_name AS \"COLUMN_NAME\",COALESCE(col_description(c.oid, ordinal_position), s.column_name) AS \"COMMENTS\",")
				.append(" s.data_type as \"DATA_TYPE\",s.CHARACTER_MAXIMUM_LENGTH AS \"DATA_LENGTH\",s.NUMERIC_PRECISION AS \"DATA_PRECISION\",s.NUMERIC_SCALE AS \"DATA_SCALE\",")
				.append(" s.IS_NULLABLE AS \"NULLABLE\",CASE WHEN s.column_name IN(SELECT pg_attribute.attname AS colname FROM pg_constraint ")
				.append(" INNER JOIN pg_class  ON pg_constraint.conrelid = pg_class.oid INNER JOIN pg_attribute  ON pg_attribute.attrelid = pg_class.oid")
				.append(" AND pg_attribute.attnum = pg_constraint.conkey[1] WHERE pg_constraint.contype = 'p' AND pg_table_is_visible(pg_class.oid)")
				.append(" AND pg_class.relname = '").append(tableNm)
				.append("') THEN '1' ELSE '0' END AS \"ISPRIMARY\" FROM information_schema.columns s, pg_class c")
				.append(" WHERE s.table_name = c.relname AND s.table_schema = current_schema() AND s.table_name = '")
				.append(tableNm).append("' order by s.table_name, s.column_name");
		return sb.toString();
	}

	private String getMysqlColumnSql(String tableNm, String dbName) {
		tableNm = tableNm.toUpperCase();
		StringBuilder buff = new StringBuilder();
		buff.append(" SELECT COL.COLUMN_NAME, COL.DATA_TYPE,case when COL.data_type='decimal' then COL.NUMERIC_PRECISION when COL.data_type='int' then COL.COLUMN_TYPE when COL.data_type='tinyint' then COL.COLUMN_TYPE when COL.data_type='bigint' then COL.COLUMN_TYPE when COL.data_type='smallint' then COL.COLUMN_TYPE  else COL.CHARACTER_MAXIMUM_LENGTH end DATA_LENGTH,"
				+ "COL.NUMERIC_PRECISION DATA_PRECISION,COL.NUMERIC_SCALE DATA_SCALE,COL.IS_NULLABLE NULLABLE, COL.COLUMN_COMMENT COMMENTS,"
				+ "CASE WHEN EXISTS (SELECT N.column_name FROM INFORMATION_SCHEMA.`KEY_COLUMN_USAGE` N WHERE COL.COLUMN_NAME = N.COLUMN_NAME AND N.table_name = '"+ tableNm +"' AND N.constraint_name = 'PRIMARY') THEN 1 ELSE 0 END ISPRIMARY  FROM information_schema.`COLUMNS` COL ")
			.append(" WHERE TABLE_SCHEMA = '").append(dbName).append("' ")
			.append(" AND TABLE_NAME = '").append(tableNm).append("'");
		return buff.toString();
	}


	private String getOracleColumnSql(String tableNm){
		tableNm = tableNm.toUpperCase();
		StringBuilder buff = new StringBuilder();
		buff.append(" WITH N(COLUMN_NAME) AS (SELECT COLUMN_NAME  FROM User_Constraints CONS, User_Cons_Columns COLS WHERE CONS.constraint_name = COLS.constraint_name ")
			.append(" AND COLS.table_name='").append(tableNm).append("'   AND CONS.table_name = '").append(tableNm).append("'   AND CONS.constraint_type = 'P')  ")
			.append(" SELECT COL.COLUMN_NAME,COL.DATA_TYPE,case when col.data_type='VARCHAR2' then col.CHAR_LENGTH else col.DATA_LENGTH end DATA_LENGTH,COL.DATA_PRECISION,COL.DATA_SCALE,COL.NULLABLE,CMT.COMMENTS, ")
			.append(" CASE WHEN EXISTS (SELECT 1 FROM N WHERE COL.COLUMN_NAME = N.COLUMN_NAME) THEN 1 ELSE 0 END ISPRIMARY ")
			.append(" FROM USER_TAB_COLUMNS COL, USER_COL_COMMENTS CMT  ")
			.append(" WHERE COL.COLUMN_NAME = CMT.COLUMN_NAME  ")
			.append(" AND COL.TABLE_NAME = '").append(tableNm).append("' ")
			.append(" AND CMT.TABLE_NAME = '").append(tableNm).append("'  ")
			.append(" ORDER BY COL.COLUMN_ID");
		/*
		buff.append(" SELECT COL.COLUMN_NAME,COL.DATA_TYPE,COL.DATA_LENGTH,COL.DATA_PRECISION,COL.DATA_SCALE,COL.NULLABLE,CMT.COMMENTS ")
			.append(" FROM USER_TAB_COLUMNS COL, USER_COL_COMMENTS CMT ")
			.append(" WHERE COL.COLUMN_NAME = CMT.COLUMN_NAME ")
			.append(" AND COL.TABLE_NAME = '").append(tableNm).append("'")
			.append(" AND CMT.TABLE_NAME = '").append(tableNm).append("' ");
		*/
		 return buff.toString();
	}
	
	private String getDB2ColumnSql(String schemaNm,String tableNm){
		schemaNm = schemaNm.toUpperCase();
		tableNm = tableNm.toUpperCase();
		StringBuilder buff = new StringBuilder();
		buff.append(" WITH N (COLNAME)  AS  (SELECT     CLOUSE.COLNAME FROM  syscat.tabconst const,syscat.keycoluse clouse WHERE const.TABNAME='").append(tableNm).append("' AND const.TABSCHEMA='").append(schemaNm).append("' ") 
			.append("   AND  CLOUSE.CONSTNAME = CONST.CONSTNAME AND CLOUSE.TABNAME='").append(tableNm).append("' AND  CLOUSE.TABSCHEMA='").append(schemaNm).append("') ")
			.append(" SELECT T.COLUMN_NAME,  COL.TYPENAME DATA_TYPE,COL.LENGTH DATA_LENGTH,COL.LENGTH DATA_PRECISION,COL.SCALE DATA_SCALE, ")
			.append(" T.IS_NULLABLE NULLABLE,COL.REMARKS AS COMMENTS ,CASE WHEN EXISTS(SELECT 1 FROM N WHERE N.COLNAME=T.COLUMN_NAME)  THEN 1 ELSE 0 END ISPRIMARY ")
			.append(" FROM SYSIBM.COLUMNS T,SYSCAT.COLUMNS COL ")
			.append("  WHERE T.TABLE_SCHEMA= COL.TABSCHEMA AND T.TABLE_NAME=COL.TABNAME AND T.COLUMN_NAME = COL.COLNAME ")
			.append("   AND T.TABLE_SCHEMA='").append(schemaNm).append("' AND  T.TABLE_NAME='").append(tableNm).append("'  ORDER BY COL.COLNO ");
		/*
		buff.append(" SELECT T.COLUMN_NAME,  COL.TYPENAME DATA_TYPE,COL.LENGTH DATA_LENGTH,COL.LENGTH DATA_PRECISION,COL.SCALE DATA_SCALE,T.IS_NULLABLE NULLABLE,COL.REMARKS AS COMMENTS ")
			.append(" FROM SYSIBM.COLUMNS T,SYSCAT.COLUMNS COL  ")
			.append(" WHERE T.TABLE_SCHEMA= COL.TABSCHEMA AND T.TABLE_NAME=COL.TABNAME AND T.COLUMN_NAME = COL.COLNAME ")
			.append(" AND T.TABLE_SCHEMA='").append(schemaNm.toUpperCase()).append("' AND T.TABLE_NAME='").append(tableNm.toUpperCase()).append("' ");
		*/
		return buff.toString();
	}
	
	public List<Map<String, String>>getColumnType(String dsId){
		String dbType = this.dataSourceBS.getDataSourceType(dsId);

		if(dbType.equals("oracle"))
			return getOracleColumnType();
		else
			return getDB2ColumnType();
	}
	private List<Map<String,String>>getOracleColumnType(){
		List<Map<String,String>>colType = Lists.newArrayList();
		
		Map<String, String> colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_CHAR);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_CHAR);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_NUMBER);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_NUMBER);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_VARCHAR2);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_VARCHAR2);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_TIMESTAMP);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_TIMESTAMP);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_DATE);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_DATE);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_INTEGER);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_INTEGER);
		colType.add(colTypeMap);
		
		return colType;
	}

	private List<Map<String,String>>getDB2ColumnType(){

		List<Map<String,String>>colType = Lists.newArrayList();
		
		Map<String, String> colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_CHAR);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_CHAR);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_DECIMAL);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_DECIMAL);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_VARCHAR);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_VARCHAR);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_TIMESTAMP);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_TIMESTAMP);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_DATE);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_DATE);
		colType.add(colTypeMap);
		colTypeMap = Maps.newHashMap();
		colTypeMap.put(UdipConstants.ID, UdipConstants.TAB_INTEGER);
		colTypeMap.put(UdipConstants.TEXT, UdipConstants.TAB_INTEGER);
		colType.add(colTypeMap);
		
		return colType;
	}

	/** ??????????????????????????????????????????sql
	 *  0 : ????????????????????????,?????????????????????
	 *	1 : ????????????
	 *	2 : ????????????????????????
	 *	3 : ???????????????????????????
	 * @param templateId ??????ID
	 * @param taskInstanceId ????????????ID
	 * @return
	 */
	@Transactional(readOnly = false)
	public String reWriteData(String templateId,String taskInstanceId) {
		RptInputRewriteTempleInfo tempInfo = tableDao.getRptInputRewriteTempleInfoByTempId(templateId);
		if(tempInfo == null )
			return "?????????????????????,?????????";
		HashMap<String,Object> sourceTblInfMap = tableDao.getRptInputListTableInfoByTempId(templateId);
		String sourceTable = (String) sourceTblInfMap.get("DBNAME")+"."+(String) sourceTblInfMap.get("TABLE_EN_NAME");
		List<RptInputRewriteFieldInfo> infoList = tableDao.getRptInputRewriteFieldInfo(templateId);

		String updateType = tempInfo.getUpdateType();
		String dsId = tempInfo.getId().getDsId();
		String dbType = this.dataSourceBS.getDataSourceType(dsId);
		List<BioneTableMetaData>tables = this.dataSourceBS.getTableMetaData(dsId, tempInfo.getTableName());
		//getTableMetaData ???????????????????????????get(0)??????????????????????????? 20190531
		//BioneTableMetaData metaData = tables.get(0);
		BioneTableMetaData metaData = null;
		for(BioneTableMetaData tableMeta : tables) {
			if(tempInfo.getTableName().equals(tableMeta.getTableName())) {
				metaData = tableMeta;
				break;
			}
		}
		return reWriteUtil.executeReWrite(tempInfo.getId().getDsId(),updateType,dbType,metaData.getSchemaName(),metaData.getTableName(),sourceTable,infoList,taskInstanceId);
	}	
	
	
	public void saveDataLoadSql(Object obj) throws Exception{
		TableSqlVO tableSqlVO = (TableSqlVO)obj;
		RptListDataloadSqlInfo rptListDataloadTypeSql = new RptListDataloadSqlInfo();
		rptListDataloadTypeSql.setCfgId(tableSqlVO.getCfgId());
		rptListDataloadTypeSql.setSql2(tableSqlVO.getSqlText());
		rptListDataloadTypeSql.setDsId(tableSqlVO.getDsId());
		try{
				tableDao.saveRptListDataloadSqlInfo(rptListDataloadTypeSql);
		}catch(Exception ex){
			logger.error(ex.getMessage());
			throw new Exception(ex);
		}
	}
	
	
	public void saveDataLoadData(Object obj) throws Exception{
		TableDataVO tableDataVO = (TableDataVO)obj;
		RptListDataloadDataInfo rptListDataloadDataInfo = new RptListDataloadDataInfo();
		rptListDataloadDataInfo.setCfgId(tableDataVO.getCfgId());
		try {
			String tmpData=URLDecoder.decode(URLDecoder.decode(tableDataVO.getDataContent(), "UTF-8"), "UTF-8");
			System.out.println("============================sunyp==============="+tmpData);
			rptListDataloadDataInfo.setDataContent((URLDecoder.decode(URLDecoder.decode(tableDataVO.getDataContent(), "UTF-8"), "UTF-8")).getBytes());
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		try{
			tableDao.saveRptListDataloadDataInfo(rptListDataloadDataInfo);
		}catch(Exception ex){
			logger.error(ex.getMessage());
			throw new Exception(ex);
		}
	}
	
	private String checkSql(String sql,String tableId,String dsId){
		sql = StringUtils.replace(sql, "@{dataDate}", "'20991231'");
		sql = StringUtils.replace(sql, "@{orgNo}", "''");
	    sql = sql.toUpperCase();
	    
	    Connection conn = null;
	    Statement ps  = null;
	    ResultSet rs = null;
	    try {
	        conn = dataSourceBS.getConnection(dsId);
	        ps = conn.createStatement();
	        String excuteSql = "select  count(1) from (\n " + sql + " \n) t ";
	        logger.info("excuteSql--->"+excuteSql);
	        rs = ps.executeQuery(excuteSql);
        } catch (Exception e) {
            return e.getMessage();
        }finally{
            dataSourceBS.releaseConnection(rs, ps, conn);
        }
	    
		int pif = sql.indexOf("from");
		int piF = sql.indexOf("FROM");
		if((pif==-1||pif==0)&&(piF==-1||piF==0))
			return "?????????sql??????";
		int pi = -1;
		if(pif==0||pif==-1)
			pi = piF;
		else if (piF==0||piF==-1)
			pi = pif;
		else {
			if(pif>piF)
				pi = piF;
			else pi = pif;
		}
		
		String sqlSelectPart = sql.substring(0, pi);
		Pattern pattern2 = Pattern.compile("(as|AS|aS|As)\\s+[a-zA-Z0-9_]*(\\s|,)");
		Matcher matcher2 = pattern2.matcher(sqlSelectPart);
		List<String>values=Lists.newArrayList();
		while(matcher2.find()){
			String v = matcher2.group();
			values.add(v.replace("AS ", "").replace(",", "").trim());
		}
		List<RptInputListTableFieldInf>fieldList=getTableColInfoById(tableId,null);
		for(RptInputListTableFieldInf field:fieldList){
			if(field.getAllowNull().equals("false")&&!checkIsNull1(field.getFieldEnName(),values))
				return "??????:"+field.getFieldEnName()+(StringUtils.isEmpty(field.getFieldCnName())?"":"["+field.getFieldCnName()+"]")+"????????????,???????????????sql";
		}
		for(String v :values){
			String fieldNm = checkIsNull2(v,fieldList);
			if(StringUtils.isNotEmpty(fieldNm)){
			    if(!"FILTER_NM".equals(fieldNm) && !"FILTER_NO".equals(fieldNm)){
			        return "??????["+fieldNm+"]?????????????????????,???????????????sql";
			    }
			}
		}
		return "1";
	}
	
	private String checkIsNull2(String checkField,List<RptInputListTableFieldInf>fieldList){
		for(RptInputListTableFieldInf inf:fieldList){
			if(inf.getFieldEnName().equalsIgnoreCase(checkField))
				return null;
		}
		return checkField;
		
	}
	
	private boolean checkIsNull1(String checkField,List<String>fieldList){
		for(String field:fieldList){
			if(field.equalsIgnoreCase(checkField))
				return true;
		}
		return false;
	}
	
	
	public String saveDataLoadInfo(Object obj,String dataLoadtype){
		//1:sql 2:data
		if(dataLoadtype.equals("1"))
		{
			//sql??????
			TableSqlVO tableSqlVO = (TableSqlVO)obj;
			String rs = checkSql(tableSqlVO.getSqlText(),tableSqlVO.getTableId(),tableSqlVO.getDsId());
			if(!rs.equals("1"))
				return rs;
		}
		TableVO tableVO = (TableVO)obj;
		String updateType = "modify";
		String delId = tableVO.getCfgId();
		tableVO.setCfgId(RandomUtils.uuid2());
		if(StringUtils.isEmpty(tableVO.getTypeId()))
		{
			updateType = "add";
			tableVO.setTypeId(RandomUtils.uuid2());
		}
		try{
			RptListDataloadType type = getTypeInfo(tableVO);
			if(updateType.equals("add"))
				tableDao.saveRptListDataloadType(type);
			else
				tableDao.updateRptListDataloadType(type);
			if(updateType .equals("modify")){
				Map<String,Object>params=Maps.newHashMap();
				params.put("cfgId", delId);
				System.out.println("====================begin sunyp update========"+params.get("cfgId"));
				tableDao.deleteRptListDataloadSqlInfo(params);
				tableDao.deleteRptListDataloadDataInfo(params);
				System.out.println("====================end  sunyp update========");
			}
			if(dataLoadtype.equals("1")){
				saveDataLoadSql(obj);
			}else
				saveDataLoadData(obj);

			
			/***********????????????????????????????????????************/
//			String saveMark = "03";//??????
//			//String saveMark = "????????????";
//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			Map<String,Object>m=Maps.newHashMap();
//			m.put("tableId", tableVO.getTableId());
//			List<InputTableVO> t = tableDao.getTableInfoById(m);
//			StringBuilder buff = new StringBuilder();
//			buff.append("??????[").append(user.getLoginName()).append("]").append("???????????????:").append(t.get(0).getTableEnName()).append("???????????????");
//			saveLog(saveMark, "???????????????-?????????????????? ", buff.toString(), user.getUserId(), user.getLoginName());
		}catch(Exception ex){
			logger.error(ex.getMessage());
			return ex.getMessage();
		}
		return "1";
	}
	
	public RptListDataloadSqlInfo getSqlInfo(String cfgId){
		return tableDao.getRptListDataloadSqlInfoById(cfgId);
	}
	public List<RptListDataloadSqlInfo> getSqlInfoByTaskId(String cfgId){
		return tableDao.getRptListDataloadSqlInfoByTaskId(cfgId);
	}
	
	public Map<String, Object> getSqlInfoVO(String cfgId){
		RptListDataloadSqlInfo d = tableDao.getRptListDataloadSqlInfoById(cfgId);
		Map<String, Object> resMap = tableDao.getRptListDataloadSqlInfoVOById(cfgId);
		if(resMap != null && d != null){
			resMap.put("SQL", d.getSql2());
		}
        return resMap;
    }
	
	
	public String getDataInfo(String cfgId){
		if(StringUtils.isNotEmpty(cfgId)){
			RptListDataloadDataInfo dataInfo =  tableDao.getRptListDataloadDataInfoById(cfgId);
			if(dataInfo!=null&&dataInfo.getDataContent()!=null)
			{
				String ctx = new String( dataInfo.getDataContent());
				if(ctx.equals("undefined"))
					return "";
				return ctx;
			}
		}
		return "";
	}
	
	
	public RptListDataloadType getTypeInfo(TableVO tableVO){
		RptListDataloadType type = new RptListDataloadType();
		type.setCfgId(tableVO.getCfgId());
		type.setRemark(tableVO.getRemark());
		type.setTableId(tableVO.getTableId());
		type.setType(tableVO.getType());
		type.setTypeId(tableVO.getTypeId());
		return type;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,String>> getTableSpace(String dsId,Pager rf) {
		if(StringUtils.isEmpty(dsId))
			return null;
		List<Map<String,String>> nodeList=Lists.newArrayList();
		Map<String,Object>map = rf.getSearchCondition();
		String tableSpaceNm = null;
		if(map!=null&&!map.isEmpty()){
			Object fieldValues = map.get("fieldValues");
			if(fieldValues!=null&&!fieldValues.equals("")){
				Map<String,Object>o= (Map<String, Object>) fieldValues;
				tableSpaceNm = (String) o.get("tableSpaceNm");
			}
		}
		List<String>list = dataUtils.getTableSpace(dsId,tableSpaceNm);
		for(String value:list){
			Map<String,String>m = Maps.newHashMap();
			m.put("tableSpaceNm", value);
			nodeList.add(m);
		}
		return nodeList;
	}

	/**
	 * ??????????????????sql
	 * @param newTableCol
	 * @param oldTableCol 
	 * @return
	 */
	public String getUpdateColSql(RptInputListTableFieldInf newTableCol, RptInputListTableFieldInf oldTableCol, RptInputListTableInfo tableInfo) {
		StringBuilder updateSql = new StringBuilder();
		String dsName = this.dataSourceBS.getSchemaByDsId(tableInfo.getDsId()); //?????????
		String dbType = this.dataSourceBS.getDataSourceType(tableInfo.getDsId()); //???????????????
		String tableEnName = tableInfo.getTableEnName(); //????????????
		String fieldEnName = newTableCol.getFieldEnName(); //???????????????
		String fieldCnName = newTableCol.getFieldCnName(); //???????????????
		String fieldType = newTableCol.getFieldType(); //????????????
		String fieldLength = newTableCol.getFieldLength(); //????????????
		String decimalLength = StringUtils.isBlank(newTableCol.getDecimalLength()) ? "0" : newTableCol.getDecimalLength(); //?????????
		String defaultValue = StringUtils.isBlank(newTableCol.getDefaultValue()) ? "" : newTableCol.getDefaultValue(); //?????????
		String colType = getColType(fieldType, fieldLength, decimalLength);
		if("mysql".equalsIgnoreCase(dbType)){
			if(!fieldCnName.equals(oldTableCol.getFieldCnName()) || !fieldType.equals(oldTableCol.getFieldType())
					|| !fieldLength.equals(oldTableCol.getFieldLength()) || !decimalLength.equals(oldTableCol.getDecimalLength() == null ? "0" : oldTableCol.getDecimalLength())){
				updateSql.append("alter table "+ dsName +"."+ tableEnName + " modify column `" + fieldEnName + "` " + colType);
				if(!defaultValue.equals(StringUtils.isBlank(oldTableCol.getDefaultValue()) ? "" : oldTableCol.getDefaultValue())){
					updateSql.append(" default '"+ defaultValue + "'");
				}
				updateSql.append(" comment '" + fieldCnName + "';");
				updateSql.append("\n");
				updateSql.append("alter table "+ dsName +"."+ tableEnName + "_delete modify column `" + fieldEnName + "` " + colType);
				if(!defaultValue.equals(StringUtils.isBlank(oldTableCol.getDefaultValue()) ? "" : oldTableCol.getDefaultValue())){
					updateSql.append(" default '"+ defaultValue + "'");
				}
				updateSql.append(" comment '" + fieldCnName + "';");
				updateSql.append("\n");
				updateSql.append("alter table "+ dsName +"."+ tableEnName + "_validate modify column `" + fieldEnName + "` " + colType);
				if(!defaultValue.equals(StringUtils.isBlank(oldTableCol.getDefaultValue()) ? "" : oldTableCol.getDefaultValue())){
					updateSql.append(" default '"+ defaultValue + "'");
				}
				updateSql.append(" comment '" + fieldCnName + "';");
				updateSql.append("\n");
			}
		}else{
			//???????????????
			if(!fieldCnName.equals(oldTableCol.getFieldCnName())){
				updateSql.append("comment on column "+ dsName +"."+ tableEnName + "." + fieldEnName + " is '" + fieldCnName + "';");
				updateSql.append("\n");
				updateSql.append("comment on column "+ dsName +"."+ tableEnName + "_delete." + fieldEnName + " is '" + fieldCnName + "';");
				updateSql.append("\n");
				updateSql.append("comment on column "+ dsName +"."+ tableEnName + "_validate." + fieldEnName + " is '" + fieldCnName + "';");
				updateSql.append("\n");
			}
			//??????????????????????????????????????????
			if(!fieldType.equals(oldTableCol.getFieldType()) || !fieldLength.equals(oldTableCol.getFieldLength()) 
					|| !decimalLength.equals(oldTableCol.getDecimalLength() == null ? "0" : oldTableCol.getDecimalLength())){
				updateSql.append("alter table "+ dsName +"."+ tableEnName + " modify " + fieldEnName + " " + colType);
				if(!defaultValue.equals(StringUtils.isBlank(oldTableCol.getDefaultValue()) ? "" : oldTableCol.getDefaultValue())){
					updateSql.append(" default '"+ defaultValue + "'");
				}
				updateSql.append(";");
				updateSql.append("\n");
				updateSql.append("alter table "+ dsName +"."+ tableEnName + "_delete modify " + fieldEnName + " " + colType);
				if(!defaultValue.equals(StringUtils.isBlank(oldTableCol.getDefaultValue()) ? "" : oldTableCol.getDefaultValue())){
					updateSql.append(" default '"+ defaultValue + "'");
				}
				updateSql.append(";");
				updateSql.append("\n");
				updateSql.append("alter table "+ dsName +"."+ tableEnName + "_validate modify " + fieldEnName + " " + colType);
				if(!defaultValue.equals(StringUtils.isBlank(oldTableCol.getDefaultValue()) ? "" : oldTableCol.getDefaultValue())){
					updateSql.append(" default '"+ defaultValue + "'");
				}
				updateSql.append(";");
				updateSql.append("\n");
			}
		}
		return updateSql.toString();
	}

	/**
	 * ??????????????????sql
	 * @param fieldEnName
	 * @return
	 */
	public String getDropColSql(String fieldEnName, RptInputListTableInfo tableInfo) {
		StringBuilder updateSql = new StringBuilder();
		String dsName = this.dataSourceBS.getSchemaByDsId(tableInfo.getDsId()); //?????????
		String tableEnName = tableInfo.getTableEnName(); //????????????
		updateSql.append("alter table " + dsName + "." + tableEnName + " drop column "+ fieldEnName +";");
		updateSql.append("\n");
		updateSql.append("alter table " + dsName + "." + tableEnName + "_delete drop column "+ fieldEnName +";");
		updateSql.append("\n");
		updateSql.append("alter table " + dsName + "." + tableEnName + "_validate drop column "+ fieldEnName +";");
		updateSql.append("\n");
		return updateSql.toString();
	}

	/**
	 * ??????????????????sql
	 * @param newTableCol
	 * @param dsName 
	 * @return
	 */
	public String getAddColSql(RptInputListTableFieldInf newTableCol, RptInputListTableInfo tableInfo) {
		StringBuilder updateSql = new StringBuilder();
		String dsName = this.dataSourceBS.getSchemaByDsId(tableInfo.getDsId()); //?????????
		String dbType = this.dataSourceBS.getDataSourceType(tableInfo.getDsId()); //???????????????
		String tableEnName = tableInfo.getTableEnName(); //????????????
		String fieldEnName = newTableCol.getFieldEnName(); //???????????????
		String fieldCnName = newTableCol.getFieldCnName(); //???????????????
		String fieldType = newTableCol.getFieldType(); //????????????
		String fieldLength = newTableCol.getFieldLength(); //????????????
		String decimalLength = newTableCol.getDecimalLength(); //?????????
		String defaultValue = newTableCol.getDefaultValue(); //?????????
		String colType = getColType(fieldType, fieldLength, decimalLength);
		if("mysql".equalsIgnoreCase(dbType)){
			updateSql.append("alter table " + dsName + "." + tableEnName + " add column `"+ fieldEnName +"` "+ colType);
			if(StringUtils.isNotBlank(defaultValue)){
				updateSql.append(" default '"+ defaultValue + "' ");
			}
			updateSql.append(" comment '"+ fieldCnName +"';");
			updateSql.append("\n");
			updateSql.append("alter table " + dsName + "." + tableEnName + "_delete add column `"+ fieldEnName +"` "+ colType);
			if(StringUtils.isNotBlank(defaultValue)){
				updateSql.append(" default '"+ defaultValue + "' ");
			}
			updateSql.append(" comment '"+ fieldCnName +"';");
			updateSql.append("\n");
			updateSql.append("alter table " + dsName + "." + tableEnName + "_validate add column `"+ fieldEnName +"` "+ colType);
			if(StringUtils.isNotBlank(defaultValue)){
				updateSql.append(" default '"+ defaultValue + "' ");
			}
			updateSql.append(" comment '"+ fieldCnName +"';");
			updateSql.append("\n");
		}else{
			updateSql.append("alter table " + dsName + "." + tableEnName + " add "+ fieldEnName +" "+ colType);
			if(StringUtils.isNotBlank(defaultValue)){
				updateSql.append(" default '"+ defaultValue + "'");
			}
			updateSql.append(";");
			updateSql.append("\n");
			updateSql.append("alter table " + dsName + "." + tableEnName + "_delete add "+ fieldEnName +" "+ colType);
			if(StringUtils.isNotBlank(defaultValue)){
				updateSql.append(" default '"+ defaultValue + "'");
			}
			updateSql.append(";");
			updateSql.append("\n");
			updateSql.append("alter table " + dsName + "." + tableEnName + "_validate add "+ fieldEnName +" "+ colType);
			if(StringUtils.isNotBlank(defaultValue)){
				updateSql.append(" default '"+ defaultValue + "'");
			}
			updateSql.append(";");
			updateSql.append("\n");
			if(StringUtils.isNotBlank(fieldCnName)){
				updateSql.append("comment on column "+ dsName +"."+ tableEnName + "." + fieldEnName + " is '" + fieldCnName + "';");
				updateSql.append("\n");
				updateSql.append("comment on column "+ dsName +"."+ tableEnName + "_delete." + fieldEnName + " is '" + fieldCnName + "';");
				updateSql.append("\n");
				updateSql.append("comment on column "+ dsName +"."+ tableEnName + "_validate." + fieldEnName + " is '" + fieldCnName + "';");
				updateSql.append("\n");
			}
		}
		return updateSql.toString();
	}


	/**
	 * ??????????????????SQL
	 * @param priIndex
	 * @param tableInfo
	 * @return
	 */
	public String getAddPriIndexSql(RptInputListTableConstraint priIndex, RptInputListTableInfo tableInfo) {
		String dsName = this.dataSourceBS.getSchemaByDsId(tableInfo.getDsId()); //?????????
		String dbType = this.dataSourceBS.getDataSourceType(tableInfo.getDsId()); //???????????????
		StringBuilder updateSql = new StringBuilder();
		String getKeyColumn = priIndex.getKeyColumn();
		if("mysql".equals(dbType)){
			if (UdipConstants.TAB_UNIQUE.equals(priIndex.getKeyType())) {
				updateSql.append("alter table ").append(dsName + "." +tableInfo.getTableEnName()).append(" add constraint ")
						.append(" unique (").append(getKeyColumn.replace(";", ","))
						.append(")");
				updateSql.append(";").append("\n");
			} else if (UdipConstants.TAB_INDEX.equals(priIndex.getKeyType())) {
				updateSql.append("create index ").append(tableInfo.getTableEnName()).append(" on ").append(dsName + "." + tableInfo.getTableEnName().trim())
						.append(" (").append(getKeyColumn.replace(";", ",")).append(");").append("\n");
			}
		}else{
			if (UdipConstants.TAB_UNIQUE.equals(priIndex.getKeyType())) {
				updateSql.append("alter table ").append(dsName).append(".").append(tableInfo.getTableEnName()).append(" add constraint ")
						.append(priIndex.getKeyName()).append(" unique (").append(getKeyColumn.replace(";", ","))
						.append(")");
				if(dbType.equals("oracle"))
					updateSql.append(" using index");
				updateSql.append(";").append("\n");
			} else if (UdipConstants.TAB_INDEX.equals(priIndex.getKeyType())) {
				updateSql.append("create index ").append(dsName).append(".").append(tableInfo.getTableEnName()).append(" on ").append(priIndex.getKeyName().trim())
						.append(" (").append(getKeyColumn.replace(";", ",")).append(");").append("\n");
			}
		}
		
		return updateSql.toString();
	}

	/**
	 * ??????????????????SQL
	 * @param priIndex
	 * @param tableInfo
	 * @return
	 */
	public String getDeletePriIndexSql(RptInputListTableConstraint oldPriIndex, RptInputListTableInfo tableInfo) {
		String dsName = this.dataSourceBS.getSchemaByDsId(tableInfo.getDsId()); //?????????
		String dbType = this.dataSourceBS.getDataSourceType(tableInfo.getDsId()); //???????????????
		StringBuilder updateSql = new StringBuilder();
		if("mysql".equals(dbType)){

		}else{
			updateSql.append("alter table ").append(dsName).append(".").append(tableInfo.getTableEnName()).append(" drop constraint ")
			.append(oldPriIndex.getKeyName());
			updateSql.append(";").append("\n");
		}
		return updateSql.toString();
	}

	public List<RptInputListTableInfo> getDataTableList(String dsId) {

		StringBuilder jql = new StringBuilder(1000);
		jql.append("select t from RptInputListTableInfo t where t.logicSysNo =:logicSysNo and dsId=:dsId order by createDate desc");

		Map<String, Object> values = new HashMap<>();
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		values.put("dsId", dsId);

		return this.baseDAO.findWithNameParm(jql.toString(), values);
	}


	/**
	 * @????????????: ?????????????????????
	 * @?????????: huzq1
	 * @????????????: 2021/9/9 11:08
	  * @param pager
	 * @param templateId
	 * @return
	 **/
	public Map<String, Object> queryInputTable(Pager pager, String templateId, boolean isExp) {
		Map<String, Object> result = Maps.newHashMap();
		
		RptInputLstTempleInfo temp = this.getEntityByProperty(RptInputLstTempleInfo.class, "templeId", templateId);
		// ?????????????????????????????????
		List<RptInputLstTempleField> templeFieldList = this.templeFieldBS.findEntityListByPropertyAndOrder("templeId", templateId, "orderNo", false);
		// ??????
		Collections.sort(templeFieldList, new Comparator<RptInputLstTempleField>() {
			@Override
			public int compare(RptInputLstTempleField o1,
							   RptInputLstTempleField o2) {
				return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		List<String> colList = new ArrayList<>();
		List<String> colNmList = new ArrayList<>();
		Map<String,String> colNameType = Maps.newHashMap();
		Map<String, Map<String, Object>> libList = Maps.newHashMap();
		for (RptInputLstTempleField rptInputLstTempleField : templeFieldList) {
			colList.add(rptInputLstTempleField.getFieldEnName());
			colNmList.add(rptInputLstTempleField.getFieldCnName());
			colNameType.put(rptInputLstTempleField.getFieldEnName(), rptInputLstTempleField.getFieldType());
			if (org.apache.commons.lang.StringUtils.isNotBlank(rptInputLstTempleField.getDictId())) {
				// ??????????????????
				List<CommonTreeNode> dictList = this.inputDataDictBS.buildLibTreeByLibId(rptInputLstTempleField.getDictId(), "20201217");
				Map<String, Object> libDataMap = Maps.newHashMap();
				if (CollectionUtils.isNotEmpty(dictList)) {
					for (CommonTreeNode node : dictList) {
						//??????????????????
						libDataMap.put(node.getText(), node.getText());
						libDataMap.put(node.getId(), node.getText());
					}
				}
				libList.put(rptInputLstTempleField.getFieldEnName(), libDataMap);
			}
		}

		colList.add("SYS_DATA_CASE");
		colList.add("FLOW_NODE_ID");
		colList.add("ERROR_MARK");
		colList.add("COMMENTS");
		colList.add("DATAINPUT_ID");
		colList.add("SYS_DATA_DATE");
		colList.add("SYS_OPER_ORG");
		colList.add("SYS_OPERATOR");
		colList.add("SYS_DATA_STATE");

		colNmList.add("????????????ID");
		colNmList.add("????????????ID");
		colNmList.add("????????????");
		colNmList.add("??????");
		colNmList.add("??????");
		colNmList.add("????????????");
		colNmList.add("????????????");
		colNmList.add("????????????");
		colNmList.add("????????????");

		List<String> dataState = Lists.newArrayList();
		dataState.add(UdipConstants.TASK_STATE_SAVE);
		dataState.add(UdipConstants.TASK_STATE_VALIDATE);
		dataState.add(UdipConstants.TASK_STATE_REFUSE);
		dataState.add(UdipConstants.TASK_STATE_DISPATCH);
		dataState.add(UdipConstants.TASK_STATE_SUBMIT);
		dataState.add(UdipConstants.TASK_STATE_SUCESS);

		StringBuilder sql = new StringBuilder();
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			List<String> curOrgNo = new ArrayList<>();
			String orgNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
			curOrgNo.add(orgNo);
			authOrgNos.add(orgNo);
			userBS.getAllChildBioneOrgNo(curOrgNo, authOrgNos);
			List<List<String>> allOrgs = SplitStringBy1000.change(authOrgNos);
			sql.append(" and (");
			for (List<String> allOrg : allOrgs) {
				List<String> collect = allOrg.stream().map(item -> "'" + item + "'").collect(Collectors.toList());
				sql.append(" SYS_OPER_ORG in (").append(StringUtils.join(collect, ",")).append(") or");
			}
			sql.delete(sql.length() - 2, sql.length()).append(")");
		}

		String condition = pager.getCondition();
		if(StringUtils.isNotBlank(condition)) {
			JSONObject obj = JSONObject.parseObject(condition);
			JSONArray array = JSONArray.parseArray(obj.getString("rules"));
			for(int i = 0; i < array.size(); i++){
				JSONObject job = array.getJSONObject(i);
				if(job.getString("field").equalsIgnoreCase("sysDataDate")){
					Date value = FormatUtils.parseDate(job.getString("value"));
					String shortDate = FormatUtils.formatShortDate(value);
					sql.append(" and SYS_DATA_DATE = ").append(shortDate);
				} else if (job.getString("field").equalsIgnoreCase("sysOperOrg")){
					sql.append(" and SYS_OPER_ORG = ").append(job.getString("value"));
				} else if (job.getString("field").equalsIgnoreCase("sysDataState")){
					sql.append(" and SYS_DATA_STATE = '").append(job.getString("value")).append("'");
				}
			}
		} else {
			pager.setCondition(StringUtils.replace(pager.getCondition(), "'", "''"));
		}

		int dataCount = 0;
		try {
			dataCount = dataUtils.getDataCount(temp, null, sql.toString(), null,
					dataState.isEmpty() ? null : dataState, null, null);
			int start = pager.getPage() == 0 ? 0 : pager.getPage() - 1;
			if(isExp){ //??????????????????
				start = 0;
				pager.setPagesize(dataCount);
			}
			List<Map<String, Object>> data = dataUtils.getData(null, temp, null, null, sql.toString(), colList, colNameType, start * pager.getPagesize(), pager.getPagesize(),
					null,
					dataState.isEmpty() ? null : dataState, null,null,
					null, libList,null,pager);
			result.put("Rows", data);
			result.put("Total", dataCount);
			if(isExp){
				result.put("ColumnNms", colNmList);
				result.put("Columns", colList);
				result.put("TableName", temp.getTableEnName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ?????????????????????
	 *
	 * @param paramStr
	 * @param paramCol
	 * @return
	 */
	public List<Map<String, Object>> getDataList(String paramStr, String[] paramCol) {
		List<Map<String, Object>> data = Lists.newArrayList();
		if (StringUtils.length(paramStr) > 0) {
			String[] paramDel = paramStr.split("@;@");
			// ???????????????????????????????????????list???
			if (paramDel != null && paramDel.length > 0) {
				for (int i = 0; i < paramDel.length; i++) {
					String[] paramValue = paramDel[i].split("#@@");
					Map<String, Object> map = Maps.newHashMap();
					for (int j = 0; j < paramCol.length; j++) {
						if (("DATAINPUT_ID".equals(paramCol[j])|| UdipConstants.TAB_ORDER_NO.equals(paramCol[j])) && paramValue.length <= j) {
							continue;
						}
						//if(paramCol[j].equalsIgnoreCase("COMMENTS"))
						//{
						//	map.put(paramCol[j], paramValue[j-1].trim());
						//}else
						if("COMMENTS".equals(paramCol[j])){
							String comments = EncodeUtils.urlDecode(EncodeUtils.urlDecode(paramValue[j].trim()));
							map.put(paramCol[j], comments);
						}else{
							map.put(paramCol[j], paramValue[j].trim());
						}

					}
					data.add(map);
				}
			}
		}
		return data;
	}

	/**
	 * @????????????: ????????????????????????
	 * @?????????: huzq1
	 * @????????????: 2021/10/19 14:48
	  * @param pager
	 * @param templateId
	 * @param realPath
	 * @param response
	 * @return
	 **/
	public void expInputData(Pager pager, String templateId, String realPath, HttpServletResponse response) {
		Map<String, Object> map = queryInputTable(pager, templateId, true);
		String tableName = (String)map.get("TableName");
		List<String> columns = (List<String>)map.get("Columns");
		List<String> columnNms = (List<String>)map.get("ColumnNms");
		List<Map<String, Object>> rows = (List<Map<String, Object>>) map.get("Rows");

		LinkedHashMap<String, String> header = new LinkedHashMap<>();
		for (int i = 0; i < columnNms.size(); i++) {
			if (i < columnNms.size() - 7) {
				header.put(i + "", columns.get(i) + "#" + columnNms.get(i));
			} else {
				header.put(i + "", columnNms.get(i));
			}
		}

		List<Map<String, Object>> vos = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			Map<String, Object> vo = new HashMap<>();
			for (int i = 0; i < columnNms.size(); i++) {
				vo.put(i+"", row.get(columns.get(i)));
			}
			vos.add(vo);
		}

		String filePath = realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
				+ tableName + "(????????????).xls";
		ExcelExportUtil util = new ExcelExportUtil(response, vos, header, filePath);
		util.exportFile("????????????");
	}

	public List<InputTableImportVO> upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(InputTableImportVO.class, file);
		List<InputTableImportVO> vos = new ArrayList<>();
		try {
			vos = (List<InputTableImportVO>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					vos);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return vos;
	}

}