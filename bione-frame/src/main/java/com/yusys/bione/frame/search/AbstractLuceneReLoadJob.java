/**
 * 
 */
package com.yusys.bione.frame.search;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.yusys.bione.frame.search.index.LuceneIndexManager;
import com.yusys.bione.frame.search.util.CacheUtil;
import com.yusys.bione.frame.search.util.IndexDocumentUtil;

/**
 * <pre>
 * Title:执行重新加载索引文件任务的抽象类
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
public abstract class AbstractLuceneReLoadJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			SearchModule searchModule = getSearchModule();
			CacheUtil.relaodCache(searchModule.getModuleId());
			LuceneIndexManager indexManager = new LuceneIndexManager(searchModule.getWriterAndSearcherBuilder());
			indexManager.create(IndexDocumentUtil.createIndexDocument(getEntities(), CacheUtil.getConfig(searchModule.getModuleId())));
		} catch (SearchException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回创建索引和检索的业务模块枚举
	 */
	public abstract SearchModule getSearchModule();
	
	/**
	 * 返回该模块要创建索引文件的对象集合
	 */
	public abstract Iterable<?> getEntities();

}
