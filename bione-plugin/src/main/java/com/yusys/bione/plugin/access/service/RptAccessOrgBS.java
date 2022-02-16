package com.yusys.bione.plugin.access.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.access.repository.RptAccessOrgDao;

/**
 * 
 * <pre>
 * Description:
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptAccessOrgBS {
	@Autowired
	private RptAccessOrgDao orgDao;

	public List<CommonTreeNode> getOrgTree(String id) {
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(id)){
			condition.put("upNo", id);
		}else{
			condition.put("upNo", "0");
		}
		List<BioneOrgInfo> list = this.orgDao.list(condition);
		List<CommonTreeNode> result = new ArrayList<CommonTreeNode>();
		for(BioneOrgInfo org : list){
			CommonTreeNode node = new CommonTreeNode();
			node.setId(org.getOrgNo());
			node.setIsParent(true);
			node.setText(org.getOrgName());
			node.setUpId(org.getUpNo());
			node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + "/images/classics/icons/mnuTree.gif");
			result.add(node);
		}
		return result;
	}

}
