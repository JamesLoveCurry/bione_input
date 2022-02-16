package com.yusys.biapp.input.inputTable.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("oracleReWriteSql")
@Transactional(readOnly = true)
public class OracleReWriteSql  extends DefaultReWriteSql{

	@Override
	protected String getClearDataSql() {
		// TODO Auto-generated method stub
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append(" TRUNCATE TABLE  ").append(schemaName).append(".").append(tableName);
		return sqlBuff.toString();
	}

	
}
