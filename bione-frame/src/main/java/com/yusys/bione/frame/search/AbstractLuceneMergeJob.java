/**
 * 
 */
package com.yusys.bione.frame.search;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.yusys.bione.frame.search.index.LuceneIndexManager;

/**
 * <pre>
 * Title:执行合并索引文件任务的抽象类
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
public abstract class AbstractLuceneMergeJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			new LuceneIndexManager(getSearchModule().getWriterAndSearcherBuilder()).merge();
		} catch (SearchException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回创建索引和检索的业务模块枚举
	 */
	public abstract SearchModule getSearchModule();

}
