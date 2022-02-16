package com.yusys.bione.comp.repository.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.dialect.DBMS;
import com.yusys.bione.comp.repository.mybatis.dialect.Dialect;
import com.yusys.bione.comp.repository.mybatis.dialect.DialectClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Intercepts(@Signature(type = Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class,
		ResultHandler.class }))
public class PageHelper implements Interceptor {
	private static final ThreadLocal<Pager> localPage = new ThreadLocal<Pager>();

	private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(
			0);

	// 数据库方言
	private static String dialectType = "";
	private static Dialect dialect;

	/**
	 * 开始分页
	 * 
	 * @param Pager
	 * 
	 */
	public static void startPage(int pageNum, int pageSize, String orderBy,
			String orderType, String filterSql) {
		Pager pager = new Pager();
		pager.setPage(pageNum);
		pager.setPagesize(pageSize);
		pager.setSortname(orderBy);
		pager.setSortorder(orderType);
		pager.setFilterSql(filterSql);
		localPage.set(pager);
	}

	/**
	 * 开始分页
	 * 
	 * @param Pager
	 * 
	 */
	public static void startPage(Pager pager) {
		localPage.set(pager);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		PageMyBatis<Object> page = new PageMyBatis<Object>();
		final Object[] args = invocation.getArgs();
		if (localPage.get() == null) {
			return invocation.proceed();
		} else {
			// 忽略RowBounds-否则会进行Mybatis自带的内存分页
			args[2] = RowBounds.DEFAULT;
			MappedStatement ms = (MappedStatement) args[0];
			Object parameterObject = args[1];
			BoundSql boundSql = ms.getBoundSql(parameterObject);
			// 分页信息
			Pager pager = localPage.get();
			// 移除本地变量
			localPage.remove();
			MappedStatement qs = newMappedStatement(ms, new BoundSqlSqlSource(
					boundSql));
			// 将参数中的MappedStatement替换为新的qs，防止并发异常
			args[0] = qs;
			MetaObject msObject = SystemMetaObject.forObject(qs);
			String sql = (String) msObject.getValue("sqlSource.boundSql.sql");
			String filterSql = "";
			if (pager.isFilter()
					&& StringUtils.isNotBlank(pager.getFilterSql())) {
				filterSql = getFilterSql(sql, pager.getFilterSql());
				sql = filterSql;
			}
			if (pager.isOrderBy()
					&& StringUtils.isNotBlank(pager.getSortname())
					&& StringUtils.isNotBlank(pager.getSortorder())) {
				sql = getSortSql(sql, pager.getSqlSortName(),
						pager.getSortorder());
			}
			if (pager.isPaging()) {
				// 求count - 重写sql
				msObject.setValue("sqlSource.boundSql.sql",
						getCountSql(filterSql.equals("") ? sql : filterSql));
				// 查询总数
				Object totalResult = invocation.proceed();
				int totalCount = (Integer) ((List<?>) totalResult).get(0);
				page.setTotalCount(totalCount);
				sql = getPageSql(sql, pager);
			}
			// 分页sql - 重写sql
			msObject.setValue("sqlSource.boundSql.sql", sql);
			// 恢复类型
			msObject.setValue("resultMaps", ms.getResultMaps());
			// 执行分页查询
			Object result = invocation.proceed();
			page.addAll((List<Object>) result);
			// 返回结果
			return page;

		}
	}

	/**
	 * 获取排序sql
	 * 
	 * @param sql
	 * @return
	 */
	private String getSortSql(String sql, String orderBy, String orderType) {
		return dialect.getSortString(sql, orderBy, orderType);
	}

	/**
	 * 获取过滤sql
	 * 
	 * @param sql
	 * @return
	 */
	private String getFilterSql(String sql, String filterSql) {
		return dialect.getFilterString(sql, filterSql);
	}

	/**
	 * 获取总数sql
	 * 
	 * @param sql
	 * @return
	 */
	private String getCountSql(String sql) {
		return dialect.getCountString(sql);
	}

	/**
	 * 获取分页sql - 如果要支持其他数据库，修改这里就可以
	 * 
	 * @param sql
	 * @param page
	 * @return
	 */
	private String getPageSql(String sql, Pager pager) {
		return dialect.getLimitString(sql, pager.getPageFirstIndex(),
				pager.getPagesize());
	}

	private class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	/**
	 * 由于MappedStatement是一个全局共享的对象，因而需要复制一个对象来进行操作，防止并发访问导致错误
	 * 
	 * @param ms
	 * @param newSqlSource
	 * @return
	 */
	private MappedStatement newMappedStatement(MappedStatement ms,
			SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(
				ms.getConfiguration(), ms.getId() + "_paging", newSqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1,
					keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		// 由于resultMaps第一次需要返回int类型的结果，所以这里需要生成一个resultMap - 防止并发错误
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(),
				ms.getId(), int.class, EMPTY_RESULTMAPPING).build();
		resultMaps.add(resultMap);
		builder.resultMaps(resultMaps);
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	/**
	 * 只拦截Executor
	 * 
	 * @param target
	 * @return
	 */
	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public void setProperties(Properties p) {
		dialectType = p.getProperty("dialect");
		if (dialectType == null || dialectType.equals("")) {
			throw new RuntimeException("没有设置分页dialect参数.");
		} else {
			DBMS dbms = DBMS.valueOf(dialectType.toUpperCase());
			dialect = DialectClient.getDbmsDialect(dbms);
		}
	}
}
