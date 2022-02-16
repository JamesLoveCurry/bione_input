/**
 * 
 */
package com.yusys.bione.comp.common;

import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;

public class BioneDataSource implements FactoryBean<DataSource> {

	private DataSource dataSource = null;

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	
	public DataSource getObject() throws Exception {
		return this.dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	
	public Class<?> getObjectType() {
		return DataSource.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	
	public boolean isSingleton() {
		return true;
	}
}
