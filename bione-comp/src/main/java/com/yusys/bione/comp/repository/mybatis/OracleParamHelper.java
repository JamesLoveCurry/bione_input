/**
 * 
 */
package com.yusys.bione.comp.repository.mybatis;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;


/**
 * <pre>
 * Title:针对oracle下空param类型处理
 * Description: 针对oracle下空param类型处理
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Intercepts({
		@Signature(type = Executor.class, args = { MappedStatement.class,
				Object.class, RowBounds.class, ResultHandler.class }, method = "query"),
		@Signature(type = Executor.class, args = { MappedStatement.class,
				Object.class }, method = "update") })
public class OracleParamHelper implements Interceptor {

	private static final String DB_TYPE_ORACLE = "oracle";

	private static String dialect;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		final Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		// 将mybatis环境中空类型设置为Types.NULL。原来的缺省值是Types.OTHER
		ms.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		if (DB_TYPE_ORACLE.equals(dialect.trim().toLowerCase())) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	@Override
	public void setProperties(Properties properties) {
		dialect = properties.getProperty("dialect");
		if (StringUtils.isEmpty(dialect)) {
			// 缺省当做ORACLE处理
			dialect = DB_TYPE_ORACLE;
		}
	}
	
}
