/**
 * 
 */
package com.yusys.bione.frame.authobj.service;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:机构管理类
 * Description: 提供机构管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author mengzx mengzx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service("orgBS")
@Transactional(readOnly = true)
public class OrgBS extends BaseBS<BioneOrgInfo> {
	protected static Logger log = LoggerFactory.getLogger(OrgBS.class);
	/**
	 * 构造异步机构树
	 * @param nodeId
	 * @return
	 */
	public List<CommonTreeNode> getOrgTree(String nodeId){
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		if(nodeId == null){
			//构造根节点
			CommonTreeNode baseNode = new CommonTreeNode();
			baseNode.setId("base");
			baseNode.setText("全部");
			baseNode.setUpId("0");
			baseNode.setIsParent(isParent("base"));
			baseNode.setIcon(basePath + GlobalConstants4frame.LOGIC_ALL_ICON);
			treeNodes.add(baseNode);
			return treeNodes;
		}
		//非根节点时，获取其子节点
		String jql = "select org from BioneOrgInfo org where org.logicSysNo=?0 ";
		List<BioneOrgInfo> orgs = new ArrayList<BioneOrgInfo>();
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())) {// 普通管理员
			// 获取用户授权机构,将授权机构挂在到
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			if("0".equals(nodeId)){
				jql = "select org from BioneOrgInfo org where 1=1 and org.logicSysNo = ?0 and org.orgSts = ?1 and org.orgNo in ?2";
				orgs = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), GlobalConstants4frame.COMMON_STATUS_VALID, authOrgNos);
				for (BioneOrgInfo org : orgs) {
					if(!authOrgNos.contains(org.getUpNo())){
						CommonTreeNode treeChildNode = new CommonTreeNode();
						treeChildNode.setId(org.getOrgNo());
						treeChildNode.setText(org.getOrgName() + "(" + org.getOrgNo() + ")");
						treeChildNode.setUpId(org.getUpNo());
						treeChildNode.setData(org);
						treeChildNode.setIsParent(true);
						treeChildNode.setIcon(basePath + GlobalConstants4frame.LOGIC_ORG_ICON);
						treeNodes.add(treeChildNode);
					}
				}
			}else{
				jql += " and org.upNo = ?1 order by org.orgNo";
				orgs = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), nodeId);
				for (BioneOrgInfo org : orgs) {
					CommonTreeNode treeChildNode = new CommonTreeNode();
					treeChildNode.setId(org.getOrgNo());
					treeChildNode.setText(org.getOrgName() + "(" + org.getOrgNo() + ")");
					treeChildNode.setUpId(org.getUpNo());
					treeChildNode.setData(org);
					treeChildNode.setIsParent(true);
					treeChildNode.setIcon(basePath + GlobalConstants4frame.LOGIC_ORG_ICON);
					treeNodes.add(treeChildNode);
				}
			}
		} else if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			if(!nodeId.equals("base")){
				jql = jql+" and org.upNo='"+nodeId+"' ";
			}else{
				jql = jql+" and org.upNo='0' ";
			}
			orgs = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			for (BioneOrgInfo org : orgs) {
				CommonTreeNode treeChildNode = new CommonTreeNode();
				treeChildNode.setId(org.getOrgNo());
				treeChildNode.setText(org.getOrgName() + "(" + org.getOrgNo() + ")");
				treeChildNode.setUpId(org.getUpNo());
				treeChildNode.setData(org);
				treeChildNode.setIsParent(isParent(org.getOrgNo()));
				treeChildNode.setIcon(basePath + GlobalConstants4frame.LOGIC_ORG_ICON);
				treeNodes.add(treeChildNode);
			}
		}
		return treeNodes;
	}
	/**
	 * 判断是否有子节点
	 * @param string
	 * @return
	 */
	private Boolean isParent(String nodeId) {
		List<BioneOrgInfo> orglist;
		if(nodeId.equals("base")){
			String jql = "select org from BioneOrgInfo org where org.logicSysNo=?0 ";
			orglist = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		}else{
			String jql = "select org from BioneOrgInfo org where org.upNo=?0 and org.logicSysNo=?1";
			orglist = this.baseDAO.findWithIndexParam(jql,nodeId, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		}
		return orglist.size()==0?false:true;
	}

	/**
	 * 根据机构标识，查询机构信息
	 * 
	 * @param orgNo
	 *            机构标识
	 */
	public BioneOrgInfo findOrgInfoByOrgNo(String orgNo) {
		String sysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		String jql = "select org from  BioneOrgInfo org where org.orgNo=?0 and logicSysNo=?1";
		return this.baseDAO.findUniqueWithIndexParam(jql, orgNo,sysNo);
	}

	/**
	 * 根据机构名称，查询机构信息
	 * 
	 * @param orgNo
	 *            机构标识
	 */
	public List<BioneOrgInfo> findOrgInfoByOrgNm(String orgName) {
		String sysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		String jql = "select org from  BioneOrgInfo org where org.orgName=?0 and logicSysNo=?1";
		return this.baseDAO.findWithIndexParam(jql, orgName,sysNo);
	}
	
	/**
	 * 根据机构标识查询超级管理员系统机构信息
	 * 
	 * @param orgNo
	 * @param logicSysNo
	 *            机构标识
	 */
	public BioneOrgInfo findOrgInfoByOrgNo(String orgNo,String logicSysNo) {
		String jql = "select org from  BioneOrgInfo org where org.orgNo=?0 and logicSysNo=?1";
		return this.baseDAO.findUniqueWithIndexParam(jql, orgNo,logicSysNo);

	}

	@Transactional(readOnly = false)
	public void removeOrgAndDept(String orgId) {
		BioneOrgInfo org = this.getEntityById(orgId);
		String jql = "delete from BioneOrgInfo org where org.orgId=?0 AND org.logicSysNo=?1";
		this.baseDAO.batchExecuteWithIndexParam(jql, orgId, BioneSecurityUtils
				.getCurrentUserInfo().getCurrentLogicSysNo());
		jql = "delete from BioneDeptInfo dept where dept.orgNo =?0 AND dept.logicSysNo=?1";
		this.baseDAO.batchExecuteWithIndexParam(jql, org.getOrgNo(),
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
	}

	@Transactional(readOnly = true)
	public void findLowerOrgInfosByOrgId(String orgNo,
			List<BioneOrgInfo> orgList) {
		String jql = "SELECT org FROM BioneOrgInfo org WHERE org.upNo=?0 AND org.logicSysNo=?1";
		List<BioneOrgInfo> orgInfos = this.baseDAO.findWithIndexParam(jql,
				orgNo, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if (orgInfos != null && orgInfos.size() != 0) {
			orgList.addAll(orgInfos);
			for (BioneOrgInfo org : orgInfos) {
				findLowerOrgInfosByOrgId(org.getOrgNo(), orgList);
			}
		}

	}
	public List<CommonTreeNode> getOrgTreeTemplate(String upId,
			String contextPath) {
		List<BioneOrgInfo> list = new ArrayList<BioneOrgInfo>();
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		if(upId == null || upId.equals("")){
			String jql = "select org from BioneOrgInfo org where org.orgNo =?0";
			list = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		}else{
			String jql = "select org from BioneOrgInfo org where org.upNo = ?0";
			list = this.baseDAO.findWithIndexParam(jql, upId);
		}
		for(BioneOrgInfo org : list){
			CommonTreeNode node = new CommonTreeNode();
			node.setId(org.getOrgNo());
			node.setText(org.getOrgName());
			node.setData(org);
			node.setIsParent(true);
			node.setChildren(new ArrayList<CommonTreeNode>());
			node.setIcon(contextPath + GlobalConstants4frame.LOGIC_ORG_ICON);
			treeNodes.add(node);
		}
		return treeNodes;
	}
	
	public void getAllChildOrgNo(String orgNo,List<BioneOrgInfo>orgList,boolean appendChild){
		
		String jql = "select org from BioneOrgInfo org where org.upNo =?0";
		List<BioneOrgInfo>list = this.baseDAO.findWithIndexParam(jql, orgNo);
		if(list==null||list.isEmpty())
			return ;
		for(BioneOrgInfo info:list  ){
			orgList.add(info);
			if(appendChild)
				getAllChildOrgNo(info.getOrgNo(),orgList,appendChild);
		}
	}
	
	public void getAllChildOrgNos(String orgNo, List<String> orgList, boolean appendChild){
		String jql = "select org.orgId from BioneOrgInfo org where org.upNo =?0";
		List<String> list = this.baseDAO.findWithIndexParam(jql, orgNo);
		if(list==null || list.isEmpty()){
			return;
		}
		for(String org : list  ){
			orgList.add(org);
			if(appendChild){
				getAllChildOrgNos(org,orgList,appendChild);
			}
		}
	}
}