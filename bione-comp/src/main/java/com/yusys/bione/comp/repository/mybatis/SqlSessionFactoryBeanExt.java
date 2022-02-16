package com.yusys.bione.comp.repository.mybatis;

import java.lang.reflect.Field;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;

import com.yusys.bione.comp.utils.SpringContextHolder;

/**
 * 为支持Mapper.xml 热部署, 重新封装SqlSessionFactoryBean
 * 
 * @author xugy xugy@yuchengtech.com
 * @version 2015年05月12日 上午11:35:45
 */
public class SqlSessionFactoryBeanExt extends SqlSessionFactoryBean {

	private SqlSessionFactory sqlSessionFactory;
	
	public void setValue(String name, Object value) {
		try {
			Field field = SqlSessionFactoryBean.class.getDeclaredField(name);
			field.setAccessible(true);
			field.set(this, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SqlSessionFactory getObject() throws Exception {
		if (this.sqlSessionFactory == null) {
			sqlSessionFactory = buildSqlSessionFactory();
			setValue("sqlSessionFactory", sqlSessionFactory);
		}
		return this.sqlSessionFactory;
	}
	
	public void afterPropertiesSet() throws Exception {
		SpringContextHolder.getBean(XMLMapperLoader.class);
	}
}
