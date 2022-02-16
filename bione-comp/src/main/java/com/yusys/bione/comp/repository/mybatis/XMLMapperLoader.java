package com.yusys.bione.comp.repository.mybatis;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Mapper.xml 热部署
 * 
 * @author xugy xugy@yuchengtech.com
 * @version 2015年05月12日 上午11:35:45
 */
@Component
public class XMLMapperLoader implements DisposableBean, InitializingBean, ApplicationContextAware {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private ConfigurableApplicationContext context;
	private Map<String, String> fileMapping = new HashMap<String, String>();
	private Set<Resource> changedMapperXml = new HashSet<Resource>();
	private Resource[] mapperLocations;
	private Scanner scanner;
	private ScheduledExecutorService service;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = (ConfigurableApplicationContext) applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			service = Executors.newScheduledThreadPool(1);
			// 获取xml所在包
			SqlSessionFactoryBeanExt sqlSessionFactory = this.context.getBean(SqlSessionFactoryBeanExt.class);
			Field field = sqlSessionFactory.getClass().getSuperclass().getDeclaredField("mapperLocations");
			field.setAccessible(true);
			mapperLocations = (Resource[]) field.get(sqlSessionFactory);
			// 触发文件监听事件
			scanner = new Scanner();
			scanner.scan();
			service.scheduleAtFixedRate(new Task(), 5, 1, TimeUnit.SECONDS);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	class Task implements Runnable {
		@Override
		public void run() {
			try {
				if (scanner.isChanged()) {
					StringBuilder xmlName = new StringBuilder();
					for (Resource resource : changedMapperXml) {
						xmlName.append(resource.getFilename() + ", ");
					}
					logger.info(xmlName.substring(0, xmlName.length() - 2) + " 文件改变, 将重新加载.");
					scanner.reloadXML();
					logger.info(xmlName.substring(0, xmlName.length() - 2) + " 加载完毕.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class Scanner {

		public void reloadXML() throws Exception {
			SqlSessionFactory factory = context.getBean(SqlSessionFactory.class);
			Configuration configuration = factory.getConfiguration();
			// 移除加载项
			removeConfig(configuration);
			// 重新扫描加载
			Resource[] resources = mapperLocations;
			if (resources != null) {
				for (int i = 0; i < resources.length; i++) {
					try {
						XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(
								resources[i].getInputStream(), configuration, resources[i].toString(), configuration.getSqlFragments());
						xmlMapperBuilder.parse();
					} catch (Exception e) {
						throw new NestedIOException("Failed to parse mapping resource: '" + resources[i] + "'", e);
					} finally {
						ErrorContext.instance().reset();
					}
				}
				changedMapperXml.clear();
			}
		}

		private void removeConfig(Configuration configuration) throws Exception {
			Class<?> classConfig = configuration.getClass();
			clearMap(classConfig, configuration, "mappedStatements");
			clearMap(classConfig, configuration, "caches");
			clearMap(classConfig, configuration, "resultMaps");
			clearMap(classConfig, configuration, "parameterMaps");
			clearMap(classConfig, configuration, "keyGenerators");
			clearMap(classConfig, configuration, "sqlFragments");
			clearSet(classConfig, configuration, "loadedResources");
		}

		private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
			Field field = classConfig.getDeclaredField(fieldName);
			field.setAccessible(true);
			((Map<?, ?>) field.get(configuration)).clear();
		}

		private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
			Field field = classConfig.getDeclaredField(fieldName);
			field.setAccessible(true);
			((Set<?>) field.get(configuration)).clear();
		}

		public void scan() throws IOException {
			if (fileMapping.isEmpty()) {
				Resource[] resources = mapperLocations;
				if (resources != null) {
					for (int i = 0; i < resources.length; i++) {
						String multi_key = getValue(resources[i]);
						fileMapping.put(resources[i].getFilename(), multi_key);
					}
				}
			}
		}

		private String getValue(Resource resource) throws IOException {
			String contentLength = String.valueOf((resource.contentLength()));
			String lastModified = String.valueOf((resource.lastModified()));
			return new StringBuilder(contentLength).append(lastModified).toString();
		}

		public boolean isChanged() throws IOException {
			boolean isChanged = false;
			Resource[] resources = mapperLocations;
			if (resources != null) {
				for (int i = 0; i < resources.length; i++) {
					String name = resources[i].getFilename();
					String value = fileMapping.get(name);
					String multi_key = getValue(resources[i]);
					if (!multi_key.equals(value)) {
						isChanged = true;
						fileMapping.put(name, multi_key);
						changedMapperXml.add(resources[i]);
					}
				}
			}
			return isChanged;
		}
	}

	@Override
	public void destroy() throws Exception {
		if (this.service != null) {
			this.service.shutdownNow();
		}
	}

}