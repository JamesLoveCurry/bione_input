/**
 * 
 */
package com.yusys.bione.frame.mainpage.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignRel;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignRelPK;

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
public class MainpageRelBS extends BaseBS<BioneMpDesignRel> {

	@Transactional(readOnly = false)
	public void saveRel(String[] userIds,String designId){
		for(int i=0; i<userIds.length; i++){
			this.removeEntityByProperty("id.userId", userIds[i]);
			BioneMpDesignRel rel = new BioneMpDesignRel();
			BioneMpDesignRelPK id = new BioneMpDesignRelPK();
			id.setUserId(userIds[i]);
			id.setDesignId(designId);
			rel.setId(id);
			this.saveOrUpdateEntity(rel);
		}
	}
	
	
}
