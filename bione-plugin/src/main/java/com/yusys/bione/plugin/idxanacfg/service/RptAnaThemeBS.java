package com.yusys.bione.plugin.idxanacfg.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.rptbank.entity.RptIdxThemeInfo;
/**
 * <pre>
 * Title:指标分析
 * Description: 
 * </pre>
 * 
 * @author yangyf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptAnaThemeBS extends BaseBS<RptIdxThemeInfo>{
	
	// 指标库主题子节点图标
	private String treeNodeIcon = "/images/classics/icons/application_view_list.png";
	
	// 获取上下文路径
	private String getContextPath() {
		return com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_CONTEXT_PATH;
	}
	
	/**
	 *构造指标库主题树
	 * @return
	 */
	public List<CommonTreeNode> getTheTree(){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		List<RptIdxThemeInfo> themelist = this.getAllEntityList();
		if(themelist!=null && themelist.size()>0){
			for(RptIdxThemeInfo PeculiarIdxThemeInfo :themelist){
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(PeculiarIdxThemeInfo.getThemeId());
				treeNode.setUpId("0");
				treeNode.setIcon(this.getContextPath() + treeNodeIcon);
				treeNode.setText(PeculiarIdxThemeInfo.getThemeNm());
				treeNode.setData(PeculiarIdxThemeInfo);
				treeNodes.add(treeNode);
			}
		}
		return treeNodes;
	}
}
