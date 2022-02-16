package com.yusys.biapp.input.inputTable.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.template.entity.RptInputRewriteFieldInfo;
import com.yusys.bione.frame.mtool.service.DataSourceBS;

public abstract class DefaultReWriteSql implements IReWriteSql{

	private String dsId;
	protected String schemaName;
	protected String tableName;
	private String sourceTable;
	private String taskInstanceId;
	
	private List<RptInputRewriteFieldInfo>infoList;
	
	private Map<String,String>fieldMap;
	private List<String>idList;

	@Autowired
	private DataSourceBS dataSourceBS;
	
	public DefaultReWriteSql(){
		
	}


	@Override
	public void init(String dsId, String schemaName, String tableName,
			String sourceTable, List<RptInputRewriteFieldInfo> infoList,String taskInstanceId) {
		// TODO Auto-generated method stub
		this.dsId = dsId;
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.sourceTable = sourceTable;
		this.taskInstanceId = taskInstanceId;
		this.infoList = infoList;
		initBaseInfo();
		
	}
	
	private void initBaseInfo(){
		fieldMap = Maps.newHashMap();
		idList = Lists.newArrayList();
		for(RptInputRewriteFieldInfo info : infoList){
			fieldMap.put(info.getFieldName(), info.getUpdateFieldName());
			if(info.getIsId()!=null&&info.getIsId().equals("1"))
				idList.add(info.getFieldName());
		}
	}

	@Override
	public String updAndIns()throws Exception {
		String sql = merge(true,true);
		return executeSql(sql);
	}

	
	protected abstract String getClearDataSql();
	
	@Override
	public String replace()throws Exception {
		String clearSql=getClearDataSql();

		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff = new StringBuilder();
		sqlBuff.append(" INSERT INTO ").append(schemaName).append(".").append(tableName).append(" (");
		StringBuilder valueBuff = new StringBuilder();
		boolean isFirst = true;
		for(String field: fieldMap.keySet()){
			if(isFirst)
				isFirst = false;
			else
			{
				sqlBuff.append(" , ");
				valueBuff.append(" , ");
			}
			sqlBuff.append(field);
			valueBuff.append(fieldMap.get(field));
		}
		sqlBuff.append(" )  SELECT  DISTINCT ").append(valueBuff).append(" FROM  ").append(sourceTable)
		.append(" WHERE SYS_DATA_CASE = '").append(taskInstanceId).append("' ");
		
		

		return executeSql(clearSql,sqlBuff.toString());
	}

	@Override
	public String updateExists() throws Exception{
		String sql = merge(true,false);
		return executeSql(sql);
	}

	@Override
	public String insertNotExists()throws Exception {
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff = new StringBuilder();
		sqlBuff.append(" INSERT INTO ").append(schemaName).append(".").append(tableName).append("  (");
		StringBuilder valueBuff = new StringBuilder();
		boolean isFirst = true;
		for(String field: fieldMap.keySet()){
			if(isFirst)
				isFirst = false;
			else
			{
				sqlBuff.append(" , ");
				valueBuff.append(" , ");
			}
			sqlBuff.append(field);
			valueBuff.append(fieldMap.get(field));
		}
		sqlBuff.append(" )  SELECT DISTINCT ").append(valueBuff).append(" FROM  ").append(sourceTable).append(" TAB1 ");
		sqlBuff.append(" WHERE SYS_DATA_CASE = '").append(taskInstanceId).append("' ")
		.append(" AND NOT EXISTS ( SELECT 1 FROM  ").append(schemaName).append(".").append(tableName) 
			   .append(" TAB2 WHERE ");
		
		if(!idList.isEmpty()){
			isFirst = true;
			for(String id:idList){
				if(isFirst)
					isFirst = false;
				else
					sqlBuff.append(" AND ");
				String sourceField = fieldMap.get(id);
				sqlBuff.append(" TAB1.").append(id).append(" = ").append(" TAB2.").append(sourceField);
			}
		}
		
		sqlBuff.append(" ) ");
		

		return executeSql(new String[]{sqlBuff.toString()});
	}
	
	private String executeSql(String ...sqls) throws Exception{
		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;
		String errSql ="";
		try {
			conn = dataSourceBS.getConnection(dsId);
			conn.setAutoCommit(false);
			state = conn.createStatement();
			for(String sql:sqls){
				errSql = sql;
				state.executeUpdate(sql);
			}
			conn.commit();
		}catch(Exception ex){
			ex.printStackTrace();
			return "回写失败,请联系管理员检查模板设置,错误原因是"+ex.getMessage()+",回写sql为 :"+errSql;
		} finally {
			dataSourceBS.releaseConnection(rs, state, conn);
		}
		return "1";
	}
	
	private String merge(boolean isUpdate,boolean isInsert){
		StringBuilder sqlBuff = new StringBuilder();
		boolean isFirst = true;
		String dbType = this.dataSourceBS.getDataSourceType(dsId);
		if(StringUtils.equals("mysql", dbType)){//mysql语法replace into
			sqlBuff.append("replace into " + schemaName+ "." + tableName +" ( ");
			isFirst = true;
			for(String field: fieldMap.keySet()){
				String f = fieldMap.get(field);
				if(StringUtils.isNotEmpty(f)){
					if(isFirst){
						sqlBuff.append(field);
						isFirst = false;
					}else{
						sqlBuff.append(" , ");
						sqlBuff.append(field);
					}
				}else{
					continue;
				}
			}
			sqlBuff.append(" )  SELECT DISTINCT ");
			List<String> hasFieldList = Lists.newArrayList();
			isFirst = true;
			for(String updField : fieldMap.values()){
				if(StringUtils.isNotEmpty(updField) && !hasFieldList.contains(updField)){
					if(isFirst){
						isFirst = false;
					} else{ 
						sqlBuff.append(",");
					}
					sqlBuff.append(updField);
					hasFieldList.add(updField);
				}else{ 
					continue;
				}
			}
			sqlBuff.append(" FROM ").append(sourceTable)
			.append(" WHERE SYS_DATA_CASE = '").append(taskInstanceId).append("' ");
		}else{//oracle语法
			sqlBuff.append("MERGE INTO ").append(schemaName).append(".").append(tableName).append(" TAB1   ")
				   .append(" USING (SELECT DISTINCT ");
			List<String> hasFieldList = Lists.newArrayList();
			for(String updField : fieldMap.values()){
				if(StringUtils.isNotEmpty(updField) && !hasFieldList.contains(updField)){
					if(isFirst){
						isFirst = false;
					}else{
						sqlBuff.append(",");
					}
					sqlBuff.append(updField);
					hasFieldList.add(updField);
				}else continue;
			}
			sqlBuff.append(" FROM ").append(sourceTable)
			.append(" WHERE SYS_DATA_CASE = '").append(taskInstanceId).append("' ")
			.append(" ) TAB2 ");
			if(!idList.isEmpty()){
				isFirst = true;
				sqlBuff.append("ON ( ");
				for(String id:idList){
					if(isFirst)
						isFirst = false;
					else
						sqlBuff.append(" AND ");
					String sourceField = fieldMap.get(id);
					sqlBuff.append(" TAB1.").append(id).append(" = ").append(" TAB2.").append(sourceField);
				}
				
				sqlBuff.append(" ) ");
			}
			if(isUpdate){
				sqlBuff.append(" \n WHEN MATCHED THEN \n ");
				sqlBuff.append(" UPDATE SET  ");
				isFirst = true;
				for(String updField : fieldMap.keySet()){
					if(!this.idList.contains(updField))
					{
	
						String f = fieldMap.get(updField);
						if(StringUtils.isNotEmpty(f)){
							if(isFirst)
								isFirst = false;
							else
								sqlBuff.append(",");
							sqlBuff.append(" TAB1.").append(updField).append(" = ").append(" TAB2.").append(fieldMap.get(updField));
						}else{
							continue;
						}
					}
				}
				sqlBuff.append("\n");
			}
			if(isInsert){
				sqlBuff.append("WHEN NOT MATCHED THEN \n");
				sqlBuff.append(" INSERT (");
				
				StringBuilder valueBuff = new StringBuilder();
				isFirst = true;
				for(String field: fieldMap.keySet()){
					String f = fieldMap.get(field);
					if(StringUtils.isNotEmpty(f)){
						if(isFirst)
							isFirst = false;
						else
						{
							sqlBuff.append(" , ");
							valueBuff.append(" , ");
						}
						sqlBuff.append(" TAB1.").append(field);
						valueBuff.append(" TAB2.").append(fieldMap.get(field));
					}else{
						continue;
					}
				}
				sqlBuff.append(" )  VALUES (").append(valueBuff).append(" ) ");
			}
		}
		return sqlBuff.toString();
	}



}
