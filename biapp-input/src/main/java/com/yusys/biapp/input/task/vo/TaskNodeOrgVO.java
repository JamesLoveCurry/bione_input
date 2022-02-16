package com.yusys.biapp.input.task.vo;

import java.util.List;

public class TaskNodeOrgVO {

	private String orgNo;

	private String orgNm;

	// 任务节点相关信息
	private List<TaskNodeObjVO> taskNodeObjList;

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public List<TaskNodeObjVO> getTaskNodeObjList() {
		return taskNodeObjList;
	}

	public void setTaskNodeObjList(List<TaskNodeObjVO> taskNodeObjList) {
		this.taskNodeObjList = taskNodeObjList;
	}

}
