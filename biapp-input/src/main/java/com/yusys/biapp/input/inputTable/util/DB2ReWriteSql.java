package com.yusys.biapp.input.inputTable.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("dB2ReWriteSql")
@Transactional(readOnly = true)
public class DB2ReWriteSql extends DefaultReWriteSql {

	@Override
	protected String getClearDataSql() {
		// TODO Auto-generated method stub
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append(" DELETE FROM  ").append(schemaName).append(".").append(tableName);
		return sqlBuff.toString();
	}

}
