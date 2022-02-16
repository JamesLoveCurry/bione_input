/**
 * 
 */
package com.yusys.bione.frame.search;

import java.io.Serializable;

/**
 * <pre>
 * Title:配置对象的接口
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
public interface ISearchAndActionConfig extends Serializable {
	
	/**
	 * 设置属性值的方法
	 * @param key
	 * @param value
	 */
	void setAttribute(String key, Object value);

	/**
	 * 获得值方法
	 * @param key
	 */
	Object getAttribute(String key);
	
	/**
	 * 获得所有配置属性名的方法
	 */
	Iterable<String> getAttributeKeys();
}
