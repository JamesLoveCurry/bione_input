/**
 * 
 */
package com.yusys.bione.frame.search;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

/**
 * <pre>
 * Title:
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
public interface ILuceneWriterAndSearcherBuilder extends IWriterAndSearcherBuilder<IndexWriter, IndexSearcher, IndexReader> {

	/**
	 * 创建对R指定索引对象的读取对象
	 */
	IndexReader getIndexReader() throws IOException;
}
