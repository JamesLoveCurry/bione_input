/**
 * 
 */
package com.yusys.bione.plugin.valid;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.common.CommonTreeNode;

/**
 * <pre>
 * Title:授权对象接口
 * Description: 定义授权对象的数据接口，用于自定义扩展授权资源，系统默认支持的授权对象都实现了此接口
 * </pre>
 * @author mengzx  
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public interface IValidTypeImpl {
	
	/**
	 * @return
	 */
	public String getAuthObjDefNo();
	
	/**
	 * 返回树形结构的授权对象数据
	 * 数据格式为Id-UpId的形式
	 * @param searchText
	 */
	public List<CommonTreeNode> doGetValidIdxAsync(String groupId, String grpType, String validType,
			CommonTreeNode parentNode, String basePath, String indexVerId, String... searchText);
	/**
	 * 获取异步树的所有节点
	 * @return 
	 */
	public Map<String, String> doGetAllValidTreeNode(String basePath, String groupId, String grpType, String validType);
	
	/**
	 * 搜索同步树
	 */
	public List<CommonTreeNode> doGetSearchSyncTreeNode(String basePath, String groupId, String grpType, String validType, String searchNm);
}
