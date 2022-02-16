package com.yusys.biapp.input.catalog.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.common.vo.TempleTreeNode;
import com.yusys.biapp.input.dict.entity.RptInputListCatalogInfo;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.security.BioneSecurityUtils;

@Service
@Transactional(readOnly = true)
public class DataInputCatalogBS extends BaseBS<RptInputListCatalogInfo> {

	
	public List<CommonTreeNode> getByType(String type, String logicSysNo) {
		//List<InputCatalogInfo> list = this.baseDao.findByPropertysAndOrder(InputCatalogInfo.class, new String[] { "dirType", "logicSysNo" }, new Object[] { type, logicSysNo }, new String[] { "orderNo", "createTime" }, new boolean[] { false, false });
		List<RptInputListCatalogInfo> list = this.baseDAO.findByPropertysAndOrder( RptInputListCatalogInfo.class,new String[] { "catalogType", "logicSysNo" }, new Object[] { type, logicSysNo }, new String[] { "orderNo", "createTime" }, new boolean[] { false, false });
		//List<InputCatalogInfo> list = Lists.newArrayList();
		List<CommonTreeNode> commonTreeNodeList = new ArrayList<CommonTreeNode>();

		if (list != null) {
			TempleTreeNode treeNode = new TempleTreeNode();
			treeNode.setId("0");
			treeNode.setUpId("0");
			treeNode.setIcon(UdipConstants.ICON_FOLDER);
			treeNode.setText(UdipConstants.DirType.get(type));
			treeNode.setTitle(treeNode.getText());
			treeNode.setData(null);
			commonTreeNodeList.add(treeNode);

			for (RptInputListCatalogInfo info : list) {
				treeNode = new TempleTreeNode();
				treeNode.setId(info.getCatalogId());
				treeNode.setUpId(info.getUpCatalog());
				treeNode.setText(info.getCatalogName());
				treeNode.setTitle(info.getCatalogName());
				treeNode.setData(info);
				treeNode.setIcon(UdipConstants.ICON_FOLDER);
				commonTreeNodeList.add(treeNode);
			}
		}
		return commonTreeNodeList;
	}
	
	public List<CommonTreeNode> getByTypeById(String nodeId,String type, String logicSysNo) {
		
		List<RptInputListCatalogInfo> list = this.baseDAO.findByPropertysAndOrder( RptInputListCatalogInfo.class,new String[] { "catalogType", "logicSysNo","upCatalog" }, new Object[] { type, logicSysNo,nodeId }, new String[] { "orderNo", "createTime" }, new boolean[] { false, false });
		List<CommonTreeNode> commonTreeNodeList = new ArrayList<CommonTreeNode>();

		if (list != null) {
			for (RptInputListCatalogInfo info : list) {
				TempleTreeNode treeNode = new TempleTreeNode();
				treeNode.setId(info.getCatalogId());
				treeNode.setUpId(info.getUpCatalog());
				treeNode.setText(info.getCatalogName());
				treeNode.setTitle(info.getCatalogName());
				treeNode.setData(info);
				treeNode.setIcon(UdipConstants.ICON_FOLDER);
				commonTreeNodeList.add(treeNode);
			}
		}
		return commonTreeNodeList;
	}

	/**
	 * 删除当前功能结点, 及其子孙结点
	 */
	@Transactional(readOnly = false)
	public void removeEntityAndChild(String id) {
		RptInputListCatalogInfo dir = new RptInputListCatalogInfo();
		dir.setCatalogId(id);
		List<RptInputListCatalogInfo> funcList = Lists.newArrayList();
		funcList.add(dir);
		findMiddleFuncNode(0, funcList);

		for (int i = 0; i < funcList.size(); i++) {
			this.removeEntity(funcList.get(i));
		}
	}

	public void findMiddleFuncNode(int index, List<RptInputListCatalogInfo> funcList) {
		int j = funcList.size();
		for (int i = index; i < j; i++) {
			List<RptInputListCatalogInfo> dirList = this.findEntityListByProperty("upCatalog", !StringUtils.isEmpty(funcList.get(i).getCatalogId()) ? funcList.get(i).getCatalogId() : "");
			if (dirList != null && dirList.size() != 0)
				funcList.addAll(dirList);
		}
		if (funcList.size() > j)
			findMiddleFuncNode(j, funcList);
	}

	public List<CommonTreeNode> buildTempleTree(String nodeId,String userName, String logicSysNo, List<String> roles, String roleType) {
			
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		List<CommonTreeNode> dirList = null;
		/** 目录 **/
		if(nodeId == null) {
			dirList = this.getByType(UdipConstants.DIR_TYPE_TEMP, logicSysNo);
		}else {
			dirList = this.getByTypeById(nodeId,UdipConstants.DIR_TYPE_TEMP, logicSysNo);
		}
		nodeList.addAll(dirList);

		return nodeList;
	}

	public List<BioneAuthObjResRel> findByPropertys(String logicSysNo,String objdefNo, String resDefNo) {
			
		String jql = "select t from BioneAuthObjResRel t where t.id.logicSysNo=?0 and t.id.objDefNo=?1 and t.id.resDefNo=?2";
		return this.baseDAO.findWithIndexParam(jql, logicSysNo, objdefNo,resDefNo);
	}
	
	public boolean testSameIndexCatalogNm(String upNo, String catalogName,String catalogId,String catalogType) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  *  FROM RPT_INPUT_LIST_CATALOG_INFO CATALOG WHERE 1=1");
		if(org.apache.commons.lang3.StringUtils.isNotBlank(upNo)) {
			sql.append(" AND CATALOG.up_catalog = '").append(upNo).append("'");
		}
		if(org.apache.commons.lang3.StringUtils.isNotBlank(catalogName)) {
			sql.append(" AND CATALOG.catalog_name = '").append(catalogName).append("'");	
		}
		if(org.apache.commons.lang3.StringUtils.isNotBlank(catalogId)) {
			sql.append(" AND CATALOG.catalog_id != '").append(catalogId).append("'");
		}
		if(org.apache.commons.lang3.StringUtils.isNotBlank(catalogType)) {
			sql.append(" AND CATALOG.catalog_type = '").append(catalogType).append("'");
		}
		List<Object[]> list = this.baseDAO.findByNativeSQLWithIndexParam(sql.toString(), null);
		return list == null ? true : list.size()==0;

	}
	
	
	/**
	 * 创建目录
	 */
	@Transactional(readOnly = false)
	public void createTskCatalog(String catalogType,String catalogId, String catalogNm,String orderNo, String upNo) {
			
		RptInputListCatalogInfo catalog = new RptInputListCatalogInfo();
		
		if (org.apache.commons.lang3.StringUtils.isBlank(upNo)) {
			catalog.setUpCatalog("0");
		}else {
			catalog.setUpCatalog(upNo);
		}
		if (catalogId == null || catalogId.equals("")) {
			catalogId = RandomUtils.uuid2();
		}
		catalog.setCatalogId(catalogId);
		catalog.setCatalogName(catalogNm);
		catalog.setOrderNo(new BigDecimal(orderNo));
		catalog.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		catalog.setCreateTime(DateUtils.getYYYY_MM_DD_HH_mm_ss());
		catalog.setCatalogType(catalogType == null ? UdipConstants.DIR_TYPE_TEMP:catalogType);
		this.saveOrUpdateEntity(catalog);
	}
	
	/**
	 * 该目录及递归获取所有子目录
	 */
	public List<RptInputListCatalogInfo>  getTempleByCatalogid(String catalogId) {
		
		//该目录及递归获取所有子目录
		RptInputListCatalogInfo dir = new RptInputListCatalogInfo();
		dir.setCatalogId(catalogId);
		List<RptInputListCatalogInfo> funcList = Lists.newArrayList();
		funcList.add(dir);
		findMiddleFuncNode(0, funcList);
		return funcList;
	}
	
	/**
	 * 判断所有目录下是否有数据
	 */
	@Transactional(readOnly = false)
	public String deleteCatalog(String catalogType,List<RptInputListCatalogInfo> funcList) {
		//所有目录下的补录模板数量
		StringBuilder ids = new StringBuilder();
		for (int i = 0; i < funcList.size(); i++) {
			if(i > 0) {
				ids.append(",'").append(funcList.get(i).getCatalogId()).append("'");
			}else {
				ids.append("'").append(funcList.get(i).getCatalogId()).append("'");
			}
		}
		
		StringBuilder sql = new StringBuilder();
		if("2".equals(catalogType)) {
			sql.append("select * from rpt_input_list_data_dict_info t where t.catalog_id in (");
		}else {
			sql.append("select * from rpt_input_lst_temple_info t where t.catalog_id in (");
		}
		sql.append(ids).append(")");
		List<Object[]> list = this.baseDAO.findByNativeSQLWithIndexParam(sql.toString(), null);
		//判断模板数量
		if(list.size() > 0) {
			return "0";
		}else {
			for (int i = 0; i < funcList.size(); i++) {
				this.removeEntity(funcList.get(i));
			}
			return "1";
		}
	}
	
}
