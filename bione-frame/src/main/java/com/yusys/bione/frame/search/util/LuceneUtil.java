/**
 * 
 */
package com.yusys.bione.frame.search.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

/**
 * <pre>
 * Title:lucene的工具类，提供公用的方法或参数
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
public class LuceneUtil {
	
	//默认取得前TOP个检索结果
	public static final int TOP = 1000;

	/**
	 * 得到当前lucene使用的版本信息对象
	 * @return			返回lucene的版本信息对象
	 */
	public static Version getVersion(){
		return Version.LUCENE_44;
	}
	
	/**
	 * 创建中文语分析器
	 */
	public static Analyzer getAnalyzer(){
		return new StandardAnalyzer(getVersion());
	}
	
	/**
	 * 返回检索最大记录数
	 */
	public static int getTop(int pageFirstIndex, int pageSize){
		int total = pageFirstIndex + pageSize;
		return LuceneUtil.TOP < total ? total : LuceneUtil.TOP;
	}
	
}
