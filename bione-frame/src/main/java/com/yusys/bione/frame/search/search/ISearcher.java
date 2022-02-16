/**
 * 
 */
package com.yusys.bione.frame.search.search;

import com.yusys.bione.frame.search.SearchException;

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
public interface ISearcher<E, F extends Iterable<E> > {

	/**
	 * 使用检索关键字进行检索，返回可遍历的结果集
	 * @param keywords			检索的关键字
	 * @return					可遍历的结果集
	 */
	F search(String keywords) throws SearchException;
	
	/**
	 * 使用检索关键字进行检索，返回可遍历的结果集，结果集按照指定拍序列进行排序
	 * @param keywords			检索的关键字
	 * @param sortColumns		排序列
	 * @return					可遍历的结果集
	 */
	F search(String keywords, String... sortColumns) throws SearchException;
	
	/**
	 * 指定分页开始索引和显示行数，使用检索关键字进行检索，返回可遍历的结果集
	 * @param keywords			检索的关键字
	 * @param pageFirstIndex	当前页数据的开始索引
	 * @param pageSize			每页显示行数
	 * @return					可遍历的结果集
	 */
	F search(String keywords, int pageFirstIndex, int pageSize) throws SearchException;
	
	/**
	 * 指定分页开始索引和显示行数，使用检索关键字进行检索，返回可遍历的结果集，结果集按照指定拍序列进行排序
	 * @param keywords			检索的关键字
	 * @param pageFirstIndex	当前页数据的开始索引
	 * @param pageSize			每页显示行数
	 * @param sortColumns		排序列
	 * @return					可遍历的结果集
	 */
	F search(String keywords, int pageFirstIndex, int pageSize, String... sortColumns) throws SearchException;
	
//	/**
//	 * 使用检索关键字对多个字段进行检索，返回可遍历的结果集
//	 * @param keywords			检索的关键字
//	 * @param fields			检索的多个字段名
//	 * @return					可遍历的结果集
//	 */
//	F search(String keywords, String[] fields) throws SearchException;
	
//	/**
//	 * 指定分页开始索引和显示行数，使用检索关键字对多个字段进行检索进行检索，返回可遍历的结果集
//	 * @param keywords			检索的关键字
//	 * @param fields			检索的多个字段
//	 * @param pageFirstIndex	当前页数据的开始索引
//	 * @param pageSize			每页显示行数
//	 * @return					可遍历的结果集
//	 */
//	F search(String keywords, String[] fields, int pageFirstIndex, int pageSize) throws SearchException;
	
}
