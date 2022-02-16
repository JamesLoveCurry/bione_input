/**
 * 
 */
package com.yusys.bione.frame.search.index;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.search.ILuceneWriterAndSearcherBuilder;
import com.yusys.bione.frame.search.SearchException;

/**
 * <pre>
 * Title:lucene对索引操作的实现类
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
public class LuceneIndexManager implements IIndexManager<IndexDocument>{
	
	private static final Logger log = LoggerFactory.getLogger(LuceneIndexManager.class);
	
	private ILuceneWriterAndSearcherBuilder builder;
	
	/**
	 * 构造方法
	 * @param builder		索引文件和检索文件的对象			
	 */
	public LuceneIndexManager(ILuceneWriterAndSearcherBuilder builder){
		this.builder = builder;
	}
	
	/**
	 * 重新建立索引文件，原来记录不保留
	 * @param indexDocuments	可遍历的多个数据文档对象
	 */
	public void create(Iterable<IndexDocument> indexDocuments) throws SearchException {
		try {
			IndexWriter writer = builder.getIndexWriter(true);
			for (IndexDocument indexDocument : indexDocuments) {
				storeModuleId(indexDocument);
				writer.addDocument(indexDocument.convert2Document());
			}
		} catch (IOException e) {
			log.error("重新建立索引文件出现异常！\n" + indexDocuments.toString());
			throw new SearchException("重新建立索引文件出错！", e);
		} finally {
			builder.closeIndexWriter();
		}
	}

	/**
	 * 向索引文件中添加记录，没有索引文件会建立索引文件
	 * @param indexDocuments	数据文档对象
	 */
	public void add(IndexDocument indexDocument) throws SearchException {
		try {
			storeModuleId(indexDocument);
			builder.getIndexWriter(false).addDocument(indexDocument.convert2Document());
			builder.closeIndexWriter();
		} catch (IOException e) {
			log.error("向索引文件中添加记录出现异常！\n" + indexDocument.toString());
			throw new SearchException("向索引文件中添加记录出现异常！", e);
		}
	}
	
	/**
	 * 向索引文件中添加多条记录
	 * @param indexDocuments	可遍历的多个数据文档对象
	 */
	public void add(Iterable<IndexDocument> indexDocuments) throws SearchException {
		try {
			IndexWriter writer = builder.getIndexWriter(false);
			for (IndexDocument indexDocument : indexDocuments) {
				storeModuleId(indexDocument);
				writer.addDocument(indexDocument.convert2Document());
			}
		} catch (IOException e) {
			log.error("向索引文件中添加多条记录出现异常！\n" + indexDocuments.toString());
			throw new SearchException("向索引文件中添加多条记录出现异常！", e);
		} finally {
			builder.closeIndexWriter();
		}
	}
	
	/**
	 * 删除索引文件中id对应的记录
	 * @param id	记录的唯一标识
	 */
	
	public void delete(String id) throws SearchException {
		try {
			builder.getIndexWriter(false).deleteDocuments(new Term(GlobalConstants4frame.INDEX_DOCUMENT_ID, id));
			builder.closeIndexWriter();
		} catch (IOException e) {
			log.error("删除索引文件中id（" + id + "）对应的记录出现异常！");
			throw new SearchException("删除索引文件中id（" + id + "）对应的记录出现异常！", e);
		}
	}
	
	/**
	 * 删除索引文件中的记录
	 * @param id	可遍历的多个记录的唯一标识
	 */
	public void delete(Iterable<String> ids) throws SearchException {
		try {
			IndexWriter writer = builder.getIndexWriter(false);
			for(String id : ids){
				writer.deleteDocuments(new Term(GlobalConstants4frame.INDEX_DOCUMENT_ID, id));
			}
			builder.closeIndexWriter();
		} catch (IOException e) {
			log.error("删除索引文件中的记录出现异常！\n" + ids);
			throw new SearchException("删除索引文件中的记录出现异常！", e);
		}
	}
	
	/**
	 * 更新索引文件中id对应的记录
	 * @param id				记录的唯一标识
	 * @param indexDocument		数据文档对象
	 */
	
	public void update(String id, IndexDocument indexDocument) throws SearchException {
		try {
			storeModuleId(indexDocument);
			builder.getIndexWriter(false).updateDocument(new Term(GlobalConstants4frame.INDEX_DOCUMENT_ID, id), indexDocument.convert2Document());
			builder.closeIndexWriter();
		} catch (IOException e) {
			log.error("更新索引文件中id（" + id + "）对应的记录出现异常！");
			throw new SearchException("更新索引文件中id（" + id + "）对应的记录出现异常！", e);
		}
	}
	
	/**
	 * 合并模块文件夹下的所有索引文件，优化检索的效率
	 */
	
	public void merge() throws SearchException {
		try {
			builder.getIndexWriter(false).forceMerge(1);
			builder.closeIndexWriter();
		} catch (IOException e) {
			log.error("合并模块文件夹下的所有索引文件出现异常！");
			throw new SearchException("合并模块文件夹下的所有索引文件出现异常！", e);
		}
	}

	/**
	 * 回滚操作，注意该操作不是线程安全的，
	 * 如果有多个线程在并发修改，一个线程调用该方法回滚可能会导致同时回滚，
	 * 所以开发人员调用此方法要保证线程的安全问题。
	 */
	
	public void rollback() throws SearchException {
		try {
			builder.getIndexWriter(false).rollback();
			builder.closeIndexWriter();
		} catch (IOException e) {
			log.error("回滚索引文件操作出现异常！");
			throw new SearchException("回滚索引文件操作出现异常！", e);
		}
	}
	
	private IndexDocument storeModuleId(IndexDocument indexDocument){
		indexDocument.store(GlobalConstants4frame.INDEX_DOCUMENT_MODULE_ID, builder.getModuleId());
		return indexDocument;
	}
}
