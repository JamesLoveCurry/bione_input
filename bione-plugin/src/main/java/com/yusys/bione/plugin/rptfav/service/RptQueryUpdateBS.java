package com.yusys.bione.plugin.rptfav.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolder;
import com.yusys.bione.plugin.rptfav.entity.RptFavQueryins;
import com.yusys.bione.plugin.rptfav.repository.FavFolderInsRelMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavFolderMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavQueryinsMybatisDao;

/**
 * <pre>
 * Description: 功能描述
 * </pre>
 * @author sunyuming  sunym@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容: 
 * </pre>
 */

@Service
@Transactional(readOnly = true)
public class RptQueryUpdateBS {
	
	@Autowired
	private FavQueryinsMybatisDao favQueryinsMybatisDao;
	@Autowired
	private FavFolderMybatisDao favFolderMybatisDao;
	@Autowired
	private FavFolderInsRelMybatisDao favFolderInsRelMybatisDao;

	public void update(String instanceId, String queryNm, String userName,
			String createTime, String remark,String menu) {
		// TODO Auto-generated method stub
		RptFavQueryins rel = new RptFavQueryins();
		rel.setCreateTime(new Timestamp(System.currentTimeMillis()));
		rel.setCreateUser(BioneSecurityUtils.getCurrentUserId());
		rel.setInstanceId(instanceId);
		rel.setQueryNm(queryNm);
		rel.setRemark(remark);
		this.favQueryinsMybatisDao.update(rel);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userIdNew", BioneSecurityUtils.getCurrentUserId());
		map.put("instanceIdNew", instanceId);
		map.put("folderIdNew", menu);
		map.put("userId", BioneSecurityUtils.getCurrentUserId());
		map.put("instanceId", instanceId);
		this.favFolderInsRelMybatisDao.updateRel(map);
	}

	public List<CommonTreeNode> getTreeNode(String contextPath, String folderId) {
		// TODO Auto-generated method stub
		String userId = BioneSecurityUtils.getCurrentUserId();
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("userId", userId);
		//文件夹
		List<RptFavFolder> params = this.favFolderMybatisDao.list(map);
		List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
		for(int i = 0;i<params.size();i++){
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setIcon(contextPath+"/"+GlobalConstants4frame.ICON_URL+"/folder.png");
			treeNode.setId(params.get(i).getFolderId());
			Map<String, String> condition = new HashMap<String, String>();
			treeNode.setParams(condition);
			treeNode.setUpId(params.get(i).getUpFolderId());
			treeNode.setText(params.get(i).getFolderNm());
			list.add(treeNode);
		}
		return list;
	}

	//修改bug时 新增的BS 仅供修改反显用                        查询的是文件夹得信息
	public List<RptFavFolder> findFolderNm(String instanceId,String userId){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("instanceId", instanceId);
		map.put("userId", userId);
		List<RptFavFolder> list = new ArrayList<RptFavFolder>();
		list = this.favFolderMybatisDao.listFolder(map);
		return list;
	}
	
}
