/**
 * 
 */
package com.yusys.bione.frame.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.search.util.LuceneUtil;


/**
 * <pre>
 * Title:lucene的对象生成器，保证多线程下读写索引的线程安全
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
public class LuceneWriterAndSearcherBuilder implements ILuceneWriterAndSearcherBuilder{
	
	private static final Logger log = LoggerFactory.getLogger(LuceneWriterAndSearcherBuilder.class);

	private IndexWriter indexWriter = null;

	private ArrayList<Thread> writerThreadList = new ArrayList<Thread>();
	
	private String moduleId;

	public LuceneWriterAndSearcherBuilder(String moduleId){
		this.moduleId = moduleId;
		init();
	}
	
	/**
	 * 初始化，如果索引文件夹不存在则创建文件夹
	 */
	private void init(){
		String path = getPath();
		log.debug("初始化索引路径 ： "  + path);
		File indexFile = new File(path);
		if (!indexFile.exists()) {
			indexFile.mkdirs();
		}
	}
	
	/**
	 * 创建生成索引对象的写入对象
	 * @param isCreate			是否重新建立索引文件
	 * @return					索引文件的写对象
	 */
	public IndexWriter getIndexWriter(boolean isCreate) throws IOException {
		Directory dir = null;
		try {
			synchronized (writerThreadList) {
				if (indexWriter == null) {
					dir = getDirectory();
//					if (IndexWriter.isLocked(dir)) {
//						IndexWriter.unlock(dir);
//					}
					indexWriter = new IndexWriter(dir, getIndexWriterConfig(isCreate));
					if (!writerThreadList.contains(Thread.currentThread()))
						writerThreadList.add(Thread.currentThread());
				}
			}
		} finally {
			IOUtils.closeQuietly(dir);
		}
		return indexWriter;
	}

	/**
	 * 创建指定索引的索引对象IndexReader
	 */
	public IndexReader getIndexReader() throws IOException {
//		if (indexReader == null) {
//			indexReader = DirectoryReader.open(getDirectory());
//		} else {
//			try {
//				IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader)indexReader, getIndexWriter(), false);// 读入新增加的增量索引内容，满足实时索引需求
//				if (newReader != null) {
//					indexReader = newReader;
//				}
//			} catch (CorruptIndexException e) {
//				e.printStackTrace();
//			}
//		}
		IndexReader newReader = DirectoryReader.open(getDirectory());
		return newReader;
	}

	/**
	 * 关闭索引的写入对象
	 */
	public void closeIndexWriter() {
		synchronized (writerThreadList) {
			if (writerThreadList.contains(Thread.currentThread()))
				writerThreadList.remove(Thread.currentThread());
			if (writerThreadList.size() == 0) {
				IOUtils.closeQuietly(indexWriter);
				indexWriter = null;
			}
		}
	}
	
	/**
	 * 创建IndexReader指定索引对象的检索对象
	 * @param indexReader		指定的索引对象
	 * @return					检索对象
	 */
	public IndexSearcher getIndexSearcher(IndexReader indexReader) {
		return new IndexSearcher(indexReader);
	}
	
	/**
	 * 关闭索引的检索对象
	 */
	public void closeIndexSearcher(IndexReader indexReader) {
		IOUtils.closeQuietly(indexReader);
		indexReader = null;
	}

	/**
	 * 创建当前的索引目录对象
	 */
	private Directory getDirectory() throws IOException {
		return FSDirectory.open(new File(getPath()));
	}

	/**
	 * 返回当前的索引目录
	 */
	private String getPath(){
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		String path;
		if (servletRequestAttributes != null) {
			path = servletRequestAttributes.getRequest().getSession().getServletContext().getRealPath("/");
		} else if(StringUtils.isNotEmpty(com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_REAL_PATH)) {
			path = com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_REAL_PATH;
		} else {
			path = this.getClass().getResource("/").getPath();
		}
		return path + GlobalConstants4frame.INDEX_PATH + moduleId;
	}

	/**
	 * 创建当前IndexWriter的配置对象
	 */
	private IndexWriterConfig getIndexWriterConfig(boolean isCreate) {
		IndexWriterConfig conf = new IndexWriterConfig(LuceneUtil.getVersion(),
				LuceneUtil.getAnalyzer());
		conf.setOpenMode(isCreate ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
		return conf;
	}
	
	public void setModuleId(String moduleId){
		this.moduleId = moduleId;
	}
	
	public String getModuleId(){
		return moduleId;
	}

}
