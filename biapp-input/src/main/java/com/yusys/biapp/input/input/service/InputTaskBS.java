package com.yusys.biapp.input.input.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.catalog.service.DataInputCatalogBS;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.task.entity.RptTskInfo;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;
@Service
public class InputTaskBS extends BaseBS<RptTskInfo>{
	protected static Logger log = LoggerFactory.getLogger(InputTaskBS.class);

	@Autowired
	private DataInputCatalogBS dirBS;
//
//	@Autowired
//	private UdipTaskRoleBS udipTaskRoleBS;
//
//	@Autowired
//	private AuthObjectUtils authObjectUtils;
//
	@Autowired
	private TaskCaseBS udipTaskCaseBS;
//
//	@Autowired
//	private DataUtils dataUtils;
//	
//	public static Object Lock = new Object();
	/**
	 * 
	 * @param logicSysNo 逻辑系统标识
	 * @param userName 用户名
	 * @param roles 用户角色
	 * @param roleType 角色类型
	 * @param dirId 目录ID
	 * @param taskName 任务名称
	 * @param withCreator 是否包含该用户创建的任务
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RptTskInfo> getTaskByRoles(String logicSysNo, String userName, List<String> roles, List<String> roleType,String dirId,String taskName,boolean withCreator,Map<String, Object> conditionMap) {
		Map<String, Object> values = Maps.newHashMap();
		if(conditionMap!=null &&conditionMap.get("params")!=null){
			values = (Map<String, Object>) conditionMap.get("params");
		}
		
		if ((roles == null || roles.isEmpty()) && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			return Lists.newArrayList();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("logicSysNo", logicSysNo);
		String jql = "select t,a from RptTskInfo t , UdipTaskRole a where t.logicSysNo =:logicSysNo and t.taskId = a.taskId ";

		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			jql += " and a.roleCode in :roleCode";
			paramMap.put("roleCode", roles);
		}
		if ( !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()&& !roleType.isEmpty()) {
			jql += " and a.roleType in :roleType";
			paramMap.put("roleType", roleType);
		}
		if(StringUtils.isNotBlank(dirId) && !"0".equals(dirId)){
			jql += " and t.dirId=:dirId";
			paramMap.put("dirId", dirId);
		}
		if (StringUtils.isNotBlank(taskName)) {
			jql += " and t.taskName like :taskName";
			paramMap.put("taskName", "%taskName%");
		}
		if(conditionMap != null && !conditionMap.get("jql").equals("") ){
			paramMap.putAll(values);
			jql += " and " + conditionMap.get("jql");
		}
		List<Object[]> list = baseDAO.findWithNameParm(jql, paramMap);

		List<RptTskInfo> taskList = Lists.newArrayList();
		for (Object[] obj : list) {
			RptTskInfo task = (RptTskInfo) obj[0];
			// TODO
			//UdipTaskRole role = (UdipTaskRole) obj[1];
			if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()&& !taskList.contains(task)){//超级管理员可以看到全部
				taskList.add(taskList.size(),task);
				continue ; 
			}
//			if (StringUtils.isNotBlank(role.getRoleUser()) ) {// 如果角色设置了人,那么就要看该用户在不在设置人的里面
//				if (ArrayUtils.asCollection(role.getRoleUser()).contains(userName) && !taskList.contains(task))
//					taskList.add(taskList.size(),task);
//			} else {
//				if (!taskList.contains(task))
//					taskList.add(taskList.size(),task);
//			}
		}
		if(withCreator){
			Map<String, Object> paramMapOwn = new HashMap<String, Object>();// 看到拥有角色和自己创建的任务
			paramMapOwn.put("logicSysNo", logicSysNo);
			paramMapOwn.put("creator", userName);
			String jqlOwn = "select t from RptTskInfo t where t.creator =:creator and t.logicSysNo = :logicSysNo ";
			if (StringUtils.isNotBlank(taskName) && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				jqlOwn += " and t.taskName like :taskName";
				paramMapOwn.put("taskName", "%taskName%");
			}
			if(conditionMap != null && !conditionMap.get("jql").equals("") ){
				paramMapOwn.putAll(values);
				jqlOwn += " and " + conditionMap.get("jql");
			}
			
			List<RptTskInfo> taskList2 = Lists.newArrayList();
			if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(dirId) && !"0".equals(dirId)) {// 可看到拥有角色和自己创建的任务
				paramMapOwn.put("dirId", dirId);
				jqlOwn += " and t.dirId = :dirId";
				taskList2 = baseDAO.findWithNameParm(jqlOwn, paramMapOwn);
			}else if(StringUtils.isNotBlank(userName)){
				taskList2 = baseDAO.findWithNameParm(jqlOwn, paramMapOwn);
			}
			
			for (RptTskInfo info : taskList2) {
				if (!taskList.contains(info)) {
					taskList.add(info);
				}
			}
		}
		
		return taskList;
	}
//
//	/**
//	 * 关联角色获取任务信息，并分页
//	 * @param logicSysNo
//	 * @param userName
//	 * @param roles
//	 * @param roleType
//	 * @param dirId
//	 * @param taskName
//	 * @param first
//	 * @param pageSize
//	 * @return Rows： 结果集 Total：总条数
//	 */
//	public Map<String,Object>  getTaskByRolesWithPage(String logicSysNo, String userName, List<String> roles, List<String> roleType,String dirId,String taskName,int first,int pageSize,Map<String, Object> conditionMap) {
//		//获取所有数据
//		List<UdipInputTaskInfo> taskList = this.getTaskByRoles(logicSysNo, userName, roles, roleType, dirId,taskName,true,conditionMap);
//		Map<String,Object> taskInfo = Maps.newHashMap();
//		taskInfo.put("Total", taskList.size());
//		//获取分页
//		List<UdipInputTaskInfo> objectList = Lists.newArrayList();
//		if (first >= 0 && pageSize > 0 && !taskList.isEmpty()) {
//			if(first > taskList.size())
//				first = 0;
//			if ((pageSize + first) > taskList.size()) {
//				objectList = taskList.subList(first, taskList.size());
//			} else {
//				objectList = taskList.subList(first, pageSize + first);
//			}
//
//		} else {
//			objectList = taskList;
//		}
//		taskInfo.put("Rows", objectList);
//		return taskInfo;
//	}
//
	/**
	 * 根据角色获取补录任务和补录任务实例带模版
	 * @param userName 用户名 BiOneSecurityUtils.getCurrentUserInfo().getUserName()
	 * @param logicSysNo 逻辑系统标识 BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo()
	 * @param roles 用户角色 BiOneSecurityUtils.getCurrentUserInfo().getRoleList()
	 * @param roleType 角色类型
	 * @param withTemp 是否包含模板
	 * @param withCreator 是否包含该用户创建的任务
	 * @param allOrSomeMission 全部或者进行中的标识
	 * @param caseId 根据传递的实例ID来获取
	 * @return 由目录，任务，实例,模板构成的树
	 */
	public List<CommonTreeNode> buildTaskTreeWithCase(String userName, String logicSysNo, List<String> roles,
			List<String> roleType, boolean withTemp,boolean withCreator,String allOrSomeMission,String caseId) {

		List<CommonTreeNode> nodeList = Lists.newArrayList();
		Collection<String> ids = new HashSet<String>(); 
		if(StringUtils.isNotBlank(caseId)){
			ids = ArrayUtils.asCollection(caseId);
		}
		/** 目录 **/
		List<CommonTreeNode> dirList = dirBS.getByType(UdipConstants.DIR_TYPE_TASK, logicSysNo);
		nodeList.addAll(dirList);

		/** 任务 **/
		List<RptTskInfo> taskList = this.getTaskByRoles(logicSysNo, userName, roles, roleType,null,null,withCreator,null);// 可看到拥有管理角色和自己创建的任务

		List<String> taskIds = Lists.newArrayList();
		for (RptTskInfo info : taskList) {
			Map<String, String> map = Maps.newHashMap();
			map.put("type", UdipConstants.TASK_TASK);

			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(info.getTaskId());
			treeNode.setUpId(info.getCatalogId());
			treeNode.setText(info.getTaskNm());
			//treeNode.setTitle(info.getTaskNm());
			treeNode.setIcon(UdipConstants.ICON_TASK);
			treeNode.setParams(map);
			taskIds.add(info.getTaskId());
			nodeList.add(treeNode);
		}
		List<RptTskIns> taskCaseList = Lists.newArrayList();
		/** 任务实例 **/
		taskCaseList = udipTaskCaseBS.getTaskCaseByTask(taskIds, BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		List<String> caseIds = Lists.newArrayList();

		for (RptTskIns info : taskCaseList) {
			if(!caseIds.contains(info.getTaskInstanceId())){
				
				Map<String, String> map = Maps.newHashMap();
				map.put("type", UdipConstants.TASK_CASE);
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(info.getTaskInstanceId());
				treeNode.setUpId(info.getTaskId());
				treeNode.setParams(map);
				treeNode.setIcon(UdipConstants.ICON_CASE);
				//TODO
				//treeNode.setText(info.getDataDate() + " [第" + info.getTaskSequnce() + "批]");
				//treeNode.setTitle(treeNode.getText());
				if(UdipConstants.CASE_STATE_START.equals(info.getSts())){
					treeNode.setText("<span style='color:#FF0000'>"+treeNode.getText() + "</span>");
				}
				caseIds.add(info.getTaskInstanceId());
				if(StringUtils.isBlank(allOrSomeMission)){//取全部
					if(ids.isEmpty()){
						nodeList.add(treeNode);
					}else if(ids.contains(info.getTaskInstanceId())){//取指定ID
						nodeList.add(treeNode);
					}
				}else if(UdipConstants.CASE_STATE_START.equals(info.getSts())) {//取进行中
					if(ids.isEmpty()){
						nodeList.add(treeNode);
					}else if(ids.contains(info.getTaskInstanceId())){//取指定ID
						nodeList.add(treeNode);
					}
				}
			}
		}

		/** 实例模板UDIP_TASK_TEMPLE **/
		if (withTemp && !taskIds.isEmpty()) {
			List<Object[]> caseTempleList = udipTaskCaseBS.getTempleByTask(taskIds);
			Map<String, String> maptype = Maps.newHashMap();
			for (Object[] obj : caseTempleList) {
				RptInputLstTempleInfo temple = (RptInputLstTempleInfo) obj[0];
				RptTskIns taskCase = (RptTskIns) obj[1];
				if (caseIds.contains(taskCase.getTaskInstanceId())) {
					CommonTreeNode treeNode = new CommonTreeNode();
					maptype.put("type", UdipConstants.CASE_TEMPLE);
					treeNode.setText(temple.getTempleName());
					//treeNode.setTitle(temple.getTemplateName());
					treeNode.setId(temple.getTempleId());
					treeNode.setUpId(taskCase.getTaskInstanceId());
					treeNode.setParams(maptype);
					treeNode.setIcon(UdipConstants.ICON_TEMPLE);
					if(StringUtils.isBlank(allOrSomeMission)){
						nodeList.add(treeNode);
					}else if(StringUtils.isNotBlank(allOrSomeMission)&&UdipConstants.CASE_STATE_START.equals(taskCase.getSts())) {
						nodeList.add(treeNode);
					}
				}
			}
		}
		return nodeList;
	}
//	
//	/**
//	 * 根据角色获取补录任务
//	 * @param userName 用户名 BiOneSecurityUtils.getCurrentUserInfo().getUserName()
//	 * @param logicSysNo 逻辑系统标识 BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo()
//	 * @param roles 用户角色 BiOneSecurityUtils.getCurrentUserInfo().getRoleList()
//	 * @param roleType 角色类型
//	 * @param withCreator 是否包含该用户创建的任务
//	 * @return 由目录，任务，实例构成的树
//	 */
//	public List<CommonTreeNode> buildTaskTree(String userName, String logicSysNo, List<String> roles, List<String> roleType,boolean withCreator) {
//
//		List<CommonTreeNode> nodeList = Lists.newArrayList();
//
//		/** 目录 **/
//		List<CommonTreeNode> dirList = dirBS.getByType(UdipConstants.DIR_TYPE_TASK, logicSysNo);
//		nodeList.addAll(dirList);
//
//		/** 任务 **/
//		List<UdipInputTaskInfo> taskList = this.getTaskByRoles(logicSysNo, userName, roles, roleType,null,null,withCreator,null);
//
//		List<String> taskIds = Lists.newArrayList();
//		for (UdipInputTaskInfo info : taskList) {
//			Map<String, String> map = Maps.newHashMap();
//			map.put("type", UdipConstants.TASK_TASK);
//
//			CommonTreeNode treeNode = new CommonTreeNode();
//			treeNode.setId(info.getTaskId());
//			treeNode.setUpId(info.getDirId());
//			treeNode.setText(info.getTaskName());
//			treeNode.setIcon(UdipConstants.ICON_TASK);
//			treeNode.setParams(map);
//			taskIds.add(info.getTaskId());
//			nodeList.add(treeNode);
//		}
//		return nodeList;
//	}
//
//	@Transactional(readOnly = false)
//	public void saveTaskRoleOrg(String taskIds, String orgCode, String roleCode) {
//		String[] ids = taskIds.split(",");
//		for (String id : ids) {
//			if (StringUtils.isNotBlank(orgCode)) {
//				String[] orgCodes = orgCode.split(",");
//				List<UdipDispatchOrg> orgList = this.getEntityListByProperty(UdipDispatchOrg.class, "taskId", id);
//				for (UdipDispatchOrg role : orgList) {
//					this.baseDAO.getEntityManager().remove(role);
//				}
//				//防止程序漏洞插入两条同机构数据
//				Set<String> orgSet = Sets.newHashSet();
//				for (String org : orgCodes) {
//					orgSet.add(org);
//				}
//				for(String org : orgSet){
//					UdipDispatchOrg info = new UdipDispatchOrg();
//					info.setOrgCode(org);
//					info.setTaskId(id);
//					info.setId(RandomUtils.uuid2());
//					this.baseDAO.getEntityManager().persist(info);
//				}
//			}
//
//			List<UdipTaskRole> orgList = this.getEntityListByProperty(UdipTaskRole.class, "taskId", id);
//			for (UdipTaskRole role : orgList) {
//				this.baseDAO.getEntityManager().remove(role);
//			}
//			if (StringUtils.isNotBlank(roleCode)) {
//				String[] roleCodes = roleCode.split("@");
//				for (int i = 0; i < roleCodes.length; i++) {
//					String[] roleAndType = roleCodes[i].split(":");
//					
//					UdipTaskRole info = new UdipTaskRole();
//					info.setRoleCode(roleAndType[0]);
//					info.setRoleType(roleAndType[1]);
//					if (roleAndType.length > 2)
//						info.setRoleUser(roleAndType[2]);
//					info.setTaskId(id);
//					info.setId(RandomUtils.uuid2());
//					this.baseDAO.getEntityManager().persist(info);
//				}
//			}
//		}
//	}
//	
//	@Transactional(readOnly = false)
//	public void saveCaseOrg(String caseId, String orgCode) throws Exception {
//		if (StringUtils.isNotBlank(orgCode)) {
//			String[] orgCodes = orgCode.split(",");
//			UdipTaskCaseInfo taskCase = this.getEntityById(UdipTaskCaseInfo.class, caseId);
//			List<UdipTemple> tempList = this.getTempleByTaskId(taskCase.getTaskId());
//			List<UdipCaseOrg> caseOrgList = this.getEntityListByProperty(UdipCaseOrg.class, "caseId", caseId);
//			List<UdipAuthRecordInfo> recordOrgList = this.getEntityListByProperty(UdipAuthRecordInfo.class, "caseId", caseId);
//			List<UdipDataStateInfo> stateOrgList = this.getEntityListByProperty(UdipDataStateInfo.class, "caseId", caseId);
//			
//			Map<String,UdipCaseOrg> caseMap= Maps.newHashMap();
//			Map<String,List<UdipAuthRecordInfo>> dispMap= Maps.newHashMap();
//			Map<String,List<UdipDataStateInfo>> stateMap= Maps.newHashMap();
//			
//			for (UdipCaseOrg info : caseOrgList) {//下发机构
//				caseMap.put(info.getOrgCode(), info);
//			}
//			for (UdipAuthRecordInfo info : recordOrgList) {//操作记录
//				if(!dispMap.containsKey(info.getOrgCode()))
//					dispMap.put(info.getOrgCode(), new ArrayList<UdipAuthRecordInfo>());
//				dispMap.get(info.getOrgCode()).add(info);
//			}
//			for (UdipDataStateInfo info : stateOrgList) {//数据状态
//				if(!stateMap.containsKey(info.getOrgCode()))
//					stateMap.put(info.getOrgCode(), new ArrayList<UdipDataStateInfo>());
//				stateMap.get(info.getOrgCode()).add(info);
//			}
//			
//			/**1.筛选出那些是新增，那些是已有，那些是取消**/
//			Set<String> addOrg = Sets.newHashSet();
//			for (String org : orgCodes) {
//				if(caseMap.containsKey(org)){
//					caseMap.remove(org);
//				}else{
//					addOrg.add(org);
//				}
//			}
//			
//			String dipatcher =  BiOneSecurityUtils.getCurrentUserInfo().getLoginName();
//			
//			/**2.新增的***/
//			for (String org : addOrg) {
//				UdipCaseOrg info = new UdipCaseOrg();
//				info.setOrgCode(org);
//				info.setCaseId(caseId);
//				info.setId(RandomUtils.uuid2());
//				this.baseDAO.getEntityManager().persist(info);
//				
//				for (UdipTemple temp : tempList) {
//					UdipAuthRecordInfo record = new UdipAuthRecordInfo();//操作记录
//					UdipDataStateInfo state = new UdipDataStateInfo();//数据状态
//					record.setRecordId(RandomUtils.uuid2());
//					record.setCaseId(caseId);
//					record.setOperateDate(taskCase.getDispatchDate());
//					record.setOperator(dipatcher);
//					record.setOperateType(UdipConstants.TASK_STATE_DISPATCH);
//					record.setOrgCode(org);
//					record.setTempleId(temp.getTempleId());
//					
//					state.setId(RandomUtils.uuid2());
//					state.setCaseId(caseId);
//					state.setDataState(UdipConstants.TASK_STATE_DISPATCH);
//					state.setOrgCode(org);
//					state.setTempleId(temp.getTempleId());
//					state.setLogicSysNo(taskCase.getLogicSysNo());
//					this.baseDAO.getEntityManager().persist(record);// 保存操作记录
//					this.baseDAO.getEntityManager().persist(state);// 保存数据状态
//				}
//			}
//			
//			/**3.删除的**/
//			if(!caseMap.isEmpty()){
//				for (String org : caseMap.keySet()) {
//					this.baseDAO.getEntityManager().remove(caseMap.get(org));
//					
//					if(dispMap.get(org)!=null){
//						for(UdipAuthRecordInfo info : dispMap.get(org)){
//							this.baseDAO.getEntityManager().remove(info);
//						}
//					}
//					
//					if(stateMap.get(org)!=null){
//						for(UdipDataStateInfo info : stateMap.get(org)){
//							this.baseDAO.getEntityManager().remove(info);
//						}
//					}
//					
//				}
//				
//				for (UdipTemple temp : tempList) {
//					if(StringUtils.isNotBlank(temp.getOrgColumn()))
//						dataUtils.deleteByCaseId(temp, null,null,new ArrayList<String>(caseMap.keySet()),null,caseId);
//				}
//			}
//			
//		}
//	}
//
//	@Transactional(readOnly = false)
//	public void cancelTaskRoleOrg(String taskIds) {
//		String[] ids = taskIds.split(",");
//		for (String id : ids) {
//			List<UdipDispatchOrg> roleList = this.getEntityListByProperty(UdipDispatchOrg.class, "taskId", id);
//
//			List<UdipTaskRole> orgList = this.getEntityListByProperty(UdipTaskRole.class, "taskId", id);
//
//			if (roleList != null) {
//				for (UdipDispatchOrg role : roleList) {
//					this.baseDAO.getEntityManager().remove(role);
//				}
//			}
//			if (orgList != null) {
//				for (UdipTaskRole org : orgList) {
//					this.baseDAO.getEntityManager().remove(org);
//				}
//			}
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public SearchResult<UdipInputTaskInfo> getInputTaskList(int firstResult, int pageSize, String orderBy, String orderType, Map<String, Object> conditionMap, String realId) {
//
//		StringBuilder jql = new StringBuilder("");
//		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
//		if (realId == null || "0".equals(realId)) {
//			jql.append("select temple from UdipInputTaskInfo temple where temple.logicSysNo=:logicSysNo");
//			values.put("logicSysNo", BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
//		} else {
//			jql.append("select temple from UdipInputTaskInfo temple where temple.dirId=:dirId and temple.logicSysNo=:logicSysNo");
//			values.put("dirId", realId);
//			values.put("logicSysNo", BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
//		}
//		if (!conditionMap.get("jql").equals("")) {
//			jql.append(" and " + conditionMap.get("jql"));
//		}
//		if (!StringUtils.isEmpty(orderBy)) {
//			jql.append(" order by Temple." + orderBy + " " + orderType);
//		}
//		SearchResult<UdipInputTaskInfo> authObjDefList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
//		return authObjDefList;
//	}
//
//	/**
//	 * 任务下发
//	 * @param taskInfo 任务
//	 * @param auto 是否自动下发的
//	 */
//	@Transactional(readOnly = false)
//	public void dispatchTask(List<UdipInputTaskInfo> taskList, String etlDate, boolean auto) {
//		for (UdipInputTaskInfo task : taskList) {
//			if (UdipConstants.TASK_STATE_USING.equals(task.getTaskState())) {// 启动的任务
//				synchronized(Lock){
//					dispatchTask(task, etlDate, auto);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 任务下发
//	 * @param taskInfo 任务
//	 * @param etlDate 数据日期
//	 * @param auto 是否自动下发的
//	 */
//	@Transactional(readOnly = false)
//	public void dispatchTask(UdipInputTaskInfo taskInfo, String etlDate, boolean auto) {
//		String dipatcher = auto ? UdipConstants.System : BiOneSecurityUtils.getCurrentUserInfo().getLoginName();
//
//		/* 1.取出任务机构 */
//		List<UdipDispatchOrg> orgList = this.getEntityListByProperty(UdipDispatchOrg.class, "taskId", taskInfo.getTaskId());
//		List<UdipTaskRole> roleList = udipTaskRoleBS.findByPropertys(UdipTaskRole.class, new String[] { "taskId", "roleType" }, new Object[] { taskInfo.getTaskId(), UdipConstants.ROLE_TYPE_INPUT });
//
//		/* 2.生成下发实例 */
//		List<UdipTaskCaseInfo> caseList = udipTaskCaseBS.findByPropertys(UdipTaskCaseInfo.class, new String[] { "taskId", "etlDate" }, new Object[] { taskInfo.getTaskId(), etlDate });
//		
//		int max = udipTaskCaseBS.getMaxSequence(taskInfo.getTaskId(),etlDate);// 最大批次
//		
//		if (auto && !caseList.isEmpty()) {// 自动下发过,就不再自动下发
//			//return;
//		}
//
//		UdipTaskCaseInfo taskCase = new UdipTaskCaseInfo();
//		taskCase.setCaseId(RandomUtils.uuid2());
//		taskCase.setEtlDate(etlDate);
//		taskCase.setDispatcher(dipatcher);
//		taskCase.setTaskId(taskInfo.getTaskId());
//		taskCase.setDispatchDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
//		taskCase.setTaskSequnce(max + 1);
//		taskCase.setCaseState(UdipConstants.CASE_STATE_START);
//		taskCase.setEndDate(taskInfo.getTaskLift());
//		taskCase.setLogicSysNo(taskInfo.getLogicSysNo());
//		
//		this.baseDAO.getEntityManager().persist(taskCase);
//
//		for (UdipDispatchOrg org : orgList) {// 保存实例机构
//			UdipCaseOrg caseOrg = new UdipCaseOrg();
//			caseOrg.setId(RandomUtils.uuid2());
//			caseOrg.setOrgCode(org.getOrgCode());
//			caseOrg.setCaseId(taskCase.getCaseId());
//			this.baseDAO.getEntityManager().persist(caseOrg);
//		}
//		
//		/* 3.生成下发记录 */
//		List<UdipTaskTempleInfo> tempList = this.getEntityListByProperty(UdipTaskTempleInfo.class, "taskId", taskInfo.getTaskId());
//		for (UdipTaskTempleInfo temp : tempList) {
//
//			List<UdipTemplePrimary> UdipTemplePrimary = this.findByPropertys(UdipTemplePrimary.class, new String[] { "templeId", "keyType" }, new Object[] { temp.getTempleId(), UdipConstants.TAB_PRIMARY });
//			if (caseList.size() > 0 && (UdipTemplePrimary.isEmpty() || !UdipTemplePrimary.get(0).getKeyColumns().contains(UdipConstants.TAB_DATA_CASE))) {
//				continue;// 不支持多批次的模板
//			}
//
//			List<String> orgs = Lists.newArrayList();
//			for (UdipDispatchOrg org : orgList) {
//				UdipAuthRecordInfo record = new UdipAuthRecordInfo();//操作记录
//				UdipDataStateInfo state = new UdipDataStateInfo();//数据状态
//				record.setRecordId(RandomUtils.uuid2());
//				record.setCaseId(taskCase.getCaseId());
//				record.setOperateDate(taskCase.getDispatchDate());
//				record.setOperator(dipatcher);
//				record.setOperateType(UdipConstants.TASK_STATE_DISPATCH);
//				record.setOrgCode(org.getOrgCode());
//				record.setTempleId(temp.getTempleId());
//				
//				state.setId(RandomUtils.uuid2());
//				state.setCaseId(taskCase.getCaseId());
//				state.setDataState(UdipConstants.TASK_STATE_DISPATCH);
//				state.setOrgCode(org.getOrgCode());
//				state.setTempleId(temp.getTempleId());
//				state.setLogicSysNo(taskInfo.getLogicSysNo());
//				state.setOperator(dipatcher);
//				state.setOperateDate(taskCase.getDispatchDate());
//				
//				orgs.add(org.getOrgCode());
//				this.baseDAO.getEntityManager().persist(record);// 保存操作记录
//				this.baseDAO.getEntityManager().persist(state);// 保存数据状态
//			}
//
//			/* 4.产生下发数据 */
//			UdipTemple udipTemple = this.getEntityById(UdipTemple.class, temp.getTempleId());
//			List<UdipDataLoadInfo> dataLoadInfoList = this.findByPropertys(UdipDataLoadInfo.class, new String[] { "templeId" }, new Object[] { temp.getTempleId() });
//
//			if (!dataLoadInfoList.isEmpty()) {
//				try {
//					dataUtils.loadData(udipTemple, null, dataLoadInfoList, etlDate, orgs, taskCase.getCaseId());// 预装载
//				} catch (Exception e) {
//					log.error("数据装载失败", e);
//				}
//			}
//		}
//
//		/* 5.发送下发邮件 */
//		if(UdipConstants.STATE_YES.equals(taskInfo.getAutoSendMail())){//由任务设置决定是否发送下发邮件
//			
//			UdipMailGroupInfo group = this.getEntityById(UdipMailGroupInfo.class, taskInfo.getMailId());
//			UdipMailSrvInfo server = this.getEntityById(UdipMailSrvInfo.class, group.getServerId());
//
//			try {
//				MailContext mailContext = MailContext.getMailContext(server.getMailClass());
//				Collection<String> addr = getMailAddsByRole(taskInfo.getLogicSysNo(), roleList);
//				if (!addr.isEmpty()) {
//					Mail mail = new Mail();
//					mail.setSubject(taskInfo.getTaskName() + UdipConstants.SUBJECT_INPUT);
//					mail.setContent(SysFunction.replaceALL(group.getAuthContent(), taskCase.getDispatchDate(), taskCase.getEndDate()));
//					mail.setFromer(server.getFromAddr());
//					mail.setReceiver(addr.toArray(new String[addr.size()]));
//					mailContext.sendMail(server, mail);
//				}
//			} catch (Exception e) {
//				log.error("下发邮件发送失败：", e);
//			}
//		}
//	}
//
//	/**
//	 * 根据角色代码和逻辑系统ID获取用户邮件地址
//	 * @param logicSysNo
//	 * @param roleList
//	 * @return
//	 */
//	public Collection<String> getMailAddsByRole(String logicSysNo, List<UdipTaskRole> roleList) {
//		List<CommonTreeNode> userList = authObjectUtils.getUserList(logicSysNo);
//
//		Collection<String> addr = new HashSet<String>();// 获取下发地址
//		for (CommonTreeNode tree : userList) {
//			UdipUserInfo user = (UdipUserInfo) tree.getData();
//
//			for (UdipTaskRole role : roleList) {
//				if (user.getRoleList().contains(role.getRoleCode())) {// 拥有补录角色
//					if(StringUtils.isNotBlank(role.getRoleUser())&&!ArrayUtils.asCollection(role.getRoleUser()).contains(user.getUserId())){
//						continue ;
//					}
//					if (StringUtils.isNotBlank(user.getEmail()))
//						addr.add(user.getEmail());
//					break;
//				}
//			}
//		}
//		return addr;
//	}
//
//	/**
//	 * 结束所有到期的任务
//	 * @param logicSysNo 逻辑系统标识
//	 */
//	@Transactional(readOnly = false)
//	public void taskOver(String logicSysNo) {
//		taskOver(getOverTimeTask(logicSysNo));
//	}
//
//	/**
//	 * 获取到期的实例
//	 * @param logicSysNo 逻辑系统标识
//	 * @return
//	 */
//	public List<UdipTaskCaseInfo> getOverTimeTask(String logicSysNo) {
//		
//		String jql = "select task,c from UdipInputTaskInfo task,UdipTaskCaseInfo c where task.logicSysNo=:logicSysNo and c.caseState=:caseState and task.taskId=c.taskId ";
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("logicSysNo", logicSysNo);
//		paramMap.put("caseState", UdipConstants.CASE_STATE_START);
//		List<Object[]> list = baseDAO.findWithNameParm(jql, paramMap);
//		
//		List<UdipTaskCaseInfo> endCaseList = Lists.newArrayList();
//		for (Object[] obj : list) {
//			UdipInputTaskInfo task = (UdipInputTaskInfo)obj[0];
//			UdipTaskCaseInfo taskcase = (UdipTaskCaseInfo)obj[1];
//			Date startDate = DateUtils.getDateStart(taskcase.getEtlDate());
//			Date endDate = DateUtils.getPastDate(-taskcase.getEndDate(), startDate);
//
//			if (UdipConstants.STATE_YES.equals(task.getAutoEnd()) && endDate.before(new Date())) {// 自动到期,并且已经到期
//				endCaseList.add(taskcase);
//			}
//		}
//		return endCaseList;
//	}
//
//	/**
//	 * 结束指定任务
//	 * @param taskCaseList
//	 */
//	@Transactional(readOnly = false)
//	public void taskOver(List<UdipTaskCaseInfo> taskCaseList) {
//
//		for (UdipTaskCaseInfo info : taskCaseList) {
//			/* 1.向任务到期表插入一条记录 */
//			List<UdipTemple> tempList = getTempleByTaskId(info.getTaskId());
//			for (UdipTemple temp : tempList) {
//
//				List<UdipDataWatcher> list = this.baseDAO.findByPropertys(UdipDataWatcher.class, new String[] { "caseId", "tableName" }, new Object[] { info.getCaseId(), temp.getTableName() });
//				for (UdipDataWatcher obj : list) {// 先删除原先的记录,如果有，也只是一条
//					this.baseDAO.getEntityManager().remove(obj);
//				}
//				UdipDataWatcher watcher = new UdipDataWatcher();
//				watcher.setId(RandomUtils.uuid2());
//				watcher.setCaseId(info.getCaseId());
//				watcher.setDataDate(info.getEtlDate());
//				watcher.setTableName(temp.getTableName());
//				watcher.setOverDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
//				this.baseDAO.getEntityManager().persist(watcher);
//			}
//			/* 2.更新实例状态 */
//			info.setCaseState(UdipConstants.CASE_STATE_END);
//			this.baseDAO.getEntityManager().save(info);
//		}
//
//	}
//	/**
//	 * 重启指定任务
//	 * @param taskCaseList
//	 */
//	@Transactional(readOnly = false)
//	public void taskStart(List<UdipTaskCaseInfo> taskCaseList) {
//		for (UdipTaskCaseInfo info : taskCaseList) {
//			/* 1.向任务到期表插入一条记录 */
//			List<UdipTemple> tempList = getTempleByTaskId(info.getTaskId());
//			for (UdipTemple temp : tempList) {
//				List<UdipDataWatcher> list = this.baseDAO.findByPropertys(UdipDataWatcher.class, new String[] { "caseId", "tableName" }, new Object[] { info.getCaseId(), temp.getTableName() });
//				for (UdipDataWatcher obj : list) {// 先删除原先的记录,如果有，也只是一条
//					this.baseDAO.getEntityManager().remove(obj);
//				}
//			}
//			/* 2.更新实例状态 */
//			info.setCaseState(UdipConstants.CASE_STATE_START);
//			this.baseDAO.getEntityManager().save(info);
//		}
//	}
//	/**
//	 * 根据任务ID获取模板列表
//	 * @param taskId
//	 * @return
//	 */
//	public List<UdipTemple> getTempleByTaskId(String taskId) {
//		String jql = ("select temple from UdipTemple temple, UdipTaskTempleInfo taskTemp where temple.templeId=taskTemp.templeId and taskTemp.taskId=:taskId");
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("taskId", taskId);
//		return baseDAO.findWithNameParm(jql, paramMap);
//	}
//
//	public List<CommonTreeNode> buildTaskTree(String logicSysNo, List<String> list) {
//		List<CommonTreeNode> nodeList = Lists.newArrayList();
//
//		/** 目录 **/
//		List<CommonTreeNode> dirList = dirBS.getByType(UdipConstants.DIR_TYPE_TASK, logicSysNo);
//		nodeList.addAll(dirList);
//
//		List<UdipInputTaskInfo> taskList = this.findEntityListByProperty("logicSysNo", logicSysNo);
//
//		for (UdipInputTaskInfo info : taskList) {
//			Map<String, String> map = Maps.newHashMap();
//			map.put("type", UdipConstants.TASK_CASE);
//
//			CommonTreeNode treeNode = new CommonTreeNode();
//			treeNode.setId(info.getTaskId());
//			treeNode.setUpId(info.getDirId());
//			treeNode.setText(info.getTaskName());
//
//			treeNode.setParams(map);
//			nodeList.add(treeNode);
//		}
//
//		return nodeList;
//	}
//
//	public List<CommonTreeNode> buildInputTaskTree(String userName, String logicSysNo, List<String> roles, String roleType) {
//		List<CommonTreeNode> nodeList = Lists.newArrayList();
//		/** 目录 **/
//		List<CommonTreeNode> dirList = this.dirBS.getByType(UdipConstants.DIR_TYPE_TASK, logicSysNo);
//		nodeList.addAll(dirList);
//
//		return nodeList;
//	}
}
