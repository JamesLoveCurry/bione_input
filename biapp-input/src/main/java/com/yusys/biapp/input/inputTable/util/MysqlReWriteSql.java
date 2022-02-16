package com.yusys.biapp.input.inputTable.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("mysqlReWriteSql")
@Transactional(readOnly = true)
public class MysqlReWriteSql extends DefaultReWriteSql {

	@Override
	protected String getClearDataSql() {
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append(" DELETE FROM  ").append(schemaName).append(".").append(tableName);
		return sqlBuff.toString();
	}

}
