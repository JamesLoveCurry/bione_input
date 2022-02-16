/**
 * 
 */
package com.yusys.bione.frame.search.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import com.yusys.bione.frame.search.DefaultSearchAndActionConfig;
import com.yusys.bione.frame.search.ILuceneWriterAndSearcherBuilder;
import com.yusys.bione.frame.search.index.IndexDocument;
import com.yusys.bione.frame.search.util.CacheUtil;

/**
 * <pre>
 * Title:对于单索引进行检索的ISearcher接口的lucene实现类
 * Description:
 * </pre>
 * 
 * @author tanxu@yuchengtech.com
 * @version
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:	修改人：		修改日期:	修改内容:
 * </pre>
 */
public class LuceneSearcher extends LuceneBaseSearcher{
	
//	private static final Logger log = LoggerFactory.getLogger(LuceneSearcher.class);
	
	private ILuceneWriterAndSearcherBuilder builder;
	
	/**
	 * 构造方法
	 * @param builder			Lucene索引检索对象生成器
	 */
	public LuceneSearcher(ILuceneWriterAndSearcherBuilder builder){
		this.builder = builder;
	}
	
	protected String[] getSearchColumns(){
		String[] searchColumns = CacheUtil.getConfig(builder.getModuleId()).getSearchColumns();
//		if(searchColumns.length == 0){
//			throw new IllegalStateException(moduleId + "-objact.properties配置文件中未给columns属性通过（*）符号指定检索字段！");
//		}
		return searchColumns;
	}
	
	/**
	 * 执行检索，返回可遍历的结果集
	 * @param query			lucene查询对象
	 * @return				List文档结果集
	 */
	protected List<IndexDocument> doQuery(Sort sort, String keywords) throws IOException{
		IndexReader indexReader = null;
		try {
			indexReader = builder.getIndexReader();
			return createIndexDocuments(builder.getIndexSearcher(indexReader), sort, keywords);
		} finally {
			builder.closeIndexSearcher(indexReader);
		}
	}
	
	/**
	 * 执行检索，返回可遍历的结果集
	 * @param keywords			检索的关键字
	 * @param pageFirstIndex	当前页数据的开始索引
	 * @param pageSize			每页显示行数
	 * @return				List文档结果集
	 */
	protected List<IndexDocument> doQuery(Sort sort, String keywords, int pageFirstIndex, int pageSize) throws IOException{
		IndexReader indexReader = null;
		try {
			indexReader = builder.getIndexReader();
			return createIndexDocuments(builder.getIndexSearcher(indexReader), sort, keywords,  pageFirstIndex, pageSize);
		} finally {
			builder.closeIndexSearcher(indexReader);
		}
	}

	@Override
	protected Sort getSort() {
		LinkedHashMap<String, Boolean> sortColumns = CacheUtil.getConfig(builder.getModuleId()).getSortColumns();
		List<SortField> sortFieldes = new ArrayList<SortField>();
		for(String key : sortColumns.keySet()){
			sortFieldes.add(new SortField(key, SortField.Type.STRING, sortColumns.get(key)));
		}
		return sortFieldes.isEmpty() ? null : new Sort(sortFieldes.toArray(new SortField[0]));
	}

	@Override
	protected Sort getSort(String... sortColumns) {
		DefaultSearchAndActionConfig config = CacheUtil.getConfig(builder.getModuleId());
		List<SortField> sortFieldes = new ArrayList<SortField>();
		Boolean isSortable = null;
		for(String sortColumn : sortColumns){
			isSortable = config.isSortable(sortColumn);
			if(isSortable != null){
				sortFieldes.add(new SortField(config.convertColumn2SortSpecColumn(sortColumn), SortField.Type.STRING, isSortable));
			}
		}
		return sortFieldes.isEmpty() ? null : new Sort(sortFieldes.toArray(new SortField[0]));
	}
	
}
