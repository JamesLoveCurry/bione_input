package com.yusys.bione.frame.authobj.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author huangye huangye@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service("deptBS")
@Transactional(readOnly = true)
public class DeptBS extends BaseBS<BioneDeptInfo> {

	/**
	 * 
	 * @return 条线树List
	 */
	public List<CommonTreeNode> buldDeptTree(String orgNo) {
		List<CommonTreeNode> orgTree = Lists.newArrayList();
		// 获取所有部门信息,用于通过机构Id，查找对应机构下属部门。
		String jql = "SELECT dept FROM BioneDeptInfo dept WHERE dept.logicSysNo=?0 AND dept.orgNo=?1";
		List<BioneDeptInfo> deptInfoList = this.baseDAO.findWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
				orgNo);

		// 将部门信息处理成 上级部门标识-->部门信息 格式
		Map<String, List<BioneDeptInfo>> upDeptIdMap = Maps.newHashMap();
		if (deptInfoList != null) {
			List<BioneDeptInfo> oneLevelDeptInfoList = null;
			for (BioneDeptInfo deptInfo : deptInfoList) {
				oneLevelDeptInfoList = upDeptIdMap.get(deptInfo.getUpNo());
				if (oneLevelDeptInfoList == null) {
					oneLevelDeptInfoList = Lists.newArrayList();
					upDeptIdMap.put(deptInfo.getUpNo(), oneLevelDeptInfoList);
				}
				oneLevelDeptInfoList.add(deptInfo);
			}
		}

		// 上级部门标识的commonTreeNode.Root_id的为根节点
		List<BioneDeptInfo> rootDeptInfoList = upDeptIdMap.get(new String(
				CommonTreeNode.ROOT_ID));
		if (rootDeptInfoList != null) {
			for (BioneDeptInfo deptInfo : rootDeptInfoList) {
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(deptInfo.getDeptNo());
				treeNode.setText(deptInfo.getDeptName());
				treeNode.setUpId(deptInfo.getUpNo());
				treeNode.setData(deptInfo);
				orgTree.add(treeNode);
				buildDeptChildrenTree(treeNode, upDeptIdMap);
			}
		}
		return orgTree;
	}

	/**
	 * 通过上级部门编号--绑定在上级节点中 ,循环查找并构造下级条线树 列表信息
	 * 
	 * @param treeNode
	 *            父节点
	 * @param upDeptIdMap
	 *            父标识--》子节点列表
	 */
	private void buildDeptChildrenTree(CommonTreeNode parentNode,
			Map<String, List<BioneDeptInfo>> upDeptIdMap) {
		List<BioneDeptInfo> deptInfoList = upDeptIdMap.get(new String(
				parentNode.getId()));
		if (deptInfoList != null) {
			for (BioneDeptInfo deptInfo : deptInfoList) {
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(deptInfo.getDeptNo());
				treeNode.setText(deptInfo.getDeptName());
				treeNode.setUpId(deptInfo.getUpNo());
				treeNode.setData(deptInfo);
				parentNode.addChildNode(treeNode);
				buildDeptChildrenTree(treeNode, upDeptIdMap);
			}
		}
	}

	/**
	 * 通过部门标识查找部门信息
	 * 
	 * @param orgNo
	 * @param deptNo
	 * @return
	 */
	public BioneDeptInfo findDeptInfoByOrgNoandDeptNo(String orgNo,
			String deptNo) {
		String sysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		String jql = "SELECT dept FROM BioneDeptInfo dept where   dept.deptNo=?0 and dept.logicSysNo=?1 and dept.orgNo=?2";
		List<BioneDeptInfo> depts = this.baseDAO.findWithIndexParam(jql,
				deptNo, sysNo, orgNo);
		if (depts != null && depts.size() > 0) {
			return depts.get(0);
		} else {
			// 条线标识不能等于机构标识。
			jql = "SELECT dept FROM BioneOrgInfo dept where dept.orgNo=?0 and dept.logicSysNo=?1 and dept.orgNo=?2";
			List<BioneOrgInfo> orgs = this.baseDAO.findWithIndexParam(jql,
					deptNo, sysNo, orgNo);
			if (orgs != null && orgs.size() > 0) {
				return new BioneDeptInfo();
			}
			return null;
		}

	}

	 /**
	 * 通过部门标识查找部门信息
	 *
	 * @param orgNo
	 * @param deptNo
	 * @param logicSysNo
	 * @return
	 */
	 public BioneDeptInfo findDeptInfoByOrgNoandDeptNo(String orgNo,String deptNo,String logicSysNo) {
	 String jql = "SELECT dept FROM BioneDeptInfo dept where  dept.orgNo=?0 and dept.deptNo=?1 and dept.logicSysNo=?2";
	 return this.baseDAO.findUniqueWithIndexParam(jql, orgNo, deptNo, logicSysNo);
	
	 }

	/**
	 * 通过机构编号，查询所有当前机构下的部门信息
	 * 
	 * @param orderBy
	 * @param orderType
	 * @param orgNo
	 * @return
	 */
	public List<BioneDeptInfo> findDeptInfoByOrg(String orderBy,
			String orderType, String orgNo) {
		String jql = "SELECT dept FROM BioneDeptInfo dept WHERE dept.orgNo=:orgNo AND dept.logicSysNo=:logicSysNo";

		if (!StringUtils.isEmpty(orderBy)) {
			jql += " order by dept." + orderBy + " " + orderType;
		}
		Map<String, Object> paramMap = Maps.newHashMap();

		paramMap.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		paramMap.put("orgNo", orgNo);

		List<BioneDeptInfo> deptList = this.baseDAO.findWithNameParm(jql,
				paramMap);
		return deptList;
	}

	/**
	 * 查询某机构下所有部门信息
	 * 
	 * @param orgNo
	 *            机构标识
	 * @param isBasicDept
	 *            是否启用基线部门
	 * @return
	 */
	public Map<String, BioneDeptInfo> findAllDeptInfoWithOrg(String orgNo) {
		StringBuilder jql = new StringBuilder("");

		jql.append("select dept from BioneDeptInfo dept where dept.logicSysNo = :logicSysNo");

		Map<String, Object> values = Maps.newHashMap();

		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());

		if (orgNo != null) {
			jql.append(" and dept.orgNo=:orgNo");
			values.put("orgNo", orgNo);
		}

		List<BioneDeptInfo> deptList = this.baseDAO.findWithNameParm(
				jql.toString(), values);
		Map<String, BioneDeptInfo> resultMap = Maps.newHashMap();
		for (BioneDeptInfo dept : deptList) {
			resultMap.put(dept.getDeptNo() + "_" + dept.getOrgNo(), dept);
		}
		return resultMap;
	}

	/**
	 * 根据查询条件获取记录 用以搭配全部记录而构造查询条线树
	 * 
	 * @param orgNo
	 *            机构标识
	 * @param conditionMap
	 *            查询支持
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BioneDeptInfo> findDeptInfoByOrg(String orgNo,
			Map<String, Object> conditionMap) {

		StringBuilder jql = new StringBuilder("");

		jql.append("select dept from BioneDeptInfo dept where dept.logicSysNo = :logicSysNo");

		Map<String, Object> values = Maps.newHashMap();

		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
			values = (Map<String, Object>) conditionMap.get("params");
		}

		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());

		if (orgNo != null) {
			jql.append(" and dept.orgNo=:orgNo");
			values.put("orgNo", orgNo);
		}

		List<BioneDeptInfo> deptList = this.baseDAO.findWithNameParm(
				jql.toString(), values);
		return deptList;
	}

	@Transactional(readOnly = false)
	public void removeEntityBatch(String ids) {
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		String[] idArray = StringUtils.split(ids, ',');
		for (String id : idArray) {
			this.removeEntityById(id);
		}
	}
	
	public List<CommonTreeNode> getDeptTree(String searchNm) {
		List<CommonTreeNode> resultnodes = new ArrayList<CommonTreeNode>();
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "select dept from BioneDeptInfo dept,BioneOrgInfo org where dept.orgNo=org.orgNo and  dept.logicSysNo = :logicSysNo and org.logicSysNo = :logicSysNo and dept.deptSts = :sts and org.orgSts = :sts";
		params.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		params.put("sts", GlobalConstants4frame.COMMON_STATUS_VALID);
		
		if(StringUtils.isNotBlank(searchNm)){
			jql += " and dept.deptName like :searchNm";
			params.put("searchNm", "%"+searchNm+"%");
		}
		List<BioneDeptInfo> depts =this.baseDAO.findWithNameParm(jql, params);
		if(depts != null && depts.size() > 0){
			Set<String> orgNos = new HashSet<String>();
			for(BioneDeptInfo dept : depts){
				orgNos.add(dept.getOrgNo());
			}
			resultnodes.addAll(generateDeptNodes(depts));
			jql = "select org from BioneOrgInfo org where org.orgNo in :orgNo and  org.logicSysNo = :logicSysNo and org.orgSts = :sts";
			params.clear();
			params.put("sts", GlobalConstants4frame.COMMON_STATUS_VALID);
			params.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			params.put("orgNo", orgNos);
			List<BioneOrgInfo> orgs =this.baseDAO.findWithNameParm(jql, params);
			resultnodes.addAll(generateOrgNodes(orgs));
		}
		return resultnodes;
	}
	
	/**
	 * 构造机构节点
	 * 
	 * @param orgs 所有机构
	 * @return 树节点list
	 */
	private List<CommonTreeNode> generateOrgNodes(List<BioneOrgInfo> orgs) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		for (int i = 0; i < orgs.size(); i++) {// 遍历机构节点
			BioneOrgInfo org = orgs.get(i);
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(org.getOrgNo());
			treeNode.setText(org.getOrgName());
			treeNode.setIcon(this.getContextPath() + GlobalConstants4frame.LOGIC_ORG_ICON_NEW);
			Map<String, String> params = new HashMap<String, String>();
			params.put("nodetype", "org");
			treeNode.setParams(params);
			if (GlobalConstants4frame.TREE_ROOT_NO.equals(org.getUpNo())) {
				treeNode.setUpId(org.getUpNo());
			} else {
				treeNode.setUpId(org.getUpNo());
			}
			nodes.add(treeNode);
		}
		return nodes;
	}

	/**
	 * 构造部门节点
	 * 
	 * @param depts 所有部门
	 * @return 树节点list
	 */
	private List<CommonTreeNode> generateDeptNodes(List<BioneDeptInfo> depts) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		for (int i = 0; i < depts.size(); i++) {// 遍历部门节点
			BioneDeptInfo dept = depts.get(i);
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(dept.getOrgNo() + "-" + dept.getDeptNo());
			treeNode.setData(dept);
			treeNode.setText(dept.getDeptName());
			treeNode.setIcon(this.getContextPath() + GlobalConstants4frame.LOGIC_DEPT_ICON);
			Map<String, String> params = new HashMap<String, String>();
			params.put("nodetype", "dept");
			treeNode.setParams(params);
			if (GlobalConstants4frame.TREE_ROOT_NO.equals(dept.getUpNo())) {
				treeNode.setUpId(dept.getOrgNo());
			} else {
				treeNode.setUpId(dept.getOrgNo() + "-" + dept.getUpNo());
			}
			nodes.add(treeNode);
		}
		return nodes;
	}
	
	// 获取上下文路径
	private String getContextPath() {
		return com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_CONTEXT_PATH;
	}

}
