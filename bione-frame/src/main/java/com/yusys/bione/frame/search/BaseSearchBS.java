/**
 * 
 */
package com.yusys.bione.frame.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.search.index.IIndexManager;
import com.yusys.bione.frame.search.index.IndexDocument;
import com.yusys.bione.frame.search.index.LuceneIndexManager;
import com.yusys.bione.frame.search.search.ISearcher;
import com.yusys.bione.frame.search.search.LuceneSearcher;
import com.yusys.bione.frame.search.search.MutiLuceneSearcher;
import com.yusys.bione.frame.search.util.CacheUtil;
import com.yusys.bione.frame.search.util.IndexDocumentUtil;

/**
 * <pre>
 * Title:基础索引业务逻辑类
 * Description:集成BaseBS，在其基础上封装常用的索引基础功能，具体的业务逻辑类继承此类后对于基础的增、删、改、查索引操作基本不用编码
 *              对于需要些使用JQL、SQL查询的功能，在继承的类中增加新的方法调用操作索引的实现，此类中不开放这些方法
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
@Transactional(readOnly = true)
public abstract class BaseSearchBS<T> extends BaseBS<T> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseSearchBS.class);
	
	public BaseSearchBS(){
		super();
	}
	
	/**
	 * 保存对象及索引文件
	 * @param entity
	 */
	@Override
	@Transactional(readOnly = false)
	public void saveEntity(T entity) {
		super.saveEntity(entity);
		try {
			log.debug("save index : entity {" + entity.toString() + "}");
			saveIndex(entity);
		} catch (SearchException e) {
			log.error("save index : entity {" + entity.toString() + "}", e);
			e.printStackTrace();
		}
	}

	/**
	 * 修改对象及索引文件
	 * 
	 * @param entity
	 */
	@Override
	@Transactional(readOnly = false)
	public T updateEntity(T entity) {
		T t = super.updateEntity(entity);
		try {
			log.debug("update index : entity {" + entity.toString() + "}");
			updateIndex(entity);
		} catch (SearchException e) {
			log.error("update index : entity {" + entity.toString() + "}", e);
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 保存或者修改对象及索引文件
	 * 
	 * @param entity
	 */
	@Override
	@Transactional(readOnly = false)
	public T saveOrUpdateEntity(T entity) {
		T t = super.saveOrUpdateEntity(entity);
		try {
			log.debug("saveOrUpdate index : entity {" + entity.toString() + "}");
			updateIndex(entity);
		} catch (SearchException e) {
			log.error("saveOrUpdate index : entity {" + entity.toString() + "}", e);
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 * 删除实体对象及索引文件
	 */
	@Override
	@Transactional(readOnly = false)
	public void removeEntity(final T entity) {
		try {
			log.debug("remove index : entity {" + entity.toString() + "}");
			removeIndex(entity);
		} catch (SearchException e) {
			log.error("remove index : entity {" + entity.toString() + "}", e);
			e.printStackTrace();
		}
		super.removeEntity(entity);
	}

	/**
	 * 按id删除对象及索引文件
	 */
	@Override
	@Transactional(readOnly = false)
	public void removeEntityById(final Serializable id) {
		try {
			log.debug("remove index : entity { id : " + id + " }");
			removeIndex(String.valueOf(id));
		} catch (SearchException e) {
			log.error("remove index : entity { id : " + id + " }", e);
			e.printStackTrace();
		}
		super.removeEntityById(id);
	}
	
	/**
	 * 删除实体对象集合及索引文件
	 */
	@Transactional(readOnly = false)
	public void removeEntity(final List<T> entitys) {
		if (entitys == null) {
			return;
		}
		for (T t : entitys) {
			this.removeEntity(t);
		}
	}

	/**
	 * 按属性删除对象及索引文件
	 * @param propertyName 属性名
	 * @param value  属性值，支持多个属性值，属性值之间用，分割
	 */
	@Transactional(readOnly = false)
	public void removeEntityByProperty(final String propertyName, final String value) {
		if (StringUtils.isBlank(value)) {
			return;
		}
		String[] values = StringUtils.split(value, ',');
		List<Object> valueList = Lists.newArrayList();
		if (values != null) {
			for (String val : values) {
				valueList.add(val);
			}
		}
		List<T> objs = super.findByPropertyAndOrderWithParams(propertyName, valueList, null, false);
		this.removeEntity(objs);
	}
	
	/**
	 * 保存对象的索引文件
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void saveIndex(T entity) throws SearchException {
		log.debug("save index : entity {" + entity.toString() + "}");
		IIndexManager<IndexDocument> indexManager = new LuceneIndexManager(getSearchModule().getWriterAndSearcherBuilder());
		indexManager.add(IndexDocumentUtil.createIndexDocument(entity, CacheUtil.getConfig(getSearchModule().getModuleId())));
	}
	
	/**
	 * 修改对象的索引文件
	 * 
	 * @param id
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void updateIndex(String id, T entity) throws SearchException {
		log.debug("update index : entity {" + entity.toString() + "}");
		IIndexManager<IndexDocument> indexManager = new LuceneIndexManager(getSearchModule().getWriterAndSearcherBuilder());
		indexManager.update(id, IndexDocumentUtil.createIndexDocument(entity, CacheUtil.getConfig(getSearchModule().getModuleId())));
	}
	
	/**
	 * 修改对象的索引文件
	 * 
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void updateIndex(T entity) throws SearchException {
		log.debug("update index : entity {" + entity.toString() + "}");
		IIndexManager<IndexDocument> indexManager = new LuceneIndexManager(getSearchModule().getWriterAndSearcherBuilder());
		DefaultSearchAndActionConfig config = CacheUtil.getConfig(getSearchModule().getModuleId());
		Map<Object, Object> bm = IndexDocumentUtil.createBeanMap(entity);
		String entityId = IndexDocumentUtil.getEntityId(bm, config, entity);
		indexManager.update(entityId, IndexDocumentUtil.createIndexDocument(config, entity));
	}
	
	/**
	 * 按id删除对象的索引文件
	 */
	@Transactional(readOnly = false)
	public void removeIndex(String id) throws SearchException {
		log.debug("remove index : entity { id : " + id + " }");
		IIndexManager<IndexDocument> indexManager = new LuceneIndexManager(getSearchModule().getWriterAndSearcherBuilder());
		indexManager.delete(id);
	}
	
	/**
	 * 删除实体对象的索引文件
	 * 
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void removeIndex(T entity) throws SearchException {
		log.debug("remove index : entity {" + entity.toString() + "}");
		IIndexManager<IndexDocument> indexManager = new LuceneIndexManager(getSearchModule().getWriterAndSearcherBuilder());
		String entityId = IndexDocumentUtil.getEntityId(IndexDocumentUtil.createBeanMap(entity), CacheUtil.getConfig(getSearchModule().getModuleId()), entity);
		indexManager.delete(entityId);
	}
	
	/**
	 * 获得系统内部所有对ILuceneWriterAndSearcherBuilder的实现
	 */
	private ILuceneWriterAndSearcherBuilder[] getWriterAndSearcherBuilders(){
		List<ILuceneWriterAndSearcherBuilder> builders = new ArrayList<ILuceneWriterAndSearcherBuilder>();
		for(SearchModule module : SearchModule.values()){
			builders.add(module.getWriterAndSearcherBuilder());
		}
		return builders.toArray(new ILuceneWriterAndSearcherBuilder[0]);
	}
	
	/**
	 * 查询所有索引文件并返回结果
	 * @param keywords			检索关键词
	 * @return
	 */
	public List<IndexDocument> searchAll(String keywords){
		try {
			ISearcher<IndexDocument, List<IndexDocument>> searcher = new MutiLuceneSearcher(getWriterAndSearcherBuilders());
			return searcher.search(keywords);
		} catch (SearchException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	/**
	 * 按检索模块查询并返回结果
	 * @param searchModule		检索模块
	 * @param keywords			检索关键词
	 */
	public List<IndexDocument> search(SearchModule searchModule, String keywords){
		try {
			ISearcher<IndexDocument, List<IndexDocument>> searcher = new LuceneSearcher(searchModule.getWriterAndSearcherBuilder());
			return searcher.search(keywords);
		} catch (SearchException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	/**
	 * 按检索模块查询并返回结果，结果集按照指定拍序列进行排序
	 * @param searchModule		检索模块
	 * @param keywords			检索关键词
	 * @param sortColumns		排序列
	 */
	public List<IndexDocument> search(SearchModule searchModule, String keywords, String... sortColumns){
		try {
			ISearcher<IndexDocument, List<IndexDocument>> searcher = new LuceneSearcher(searchModule.getWriterAndSearcherBuilder());
			return searcher.search(keywords, sortColumns);
		} catch (SearchException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	/**
	 * 内存分页，返回当前页的结果集
	 * @param allList			所有的结果集
	 * @param pageFirstIndex	当前页数据的开始索引
	 * @param pageSize			每页显示行数
	 * @return					当前页结果集
	 */
	@SuppressWarnings("unchecked")
	public List<IndexDocument> getPagingList(List<IndexDocument> allList, int pageFirstIndex, int pageSize){
		int size = allList.size();
		return pageFirstIndex >= size ? Collections.EMPTY_LIST : allList.subList(pageFirstIndex, pageFirstIndex + pageSize > size ? size : pageFirstIndex + pageSize);
	}
	
	/**
	 * 查询当前模块的索引文件并返回结果
	 * @param keywords			检索关键词
	 */
	public List<IndexDocument> search(String keywords){
		return search(getSearchModule(), keywords);
	}
	
	/**
	 * 查询当前模块的索引文件并返回结果，结果集按照指定拍序列进行排序
	 * @param keywords			检索关键词
	 * @param sortColumns		排序列
	 */
	public List<IndexDocument> search(String keywords, String... sortColumns){
		return search(getSearchModule(), keywords);
	}
	
	/**
	 * 把索引文档列表转换为JSON数组对象
	 * @param indexDocuments			
	 */
	public JSONArray toJSONArray(List<IndexDocument> indexDocuments){
		JSONArray arrayTmp = new JSONArray();
		JSONObject jobj = null;
		for(IndexDocument indexDocument : indexDocuments){
			jobj = new JSONObject();
			for(String key : indexDocument.keys()){
				jobj.put(key, indexDocument.get(key));
			}
			arrayTmp.add(jobj);
		}
		return arrayTmp;
	}
	
	/**
	 * 通过模块ID返回对应配置文件中的URL字符串
	 * @param moduleId			
	 */
	public String getUrl(String moduleId){
		DefaultSearchAndActionConfig config = CacheUtil.getConfig(moduleId);
		StringBuilder sb = new StringBuilder();
		sb.append(config.getUrl());
		if (!StringUtils.isEmpty(config.getPkName())) {
			if ("{id}".equals(config.getPkName())) {
				sb.append("/");
			} else {
				if (StringUtils.contains(config.getUrl(), "?")) {
					sb.append("&");
				} else {
					sb.append("?");
				}
				sb.append(config.getPkName()).append("=");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 通过模块ID返回Grid的列信息
	 * @param moduleId			
	 */
	public List<Map<String, String>> getGridColumns(String moduleId){
		if (SearchModule.formString(moduleId) != null) {
			return toListMap(CacheUtil.getConfig(moduleId).getColumnsAndNames());
		} else {
			LinkedHashMap<String, String> results = null , temp = null;
			for(SearchModule searchModule : SearchModule.values()){
				if(results == null){
					results = CacheUtil.getConfig(searchModule.getModuleId()).getColumnsAndNames();
				} else {
					temp = CacheUtil.getConfig(searchModule.getModuleId()).getColumnsAndNames();
					for(String key : results.keySet()){
						if (temp.get(key) == null) {
							results.remove(key);
						}
					}
				}
			}
			if(results == null) results = Maps.newLinkedHashMap();
			//全局检索的列属性
			results.put(GlobalConstants4frame.INDEX_DOCUMENT_MODULE_ID, "类型ID");
			results.put(GlobalConstants4frame.INDEX_DOCUMENT_MODULE_NAME, "类型");
			return toListMap(results);
		}
	}
	
	/**
	 * 把LinkedHashMap对象转换为list对象
	 * @param columnsAndNames			
	 */
	private List<Map<String, String>> toListMap(LinkedHashMap<String, String> columnsAndNames){
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		for(String key : columnsAndNames.keySet()){
			addColumnsAndNames2Map(results, key, columnsAndNames.get(key));
		}
		return results;
	}
	
	private void addColumnsAndNames2Map(List<Map<String,String>> results, String column , String columnsName){
		Map<String,String> map = Maps.newHashMap();
		map.put("column", column);
		map.put("columnsName", columnsName);
		results.add(map);
	}
	
	/**
	 * 根据配置和实体对象列表创建索引文档对象列表
	 * @param entitys
	 * @param config			
	 */
	public List<IndexDocument> createIndexDocument(Iterable<T> entitys, DefaultSearchAndActionConfig config){
		return IndexDocumentUtil.createIndexDocument(entitys, config);
	}
	
	/**
	 * 根据配置和实体对象创建索引文档对象
	 * @param entity			
	 * @param config
	 */
	public IndexDocument createIndexDocument(T entity, DefaultSearchAndActionConfig config){
		return IndexDocumentUtil.createIndexDocument(entity, config);
	}
	
	/**
	 * 实现类要实现该方法，返回创建索引和检索的业务模块枚举
	 */
	public abstract SearchModule getSearchModule();
}
