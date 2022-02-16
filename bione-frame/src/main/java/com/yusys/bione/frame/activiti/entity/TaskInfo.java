package com.yusys.bione.frame.activiti.entity;

import java.util.List;
import java.util.Map;

public class TaskInfo {

	/**
	 * 工作流程定义ID
	 */
	private String processDefinitionId;

	/**
	 * 工作流程实例ID
	 */
	private String processInstanceId;

	/**
	 * 当前节点定义ID
	 */
	private String definitionKey;

	/**
	 * 当前节点名称
	 */
	private String name;

	/**
	 * 任务执行人
	 */
	private String assignee;

	/**
	 * 任务结束时间
	 */
	private String endTime;

	/**
	 * 候选用户列表
	 */
	private List<String> candidateUserList;

	/**
	 * 候选角色列表
	 */
	private List<String> candidateRoleList;

	/**
	 * 是否当前任务
	 */
	private boolean active;

	/**
	 * 业务参数
	 */
	private Map<String, Object> parameters;

	/**
	 * 获取工作流程定义ID
	 */
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	/**
	 * 设置工作流程定义ID
	 */
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	/**
	 * 获取工作流程实例ID
	 */
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	/**
	 * 设置工作流程实例ID
	 */
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	/**
	 * 获取当前节点定义ID
	 */
	public String getDefinitionKey() {
		return definitionKey;
	}

	/**
	 * 设置当前节点定义ID
	 */
	public void setDefinitionKey(String definitionKey) {
		this.definitionKey = definitionKey;
	}

	/**
	 * 获取当前节点名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置当前节点名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取任务执行人
	 */
	public String getAssignee() {
		return assignee;
	}

	/**
	 * 设置任务执行人
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	/**
	 * 获取任务结束时间
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * 设置任务结束时间
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * 获取候选用户列表
	 */
	public List<String> getCandidateUserList() {
		return candidateUserList;
	}

	/**
	 * 设置候选用户列表
	 */
	public void setCandidateUserList(List<String> candidateUserList) {
		this.candidateUserList = candidateUserList;
	}

	/**
	 * 获取候选角色列表
	 */
	public List<String> getCandidateRoleList() {
		return candidateRoleList;
	}

	/**
	 * 设置候选角色列表
	 */
	public void setCandidateRoleList(List<String> candidateRoleList) {
		this.candidateRoleList = candidateRoleList;
	}

	/**
	 * 获取是否当前任务
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * 设置是否当前任务
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * 获取业务参数
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * 设置业务参数
	 */
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
}
