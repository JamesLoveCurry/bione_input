/**
 * 
 */
package com.yusys.bione.frame.search;

import java.io.IOException;

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
public interface IWriterAndSearcherBuilder<W, S, R> {
	
	/**
	 * 创建生成索引对象的写入对象
	 * @param isCreate			是否重新建立索引文件
	 * @return					索引文件的写对象
	 */
	W getIndexWriter(boolean isCreate) throws IOException;
	
	/**
	 * 创建对R指定索引对象的检索对象
	 * @param R					指定的索引对象
	 * @return					检索对象
	 */
	S getIndexSearcher(R r) throws IOException;
	
	/**
	 * 关闭索引的写入对象
	 */
	void closeIndexWriter();
	
	/**
	 * 关闭索引的检索对象
	 * @param R					指定的索引对象
	 */
	void closeIndexSearcher(R r);
	
	/**
	 * 设置模块唯一标识
	 * @param moduleId			模块唯一标识
	 */
	void setModuleId(String moduleId);
	
	/**
	 * 返回模块唯一标识
	 */
	String getModuleId();
	
}
