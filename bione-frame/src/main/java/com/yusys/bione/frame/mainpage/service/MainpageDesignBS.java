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
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignFunc;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignRel;
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
public class MainpageDesignBS extends BaseBS<BioneMpDesignFunc> {


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
		jql.append("select design from BioneMpDesignFunc design where 1=1 and logicSysNo = :logicSysNo ");
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
	
	public BioneMpDesignFunc getDesign(String designId){
		return this.getEntityById(BioneMpDesignFunc.class, designId);
	}
	@Transactional(readOnly = false)
	public void saveModule(BioneMpDesignFunc info){
		this.saveOrUpdateEntity(info);
	}
	@Transactional(readOnly = false)
	public Map<String,Object> deleteDesign(String designIds){
		List<String> designNms = new ArrayList<String>();
		String designIdArray[] = StringUtils.split(designIds,",");
		for(int i =0 ; i < designIdArray.length ; i++){
			List<BioneMpDesignRel> details = this.getEntityListByProperty(BioneMpDesignRel.class, "id.designId", designIdArray[i]);
			if(details.size()<=0){
				this.removeEntityById(designIdArray[i]);
			}
			else{
				designNms.add(this.getEntityById(designIdArray[i]).getDesignNm());
			}
		}
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("designNms", designNms);
		return result;
	}
	
	public List<BioneMpDesignFunc> getMainPageTree(){
		List<BioneMpDesignFunc> funcs =  this.getEntityListByProperty(BioneMpDesignFunc.class, "logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		return funcs;
	}
	
	
}
