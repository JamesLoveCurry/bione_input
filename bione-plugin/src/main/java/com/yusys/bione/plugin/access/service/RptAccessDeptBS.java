package com.yusys.bione.plugin.access.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.access.repository.RptAccessDeptDao;

/**
 * 
 * <pre>
 * Description:
 * </pre>
 * 
 * @author sunyuming sunym@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptAccessDeptBS extends BaseBS<Object>{
	
	@Autowired 
	private RptAccessDeptDao rptAccessDeptDao;

	public List<CommonTreeNode> getTree(String path,String orgNo) {
		// TODO Auto-generated method stub
		List<CommonTreeNode> node = Lists.newArrayList();
		Map<String , Object> map = new HashMap<String,Object>();
		map.put("orgNo", orgNo);
		List<BioneDeptInfo> list = this.rptAccessDeptDao.getTree(map);
		for(int i = 0;i<list.size();i++){
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setIcon(path+"/"+GlobalConstants4frame.ICON_URL+"/folder.png");
			treeNode.setId(list.get(i).getDeptNo());
			treeNode.setUpId(list.get(i).getUpNo());
			treeNode.setText(list.get(i).getDeptName());
			node.add(treeNode);
		}
		return node;
	}
	
}
