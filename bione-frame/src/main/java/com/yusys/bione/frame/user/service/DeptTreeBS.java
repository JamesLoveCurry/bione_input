package com.yusys.bione.frame.user.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;


@Service
@Transactional(readOnly = true)
public class DeptTreeBS extends BaseBS<BioneDeptInfo> {

	protected static Logger log = LoggerFactory.getLogger(DeptTreeBS.class);

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
	public void findAllValidDept(String logicSystem, Map<String, Object> conditionMap, String orgNo, List<CommonTreeNode> commonTreeNodeList) {

		StringBuilder jql = new StringBuilder("");
//		jql.append("select logicSys.basicDeptSts from BioneLogicSysInfo logicSys where logicSys.logicSysNo = :logicSysNo");
		
		Map<String, Object> values = Maps.newHashMap();
		values.put("logicSysNo", logicSystem);
		
//		List<Object> objList = this.baseDAO.findWithNameParm(jql.toString(), values);
		
		jql = new StringBuilder("select dept from BioneDeptInfo dept where dept.deptSts = :deptSts and dept.orgNo = :orgNo");
//		if(objList != null && GlobalConstants4frame.COMMON_STATUS_VALID.equals(objList.get(0).toString())) {
//			values = Maps.newHashMap();
//		} else {
			jql.append(" and dept.logicSysNo = :logicSysNo");
//		}
		
		values.put("deptSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		values.put("orgNo", orgNo);
		
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

		// 获取所有效机构
		List<BioneDeptInfo> deptInfoList = this.baseDAO.findWithNameParm(jql.toString(), values);
		
		// 树结构的完整性检查及修复
				// 此处 index 为所有匹配查询条件的搜索结果的 “结束索引   + 1”
				int index = deptInfoList.size();
				if(index != 0 && key) {
					repairUpDept(deptInfoList, 0, orgNo, commonTreeNodeList);
					
					// 此处   p 为所有匹配搜索节点的下级节点的开始索引
					int p = deptInfoList.size();
					
					// 执行目标节点的子节点修复
					for (int i = 0; i < index; i++) {
						List<BioneDeptInfo> downDeptInfoList = this.baseDAO.findWithNameParm("select dept from BioneDeptInfo dept where dept.orgNo = '" 
								+ orgNo + "' and dept.upNo = '" + deptInfoList.get(i).getDeptNo() + "'", null);
						if(downDeptInfoList != null) {
							for(BioneDeptInfo deptInfo : downDeptInfoList) {
								
								// 查检结果集中是否已存在
								int t = 0, length = deptInfoList.size();
								for( ; t < length; t++) {
									if(deptInfo.getDeptId().equals(deptInfoList.get(t).getDeptId())) {
										break;
									}
								}
								
								// 若提前退出上面的循环, 则表明存在, 否则说明不存在
								if(t >= length) {
									deptInfoList.add(deptInfo);
									CommonTreeNode treeNode = new CommonTreeNode();
									treeNode.setId(deptInfo.getDeptNo());
									treeNode.setUpId(deptInfo.getUpNo());
									treeNode.setText(deptInfo.getDeptName());
									treeNode.setData(deptInfo);
									commonTreeNodeList.add(treeNode);
								}
							}
						}
					}
					
					// 执行目标节点的孙以及孙的后代节点修复
					repairDownDept(deptInfoList, p, orgNo, commonTreeNodeList);
				} else if(index != 0 && !key) {
					for(BioneDeptInfo deptInfo : deptInfoList) {
						CommonTreeNode treeNode = new CommonTreeNode();
						treeNode.setId(deptInfo.getDeptNo());
						treeNode.setUpId(deptInfo.getUpNo());
						treeNode.setText(deptInfo.getDeptName());
						treeNode.setData(deptInfo);
						commonTreeNodeList.add(treeNode);
					}
				} else {
					return;
				}
	}
	
	/**
	 * 树结构的完整性检查及修复
	 * 			修复上级
	 * @param deptInfoList
	 * 			机构集合
	 * @param index
	 * 			已遍历到的索引值
	 */
	public void repairUpDept(List<BioneDeptInfo> deptInfoList, int index, String orgNo, List<CommonTreeNode> commonTreeNodeList) {
		boolean flag = false;
		BioneDeptInfo deptInfo;
		for(int length = deptInfoList.size(); index < length; index++) {
			deptInfo = deptInfoList.get(index);
			
			// begin 封装的树
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(deptInfo.getDeptNo());
			treeNode.setUpId(deptInfo.getUpNo());
			treeNode.setText(deptInfo.getDeptName());
			treeNode.setData(deptInfo);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("open", "true");
			treeNode.setParams(paramMap);
			commonTreeNodeList.add(treeNode);
			// end
			
			if(deptInfo.getUpNo().equals(CommonTreeNode.ROOT_ID)) {
				continue;
			}
			
			// 如果存在上级, 则将 flag 置为 true, 否则, 保持 false
			for(BioneDeptInfo _deptInfo : deptInfoList) {
				if(deptInfo.getUpNo().equals(_deptInfo.getDeptNo())) {
					flag = true;
					break;
				}
			}
			
			// 如果不存在上级, 则进行修复
			if(!flag) {
				BioneDeptInfo deptInfo_ = this.baseDAO.findUniqueWithNameParam("select dept from BioneDeptInfo dept where dept.orgNo = '" 
						+ orgNo + "' and dept.deptNo = '" + deptInfo.getUpNo() + "'", null);
				if(deptInfo_ == null) {
					
					// 如果找不到上级节点, 则此节点为异常节点, 将此节点的上级标识为"0", 即根节点标识, 
					// 此为辅助措施, 破坏了树结构, 但保证了数据完整性, 可帮助使用者发现。
					deptInfo.setUpNo("0");
					
				} else {
					deptInfoList.add(deptInfo_);
				}
				repairUpDept(deptInfoList, index + 1, orgNo, commonTreeNodeList);
			}
			flag = false;
		}
	}
	
	/**
	 * 树结构的完整性检查及修复
	 * 			修复下级
	 * @param deptInfoList
	 * 			机构集合
	 * @param index
	 * 			查询节点的目标索引
	 */
	public void repairDownDept(List<BioneDeptInfo> deptInfoList, int index, String orgNo, List<CommonTreeNode> commonTreeNodeList) {
		for (int i = index; i < deptInfoList.size(); i++) {
			List<BioneDeptInfo> downDeptInfoList = this.baseDAO.findWithNameParm("select dept from BioneDeptInfo dept where dept.orgNo = '" 
					+ orgNo + "' and dept.upNo = '" + deptInfoList.get(i).getDeptNo() + "'", null);
			if(downDeptInfoList != null) {
				for(BioneDeptInfo deptInfo : downDeptInfoList) {
					
					// 查检结果集中是否已存在
					int t = 0, length = deptInfoList.size();
					for( ; t < length; t++) {
						if(deptInfo.getDeptId().equals(deptInfoList.get(t).getDeptId())) {
							break;
						}
					}
					
					// 若提前退出上面的循环, 则表明存在, 否则说明不存在
					if(t >= length) {
						deptInfoList.add(deptInfo);
						CommonTreeNode treeNode = new CommonTreeNode();
						treeNode.setId(deptInfo.getDeptNo());
						treeNode.setUpId(deptInfo.getUpNo());
						treeNode.setText(deptInfo.getDeptName());
						treeNode.setData(deptInfo);
						commonTreeNodeList.add(treeNode);
					}
				}
			}
		}
	}

	/**
	 * 构造显示树，并返回第一层的节点
	 * 
	 * @param parentId
	 *            上级节点id
	 * @param isLoop
	 *            是否遍历子节点
	 * @param conditionMap
	 * 			生成的搜索查询语句->BaseController->buildSerachCondition
	 * @return firstLevelNodeList
	 * 			构造好树结构的部门信息
	 */
	public List<CommonTreeNode> buildDeptTree(String logicSystem, boolean isLoop, Map<String, Object> conditionMap, String orgNo) {


		List<CommonTreeNode> commonTreeNodeList = Lists.newArrayList();
		
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId("0");
		treeNode.setUpId("0");
		treeNode.setIcon(null);
		treeNode.setText("条线树");
		treeNode.setData(null);
		
		Map<String, String> params = Maps.newHashMap();
		params.put("open", "true");
		
		treeNode.setParams(params);
		
		commonTreeNodeList.add(treeNode);

		this.findAllValidDept(logicSystem, conditionMap, orgNo, commonTreeNodeList);
			
		return commonTreeNodeList;
	}
	
	/**
	 * 获取 部门标识 与 部门名称 信息列表,准备存入缓存
	 * 
	 * @param logicSysNo
	 * 			逻辑系统标识
	 * 
	 * @return
	 */
	public Map<String, Map<String, Object>> findDeptNoAndDeptName(Collection<?> logicSysNo) {
		
		StringBuilder jql = new StringBuilder("");
		jql.append("select dept.orgNo, dept.deptNo, dept.deptName, dept.logicSysNo from BioneDeptInfo dept where dept.deptSts = :deptSts and dept.logicSysNo = :logicSysNo");
		
		Iterator<?> iter = logicSysNo.iterator();
		
		Map<String, Map<String, Object>> deptNoAndNameMapMap = Maps.newHashMap();

		while(iter.hasNext()) {
			
			Map<String, Object> values = Maps.newHashMap();
			values.put("deptSts", GlobalConstants4frame.COMMON_STATUS_VALID);
			values.put("logicSysNo", iter.next());
			
			List<Object[]> objList = this.baseDAO.findWithNameParm(jql.toString(), values);
			
			if(!objList.isEmpty()) {
				
				Map<String, Object> orgNoAndNameMap = Maps.newHashMap();
				
				for(Object[] obj : objList) {
					orgNoAndNameMap.put(obj[0] + "" + obj[1], obj[2] + "");
				}
				
				deptNoAndNameMapMap.put((String) objList.get(0)[3], orgNoAndNameMap);
			}
		}
		return deptNoAndNameMapMap;
	}

	public List<CommonTreeNode> getDeptTree(String id, String search,
			String orgNo) {
		String jql = "select dept from BioneDeptInfo dept where dept.deptSts = :deptSts and dept.logicSysNo = :logicSysNo and dept.orgNo =:orgNo";
		Map<String, Object> values = Maps.newHashMap();
		values.put("deptSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		values.put("orgNo", orgNo);
		List<CommonTreeNode> result = new ArrayList<CommonTreeNode>();
		if(!StringUtils.isEmpty(search)){
			jql += " and dept.deptName like :deptName";
			values.put("deptName", "%" + search + "%");
			List<BioneDeptInfo> list = this.baseDAO.findWithNameParm(jql, values);
			CommonTreeNode rootNode = new CommonTreeNode();
			rootNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
			rootNode.setText("条线树");
			rootNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
			rootNode.setIsParent(true);
			rootNode.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
			List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();
			for(BioneDeptInfo dept : list){
				CommonTreeNode node = new CommonTreeNode();
				node.setId(dept.getDeptNo());
				node.setText(dept.getDeptName());
				node.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
				node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_ORG_ICON);
				node.setIsParent(false);
				childList.add(node);
			}
			rootNode.setChildren(childList);
			result.add(rootNode);
		}else{
			List<BioneDeptInfo> list = new ArrayList<BioneDeptInfo>();
			if(!StringUtils.isEmpty(id)){
				jql += " and dept.upNo = :upNo";
				values.put("upNo", id);
				list = this.baseDAO.findWithNameParm(jql, values);
			}else{
				CommonTreeNode node = new CommonTreeNode();
				node.setId(GlobalConstants4frame.TREE_ROOT_NO);
				node.setText("条线树");
				node.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
				node.setIsParent(true);
				node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
				result.add(node);
			}
			
			for(BioneDeptInfo dept : list){
				CommonTreeNode node = new CommonTreeNode();
				node.setId(dept.getDeptNo());
				node.setText(dept.getDeptName());
				node.setUpId(dept.getUpNo());
				node.setIsParent(true);
				node.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_ORG_ICON);
				result.add(node);
			}
		}
		return result;
	}
}
