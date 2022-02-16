/**
 * 
 */
package com.yusys.bione.frame.search.util;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.search.DefaultSearchAndActionConfig;
import com.yusys.bione.frame.search.SearchModule;

/**
 * <pre>
 * Title:关于索引配置缓存的工具类
 * Description:
 * </pre>
 * 
 * @author tanxu@yuchengtech.com
 * @version 
 * 
 * <pre>
 * 修改记录
 *    修改后版本:	修改人：		修改日期:	修改内容:
 * </pre>
 */
public class CacheUtil {
	
	private static final Logger log = LoggerFactory.getLogger(CacheUtil.class);
	//配置文件的路径
	private static final String DEFAULE_PROPERTIES_PATH = "bireport/extension/search-objact.properties";
	
	/**
	 * 重新加载指定模块的索引配置信息
	 * @param moduleId		模块唯一标识	
	 */
	public static void relaodCache(String moduleId){
		Map<String, DefaultSearchAndActionConfig> cacheMap = Maps.newHashMap();
		cacheMap.put(moduleId, buildConfig(PropertiesUtils.get(DEFAULE_PROPERTIES_PATH), moduleId));
		removeCache(GlobalConstants4frame.CACHE_OBJECT_ACTION);
		EhcacheUtils.put(GlobalConstants4frame.CACHE_OBJECT_ACTION, GlobalConstants4frame.CACHE_OBJECT_ACTION, cacheMap);
	}
	
	/**
	 * 重新加载所有模块的索引配置信息
	 */
	public static void reloadCache(){
		Map<String, DefaultSearchAndActionConfig> cacheMap = Maps.newHashMap();
		PropertiesUtils propertiesUtils = PropertiesUtils.get(DEFAULE_PROPERTIES_PATH);
		for(SearchModule searchModule : SearchModule.values()){
			cacheMap.put(searchModule.getModuleId(), buildConfig(propertiesUtils, searchModule.getModuleId()));
		}
		removeCache(GlobalConstants4frame.CACHE_OBJECT_ACTION);
		EhcacheUtils.put(GlobalConstants4frame.CACHE_OBJECT_ACTION, GlobalConstants4frame.CACHE_OBJECT_ACTION, cacheMap);
	}
	
	/**
	 * 读取指定模块的配置信息对象
	 */
	private static DefaultSearchAndActionConfig buildConfig(PropertiesUtils propertiesUtils, String moduleId){
		DefaultSearchAndActionConfig config = new DefaultSearchAndActionConfig();
		config.setName(moduleId);
		for(String name : getPropertyNames(propertiesUtils.getProperties())){
			if(StringUtils.startsWith(name, moduleId)){
				config.setAttribute(StringUtils.remove(name, moduleId + "."), propertiesUtils.getProperty(name));
			}
		}
		return config;
	}
	
	/**
	 * 返回Properties所有属性名称
	 */
	private static Set<String> getPropertyNames(Properties properties){
//		Enumeration<?> e = properties.propertyNames();
//		Set<String> set = new HashSet<String>();
//		while(e.hasMoreElements()){
//			set.add(String.valueOf(e.nextElement()));
//		}
//		return set; //JDK 1.5
		return properties.stringPropertyNames(); //目前报表系统默认的JDK版本为JDK 1.6
	}

	/**
	 * 从缓存中获得指定模块的配置信息对象
	 */
	public static DefaultSearchAndActionConfig getConfig(String moduleId){
		Map<String, DefaultSearchAndActionConfig> ehcache = getEhcache();
		if(ehcache == null){
			reloadCache();
			ehcache = getEhcache();
		}
		DefaultSearchAndActionConfig config = ehcache.get(moduleId);
		if(config == null){
			relaodCache(moduleId);
			config = ehcache.get(moduleId);
		}
		if(config == null){
			throw new IllegalStateException("系统未找到指定" + moduleId + "模块的" + moduleId + "-objact.properties配置文件！");
		} else {
			return config;
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, DefaultSearchAndActionConfig> getEhcache() {
		return (Map<String, DefaultSearchAndActionConfig>) EhcacheUtils.get(GlobalConstants4frame.CACHE_OBJECT_ACTION, GlobalConstants4frame.CACHE_OBJECT_ACTION);
	}
	
	/**
	 * 获得非缓存的指定模块的配置信息对象
	 */
	public static DefaultSearchAndActionConfig getNotCachedConfig(String moduleId){
		DefaultSearchAndActionConfig config = buildConfig(PropertiesUtils.get(DEFAULE_PROPERTIES_PATH), moduleId);
		if(config == null){
			throw new IllegalStateException("系统未找到指定" + moduleId + "模块的" + moduleId + "-objact.properties配置文件！");
		} else {
			return config;
		}
	}
	
	/**
	 * 移除索引配置信息
	 */
	private static void removeCache(String cacheName) {
		if (!StringUtils.isEmpty(cacheName)) {
			try {
				EhcacheUtils.remove(cacheName, GlobalConstants4frame.CACHE_OBJECT_ACTION);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("缓存移除错误", e);
			}
		}
	}
}
