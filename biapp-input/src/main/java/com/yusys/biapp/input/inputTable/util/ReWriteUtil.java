package com.yusys.biapp.input.inputTable.util;

import com.yusys.biapp.input.template.entity.RptInputRewriteFieldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("reWriteUtil")
@Transactional(readOnly = true)
public class ReWriteUtil{
	

	@Autowired
	private OracleReWriteSql oracleReWriteSql;

	@Autowired
	private DB2ReWriteSql dB2ReWriteSql;

	@Autowired
	private MysqlReWriteSql mysqlReWriteSql;

	@Autowired
	private PostgresqlReWriteSql postgresqlReWriteSql;
	/**
	 *  0 : 不存在的数据新增,存在的数据更新
	 *	1 : 完全覆盖
	 *	2 : 只更新存在的数据
	 *	3 : 只新增不存在的数据
	 */
	public String executeReWrite(String dsId,String updateType,String dbType,String schemaName,
			String tableName,String sourceTable,List<RptInputRewriteFieldInfo>infoList,String taskInstanceId){
		IReWriteSql rewrite = null;
		if(dbType.equals("oracle"))
			rewrite = oracleReWriteSql;
		else if (dbType.equals("db2"))
			rewrite = dB2ReWriteSql;
		else if (dbType.equals("mysql"))
			rewrite = mysqlReWriteSql;
		else if (dbType.equals("postgresql")){
			rewrite = postgresqlReWriteSql;
		}
		rewrite.init(dsId, schemaName, tableName,sourceTable, infoList,taskInstanceId);

		try {
			if(updateType.equals("0"))
				return rewrite.updAndIns();
			else if (updateType.equals("1"))
				return rewrite.replace();
			else if (updateType.equals("2"))
				return rewrite.updateExists();
			else if (updateType.equals("3"))
				return rewrite.insertNotExists();
			else return "0";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
	}
	
	
}
