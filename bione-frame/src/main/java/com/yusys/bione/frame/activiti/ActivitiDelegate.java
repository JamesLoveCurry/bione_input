package com.yusys.bione.frame.activiti;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfoQuery;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.frame.activiti.entity.TaskInfo;

@Service
@Transactional(readOnly = true)
public class ActivitiDelegate {

	public enum AlterIdentity {
		OP_ADD, OP_MODIFY, OP_REMOVE
	};

	protected Logger logger = LoggerFactory.getLogger(ActivitiDelegate.class);

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private HistoryService historyService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private ActivitiSupport activitiSupport;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取工作流配置
	 * 
	 * @param name 配置字段
	 * @return 配置值
	 */
	public String getProperty(String name) {
		PropertiesUtils utils = PropertiesUtils.get("workflow.properties");
		return utils.getProperty(name);
	}

	private List<String> getPropertyStartWith(String name) {
		PropertiesUtils utils = PropertiesUtils.get("workflow.properties");
		List<String> list = new ArrayList<String>();
		for (Iterator<Entry<Object, Object>> it = utils.getProperties().entrySet().iterator(); it.hasNext(); ) {
			Entry<Object, Object> entry = it.next();
			String key = (String)entry.getKey();
			if (key.startsWith(name)) {
				list.add((String)entry.getValue());
			}
		}
		return list;
	}

	/**
	 * 查询指定机构号的流程定义ID
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param orgNo 机构号，可以为null，这时只按照业务类型查询
	 * 
	 * @return 流程定义ID
	 */
	public String getProcessDefinitionId(String buzType, String orgNo) {
		StringBuilder sb = new StringBuilder();
		sb.append(getProperty(buzType));
		if (StringUtils.isNotEmpty(orgNo)) {
			sb.append('_').append(orgNo);
		}
		// 首先查询指定业务类型(和机构号)的流程
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		ProcessDefinition processDefinition = processDefinitionQuery
				.processDefinitionKey(sb.toString()).singleResult();
		if (processDefinition != null) {
			return processDefinition.getId();
		}
		if (StringUtils.isNotEmpty(orgNo)) {
			// 在机构号不为空时，忽视机构号，只按照业务类型查询
			sb.setLength(0);
			sb.append(getProperty(buzType));
			processDefinition = processDefinitionQuery.processDefinitionKey(sb.toString()).singleResult();
			if (processDefinition != null) {
				return processDefinition.getId();
			}
		}
		throw new RuntimeException("Cannot find Process Definition of buzType: " + buzType + ", orgNo: " + orgNo);
	}

	private TaskInfo createTaskInfo(org.activiti.engine.task.TaskInfo taskInfo) {
		TaskInfo newTaskInfo = new TaskInfo();
		newTaskInfo.setProcessDefinitionId(taskInfo.getProcessDefinitionId());
		newTaskInfo.setProcessInstanceId(taskInfo.getProcessInstanceId());
		newTaskInfo.setDefinitionKey(taskInfo.getTaskDefinitionKey());
		newTaskInfo.setName(taskInfo.getName());
		if (taskInfo instanceof HistoricTaskInstance) {
			// 结束时间
			if (((HistoricTaskInstance)taskInfo).getEndTime() != null) {
				newTaskInfo.setEndTime(sdf.format(((HistoricTaskInstance)taskInfo).getEndTime()));
			}
		} else {
			newTaskInfo.setActive(true);
		}
		Map<String, Object> variables = taskInfo.getProcessVariables();
		if (variables != null) {
			// 删除内部参数
			variables.remove(getProperty("resultVariable"));
			newTaskInfo.setParameters(variables);
		}
		return newTaskInfo;
	}

	private void setQueryParameter(TaskInfoQuery<?, ?> query, Map<String, Object> parameter) {
		if (MapUtils.isNotEmpty(parameter)) {
			for (Iterator<Entry<String, Object>> it = parameter.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, Object> entry = it.next();
				query.processVariableValueEquals(entry.getKey(), entry.getValue());
			}
		}
	}

	private String getUser(UserQuery userQuery, String userId) {
		User user = userQuery.userId(userId).singleResult();
		return user == null ? userId : user.getFirstName();
	}

	private String getGroup(GroupQuery groupQuery, String groupId) {
		Group group = groupQuery.groupId(groupId).singleResult();
		return group == null ? groupId : group.getName();
	}
	
	private String getActivityName(Collection<FlowElement> flowElements, String activityId) {
		for (Iterator<FlowElement> it = flowElements.iterator(); it.hasNext(); ) {
			FlowElement element = it.next();
			if (element.getId().equals(activityId)) {
				return element.getName();
			}
		}
		return null;
	}

	private void addTaskInfo(Map<String, List<TaskInfo>> taskInfoListMap, String key, TaskInfo taskInfo) {
		List<TaskInfo> list = taskInfoListMap.get(key);
		if (list == null) {
			list = new ArrayList<TaskInfo>();
			taskInfoListMap.put(key, list);
		}
		list.add(taskInfo);
	}

	/**
	 * 获取指定参数的任务信息（包括已完成的任务），将设置候选人等详细信息
	 * 
	 * @param processDefinitionId 流程定义ID
	 * @param processInstanceId 流程实例ID
	 * 
	 * @return 任务对象MAP，MAP的KEY值为各任务节点ID，其VALUE值为对应任务列表，以创建时间从晚到早排序
	 */
	public Map<String, List<TaskInfo>> getAllTaskInfo(String processDefinitionId, String processInstanceId) {
		Map<String, List<TaskInfo>> retTaskInfoMap = new HashMap<String, List<TaskInfo>>();

		// 查询BPMN中所有任务节点
		BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
		Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();

		// 所有UserTask类型任务信息
		List<org.activiti.engine.task.TaskInfo> allTaskInfoList = new ArrayList<org.activiti.engine.task.TaskInfo>();
		// 加入所有已完成的历史UserTask
		allTaskInfoList.addAll(historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.includeProcessVariables()
				.finished()
				.list());
		// 加入所有运行中UserTask
		allTaskInfoList.addAll(taskService.createTaskQuery()
				.processInstanceId(processInstanceId)
				.includeProcessVariables()
				.list());
		// 以创建时间从晚到早排序
		org.activiti.engine.task.TaskInfo[] allTaskInfoes = allTaskInfoList.toArray(new org.activiti.engine.task.TaskInfo[0]);
		Arrays.sort(allTaskInfoes, new Comparator<org.activiti.engine.task.TaskInfo>() {
			@Override
			public int compare(org.activiti.engine.task.TaskInfo taskInfo1, org.activiti.engine.task.TaskInfo taskInfo2) {
				return taskInfo2.getCreateTime().compareTo(taskInfo1.getCreateTime());
			}
		});
		// 获取所有执行对象
		List<Execution> executionList = runtimeService.createExecutionQuery()
				.processInstanceId(processInstanceId)
				.list();
		Map<String, Execution> executionMap = new HashMap<String, Execution>();
		for (int i = 0; i < executionList.size(); i ++) {
			executionMap.put(executionList.get(i).getId(), executionList.get(i));
		}
		
		UserQuery userQuery = identityService.createUserQuery();
		GroupQuery groupQuery = identityService.createGroupQuery();
		// 处理所有UserTask
		for (int i = 0; i < allTaskInfoes.length; i ++) {
			org.activiti.engine.task.TaskInfo currentTask = allTaskInfoes[i];
			TaskInfo taskInfo = createTaskInfo(currentTask);
			if (StringUtils.isNotEmpty(currentTask.getAssignee())) {
				// 执行人
				taskInfo.setAssignee(getUser(userQuery, currentTask.getAssignee()));
			}
			if (currentTask instanceof Task) {
				// 执行候选人/角色
				List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(currentTask.getId());
				if (CollectionUtils.isNotEmpty(identityLinkList)) {
					List<String> userList = new ArrayList<String>();
					List<String> groupList = new ArrayList<String>();
					for (IdentityLink identityLink : identityLinkList) {
						if (StringUtils.isNotEmpty(identityLink.getUserId())) {
							userList.add(getUser(userQuery, identityLink.getUserId()));
						}
						if (StringUtils.isNotEmpty(identityLink.getGroupId())) {
							groupList.add(getGroup(groupQuery, identityLink.getGroupId()));
						}
					}
					if (CollectionUtils.isNotEmpty(userList)) {
						taskInfo.setCandidateUserList(userList);
					}
					if (CollectionUtils.isNotEmpty(groupList)) {
						taskInfo.setCandidateRoleList(groupList);
					}
				}
				// 从执行对象中删除当前UserTask
				executionMap.remove(currentTask.getExecutionId());
			}
			addTaskInfo(retTaskInfoMap, currentTask.getTaskDefinitionKey(), taskInfo);
		}

		// 处理剩下的Execution，应为除UserTask外的其他类型Task的Execution
		for (Iterator<Entry<String, Execution>> it = executionMap.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, Execution> entry = it.next();
			Execution execution = entry.getValue();
			TaskInfo taskInfo = new TaskInfo();
			taskInfo.setProcessDefinitionId(processDefinitionId);
			taskInfo.setProcessInstanceId(execution.getProcessInstanceId());
			taskInfo.setName(getActivityName(flowElements, execution.getActivityId()));	// 用key匹配高亮圈
			taskInfo.setActive(true);
			addTaskInfo(retTaskInfoMap, execution.getActivityId(), taskInfo);
		}

		// 浏览BPMN中所有任务节点，对其中在retTaskInfoMap还没有对应VALUE的UserTask节点，是还没有执行过的
		for (FlowElement element : flowElements) {
			if (! (element instanceof org.activiti.bpmn.model.UserTask) ||
				retTaskInfoMap.containsKey(element.getName())) {
				continue;
			}
			UserTask userTask = (UserTask)element;
			TaskInfo taskInfo = new TaskInfo();
			taskInfo.setProcessDefinitionId(processDefinitionId);
			taskInfo.setName(userTask.getName());	// 用key匹配高亮圈
			if (StringUtils.isNotEmpty(userTask.getAssignee())) {
				// 执行人
				taskInfo.setAssignee(getUser(userQuery, userTask.getAssignee()));
			}
			// 执行候选人
			List<String> userIdList = userTask.getCandidateUsers();
			if (CollectionUtils.isNotEmpty(userIdList)) {
				List<String> userList = new ArrayList<String>();
				for (String userId : userIdList) {
					userList.add(getUser(userQuery, userId));
				}
				taskInfo.setCandidateUserList(userList);
			}
			// 执行候选角色
			taskInfo.setCandidateRoleList(userTask.getCandidateGroups());
			addTaskInfo(retTaskInfoMap, element.getId(), taskInfo);
		}
		
		return retTaskInfoMap;
	}

	/**
	 * 查询任务
	 * 
	 * @param processDefinitionId 流程定义ID
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 *
	 * @return 查询成功时，返回任务对象；否则返回null
	 */
	private Task getTask(String processDefinitionId, String businessKey, Map<String, Object> parameter) {
		TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionId(processDefinitionId);
		if (StringUtils.isNotEmpty(businessKey)) {
			taskQuery.processInstanceBusinessKey(businessKey);
		} else {
			setQueryParameter(taskQuery, parameter);
		}
		return taskQuery.singleResult();
	}

	/**
	 * 查询业务流程实例
	 * 
	 * @param processDefinitionId 流程定义ID
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 *
	 * @return 查询成功时，返回业务流程实例；否则返回null
	 */
	private ProcessInstance getProcessInstance(String processDefinitionId, String businessKey,
			Map<String, Object> parameter) {
		ProcessInstanceQuery piQuery = runtimeService.createProcessInstanceQuery()
				.processDefinitionId(processDefinitionId);
		if (StringUtils.isNotEmpty(businessKey)) {
			piQuery.processInstanceBusinessKey(businessKey);
		} else if (MapUtils.isNotEmpty(parameter)) {
			for (Iterator<Entry<String, Object>> it = parameter.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, Object> entry = it.next();
				piQuery.variableValueEquals(entry.getKey(), entry.getValue());
			}
		}
		return piQuery.singleResult();
	}
	
	/**
	 * 查询业务流程实例ID
	 * 
	 * @param processDefinitionId 流程定义ID
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 *
	 * @return 查询成功时，返回业务流程ID；否则返回null
	 */
	public String getProcessInstanceId(String processDefinitionId, String businessKey,
			Map<String, Object> parameter) {
		ProcessInstance processInstance = getProcessInstance(processDefinitionId, businessKey, parameter);
		return processInstance == null ? null : processInstance.getProcessInstanceId();
	}

	/**
	 * 查询最后一个历史业务流程实例
	 * 
	 * @param processDefinitionId 流程定义ID
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 *
	 * @return 查询成功时，返回历史业务流程实例；否则返回null
	 */
	private HistoricProcessInstance getHistoryProcessInstance(String processDefinitionId, String businessKey,
			Map<String, Object> parameter) {
		HistoricProcessInstanceQuery hpiQuery = historyService.createHistoricProcessInstanceQuery()
				.includeProcessVariables().processDefinitionId(processDefinitionId);
		if (StringUtils.isNotEmpty(businessKey)) {
			hpiQuery.processInstanceBusinessKey(businessKey);
		} else if (MapUtils.isNotEmpty(parameter)) {
			for (Iterator<Entry<String, Object>> it = parameter.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, Object> entry = it.next();
				hpiQuery.variableValueEquals(entry.getKey(), entry.getValue());
			}
		}
		hpiQuery.orderByProcessInstanceEndTime().desc();
		List<HistoricProcessInstance> list = hpiQuery.listPage(0, 1);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}
	
	/**
	 * 查询历史业务流程实例ID
	 * 
	 * @param processDefinitionId 流程定义ID
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 *
	 * @return 查询成功时，返回历史业务流程ID；否则返回null
	 */
	public String getHistoryProcessInstanceId(String processDefinitionId, String businessKey,
			Map<String, Object> parameter) {
		HistoricProcessInstance historicProcessInstance =
				getHistoryProcessInstance(processDefinitionId, businessKey, parameter);
		return historicProcessInstance == null ? null : historicProcessInstance.getId();
	}

	private void addOpTypeToQuery(TaskInfoQuery<?, ?> taskInfoQuery, String[] opTypes) {
		if (ArrayUtils.isNotEmpty(opTypes)) {
			Set<String> taskDefinitionKeySet = new HashSet<String>();
			for (String opType : opTypes) {
				String taskDefinitionKeys = getProperty(opType);
				if (StringUtils.isNotEmpty(taskDefinitionKeys)) {
					CollectionUtils.addAll(taskDefinitionKeySet, StringUtils.split(taskDefinitionKeys, ','));
				}
			}
			if (CollectionUtils.isNotEmpty(taskDefinitionKeySet)) {
				taskInfoQuery.or();
				for (Iterator<String> it = taskDefinitionKeySet.iterator(); it.hasNext(); ) {
					taskInfoQuery.taskDefinitionKey(it.next());
				}
				taskInfoQuery.endOr();
			}
		}
	}

	/**
	 * 获取当前用户涉及到的任务列表
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param nodeTypes 任务节点类型，可以为null，以任务节点类型为KEY值在workflow.properties中查找对应任务节点ID
	 * @param complete 是否已完成
	 * @param offset 开始条数（从 1 开始）
	 * @param pageSize 每页条数
	 * 
	 * @return 当前用户涉及到的任务列表
	 */
	public List<TaskInfo> getTaskList(String buzType, String[] opTypes, boolean complete,
			int offset, int pageSize) {
		List<TaskInfo> taskInfoList = new ArrayList<TaskInfo>();

		// 查询指定业务类型的流程
		List<String> processDefinitionKeyList = getPropertyStartWith(buzType);
		
		if (complete) {
			// 已完成/已办结的
			HistoricTaskInstanceQuery htiQuery = historyService.createHistoricTaskInstanceQuery()
					.processDefinitionKeyIn(processDefinitionKeyList)
					.includeProcessVariables();
			if (! activitiSupport.isCurrentUserSuperUser()) {
				htiQuery.taskAssignee(activitiSupport.getCurrentUserId());
			}
			addOpTypeToQuery(htiQuery, opTypes);
			List<HistoricTaskInstance> htiList = htiQuery
						.orderByHistoricTaskInstanceEndTime().asc()
						.listPage(offset - 1, pageSize);
			for (int i = 0; i < htiList.size(); i ++) {
				if (htiList.get(i).getEndTime() == null) {
					continue;
				}
				TaskInfo taskInfo = createTaskInfo(htiList.get(i));
				taskInfo.setEndTime(sdf.format(htiList.get(i).getEndTime()));
				taskInfoList.add(taskInfo);
			}
		} else {
			// 未签收/待办理的
			TaskQuery taskQuery = taskService.createTaskQuery()
					.processDefinitionKeyIn(processDefinitionKeyList)
					.includeProcessVariables();
			if (! activitiSupport.isCurrentUserSuperUser()) {
				taskQuery.taskCandidateUser(activitiSupport.getCurrentUserId());
			}
			addOpTypeToQuery(taskQuery, opTypes);
			List<Task> taskList = taskQuery
					.orderByTaskCreateTime().asc()
					.listPage(offset - 1, pageSize);
			for (int i = 0; i < taskList.size(); i ++) {
				taskInfoList.add(createTaskInfo(taskList.get(i)));
			}
		}
		return taskInfoList;
	}

	private boolean signForTask(Task task) {
		if (activitiSupport.getCurrentUserId().equals(task.getAssignee())) {
			return true;
		}
		logger.debug("claim: " + task.getName() + " " + activitiSupport.getCurrentUserId());
		taskService.claim(task.getId(), activitiSupport.getCurrentUserId());
		return true;
	}

	/**
	 * 签收工作流当前任务
	 * 
	 * @param processInstanceId 工作流实例ID
	 * 
	 * @return 处理成功时，返回true；否则返回false
	 */
	@Transactional(readOnly = false)
	public boolean signForTask(String processInstanceId) {
		Task task = null;
		if (activitiSupport.isCurrentUserSuperUser()) {
			task = taskService.createTaskQuery().processInstanceId(processInstanceId)
					.singleResult();
		} else {
			task = taskService.createTaskQuery().processInstanceId(processInstanceId)
					.taskCandidateUser(activitiSupport.getCurrentUserId()).singleResult();
		}
		return signForTask(task);
	}

	private Execution getExecution(String processDefinitionId, String businessKey,
			Map<String, Object> parameter) {
		ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
				.processDefinitionId(processDefinitionId);
		if (StringUtils.isNotEmpty(businessKey)) {
			executionQuery.processInstanceBusinessKey(businessKey);
		} else if (MapUtils.isNotEmpty(parameter)) {
			for (Iterator<Entry<String, Object>> it = parameter.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, Object> entry = it.next();
				executionQuery.processVariableValueEquals(entry.getKey(), entry.getValue());
			}
		}
		return executionQuery.singleResult();
	}

	private boolean signal(String processDefinitionId, String businessKey,
			Map<String, Object> parameter) {
		Execution execution = getExecution(processDefinitionId, businessKey, parameter);
		if (execution == null) {
			return false;
		}
		runtimeService.signal(execution.getId());
		return true;
	}

	/**
	 * 处理业务任务实例<br>
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param orgNo 指定目标机构，可以为null
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 * @param processResult 处理结果
	 * @param comment 备注，可以为null
	 *
	 * @return 处理成功时，返回true；否则返回false
	 */
	@Transactional(readOnly = false)
	public boolean process(String buzType, String orgNo, String businessKey,
			Map<String, Object> parameter, String processResult, String comment) {
		// 查询流程定义
		String processDefinitionId = getProcessDefinitionId(buzType, orgNo);
		Task task = getTask(processDefinitionId, businessKey, parameter);
		if (task == null) {
			// 如果不是 UserTask，那么假定是 ReceiveTask
			return signal(processDefinitionId, businessKey, parameter);
		}
		if (! signForTask(task)) {
			return false;
		}
		if (StringUtils.isNotEmpty(comment)) {
			taskService.addComment(task.getId(), task.getProcessInstanceId(), comment);
		}
		logger.debug("process: " + task.getName() + " " + processResult);
		taskService.setVariable(task.getId(), getProperty("resultVariable"), processResult);
		taskService.complete(task.getId());
		return true;
	}

	/**
	 * 向业务任务实例发信号<br>
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param orgNo 指定目标机构，可以为null
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 *
	 * @return 处理成功时，返回true；否则返回false
	 */
	@Transactional(readOnly = false)
	public boolean signal(String buzType, String orgNo, String businessKey,
			Map<String, Object> parameter) {
		// 查询流程定义
		String processDefinitionId = getProcessDefinitionId(buzType, orgNo);
		Task task = getTask(processDefinitionId, businessKey, parameter);
		if (task != null) {
			// 当前任务是普通的 UserTask，不是 ReceiveTask
			return false;
		}
		return signal(processDefinitionId, businessKey, parameter);
	}

	/**
	 * 取消业务流程执行<br>
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param orgNo 指定目标机构，可以为null
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 * @param comment 备注，可以为null
	 *
	 * @return 处理成功时，返回true；否则返回false
	 */
	@Transactional(readOnly = false)
	public void cancel(String buzType, String orgNo, String businessKey,
			Map<String, Object> parameter, String comment) {
		// 查询流程定义
		String processDefinitionId = getProcessDefinitionId(buzType, orgNo);
		String processInstanceId = getProcessInstanceId(processDefinitionId, businessKey, parameter);
		if (processInstanceId == null) {
			return;
		}
		runtimeService.deleteProcessInstance(processInstanceId, comment);
	}

	/**
	 * 设置业务流程跳转到任意节点<br>
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param orgNo 指定目标机构，可以为null
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 * @param targetId 跳转到的目标任务ID，如 "&lt;userTask id="xxx"
	 * @param comment 备注，可以为null
	 *
	 * @return 处理成功时，返回true；否则返回false
	 */
	@Transactional(readOnly = false)
	public boolean jump(String buzType, String orgNo, String businessKey,
			Map<String, Object> parameter, String targetId, String comment) {
		// 查询流程定义
		String processDefinitionId = getProcessDefinitionId(buzType, orgNo);
		
		Execution execution = null;
		String taskDefinitionKey;
		Task task = getTask(processDefinitionId, businessKey, parameter);
		if (task != null) {
			taskDefinitionKey = task.getTaskDefinitionKey();
		} else {
			execution = getExecution(processDefinitionId, businessKey, parameter);
			if (execution == null) {
				// 没有正在运行中任务，直接返回
				return false;
			}
			taskDefinitionKey = execution.getActivityId();
		}

		RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl)repositoryService;
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryServiceImpl
				.getDeployedProcessDefinition(processDefinitionId);

		// 获得当前流程定义模型的所有任务节点
		List<ActivityImpl> activitilist = processDefinitionEntity.getActivities();

		ActivityImpl currActiviti = null;	// 当前活动节点
		ActivityImpl destActiviti = null;	// 目标节点
		int sign = 0;
		for (ActivityImpl activityImpl : activitilist) {
			if(taskDefinitionKey.equals(activityImpl.getId())) {
				currActiviti = activityImpl;
				sign ++;
			} else if (targetId.equals(activityImpl.getId())) {
				destActiviti = activityImpl;
				sign ++;
			}
			if (sign == 2) {
				break;
			}
		}
		// 保存当前活动节点的流出项参数
		List<PvmTransition> hisPvmTransitionList = new ArrayList<PvmTransition>();
		for (PvmTransition pvmTransition : currActiviti.getOutgoingTransitions()){
			hisPvmTransitionList.add(pvmTransition);
		}
		// 清空当前活动节点的所有流出项
		currActiviti.getOutgoingTransitions().clear();
		// 为当前节点动态创建新的流出项
		TransitionImpl newTransitionImpl = currActiviti.createOutgoingTransition();
		// 为当前活动节点新的流出目标指定流程目标
		newTransitionImpl.setDestination(destActiviti);
		if (task == null) {
			runtimeService.signal(execution.getId());
		} else {
			// 保存描述信息
			if (StringUtils.isNotEmpty(comment)) {
				task.setDescription(comment);
				taskService.saveTask(task);
			}
			// 执行当前任务到目标任务
			taskService.complete(task.getId());
		}
		// 清除目标节点的新流入项
		destActiviti.getIncomingTransitions().remove(newTransitionImpl);
		// 清除原活动节点的临时流出项
		currActiviti.getOutgoingTransitions().clear();
		// 还原原活动节点流出项参数
		currActiviti.getOutgoingTransitions().addAll(hisPvmTransitionList);
		return true;
	}

	private boolean startupImpl(String processDefinitionId, String businessKey,
			Map<String, Object> parameter) {
		// 启动流程实例
		ProcessInstance processInstance = null;
		if (StringUtils.isEmpty(businessKey)) {
			processInstance = runtimeService.startProcessInstanceById(processDefinitionId, parameter);
		} else {
			processInstance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, parameter);
		}
		if (processInstance == null) {
			throw new RuntimeException("Process instance startup fail");
		}
		return processInstance.getProcessInstanceId() != null;
	}

	/**
	 * 业务启动：创建业务流程实例并启动<br>
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param orgNo 指定目标机构，可以为null
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 * @param setParameters 需要设置的业务参数，可以为null
	 *
	 * @return 处理成功时，返回true；否则返回false
	 */
	@Transactional(readOnly = false)
	public boolean startup(String buzType, String orgNo, String businessKey,
			Map<String, Object> parameter, Map<String, Object> setParameters) {
		// 查询流程定义
		String processDefinitionId = getProcessDefinitionId(buzType, orgNo);
		String processInstanceId = getProcessInstanceId(processDefinitionId, businessKey, parameter);
		if (processInstanceId != null) {
			throw new RuntimeException("Have activiti task of same processDefinition running");
		}
		// 没有找到对应任务，启动业务流程实例
		return startupImpl(processDefinitionId, businessKey, setParameters);
	}
	
	/**
	 * 业务重启动：如果业务正运行，那么重新开始；如果已完毕或者未启动，创建新业务流程实例并启动<br>
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param orgNo 指定目标机构，可以为null
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他查询时使用的业务参数，在businessKey为null时使用，可以为null
	 * @param setParameters 需要设置的业务参数，可以为null
	 *
	 * @return 处理成功时，返回true；否则返回false
	 */
	@Transactional(readOnly = false)
	public boolean restartup(String buzType, String orgNo, String businessKey,
			Map<String, Object> parameter) {
		Map<String, Object> setParameters = null;
		// 查询流程定义
		String processDefinitionId = getProcessDefinitionId(buzType, orgNo);
		ProcessInstance processInstance = getProcessInstance(processDefinitionId, businessKey, parameter);
		if (processInstance != null) {
			setParameters = processInstance.getProcessVariables();
			runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "强制重启");
		} else {
			HistoricProcessInstance historicProcessInstance = this.getHistoryProcessInstance(processDefinitionId, businessKey, parameter);
			if (historicProcessInstance != null) {
				setParameters = historicProcessInstance.getProcessVariables();
			}
		}
		// 启动业务流程
		return setParameters == null ? false : startupImpl(processDefinitionId, businessKey, setParameters);
	}
	
	/**
	 * 设置流程参数<br>
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param orgNo 指定目标机构，可以为null
	 * @param businessKey 业务主键，可以为null
	 * @param parameter 其他业务参数，在businessKey为null时使用，可以为null
	 * @param setParameters 需要设置的流程参数，可以为null
	 *
	 * @return 处理成功时，返回true；否则返回false
	 */
	/*
	@Transactional(readOnly = false)
	public boolean setProcessParameter(String buzType, String orgNo, String businessKey,
			Map<String, Object> parameter, Map<String, Object> setParameters) {
		if (MapUtils.isEmpty(setParameters)) {
			return false;
		}
		// 查询流程定义
		String processDefinitionId = getProcessDefinitionId(buzType, orgNo);
		Execution execution = getExecution(processDefinitionId, businessKey, parameter);
		if (execution == null) {
			return false;
		}
		runtimeService.setVariables(execution.getId(), setParameters);
		return true;
	}
	*/

	/**
	 * 查询当前任务节点ID
	 * 
	 * @param buzType 业务类型，以业务类型为KEY值在workflow.properties中查找对应业务流程定义
	 * @param businessKeys 业务主键集合
	 * 
	 * @return 业务主键=>任务节点ID的哈希对，如果找到了该业务流程且该流程已完成，那么任务节点ID为""；
	 * 			如果没有找到该业务流程，在返回哈希对中补不包括该业务主键
	 */
	public Map<String, String> getAllActivitiId(String buzType, Collection<String> businessKeys) {
		Map<String, String> retMap = new HashMap<String, String>();
		if (CollectionUtils.isEmpty(businessKeys)) {
			return retMap;
		}

		// 查询指定业务类型的流程
		List<String> processDefinitionKeyList = getPropertyStartWith(buzType);
		Set<String> processDefinitionKeySet = new HashSet<String>(processDefinitionKeyList);

		// 查找历史流程实例，为已完成的业务流程在返回值中赋初值
		HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKeyIn(processDefinitionKeyList)
				.finished();
		historicProcessInstanceQuery.or();
		for (Iterator<String> it = businessKeys.iterator(); it.hasNext(); ) {
			historicProcessInstanceQuery.processInstanceBusinessKey(it.next());
		}
		historicProcessInstanceQuery.endOr();

		List<HistoricProcessInstance> historicProcessInstanceList = historicProcessInstanceQuery.list();
		if (CollectionUtils.isNotEmpty(historicProcessInstanceList)) {
			for (int i = 0; i < historicProcessInstanceList.size(); i ++) {
				retMap.put(historicProcessInstanceList.get(i).getBusinessKey(), "");
			}
		}

		// 查询正在运行的业务流程实例
		ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery()
				.processDefinitionKeys(processDefinitionKeySet);
		processInstanceQuery.or();
		for (Iterator<String> it = businessKeys.iterator(); it.hasNext(); ) {
			processInstanceQuery.processInstanceBusinessKey(it.next());
		}
		processInstanceQuery.endOr();
		List<ProcessInstance> processInstanceList = processInstanceQuery.list();
		if (CollectionUtils.isEmpty(processInstanceList)) {
			return retMap;
		}
		
		// 查询流程定义下的执行对象
		// 目前executionQuery不支持或查询，也不支持processInstanceId或businessKey的集合查询，因此先全部查出
		List<Execution> executionList = runtimeService.createExecutionQuery()
				.processDefinitionKeys(processDefinitionKeySet)
				.list();
		if (CollectionUtils.isEmpty(executionList)) {
			return retMap;
		}
		
		// 构造业务流程实例ID=>业务流程实例的MAP
		Map<String, ProcessInstance> processInstanceMap = new HashMap<String, ProcessInstance>();
		for (int i = 0; i < processInstanceList.size(); i ++) {
			processInstanceMap.put(processInstanceList.get(i).getId(), processInstanceList.get(i));
		}

		// 再结合业务流程实例ID=>业务流程实例的MAP进行Execution的筛选
		for (int i = 0; i < executionList.size(); i ++) {
			Execution execution = executionList.get(i);
			ProcessInstance processInstance = processInstanceMap.get(execution.getProcessInstanceId());
			if (processInstance == null) {
				continue;
			}
			retMap.put(processInstance.getBusinessKey(), execution.getActivityId());
		}
		
		return retMap;
	}
	
}
