/**
 * 
 */
package com.yusys.bione.frame.search.search;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;

import com.google.common.collect.Lists;
import com.yusys.bione.frame.search.ILuceneWriterAndSearcherBuilder;
import com.yusys.bione.frame.search.index.IndexDocument;
import com.yusys.bione.frame.search.util.CacheUtil;

/**
 * <pre>
 * Title:对于多索引进行全文检索的ISearcher接口的lucene实现类
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
public class MutiLuceneSearcher extends LuceneBaseSearcher {
	
//	private static final Logger log = LoggerFactory.getLogger(MutiLuceneSearcher.class);
	
	private final ILuceneWriterAndSearcherBuilder[] builders;
	
	/**
	 * 构造方法
	 * @param builder			Lucene索引检索对象生成器
	 */
	public MutiLuceneSearcher(ILuceneWriterAndSearcherBuilder... builders){
		this.builders = builders;
	}
	
	/**
	 * 执行检索，返回可遍历的结果集
	 * @param keywords			检索的关键字
	 * @param pageFirstIndex	当前页数据的开始索引
	 * @param pageSize			每页显示行数
	 * @return				List文档结果集
	 */
	protected List<IndexDocument> doQuery(Sort sort, String keywords, int pageFirstIndex, int pageSize) throws IOException {
		IndexReader indexReader = new MultiReader(getIndexReaders());
		try {
			return createIndexDocuments(new IndexSearcher(indexReader), sort, keywords, pageFirstIndex, pageSize);
		} finally {
			indexReader.close();
		}
	}
	
	/**
	 * 执行检索，返回可遍历的结果集
	 * @param query			lucene查询对象
	 * @return				List文档结果集
	 */
	protected List<IndexDocument> doQuery(Sort sort, String keywords) throws IOException {
		IndexReader indexReader = new MultiReader(getIndexReaders());
		try {
			return createIndexDocuments(new IndexSearcher(indexReader), sort, keywords);
		} finally {
			indexReader.close();
		}
	}
	
	/**
	 * 遍历所有索引的对象生成器，取得对应的索引读取对象IndexReader
	 * @return				IndexReader数组
	 */
	private IndexReader[] getIndexReaders() throws IOException {
		IndexReader[] indexReaders = new IndexReader[builders.length];
		for(int i = 0 ; i < builders.length; i++){
			indexReaders[i] = builders[i].getIndexReader();
		}
		return indexReaders;
	}

//	protected Query createQuery(String keywords) throws ParseException {
//		return new QueryParser(LuceneUtil.getVersion(), GlobalConstants4frame.INDEX_DOCUMENT_SEARCH_CONTENT, LuceneUtil.getAnalyzer()).parse(keywords);
//	}

	protected String[] getSearchColumns() {
		String[] columns = null;
		List<String> searchColumns = null;
		for(int i = 0 ; i < builders.length; i++){
			String moduleId = builders[i].getModuleId();
			columns = CacheUtil.getConfig(moduleId).getSearchColumns();
//			if(columns.length == 0){
//				throw new IllegalStateException(moduleId + "-objact.properties配置文件中未给columns属性通过（*）符号指定检索字段！");
//			}
			if (searchColumns == null) {
				searchColumns = Lists.newArrayList(columns);
			} else {
				List<String> result = Lists.newArrayList();
				for(String c : columns){
					if(searchColumns.contains(c)){
						result.add(c);
					}
				}
				searchColumns = result;
			}
		}
		if(searchColumns == null){
			searchColumns = Collections.emptyList();
		}
		return searchColumns.toArray(new String[0]);
	}

	@Override
	protected Sort getSort() {
		//TODO 全文检索使用默认的按照相关度排序
//		return Sort.RELEVANCE;
		return null;
	}

	@Override
	protected Sort getSort(String... sortColumns) {
		//TODO 全文检索使用默认的按照相关度排序
		return null;
	}

}
