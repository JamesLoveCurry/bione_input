/**
 * 
 */
package com.yusys.bione.frame.search.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.beans.BeanMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.frame.search.DefaultSearchAndActionConfig;
import com.yusys.bione.frame.search.index.IndexDocument;

/**
 * <pre>
 * Title:创建内部索引文档对象的工具类
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
public class IndexDocumentUtil {

	/**
	 * 创建索引文档对象列表
	 * @param entitys
	 * @param config
	 */
	public static List<IndexDocument> createIndexDocument(Iterable<?> entitys, DefaultSearchAndActionConfig config){
		List<IndexDocument> results = new ArrayList<IndexDocument>();
		BeanMap bm = null;
		for(Object obj : entitys){
			if(bm == null){
				bm = BeanMap.create(obj);
			}else{
				bm.setBean(obj);
			}
			results.add(createIndexDocument(config, obj));
		}
		return results;
	}
	
	/**
	 * 创建索引文档对象
	 * @param entitys
	 * @param config
	 */
	public static <T> IndexDocument createIndexDocument(T entity, DefaultSearchAndActionConfig config){
		return createIndexDocument(config, entity);
	}
	
	/**
	 * 获取实体对象中唯一标识的值
	 * @param beanMap
	 * @param config
	 * @param obj
	 */
	public static String getEntityId(Map<Object, Object> beanMap, DefaultSearchAndActionConfig config, Object obj){
		String entityId = String.valueOf(beanMap.get(config.getEntityId()));
		if(StringUtils.isEmpty(entityId)){
			throw new IllegalStateException("实体" + obj.toString() + "的主键值不能为空！");
		}
		return entityId;
	}
	
	/**
	 * 创建索引文档对象
	 * @param beanMap
	 * @param config
	 * @param entityId
	 */
	public static IndexDocument createIndexDocument(DefaultSearchAndActionConfig config, Object entity){
		LinkedHashMap<String, String> entityPropertiesAndColumns = config.getEntityPropertiesAndColumns();
		Map<Object, Object> beanMap = createBeanMap(entity);
		String[] searchColumns = config.getSearchColumns();
		IndexDocument document = new IndexDocument(getEntityId(beanMap, config, entity));
		String column;
		for(String property : config.getEntityProperties()){
			Object value = beanMap.get(property);
			if(value != null){
				column = entityPropertiesAndColumns.get(property);
				document.add(column, String.valueOf(value), true, ArrayUtils.contains(searchColumns, column) ? true : false);
			}
		}
		LinkedHashMap<String, String> entityPropertiesAndSortSpecColumns = config.getEntityPropertiesAndSortSpecColumns();
		for(String property : entityPropertiesAndSortSpecColumns.keySet()){
			Object value = beanMap.get(property);
			if(value != null){
				column = entityPropertiesAndSortSpecColumns.get(property);
				document.add(column, String.valueOf(value), false, false);
			}
		}
		return document;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> createBeanMap(Object entity){
//		try {
//			return PropertyUtils.describe(entity);
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}
//		return Maps.newHashMap();//JDK 1.5
		return BeanMap.create(entity); // JDK 1.6 
	}
}
