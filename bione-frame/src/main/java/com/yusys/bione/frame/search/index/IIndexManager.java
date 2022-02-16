/**
 * 
 */
package com.yusys.bione.frame.search.index;

import com.yusys.bione.frame.search.SearchException;

/**
 * <pre>
 * Title:对索引操作的接口
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
public interface IIndexManager<E> {
	
	/**
	 * 重新建立索引文件，原来记录不保留
	 * @param E			可遍历的多个数据文档对象
	 */
	void create(Iterable<E> e) throws SearchException;
	
	/**
	 * 向索引文件中添加记录，没有索引文件会建立索引文件
	 * @param E			数据文档对象
	 */
	void add(E e) throws SearchException;
	
	/**
	 * 向索引文件中添加多条记录
	 * @param E			可遍历的多个数据文档对象
	 */
	void add(Iterable<E> e) throws SearchException;
	
	/**
	 * 删除索引文件中id对应的记录
	 * @param id		记录的唯一标识
	 */
	void delete(String id) throws SearchException;
	
	/**
	 * 删除索引文件中的记录
	 * @param id		可遍历的多个记录的唯一标识
	 */
	void delete(Iterable<String> ids) throws SearchException;
	
	/**
	 * 更新索引文件中id对应的记录
	 * @param id		记录的唯一标识
	 * @param E			数据文档对象
	 */
	void update(String id, E e) throws SearchException;
	
	/**
	 * 合并模块文件夹下的所有索引文件，优化检索的效率
	 */
	void merge() throws SearchException ;
	
	/**
	 * 回滚操作，注意该操作不是线程安全的
	 * 如果有多个线程在并发修改，一个线程调用该方法回滚可能会导致同时回滚
	 * 所以开发人员调用此方法要保证线程的安全问题。
	 */
	void rollback() throws SearchException;
}
