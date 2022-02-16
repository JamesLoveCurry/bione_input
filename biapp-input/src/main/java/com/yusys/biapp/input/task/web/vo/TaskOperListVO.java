package com.yusys.biapp.input.task.web.vo;

public class TaskOperListVO {
	//任务实例ID
	private String taskInstanceId;
	//任务节点实例ID
	private String taskNodeInstanceId;
	//任务名称
	private String taskNm;
	//任务类型
	private String taskTypeNm;
	//补录类型
	private String taskExeObjTypeNm;
	//
	private String taskExeObjNm;
	//创建人
	private String  creator;
	//任务状态
	private String sts;
	//数据日期
	private String dataDate;
	
	private String orgName;
	
	private String orgNm;
	
	private String catalogNm;

	private String deployOrg;
	
	private String taskTitle;
	
	private String canDelete;
	
	private String startTime;
	
	private String taskId;
	
	private String templateId;

	private String taskNodeNm;

	private String orgNo;

	private String deployOrgNo;
	
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getTaskInstanceId() {
		return taskInstanceId;
	}

	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getTaskNodeInstanceId() {
		return taskNodeInstanceId;
	}

	public void setTaskNodeInstanceId(String taskNodeInstanceId) {
		this.taskNodeInstanceId = taskNodeInstanceId;
	}

	public String getTaskNm() {
		return taskNm;
	}

	public void setTaskNm(String taskNm) {
		this.taskNm = taskNm;
	}

	public String getTaskTypeNm() {
		return taskTypeNm;
	}

	public void setTaskTypeNm(String taskTypeNm) {
		this.taskTypeNm = taskTypeNm;
	}

	public String getTaskExeObjNm() {
		return taskExeObjNm;
	}

	public void setTaskExeObjNm(String taskExeObjNm) {
		this.taskExeObjNm = taskExeObjNm;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getTaskExeObjTypeNm() {
		return taskExeObjTypeNm;
	}

	public void setTaskExeObjTypeNm(String taskExeObjTypeNm) {
		this.taskExeObjTypeNm = taskExeObjTypeNm;
	}

	public String getSts() {
		return sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}
	
	public String getCatalogNm() {
		return catalogNm;
	}

	public void setCatalogNm(String catalogNm) {
		this.catalogNm = catalogNm;
	}

	public String getDeployOrg() {
		return deployOrg;
	}

	public void setDeployOrg(String deployOrg) {
		this.deployOrg = deployOrg;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getCanDelete() {
		return canDelete;
	}

	public void setCanDelete(String canDelete) {
		this.canDelete = canDelete;
	}

	public String getTaskNodeNm() {
		return taskNodeNm;
	}

	public void setTaskNodeNm(String taskNodeNm) {
		this.taskNodeNm = taskNodeNm;
	}

	public String getDeployOrgNo() {
		return deployOrgNo;
	}

	public void setDeployOrgNo(String deployOrgNo) {
		this.deployOrgNo = deployOrgNo;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
}
