package com.yusys.bione.plugin.idxanacfg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorggrp.entity.RptGrpOrgRel;
import com.yusys.bione.plugin.rptorggrp.entity.RptOrgGrp;
/**
 * <pre>
 * Title:指标分析机构组配置BS
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
public class RptAnaOrgBS extends BaseBS<Object>{
	
	/**
	 *获取指标组父节点信息
	 * @return
	 */
	public List<CommonComboBoxNode> getorgGrpCombo(String orgType){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orgNo", BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		String jql ="select grp from RptOrgGrp grp,RptGrpAuthRel rel where 1=1 and rel.id.grpId = grp.grpId and rel.id.orgNo = :orgNo "; 
		if(StringUtils.isNotBlank(orgType)){
			jql += " and grp.orgType = :orgType";
			map.put("orgType", orgType);
		}
		jql += " order by grp.grpNm";
		List<RptOrgGrp> grps = this.baseDAO.findWithNameParm(jql, map);
		if(grps!=null && grps.size()>0){
			for(RptOrgGrp grp :grps){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(grp.getGrpId());
				node.setText(grp.getGrpNm());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	/**
	 * 获取指标组节点信息
	 * @param grpId
	 * @return
	 */
	public List<String> getGrpOrgNos(String grpId){
		List<String> orgNos = new ArrayList<String>();
		List<RptGrpOrgRel> grps = new ArrayList<RptGrpOrgRel>();
		List<String> srcSysCodes = new ArrayList<String>();
		srcSysCodes.add("01");
		srcSysCodes.add("02");
		String jql ="select rel from RptGrpOrgRel rel,RptOrgInfo org where 1=1 and rel.id.grpId = ?0 and org.srcSysCode in ?1 and org.id.orgNo = rel.id.orgNo";
		grps = this.baseDAO.findWithIndexParam(jql, grpId,srcSysCodes);
		if(grps!=null && grps.size()>0){
			for(RptGrpOrgRel grp :grps){
				orgNos.add(grp.getId().getOrgNo());
			}
		}
		return orgNos;
	}
	
	/**
	 * 查询机构数据
	 * @param upId
	 * @return
	 */
	public List<CommonTreeNode> getOrgTree(String upId){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		List<RptOrgInfo> RptOrgInfoList = this.baseDAO.findWithIndexParam("select org from RptOrgInfo org");
		if(RptOrgInfoList.size()>0){
			for(RptOrgInfo RptOrgInfo : RptOrgInfoList){
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(RptOrgInfo.getId().getOrgNo());
				treeNode.setText(RptOrgInfo.getOrgNm());
				treeNode.setUpId(RptOrgInfo.getUpOrgNo());
				treeNode.setData(RptOrgInfo);
				treeNodes.add(treeNode);
			}
		}
		return treeNodes;
	}
}
