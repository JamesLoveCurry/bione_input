/**
 * 
 */
package com.yusys.bione.frame.search.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yusys.bione.frame.search.SearchException;
import com.yusys.bione.frame.search.index.IndexDocument;
import com.yusys.bione.frame.search.util.LuceneUtil;

/**
 * <pre>
 * Title:使用lucene进行检索的ISearcher接口的基础实现类
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
public abstract class LuceneBaseSearcher implements ISearcher<IndexDocument, List<IndexDocument>> {
	
	private static final Logger log = LoggerFactory.getLogger(LuceneBaseSearcher.class);
	
	/**
	 * 使用检索关键字进行检索，返回List文档结果集
	 * @param keywords			检索的关键字
	 * @return					List文档结果集
	 */
	public List<IndexDocument> search(String keywords) throws SearchException {
		try {
			if(StringUtils.isEmpty(keywords)){
				return Collections.emptyList();
			}
			return doQuery(getSort(), keywords);
		} catch (IOException e) {
			log.error("使用检索关键字(" + keywords + ")进行检索出现异常！");
			throw new SearchException("使用检索关键字(" + keywords + ")进行检索出现异常！", e);
		}
	}
	
	/**
	 * 使用检索关键字进行检索，返回可遍历的结果集，结果集按照指定拍序列进行排序
	 * @param keywords			检索的关键字
	 * @param sortColumns		排序列
	 * @return					可遍历的结果集
	 */
	public List<IndexDocument> search(String keywords, String... sortColumns)
			throws SearchException {
		try {
			if(StringUtils.isEmpty(keywords)){
				return Collections.emptyList();
			}
			return doQuery(getSort(sortColumns), keywords);
		} catch (IOException e) {
			log.error("使用检索关键字(" + keywords + ")进行检索出现异常！");
			throw new SearchException("使用检索关键字(" + keywords + ")进行检索出现异常！", e);
		}
	}
	
	/**
	 * 指定分页开始索引和显示行数，使用检索关键字进行检索，返回可遍历的结果集
	 * @param keywords			检索的关键字
	 * @param pageFirstIndex	当前页数据的开始索引
	 * @param pageSize			每页显示行数
	 * @return					可遍历的结果集
	 */
	public List<IndexDocument> search(String keywords, int pageFirstIndex, int pageSize) throws SearchException {
		try {
			if(StringUtils.isEmpty(keywords)){
				return Collections.emptyList();
			}
			return doQuery(getSort(), keywords, pageFirstIndex, pageSize);
		} catch (IOException e) {
			log.error("使用检索关键字(" + keywords + ")进行检索出现异常！");
			throw new SearchException("使用检索关键字(" + keywords + ")进行检索出现异常！", e);
		}
	}
	
	/**
	 * 指定分页开始索引和显示行数，使用检索关键字进行检索，返回可遍历的结果集，结果集按照指定拍序列进行排序
	 * @param keywords			检索的关键字
	 * @param pageFirstIndex	当前页数据的开始索引
	 * @param pageSize			每页显示行数
	 * @param sortColumns		排序列
	 * @return					可遍历的结果集
	 */
	public List<IndexDocument> search(String keywords, int pageFirstIndex, int pageSize, String... sortColumns) throws SearchException {
		try {
			if(StringUtils.isEmpty(keywords)){
				return Collections.emptyList();
			}
			return doQuery(getSort(sortColumns), keywords, pageFirstIndex, pageSize);
		} catch (IOException e) {
			log.error("使用检索关键字(" + keywords + ")进行检索出现异常！");
			throw new SearchException("使用检索关键字(" + keywords + ")进行检索出现异常！", e);
		}
	}
	
	/**
	 * 使用lucene检索方法进行检索，并返回系统内部的文档对象
	 * @param searcher			lucene检索对象
	 * @param query				lucene查询对象
	 * @return					List文档结果集
	 */
	protected List<IndexDocument> createIndexDocuments(IndexSearcher searcher, Sort sort, String keywords) throws IOException{
		List<IndexDocument> docs = new ArrayList<IndexDocument>();
		try {
			Query query = createQuery(keywords);
			String[] searchColumns = getSearchColumns();
			for (ScoreDoc scoreDoc : search(searcher, query, LuceneUtil.TOP, sort).scoreDocs) {
				docs.add(createIndexDocument(searcher.doc(scoreDoc.doc), createHighlighter(query), searchColumns, keywords));
			}
			return docs;
		} catch (ParseException e) {
			e.printStackTrace();
			log.warn("对检索关键字(" + keywords + ")进行查询解析出现异常！", e);
			return Collections.emptyList();
		} 
	}
	
	/**
	 * 使用lucene检索方法进行检索，并返回系统内部的文档对象
	 * @param searcher			lucene检索对象
	 * @param query				lucene查询对象
	 * @return					List文档结果集
	 */
	protected List<IndexDocument> createIndexDocuments(IndexSearcher searcher, Sort sort, String keywords, int pageFirstIndex, int pageSize) throws IOException{
		List<IndexDocument> docs = new ArrayList<IndexDocument>();
		try {
			Query query = createQuery(keywords);
			String[] searchColumns = getSearchColumns();
			ScoreDoc[] scoreDocs = search(searcher, query, LuceneUtil.getTop(pageFirstIndex, pageSize), sort).scoreDocs;
			for (int i = pageFirstIndex ; i < scoreDocs.length && i < pageFirstIndex + pageSize; i++) {
				docs.add(createIndexDocument(searcher.doc(scoreDocs[i].doc), createHighlighter(query), searchColumns, keywords));
			}
			return docs;
		} catch (ParseException e) {
			e.printStackTrace();
			log.warn("对检索关键字(" + keywords + ")进行查询解析出现异常！", e);
			return Collections.emptyList();
		}
	}
	
	private TopDocs search(IndexSearcher searcher, Query query, int top, Sort sort) throws IOException{
		if (sort == null) {
			return searcher.search(query, top);
		} else {
			return searcher.search(query, top, sort);
		}
	}
	
	private Highlighter createHighlighter(Query query){
		return new Highlighter(new QueryScorer(query));
	}
	
	/**
	 * 把检索的结果lucene文档转换为系统内部的文档对象
	 * @param document			lucene文档对象
	 * @return					系统内部的文档对象
	 */
	private IndexDocument createIndexDocument(Document document, Highlighter highlighter, String[] searchColumns, String keywords) throws IOException {
		IndexDocument indexDocument = new IndexDocument(null);
		for (IndexableField field : document.getFields()) {
			indexDocument.store(field.name(), ArrayUtils.contains(searchColumns, field.name()) ? convert2HighLighter(highlighter, keywords, document.get(field.name())) : document.get(field.name()));
		}
		return indexDocument;
	}
	
	/**
	 * 把当前结果字符串进行高亮（默认是html的加粗）转换
	 * @param highlighter		Highlighter高亮对象
	 * @param keywords			检索关键字
	 * @param text				结果字符串
	 * @return					转换以后的字符串
	 */
	private String convert2HighLighter(Highlighter highlighter, String keywords, String text) throws IOException {
		try {
			String result = StringUtils.defaultString((highlighter.getBestFragment(LuceneUtil.getAnalyzer(), keywords, text)), "");
			if(result.length() >= text.length()){
				return result;
			} else {
				return text;
			}
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	protected Query createQuery(String keywords) throws ParseException{
		return new MultiFieldQueryParser(LuceneUtil.getVersion(), getSearchColumns(), LuceneUtil.getAnalyzer()).parse(keywords);
	}
	
	abstract protected Sort getSort();
	
	abstract protected Sort getSort(String... sortColumns);
	
	abstract protected String[] getSearchColumns();
	
	abstract protected List<IndexDocument> doQuery(Sort sort, String keywords) throws IOException; 
	
	abstract protected List<IndexDocument> doQuery(Sort sort, String keywords, int pageFirstIndex, int pageSize) throws IOException;
}
