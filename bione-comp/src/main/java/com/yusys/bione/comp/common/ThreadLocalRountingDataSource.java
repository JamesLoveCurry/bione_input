package com.yusys.bione.comp.common;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ThreadLocalRountingDataSource extends AbstractRoutingDataSource {

	protected static Logger logger = LoggerFactory.getLogger(ThreadLocalRountingDataSource.class);

	private static Map<Object, Object> targetDataSources2;

	@Override
	protected Object determineCurrentLookupKey() {
		DataSourceType key = DataSourceTypeManager.get();
		logger.debug("(" + Thread.currentThread().getId() + ") key = " + key);
		return key;
	}

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
		targetDataSources2 = targetDataSources;
	}

	public static DataSource getDataSource() {
		return (DataSource)targetDataSources2.get(DataSourceTypeManager.get());
	}
}
