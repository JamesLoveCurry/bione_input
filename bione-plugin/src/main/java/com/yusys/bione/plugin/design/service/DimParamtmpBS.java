/**
 * 
 */
package com.yusys.bione.plugin.design.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
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
public class DimParamtmpBS extends BaseBS<Object> {

	public List<CommonTreeNode> getDimTree(String publicDim, String dimName) {
		List<RptDimTypeInfo> list = new ArrayList<RptDimTypeInfo>();
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		if(!StringUtils.isEmpty(publicDim)){
			List<String> queryDimList = Arrays.asList(StringUtils.split(publicDim, ','));
			if(StringUtils.isEmpty(dimName)){
				String jql = "select dim from RptDimTypeInfo dim where dim.dimSts = ?0 and dim.dimTypeNo in(?1) order by dimType desc, dimTypeNm";
				list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.COMMON_BOOLEAN_YES, queryDimList);
			}else{
				String jql = "select dim from RptDimTypeInfo dim where dim.dimSts = ?0 and dim.dimTypeNo in(?1) and dim.dimTypeNm like ?2 order by dimType desc, dimTypeNm";
				list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.COMMON_BOOLEAN_YES, queryDimList, "%" + dimName + "%");
			}
			
		}else{
			if(StringUtils.isEmpty(dimName)){
				String jql = "select dim from RptDimTypeInfo dim where dim.dimSts = ?0 order by dimType desc, dimTypeNm";
				list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			}else{
				String jql = "select dim from RptDimTypeInfo dim where dim.dimSts = ?0 and dim.dimTypeNm like ?2 order by dimType desc, dimTypeNm";
				list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.COMMON_BOOLEAN_YES, "%" + dimName + "%");
			}
		}
		List<CommonTreeNode> treeList = new ArrayList<CommonTreeNode>();
		if(list != null && list.size() > 0){
			for(RptDimTypeInfo info : list){
				CommonTreeNode node = new CommonTreeNode();
				node.setId(info.getDimTypeNo());
				node.setText(info.getDimTypeNm());
				node.setIcon(basePath + "/" + GlobalConstants4frame.ICON_URL + "/list-items.gif");
				Map<String, String> params = new HashMap<String, String>();
				params.put("dimType", info.getDimType());
				params.put("dimTypeStruct", info.getDimTypeStruct());
				node.setParams(params);
				treeList.add(node);
			}
		}
		
		return treeList;
		
	}

	public List<CommonTreeNode> getTreeDimItems(String dimTypeNo,String searchNm) {
		List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
		if(dimTypeNo != null && !dimTypeNo.equals("")){
			String jql = "select info from RptDimItemInfo info where info.id.dimTypeNo =?0 order by info.rankOrder,info.id.dimItemNo";
			List<RptDimItemInfo> items = this.baseDAO.findWithIndexParam(jql, dimTypeNo);
			if(items != null){
				for(RptDimItemInfo info : items){
					
					CommonTreeNode node = new CommonTreeNode();
					node.setId(info.getId().getDimItemNo());
					node.setText(info.getDimItemNm());
					node.setUpId(info.getUpNo());
					if(org.apache.commons.lang3.StringUtils.isNotBlank(searchNm)){
						if(info.getDimItemNm().indexOf(searchNm)>=0){
							list.add(node);
						}
					}
					else{
						list.add(node);
					}
				}
			}
		}
		return createTreeNode(list);
	}

	private Map<String, CommonTreeNode> createMapCache(
			List<CommonTreeNode> nodes) {
		Map<String, CommonTreeNode> result = Maps.newHashMap();
		for (CommonTreeNode node : nodes) {
			result.put(node.getId(), node);
		}
		return result;
	}

	private List<CommonTreeNode> createTreeNode(List<CommonTreeNode> nodes) {
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
	
	public List<CommonComboBoxNode> getSelectDimItems(String dimTypeNo) {
		List<CommonComboBoxNode> list = new ArrayList<CommonComboBoxNode>();
		if(dimTypeNo != null && !dimTypeNo.equals("")){
			String jql = "select info from RptDimItemInfo info where info.id.dimTypeNo =?0";
			List<RptDimItemInfo> items = this.baseDAO.findWithIndexParam(jql, dimTypeNo);
			if(items != null){
				CommonComboBoxNode rootNode = new CommonComboBoxNode();
				rootNode.setId("");
				rootNode.setText("请选择");
				list.add(rootNode);
				for(RptDimItemInfo info : items){
					CommonComboBoxNode node = new CommonComboBoxNode();
					node.setId(info.getId().getDimItemNo());
					node.setText(info.getDimItemNm());
					list.add(node);
				}
			}
		}
		return list;
	}

	public List<CommonComboBoxNode> getOpers(String type) {
		List<CommonComboBoxNode> list = new ArrayList<CommonComboBoxNode>();
		if(type.equals("01")){
			String[] opers = {"=",">","<","<>",">=","<="};
			for(String tmp : opers){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(tmp);
				node.setText(tmp);
				list.add(node);
			}
		}else if(type.equals("02")){
			String[] opers = {"=", "like"};
			String[] text = {"精确查找", "模糊查询"};
			for(int i=0;i<opers.length;i++){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(opers[i]);
				node.setText(text[i]);
				list.add(node);
			}
		}
		return list;
	}

	
}
