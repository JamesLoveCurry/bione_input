package com.yusys.biapp.input.inputTable.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @项目名称： 统一监管报送
 * @类名称： PostgresqlReWriteSql
 * @类描述:
 * @功能描述:
 * @创建人: huzq1
 * @创建时间: 2021/08/19 10:25
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
@Component("postgresqlReWriteSql")
@Transactional(readOnly = true)
public class PostgresqlReWriteSql extends DefaultReWriteSql {

    @Override
    protected String getClearDataSql() {
        StringBuilder sqlBuff = new StringBuilder();
        sqlBuff.append(" DELETE FROM  ").append(schemaName).append(".").append(tableName);
        return sqlBuff.toString();
    }
}
