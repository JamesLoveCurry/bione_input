/**
 * 
 */
package com.yusys.bione.frame.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

/**
 * <pre>
 * Title:读取配置文件后对配置信息封装的对象，提供一些方便内部使用的方法
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
public class DefaultSearchAndActionConfig  implements ISearchAndActionConfig {
	
	private static final long serialVersionUID = 5445237532305955512L;

	//配置文件中属性名称的字符串常量
	private static final String URL = "url";
	private static final String NAME = "name";
	private static final String PK_NAME = "pkName";
	private static final String COLUMNS = "columns";
	private static final String COLUMN_NAMES = "columnNames";
	private static final String ENTITY_ID = "entityId";
	private static final String ENTITY_PROPERTIES = "entityProperties";
	
	//对象中的缓存属性名称的字符串常量
	private static final String CONFIG_COLUMNS_AND_NAMES = "CONFIG_COLUMNS_AND_NAMES";
	private static final String CONFIG_ENTITY_PROPERTIES_AND_COLUMNS = "CONFIG_ENTITY_PROPERTIES_AND_COLUMNS";
	private static final String CONFIG_SORT_SPEC_ENTITY_PROPERTIES = "CONFIG_SORT_SPEC_ENTITY_PROPERTIES";
	private static final String CONFIG_COLUMNS = "CONFIG_COLUMNS";
	private static final String CONFIG_SEARCH_COLUMNS = "CONFIG_SEARCH_COLUMNS";
	private static final String CONFIG_SORT_COLUMNS = "CONFIG_SORT_COLUMNS";
	private static final String CONFIG_FIND_SORT_COLUMNS = "CONFIG_FiND_SORT_COLUMNS";
	private static final String CONFIG_COLUMN_TO_SORT_SPEC_COLUMN = "CONFIG_COLUMN_TO_SORT_SPEC_COLUMN";
	
	//排序列的前缀
	private static final String SORT_COLUMN_PREFIX = "prefix_";
	
	//储存所有属性的Map容器
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public String getUrl() {
		return StringUtils.defaultString((String)getAttribute(URL), "");
	}

	public void setUrl(String url) {
		setAttribute(URL, url);
	}

	public String getName() {
		return StringUtils.defaultString((String)getAttribute(NAME), "");
	}

	public void setName(String name) {
		setAttribute(NAME, name);
	}

	public String getPkName() {
		return StringUtils.defaultString((String)getAttribute(PK_NAME), "");
	}

	public void setPkName(String pkName) {
		setAttribute(PK_NAME, pkName);
	}
	
	public String getEntityId() {
		String entityId =  (String)getAttribute(ENTITY_ID);
		if(entityId == null){
			throw new IllegalStateException("search-objact.properties配置文件中" + getName() + "部分的实体主键属性" + ENTITY_ID + "不能为空!");
		}
		if(StringUtils.contains("entityId", ',')){
			throw new IllegalStateException("search-objact.properties配置文件中" + getName() + "部分的实体主键属性" + ENTITY_ID + "要唯一且不能用,分隔！");
		}
		return entityId;
	}

	public void setEntityId(String entityId) {
		setAttribute(ENTITY_ID, entityId);
	}
	
	public void setEntityProperties(String entityProperties) {
		setAttribute(ENTITY_PROPERTIES, entityProperties);
	}
	
	/**
	 * 获取实体的属性名数组
	 */
	public String[] getEntityProperties() {
		return StringUtils.split((String)getAttribute(ENTITY_PROPERTIES), ",");
	}
	
	/**
	 * 获取因排序功能需要特殊处理的属性
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, String> getEntityPropertiesAndSortSpecColumns() {
		if (getAttribute(CONFIG_SORT_SPEC_ENTITY_PROPERTIES) == null) {
			LinkedHashMap<String, String> sortSpecEntityProperties = Maps.newLinkedHashMap();
			LinkedHashMap<String, Boolean> sortColumns = findSortColumns();
			String entityProperty = null;
			for(String col : sortColumns.keySet()){
				if(!StringUtils.equals(col, convertColumn2SortSpecColumn(col))){
					entityProperty = getColumn2EntityProperties(col);
					if(entityProperty != null){
						sortSpecEntityProperties.put(entityProperty, SORT_COLUMN_PREFIX + col);
					}
				}
			}
			setAttribute(CONFIG_SORT_SPEC_ENTITY_PROPERTIES, sortSpecEntityProperties);
		}
		return (LinkedHashMap<String, String>)getAttribute(CONFIG_SORT_SPEC_ENTITY_PROPERTIES);
	}
	
	private String getColumn2EntityProperties(String column){
		LinkedHashMap<String, String> entityPropertiesAndColumns = getEntityPropertiesAndColumns();
		for(String property : entityPropertiesAndColumns.keySet()){
			if(StringUtils.equals(entityPropertiesAndColumns.get(property), column)){
				return property;
			}
		}
		return null;
	}
	
	public void setColumns(String columns) {
		setAttribute(COLUMNS, columns);
	}
	
	/**
	 * 获取索引文档索引的属性数组
	 */
	public String[] getColumns(){
		if(getAttribute(CONFIG_COLUMNS) == null){
			String[] columns = StringUtils.split((String)getAttribute(COLUMNS), ",");
			List<String> results = new ArrayList<String>();
			for(String col : columns){
				results.add(columnsFilter(col));
			}
			setAttribute(CONFIG_COLUMNS, results.toArray((new String[0])));
		}
		return (String[])getAttribute(CONFIG_COLUMNS);
	}
	
	/**
	 * 获取可以被检索的索引文档索引的属性数组
	 */
	public String[] getSearchColumns(){
		if (getAttribute(CONFIG_SEARCH_COLUMNS) == null) {
			String[] columns = StringUtils.split((String)getAttribute(COLUMNS), ",");
			List<String> results = new ArrayList<String>();
			for(String col : columns){
				if(isSearchColumn(col)){
					results.add(columnsFilter(col));
				}
			}
			setAttribute(CONFIG_SEARCH_COLUMNS, results.toArray((new String[0])));
		}
		return (String[])getAttribute(CONFIG_SEARCH_COLUMNS);
	}
	
	@SuppressWarnings("unchecked")
	public String convertColumn2SortSpecColumn(String column){
		if (getAttribute(CONFIG_COLUMN_TO_SORT_SPEC_COLUMN) == null) {
			Map<String, String> map = Maps.newHashMap();
			String[] searchColumns = getSearchColumns();
			LinkedHashMap<String, Boolean> sortColumns = findSortColumns();
			for(String col : sortColumns.keySet()){
				if(ArrayUtils.contains(searchColumns, col)){
					map.put(col, SORT_COLUMN_PREFIX + col);
				} else {
					map.put(col, col);
				}
			}
			setAttribute(CONFIG_COLUMN_TO_SORT_SPEC_COLUMN, map);
		}
		String sortSpecColumn = ((Map<String, String>)getAttribute(CONFIG_COLUMN_TO_SORT_SPEC_COLUMN)).get(columnsFilter(column));
		return sortSpecColumn == null ? column : sortSpecColumn;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Boolean> getSortColumns(){
		if (getAttribute(CONFIG_SORT_COLUMNS) == null) {
			LinkedHashMap<String, Boolean> sortColumns = findSortColumns();
			LinkedHashMap<String, Boolean> transformSortColumns = Maps.newLinkedHashMap(); 
			for(String key : sortColumns.keySet()){
				transformSortColumns.put(convertColumn2SortSpecColumn(key), sortColumns.get(key));
			}
			setAttribute(CONFIG_SORT_COLUMNS, transformSortColumns);
		}
		return (LinkedHashMap<String, Boolean>)getAttribute(CONFIG_SORT_COLUMNS);
	}
	
	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Boolean> findSortColumns(){
		if (getAttribute(CONFIG_FIND_SORT_COLUMNS) == null) {
			LinkedHashMap<String, Boolean> sortColumns = Maps.newLinkedHashMap();
			for(String col : StringUtils.split((String)getAttribute(COLUMNS), ",")){
				Boolean isSortable = isSortable(col);
				if(isSortable == Boolean.TRUE){
					sortColumns.put(columnsFilter(col), false);
				} else if(isSortable == Boolean.FALSE){
					sortColumns.put(columnsFilter(col), true);
				}
			}
			setAttribute(CONFIG_FIND_SORT_COLUMNS, sortColumns);
		}
		return (LinkedHashMap<String, Boolean>)getAttribute(CONFIG_FIND_SORT_COLUMNS);
	}
	
	public Boolean isSortable(String column){
		if(StringUtils.startsWith(column, "+")){
			return Boolean.TRUE;
		} else if(StringUtils.startsWith(column, "-")){
			return Boolean.FALSE;
		} else {
			return null;
		}
	}
	
	private boolean isSearchColumn(String column){
		return StringUtils.endsWith(column, "*");
	}
	
	private String columnsFilter(String column){
		String col = StringUtils.removeEnd(column, "*");
		col = StringUtils.removeStart(col, "+");
		col = StringUtils.removeStart(col, "-");
		return col;
	}
	
	public void setColumnNames(String columnNames) {
		setAttribute(COLUMN_NAMES, columnNames);
	}
	
	/**
	 * 获取索引文档索引的属性中文名数组
	 */
	public String[] getColumnNames(){
		return StringUtils.split((String)getAttribute(COLUMN_NAMES), ",");
	}
	
	/**
	 * 获取实体的属性名和对应的索引文档索引的属性
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, String> getEntityPropertiesAndColumns(){
		if(getAttribute(CONFIG_ENTITY_PROPERTIES_AND_COLUMNS) == null){
			String[] entityProperties = getEntityProperties();
			String[] columns = getColumns();
			if (columns.length != entityProperties.length) {
				throw new IllegalStateException("search-objact.properties配置文件中" + getName() + "部分的实体类属性" + entityProperties.toString()
						+ "和检索字段" + columns.toString() + "不匹配");
			}
			setAttribute(CONFIG_ENTITY_PROPERTIES_AND_COLUMNS, createLinkedHashMap(entityProperties, columns));
		}
		return (LinkedHashMap<String, String>)getAttribute(CONFIG_ENTITY_PROPERTIES_AND_COLUMNS);
	}
	
	/**
	 * 获取索引文档索引的属性和对应的中文名称
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, String> getColumnsAndNames(){
		if (getAttribute(CONFIG_COLUMNS_AND_NAMES) == null) {
			String[] columns = getColumns();
			String[] columnNames = getColumnNames();
			if (columns.length != columnNames.length) {
				throw new IllegalStateException("search-objact.properties配置文件中"
						+ getName() + "部分的检索字段" + columns.toString()
						+ "和检索字段名称" + columnNames.toString() + "不匹配");
			}
			setAttribute(CONFIG_COLUMNS_AND_NAMES, createLinkedHashMap(columns, columnNames));
		}
		return (LinkedHashMap<String, String>)getAttribute(CONFIG_COLUMNS_AND_NAMES);
	}
	
	private LinkedHashMap<String, String> createLinkedHashMap(String[] keys, String[] values){
		LinkedHashMap<String, String> linkedHashMap = Maps.newLinkedHashMap();
		for (int i = 0; i < keys.length; i++) {
			linkedHashMap.put(keys[i], values[i]);
		}
		return linkedHashMap;
	}
	
	/**
	 * 设置属性值的方法
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, Object value){
		attributes.put(key, value);
	}
	
	/**
	 * 获得值方法
	 * @param key
	 */
	public Object getAttribute(String key){
		return attributes.get(key);
	}
	
	/**
	 * 获得所有配置属性名的方法
	 */
	public Iterable<String> getAttributeKeys(){
		return attributes.keySet();
	}
}
