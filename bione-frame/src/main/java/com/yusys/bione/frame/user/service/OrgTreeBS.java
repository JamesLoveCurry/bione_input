package com.yusys.bione.frame.user.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.util.SplitStringBy1000;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional(readOnly = true)
public class OrgTreeBS extends BaseBS<BioneOrgInfo> {

	protected static Logger log = LoggerFactory.getLogger(OrgTreeBS.class);

	/**
	 * 根据条件查找匹配的所有机构
	 * 
	 * @param logicSystem
	 * 			逻辑系统标识
	 * @param conditionMap
	 * 			生成的搜索查询语句->BaseController->buildSerachCondition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void findAllValidOrg(String logicSystem, Map<String, Object> conditionMap, List<CommonTreeNode> commonTreeNodeList) {
		
		StringBuilder jql = new StringBuilder("");
		Map<String, Object> values = Maps.newHashMap();
		values.put("logicSysNo",logicSystem);
		values.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		jql = new StringBuilder("select org from BioneOrgInfo org where org.orgSts = :orgSts");
		jql.append(" and org.logicSysNo = :logicSysNo");
		boolean key = false;
		if(key = !"".equals(conditionMap.get("jql"))) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		Map<String, ?> params = (Map<String, ?>) conditionMap.get("params");
		
		Iterator<?> iter = params.entrySet().iterator();
		
		while(iter.hasNext()) {
			
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
			
			values.put(entry.getKey(), entry.getValue());
		}
		//edit by zhongqh 按照机构编码递增排序
		jql.append(" order by org.orgNo asc");
		// 获取所有效部门信息
		List<BioneOrgInfo> orgInfoList = this.baseDAO.findWithNameParm(jql.toString(), values);
		
		// 树结构的完整性检查及修复
		// 此处 index 为所有匹配查询条件的搜索结果的 “结束索引   + 1”
		int index = orgInfoList.size();
		if(index != 0 && key) {
			repairUpOrg(orgInfoList, 0, commonTreeNodeList);
			
			// 此处   p 为所有匹配搜索节点的下级节点的开始索引
			int p = orgInfoList.size();
			
			// 执行目标节点的子节点修复
			for (int i = 0; i < index; i++) {
				List<BioneOrgInfo> downOrgInfoList = findEntityListByProperty("upNo", orgInfoList.get(i).getOrgNo());
				if(downOrgInfoList != null) {
					for(BioneOrgInfo orgInfo : downOrgInfoList) {
						
						// 查检结果集中是否已存在
						int t = 0, length = orgInfoList.size();
						for( ; t < length; t++) {
							if(orgInfo.getOrgId().equals(orgInfoList.get(t).getOrgId())) {
								break;
							}
						}
						
						// 若提前退出上面的循环, 则表明存在, 否则说明不存在
						if(t >= length) {
							orgInfoList.add(orgInfo);
							CommonTreeNode treeNode = new CommonTreeNode();
							treeNode.setId(orgInfo.getOrgNo());
							treeNode.setUpId(orgInfo.getUpNo());
							treeNode.setText(orgInfo.getOrgName());
							treeNode.setData(orgInfo);
							commonTreeNodeList.add(treeNode);
						}
					}
				}
			}
			
			// 执行目标节点的孙以及孙的后代节点修复
			repairDownOrg(orgInfoList, p, commonTreeNodeList);
		} else if(index != 0 && !key) {
			for(BioneOrgInfo orgInfo : orgInfoList) {
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(orgInfo.getOrgNo());
				treeNode.setUpId(orgInfo.getUpNo());
				treeNode.setText(orgInfo.getOrgName());
				treeNode.setData(orgInfo);
				commonTreeNodeList.add(treeNode);
			}
		} else {
			return;
		}
	}
	
	/**
	 * 树结构的完整性检查及修复
	 * 			修复上级
	 * @param orgInfoList
	 * 			机构集合
	 * @param index
	 * 			已遍历到的索引值
	 */
	public void repairUpOrg(List<BioneOrgInfo> orgInfoList, int index, List<CommonTreeNode> commonTreeNodeList) {
		boolean flag = false;
		BioneOrgInfo orgInfo;
		for(int length = orgInfoList.size(); index < length; index++) {
			orgInfo = orgInfoList.get(index);
			
			// begin 封装的树
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(orgInfo.getOrgNo());
			treeNode.setUpId(orgInfo.getUpNo());
			treeNode.setText(orgInfo.getOrgName());
			treeNode.setData(orgInfo);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("open", "true");
			treeNode.setParams(paramMap);
			commonTreeNodeList.add(treeNode);
			// end
			
			if(orgInfo.getUpNo().equals(CommonTreeNode.ROOT_ID)) {
				continue;
			}
			
			// 如果存在上级, 则将 flag 置为 true, 否则, 保持 false
			for(BioneOrgInfo _orgInfo : orgInfoList) {
				if(orgInfo.getUpNo().equals(_orgInfo.getOrgNo())) {
					flag = true;
					break;
				}
			}
			
			// 如果不存在上级, 则进行修复
			if(!flag) {
				BioneOrgInfo orgInfo_ = findUniqueEntityByProperty("orgNo", orgInfo.getUpNo());
				if(orgInfo_ == null) {
					
					// 如果找不到上级节点, 则此节点为异常节点, 将此节点的上级标识为"0", 即根节点标识, 
					// 此为辅助措施, 破坏了树结构, 但保证了数据完整性, 可帮助使用者发现。
					orgInfo.setUpNo("0");
					
				} else {
					orgInfoList.add(orgInfo_);
				}
				repairUpOrg(orgInfoList, index + 1, commonTreeNodeList);
			}
			flag = false;
		}
	}
	
	/**
	 * 树结构的完整性检查及修复
	 * 			修复下级
	 * @param orgInfoList
	 * 			机构集合
	 * @param index
	 * 			查询节点的目标索引
	 */
	public void repairDownOrg(List<BioneOrgInfo> orgInfoList, int index, List<CommonTreeNode> commonTreeNodeList) {
		for (int i = index; i < orgInfoList.size(); i++) {
			List<BioneOrgInfo> downOrgInfoList = findEntityListByProperty("upNo", orgInfoList.get(i).getOrgNo());
			if(downOrgInfoList != null) {
				for(BioneOrgInfo orgInfo : downOrgInfoList) {
					
					// 查检结果集中是否已存在
					int t = 0, length = orgInfoList.size();
					for( ; t < length; t++) {
						if(orgInfo.getOrgId().equals(orgInfoList.get(t).getOrgId())) {
							break;
						}
					}
					
					// 若提前退出上面的循环, 则表明存在, 否则说明不存在
					if(t >= length) {
						orgInfoList.add(orgInfo);
						CommonTreeNode treeNode = new CommonTreeNode();
						treeNode.setId(orgInfo.getOrgNo());
						treeNode.setUpId(orgInfo.getUpNo());
						treeNode.setText(orgInfo.getOrgName());
						treeNode.setData(orgInfo);
						commonTreeNodeList.add(treeNode);
					}
				}
			}
		}
	}

	/**
	 * 获取用于构造树的信息
	 * 
	 * @param isLoop
	 *          是否遍历子节点
	 * @param conditionMap
	 * 			生成的搜索查询语句->BaseController->buildSerachCondition
	 * @return commonTreeNodeList
	 * 			部门信息列表
	 */
	public List<CommonTreeNode> buildOrgTree(String logicSystem, boolean isLoop, Map<String, Object> conditionMap) {

		List<CommonTreeNode> commonTreeNodeList = Lists.newArrayList();
		
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId("0");
		treeNode.setUpId("0");
		treeNode.setIcon(null);
		treeNode.setText("机构树");
		treeNode.setData(null);
		
		Map<String, String> params = Maps.newHashMap();
		params.put("open", "true");
		
		treeNode.setParams(params);
		
		commonTreeNodeList.add(treeNode);

		this.findAllValidOrg(logicSystem, conditionMap, commonTreeNodeList);
			
		return commonTreeNodeList;
	}

	/**
	 * 获取 机构标识 与 机构名称 信息列表,准备存入缓存
	 * 
	 * @param logicSysNo
	 * 			逻辑系统标识
	 * 
	 * @return
	 */
	public Map<String, Map<String, Object>> findOrgNoAndOrgName(Collection<?> logicSysNo) {
		
		StringBuilder jql = new StringBuilder("");
		jql.append("select org.orgNo, org.orgName, org.logicSysNo from BioneOrgInfo org where org.orgSts = :orgSts and org.logicSysNo = :logicSysNo");
		
		Iterator<?> iter = logicSysNo.iterator();
		
		Map<String, Map<String, Object>> orgNoAndNameMapMap = Maps.newHashMap();
		
		while(iter.hasNext()) {
			
			Map<String, Object> values = Maps.newHashMap();
			values.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
			values.put("logicSysNo", iter.next());
			
			List<Object[]> objList = this.baseDAO.findWithNameParm(jql.toString(), values);
			
			if(!objList.isEmpty()) {
				
				Map<String, Object> orgNoAndNameMap = Maps.newHashMap();
				
				for(Object[] obj : objList) {
					orgNoAndNameMap.put(obj[0] + "", obj[1]);
				}
				
				orgNoAndNameMapMap.put((String) objList.get(0)[2], orgNoAndNameMap);
			}
		}
		return orgNoAndNameMapMap;
	}

	public List<CommonTreeNode> getOrgTree(String id, String search) {
 		String jql = "select org from BioneOrgInfo org where 1=1 and org.orgSts = ?0";
		List<CommonTreeNode> result = new ArrayList<CommonTreeNode>();
		if(!StringUtils.isEmpty(search)){
			jql += " and (org.orgName like ?1 or org.orgNo like ?2) order by org.orgNo";
			List<BioneOrgInfo> list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4frame.COMMON_STATUS_VALID, "%" + search + "%", "%" + search + "%");
			List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				if("Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){ //管理者
					List<String> curOrgNo = Lists.newArrayList();
					List<String> orgNos = Lists.newArrayList();
					// 获取用户授权机构
					List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
					curOrgNo.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
					orgNos.addAll(authOrgNos);
					// 获取辖内机构
					this.getAllChildBioneOrgNo(curOrgNo, orgNos);
					for(BioneOrgInfo org : list){
						if(orgNos.contains(org.getOrgNo())){
							CommonTreeNode node = new CommonTreeNode();
							node.setId(org.getOrgNo());
							node.setText(org.getOrgName());
							node.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
							node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_ORG_ICON);
							node.setIsParent(false);
							childList.add(node);
						}
					}
				}
			}else{
				for(BioneOrgInfo org : list){
					CommonTreeNode node = new CommonTreeNode();
					node.setId(org.getOrgNo());
					node.setText(org.getOrgName());
					node.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
					node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_ORG_ICON);
					node.setIsParent(false);
					childList.add(node);
				}
			}
			CommonTreeNode rootNode = new CommonTreeNode();
			rootNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
			rootNode.setText("机构树");
			rootNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
			rootNode.setIsParent(childList.size() > 0);
			rootNode.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
			rootNode.setChildren(childList);
			result.add(rootNode);
		}else{
			List<BioneOrgInfo> list = new ArrayList<BioneOrgInfo>();
			if(!StringUtils.isEmpty(id)){
				if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {// 普通用户
					// 获取用户授权机构,将授权机构挂在到
					List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
					if("0".equals(id)){
//						jql = "select org from BioneOrgInfo org where 1=1 and org.orgSts = ?0 and org.orgNo in ?1";
//						list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4frame.COMMON_STATUS_VALID, authOrgNos);
						jql = "select org from BioneOrgInfo org where org.orgSts = :orgSts ";
						Map<String,Object> params = new HashMap<>();
						params.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
						jql += " and "+splitListSql(authOrgNos, params, "org.orgNo", "orgNo");

						list = this.baseDAO.findWithNameParm(jql, params);
						for(BioneOrgInfo org : list){
							if(!authOrgNos.contains(org.getUpNo())){
								CommonTreeNode node = new CommonTreeNode();
								node.setId(org.getOrgNo());
								node.setText(org.getOrgName());
								node.setUpId(org.getUpNo());
								node.setIsParent(true);
								node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_ORG_ICON);
								result.add(node);
							}
						}
					}else{
						jql += " and org.upNo = ?1 order by org.orgNo";
						list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4frame.COMMON_STATUS_VALID, id);
						for(BioneOrgInfo org : list){
							CommonTreeNode node = new CommonTreeNode();
							node.setId(org.getOrgNo());
							node.setText(org.getOrgName());
							node.setUpId(org.getUpNo());
							node.setIsParent(true);
							node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_ORG_ICON);
							result.add(node);
						}
					}
				}else{
					jql += " and org.upNo = ?1 order by org.orgNo";
					list = this.baseDAO.findWithIndexParam(jql, GlobalConstants4frame.COMMON_STATUS_VALID, id);
					for(BioneOrgInfo org : list){
						CommonTreeNode node = new CommonTreeNode();
						node.setId(org.getOrgNo());
						node.setText(org.getOrgName());
						node.setUpId(org.getUpNo());
						node.setIsParent(true);
						node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_ORG_ICON);
						result.add(node);
					}
				}
			}else{
				CommonTreeNode node = new CommonTreeNode();
				node.setId(GlobalConstants4frame.TREE_ROOT_NO);
				node.setText("机构树");
				node.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
				node.setIsParent(true);
				node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
				result.add(node);
			}
		}
		return result;
	}

	public static String splitListSql(List<String> lists,Map<String,Object> params,String name,String pName){
		String jql = "( ";
		List<List<String>> allOrgs = SplitStringBy1000.change(lists);
		int i = 0;
		for(List<?> newlist : allOrgs){
			jql += " "+name+" in (:"+pName+i+")";
			params.put(pName+i, newlist);
			if(i < allOrgs.size() -1){
				jql += " or ";
			}
			else{
				jql += " ) ";
			}
			i++;
		}
		return jql;
	}
	
	private void getAllChildBioneOrgNo(List<String> orgs,List<String> allowOrgs) {
		if (orgs != null && orgs.size() > 0) {
			List<List<String>> orgLists = SplitStringBy1000.change(orgs);
			List<String> upOrgs = new ArrayList<String>();
			for(List<String> orgList : orgLists){
				String jql = "select org from BioneOrgInfo org where org.upNo in ?0";
				List<BioneOrgInfo> neworgs = this.baseDAO.findWithIndexParam(jql, orgList);
				if (neworgs != null && neworgs.size() > 0) {
					for (BioneOrgInfo org : neworgs) {
						if(!allowOrgs.contains(org.getOrgNo())){
							allowOrgs.add(org.getOrgNo());
							upOrgs.add(org.getOrgNo());
						}
					}
				}
			}
			if (upOrgs.size() > 0) {
				getAllChildBioneOrgNo(upOrgs, allowOrgs);
			}
		}
	}

	public Map<String,List<CommonTreeNode>> getAllOrgTree() {
		EhcacheUtils.remove("orgMap", "BIONE_FRS");
		Map<String,List<CommonTreeNode>> common = (Map<String, List<CommonTreeNode>>) EhcacheUtils.get("orgMap", "BIONE_FRS");
		if(common!= null){
			return common;
		} else {
			common = makeMap(false);
			EhcacheUtils.put("orgMap", "BIONE_FRS", common);
			return common;
		}
	}

	private  Map<String,List<CommonTreeNode>> makeMap(boolean isParent){
		Map<String,List<CommonTreeNode>> map=new HashMap<String, List<CommonTreeNode>>();
		List<BioneOrgInfo> childfrsOrgs = getAllOrg();
		if(childfrsOrgs!=null&&childfrsOrgs.size()>0){
			for(BioneOrgInfo org:childfrsOrgs){
				if(map.get(org.getUpNo())==null){
					List<CommonTreeNode> orgs=new ArrayList<CommonTreeNode>();
					CommonTreeNode childNode = new CommonTreeNode();
					childNode.setId(org.getOrgNo());
					childNode.setText(org.getOrgName());
					childNode.setUpId(org.getUpNo());
					Map<String, String> nodeParams_c = Maps.newHashMap();
					nodeParams_c.put("type", "org");
//					nodeParams_c.put("orgType", orgType);
					nodeParams_c.put("realId", org.getOrgNo());
					nodeParams_c.put("isParent", "true");
					childNode.setParams(nodeParams_c);
					childNode.setData(org);
					if(!isParent || checkOrgParent(org.getOrgNo())){
						orgs.add(childNode);
						map.put(org.getUpNo(), orgs);
					}
				}
				else{
					CommonTreeNode childNode = new CommonTreeNode();
					childNode.setId(org.getOrgNo());
					childNode.setText(org.getOrgName());
					childNode.setUpId(org.getUpNo());
					Map<String, String> nodeParams_c = Maps.newHashMap();
					nodeParams_c.put("type", "org");
//					nodeParams_c.put("orgType", orgType);
					nodeParams_c.put("realId", org.getOrgNo());
					nodeParams_c.put("isParent", "true");
					childNode.setParams(nodeParams_c);
					childNode.setData(org);
					if(!isParent || checkOrgParent(org.getOrgNo())){
						map.get(org.getUpNo()).add(childNode);
					}
				}
			}
		}
		return map;
	}

	private boolean checkOrgParent(String orgNo) {
		String jql =" select org from BioneOrgInfo org where org.upNo = ?0";
		List<BioneOrgInfo> orgs = this.baseDAO.findWithIndexParam(jql, orgNo);
		return orgs.size() > 0;
	}

	private List<BioneOrgInfo> getAllOrg() {
		String jql = "select org from BioneOrgInfo org where org.orgSts = :orgSts ";
		Map<String,Object> params = new HashMap<>();
		params.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		return this.baseDAO.findWithNameParm(jql, params);
	}

	public Map<String,String> getAllOrgData() {
		Map<String,String> orgInfo = (Map<String, String>) EhcacheUtils.get("orgData", "BIONE");
		if(orgInfo != null){
			return orgInfo;
		} else{
			orgInfo = new HashMap<>();
			Map<String,Object> map = new HashMap<>();
			map.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
			String jql = "select org from BioneOrgInfo org where org.orgSts = :orgSts";
			List<BioneOrgInfo> orgs = this.baseDAO.findWithNameParm(jql, map);
			if(orgs !=null && orgs.size()>0){
				for(BioneOrgInfo org : orgs){
					orgInfo.put(org.getOrgNo(), org.getOrgName());
				}
			}
			EhcacheUtils.put("orgData", "BIONE", orgInfo);
			return orgInfo;
		}
	}
}
