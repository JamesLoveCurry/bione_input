package com.yusys.bione.plugin.rptfav.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolder;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolderInsRel;
import com.yusys.bione.plugin.rptfav.repository.FavFolderInsRelMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavFolderMybatisDao;
import com.yusys.bione.plugin.rptfav.web.vo.RptFavFolderRelInsVo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;

/**
 * <pre>
 * Description: 功能描述
 * </pre>
 * 
 * @author sunyuming sunym@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容:
 * </pre>
 */

@Service
@Transactional(readOnly = false)
public class RptMyQueryBS extends BaseBS<Object> {

	@Autowired
	private FavFolderMybatisDao favFolderMybatisDao;

	@Autowired
	private FavFolderInsRelMybatisDao favFolderInsRelMybatisDao;

	public List<CommonTreeNode> getTreeNode(String path, String folderNm) {
		String userId = BioneSecurityUtils.getCurrentUserId();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		List<RptFavFolder> params = new ArrayList<RptFavFolder>();
		if (folderNm != null && !folderNm.equals("")) {
			map.put("folderNm", "%" + folderNm + "%");
			// 收索文件夹
			params = this.favFolderMybatisDao.findList(map);
		} else {
			// 文件夹
			params = this.favFolderMybatisDao.list(map);
		}
		// 文件
		List<RptFavFolderRelInsVo> aparms = this.favFolderMybatisDao
				.listInstance(map);
		List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
		for (int i = 0; i < params.size(); i++) {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setIcon(path + GlobalConstants4frame.LOGIC_MODULE_ICON);
			treeNode.setId(params.get(i).getFolderId());
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("type", "catalog");
			treeNode.setParams(condition);
			treeNode.setUpId(params.get(i).getUpFolderId());
			treeNode.setText(params.get(i).getFolderNm());
			list.add(treeNode);
		}
		for (int j = 0; j < aparms.size(); j++) {
			CommonTreeNode treeNodes = new CommonTreeNode();
			// 03 指标查询
			if (aparms.get(j).getInstanceType()
					.equals(GlobalConstants4plugin.INST_TYPE_IDX)) {
				treeNodes.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
						+ "/tag_blue.png");
				Map<String, String> typeMap = new HashMap<String, String>();
				typeMap.put("type", "idx");
				treeNodes.setParams(typeMap);
				treeNodes.setId(aparms.get(j).getInstanceId());
				treeNodes.setUpId(aparms.get(j).getFolderId());
				treeNodes.setText(aparms.get(j).getQueryNm());
				list.add(treeNodes);
			}
			// 04 报表
			if (aparms.get(j).getInstanceType()
					.equals(GlobalConstants4plugin.INST_TYPE_RPT)) {
				treeNodes.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
						+ "/report.png");
				Map<String, String> typeMap = new HashMap<String, String>();
				typeMap.put("type", "report");
				treeNodes.setParams(typeMap);
				treeNodes.setId(aparms.get(j).getInstanceId());
				treeNodes.setUpId(aparms.get(j).getFolderId());
				treeNodes.setText(aparms.get(j).getQueryNm());
				list.add(treeNodes);
			}
		}
		return list;
	}

	private Map<String, CommonTreeNode> createMapCache(
			List<CommonTreeNode> nodes) {
		Map<String, CommonTreeNode> result = Maps.newHashMap();
		for (CommonTreeNode node : nodes) {
			result.put(node.getId(), node);
		}
		return result;
	}

	// list生产成树的方法
	public List<CommonTreeNode> createTreeNode(List<CommonTreeNode> nodes) {
		List<CommonTreeNode> result = Lists.newArrayList();
		Map<String, CommonTreeNode> cache = createMapCache(nodes);
		CommonTreeNode parent = null;
		for (CommonTreeNode node : nodes) {
			parent = cache.get(node.getUpId());
			if (parent == null) {
				result.add(node);
			} else {
				parent.addChildNode(node);
			}
		}
		return result;
	}

	public void getUpId(List<String> upIdList, List<String> idList) {

	}

	public List<RptFavFolder> findRptFavFolder(String folderNm,
			String upFolderId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("folderNm", folderNm);
		map.put("upFolderId", upFolderId);
		String userId = BioneSecurityUtils.getCurrentUserId();
		map.put("userId", userId);
		List<RptFavFolder> params = this.favFolderMybatisDao.list(map);
		return params;
	}

	public void edit(RptFavFolder rptFavFolder) {
		// TODO Auto-generated method stub
		String userId = BioneSecurityUtils.getCurrentUserId();
		rptFavFolder.setUserId(userId);
		String folderId = RandomUtils.uuid2();
		rptFavFolder.setFolderId(folderId);
		this.favFolderMybatisDao.save(rptFavFolder);
	}

	public void update(RptFavFolder rptFavFolder) {
		// TODO Auto-generated method stub
		String userId = BioneSecurityUtils.getCurrentUserId();
		rptFavFolder.setUserId(userId);
		this.favFolderMybatisDao.update(rptFavFolder);
	}

	public String delete(String folderId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("upFolderId", folderId);
		List<RptFavFolder> fol = this.favFolderMybatisDao.list(map);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("folderId", folderId);
		List<RptFavFolderInsRel> rel = this.favFolderInsRelMybatisDao
				.list(params);
		if (fol != null && fol.size() != 0) {
			return "exit";
		} else if (rel != null && rel.size() != 0) {
			return "wenjian";
		} else {
			this.favFolderMybatisDao.delete(params);
			return "";
		}
	}

	/**
	 * 及联删除所有文件
	 * 
	 * @param folderId
	 */
	@Transactional(readOnly = false)
	public void cascadeDelete(String folderId) {
		String rmIdxDim = "delete from RptFavIdxDim t where t.id.instanceId in (select r.id.instanceId from RptFavFolderInsRel r where r.id.folderId in (:ids))";
		String rmIdxDetail = "delete from RptFavIdxDetail t where t.instanceId in (select r.id.instanceId from RptFavFolderInsRel r where r.id.folderId in (:ids))";
		String rmIdxDimFilter = "delete from RptFavIdxDimFilter t where t.id.detailId in (select d.detailId from RptFavIdxDetail d, RptFavFolderInsRel r where d.instanceId=r.id.instanceId and r.id.folderId in (:ids))";
		String rmQueryins = "delete from RptFavQueryins t where t.instanceId in (select r.id.instanceId from RptFavFolderInsRel r where r.id.folderId in (:ids))";
		String rmFolderInsRel = "delete from RptFavFolderInsRel r where r.id.folderId in (:ids)";
		String rmFolder = "delete from RptFavFolder t where t.folderId in (:ids)";

		List<String> result = Lists.newArrayList();
		result.add(folderId);
		getAllFolderId(Arrays.asList(folderId), result);
		
		Map<String,Object> values = new HashMap<String, Object>();
		values.put("ids", result);
		this.baseDAO.batchExecuteWithNameParam(rmIdxDimFilter, values);
		this.baseDAO.batchExecuteWithNameParam(rmIdxDetail, values);
		this.baseDAO.batchExecuteWithNameParam(rmIdxDim, values);
		this.baseDAO.batchExecuteWithNameParam(rmQueryins, values);
		this.baseDAO.batchExecuteWithNameParam(rmFolderInsRel, values);
		this.baseDAO.batchExecuteWithNameParam(rmFolder, values);
	}
	
	private void getAllFolderId(List<String> folderIds, List<String> result) {
		if (folderIds != null && folderIds.size() > 0) {
			String jql = "select t.folderId from RptFavFolder t where t.upFolderId in ?0";
			List<String> ids = this.baseDAO.findWithIndexParam(jql, folderIds);
			result.addAll(ids);
			getAllFolderId(ids, result);
		}
	}
	
	public String getRptType(String rptId) {
		RptMgrReportInfo info = this.getEntityById(RptMgrReportInfo.class, rptId);
		return info !=null ? info.getRptType() : "";
	}
	public RptMgrReportInfo getRptInfo(String rptId) {
		RptMgrReportInfo info = this.getEntityById(RptMgrReportInfo.class, rptId);
		return info;
	}
	
	public List<CommonTreeNode> getStoreTreeReport(String type) {
		String iconpath = GlobalConstants4frame.APP_CONTEXT_PATH + "/" + GlobalConstants4frame.ICON_URL;
		List<CommonTreeNode> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
	
		params.put("userId", BioneSecurityUtils.getCurrentUserId());
		params.put("path", iconpath + "/report.png");
		params.put("type", type);
		list = favFolderMybatisDao.findStoreItems(params);
		return list;
	}
	/**
	 * 取得收藏的报表的树
	 * @param name 报表名称模糊查询
	 * @param type 类型（报表）
	 * @return 收藏的报表的树
	 */
	public List<CommonTreeNode> getStoreTree(String name, String type) {
		String iconpath = GlobalConstants4frame.APP_CONTEXT_PATH + "/" + GlobalConstants4frame.ICON_URL + "/folder.gif";
		List<CommonTreeNode> list = Lists.newArrayList();
		list = getStoreReportTree(name,type);
		Map<String, Object> params = Maps.newHashMap();
		params.put("userId", BioneSecurityUtils.getCurrentUserId());
		if (StringUtils.isEmpty(name)) {
			list.addAll(this.getFindFolder(iconpath, params));
		} else if (list.size() > 0) {
			params.put("nodes", list);
			List<CommonTreeNode> tmp = this.getFindFolder(iconpath, params);
			// 为防止潜在的死循环风险，约束了目录遍历层数
			int count = 100;
			while(count-- > 0 && tmp.size() > 0) {
				list.addAll(tmp);
				params.put("nodes", tmp);
				tmp = this.getFindFolder(iconpath, params);
			}
			list.addAll(this.getFindFolder(iconpath, params));
		}
		return list;
	}
	
	/**
	 * 取得收藏的报表的树
	 * @param name 报表名称模糊查询
	 * @param type 类型（报表）
	 * @return 收藏的报表的树
	 */
	public List<CommonTreeNode> getStoreTreeByRoot(String name, String type) {
		String iconpath = GlobalConstants4frame.APP_CONTEXT_PATH + "/" + GlobalConstants4frame.ICON_URL;
		List<CommonTreeNode> list = Lists.newArrayList();
		CommonTreeNode root=new CommonTreeNode();
		root.setId("0");
		root.setText("我的收藏");
		root.setUpId("");
		root.setIcon(iconpath+"/house.png");
		
		list = getStoreReportTree(name,type);
		list.add(root);
		Map<String, Object> params = Maps.newHashMap();
		params.put("userId", BioneSecurityUtils.getCurrentUserId());
		
		if (StringUtils.isEmpty(name)) {
			list.addAll(this.getFindFolder(iconpath, params));
		} else if (list.size() > 0) {
			params.put("nodes", list);
			List<CommonTreeNode> tmp = this.getFindFolder(iconpath, params);
			// 为防止潜在的死循环风险，约束了目录遍历层数
			int count = 100;
			while(count-- > 0 && tmp.size() > 0) {
				list.addAll(tmp);
				params.put("nodes", tmp);
				tmp = this.getFindFolder(iconpath, params);
			}
			list.addAll(this.getFindFolder(iconpath, params));
		}
		return list;
	}
	
	/**
	 * 漏洞修复 ：MyBatis文件-代码注入 爆发点12 25
	 * @param iconpath
	 * @param params
	 * @return
	 */
	private List<CommonTreeNode> getFindFolder(String iconpath, Map<String, Object> params){
		
		List<CommonTreeNode> rsNodeList = this.favFolderMybatisDao.findFolder(params);
		if(rsNodeList != null) {
			String folderIconPath = iconpath + "/folder.gif";
			for(CommonTreeNode node : rsNodeList) {
				node.setIcon(folderIconPath);
			}
		}else {
			rsNodeList = Lists.newArrayList();
		}
		return rsNodeList;
	}
	
	/**
	 * 取得收藏的报表的树(不带文件夹)
	 * @param name 报表名称模糊查询
	 * @param type 类型（报表）
	 * @return 收藏的报表的树
	 */
	public List<CommonTreeNode> getStoreReportTree(String name, String type) {
		List<CommonTreeNode> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		if (StringUtils.isNotEmpty(name)) {
			params.put("name", "%" + name + "%");
		}
		params.put("userId", BioneSecurityUtils.getCurrentUserId());
		params.put("type", type);
		list = favFolderMybatisDao.findStoreItems(params);
		if(list!=null&&list.size()>0){
			//漏洞修复 ：MyBatis文件-代码注入 爆发点12 25
			String rptIconPath = GlobalConstants4frame.APP_CONTEXT_PATH + "/" + GlobalConstants4frame.ICON_URL + "/report.png";
			for(CommonTreeNode node : list){
				Map<String,String> map=new HashMap<String, String>();
				map.put("nodeType", "03");
				map.put("realId", node.getId());
				node.setIcon(rptIconPath);
				node.setParams(map);
			}
		}
		return list;
	}
	
}
