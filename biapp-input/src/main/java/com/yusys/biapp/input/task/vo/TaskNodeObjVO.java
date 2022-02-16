package com.yusys.biapp.input.task.vo;

import java.util.List;


public class TaskNodeObjVO {
	/*********节点基本信息 start****************/
	//流程节点ID
	private String taskNodeDefId;
	//流程节点名称
	private String taskNodeNm;
	//流程节点数序号
	private int taskOrderno; 
	//是否可以中断
	private String isCanInterrupt;
	
	private String nodeType;
	
	/*********节点基本信息 end****************/
	

	/*********节点执行对象信息 start****************/

	private List<TaskObjVO> taskObjVOList;

	/*********节点执行对象信息end****************/
	public String getTaskNodeDefId() {
		return taskNodeDefId;
	}
	public void setTaskNodeDefId(String taskNodeDefId) {
		this.taskNodeDefId = taskNodeDefId;
	}
	public String getTaskNodeNm() {
		return taskNodeNm;
	}
	public void setTaskNodeNm(String taskNodeNm) {
		this.taskNodeNm = taskNodeNm;
	}
	public int getTaskOrderno() {
		return taskOrderno;
	}
	public void setTaskOrderno(int taskOrderno) {
		this.taskOrderno = taskOrderno;
	}
	public String getIsCanInterrupt() {
		return isCanInterrupt;
	}
	public void setIsCanInterrupt(String isCanInterrupt) {
		this.isCanInterrupt = isCanInterrupt;
	}
	public List<TaskObjVO> getTaskObjVOList() {
		return taskObjVOList;
	}
	public void setTaskObjVOList(List<TaskObjVO> taskObjVOList) {
		this.taskObjVOList = taskObjVOList;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
}
