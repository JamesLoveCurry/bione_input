/**
 * 
 */
package com.yusys.bione.frame.mainpage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignDetail;
import com.yusys.bione.frame.mainpage.entity.BioneMpModuleInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:首页模块配置相关BS
 * Description: 首页模块配置相关BS
 * </pre>
 * 
 * @author weijx wijx@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class MainpageModuleBS extends BaseBS<BioneMpModuleInfo> {

	/**
	 * 获取列表数据, 支持查询
	 * 
	 * @param firstResult
	 *            分页的开始索引
	 * @param pageSize
	 *            页面大小
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            搜索条件
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneMpModuleInfo> getMpModuleList(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder("");
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		jql.append("select module from BioneMpModuleInfo module where 1=1 and logicSysNo = :logicSysNo ");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		values.put("logicSysNo", logicSysNo);
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by " + orderBy + " " + orderType);
		}
		return this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
	}
	
	public BioneMpModuleInfo getModuleInfo(String moduleId){
		return this.getEntityById(BioneMpModuleInfo.class, moduleId);
	}
	
	@Transactional(readOnly = false)
	public void saveModule(BioneMpModuleInfo info){
		this.saveOrUpdateEntity(info);
	}
	
	@Transactional(readOnly = false)
	public Map<String,Object> deleteModule(String moduelIds){
		List<String> moduleNms = new ArrayList<String>();
		String moduleIdArray[] = StringUtils.split(moduelIds,",");
		for(int i =0 ; i < moduleIdArray.length ; i++){
			List<BioneMpDesignDetail> details = this.getEntityListByProperty(BioneMpDesignDetail.class, "moduleId", moduleIdArray[i]);
			if(details.size()<=0){
				this.removeEntityById(moduleIdArray[i]);
			}
			else{
				moduleNms.add(this.getEntityById(moduleIdArray[i]).getModuleName());
			}
		}
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("moduleNms", moduleNms);
		return result;
	}
}
