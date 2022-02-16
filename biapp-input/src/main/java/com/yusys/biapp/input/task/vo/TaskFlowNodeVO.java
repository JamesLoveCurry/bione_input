package com.yusys.biapp.input.task.vo;

import java.util.List;

import com.yusys.biapp.input.task.entity.RptTskFlowNode;

public class TaskFlowNodeVO {

	
	private List<RptTskFlowNode> flowNodeList;
	private String taskDefId;
	public List<RptTskFlowNode> getFlowNodeList() {
		return flowNodeList;
	}
	public void setFlowNodeList(List<RptTskFlowNode> flowNodeList) {
		this.flowNodeList = flowNodeList;
	}
	public String getTaskDefId() {
		return taskDefId;
	}
	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}
	
	
}
